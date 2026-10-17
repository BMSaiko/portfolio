# USBD30 - As a Factory Manager, I want to use/consume a material/component, i.e., to deduct a given amount from the stock. The operation should not be allowed if the remaining stock falls below the currently reserved quantity.

# Overview
The USBD30 requires a PL/SQL function to manage stock deductions for materials or components. This function checks if the stock after the deduction falls below the reserved quantity. If it does, the operation is not allowed. Otherwise, the stock is updated, and the transaction is committed.
The function deduct_stock is designed to ensure that inventory management is done accurately and that reserved stock levels are respected.

# Function Signature

```
--USBD30
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
```

# Description
The **deduct_stock** function handles the deduction of stock for a given part. It checks if the remaining stock after the deduction would fall below the reserved stock. If this is the case, the operation is aborted, and an appropriate message is returned. If the stock can be deducted, the function updates the stock in the database and commits the transaction.

# Logic Breakdown
## 1. Fetch Current and Reserved Stock
```
SELECT stock, reserved INTO v_current_stock, v_reserved_stock
FROM ExternalPart
WHERE PartID = p_partid;
```
**Purpose:** Retrieve the current stock and reserved quantity for the part identified by p_partid.

## 2. Stock Deduction Check
```
IF v_current_stock - p_quantity < v_reserved_stock THEN
    RETURN 'Operation cannot be performed. Remaining stock is below the reserved quantity.';
END IF;
```
**Purpose:** Ensure that after deducting p_quantity from the current stock, the remaining stock does not fall below the reserved quantity. If it does, the operation is rejected.

## 3. Deduct Stock and Update Database
```
v_new_stock := v_current_stock - p_quantity;
UPDATE ExternalPart
SET stock = v_new_stock
WHERE PartID = p_partid;
COMMIT;
```
**Purpose:** Perform the deduction by subtracting p_quantity from v_current_stock, then update the stock in the ExternalPart table. A commit is issued to finalize the change.

## 4. Exception Handling
```
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN 'PartID not found.';
    WHEN OTHERS THEN
        RETURN 'An error occurred: ' || SQLERRM;
```
**Purpose:** Handle exceptions. If no matching part is found, a specific message is returned. Other exceptions are caught and reported with the error message.

# Usage
To execute the deduct_stock function, you can use the following anonymous PL/SQL block:
```
SET SERVEROUTPUT ON;

DECLARE
  result VARCHAR2(255); -- Variable to store the result of the function
BEGIN
  -- Test the function with a valid PartID and quantity
  result := deduct_stock('PN94561L67', 0);
  DBMS_OUTPUT.PUT_LINE('Test 1: ' || result);
  
  -- Test the function where the stock will go below reserved stock
  result := deduct_stock('PN94561L67', 100);  -- assuming reserved stock is 50 and current stock is 60
  DBMS_OUTPUT.PUT_LINE('Test 2: ' || result);
  
  -- Test the function with an invalid PartID
  result := deduct_stock('INVALID_PART', 5);
  DBMS_OUTPUT.PUT_LINE('Test 3: ' || result);
END;
/
```

# Example Output
```
Test 1: Stock deduction successful.
Test 2: Operation cannot be performed. Remaining stock is below the reserved quantity.
Test 3: PartID not valid.
```

# Consideration
### 1. Data Integrity

Ensure that the **ExternalPart** table contains accurate data, particularly for the stock and reserved columns. This will prevent errors in stock management.

### 2. Performance 

- Indexes on the **PartID** field in the ExternalPart table can improve the query performance.
- Keep in mind the impact of the **COMMIT** statement in terms of database locks and transaction management.

### 3. Error Handling

The function handles cases where:

- No matching part is found (**NO_DATA_FOUND**).
- Any other unexpected issues are raised with the message **An error occurred**.

# Coclusion

The **deduct_stock** function ensures that the stock management system respects reserved quantities when deducting material from the inventory. By using this function, factory managers can effectively manage stock levels and avoid overselling or reducing available materials below the reserved amount.

# License

This project is licensed under the MIT License.

# Author

Jorge Almeida
[1220838@isep.ipp.pt](mailto:1220838@isep.ipp.pt)