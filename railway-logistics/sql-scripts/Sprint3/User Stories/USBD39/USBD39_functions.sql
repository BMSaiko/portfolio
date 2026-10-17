

CREATE OR REPLACE PROCEDURE p_add_locomotive_to_train(
    p_train_id          IN Train.train_id%TYPE,
    p_locomotive_id     IN Locomotive.locomotive_number%TYPE
)
IS
    v_max_allowed               Train.max_length%TYPE;
    v_path_id                   Train.path_id%TYPE;
    v_validation                NUMBER;
    v_current_length            NUMBER;
    v_new_locomotive_length     NUMBER;
    

    ex_length_limit EXCEPTION;
    ex_invalid_locomotive EXCEPTION;
BEGIN

    BEGIN
        SELECT 1 INTO v_validation FROM Locomotive WHERE locomotive_number = p_locomotive_id;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN RAISE ex_invalid_locomotive;
    END;


    SELECT max_length, path_id
    INTO v_max_allowed, v_path_id
    FROM Train
    WHERE train_id = p_train_id;

    v_current_length := get_train_max_length(p_train_id, v_path_id);

    SELECT lm.locomotive_model_length
    INTO v_new_locomotive_length
    FROM Locomotive l
    JOIN LocomotiveModel lm ON l.model_id = lm.locomotive_model_id
    WHERE l.locomotive_number = p_locomotive_id;


    IF (v_current_length + v_new_locomotive_length) > v_max_allowed THEN
        RAISE ex_length_limit;
    END IF;

    INSERT INTO TrainLocomotive (train_id, locomotive_id)
    VALUES (p_train_id, p_locomotive_id);


    DBMS_OUTPUT.PUT_LINE('Locomotiva adicionada com sucesso.');

EXCEPTION
    WHEN ex_length_limit THEN
        RAISE_APPLICATION_ERROR(-20001, 'Erro: O comboio excederia o limite máximo de comprimento.');
    WHEN ex_invalid_locomotive THEN
        RAISE_APPLICATION_ERROR(-20003, 'Erro: Locomotiva não encontrada.');
    WHEN OTHERS THEN
        RAISE;
END p_add_locomotive_to_train;
/



CREATE OR REPLACE FUNCTION fn_show_train_locomotives(
    p_train_id      IN Train.train_id%TYPE
) RETURN SYS_REFCURSOR
IS
    v_locomotive_cursor     SYS_REFCURSOR;
    v_validation            NUMBER;

    ex_invalid_train  EXCEPTION;
BEGIN
    BEGIN
        SELECT 1 INTO v_validation FROM Train WHERE train_id = p_train_id;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN RAISE ex_invalid_train;
    END;

    OPEN v_locomotive_cursor FOR
        SELECT tl.locomotive_id, tl.train_id
        FROM TrainLocomotive tl
        WHERE tl.train_id = p_train_id;

    RETURN v_locomotive_cursor;

EXCEPTION
    WHEN ex_invalid_train THEN
        RAISE_APPLICATION_ERROR(-20002, 'Erro: Comboio não encontrado.');
    WHEN OTHERS THEN
        RAISE;
    

END fn_show_train_locomotives;
/

COMMIT;