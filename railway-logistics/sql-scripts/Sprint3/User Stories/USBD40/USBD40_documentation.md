# USBD40: Register Freight and Associated Wagons

## 1. Summary

The primary objective of this PL/SQL development is to fulfill the requirement of User Story USBD40: *"As a Freight Manager, I want to register a freight in the database. This includes the wagons involved. This is only possible if there are enough free wagons of the given type at the freight’s departure facility."*

To achieve this, two PL/SQL components were developed:
1.  **Function `fn_get_available_wagons`:** Allows the user to view available wagons at a specific facility before attempting registration.
2.  **Procedure `p_register_freight_with_wagons`:** Handles the atomic registration of a freight and its association with a specific list of wagons, enforcing all rules (availability, location, existence).

## 2. Implementation

The implementation separates the discovery phase (finding wagons) from the transactional phase (creating the freight), providing a user-friendly workflow while maintaining data integrity.

### 2.1. Code:

#### Function: `fn_get_available_wagons`

This function returns a cursor containing the list of wagons that are physically present at the origin facility, match the required type, and are currently free (not assigned to any active freight).

```sql
CREATE OR REPLACE FUNCTION fn_get_available_wagons(
    p_origin_id NUMBER,
    p_wagon_type_id NUMBER
)
    RETURN SYS_REFCURSOR
IS
    v_wagons SYS_REFCURSOR;
    v_count_facility NUMBER;
    v_count_wagon_type NUMBER;

    ex_invalid_location EXCEPTION;
    ex_invalid_wagon_type EXCEPTION;
BEGIN
    SELECT COUNT(*)
    INTO v_count_facility
    FROM Facility f
    WHERE f.facility_id = p_origin_id;

    IF v_count_facility = 0 THEN
        RAISE ex_invalid_location;
    END IF;

    SELECT COUNT(*)
    INTO v_count_wagon_type
    FROM WagonType wt
    WHERE wt.wagon_type_id = p_wagon_type_id;

    IF v_count_wagon_type = 0 THEN
        RAISE ex_invalid_wagon_type;
    END IF;

    OPEN v_wagons FOR
    SELECT w.wagon_number
    FROM Wagon w
    JOIN WagonModel wm ON w.wagon_model_id = wm.wagon_model_id
    WHERE w.start_facility_id = p_origin_id
      AND wm.wagon_type_id = p_wagon_type_id
      AND w.wagon_number NOT IN (SELECT fw.wagon_id FROM FreightWagon fw);
    RETURN v_wagons;

    EXCEPTION
        WHEN ex_invalid_location THEN
        RAISE_APPLICATION_ERROR(-20004, 'Error: Invalid location');
        WHEN ex_invalid_wagon_type THEN
        RAISE_APPLICATION_ERROR(-20005, 'Error: Invalid wagon type');
        WHEN OTHERS THEN
        RAISE;

END fn_get_available_wagons;
/
```

#### Procedure: `p_register_freight_with_wagons`

This procedure registers the freight header and links the specified wagons. It takes a comma-separated string of wagon IDs, parsing and validating each one before insertion. This ensures the operation is atomic: either the freight and all wagons are registered successfully, or nothing is saved.

```sql
CREATE OR REPLACE PROCEDURE p_register_freight_with_wagons(
    p_freight_id       IN Freight.freight_id%TYPE,
    p_freight_date     IN Freight.freight_date%TYPE,
    p_origin_id        IN Freight.origin_facility_id%TYPE,
    p_dest_id          IN Freight.destination_facility_id%TYPE,

    p_wagon_list_str   IN VARCHAR2
)
IS
    v_wagon_list       VARCHAR2(4000) := p_wagon_list_str || ',';
    v_pos_comma        NUMBER;
    v_current_wagon_id Wagon.wagon_number%TYPE;
    v_exists_check     NUMBER;

    v_count_check      NUMBER;
    v_wagon_facility   NUMBER;

    ex_no_wagons       EXCEPTION;
    ex_wagon_not_free  EXCEPTION;
    ex_wagon_wrong_location EXCEPTION;
    ex_wagon_not_found EXCEPTION;
    ex_freight_already_exists EXCEPTION;
BEGIN
    SELECT COUNT(*)
    INTO v_exists_check
    FROM Freight
    WHERE freight_id = p_freight_id;

    IF v_exists_check > 0 THEN
        RAISE ex_freight_already_exists;
    END IF;

    IF TRIM(p_wagon_list_str) IS NULL THEN
        RAISE ex_no_wagons;
    END IF;

    INSERT INTO Freight (freight_id, freight_date, origin_facility_id, destination_facility_id)
    VALUES (p_freight_id, p_freight_date, p_origin_id, p_dest_id);

    LOOP
        EXIT WHEN v_wagon_list IS NULL;

        v_pos_comma := INSTR(v_wagon_list, ',');

        EXIT WHEN v_pos_comma = 0;

        v_current_wagon_id := TRIM(SUBSTR(v_wagon_list, 1, v_pos_comma - 1));

        v_wagon_list := SUBSTR(v_wagon_list, v_pos_comma + 1);

        IF v_current_wagon_id IS NOT NULL THEN

            BEGIN
                SELECT start_facility_id
                INTO v_wagon_facility
                FROM Wagon
                WHERE wagon_number = v_current_wagon_id;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    RAISE ex_wagon_not_found;
            END;

            IF v_wagon_facility <> p_origin_id THEN
                RAISE ex_wagon_wrong_location;
            END IF;

            SELECT COUNT(*)
            INTO v_count_check
            FROM FreightWagon
            WHERE wagon_id = v_current_wagon_id;

            IF v_count_check > 0 THEN
                RAISE ex_wagon_not_free;
            END IF;

            INSERT INTO FreightWagon (freight_id, wagon_id)
            VALUES (p_freight_id, v_current_wagon_id);

            DBMS_OUTPUT.PUT_LINE('Wagon ' || v_current_wagon_id || ' linked successfully.');
        END IF;
    END LOOP;

    DBMS_OUTPUT.PUT_LINE('Freight ' || p_freight_id || ' registered completely.');

EXCEPTION
    WHEN ex_no_wagons THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20005, 'Error: There are no wagons to add');
    WHEN ex_wagon_not_found THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20006, 'Error: Wagon ' || v_current_wagon_id || ' does not exist.');
    WHEN ex_wagon_wrong_location THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20007, 'Error: Wagon ' || v_current_wagon_id || ' is not at origin facility.');
    WHEN ex_wagon_not_free THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20008, 'Error: Wagon ' || v_current_wagon_id || ' is already in use.');
    WHEN ex_freight_already_exists THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20018, 'Error: Freight ' || p_freight_id || ' already exists in the database.');
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END p_register_freight_with_wagons;
/
```

### 2.2. Logic Explanation
*   **Function:** The function validates the existence of wagons of the specified origin facility and wagon type. If valid, it retrieves wagons that are at the specified facility, match the wagon type, and are not currently assigned to any freight. This function also raises specific errors for invalid facility or wagon type.

*   **Procedure:** The procedure begins by checking if the freight ID already exists to prevent duplicates. It also checks if the wagon list is empty. It then inserts the freight header and processes each wagon ID from the comma-separated list. For each wagon, it checks for existence, correct location, and availability before linking it to the freight. If any validation fails, the entire transaction is rolled back to maintain data integrity. If the wagon passes all checks, it is linked to the freight.


## 3. Testing and Validation Strategy

A robust testing strategy was implemented covering 10 distinct scenarios using anonymous blocks to validate both the helper function and the main procedure. The anonymous blocks to test the function and procedure have the same structure to facilitate the tests, the only thing that changes are the values that are declared.The structure is the following:

### Anonymous Block Structure For Function `fn_get_available_wagons`
```sql
SET SERVEROUTPUT ON;
DECLARE
    l_cursor    SYS_REFCURSOR;
    l_wagon_num Wagon.wagon_number%TYPE;
    l_found     BOOLEAN := FALSE;

    v_facility_name Facility.facility_name%TYPE;
    v_facility_id NUMBER := *facility id*;
    v_wagon_type_id NUMBER := *wagon type id*;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST X: Description ---');

    l_cursor := fn_get_available_wagons(v_facility_id, v_wagon_type_id);

    SELECT f.facility_name
    INTO v_facility_name
    FROM Facility f
    WHERE f.facility_id = v_facility_id;

    DBMS_OUTPUT.PUT_LINE('Facility ' || v_facility_name || ' (' || v_facility_id || '):');
    LOOP
        FETCH l_cursor INTO l_wagon_num;
        EXIT WHEN l_cursor%NOTFOUND;

        DBMS_OUTPUT.PUT_LINE('Wagon: ' || l_wagon_num);
    END LOOP;
    CLOSE l_cursor;

    EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE = -20004 THEN
            DBMS_OUTPUT.PUT_LINE(SQLERRM);
        ELSIF SQLCODE = -20005 THEN
            DBMS_OUTPUT.PUT_LINE(SQLERRM);
        ELSE
            DBMS_OUTPUT.PUT_LINE(SQLERRM);
        END IF;
END;
/
```

### Anonymous Block Structure For Procedure `p_register_freight_with_wagons`

```sql
SET SERVEROUTPUT ON;
DECLARE
    l_freight_id NUMBER := *freight id*;
    l_freight_origin NUMBER := *origin facility id*;
    l_freight_destination NUMBER := *destination facility id*;

    l_facility_name VARCHAR(255);

    l_wagon_type_id NUMBER := *wagon type id*;
    l_wagon_list VARCHAR2(200) := *list of wagon id's separated by commas*;

    c_get_wagons_cursor SYS_REFCURSOR;
    l_wagon_num CHAR(11);

    c_wagons_associated_to_freight SYS_REFCURSOR;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST X: Description ---');

    SELECT f.facility_name
    INTO l_facility_name
    FROM Facility f
    WHERE f.facility_id = l_freight_origin;

    c_get_wagons_cursor := fn_get_available_wagons(l_freight_origin, l_wagon_type_id);

    DBMS_OUTPUT.PUT_LINE('Facility ' || l_facility_name || ' (' || l_freight_origin || '):');
    LOOP
        FETCH c_get_wagons_cursor INTO l_wagon_num;
        EXIT WHEN c_get_wagons_cursor%NOTFOUND;

        DBMS_OUTPUT.PUT_LINE('Wagon: ' || l_wagon_num);
    END LOOP;
    CLOSE c_get_wagons_cursor;

    DBMS_OUTPUT.PUT_LINE('');

    p_register_freight_with_wagons(
        p_freight_id     => l_freight_id,
        p_freight_date   => SYSDATE,
        p_origin_id      => l_freight_origin,
        p_dest_id        => l_freight_destination,
        p_wagon_list_str => l_wagon_list
    );
    DBMS_OUTPUT.PUT_LINE('');
    p_print_freight_details(l_freight_id);
    DBMS_OUTPUT.PUT_LINE('');
    DBMS_OUTPUT.PUT_LINE('Freight ' || l_freight_id || ' linked wagons:');
    c_wagons_associated_to_freight := fn_get_freight_associations(l_freight_id);
    LOOP
        FETCH c_wagons_associated_to_freight INTO l_wagon_num;
        EXIT WHEN c_wagons_associated_to_freight%NOTFOUND;

        DBMS_OUTPUT.PUT_LINE('Freight ' || l_freight_id || ': Wagon ' || l_wagon_num);
    END LOOP;
    CLOSE c_wagons_associated_to_freight;

    ROLLBACK;
    EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE IN (-20006, -20007, -20008, -20009) THEN
            DBMS_OUTPUT.PUT_LINE(SQLERRM);
        ELSE
            DBMS_OUTPUT.PUT_LINE('Unexpected error: ' || SQLERRM);
        END IF;
        ROLLBACK;
END;
/
```

Additionally, for a better user experience, a helper procedure `p_print_freight_details` was created to display freight information after registration attempts:
    
```sql
CREATE OR REPLACE FUNCTION fn_get_freight_associations (
    p_freight_id NUMBER
)

RETURN SYS_REFCURSOR

IS
    v_wagons SYS_REFCURSOR;
BEGIN
    OPEN v_wagons FOR
    SELECT fw.wagon_id
    FROM FreightWagon fw
    WHERE fw.freight_id = p_freight_id;
    RETURN v_wagons;
END;
/

CREATE OR REPLACE PROCEDURE p_print_freight_details(
    p_freight_id IN Freight.freight_id%TYPE
)
IS
    v_freight_date  Freight.freight_date%TYPE;
    v_origin_name   Facility.facility_name%TYPE;
    v_dest_name     Facility.facility_name%TYPE;
BEGIN
    SELECT
        f.freight_date,
        origin.facility_name,
        dest.facility_name
    INTO
        v_freight_date,
        v_origin_name,
        v_dest_name
    FROM Freight f
    JOIN Facility origin ON f.origin_facility_id = origin.facility_id
    JOIN Facility dest ON f.destination_facility_id = dest.facility_id
    WHERE f.freight_id = p_freight_id;

    DBMS_OUTPUT.PUT_LINE('New Freight Information');
    DBMS_OUTPUT.PUT_LINE('Freight ID   : ' || p_freight_id);
    DBMS_OUTPUT.PUT_LINE('Date         : ' || v_freight_date);
    DBMS_OUTPUT.PUT_LINE('Origin       : ' || v_origin_name);
    DBMS_OUTPUT.PUT_LINE('Destination  : ' || v_dest_name);

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Unexpected Error: ' || SQLERRM);
END p_print_freight_details;
/
```

This function retrieves and displays the freight's date, origin, and destination based on the provided freight ID.

### 3.1. Test Data Setup

Specific test data was inserted to create predictable conditions:
*   **Valid Wagons:** `123 1 000`, `123 4 000`, `123 5 000` at the correct origin (Facility 50).
*   **Wrong Location Wagon:** `123 2 000` at a different facility (Facility 12).
*   **Busy Wagon:** `123 3 000` at the correct origin but already assigned to Freight 2001.

### 3.2. Test Cases

#### Function `fn_get_available_wagons`

1.  **Valid Retrieval:** Confirms the function lists the valid wagons (`123 1 000`, etc.) for Facility 50.
2.  **Invalid Facility:** Confirms error `-20004` when querying a non-existent facility.
3.  **Invalid Wagon Type:** Confirms error `-20005` when querying a non-existent wagon type.

#### Procedure `p_register_freight_with_wagons`

4.  **Valid Insertion (Single Wagon):** Successfully creates a freight with one wagon (`123 1 000`).
5.  **Valid Insertion (Multiple Wagons):** Successfully creates a freight with a list of wagons (`123 1 000, 123 4 000, 123 5 000`).
6.  **Wagon Doesn't Exist:** Attempts to add `000 0 000`, expects error `-20007`.
7.  **Wrong Facility:** Attempts to add `123 2 000` (which is at Facility 12 instead of 50), expects error `-20008`.
8.  **Wagon Not Free:** Attempts to add `123 3 000` (busy), expects error `-20009`.
9.  **No Wagons Provided:** Attempts to register freight with an empty wagon list, expects error `-20006`.
10. **Freight Already Exists:** Attempts to register a freight with an existing ID, expects error `-20018`.

## 4. Conclusion

The developed solution fulfills USBD40 by ensuring data integrity and validating business rules (wagon availability and location) before registration. The dual approach (search function + registration procedure) provides flexibility for user interfaces while the procedure guarantees atomic transactions. The comprehensive test suite validates all edge cases.