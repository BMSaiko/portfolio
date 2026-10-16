BEGIN

    -- Insert Units
    INSERT INTO Unit (UnitID, Name)
    VALUES (1, 'kg');
    INSERT INTO Unit (UnitID, Name)
    VALUES (2, 'l');
    INSERT INTO Unit (UnitID, Name)
    VALUES (3, 'g');
    INSERT INTO Unit (UnitID, Name)
    VALUES (4, 'unit');

    -- Insert Parts
    INSERT INTO Part (PartID, Description)
    VALUES ('PN12344A21', 'Screw M6 35 mm');
    INSERT INTO ExternalPart (PartID, Stock)
    VALUES ('PN12344A21', 10);
    INSERT INTO Component (PartID) VALUES ('PN12344A21');

    INSERT INTO Part (PartID, Description)
    VALUES ('PN52384R50', '300x300 mm 5 mm stainless steel sheet');
    INSERT INTO ExternalPart (PartID, Stock)
    VALUES ('PN52384R50', 10);
    INSERT INTO Component (PartID) VALUES ('PN52384R50');

    INSERT INTO Part (PartID, Description)
    VALUES ('PN52384R10', '300x300 mm 1 mm stainless steel sheet');
    INSERT INTO ExternalPart (PartID, Stock)
    VALUES ('PN52384R10', 10);
    INSERT INTO Component (PartID) VALUES ('PN52384R10');

    INSERT INTO Part (PartID, Description)
    VALUES ('PN18544A21', 'Rivet 6 mm');
    INSERT INTO ExternalPart (PartID, Stock)
    VALUES ('PN18544A21', 10);
    INSERT INTO Component (PartID) VALUES ('PN18544A21');

    INSERT INTO Part (PartID, Description)
    VALUES ('PN18544C21', 'Stainless steel handle model U6');
    INSERT INTO ExternalPart (PartID, Stock)
    VALUES ('PN18544C21', 10);
    INSERT INTO Component (PartID) VALUES ('PN18544C21');

    INSERT INTO Part (PartID, Description)
    VALUES ('PN18324C54', 'Stainless steel handle model R12');
    INSERT INTO ExternalPart (PartID, Stock)
    VALUES ('PN18324C54', 10);
    INSERT INTO Component (PartID) VALUES ('PN18324C54');

    INSERT INTO Part (PartID, Description)
    VALUES ('PN52384R45', '250x250 mm 5mm stainless steel sheet');
    INSERT INTO ExternalPart (PartID, Stock)
    VALUES ('PN52384R45', 10);
    INSERT INTO Component (PartID) VALUES ('PN52384R45');

    INSERT INTO Part (PartID, Description)
    VALUES ('PN52384R12', '250x250 mm 1mm stainless steel sheet');
    INSERT INTO ExternalPart (PartID, Stock)
    VALUES ('PN52384R12', 10);
    INSERT INTO Component (PartID) VALUES ('PN52384R12');

    INSERT INTO Part (PartID, Description)
    VALUES ('PN18324C91', 'Stainless steel handle model S26');
    INSERT INTO ExternalPart (PartID, Stock)
    VALUES ('PN18324C91', 10);
    INSERT INTO Component (PartID) VALUES ('PN18324C91');

    INSERT INTO Part (PartID, Description)
    VALUES ('PN18324C51', 'Stainless steel handle model R11');
    INSERT INTO ExternalPart (PartID, Stock)
    VALUES ('PN18324C51', 10);
    INSERT INTO Component (PartID) VALUES ('PN18324C51');

    INSERT INTO Part (PartID, Description)
    VALUES ('AS12945T22', '5l 22 cm aluminium and teflon non stick pot');
    INSERT INTO InternalPart (PartID) VALUES ('AS12945T22');
    INSERT INTO Product (PartID, Name, PFID)
    VALUES ('AS12945T22', 'La Belle 22 5l pot', 130);

    INSERT INTO Part (PartID, Description)
    VALUES ('AS12945S22', '5l 22 cm stainless steel pot');
    INSERT INTO InternalPart (PartID) VALUES ('AS12945S22');
    INSERT INTO Product (PartID, Name, PFID)
    VALUES ('AS12945S22', 'Pro 22 5l pot', 125);

    INSERT INTO Part (PartID, Description)
    VALUES ('AS12946S22', '5l 22 cm stainless steel pot bottom');
    INSERT INTO InternalPart (PartID) VALUES ('AS12946S22');
    INSERT INTO Product (PartID, Name, PFID)
    VALUES ('AS12946S22', 'Pro 22 5l pot bottom', 125);

    INSERT INTO Part (PartID, Description)
    VALUES ('AS12947S22', '22 cm stainless steel lid');
    INSERT INTO InternalPart (PartID) VALUES ('AS12947S22');
    INSERT INTO Product (PartID, Name, PFID)
    VALUES ('AS12947S22', 'Pro 22 lid', 145);

    INSERT INTO Part (PartID, Description)
    VALUES ('AS12945S20', '3l 20 cm stainless steel pot');
    INSERT INTO InternalPart (PartID) VALUES ('AS12945S20');
    INSERT INTO Product (PartID, Name, PFID)
    VALUES ('AS12945S20', 'Pro 20 3l pot', 125);

    INSERT INTO Part (PartID, Description)
    VALUES ('AS12946S20', '3l 20 cm stainless steel pot bottom');
    INSERT INTO InternalPart (PartID) VALUES ('AS12946S20');
    INSERT INTO Product (PartID, Name, PFID)
    VALUES ('AS12946S20', 'Pro 20 3l pot bottom', 125);

    INSERT INTO Part (PartID, Description)
    VALUES ('AS12947S20', '20 cm stainless steel lid');
    INSERT INTO InternalPart (PartID) VALUES ('AS12947S20');
    INSERT INTO Product (PartID, Name, PFID)
    VALUES ('AS12947S20', 'Pro 20 lid', 145);

    INSERT INTO Part (PartID, Description)
    VALUES ('AS12945S17', '2l 17 cm stainless steel pot');
    INSERT INTO InternalPart (PartID) VALUES ('AS12945S17');
    INSERT INTO Product (PartID, Name, PFID)
    VALUES ('AS12945S17', 'Pro 17 2l pot', 125);

    INSERT INTO Part (PartID, Description)
    VALUES ('AS12945P17', '2l 17 cm stainless steel souce pan');
    INSERT INTO InternalPart (PartID) VALUES ('AS12945P17');
    INSERT INTO Product (PartID, Name, PFID)
    VALUES ('AS12945P17', 'Pro 17 2l sauce pan', 132);

    INSERT INTO Part (PartID, Description)
    VALUES ('AS12945S48', '17 cm stainless steel lid');
    INSERT INTO InternalPart (PartID) VALUES ('AS12945S48');
    INSERT INTO Product (PartID, Name, PFID)
    VALUES ('AS12945S48', 'Pro 17 lid', 145);

    INSERT INTO Part (PartID, Description)
    VALUES ('AS12945G48', '17 cm glass lid');
    INSERT INTO InternalPart (PartID) VALUES ('AS12945G48');
    INSERT INTO Product (PartID, Name, PFID)
    VALUES ('AS12945G48', 'Pro Clear 17 lid', 146);

    INSERT INTO Part (PartID, Description)
    VALUES ('IP12945A01', '250 mm 5 mm stailess steel disc');
    INSERT INTO InternalPart (PartID) VALUES ('IP12945A01');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12945A01');

    INSERT INTO Part (PartID, Description)
    VALUES ('IP12945A02', '220 mm pot base phase 1');
    INSERT INTO InternalPart (PartID) VALUES ('IP12945A02');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12945A02');

    INSERT INTO Part (PartID, Description)
    VALUES ('IP12945A03', '220 mm pot base phase 2');
    INSERT INTO InternalPart (PartID) VALUES ('IP12945A03');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12945A03');

    INSERT INTO Part (PartID, Description)
    VALUES ('IP12945A04', '220 mm pot base final');
    INSERT INTO InternalPart (PartID) VALUES ('IP12945A04');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12945A04');

    INSERT INTO Part (PartID, Description)
    VALUES ('IP12947A01', '250 mm 1 mm stailess steel disc');
    INSERT INTO InternalPart (PartID) VALUES ('IP12947A01');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12947A01');

    INSERT INTO Part (PartID, Description)
    VALUES ('IP12947A02', '220 mm lid pressed');
    INSERT INTO InternalPart (PartID) VALUES ('IP12947A02');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12947A02');

    INSERT INTO Part (PartID, Description)
    VALUES ('IP12947A03', '220 mm lid polished');
    INSERT INTO InternalPart (PartID) VALUES ('IP12947A03');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12947A03');

    INSERT INTO Part (PartID, Description)
    VALUES ('IP12947A04', '220 mm lid with handle');
    INSERT INTO InternalPart (PartID) VALUES ('IP12947A04');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12947A04');

    INSERT INTO Part (PartID, Description)
    VALUES ('IP12945A32', '200 mm pot base phase 1');
    INSERT INTO InternalPart (PartID) VALUES ('IP12945A32');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12945A32');

    INSERT INTO Part (PartID, Description)
    VALUES ('IP12945A33', '200 mm pot base phase 2');
    INSERT INTO InternalPart (PartID) VALUES ('IP12945A33');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12945A33');

    INSERT INTO Part (PartID, Description)
    VALUES ('IP12945A34', '200 mm pot base final');
    INSERT INTO InternalPart (PartID) VALUES ('IP12945A34');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12945A34');

    INSERT INTO Part (PartID, Description)
    VALUES ('IP12947A32', '200 mm lid pressed');
    INSERT INTO InternalPart (PartID) VALUES ('IP12947A32');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12947A32');

    INSERT INTO Part (PartID, Description)
    VALUES ('IP12947A33', '200 mm lid polished');
    INSERT INTO InternalPart (PartID) VALUES ('IP12947A33');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12947A33');

    INSERT INTO Part (PartID, Description)
    VALUES ('IP12947A34', '200 mm lid with handle');
    INSERT INTO InternalPart (PartID) VALUES ('IP12947A34');
    INSERT INTO IntermediateProduct (PartID) VALUES ('IP12947A34');

    INSERT INTO Part (PartID, Description)
    VALUES ('PN94561L67', 'Coolube 2210XP');
    INSERT INTO ExternalPart (PartID, Stock)
    VALUES ('PN94561L67', 10);
    INSERT INTO RawMaterial (PartID, UnitID)
    VALUES ('PN94561L67', 1);


    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/