CREATE OR REPLACE FUNCTION deduct_stock (
  p_partid IN VARCHAR2,
  p_quantity IN NUMBER
) RETURN VARCHAR2 IS
  v_current_stock NUMBER;
  v_reserved_stock NUMBER;
  v_new_stock NUMBER;
BEGIN
  -- Fetch the current stock and reserved quantity for the given part
  SELECT stock, reserved INTO v_current_stock, v_reserved_stock
  FROM ExternalPart
  WHERE PartID = p_partid;
  
  -- Check if the stock after deduction will go below the reserved quantity
  IF v_current_stock - p_quantity < v_reserved_stock THEN
    RETURN 'Operation cannot be performed. Remaining stock is below the reserved quantity.';
  END IF;

  -- Deduct the quantity from stock
  v_new_stock := v_current_stock - p_quantity;

  -- Update the stock in the ExternalPart table
  UPDATE ExternalPart
  SET stock = v_new_stock
  WHERE PartID = p_partid;

  -- Commit the transaction
  COMMIT;

  RETURN 'Stock deduction successful.';
EXCEPTION
  WHEN NO_DATA_FOUND THEN
    RETURN 'PartID not found.';
  WHEN OTHERS THEN
    -- Handle other exceptions
    RETURN 'An error occurred: ' || SQLERRM;
END deduct_stock;
/