# USBD39: Associate Locomotive with Planned Train

## 1. Summary

The goal of this PL/SQL development is to fulfill the requirement of User Story USBD39: *"As a Freight Manager, I want to associate a locomotive with a planned train."*

To achieve this, a stored procedure `p_add_locomotive_to_train` was developed. This procedure validates the existence of the train and locomotive, and adherence to the train's maximum length (USBD33) before associating the locomotive with the train in the database.

## 2. Procedure Implementation

The implementation strategy focuses on encapsulated logic within a stored procedure to handle validation and data modification securely.

### 2.1. Procedure Code

```sql
CREATE OR REPLACE PROCEDURE p_add_locomotive_to_train(
    p_train_id          IN Train.train_id%TYPE,
    p_locomotive_id     IN Locomotive.locomotive_number%TYPE
)
IS
    v_max_allowed               Train.max_length%TYPE;
    v_path_id                   Train.path_id%TYPE;
    v_validation                NUMBER;
    v_current_length            NUMBER;
    v_new_locomotive_length     NUMBER;
    

    ex_length_limit EXCEPTION;
    ex_invalid_locmotive EXCEPTION;
BEGIN

    BEGIN
        SELECT 1 INTO v_validation FROM Locomotive WHERE locomotive_number = p_locomotive_id;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN RAISE ex_invalid_locmotive;
    END;

    SELECT max_length, path_id
    INTO v_max_allowed, v_path_id
    FROM Train
    WHERE train_id = p_train_id;

    v_current_length := get_train_max_length(p_train_id, v_path_id);

    SELECT lm.locomotive_model_length
    INTO v_new_locomotive_length
    FROM Locomotive l
    JOIN LocomotiveModel lm ON l.model_id = lm.locomotive_model_id
    WHERE l.locomotive_number = p_locomotive_id;

    IF (v_current_length + v_new_locomotive_length) > v_max_allowed THEN
        RAISE ex_length_limit;
    END IF;

    INSERT INTO TrainLocomotive (train_id, locomotive_id)
    VALUES (p_train_id, p_locomotive_id);

    DBMS_OUTPUT.PUT_LINE('Locomotive added successfully to the train.');

EXCEPTION
    WHEN ex_length_limit THEN
        RAISE_APPLICATION_ERROR(-20001, 'Error: The train would exceed the maximum length limit.');
    WHEN ex_invalid_locmotive THEN
        RAISE_APPLICATION_ERROR(-20003, 'Error: Locomotive not found.');
    WHEN OTHERS THEN
        RAISE;
END p_add_locomotive_to_train;
/
```

### 2.2. Logic Explanation

The procedure executes a sequence of validations before committing the change:
1.  **Validation of Locomotive Existence:** Checks if the `p_locomotive_id` exists in the `Locomotive` table.
2.  **Retrieval of Train Constraints:** Obtains the `max_length` allowed and `path_id` for the specified `p_train_id`. If the train does not exist, an Oracle error (`NO_DATA_FOUND`) will naturally be raised and caught by the general exception handler.
3.  **Calculation of Current Length:** Invokes a helper function `get_train_max_length` (from USBD33) to calculate the current total length of the train (including existing locomotives and wagons).
4.  **Retrieval of New Locomotive Length:** Fetches the physical length of the model of the locomotive to be added.
5.  **Length Validation:** Compares the sum of the current length and the new locomotive's length against the train's maximum allowed length. If it exceeds the limit, a custom exception `ex_length_limit` is raised.
6.  **Insertion:** If all checks pass, the locomotive is inserted into the `TrainLocomotive` table.

## 3. Testing and Validation Strategy

To ensure robust functionality, a series of PL/SQL anonymous blocks were created to test various scenarios, including valid operations and error conditions. All the anonymous blocks have the same structure to facilitate the tests, the only thing that changes are the values that are declared.The structure is the following:

```sql
SET SERVEROUTPUT ON;
DECLARE
    v_train_id              Train.train_id%TYPE := *train_id*;
    v_locomotive_id         Locomotive.locomotive_number%TYPE := *locomotive_id*;

    v_temp_locomotive_id Locomotive.locomotive_number%TYPE;
    v_temp_train_id Train.train_id%TYPE;
    v_locomotive_cursor SYS_REFCURSOR;

BEGIN

    DBMS_OUTPUT.PUT_LINE('--- Teste 1: Locomotive fits within Train max length after addition, loc n 8801 to train n 9001---');
    v_locomotive_cursor := fn_show_train_locomotives(v_train_id);


    DBMS_OUTPUT.PUT_LINE('Locomotive ID before addition: ');
    LOOP
        FETCH v_locomotive_cursor INTO v_temp_locomotive_id, v_temp_train_id;
        EXIT WHEN v_locomotive_cursor%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Train ID: ' || v_temp_train_id || ', Locomotive ID: ' || v_temp_locomotive_id);
    END LOOP;
    CLOSE v_locomotive_cursor;
    p_add_locomotive_to_train(v_train_id, v_locomotive_id);
    v_locomotive_cursor := fn_show_train_locomotives(v_train_id);

    DBMS_OUTPUT.PUT_LINE('Locomotive ID after addition: ');
    LOOP
        FETCH v_locomotive_cursor INTO v_temp_locomotive_id, v_temp_train_id;
        EXIT WHEN v_locomotive_cursor%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Train ID: ' || v_temp_train_id || ', Locomotive ID: ' || v_temp_locomotive_id);
    END LOOP;
    CLOSE v_locomotive_cursor;
    ROLLBACK;
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE IN (-20001, -20002, -20003) THEN
            DBMS_OUTPUT.PUT_LINE(SQLERRM);
        ELSE
            DBMS_OUTPUT.PUT_LINE('ERRO INESPERADO: ' || SQLERRM);
        END IF;
    ROLLBACK;
END;
/
```

### 3.1. Test Data Setup

Specific data was inserted to facilitate testing:
*   A test locomotive model (ID 99) with defined dimensions.
*   Two test locomotives (IDs 8801 and 8802).
*   A train with ample capacity (ID 9001, max length 500m) for positive testing.
*   A train with restricted capacity (ID 9002, max length 10m) for negative testing (length limit).

### 3.2. Test Outputs

For a better user experience, the function `fn_show_train_locomotives` was created to display the current locomotives associated with a train before and after the addition attempt:

```sql
CREATE OR REPLACE FUNCTION fn_show_train_locomotives(
    p_train_id      IN Train.train_id%TYPE
) RETURN SYS_REFCURSOR
IS
    v_locomotive_cursor     SYS_REFCURSOR;
    v_validation            NUMBER;

    ex_invalid_train  EXCEPTION;
BEGIN
    BEGIN
        SELECT 1 INTO v_validation FROM Train WHERE train_id = p_train_id;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN RAISE ex_invalid_train;
    END;

    OPEN v_locomotive_cursor FOR
        SELECT tl.locomotive_id, tl.train_id
        FROM TrainLocomotive tl
        WHERE tl.train_id = p_train_id;

    RETURN v_locomotive_cursor;

EXCEPTION
    WHEN ex_invalid_train THEN
        RAISE_APPLICATION_ERROR(-20002, 'Erro: Comboio nÃ£o encontrado.');
    WHEN OTHERS THEN
        RAISE;
END fn_show_train_locomotives;
/
```

### 3.3. Test Cases

#### Test 1: Valid Addition
*   **Objective:** Verify that a valid locomotive can be added to a train with sufficient space.
*   **Method:** Displays the list of locomotives before and after calling the procedure for Train 9001 and Locomotive 8802.
*   **Expected Result:** The locomotive ID 8802 appears in the "after" list without errors.

#### Test 2: Invalid Locomotive
*   **Objective:** Verify that the procedure correctly identifies and rejects a non-existent locomotive ID.
*   **Method:** Attempts to add Locomotive 9999 to Train 9001.
*   **Expected Result:** Captures the exception with SQLCODE -20003 ("Locomotive not found").

#### Test 3: Invalid Train
*   **Objective:** Verify that the procedure handles a non-existent train ID.
*   **Method:** Attempts to add Locomotive 8802 to Train 9999.
*   **Expected Result:** Since the `SELECT INTO` for train details fails, it raises an unexpected error (typically ORA-01403: no data found), which is caught by the `OTHERS` handler.

#### Test 4: Maximum Length Exceeded
*   **Objective:** Verify that the procedure enforces the train's length constraint.
*   **Method:** Attempts to add Locomotive 8802 (length 20m) to Train 9002 (max length 10m).
*   **Expected Result:** Captures the exception with SQLCODE -20001 ("The train would exceed the maximum length limit").

## 4. Conclusion

The procedure `p_add_locomotive_to_train` successfully implements the logic required for USBD39. It ensures data integrity by validating entities and enforcing physical constraints (train length) before modification, adhering to the project's requirements for secure database operations.
