-- from bdadd

DROP TABLE TrainSchedule                CASCADE CONSTRAINTS PURGE;
DROP TABLE TrainWagonCargo              CASCADE CONSTRAINTS PURGE;
DROP TABLE TrainLocomotive              CASCADE CONSTRAINTS PURGE;
DROP TABLE FreightWagon                 CASCADE CONSTRAINTS PURGE;
DROP TABLE TrainFreight                 CASCADE CONSTRAINTS PURGE;
DROP TABLE PathFacilities               CASCADE CONSTRAINTS PURGE;
DROP TABLE LineSegment                  CASCADE CONSTRAINTS PURGE;
DROP TABLE FacilityInfrastructure       CASCADE CONSTRAINTS PURGE;
DROP TABLE WagonModelGauge              CASCADE CONSTRAINTS PURGE;
DROP TABLE LocomotiveModelGauge         CASCADE CONSTRAINTS PURGE;
DROP TABLE Train                        CASCADE CONSTRAINTS PURGE;
DROP TABLE Freight                      CASCADE CONSTRAINTS PURGE;
DROP TABLE Wagon                        CASCADE CONSTRAINTS PURGE;
DROP TABLE Locomotive                   CASCADE CONSTRAINTS PURGE;
DROP TABLE TrainLine                    CASCADE CONSTRAINTS PURGE;
DROP TABLE TrainPath                    CASCADE CONSTRAINTS PURGE;
DROP TABLE Siding                       CASCADE CONSTRAINTS PURGE;
DROP TABLE EletricEngine                CASCADE CONSTRAINTS PURGE;
DROP TABLE DieselEngine                 CASCADE CONSTRAINTS PURGE;
DROP TABLE LocomotiveModel              CASCADE CONSTRAINTS PURGE;
DROP TABLE WagonModel                   CASCADE CONSTRAINTS PURGE;
DROP TABLE Engine                       CASCADE CONSTRAINTS PURGE;
DROP TABLE Facility                     CASCADE CONSTRAINTS PURGE;
DROP TABLE Infrastructure               CASCADE CONSTRAINTS PURGE;
DROP TABLE Maker                        CASCADE CONSTRAINTS PURGE;
DROP TABLE BogieType                    CASCADE CONSTRAINTS PURGE;
DROP TABLE WagonType                    CASCADE CONSTRAINTS PURGE;
DROP TABLE EngineType                   CASCADE CONSTRAINTS PURGE;
DROP TABLE LineOwner                    CASCADE CONSTRAINTS PURGE;
DROP TABLE VehicleOperator              CASCADE CONSTRAINTS PURGE;
DROP TABLE Gauge                        CASCADE CONSTRAINTS PURGE;



CREATE TABLE Gauge (
                       gauge_id    integer                   CONSTRAINT pkGaugeID PRIMARY KEY,
                       gauge_name  varchar(255)              CONSTRAINT nnGaugeName NOT NULL,
                       gauge_width integer                   CONSTRAINT nnGaugeWidth NOT NULL
);

CREATE TABLE EngineType (
                            engine_type_id    integer               CONSTRAINT pkEngineTypeID PRIMARY KEY,
                            engine_type_name  varchar(255)          CONSTRAINT nnEngineTypeName NOT NULL
);

CREATE TABLE BogieType (
                           bogie_type_id     integer             CONSTRAINT pkBogieTypeID PRIMARY KEY,
                           bogie_type_name   varchar(255)        CONSTRAINT nnBogieTypeName NOT NULL
);

CREATE TABLE WagonType (
                           wagon_type_id   integer                CONSTRAINT pkWagonTypeID PRIMARY KEY,
                           wagon_type_name varchar(255)           CONSTRAINT nnWagonTypeName NOT NULL
);

CREATE TABLE Maker (
                       maker_id      integer                   CONSTRAINT pkMakerID PRIMARY KEY,
                       maker_name    varchar(255)              CONSTRAINT nnMakerName NOT NULL
);

CREATE TABLE Infrastructure (
                                infrastructure_id     integer         CONSTRAINT pkInfrastructureID PRIMARY KEY,
                                infrastructure_name   varchar(255)    CONSTRAINT nnInfrastructureName NOT NULL
);

CREATE TABLE TrainPath (
                           path_id integer                   CONSTRAINT pkPathID PRIMARY KEY
);

CREATE TABLE LineOwner (
                           vat_number  char(11)                CONSTRAINT pkLineOwnerVatNumber PRIMARY KEY
                               CONSTRAINT ckLineOwnerVATNumber CHECK(REGEXP_LIKE(vat_number, '^[A-Z]{2}[0-9]{9}$')),
                           owner_name  varchar(255)            CONSTRAINT nnLineOwnerName NOT NULL,
                           short_name  varchar(255)            CONSTRAINT nnLineOwnerShortName NOT NULL
);

CREATE TABLE VehicleOperator (
                                 vat_number    char(11)              CONSTRAINT pkVehicleOperatorVATNumber PRIMARY KEY
                                     CONSTRAINT ckVehicleOperatorVATNumber CHECK (REGEXP_LIKE(vat_number, '^[A-Z]{2}[0-9]{9}$')),
                                 operator_name varchar(255)          CONSTRAINT nnVehicleOperatorName NOT NULL,
                                 short_name    varchar(255)          CONSTRAINT nnVehicleOperatorShortName NOT NULL
);

CREATE TABLE Facility (
                          facility_id     integer             CONSTRAINT pkFacilityID PRIMARY KEY,
                          facility_name   varchar(255)        CONSTRAINT nnFacilityName NOT NULL,
                          is_intermodal   varchar(3)          CONSTRAINT nnFacilityIsIntermodal NOT NULL
);

CREATE TABLE Siding (
                        siding_id       integer           CONSTRAINT pkSidingID PRIMARY KEY,
                        siding_position integer           CONSTRAINT nnSidingPosition NOT NULL,
                        siding_length   integer           CONSTRAINT nnSidingLength NOT NULL
);


CREATE TABLE Engine (
                        engine_id      integer                CONSTRAINT pkEngineID PRIMARY KEY,
                        engine_type_id integer                CONSTRAINT nnEngineEngineTypeID NOT NULL
);

CREATE TABLE WagonModel (
                            wagon_model_id      integer           CONSTRAINT pkWagonModelID PRIMARY KEY,
                            wagon_type_id      integer           CONSTRAINT nnWagonModelWagonTypeID NOT NULL,
                            maker_id            integer           CONSTRAINT nnWagonModelMakerID NOT NULL,
                            wagon_model_name    varchar(255)      CONSTRAINT nnWagonModelName NOT NULL,
                            number_of_bogies    integer           CONSTRAINT nnWagonModelNumberOfBogies NOT NULL,
                            wagon_model_payload integer           CONSTRAINT nnWagonModelPayload NOT NULL,
                            max_speed           integer           CONSTRAINT nnWagonModelMaxSpeed NOT NULL,
                            wagon_model_length  integer           CONSTRAINT nnWagonModelLength NOT NULL,
                            wagon_model_width   integer           CONSTRAINT nnWagonModelWidth NOT NULL,
                            wagon_model_height  integer           CONSTRAINT nnWagonModelHeight NOT NULL,
                            wagon_model_weight  integer           CONSTRAINT nnWagonModelWeight NOT NULL,
                            wagon_model_volume  integer           CONSTRAINT nnWagonModelVolume NOT NULL,
                            bogie_type_id       integer           CONSTRAINT nnWagonModelBogieTypeID NOT NULL
);

CREATE TABLE LocomotiveModel (
                                 locomotive_model_id       integer           CONSTRAINT pkLocomotiveModelID PRIMARY KEY,
                                 engine_id                 integer           CONSTRAINT nnLocomotiveModelEngineID NOT NULL,
                                 bogie_type_id             integer           CONSTRAINT nnLocomotiveModelBogieTypeID NOT NULL,
                                 maker_id                  integer           CONSTRAINT nnLocomotiveModelMakerID NOT NULL,
                                 locomotive_model_name     varchar(255)      CONSTRAINT nnLocomotiveModelName NOT NULL,
                                 number_of_bogies          integer           CONSTRAINT nnLocomotiveModelNumberOfBogies NOT NULL,
                                 locomotive_model_power    integer           CONSTRAINT nnLocomotiveModelPower NOT NULL,
                                 locomotive_model_length   integer           CONSTRAINT nnLocomotiveModelLength NOT NULL,
                                 locomotive_model_height   integer           CONSTRAINT nnLocomotiveModelheight NOT NULL,
                                 locomotive_model_width    integer           CONSTRAINT nnLocomotiveModelWidth NOT NULL,
                                 locomotive_model_weight   integer           CONSTRAINT nnLocomotiveModelWeight NOT NULL,
                                 maximum_speed             integer           CONSTRAINT nnLocomotiveModelMaximumSpeed NOT NULL,
                                 operational_speed         integer           CONSTRAINT nnLocomotiveModelOperationalSpeed NOT NULL,
                                 locomotive_model_traction integer           CONSTRAINT nnLocomotiveModelTraction NOT NULL
);

CREATE TABLE EletricEngine (
                               engine_id integer                     CONSTRAINT pkEletricEngineID PRIMARY KEY,
                               voltage   integer                     CONSTRAINT nnEletricEngineVoltage NOT NULL,
                               frequency integer                     CONSTRAINT nnEletricEngineFrequency NOT NULL
);

CREATE TABLE DieselEngine (
                              engine_id     integer                 CONSTRAINT pkDieselEngineID PRIMARY KEY,
                              fuel_capacity integer                 CONSTRAINT nnDieselEngineFuelCapacity NOT NULL
);

CREATE TABLE FacilityInfrastructure (
                                        facility_id       integer,
                                        infrastructure_id integer,
                                        PRIMARY KEY(facility_id, infrastructure_id)
);

CREATE TABLE Wagon (
                       wagon_number   char(9)             CONSTRAINT pkWagonNumber PRIMARY KEY
                           CONSTRAINT ckWagonNumber CHECK (REGEXP_LIKE(wagon_number, '^[0-9]{3} [0-9]{1} [0-9]{3}$')),
                       operator_id    char(11)             CONSTRAINT nnWagonOperatorID NOT NULL,
                       wagon_model_id integer              CONSTRAINT nnWagonWagonModelID NOT NULL,
                       wagon_year     integer              CONSTRAINT nnWagonYear NOT NULL
                           CONSTRAINT ckWagonYear CHECK (wagon_year BETWEEN 1900 AND 2025)
);

CREATE TABLE Locomotive (
                            locomotive_number   integer           CONSTRAINT pkLocomotiveNumber PRIMARY KEY,
                            operator_id         char(11)          CONSTRAINT nnLocomotiveOperatorID NOT NULL,
                            model_id            integer           CONSTRAINT nnLocomotiveModelID NOT NULL,
                            locomotive_name     varchar(255)      CONSTRAINT nnLocomotiveName NOT NULL,
                            locomotive_year     integer           CONSTRAINT nnLocomotiveYear NOT NULL
                                CONSTRAINT ckLocomotiveYear CHECK (locomotive_year BETWEEN 1900 AND 2025)
);

CREATE TABLE TrainLine (
                           line_id          integer              CONSTRAINT pkTrainLineID PRIMARY KEY,
                           owner_id         char(11)             CONSTRAINT nnTrainLineOwnerID NOT NULL,
                           start_station_id integer              CONSTRAINT nnTrainLineStartStationID NOT NULL,
                           end_station_id   integer              CONSTRAINT nnTrainLineEndStationID NOT NULL,
                           gauge_id         integer              CONSTRAINT nnTrainLineGaugeID NOT NULL,
                           line_name        varchar(255)         CONSTRAINT nnTrainLineName NOT NULL
);

CREATE TABLE LineSegment (
                             line_segment_id       integer             CONSTRAINT pkLineSegmentID PRIMARY KEY,
                             line_id               integer             CONSTRAINT nnLineSegmentLineID NOT NULL,
                             is_electrified        varchar(3)          CONSTRAINT nnLineSegmentIdElectrified NOT NULL,
                             maximum_weight        integer             CONSTRAINT nnLineSegmentMaximumWeight NOT NULL,
                             line_segment_length   integer             CONSTRAINT nnLineSegmentLength NOT NULL,
                             number_of_tracks      integer             CONSTRAINT nnLineSegmentNumberOfTracks NOT NULL,
                             line_segment_order    integer             CONSTRAINT nnLineSegmentOrder NOT NULL,
                             siding_id             integer
);

CREATE TABLE LocomotiveModelGauge (
                                      locomotive_model_id integer,
                                      gauge_id            integer,
                                      PRIMARY KEY(locomotive_model_id, gauge_id)
);

CREATE TABLE WagonModelGauge (
                                 wagon_model_id integer,
                                 gauge_id       integer,
                                 PRIMARY KEY(wagon_model_id, gauge_id)
);

CREATE TABLE Freight (
                         freight_id              integer       CONSTRAINT pkFreightID PRIMARY KEY,
                         freight_date            date          CONSTRAINT nnFreightDate NOT NULL,
                         origin_facility_id      integer       CONSTRAINT nnFreightOriginFacilityID NOT NULL,
                         destination_facility_id integer       CONSTRAINT nnFreightDestinationFacilityID NOT NULL
);

CREATE TABLE Train (
                       train_id                   integer               CONSTRAINT pkTrainID PRIMARY KEY,
                       operator_id                char(11)              CONSTRAINT nnTrainOperatorID NOT NULL,
                       train_departure_date_time  date                  CONSTRAINT nnTrainDepartureDate NOT NULL,
                       path_id                    integer               CONSTRAINT nnTrainPathID NOT NULL
);

CREATE TABLE PathFacilities (
                                path_id                 integer,
                                facility_id             integer,
                                path_facility_order     integer               CONSTRAINT nnPathFacilitiesOrder NOT NULL,
                                PRIMARY KEY(path_id, facility_id)
);

CREATE TABLE TrainLocomotive (
                                 train_id      integer,
                                 locomotive_id integer,
                                 PRIMARY KEY(train_id, locomotive_id)
);

CREATE TABLE TrainFreight (
                              train_id   integer,
                              freight_id integer,
                              PRIMARY KEY(train_id, freight_id)
);

CREATE TABLE FreightWagon (
                              freight_id integer,
                              wagon_id   char(9),
                              PRIMARY KEY(freight_id, wagon_id)
);

--needed for USLP07
CREATE TABLE TrainWagonCargo (
                                 train_id      INTEGER,
                                 wagon_number  CHAR(9),
                                 is_loaded     VARCHAR(3)       CONSTRAINT nnTrainWagonCargoLoaded NOT NULL
                                     CONSTRAINT ckTrainWagonCargoLoaded CHECK (is_loaded IN ('Sim', 'Nao')),
                                 cargo_weight  INTEGER DEFAULT 0, -- actual cargo in tons
                                 PRIMARY KEY(train_id, wagon_number),
                                 CONSTRAINT fkTrainWagonCargoTrain FOREIGN KEY (train_id) REFERENCES Train(train_id),
                                 CONSTRAINT fkTrainWagonCargoWagon FOREIGN KEY (wagon_number) REFERENCES Wagon(wagon_number)
);

CREATE TABLE TrainSchedule (
                               train_id            INTEGER         CONSTRAINT nnScheduleTrainID NOT NULL,
                               facility_id         INTEGER         CONSTRAINT nnScheduleFacilityID NOT NULL,
                               arrival_time        DATE,
                               departure_time      DATE,
                               path_facility_order INTEGER         CONSTRAINT nnScheduleOrder NOT NULL,

                               CONSTRAINT pkTrainSchedule PRIMARY KEY (train_id, facility_id),

    -- Foreign Keys - These are crucial for integrity
                               CONSTRAINT fkScheduleTrainID FOREIGN KEY (train_id)
                                   REFERENCES Train(train_id),
                               CONSTRAINT fkScheduleFacilityID FOREIGN KEY (facility_id)
                                   REFERENCES Facility(facility_id)
);

ALTER TABLE WagonModel                  ADD CONSTRAINT fkWagonModelWagonTypeID                          FOREIGN KEY (wagon_type_id)            REFERENCES WagonType (wagon_type_id);
ALTER TABLE Engine                      ADD CONSTRAINT fkEngineEngineTypeID                             FOREIGN KEY (engine_type_id)            REFERENCES EngineType (engine_type_id);
ALTER TABLE DieselEngine                ADD CONSTRAINT fkDieselEngineEngineID                           FOREIGN KEY (engine_id)                 REFERENCES Engine (engine_id);
ALTER TABLE EletricEngine               ADD CONSTRAINT fkEletricEngineEngineID                          FOREIGN KEY (engine_id)                 REFERENCES Engine (engine_id);
ALTER TABLE LocomotiveModel             ADD CONSTRAINT fkLocomotiveModelEngineID                        FOREIGN KEY (engine_id)                 REFERENCES Engine (engine_id);
ALTER TABLE Locomotive                  ADD CONSTRAINT fkLocomotiveLocomotiveModelID                    FOREIGN KEY (model_id)                  REFERENCES LocomotiveModel (locomotive_model_id);
ALTER TABLE TrainLocomotive             ADD CONSTRAINT fkTrainLocomotiveLocomotiveNumber                FOREIGN KEY (locomotive_id)             REFERENCES Locomotive (locomotive_number);
ALTER TABLE TrainLocomotive             ADD CONSTRAINT fkTrainLocomotiveTrainID                         FOREIGN KEY (train_id)                  REFERENCES Train (train_id);
ALTER TABLE WagonModelGauge             ADD CONSTRAINT fkWagonModelGaugeWagonModelID                    FOREIGN KEY (wagon_model_id)            REFERENCES WagonModel (wagon_model_id);
ALTER TABLE WagonModelGauge             ADD CONSTRAINT fkWagonModelGaugeGaugeID                         FOREIGN KEY (gauge_id)                  REFERENCES Gauge (gauge_id);
ALTER TABLE LocomotiveModelGauge        ADD CONSTRAINT fkLocomotiveModelGaugeGaugeID                    FOREIGN KEY (gauge_id)                  REFERENCES Gauge (gauge_id);
ALTER TABLE LocomotiveModelGauge        ADD CONSTRAINT fkLocomotiveModelGaugeLocomotiveModelID          FOREIGN KEY (locomotive_model_id)       REFERENCES LocomotiveModel (locomotive_model_id);
ALTER TABLE Train                       ADD CONSTRAINT fkTrainPathID                                    FOREIGN KEY (path_id)                   REFERENCES TrainPath (path_id);
ALTER TABLE PathFacilities              ADD CONSTRAINT fkPathFacilitiesPathID                           FOREIGN KEY (path_id)                   REFERENCES TrainPath (path_id);
ALTER TABLE PathFacilities              ADD CONSTRAINT fkPathFacilitiesFacilityID                       FOREIGN KEY (facility_id)               REFERENCES Facility (facility_id);
ALTER TABLE FacilityInfrastructure      ADD CONSTRAINT fkFacilityInfrastructureFacilityID               FOREIGN KEY (facility_id)               REFERENCES Facility (facility_id);
ALTER TABLE FacilityInfrastructure      ADD CONSTRAINT fkFacilityInfrastructureInfrastructureID         FOREIGN KEY (infrastructure_id)         REFERENCES Infrastructure (infrastructure_id);
ALTER TABLE FreightWagon                ADD CONSTRAINT fkFreightWagonWagonNumber                        FOREIGN KEY (wagon_id)                  REFERENCES Wagon (wagon_number);
ALTER TABLE FreightWagon                ADD CONSTRAINT fkFreightWagonFreightID                          FOREIGN KEY (freight_id)                REFERENCES Freight (freight_id);
ALTER TABLE TrainFreight                ADD CONSTRAINT fkTrainFreightFreightID                          FOREIGN KEY (freight_id)                REFERENCES Freight (freight_id);
ALTER TABLE TrainFreight                ADD CONSTRAINT fkTrainFreightTrainID                            FOREIGN KEY (train_id)                  REFERENCES Train (train_id);
ALTER TABLE TrainLine                   ADD CONSTRAINT fkLineOwnerVATNumber                             FOREIGN KEY (owner_id)                  REFERENCES LineOwner (vat_number);
ALTER TABLE LineSegment                 ADD CONSTRAINT fkLineSegmentLineID                              FOREIGN KEY (line_id)                   REFERENCES TrainLine (line_id);
ALTER TABLE LineSegment                 ADD CONSTRAINT fkLineSegmentSidingID                            FOREIGN KEY (siding_id)                 REFERENCES Siding (siding_id);
ALTER TABLE TrainLine                   ADD CONSTRAINT fkLineStartStationID                             FOREIGN KEY (start_station_id)          REFERENCES Facility (facility_id);
ALTER TABLE TrainLine                   ADD CONSTRAINT fkLineEndStationID                               FOREIGN KEY (end_station_id)            REFERENCES Facility (facility_id);
ALTER TABLE LocomotiveModel             ADD CONSTRAINT fkLocomotiveModelBogieTypeID                     FOREIGN KEY (bogie_type_id)             REFERENCES BogieType (bogie_type_id);
ALTER TABLE WagonModel                  ADD CONSTRAINT fkWagonModelBogieTypeID                          FOREIGN KEY (bogie_type_id)             REFERENCES BogieType (bogie_type_id);
ALTER TABLE LocomotiveModel             ADD CONSTRAINT fkLocomotiveModelMakerID                         FOREIGN KEY (maker_id)                  REFERENCES Maker (maker_id);
ALTER TABLE WagonModel                  ADD CONSTRAINT fkWagonModelMakerID                              FOREIGN KEY (maker_id)                  REFERENCES Maker (maker_id);
ALTER TABLE Wagon                       ADD CONSTRAINT fkWagonOperatorVATNumber                         FOREIGN KEY (operator_id)               REFERENCES VehicleOperator (vat_number);
ALTER TABLE Train                       ADD CONSTRAINT fkTrainOperatorVATNumber                         FOREIGN KEY (operator_id)               REFERENCES VehicleOperator (vat_number);
ALTER TABLE Locomotive                  ADD CONSTRAINT fkLocomotiveOperatorVATNumber                    FOREIGN KEY (operator_id)               REFERENCES VehicleOperator (vat_number);
ALTER TABLE TrainLine                   ADD CONSTRAINT fkLineGaugeID                                    FOREIGN KEY (gauge_id)                  REFERENCES Gauge (gauge_id);
ALTER TABLE Wagon                       ADD CONSTRAINT fkWagonWagonModelID                              FOREIGN KEY (wagon_model_id)            REFERENCES WagonModel (wagon_model_id);
ALTER TABLE Freight                     ADD CONSTRAINT fkFreightOriginFacilityID                        FOREIGN KEY (origin_facility_id)        REFERENCES Facility (facility_id);
ALTER TABLE Freight                     ADD CONSTRAINT fkFreightDestinationFacilityID                   FOREIGN KEY (destination_facility_id)   REFERENCES Facility (facility_id);

-- needed for uslp07

ALTER TABLE LineSegment ADD speed_limit_kmh INTEGER;





--from bdadd
INSERT INTO Facility (facility_id, facility_name, is_intermodal) VALUES (1, 'São Romão', 'Nao');
INSERT INTO Facility (facility_id, facility_name, is_intermodal) VALUES (2, 'Tamel', 'Sim');
INSERT INTO Facility (facility_id, facility_name, is_intermodal) VALUES (3, 'Senhora das Dores', 'Sim');
INSERT INTO Facility (facility_id, facility_name, is_intermodal) VALUES (4, 'Lousado', 'Sim');
INSERT INTO Facility (facility_id, facility_name, is_intermodal) VALUES (5, 'Porto Campanhã', 'Nao');
INSERT INTO Facility (facility_id, facility_name, is_intermodal) VALUES (6, 'Leandro', 'Sim');
INSERT INTO Facility (facility_id, facility_name, is_intermodal) VALUES (7, 'Porto São Bento', 'Sim');
INSERT INTO Facility (facility_id, facility_name, is_intermodal) VALUES (8, 'Barcelos', 'Nao');
INSERT INTO Facility (facility_id, facility_name, is_intermodal) VALUES (9, 'Vila Nova da Cerveira', 'Sim');
INSERT INTO Facility (facility_id, facility_name, is_intermodal) VALUES (10, 'Midões', 'Sim');
INSERT INTO Facility (facility_id, facility_name, is_intermodal) VALUES (11, 'Valença', 'Sim');
INSERT INTO Facility (facility_id, facility_name, is_intermodal) VALUES (12, 'Darque', 'Sim');
INSERT INTO Facility (facility_id, facility_name, is_intermodal) VALUES (13, 'Contumil', 'Sim');
INSERT INTO Facility (facility_id, facility_name, is_intermodal) VALUES (14, 'Ermesinde', 'Nao');
INSERT INTO Facility (facility_id, facility_name, is_intermodal) VALUES (15, 'São Frutuoso', 'Sim');
INSERT INTO Facility (facility_id, facility_name, is_intermodal) VALUES (16, 'São Pedro da Torre', 'Nao');
INSERT INTO Facility (facility_id, facility_name, is_intermodal) VALUES (17, 'Viana do Castelo', 'Sim');
INSERT INTO Facility (facility_id, facility_name, is_intermodal) VALUES (18, 'Famalicão', 'Sim');
INSERT INTO Facility (facility_id, facility_name, is_intermodal) VALUES (19, 'Barroselas', 'Nao');
INSERT INTO Facility (facility_id, facility_name, is_intermodal) VALUES (20, 'Nine', 'Nao');
INSERT INTO Facility (facility_id, facility_name, is_intermodal) VALUES (21, 'Caminha', 'Sim');
INSERT INTO Facility (facility_id, facility_name, is_intermodal) VALUES (22, 'Carvalha', 'Nao');
INSERT INTO Facility (facility_id, facility_name, is_intermodal) VALUES (23, 'Carreço', 'Nao');
INSERT INTO Facility (facility_id, facility_name, is_intermodal) VALUES (50, 'Leixões', 'Nao');
INSERT INTO Facility (facility_id, facility_name, is_intermodal) VALUES (45, '	São Mamede de Infesta', 'Sim');
INSERT INTO Facility (facility_id, facility_name, is_intermodal) VALUES (48, 'Leça do Balio', 'Sim');
INSERT INTO Facility (facility_id, facility_name, is_intermodal) VALUES (43, 'São Gemil', 'Nao');

INSERT INTO Siding (siding_id, siding_position, siding_length) VALUES (1, '2000', 864);
INSERT INTO Siding (siding_id, siding_position, siding_length) VALUES (2, '11000', 266);

INSERT INTO LineOwner (vat_number, owner_name, short_name) VALUES ('PT503933813', 'Infraestruturas de Portugal, SA', 'IP');

INSERT INTO VehicleOperator (vat_number, operator_name, short_name) VALUES ('PT509017800', 'Medway - Operador Ferroviário de Mercadorias, S.A', 'Medway');
INSERT INTO VehicleOperator (vat_number, operator_name, short_name) VALUES ('PT507832388', 'Captrain Portugal S.A.', 'Captrain');

INSERT INTO Maker (maker_id, maker_name) VALUES (1, 'Siemens');
INSERT INTO Maker (maker_id, maker_name) VALUES (2, 'Sorefame - Alsthom');
INSERT INTO Maker (maker_id, maker_name) VALUES (3, 'Stadler');
INSERT INTO Maker (maker_id, maker_name) VALUES (4, 'Metalsines');
INSERT INTO Maker (maker_id, maker_name) VALUES (5, 'Equimetal');
INSERT INTO Maker (maker_id, maker_name) VALUES (6, 'Sepsa Cometna');
INSERT INTO Maker (maker_id, maker_name) VALUES (7, 'Emef');
INSERT INTO Maker (maker_id, maker_name) VALUES (8, 'Simmering');

INSERT INTO Infrastructure (infrastructure_id, infrastructure_name) VALUES (1, 'warehouse');
INSERT INTO Infrastructure (infrastructure_id, infrastructure_name) VALUES (2, 'refrigerated area');
INSERT INTO Infrastructure (infrastructure_id, infrastructure_name) VALUES (3, 'grain silo');

INSERT INTO TrainPath (path_id) VALUES (1);
INSERT INTO TrainPath (path_id) VALUES (2);

INSERT INTO Gauge (gauge_id, gauge_name, gauge_width) VALUES (1, 'Iberian ', 1668);
INSERT INTO Gauge (gauge_id, gauge_name, gauge_width) VALUES (2, 'Standart', 1435);

INSERT INTO EngineType (engine_type_id, engine_type_name) VALUES (1, 'Electric');
INSERT INTO EngineType (engine_type_id, engine_type_name) VALUES (2, 'Diesel');

INSERT INTO WagonType (wagon_type_id, wagon_type_name) VALUES (1, 'Cereal wagon');
INSERT INTO WagonType (wagon_type_id, wagon_type_name) VALUES (2, 'Covered wagon with sliding door');
INSERT INTO WagonType (wagon_type_id, wagon_type_name) VALUES (3, 'Container wagon (max 40'''' HC)');
INSERT INTO WagonType (wagon_type_id, wagon_type_name) VALUES (4, 'Biodiesel wagaon');
INSERT INTO WagonType (wagon_type_id, wagon_type_name) VALUES (5, 'Wood wagon');

INSERT INTO BogieType (bogie_type_id, bogie_type_name) VALUES (1, 'Duplo');
INSERT INTO BogieType (bogie_type_id, bogie_type_name) VALUES (2, 'Simples');
INSERT INTO BogieType (bogie_type_id, bogie_type_name) VALUES (3, 'Co-Co');
INSERT INTO BogieType (bogie_type_id, bogie_type_name) VALUES (4, 'Bo-Bo');

INSERT INTO Freight (freight_id, freight_date, origin_facility_id, destination_facility_id) VALUES (2001,TO_DATE('2025/10/03', 'YYYY/MM/DD'), 50, 12);
INSERT INTO Freight (freight_id, freight_date, origin_facility_id, destination_facility_id) VALUES (2002,TO_DATE('2025/10/03', 'YYYY/MM/DD'), 13, 11);
INSERT INTO Freight (freight_id, freight_date, origin_facility_id, destination_facility_id) VALUES (2003,TO_DATE('2025/10/03', 'YYYY/MM/DD'), 50, 11);
INSERT INTO Freight (freight_id, freight_date, origin_facility_id, destination_facility_id) VALUES (2004,TO_DATE('2025/10/03', 'YYYY/MM/DD'), 50, 21);
INSERT INTO Freight (freight_id, freight_date, origin_facility_id, destination_facility_id) VALUES (2005,TO_DATE('2025/10/03', 'YYYY/MM/DD'), 20, 11);
INSERT INTO Freight (freight_id, freight_date, origin_facility_id, destination_facility_id) VALUES (2006,TO_DATE('2025/10/03', 'YYYY/MM/DD'), 12, 50);
INSERT INTO Freight (freight_id, freight_date, origin_facility_id, destination_facility_id) VALUES (2007,TO_DATE('2025/10/03', 'YYYY/MM/DD'), 50, 5);
INSERT INTO Freight (freight_id, freight_date, origin_facility_id, destination_facility_id) VALUES (2050,TO_DATE('2025/10/06', 'YYYY/MM/DD'), 12, 50);
INSERT INTO Freight (freight_id, freight_date, origin_facility_id, destination_facility_id) VALUES (2051,TO_DATE('2025/10/06', 'YYYY/MM/DD'), 11, 50);

INSERT INTO FacilityInfrastructure (facility_id, infrastructure_id) VALUES (1, 2);
INSERT INTO FacilityInfrastructure (facility_id, infrastructure_id) VALUES (2, 3);
INSERT INTO FacilityInfrastructure (facility_id, infrastructure_id) VALUES (3, 1);
INSERT INTO FacilityInfrastructure (facility_id, infrastructure_id) VALUES (4, 3);
INSERT INTO FacilityInfrastructure (facility_id, infrastructure_id) VALUES (5, 1);
INSERT INTO FacilityInfrastructure (facility_id, infrastructure_id) VALUES (6, 2);
INSERT INTO FacilityInfrastructure (facility_id, infrastructure_id) VALUES (7, 2);
INSERT INTO FacilityInfrastructure (facility_id, infrastructure_id) VALUES (50, 1);
INSERT INTO FacilityInfrastructure (facility_id, infrastructure_id) VALUES (9, 3);
INSERT INTO FacilityInfrastructure (facility_id, infrastructure_id) VALUES (10, 3);
INSERT INTO FacilityInfrastructure (facility_id, infrastructure_id) VALUES (11, 2);
INSERT INTO FacilityInfrastructure (facility_id, infrastructure_id) VALUES (23, 2);
INSERT INTO FacilityInfrastructure (facility_id, infrastructure_id) VALUES (13, 3);
INSERT INTO FacilityInfrastructure (facility_id, infrastructure_id) VALUES (14, 3);
INSERT INTO FacilityInfrastructure (facility_id, infrastructure_id) VALUES (15, 1);
INSERT INTO FacilityInfrastructure (facility_id, infrastructure_id) VALUES (45, 2);
INSERT INTO FacilityInfrastructure (facility_id, infrastructure_id) VALUES (17, 3);
INSERT INTO FacilityInfrastructure (facility_id, infrastructure_id) VALUES (18, 1);
INSERT INTO FacilityInfrastructure (facility_id, infrastructure_id) VALUES (48, 3);
INSERT INTO FacilityInfrastructure (facility_id, infrastructure_id) VALUES (10, 2);
INSERT INTO FacilityInfrastructure (facility_id, infrastructure_id) VALUES (23, 1);
INSERT INTO FacilityInfrastructure (facility_id, infrastructure_id) VALUES (18, 2);

INSERT INTO Engine (engine_id, engine_type_id) VALUES (1, 1);
INSERT INTO Engine (engine_id, engine_type_id) VALUES (2, 2);
INSERT INTO Engine (engine_id, engine_type_id) VALUES (3, 2);

INSERT INTO WagonModel (wagon_model_id, wagon_type_id, maker_id, wagon_model_name, number_of_bogies, wagon_model_payload, max_speed, wagon_model_length, wagon_model_width, wagon_model_height, wagon_model_weight, wagon_model_volume, bogie_type_id) VALUES (1245, 1, 4, 'Tadgs 32 94 082 3', 2, 56, 120, 17240, 3072, 4270, 24, 75, 1);
INSERT INTO WagonModel (wagon_model_id, wagon_type_id, maker_id, wagon_model_name, number_of_bogies, wagon_model_payload, max_speed, wagon_model_length, wagon_model_width, wagon_model_height, wagon_model_weight, wagon_model_volume, bogie_type_id) VALUES (1278, 1, 5, 'Tdgs 41 94 074 1', 2, 26, 100, 9640, 3120, 4165, 13, 38, 1);
INSERT INTO WagonModel (wagon_model_id, wagon_type_id, maker_id, wagon_model_name, number_of_bogies, wagon_model_payload, max_speed, wagon_model_length, wagon_model_width, wagon_model_height, wagon_model_weight, wagon_model_volume, bogie_type_id) VALUES (1325, 2, 6, 'Gabs 81 94 181 1', 2, 50, 100, 21700, 3180, 4170, 29, 110, 1);
INSERT INTO WagonModel (wagon_model_id, wagon_type_id, maker_id, wagon_model_name, number_of_bogies, wagon_model_payload, max_speed, wagon_model_length, wagon_model_width, wagon_model_height, wagon_model_weight, wagon_model_volume, bogie_type_id) VALUES (1104, 3, 4, 'Regmms 32 94 356 3', 2, 60, 120, 14040, 3104, 2535, 21, 76, 1);
INSERT INTO WagonModel (wagon_model_id, wagon_type_id, maker_id, wagon_model_name, number_of_bogies, wagon_model_payload, max_speed, wagon_model_length, wagon_model_width, wagon_model_height, wagon_model_weight, wagon_model_volume, bogie_type_id) VALUES (985, 3, 4, 'Lgs 22 94 441 6', 2, 28, 120, 13860, 2850, 1060, 11, 76, 2);
INSERT INTO WagonModel (wagon_model_id, wagon_type_id, maker_id, wagon_model_name, number_of_bogies, wagon_model_payload, max_speed, wagon_model_length, wagon_model_width, wagon_model_height, wagon_model_weight, wagon_model_volume, bogie_type_id) VALUES (988, 3, 7, 'Sgnss 12 94 455 2', 2, 68, 120, 18116, 2950, 1030, 21, 76, 1);
INSERT INTO WagonModel (wagon_model_id, wagon_type_id, maker_id, wagon_model_name, number_of_bogies, wagon_model_payload, max_speed, wagon_model_length, wagon_model_width, wagon_model_height, wagon_model_weight, wagon_model_volume, bogie_type_id) VALUES (1525, 4, 5, 'Zaes 81 94 788', 2, 57, 120, 13800, 2950, 4226, 22, 64, 1);
INSERT INTO WagonModel (wagon_model_id, wagon_type_id, maker_id, wagon_model_name, number_of_bogies, wagon_model_payload, max_speed, wagon_model_length, wagon_model_width, wagon_model_height, wagon_model_weight, wagon_model_volume, bogie_type_id) VALUES (1212, 5, 8, 'Kbs 41 94 333', 2, 25, 100, 14020, 2842, 3300, 14, 62, 2);
INSERT INTO WagonModel (wagon_model_id, wagon_type_id, maker_id, wagon_model_name, number_of_bogies, wagon_model_payload, max_speed, wagon_model_length, wagon_model_width, wagon_model_height, wagon_model_weight, wagon_model_volume, bogie_type_id) VALUES (987, 3, 7, 'Sgnss 12 94 455 2', 2, 68, 120, 18116, 2950, 1030, 21, 76, 1);
INSERT INTO WagonModel (wagon_model_id, wagon_type_id, maker_id, wagon_model_name, number_of_bogies, wagon_model_payload, max_speed, wagon_model_length, wagon_model_width, wagon_model_height, wagon_model_weight, wagon_model_volume, bogie_type_id) VALUES (1523, 4, 5, 'Zaes 94 788', 2, 57, 120, 13800, 2950, 4226, 22, 64, 1);

INSERT INTO DieselEngine (engine_id, fuel_capacity) VALUES (2, '4882');
INSERT INTO DieselEngine (engine_id, fuel_capacity) VALUES (3, '6700');

INSERT INTO EletricEngine (engine_id, voltage, frequency) VALUES (1, 25000, 50);

INSERT INTO LocomotiveModel (locomotive_model_id, engine_id, bogie_type_id, maker_id, locomotive_model_name, number_of_bogies, locomotive_model_power, locomotive_model_length, locomotive_model_height, locomotive_model_width, locomotive_model_weight, maximum_speed, operational_speed, locomotive_model_traction) VALUES (1, 1, 3, 1, 'Eurosprinter', 2, 5600.0, 19, 4, 3, 87, 220, 70, 300);
INSERT INTO LocomotiveModel (locomotive_model_id, engine_id, bogie_type_id, maker_id, locomotive_model_name, number_of_bogies, locomotive_model_power, locomotive_model_length, locomotive_model_height, locomotive_model_width, locomotive_model_weight, maximum_speed, operational_speed, locomotive_model_traction) VALUES (2, 2, 4, 2, 'CP 1900', 2, 1623.0, 19, 4, 3, 117, 100, 42, 396);
INSERT INTO LocomotiveModel (locomotive_model_id, engine_id, bogie_type_id, maker_id, locomotive_model_name, number_of_bogies, locomotive_model_power, locomotive_model_length, locomotive_model_height, locomotive_model_width, locomotive_model_weight, maximum_speed, operational_speed, locomotive_model_traction) VALUES (3, 3, 4, 3, 'E4000', 3, 3178.0, 23, 4, 3000, 124, 120, 100, 400);

INSERT INTO TrainLine (line_id, owner_id, start_station_id, end_station_id, gauge_id, line_name) VALUES (1, 'PT503933813', 7, 5, 1, 'Ramal São Bento - Campanhã');
INSERT INTO TrainLine (line_id, owner_id, start_station_id, end_station_id, gauge_id, line_name) VALUES (2, 'PT503933813', 5, 13, 1, 'Ramal Camapanhã - Contumil');
INSERT INTO TrainLine (line_id, owner_id, start_station_id, end_station_id, gauge_id, line_name) VALUES (3, 'PT503933813', 13, 20, 1, 'Ramal Contumil - Nine');
INSERT INTO TrainLine (line_id, owner_id, start_station_id, end_station_id, gauge_id, line_name) VALUES (4, 'PT503933813', 20, 8, 1, 'Ramal Nine - Barcelos');
INSERT INTO TrainLine (line_id, owner_id, start_station_id, end_station_id, gauge_id, line_name) VALUES (5, 'PT503933813', 8, 12, 1, 'Ramal Barcelos - Darque');
INSERT INTO TrainLine (line_id, owner_id, start_station_id, end_station_id, gauge_id, line_name) VALUES (6, 'PT503933813', 12, 17, 1, 'Ramal Darque - Viana');
INSERT INTO TrainLine (line_id, owner_id, start_station_id, end_station_id, gauge_id, line_name) VALUES (7, 'PT503933813', 17, 21, 1, 'Ramal Viana - Caminha');
INSERT INTO TrainLine (line_id, owner_id, start_station_id, end_station_id, gauge_id, line_name) VALUES (8, 'PT503933813', 21, 16, 1, 'Ramal Caminha - Torre');
INSERT INTO TrainLine (line_id, owner_id, start_station_id, end_station_id, gauge_id, line_name) VALUES (9, 'PT503933813', 16, 11, 1, 'Ramal Torre - Valença');
INSERT INTO TrainLine (line_id, owner_id, start_station_id, end_station_id, gauge_id, line_name) VALUES (21, 'PT503933813', 13, 43, 1, 'Ramal Contumil - São Gemil');
INSERT INTO TrainLine (line_id, owner_id, start_station_id, end_station_id, gauge_id, line_name) VALUES (22, 'PT503933813', 43, 45, 1, 'Ramal São Gemil - 	São Mamede de Infesta');
INSERT INTO TrainLine (line_id, owner_id, start_station_id, end_station_id, gauge_id, line_name) VALUES (23, 'PT503933813', 45, 48, 1, 'Ramal São Mamede de Infesta - Leça do Balio');
INSERT INTO TrainLine (line_id, owner_id, start_station_id, end_station_id, gauge_id, line_name) VALUES (24, 'PT503933813', 48, 50, 1, 'Ramal Leça do Balio - Leixões');

INSERT INTO LocomotiveModelGauge (locomotive_model_id, gauge_id) VALUES (1, 1);
INSERT INTO LocomotiveModelGauge (locomotive_model_id, gauge_id) VALUES (2, 1);
INSERT INTO LocomotiveModelGauge (locomotive_model_id, gauge_id) VALUES (3, 1);

INSERT INTO WagonModelGauge (wagon_model_id, gauge_id) VALUES (1245, 1);
INSERT INTO WagonModelGauge (wagon_model_id, gauge_id) VALUES (1278, 1);
INSERT INTO WagonModelGauge (wagon_model_id, gauge_id) VALUES (1325, 1);
INSERT INTO WagonModelGauge (wagon_model_id, gauge_id) VALUES (1104, 1);
INSERT INTO WagonModelGauge (wagon_model_id, gauge_id) VALUES (985, 1);
INSERT INTO WagonModelGauge (wagon_model_id, gauge_id) VALUES (988, 1);
INSERT INTO WagonModelGauge (wagon_model_id, gauge_id) VALUES (988, 2);
INSERT INTO WagonModelGauge (wagon_model_id, gauge_id) VALUES (1525, 1);
INSERT INTO WagonModelGauge (wagon_model_id, gauge_id) VALUES (1525, 2);
INSERT INTO WagonModelGauge (wagon_model_id, gauge_id) VALUES (1212, 1);
INSERT INTO WagonModelGauge (wagon_model_id, gauge_id) VALUES (987, 1);
INSERT INTO WagonModelGauge (wagon_model_id, gauge_id) VALUES (1523, 2);

INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('356 3 077', 'PT509017800', 1104, 1987);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('356 3 078', 'PT509017800', 1104, 1987);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('356 3 079', 'PT509017800', 1104, 1987);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('356 3 080', 'PT509017800', 1104, 1987);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('356 3 081', 'PT509017800', 1104, 1987);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('356 3 082', 'PT509017800', 1104, 1987);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('356 3 083', 'PT509017800', 1104, 1987);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('356 3 084', 'PT509017800', 1104, 1987);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('356 3 085', 'PT509017800', 1104, 1987);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('356 3 086', 'PT509017800', 1104, 1987);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('356 3 087', 'PT509017800', 1104, 1987);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('356 3 088', 'PT509017800', 1104, 1987);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('356 3 089', 'PT509017800', 1104, 1987);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('356 3 090', 'PT509017800', 1104, 1987);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('356 3 091', 'PT509017800', 1104, 1987);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('356 3 092', 'PT509017800', 1104, 1987);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('082 3 045', 'PT509017800', 1245, 1990);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('082 3 046', 'PT509017800', 1245, 1990);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('082 3 047', 'PT509017800', 1245, 1990);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('082 3 048', 'PT509017800', 1245, 1990);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('074 1 001', 'PT509017800', 1278, 1977);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('074 1 002', 'PT509017800', 1278, 1977);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('074 1 003', 'PT509017800', 1278, 1977);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('074 1 004', 'PT509017800', 1278, 1977);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('074 1 005', 'PT509017800', 1278, 1977);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('074 1 006', 'PT509017800', 1278, 1977);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('181 1 010', 'PT509017800', 1325, 1977);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('181 1 011', 'PT509017800', 1325, 1977);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('181 1 012', 'PT509017800', 1325, 1977);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('181 1 013', 'PT509017800', 1325, 1977);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('181 1 014', 'PT509017800', 1325, 1977);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('333 0 001', 'PT509017800', 1212, 2005);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('333 0 002', 'PT509017800', 1212, 2005);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('333 0 003', 'PT509017800', 1212, 2005);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('333 0 004', 'PT509017800', 1212, 2005);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('333 0 005', 'PT509017800', 1212, 2005);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('333 0 006', 'PT509017800', 1212, 2005);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('333 0 007', 'PT509017800', 1212, 2005);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('333 0 008', 'PT509017800', 1212, 2005);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('333 0 009', 'PT509017800', 1212, 2005);
INSERT INTO Wagon (wagon_number, operator_id, wagon_model_id, wagon_year)VALUES ('333 0 010', 'PT509017800', 1212, 2005);


INSERT INTO Locomotive (locomotive_number, locomotive_name, locomotive_year, operator_id, model_id) VALUES (5621, 'Inês', 1995, 'PT509017800', 1);
INSERT INTO Locomotive (locomotive_number, locomotive_name, locomotive_year, operator_id, model_id) VALUES (5623, 'Paz', 1995, 'PT509017800', 1);
INSERT INTO Locomotive (locomotive_number, locomotive_name, locomotive_year, operator_id, model_id) VALUES (5630, 'Helena', 1996, 'PT509017800', 1);
INSERT INTO Locomotive (locomotive_number, locomotive_name, locomotive_year, operator_id, model_id) VALUES (1903, 'Eva', 1981, 'PT509017800', 2);
INSERT INTO Locomotive (locomotive_number, locomotive_name, locomotive_year, operator_id, model_id) VALUES (5034, 'Adriana', 2017, 'PT509017800', 3);
INSERT INTO Locomotive (locomotive_number, locomotive_name, locomotive_year, operator_id, model_id) VALUES (5036, 'Marina', 2017, 'PT509017800', 3);
INSERT INTO Locomotive (locomotive_number, locomotive_name, locomotive_year, operator_id, model_id) VALUES (335001, 'Unknown', 2019, 'PT507832388', 3);
INSERT INTO Locomotive (locomotive_number, locomotive_name, locomotive_year, operator_id, model_id) VALUES (335003, 'Unknown', 2019, 'PT507832388', 3);

INSERT INTO Train (train_id, operator_id, train_departure_date_time, path_id) VALUES (5421, 'PT509017800', TO_DATE('2025/10/03 09:45:00', 'YYYY/MM/DD HH24:MI:SS'), 1);
INSERT INTO Train (train_id, operator_id, train_departure_date_time, path_id) VALUES (5435, 'PT509017800', TO_DATE('2025/10/03 18:00:00', 'YYYY/MM/DD HH24:MI:SS'), 2);
INSERT INTO Train (train_id, operator_id, train_departure_date_time, path_id) VALUES (5437, 'PT509017800', TO_DATE('2025/10/06 10:00:00', 'YYYY/MM/DD HH24:MI:SS'), 2);

INSERT INTO PathFacilities (path_id, facility_id, path_facility_order) VALUES (1, 50, 1);
INSERT INTO PathFacilities (path_id, facility_id, path_facility_order) VALUES (1, 48, 2);
INSERT INTO PathFacilities (path_id, facility_id, path_facility_order) VALUES (1, 45, 3);
INSERT INTO PathFacilities (path_id, facility_id, path_facility_order) VALUES (1, 43, 4);
INSERT INTO PathFacilities (path_id, facility_id, path_facility_order) VALUES (1, 13, 5);
INSERT INTO PathFacilities (path_id, facility_id, path_facility_order) VALUES (1, 20, 6);
INSERT INTO PathFacilities (path_id, facility_id, path_facility_order) VALUES (1, 8, 7);
INSERT INTO PathFacilities (path_id, facility_id, path_facility_order) VALUES (1, 12, 8);
INSERT INTO PathFacilities (path_id, facility_id, path_facility_order) VALUES (1, 17, 9);
INSERT INTO PathFacilities (path_id, facility_id, path_facility_order) VALUES (1, 21, 10);
INSERT INTO PathFacilities (path_id, facility_id, path_facility_order) VALUES (1, 16, 11);
INSERT INTO PathFacilities (path_id, facility_id, path_facility_order) VALUES (1, 11, 12);
INSERT INTO PathFacilities (path_id, facility_id, path_facility_order) VALUES (2, 11, 1);
INSERT INTO PathFacilities (path_id, facility_id, path_facility_order) VALUES (2, 16, 2);
INSERT INTO PathFacilities (path_id, facility_id, path_facility_order) VALUES (2, 21, 3);
INSERT INTO PathFacilities (path_id, facility_id, path_facility_order) VALUES (2, 17, 4);
INSERT INTO PathFacilities (path_id, facility_id, path_facility_order) VALUES (2, 12, 5);
INSERT INTO PathFacilities (path_id, facility_id, path_facility_order) VALUES (2, 8, 6);
INSERT INTO PathFacilities (path_id, facility_id, path_facility_order) VALUES (2, 20, 7);
INSERT INTO PathFacilities (path_id, facility_id, path_facility_order) VALUES (2, 13, 8);
INSERT INTO PathFacilities (path_id, facility_id, path_facility_order) VALUES (2, 43, 9);
INSERT INTO PathFacilities (path_id, facility_id, path_facility_order) VALUES (2, 45, 10);
INSERT INTO PathFacilities (path_id, facility_id, path_facility_order) VALUES (2, 48, 11);
INSERT INTO PathFacilities (path_id, facility_id, path_facility_order) VALUES (2, 50, 12);

INSERT INTO TrainLocomotive (train_id, locomotive_id) VALUES (5421, '5621');
INSERT INTO TrainLocomotive (train_id, locomotive_id) VALUES (5421, '5623');
INSERT INTO TrainLocomotive (train_id, locomotive_id) VALUES (5435, '5623');
INSERT INTO TrainLocomotive (train_id, locomotive_id) VALUES (5437, '5621');

INSERT INTO TrainFreight (train_id, freight_id) VALUES (5421, 2001);
INSERT INTO TrainFreight (train_id, freight_id) VALUES (5421, 2002);
INSERT INTO TrainFreight (train_id, freight_id) VALUES (5421, 2003);
INSERT INTO TrainFreight (train_id, freight_id) VALUES (5421, 2004);
INSERT INTO TrainFreight (train_id, freight_id) VALUES (5421, 2005);
INSERT INTO TrainFreight (train_id, freight_id) VALUES (5435, 2006);
INSERT INTO TrainFreight (train_id, freight_id) VALUES (5437, 2050);
INSERT INTO TrainFreight (train_id, freight_id) VALUES (5437, 2051);

INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (2001, '333 0 001');
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (2001, '333 0 002');
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (2001, '333 0 004');
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (2001, '333 0 005');
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (2001, '333 0 006');
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (2002, '356 3 089');
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (2003, '181 1 011');
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (2003, '181 1 012');
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (2004, '181 1 013');
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (2005, '356 3 077');
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (2005, '356 3 078');
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (2005, '356 3 079');
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (2005, '356 3 080');
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (2006, '333 0 003');
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (2006, '333 0 007');
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (2007, '356 3 090');
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (2007, '356 3 091');
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (2007, '356 3 092');
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (2050, '333 0 001');
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (2050, '333 0 002');
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (2050, '333 0 004');
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (2050, '333 0 005');
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (2050, '333 0 006');
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (2051, '181 1 011');
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (2051, '181 1 012');
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (2051, '356 3 077');
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (2051, '356 3 078');
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (2051, '356 3 079');
INSERT INTO FreightWagon (freight_id, wagon_id) VALUES (2051, '356 3 080');


--needed for uslp07

INSERT INTO TrainWagonCargo (train_id, wagon_number, is_loaded, cargo_weight)
VALUES (5421, '333 0 001', 'Sim', 20);
INSERT INTO TrainWagonCargo (train_id, wagon_number, is_loaded, cargo_weight)
VALUES (5421, '333 0 002', 'Sim', 22);
INSERT INTO TrainWagonCargo (train_id, wagon_number, is_loaded, cargo_weight)
VALUES (5421, '333 0 004', 'Sim', 18);
INSERT INTO TrainWagonCargo (train_id, wagon_number, is_loaded, cargo_weight)
VALUES (5421, '333 0 005', 'Sim', 21);
INSERT INTO TrainWagonCargo (train_id, wagon_number, is_loaded, cargo_weight)
VALUES (5421, '333 0 006', 'Sim', 19);
INSERT INTO TrainWagonCargo (train_id, wagon_number, is_loaded, cargo_weight)
VALUES (5421, '356 3 089', 'Sim', 45);
INSERT INTO TrainWagonCargo (train_id, wagon_number, is_loaded, cargo_weight)
VALUES (5421, '181 1 011', 'Sim', 35);
INSERT INTO TrainWagonCargo (train_id, wagon_number, is_loaded, cargo_weight)
VALUES (5421, '181 1 012', 'Sim', 38);
INSERT INTO TrainWagonCargo (train_id, wagon_number, is_loaded, cargo_weight)
VALUES (5421, '181 1 013', 'Sim', 32);
INSERT INTO TrainWagonCargo (train_id, wagon_number, is_loaded, cargo_weight)
VALUES (5421, '356 3 077', 'Sim', 50);
INSERT INTO TrainWagonCargo (train_id, wagon_number, is_loaded, cargo_weight)
VALUES (5421, '356 3 078', 'Sim', 48);
INSERT INTO TrainWagonCargo (train_id, wagon_number, is_loaded, cargo_weight)
VALUES (5421, '356 3 079', 'Sim', 52);
INSERT INTO TrainWagonCargo (train_id, wagon_number, is_loaded, cargo_weight)
VALUES (5421, '356 3 080', 'Sim', 47);
INSERT INTO TrainWagonCargo (train_id, wagon_number, is_loaded, cargo_weight)
VALUES (5435, '333 0 003', 'Nao', 0);
INSERT INTO TrainWagonCargo (train_id, wagon_number, is_loaded, cargo_weight)
VALUES (5435, '333 0 007', 'Nao', 0);
INSERT INTO TrainWagonCargo (train_id, wagon_number, is_loaded, cargo_weight)
VALUES (5437, '333 0 001', 'Nao', 0);
INSERT INTO TrainWagonCargo (train_id, wagon_number, is_loaded, cargo_weight)
VALUES (5437, '333 0 002', 'Nao', 0);
INSERT INTO TrainWagonCargo (train_id, wagon_number, is_loaded, cargo_weight)
VALUES (5437, '333 0 004', 'Nao', 0);
INSERT INTO TrainWagonCargo (train_id, wagon_number, is_loaded, cargo_weight)
VALUES (5437, '333 0 005', 'Nao', 0);
INSERT INTO TrainWagonCargo (train_id, wagon_number, is_loaded, cargo_weight)
VALUES (5437, '333 0 006', 'Nao', 0);
INSERT INTO TrainWagonCargo (train_id, wagon_number, is_loaded, cargo_weight)
VALUES (5437, '181 1 011', 'Sim', 30);
INSERT INTO TrainWagonCargo (train_id, wagon_number, is_loaded, cargo_weight)
VALUES (5437, '181 1 012', 'Sim', 28);
INSERT INTO TrainWagonCargo (train_id, wagon_number, is_loaded, cargo_weight)
VALUES (5437, '356 3 077', 'Sim', 44);
INSERT INTO TrainWagonCargo (train_id, wagon_number, is_loaded, cargo_weight)
VALUES (5437, '356 3 078', 'Sim', 46);
INSERT INTO TrainWagonCargo (train_id, wagon_number, is_loaded, cargo_weight)
VALUES (5437, '356 3 079', 'Sim', 49);
INSERT INTO TrainWagonCargo (train_id, wagon_number, is_loaded, cargo_weight)
VALUES (5437, '356 3 080', 'Sim', 51);

INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, line_segment_length, number_of_tracks, line_segment_order, siding_id) VALUES (1, 1, 'Sim', 8000, 2618, 4, 1, NULL);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, line_segment_length, number_of_tracks, line_segment_order, siding_id) VALUES (3, 2, 'Sim', 8000, 2443, 4, 2, NULL);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, line_segment_length, number_of_tracks, line_segment_order, siding_id) VALUES (10, 3, 'Sim', 8000, 26560, 2, 3, NULL);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, line_segment_length, number_of_tracks, line_segment_order, siding_id) VALUES (11, 3, 'Sim', 8000, 10000, 2, 4, NULL);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, line_segment_length, number_of_tracks, line_segment_order, siding_id) VALUES (15, 4, 'Sim', 8000, 5286, 2, 1, NULL);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, line_segment_length, number_of_tracks, line_segment_order, siding_id) VALUES (16, 4, 'Sim', 8000, 6000, 2, 2, NULL);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, line_segment_length, number_of_tracks, line_segment_order, siding_id) VALUES (14, 5, 'Sim', 8000, 10387, 2, 1, NULL);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, line_segment_length, number_of_tracks, line_segment_order, siding_id) VALUES (12, 5, 'Sim', 8000, 12000, 2, 2, NULL);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, line_segment_length, number_of_tracks, line_segment_order, siding_id) VALUES (13, 6, 'Sim', 8000, 3100, 2, 3, NULL);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, line_segment_length, number_of_tracks, line_segment_order, siding_id) VALUES (20, 7, 'Nao', 6400, 4890, 2, 1, NULL);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, line_segment_length, number_of_tracks, line_segment_order, siding_id) VALUES (18, 8, 'Sim', 8000, 6000, 1, 1, NULL);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, line_segment_length, number_of_tracks, line_segment_order, siding_id) VALUES (21, 8, 'Sim', 8000, 5000, 1, 2, 1);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, line_segment_length, number_of_tracks, line_segment_order, siding_id) VALUES (22, 9, 'Sim', 8000, 12000, 1, 3, NULL);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, line_segment_length, number_of_tracks, line_segment_order, siding_id) VALUES (25, 9, 'Sim', 8000, 20829, 1, 8, 2);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, line_segment_length, number_of_tracks, line_segment_order, siding_id) VALUES (26, 9, 'Sim', 8000, 4264, 1, 9, NULL);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, line_segment_length, number_of_tracks, line_segment_order, siding_id) VALUES (30, 21, 'Sim', 8000, 3883, 2, 1, NULL);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, line_segment_length, number_of_tracks, line_segment_order, siding_id) VALUES (31, 22, 'Sim', 8400, 1174, 2, 2, NULL);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, line_segment_length, number_of_tracks, line_segment_order, siding_id) VALUES (32, 22, 'Sim', 8000, 2534, 2, 2, NULL);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, line_segment_length, number_of_tracks, line_segment_order, siding_id) VALUES (33, 23, 'Sim', 8000, 1566, 2, 1, NULL);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, line_segment_length, number_of_tracks, line_segment_order, siding_id) VALUES (34, 23, 'Sim', 8000, 1453, 2, 2, NULL);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, line_segment_length, number_of_tracks, line_segment_order, siding_id) VALUES (35, 24, 'Sim', 8100, 3597, 2, 1, NULL);
INSERT INTO LineSegment (line_segment_id, line_id, is_electrified, maximum_weight, line_segment_length, number_of_tracks, line_segment_order, siding_id) VALUES (36, 24, 'Sim', 8000, 4334, 2, 2, NULL);

UPDATE LineSegment SET speed_limit_kmh = 120 WHERE line_segment_id IN (1, 3);

UPDATE LineSegment SET speed_limit_kmh = 120 WHERE line_segment_id IN (10, 11, 15, 16, 14, 12, 13, 30, 31, 32, 33, 34, 35, 36);

UPDATE LineSegment SET speed_limit_kmh = 100 WHERE line_segment_id IN (21, 22, 25, 26);

UPDATE LineSegment SET speed_limit_kmh = 100 WHERE line_segment_id = 20;

UPDATE LineSegment SET speed_limit_kmh = 100 WHERE line_segment_id = 18;

COMMIT;