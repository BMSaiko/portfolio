
-- Valid Test 1: Valid removal of Freight from Train, freight n 2003 from train n 5421

SET SERVEROUTPUT ON;
DECLARE
    v_train_id      Train.train_id%TYPE := 5421;
    v_freight_id       Freight.freight_id%TYPE := 2003;

    v_temp_freight_id Freight.freight_id%TYPE;
    v_temp_train_id Train.train_id%TYPE;
    v_freight_cursor SYS_REFCURSOR;
BEGIN

    DBMS_OUTPUT.PUT_LINE('--- Teste 1: Valid Removal of Freight 2003 from Train 5421 ---');
    v_freight_cursor := fn_show_train_freights(v_train_id);

    DBMS_OUTPUT.PUT_LINE('Freight before removal: ');
    LOOP
        FETCH v_freight_cursor INTO v_temp_freight_id, v_temp_train_id;
        EXIT WHEN v_freight_cursor%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Train ID: ' || v_temp_train_id || ', Freight ID: ' || v_temp_freight_id);
    END LOOP;
    CLOSE v_freight_cursor;


    p_remove_freight_from_train(v_train_id, v_freight_id);
    DBMS_OUTPUT.PUT_LINE('Freight after removal: ');
    v_freight_cursor := fn_show_train_freights(v_train_id);
    LOOP
        FETCH v_freight_cursor INTO v_temp_freight_id, v_temp_train_id;
        EXIT WHEN v_freight_cursor%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Train ID: ' || v_temp_train_id || ', Freight ID: ' || v_temp_freight_id);
    END LOOP;
    CLOSE v_freight_cursor;
    ROLLBACK;

EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE IN (-20010, -20011, -20012, -20013) THEN
            DBMS_OUTPUT.PUT_LINE(SQLERRM);
        ELSE
            DBMS_OUTPUT.PUT_LINE('Unexpected Error: ' || SQLERRM);
        END IF;
        ROLLBACK;
END;
/

-- Invalid Test 2: Invalid associantion between Freight and Train (removal of Freight 2003 from Train 5422)

SET SERVEROUTPUT ON;
DECLARE
    v_train_id      Train.train_id%TYPE := 5435;
    v_freight_id       Freight.freight_id%TYPE := 2003;

    v_temp_freight_id Freight.freight_id%TYPE;
    v_temp_train_id Train.train_id%TYPE;
    v_freight_cursor SYS_REFCURSOR;
BEGIN

    DBMS_OUTPUT.PUT_LINE('--- Teste 2: Invalid Removal of Freight 2003 from Train 5422 ---');
    v_freight_cursor := fn_show_train_freights(v_train_id);

    DBMS_OUTPUT.PUT_LINE('Freight information before removal: ');
    LOOP
        FETCH v_freight_cursor INTO v_temp_freight_id, v_temp_train_id;
        EXIT WHEN v_freight_cursor%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Train ID: ' || v_temp_train_id || ', Freight ID: ' || v_temp_freight_id);
    END LOOP;
    CLOSE v_freight_cursor;


    p_remove_freight_from_train(v_train_id, v_freight_id);
    DBMS_OUTPUT.PUT_LINE('Freight after removal: ');
    v_freight_cursor := fn_show_train_freights(v_train_id);
    LOOP
        FETCH v_freight_cursor INTO v_temp_freight_id, v_temp_train_id;
        EXIT WHEN v_freight_cursor%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Train ID: ' || v_temp_train_id || ', Freight ID: ' || v_temp_freight_id);
    END LOOP;
    CLOSE v_freight_cursor;
    ROLLBACK;

EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE IN (-20010, -20011, -20012, -20013) THEN
            DBMS_OUTPUT.PUT_LINE(SQLERRM);
        ELSE
            DBMS_OUTPUT.PUT_LINE('Unexpected Error: ' || SQLERRM);
        END IF;
        ROLLBACK;
END;
/

-- Invalid Test 3: Invalid Train ID (removal of Freight 2003 from Train 0000)

SET SERVEROUTPUT ON;
DECLARE
    v_train_id      Train.train_id%TYPE := 0000;
    v_freight_id       Freight.freight_id%TYPE := 2003;

    v_temp_freight_id Freight.freight_id%TYPE;
    v_temp_train_id Train.train_id%TYPE;
    v_freight_cursor SYS_REFCURSOR;
BEGIN

    DBMS_OUTPUT.PUT_LINE('--- Teste 3: Invalid Train 0000 ---');
    v_freight_cursor := fn_show_train_freights(v_train_id);

    DBMS_OUTPUT.PUT_LINE('Freight information before removal: ');
    LOOP
        FETCH v_freight_cursor INTO v_temp_freight_id, v_temp_train_id;
        EXIT WHEN v_freight_cursor%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Train ID: ' || v_temp_train_id || ', Freight ID: ' || v_temp_freight_id);
    END LOOP;
    CLOSE v_freight_cursor;


    p_remove_freight_from_train(v_train_id, v_freight_id);
    DBMS_OUTPUT.PUT_LINE('Freight after removal: ');
    v_freight_cursor := fn_show_train_freights(v_train_id);
    LOOP
        FETCH v_freight_cursor INTO v_temp_freight_id, v_temp_train_id;
        EXIT WHEN v_freight_cursor%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Train ID: ' || v_temp_train_id || ', Freight ID: ' || v_temp_freight_id);
    END LOOP;
    CLOSE v_freight_cursor;
    ROLLBACK;

EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE IN (-20010, -20011, -20012, -20013) THEN
            DBMS_OUTPUT.PUT_LINE(SQLERRM);
        ELSE
            DBMS_OUTPUT.PUT_LINE('Unexpected Error: ' || SQLERRM);
        END IF;
        ROLLBACK;
END;
/

-- Invalid Test 4: Invalid Freight ID (removal of Freight 9999 from Train 5422)

SET SERVEROUTPUT ON;
DECLARE
    v_train_id      Train.train_id%TYPE := 5435;
    v_freight_id       Freight.freight_id%TYPE := 9999;

    v_temp_freight_id Freight.freight_id%TYPE;
    v_temp_train_id Train.train_id%TYPE;
    v_freight_cursor SYS_REFCURSOR;
BEGIN

    DBMS_OUTPUT.PUT_LINE('--- Teste 4: Invalid Removal of Freight 9999 from Train 5435 ---');
    v_freight_cursor := fn_show_train_freights(v_train_id);

    DBMS_OUTPUT.PUT_LINE('Freight information before removal: ');
    LOOP
        FETCH v_freight_cursor INTO v_temp_freight_id, v_temp_train_id;
        EXIT WHEN v_freight_cursor%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Train ID: ' || v_temp_train_id || ', Freight ID: ' || v_temp_freight_id);
    END LOOP;
    CLOSE v_freight_cursor;


    p_remove_freight_from_train(v_train_id, v_freight_id);
    DBMS_OUTPUT.PUT_LINE('Freight after removal: ');
    v_freight_cursor := fn_show_train_freights(v_train_id);
    LOOP
        FETCH v_freight_cursor INTO v_temp_freight_id, v_temp_train_id;
        EXIT WHEN v_freight_cursor%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Train ID: ' || v_temp_train_id || ', Freight ID: ' || v_temp_freight_id);
    END LOOP;
    CLOSE v_freight_cursor;
    ROLLBACK;

EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE IN (-20010, -20011, -20012, -20013) THEN
            DBMS_OUTPUT.PUT_LINE(SQLERRM);
        ELSE
            DBMS_OUTPUT.PUT_LINE('Unexpected Error: ' || SQLERRM);
        END IF;
        ROLLBACK;
END;
/