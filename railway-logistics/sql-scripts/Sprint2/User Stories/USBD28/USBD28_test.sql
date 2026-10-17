
-- ---------------------------------------------------------------------------------------------
-- Test for Operator: Medway
-- ---------------------------------------------------------------------------------------------
SET SERVEROUTPUT ON;

DECLARE
    locomotive_cursor                 SYS_REFCURSOR;
    locomotive_number                 Locomotive.locomotive_number%TYPE;
    locomotive_name                   Locomotive.locomotive_name%TYPE;
    locomotive_model_name             LocomotiveModel.locomotive_model_name%TYPE;
    locomotive_operator_name          VehicleOperator.operator_name%TYPE;
    locomotive_gauge_count            NUMBER;
    var_records_found                 BOOLEAN := FALSE;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- Operator: Medway ---');

    locomotive_cursor := LocomotiveWithMoreThanOneGaugeByOperator('PT509017800');

    LOOP
        FETCH locomotive_cursor INTO
            locomotive_number,
            locomotive_name,
            locomotive_model_name,
            locomotive_operator_name,
            locomotive_gauge_count;
        EXIT WHEN locomotive_cursor%NOTFOUND;

        var_records_found := TRUE;
        DBMS_OUTPUT.PUT_LINE(
            '  -> No.: ' || locomotive_number ||
            ' | Name: ' || locomotive_name ||
            ' | Model: ' || locomotive_model_name ||
            ' | Gauges Supported: ' || locomotive_gauge_count
        );
    END LOOP;

    IF NOT var_records_found THEN
        DBMS_OUTPUT.PUT_LINE('  No multi-gauge locomotives found for this operator.');
    END IF;

    CLOSE locomotive_cursor;
    DBMS_OUTPUT.PUT_LINE('--- Test for Medway completed ---');
END;
/
-- ------------------------------------------------------------------------------
--- Test for Operator: Captrain
-- ------------------------------------------------------------------------------
SET SERVEROUTPUT ON;

DECLARE
    locomotive_cursor                 SYS_REFCURSOR;
    locomotive_number                 Locomotive.locomotive_number%TYPE;
    locomotive_name                   Locomotive.locomotive_name%TYPE;
    locomotive_model_name             LocomotiveModel.locomotive_model_name%TYPE;
    locomotive_operator_name          VehicleOperator.operator_name%TYPE;
    locomotive_gauge_count            NUMBER;
    var_records_found                 BOOLEAN := FALSE;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- Operator: Captrain ---');

    locomotive_cursor := LocomotiveWithMoreThanOneGaugeByOperator('PT507832388');

    LOOP
        FETCH locomotive_cursor INTO
            locomotive_number,
            locomotive_name,
            locomotive_model_name,
            locomotive_operator_name,
            locomotive_gauge_count;

        EXIT WHEN locomotive_cursor%NOTFOUND;

        var_records_found := TRUE;
        DBMS_OUTPUT.PUT_LINE(
            '  -> No.: ' || locomotive_number ||
            ' | Name: ' || locomotive_name ||
            ' | Model: ' || locomotive_model_name ||
            ' | Gauges Supported: ' || locomotive_gauge_count
        );
    END LOOP;

    IF NOT var_records_found THEN
        DBMS_OUTPUT.PUT_LINE('  No multi-gauge locomotives found for this operator.');
    END IF;

    CLOSE locomotive_cursor;
    DBMS_OUTPUT.PUT_LINE('--- Test for Captrain completed ---');
END;
/

-- ------------------------------------------------------------------------------
-- Test with DHL Operator (Negative Test)
-- ------------------------------------------------------------------------------
SET SERVEROUTPUT ON;

DECLARE
    locomotive_cursor              SYS_REFCURSOR;
    locomotive_number              Locomotive.locomotive_number%TYPE;
    locomotive_name                Locomotive.locomotive_name%TYPE;
    locomotive_model_name          LocomotiveModel.locomotive_model_name%TYPE;
    locomotive_operator_name       VehicleOperator.operator_name%TYPE;
    gauge_count                    NUMBER;
    var_found_locomotives          BOOLEAN := FALSE;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- Operator: DHL (Negative Test) ---');
    locomotive_cursor := LocomotiveWithMoreThanOneGaugeByOperator('PT508123456');

    LOOP
        FETCH locomotive_cursor INTO
            locomotive_number,
            locomotive_name,
            locomotive_model_name,
            locomotive_operator_name,
            gauge_count;
        EXIT WHEN locomotive_cursor%NOTFOUND;

        var_found_locomotives := TRUE;

        DBMS_OUTPUT.PUT_LINE(
            '  -> Nº: ' || locomotive_number ||
            ' | Nome: ' || locomotive_name ||
            ' | Modelo: ' || locomotive_model_name ||
            ' | Gauges: ' || gauge_count
        );
    END LOOP;

    IF NOT var_found_locomotives THEN
        DBMS_OUTPUT.PUT_LINE('No results found for this operator.');
    END IF;

    CLOSE locomotive_cursor;
    DBMS_OUTPUT.PUT_LINE('--- Test completed ---');
END;
/

-- ------------------------------------------------------------------------------
-- Test with a Nonexistent Operator
-- ------------------------------------------------------------------------------
SET SERVEROUTPUT ON;

DECLARE
locomotive_cursor              SYS_REFCURSOR;
    locomotive_number              Locomotive.locomotive_number%TYPE;
    locomotive_name                Locomotive.locomotive_name%TYPE;
    locomotive_model_name          LocomotiveModel.locomotive_model_name%TYPE;
    locomotive_operator_name       VehicleOperator.operator_name%TYPE;
    gauge_count                    NUMBER;
    var_found_locomotives          BOOLEAN := FALSE;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- Operator: Nonexistent ---');
    locomotive_cursor := LocomotiveWithMoreThanOneGaugeByOperator('XX000000000');

    LOOP
        FETCH locomotive_cursor INTO
            locomotive_number,
            locomotive_name,
            locomotive_model_name,
            locomotive_operator_name,
            gauge_count;
        EXIT WHEN locomotive_cursor%NOTFOUND;

        var_found_locomotives := TRUE;

        DBMS_OUTPUT.PUT_LINE(
            '  -> Nº: ' || locomotive_number ||
            ' | Nome: ' || locomotive_name ||
            ' | Modelo: ' || locomotive_model_name ||
            ' | Gauges: ' || gauge_count
        );
    END LOOP;

    IF NOT var_found_locomotives THEN
        DBMS_OUTPUT.PUT_LINE('No results found for this operator.');
    END IF;

    CLOSE locomotive_cursor;
    DBMS_OUTPUT.PUT_LINE('--- Test completed ---');
END;
/

COMMIT;