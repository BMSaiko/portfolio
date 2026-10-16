-- Inserções geradas automaticamente pelo script Python

BEGIN
    -- Inserir ProductFamily
    INSERT INTO ProductFamily (PFID, Name) VALUES (130, 'Family 130');
    INSERT INTO ProductFamily (PFID, Name) VALUES (125, 'Family 125');
    INSERT INTO ProductFamily (PFID, Name) VALUES (145, 'Family 145');
    INSERT INTO ProductFamily (PFID, Name) VALUES (132, 'Family 132');
    INSERT INTO ProductFamily (PFID, Name) VALUES (146, 'Family 146');

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

    -- Inserir OperationType
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

    -- Inserir WSTypes_OperationTypes
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('A4578', 5647);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('A4588', 5647);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('A4598', 5647);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('A4588', 5649);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('A4598', 5649);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('A4588', 5651);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('A4598', 5651);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('C5637', 5653);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('A4588', 5655);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('A4598', 5655);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('C5637', 5657);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('S3271', 5659);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('T3452', 5661);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('K3675', 5663);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('D9123', 5665);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('Q3547', 5667);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('Q3547', 5669);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('Q5478', 5671);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('A4588', 5681);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('A4598', 5681);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('A4588', 5682);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('A4598', 5682);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('C5637', 5683);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('D9123', 5685);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('K3675', 5688);

    -- Inserir Part
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

    -- Inserir Component
    INSERT INTO Component (PartID) VALUES ('PN12344A21');
    INSERT INTO Component (PartID) VALUES ('PN52384R50');
    INSERT INTO Component (PartID) VALUES ('PN52384R10');
    INSERT INTO Component (PartID) VALUES ('PN18544A21');
    INSERT INTO Component (PartID) VALUES ('PN18544C21');
    INSERT INTO Component (PartID) VALUES ('PN18324C54');
    INSERT INTO Component (PartID) VALUES ('PN52384R45');
    INSERT INTO Component (PartID) VALUES ('PN52384R12');
    INSERT INTO Component (PartID) VALUES ('PN18324C91');
    INSERT INTO Component (PartID) VALUES ('PN18324C51');

    -- Inserir Product
    INSERT INTO Product (Name, PFID, PartID) VALUES ('La Belle 22 5l pot', 130, 'AS12945T22');
    INSERT INTO Product (Name, PFID, PartID) VALUES ('Pro 22 5l pot', 125, 'AS12945S22');
    INSERT INTO Product (Name, PFID, PartID) VALUES ('Pro 22 5l pot bottom', 125, 'AS12946S22');
    INSERT INTO Product (Name, PFID, PartID) VALUES ('Pro 22 lid', 145, 'AS12947S22');
    INSERT INTO Product (Name, PFID, PartID) VALUES ('Pro 20 3l pot', 125, 'AS12945S20');
    INSERT INTO Product (Name, PFID, PartID) VALUES ('Pro 20 3l pot bottom', 125, 'AS12946S20');
    INSERT INTO Product (Name, PFID, PartID) VALUES ('Pro 20 lid', 145, 'AS12947S20');
    INSERT INTO Product (Name, PFID, PartID) VALUES ('Pro 17 2l pot', 125, 'AS12945S17');
    INSERT INTO Product (Name, PFID, PartID) VALUES ('Pro 17 2l sauce pan', 132, 'AS12945P17');
    INSERT INTO Product (Name, PFID, PartID) VALUES ('Pro 17 lid', 145, 'AS12945S48');
    INSERT INTO Product (Name, PFID, PartID) VALUES ('Pro Clear 17 lid', 146, 'AS12945G48');

    -- Inserir IntermediateProduct
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12945A01');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12945A02');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12945A03');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12945A04');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12947A01');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12947A02');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12947A03');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12947A04');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12945A32');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12945A33');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12945A34');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12947A32');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12947A33');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12947A34');

    -- Inserir RawMaterial
    INSERT INTO RawMaterial (PartID) VALUES ('PN94561L67');

    -- Inserir BOO
    INSERT INTO BOO (BOOID, PartID) VALUES ('AS12946S22', 'AS12946S22');
    INSERT INTO BOO (BOOID, PartID) VALUES ('AS12947S22', 'AS12947S22');
    INSERT INTO BOO (BOOID, PartID) VALUES ('AS12945S22', 'AS12945S22');
    INSERT INTO BOO (BOOID, PartID) VALUES ('AS12946S20', 'AS12946S20');
    INSERT INTO BOO (BOOID, PartID) VALUES ('AS12947S20', 'AS12947S20');
    INSERT INTO BOO (BOOID, PartID) VALUES ('AS12945S20', 'AS12945S20');

    -- Inserir Operation (sem NextOPID)
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, NextOPID) VALUES (100, (SELECT Description FROM OperationType WHERE OTID = 5647), 5647, NULL);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, NextOPID) VALUES (103, (SELECT Description FROM OperationType WHERE OTID = 5649), 5649, NULL);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, NextOPID) VALUES (112, (SELECT Description FROM OperationType WHERE OTID = 5651), 5651, NULL);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, NextOPID) VALUES (114, (SELECT Description FROM OperationType WHERE OTID = 5653), 5653, NULL);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, NextOPID) VALUES (115, (SELECT Description FROM OperationType WHERE OTID = 5659), 5659, NULL);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, NextOPID) VALUES (120, (SELECT Description FROM OperationType WHERE OTID = 5647), 5647, NULL);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, NextOPID) VALUES (121, (SELECT Description FROM OperationType WHERE OTID = 5655), 5655, NULL);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, NextOPID) VALUES (122, (SELECT Description FROM OperationType WHERE OTID = 5657), 5657, NULL);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, NextOPID) VALUES (123, (SELECT Description FROM OperationType WHERE OTID = 5661), 5661, NULL);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, NextOPID) VALUES (124, (SELECT Description FROM OperationType WHERE OTID = 5667), 5667, NULL);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, NextOPID) VALUES (130, (SELECT Description FROM OperationType WHERE OTID = 5663), 5663, NULL);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, NextOPID) VALUES (150, (SELECT Description FROM OperationType WHERE OTID = 5647), 5647, NULL);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, NextOPID) VALUES (151, (SELECT Description FROM OperationType WHERE OTID = 5649), 5649, NULL);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, NextOPID) VALUES (152, (SELECT Description FROM OperationType WHERE OTID = 5651), 5651, NULL);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, NextOPID) VALUES (153, (SELECT Description FROM OperationType WHERE OTID = 5653), 5653, NULL);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, NextOPID) VALUES (154, (SELECT Description FROM OperationType WHERE OTID = 5659), 5659, NULL);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, NextOPID) VALUES (160, (SELECT Description FROM OperationType WHERE OTID = 5647), 5647, NULL);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, NextOPID) VALUES (161, (SELECT Description FROM OperationType WHERE OTID = 5655), 5655, NULL);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, NextOPID) VALUES (162, (SELECT Description FROM OperationType WHERE OTID = 5657), 5657, NULL);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, NextOPID) VALUES (163, (SELECT Description FROM OperationType WHERE OTID = 5661), 5661, NULL);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, NextOPID) VALUES (164, (SELECT Description FROM OperationType WHERE OTID = 5667), 5667, NULL);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID, NextOPID) VALUES (170, (SELECT Description FROM OperationType WHERE OTID = 5663), 5663, NULL);

    -- Inserir BOO_Operation
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (100, 'AS12946S22');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (103, 'AS12946S22');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (112, 'AS12946S22');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (114, 'AS12946S22');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (115, 'AS12946S22');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (120, 'AS12947S22');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (121, 'AS12947S22');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (122, 'AS12947S22');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (123, 'AS12947S22');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (124, 'AS12947S22');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (130, 'AS12945S22');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (150, 'AS12946S20');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (151, 'AS12946S20');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (152, 'AS12946S20');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (153, 'AS12946S20');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (154, 'AS12946S20');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (160, 'AS12947S20');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (161, 'AS12947S20');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (162, 'AS12947S20');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (163, 'AS12947S20');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (164, 'AS12947S20');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (170, 'AS12945S20');

    -- Atualizar NextOPID nas Operações
    UPDATE Operation SET NextOPID = 103 WHERE OPID = 100;
    UPDATE Operation SET NextOPID = 112 WHERE OPID = 103;
    UPDATE Operation SET NextOPID = 114 WHERE OPID = 112;
    UPDATE Operation SET NextOPID = 115 WHERE OPID = 114;
    UPDATE Operation SET NextOPID = 121 WHERE OPID = 120;
    UPDATE Operation SET NextOPID = 122 WHERE OPID = 121;
    UPDATE Operation SET NextOPID = 123 WHERE OPID = 122;
    UPDATE Operation SET NextOPID = 124 WHERE OPID = 123;
    UPDATE Operation SET NextOPID = 151 WHERE OPID = 150;
    UPDATE Operation SET NextOPID = 152 WHERE OPID = 151;
    UPDATE Operation SET NextOPID = 153 WHERE OPID = 152;
    UPDATE Operation SET NextOPID = 154 WHERE OPID = 153;
    UPDATE Operation SET NextOPID = 161 WHERE OPID = 160;
    UPDATE Operation SET NextOPID = 162 WHERE OPID = 161;
    UPDATE Operation SET NextOPID = 163 WHERE OPID = 162;
    UPDATE Operation SET NextOPID = 164 WHERE OPID = 163;

    -- Inserir PartIN
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('PN52384R50', 100, 1, 'mm');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('IP12945A01', 103, 1, 'mm');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('PN94561L67', 103, 5, 'unit');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('IP12945A02', 112, 1, 'mm');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('PN94561L67', 112, 5, 'unit');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('IP12945A03', 114, 1, 'mm');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('IP12945A04', 115, 1, 'mm');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('PN18544C21', 115, 2, 'unit');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('PN52384R10', 120, 1, 'mm');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('IP12947A01', 121, 1, 'mm');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('PN94561L67', 121, 5, 'unit');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('IP12947A02', 122, 1, 'mm');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('IP12947A03', 123, 1, 'mm');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('PN18324C54', 123, 1, 'unit');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('IP12947A04', 124, 1, 'mm');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('AS12947S22', 130, 1, 'cm');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('AS12946S22', 130, 1, 'cm');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('PN52384R50', 150, 1, 'mm');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('IP12945A01', 151, 1, 'mm');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('PN94561L67', 151, 5, 'unit');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('IP12945A32', 152, 1, 'mm');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('PN94561L67', 152, 5, 'unit');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('IP12945A33', 153, 1, 'mm');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('IP12945A04', 154, 1, 'mm');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('PN18544C21', 154, 2, 'unit');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('PN52384R10', 160, 1, 'mm');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('IP12947A01', 161, 1, 'mm');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('PN94561L67', 161, 5, 'unit');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('IP12947A32', 162, 1, 'mm');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('IP12947A33', 163, 1, 'mm');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('PN18324C51', 163, 1, 'unit');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('IP12947A34', 164, 1, 'mm');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('AS12946S20', 170, 1, 'cm');
    INSERT INTO PartIN (PartID, OPID, QuantityIn, Unit) VALUES ('AS12947S20', 170, 1, 'cm');

    -- Inserir PartOut
    INSERT INTO PartOut (PartID, OPID, Quantity, Unit) VALUES ('IP12945A01', 100, 1, 'mm');
    INSERT INTO PartOut (PartID, OPID, Quantity, Unit) VALUES ('IP12945A02', 103, 1, 'mm');
    INSERT INTO PartOut (PartID, OPID, Quantity, Unit) VALUES ('IP12945A03', 112, 1, 'mm');
    INSERT INTO PartOut (PartID, OPID, Quantity, Unit) VALUES ('IP12945A04', 114, 1, 'mm');
    INSERT INTO PartOut (PartID, OPID, Quantity, Unit) VALUES ('AS12946S22', 115, 1, 'cm');
    INSERT INTO PartOut (PartID, OPID, Quantity, Unit) VALUES ('IP12947A01', 120, 1, 'mm');
    INSERT INTO PartOut (PartID, OPID, Quantity, Unit) VALUES ('IP12947A02', 121, 1, 'mm');
    INSERT INTO PartOut (PartID, OPID, Quantity, Unit) VALUES ('IP12947A03', 122, 1, 'mm');
    INSERT INTO PartOut (PartID, OPID, Quantity, Unit) VALUES ('IP12947A04', 123, 1, 'mm');
    INSERT INTO PartOut (PartID, OPID, Quantity, Unit) VALUES ('AS12947S22', 124, 1, 'cm');
    INSERT INTO PartOut (PartID, OPID, Quantity, Unit) VALUES ('AS12945S22', 130, 1, 'cm');
    INSERT INTO PartOut (PartID, OPID, Quantity, Unit) VALUES ('IP12945A01', 150, 1, 'mm');
    INSERT INTO PartOut (PartID, OPID, Quantity, Unit) VALUES ('IP12945A32', 151, 1, 'mm');
    INSERT INTO PartOut (PartID, OPID, Quantity, Unit) VALUES ('IP12945A33', 152, 1, 'mm');
    INSERT INTO PartOut (PartID, OPID, Quantity, Unit) VALUES ('IP12945A34', 153, 1, 'mm');
    INSERT INTO PartOut (PartID, OPID, Quantity, Unit) VALUES ('AS12946S20', 154, 1, 'cm');
    INSERT INTO PartOut (PartID, OPID, Quantity, Unit) VALUES ('IP12947A01', 160, 1, 'mm');
    INSERT INTO PartOut (PartID, OPID, Quantity, Unit) VALUES ('IP12947A32', 161, 1, 'mm');
    INSERT INTO PartOut (PartID, OPID, Quantity, Unit) VALUES ('IP12947A33', 162, 1, 'mm');
    INSERT INTO PartOut (PartID, OPID, Quantity, Unit) VALUES ('IP12947A34', 163, 1, 'mm');
    INSERT INTO PartOut (PartID, OPID, Quantity, Unit) VALUES ('AS12947S20', 164, 1, 'cm');
    INSERT INTO PartOut (PartID, OPID, Quantity, Unit) VALUES ('AS12945S20', 170, 1, 'cm');

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
