# USBD41: Remove Freight from Planned Train

## 1. Summary

The primary objective of this PL/SQL development is to fulfill the requirement of User Story USBD41: *"As a Freight Manager, I want to remove a freight from a planned train."*

To achieve this, a stored procedure `p_remove_freight_from_train` was developed. This procedure ensures data integrity by validating the existence of the freight and its association with the train before performing the deletion. When a freight is removed, its logical link to the train is severed, effectively removing all associated wagons from the train's composition.

## 2. Procedure Implementation

The implementation strategy focuses on encapsulated logic within a stored procedure to handle validation and data modification securely.

### 2.1. Procedure Code

```sql
CREATE OR REPLACE PROCEDURE p_remove_freight_from_train(
    p_train_id   IN Train.train_id%TYPE,
    p_freight_id IN Freight.freight_id%TYPE
)
IS
    v_check_assign NUMBER;
    v_validation   NUMBER;
    
    ex_invalid_train         EXCEPTION;
    ex_invalid_freight       EXCEPTION;
    ex_association_not_found EXCEPTION;
BEGIN
      -- 1. Validate if Freight exists
      BEGIN
        SELECT 1 INTO v_validation FROM Freight WHERE freight_id = p_freight_id;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN RAISE ex_invalid_freight;
    END;

    -- 2. Validate Association
    SELECT COUNT(*)
    INTO v_check_assign
    FROM TrainFreight
    WHERE train_id = p_train_id AND freight_id = p_freight_id;

    IF v_check_assign = 0 THEN
        RAISE ex_association_not_found;
    END IF;

    -- 3. Perform Deletion
    DELETE FROM TrainFreight
    WHERE train_id = p_train_id AND freight_id = p_freight_id;

EXCEPTION
    WHEN ex_association_not_found THEN
        RAISE_APPLICATION_ERROR(-20010, 'Error: Freight ' || p_freight_id || ' is not associated with Train ' || p_train_id || '.');
    WHEN ex_invalid_freight THEN
        RAISE_APPLICATION_ERROR(-20012, 'Error: Freight ' || p_freight_id || ' does not exist.');
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20013, 'Error removing freight: ' || SQLERRM);
END p_remove_freight_from_train;
/
```

### 2.2. Logic Explanation

The procedure executes a sequence of validations before committing the change:
1.  **Validation of Freight Existence:** Checks if the `p_freight_id` exists in the `Freight` table.
2.  **Validation of Association:** Verifies if there is a record in `TrainFreight` linking the specified train and freight. If `COUNT` is 0, it means the freight is not part of this train, and an exception is raised.
3.  **Deletion:** If all checks pass, the procedure executes a `DELETE` on `TrainFreight`. This removes the logical link. Since wagons are associated with a Freight, removing the Freight from the Train effectively removes the wagons from that Train's manifest.

## 3. Testing and Validation Strategy

To ensure robust functionality, a series of PL/SQL anonymous blocks were created to test various scenarios, including valid operations and error conditions. The anonymous blocks follow the same structure for consistency:

```sql
SET SERVEROUTPUT ON;
DECLARE
    v_train_id          Train.train_id%TYPE := *train id*;
    v_freight_id        Freight.freight_id%TYPE := *freight id*;

    v_temp_freight_id Freight.freight_id%TYPE;
    v_temp_train_id Train.train_id%TYPE;
    v_freight_cursor SYS_REFCURSOR;
BEGIN

    DBMS_OUTPUT.PUT_LINE('--- Teste X: Description ---');
    v_freight_cursor := fn_show_train_freights(v_train_id);

    DBMS_OUTPUT.PUT_LINE('Freight before removal: ');
    LOOP
        FETCH v_freight_cursor INTO v_temp_freight_id, v_temp_train_id;
        EXIT WHEN v_freight_cursor%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Train ID: ' || v_temp_train_id || ', Freight ID: ' || v_temp_freight_id);
    END LOOP;
    CLOSE v_freight_cursor;


    p_remove_freight_from_train(v_train_id, v_freight_id);
    DBMS_OUTPUT.PUT_LINE('Freight after removal: ');
    v_freight_cursor := fn_show_train_freights(v_train_id);
    LOOP
        FETCH v_freight_cursor INTO v_temp_freight_id, v_temp_train_id;
        EXIT WHEN v_freight_cursor%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Train ID: ' || v_temp_train_id || ', Freight ID: ' || v_temp_freight_id);
    END LOOP;
    CLOSE v_freight_cursor;
    ROLLBACK;

EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE IN (-20010, -20011, -20012, -20013) THEN
            DBMS_OUTPUT.PUT_LINE(SQLERRM);
        ELSE
            DBMS_OUTPUT.PUT_LINE('Unexpected Error: ' || SQLERRM);
        END IF;
        ROLLBACK;
END;
/
```

Furthermore, the function `fn_show_train_freights` is implemented to increase user-friendliness by displaying the current freights associated with a train:

```sql
CREATE OR REPLACE FUNCTION fn_show_train_freights(
    p_train_id      IN Train.train_id%TYPE
) RETURN SYS_REFCURSOR
IS
    v_freight_cursor     SYS_REFCURSOR;
    v_validation         NUMBER;

    ex_invalid_train     EXCEPTION;
BEGIN
    BEGIN
        SELECT 1 INTO v_validation FROM Train WHERE train_id = p_train_id;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN RAISE ex_invalid_train;
    END;

    OPEN v_freight_cursor FOR
        SELECT tf.freight_id, tf.train_id
        FROM TrainFreight tf
        JOIN Freight f ON tf.freight_id = f.freight_id
        WHERE tf.train_id = p_train_id;

    RETURN v_freight_cursor;

EXCEPTION
    WHEN ex_invalid_train THEN
        RAISE_APPLICATION_ERROR(-20011, 'Error: Train not found.');
    WHEN OTHERS THEN
        RAISE;
END fn_show_train_freights;
/
```

### 3.1. Test Cases

#### Test 1: Valid Removal
*   **Objective:** Verify that a valid association between a freight and a train can be successfully removed.
*   **Method:** Displays the list of freights associated with Train 5421 before and after calling the procedure to remove Freight 2003.
*   **Expected Result:** Freight 2003 disappears from the list after execution.

#### Test 2: Invalid Association
*   **Objective:** Verify that the procedure correctly handles a request to remove a freight that is not associated with the specified train.
*   **Method:** Attempts to remove Freight 2003 from Train 5435 (where it is not assigned).
*   **Expected Result:** Captures the exception with SQLCODE -20010 ("Freight is not associated with Train").

#### Test 3: Invalid Train ID
*   **Objective:** Verify behavior when an invalid train ID is provided (although not explicitly checked via SELECT, the Association check implicitly handles this).
*   **Method:** Attempts to remove Freight 2003 from Train 0000.
*   **Expected Result:** Since no association exists for Train 0000, it raises SQLCODE -20011 ("Train does not exist").

#### Test 4: Invalid Freight ID
*   **Objective:** Verify that the procedure detects a non-existent freight ID.
*   **Method:** Attempts to remove Freight 9999 from Train 5435.
*   **Expected Result:** Captures the exception with SQLCODE -20012 ("Freight does not exist").

## 4. Conclusion

The procedure `p_remove_freight_from_train` successfully implements the business logic required for USBD41. It ensures data integrity by validating entities and relationships before modification, allowing Freight Managers to correct or update train compositions securely.