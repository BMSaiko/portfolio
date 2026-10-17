-- USBD19
CREATE OR REPLACE FUNCTION GetProductWithMostOperations
RETURN SYS_REFCURSOR IS
    cur_Result SYS_REFCURSOR;
BEGIN
    -- Open a cursor to retrieve the product with the most operations
    OPEN cur_Result FOR
    SELECT 
        p.PartID AS ProductPartID,
        p.Name AS ProductName,
        COUNT(op.OPID) AS OperationCount
    FROM 
        Product p
    JOIN PartOut po ON p.PartID = po.PartID
    JOIN Operation op ON po.OPID = op.OPID
    GROUP BY 
        p.PartID, p.Name
    ORDER BY 
        COUNT(op.OPID) DESC, -- Sort by operation count in descending order
        p.PartID ASC         -- Break ties by Product Part ID in ascending order
    FETCH FIRST 1 ROW ONLY; -- Return only the first product in the sorted result

    RETURN cur_Result;

EXCEPTION
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20001, 'An error occurred: ' || SQLERRM);
END;
/