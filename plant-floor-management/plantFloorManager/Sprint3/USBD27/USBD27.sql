CREATE OR REPLACE PROCEDURE ReserveMaterialsForOrder(p_oid IN NUMBER) IS
    -- Cursor to fetch order products and their quantities
    CURSOR c_order_products IS
        SELECT ProductPartID, Quantity
        FROM OrderProduct
        WHERE OID = p_oid;

    -- Cursor to fetch required materials for a product
    CURSOR c_required_materials(p_product_part_id VARCHAR2) IS
        SELECT PartID, QuantityIn * :qty AS TotalQuantityNeeded
        FROM PartIN
        WHERE BOOID = p_product_part_id;

    -- Variable to track stock availability
    v_stock_availability BOOLEAN := TRUE;

    -- Variable to hold stock quantity
    v_current_stock NUMBER;

    -- Variable to hold total quantity needed
    v_quantity_needed NUMBER;

    -- Exception for insufficient stock
    ex_insufficient_stock EXCEPTION;
    
BEGIN
    -- Loop through the products in the order
    FOR order_product IN c_order_products LOOP
        -- Loop through the materials required for each product
        FOR required_material IN c_required_materials(order_product.ProductPartID) LOOP
            -- Check current stock level
            SELECT StockQuantity
            INTO v_current_stock
            FROM Stock
            WHERE PartID = required_material.PartID
            FOR UPDATE;

            -- Calculate total quantity needed
            v_quantity_needed := required_material.TotalQuantityNeeded;

            -- Check if stock is sufficient
            IF v_current_stock < v_quantity_needed THEN
                v_stock_availability := FALSE;
                RAISE ex_insufficient_stock;
            END IF;
        END LOOP;
    END LOOP;

    -- If all materials are available, reserve them
    IF v_stock_availability THEN
        -- Loop through the products in the order again to reserve materials
        FOR order_product IN c_order_products LOOP
            -- Loop through the materials required for each product
            FOR required_material IN c_required_materials(order_product.ProductPartID) LOOP
                -- Insert reservation into Reservation table
                INSERT INTO Reservation (OID, PartID, ReservedQuantity, ReservationDate)
                VALUES (p_oid, required_material.PartID, required_material.TotalQuantityNeeded, SYSDATE);
            END LOOP;
        END LOOP;

        -- Commit the reservation
        COMMIT;
    END IF;

    -- Output success message
    DBMS_OUTPUT.PUT_LINE('Materials successfully reserved for Order ID: ' || p_oid);

EXCEPTION
    WHEN ex_insufficient_stock THEN
        -- Rollback changes if stock is insufficient
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('Reservation failed: Insufficient stock for Order ID: ' || p_oid);
    WHEN OTHERS THEN
        -- Handle other exceptions
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('An error occurred: ' || SQLERRM);
END;
/

BEGIN
    ReserveMaterialsForOrder(1); -- Replace 1 with the actual order ID
END;
/
