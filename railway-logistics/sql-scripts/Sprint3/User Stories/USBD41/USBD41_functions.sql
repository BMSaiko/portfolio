-- USBD41 - As a Freight Manager, I want to remove a freight from a planned train.

CREATE OR REPLACE PROCEDURE p_remove_freight_from_train(
    p_train_id   IN Train.train_id%TYPE,
    p_freight_id IN Freight.freight_id%TYPE
)
IS
    v_check_assign NUMBER;
    v_validation   NUMBER;

    ex_invalid_train         EXCEPTION;
    ex_invalid_freight       EXCEPTION;
    ex_association_not_found EXCEPTION;
BEGIN
      BEGIN
        SELECT 1 INTO v_validation FROM Freight WHERE freight_id = p_freight_id;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN RAISE ex_invalid_freight;
    END;
    SELECT COUNT(*)
    INTO v_check_assign
    FROM TrainFreight
    WHERE train_id = p_train_id AND freight_id = p_freight_id;

    IF v_check_assign = 0 THEN
        RAISE ex_association_not_found;
    END IF;

    DELETE FROM TrainFreight
    WHERE train_id = p_train_id AND freight_id = p_freight_id;


EXCEPTION
    WHEN ex_association_not_found THEN
        RAISE_APPLICATION_ERROR(-20010, 'Error: Freight ' || p_freight_id || ' is not associated with Train ' || p_train_id || '.');
    WHEN ex_invalid_train THEN
        RAISE_APPLICATION_ERROR(-20011, 'Error: Train ' || p_train_id || ' does not exist.');
    WHEN ex_invalid_freight THEN
        RAISE_APPLICATION_ERROR(-20012, 'Error: Freight ' || p_freight_id || ' does not exist.');
    WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20013, 'Error removing freight: ' || SQLERRM);
END p_remove_freight_from_train;
/


CREATE OR REPLACE FUNCTION fn_show_train_freights(
    p_train_id      IN Train.train_id%TYPE
) RETURN SYS_REFCURSOR
IS
    v_freight_cursor     SYS_REFCURSOR;
    v_validation         NUMBER;

    ex_invalid_train     EXCEPTION;
BEGIN
    BEGIN
        SELECT 1 INTO v_validation FROM Train WHERE train_id = p_train_id;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN RAISE ex_invalid_train;
    END;

    OPEN v_freight_cursor FOR
        SELECT tf.freight_id, tf.train_id
        FROM TrainFreight tf
        JOIN Freight f ON tf.freight_id = f.freight_id
        WHERE tf.train_id = p_train_id;

    RETURN v_freight_cursor;

EXCEPTION
    WHEN ex_invalid_train THEN
        RAISE_APPLICATION_ERROR(-20011, 'Error: Train not found.');
    WHEN OTHERS THEN
        RAISE;
END fn_show_train_freights;
/

COMMIT;