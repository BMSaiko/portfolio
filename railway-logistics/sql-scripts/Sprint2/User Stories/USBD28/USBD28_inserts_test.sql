
-- Com os dados já existentes na base de dados, inserir os dados adicionais necessários para testar a USBD28.
INSERT INTO LocomotiveModelGauge (locomotive_model_id, gauge_id) VALUES (1, 2);
--------------------------------------------------------------------------------------------------------------
-- DADOS ADICIONAIS PARA TESTE DA USBD28
--------------------------------------------------------------------------------------------------------------

--nova gauge
INSERT INTO Gauge (gauge_id, gauge_name, gauge_width) VALUES (3, 'Metre Gauge', 1000);

-- operator
INSERT INTO VehicleOperator (vat_number, operator_name, short_name) VALUES ('PT508123456', 'DHL, S.A.', 'DHL');

SELECT * FROM ENGINE;


-- Adicionar um novo motor para o modelo
INSERT INTO Engine (engine_id, engine_type_id) VALUES (4, 2); -- Motor a Diesel
INSERT INTO DieselEngine (engine_id, fuel_capacity) VALUES (4, 7000);


-- Criar o novo modelo de locomotive
INSERT INTO LocomotiveModel (locomotive_model_id, engine_id, bogie_type_id, maker_id, locomotive_model_name, number_of_bogies, locomotive_model_power, locomotive_model_length, locomotive_model_height, locomotive_model_width, locomotive_model_weight, maximum_speed, operational_speed, locomotive_model_traction) VALUES (4, 4, 3, 7, 'Vossloh Euro 4000', 2, 3178.0, 23, 4, 3, 123, 120, 80, 400);
INSERT INTO LocomotiveModel (locomotive_model_id, engine_id, bogie_type_id, maker_id, locomotive_model_name, number_of_bogies, locomotive_model_power, locomotive_model_length, locomotive_model_height, locomotive_model_width, locomotive_model_weight, maximum_speed, operational_speed, locomotive_model_traction) VALUES (5, 1, 3, 5, 'EMD Class 66', 2, 2460.0, 21, 4, 3, 129, 120, 70, 408);
INSERT INTO LocomotiveModel (locomotive_model_id, engine_id, bogie_type_id, maker_id, locomotive_model_name, number_of_bogies, locomotive_model_power, locomotive_model_length, locomotive_model_height, locomotive_model_width, locomotive_model_weight, maximum_speed, operational_speed, locomotive_model_traction) VALUES (6, 4, 4, 7, 'Alstom Prima H4', 2, 4000.0, 19, 4, 3, 90, 160, 100, 320);

-- Associar o novo modelo de locomotive a mais de uma gauge
INSERT INTO LocomotiveModelGauge (locomotive_model_id, gauge_id) VALUES (4, 1);
INSERT INTO LocomotiveModelGauge (locomotive_model_id, gauge_id) VALUES (4, 2);
INSERT INTO LocomotiveModelGauge (locomotive_model_id, gauge_id) VALUES (5, 1);
INSERT INTO LocomotiveModelGauge (locomotive_model_id, gauge_id) VALUES (6, 1);
INSERT INTO LocomotiveModelGauge (locomotive_model_id, gauge_id) VALUES (6, 2);
INSERT INTO LocomotiveModelGauge (locomotive_model_id, gauge_id) VALUES (6, 3);

-- criar a nova locomotive da Captrain usando o novo modelo
INSERT INTO Locomotive (locomotive_number, locomotive_name, locomotive_year, operator_id, model_id) VALUES (4001, 'Santiago', 2008, 'PT507832388', 4); -- 'PT507832388' é o NIF da Captrain
INSERT INTO Locomotive (locomotive_number, locomotive_name, locomotive_year, operator_id, model_id) VALUES (6601, 'Pioneer', 2009, 'PT508123456', 5); -- 'PT508123456' é o NIF da DHL
INSERT INTO Locomotive (locomotive_number, locomotive_name, locomotive_year, operator_id, model_id) VALUES (4005, 'Versatile', 2020, 'PT509017800', 6); -- 'PT509017800' é o NIF da Medway

COMMIT;

