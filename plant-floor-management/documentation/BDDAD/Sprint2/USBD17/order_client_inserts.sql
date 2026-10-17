-- -----------------------------------------------------
-- Inserções de Dados Ajustadas
-- -----------------------------------------------------
BEGIN
    -- Transaction Start

    -- Inserir ProductFamily
    INSERT INTO ProductFamily (PFID, Name) VALUES (1, 'Family 1');
    INSERT INTO ProductFamily (PFID, Name) VALUES (2, 'Family 2');
    INSERT INTO ProductFamily (PFID, Name) VALUES (3, 'Family 3');
    INSERT INTO ProductFamily (PFID, Name) VALUES (4, 'Family 4');
    INSERT INTO ProductFamily (PFID, Name) VALUES (5, 'Family 5');
    INSERT INTO ProductFamily (PFID, Name) VALUES (6, 'Family 6');
    INSERT INTO ProductFamily (PFID, Name) VALUES (7, 'Family 7');
    INSERT INTO ProductFamily (PFID, Name) VALUES (8, 'Family 8');
    INSERT INTO ProductFamily (PFID, Name) VALUES (9, 'Family 9');
    INSERT INTO ProductFamily (PFID, Name) VALUES (10, 'Family 10');
    INSERT INTO ProductFamily (PFID, Name) VALUES (11, 'Family 11');

    -- Inserir Part
    INSERT INTO Part (PartID, Description) VALUES ('ROOT', 'Root Part');
    INSERT INTO Part (PartID, Description) VALUES ('PN12344A21', 'Screw M6 35 mm');
    INSERT INTO Part (PartID, Description) VALUES ('PN52384R50', '300x300 mm 5 mm stainless steel sheet');
    INSERT INTO Part (PartID, Description) VALUES ('PN52384R10', '300x300 mm 1 mm stainless steel sheet');
    INSERT INTO Part (PartID, Description) VALUES ('PN18544A21', 'Rivet 6 mm');
    INSERT INTO Part (PartID, Description) VALUES ('PN18544C21', 'Stainless steel handle model U6');
    INSERT INTO Part (PartID, Description) VALUES ('PN18324C54', 'Stainless steel handle model R12');
    INSERT INTO Part (PartID, Description) VALUES ('PN52384R45', '250x250 mm 5mm stainless steel sheet');
    INSERT INTO Part (PartID, Description) VALUES ('PN52384R12', '250x250 mm 1mm stainless steel sheet');
    INSERT INTO Part (PartID, Description) VALUES ('PN18324C91', 'Stainless steel handle model S26');
    INSERT INTO Part (PartID, Description) VALUES ('PN18324C51', 'Stainless steel handle model R11');
    INSERT INTO Part (PartID, Description) VALUES ('AS12945T22', '5l 22 cm aluminium and teflon non stick pot');
    INSERT INTO Part (PartID, Description) VALUES ('AS12945S22', '5l 22 cm stainless steel pot');
    INSERT INTO Part (PartID, Description) VALUES ('AS12946S22', '5l 22 cm stainless steel pot bottom');
    INSERT INTO Part (PartID, Description) VALUES ('AS12947S22', '22 cm stainless steel lid');
    INSERT INTO Part (PartID, Description) VALUES ('AS12945S20', '3l 20 cm stainless steel pot');
    INSERT INTO Part (PartID, Description) VALUES ('AS12946S20', '3l 20 cm stainless steel pot bottom');
    INSERT INTO Part (PartID, Description) VALUES ('AS12947S20', '20 cm stainless steel lid');
    INSERT INTO Part (PartID, Description) VALUES ('AS12945S17', '2l 17 cm stainless steel pot');
    INSERT INTO Part (PartID, Description) VALUES ('AS12945P17', '2l 17 cm stainless steel souce pan');
    INSERT INTO Part (PartID, Description) VALUES ('AS12945S48', '17 cm stainless steel lid');
    INSERT INTO Part (PartID, Description) VALUES ('AS12945G48', '17 cm glass lid');
    INSERT INTO Part (PartID, Description) VALUES ('IP12945A01', '250 mm 5 mm stailess steel disc');
    INSERT INTO Part (PartID, Description) VALUES ('IP12945A02', '220 mm pot base phase 1');
    INSERT INTO Part (PartID, Description) VALUES ('IP12945A03', '220 mm pot base phase 2');
    INSERT INTO Part (PartID, Description) VALUES ('IP12945A04', '220 mm pot base final');
    INSERT INTO Part (PartID, Description) VALUES ('IP12947A01', '250 mm 1 mm stailess steel disc');
    INSERT INTO Part (PartID, Description) VALUES ('IP12947A02', '220 mm lid pressed');
    INSERT INTO Part (PartID, Description) VALUES ('IP12947A03', '220 mm lid polished');
    INSERT INTO Part (PartID, Description) VALUES ('IP12947A04', '220 mm lid with handle');
    INSERT INTO Part (PartID, Description) VALUES ('IP12945A32', '200 mm pot base phase 1');
    INSERT INTO Part (PartID, Description) VALUES ('IP12945A33', '200 mm pot base phase 2');
    INSERT INTO Part (PartID, Description) VALUES ('IP12945A34', '200 mm pot base final');
    INSERT INTO Part (PartID, Description) VALUES ('IP12947A32', '200 mm lid pressed');
    INSERT INTO Part (PartID, Description) VALUES ('IP12947A33', '200 mm lid polished');
    INSERT INTO Part (PartID, Description) VALUES ('IP12947A34', '200 mm lid with handle');
    INSERT INTO Part (PartID, Description) VALUES ('PN94561L67', 'Coolube 2210XP');

    -- Inserir Product
    INSERT INTO Product (PartID, Name, PFID, PartPartID) VALUES ('AS12945T22', '5l 22 cm aluminium and teflon non stick pot', 1, 'ROOT');
    INSERT INTO Product (PartID, Name, PFID, PartPartID) VALUES ('AS12945S22', '5l 22 cm stainless steel pot', 2, 'ROOT');
    INSERT INTO Product (PartID, Name, PFID, PartPartID) VALUES ('AS12946S22', '5l 22 cm stainless steel pot bottom', 3, 'ROOT');
    INSERT INTO Product (PartID, Name, PFID, PartPartID) VALUES ('AS12947S22', '22 cm stainless steel lid', 4, 'ROOT');
    INSERT INTO Product (PartID, Name, PFID, PartPartID) VALUES ('AS12945S20', '3l 20 cm stainless steel pot', 5, 'ROOT');
    INSERT INTO Product (PartID, Name, PFID, PartPartID) VALUES ('AS12946S20', '3l 20 cm stainless steel pot bottom', 6, 'ROOT');
    INSERT INTO Product (PartID, Name, PFID, PartPartID) VALUES ('AS12947S20', '20 cm stainless steel lid', 7, 'ROOT');
    INSERT INTO Product (PartID, Name, PFID, PartPartID) VALUES ('AS12945S17', '2l 17 cm stainless steel pot', 8, 'ROOT');
    INSERT INTO Product (PartID, Name, PFID, PartPartID) VALUES ('AS12945P17', '2l 17 cm stainless steel souce pan', 9, 'ROOT');
    INSERT INTO Product (PartID, Name, PFID, PartPartID) VALUES ('AS12945S48', '17 cm stainless steel lid', 10, 'ROOT');
    INSERT INTO Product (PartID, Name, PFID, PartPartID) VALUES ('AS12945G48', '17 cm glass lid', 11, 'ROOT');

    -- Inserir WorkstationTypes
    INSERT INTO WorkstationTypes (WTID, Name) VALUES ('A4578', 'Workstation A4578');
    INSERT INTO WorkstationTypes (WTID, Name) VALUES ('A4588', 'Workstation A4588');
    INSERT INTO WorkstationTypes (WTID, Name) VALUES ('A4598', 'Workstation A4598');
    INSERT INTO WorkstationTypes (WTID, Name) VALUES ('C5637', 'Workstation C5637');
    INSERT INTO WorkstationTypes (WTID, Name) VALUES ('S3271', 'Workstation S3271');
    INSERT INTO WorkstationTypes (WTID, Name) VALUES ('T3452', 'Workstation T3452');
    INSERT INTO WorkstationTypes (WTID, Name) VALUES ('K3675', 'Workstation K3675');
    INSERT INTO WorkstationTypes (WTID, Name) VALUES ('D9123', 'Workstation D9123');
    INSERT INTO WorkstationTypes (WTID, Name) VALUES ('Q3547', 'Workstation Q3547');
    INSERT INTO WorkstationTypes (WTID, Name) VALUES ('Q5478', 'Workstation Q5478');

    -- Inserir Operation
    INSERT INTO Operation (OPID, Name) VALUES (5647, 'Disc cutting');
    INSERT INTO Operation (OPID, Name) VALUES (5649, 'Initial pot base pressing');
    INSERT INTO Operation (OPID, Name) VALUES (5651, 'Final pot base pressing');
    INSERT INTO Operation (OPID, Name) VALUES (5653, 'Pot base finishing');
    INSERT INTO Operation (OPID, Name) VALUES (5655, 'Lid pressing');
    INSERT INTO Operation (OPID, Name) VALUES (5657, 'Lid finishing');
    INSERT INTO Operation (OPID, Name) VALUES (5659, 'Pot handles riveting');
    INSERT INTO Operation (OPID, Name) VALUES (5661, 'Lid handle screw');
    INSERT INTO Operation (OPID, Name) VALUES (5663, 'Pot test and packaging');
    INSERT INTO Operation (OPID, Name) VALUES (5665, 'Handle welding');
    INSERT INTO Operation (OPID, Name) VALUES (5667, 'Lid polishing');
    INSERT INTO Operation (OPID, Name) VALUES (5669, 'Pot base polishing');
    INSERT INTO Operation (OPID, Name) VALUES (5671, 'Teflon painting');
    INSERT INTO Operation (OPID, Name) VALUES (5681, 'Initial pan base pressing');
    INSERT INTO Operation (OPID, Name) VALUES (5682, 'Final pan base pressing');
    INSERT INTO Operation (OPID, Name) VALUES (5683, 'Pan base finishing');
    INSERT INTO Operation (OPID, Name) VALUES (5685, 'Handle gluing');
    INSERT INTO Operation (OPID, Name) VALUES (5688, 'Pan test and packaging');

    -- Inserir OperationWorkstationTypes
    INSERT INTO OperationWorkstationTypes (WTID, OPID) VALUES ('A4578', 5647);
    INSERT INTO OperationWorkstationTypes (WTID, OPID) VALUES ('A4588', 5647);
    INSERT INTO OperationWorkstationTypes (WTID, OPID) VALUES ('A4598', 5647);
    INSERT INTO OperationWorkstationTypes (WTID, OPID) VALUES ('A4588', 5649);
    INSERT INTO OperationWorkstationTypes (WTID, OPID) VALUES ('A4598', 5649);
    INSERT INTO OperationWorkstationTypes (WTID, OPID) VALUES ('A4588', 5651);
    INSERT INTO OperationWorkstationTypes (WTID, OPID) VALUES ('A4598', 5651);
    INSERT INTO OperationWorkstationTypes (WTID, OPID) VALUES ('C5637', 5653);
    INSERT INTO OperationWorkstationTypes (WTID, OPID) VALUES ('A4588', 5655);
    INSERT INTO OperationWorkstationTypes (WTID, OPID) VALUES ('A4598', 5655);
    INSERT INTO OperationWorkstationTypes (WTID, OPID) VALUES ('C5637', 5657);
    INSERT INTO OperationWorkstationTypes (WTID, OPID) VALUES ('S3271', 5659);
    INSERT INTO OperationWorkstationTypes (WTID, OPID) VALUES ('T3452', 5661);
    INSERT INTO OperationWorkstationTypes (WTID, OPID) VALUES ('K3675', 5663);
    INSERT INTO OperationWorkstationTypes (WTID, OPID) VALUES ('D9123', 5665);
    INSERT INTO OperationWorkstationTypes (WTID, OPID) VALUES ('Q3547', 5667);
    INSERT INTO OperationWorkstationTypes (WTID, OPID) VALUES ('Q3547', 5669);
    INSERT INTO OperationWorkstationTypes (WTID, OPID) VALUES ('Q5478', 5671);
    INSERT INTO OperationWorkstationTypes (WTID, OPID) VALUES ('A4588', 5681);
    INSERT INTO OperationWorkstationTypes (WTID, OPID) VALUES ('A4598', 5681);
    INSERT INTO OperationWorkstationTypes (WTID, OPID) VALUES ('A4588', 5682);
    INSERT INTO OperationWorkstationTypes (WTID, OPID) VALUES ('A4598', 5682);
    INSERT INTO OperationWorkstationTypes (WTID, OPID) VALUES ('C5637', 5683);
    INSERT INTO OperationWorkstationTypes (WTID, OPID) VALUES ('D9123', 5685);
    INSERT INTO OperationWorkstationTypes (WTID, OPID) VALUES ('K3675', 5688);

    -- Inserir BOO
    INSERT INTO BOO (BOOID, PartPartID) VALUES ('BO01', 'AS12946S22');
    INSERT INTO BOO (BOOID, PartPartID) VALUES ('BO02', 'AS12947S22');
    INSERT INTO BOO (BOOID, PartPartID) VALUES ('BO03', 'AS12945S22');
    INSERT INTO BOO (BOOID, PartPartID) VALUES ('BO04', 'AS12946S20');
    INSERT INTO BOO (BOOID, PartPartID) VALUES ('BO05', 'AS12947S20');
    INSERT INTO BOO (BOOID, PartPartID) VALUES ('BO06', 'AS12945S20');

    -- Inserir BOO_Operation
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (5647, 'BO01');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (5649, 'BO01');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (5651, 'BO01');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (5653, 'BO01');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (5659, 'BO01');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (5647, 'BO02');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (5655, 'BO02');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (5657, 'BO02');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (5661, 'BO02');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (5667, 'BO02');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (5663, 'BO03');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (5647, 'BO04');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (5649, 'BO04');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (5651, 'BO04');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (5653, 'BO04');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (5659, 'BO04');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (5647, 'BO05');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (5655, 'BO05');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (5657, 'BO05');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (5661, 'BO05');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (5667, 'BO05');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (5663, 'BO06');

    -- Inserir PartIN
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('PN52384R50', 5647, 'BO01', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('IP12945A01', 5649, 'BO01', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('PN94561L67', 5649, 'BO01', 5);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('IP12945A02', 5651, 'BO01', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('PN94561L67', 5651, 'BO01', 5);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('IP12945A03', 5653, 'BO01', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('IP12945A04', 5659, 'BO01', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('PN18544C21', 5659, 'BO01', 2);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('PN52384R10', 5647, 'BO02', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('IP12947A01', 5655, 'BO02', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('PN94561L67', 5655, 'BO02', 5);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('IP12947A02', 5657, 'BO02', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('IP12947A03', 5661, 'BO02', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('PN18324C54', 5661, 'BO02', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('IP12947A04', 5667, 'BO02', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('AS12947S22', 5663, 'BO03', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('AS12946S22', 5663, 'BO03', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('PN52384R50', 5647, 'BO04', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('IP12945A01', 5649, 'BO04', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('PN94561L67', 5649, 'BO04', 5);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('IP12945A32', 5651, 'BO04', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('PN94561L67', 5651, 'BO04', 5);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('IP12945A33', 5653, 'BO04', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('IP12945A04', 5659, 'BO04', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('PN18544C21', 5659, 'BO04', 2);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('PN52384R10', 5647, 'BO05', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('IP12947A01', 5655, 'BO05', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('PN94561L67', 5655, 'BO05', 5);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('IP12947A32', 5657, 'BO05', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('IP12947A33', 5661, 'BO05', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('PN18324C51', 5661, 'BO05', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('IP12947A34', 5667, 'BO05', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('AS12946S20', 5663, 'BO06', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('AS12947S20', 5663, 'BO06', 1);

    -- Inserir ClientType
    INSERT INTO ClientType (CID, Type) VALUES ('C1', 'Individual');
    INSERT INTO ClientType (CID, Type) VALUES ('C2', 'Company');

    -- Inserir Country
    INSERT INTO Country (CountryCode, Name) VALUES ('PT', 'Portugal');
    INSERT INTO Country (CountryCode, Name) VALUES ('CZ', 'Czechia');

    -- Inserir Client
    INSERT INTO Client (
        IDClient,
        Name,
        NIF,
        CID,
        Email,
        Phone,
        Status
    ) VALUES (
        456,
        'Carvalho & Carvalho, Lda',
        'PT501245987',
        'C2',
        'idont@care.com',
        3518340500,
        1
    );
    INSERT INTO Client (
        IDClient,
        Name,
        NIF,
        CID,
        Email,
        Phone,
        Status
    ) VALUES (
        785,
        'Tudo para a casa, Lda',
        'PT501245488',
        'C2',
        'me@neither.com',
        3518340500,
        1
    );
    INSERT INTO Client (
        IDClient,
        Name,
        NIF,
        CID,
        Email,
        Phone,
        Status
    ) VALUES (
        657,
        'Sair de Cena',
        'PT501242417',
        'C2',
        'some@email.com',
        3518340500,
        1
    );
    INSERT INTO Client (
        IDClient,
        Name,
        NIF,
        CID,
        Email,
        Phone,
        Status
    ) VALUES (
        348,
        'U Fleku',
        'CZ6451237810',
        'C2',
        'some.random@email.cz',
        4201234567,
        1
    );

    -- Inserir Address
    INSERT INTO Address (
        Address,
        ZIP,
        Town,
        CountryCode,
        ClientNIF
    ) VALUES (
        'Tv. Augusto Lessa 23',
        '4200-047',
        'Porto',
        'PT',
        'PT501245987'
    );
    INSERT INTO Address (
        Address,
        ZIP,
        Town,
        CountryCode,
        ClientNIF
    ) VALUES (
        'R. Dr. Barros 93',
        '4465-219',
        'São Mamede de Infesta',
        'PT',
        'PT501245488'
    );
    INSERT INTO Address (
        Address,
        ZIP,
        Town,
        CountryCode,
        ClientNIF
    ) VALUES (
        'EDIFICIO CRISTAL lj18, R. António Correia de Carvalho 88',
        '4400-023',
        'Vila Nova de Gaia',
        'PT',
        'PT501242417'
    );
    INSERT INTO Address (
        Address,
        ZIP,
        Town,
        CountryCode,
        ClientNIF
    ) VALUES (
        'Křemencova 11',
        '110 00',
        'Nové Město',
        'CZ',
        'CZ6451237810'
    );

    -- Inserir "Order"
    INSERT INTO "Order" (
        OID,
        ClientNIF,
        DateOrder,
        DateDelivery
    ) VALUES (
        1,
        'PT501245488',
        TO_DATE('2024-09-15', 'YYYY-MM-DD'),
        TO_DATE('2024-09-23', 'YYYY-MM-DD')
    );
    INSERT INTO "Order" (
        OID,
        ClientNIF,
        DateOrder,
        DateDelivery
    ) VALUES (
        2,
        'PT501242417',
        TO_DATE('2024-09-15', 'YYYY-MM-DD'),
        TO_DATE('2024-09-25', 'YYYY-MM-DD')
    );
    INSERT INTO "Order" (
        OID,
        ClientNIF,
        DateOrder,
        DateDelivery
    ) VALUES (
        3,
        'CZ6451237810',
        TO_DATE('2024-09-15', 'YYYY-MM-DD'),
        TO_DATE('2024-09-25', 'YYYY-MM-DD')
    );
    INSERT INTO "Order" (
        OID,
        ClientNIF,
        DateOrder,
        DateDelivery
    ) VALUES (
        4,
        'PT501245488',
        TO_DATE('2024-09-18', 'YYYY-MM-DD'),
        TO_DATE('2024-09-25', 'YYYY-MM-DD')
    );
    INSERT INTO "Order" (
        OID,
        ClientNIF,
        DateOrder,
        DateDelivery
    ) VALUES (
        5,
        'PT501242417',
        TO_DATE('2024-09-18', 'YYYY-MM-DD'),
        TO_DATE('2024-09-25', 'YYYY-MM-DD')
    );
    INSERT INTO "Order" (
        OID,
        ClientNIF,
        DateOrder,
        DateDelivery
    ) VALUES (
        6,
        'CZ6451237810',
        TO_DATE('2024-09-18', 'YYYY-MM-DD'),
        TO_DATE('2024-09-26', 'YYYY-MM-DD')
    );
    INSERT INTO "Order" (
        OID,
        ClientNIF,
        DateOrder,
        DateDelivery
    ) VALUES (
        7,
        'PT501245987',
        TO_DATE('2024-09-21', 'YYYY-MM-DD'),
        TO_DATE('2024-09-26', 'YYYY-MM-DD')
    );

    -- Inserir OrderProduct
    INSERT INTO OrderProduct (
        OID,
        ProductPartD,
        Quantity
    ) VALUES (
        1,
        'AS12945S22',
        5
    );
    INSERT INTO OrderProduct (
        OID,
        ProductPartD,
        Quantity
    ) VALUES (
        1,
        'AS12945S20',
        15
    );
    INSERT INTO OrderProduct (
        OID,
        ProductPartD,
        Quantity
    ) VALUES (
        2,
        'AS12945S22',
        10
    );
    INSERT INTO OrderProduct (
        OID,
        ProductPartD,
        Quantity
    ) VALUES (
        2,
        'AS12945P17',
        20
    );
    INSERT INTO OrderProduct (
        OID,
        ProductPartD,
        Quantity
    ) VALUES (
        3,
        'AS12945S22',
        10
    );
    INSERT INTO OrderProduct (
        OID,
        ProductPartD,
        Quantity
    ) VALUES (
        3,
        'AS12945S20',
        10
    );
    INSERT INTO OrderProduct (
        OID,
        ProductPartD,
        Quantity
    ) VALUES (
        4,
        'AS12945S20',
        24
    );
    INSERT INTO OrderProduct (
        OID,
        ProductPartD,
        Quantity
    ) VALUES (
        4,
        'AS12945S22',
        16
    );
    INSERT INTO OrderProduct (
        OID,
        ProductPartD,
        Quantity
    ) VALUES (
        4,
        'AS12945S17',
        8
    );
    INSERT INTO OrderProduct (
        OID,
        ProductPartD,
        Quantity
    ) VALUES (
        5,
        'AS12945S22',
        12
    );
    INSERT INTO OrderProduct (
        OID,
        ProductPartD,
        Quantity
    ) VALUES (
        6,
        'AS12945S17',
        8
    );
    INSERT INTO OrderProduct (
        OID,
        ProductPartD,
        Quantity
    ) VALUES (
        6,
        'AS12945P17',
        16
    );
    INSERT INTO OrderProduct (
        OID,
        ProductPartD,
        Quantity
    ) VALUES (
        7,
        'AS12945S22',
        8
    );

    -- Inserir ProductionOrder
    -- Ajustando para utilizar PartIDs válidos existentes em OrderProduct
    INSERT INTO ProductionOrder (
        PrOrID,
        OID,
        PartID
    ) VALUES (
        'PO100',
        1,
        'AS12945S22'
    );
    INSERT INTO ProductionOrder (
        PrOrID,
        OID,
        PartID
    ) VALUES (
        'PO101',
        1,
        'AS12945S20'
    );
    INSERT INTO ProductionOrder (
        PrOrID,
        OID,
        PartID
    ) VALUES (
        'PO102',
        2,
        'AS12945S22'
    );
    INSERT INTO ProductionOrder (
        PrOrID,
        OID,
        PartID
    ) VALUES (
        'PO103',
        2,
        'AS12945P17'
    );
    INSERT INTO ProductionOrder (
        PrOrID,
        OID,
        PartID
    ) VALUES (
        'PO104',
        3,
        'AS12945S22'
    );
    INSERT INTO ProductionOrder (
        PrOrID,
        OID,
        PartID
    ) VALUES (
        'PO105',
        3,
        'AS12945S20'
    );
    INSERT INTO ProductionOrder (
        PrOrID,
        OID,
        PartID
    ) VALUES (
        'PO106',
        4,
        'AS12945S20'
    );
    INSERT INTO ProductionOrder (
        PrOrID,
        OID,
        PartID
    ) VALUES (
        'PO107',
        4,
        'AS12945S22'
    );
    INSERT INTO ProductionOrder (
        PrOrID,
        OID,
        PartID
    ) VALUES (
        'PO108',
        4,
        'AS12945S17'
    );
    INSERT INTO ProductionOrder (
        PrOrID,
        OID,
        PartID
    ) VALUES (
        'PO109',
        5,
        'AS12945S22'
    );
    INSERT INTO ProductionOrder (
        PrOrID,
        OID,
        PartID
    ) VALUES (
        'PO110',
        6,
        'AS12945S17'
    );
    INSERT INTO ProductionOrder (
        PrOrID,
        OID,
        PartID
    ) VALUES (
        'PO111',
        6,
        'AS12945P17'
    );
    INSERT INTO ProductionOrder (
        PrOrID,
        OID,
        PartID
    ) VALUES (
        'PO112',
        7,
        'AS12945S22'
    );

    -- Commit das transações
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
-- Transaction End
