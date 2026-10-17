-- Enable output so you can see DBMS_OUTPUT.PUT_LINE messages
SET SERVEROUTPUT ON;

------------------------------------------------------------------------
-- Test 1: Insert Operation within the allowed limit
------------------------------------------------------------------------
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
------------------------------------------------------------------------

------------------------------------------------------------------------
-- Test 2: Insert Operation exceeding the allowed limit
------------------------------------------------------------------------
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
------------------------------------------------------------------------

------------------------------------------------------------------------
-- Test 3: Update OperationTypeOTID to a more restrictive one
------------------------------------------------------------------------
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
------------------------------------------------------------------------

------------------------------------------------------------------------
-- Test 4: Update ExpectedExecutionTime to exceed the limit
------------------------------------------------------------------------
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
------------------------------------------------------------------------

------------------------------------------------------------------------
-- Test 5: Boundary Condition - Exactly equal to the minimum limit
------------------------------------------------------------------------
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
------------------------------------------------------------------------

PROMPT
PROMPT *** All tests completed. Check the DBMS_OUTPUT above for pass/fail details. ***
