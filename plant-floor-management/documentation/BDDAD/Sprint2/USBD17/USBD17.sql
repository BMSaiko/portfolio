-- ===========================================================================
-- Master Script: Setup Types, Function, and Execute Test Cases
-- Purpose      : Drops existing function and types if they exist, recreates them,
--                and runs test cases for the create_order function.
-- ===========================================================================
SET SERVEROUTPUT ON;
SET ECHO OFF;

-- ===========================================================================
-- Step 1: Drop the create_order Function if it Exists
-- ===========================================================================
BEGIN
    EXECUTE IMMEDIATE 'DROP FUNCTION create_order';
    DBMS_OUTPUT.PUT_LINE('Function create_order dropped successfully.');
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE = -04043 THEN -- ORA-04043: object does not exist
            DBMS_OUTPUT.PUT_LINE('Function create_order does not exist. Skipping drop.');
        ELSE
            RAISE;
        END IF;
END;
/
    
-- ===========================================================================
-- Step 2: Drop the order_product_table Type if it Exists
-- ===========================================================================
BEGIN
    EXECUTE IMMEDIATE 'DROP TYPE order_product_table FORCE';
    DBMS_OUTPUT.PUT_LINE('Type order_product_table dropped successfully.');
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE = -4043 THEN -- ORA-04043: object does not exist
            DBMS_OUTPUT.PUT_LINE('Type order_product_table does not exist. Skipping drop.');
        ELSE
            RAISE;
        END IF;
END;
/

-- ===========================================================================
-- Step 3: Drop the order_product_rec Type if it Exists
-- ===========================================================================
BEGIN
    EXECUTE IMMEDIATE 'DROP TYPE order_product_rec FORCE';
    DBMS_OUTPUT.PUT_LINE('Type order_product_rec dropped successfully.');
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE = -4043 THEN -- ORA-04043: object does not exist
            DBMS_OUTPUT.PUT_LINE('Type order_product_rec does not exist. Skipping drop.');
        ELSE
            RAISE;
        END IF;
END;
/

-- ===========================================================================
-- Step 4: Create the order_product_rec Type
-- ===========================================================================
CREATE OR REPLACE TYPE order_product_rec AS OBJECT (
    product_part_id VARCHAR2(50),
    quantity        NUMBER
);
/
-- Type created successfully.

-- ===========================================================================
-- Step 5: Create the order_product_table Type
-- ===========================================================================
CREATE OR REPLACE TYPE order_product_table AS TABLE OF order_product_rec;
/
-- Type created successfully.

-- ===========================================================================
-- Step 6: Create the create_order Function
-- ===========================================================================
CREATE OR REPLACE FUNCTION create_order(
    p_client_nif     IN VARCHAR2,
    p_date_order     IN DATE,
    p_date_delivery  IN DATE,
    p_product_list   IN order_product_table
) RETURN NUMBER
IS
    v_oid           NUMBER;
    v_status        NUMBER;
    v_product_count NUMBER;
BEGIN
    -- ---------------------------
    -- Validate Customer Status
    -- ---------------------------
    SELECT Status INTO v_status
    FROM Client
    WHERE NIF = p_client_nif;
    
    IF v_status != 1 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Customer is not active.');
    END IF;

    -- ---------------------------
    -- Validate Products in Order
    -- ---------------------------
    IF p_product_list IS NULL OR p_product_list.COUNT = 0 THEN
        RAISE_APPLICATION_ERROR(-20002, 'Order must contain at least one product.');
    END IF;

    FOR i IN 1 .. p_product_list.COUNT LOOP
        SELECT COUNT(*) INTO v_product_count
        FROM Product
        WHERE PartID = p_product_list(i).product_part_id;
        
        IF v_product_count = 0 THEN
            RAISE_APPLICATION_ERROR(-20003, 'Product ' || p_product_list(i).product_part_id || ' is not available in the current product line-up.');
        END IF;
    END LOOP;

    -- ---------------------------
    -- Generate New Order ID (OID)
    -- ---------------------------
    SELECT NVL(MAX(OID), 0) + 1 INTO v_oid FROM "Order";

    -- ---------------------------
    -- Insert New Order into "Order" Table
    -- ---------------------------
    INSERT INTO "Order" (OID, ClientNIF, DateOrder, DateDelivery)
    VALUES (v_oid, p_client_nif, p_date_order, p_date_delivery);

    -- ---------------------------
    -- Insert Products into OrderProduct Table
    -- ---------------------------
    FOR i IN 1 .. p_product_list.COUNT LOOP
        INSERT INTO OrderProduct (OID, ProductPartD, Quantity)
        VALUES (v_oid, p_product_list(i).product_part_id, p_product_list(i).quantity);
    END LOOP;

    -- ---------------------------
    -- Commit the Transaction
    -- ---------------------------
    COMMIT;

    -- ---------------------------
    -- Return the Newly Generated OID
    -- ---------------------------
    RETURN v_oid;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20004, 'Customer with NIF ' || p_client_nif || ' does not exist.');
    WHEN OTHERS THEN
        -- Rollback in case of any unexpected errors
        ROLLBACK;
        RAISE;
END create_order;
/
-- Function created successfully.

-- ===========================================================================
-- Step 7: Run Test Cases
-- ===========================================================================
-- Each test case is provided as a separate anonymous block.
-- To execute multiple test cases, run each block sequentially.

-- ---------------------------------------------------------------------------
-- Test Case 1: Non-Existent Customer NIF
-- ---------------------------------------------------------------------------
BEGIN
    -- Initialize a collection to hold order products
    DECLARE
        v_product_list order_product_table := order_product_table();
    BEGIN
        -- Add a valid product to the order
        v_product_list.EXTEND;
        v_product_list(v_product_list.COUNT) := order_product_rec('AS12945S22', 10);
        
        -- Attempt to create an order with a non-existent Customer NIF
        DECLARE
            v_new_oid NUMBER;
        BEGIN
            v_new_oid := create_order(
                p_client_nif     => 'NONEXISTENTNIF',                       -- Invalid Customer NIF
                p_date_order     => TO_DATE('2024-11-17', 'YYYY-MM-DD'),    -- Order Date
                p_date_delivery  => TO_DATE('2024-11-25', 'YYYY-MM-DD'),    -- Delivery Date
                p_product_list   => v_product_list                         -- List of Products
            );
            
            -- If no error, print failure message
            DBMS_OUTPUT.PUT_LINE('Test Case 1 Failed: Order should not have been created.');
        EXCEPTION
            WHEN OTHERS THEN
                -- Display the error message
                DBMS_OUTPUT.PUT_LINE('Test Case 1 Passed: ' || SQLERRM);
        END;
    END;
END;
/
-- Expected Output:
-- Test Case 1 Passed: ORA-20004: Customer with NIF NONEXISTENTNIF does not exist.

-- ---------------------------------------------------------------------------
-- Test Case 2: Inactive Customer
-- ---------------------------------------------------------------------------
BEGIN
    -- Preparation: Inactivate a Customer
    BEGIN
        UPDATE Client
        SET Status = 0
        WHERE NIF = 'PT501245987'; -- Assuming this NIF exists
        COMMIT;
        DBMS_OUTPUT.PUT_LINE('Preparation for Test Case 2: Customer PT501245987 set to inactive.');
    END;
    
    -- Test Case 2: Create Order with Inactive Customer
    DECLARE
        v_product_list order_product_table := order_product_table();
    BEGIN
        -- Add a valid product to the order
        v_product_list.EXTEND;
        v_product_list(v_product_list.COUNT) := order_product_rec('AS12945S22', 10);
        
        -- Attempt to create an order with an inactive Customer NIF
        DECLARE
            v_new_oid NUMBER;
        BEGIN
            v_new_oid := create_order(
                p_client_nif     => 'PT501245987',                        -- Inactive Customer NIF
                p_date_order     => TO_DATE('2024-11-17', 'YYYY-MM-DD'),   -- Order Date
                p_date_delivery  => TO_DATE('2024-11-25', 'YYYY-MM-DD'),   -- Delivery Date
                p_product_list   => v_product_list                        -- List of Products
            );
            
            -- If no error, print failure message
            DBMS_OUTPUT.PUT_LINE('Test Case 2 Failed: Order should not have been created.');
        EXCEPTION
            WHEN OTHERS THEN
                -- Display the error message
                DBMS_OUTPUT.PUT_LINE('Test Case 2 Passed: ' || SQLERRM);
        END;
    END;
    
    -- Cleanup: Reactivate the Customer
    BEGIN
        UPDATE Client
        SET Status = 1
        WHERE NIF = 'PT501245987'; -- Reverting back to active
        COMMIT;
        DBMS_OUTPUT.PUT_LINE('Cleanup for Test Case 2: Customer PT501245987 reactivated.');
    END;
END;
/
-- Expected Output:
-- Preparation for Test Case 2: Customer PT501245987 set to inactive.
-- Test Case 2 Passed: ORA-20001: Customer is not active.
-- Cleanup for Test Case 2: Customer PT501245987 reactivated.

-- ---------------------------------------------------------------------------
-- Test Case 3: Non-Existent Product
-- ---------------------------------------------------------------------------
BEGIN
    -- Initialize a collection to hold order products
    DECLARE
        v_product_list order_product_table := order_product_table();
    BEGIN
        -- Add a valid product to the order
        v_product_list.EXTEND;
        v_product_list(v_product_list.COUNT) := order_product_rec('AS12945S22', 10);
        
        -- Add a non-existent product to the order
        v_product_list.EXTEND;
        v_product_list(v_product_list.COUNT) := order_product_rec('NONEXISTENTPRODUCT', 5);
        
        -- Attempt to create an order with a non-existent product
        DECLARE
            v_new_oid NUMBER;
        BEGIN
            v_new_oid := create_order(
                p_client_nif     => 'PT501245488',                        -- Valid Customer NIF
                p_date_order     => TO_DATE('2024-11-17', 'YYYY-MM-DD'),   -- Order Date
                p_date_delivery  => TO_DATE('2024-11-25', 'YYYY-MM-DD'),   -- Delivery Date
                p_product_list   => v_product_list                        -- List of Products
            );
            
            -- If no error, print failure message
            DBMS_OUTPUT.PUT_LINE('Test Case 3 Failed: Order should not have been created.');
        EXCEPTION
            WHEN OTHERS THEN
                -- Display the error message
                DBMS_OUTPUT.PUT_LINE('Test Case 3 Passed: ' || SQLERRM);
        END;
    END;
END;
/
-- Expected Output:
-- Test Case 3 Passed: ORA-20003: Product NONEXISTENTPRODUCT is not available in the current product line-up.

-- ---------------------------------------------------------------------------
-- Test Case 4: Empty Product List
-- ---------------------------------------------------------------------------
BEGIN
    -- Initialize an empty collection to hold order products
    DECLARE
        v_product_list order_product_table := order_product_table();
    BEGIN
        -- Attempt to create an order with an empty product list
        DECLARE
            v_new_oid NUMBER;
        BEGIN
            v_new_oid := create_order(
                p_client_nif     => 'PT501245488',                        -- Valid Customer NIF
                p_date_order     => TO_DATE('2024-11-17', 'YYYY-MM-DD'),   -- Order Date
                p_date_delivery  => TO_DATE('2024-11-25', 'YYYY-MM-DD'),   -- Delivery Date
                p_product_list   => v_product_list                        -- Empty List of Products
            );
            
            -- If no error, print failure message
            DBMS_OUTPUT.PUT_LINE('Test Case 4 Failed: Order should not have been created.');
        EXCEPTION
            WHEN OTHERS THEN
                -- Display the error message
                DBMS_OUTPUT.PUT_LINE('Test Case 4 Passed: ' || SQLERRM);
        END;
    END;
END;
/
-- Expected Output:
-- Test Case 4 Passed: ORA-20002: Order must contain at least one product.

-- ---------------------------------------------------------------------------
-- Test Case 5: Valid Order Creation
-- ---------------------------------------------------------------------------
BEGIN
    -- Initialize a collection to hold order products
    DECLARE
        v_product_list order_product_table := order_product_table();
        v_new_oid NUMBER;
    BEGIN
        -- Add valid products to the order
        v_product_list.EXTEND;
        v_product_list(v_product_list.COUNT) := order_product_rec('AS12945S22', 10);
        
        v_product_list.EXTEND;
        v_product_list(v_product_list.COUNT) := order_product_rec('AS12945S20', 5);
        
        -- Attempt to create an order with valid inputs
        v_new_oid := create_order(
            p_client_nif     => 'PT501245488',                        -- Valid Customer NIF
            p_date_order     => TO_DATE('2024-11-17', 'YYYY-MM-DD'),   -- Order Date
            p_date_delivery  => TO_DATE('2024-11-25', 'YYYY-MM-DD'),   -- Delivery Date
            p_product_list   => v_product_list                        -- List of Products
        );
        
        -- Display the new Order ID
        DBMS_OUTPUT.PUT_LINE('Test Case 5 Passed: Order created successfully. OID: ' || v_new_oid);
    EXCEPTION
        WHEN OTHERS THEN
            -- Display the error message
            DBMS_OUTPUT.PUT_LINE('Test Case 5 Failed: ' || SQLERRM);
    END;
END;
/
-- Expected Output:
-- Test Case 5 Passed: Order created successfully. OID: <new_order_id>
