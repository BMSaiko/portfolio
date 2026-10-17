BEGIN

    -- Insert Orders
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

    -- Insert OrderProducts
    INSERT INTO OrderProduct (OID, ProductPartID, Quantity)
    VALUES (1, 'AS12945S22', 5);
    INSERT INTO OrderProduct (OID, ProductPartID, Quantity)
    VALUES (1, 'AS12945S20', 15);
    INSERT INTO OrderProduct (OID, ProductPartID, Quantity)
    VALUES (2, 'AS12945S22', 10);
    INSERT INTO OrderProduct (OID, ProductPartID, Quantity)
    VALUES (2, 'AS12945P17', 20);
    INSERT INTO OrderProduct (OID, ProductPartID, Quantity)
    VALUES (3, 'AS12945S22', 10);
    INSERT INTO OrderProduct (OID, ProductPartID, Quantity)
    VALUES (3, 'AS12945S20', 10);
    INSERT INTO OrderProduct (OID, ProductPartID, Quantity)
    VALUES (4, 'AS12945S20', 24);
    INSERT INTO OrderProduct (OID, ProductPartID, Quantity)
    VALUES (4, 'AS12945S22', 16);
    INSERT INTO OrderProduct (OID, ProductPartID, Quantity)
    VALUES (4, 'AS12945S17', 8);
    INSERT INTO OrderProduct (OID, ProductPartID, Quantity)
    VALUES (5, 'AS12945S22', 12);
    INSERT INTO OrderProduct (OID, ProductPartID, Quantity)
    VALUES (6, 'AS12945S17', 8);
    INSERT INTO OrderProduct (OID, ProductPartID, Quantity)
    VALUES (6, 'AS12945P17', 16);
    INSERT INTO OrderProduct (OID, ProductPartID, Quantity)
    VALUES (7, 'AS12945S22', 8);

    -- Insert ProductionOrders
    INSERT INTO ProductionOrder (PrOrID, OID, ProductPartID)
    VALUES ('PO001', 1, 'AS12945S22');
    INSERT INTO ProductionOrder (PrOrID, OID, ProductPartID)
    VALUES ('PO002', 1, 'AS12945S20');
    INSERT INTO ProductionOrder (PrOrID, OID, ProductPartID)
    VALUES ('PO003', 2, 'AS12945S22');
    INSERT INTO ProductionOrder (PrOrID, OID, ProductPartID)
    VALUES ('PO004', 2, 'AS12945P17');
    INSERT INTO ProductionOrder (PrOrID, OID, ProductPartID)
    VALUES ('PO005', 3, 'AS12945S22');
    INSERT INTO ProductionOrder (PrOrID, OID, ProductPartID)
    VALUES ('PO006', 3, 'AS12945S20');
    INSERT INTO ProductionOrder (PrOrID, OID, ProductPartID)
    VALUES ('PO007', 4, 'AS12945S20');
    INSERT INTO ProductionOrder (PrOrID, OID, ProductPartID)
    VALUES ('PO008', 4, 'AS12945S22');
    INSERT INTO ProductionOrder (PrOrID, OID, ProductPartID)
    VALUES ('PO009', 4, 'AS12945S17');
    INSERT INTO ProductionOrder (PrOrID, OID, ProductPartID)
    VALUES ('PO010', 5, 'AS12945S22');
    INSERT INTO ProductionOrder (PrOrID, OID, ProductPartID)
    VALUES ('PO011', 6, 'AS12945S17');
    INSERT INTO ProductionOrder (PrOrID, OID, ProductPartID)
    VALUES ('PO012', 6, 'AS12945P17');
    INSERT INTO ProductionOrder (PrOrID, OID, ProductPartID)
    VALUES ('PO013', 7, 'AS12945S22');

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/