BEGIN
    -- ----------------------------------------
    -- 1. Insert Operation Types
    -- ----------------------------------------
    INSERT INTO OperationType (OTID, Description) VALUES (5647, 'Disc cutting');
    INSERT INTO OperationType (OTID, Description) VALUES (5649, 'Initial pot base pressing');
    INSERT INTO OperationType (OTID, Description) VALUES (5651, 'Final pot base pressing');
    INSERT INTO OperationType (OTID, Description) VALUES (5653, 'Pot base finishing');
    INSERT INTO OperationType (OTID, Description) VALUES (5655, 'Lid pressing');
    INSERT INTO OperationType (OTID, Description) VALUES (5657, 'Lid finishing');
    INSERT INTO OperationType (OTID, Description) VALUES (5659, 'Pot handles riveting');
    INSERT INTO OperationType (OTID, Description) VALUES (5661, 'Lid handle screw');
    INSERT INTO OperationType (OTID, Description) VALUES (5663, 'Pot test and packaging');
    INSERT INTO OperationType (OTID, Description) VALUES (5665, 'Handle welding');
    INSERT INTO OperationType (OTID, Description) VALUES (5667, 'Lid polishing');
    INSERT INTO OperationType (OTID, Description) VALUES (5669, 'Pot base polishing');
    INSERT INTO OperationType (OTID, Description) VALUES (5671, 'Teflon painting');
    INSERT INTO OperationType (OTID, Description) VALUES (5681, 'Initial pan base pressing');
    INSERT INTO OperationType (OTID, Description) VALUES (5682, 'Final pan base pressing');
    INSERT INTO OperationType (OTID, Description) VALUES (5683, 'Pan base finishing');
    INSERT INTO OperationType (OTID, Description) VALUES (5685, 'Handle gluing');
    INSERT INTO OperationType (OTID, Description) VALUES (5688, 'Pan test and packaging');

    -- ----------------------------------------
    -- 2. Insert Workstation-Operation Type Mappings
    -- ----------------------------------------
    -- Operation 5647: Disc cutting
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('A4578', 5647);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('A4588', 5647);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('A4598', 5647);

    -- Operation 5649: Initial pot base pressing
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('A4588', 5649);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('A4598', 5649);

    -- Operation 5651: Final pot base pressing
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('A4588', 5651);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('A4598', 5651);

    -- Operation 5653: Pot base finishing
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('C5637', 5653);

    -- Operation 5655: Lid pressing
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('A4588', 5655);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('A4598', 5655);

    -- Operation 5657: Lid finishing
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('C5637', 5657);

    -- Operation 5659: Pot handles riveting
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('S3271', 5659);

    -- Operation 5661: Lid handle screw
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('T3452', 5661);

    -- Operation 5663: Pot test and packaging
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('K3675', 5663);

    -- Operation 5665: Handle welding
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('D9123', 5665);

    -- Operation 5667: Lid polishing
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('Q3547', 5667);

    -- Operation 5669: Pot base polishing
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('Q3547', 5669);

    -- Operation 5671: Teflon painting
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('Q5478', 5671);

    -- Operation 5681: Initial pan base pressing
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('A4588', 5681);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('A4598', 5681);

    -- Operation 5682: Final pan base pressing
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('A4588', 5682);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('A4598', 5682);

    -- Operation 5683: Pan base finishing
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('C5637', 5683);

    -- Operation 5685: Handle gluing
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('D9123', 5685);

    -- Operation 5688: Pan test and packaging
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('K3675', 5688);

    -- ----------------------------------------
    -- 3. Insert Operations
    -- ----------------------------------------
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
    VALUES (100, 'Operation 100', 5647, 120);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
    VALUES (103, 'Operation 103', 5649, 90);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
    VALUES (112, 'Operation 112', 5651, 120);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
    VALUES (114, 'Operation 114', 5653, 300);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
    VALUES (115, 'Operation 115', 5659, 600);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
    VALUES (120, 'Operation 120', 5647, 120);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
    VALUES (121, 'Operation 121', 5655, 60);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
    VALUES (122, 'Operation 122', 5657, 240);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
    VALUES (123, 'Operation 123', 5661, 120);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
    VALUES (124, 'Operation 124', 5667, 1200);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
    VALUES (130, 'Operation 130', 5663, 240);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
    VALUES (150, 'Operation 150', 5647, 120);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
    VALUES (151, 'Operation 151', 5649, 90);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
    VALUES (152, 'Operation 152', 5651, 120);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
    VALUES (153, 'Operation 153', 5653, 300);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
    VALUES (154, 'Operation 154', 5659, 600);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
    VALUES (160, 'Operation 160', 5647, 120);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
    VALUES (161, 'Operation 161', 5655, 60);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
    VALUES (162, 'Operation 162', 5657, 240);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
    VALUES (163, 'Operation 163', 5661, 120);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
    VALUES (164, 'Operation 164', 5667, 1200);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, ExpectedExecutionTime)
    VALUES (170, 'Operation 170', 5663, 240);

    -- ----------------------------------------
    -- 4. Insert BOO (Business Object Operations)
    -- ----------------------------------------
    INSERT INTO BOO_Operation (OPID, BOOID, NextOPID, NextBOOID)
    VALUES (100, 'AS12946S22', NULL, NULL);
    INSERT INTO BOO_Operation (OPID, BOOID, NextOPID, NextBOOID)
    VALUES (103, 'AS12946S22', NULL, NULL);
    INSERT INTO BOO_Operation (OPID, BOOID, NextOPID, NextBOOID)
    VALUES (112, 'AS12946S22', NULL, NULL);
    INSERT INTO BOO_Operation (OPID, BOOID, NextOPID, NextBOOID)
    VALUES (114, 'AS12946S22', NULL, NULL);
    INSERT INTO BOO_Operation (OPID, BOOID, NextOPID, NextBOOID)
    VALUES (115, 'AS12946S22', NULL, NULL);
    INSERT INTO BOO_Operation (OPID, BOOID, NextOPID, NextBOOID)
    VALUES (120, 'AS12947S22', NULL, NULL);
    INSERT INTO BOO_Operation (OPID, BOOID, NextOPID, NextBOOID)
    VALUES (121, 'AS12947S22', NULL, NULL);
    INSERT INTO BOO_Operation (OPID, BOOID, NextOPID, NextBOOID)
    VALUES (122, 'AS12947S22', NULL, NULL);
    INSERT INTO BOO_Operation (OPID, BOOID, NextOPID, NextBOOID)
    VALUES (123, 'AS12947S22', NULL, NULL);
    INSERT INTO BOO_Operation (OPID, BOOID, NextOPID, NextBOOID)
    VALUES (124, 'AS12947S22', NULL, NULL);
    INSERT INTO BOO_Operation (OPID, BOOID, NextOPID, NextBOOID)
    VALUES (130, 'AS12945S22', NULL, NULL);
    INSERT INTO BOO_Operation (OPID, BOOID, NextOPID, NextBOOID)
    VALUES (150, 'AS12946S20', NULL, NULL);
    INSERT INTO BOO_Operation (OPID, BOOID, NextOPID, NextBOOID)
    VALUES (151, 'AS12946S20', NULL, NULL);
    INSERT INTO BOO_Operation (OPID, BOOID, NextOPID, NextBOOID)
    VALUES (152, 'AS12946S20', NULL, NULL);
    INSERT INTO BOO_Operation (OPID, BOOID, NextOPID, NextBOOID)
    VALUES (153, 'AS12946S20', NULL, NULL);
    INSERT INTO BOO_Operation (OPID, BOOID, NextOPID, NextBOOID)
    VALUES (154, 'AS12946S20', NULL, NULL);
    INSERT INTO BOO_Operation (OPID, BOOID, NextOPID, NextBOOID)
    VALUES (160, 'AS12947S20', NULL, NULL);
    INSERT INTO BOO_Operation (OPID, BOOID, NextOPID, NextBOOID)
    VALUES (161, 'AS12947S20', NULL, NULL);
    INSERT INTO BOO_Operation (OPID, BOOID, NextOPID, NextBOOID)
    VALUES (162, 'AS12947S20', NULL, NULL);
    INSERT INTO BOO_Operation (OPID, BOOID, NextOPID, NextBOOID)
    VALUES (163, 'AS12947S20', NULL, NULL);
    INSERT INTO BOO_Operation (OPID, BOOID, NextOPID, NextBOOID)
    VALUES (164, 'AS12947S20', NULL, NULL);
    INSERT INTO BOO_Operation (OPID, BOOID, NextOPID, NextBOOID)
    VALUES (170, 'AS12945S20', NULL, NULL);

    -- ----------------------------------------
    -- 5. Update BOO_Operation with Next Operations
    -- ----------------------------------------
    -- Update sequences for AS12946S22
    UPDATE BOO_Operation
    SET NextOPID = 103, NextBOOID = 'AS12946S22'
    WHERE OPID = 100 AND BOOID = 'AS12946S22';
    
    UPDATE BOO_Operation
    SET NextOPID = 112, NextBOOID = 'AS12946S22'
    WHERE OPID = 103 AND BOOID = 'AS12946S22';
    
    UPDATE BOO_Operation
    SET NextOPID = 114, NextBOOID = 'AS12946S22'
    WHERE OPID = 112 AND BOOID = 'AS12946S22';
    
    UPDATE BOO_Operation
    SET NextOPID = 115, NextBOOID = 'AS12946S22'
    WHERE OPID = 114 AND BOOID = 'AS12946S22';
    
    -- Update sequences for AS12947S22
    UPDATE BOO_Operation
    SET NextOPID = 121, NextBOOID = 'AS12947S22'
    WHERE OPID = 120 AND BOOID = 'AS12947S22';
    
    UPDATE BOO_Operation
    SET NextOPID = 122, NextBOOID = 'AS12947S22'
    WHERE OPID = 121 AND BOOID = 'AS12947S22';
    
    UPDATE BOO_Operation
    SET NextOPID = 123, NextBOOID = 'AS12947S22'
    WHERE OPID = 122 AND BOOID = 'AS12947S22';
    
    UPDATE BOO_Operation
    SET NextOPID = 124, NextBOOID = 'AS12947S22'
    WHERE OPID = 123 AND BOOID = 'AS12947S22';
    
    -- Update sequences for AS12946S20
    UPDATE BOO_Operation
    SET NextOPID = 151, NextBOOID = 'AS12946S20'
    WHERE OPID = 150 AND BOOID = 'AS12946S20';
    
    UPDATE BOO_Operation
    SET NextOPID = 152, NextBOOID = 'AS12946S20'
    WHERE OPID = 151 AND BOOID = 'AS12946S20';
    
    UPDATE BOO_Operation
    SET NextOPID = 153, NextBOOID = 'AS12946S20'
    WHERE OPID = 152 AND BOOID = 'AS12946S20';
    
    UPDATE BOO_Operation
    SET NextOPID = 154, NextBOOID = 'AS12946S20'
    WHERE OPID = 153 AND BOOID = 'AS12946S20';
    
    -- Update sequences for AS12947S20
    UPDATE BOO_Operation
    SET NextOPID = 161, NextBOOID = 'AS12947S20'
    WHERE OPID = 160 AND BOOID = 'AS12947S20';
    
    UPDATE BOO_Operation
    SET NextOPID = 162, NextBOOID = 'AS12947S20'
    WHERE OPID = 161 AND BOOID = 'AS12947S20';
    
    UPDATE BOO_Operation
    SET NextOPID = 163, NextBOOID = 'AS12947S20'
    WHERE OPID = 162 AND BOOID = 'AS12947S20';
    
    UPDATE BOO_Operation
    SET NextOPID = 164, NextBOOID = 'AS12947S20'
    WHERE OPID = 163 AND BOOID = 'AS12947S20';

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
