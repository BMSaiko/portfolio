BEGIN

    -- Insert Suppliers
    INSERT INTO supplier (SupplierID, Name)
    VALUES (12345, 'Supplier 12345');
    INSERT INTO supplier (SupplierID, Name)
    VALUES (12298, 'Supplier 12298');

    -- Insert Offers
    INSERT INTO Offer (PartID, SupplierID, UnitCost, DateStart, DateEnd, minimumOrderSize)
    VALUES ('PN18544C21', 12345, 1.25, TO_DATE('2023/10/01', 'YYYY/MM/DD'), NULL, 20);
    INSERT INTO Offer (PartID, SupplierID, UnitCost, DateStart, DateEnd, minimumOrderSize)
    VALUES ('PN18324C54', 12345, 1.70, TO_DATE('2023/10/01', 'YYYY/MM/DD'), TO_DATE('2024/02/29', 'YYYY/MM/DD'), 10);
    INSERT INTO Offer (PartID, SupplierID, UnitCost, DateStart, DateEnd, minimumOrderSize)
    VALUES ('PN18324C54', 12345, 1.80, TO_DATE('2024/04/01', 'YYYY/MM/DD'), NULL, 16);
    INSERT INTO Offer (PartID, SupplierID, UnitCost, DateStart, DateEnd, minimumOrderSize)
    VALUES ('PN18324C51', 12345, 1.90, TO_DATE('2023/07/01', 'YYYY/MM/DD'), TO_DATE('2024/03/31', 'YYYY/MM/DD'), 30);
    INSERT INTO Offer (PartID, SupplierID, UnitCost, DateStart, DateEnd, minimumOrderSize)
    VALUES ('PN18324C51', 12345, 1.90, TO_DATE('2024/04/01', 'YYYY/MM/DD'), NULL, 20);
    INSERT INTO Offer (PartID, SupplierID, UnitCost, DateStart, DateEnd, minimumOrderSize)
    VALUES ('PN18544C21', 12298, 1.35, TO_DATE('2023/09/01', 'YYYY/MM/DD'), NULL, 10);
    INSERT INTO Offer (PartID, SupplierID, UnitCost, DateStart, DateEnd, minimumOrderSize)
    VALUES ('PN18324C54', 12298, 1.80, TO_DATE('2023/08/01', 'YYYY/MM/DD'), TO_DATE('2024/01/29', 'YYYY/MM/DD'), 10);
    INSERT INTO Offer (PartID, SupplierID, UnitCost, DateStart, DateEnd, minimumOrderSize)
    VALUES ('PN18324C54', 12298, 1.75, TO_DATE('2024/02/15', 'YYYY/MM/DD'), NULL, 20);
    INSERT INTO Offer (PartID, SupplierID, UnitCost, DateStart, DateEnd, minimumOrderSize)
    VALUES ('PN18324C51', 12298, 1.80, TO_DATE('2023/08/01', 'YYYY/MM/DD'), TO_DATE('2024/05/31', 'YYYY/MM/DD'), 40);
    INSERT INTO Offer (PartID, SupplierID, UnitCost, DateStart, DateEnd, minimumOrderSize)
    VALUES ('PN12344A21', 12298, 0.65, TO_DATE('2023/07/01', 'YYYY/MM/DD'), NULL, 200);

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/