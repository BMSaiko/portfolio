-- Begin block to setup initial conditions
BEGIN
  -- Setup: Ensure PN12344A21 is not reserved by setting its Reserved field to 0
  UPDATE ExternalPart
     SET Reserved = 0
   WHERE PartID = 'PN12344A21';

  -- Commit the changes to the database
  COMMIT;
END;
/

-- Begin the main test scenario
DECLARE
  -- Define a record type to store information about reserved parts
  -- Contains fields for part ID, quantity reserved, and supplier ID
  TYPE t_reserved_info IS RECORD (
    part_id     Part.PartID%TYPE,
    quantity    ExternalPart.Reserved%TYPE,
    supplier_id supplier.SupplierID%TYPE
  );

  -- Define a cursor to fetch all reserved parts
  -- Joins ExternalPart with Offer table to get supplier information
  -- Only selects parts where Reserved quantity is greater than 0
  CURSOR c_reserved IS
    SELECT e.PartID,
           e.Reserved,
           o.SupplierID
      FROM ExternalPart e
      LEFT JOIN Offer o ON e.PartID = o.PartID
     WHERE e.Reserved > 0
     ORDER BY e.PartID;

  -- Declare a variable to store the fetched record information
  v_info t_reserved_info;

BEGIN
  -- Print start of test execution
  DBMS_OUTPUT.PUT_LINE('---- Test 4 Execution Start ----');
  
  -- Open the cursor to begin fetching records
  OPEN c_reserved;
  
  -- Loop through all reserved parts
  LOOP
    -- Fetch the next record into our variable
    FETCH c_reserved INTO v_info;
    
    -- Exit loop when no more records are found
    EXIT WHEN c_reserved%NOTFOUND;
    
    -- Print information about each reserved part
    -- If supplier ID is null, display 'N/A' instead
    DBMS_OUTPUT.PUT_LINE('PartID: ' || v_info.part_id ||
                         ' | Reserved Qty: ' || v_info.quantity ||
                         ' | Supplier ID: ' || NVL(TO_CHAR(v_info.supplier_id), 'N/A'));
  END LOOP;
  
  -- Close the cursor
  CLOSE c_reserved;
  
  -- Print end of test execution
  DBMS_OUTPUT.PUT_LINE('---- Test 4 Execution End ----');
END;
/