-- Begin block to setup test data
BEGIN
  -- Update ExternalPart table to set Reserved quantity to 10 for part PN18324C54
  -- This ensures the part has a reserved status for testing
  UPDATE ExternalPart
     SET Reserved = 10
   WHERE PartID = 'PN18324C54';

  -- Commit the changes to make them permanent
  COMMIT;
END;
/

-- Begin the test scenario execution block
DECLARE
  -- Define a record type to store reserved part information
  -- Contains fields for part ID, reserved quantity, and supplier ID
  TYPE t_reserved_info IS RECORD (
    part_id     Part.PartID%TYPE,
    quantity    ExternalPart.Reserved%TYPE,
    supplier_id supplier.SupplierID%TYPE
  );

  -- Define a cursor to fetch all reserved parts
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

  -- Declare a variable of the record type to store fetched data
  v_info t_reserved_info;

BEGIN
  -- Print start of test execution
  DBMS_OUTPUT.PUT_LINE('---- Test 3 Execution Start ----');
  -- Open the cursor to start fetching data
  OPEN c_reserved;
  -- Loop through all reserved parts
  LOOP
    -- Fetch next record into the variable
    FETCH c_reserved INTO v_info;
    -- Exit loop when no more records found
    EXIT WHEN c_reserved%NOTFOUND;
    -- Print information for each reserved part
    -- Includes Part ID, Reserved Quantity, and Supplier ID (shows N/A if null)
    DBMS_OUTPUT.PUT_LINE('PartID: ' || v_info.part_id ||
                         ' | Reserved Qty: ' || v_info.quantity ||
                         ' | Supplier ID: ' || NVL(TO_CHAR(v_info.supplier_id), 'N/A'));
  END LOOP;
  -- Close the cursor
  CLOSE c_reserved;
  -- Print end of test execution
  DBMS_OUTPUT.PUT_LINE('---- Test 3 Execution End ----');
END;
/