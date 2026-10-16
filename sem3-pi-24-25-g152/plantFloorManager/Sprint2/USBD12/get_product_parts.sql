CREATE OR REPLACE FUNCTION get_product_parts(p_part_id IN VARCHAR2) RETURN SYS_REFCURSOR IS
    -- Cursor para armazenar e retornar os resultados da consulta
    rc SYS_REFCURSOR;
BEGIN
    -- Abrir cursor com consulta recursiva para obter todas as partes e suas quantidades
    OPEN rc FOR
        WITH parts_cte (PartID, Description, Quantity) AS (
            -- Caso base: Obter partes diretamente usadas no produto
            -- Une BOO com operações e suas partes de entrada
            SELECT
                pi.PartID,
                p.Description,
                pi.QuantityIn
            FROM
                BOO b
                JOIN BOO_Operation bo ON b.BOOID = bo.BOOID
                JOIN PartIN pi ON bo.OPID = pi.OPID AND bo.BOOID = pi.BOOID
                JOIN Part p ON pi.PartID = p.PartID
            WHERE
                b.PartID = p_part_id  -- Corrigido para usar PartID

            UNION ALL

            -- Caso recursivo: Obter partes usadas em subprodutos
            SELECT
                pi.PartID,
                p.Description,
                parts_cte.Quantity * pi.QuantityIn
            FROM
                parts_cte
                JOIN BOO b ON b.PartID = parts_cte.PartID  -- Corrigido para usar PartID
                JOIN BOO_Operation bo ON b.BOOID = bo.BOOID
                JOIN PartIN pi ON bo.OPID = pi.OPID AND bo.BOOID = pi.BOOID
                JOIN Part p ON pi.PartID = p.PartID
        )
        -- Seleção final agrega todas as quantidades para cada parte
        SELECT
            PartID,
            Description,
            SUM(Quantity) AS TotalQuantity
        FROM
            parts_cte
        GROUP BY
            PartID,
            Description
        ORDER BY
            PartID;
    
    RETURN rc;
END;
/
