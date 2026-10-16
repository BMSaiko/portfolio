# USBD19 - As a Production Manager, I want to know which product has more operations in its BOO.

# Overview

The USBD19 asked us to build a PL/SQL code that would return the product that has the most operations in its BOO. For that we decided build the function GetProductWithMostOperations that return a cursor that include some details of the product like the name, the id and the operation count.

# Function Signature

```
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
```

# Description

The GetProductWithMostOperations function finds the product with the most operations base on the BOO, in case of multiple products having the same highest number of operation, the function selects the one with the smallest **Product Part ID**.

# Logic Breakdown
## 1. Main Query
```
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
    COUNT(op.OPID) DESC, -- Sort by operation count
    p.PartID ASC         -- Tie-breaking by Product Part ID
FETCH FIRST 1 ROW ONLY; -- Return only one result`
```

### Purpose:
Identify the productwith the highest number of associated operations by analyzing relationships in the **PartOut**, **Operation** and **Product** tables.

### Tables Involved:
- **Product:** Contains product information, including **PartID** and **Name**;
- **PartOut:** Relates operations to specific parts or products;
- **Operation:** Holds details about operations, including operation types and IDs.

### Output:
The product with the most operations, including its **Product Part ID**, **Product Name**, and the total count of operations.

## 2. Tie-Breaking Logic
If multiple products have the same operation count, the functionn selects the product with the smallest **Product Part ID**:

```
ORDER BY 
    COUNT(op.OPID) DESC, -- Operation count in descending order
    p.PartID ASC         -- Ascending Product Part ID for tie-breaking
```

## 3. Cursor Handling

```
OPEN cur_Result FOR
-- Main query
RETURN cur_Result;
```

**Functionality:** The cursor executes the SQL query and return the result to the caller, enabling flexible and efficient result processing.

# Usage
To execute the GetProductWithMostOperations function you just need to run the follow anonymous block
```
SET SERVEROUTPUT ON;

DECLARE
    cur_Result SYS_REFCURSOR;
    v_ProductPartID VARCHAR2(50);
    v_ProductName VARCHAR2(255);
    v_OperationCount NUMBER;
BEGIN

    cur_Result := GetProductWithMostOperations;

    LOOP
        FETCH cur_Result INTO v_ProductPartID, v_ProductName, v_OperationCount;
        EXIT WHEN cur_Result%NOTFOUND;

        -- Display the result
        DBMS_OUTPUT.PUT_LINE('Product Part ID: ' || v_ProductPartID || 
                             ', Product Name: ' || v_ProductName || 
                             ', Operation Count: ' || v_OperationCount);
    END LOOP;

    CLOSE cur_Result;
END;
/
```

# Example Output

Assuming the following data:
| Product Part ID | Product Name | Operation Count |
| --------------- | ------------ | --------------- |
| P12345 | Widget A | 12 |
| P12346 | Widget B | 12 |
| P12347 | Widget C | 10 |

The output will be:

Product Part ID: P12345, Product Name: Widget A, Operation Count: 12

# Consideration
### 1. Data Integrity

Ensure that the Product, PartOut and Operation tables are properly populated with valid data before running the function.

### 2. Performance 

For large datasets, query performance can be improved by:
- Indexing columns used in joins (PartID, OPID);
- Using a smaller buffer size for cursors if necessary.

### 3. Error Handling

The function includes an exception handler to raise a custom error message (ORA.20001) in case of any unexpected issues.

# Coclusion

The GetProductWithMostOperations function provides production managers with valuable insights into the complexity of their products' BOO. By identifying the product with the most operations, managers can prioritize resorces and address bottlenecks effectively.

# License

This project is licensed under the MIT License.

# Author

Jorge Almeida
[1220838@isep.ipp.pt](mailto:1220838@isep.ipp.pt)