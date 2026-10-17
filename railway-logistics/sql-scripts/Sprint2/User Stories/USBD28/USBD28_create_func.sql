
CREATE OR REPLACE FUNCTION LocomotiveWithMoreThanOneGaugeByOperator (
    given_operator_id IN VehicleOperator.vat_number%TYPE
) RETURN SYS_REFCURSOR
IS
    var_cursor SYS_REFCURSOR;
BEGIN
OPEN var_cursor FOR
SELECT
    locomotive.locomotive_number,
    locomotive.locomotive_name,
    locomotive_model.locomotive_model_name,
    vehicle_operator.operator_name,

    (SELECT COUNT(locomotive_model_gauge.gauge_id)
     FROM LocomotiveModelGauge locomotive_model_gauge
     WHERE locomotive_model_gauge.locomotive_model_id = locomotive_model.locomotive_model_id) AS gauge_count

FROM
    Locomotive locomotive
        JOIN
    LocomotiveModel locomotive_model ON locomotive.model_id = locomotive_model.locomotive_model_id
        JOIN
    VehicleOperator vehicle_operator ON locomotive.operator_id = vehicle_operator.vat_number
WHERE
    locomotive.operator_id = given_operator_id
  AND locomotive.model_id IN (
    SELECT
        locomotive_model_id
    FROM
        LocomotiveModelGauge
    GROUP BY
        locomotive_model_id
    HAVING
        COUNT(gauge_id) > 1
);

RETURN var_cursor;

EXCEPTION
    WHEN OTHERS THEN
        RAISE;
END LocomotiveWithMoreThanOneGaugeByOperator;
/

COMMIT;
