-- First let's check what order 1 contains
SELECT 
    o.OID,
    c.Name as ClientName,
    TO_CHAR(o.DateOrder, 'DD-MON-YY') as DateOrder,
    TO_CHAR(o.DateDelivery, 'DD-MON-YY') as DateDelivery,
    op.ProductPartID,
    p.Name as ProductName,
    op.Quantity
FROM "Order" o
JOIN Client c ON o.ClientNIF = c.NIF
JOIN OrderProduct op ON o.OID = op.OID
JOIN Product p ON op.ProductPartID = p.PartID
WHERE o.OID = 1;

-- Set good stock levels for testing
UPDATE ExternalPart SET Stock = 1000;

-- Check if we have enough materials for order 1
DECLARE
    v_has_materials BOOLEAN;
BEGIN
    v_has_materials := check_order_materials(1);
    IF v_has_materials THEN
        DBMS_OUTPUT.PUT_LINE('All materials available for Order #1');
    ELSE
        DBMS_OUTPUT.PUT_LINE('Missing materials for Order #1');
    END IF;
END;
/

-- Get detailed materials report for order 1
DECLARE
    v_report_cursor SYS_REFCURSOR;
    v_product_name VARCHAR2(255);
    v_order_qty NUMBER;
    v_part_id VARCHAR2(50);
    v_description VARCHAR2(255);
    v_total_required NUMBER;
    v_current_stock NUMBER;
    v_status VARCHAR2(20);
    v_missing_qty NUMBER;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Material Requirements Report for Order #1:');
    DBMS_OUTPUT.PUT_LINE('');
    
    v_report_cursor := get_order_materials_report(1);
    
    DBMS_OUTPUT.PUT_LINE(RPAD('Product', 30) || 
                        RPAD('Qty', 5) || 
                        RPAD('Part ID', 15) || 
                        RPAD('Description', 40) || 
                        RPAD('Required', 10) || 
                        RPAD('Stock', 10) || 
                        RPAD('Status', 15) || 
                        'Missing');
    DBMS_OUTPUT.PUT_LINE(RPAD('-', 140, '-'));
    
    LOOP
        FETCH v_report_cursor INTO 
            v_product_name, v_order_qty, v_part_id, v_description,
            v_total_required, v_current_stock, v_status, v_missing_qty;
        EXIT WHEN v_report_cursor%NOTFOUND;
        
        DBMS_OUTPUT.PUT_LINE(RPAD(v_product_name, 30) || 
                            RPAD(TO_CHAR(v_order_qty), 5) || 
                            RPAD(v_part_id, 15) || 
                            RPAD(v_description, 40) || 
                            RPAD(TO_CHAR(v_total_required), 10) || 
                            RPAD(TO_CHAR(v_current_stock), 10) || 
                            RPAD(v_status, 15) || 
                            TO_CHAR(v_missing_qty));
    END LOOP;
    
    CLOSE v_report_cursor;
END;
/

-- Reset stock levels
UPDATE ExternalPart SET Stock = 10;
COMMIT; 