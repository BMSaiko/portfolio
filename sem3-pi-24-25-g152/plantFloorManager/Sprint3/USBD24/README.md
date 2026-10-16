# USBD24 - AsaProduction Manager, I don’t want it to be possible to add a product as an input in its own BOO. A trigger should be developed to avoid such circular references. This doesn’t apply to the BOOs of subproducts.

# Overview
The USBD24 creates a trigger that ensures that circular references are not introduced in the BOO_Operation table when inserting a new record into the PartIN table. The trigger detects potential cycles in the BOO chain to maintain data integrity and prevent logical errors in the BOO (Bill of Operations) structure.
This trigger is critical for managing relationships between operations and ensuring that there are no cyclic dependencies, which could lead to infinite loops in processing or incorrect data representations.

# Function Signature

```
--USBD24
CREATE OR REPLACE TRIGGER prevent_circular_boo_references
BEFORE INSERT ON PartIN
FOR EACH ROW
DECLARE
    v_is_circular BOOLEAN := FALSE;
    v_partid VARCHAR2(50);
    v_opid NUMBER(10);
    v_booid VARCHAR2(10);
    v_next_opid NUMBER(10);
    v_next_booid VARCHAR2(10);
BEGIN
    -- Get the PartID and BOO information for the current insert
    v_partid := :NEW.PartID;
    v_opid := :NEW.OPID;
    v_booid := :NEW.BOOID;

    -- Check if the PartID being inserted is part of any BOO chain
    -- Start by checking if the current OPID and BOOID are part of a cycle
    FOR rec IN (
        SELECT bo.OPID, bo.BOOID, bo.NextOPID, bo.NextBOOID
        FROM BOO_Operation bo
        WHERE bo.BOOID = v_booid AND bo.OPID = v_opid
    ) LOOP
        -- Check if the next operation in the chain points back to the same PartID
        v_next_opid := rec.NextOPID;
        v_next_booid := rec.NextBOOID;

        -- Check for circular references by looping through the BOO_Operation table
        -- Ensure that no operation leads back to the original PartID in a circular manner
        LOOP
            EXIT WHEN v_next_opid IS NULL OR v_next_booid IS NULL;
            
            -- Check the next operation in the BOO_Operation table
            SELECT NextOPID, NextBOOID
            INTO v_next_opid, v_next_booid
            FROM BOO_Operation
            WHERE OPID = v_next_opid AND BOOID = v_next_booid;

            -- If we have reached the original OPID and BOOID, then it's a circular reference
            IF v_next_opid = v_opid AND v_next_booid = v_booid THEN
                v_is_circular := TRUE;
                EXIT;
            END IF;
        END LOOP;
    END LOOP;

    -- Raise an exception if a circular reference is detected
    IF v_is_circular THEN
        RAISE_APPLICATION_ERROR(-20001, 'Circular reference detected in BOO input. The part cannot be inserted.');
    END IF;
END;
/
```

# Description
The **prevent_circular_boo_references** trigger validates the BOO structure by ensuring that no circular references exist when a new part is added. It checks the chain of operations in the BOO_Operation table to detect if any operation loops back to the same PartID and BOOID.

# Logic Breakdown
## 1. Fetch Current and Reserved Stock
```
v_partid := :NEW.PartID;
v_opid := :NEW.OPID;
v_booid := :NEW.BOOID;
```
**Purpose:** Retrieve the PartID, OPID, and BOOID from the new record being inserted.

## 2. Stock Deduction Check
```
FOR rec IN (
    SELECT bo.OPID, bo.BOOID, bo.NextOPID, bo.NextBOOID
    FROM BOO_Operation bo
    WHERE bo.BOOID = v_booid AND bo.OPID = v_opid
) LOOP
```
**Purpose:** Iterate through the BOO chains related to the current BOOID and OPID to begin cycle detection.

## 3. Deduct Stock and Update Database
```
LOOP
    EXIT WHEN v_next_opid IS NULL OR v_next_booid IS NULL;
    SELECT NextOPID, NextBOOID
    INTO v_next_opid, v_next_booid
    FROM BOO_Operation
    WHERE OPID = v_next_opid AND BOOID = v_next_booid;

    IF v_next_opid = v_opid AND v_next_booid = v_booid THEN
        v_is_circular := TRUE;
        EXIT;
    END IF;
END LOOP;
```
**Purpose:** Traverse the BOO chain iteratively. If a cycle is detected (next operation matches the original operation), set the circular flag.

## 4. Exception Handling
```
IF v_is_circular THEN
    RAISE_APPLICATION_ERROR(-20001, 'Circular reference detected in BOO input. The part cannot be inserted.');
END IF;
```
**Purpose:** Abort the insert operation if a circular reference is found, ensuring data integrity.

# Usage
This trigger is invoked automatically when a new record is inserted into the PartIN table. It ensures that the BOO_Operation table maintains a valid and acyclic structure.

## Example Scenario

### 1.Valid Insertion:
- A new part is added with no circular references in the BOO chain.
- The operation is allowed, and the record is inserted.

### 2.Invalid Insertion:
- A new part is added, introducing a circular reference in the BOO chain.
- The operation fails with the error: Circular reference detected in BOO input. The part cannot be inserted.

# Consideration
### 1. Data Integrity

Ensure that the **BOO_Operation** table is well-indexed on OPID and BOOID for efficient lookups.

### 2. Performance 

Large or deeply nested **BOO** chains may impact performance. Optimize queries where possible.

### 3. Error Handling

The trigger prevents invalid data but may fail if the chain is too long or contains invalid entries. Ensure data consistency before insertion.

# License

This project is licensed under the MIT License.

# Author

Jorge Almeida
[1220838@isep.ipp.pt](mailto:1220838@isep.ipp.pt)