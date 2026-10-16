# Master Script Explanation: Setup, Function Creation, and Test Execution for create_order

## Overview
This document provides a comprehensive explanation of the Master Script designed to set up necessary types and functions, insert test data, and execute test cases for the create_order function in an Oracle database environment. The create_order function facilitates the creation of orders by validating customer status, ensuring product availability, and managing order transactions.

## Table of Contents
- Script Purpose
- Environment Settings
- Step-by-Step Breakdown
  - Step 1: Drop Existing create_order Function
  - Step 2: Drop Existing order_product_table Type
  - Step 3: Drop Existing order_product_rec Type
  - Step 4: Create order_product_rec Type
  - Step 5: Create order_product_table Type
  - Step 6: Insert Test Data into ClientType and Client Tables
  - Step 7: Create create_order Function
  - Step 8: Execute Test Cases
    - Test Case 1: Non-Existent Customer NIF
    - Test Case 2: Inactive Customer
    - Test Case 3: Non-Existent Product
    - Test Case 4: Empty Product List
    - Test Case 5: Valid Order Creation

## Script Purpose
The master script serves multiple purposes:

- Setup Environment: Drops existing functions and types to ensure a clean slate
- Define Data Structures: Creates necessary Oracle SQL types for handling order products
- Populate Test Data: Inserts predefined client types and clients to facilitate testing
- Function Creation: Defines the create_order function responsible for order creation with necessary validations
- Testing: Executes a series of test cases to validate the functionality and robustness of the create_order function

## Environment Settings

SET SERVEROUTPUT ON;
SET ECHO OFF;

- SET SERVEROUTPUT ON: Enables the display of output from DBMS_OUTPUT.PUT_LINE statements
- SET ECHO OFF: Prevents the display of the commands themselves, showing only the output

## Step-by-Step Breakdown

### Step 1: Drop Existing create_order Function

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


### Step 2: Drop Existing order_product_table Type

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


### Step 3: Drop Existing order_product_rec Type

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


### Step 4: Create order_product_rec Type

CREATE OR REPLACE TYPE order_product_rec AS OBJECT (
    product_part_id VARCHAR2(50),
    quantity        NUMBER
);
/


### Step 5: Create order_product_table Type

CREATE OR REPLACE TYPE order_product_table AS TABLE OF order_product_rec;
/


### Step 6: Insert Test Data

BEGIN
    -- Insert Client Types
    BEGIN
        DECLARE
            v_count NUMBER;
        BEGIN
            SELECT COUNT(*) INTO v_count FROM ClientType WHERE CID IN ('C1', 'C2');
            IF v_count < 2 THEN
                INSERT INTO ClientType (CID, Type) VALUES ('C1', 'Regular');
                INSERT INTO ClientType (CID, Type) VALUES ('C2', 'Premium');
                DBMS_OUTPUT.PUT_LINE('Client Types inserted successfully.');
            ELSE
                DBMS_OUTPUT.PUT_LINE('Client Types already exist. Skipping insertion.');
            END IF;
            COMMIT;
        END;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            DBMS_OUTPUT.PUT_LINE('Error inserting Client Types: ' || SQLERRM);
    END;
    
    -- Insert Clients for Testing
    BEGIN
        DECLARE
            v_count_active NUMBER;
            v_count_inactive NUMBER;
        BEGIN
            SELECT COUNT(*) INTO v_count_active FROM Client WHERE NIF = 'PT501245488';
            IF v_count_active = 0 THEN
                INSERT INTO Client (NIF, Name, Email, Phone, IDClient, CID, Status) 
                VALUES ('PT501245488', 'Active Client', 'active@example.com', 912345678, 1, 'C1', 1);
                DBMS_OUTPUT.PUT_LINE('Active Client inserted successfully.');
            ELSE
                DBMS_OUTPUT.PUT_LINE('Active Client already exists. Skipping insertion.');
            END IF;
            
            SELECT COUNT(*) INTO v_count_inactive FROM Client WHERE NIF = 'PT501245987';
            IF v_count_inactive = 0 THEN
                INSERT INTO Client (NIF, Name, Email, Phone, IDClient, CID, Status) 
                VALUES ('PT501245987', 'Inactive Client', 'inactive@example.com', 987654321, 2, 'C2', 1);
                DBMS_OUTPUT.PUT_LINE('Inactive Client inserted successfully.');
            ELSE
                DBMS_OUTPUT.PUT_LINE('Inactive Client already exists. Skipping insertion.');
            END IF;
            COMMIT;
        END;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            DBMS_OUTPUT.PUT_LINE('Error inserting Clients: ' || SQLERRM);
    END;
END;
/


### Step 7: Create create_order Function

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
    -- Validate Customer Status
    SELECT Status INTO v_status
    FROM Client
    WHERE NIF = p_client_nif;
    
    IF v_status != 1 THEN
        RAISE_APPLICATION_ERROR(-20001, 'Customer is not active.');
    END IF;

    -- Validate Products in Order
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

    -- Generate New Order ID (OID)
    SELECT NVL(MAX(OID), 0) + 1 INTO v_oid FROM "Order";

    -- Insert New Order
    INSERT INTO "Order" (OID, ClientNIF, DateOrder, DateDelivery)
    VALUES (v_oid, p_client_nif, p_date_order, p_date_delivery);

    -- Insert Products into OrderProduct Table
    FOR i IN 1 .. p_product_list.COUNT LOOP
        INSERT INTO OrderProduct (OID, ProductPartID, Quantity)
        VALUES (v_oid, p_product_list(i).product_part_id, p_product_list(i).quantity);
    END LOOP;

    COMMIT;
    RETURN v_oid;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20004, 'Customer with NIF ' || p_client_nif || ' does not exist.');
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END create_order;
/

Purpose:

Defines the create_order function, which facilitates the creation of orders by performing necessary validations and database operations.

Parameters:

* p_client_nif: Customer's NIF (Tax Identification Number).
* p_date_order: Date when the order is placed.
* p_date_delivery: Expected delivery date.
* p_product_list: List of products to be included in the order (order_product_table type).

Function Logic:

1. Validate Customer Status:
   * Retrieves the Status of the customer from the Client table.
   * Raises an error if the customer is not active (Status ≠ 1).

2. Validate Products in Order:
   * Ensures that the product list is not null or empty.
   * Iterates through each product in the list to verify its existence in the Product table.
   * Raises an error if any product does not exist.

3. Generate New Order ID (OID):
   * Retrieves the maximum existing OID from the Order table and increments it by 1 to generate a new OID.

4. Insert New Order:
   * Inserts a new record into the Order table with the generated OID and provided details.

5. Insert Products into OrderProduct Table:
   * Inserts each product from the p_product_list into the OrderProduct table, associating it with the new OID.

6. Commit Transaction:
   * Commits all changes to the database to ensure data integrity.

7. Return OID:
   * Returns the newly generated OID to confirm successful order creation.

Exception Handling:

* NO_DATA_FOUND: Triggered if the customer with the provided NIF does not exist.
* OTHERS: Catches all other exceptions, rolls back the transaction, and re-raises the error for further handling.

## Step 8: Execute Test Cases

This section contains multiple test cases designed to validate the create_order function under various scenarios, including edge cases and expected failures.

### Test Case 1: Non-Existent Customer NIF


BEGIN
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
                p_client_nif     => 'NONEXISTENTNIF',
                p_date_order     => TO_DATE('2024-11-17', 'YYYY-MM-DD'),
                p_date_delivery  => TO_DATE('2024-11-25', 'YYYY-MM-DD'),
                p_product_list   => v_product_list
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


Expected Output:

Test Case 1 Passed: ORA-20004: Customer with NIF NONEXISTENTNIF does not exist.


Objective:

* Verify that the create_order function correctly handles attempts to create an order with a non-existent customer NIF.

Expected Outcome:

* The function should raise an error (ORA-20004) indicating that the customer does not exist.

Result Interpretation:

* If the expected error is caught, the test passes. Otherwise, it fails by erroneously creating an order.

### Test Case 2: Inactive Customer


BEGIN
    -- Preparation: Inactivate a Customer
    BEGIN
        UPDATE Client
        SET Status = 0
        WHERE NIF = 'PT501245987';
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
                p_client_nif     => 'PT501245987',
                p_date_order     => TO_DATE('2024-11-17', 'YYYY-MM-DD'),
                p_date_delivery  => TO_DATE('2024-11-25', 'YYYY-MM-DD'),
                p_product_list   => v_product_list
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
        WHERE NIF = 'PT501245987';
        COMMIT;
        DBMS_OUTPUT.PUT_LINE('Cleanup for Test Case 2: Customer PT501245987 reactivated.');
    END;
END;
/


Expected Output:

Preparation for Test Case 2: Customer PT501245987 set to inactive.
Test Case 2 Passed: ORA-20001: Customer is not active.
Cleanup for Test Case 2: Customer PT501245987 reactivated.


Objective:

* Ensure that the create_order function does not allow order creation for inactive customers.

Procedure:

1. Preparation: Set the status of customer PT501245987 to inactive (Status = 0).
2. Test Execution: Attempt to create an order for the now inactive customer.
3. Cleanup: Restore the customer's status to active (Status = 1) after the test.

Expected Outcome:

* The function should raise an error (ORA-20001) indicating that the customer is not active.

Result Interpretation:

* Successful inactivation, correct error handling during order creation, and proper reactivation signify a passing test.

### Test Case 3: Non-Existent Product


BEGIN
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
                p_client_nif     => 'PT501245488',
                p_date_order     => TO_DATE('2024-11-17', 'YYYY-MM-DD'),
                p_date_delivery  => TO_DATE('2024-11-25', 'YYYY-MM-DD'),
                p_product_list   => v_product_list
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


Expected Output:

Test Case 3 Passed: ORA-20003: Product NONEXISTENTPRODUCT is not available in the current product line-up.


Objective:

* Validate that the create_order function rejects orders containing products that do not exist in the Product table.

Procedure:

1. Create a product list containing both a valid product (AS12945S22) and a non-existent product (NONEXISTENTPRODUCT).
2. Attempt to create an order with this product list.

Expected Outcome:

* The function should raise an error (ORA-20003) indicating that the specified non-existent product is unavailable.

Result Interpretation:

* Proper error detection and messaging confirm that the function correctly handles invalid products.

### Test Case 4: Empty Product List


BEGIN
    DECLARE
        v_product_list order_product_table := order_product_table();
    BEGIN
        -- Attempt to create an order with an empty product list
        DECLARE
            v_new_oid NUMBER;
        BEGIN
            v_new_oid := create_order(
                p_client_nif     => 'PT501245488',
                p_date_order     => TO_DATE('2024-11-17', 'YYYY-MM-DD'),
                p_date_delivery  => TO_DATE('2024-11-25', 'YYYY-MM-DD'),
                p_product_list   => v_product_list
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


Expected Output:

Test Case 4 Passed: ORA-20002: Order must contain at least one product.


Objective:

* Ensure that the create_order function does not allow the creation of orders without any products.

Procedure:

* Attempt to create an order with an empty product_list.

Expected Outcome:

* The function should raise an error (ORA-20002) stating that the order must contain at least one product.

Result Interpretation:

* Proper validation and error messaging confirm that orders cannot be created without products.

### Test Case 5: Valid Order Creation


BEGIN
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
            p_client_nif     => 'PT501245488',
            p_date_order     => TO_DATE('2024-11-17', 'YYYY-MM-DD'),
            p_date_delivery  => TO_DATE('2024-11-25', 'YYYY-MM-DD'),
            p_product_list   => v_product_list
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


Expected Output:

Test Case 5 Passed: Order created successfully. OID: <new_order_id>


Objective:

* Confirm that the create_order function successfully creates an order when provided with valid inputs.

Procedure:

1. Create a product list containing two valid products (AS12945S22 and AS12945S20).
2. Attempt to create an order with a valid customer NIF and valid product list.

Expected Outcome:

* The function should successfully create the order and return a new OID.

Result Interpretation:

* Successful creation and retrieval of OID indicate that the function operates correctly under valid conditions.

## Conclusion

The Master Script meticulously sets up the necessary database environment by ensuring that existing functions and types are appropriately managed. It defines essential data structures and populates the database with requisite test data to facilitate comprehensive testing of the create_order function. The series of test cases systematically validate the function's ability to handle various scenarios, including error conditions and successful order creation. This structured approach ensures that the create_order function is robust, reliable, and ready for integration into the broader application ecosystem.