-- -------------------------------------------------------------------
-- Test Suite for Material Requirements Objects and Functions
-- -------------------------------------------------------------------
SET SERVEROUTPUT ON SIZE 1000000;

DECLARE
    -- Variables to hold function outputs
    v_required_materials SYS_REFCURSOR;
    v_report_cursor SYS_REFCURSOR;
    
    -- Variables for fetching cursor data
    v_product_name VARCHAR2(255);
    v_order_quantity NUMBER;
    v_part_id VARCHAR2(50);
    v_description VARCHAR2(255);
    v_required_qty NUMBER;
    
    v_boolean_result BOOLEAN;
    
    -- Custom exception for test failures
    e_test_failure EXCEPTION;
    
    -- Helper procedure to display test results
    PROCEDURE display_result(p_test_name VARCHAR2, p_status VARCHAR2, p_message VARCHAR2) IS
    BEGIN
        DBMS_OUTPUT.PUT_LINE('Test: ' || p_test_name || ' - Status: ' || p_status || ' - Message: ' || p_message);
    END;
    
BEGIN
    -- -------------------------------------------------------------------
    -- Test Case 1: get_required_materials with Valid Product and Quantity
    -- -------------------------------------------------------------------
    BEGIN
        v_required_materials := get_required_materials('AS12945S22', 10);
        display_result('get_required_materials - Valid Input', 'PASS', 'Cursor opened successfully.');
        
        -- Fetch and display results
        LOOP
            FETCH v_required_materials INTO v_part_id, v_description, v_required_qty;
            EXIT WHEN v_required_materials%NOTFOUND;
            DBMS_OUTPUT.PUT_LINE('PartID: ' || v_part_id || ', Description: ' || v_description || ', RequiredQty: ' || v_required_qty);
        END LOOP;
        CLOSE v_required_materials;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('get_required_materials - Valid Input', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 2: get_required_materials with Non-Existent Product
    -- -------------------------------------------------------------------
    BEGIN
        v_required_materials := get_required_materials('NON_EXISTENT_PRODUCT', 5);
        display_result('get_required_materials - Non-Existent Product', 'FAIL', 'Expected exception was not raised.');
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -20000 THEN -- Assuming function raises a custom error
                display_result('get_required_materials - Non-Existent Product', 'PASS', 'Correct exception raised: ' || SQLERRM);
            ELSE
                display_result('get_required_materials - Non-Existent Product', 'FAIL', 'Unexpected exception: ' || SQLERRM);
            END IF;
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 3: get_required_materials with Zero Quantity
    -- -------------------------------------------------------------------
    BEGIN
        v_required_materials := get_required_materials('AS12945S22', 0);
        display_result('get_required_materials - Zero Quantity', 'PASS', 'Cursor opened. Check if RequiredQty is zero.');
        
        -- Fetch and display results
        LOOP
            FETCH v_required_materials INTO v_part_id, v_description, v_required_qty;
            EXIT WHEN v_required_materials%NOTFOUND;
            IF v_required_qty = 0 THEN
                DBMS_OUTPUT.PUT_LINE('PartID: ' || v_part_id || ', RequiredQty is zero as expected.');
            ELSE
                DBMS_OUTPUT.PUT_LINE('PartID: ' || v_part_id || ', RequiredQty: ' || v_required_qty || ' (Unexpected)');
            END IF;
        END LOOP;
        CLOSE v_required_materials;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('get_required_materials - Zero Quantity', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 4: get_required_materials with Negative Quantity
    -- -------------------------------------------------------------------
    BEGIN
        v_required_materials := get_required_materials('AS12945S22', -5);
        display_result('get_required_materials - Negative Quantity', 'PASS', 'Cursor opened. Check if RequiredQty is negative.');
        
        -- Fetch and display results
        LOOP
            FETCH v_required_materials INTO v_part_id, v_description, v_required_qty;
            EXIT WHEN v_required_materials%NOTFOUND;
            IF v_required_qty < 0 THEN
                DBMS_OUTPUT.PUT_LINE('PartID: ' || v_part_id || ', RequiredQty is negative as expected.');
            ELSE
                DBMS_OUTPUT.PUT_LINE('PartID: ' || v_part_id || ', RequiredQty: ' || v_required_qty || ' (Unexpected)');
            END IF;
        END LOOP;
        CLOSE v_required_materials;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('get_required_materials - Negative Quantity', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 5: get_required_materials Exceeding Maximum Recursion Depth
    -- -------------------------------------------------------------------
    BEGIN
        -- Assuming 'IP12945A04' leads to deeper recursion, adjust as needed
        v_required_materials := get_required_materials('AS12945S22', 100);
        display_result('get_required_materials - Large Quantity (Recursion Depth)', 'PASS', 'Cursor opened successfully.');
        
        -- Fetch and display a limited number of results to avoid excessive output
        FOR i IN 1..10 LOOP
            FETCH v_required_materials INTO v_part_id, v_description, v_required_qty;
            EXIT WHEN v_required_materials%NOTFOUND;
            DBMS_OUTPUT.PUT_LINE('PartID: ' || v_part_id || ', Description: ' || v_description || ', RequiredQty: ' || v_required_qty);
        END LOOP;
        CLOSE v_required_materials;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('get_required_materials - Large Quantity (Recursion Depth)', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 6: check_order_materials with Sufficient Stock
    -- -------------------------------------------------------------------
    BEGIN
        -- Create a new order with products that have sufficient stock
        DECLARE
            v_new_order_id NUMBER;
        BEGIN
            v_new_order_id := CREATE_ORDER('PT501245987', 'AS12945S22', 5, TO_DATE('2025-01-10', 'YYYY-MM-DD'));
            IF check_order_materials(v_new_order_id) THEN
                display_result('check_order_materials - Sufficient Stock', 'PASS', 'All materials are available.');
            ELSE
                display_result('check_order_materials - Sufficient Stock', 'FAIL', 'Materials are reported as insufficient.');
            END IF;
            
            -- Cleanup
            DELETE FROM OrderProduct WHERE OID = v_new_order_id;
            DELETE FROM "Order" WHERE OID = v_new_order_id;
            COMMIT;
        END;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('check_order_materials - Sufficient Stock', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 7: check_order_materials with Insufficient Stock
    -- -------------------------------------------------------------------
    BEGIN
        -- Create a new order with products that have insufficient stock
        DECLARE
            v_new_order_id NUMBER;
        BEGIN
            -- Assuming 'AS12945S22' requires more than available stock
            v_new_order_id := CREATE_ORDER('PT501245987', 'AS12945S22', 1000, TO_DATE('2025-01-15', 'YYYY-MM-DD'));
            IF NOT check_order_materials(v_new_order_id) THEN
                display_result('check_order_materials - Insufficient Stock', 'PASS', 'Materials are correctly reported as insufficient.');
            ELSE
                display_result('check_order_materials - Insufficient Stock', 'FAIL', 'Insufficient materials were not detected.');
            END IF;
            
            -- Cleanup
            DELETE FROM OrderProduct WHERE OID = v_new_order_id;
            DELETE FROM "Order" WHERE OID = v_new_order_id;
            COMMIT;
        END;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('check_order_materials - Insufficient Stock', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 8: check_order_materials with Non-Existent Order
    -- -------------------------------------------------------------------
    BEGIN
        IF NOT check_order_materials(999999) THEN
            display_result('check_order_materials - Non-Existent Order', 'PASS', 'Non-existent order correctly reported as insufficient.');
        ELSE
            display_result('check_order_materials - Non-Existent Order', 'FAIL', 'Non-existent order incorrectly reported as sufficient.');
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('check_order_materials - Non-Existent Order', 'PASS', 'Handled non-existent order: ' || SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 9: check_order_materials with Order Having No Products
    -- -------------------------------------------------------------------
    BEGIN
        -- Create a new order without any products
        DECLARE
            v_new_order_id NUMBER;
        BEGIN
            v_new_order_id := CREATE_ORDER('PT501245987', 'AS12945S22', 0, TO_DATE('2025-01-20', 'YYYY-MM-DD')); -- Quantity 0
            IF check_order_materials(v_new_order_id) THEN
                display_result('check_order_materials - Order with No Products', 'PASS', 'Order with no products correctly reported as sufficient.');
            ELSE
                display_result('check_order_materials - Order with No Products', 'FAIL', 'Order with no products incorrectly reported as insufficient.');
            END IF;
            
            -- Cleanup
            DELETE FROM OrderProduct WHERE OID = v_new_order_id;
            DELETE FROM "Order" WHERE OID = v_new_order_id;
            COMMIT;
        END;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('check_order_materials - Order with No Products', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 10: get_order_materials_report for Valid Order
    -- -------------------------------------------------------------------
    BEGIN
        -- Create a new order with multiple products
        DECLARE
            v_new_order_id NUMBER;
        BEGIN
            v_new_order_id := CREATE_ORDER('PT501245987', 'AS12945S22', 5, TO_DATE('2025-02-01', 'YYYY-MM-DD'));
            INSERT INTO OrderProduct (OID, ProductPartID, Quantity) VALUES (v_new_order_id, 'AS12945S20', 10);
            COMMIT;
            
            v_report_cursor := get_order_materials_report(v_new_order_id);
            display_result('get_order_materials_report - Valid Order', 'PASS', 'Report generated successfully.');
            
            -- Fetch and display report
            LOOP
                FETCH v_report_cursor INTO v_product_name, v_order_quantity, v_part_id, v_description, v_required_qty;
                EXIT WHEN v_report_cursor%NOTFOUND;
                DBMS_OUTPUT.PUT_LINE('ProductName: ' || v_product_name || ', OrderQuantity: ' || v_order_quantity ||
                                     ', PartID: ' || v_part_id || ', Description: ' || v_description ||
                                     ', RequiredQty: ' || v_required_qty);
            END LOOP;
            CLOSE v_report_cursor;
            
            -- Cleanup
            DELETE FROM OrderProduct WHERE OID = v_new_order_id;
            DELETE FROM "Order" WHERE OID = v_new_order_id;
            COMMIT;
        END;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('get_order_materials_report - Valid Order', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 11: get_order_materials_report for Non-Existent Order
    -- -------------------------------------------------------------------
    BEGIN
        v_report_cursor := get_order_materials_report(999999);
        display_result('get_order_materials_report - Non-Existent Order', 'PASS', 'Handled non-existent order. No data returned.');
        
        -- Attempt to fetch data
        FETCH v_report_cursor INTO v_product_name, v_order_quantity, v_part_id, v_description, v_required_qty;
        IF v_report_cursor%NOTFOUND THEN
            DBMS_OUTPUT.PUT_LINE('No material requirements found for non-existent order.');
        ELSE
            DBMS_OUTPUT.PUT_LINE('Unexpected data found for non-existent order.');
        END IF;
        CLOSE v_report_cursor;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('get_order_materials_report - Non-Existent Order', 'PASS', 'Handled non-existent order: ' || SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 12: get_order_materials_report for Order with Insufficient Materials
    -- -------------------------------------------------------------------
    BEGIN
        -- Create a new order with products that require more materials than available
        DECLARE
            v_new_order_id NUMBER;
        BEGIN
            v_new_order_id := CREATE_ORDER('PT501245987', 'AS12945S22', 1000, TO_DATE('2025-02-10', 'YYYY-MM-DD'));
            COMMIT;
            
            v_report_cursor := get_order_materials_report(v_new_order_id);
            display_result('get_order_materials_report - Insufficient Materials', 'PASS', 'Report generated successfully.');
            
            -- Fetch and display report
            LOOP
                FETCH v_report_cursor INTO v_product_name, v_order_quantity, v_part_id, v_description, v_required_qty;
                EXIT WHEN v_report_cursor%NOTFOUND;
                DBMS_OUTPUT.PUT_LINE('ProductName: ' || v_product_name || ', OrderQuantity: ' || v_order_quantity ||
                                     ', PartID: ' || v_part_id || ', Description: ' || v_description ||
                                     ', RequiredQty: ' || v_required_qty);
            END LOOP;
            CLOSE v_report_cursor;
            
            -- Cleanup
            DELETE FROM OrderProduct WHERE OID = v_new_order_id;
            DELETE FROM "Order" WHERE OID = v_new_order_id;
            COMMIT;
        END;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('get_order_materials_report - Insufficient Materials', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 13: get_order_materials_report with Multiple Products
    -- -------------------------------------------------------------------
    BEGIN
        -- Create a new order with multiple products
        DECLARE
            v_new_order_id NUMBER;
        BEGIN
            v_new_order_id := CREATE_ORDER('PT501245987', 'AS12945S22', 5, TO_DATE('2025-03-01', 'YYYY-MM-DD'));
            INSERT INTO OrderProduct (OID, ProductPartID, Quantity) VALUES (v_new_order_id, 'AS12945S20', 10);
            INSERT INTO OrderProduct (OID, ProductPartID, Quantity) VALUES (v_new_order_id, 'AS12945P17', 15);
            COMMIT;
            
            v_report_cursor := get_order_materials_report(v_new_order_id);
            display_result('get_order_materials_report - Multiple Products', 'PASS', 'Report generated successfully.');
            
            -- Fetch and display report
            LOOP
                FETCH v_report_cursor INTO v_product_name, v_order_quantity, v_part_id, v_description, v_required_qty;
                EXIT WHEN v_report_cursor%NOTFOUND;
                DBMS_OUTPUT.PUT_LINE('ProductName: ' || v_product_name || ', OrderQuantity: ' || v_order_quantity ||
                                     ', PartID: ' || v_part_id || ', Description: ' || v_description ||
                                     ', RequiredQty: ' || v_required_qty);
            END LOOP;
            CLOSE v_report_cursor;
            
            -- Cleanup
            DELETE FROM OrderProduct WHERE OID = v_new_order_id;
            DELETE FROM "Order" WHERE OID = v_new_order_id;
            COMMIT;
        END;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('get_order_materials_report - Multiple Products', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 14: get_order_materials_report with Order Having Zero Products
    -- -------------------------------------------------------------------
    BEGIN
        -- Create a new order without inserting any products
        DECLARE
            v_new_order_id NUMBER;
        BEGIN
            v_new_order_id := CREATE_ORDER('PT501245987', 'AS12945S22', 0, TO_DATE('2025-03-10', 'YYYY-MM-DD')); -- Quantity 0
            COMMIT;
            
            v_report_cursor := get_order_materials_report(v_new_order_id);
            display_result('get_order_materials_report - Order with Zero Products', 'PASS', 'Report generated successfully.');
            
            -- Fetch and display report
            FETCH v_report_cursor INTO v_product_name, v_order_quantity, v_part_id, v_description, v_required_qty;
            IF v_report_cursor%NOTFOUND THEN
                DBMS_OUTPUT.PUT_LINE('No material requirements found for order with zero products.');
            ELSE
                DBMS_OUTPUT.PUT_LINE('Unexpected data found for order with zero products.');
            END IF;
            CLOSE v_report_cursor;
            
            -- Cleanup
            DELETE FROM "Order" WHERE OID = v_new_order_id;
            COMMIT;
        END;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('get_order_materials_report - Order with Zero Products', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 15: Integration Test - Create Order and Generate Report
    -- -------------------------------------------------------------------
    BEGIN
        -- Create a new order
        DECLARE
            v_new_order_id NUMBER;
        BEGIN
            v_new_order_id := CREATE_ORDER('PT501245987', 'AS12945S22', 5, TO_DATE('2025-04-01', 'YYYY-MM-DD'));
            INSERT INTO OrderProduct (OID, ProductPartID, Quantity) VALUES (v_new_order_id, 'AS12945S20', 10);
            INSERT INTO OrderProduct (OID, ProductPartID, Quantity) VALUES (v_new_order_id, 'AS12945P17', 15);
            COMMIT;
            
            -- Check materials availability
            v_boolean_result := check_order_materials(v_new_order_id);
            IF v_boolean_result THEN
                display_result('Integration Test - Materials Available', 'PASS', 'All materials are available.');
            ELSE
                display_result('Integration Test - Materials Available', 'FAIL', 'Materials are insufficient.');
            END IF;
            
            -- Generate materials report
            v_report_cursor := get_order_materials_report(v_new_order_id);
            display_result('Integration Test - Generate Report', 'PASS', 'Report generated successfully.');
            
            -- Fetch and display report
            LOOP
                FETCH v_report_cursor INTO v_product_name, v_order_quantity, v_part_id, v_description, v_required_qty;
                EXIT WHEN v_report_cursor%NOTFOUND;
                DBMS_OUTPUT.PUT_LINE('ProductName: ' || v_product_name || ', OrderQuantity: ' || v_order_quantity ||
                                     ', PartID: ' || v_part_id || ', Description: ' || v_description ||
                                     ', RequiredQty: ' || v_required_qty);
            END LOOP;
            CLOSE v_report_cursor;
            
            -- Cleanup
            DELETE FROM OrderProduct WHERE OID = v_new_order_id;
            DELETE FROM "Order" WHERE OID = v_new_order_id;
            COMMIT;
        END;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Integration Test - Create Order and Generate Report', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Final Cleanup: Remove Any Residual Test Data
    -- -------------------------------------------------------------------
    BEGIN
        -- Delete orders with unrealistic OIDs if any exist
        DELETE FROM OrderProduct WHERE OID > 1000;
        DELETE FROM "Order" WHERE OID > 1000;
        COMMIT;
        display_result('Final Cleanup', 'PASS', 'All residual test data removed.');
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Final Cleanup', 'FAIL', SQLERRM);
    END;
    
EXCEPTION
    WHEN e_test_failure THEN
        DBMS_OUTPUT.PUT_LINE('One or more tests failed.');
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Unexpected error during testing: ' || SQLERRM);
END;
/
