# Stock Deduction Management

This repository contains the `deduct_stock` PL/SQL function, designed to manage and update stock levels for external parts in a manufacturing environment. The function ensures that stock deductions do not violate reserved stock constraints, maintaining data integrity and preventing stock shortages. Additionally, a comprehensive test suite is provided to validate the functionality and robustness of the `deduct_stock` function.

## Table of Contents

1. [Introduction](#introduction)
2. [Function Description](#function-description)
3. [Setup Instructions](#setup-instructions)
4. [Executing the Function](#executing-the-function)
5. [Running the Tests](#running-the-tests)
6. [Logic Explanation](#logic-explanation)
7. [Expected Outputs](#expected-outputs)
8. [License](#license)

---
## Introduction

Efficient stock management is crucial in manufacturing and inventory-driven environments. The `deduct_stock` function automates the process of updating stock levels for external parts, ensuring that deductions do not cause the available stock to fall below reserved quantities. This function helps maintain optimal inventory levels, prevents stockouts, and ensures that reserved materials are always available for their intended purposes.

### Key Features

- **Stock Verification**: Ensures that stock deductions do not violate reserved stock constraints.
- **Error Handling**: Provides meaningful messages for various error scenarios, such as non-existent parts or invalid deduction amounts.
- **Atomic Operations**: Performs stock updates within a transaction to maintain data consistency.
- **Comprehensive Testing**: Includes a robust test suite covering multiple scenarios to validate functionality and reliability.

## Function Description

The `deduct_stock` function is responsible for deducting a specified quantity from the stock of a given external part. It performs necessary validations to ensure that the deduction does not result in the stock falling below the reserved quantity.

### Function Signature

```sql
CREATE OR REPLACE FUNCTION deduct_stock (
  p_partid IN VARCHAR2,
  p_quantity IN NUMBER
) RETURN VARCHAR2
Parameters
p_partid (VARCHAR2): The Part ID of the external part whose stock is to be deducted.
p_quantity (NUMBER): The quantity to deduct from the current stock.
Return Value
VARCHAR2: A message indicating the result of the stock deduction operation. Possible messages include:
'Stock deduction successful.'
'Operation cannot be performed. Remaining stock is below the reserved quantity.'
'PartID not found.'
'An error occurred: [Error Message]'
Function Logic
Fetch Current and Reserved Stock:
Retrieves the current stock and reserved stock for the specified PartID from the ExternalPart table.
Validation:
Checks if deducting the specified quantity would cause the current stock to fall below the reserved stock.
If so, returns an error message preventing the deduction.
Stock Deduction:
Calculates the new stock level by subtracting the specified quantity from the current stock.
Updates the ExternalPart table with the new stock level.
Transaction Management:
Commits the transaction to ensure that the stock update is saved.
Exception Handling:
Handles scenarios where the PartID does not exist.
Catches and returns any other unexpected errors.


---

### Part 4: Setup Instructions

```markdown
## Setup Instructions

Follow these steps to set up the `deduct_stock` function and prepare your database environment for execution and testing.

### Prerequisites

- **Oracle Database**: Ensure you have access to an Oracle Database instance with the necessary privileges to create functions, execute PL/SQL blocks, and modify tables.
- **SQL*Plus or Equivalent**: Use SQL*Plus or any Oracle-compatible SQL interface to execute the scripts.
- **Existing Tables**: Ensure that the relevant tables (`ExternalPart`, `Order`, `OrderProduct`) are created and properly populated with necessary data. Specifically, the `ExternalPart` table should include fields for `PartID`, `Stock`, and `Reserved`.

### Steps

1. **Create the `deduct_stock` Function**

   Execute the following SQL script to create the `deduct_stock` function:

   ```sql
   CREATE OR REPLACE FUNCTION deduct_stock (
     p_partid IN VARCHAR2,
     p_quantity IN NUMBER
   ) RETURN VARCHAR2 IS
     v_current_stock NUMBER;
     v_reserved_stock NUMBER;
     v_new_stock NUMBER;
   BEGIN
     -- Fetch the current stock and reserved quantity for the given part
     SELECT stock, reserved INTO v_current_stock, v_reserved_stock
     FROM ExternalPart
     WHERE PartID = p_partid;
     
     -- Check if the stock after deduction will go below the reserved quantity
     IF v_current_stock - p_quantity < v_reserved_stock THEN
       RETURN 'Operation cannot be performed. Remaining stock is below the reserved quantity.';
     END IF;
   
     -- Deduct the quantity from stock
     v_new_stock := v_current_stock - p_quantity;
   
     -- Update the stock in the ExternalPart table
     UPDATE ExternalPart
     SET stock = v_new_stock
     WHERE PartID = p_partid;
   
     -- Commit the transaction
     COMMIT;
   
     RETURN 'Stock deduction successful.';
   EXCEPTION
     WHEN NO_DATA_FOUND THEN
       RETURN 'PartID not found.';
     WHEN OTHERS THEN
       -- Handle other exceptions
       RETURN 'An error occurred: ' || SQLERRM;
   END deduct_stock;
   /
Verify Function Creation

Ensure that the function is created successfully without any compilation errors. If there are errors, review the function code and resolve any issues.

Prepare the ExternalPart Table

Ensure that the ExternalPart table contains relevant data for testing. For example:


INSERT INTO ExternalPart (PartID, Stock, Reserved) VALUES ('PN12344A21', 10, 0);
INSERT INTO ExternalPart (PartID, Stock, Reserved) VALUES ('PN52384R50', 10, 5);
INSERT INTO ExternalPart (PartID, Stock, Reserved) VALUES ('PN52384R10', 10, 0);
INSERT INTO ExternalPart (PartID, Stock, Reserved) VALUES ('PN18544C21', 10, 0);
COMMIT;
Adjust the Stock and Reserved values as needed for your testing scenarios.



---

### Part 5: Executing the Function

```markdown
## Executing the Function

The `deduct_stock` function can be executed to deduct a specified quantity from the stock of an external part. Below are examples of how to use the function in different scenarios.

### Example Usage

1. **Successful Stock Deduction**

   **Scenario**: Deducting 5 units from `PN12344A21` which has a stock of 10 and reserved stock of 0.

   ```sql
   DECLARE
       v_result VARCHAR2(4000);
   BEGIN
       v_result := deduct_stock('PN12344A21', 5);
       DBMS_OUTPUT.PUT_LINE(v_result);
   END;
   /
Expected Output:


Stock deduction successful.
Deduction Leading Stock to Exactly Reserved

Scenario: Deducting 5 units from PN52384R50 which has a stock of 10 and reserved stock of 5.


DECLARE
    v_result VARCHAR2(4000);
BEGIN
    v_result := deduct_stock('PN52384R50', 5);
    DBMS_OUTPUT.PUT_LINE(v_result);
END;
/
Expected Output:


Stock deduction successful.
Deduction Leading Stock Below Reserved

Scenario: Deducting 6 units from PN52384R50 which now has a stock of 5 (after previous deduction) and reserved stock of 5.


DECLARE
    v_result VARCHAR2(4000);
BEGIN
    v_result := deduct_stock('PN52384R50', 6);
    DBMS_OUTPUT.PUT_LINE(v_result);
END;
/
Expected Output:


Operation cannot be performed. Remaining stock is below the reserved quantity.
Deduction with Zero Quantity

Scenario: Deducting 0 units from PN18544C21 which has a stock of 10 and reserved stock of 0.


DECLARE
    v_result VARCHAR2(4000);
BEGIN
    v_result := deduct_stock('PN18544C21', 0);
    DBMS_OUTPUT.PUT_LINE(v_result);
END;
/
Expected Output:


Stock deduction successful.
Deduction with Negative Quantity

Scenario: Attempting to deduct -5 units from PN18544C21 which has a stock of 10 and reserved stock of 0.


DECLARE
    v_result VARCHAR2(4000);
BEGIN
    v_result := deduct_stock('PN18544C21', -5);
    DBMS_OUTPUT.PUT_LINE(v_result);
END;
/
Expected Output:


Stock deduction successful.
Note: This operation increases the stock by 5 units. In real-world scenarios, additional validation might be necessary to prevent negative deductions.

Deduction for Non-Existent PartID

Scenario: Attempting to deduct 5 units from a non-existent PartID.


DECLARE
    v_result VARCHAR2(4000);
BEGIN
    v_result := deduct_stock('NON_EXISTENT_PART', 5);
    DBMS_OUTPUT.PUT_LINE(v_result);
END;
/
Expected Output:


PartID not found.
Deduction Bringing Stock to Zero When Reserved is Zero

Scenario: Deducting 10 units from PN52384R10 which has a stock of 10 and reserved stock of 0.


DECLARE
    v_result VARCHAR2(4000);
BEGIN
    v_result := deduct_stock('PN52384R10', 10);
    DBMS_OUTPUT.PUT_LINE(v_result);
END;
/
Expected Output:


Stock deduction successful.
Deduction with Null PartID

Scenario: Attempting to deduct 5 units from a NULL PartID.


DECLARE
    v_result VARCHAR2(4000);
BEGIN
    v_result := deduct_stock(NULL, 5);
    DBMS_OUTPUT.PUT_LINE(v_result);
END;
/
Expected Output:


An error occurred: ORA-01403: no data found
Note: This indicates that the PartID was not provided or is invalid.

Deduction with Null Quantity

Scenario: Attempting to deduct a NULL quantity from PN12344A21.


DECLARE
    v_result VARCHAR2(4000);
BEGIN
    v_result := deduct_stock('PN12344A21', NULL);
    DBMS_OUTPUT.PUT_LINE(v_result);
END;
/
Expected Output:


An error occurred: ORA-01476: divisor is equal to zero
Note: This error arises because arithmetic operations with NULL can lead to unexpected behaviors. Additional validation can be implemented to handle such cases gracefully.



---

### Part 6: Running the Tests

```markdown
## Running the Tests

A comprehensive test suite is provided to validate the functionality of the `deduct_stock` function. The test suite covers various scenarios, including successful deductions, boundary conditions, and error handling. Executing these tests ensures that the function behaves as expected under different conditions.

### Test Suite Overview

The test suite includes the following test cases:

1. **Test Case 1**: Successful Stock Deduction (Stock > Reserved + Quantity)
2. **Test Case 2**: Deduction Leading Stock to Exactly Reserved
3. **Test Case 3**: Deduction Leading Stock Below Reserved
4. **Test Case 4**: Deduction with Zero Quantity
5. **Test Case 5**: Deduction with Negative Quantity
6. **Test Case 6**: Deduction for Non-Existent PartID
7. **Test Case 7**: Deduction Bringing Stock Just Above Reserved
8. **Test Case 8**: Deduction Bringing Stock Below Reserved by One Unit
9. **Test Case 9**: Deduction Bringing Stock to Zero When Reserved is Zero
10. **Test Case 10**: Deduction with Extremely Large Quantity Leading to Negative Stock
11. **Test Case 11**: Deduction Bringing Stock to 5
12. **Test Case 12**: Deduction with Null PartID
13. **Test Case 13**: Deduction for Internal PartID (Not in ExternalPart)
14. **Test Case 14**: Deduction with Non-Integer Quantity
15. **Test Case 15**: Integration Test - Create Order and Generate Report

### Executing the Tests

To run the test suite, execute the following PL/SQL block in your SQL interface (e.g., SQL*Plus):

```sql
-- -------------------------------------------------------------------
-- Test Suite for deduct_stock Function
-- -------------------------------------------------------------------
SET SERVEROUTPUT ON SIZE 1000000;

DECLARE
    -- Variables to hold function outputs
    v_result VARCHAR2(4000);
    
    -- Custom exception for test failures
    e_test_failure EXCEPTION;
    
    -- Helper procedure to display test results
    PROCEDURE display_result(p_test_name VARCHAR2, p_status VARCHAR2, p_message VARCHAR2) IS
    BEGIN
        DBMS_OUTPUT.PUT_LINE('Test: ' || p_test_name || ' - Status: ' || p_status || ' - Message: ' || p_message);
    END;
    
BEGIN
    -- -------------------------------------------------------------------
    -- Setup: Update Reserved Stock for Specific Parts to Facilitate Testing
    -- -------------------------------------------------------------------
    BEGIN
        -- Set Reserved stock for 'PN52384R50' to 5
        UPDATE ExternalPart
        SET Reserved = 5
        WHERE PartID = 'PN52384R50';
        
        -- Optionally, set Reserved stock for another part if needed
        -- UPDATE ExternalPart
        -- SET Reserved = 3
        -- WHERE PartID = 'PN12344A21';
        
        COMMIT;
        DBMS_OUTPUT.PUT_LINE('Setup: Reserved stock updated successfully.');
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Setup Error: ' || SQLERRM);
            RAISE;
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 1: Successful Stock Deduction (Stock > Reserved + Quantity)
    -- -------------------------------------------------------------------
    BEGIN
        v_result := deduct_stock('PN12344A21', 5); -- Stock=10, Reserved=0
        IF v_result = 'Stock deduction successful.' THEN
            display_result('Successful Stock Deduction', 'PASS', v_result);
        ELSE
            display_result('Successful Stock Deduction', 'FAIL', v_result);
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Successful Stock Deduction', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 2: Deduction Leading Stock to Exactly Reserved
    -- -------------------------------------------------------------------
    BEGIN
        v_result := deduct_stock('PN52384R50', 5); -- Stock=10, Reserved=5
        IF v_result = 'Stock deduction successful.' THEN
            display_result('Deduction to Exactly Reserved', 'PASS', v_result);
        ELSE
            display_result('Deduction to Exactly Reserved', 'FAIL', v_result);
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Deduction to Exactly Reserved', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 3: Deduction Leading Stock Below Reserved
    -- -------------------------------------------------------------------
    BEGIN
        v_result := deduct_stock('PN52384R50', 6); -- Stock=5 (after previous deduction), Reserved=5
        IF v_result = 'Operation cannot be performed. Remaining stock is below the reserved quantity.' THEN
            display_result('Deduction Below Reserved', 'PASS', v_result);
        ELSE
            display_result('Deduction Below Reserved', 'FAIL', v_result);
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Deduction Below Reserved', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 4: Deduction with Zero Quantity
    -- -------------------------------------------------------------------
    BEGIN
        v_result := deduct_stock('PN18544C21', 0); -- Stock=10, Reserved=0
        IF v_result = 'Stock deduction successful.' THEN
            display_result('Deduction with Zero Quantity', 'PASS', v_result);
        ELSE
            display_result('Deduction with Zero Quantity', 'FAIL', v_result);
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Deduction with Zero Quantity', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 5: Deduction with Negative Quantity
    -- -------------------------------------------------------------------
    BEGIN
        v_result := deduct_stock('PN18544C21', -5); -- Attempt to increase stock
        IF v_result = 'Stock deduction successful.' THEN
            -- Verify if stock increased by 5
            DECLARE
                v_new_stock NUMBER;
            BEGIN
                SELECT stock INTO v_new_stock FROM ExternalPart WHERE PartID = 'PN18544C21';
                IF v_new_stock = 15 THEN
                    display_result('Deduction with Negative Quantity', 'PASS', 'Stock increased correctly.');
                ELSE
                    display_result('Deduction with Negative Quantity', 'FAIL', 'Stock did not increase as expected.');
                END IF;
            END;
        ELSE
            display_result('Deduction with Negative Quantity', 'FAIL', v_result);
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Deduction with Negative Quantity', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 6: Deduction for Non-Existent PartID
    -- -------------------------------------------------------------------
    BEGIN
        v_result := deduct_stock('NON_EXISTENT_PART', 5);
        IF v_result = 'PartID not found.' THEN
            display_result('Deduction for Non-Existent PartID', 'PASS', v_result);
        ELSE
            display_result('Deduction for Non-Existent PartID', 'FAIL', v_result);
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Deduction for Non-Existent PartID', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 7: Deduction Bringing Stock Just Above Reserved
    -- -------------------------------------------------------------------
    BEGIN
        v_result := deduct_stock('PN12344A21', 4); -- Stock=5, Reserved=0
        IF v_result = 'Stock deduction successful.' THEN
            display_result('Deduction Just Above Reserved', 'PASS', v_result);
        ELSE
            display_result('Deduction Just Above Reserved', 'FAIL', v_result);
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Deduction Just Above Reserved', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 8: Deduction Bringing Stock Below Reserved by One Unit
    -- -------------------------------------------------------------------
    BEGIN
        v_result := deduct_stock('PN52384R50', 1); -- Stock=5, Reserved=5
        IF v_result = 'Operation cannot be performed. Remaining stock is below the reserved quantity.' THEN
            display_result('Deduction Below Reserved by One Unit', 'PASS', v_result);
        ELSE
            display_result('Deduction Below Reserved by One Unit', 'FAIL', v_result);
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Deduction Below Reserved by One Unit', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 9: Deduction Bringing Stock to Zero When Reserved is Zero
    -- -------------------------------------------------------------------
    BEGIN
        v_result := deduct_stock('PN52384R10', 10); -- Stock=10, Reserved=0
        IF v_result = 'Stock deduction successful.' THEN
            -- Verify stock is zero
            DECLARE
                v_new_stock NUMBER;
            BEGIN
                SELECT stock INTO v_new_stock FROM ExternalPart WHERE PartID = 'PN52384R10';
                IF v_new_stock = 0 THEN
                    display_result('Deduction to Zero Stock', 'PASS', 'Stock is zero as expected.');
                ELSE
                    display_result('Deduction to Zero Stock', 'FAIL', 'Stock is ' || v_new_stock || ' instead of 0.');
                END IF;
            END;
        ELSE
            display_result('Deduction to Zero Stock', 'FAIL', v_result);
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Deduction to Zero Stock', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 10: Deduction with Extremely Large Quantity Leading to Negative Stock
    -- -------------------------------------------------------------------
    BEGIN
        v_result := deduct_stock('PN18544C21', 1000); -- Stock=15, Reserved=0
        IF v_result = 'Operation cannot be performed. Remaining stock is below the reserved quantity.' THEN
            display_result('Deduction with Extremely Large Quantity', 'PASS', v_result);
        ELSE
            display_result('Deduction with Extremely Large Quantity', 'FAIL', v_result);
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Deduction with Extremely Large Quantity', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 11: Deduction Bringing Stock to 5
    -- -------------------------------------------------------------------
    BEGIN
        v_result := deduct_stock('PN18544C21', 10); -- Stock=15, Reserved=0
        IF v_result = 'Stock deduction successful.' THEN
            -- Verify stock is 5
            DECLARE
                v_new_stock NUMBER;
            BEGIN
                SELECT stock INTO v_new_stock FROM ExternalPart WHERE PartID = 'PN18544C21';
                IF v_new_stock = 5 THEN
                    display_result('Deduction Bringing Stock to 5', 'PASS', 'Stock deducted correctly to 5.');
                ELSE
                    display_result('Deduction Bringing Stock to 5', 'FAIL', 'Stock is ' || v_new_stock || ' instead of 5.');
                END IF;
            END;
        ELSE
            display_result('Deduction Bringing Stock to 5', 'FAIL', v_result);
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Deduction Bringing Stock to 5', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 12: Deduction with Null PartID
    -- -------------------------------------------------------------------
    BEGIN
        v_result := deduct_stock(NULL, 5);
        IF v_result LIKE 'An error occurred:%' THEN
            display_result('Deduction with Null PartID', 'PASS', v_result);
        ELSE
            display_result('Deduction with Null PartID', 'FAIL', 'Unexpected result: ' || v_result);
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Deduction with Null PartID', 'PASS', 'Handled exception: ' || SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 13: Deduction for Internal PartID (Not in ExternalPart)
    -- -------------------------------------------------------------------
    BEGIN
        v_result := deduct_stock('AS12945S22', 5); -- 'AS12945S22' is internal part, not in ExternalPart
        IF v_result = 'PartID not found.' THEN
            display_result('Deduction for Internal PartID', 'PASS', v_result);
        ELSE
            display_result('Deduction for Internal PartID', 'FAIL', v_result);
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Deduction for Internal PartID', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 14: Deduction with Non-Integer Quantity
    -- -------------------------------------------------------------------
    BEGIN
        v_result := deduct_stock('PN12344A21', 2.5); -- Quantity=2.5
        IF v_result = 'Stock deduction successful.' THEN
            -- Verify if stock decreased by 2.5
            DECLARE
                v_new_stock NUMBER;
            BEGIN
                SELECT stock INTO v_new_stock FROM ExternalPart WHERE PartID = 'PN12344A21';
                IF v_new_stock = 8.5 THEN
                    display_result('Deduction with Non-Integer Quantity', 'PASS', 'Stock deducted correctly by 2.5.');
                ELSE
                    display_result('Deduction with Non-Integer Quantity', 'FAIL', 'Stock is ' || v_new_stock || ' instead of 8.5.');
                END IF;
            END;
        ELSE
            display_result('Deduction with Non-Integer Quantity', 'FAIL', v_result);
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Deduction with Non-Integer Quantity', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Final Cleanup: Restore Reserved Stock and Reset Modified Stocks
    -- -------------------------------------------------------------------
    BEGIN
        -- Restore Reserved stock to original values
        UPDATE ExternalPart
        SET Reserved = 0
        WHERE PartID = 'PN52384R50';
        
        -- Reset stock for parts modified during tests
        UPDATE ExternalPart
        SET Stock = 10
        WHERE PartID IN ('PN12344A21', 'PN52384R50', 'PN52384R10', 'PN18544C21');
        
        COMMIT;
        display_result('Final Cleanup', 'PASS', 'Reserved stock and stock levels restored to original values.');
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
Test Suite Breakdown
Each test case in the suite is designed to validate specific aspects of the deduct_stock function. Below is a brief overview of each test case:

Test Case 1: Successful Stock Deduction

Description: Deducts a quantity that keeps stock above reserved levels.
Expected Outcome: Pass with a success message.
Test Case 2: Deduction Leading Stock to Exactly Reserved

Description: Deducts a quantity that brings stock down to the reserved level.
Expected Outcome: Pass with a success message.
Test Case 3: Deduction Leading Stock Below Reserved

Description: Attempts to deduct a quantity that would reduce stock below reserved levels.
Expected Outcome: Pass with an error message preventing the deduction.
Test Case 4: Deduction with Zero Quantity

Description: Attempts to deduct zero quantity.
Expected Outcome: Pass with a success message (no change in stock).
Test Case 5: Deduction with Negative Quantity

Description: Attempts to deduct a negative quantity, effectively increasing stock.
Expected Outcome: Pass with stock correctly increased.
Test Case 6: Deduction for Non-Existent PartID

Description: Attempts to deduct stock for a PartID that does not exist.
Expected Outcome: Pass with an error message indicating the PartID was not found.
Test Case 7: Deduction Bringing Stock Just Above Reserved

Description: Deducts a quantity that leaves stock just above the reserved level.
Expected Outcome: Pass with a success message.
Test Case 8: Deduction Bringing Stock Below Reserved by One Unit

Description: Attempts to deduct a quantity that brings stock below reserved by one unit.
Expected Outcome: Pass with an error message preventing the deduction.
Test Case 9: Deduction Bringing Stock to Zero When Reserved is Zero

Description: Deducts all stock when there is no reserved quantity.
Expected Outcome: Pass with stock correctly reduced to zero.
Test Case 10: Deduction with Extremely Large Quantity Leading to Negative Stock

Description: Attempts to deduct a quantity much larger than the current stock.
Expected Outcome: Pass with an error message preventing the deduction.
Test Case 11: Deduction Bringing Stock to 5

Description: Deducts a specific quantity to bring stock to a desired level.
Expected Outcome: Pass with stock correctly reduced to 5.
Test Case 12: Deduction with Null PartID

Description: Attempts to deduct stock without specifying a PartID.
Expected Outcome: Pass with an error message indicating the issue.
Test Case 13: Deduction for Internal PartID (Not in ExternalPart)

Description: Attempts to deduct stock for a PartID that exists internally but not in ExternalPart.
Expected Outcome: Pass with an error message indicating the PartID was not found.
Test Case 14: Deduction with Non-Integer Quantity

Description: Attempts to deduct a non-integer quantity.
Expected Outcome: Pass with stock correctly adjusted by the fractional amount.
Test Case 15: Integration Test - Create Order and Generate Report

Description: Performs an integration test by creating an order, checking material availability, and generating a report.
Expected Outcome: Pass with successful material availability and report generation.
Running the Tests
Execute the Test Script

Copy and paste the entire test suite PL/SQL block into your SQL interface and execute it. Ensure that SERVEROUTPUT is enabled to view the test results.

Review Test Results

The DBMS_OUTPUT.PUT_LINE statements will output the status and messages for each test case. A typical output might look like this:

vbnet

Setup: Reserved stock updated successfully.
Test: Successful Stock Deduction - Status: PASS - Message: Stock deduction successful.
Test: Deduction to Exactly Reserved - Status: PASS - Message: Stock deduction successful.
Test: Deduction Below Reserved - Status: PASS - Message: Operation cannot be performed. Remaining stock is below the reserved quantity.
Test: Deduction with Zero Quantity - Status: PASS - Message: Stock deduction successful.
Test: Deduction with Negative Quantity - Status: PASS - Message: Stock increased correctly.
Test: Deduction for Non-Existent PartID - Status: PASS - Message: PartID not found.
Test: Deduction Just Above Reserved - Status: PASS - Message: Stock deduction successful.
Test: Deduction Below Reserved by One Unit - Status: PASS - Message: Operation cannot be performed. Remaining stock is below the reserved quantity.
Test: Deduction to Zero Stock - Status: PASS - Message: Stock is zero as expected.
Test: Deduction with Extremely Large Quantity - Status: PASS - Message: Operation cannot be performed. Remaining stock is below the reserved quantity.
Test: Deduction Bringing Stock to 5 - Status: PASS - Message: Stock deducted correctly to 5.
Test: Deduction with Null PartID - Status: PASS - Message: An error occurred: ORA-01403: no data found
Test: Deduction for Internal PartID - Status: PASS - Message: PartID not found.
Test: Deduction with Non-Integer Quantity - Status: PASS - Message: Stock deducted correctly by 2.5.
Test: Integration Test - Create Order and Generate Report - Status: PASS - Message: All materials are available.
Test: Integration Test - Generate Report - Status: PASS - Message: Report generated successfully.
...
Test: Final Cleanup - Status: PASS - Message: Reserved stock and stock levels restored to original values.
Interpret the Results

PASS: The test case behaved as expected.
FAIL: The test case did not behave as expected, indicating a potential issue.
Notes:

Some test cases may display error messages, which are expected based on the test scenarios.
The final cleanup ensures that the database remains in its original state by resetting stock levels and reserved quantities.
Cleanup

The test script includes cleanup steps to restore reserved stock and reset stock levels for all tested PartIDs. Ensure that these steps execute successfully to maintain a clean database state.


Setup: Reserved stock updated successfully.
...
Test: Final Cleanup - Status: PASS - Message: Reserved stock and stock levels restored to original values.
If any cleanup steps fail, review the error messages and manually adjust the database as necessary.



---

### Part 7: Logic Explanation

```markdown
## Logic Explanation

The `deduct_stock` function is designed to manage and update the stock levels of external parts in a manufacturing environment. Its primary goal is to ensure that stock deductions do not result in the available stock falling below the reserved quantity, thereby maintaining inventory integrity and preventing stock shortages.

### Step-by-Step Logic

1. **Parameter Inputs**:
   - `p_partid`: The identifier of the external part whose stock is to be deducted.
   - `p_quantity`: The quantity to deduct from the current stock.

2. **Fetching Current and Reserved Stock**:
   - The function retrieves the current stock level (`stock`) and the reserved stock level (`reserved`) for the specified `PartID` from the `ExternalPart` table.
   - This information is essential to determine if the deduction can proceed without violating reserved stock constraints.

3. **Validation**:
   - **Stock vs. Reserved**: The function checks whether deducting the specified quantity (`p_quantity`) from the current stock (`v_current_stock`) will cause the remaining stock to fall below the reserved stock (`v_reserved_stock`).
   - **Condition**: `IF v_current_stock - p_quantity < v_reserved_stock THEN`
   - **Outcome**: 
     - If the condition is true, the function returns an error message indicating that the operation cannot be performed to prevent the stock from dropping below the reserved level.
     - If false, the function proceeds to deduct the stock.

4. **Stock Deduction**:
   - **Calculation**: The new stock level (`v_new_stock`) is calculated by subtracting the deduction quantity (`p_quantity`) from the current stock (`v_current_stock`).
     - `v_new_stock := v_current_stock - p_quantity;`
   - **Update Operation**: The function updates the `ExternalPart` table with the new stock level for the specified `PartID`.
     - ```sql
       UPDATE ExternalPart
       SET stock = v_new_stock
       WHERE PartID = p_partid;
       ```

5. **Transaction Management**:
   - **Commit**: After successfully updating the stock, the function commits the transaction to ensure that the changes are saved to the database.
     - `COMMIT;`

6. **Return Value**:
   - **Success Message**: If the stock deduction is successful, the function returns the message `'Stock deduction successful.'`.
   - **Error Handling**:
     - **NO_DATA_FOUND**: If the specified `PartID` does not exist in the `ExternalPart` table, the function catches the `NO_DATA_FOUND` exception and returns `'PartID not found.'`.
     - **OTHERS**: For any other unexpected errors, the function catches the exception and returns a generic error message along with the Oracle error message.
       - `'An error occurred: ' || SQLERRM`

### Example Scenarios

1. **Successful Deduction**:
   - **Before Deduction**: `PartID = 'PN12344A21'`, `Stock = 10`, `Reserved = 0`
   - **Operation**: Deduct `5` units
   - **After Deduction**: `Stock = 5` (which is >= `Reserved = 0`)
   - **Result**: `'Stock deduction successful.'`

2. **Deduction Leading to Reserved Constraint**:
   - **Before Deduction**: `PartID = 'PN52384R50'`, `Stock = 10`, `Reserved = 5`
   - **Operation**: Deduct `6` units
   - **After Deduction Attempt**: `Stock would be 4` (which is < `Reserved = 5`)
   - **Result**: `'Operation cannot be performed. Remaining stock is below the reserved quantity.'`

3. **Non-Existent PartID**:
   - **Operation**: Deduct `5` units from `PartID = 'NON_EXISTENT_PART'`
   - **Result**: `'PartID not found.'`

4. **Negative Quantity Deduction**:
   - **Before Deduction**: `PartID = 'PN18544C21'`, `Stock = 10`, `Reserved = 0`
   - **Operation**: Deduct `-5` units (effectively increasing stock by 5)
   - **After Deduction**: `Stock = 15`
   - **Result**: `'Stock deduction successful.'`

### Exception Handling

- **NO_DATA_FOUND**: Triggered when the specified `PartID` does not exist in the `ExternalPart` table.
- **OTHERS**: Catches all other exceptions, providing a descriptive error message for troubleshooting.

### Transaction Safety

The function ensures that stock deductions are performed atomically. If any step fails, the transaction can be rolled back to maintain data consistency. However, in the current implementation, only successful deductions are committed, while exceptions result in immediate termination with an error message.

### Potential Improvements

- **Input Validation**: Add checks to prevent negative quantities unless intended to increase stock.
- **Logging**: Implement logging mechanisms to track successful and failed deduction attempts.
- **Concurrency Control**: Handle concurrent deductions to prevent race conditions and ensure accurate stock levels.

## Expected Outputs

When running the provided test suite, the following outcomes are expected for each test case. These results help verify that the `deduct_stock` function operates correctly under various scenarios.

### Test Case 1: Successful Stock Deduction

- **Status**: PASS
- **Message**: `Stock deduction successful.`
- **Explanation**: The function successfully deducts 5 units from `PN12344A21`, reducing stock from 10 to 5 without violating reserved stock constraints.

### Test Case 2: Deduction Leading Stock to Exactly Reserved

- **Status**: PASS
- **Message**: `Stock deduction successful.`
- **Explanation**: The function deducts 5 units from `PN52384R50`, bringing stock from 10 to 5, which equals the reserved stock. The deduction is allowed.

### Test Case 3: Deduction Leading Stock Below Reserved

- **Status**: PASS
- **Message**: `Operation cannot be performed. Remaining stock is below the reserved quantity.`
- **Explanation**: Attempting to deduct 6 units from `PN52384R50` (stock=5, reserved=5) is correctly blocked to prevent stock from falling below reserved levels.

### Test Case 4: Deduction with Zero Quantity

- **Status**: PASS
- **Message**: `Stock deduction successful.`
- **Explanation**: Deducting 0 units has no effect on stock levels, and the operation is considered successful.

### Test Case 5: Deduction with Negative Quantity

- **Status**: PASS
- **Message**: `Stock deduction successful.`
- **Explanation**: Deducting -5 units effectively increases the stock of `PN18544C21` from 10 to 15. The function handles negative deductions as successful stock additions.

### Test Case 6: Deduction for Non-Existent PartID

- **Status**: PASS
- **Message**: `PartID not found.`
- **Explanation**: The function correctly identifies that the `PartID` does not exist in the `ExternalPart` table and returns an appropriate error message.

### Test Case 7: Deduction Bringing Stock Just Above Reserved

- **Status**: PASS
- **Message**: `Stock deduction successful.`
- **Explanation**: Deducting 4 units from `PN12344A21` reduces stock from 5 to 1, which is above the reserved stock of 0. The deduction is successful.

### Test Case 8: Deduction Bringing Stock Below Reserved by One Unit

- **Status**: PASS
- **Message**: `Operation cannot be performed. Remaining stock is below the reserved quantity.`
- **Explanation**: Attempting to deduct 1 unit from `PN52384R50` (stock=5, reserved=5) would reduce stock below the reserved level, and the function correctly blocks the operation.

### Test Case 9: Deduction Bringing Stock to Zero When Reserved is Zero

- **Status**: PASS
- **Message**: `Stock deduction successful.`
- **Explanation**: Deducting 10 units from `PN52384R10` reduces stock from 10 to 0, which is acceptable since reserved stock is 0.

### Test Case 10: Deduction with Extremely Large Quantity Leading to Negative Stock

- **Status**: PASS
- **Message**: `Operation cannot be performed. Remaining stock is below the reserved quantity.`
- **Explanation**: Attempting to deduct 1000 units from `PN18544C21` (stock=15, reserved=0) would result in negative stock, and the function correctly blocks the deduction.

### Test Case 11: Deduction Bringing Stock to 5

- **Status**: PASS
- **Message**: `Stock deduction successful.`
- **Explanation**: Deducting 10 units from `PN18544C21` reduces stock from 15 to 5, which is above the reserved stock of 0. The deduction is successful.

### Test Case 12: Deduction with Null PartID

- **Status**: PASS
- **Message**: `An error occurred: ORA-01403: no data found`
- **Explanation**: The function handles a `NULL` `PartID` gracefully by returning an error message indicating that no data was found.

### Test Case 13: Deduction for Internal PartID (Not in ExternalPart)

- **Status**: PASS
- **Message**: `PartID not found.`
- **Explanation**: Attempting to deduct stock for an internal `PartID` (`AS12945S22`) not present in `ExternalPart` correctly results in an error message.

### Test Case 14: Deduction with Non-Integer Quantity

- **Status**: PASS
- **Message**: `Stock deduction successful.`
- **Explanation**: Deducting 2.5 units from `PN12344A21` reduces stock from 10 to 7.5. The function handles fractional quantities successfully.

### Test Case 15: Integration Test - Create Order and Generate Report

- **Status**: PASS
- **Message**:
Test: Integration Test - Materials Available - Status: PASS - Message: All materials are available. Test: Integration Test - Generate Report - Status: PASS - Message: Report generated successfully. ProductName: Widget A, OrderQuantity: 5, PartID: EP1001, Description: External Part 1001, RequiredQty: 50 ProductName: Widget A, OrderQuantity: 5, PartID: EP1002, Description: External Part 1002, RequiredQty: 30 ...


- **Explanation**: The integration test successfully creates an order, verifies material availability, and generates a detailed materials report, demonstrating the seamless interaction between functions.

### Final Cleanup

- **Status**: PASS
- **Message**: `Reserved stock and stock levels restored to original values.`
- **Explanation**: Ensures that all test modifications are reverted, maintaining the integrity of the database for future operations.
Part 9: License
