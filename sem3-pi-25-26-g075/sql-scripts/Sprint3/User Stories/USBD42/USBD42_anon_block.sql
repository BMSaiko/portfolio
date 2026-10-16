
-- TEST 1 - Valid insert (train does not stop at the facility)

SET SERVEROUTPUT ON;
DECLARE
v_train_id     NUMBER := 5437;
    v_facility_id  NUMBER := 16;
    v_arrival      DATE := SYSDATE;
    v_departure    DATE := NULL;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST 1: Valid insert (train does not stop at the facility) ---');

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

-- TEST 2 - Valid insert (train stops at the facility)

SET SERVEROUTPUT ON;
DECLARE
v_train_id     NUMBER := 5421;
    v_facility_id  NUMBER := 20;
    v_arrival      DATE := SYSDATE;
    v_departure    DATE := SYSDATE + (1/24);
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST 2: Valid insert (train stops at the facility) ---');

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

-- TEST 3 - Invalid Train

SET SERVEROUTPUT ON;
DECLARE
v_train_id     NUMBER := 5555;
    v_facility_id  NUMBER := 50;
    v_arrival      DATE := SYSDATE;
    v_departure    DATE := NULL;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST 3: Invalid train ---');

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

-- TEST 4 - Invalid Facility

SET SERVEROUTPUT ON;
DECLARE
v_train_id     NUMBER := 5437;
    v_facility_id  NUMBER := 100;
    v_arrival      DATE := SYSDATE;
    v_departure    DATE := NULL;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST 4: Invalid Facility ---');

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

-- TEST 5 - Invalid Departure Date (before the Arrival Date)

SET SERVEROUTPUT ON;
DECLARE
v_train_id     NUMBER := 5437;
    v_facility_id  NUMBER := 50;
    v_arrival      DATE := SYSDATE;
    v_departure    DATE := SYSDATE - (1/24);
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST 5: Invalid Departure Date (before the Arrival Date) ---');

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

-- TEST 6 - No Departure Date when the Train stops

SET SERVEROUTPUT ON;
DECLARE
v_train_id     NUMBER := 5437;
    v_facility_id  NUMBER := 50;
    v_arrival      DATE := SYSDATE;
    v_departure    DATE := NULL;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST 6: No Departure Date when the Train stops ---');

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

-- TEST 7 - Departure Date exists when the Train does not stop

SET SERVEROUTPUT ON;
DECLARE
v_train_id     NUMBER := 5437;
    v_facility_id  NUMBER := 16;
    v_arrival      DATE := SYSDATE;
    v_departure    DATE := SYSDATE + (1/24);
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST 7: Departure Date exists when the Train does not stop ---');

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