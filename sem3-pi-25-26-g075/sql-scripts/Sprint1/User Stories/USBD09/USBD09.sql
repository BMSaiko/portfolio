SELECT
    gauge.gauge_id       AS gauge_id,
	gauge."name"        AS gauge_name,
    locomotive."number"      AS locomotive_number,
    locomotive."name"        AS locomotive_name,
    locomotive."year"        AS build_year,
    locomotiveModel."name"       AS model_name
FROM
    Locomotive locomotive
JOIN
    LocomotiveModel locomotiveModel ON locomotive.model_id = locomotiveModel.locomotive_model_id
JOIN
    Gauge gauge ON locomotiveModel.gauge_id = gauge.gauge_id
WHERE
    gauge."name" = 'Iberian';