-- New Wagon Model (Length = 100m)
INSERT INTO WagonModel (wagon_model_id, wagon_type_id, maker_id, wagon_model_name, number_of_bogies, wagon_model_payload, max_speed, wagon_model_length, wagon_model_width, wagon_model_height, wagon_model_weight, wagon_model_volume, bogie_type_id)
VALUES (98, 1, 1, 'Wagon Test 100m', 2, 50, 120, 100, 3, 4, 20, 100, 1);

-- New Wagons (100m)
INSERT INTO Wagon (wagon_number, operator_id, start_facility_id, wagon_model_id, wagon_year)
VALUES ('123 0 111', 'PT509017800', 50, 98, 2020);

INSERT INTO Wagon (wagon_number, operator_id, start_facility_id, wagon_model_id, wagon_year)
VALUES ('123 0 222', 'PT509017800', 12, 98, 2020);

INSERT INTO Wagon (wagon_number, operator_id, start_facility_id, wagon_model_id, wagon_year)
VALUES ('123 0 333', 'PT509017800', 50, 98, 2020);

-- Freight 8801: Leixões (50) -> Darque (12)
INSERT INTO Freight (freight_id, freight_date, origin_facility_id, destination_facility_id) VALUES (8801, SYSDATE, 50, 12);
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (8801, '123 0 111');

-- Freight 8802: Darque (12) -> Caminha (21)
INSERT INTO Freight (freight_id, freight_date, origin_facility_id, destination_facility_id) VALUES (8802, SYSDATE, 12, 21);
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (8802, '123 0 222');

-- Freight 8803: Leixões (50) -> Caminha (21)
INSERT INTO Freight (freight_id, freight_date, origin_facility_id, destination_facility_id) VALUES (8803, SYSDATE, 50, 21);
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (8803, '123 0 333');


-- New Train (Max 200m)
INSERT INTO Train (train_id, operator_id, path_id, train_departure_date_time, max_length)
VALUES (9900, 'PT509017800', 1, SYSDATE, 200);

-- New Train (Max 10m)
INSERT INTO Train (train_id, operator_id, path_id, train_departure_date_time, max_length)
VALUES (9901, 'PT509017800', 1, SYSDATE, 10);

COMMIT;