-- Begin block to setup test conditions
BEGIN
  -- Update ExternalPart table to set Reserved quantity to 3 for part PN52384R50
  UPDATE ExternalPart
     SET Reserved = 3
   WHERE PartID = 'PN52384R50';

  -- Commit the transaction
  COMMIT;
END;
/

-- Begin main test scenario
DECLARE
  -- Define record type to store part reservation information
  TYPE t_reserved_info IS RECORD (
    part_id     Part.PartID%TYPE,        -- Stores the Part ID
    quantity    ExternalPart.Reserved%TYPE, -- Stores the reserved quantity
    supplier_id supplier.SupplierID%TYPE  -- Stores the supplier ID
  );

  -- Cursor to fetch all reserved parts and their supplier information
  CURSOR c_reserved IS
    SELECT e.PartID,
           e.Reserved,
           o.SupplierID
      FROM ExternalPart e
      LEFT JOIN Offer o ON e.PartID = o.PartID -- Join with Offer table to get supplier info
     WHERE e.Reserved > 0                      -- Only get parts with reservations
     ORDER BY e.PartID;                        -- Order results by Part ID

  -- Variable to store the fetched reservation information
  v_info t_reserved_info;

BEGIN
  -- Print start of test execution
  DBMS_OUTPUT.PUT_LINE('---- Test 2 Execution Start ----');
  
  -- Open the cursor
  OPEN c_reserved;
  
  -- Loop through all reserved parts
  LOOP
    -- Fetch next record into v_info variable
    FETCH c_reserved INTO v_info;
    -- Exit loop when no more records found
    EXIT WHEN c_reserved%NOTFOUND;
    -- Print information for current part
    DBMS_OUTPUT.PUT_LINE('PartID: ' || v_info.part_id ||
                         ' | Reserved Qty: ' || v_info.quantity ||
                         ' | Supplier ID: ' || NVL(TO_CHAR(v_info.supplier_id), 'N/A'));
  END LOOP;
  
  -- Close the cursor
  CLOSE c_reserved;
  
  -- Print end of test execution
  DBMS_OUTPUT.PUT_LINE('---- Test 2 Execution End ----');
END;
/