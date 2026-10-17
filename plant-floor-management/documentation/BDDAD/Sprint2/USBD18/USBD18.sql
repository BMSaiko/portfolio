

CREATE OR REPLACE FUNCTION deactivate_customer(p_nif Client.NIF%TYPE)
RETURN VARCHAR2
AS
    v_status Client.Status%TYPE;
    v_pending_orders NUMBER;
BEGIN
    -- Verifica se o cliente existe e obtém seu status
    SELECT Status INTO v_status
    FROM Client
    WHERE Client.NIF = p_nif;

    -- Verifica se o cliente já está desativado
    IF v_status = 0 THEN
        RETURN 'Erro: O cliente já está desativado.';
    END IF;

    -- Verifica se há pedidos pendentes para o cliente
    SELECT COUNT(*)
    INTO v_pending_orders
    FROM "Order"
    WHERE ClientNIF = p_nif
      AND (DateDelivery > SYSDATE OR DateDelivery IS NULL);

    -- Se houver pedidos pendentes, retorna uma mensagem de erro
    IF v_pending_orders > 0 THEN
        RETURN 'Erro: O cliente tem pedidos pendentes e não pode ser desativado.';
    END IF;

    -- Atualiza o status do cliente para 0 (inativo)
    UPDATE Client
    SET Status = 0
    WHERE NIF = p_nif;

    -- Confirma a atualização
    COMMIT;

    RETURN 'Sucesso: Cliente desativado com sucesso.';

EXCEPTION
    -- Captura o erro se o cliente não for encontrado
    WHEN NO_DATA_FOUND THEN
        RETURN 'Erro: Cliente com o NIF fornecido não encontrado.';
    -- Captura outros erros genéricos
    WHEN OTHERS THEN
        RETURN 'Erro: Não foi possível desativar o cliente devido a um problema inesperado.';
END;
/
    



DECLARE
    v_result VARCHAR2(100);
BEGIN
    -- Testa com um cliente ativo
    v_result := deactivate_customer('PT501245987');
    DBMS_OUTPUT.PUT_LINE(v_result);

	-- Testa com um cliente que nao existe
    v_result := deactivate_customer('INEXISTENTE');
    DBMS_OUTPUT.PUT_LINE(v_result);

	-- Testa com um cliente que tenha status inativo (cliente que acabei de mudar o status para 0)
    v_result := deactivate_customer('PT501245987');
    DBMS_OUTPUT.PUT_LINE(v_result);

END;
/