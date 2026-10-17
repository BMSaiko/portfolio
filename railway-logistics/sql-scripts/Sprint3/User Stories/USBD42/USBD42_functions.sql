--USBD42 - As a Freight Manager, I want to register a train movement in the database. If the train stopped at the facility, the departure time shall also be registered.

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
BEGIN
SELECT 1 INTO v_validation FROM Train WHERE train_id = p_train_id;
EXCEPTION
        WHEN NO_DATA_FOUND THEN RAISE ex_train_not_found;
END;

BEGIN
SELECT 1 INTO v_validation FROM Facility WHERE facility_id = p_facility_id;
EXCEPTION
        WHEN NO_DATA_FOUND THEN RAISE ex_facility_not_found;
END;


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
    IF p_departure_time IS NOT NULL AND p_departure_time < p_arrival_time THEN
        RAISE ex_invalid_dates;
END IF;

    v_train_stops := fn_check_stop(p_train_id, p_facility_id);

    IF (v_train_stops AND p_departure_time IS NULL) THEN
        RAISE ex_train_stops;
    ELSIF (NOT v_train_stops AND p_departure_time IS NOT NULL) THEN
        RAISE ex_train_does_not_stop;
END IF;

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

CREATE OR REPLACE PROCEDURE p_print_train_record(
    p_train_id IN Train.train_id%TYPE,
    p_facility_id IN Facility.facility_id%TYPE
)
IS
    v_arrival_date      TrainRecord.arrival_date_time%TYPE;
    v_departure_date    TrainRecord.departure_date_time%TYPE;
    v_facility_name         Facility.facility_name%TYPE;
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

COMMIT;