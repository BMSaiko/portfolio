-- Auxiiliary inserts for testing USBD39
-- 1. Locomootive Model for testing
INSERT INTO LocomotiveModel (locomotive_model_id, engine_id, bogie_type_id, maker_id, locomotive_model_name, number_of_bogies, locomotive_model_power, locomotive_model_length, locomotive_model_height, locomotive_model_width, locomotive_model_weight, maximum_speed, operational_speed, locomotive_model_traction) 
VALUES (99, 1, 3, 1, 'Test Model', 2, 5000, 20, 4, 3, 80, 200, 100, 300);

-- 2. Two locomotives for testing
INSERT INTO Locomotive (locomotive_number, locomotive_name, locomotive_year, operator_id, model_id, start_facility_id) VALUES (8801, 'Loco Teste', 2020, 'PT509017800', 99, 50);
INSERT INTO Locomotive (locomotive_number, locomotive_name, locomotive_year, operator_id, model_id, start_facility_id) VALUES (8802, 'Loco Teste 2', 2021, 'PT509017800', 99, 50);

-- 3.Train with enough space (Max = 500) -> For valid test
INSERT INTO Train (train_id, operator_id, path_id, train_departure_date_time, max_length)
VALUES (9001, 'PT509017800', 1, SYSDATE, 500);

-- 4. Train with small max length (Max = 10) -> For invalid test
INSERT INTO Train (train_id, operator_id, path_id, train_departure_date_time, max_length)
VALUES (9002, 'PT509017800', 1, SYSDATE, 10);

COMMIT;