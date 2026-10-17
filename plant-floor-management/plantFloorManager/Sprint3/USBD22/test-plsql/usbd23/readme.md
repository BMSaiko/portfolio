# Operation Time Validation Trigger

This repository contains a PL/SQL trigger (`trg_check_operation_time`) designed to ensure that the `ExpectedExecutionTime` for operations does not exceed the permissible limits based on the associated workstation types. Additionally, a suite of tests is provided to verify the correctness and robustness of the trigger.

## Table of Contents

1. [Trigger Description](trigger-description)
2. [Setup Instructions](setup-instructions)
3. [Executing the Trigger](executing-the-trigger)
4. [Running the Tests](running-the-tests)
5. [Logic Explanation](logic-explanation)
6. [Expected Outputs](expected-outputs)

## Trigger Description

The `trg_check_operation_time` is a `BEFORE INSERT OR UPDATE` trigger on the `Operation` table. Its primary purpose is to validate that the `ExpectedExecutionTime` for an operation does not exceed the minimum `MaximumExecutionTime` of all `WorkstationTypes` capable of performing the operation's type (`OperationTypeOTID`).

### Key Functions

- **Validation**: Ensures that the `ExpectedExecutionTime` is within acceptable bounds.
- **Data Integrity**: Maintains consistency between the `Operation` and `WorkstationTypes` tables.
- **Error Handling**: Raises a custom error if the validation fails, preventing invalid data entry.

### Trigger Code


CREATE OR REPLACE TRIGGER trg_check_operation_time
BEFORE INSERT OR UPDATE ON Operation
FOR EACH ROW
DECLARE
    v_min_max_time NUMBER(10);
BEGIN
    -- Obtain the minimum MaximumExecutionTime from WorkstationTypes that can perform the operation type
    SELECT MIN(wt.MaximumExecutionTime)
      INTO v_min_max_time
      FROM WorkstationTypes wt
      JOIN WSTypes_OperationTypes wot ON wot.WTID = wt.WTID
     WHERE wot.OperationTypeOTID = :NEW.OperationTypeOTID;

    -- Check if ExpectedExecutionTime exceeds the minimum supported maximum time
    IF :NEW.ExpectedExecutionTime > v_min_max_time THEN
        RAISE_APPLICATION_ERROR(-20001,
            'O ExpectedExecutionTime (' || :NEW.ExpectedExecutionTime ||
            ') excede o tempo máximo permitido (' || v_min_max_time || ') para o tipo de operação.');
    END IF;
END;
/


## Setup Instructions

Follow these steps to set up the trigger and prepare your database environment.

### Prerequisites

- Oracle Database with appropriate privileges.
- SQL*Plus or any other Oracle-compatible SQL interface.

### Steps

1. **Ensure Tables Exist**: Make sure the relevant tables (`Operation`, `WorkstationTypes`, `WSTypes_OperationTypes`) are created and populated with the necessary data.

2. **Create the Trigger**:


CREATE OR REPLACE TRIGGER trg_check_operation_time
BEFORE INSERT OR UPDATE ON Operation
FOR EACH ROW
DECLARE
    v_min_max_time NUMBER(10);
BEGIN
    -- Obtain the minimum MaximumExecutionTime from WorkstationTypes that can perform the operation type
    SELECT MIN(wt.MaximumExecutionTime)
      INTO v_min_max_time
      FROM WorkstationTypes wt
      JOIN WSTypes_OperationTypes wot ON wot.WTID = wt.WTID
     WHERE wot.OperationTypeOTID = :NEW.OperationTypeOTID;

    -- Check if ExpectedExecutionTime exceeds the minimum supported maximum time
    IF :NEW.ExpectedExecutionTime > v_min_max_time THEN
        RAISE_APPLICATION_ERROR(-20001,
            'O ExpectedExecutionTime (' || :NEW.ExpectedExecutionTime ||
            ') excede o tempo máximo permitido (' || v_min_max_time || ') para o tipo de operação.');
    END IF;
END;
/


## Executing the Trigger

The trigger automatically fires before any `INSERT` or `UPDATE` operation on the `Operation` table. To see it in action, perform operations that will trigger the validation logic.

### Example Operations

1. **Insert a Valid Operation**:


INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
VALUES (999, 'Test Operation OK', 5647, 60);


2. **Insert an Invalid Operation**:


INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
VALUES (1000, 'Test Operation Fail', 5647, 61);


3. **Update an Operation**:


UPDATE Operation
   SET OperationTypeOTID = 5647, ExpectedExecutionTime = 75
 WHERE OPID = 999;


## Running the Tests

A suite of tests is provided to verify the functionality of the `trg_check_operation_time` trigger. These tests cover various scenarios, including valid and invalid inserts and updates.

### Test Scripts

The following tests are included:

1. **Test 1: Insert Operation within the Allowed Limit**
2. **Test 2: Insert Operation Exceeding the Allowed Limit**
3. **Test 3: Update OperationTypeOTID to a More Restrictive One**
4. **Test 4: Update ExpectedExecutionTime to Exceed the Limit**
5. **Test 5: Boundary Condition - Exactly Equal to the Minimum Limit**

### Executing Tests

Run each test script sequentially in your SQL interface. Each test includes output messages indicating success or failure.

#### Test 1: Insert Operation within the Allowed Limit


BEGIN
    DBMS_OUTPUT.PUT_LINE('Test 1: Insert Operation within limit');

    -- We assume OperationTypeOTID = 5647 has a min max time of 60
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
    VALUES (999, 'Test Operation OK', 5647, 60);

    DBMS_OUTPUT.PUT_LINE('  -> Success: Operation inserted without error');
    ROLLBACK;  -- to clean up test data
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('  -> Failed: ' || SUBSTR(SQLERRM, 1, 200));
        ROLLBACK;
END;
/


#### Test 2: Insert Operation Exceeding the Allowed Limit


BEGIN
    DBMS_OUTPUT.PUT_LINE('Test 2: Insert Operation exceeding limit');

    -- Attempt to insert an Operation with ExpectedExecutionTime > 60
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
    VALUES (1000, 'Test Operation Fail', 5647, 61);

    -- If the trigger is correct, we should never reach here
    DBMS_OUTPUT.PUT_LINE('  -> ERROR: Expected the trigger to fail but it did not!');
    ROLLBACK;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('  -> Trigger error as expected: ' || SUBSTR(SQLERRM, 1, 200));
        ROLLBACK;
END;
/


#### Test 3: Update OperationTypeOTID to a More Restrictive One


DECLARE
    v_opid NUMBER := 2000;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Test 3: Update OperationTypeOTID to more restrictive one');

    -- 1) Insert an Operation with a type that supports 90 (for example, OTID = 5649)
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
    VALUES (v_opid, 'Test Operation Update Type', 5649, 90);

    DBMS_OUTPUT.PUT_LINE('  -> Inserted Operation with OTID = 5649 (supports 90).');

    -- 2) Now update the OperationTypeOTID to 5647 (which has min max time = 60)
    --    This should fail because ExpectedExecutionTime is still 90
    UPDATE Operation
       SET OperationTypeOTID = 5647
     WHERE OPID = v_opid;

    DBMS_OUTPUT.PUT_LINE('  -> ERROR: Expected the trigger to fail but it did not!');
    ROLLBACK;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('  -> Trigger error as expected: ' || SUBSTR(SQLERRM, 1, 200));
        ROLLBACK;
END;
/


#### Test 4: Update ExpectedExecutionTime to Exceed the Limit


DECLARE
    v_opid NUMBER := 3000;
BEGIN
    DBMS_OUTPUT.PUT_LINE('Test 4: Update ExpectedExecutionTime to exceed the limit');

    -- Insert an operation within the limit (ExpectedExecutionTime = 60 for OTID = 5647)
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
    VALUES (v_opid, 'Test Op Update Time', 5647, 60);

    DBMS_OUTPUT.PUT_LINE('  -> Inserted operation with ExpectedExecutionTime = 60.');

    -- Now try to update that operation to 75, which exceeds 60
    UPDATE Operation
       SET ExpectedExecutionTime = 75
     WHERE OPID = v_opid;

    DBMS_OUTPUT.PUT_LINE('  -> ERROR: Expected the trigger to fail but it did not!');
    ROLLBACK;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('  -> Trigger error as expected: ' || SUBSTR(SQLERRM, 1, 200));
        ROLLBACK;
END;
/


#### Test 5: Boundary Condition - Exactly Equal to the Minimum Limit


BEGIN
    DBMS_OUTPUT.PUT_LINE('Test 5: Boundary Condition - Exactly equal to min max time');

    -- Inserting with EXACT match (60) for an OperationTypeOTID (5647) that has a min max time = 60
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
    VALUES (5000, 'Test Boundary Condition', 5647, 60);

    DBMS_OUTPUT.PUT_LINE('  -> Success: Inserted at boundary value (60).');
    ROLLBACK;
EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('  -> Failed: ' || SUBSTR(SQLERRM, 1, 200));
        ROLLBACK;
END;
/


## Logic Explanation

The `trg_check_operation_time` trigger ensures that any insertion or update to the `Operation` table adheres to predefined execution time constraints based on the operation type. Here's a step-by-step breakdown of how the trigger works:

1. **Trigger Activation**:
   - The trigger is set to fire `BEFORE INSERT OR UPDATE` on the `Operation` table for each row affected.

2. **Retrieving Minimum Maximum Execution Time**:
   - The trigger performs a `SELECT` query to find the minimum `MaximumExecutionTime` among all `WorkstationTypes` that are associated with the given `OperationTypeOTID`. This is done by joining `WorkstationTypes` with `WSTypes_OperationTypes`.
   - The result is stored in the variable `v_min_max_time`.

3. **Validation Check**:
   - The trigger compares the `ExpectedExecutionTime` of the new or updated operation (`:NEW.ExpectedExecutionTime`) with `v_min_max_time`.
   - If `ExpectedExecutionTime` exceeds `v_min_max_time`, the trigger raises a custom application error (`-20001`) with a descriptive message.

4. **Error Handling**:
   - When the error is raised, the current transaction is aborted, preventing the insertion or update of the invalid operation record.

5. **Success Scenario**:
   - If `ExpectedExecutionTime` is within the allowed limit, the operation proceeds normally without any interruption.

### Example Scenario

- **OperationTypeOTID = 5647**:
  - Assume this operation type is supported by workstation types with `MaximumExecutionTime` of 60 minutes.
  - Therefore, `v_min_max_time` is 60.
  - Any operation with `ExpectedExecutionTime` greater than 60 will be rejected by the trigger.

### Benefits

- **Data Integrity**: Prevents inconsistent or invalid data entries that could disrupt business processes.
- **Automation**: Automatically enforces business rules without requiring manual checks.
- **Maintainability**: Centralizes the validation logic within the database, ensuring consistency across applications.

## Expected Outputs

When running the provided tests, the following outcomes are expected:

1. **Test 1: Insert Operation within the Allowed Limit**
   
   Test 1: Insert Operation within limit
     -> Success: Operation inserted without error
   

2. **Test 2: Insert Operation Exceeding the Allowed Limit**
   
   Test 2: Insert Operation exceeding limit
     -> Trigger error as expected: ORA-20001: O ExpectedExecutionTime (61) excede o tempo máximo permitido (60) para o tipo de operação.
   

3. **Test 3: Update OperationTypeOTID to a More Restrictive One**
   
   Test 3: Update OperationTypeOTID to more restrictive one
     -> Inserted Operation with OTID = 5649 (supports 90).
     -> Trigger error as expected: ORA-20001: O ExpectedExecutionTime (90) excede o tempo máximo permitido (60) para o tipo de operação.
   

4. **Test 4: Update ExpectedExecutionTime to Exceed the Limit**
   
   Test 4: Update ExpectedExecutionTime to exceed the limit
     -> Inserted operation with ExpectedExecutionTime = 60.
     -> Trigger error as expected: ORA-20001: O ExpectedExecutionTime (75) excede o tempo máximo permitido (60) para o tipo de operação.
   

5. **Test 5: Boundary Condition - Exactly Equal to the Minimum Limit**
   
   Test 5: Boundary Condition - Exactly equal to min max time
     -> Success: Inserted at boundary value (60).
   

### Final Note

After executing all tests, you should see messages indicating whether each test passed or if the trigger correctly prevented invalid operations. Review the `DBMS_OUTPUT` messages to verify the trigger's behavior.