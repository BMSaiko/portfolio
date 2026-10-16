-- 1. Create Valid Wagons at Origin (Leixões - 50)
INSERT INTO Wagon (wagon_number, operator_id, start_facility_id, wagon_model_id, wagon_year)
VALUES ('123 1 000', 'PT509017800', 50, 1104, 2020);

INSERT INTO Wagon (wagon_number, operator_id, start_facility_id, wagon_model_id, wagon_year)
VALUES ('123 4 000', 'PT509017800', 50, 1104, 2020);

INSERT INTO Wagon (wagon_number, operator_id, start_facility_id, wagon_model_id, wagon_year)
VALUES ('123 5 000', 'PT509017800', 50, 1104, 2020);

-- 2. Create a Wagon at Wrong Facility (Darque - 12) -> '123 2 000'
INSERT INTO Wagon (wagon_number, operator_id, start_facility_id, wagon_model_id, wagon_year)
VALUES ('123 2 000', 'PT509017800', 12, 1104, 2020);

-- 3. Create a Wagon that is already Busy -> '123 3 000'
INSERT INTO Wagon (wagon_number, operator_id, start_facility_id, wagon_model_id, wagon_year)
VALUES ('123 3 000', 'PT509017800', 50, 1104, 2020);

-- Make it busy by assigning it to an existing Freight
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (2001, '123 3 000');

COMMIT;