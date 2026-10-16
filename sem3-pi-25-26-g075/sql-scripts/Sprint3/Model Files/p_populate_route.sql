
CREATE OR REPLACE PROCEDURE p_populate_routes (
    p_train_id IN Train.train_id%TYPE
)
IS
    v_path_id           TrainPath.path_id%TYPE;

    v_route_id          Route.route_id%TYPE;
    v_start_facility_id Route.start_facility_id%TYPE;
    v_end_facility_id   Route.end_facility_id%TYPE;

    v_freights_in       NUMBER;
    v_freights_out      NUMBER;
    v_freight_change    NUMBER;
    v_number_of_freights NUMBER := 0;
    
    v_current_type      NUMBER;
    v_previous_type     NUMBER := -1;

    v_beginning_facility_id NUMBER;
    v_last_facility_id      NUMBER;
BEGIN
SELECT t.path_id
INTO v_path_id
FROM Train t
WHERE t.train_id = p_train_id;

FOR r IN (
        SELECT pf.facility_id
        FROM PathFacility pf
        WHERE pf.path_id = v_path_id
        ORDER BY pf.path_facility_order
    ) LOOP
        v_last_facility_id := r.facility_id;

SELECT COUNT(*)
INTO v_freights_in
FROM TrainFreight tf
         JOIN Freight f ON f.freight_id = tf.freight_id
WHERE train_id = p_train_id
  AND f.origin_facility_id = r.facility_id;

SELECT COUNT(*)
INTO v_freights_out
FROM TrainFreight tf
         JOIN Freight f ON f.freight_id = tf.freight_id
WHERE train_id = p_train_id
  AND f.destination_facility_id = r.facility_id;

v_freight_change := v_freights_in - v_freights_out;
        v_number_of_freights := v_number_of_freights + v_freight_change;

        IF (v_number_of_freights > 1) THEN
            v_current_type := 2; -- Complex
ELSE
            v_current_type := 1; -- Simple
END IF;

        IF v_previous_type = -1 THEN

            v_previous_type := v_current_type;
            v_beginning_facility_id := r.facility_id;

        ELSIF v_previous_type <> v_current_type THEN

SELECT NVL(MAX(route_id), 0) + 1 INTO v_route_id FROM Route;

INSERT INTO Route (
    route_id,
    train_id,
    route_type_id,
    start_facility_id,
    end_facility_id
) VALUES (
             v_route_id,
             p_train_id,
             v_previous_type,
             v_beginning_facility_id,
             r.facility_id
         );


v_beginning_facility_id := r.facility_id;
            v_previous_type := v_current_type;
END IF;
END LOOP;

    IF v_beginning_facility_id IS NOT NULL AND v_beginning_facility_id <> v_last_facility_id THEN

SELECT NVL(MAX(route_id), 0) + 1 INTO v_route_id FROM Route;

INSERT INTO Route (
    route_id,
    train_id,
    route_type_id,
    start_facility_id,
    end_facility_id
) VALUES (
             v_route_id,
             p_train_id,
             v_previous_type,
             v_beginning_facility_id,
             v_last_facility_id
         );
END IF;

COMMIT;
END;
/



SET SERVEROUTPUT ON;
BEGIN
FOR r IN (
        SELECT train_id
        FROM Train
    ) LOOP
        p_populate_routes(r.train_id);
END LOOP;
END;
/

SELECT * FROM Route;

DELETE FROM Route;