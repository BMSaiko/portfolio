CREATE OR REPLACE TRIGGER check_operation_execution_time
BEFORE INSERT OR UPDATE ON Operation
FOR EACH ROW
DECLARE
    v_max_time NUMBER;
BEGIN

    SELECT MAX(wt.MaximumExecutionTime)
    INTO v_max_time
    FROM WorkstationTypes wt;

    IF :NEW.ExpectedExecutionTime IS NOT NULL AND :NEW.ExpectedExecutionTime > v_max_time THEN
        RAISE_APPLICATION_ERROR(
            -20001,
            'ExpectedExecutionTime (' || :NEW.ExpectedExecutionTime || ') excede o tempo máximo permitido (' || v_max_time || ').'
        );
    END IF;
END;
/


