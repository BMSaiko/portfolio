
--teste 1- desativar cliente ativo
DECLARE
    v_result VARCHAR2(100);
BEGIN
    -- Testa com um cliente ativo
    v_result := deactivate_customer('PT501245488');
    DBMS_OUTPUT.PUT_LINE(v_result);

END;
/

    --teste 2- desativar cliente com order pendente
 INSERT INTO "Order" (
        OID,
        ClientNIF,
        DateOrder,
        DateDelivery
    ) VALUES (
        100,
        'PT501245987',
        TO_DATE('2024-09-15', 'YYYY-MM-DD'),
        TO_DATE('2024-12-25', 'YYYY-MM-DD')
    );

DECLARE
    v_result VARCHAR2(100);
BEGIN
    v_result := deactivate_customer('PT501245987');
    DBMS_OUTPUT.PUT_LINE(v_result);

END;
/

    --teste 3- desativar cliente ja desativado

    UPDATE Client
    SET Status = 0
    WHERE NIF = 'CZ6451237810';

DECLARE
    v_result VARCHAR2(100);
BEGIN

    v_result := deactivate_customer('CZ6451237810');
    DBMS_OUTPUT.PUT_LINE(v_result);

END;
/


    --teste 4- ID invalido
DECLARE
    v_result VARCHAR2(100);
BEGIN

	-- Testa com um cliente que nao existe
    v_result := deactivate_customer('INEXISTENTE');
    DBMS_OUTPUT.PUT_LINE(v_result);

END;
/