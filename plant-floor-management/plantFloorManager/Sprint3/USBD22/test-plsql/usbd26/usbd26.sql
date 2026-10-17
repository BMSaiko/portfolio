/*
This object type (material_requirement_obj) is used to create a comprehensive material requirements report.
Instead of just using Part or ExternalPart tables directly, we need this custom type because:

1. It aggregates information from multiple tables in a single object:
   - Product info (name, quantity ordered)
   - Part details (ID, description)
   - Required quantities calculated from the Bill of Materials (BOM)

2. It helps track material requirements across the entire production chain:
   - Shows which products need which materials
   - Links orders to their material requirements
   - Calculates total quantities needed for each material

3. Enables better reporting by combining:
   - Order information
   - Stock levels
   - Material requirements
   - Shortage calculations

This makes it easier to:
- Check if we have enough materials for an order
- Generate detailed material requirement reports
- Identify potential shortages before production starts
*/

-- Create object type for material requirements
CREATE OR REPLACE TYPE material_requirement_obj AS OBJECT (
    ProductName VARCHAR2(255),
    OrderQuantity NUMBER,
    PartID VARCHAR2(50),
    Description VARCHAR2(255),
    RequiredQty NUMBER
);
/

/*
The nested table type (material_requirement_tbl) is needed because:

1. An order can contain multiple products, each requiring multiple materials
   - We need to store a collection of material requirements

2. It allows us to:
   - Accumulate all material requirements for an order
   - Process them as a single unit
   - Perform aggregate operations (like summing total requirements)

3. Benefits over separate rows:
   - Keeps related material requirements together
   - Easier to process in PL/SQL
   - More efficient for bulk operations
   - Maintains data relationships

This table type acts as a container for all material requirements,
making it easier to handle complex orders with multiple products and materials.
*/

-- Create nested table type
CREATE OR REPLACE TYPE material_requirement_tbl AS TABLE OF material_requirement_obj;
/

-- Helper function to get all required materials for a product
CREATE OR REPLACE FUNCTION get_required_materials(
    p_part_id IN VARCHAR2,
    p_quantity IN NUMBER
) RETURN SYS_REFCURSOR IS
    v_result SYS_REFCURSOR;
BEGIN
    OPEN v_result FOR
        WITH bom_tree (PartID, RequiredQty, TreeLevel, PartDesc) AS (
            -- Base case: direct materials for the product
            SELECT 
                pi.PartID,
                pi.QuantityIn * p_quantity as RequiredQty,
                1 as TreeLevel,
                p.Description as PartDesc
            FROM BOO b
            JOIN BOO_Operation bo ON b.BOOID = bo.BOOID
            JOIN PartIN pi ON bo.OPID = pi.OPID AND bo.BOOID = pi.BOOID
            JOIN Part p ON pi.PartID = p.PartID
            WHERE b.PartID = p_part_id
            
            UNION ALL
            
            -- Recursive case: materials for intermediate parts
            SELECT 
                pi2.PartID,
                pi2.QuantityIn * bt.RequiredQty as RequiredQty,
                bt.TreeLevel + 1 as TreeLevel,
                p2.Description as PartDesc
            FROM bom_tree bt
            JOIN PartOut po ON bt.PartID = po.PartID
            JOIN BOO_Operation bo2 ON po.OPID = bo2.OPID AND po.BOOID = bo2.BOOID
            JOIN PartIN pi2 ON bo2.OPID = pi2.OPID AND bo2.BOOID = pi2.BOOID
            JOIN Part p2 ON pi2.PartID = p2.PartID
            WHERE bt.TreeLevel < 10  -- Prevent infinite recursion
        )
        SELECT 
            PartID,
            PartDesc as Description,
            SUM(RequiredQty) as TotalRequired
        FROM bom_tree
        WHERE PartID IN (SELECT PartID FROM ExternalPart)
        GROUP BY PartID, PartDesc;
        
    RETURN v_result;
END;
/

-- Function to check if there's enough stock for an order
CREATE OR REPLACE FUNCTION check_order_materials(p_order_id IN NUMBER) 
RETURN BOOLEAN IS
    v_materials SYS_REFCURSOR;
    v_part_id VARCHAR2(50);
    v_description VARCHAR2(255);
    v_required NUMBER;
    v_stock NUMBER;
    v_all_available BOOLEAN := TRUE;
    
    -- Cursor to get all products in the order
    CURSOR c_order_products IS
        SELECT ProductPartID, Quantity
        FROM OrderProduct
        WHERE OID = p_order_id;
        
    -- Variables for product cursor
    v_product_cursor SYS_REFCURSOR;
    v_total_required NUMBER;
BEGIN
    -- Get all required materials for the order
    FOR product_rec IN c_order_products LOOP
        v_product_cursor := get_required_materials(product_rec.ProductPartID, product_rec.Quantity);
        
        LOOP
            FETCH v_product_cursor INTO v_part_id, v_description, v_required;
            EXIT WHEN v_product_cursor%NOTFOUND;
            
            -- Check stock level
            SELECT Stock INTO v_stock
            FROM ExternalPart
            WHERE PartID = v_part_id;
            
            IF v_stock < v_required THEN
                v_all_available := FALSE;
                EXIT;
            END IF;
        END LOOP;
        
        CLOSE v_product_cursor;
        
        -- Exit early if we found insufficient materials
        IF NOT v_all_available THEN
            EXIT;
        END IF;
    END LOOP;
    
    RETURN v_all_available;
END;
/

-- Function to get detailed materials report for an order
CREATE OR REPLACE FUNCTION get_order_materials_report(p_order_id IN NUMBER) 
RETURN SYS_REFCURSOR IS
    v_result SYS_REFCURSOR;
    v_requirements material_requirement_tbl := material_requirement_tbl();
    
    -- Cursor to get all products in the order
    CURSOR c_order_products IS
        SELECT 
            op.ProductPartID,
            op.Quantity as OrderQuantity,
            p.Name as ProductName
        FROM OrderProduct op
        JOIN Product p ON p.PartID = op.ProductPartID
        WHERE op.OID = p_order_id;
        
    -- Variables for material cursor
    v_material_cursor SYS_REFCURSOR;
    v_part_id VARCHAR2(50);
    v_description VARCHAR2(255);
    v_required NUMBER;
BEGIN
    -- Process each product in the order
    FOR product_rec IN c_order_products LOOP
        v_material_cursor := get_required_materials(product_rec.ProductPartID, product_rec.OrderQuantity);
        
        LOOP
            FETCH v_material_cursor INTO v_part_id, v_description, v_required;
            EXIT WHEN v_material_cursor%NOTFOUND;
            
            v_requirements.EXTEND;
            v_requirements(v_requirements.LAST) := material_requirement_obj(
                product_rec.ProductName,
                product_rec.OrderQuantity,
                v_part_id,
                v_description,
                v_required
            );
        END LOOP;
        
        CLOSE v_material_cursor;
    END LOOP;
    
    -- Return aggregated results
    OPEN v_result FOR
        SELECT 
            r.ProductName,
            r.OrderQuantity,
            r.PartID,
            r.Description,
            SUM(r.RequiredQty) as TotalRequired,
            ep.Stock as CurrentStock,
            CASE 
                WHEN ep.Stock >= SUM(r.RequiredQty) THEN 'Available'
                ELSE 'Insufficient'
            END as Status,
            GREATEST(0, SUM(r.RequiredQty) - ep.Stock) as MissingQuantity
        FROM TABLE(v_requirements) r
        JOIN ExternalPart ep ON ep.PartID = r.PartID
        GROUP BY 
            r.ProductName,
            r.OrderQuantity,
            r.PartID,
            r.Description,
            ep.Stock
        ORDER BY 
            r.ProductName,
            CASE WHEN ep.Stock >= SUM(r.RequiredQty) THEN 1 ELSE 0 END;
            
    RETURN v_result;
END;
/ 