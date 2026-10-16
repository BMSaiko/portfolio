# get_product_parts Function

## Overview
The get_product_parts function is a PL/SQL function designed to retrieve all components (parts) associated with a specific product, including nested subcomponents. It calculates the total quantity needed for each part, considering the hierarchical structure of products and their assemblies.

## Function Signature

    CREATE OR REPLACE FUNCTION get_product_parts(p_part_id IN VARCHAR2) RETURN SYS_REFCURSOR IS
    rc SYS_REFCURSOR;
    BEGIN
    OPEN rc FOR
        WITH parts_cte (PartID, Description, Quantity) AS (
            -- Base case: Parts directly used in the product
            SELECT
                pi.PartID,
                p.Description,
                pi.QuantityIn
            FROM
                BOO b
                JOIN BOO_Operation bo ON b.BOOID = bo.BOOID
                JOIN PartIN pi ON bo.OPID = pi.OPID AND bo.BOOID = pi.BOOID
                JOIN Part p ON pi.PartID = p.PartID
            WHERE
                b.PartPartID = p_part_id
            UNION ALL
            -- Recursive case: Parts used in sub-products
            SELECT
                pi.PartID,
                p.Description,
                parts_cte.Quantity * pi.QuantityIn
            FROM
                parts_cte
                JOIN BOO b ON b.PartPartID = parts_cte.PartID
                JOIN BOO_Operation bo ON b.BOOID = bo.BOOID
                JOIN PartIN pi ON bo.OPID = pi.OPID AND bo.BOOID = pi.BOOID
                JOIN Part p ON pi.PartID = p.PartID
        )
        SELECT
            PartID,
            Description,
            SUM(Quantity) AS TotalQuantity
        FROM
            parts_cte
        GROUP BY
            PartID,
            Description
        ORDER BY
            PartID;
    
    RETURN rc;
    END;
    /

## Description
The get_product_parts function takes a product's PartID as input and returns a cursor containing all parts needed to build that product. This includes both direct components and subcomponents (parts of parts), along with the total quantity needed for each part.

## Logic Breakdown

1. Common Table Expression (CTE) - parts_cte
The function uses a recursive Common Table Expression (CTE) called parts_cte to traverse the Bill of Operations (BOO) hierarchy and accumulate the necessary parts and their quantities.

### a. Base Case

    SELECT
    pi.PartID,
    p.Description,
    pi.QuantityIn
    FROM
    BOO b
    JOIN BOO_Operation bo ON b.BOOID = bo.BOOID
    JOIN PartIN pi ON bo.OPID = pi.OPID AND bo.BOOID = pi.BOOID
    JOIN Part p ON pi.PartID = p.PartID
    WHERE
    b.PartPartID = p_part_id

**Purpose:** Retrieves parts that are directly used in the specified product.

**Tables Involved:**

- BOO: Bill of Operations, relates products to operations.
- BOO_Operation: Associates operations with BOO entries.
- PartIN: Details input parts in operations, including quantities.
- Part: Contains part details, such as descriptions.

**Output:** PartID, Description, and QuantityIn for each direct part.

### b. Recursive Case

    SELECT
    pi.PartID,
    p.Description,
    parts_cte.Quantity * pi.QuantityIn
    FROM
    parts_cte
    JOIN BOO b ON b.PartPartID = parts_cte.PartID
    JOIN BOO_Operation bo ON b.BOOID = bo.BOOID
    JOIN PartIN pi ON bo.OPID = pi.OPID AND bo.BOOID = pi.BOOID
    JOIN Part p ON pi.PartID = p.PartID

**Purpose:** Recursively finds subcomponents used in parts identified in the previous step.

**Logic:**

- For each PartID in parts_cte, checks if that part also has an associated BOO (i.e., is composed of other parts).
- Retrieves subparts (pi.PartID) used in these operations.
- Calculates the total quantity needed by multiplying the parent part quantity by the subpart's QuantityIn.

**Output:** PartID, Description, and cumulative Quantity for each subpart.

## 2. Aggregation and Ordering

    SELECT
    PartID,
    Description,
    SUM(Quantity) AS TotalQuantity
    FROM
    parts_cte
    GROUP BY
    PartID,
    Description
    ORDER BY
    PartID;

**Purpose:** Aggregates quantities for each part across all hierarchy levels.

**Operations:**

- SUM(Quantity): Sums quantities for parts that appear multiple times due to being used in various subassemblies.
- GROUP BY: Groups results by PartID and Description to ensure accurate aggregation.
- ORDER BY: Sorts final output by PartID for better readability.

## 3. Cursor Handling

    OPEN rc FOR
    -- CTE and SELECT statement
    RETURN rc;

**Functionality:** Opens a cursor for the executed SQL statement and returns it to the caller.

**Benefit:** Allows the caller to fetch and process results efficiently, especially useful for large datasets.

## Usage
To use the get_product_parts function, execute it with the desired PartID. For example:

    SET SERVEROUTPUT ON;

    DECLARE
    rc SYS_REFCURSOR;
    v_part_id VARCHAR2(50) := 'ROOT'; -- Use a valid PartID existing in the new structure
    v_part VARCHAR2(50);
    v_description VARCHAR2(200);
    v_quantity NUMBER;
    BEGIN
    rc := get_product_parts(v_part_id);
    
    DBMS_OUTPUT.PUT_LINE('Parts List for Product: ' || v_part_id);
    DBMS_OUTPUT.PUT_LINE('---------------------------------------------');
    DBMS_OUTPUT.PUT_LINE('PartID' || CHR(9) || 'Description' || CHR(9) || 'Quantity');
    DBMS_OUTPUT.PUT_LINE('---------------------------------------------');
    
    LOOP
        FETCH rc INTO v_part, v_description, v_quantity;
        EXIT WHEN rc%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE(v_part || CHR(9) || v_description || CHR(9) || v_quantity);
    END LOOP;
    
    CLOSE rc;
    END;
    /

**Explanation:**

- Declaration: Variables to store data returned by the cursor.
- Execution: Calls the function with 'ROOT' as PartID. Ensure 'ROOT' exists in the Part table.
- Processing: Iterates through the cursor to display details of each part.
- Closure: Closes the cursor after processing.

## Example Output
Assuming the 'ROOT' product requires both direct parts and subcomponents, the output might look like:

| PartID | Description | Quantity |
|--------|-------------|----------|
| PART001 | Part Description 1 | 10 |
| PART002 | Part Description 2 | 5 |
| PART003 | Part Description 3 | 20 |
| PART004 | Part Description 4 | 15 |

**Note:** Actual output will depend on data inserted into the Part, BOO, BOO_Operation, and PartIN tables through the updated Python script.

## Considerations

- **Data Integrity:** Ensure BOO, BOO_Operation, and PartIN tables are correctly populated and reflect updated relationships and quantities in the new schema.
- **Performance:** For products with deeply nested subcomponents, the recursive CTE may impact performance. Creating indexes on columns used in joins (PartID, BOOID, OPID) can help mitigate this.
- **Error Handling:** The function assumes the input PartID exists and relationships are correctly defined. Additional error handling implementations may be needed depending on usage context.
- **Data Validation:** After inserting data using the Python script, validate it to ensure all relationships and quantities are correct before using the function.

## Conclusion
The get_product_parts function is a powerful tool for inventory and production planning, allowing users to fully understand the Bill of Materials (BOM) for any specific product. By using recursive queries, it efficiently traverses complex assembly hierarchies to provide comprehensive insights into part requirements.

## License
This project is licensed under the MIT License.

## Author
Bruno Silva  
[1221514@isep.ipp.pt](mailto:1221514@isep.ipp.pt)
