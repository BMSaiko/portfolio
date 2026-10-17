-- Test
DECLARE
    cur_Result SYS_REFCURSOR;
    v_ProductPartID VARCHAR2(50);
    v_ProductName VARCHAR2(255);
    v_OperationCount NUMBER;
BEGIN
    -- Call the function
    cur_Result := GetProductWithMostOperations;

    -- Fetch the result
    LOOP
        FETCH cur_Result INTO v_ProductPartID, v_ProductName, v_OperationCount;
        EXIT WHEN cur_Result%NOTFOUND;

        -- Display the result
        DBMS_OUTPUT.PUT_LINE('Product Part ID: ' || v_ProductPartID || 
                             ', Product Name: ' || v_ProductName || 
                             ', Operation Count: ' || v_OperationCount);
    END LOOP;

    -- Close the cursor
    CLOSE cur_Result;
END;
/