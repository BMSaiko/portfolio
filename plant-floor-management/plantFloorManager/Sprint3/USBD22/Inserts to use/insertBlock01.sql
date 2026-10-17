BEGIN
    -- ----------------------------------------
    -- 1. Insert Client Types
    -- ----------------------------------------
    INSERT INTO ClientType (CID, Type) VALUES ('C1', 'Individual');
    INSERT INTO ClientType (CID, Type) VALUES ('C2', 'Company');

    -- ----------------------------------------
    -- 2. Insert Countries
    -- ----------------------------------------
    INSERT INTO Country (CountryCode, Name) VALUES ('PT', 'Portugal');
    INSERT INTO Country (CountryCode, Name) VALUES ('CZ', 'Czechia');

    -- ----------------------------------------
    -- 3. Insert Product Families
    -- ----------------------------------------
    INSERT INTO ProductFamily (PFID, Name) VALUES (130, 'Family 130');
    INSERT INTO ProductFamily (PFID, Name) VALUES (125, 'Family 125');
    INSERT INTO ProductFamily (PFID, Name) VALUES (145, 'Family 145');
    INSERT INTO ProductFamily (PFID, Name) VALUES (132, 'Family 132');
    INSERT INTO ProductFamily (PFID, Name) VALUES (146, 'Family 146');

    -- ----------------------------------------
    -- 4. Insert Workstation Types
    -- ----------------------------------------
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

    -- ----------------------------------------
    -- 5. Insert Units of Measurement
    -- ----------------------------------------
    INSERT INTO Unit (UnitID, Name) VALUES (1, 'kg');
    INSERT INTO Unit (UnitID, Name) VALUES (2, 'l');
    INSERT INTO Unit (UnitID, Name) VALUES (3, 'g');
    INSERT INTO Unit (UnitID, Name) VALUES (4, 'unit');

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
