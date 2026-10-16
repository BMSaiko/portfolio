# USBD42: Register Train Movement

## 1. Summary

The primary objective of this PL/SQL development is to fulfill the requirement of User Story USBD42: *"As a Freight Manager, I want to register a train movement in the database. If the train stopped at the facility, the departure time shall also be registered."*

To achieve this, two stored PL/SQL components were developed:
1.  **Function `fn_check_stop`:** A boolean function to determine if a specific train has freight operations (loading or unloading) at a given facility, classifying it as a "stop".
2.  **Procedure `pr_register_train_movement`:** A procedure to register the movement event in the `TrainRecord` table. It enforces business rules regarding arrival/departure times based on whether the train stops or just passes through.

Additionally, a helper procedure `p_print_train_record` was created to display the registered information for verification.

## 2. Implementation

The solution implements strict validation logic to ensure data consistency regarding train stops and timing.

### 2.1. Function: `fn_check_stop`

This function verifies if a train is scheduled to stop at a facility. A stop is defined as a location where freight is either picked up (origin) or dropped off (destination).

```sql
CREATE OR REPLACE FUNCTION fn_check_stop(
    p_train_id      IN TrainRecord.train_id%TYPE,
    p_facility_id   IN TrainRecord.facility_id%TYPE
) RETURN BOOLEAN
IS
    v_stops_bool          BOOLEAN := FALSE;
    v_validation          NUMBER;
    v_verification_count  NUMBER;
    
    ex_train_not_found    EXCEPTION;
    ex_facility_not_found EXCEPTION;
BEGIN
    -- Validate Train Existence
    BEGIN
        SELECT 1 INTO v_validation FROM Train WHERE train_id = p_train_id;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN RAISE ex_train_not_found;
    END;

    -- Validate Facility Existence
    BEGIN
        SELECT 1 INTO v_validation FROM Facility WHERE facility_id = p_facility_id;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN RAISE ex_facility_not_found;
    END;

    -- Check for Freight Operations (Origin or Destination)
    SELECT COUNT(*)
    INTO v_verification_count
    FROM TrainFreight tf
    JOIN Freight fr ON tf.freight_id = fr.freight_id
    WHERE tf.train_id = p_train_id
        AND (fr.origin_facility_id = p_facility_id
        OR fr.destination_facility_id = p_facility_id);

    IF v_verification_count > 0 THEN
        v_stops_bool := TRUE;
    END IF;

    RETURN v_stops_bool;

EXCEPTION
    WHEN ex_train_not_found THEN
        RAISE_APPLICATION_ERROR(-20014, 'Error: Train ID ' || p_train_id || ' not found.');
    WHEN ex_facility_not_found THEN
        RAISE_APPLICATION_ERROR(-20015, 'Error: Facility ID ' || p_facility_id || ' not found.');
    WHEN OTHERS THEN
        RAISE;
END fn_check_stop;
/
```

### 2.2. Procedure: `pr_register_train_movement`

This procedure handles the registration logic, enforcing the rule that stops must have departure times, while non-stops (passing through) should not.

```sql
CREATE OR REPLACE PROCEDURE pr_register_train_movement(
    p_train_id      IN TrainRecord.train_id%TYPE,
    p_facility_id   IN TrainRecord.facility_id%TYPE,
    p_arrival_time  IN DATE,
    p_departure_time   IN DATE DEFAULT NULL
)
IS
    v_new_record_id TrainRecord.record_id%TYPE;
    v_train         NUMBER;
    v_train_stops   BOOLEAN;
    
    ex_invalid_dates      EXCEPTION;
    ex_train_does_not_stop EXCEPTION;
    ex_train_stops EXCEPTION;
BEGIN
    -- Time Consistency Check
    IF p_departure_time IS NOT NULL AND p_departure_time < p_arrival_time THEN
        RAISE ex_invalid_dates;
    END IF;

    -- Determine if it is a Stop
    v_train_stops := fn_check_stop(p_train_id, p_facility_id);

    -- Enforce Stop/Non-Stop Rules
    IF (v_train_stops AND p_departure_time IS NULL) THEN
        RAISE ex_train_stops; -- Error: Stops MUST have departure time
    ELSIF (NOT v_train_stops AND p_departure_time IS NOT NULL) THEN
        RAISE ex_train_does_not_stop; -- Error: Non-stops CANNOT have departure time
    END IF;

    -- Generate ID and Insert
    SELECT NVL(MAX(record_id), 0) + 1
    INTO v_new_record_id
    FROM TrainRecord;

    INSERT INTO TrainRecord (
        record_id,
        train_id,
        facility_id,
        arrival_date_time,
        departure_date_time
    ) VALUES (
        v_new_record_id,
        p_train_id,
        p_facility_id,
        p_arrival_time,
        p_departure_time
    );

EXCEPTION
    WHEN ex_invalid_dates THEN
        RAISE_APPLICATION_ERROR(-20016, 'Error: Departure time cannot be before Arrival time.');
    WHEN ex_train_does_not_stop THEN
        RAISE_APPLICATION_ERROR(-20017, 'Error: The train does not stop; can not have departure time.');
    WHEN ex_train_stops THEN
        RAISE_APPLICATION_ERROR(-20019, 'Error: The train stops at this facility; should have departure time.');
    WHEN OTHERS THEN
        RAISE;
END pr_register_train_movement;
/
```

### 2.3. Procedure: `p_print_train_record`

A utility procedure to output the recorded data for verification and better user experience.

```sql
CREATE OR REPLACE PROCEDURE p_print_train_record(
    p_train_id IN Train.train_id%TYPE,
    p_facility_id IN Facility.facility_id%TYPE
)
IS
    v_arrival_date      TrainRecord.arrival_date_time%TYPE;
    v_departure_date    TrainRecord.departure_date_time%TYPE;
    v_facility_name     Facility.facility_name%TYPE;
BEGIN
    SELECT 
        f.facility_name,
        tr.arrival_date_time,
        tr.departure_date_time
    INTO 
        v_facility_name,
        v_arrival_date,
        v_departure_date
    FROM TrainRecord tr
    JOIN Facility f ON tr.facility_id = f.facility_id
    WHERE tr.train_id = p_train_id AND tr.facility_id = p_facility_id;

    DBMS_OUTPUT.PUT_LINE('New Train Record Information');
    DBMS_OUTPUT.PUT_LINE('Train ID: ' || p_train_id);
    DBMS_OUTPUT.PUT_LINE('Facility Name: ' || v_facility_name); 
    DBMS_OUTPUT.PUT_LINE('Arrival: ' || TO_CHAR(v_arrival_date, 'YYYY-MM-DD HH24:MI:SS'));
    DBMS_OUTPUT.PUT_LINE('Departure: ' || TO_CHAR(v_departure_date, 'YYYY-MM-DD HH24:MI:SS'));

EXCEPTION
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Unexpected Error: ' || SQLERRM);
END p_print_train_record;
/
```

## 3. Testing and Validation Strategy

A complete set of 7 anonymous blocks was created to test all logical branches of the procedure. All the anonymous blocks follow the same structure:

```sql
SET SERVEROUTPUT ON;
DECLARE
    v_train_id     NUMBER := *train id*;
    v_facility_id  NUMBER := *facility id*;
    v_arrival      DATE := *arrival date and time*;
    v_departure    DATE := *departure date and time*;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST X: Description ---');

    pr_register_train_movement(v_train_id, v_facility_id, v_arrival, v_departure);

    DBMS_OUTPUT.PUT_LINE('Train Movement registered successfully.');

    p_print_train_record(v_train_id, v_facility_id);

ROLLBACK;
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE IN (-20014, -20015, -20016, -20017, -20019) THEN
            DBMS_OUTPUT.PUT_LINE(SQLERRM);
ELSE
            DBMS_OUTPUT.PUT_LINE('Unexpected Error: ' || SQLERRM);
END IF;
ROLLBACK;
END;
/
```

### 3.1. Test Cases

#### Test 1: Valid Insert (No Stop)
*   **Scenario:** Registering movement at a facility where the train simply passes through (no freight ops). Departure time is NULL.
*   **Result:** Success.

#### Test 2: Valid Insert (Stop)
*   **Scenario:** Registering movement at a facility where the train has freight operations. Departure time is provided.
*   **Result:** Success.

#### Test 3: Invalid Train
*   **Scenario:** Using a non-existent train ID.
*   **Result:** Error -20014 (Train not found).

#### Test 4: Invalid Facility
*   **Scenario:** Using a non-existent facility ID.
*   **Result:** Error -20015 (Facility not found).

#### Test 5: Invalid Departure Date
*   **Scenario:** Providing a departure time earlier than the arrival time.
*   **Result:** Error -20016 (Time consistency violation).

#### Test 6: Missing Departure Date for Stop
*   **Scenario:** Train stops (has freight ops) but departure time is NULL.
*   **Result:** Error -20019 (Stop requires departure time).

#### Test 7: Unexpected Departure Date for Non-Stop
*   **Scenario:** Train does not stop (no freight ops) but departure time is provided.
*   **Result:** Error -20017 (Non-stop cannot have departure time).

## 4. Conclusion

The implemented solution for USBD42 robustly handles train movement registration. By integrating a helper function to determine operational stops dynamically based on freight data, the system ensures that the `TrainRecord` table accurately reflects the logistical reality, distinguishing between simple transits and operational stops with required departure timestamps. The extensive test suite confirms the system behaves correctly under valid and invalid conditions.
