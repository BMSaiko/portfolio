
-- Test 1: Can retrieve the list of wagons --

SET SERVEROUTPUT ON;
DECLARE
    l_cursor    SYS_REFCURSOR;
    l_wagon_num Wagon.wagon_number%TYPE;
    l_found     BOOLEAN := FALSE;

    v_facility_name Facility.facility_name%TYPE;
    v_facility_id NUMBER := 50;
    v_wagon_type_id NUMBER := 3;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST 1: Valid Available Wagons Retrieval ---');

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

-- Test 2: The facility provided doesn't exist --

SET SERVEROUTPUT ON;
DECLARE
    l_cursor    SYS_REFCURSOR;
    l_wagon_num Wagon.wagon_number%TYPE;
    l_found     BOOLEAN := FALSE;

    v_facility_name Facility.facility_name%TYPE;
    v_facility_id NUMBER := 999;
    v_wagon_type_id NUMBER := 3;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST 2: Invlaid Origin Facility ---');

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

-- Test 3: The wagon type provided doesn't exist --

SET SERVEROUTPUT ON;
DECLARE
    l_cursor    SYS_REFCURSOR;
    l_wagon_num Wagon.wagon_number%TYPE;
    l_found     BOOLEAN := FALSE;

    v_facility_name Facility.facility_name%TYPE;
    v_facility_id NUMBER := 50;
    v_wagon_type_id NUMBER := 999;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST 3: Invalid Wagon Type ---');

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

-- Test 4: Valid association (1 wagon)

SET SERVEROUTPUT ON;
DECLARE
    l_freight_id NUMBER := 8001;
    l_freight_origin NUMBER := 50;
    l_freight_destination NUMBER := 12;

    l_facility_name VARCHAR(255);

    l_wagon_type_id NUMBER := 3;
    l_wagon_list VARCHAR2(200) := '123 1 000';

    c_get_wagons_cursor SYS_REFCURSOR;
    l_wagon_num CHAR(11);

    c_wagons_associated_to_freight SYS_REFCURSOR;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST 4: Valid Freight Insertion (1 Wagons) ---');

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

-- Test 5: Valid association (multiple wagons)

SET SERVEROUTPUT ON;
DECLARE
    l_freight_id NUMBER := 8001;
    l_freight_origin NUMBER := 50;
    l_freight_destination NUMBER := 12;

    l_facility_name VARCHAR(255);

    l_wagon_type_id NUMBER := 3;
    l_wagon_list VARCHAR2(200) := '123 1 000, 123 4 000, 123 5 000';

    c_get_wagons_cursor SYS_REFCURSOR;
    l_wagon_num CHAR(11);

    c_wagons_associated_to_freight SYS_REFCURSOR;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST 5: Valid Freight Insertion (Multiple Wagons) ---');

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

-- Test 6: Wagon doesn't exist --

SET SERVEROUTPUT ON;
DECLARE
    l_freight_id NUMBER := 8001;
    l_freight_origin NUMBER := 50;
    l_freight_destination NUMBER := 12;

    l_facility_name VARCHAR(255);

    l_wagon_type_id NUMBER := 3;
    l_wagon_list VARCHAR2(200) := '000 0 000';

    c_get_wagons_cursor SYS_REFCURSOR;
    l_wagon_num CHAR(11);

    c_wagons_associated_to_freight SYS_REFCURSOR;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST 6: Wagon provided does not exist ---');

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

-- Test 7: Wagon is at the wrong facility --

SET SERVEROUTPUT ON;
DECLARE
    l_freight_id NUMBER := 8001;
    l_freight_origin NUMBER := 50;
    l_freight_destination NUMBER := 12;

    l_facility_name VARCHAR(255);

    l_wagon_type_id NUMBER := 3;
    l_wagon_list VARCHAR2(200) := '123 2 000';

    c_get_wagons_cursor SYS_REFCURSOR;
    l_wagon_num CHAR(11);

    c_wagons_associated_to_freight SYS_REFCURSOR;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST 7: Wagon provided is at the wrong facility ---');

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

-- Test 8: Wagon isn't free --

SET SERVEROUTPUT ON;
DECLARE
    l_freight_id NUMBER := 8001;
    l_freight_origin NUMBER := 50;
    l_freight_destination NUMBER := 12;

    l_facility_name VARCHAR(255);

    l_wagon_type_id NUMBER := 3;
    l_wagon_list VARCHAR2(200) := '123 3 000';

    c_get_wagons_cursor SYS_REFCURSOR;
    l_wagon_num CHAR(11);

    c_wagons_associated_to_freight SYS_REFCURSOR;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST 8: The wagon provided is not free ---');

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

-- Test 9: Empty list of wagons

SET SERVEROUTPUT ON;
DECLARE
    l_freight_id NUMBER := 8001;
    l_freight_origin NUMBER := 50;
    l_freight_destination NUMBER := 12;

    l_facility_name VARCHAR(255);

    l_wagon_type_id NUMBER := 3;
    l_wagon_list VARCHAR2(200) := '  ';

    c_get_wagons_cursor SYS_REFCURSOR;
    l_wagon_num CHAR(11);

    c_wagons_associated_to_freight SYS_REFCURSOR;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST 9: Empty List of Wagons ---');

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

-- Test 10: Freight already exists

SET SERVEROUTPUT ON;
DECLARE
    l_freight_id NUMBER := 2006;
    l_freight_origin NUMBER := 50;
    l_freight_destination NUMBER := 12;

    l_facility_name VARCHAR(255);

    l_wagon_type_id NUMBER := 3;
    l_wagon_list VARCHAR2(200) := '  ';

    c_get_wagons_cursor SYS_REFCURSOR;
    l_wagon_num CHAR(11);

    c_wagons_associated_to_freight SYS_REFCURSOR;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST 10: Provided freight already exists ---');

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
        IF SQLCODE IN (-20006, -20007, -20008, -20009, -20018) THEN
            DBMS_OUTPUT.PUT_LINE(SQLERRM);
        ELSE
            DBMS_OUTPUT.PUT_LINE('Unexpected error: ' || SQLERRM);
        END IF;
        ROLLBACK;
END;
/

COMMIT;