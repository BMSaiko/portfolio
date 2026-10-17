DECLARE
  result VARCHAR2(255); -- Variable to store the result of the function
BEGIN
  -- Test the function with a valid PartID and quantity
  result := deduct_stock('PN94561L67', 0);
  DBMS_OUTPUT.PUT_LINE('Test 1: ' || result);
  
  -- Test the function where the stock will go below reserved stock
  result := deduct_stock('PN94561L67', 100);  -- assuming reserved stock is 50 and current stock is 60
  DBMS_OUTPUT.PUT_LINE('Test 2: ' || result);
  
  -- Test the function with an invalid PartID
  result := deduct_stock('INVALID_PART', 5);
  DBMS_OUTPUT.PUT_LINE('Test 3: ' || result);
END;
/