-- USBD13
CREATE OR REPLACE FUNCTION GetProductOperations (
    p_ProductPartID IN VARCHAR2
) RETURN SYS_REFCURSOR IS
    -- Define the cursor to hold the result set
    cur_Operations SYS_REFCURSOR;
BEGIN
    -- Validate the input parameter
    IF p_ProductPartID IS NULL THEN
        -- Raise an error if the Product Part ID is NULL
        RAISE_APPLICATION_ERROR(-20001, 'Product Part ID cannot be NULL');
    END IF;

    -- Open the cursor and execute the query
    OPEN cur_Operations FOR
    WITH RecursiveOperations (OPID, OperationName, WorkstationType, InputPartID, OutputPartID) AS (
        -- Base Query: Fetch operations related to the given Product Part ID
        SELECT
            op.OPID,
            op.Name AS OperationName,
            wt.Name AS WorkstationType,
            pi.PartID AS InputPartID,
            po.PartID AS OutputPartID
        FROM
            Operation op
            JOIN WSTypes_OperationTypes wot ON op.OperationTypeOTID = wot.OperationTypeOTID
            JOIN WorkstationTypes wt ON wot.WTID = wt.WTID
            LEFT JOIN PartIN pi ON op.OPID = pi.OPID
            LEFT JOIN PartOut po ON op.OPID = po.OPID
            JOIN Product p ON po.PartID = p.PartID
        WHERE
            p.PartID = p_ProductPartID
        UNION ALL
        -- Recursive Query: Fetch operations for intermediate products
        SELECT
            op.OPID,
            op.Name AS OperationName,
            wt.Name AS WorkstationType,
            pi.PartID AS InputPartID,
            po.PartID AS OutputPartID
        FROM
            Operation op
            JOIN WSTypes_OperationTypes wot ON op.OperationTypeOTID = wot.OperationTypeOTID
            JOIN WorkstationTypes wt ON wot.WTID = wt.WTID
            LEFT JOIN PartIN pi ON op.OPID = pi.OPID
            LEFT JOIN PartOut po ON op.OPID = po.OPID
            JOIN IntermediateProduct ip ON po.PartID = ip.PartID
            JOIN RecursiveOperations ro ON ro.InputPartID = ip.PartID
    )
    SELECT DISTINCT -- Deduplicate results to avoid duplicate rows in the final output
        OPID,
        OperationName,
        WorkstationType,
        InputPartID,
        OutputPartID
    FROM
        RecursiveOperations
    ORDER BY
        OPID;

    -- Return the cursor to the caller
    RETURN cur_Operations;

EXCEPTION
    -- Handle unexpected errors and raise an application-specific error
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20002, 'An error occurred: ' || SQLERRM);
END;
/
