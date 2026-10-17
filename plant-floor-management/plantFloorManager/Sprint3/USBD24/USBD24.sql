CREATE OR REPLACE TRIGGER prevent_circular_boo_references
BEFORE INSERT ON PartIN
FOR EACH ROW
DECLARE
    v_is_circular BOOLEAN := FALSE;
    v_partid VARCHAR2(50);
    v_opid NUMBER(10);
    v_booid VARCHAR2(10);
    v_next_opid NUMBER(10);
    v_next_booid VARCHAR2(10);
BEGIN
    -- Get the PartID and BOO information for the current insert
    v_partid := :NEW.PartID;
    v_opid := :NEW.OPID;
    v_booid := :NEW.BOOID;

    -- Check if the PartID being inserted is part of any BOO chain
    -- Start by checking if the current OPID and BOOID are part of a cycle
    FOR rec IN (
        SELECT bo.OPID, bo.BOOID, bo.NextOPID, bo.NextBOOID
        FROM BOO_Operation bo
        WHERE bo.BOOID = v_booid AND bo.OPID = v_opid
    ) LOOP
        -- Check if the next operation in the chain points back to the same PartID
        v_next_opid := rec.NextOPID;
        v_next_booid := rec.NextBOOID;

        -- Check for circular references by looping through the BOO_Operation table
        -- Ensure that no operation leads back to the original PartID in a circular manner
        LOOP
            EXIT WHEN v_next_opid IS NULL OR v_next_booid IS NULL;
            
            -- Check the next operation in the BOO_Operation table
            SELECT NextOPID, NextBOOID
            INTO v_next_opid, v_next_booid
            FROM BOO_Operation
            WHERE OPID = v_next_opid AND BOOID = v_next_booid;

            -- If we have reached the original OPID and BOOID, then it's a circular reference
            IF v_next_opid = v_opid AND v_next_booid = v_booid THEN
                v_is_circular := TRUE;
                EXIT;
            END IF;
        END LOOP;
    END LOOP;

    -- Raise an exception if a circular reference is detected
    IF v_is_circular THEN
        RAISE_APPLICATION_ERROR(-20001, 'Circular reference detected in BOO input. The part cannot be inserted.');
    END IF;
END;
/