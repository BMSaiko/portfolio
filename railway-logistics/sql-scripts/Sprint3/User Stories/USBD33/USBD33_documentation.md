# USBD33: Validate Train Maximum Length

## 1. Summary

The primary objective of this PL/SQL development is to fulfill the requirement of User Story USBD33: *"As a Freight Manager, I want the database to not allow a train to exceed its predefined maximum length."*

The challenge with this requirement is that a train's length is not static; it changes as the train picks up and drops off freight at different stations along its path. To address this, a stored function (`get_train_max_length`) was created to calculate the peak length usage throughout the journey. This function is utilized within stored procedures, (`p_add_freight_to_train`) and (`p_add_locomotive_to_train`).
*   **`p_add_freight_to_train`**: Adds a freight to a train after validating that the addition does not cause the train to exceed its maximum length at any point in its route.
*   **`p_add_locomotive_to_train`**: Adds a locomotive to a train, ensuring that the total length of the train (including the new locomotive) does not exceed its maximum length. This procedure can be found in the USBD39 documentation.


## 2. Implementation

The implementation relies on an "Insertion with Rollback" strategy within a stored procedure to bypass Oracle's "Mutating Table" constraints while ensuring data consistency.

### 2.1. Helper Function: `get_train_max_length`

This function calculates the maximum length a train reaches at any point in its journey. It iterates through the train's path, adding the length of freights picked up and subtracting the length of freights dropped off at each station.

```sql
CREATE OR REPLACE FUNCTION get_train_max_length (
    p_train_id IN NUMBER,
    p_path_id  IN NUMBER
) RETURN NUMBER
IS
    v_locomotive_length NUMBER := 0;
    v_maximum_length    NUMBER := 0;
    current_length      NUMBER := 0;
BEGIN
    -- 1. Calculate Locomotive Length (Base Length)
    SELECT NVL(SUM(lm.locomotive_model_length), 0)
    INTO v_locomotive_length
    FROM TrainLocomotive tl
    JOIN Locomotive l ON l.locomotive_number = tl.locomotive_id
    JOIN LocomotiveModel lm ON lm.locomotive_model_id = l.model_id
    WHERE tl.train_id = p_train_id;

    v_maximum_length := v_locomotive_length;
    current_length := 0;

    -- 2. Iterate through path stops to calculate net length change
    FOR r IN (
        SELECT
            path_facility_order,
            NVL(SUM(wagon_lenght), 0) AS length_change_per_station
        FROM (
            -- Freights picked up (Positive Length)
            SELECT
                pf.path_facility_order,
                wm.wagon_model_length AS wagon_lenght
            FROM TrainFreight tf
            JOIN Freight f ON f.freight_id = tf.freight_id
            JOIN FreightWagon fw ON fw.freight_id = f.freight_id
            JOIN Wagon w ON w.wagon_number = fw.wagon_id
            JOIN WagonModel wm ON wm.wagon_model_id = w.wagon_model_id
            JOIN PathFacility pf 
                ON pf.facility_id = f.origin_facility_id 
               AND pf.path_id = p_path_id 
            WHERE tf.train_id = p_train_id

            UNION ALL

            -- Freights dropped off (Negative Length)
            SELECT
                pf.path_facility_order,
                -wm.wagon_model_length AS wagon_lenght
            FROM TrainFreight tf
            JOIN Freight f ON f.freight_id = tf.freight_id
            JOIN FreightWagon fw ON fw.freight_id = f.freight_id
            JOIN Wagon w ON w.wagon_number = fw.wagon_id
            JOIN WagonModel wm ON wm.wagon_model_id = w.wagon_model_id
            JOIN PathFacility pf 
                ON pf.facility_id = f.destination_facility_id 
               AND pf.path_id = p_path_id
            WHERE tf.train_id = p_train_id
        ) combined_changes
        GROUP BY path_facility_order
        ORDER BY path_facility_order ASC
    ) LOOP
        -- Update current running length
        IF r.length_change_per_station IS NOT NULL THEN
            current_length := current_length + r.length_change_per_station;
        END IF;

        -- Check if this is the new peak
        v_maximum_length := GREATEST(v_maximum_length, v_locomotive_length + current_length);
    END LOOP;

    RETURN v_maximum_length;
END;
/
```

#### 2.1.1. Logic Explanation
The function works as follows:
1. **Locomotive Length Calculation**: It first calculates the total length of all locomotives attached to the train, which forms the base length.
2. **Path Iteration**: It then iterates through each station on the train's path, calculating the net length change at each stop by summing the lengths of freights picked up (positive) and those dropped off (negative).
3. **Peak Length Tracking**: During the iteration, it keeps track of the maximum length reached at any point in the journey, which is returned as the function's result.

### 2.2. Main Procedure: `p_add_freight_to_train`

This procedure encapsulates the logic for adding a freight. It performs the validation *after* a tentative insert but *before* the final commit.

```sql
CREATE OR REPLACE PROCEDURE p_add_freight_to_train(
    p_train_id   IN Train.train_id%TYPE,
    p_freight_id IN Freight.freight_id%TYPE
)
IS
    v_max_allowed    Train.max_length%TYPE;
    v_path_id        Train.path_id%TYPE;
    v_real_maximum_length   NUMBER;
    v_verication NUMBER;

    ex_train_not_found     EXCEPTION;
    ex_freight_not_found   EXCEPTION;
    ex_length_limit        EXCEPTION;
BEGIN
    -- 1. Get Train Info
    BEGIN
        SELECT max_length, path_id 
        INTO v_max_allowed, v_path_id 
        FROM Train 
        WHERE train_id = p_train_id;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN RAISE ex_train_not_found;
    END;

    -- 2. Validate Freight Existence
    BEGIN
        SELECT 1 INTO v_verication FROM Freight WHERE freight_id = p_freight_id;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN RAISE ex_freight_not_found;
    END;

    -- 3. Optimistic Insert
    INSERT INTO TrainFreight (train_id, freight_id) 
    VALUES (p_train_id, p_freight_id);

    -- 4. Calculate NEW Peak Length (Function reads the inserted data)
    v_real_maximum_length := get_train_max_length(p_train_id, v_path_id);

    -- 5. Validation Check
    IF v_real_maximum_length > v_max_allowed THEN
        ROLLBACK; -- Revert the insert
        RAISE ex_length_limit;
    END IF;

    -- 6. Commit if successful
    COMMIT;
    DBMS_OUTPUT.PUT_LINE('Success: Freight ' || p_freight_id || ' added. Peak Length on Route: ' || v_real_maximum_length);

EXCEPTION
    WHEN ex_length_limit THEN
        RAISE_APPLICATION_ERROR(-20020, 
            'Train Length Limit Exceeded! Max Allowed: ' || v_max_allowed || 
            ', Required Maximum Length: ' || v_real_maximum_length);
    WHEN ex_train_not_found THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20021, 'Error: Train ' || p_train_id || ' not found.');
    WHEN ex_freight_not_found THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20022, 'Error: Freight ' || p_freight_id || ' not found.');
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20023, 'Unexpected Error: ' || SQLERRM);
END p_add_freight_to_train;
/
```

#### 2.2.1. Logic Explanation
The procedure follows these steps:
1. **Train Information Retrieval**: It fetches the maximum allowed length and path ID for the specified train.
2. **Freight Existence Validation**: It checks if the specified freight exists in the database.
3. **Optimistic Insert**: It performs a tentative insert of the freight into the `TrainFreight` table.
4. **Peak Length Calculation**: It calls the `get_train_max_length` function to determine the new peak length of the train after the insertion.
5. **Length Validation**: If the new peak length exceeds the maximum allowed length, it rolls back the insert and raises a custom exception.
6. **Commit on Success**: If all validations pass, it commits the transaction.

### 2.3. Helper Procedure: `p_print_train_data`

A simple utility procedure used in tests to display the train's current status (length, associated locos, and freights) for verification and better user experience:

```sql
CREATE OR REPLACE PROCEDURE p_print_train_data(
    p_train_id IN Train.train_id%TYPE
)
IS
    v_max_allowed    Train.max_length%TYPE;
    v_path_id        Train.path_id%TYPE;
    v_real_length    NUMBER;
    v_count_items    NUMBER := 0;
BEGIN
    SELECT max_length, path_id
    INTO v_max_allowed, v_path_id
    FROM Train
    WHERE train_id = p_train_id;

    v_real_length := get_train_max_length(p_train_id, v_path_id);

    DBMS_OUTPUT.PUT_LINE('== TRAIN DATA ==');
    DBMS_OUTPUT.PUT_LINE('Train ID: ' || p_train_id);
    DBMS_OUTPUT.PUT_LINE('Max Allowed Length : ' || v_max_allowed);
    DBMS_OUTPUT.PUT_LINE('Real Peak Length   : ' || v_real_length);


    DBMS_OUTPUT.PUT_LINE('Associated Locomotives:');
    v_count_items := 0;

    FOR r IN (
        SELECT locomotive_id
        FROM TrainLocomotive
        WHERE train_id = p_train_id
    ) LOOP
        DBMS_OUTPUT.PUT_LINE('  . Loco ID: ' || r.locomotive_id);
        v_count_items := v_count_items + 1;
    END LOOP;

    IF v_count_items = 0 THEN
        DBMS_OUTPUT.PUT_LINE('  . (None)');
    END IF;

    DBMS_OUTPUT.PUT_LINE('Associated Freights:');
    v_count_items := 0;

    FOR r IN (
        SELECT freight_id
        FROM TrainFreight
        WHERE train_id = p_train_id
    ) LOOP
        DBMS_OUTPUT.PUT_LINE('  . Freight ID: ' || r.freight_id);
        v_count_items := v_count_items + 1;
    END LOOP;

    IF v_count_items = 0 THEN
        DBMS_OUTPUT.PUT_LINE('  . (None)');
    END IF;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Error: Train ID ' || p_train_id || ' does not exist.');
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Unexpected Error: ' || SQLERRM);
END p_print_train_data;
/
```

## 3. Testing and Validation Strategy

A specific data setup was created to test edge cases, including overlapping routes and capacity limits. In this case, we didn't feel the need to test the locomotive addition procedure again, as it is required for USBD39 (that was done as well).

### 3.1. Test Data Setup

*   **Train 9900:** A standard train with a 200m limit.
*   **Train 9901:** A tiny train with a 10m limit (to force errors).
*   **Wagons:** 100m length each.
*   **Freight 8801:** 100m length (Leixões -> Darque).
*   **Freight 8802:** 100m length (Darque -> Caminha). *Note: Does not overlap with 8801.*
*   **Freight 8803:** 100m length (Leixões -> Caminha). *Note: Overlaps with everything.*

### 3.2. Test Cases

#### Test 1: Valid Insertion (Increases Peak)
*   **Action:** Add Freight 8801 (100m) to empty train.
*   **Result:** Peak length goes from 0m to 100m. Success.

#### Test 2: Valid Insertion (No Peak Increase)
*   **Action:** Add Freight 8802 (100m) to train that already has 8801.
*   **Logic:** Since 8801 drops off at Darque and 8802 picks up at Darque, they don't overlap.
*   **Result:** Peak length stays at 100m. Success.

#### Test 3: Invalid Train
*   **Action:** Try to add freight to Train ID 0.
*   **Result:** Error -20021 (Train not found).

#### Test 4: Invalid Freight
*   **Action:** Try to add Freight ID 0.
*   **Result:** Error -20022 (Freight not found).

#### Test 5: Invalid Association (Max Length Exceeded)
*   **Action:** Add Freight 8803 (100m) to Train 9901 (Max 10m).
*   **Result:** Calculation shows 100m required vs 10m allowed. Error -20020 raised. Rollback executed.

## 4. Conclusion

The solution successfully enforces the maximum train length constraint dynamically. By correctly calculating the cumulative length at each station stop, the system handles complex logistics (loading/unloading along the route) rather than just summing all cargo. 