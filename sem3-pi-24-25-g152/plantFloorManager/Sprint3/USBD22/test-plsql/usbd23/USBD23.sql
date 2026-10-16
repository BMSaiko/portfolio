CREATE OR REPLACE TRIGGER trg_check_operation_time
BEFORE INSERT OR UPDATE ON Operation
FOR EACH ROW
DECLARE
    v_min_max_time NUMBER(10);
BEGIN
    -- Obter o mínimo de MaximumExecutionTime de todos os tipos de estação de trabalho
    -- que podem realizar o tipo de operação desta nova/atualizada operação
    SELECT MIN(wt.MaximumExecutionTime)
      INTO v_min_max_time
      FROM WorkstationTypes wt
      JOIN WSTypes_OperationTypes wot ON wot.WTID = wt.WTID
     WHERE wot.OperationTypeOTID = :NEW.OperationTypeOTID;

    -- Verificar se o ExpectedExecutionTime excede o mínimo dos tempos máximos suportados
    IF :NEW.ExpectedExecutionTime > v_min_max_time THEN
        RAISE_APPLICATION_ERROR(-20001,
            'O ExpectedExecutionTime (' || :NEW.ExpectedExecutionTime ||
            ') excede o tempo máximo permitido (' || v_min_max_time || ') para o tipo de operação.');
    END IF;
END;
/
