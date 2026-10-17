
DECLARE
    rc SYS_REFCURSOR;
    v_part_id VARCHAR2(50) := 'AS12947S20'; -- Utilize um PartID válido existente na nova estrutura
    v_part VARCHAR2(50);
    v_description VARCHAR2(200);
    v_quantity NUMBER;
BEGIN
    rc := get_product_parts(v_part_id);
    
    DBMS_OUTPUT.PUT_LINE('Lista de Peças para o Produto: ' || v_part_id);
    DBMS_OUTPUT.PUT_LINE('---------------------------------------------');
    DBMS_OUTPUT.PUT_LINE('PartID' || CHR(9) || 'Description' || CHR(9) || 'Quantity');
    DBMS_OUTPUT.PUT_LINE('---------------------------------------------');
    
    LOOP
        FETCH rc INTO v_part, v_description, v_quantity;
        EXIT WHEN rc%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE(v_part || CHR(9) || v_description || CHR(9) || v_quantity);
    END LOOP;
    
    CLOSE rc;
END;
/
