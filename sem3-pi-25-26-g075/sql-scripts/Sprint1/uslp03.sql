--original tables

CREATE TABLE "Line" (
                        line_id integer                 CONSTRAINT pkLineID PRIMARY KEY,
                        owner_id char(11)               CONSTRAINT nnLineOwnerID NOT NULL,
                        start_station_id integer        CONSTRAINT nnLineStartStationID NOT NULL,
                        end_station_id integer          CONSTRAINT nnLineEndStationID NOT NULL,
                        gauge_id integer                CONSTRAINT nnLineGaugeID NOT NULL,
                        "name" varchar(255)             CONSTRAINT nnLineName NOT NULL
);


CREATE TABLE LineSegment (
                             line_segment_id integer         CONSTRAINT pkLineSegmentID PRIMARY KEY,
                             line_id integer                 CONSTRAINT nnLineSegmentLineID NOT NULL,
                             is_electrified varchar(3)       CONSTRAINT nnLineSegmentIsElectrified NOT NULL,
                             maximum_weight integer          CONSTRAINT nnLineSegmentMaximumWeight NOT NULL,
                             "length" integer                CONSTRAINT nnLineSegmentLength NOT NULL,
                             number_of_tracks integer        CONSTRAINT nnLineSegmentNumberofTracks NOT NULL,
                             "order" integer                 CONSTRAINT nnLineSegmentOrder NOT NULL,
    -- new columns for uslp03
                             start_station_id integer        CONSTRAINT nnLineSegmentStartStation NOT NULL,
                             end_station_id integer          CONSTRAINT nnLineSegmentEndStation NOT NULL,
                             speed_limit_kmh integer         CONSTRAINT nnLineSegmentSpeedLimit NOT NULL
);



CREATE TABLE Station (
                         station_id integer              CONSTRAINT pkStationID PRIMARY KEY,
                         "name" varchar(255)             CONSTRAINT nnStationName NOT NULL
);

CREATE TABLE Locomotive (
                            "number" integer                CONSTRAINT pkLocomotiveNumber PRIMARY KEY,
                            "name" varchar(255)             CONSTRAINT nnLocomotiveName NOT NULL,
                            "year" integer                  CONSTRAINT nnLocomotiveYear NOT NULL,
                            operator_id char(11)            CONSTRAINT nnLocomotiveOperatorID NOT NULL,
                            model_id integer                CONSTRAINT nnLocomotiveModelID NOT NULL
);

CREATE TABLE Wagon (
                       "number" varchar(9)             CONSTRAINT pkWagonNumber PRIMARY KEY,
                       operator_id char(11)            CONSTRAINT nnWagonOperatorID NOT NULL,
                       wagon_model_id integer          CONSTRAINT nnWagonWagonModelID NOT NULL,
                       "year" integer                  CONSTRAINT nnABCYear NOT NULL
);

CREATE TABLE DieselEngine (
                              engine_id integer               CONSTRAINT pkDieselEngineEngineID PRIMARY KEY,
                              fuel_capacity integer           CONSTRAINT nnDieselEngineFuelCapacity NOT NULL
);

CREATE TABLE EngineType (
                            engine_type_id integer              CONSTRAINT pkEngineTypeID PRIMARY KEY,
                            "name" varchar(255)                 CONSTRAINT nnLocomotiveEngineTypeName NOT NULL
);

CREATE TABLE WagonType (
                           wagon_type_id integer           CONSTRAINT pkWagonTypeID PRIMARY KEY,
                           "name" varchar(255)             CONSTRAINT nnWagonTypeName NOT NULL
);

CREATE TABLE Gauge (
                       gauge_id integer                CONSTRAINT pkGaugeID PRIMARY KEY,
                       "name" varchar(255)             CONSTRAINT nnGaugeName NOT NULL,
                       width integer                   CONSTRAINT nnGaugeWidth NOT NULL
);

CREATE TABLE "Owner" (
                         vat_number char(11)             CONSTRAINT pkOwnerVATNumber PRIMARY KEY,
                         "name" varchar(255)             CONSTRAINT nnOwnerName NOT NULL,
                         short_name varchar(255)         CONSTRAINT nnOwnerShortName NOT NULL
);

CREATE TABLE Operator (
                          vat_number char(11)             CONSTRAINT pkOperatorVATNumber PRIMARY KEY,
                          "name" varchar(255)             CONSTRAINT nnOperatorName NOT NULL,
                          short_name varchar(255)         CONSTRAINT nnOperatorShortName NOT NULL
);

CREATE TABLE WagonModel (
                            wagon_model_id integer          CONSTRAINT pkWagonModelID PRIMARY KEY,
                            gauge_id integer                CONSTRAINT nnWagonModelGaugeID NOT NULL,
                            wagon_type_id integer           CONSTRAINT nnWagonModelWagonTypeID NOT NULL,
                            bogie varchar(255)              CONSTRAINT nnWagonModelBogie NOT NULL,
                            "name" varchar(255)             CONSTRAINT nnWagonModelName NOT NULL,
                            number_of_bogies integer        CONSTRAINT nnWagonModelNumberOfBogies NOT NULL,
                            payload integer                 CONSTRAINT nnWagonModelPayload NOT NULL,
                            maximum_speed integer           CONSTRAINT nnWagonModelMaximumSpeed NOT NULL,
                            "length" integer                CONSTRAINT nnWagonModelLength NOT NULL,
                            width integer                   CONSTRAINT nnWagonModelWidth NOT NULL,
                            height integer                  CONSTRAINT nnWagonModelHeight NOT NULL,
                            volume integer                  CONSTRAINT nnWagonModelVolume NOT NULL,
                            maker varchar(255)              CONSTRAINT nnWagonModelMaker NOT NULL,
                            tare integer                    CONSTRAINT nnWagonModelTare NOT NULL
);

CREATE TABLE ElectricEngine (
                                engine_id integer               CONSTRAINT pkElectricEngineEngineID PRIMARY KEY,
                                voltage integer                 CONSTRAINT nnElectricEngineVoltage NOT NULL,
                                frequency integer               CONSTRAINT nnElectricEngineFrequency NOT NULL
);

CREATE TABLE LocomotiveModel (
                                 locomotive_model_id integer     CONSTRAINT pkLocomotiveModelID PRIMARY KEY,
                                 "name" varchar(255)             CONSTRAINT nnLocomotiveModelName NOT NULL,
                                 gauge_id integer                CONSTRAINT nnLocomotiveModelGauge NOT NULL,
                                 engine_id integer               CONSTRAINT nnLocomotiveModelEngineID NOT NULL,
                                 bogie varchar(5)                CONSTRAINT nnLocomotiveModelBogie NOT NULL,
                                 maker varchar(255)               CONSTRAINT nnLocomotiveModelMaker NOT NULL,
                                 power integer                   CONSTRAINT nnLocomotiveModelPower NOT NULL,
                                 "weight" integer                  CONSTRAINT nnLocomotiveModelWeight NOT NULL,
                                 number_of_bogies integer        CONSTRAINT nnLocomotiveModelNumberOfBogies NOT NULL,
                                 "length" integer                CONSTRAINT nnLocomotiveModelLength NOT NULL,
                                 width integer                   CONSTRAINT nnLocomotiveModelWidth NOT NULL,
                                 height integer                  CONSTRAINT nnLocomotiveModelHeight NOT NULL,
                                 maximum_speed integer           CONSTRAINT nnLocomotiveModelMaximumSpeed NOT NULL,
                                 operational_speed integer       CONSTRAINT nnLocomotiveModelOperationalSpeed NOT NULL,
                                 traction integer                CONSTRAINT nnLocomotiveModelTraction NOT NULL
);

CREATE TABLE Engine (
                        engine_id integer               CONSTRAINT pkEngineEngineID PRIMARY KEY,
                        engine_type_id integer          CONSTRAINT nnEngineEngineTypeID NOT NULL
);



ALTER TABLE LineSegment ADD CONSTRAINT fkLineSegmentLine
    FOREIGN KEY (line_id) REFERENCES "Line"(line_id);

ALTER TABLE LineSegment ADD CONSTRAINT fkLineSegmentStartStation
    FOREIGN KEY (start_station_id) REFERENCES Station(station_id);

ALTER TABLE LineSegment ADD CONSTRAINT fkLineSegmentEndStation
    FOREIGN KEY (end_station_id) REFERENCES Station(station_id);



INSERT INTO "Owner" (vat_number, "name", short_name) VALUES ('PT503933813', 'Infraestruturas de Portugal, SA', 'IP');
INSERT INTO Operator (vat_number, "name", short_name) VALUES ('PT509017800', 'Medway - Operador Ferroviário de Mercadorias, S.A', 'Medway');

INSERT INTO Station (station_id, "name") VALUES (1, 'São Romão');
INSERT INTO Station (station_id, "name") VALUES (2, 'Tamel');
INSERT INTO Station (station_id, "name") VALUES (3, 'Senhora das Dores');
INSERT INTO Station (station_id, "name") VALUES (4, 'Lousado');
INSERT INTO Station (station_id, "name") VALUES (5, 'Porto Campanhã');
INSERT INTO Station (station_id, "name") VALUES (6, 'Leandro');
INSERT INTO Station (station_id, "name") VALUES (7, 'Porto São Bento');
INSERT INTO Station (station_id, "name") VALUES (8, 'Barcelos');
INSERT INTO Station (station_id, "name") VALUES (9, 'Vila Nova da Cerveira');
INSERT INTO Station (station_id, "name") VALUES (10, 'Midões');
INSERT INTO Station (station_id, "name") VALUES (11, 'Valença');
INSERT INTO Station (station_id, "name") VALUES (12, 'Darque');
INSERT INTO Station (station_id, "name") VALUES (13, 'Contumil');
INSERT INTO Station (station_id, "name") VALUES (14, 'Ermesinde');
INSERT INTO Station (station_id, "name") VALUES (15, 'São Frutuoso');
INSERT INTO Station (station_id, "name") VALUES (16, 'São Pedro da Torre');
INSERT INTO Station (station_id, "name") VALUES (17, 'Viana do Castelo');
INSERT INTO Station (station_id, "name") VALUES (18, 'Famalicão');
INSERT INTO Station (station_id, "name") VALUES (19, 'Barroselas');
INSERT INTO Station (station_id, "name") VALUES (20, 'Nine');
INSERT INTO Station (station_id, "name") VALUES (21, 'Caminha');
INSERT INTO Station (station_id, "name") VALUES (22, 'Carvalha');
INSERT INTO Station (station_id, "name") VALUES (23, 'Carreço');

INSERT INTO Gauge (gauge_id, "name", width) VALUES (1, 'Iberian', 1668);
INSERT INTO Gauge (gauge_id, "name", width) VALUES (2, 'Standard', 1435);

INSERT INTO EngineType (engine_type_id, "name") VALUES (1, 'Electric');
INSERT INTO EngineType (engine_type_id, "name") VALUES (2, 'Diesel');

INSERT INTO WagonType (wagon_type_id, "name") VALUES (1, 'Cereal wagon');
INSERT INTO WagonType (wagon_type_id, "name") VALUES (2, 'Covered wagon with sliding door');
INSERT INTO WagonType (wagon_type_id, "name") VALUES (3, 'Container wagon (max 40'''' HC)');

INSERT INTO "Line" (line_id, owner_id, start_station_id, end_station_id, gauge_id, "name")
VALUES (1, 'PT503933813', 7, 5, 1, 'Ramal São Bento - Campanhã');
INSERT INTO "Line" (line_id, owner_id, start_station_id, end_station_id, gauge_id, "name")
VALUES (2, 'PT503933813', 5, 20, 1, 'Ramal Camapanhã - Nine');
INSERT INTO "Line" (line_id, owner_id, start_station_id, end_station_id, gauge_id, "name")
VALUES (3, 'PT503933813', 20, 8, 1, 'Ramal Nine - Barcelos');
INSERT INTO "Line" (line_id, owner_id, start_station_id, end_station_id, gauge_id, "name")
VALUES (4, 'PT503933813', 8, 17, 1, 'Ramal Barcelos - Viana');
INSERT INTO "Line" (line_id, owner_id, start_station_id, end_station_id, gauge_id, "name")
VALUES (5, 'PT503933813', 17, 21, 1, 'Ramal viana - Caminha');
INSERT INTO "Line" (line_id, owner_id, start_station_id, end_station_id, gauge_id, "name")
VALUES (6, 'PT503933813', 21, 16, 1, 'Ramal Caminha - Torre');
INSERT INTO "Line" (line_id, owner_id, start_station_id, end_station_id, gauge_id, "name")
VALUES (7, 'PT503933813', 16, 11, 1, 'Ramal Torre - Valença');

INSERT INTO Engine (engine_id, engine_type_id) VALUES (1, 1);
INSERT INTO Engine (engine_id, engine_type_id) VALUES (2, 2);

INSERT INTO ElectricEngine (engine_id, voltage, frequency) VALUES (1, 25, 50);
INSERT INTO DieselEngine (engine_id, fuel_capacity) VALUES (2, '4882');

INSERT INTO WagonModel (wagon_model_id, gauge_id, wagon_type_id, bogie, "name", number_of_bogies, payload, maximum_speed, "length", width, height, volume, maker, tare)
VALUES (1245, 1, 1, 'Duplo', 'Tadgs 32 94 082 3', 2, 56, 120, 17240, 3072, 4270, 75, 'Metalsines', 24);
INSERT INTO WagonModel (wagon_model_id, gauge_id, wagon_type_id, bogie, "name", number_of_bogies, payload, maximum_speed, "length", width, height, volume, maker, tare)
VALUES (1278, 1, 1, 'Duplo', 'Tdgs 41 94 074 1', 2, 26, 100, 9640, 3120, 4165, 38, 'Equimetal', 13);
INSERT INTO WagonModel (wagon_model_id, gauge_id, wagon_type_id, bogie, "name", number_of_bogies, payload, maximum_speed, "length", width, height, volume, maker, tare)
VALUES (1325, 1, 2, 'Duplo', 'Gabs 81 94 181 1', 2, 50, 100, 21700, 3180, 4170, 110, 'Sepsa Cometna', 29);
INSERT INTO WagonModel (wagon_model_id, gauge_id, wagon_type_id, bogie, "name", number_of_bogies, payload, maximum_speed, "length", width, height, volume, maker, tare)
VALUES (1104, 1, 3, 'Duplo', 'Regmms 32 94 356 3', 2, 60, 120, 14040, 3104, 2535, 76, 'Metalsines', 21);
INSERT INTO WagonModel (wagon_model_id, gauge_id, wagon_type_id, bogie, "name", number_of_bogies, payload, maximum_speed, "length", width, height, volume, maker, tare)
VALUES (985, 1, 3, 'Simples', 'Lgs 22 94 441 6', 1, 28, 120, 13860, 2850, 1060, 76, 'Metalsines', 11);
INSERT INTO WagonModel (wagon_model_id, gauge_id, wagon_type_id, bogie, "name", number_of_bogies, payload, maximum_speed, "length", width, height, volume, maker, tare)
VALUES (987, 1, 3, 'Duplo', 'Sgnss 12 94 455 2', 2, 68, 120, 18116, 2950, 1030, 76, 'Emef', 21);
INSERT INTO WagonModel (wagon_model_id, gauge_id, wagon_type_id, bogie, "name", number_of_bogies, payload, maximum_speed, "length", width, height, volume, maker, tare)
VALUES (988, 2, 3, 'Duplo', 'Sgnss 12 94 455 2', 2, 68, 120, 18116, 2950, 1030, 76, 'Emef', 21);



-- inserts for linesegment, start station, end station, speed limit

-- Line 1: Porto São Bento (7) → Porto Campanhã (5) - Single segment
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, "length", number_of_tracks, "order", start_station_id, end_station_id, speed_limit_kmh)
VALUES (1, 1, 'Yes', 8000, 2618, 4, 1, 7, 5, 80);

-- Line 2: Porto Campanhã (5) → Nine (20) - 2 segments via Ermesinde (14)
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, "length", number_of_tracks, "order", start_station_id, end_station_id, speed_limit_kmh)
VALUES (10, 2, 'Yes', 8000, 29003, 2, 1, 5, 14, 120);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, "length", number_of_tracks, "order", start_station_id, end_station_id, speed_limit_kmh)
VALUES (11, 2, 'Yes', 8000, 10000, 2, 2, 14, 20, 120);

-- Line 3: Nine (20) → Barcelos (8) - 2 segments via Famalicão (18)
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, "length", number_of_tracks, "order", start_station_id, end_station_id, speed_limit_kmh)
VALUES (15, 3, 'Yes', 8000, 5286, 2, 1, 20, 18, 120);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, "length", number_of_tracks, "order", start_station_id, end_station_id, speed_limit_kmh)
VALUES (16, 3, 'Yes', 8000, 6000, 2, 2, 18, 8, 120);

-- Line 4: Barcelos (8) → Viana do Castelo (17) - 3 segments via Barroselas (19) and Darque (12)
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, "length", number_of_tracks, "order", start_station_id, end_station_id, speed_limit_kmh)
VALUES (14, 4, 'Yes', 8000, 10387, 2, 1, 8, 19, 120);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, "length", number_of_tracks, "order", start_station_id, end_station_id, speed_limit_kmh)
VALUES (12, 4, 'Yes', 8000, 12000, 2, 2, 19, 12, 100);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, "length", number_of_tracks, "order", start_station_id, end_station_id, speed_limit_kmh)
VALUES (13, 4, 'Yes', 6400, 8000, 2, 3, 12, 17, 100);

-- Line 5: Viana (17) → Caminha (21) - 3 segments via Carreço (23) and Midões (10)
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, "length", number_of_tracks, "order", start_station_id, end_station_id, speed_limit_kmh)
VALUES (20, 5, 'Yes', 8000, 6000, 2, 1, 17, 23, 100);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, "length", number_of_tracks, "order", start_station_id, end_station_id, speed_limit_kmh)
VALUES (21, 5, 'Yes', 8000, 3000, 2, 2, 23, 10, 100);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, "length", number_of_tracks, "order", start_station_id, end_station_id, speed_limit_kmh)
VALUES (22, 5, 'Yes', 8000, 15000, 2, 3, 10, 21, 100);

-- Line 6: Caminha (21) → São Pedro da Torre (16) - Single segment
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, "length", number_of_tracks, "order", start_station_id, end_station_id, speed_limit_kmh)
VALUES (25, 6, 'Yes', 8000, 20829, 2, 1, 21, 16, 100);

-- Line 7: São Pedro da Torre (16) → Valença (11) - Single segment
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, "length", number_of_tracks, "order", start_station_id, end_station_id, speed_limit_kmh)
VALUES (26, 7, 'Yes', 8000, 4264, 2, 1, 16, 11, 100);

-- original inserts

INSERT INTO Wagon ("number", operator_id, wagon_model_id, "year") VALUES ('356 3 077', 'PT509017800', 1104, 1987);
INSERT INTO Wagon ("number", operator_id, wagon_model_id, "year") VALUES ('356 3 078', 'PT509017800', 1104, 1987);
INSERT INTO Wagon ("number", operator_id, wagon_model_id, "year") VALUES ('356 3 079', 'PT509017800', 1104, 1987);
INSERT INTO Wagon ("number", operator_id, wagon_model_id, "year") VALUES ('356 3 080', 'PT509017800', 1104, 1987);
INSERT INTO Wagon ("number", operator_id, wagon_model_id, "year") VALUES ('356 3 081', 'PT509017800', 1104, 1987);
INSERT INTO Wagon ("number", operator_id, wagon_model_id, "year") VALUES ('356 3 082', 'PT509017800', 1104, 1987);
INSERT INTO Wagon ("number", operator_id, wagon_model_id, "year") VALUES ('356 3 083', 'PT509017800', 1104, 1987);
INSERT INTO Wagon ("number", operator_id, wagon_model_id, "year") VALUES ('356 3 084', 'PT509017800', 1104, 1987);
INSERT INTO Wagon ("number", operator_id, wagon_model_id, "year") VALUES ('356 3 085', 'PT509017800', 1104, 1987);
INSERT INTO Wagon ("number", operator_id, wagon_model_id, "year") VALUES ('356 3 086', 'PT509017800', 1104, 1987);
INSERT INTO Wagon ("number", operator_id, wagon_model_id, "year") VALUES ('356 3 087', 'PT509017800', 1104, 1987);
INSERT INTO Wagon ("number", operator_id, wagon_model_id, "year") VALUES ('356 3 088', 'PT509017800', 1104, 1987);
INSERT INTO Wagon ("number", operator_id, wagon_model_id, "year") VALUES ('356 3 089', 'PT509017800', 1104, 1987);
INSERT INTO Wagon ("number", operator_id, wagon_model_id, "year") VALUES ('356 3 090', 'PT509017800', 1104, 1987);
INSERT INTO Wagon ("number", operator_id, wagon_model_id, "year") VALUES ('356 3 091', 'PT509017800', 1104, 1987);
INSERT INTO Wagon ("number", operator_id, wagon_model_id, "year") VALUES ('356 3 092', 'PT509017800', 1104, 1987);
INSERT INTO Wagon ("number", operator_id, wagon_model_id, "year") VALUES ('082 3 045', 'PT509017800', 1245, 1990);
INSERT INTO Wagon ("number", operator_id, wagon_model_id, "year") VALUES ('082 3 046', 'PT509017800', 1245, 1990);
INSERT INTO Wagon ("number", operator_id, wagon_model_id, "year") VALUES ('082 3 047', 'PT509017800', 1245, 1990);
INSERT INTO Wagon ("number", operator_id, wagon_model_id, "year") VALUES ('082 3 048', 'PT509017800', 1245, 1990);
INSERT INTO Wagon ("number", operator_id, wagon_model_id, "year") VALUES ('074 1 001', 'PT509017800', 1278, 1977);
INSERT INTO Wagon ("number", operator_id, wagon_model_id, "year") VALUES ('074 1 002', 'PT509017800', 1278, 1977);
INSERT INTO Wagon ("number", operator_id, wagon_model_id, "year") VALUES ('074 1 003', 'PT509017800', 1278, 1977);
INSERT INTO Wagon ("number", operator_id, wagon_model_id, "year") VALUES ('074 1 004', 'PT509017800', 1278, 1977);
INSERT INTO Wagon ("number", operator_id, wagon_model_id, "year") VALUES ('074 1 005', 'PT509017800', 1278, 1977);
INSERT INTO Wagon ("number", operator_id, wagon_model_id, "year") VALUES ('074 1 006', 'PT509017800', 1278, 1977);
INSERT INTO Wagon ("number", operator_id, wagon_model_id, "year") VALUES ('181 1 010', 'PT509017800', 1325, 1977);
INSERT INTO Wagon ("number", operator_id, wagon_model_id, "year") VALUES ('181 1 011', 'PT509017800', 1325, 1977);
INSERT INTO Wagon ("number", operator_id, wagon_model_id, "year") VALUES ('181 1 012', 'PT509017800', 1325, 1977);
INSERT INTO Wagon ("number", operator_id, wagon_model_id, "year") VALUES ('181 1 013', 'PT509017800', 1325, 1977);
INSERT INTO Wagon ("number", operator_id, wagon_model_id, "year") VALUES ('181 1 014', 'PT509017800', 1325, 1977);

INSERT INTO LocomotiveModel (locomotive_model_id, gauge_id, engine_id, power, maximum_speed, "length", "weight", maker, "name", number_of_bogies, bogie, height, operational_speed, traction, width)
VALUES (1, 1, 1, 5600.0, 220, 19, 87, 'Siemens', 'Eurosprinter', 2, 'Bo-Bo', 4, 70, 300, 3);
INSERT INTO LocomotiveModel (locomotive_model_id, gauge_id, engine_id, power, maximum_speed, "length", "weight", maker, "name", number_of_bogies, bogie, height, operational_speed, traction, width)
VALUES (2, 1, 2, 1623.0, 100, 19, 117, 'Sorefame - Alsthom', 'CP 1900', 2, 'Co-Co', 4, 42, 396, 3);

INSERT INTO Locomotive ("number", "name", "year", operator_id, model_id) VALUES (5621, 'Inês', 1995, 'PT509017800', 1);
INSERT INTO Locomotive ("number", "name", "year", operator_id, model_id) VALUES (5623, 'Paz', 1995, 'PT509017800', 1);
INSERT INTO Locomotive ("number", "name", "year", operator_id, model_id) VALUES (5630, 'Helena', 1996, 'PT509017800', 1);
INSERT INTO Locomotive ("number", "name", "year", operator_id, model_id) VALUES (1903, 'Eva', 1981, 'PT509017800', 2);

COMMIT;