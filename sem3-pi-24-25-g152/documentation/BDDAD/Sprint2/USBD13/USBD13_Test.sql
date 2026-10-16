-- Test
DECLARE
    v_ProductPartID VARCHAR2(50) := 'AS12947S20'; -- Replace with a Product Part ID
    cur_Operations  SYS_REFCURSOR;
    v_OPID          NUMBER;
    v_OperationName VARCHAR2(255);
    v_WorkstationType VARCHAR2(255);
    v_InputPartID   VARCHAR2(50);
    v_OutputPartID  VARCHAR2(50);
BEGIN
    -- Call the function
    cur_Operations := GetProductOperations(p_ProductPartID => v_ProductPartID);

    -- Fetch the results
    LOOP
        FETCH cur_Operations INTO v_OPID, v_OperationName, v_WorkstationType, v_InputPartID, v_OutputPartID;
        EXIT WHEN cur_Operations%NOTFOUND;

        -- Display the results
        DBMS_OUTPUT.PUT_LINE(
            'Operation ID: ' || v_OPID || ', Operation Name: ' || v_OperationName || 
            ', Workstation Type: ' || v_WorkstationType || ', Input Part ID: ' || v_InputPartID || 
            ', Output Part ID: ' || v_OutputPartID
        );
    END LOOP;

    -- Close the cursor
    CLOSE cur_Operations;
END;
/

