-- Enable DBMS_OUTPUT to view test results
SET SERVEROUTPUT ON;
DECLARE
    -- Variables to capture test results
    v_test_number NUMBER := 1;
    
    -- Exception types
    e_circular_ref EXCEPTION;
    PRAGMA EXCEPTION_INIT(e_circular_ref, -20001);
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- Starting Trigger Test Coverage ---');

    -- ----------------------------------------
    -- Test Case 1: Valid Insert (No Circular Reference)
    -- ----------------------------------------
    BEGIN
        DBMS_OUTPUT.PUT_LINE('Test Case ' || v_test_number || ': Valid Insert (No Circular Reference)');
        INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn)
        VALUES ('PN12344A21', 170, 'AS12945S20', 5);
        DBMS_OUTPUT.PUT_LINE('Test Case ' || v_test_number || ': Passed');
    EXCEPTION
        WHEN e_circular_ref THEN
            DBMS_OUTPUT.PUT_LINE('Test Case ' || v_test_number || ': Failed - Circular reference detected unexpectedly.');
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Test Case ' || v_test_number || ': Failed - Unexpected error: ' || SQLERRM);
    END;
    v_test_number := v_test_number + 1;

    -- ----------------------------------------
    -- Test Case 2: Direct Circular Reference
    -- ----------------------------------------
    BEGIN
        DBMS_OUTPUT.PUT_LINE('Test Case ' || v_test_number || ': Direct Circular Reference');
        -- Attempt to create a circular reference by pointing NextOPID and NextBOOID back to the original
        INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn)
        VALUES ('PN52384R50', 100, 'AS12946S22', 2); -- OPID 100, BOOID 'AS12946S22' already exists
        DBMS_OUTPUT.PUT_LINE('Test Case ' || v_test_number || ': Failed - Circular reference was not detected.');
    EXCEPTION
        WHEN e_circular_ref THEN
            DBMS_OUTPUT.PUT_LINE('Test Case ' || v_test_number || ': Passed - Circular reference detected.');
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Test Case ' || v_test_number || ': Failed - Unexpected error: ' || SQLERRM);
    END;
    v_test_number := v_test_number + 1;

    -- ----------------------------------------
    -- Test Case 3: Indirect Circular Reference
    -- ----------------------------------------
    BEGIN
        DBMS_OUTPUT.PUT_LINE('Test Case ' || v_test_number || ': Indirect Circular Reference');
        -- Insert a new BOO and Operations to create an indirect cycle
        -- Step 1: Create a new BOO
        INSERT INTO BOO (BOOID, PartID) VALUES ('BOO_NEW1', 'AS12945S22');

        -- Step 2: Insert Operations for BOO_NEW1
        INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
        VALUES (200, 'Operation 200', 5647, 100);

        INSERT INTO BOO_Operation (OPID, BOOID, NextOPID, NextBOOID)
        VALUES (200, 'BOO_NEW1', 201, 'BOO_NEW1');

        INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
        VALUES (201, 'Operation 201', 5647, 100);

        INSERT INTO BOO_Operation (OPID, BOOID, NextOPID, NextBOOID)
        VALUES (201, 'BOO_NEW1', 200, 'BOO_NEW1'); -- Creates an indirect cycle

        -- Step 3: Attempt to insert PartIN that triggers the cycle
        INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn)
        VALUES ('PN52384R50', 200, 'BOO_NEW1', 3);

        DBMS_OUTPUT.PUT_LINE('Test Case ' || v_test_number || ': Failed - Indirect circular reference was not detected.');
    EXCEPTION
        WHEN e_circular_ref THEN
            DBMS_OUTPUT.PUT_LINE('Test Case ' || v_test_number || ': Passed - Indirect circular reference detected.');
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Test Case ' || v_test_number || ': Failed - Unexpected error: ' || SQLERRM);
    END;
    v_test_number := v_test_number + 1;

    -- ----------------------------------------
    -- Test Case 4: Insert with NULL NextOPID and NextBOOID
    -- ----------------------------------------
    BEGIN
        DBMS_OUTPUT.PUT_LINE('Test Case ' || v_test_number || ': Insert with NULL NextOPID and NextBOOID');
        -- Insert PartIN with BOO_Operation having NULL NextOPID and NextBOOID
        -- First, ensure there's a BOO_Operation with NULL NextOPID and NextBOOID
        INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
        VALUES (300, 'Operation 300', 5647, 100);

        INSERT INTO BOO_Operation (OPID, BOOID, NextOPID, NextBOOID)
        VALUES (300, 'BOO_NEW2', NULL, NULL);

        INSERT INTO BOO (BOOID, PartID) VALUES ('BOO_NEW2', 'AS12945S22');

        -- Now, insert PartIN
        INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn)
        VALUES ('PN52384R50', 300, 'BOO_NEW2', 4);

        DBMS_OUTPUT.PUT_LINE('Test Case ' || v_test_number || ': Passed');
    EXCEPTION
        WHEN e_circular_ref THEN
            DBMS_OUTPUT.PUT_LINE('Test Case ' || v_test_number || ': Failed - Circular reference detected unexpectedly.');
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Test Case ' || v_test_number || ': Failed - Unexpected error: ' || SQLERRM);
    END;
    v_test_number := v_test_number + 1;

    -- ----------------------------------------
    -- Test Case 5: Insert with Non-Existing OPID or BOOID
    -- ----------------------------------------
    BEGIN
        DBMS_OUTPUT.PUT_LINE('Test Case ' || v_test_number || ': Insert with Non-Existing OPID or BOOID');
        -- Attempt to insert PartIN with OPID and BOOID that do not exist in BOO_Operation
        INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn)
        VALUES ('PN52384R50', 999, 'BOO_NON_EXIST', 5);
        DBMS_OUTPUT.PUT_LINE('Test Case ' || v_test_number || ': Failed - Non-existing OPID/BOOID was not detected.');
    EXCEPTION
        WHEN e_circular_ref THEN
            DBMS_OUTPUT.PUT_LINE('Test Case ' || v_test_number || ': Passed - Circular reference detected (unexpectedly) for non-existing BOO.');
        WHEN NO_DATA_FOUND THEN
            DBMS_OUTPUT.PUT_LINE('Test Case ' || v_test_number || ': Passed - No BOO_Operation found, handled gracefully.');
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Test Case ' || v_test_number || ': Passed - Expected failure due to foreign key or trigger error: ' || SQLERRM);
    END;
    v_test_number := v_test_number + 1;

    DBMS_OUTPUT.PUT_LINE('--- Trigger Test Coverage Completed ---');
END;
/
