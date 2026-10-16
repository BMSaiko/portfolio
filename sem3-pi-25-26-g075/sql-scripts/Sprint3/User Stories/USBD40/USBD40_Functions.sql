-- USBD40 - As a Freight Manager, I want to register a freight in the database.
-- This includes the wagons involved.
-- This is only possible if there are enough free wagons of the given type at the freight’s departure facility.

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

COMMIT;