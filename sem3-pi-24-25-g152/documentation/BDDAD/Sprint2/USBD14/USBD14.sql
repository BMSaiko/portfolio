CREATE OR REPLACE FUNCTION get_products_using_all_workstation_types
RETURN SYS_REFCURSOR
AS
    result_cursor SYS_REFCURSOR;
BEGIN
    OPEN result_cursor FOR
    SELECT Product.PartID
    FROM Product
    WHERE NOT EXISTS (
        SELECT WTID
        FROM WorkstationTypes
        MINUS
    
        SELECT DISTINCT WorkstationTypes.WTID
        FROM BOO
        JOIN BOO_Operation ON BOO.BOOID = BOO_Operation.BOOID
        JOIN Operation ON BOO_Operation.OPID = Operation.OPID
    	JOIN OperationType ON Operation.OperationTypeOTID = OperationType.OTID
    	JOIN WSTypes_OperationTypes ON OperationType.OTID = WSTypes_OperationTypes.OperationTypeOTID
        JOIN WorkstationTypes ON WSTypes_OperationTypes.WTID = WorkstationTypes.WTID
        WHERE BOO.PartID = Product.PartID
    );

    RETURN result_cursor;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Nenhum produto encontrado que utilize todos os tipos de estações de trabalho.');
        RETURN NULL;
END;
/


    
DECLARE
    products_cursor SYS_REFCURSOR;
    product_id Product.PartID%TYPE;
    v_no_data_found BOOLEAN := TRUE;
BEGIN
    products_cursor := get_products_using_all_workstation_types;

    LOOP
        FETCH products_cursor INTO product_id;
        EXIT WHEN products_cursor%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('Produto que utiliza todas as máquinas: ' || product_id);
		v_no_data_found := FALSE;
    END LOOP;
	    IF v_no_data_found THEN
            DBMS_OUTPUT.PUT_LINE('Nenhum produto encontrado que utilize todos os tipos de estações de trabalho.');
        END IF;
        
    CLOSE products_cursor;
END;
/