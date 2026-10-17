-- ===========================================================================
-- Test Case 1: Create Order with Non-Existent Customer NIF
-- Purpose : To verify that the function correctly handles cases where the
--           provided Customer NIF does not exist in the Client table.
-- Expected Output:
--   Error creating order: ORA-20004: Customer with NIF NONEXISTENTNIF does not exist.
-- ===========================================================================
SET SERVEROUTPUT ON;

BEGIN
    -- Initialize a collection to hold order products
    DECLARE
        v_product_list order_product_table := order_product_table();
    BEGIN
        -- Add a valid product to the order
        v_product_list.EXTEND;
        v_product_list(v_product_list.COUNT) := order_product_rec('AS12945S22', 10);
        
        -- Attempt to create an order with a non-existent Customer NIF
        create_order(
            p_client_nif     => 'NONEXISTENTNIF',                       -- Invalid Customer NIF
            p_date_order     => TO_DATE('2024-11-17', 'YYYY-MM-DD'),    -- Order Date
            p_date_delivery  => TO_DATE('2024-11-25', 'YYYY-MM-DD'),    -- Delivery Date
            p_product_list   => v_product_list                         -- List of Products
        );
        
        -- If no error, print success message
        DBMS_OUTPUT.PUT_LINE('Test Case 1 Failed: Order should not have been created.');
    EXCEPTION
        WHEN OTHERS THEN
            -- Display the error message
            DBMS_OUTPUT.PUT_LINE('Test Case 1 Passed: ' || SQLERRM);
    END;
END;
/
