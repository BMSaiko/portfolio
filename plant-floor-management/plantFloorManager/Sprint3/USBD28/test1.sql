-- Begin block to setup test data
BEGIN
  -- Update ExternalPart table to set Reserved quantity to 5 for part PN18544C21
  UPDATE ExternalPart
     SET Reserved = 5
   WHERE PartID = 'PN18544C21';

  -- Commit the transaction
  COMMIT;
END;
/

-- Begin test scenario execution block
DECLARE
  -- Define record type to store part reservation information
  -- Contains fields for part ID, reserved quantity, and supplier ID
  TYPE t_reserved_info IS RECORD (
    part_id     Part.PartID%TYPE,
    quantity    ExternalPart.Reserved%TYPE,
    supplier_id supplier.SupplierID%TYPE
  );

  -- Define cursor to fetch all reserved parts
  -- Joins ExternalPart with Offer table to get supplier information
  -- Only selects parts with Reserved quantity greater than 0
  CURSOR c_reserved IS
    SELECT e.PartID,
           e.Reserved,
           o.SupplierID
      FROM ExternalPart e
      LEFT JOIN Offer o ON e.PartID = o.PartID
     WHERE e.Reserved > 0
     ORDER BY e.PartID;

  -- Declare variable to store fetched reservation information
  v_info t_reserved_info;

BEGIN
  -- Print start of test execution
  DBMS_OUTPUT.PUT_LINE('---- Test 1 Execution Start ----');
  
  -- Open cursor for processing
  OPEN c_reserved;
  
  -- Loop through all reserved parts
  LOOP
    -- Fetch next record into v_info variable
    FETCH c_reserved INTO v_info;
    
    -- Exit loop when no more records found
    EXIT WHEN c_reserved%NOTFOUND;
    
    -- Print information for current record
    -- Includes Part ID, Reserved Quantity, and Supplier ID (shows N/A if null)
    DBMS_OUTPUT.PUT_LINE('PartID: ' || v_info.part_id ||
                         ' | Reserved Qty: ' || v_info.quantity ||
                         ' | Supplier ID: ' || NVL(TO_CHAR(v_info.supplier_id), 'N/A'));
  END LOOP;
  
  -- Close cursor
  CLOSE c_reserved;
  
  -- Print end of test execution
  DBMS_OUTPUT.PUT_LINE('---- Test 1 Execution End ----');
END;
/