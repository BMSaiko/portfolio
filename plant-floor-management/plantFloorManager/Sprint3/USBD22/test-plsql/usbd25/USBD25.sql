CREATE OR REPLACE FUNCTION get_product_operations (
    p_productID IN VARCHAR2
) RETURN SYS_REFCURSOR
IS
    v_cursor SYS_REFCURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT
            bo.BOOID                 AS boo_id,
            op.OPID                  AS operation_id,
            op.Name                  AS operation_description,
            op.ExpectedExecutionTime AS execution_time,

            pi.PartID    AS input_part_id,
            pi.QuantityIn AS input_quantity,

            po.PartID    AS output_part_id,
            po.Quantity  AS output_quantity

        FROM BOO_Operation bo
        JOIN Operation op
             ON bo.OPID = op.OPID

        LEFT JOIN PartIN pi
               ON bo.OPID  = pi.OPID
              AND bo.BOOID = pi.BOOID

        LEFT JOIN PartOut po
               ON bo.OPID  = po.OPID
              AND bo.BOOID = po.BOOID

        WHERE bo.BOOID = p_productID
           OR bo.BOOID IN (
               SELECT PartID
                 FROM IntermediateProduct
           )
        ORDER BY op.OPID, bo.BOOID;

    RETURN v_cursor;
END;
/
DECLARE
    v_cursor         SYS_REFCURSOR;
    v_boo_id         VARCHAR2(50);
    v_operation_id   NUMBER;
    v_operation_desc VARCHAR2(255);
    v_exec_time      NUMBER;
    v_in_part_id     VARCHAR2(50);
    v_in_qty         NUMBER;
    v_out_part_id    VARCHAR2(50);
    v_out_qty        NUMBER;
BEGIN
    v_cursor := get_product_operations('AS12946S20');

    LOOP
        FETCH v_cursor INTO
            v_boo_id,
            v_operation_id,
            v_operation_desc,
            v_exec_time,
            v_in_part_id,
            v_in_qty,
            v_out_part_id,
            v_out_qty;

        EXIT WHEN v_cursor%NOTFOUND;

        DBMS_OUTPUT.PUT_LINE(
              'BOO = ' || v_boo_id
            || ' | OPID = ' || v_operation_id
            || ' | Desc = ' || v_operation_desc
            || ' | Time = ' || NVL(TO_CHAR(v_exec_time), 'NULL')
            || ' | IN = '   || NVL(v_in_part_id, 'NULL')
            || ' (QtyIn='   || NVL(TO_CHAR(v_in_qty), '0') || ')'
            || ' | OUT = '  || NVL(v_out_part_id, 'NULL')
            || ' (QtyOut='  || NVL(TO_CHAR(v_out_qty), '0') || ')'
        );
    END LOOP;

    CLOSE v_cursor;
END;
/
