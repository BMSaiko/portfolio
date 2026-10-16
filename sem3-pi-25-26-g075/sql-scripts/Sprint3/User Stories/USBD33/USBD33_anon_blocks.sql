-- TEST 1: Valid insertion (Increases Peak Length)

SET SERVEROUTPUT ON;
DECLARE
    l_train_id NUMBER := 9900;
    l_freight_id NUMBER := 8801;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST 1: Valid Insertion (Increases Peak Length) ---');
    p_print_train_data(l_train_id);
    DBMS_OUTPUT.PUT_LINE('');
    p_add_freight_to_train(l_train_id, l_freight_id);
    DBMS_OUTPUT.PUT_LINE('');
    p_print_train_data(l_train_id);
    ROLLBACK;
EXCEPTION
    WHEN OTHERS THEN DBMS_OUTPUT.PUT_LINE('[FAIL] ' || SQLERRM);
END;
/

-- TEST 2: Valid insertion (Doesn't Increase Peak Length)

SET SERVEROUTPUT ON;
DECLARE
    l_train_id NUMBER := 9900;
    l_freight_id NUMBER := 8802;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST 2: Valid Insertion (Does not Increase Peak Length) ---');
    p_print_train_data(l_train_id);
    DBMS_OUTPUT.PUT_LINE('');
    p_add_freight_to_train(l_train_id, l_freight_id);
    DBMS_OUTPUT.PUT_LINE('');
    p_print_train_data(l_train_id);
    ROLLBACK;
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE IN (-20020, -20021, -20022, -20023) THEN
            DBMS_OUTPUT.PUT_LINE(SQLERRM);
        ELSE
            DBMS_OUTPUT.PUT_LINE('Unexpected error: ' || SQLERRM);
        END IF;
        ROLLBACK;
END;
/

-- TEST 3: Invalid Train

SET SERVEROUTPUT ON;
DECLARE
    l_train_id NUMBER := 0;
    l_freight_id NUMBER := 8801;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST 3: Invalid Train ---');
    p_print_train_data(l_train_id);
    DBMS_OUTPUT.PUT_LINE('');
    p_add_freight_to_train(l_train_id, l_freight_id);
    DBMS_OUTPUT.PUT_LINE('');
    p_print_train_data(l_train_id);
    ROLLBACK;
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE IN (-20020, -20021, -20022, -20023) THEN
            DBMS_OUTPUT.PUT_LINE(SQLERRM);
        ELSE
            DBMS_OUTPUT.PUT_LINE('Unexpected error: ' || SQLERRM);
        END IF;
        ROLLBACK;
END;
/

-- TEST 4: Invalid Freight

SET SERVEROUTPUT ON;
DECLARE
    l_train_id NUMBER := 9900;
    l_freight_id NUMBER := 0;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST 4: Invalid Freight ---');
    p_print_train_data(l_train_id);
    DBMS_OUTPUT.PUT_LINE('');
    p_add_freight_to_train(l_train_id, l_freight_id);
    DBMS_OUTPUT.PUT_LINE('');
    p_print_train_data(l_train_id);
    ROLLBACK;
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE IN (-20020, -20021, -20022, -20023) THEN
            DBMS_OUTPUT.PUT_LINE(SQLERRM);
        ELSE
            DBMS_OUTPUT.PUT_LINE('Unexpected error: ' || SQLERRM);
        END IF;
        ROLLBACK;
END;
/

-- TEST 5: Invalid Association (Max Length Exceeded)

SET SERVEROUTPUT ON;
DECLARE
    l_train_id NUMBER := 9901;
    l_freight_id NUMBER := 8803;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- TEST 5: Invalid Association (Max Length Exceeded) ---');
    p_print_train_data(l_train_id);
    DBMS_OUTPUT.PUT_LINE('');
    p_add_freight_to_train(l_train_id, l_freight_id);
    DBMS_OUTPUT.PUT_LINE('');
    p_print_train_data(l_train_id);
    ROLLBACK;
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE IN (-20020, -20021, -20022, -20023) THEN
            DBMS_OUTPUT.PUT_LINE(SQLERRM);
        ELSE
            DBMS_OUTPUT.PUT_LINE('Unexpected error: ' || SQLERRM);
        END IF;
        ROLLBACK;
END;
/

-- DELETE FROM TRAINFREIGHT WHERE train_id = 9900;