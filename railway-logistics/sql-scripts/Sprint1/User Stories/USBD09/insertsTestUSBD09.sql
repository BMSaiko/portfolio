--novo gauge AMERICAN
INSERT INTO Gauge (gauge_id, "name", width) VALUES (3, 'American', 1857);

--novo modelo elétrico em gauge STANDARD
INSERT INTO LocomotiveModel (locomotive_model_id, gauge_id, engine_id, power, maximum_speed, "length", "weight",maker_id, "name", number_of_bogies, bogie_type_id, height, operational_speed, traction, width) VALUES (3, 2, 1, 6400, 230, 20, 84, 5, 'Vectron', 2, 1, 4, 90, 320, 3);

--novo modelo diesel em gauge STANDARD
INSERT INTO LocomotiveModel (locomotive_model_id, gauge_id, engine_id, power, maximum_speed, "length", "weight",maker_id, "name", number_of_bogies, bogie_type_id, height, operational_speed, traction, width) VALUES (4, 2, 2, 2200, 120, 18, 112, 6, 'Euro4000', 2, 2, 4, 75, 400, 3);

--novo modelo elétrico (gauge American)
INSERT INTO LocomotiveModel (locomotive_model_id, gauge_id, engine_id, power, maximum_speed, "length", "weight",maker_id, "name", number_of_bogies, bogie_type_id, height, operational_speed, traction, width) VALUES (5, 3, 1, 6800, 200, 21, 90, 5, 'GE AC44i', 2, 2, 4, 85, 430, 3);

--novo modelo diesel (gauge American)
INSERT INTO LocomotiveModel (locomotive_model_id, gauge_id, engine_id, power, maximum_speed, "length", "weight",maker_id, "name", number_of_bogies, bogie_type_id, height, operational_speed, traction, width) VALUES (6, 3, 2, 3300, 120, 22, 120, 6, 'EMD SD70', 3, 2, 5, 80, 500, 3);

--locomotivas elétricas (American)
INSERT INTO Locomotive ("number", "name", "year", operator_id, model_id)VALUES (4401, 'Liberty', 2012, 'PT509017800', 5);

INSERT INTO Locomotive ("number", "name", "year", operator_id, model_id)VALUES (4402, 'Freedom', 2013, 'PT509017800', 5);

--locomotivas diesel (American)
INSERT INTO Locomotive ("number", "name", "year", operator_id, model_id)VALUES (7001, 'Eagle', 2010, 'PT509017800', 6);

INSERT INTO Locomotive ("number", "name", "year", operator_id, model_id)VALUES (7002, 'Patriot', 2011, 'PT509017800', 6);

--locomotivas associadas ao modelo "Vectron" (gauge: Standard)
INSERT INTO Locomotive ("number", "name", "year", operator_id, model_id)VALUES (4701, 'Luz', 2019, 'PT509017800', 3);

INSERT INTO Locomotive ("number", "name", "year", operator_id, model_id)VALUES (4702, 'Ria', 2020, 'PT509017800', 3);

--locomotivas associadas ao modelo "Euro4000" (gauge: Standard)
INSERT INTO Locomotive ("number", "name", "year", operator_id, model_id)VALUES (4001, 'Gaia', 2015, 'PT509017800', 4);

INSERT INTO Locomotive ("number", "name", "year", operator_id, model_id)VALUES (4002, 'Sado', 2016, 'PT509017800', 4);

--locomotivas elétricas em Iberian para teste
INSERT INTO Locomotive ("number", "name", "year", operator_id, model_id)VALUES (5632, 'Maria', 1997, 'PT509017800', 1);

INSERT INTO Locomotive ("number", "name", "year", operator_id, model_id)VALUES (5633, 'Laura', 1998, 'PT509017800', 1);
