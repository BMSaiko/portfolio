# USBD13 - As a Production Manager, I want to get a list of operations involved in the production of a product, as well as each workstation type.

# Overview
The USBD13 asked us to build a PL/SQL code that would retrive the sequence of operations required for a specific product. Including their respective workstation types and input/output parts. For that we created the function GetProductOperations that travesses the BOO structure to provide detailed insgights into a product's operations, enabling production managers to track the processes efficiently.

# Function Signature

```
--USBD13
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
```

# Description
The GetProductOperations function retrieves all oprations required for a specific product, organized hierarchically, includinding their workstation types and associated parts. This helps in understanding the manufacturing processes for a given product, tracking input/output relationships across operations.

# Logic Breakdown
## 1. Recursive Common Table Expression (CTE)
### a. Base Code
```
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
```

**Purpose:** Fetch operations directly associated with the product, including workstation types and input/output parts.

### b. Recusrsive Case
```
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
```

**Purpose:** Retrieve operations for intermediate products used in the product's operations, enabling recursive exploration of the production hierarchy.

## 2. Final Selection and Ordering
```
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
```

**Purpose:** Aggregate all retrived operations into a single dataset, ordered by operation ID.

## 3. Cursor Handling

```
OPEN cur_Operations FOR
-- Query using CTE
RETURN cur_Operations;
```

**Functionality:** Executes the recursive query and returns a cursor for the results, allowing efficient fetching by the caller.

# Usage
To execute the GetProductOperations function you just need to run the follow anonymous block
```
SET SERVEROUTPUT ON;

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
```

# Example Output
For a product with the ID **P12345**, the output might be:
```
Operation ID: 1, Operation Name: Cut Material, Workstation Type: Cutting Machine, Input Part ID: NULL, Output Part ID: P10001
Operation ID: 2, Operation Name: Weld Frame, Workstation Type: Welding Station, Input Part ID: P10001, Output Part ID: P10002
Operation ID: 3, Operation Name: Paint Frame, Workstation Type: Paiting Booth, Input Part ID: P10002, Output Part ID: P10003
Operation ID: 4, Operation Name: Assemble Parts, Workstation Type: Assembly Line, Input Part ID: P10003, Output Part ID: P12345
```

# Consideration
### 1. Data Integrity

Ensure that the **Operation, WorkstationTypes, PartIN, PartOut, Product, and IntermediateProduct** tables are populated with valid relationships.

### 2. Performance 

- Use indexes on commonly joined columns (**PartID, OPID**, etc) to improve query performance;
- Optimize the size of the cursor if the datraset is large.

### 3. Error Handling

The function includes an exception handler to raise a custom error message (ORA-20002) in case of any unexpected issues.

# Coclusion

The GetProductOperations function provides production managers a detailed breakdown of operations for any product, enabling efficient tracking of processes and resource allocation.

# License

This project is licensed under the MIT License.

# Author

Jorge Almeida
[1220838@isep.ipp.pt](mailto:1220838@isep.ipp.pt)