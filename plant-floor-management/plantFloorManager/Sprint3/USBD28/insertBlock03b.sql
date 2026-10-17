BEGIN
    -- ----------------------------------------
    -- 1. Insert Suppliers
    -- ----------------------------------------
    INSERT INTO supplier (SupplierID, Name) VALUES (12345, 'Supplier 12345');
    INSERT INTO supplier (SupplierID, Name) VALUES (12298, 'Supplier 12298');

    -- ----------------------------------------
    -- 2. Insert Offers
    -- ----------------------------------------
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

    -- ----------------------------------------
    -- 3. Insert Orders
    -- ----------------------------------------
    INSERT INTO "Order" (OID, DateOrder, DateDelivery, ClientNIF)
    VALUES (1, TO_DATE('2024-09-15', 'YYYY-MM-DD'), TO_DATE('2024-09-23', 'YYYY-MM-DD'), 'PT501245488');
    
    INSERT INTO "Order" (OID, DateOrder, DateDelivery, ClientNIF)
    VALUES (2, TO_DATE('2024-09-15', 'YYYY-MM-DD'), TO_DATE('2024-09-26', 'YYYY-MM-DD'), 'PT501242417');
    
    INSERT INTO "Order" (OID, DateOrder, DateDelivery, ClientNIF)
    VALUES (3, TO_DATE('2024-09-15', 'YYYY-MM-DD'), TO_DATE('2024-09-25', 'YYYY-MM-DD'), 'CZ6451237810');
    
    INSERT INTO "Order" (OID, DateOrder, DateDelivery, ClientNIF)
    VALUES (4, TO_DATE('2024-09-18', 'YYYY-MM-DD'), TO_DATE('2024-09-25', 'YYYY-MM-DD'), 'PT501245488');
    
    INSERT INTO "Order" (OID, DateOrder, DateDelivery, ClientNIF)
    VALUES (5, TO_DATE('2024-09-18', 'YYYY-MM-DD'), TO_DATE('2024-09-25', 'YYYY-MM-DD'), 'PT501242417');
    
    INSERT INTO "Order" (OID, DateOrder, DateDelivery, ClientNIF)
    VALUES (6, TO_DATE('2024-09-18', 'YYYY-MM-DD'), TO_DATE('2024-09-26', 'YYYY-MM-DD'), 'CZ6451237810');
    
    INSERT INTO "Order" (OID, DateOrder, DateDelivery, ClientNIF)
    VALUES (7, TO_DATE('2024-09-21', 'YYYY-MM-DD'), TO_DATE('2024-09-26', 'YYYY-MM-DD'), 'PT501245987');

    -- ----------------------------------------
    -- 4. Insert Order Products
    -- ----------------------------------------
    INSERT INTO OrderProduct (OID, ProductPartID, Quantity) VALUES (1, 'AS12945S22', 5);
    INSERT INTO OrderProduct (OID, ProductPartID, Quantity) VALUES (1, 'AS12945S20', 15);
    
    INSERT INTO OrderProduct (OID, ProductPartID, Quantity) VALUES (2, 'AS12945S22', 10);
    INSERT INTO OrderProduct (OID, ProductPartID, Quantity) VALUES (2, 'AS12945P17', 20);
    
    INSERT INTO OrderProduct (OID, ProductPartID, Quantity) VALUES (3, 'AS12945S22', 10);
    INSERT INTO OrderProduct (OID, ProductPartID, Quantity) VALUES (3, 'AS12945S20', 10);
    
    INSERT INTO OrderProduct (OID, ProductPartID, Quantity) VALUES (4, 'AS12945S20', 24);
    INSERT INTO OrderProduct (OID, ProductPartID, Quantity) VALUES (4, 'AS12945S22', 16);
    INSERT INTO OrderProduct (OID, ProductPartID, Quantity) VALUES (4, 'AS12945S17', 8);
    
    INSERT INTO OrderProduct (OID, ProductPartID, Quantity) VALUES (5, 'AS12945S22', 12);
    
    INSERT INTO OrderProduct (OID, ProductPartID, Quantity) VALUES (6, 'AS12945S17', 8);
    INSERT INTO OrderProduct (OID, ProductPartID, Quantity) VALUES (6, 'AS12945P17', 16);
    
    INSERT INTO OrderProduct (OID, ProductPartID, Quantity) VALUES (7, 'AS12945S22', 8);

    -- ----------------------------------------
    -- 5. Insert Production Orders
    -- ----------------------------------------
    INSERT INTO ProductionOrder (PrOrID, OID, ProductPartID) VALUES ('PO001', 1, 'AS12945S22');
    INSERT INTO ProductionOrder (PrOrID, OID, ProductPartID) VALUES ('PO002', 1, 'AS12945S20');
    INSERT INTO ProductionOrder (PrOrID, OID, ProductPartID) VALUES ('PO003', 2, 'AS12945S22');
    INSERT INTO ProductionOrder (PrOrID, OID, ProductPartID) VALUES ('PO004', 2, 'AS12945P17');
    INSERT INTO ProductionOrder (PrOrID, OID, ProductPartID) VALUES ('PO005', 3, 'AS12945S22');
    INSERT INTO ProductionOrder (PrOrID, OID, ProductPartID) VALUES ('PO006', 3, 'AS12945S20');
    INSERT INTO ProductionOrder (PrOrID, OID, ProductPartID) VALUES ('PO007', 4, 'AS12945S20');
    INSERT INTO ProductionOrder (PrOrID, OID, ProductPartID) VALUES ('PO008', 4, 'AS12945S22');
    INSERT INTO ProductionOrder (PrOrID, OID, ProductPartID) VALUES ('PO009', 4, 'AS12945S17');
    INSERT INTO ProductionOrder (PrOrID, OID, ProductPartID) VALUES ('PO010', 5, 'AS12945S22');
    INSERT INTO ProductionOrder (PrOrID, OID, ProductPartID) VALUES ('PO011', 6, 'AS12945S17');
    INSERT INTO ProductionOrder (PrOrID, OID, ProductPartID) VALUES ('PO012', 6, 'AS12945P17');
    INSERT INTO ProductionOrder (PrOrID, OID, ProductPartID) VALUES ('PO013', 7, 'AS12945S22');

    -- ----------------------------------------
    -- 6. Insert PartIN and PartOut Records
    -- ----------------------------------------
    -- PartIN and PartOut for BOO 'AS12946S22'
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('PN52384R50', 100, 'AS12946S22', 1);
    INSERT INTO PartOut (PartID, OPID, BOOID, Quantity) VALUES ('IP12945A01', 100, 'AS12946S22', 1);
    
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('IP12945A01', 103, 'AS12946S22', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('PN94561L67', 103, 'AS12946S22', 5);
    INSERT INTO PartOut (PartID, OPID, BOOID, Quantity) VALUES ('IP12945A02', 103, 'AS12946S22', 1);
    
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('IP12945A02', 112, 'AS12946S22', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('PN94561L67', 112, 'AS12946S22', 5);
    INSERT INTO PartOut (PartID, OPID, BOOID, Quantity) VALUES ('IP12945A03', 112, 'AS12946S22', 1);
    
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('IP12945A03', 114, 'AS12946S22', 1);
    INSERT INTO PartOut (PartID, OPID, BOOID, Quantity) VALUES ('IP12945A04', 114, 'AS12946S22', 1);
    
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('IP12945A04', 115, 'AS12946S22', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('PN18544C21', 115, 'AS12946S22', 2);
    INSERT INTO PartOut (PartID, OPID, BOOID, Quantity) VALUES ('AS12946S22', 115, 'AS12946S22', 1);
    
    -- PartIN and PartOut for BOO 'AS12947S22'
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('PN52384R10', 120, 'AS12947S22', 1);
    INSERT INTO PartOut (PartID, OPID, BOOID, Quantity) VALUES ('IP12947A01', 120, 'AS12947S22', 1);
    
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('IP12947A01', 121, 'AS12947S22', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('PN94561L67', 121, 'AS12947S22', 5);
    INSERT INTO PartOut (PartID, OPID, BOOID, Quantity) VALUES ('IP12947A02', 121, 'AS12947S22', 1);
    
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('IP12947A02', 122, 'AS12947S22', 1);
    INSERT INTO PartOut (PartID, OPID, BOOID, Quantity) VALUES ('IP12947A03', 122, 'AS12947S22', 1);
    
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('IP12947A03', 123, 'AS12947S22', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('PN18324C54', 123, 'AS12947S22', 1);
    INSERT INTO PartOut (PartID, OPID, BOOID, Quantity) VALUES ('IP12947A04', 123, 'AS12947S22', 1);
    
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('IP12947A04', 124, 'AS12947S22', 1);
    INSERT INTO PartOut (PartID, OPID, BOOID, Quantity) VALUES ('AS12947S22', 124, 'AS12947S22', 1);
    
    -- PartIN and PartOut for BOO 'AS12945S22'
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('AS12947S22', 130, 'AS12945S22', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('AS12946S22', 130, 'AS12945S22', 1);
    INSERT INTO PartOut (PartID, OPID, BOOID, Quantity) VALUES ('AS12945S22', 130, 'AS12945S22', 1);
    
    -- PartIN and PartOut for BOO 'AS12946S20'
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('PN52384R50', 150, 'AS12946S20', 1);
    INSERT INTO PartOut (PartID, OPID, BOOID, Quantity) VALUES ('IP12945A01', 150, 'AS12946S20', 1);
    
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('IP12945A01', 151, 'AS12946S20', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('PN94561L67', 151, 'AS12946S20', 5);
    INSERT INTO PartOut (PartID, OPID, BOOID, Quantity) VALUES ('IP12945A32', 151, 'AS12946S20', 1);
    
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('IP12945A32', 152, 'AS12946S20', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('PN94561L67', 152, 'AS12946S20', 5);
    INSERT INTO PartOut (PartID, OPID, BOOID, Quantity) VALUES ('IP12945A33', 152, 'AS12946S20', 1);
    
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('IP12945A33', 153, 'AS12946S20', 1);
    INSERT INTO PartOut (PartID, OPID, BOOID, Quantity) VALUES ('IP12945A34', 153, 'AS12946S20', 1);
    
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('IP12945A34', 154, 'AS12946S20', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('PN18544C21', 154, 'AS12946S20', 2);
    INSERT INTO PartOut (PartID, OPID, BOOID, Quantity) VALUES ('AS12946S20', 154, 'AS12946S20', 1);
    
    -- PartIN and PartOut for BOO 'AS12947S20'
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('PN52384R10', 160, 'AS12947S20', 1);
    INSERT INTO PartOut (PartID, OPID, BOOID, Quantity) VALUES ('IP12947A01', 160, 'AS12947S20', 1);
    
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('IP12947A01', 161, 'AS12947S20', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('PN94561L67', 161, 'AS12947S20', 5);
    INSERT INTO PartOut (PartID, OPID, BOOID, Quantity) VALUES ('IP12947A32', 161, 'AS12947S20', 1);
    
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('IP12947A32', 162, 'AS12947S20', 1);
    INSERT INTO PartOut (PartID, OPID, BOOID, Quantity) VALUES ('IP12947A33', 162, 'AS12947S20', 1);
    
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('IP12947A33', 163, 'AS12947S20', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('PN18324C51', 163, 'AS12947S20', 1);
    INSERT INTO PartOut (PartID, OPID, BOOID, Quantity) VALUES ('IP12947A34', 163, 'AS12947S20', 1);
    
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('IP12947A34', 164, 'AS12947S20', 1);
    INSERT INTO PartOut (PartID, OPID, BOOID, Quantity) VALUES ('AS12947S20', 164, 'AS12947S20', 1);
    
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('AS12946S20', 170, 'AS12945S20', 1);
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('AS12947S20', 170, 'AS12945S20', 1);
    INSERT INTO PartOut (PartID, OPID, BOOID, Quantity) VALUES ('AS12945S20', 170, 'AS12945S20', 1);

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
