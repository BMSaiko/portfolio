-- Declare a record type to store information about reserved parts
DECLARE
  TYPE t_reserved_info IS RECORD (
    part_id     Part.PartID%TYPE,      -- Stores the Part ID
    quantity    ExternalPart.Reserved%TYPE,  -- Stores the reserved quantity
    supplier_id supplier.SupplierID%TYPE     -- Stores the Supplier ID
  );

  -- Cursor to fetch all reserved parts with their quantities and supplier information
  CURSOR c_reserved IS
    SELECT e.PartID,
           e.Reserved,
           o.SupplierID
      FROM ExternalPart e
      LEFT JOIN Offer o ON e.PartID = o.PartID  -- Join with Offer table to get supplier info
     WHERE e.Reserved > 0                        -- Only get parts with positive reservations
     ORDER BY e.PartID;                         -- Order results by Part ID

  -- Variable to store the fetched record
  v_info t_reserved_info;

BEGIN
  -- Open the cursor
  OPEN c_reserved;
  -- Start loop to process each record
  LOOP
    -- Fetch the next record into our variable
    FETCH c_reserved INTO v_info;
    -- Exit when no more records are found
    EXIT WHEN c_reserved%NOTFOUND;

    -- Display the information for each reserved part
    -- If supplier_id is null, display 'N/A' instead
    DBMS_OUTPUT.PUT_LINE('PartID: ' || v_info.part_id
                         || ' | Reserved Qty: ' || v_info.quantity
                         || ' | Supplier ID: ' || NVL(TO_CHAR(v_info.supplier_id), 'N/A'));
  END LOOP;
  -- Close the cursor
  CLOSE c_reserved;
END;
/