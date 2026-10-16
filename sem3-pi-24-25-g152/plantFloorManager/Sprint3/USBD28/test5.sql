-- Begin block to setup test data
BEGIN
  -- Setup: Ensure PN18324C51 is reserved. It has multiple offers with various date ranges.
  -- Update the ExternalPart table to set Reserved quantity to 4 for part PN18324C51
  UPDATE ExternalPart
     SET Reserved = 4
   WHERE PartID = 'PN18324C51';

  -- Commit the transaction
  COMMIT;
END;
/

-- Begin the main test scenario
DECLARE
  -- Define a record type to store reserved part information including part ID, quantity and supplier ID
  TYPE t_reserved_info IS RECORD (
    part_id     Part.PartID%TYPE,
    quantity    ExternalPart.Reserved%TYPE,
    supplier_id supplier.SupplierID%TYPE
  );

  -- Define a cursor to fetch all reserved parts with their supplier information
  -- Orders results by part ID
  CURSOR c_reserved IS
    SELECT e.PartID,
           e.Reserved,
           o.SupplierID
      FROM ExternalPart e
      LEFT JOIN Offer o ON e.PartID = o.PartID
     WHERE e.Reserved > 0
     ORDER BY e.PartID;

  -- Declare a variable of the record type to store current row data
  v_info t_reserved_info;

BEGIN
  -- Print start of test execution
  DBMS_OUTPUT.PUT_LINE('---- Test 5 Execution Start ----');
  -- Open the cursor
  OPEN c_reserved;
  -- Start loop to process all reserved parts
  LOOP
    -- Fetch next row into record variable
    FETCH c_reserved INTO v_info;
    -- Exit loop when no more rows found
    EXIT WHEN c_reserved%NOTFOUND;
    -- Print information for current part including part ID, reserved quantity and supplier ID
    -- If supplier ID is null, display 'N/A'
    DBMS_OUTPUT.PUT_LINE('PartID: ' || v_info.part_id ||
                         ' | Reserved Qty: ' || v_info.quantity ||
                         ' | Supplier ID: ' || NVL(TO_CHAR(v_info.supplier_id), 'N/A'));
  END LOOP;
  -- Close the cursor
  CLOSE c_reserved;
  -- Print end of test execution
  DBMS_OUTPUT.PUT_LINE('---- Test 5 Execution End ----');
END;
/