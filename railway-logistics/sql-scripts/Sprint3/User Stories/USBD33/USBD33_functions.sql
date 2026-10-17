CREATE OR REPLACE FUNCTION get_train_max_length (
    p_train_id IN NUMBER,
    p_path_id  IN NUMBER
) RETURN NUMBER
IS
    v_locomotive_length NUMBER := 0;
    v_maximum_length    NUMBER := 0;
    current_length      NUMBER := 0;
BEGIN

    SELECT NVL(SUM(lm.locomotive_model_length), 0)
    INTO v_locomotive_length
    FROM TrainLocomotive tl
    JOIN Locomotive l ON l.locomotive_number = tl.locomotive_id
    JOIN LocomotiveModel lm ON lm.locomotive_model_id = l.model_id
    WHERE tl.train_id = p_train_id;


    v_maximum_length := v_locomotive_length;
    current_length := 0;

    FOR r IN (
        SELECT
            path_facility_order,
            NVL(SUM(wagon_lenght), 0) AS length_change_per_station
        FROM (

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

        IF r.length_change_per_station IS NOT NULL THEN
            current_length := current_length + r.length_change_per_station;
        END IF;


        v_maximum_length := GREATEST(v_maximum_length, v_locomotive_length + current_length);
    END LOOP;

    RETURN v_maximum_length;
END;
/

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
    BEGIN
        SELECT max_length, path_id
        INTO v_max_allowed, v_path_id
        FROM Train
        WHERE train_id = p_train_id;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN RAISE ex_train_not_found;
    END;

    DECLARE v_verication NUMBER;
    BEGIN
        SELECT 1 INTO v_verication FROM Freight WHERE freight_id = p_freight_id;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN RAISE ex_freight_not_found;
    END;

    INSERT INTO TrainFreight (train_id, freight_id)
    VALUES (p_train_id, p_freight_id);

    v_real_maximum_length := get_train_max_length(p_train_id, v_path_id);

    IF v_real_maximum_length > v_max_allowed THEN
        ROLLBACK;
        RAISE ex_length_limit;
    END IF;

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

COMMIT;