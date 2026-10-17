

-- ---------------------------------------------------------------------------------------------
-- Valid Test: Locomotive fits within Train max length after addition, loc n 8801 to train n 9001
-- ---------------------------------------------------------------------------------------------
SET SERVEROUTPUT ON;
DECLARE
v_train_id      Train.train_id%TYPE := 9001; -- Comboio com 500m max
    v_locomotive_id       Locomotive.locomotive_number%TYPE := 8802; -- Locomotiva com 20m

    v_temp_locomotive_id Locomotive.locomotive_number%TYPE;
    v_temp_train_id Train.train_id%TYPE;
    v_locomotive_cursor SYS_REFCURSOR;

BEGIN

    DBMS_OUTPUT.PUT_LINE('--- Teste 1: Locomotive fits within Train max length after addition, loc n 8801 to train n 9001---');
    v_locomotive_cursor := fn_show_train_locomotives(v_train_id);


    DBMS_OUTPUT.PUT_LINE('Locomotive ID before addition: ');
    LOOP
FETCH v_locomotive_cursor INTO v_temp_locomotive_id, v_temp_train_id;
        EXIT WHEN v_locomotive_cursor%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Train ID: ' || v_temp_train_id || ', Locomotive ID: ' || v_temp_locomotive_id);
END LOOP;
CLOSE v_locomotive_cursor;
p_add_locomotive_to_train(v_train_id, v_locomotive_id);
    v_locomotive_cursor := fn_show_train_locomotives(v_train_id);

    DBMS_OUTPUT.PUT_LINE('Locomotive ID after addition: ');
    LOOP
FETCH v_locomotive_cursor INTO v_temp_locomotive_id, v_temp_train_id;
        EXIT WHEN v_locomotive_cursor%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Train ID: ' || v_temp_train_id || ', Locomotive ID: ' || v_temp_locomotive_id);
END LOOP;
CLOSE v_locomotive_cursor;
ROLLBACK;
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE IN (-20001, -20002, -20003) THEN
            DBMS_OUTPUT.PUT_LINE(SQLERRM);
ELSE
            DBMS_OUTPUT.PUT_LINE('ERRO INESPERADO: ' || SQLERRM);
END IF;
ROLLBACK;
END;
/

-- ---------------------------------------------------------------------------------------------
-- Invalid Test: Locomotive does not exist
-- ---------------------------------------------------------------------------------------------

SET SERVEROUTPUT ON;
DECLARE
v_train_id      Train.train_id%TYPE := 9001;
    v_locomotive_id       Locomotive.locomotive_number%TYPE := 9999;

    v_temp_locomotive_id Locomotive.locomotive_number%TYPE;
    v_temp_train_id Train.train_id%TYPE;
    v_locomotive_cursor SYS_REFCURSOR;

BEGIN

    DBMS_OUTPUT.PUT_LINE('--- Test 2: Locomotive does not exist, trying to add locomotive n 9999 to train n 9001---');
    v_locomotive_cursor := fn_show_train_locomotives(v_train_id);


    DBMS_OUTPUT.PUT_LINE('Locomotive ID before addition: ');
    LOOP
FETCH v_locomotive_cursor INTO v_temp_locomotive_id, v_temp_train_id;
        EXIT WHEN v_locomotive_cursor%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Train ID: ' || v_temp_train_id || ', Locomotive ID: ' || v_temp_locomotive_id);
END LOOP;
CLOSE v_locomotive_cursor;
p_add_locomotive_to_train(v_train_id, v_locomotive_id);
    v_locomotive_cursor := fn_show_train_locomotives(v_train_id);

    DBMS_OUTPUT.PUT_LINE('Locomotive ID after addition: ');
    LOOP
FETCH v_locomotive_cursor INTO v_temp_locomotive_id, v_temp_train_id;
        EXIT WHEN v_locomotive_cursor%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Train ID: ' || v_temp_train_id || ', Locomotive ID: ' || v_temp_locomotive_id);
END LOOP;
CLOSE v_locomotive_cursor;
ROLLBACK;
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE IN (-20001, -20002, -20003) THEN
            DBMS_OUTPUT.PUT_LINE(SQLERRM);
ELSE
            DBMS_OUTPUT.PUT_LINE('ERRO INESPERADO: ' || SQLERRM);
END IF;
ROLLBACK;
END;
/


-- ---------------------------------------------------------------------------------------------
-- Invalid Test: Train does not exist
-- ---------------------------------------------------------------------------------------------

SET SERVEROUTPUT ON;
DECLARE
v_train_id      Train.train_id%TYPE := 9999;
    v_locomotive_id       Locomotive.locomotive_number%TYPE := 8802;

    v_temp_locomotive_id Locomotive.locomotive_number%TYPE;
    v_temp_train_id Train.train_id%TYPE;
    v_locomotive_cursor SYS_REFCURSOR;

BEGIN

    DBMS_OUTPUT.PUT_LINE('--- Test 3: Train does not exist, trying to add locomotive n 8802 to train n 9999---');
    v_locomotive_cursor := fn_show_train_locomotives(v_train_id);


    DBMS_OUTPUT.PUT_LINE('Locomotive ID before addition: ');
    LOOP
FETCH v_locomotive_cursor INTO v_temp_locomotive_id, v_temp_train_id;
        EXIT WHEN v_locomotive_cursor%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Train ID: ' || v_temp_train_id || ', Locomotive ID: ' || v_temp_locomotive_id);
END LOOP;
CLOSE v_locomotive_cursor;
p_add_locomotive_to_train(v_train_id, v_locomotive_id);
    v_locomotive_cursor := fn_show_train_locomotives(v_train_id);

    DBMS_OUTPUT.PUT_LINE('Locomotive ID after addition: ');
    LOOP
FETCH v_locomotive_cursor INTO v_temp_locomotive_id, v_temp_train_id;
        EXIT WHEN v_locomotive_cursor%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Train ID: ' || v_temp_train_id || ', Locomotive ID: ' || v_temp_locomotive_id);
END LOOP;
CLOSE v_locomotive_cursor;
ROLLBACK;
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE IN (-20001, -20002, -20003) THEN
            DBMS_OUTPUT.PUT_LINE(SQLERRM);
ELSE
            DBMS_OUTPUT.PUT_LINE('ERRO INESPERADO: ' || SQLERRM);
END IF;
ROLLBACK;
END;
/


-- ---------------------------------------------------------------------------------------------
-- Invalid Test: Train exceeds max length after addition
-- ---------------------------------------------------------------------------------------------

SET SERVEROUTPUT ON;
DECLARE
v_train_id      Train.train_id%TYPE := 9002;
    v_locomotive_id       Locomotive.locomotive_number%TYPE := 8802;

    v_temp_locomotive_id Locomotive.locomotive_number%TYPE;
    v_temp_train_id Train.train_id%TYPE;
    v_locomotive_cursor SYS_REFCURSOR;

BEGIN

    DBMS_OUTPUT.PUT_LINE('--- Test 4: Train exceeds max length after addition, trying to add locomotive n 8802 to train n 9999---');
    v_locomotive_cursor := fn_show_train_locomotives(v_train_id);


    DBMS_OUTPUT.PUT_LINE('Locomotive ID before addition: ');
    LOOP
FETCH v_locomotive_cursor INTO v_temp_locomotive_id, v_temp_train_id;
        EXIT WHEN v_locomotive_cursor%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Train ID: ' || v_temp_train_id || ', Locomotive ID: ' || v_temp_locomotive_id);
END LOOP;
CLOSE v_locomotive_cursor;
p_add_locomotive_to_train(v_train_id, v_locomotive_id);
    v_locomotive_cursor := fn_show_train_locomotives(v_train_id);

    DBMS_OUTPUT.PUT_LINE('Locomotive ID after addition: ');
    LOOP
FETCH v_locomotive_cursor INTO v_temp_locomotive_id, v_temp_train_id;
        EXIT WHEN v_locomotive_cursor%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Train ID: ' || v_temp_train_id || ', Locomotive ID: ' || v_temp_locomotive_id);
END LOOP;
CLOSE v_locomotive_cursor;
ROLLBACK;
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE IN (-20001, -20002, -20003) THEN
            DBMS_OUTPUT.PUT_LINE(SQLERRM);
ELSE
            DBMS_OUTPUT.PUT_LINE('ERRO INESPERADO: ' || SQLERRM);
END IF;
ROLLBACK;
END;
/
