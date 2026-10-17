BEGIN

    -- Insert ProductFamily
    INSERT INTO ProductFamily (PFID, Name) VALUES (130, 'Family 130');
    INSERT INTO ProductFamily (PFID, Name) VALUES (125, 'Family 125');
    INSERT INTO ProductFamily (PFID, Name) VALUES (145, 'Family 145');
    INSERT INTO ProductFamily (PFID, Name) VALUES (132, 'Family 132');
    INSERT INTO ProductFamily (PFID, Name) VALUES (146, 'Family 146');

    -- Insert WorkstationTypes
    INSERT INTO WorkstationTypes (WTID, Name, MaximumExecutionTime, SetupTime)
    VALUES ('A4578', 'Workstation A4578', 60, 10);
    INSERT INTO WorkstationTypes (WTID, Name, MaximumExecutionTime, SetupTime)
    VALUES ('A4588', 'Workstation A4588', 60, 10);
    INSERT INTO WorkstationTypes (WTID, Name, MaximumExecutionTime, SetupTime)
    VALUES ('A4598', 'Workstation A4598', 60, 10);
    INSERT INTO WorkstationTypes (WTID, Name, MaximumExecutionTime, SetupTime)
    VALUES ('C5637', 'Workstation C5637', 60, 10);
    INSERT INTO WorkstationTypes (WTID, Name, MaximumExecutionTime, SetupTime)
    VALUES ('D9123', 'Workstation D9123', 60, 10);
    INSERT INTO WorkstationTypes (WTID, Name, MaximumExecutionTime, SetupTime)
    VALUES ('K3675', 'Workstation K3675', 60, 10);
    INSERT INTO WorkstationTypes (WTID, Name, MaximumExecutionTime, SetupTime)
    VALUES ('K3676', 'Workstation K3676', 60, 10);
    INSERT INTO WorkstationTypes (WTID, Name, MaximumExecutionTime, SetupTime)
    VALUES ('Q3547', 'Workstation Q3547', 60, 10);
    INSERT INTO WorkstationTypes (WTID, Name, MaximumExecutionTime, SetupTime)
    VALUES ('Q3548', 'Workstation Q3548', 60, 10);
    INSERT INTO WorkstationTypes (WTID, Name, MaximumExecutionTime, SetupTime)
    VALUES ('Q3549', 'Workstation Q3549', 60, 10);
    INSERT INTO WorkstationTypes (WTID, Name, MaximumExecutionTime, SetupTime)
    VALUES ('Q5478', 'Workstation Q5478', 60, 10);
    INSERT INTO WorkstationTypes (WTID, Name, MaximumExecutionTime, SetupTime)
    VALUES ('S3271', 'Workstation S3271', 60, 10);
    INSERT INTO WorkstationTypes (WTID, Name, MaximumExecutionTime, SetupTime)
    VALUES ('T3452', 'Workstation T3452', 60, 10);

    -- Insert Workstations
    INSERT INTO Workstations (WSID, Name, Description, WTID)
    VALUES (9875, 'Press 01', '220-630t cold forging press', 'A4578');
    INSERT INTO Workstations (WSID, Name, Description, WTID)
    VALUES (9886, 'Press 02', '220-630t cold forging press', 'A4578');
    INSERT INTO Workstations (WSID, Name, Description, WTID)
    VALUES (9847, 'Press 03', '220-630t precision cold forging press', 'A4588');
    INSERT INTO Workstations (WSID, Name, Description, WTID)
    VALUES (9855, 'Press 04', '160-1000t precision cold forging press', 'A4588');
    INSERT INTO Workstations (WSID, Name, Description, WTID)
    VALUES (8541, 'Rivet 02', 'Rivet station', 'S3271');
    INSERT INTO Workstations (WSID, Name, Description, WTID)
    VALUES (8543, 'Rivet 03', 'Rivet station', 'S3271');
    INSERT INTO Workstations (WSID, Name, Description, WTID)
    VALUES (6814, 'Packaging 01', 'Packaging station', 'K3675');
    INSERT INTO Workstations (WSID, Name, Description, WTID)
    VALUES (6815, 'Packaging 02', 'Packaging station', 'K3675');
    INSERT INTO Workstations (WSID, Name, Description, WTID)
    VALUES (6816, 'Packaging 03', 'Packaging station', 'K3675');
    INSERT INTO Workstations (WSID, Name, Description, WTID)
    VALUES (6821, 'Packaging 04', 'Packaging station', 'K3675');
    INSERT INTO Workstations (WSID, Name, Description, WTID)
    VALUES (6822, 'Packaging 05', 'Packaging station', 'K3676');
    INSERT INTO Workstations (WSID, Name, Description, WTID)
    VALUES (8167, 'Welding 01', 'Spot welding staion', 'D9123');
    INSERT INTO Workstations (WSID, Name, Description, WTID)
    VALUES (8170, 'Welding 02', 'Spot welding staion', 'D9123');
    INSERT INTO Workstations (WSID, Name, Description, WTID)
    VALUES (8171, 'Welding 03', 'Spot welding staion', 'D9123');
    INSERT INTO Workstations (WSID, Name, Description, WTID)
    VALUES (7235, 'Assembly 01', 'Product assembly station', 'T3452');
    INSERT INTO Workstations (WSID, Name, Description, WTID)
    VALUES (7236, 'Assembly 02', 'Product assembly station', 'T3452');
    INSERT INTO Workstations (WSID, Name, Description, WTID)
    VALUES (7238, 'Assembly 03', 'Product assembly station', 'T3452');
    INSERT INTO Workstations (WSID, Name, Description, WTID)
    VALUES (5124, 'Trimming 01', 'Metal trimming station', 'C5637');
    INSERT INTO Workstations (WSID, Name, Description, WTID)
    VALUES (4123, 'Polishing 01', 'Metal polishing station', 'Q3547');
    INSERT INTO Workstations (WSID, Name, Description, WTID)
    VALUES (4124, 'Polishing 02', 'Metal polishing station', 'Q3547');
    INSERT INTO Workstations (WSID, Name, Description, WTID)
    VALUES (4125, 'Polishing 03', 'Metal polishing station', 'Q3547');

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/