BEGIN
    -- ----------------------------------------
    -- 1. Insert Clients
    -- ----------------------------------------
    INSERT INTO Client (IDClient, Name, NIF, CID, Email, Phone, Status)
    VALUES (456, 'Carvalho & Carvalho, Lda', 'PT501245987', 'C2', 'idont@care.com', '003518340500', 1);
    
    INSERT INTO Client (IDClient, Name, NIF, CID, Email, Phone, Status)
    VALUES (785, 'Tudo para a casa, Lda', 'PT501245488', 'C2', 'me@neither.com', '003518340500', 1);
    
    INSERT INTO Client (IDClient, Name, NIF, CID, Email, Phone, Status)
    VALUES (657, 'Sair de Cena', 'PT501242417', 'C2', 'some@email.com', '003518340500', 1);
    
    INSERT INTO Client (IDClient, Name, NIF, CID, Email, Phone, Status)
    VALUES (348, 'U Fleku', 'CZ6451237810', 'C2', 'some.random@email.cz', '004201234567', 1);

    -- ----------------------------------------
    -- 2. Insert Addresses
    -- ----------------------------------------
    INSERT INTO Address (Address, ZIP, Town, CountryCode, ClientNIF)
    VALUES ('Tv. Augusto Lessa 23', '4200-047', 'Porto', 'PT', 'PT501245987');
    
    INSERT INTO Address (Address, ZIP, Town, CountryCode, ClientNIF)
    VALUES ('R. Dr. Barros 93', '4465-219', 'São Mamede de Infesta', 'PT', 'PT501245488');
    
    INSERT INTO Address (Address, ZIP, Town, CountryCode, ClientNIF)
    VALUES ('EDIFICIO CRISTAL lj18, R. António Correia de Carvalho 88', '4400-023', 'Vila Nova de Gaia', 'PT', 'PT501242417');
    
    INSERT INTO Address (Address, ZIP, Town, CountryCode, ClientNIF)
    VALUES ('Křemencova 11', '110 00', 'Nové Město', 'CZ', 'CZ6451237810');

    -- ----------------------------------------
    -- 3. Insert Workstations
    -- ----------------------------------------
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
    VALUES (8167, 'Welding 01', 'Spot welding station', 'D9123');
    
    INSERT INTO Workstations (WSID, Name, Description, WTID)
    VALUES (8170, 'Welding 02', 'Spot welding station', 'D9123');
    
    INSERT INTO Workstations (WSID, Name, Description, WTID)
    VALUES (8171, 'Welding 03', 'Spot welding station', 'D9123');
    
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

    -- ----------------------------------------
    -- 4. Insert Parts, External/Internal Parts, Components, Products, Intermediate Products, BOO
    -- ----------------------------------------
    
    -- Insert Parts and Related Entities
    -- Example for each part; replicate as needed for all parts

    -- Part: PN12344A21
    INSERT INTO Part (PartID, Description) VALUES ('PN12344A21', 'Screw M6 35 mm');
    INSERT INTO ExternalPart (PartID, Stock) VALUES ('PN12344A21', 10);
    INSERT INTO Component (PartID) VALUES ('PN12344A21');

    -- Part: PN52384R50
    INSERT INTO Part (PartID, Description) VALUES ('PN52384R50', '300x300 mm 5 mm stainless steel sheet');
    INSERT INTO ExternalPart (PartID, Stock) VALUES ('PN52384R50', 10);
    INSERT INTO Component (PartID) VALUES ('PN52384R50');

    -- Part: PN52384R10
    INSERT INTO Part (PartID, Description) VALUES ('PN52384R10', '300x300 mm 1 mm stainless steel sheet');
    INSERT INTO ExternalPart (PartID, Stock) VALUES ('PN52384R10', 10);
    INSERT INTO Component (PartID) VALUES ('PN52384R10');

    -- Part: PN18544A21
    INSERT INTO Part (PartID, Description) VALUES ('PN18544A21', 'Rivet 6 mm');
    INSERT INTO ExternalPart (PartID, Stock) VALUES ('PN18544A21', 10);
    INSERT INTO Component (PartID) VALUES ('PN18544A21');

    -- Part: PN18544C21
    INSERT INTO Part (PartID, Description) VALUES ('PN18544C21', 'Stainless steel handle model U6');
    INSERT INTO ExternalPart (PartID, Stock) VALUES ('PN18544C21', 10);
    INSERT INTO Component (PartID) VALUES ('PN18544C21');

    -- Part: PN18324C54
    INSERT INTO Part (PartID, Description) VALUES ('PN18324C54', 'Stainless steel handle model R12');
    INSERT INTO ExternalPart (PartID, Stock) VALUES ('PN18324C54', 10);
    INSERT INTO Component (PartID) VALUES ('PN18324C54');

    -- Part: PN52384R45
    INSERT INTO Part (PartID, Description) VALUES ('PN52384R45', '250x250 mm 5mm stainless steel sheet');
    INSERT INTO ExternalPart (PartID, Stock) VALUES ('PN52384R45', 10);
    INSERT INTO Component (PartID) VALUES ('PN52384R45');

    -- Part: PN52384R12
    INSERT INTO Part (PartID, Description) VALUES ('PN52384R12', '250x250 mm 1mm stainless steel sheet');
    INSERT INTO ExternalPart (PartID, Stock) VALUES ('PN52384R12', 10);
    INSERT INTO Component (PartID) VALUES ('PN52384R12');

    -- Part: PN18324C91
    INSERT INTO Part (PartID, Description) VALUES ('PN18324C91', 'Stainless steel handle model S26');
    INSERT INTO ExternalPart (PartID, Stock) VALUES ('PN18324C91', 10);
    INSERT INTO Component (PartID) VALUES ('PN18324C91');

    -- Part: PN18324C51
    INSERT INTO Part (PartID, Description) VALUES ('PN18324C51', 'Stainless steel handle model R11');
    INSERT INTO ExternalPart (PartID, Stock) VALUES ('PN18324C51', 10);
    INSERT INTO Component (PartID) VALUES ('PN18324C51');

    -- Part: AS12945T22
    INSERT INTO Part (PartID, Description) VALUES ('AS12945T22', '5l 22 cm aluminium and teflon non stick pot');
    INSERT INTO InternalPart (PartID) VALUES ('AS12945T22');
    INSERT INTO Product (PartID, Name, PFID) VALUES ('AS12945T22', 'La Belle 22 5l pot', 130);

    -- Part: AS12945S22
    INSERT INTO Part (PartID, Description) VALUES ('AS12945S22', '5l 22 cm stainless steel pot');
    INSERT INTO InternalPart (PartID) VALUES ('AS12945S22');
    INSERT INTO Product (PartID, Name, PFID) VALUES ('AS12945S22', 'Pro 22 5l pot', 125);

    -- Part: AS12946S22
    INSERT INTO Part (PartID, Description) VALUES ('AS12946S22', '5l 22 cm stainless steel pot bottom');
    INSERT INTO InternalPart (PartID) VALUES ('AS12946S22');
    INSERT INTO Product (PartID, Name, PFID) VALUES ('AS12946S22', 'Pro 22 5l pot bottom', 125);

    -- Part: AS12947S22
    INSERT INTO Part (PartID, Description) VALUES ('AS12947S22', '22 cm stainless steel lid');
    INSERT INTO InternalPart (PartID) VALUES ('AS12947S22');
    INSERT INTO Product (PartID, Name, PFID) VALUES ('AS12947S22', 'Pro 22 lid', 145);

    -- Part: AS12945S20
    INSERT INTO Part (PartID, Description) VALUES ('AS12945S20', '3l 20 cm stainless steel pot');
    INSERT INTO InternalPart (PartID) VALUES ('AS12945S20');
    INSERT INTO Product (PartID, Name, PFID) VALUES ('AS12945S20', 'Pro 20 3l pot', 125);

    -- Part: AS12946S20
    INSERT INTO Part (PartID, Description) VALUES ('AS12946S20', '3l 20 cm stainless steel pot bottom');
    INSERT INTO InternalPart (PartID) VALUES ('AS12946S20');
    INSERT INTO Product (PartID, Name, PFID) VALUES ('AS12946S20', 'Pro 20 3l pot bottom', 125);

    -- Part: AS12947S20
    INSERT INTO Part (PartID, Description) VALUES ('AS12947S20', '20 cm stainless steel lid');
    INSERT INTO InternalPart (PartID) VALUES ('AS12947S20');
    INSERT INTO Product (PartID, Name, PFID) VALUES ('AS12947S20', 'Pro 20 lid', 145);

    -- Part: AS12945S17
    INSERT INTO Part (PartID, Description) VALUES ('AS12945S17', '2l 17 cm stainless steel pot');
    INSERT INTO InternalPart (PartID) VALUES ('AS12945S17');
    INSERT INTO Product (PartID, Name, PFID) VALUES ('AS12945S17', 'Pro 17 2l pot', 125);

    -- Part: AS12945P17
    INSERT INTO Part (PartID, Description) VALUES ('AS12945P17', '2l 17 cm stainless steel sauce pan');
    INSERT INTO InternalPart (PartID) VALUES ('AS12945P17');
    INSERT INTO Product (PartID, Name, PFID) VALUES ('AS12945P17', 'Pro 17 2l sauce pan', 132);

    -- Part: AS12945S48
    INSERT INTO Part (PartID, Description) VALUES ('AS12945S48', '17 cm stainless steel lid');
    INSERT INTO InternalPart (PartID) VALUES ('AS12945S48');
    INSERT INTO Product (PartID, Name, PFID) VALUES ('AS12945S48', 'Pro 17 lid', 145);

    -- Part: AS12945G48
    INSERT INTO Part (PartID, Description) VALUES ('AS12945G48', '17 cm glass lid');
    INSERT INTO InternalPart (PartID) VALUES ('AS12945G48');
    INSERT INTO Product (PartID, Name, PFID) VALUES ('AS12945G48', 'Pro Clear 17 lid', 146);

    -- Part: IP12945A01
    INSERT INTO Part (PartID, Description) VALUES ('IP12945A01', '250 mm 5 mm stainless steel disc');
    INSERT INTO InternalPart (PartID) VALUES ('IP12945A01');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12945A01');

    -- Part: IP12945A02
    INSERT INTO Part (PartID, Description) VALUES ('IP12945A02', '220 mm pot base phase 1');
    INSERT INTO InternalPart (PartID) VALUES ('IP12945A02');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12945A02');

    -- Part: IP12945A03
    INSERT INTO Part (PartID, Description) VALUES ('IP12945A03', '220 mm pot base phase 2');
    INSERT INTO InternalPart (PartID) VALUES ('IP12945A03');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12945A03');

    -- Part: IP12945A04
    INSERT INTO Part (PartID, Description) VALUES ('IP12945A04', '220 mm pot base final');
    INSERT INTO InternalPart (PartID) VALUES ('IP12945A04');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12945A04');

    -- Part: IP12947A01
    INSERT INTO Part (PartID, Description) VALUES ('IP12947A01', '250 mm 1 mm stainless steel disc');
    INSERT INTO InternalPart (PartID) VALUES ('IP12947A01');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12947A01');

    -- Part: IP12947A02
    INSERT INTO Part (PartID, Description) VALUES ('IP12947A02', '220 mm lid pressed');
    INSERT INTO InternalPart (PartID) VALUES ('IP12947A02');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12947A02');

    -- Part: IP12947A03
    INSERT INTO Part (PartID, Description) VALUES ('IP12947A03', '220 mm lid polished');
    INSERT INTO InternalPart (PartID) VALUES ('IP12947A03');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12947A03');

    -- Part: IP12947A04
    INSERT INTO Part (PartID, Description) VALUES ('IP12947A04', '220 mm lid with handle');
    INSERT INTO InternalPart (PartID) VALUES ('IP12947A04');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12947A04');

    -- Part: IP12945A32
    INSERT INTO Part (PartID, Description) VALUES ('IP12945A32', '200 mm pot base phase 1');
    INSERT INTO InternalPart (PartID) VALUES ('IP12945A32');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12945A32');

    -- Part: IP12945A33
    INSERT INTO Part (PartID, Description) VALUES ('IP12945A33', '200 mm pot base phase 2');
    INSERT INTO InternalPart (PartID) VALUES ('IP12945A33');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12945A33');

    -- Part: IP12945A34
    INSERT INTO Part (PartID, Description) VALUES ('IP12945A34', '200 mm pot base final');
    INSERT INTO InternalPart (PartID) VALUES ('IP12945A34');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12945A34');

    -- Part: IP12947A32
    INSERT INTO Part (PartID, Description) VALUES ('IP12947A32', '200 mm lid pressed');
    INSERT INTO InternalPart (PartID) VALUES ('IP12947A32');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12947A32');

    -- Part: IP12947A33
    INSERT INTO Part (PartID, Description) VALUES ('IP12947A33', '200 mm lid polished');
    INSERT INTO InternalPart (PartID) VALUES ('IP12947A33');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12947A33');

    -- Part: IP12947A34
    INSERT INTO Part (PartID, Description) VALUES ('IP12947A34', '200 mm lid with handle');
    INSERT INTO InternalPart (PartID) VALUES ('IP12947A34');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12947A34');

    -- Part: PN94561L67
    INSERT INTO Part (PartID, Description) VALUES ('PN94561L67', 'Coolube 2210XP');
    INSERT INTO ExternalPart (PartID, Stock) VALUES ('PN94561L67', 10);
    INSERT INTO RawMaterial (PartID, UnitID) VALUES ('PN94561L67', 1);

    -- ----------------------------------------
    -- 5. Insert BOO (Business Object Operations)
    -- ----------------------------------------
    INSERT INTO BOO (BOOID, PartID) VALUES ('AS12946S22', 'AS12946S22');
    INSERT INTO BOO (BOOID, PartID) VALUES ('AS12947S22', 'AS12947S22');
    INSERT INTO BOO (BOOID, PartID) VALUES ('AS12945S22', 'AS12945S22');
    INSERT INTO BOO (BOOID, PartID) VALUES ('AS12946S20', 'AS12946S20');
    INSERT INTO BOO (BOOID, PartID) VALUES ('AS12947S20', 'AS12947S20');
    INSERT INTO BOO (BOOID, PartID) VALUES ('AS12945S20', 'AS12945S20');

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
