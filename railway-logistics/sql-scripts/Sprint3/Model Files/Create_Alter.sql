-- DROP TABLES --

DROP TABLE TrainLocomotive              CASCADE CONSTRAINTS PURGE;
DROP TABLE Siding                       CASCADE CONSTRAINTS PURGE;
DROP TABLE FacilityInfrastructure       CASCADE CONSTRAINTS PURGE;
DROP TABLE Infrastructure               CASCADE CONSTRAINTS PURGE;
DROP TABLE FreightWagon                 CASCADE CONSTRAINTS PURGE;
DROP TABLE TrainFreight                 CASCADE CONSTRAINTS PURGE;
DROP TABLE PathFacility                 CASCADE CONSTRAINTS PURGE;
DROP TABLE WagonModelGauge              CASCADE CONSTRAINTS PURGE;
DROP TABLE LocomotiveModelGauge         CASCADE CONSTRAINTS PURGE;
DROP TABLE LineSegment                  CASCADE CONSTRAINTS PURGE;
DROP TABLE Freight                      CASCADE CONSTRAINTS PURGE;
DROP TABLE Train                        CASCADE CONSTRAINTS PURGE;
DROP TABLE TrainPath                    CASCADE CONSTRAINTS PURGE;
DROP TABLE RailwayLine                  CASCADE CONSTRAINTS PURGE;
DROP TABLE EletricEngine                CASCADE CONSTRAINTS PURGE;
DROP TABLE DieselEngine                 CASCADE CONSTRAINTS PURGE;
DROP TABLE Engine                       CASCADE CONSTRAINTS PURGE;
DROP TABLE EngineType                   CASCADE CONSTRAINTS PURGE;
DROP TABLE Locomotive                   CASCADE CONSTRAINTS PURGE;
DROP TABLE LocomotiveModel              CASCADE CONSTRAINTS PURGE;
DROP TABLE Wagon                        CASCADE CONSTRAINTS PURGE;
DROP TABLE WagonModel                   CASCADE CONSTRAINTS PURGE;
DROP TABLE WagonType                    CASCADE CONSTRAINTS PURGE;
DROP TABLE Maker                        CASCADE CONSTRAINTS PURGE;
DROP TABLE BogieType                    CASCADE CONSTRAINTS PURGE;
DROP TABLE Facility                     CASCADE CONSTRAINTS PURGE;
DROP TABLE LineOwner                    CASCADE CONSTRAINTS PURGE;
DROP TABLE Gauge                        CASCADE CONSTRAINTS PURGE;
DROP TABLE VehicleOperator              CASCADE CONSTRAINTS PURGE;
DROP TABLE Route                        CASCADE CONSTRAINTS PURGE;
DROP TABLE RouteType                    CASCADE CONSTRAINTS PURGE;
DROP TABLE TrainRecord                  CASCADE CONSTRAINTS PURGE;

-- CREATE TABLES --

CREATE TABLE WagonType (
  wagon_type_id   integer CONSTRAINT pkWagonTypeID PRIMARY KEY, 
  wagon_type_name varchar(255) CONSTRAINT nnWagonTypeName NOT NULL
);

CREATE TABLE WagonModel (
  wagon_model_id      integer CONSTRAINT pkWagonModelID PRIMARY KEY, 
  wagon_type_id      integer CONSTRAINT nnWagonModelTypeID NOT NULL, 
  maker_id            integer CONSTRAINT nnWagonModelMakerId NOT NULL, 
  wagon_model_name    varchar(255) CONSTRAINT nnWagonModelName NOT NULL, 
  number_of_bogies    integer CONSTRAINT nnWagonModelNumberOfBogies NOT NULL, 
  wagon_model_payload integer CONSTRAINT nnWagonModelPayload NOT NULL, 
  max_speed           integer CONSTRAINT nnWagonModelMaxSpeed NOT NULL, 
  wagon_model_lenght  integer CONSTRAINT nnWagonModelLength NOT NULL, 
  wagon_model_width   integer CONSTRAINT nnWagonModelWidth NOT NULL, 
  wagon_model_height  integer CONSTRAINT nnWagonModelHeight NOT NULL, 
  wagon_model_weight  integer CONSTRAINT nnWagonModelWeight NOT NULL, 
  wagon_model_volume  integer CONSTRAINT nnWagonModelVolume NOT NULL, 
  bogie_type_id       integer CONSTRAINT nnWagonModelBogieTypeID NOT NULL
);

CREATE TABLE Wagon (
  wagon_number      char(9) CONSTRAINT pkWagonNumber PRIMARY KEY
                            CONSTRAINT ckWagonNumber CHECK (REGEXP_LIKE(wagon_number, '^[0-9]{3} [0-9]{1} [0-9]{3}$')), 
  operator_id       char(11) CONSTRAINT nnWagonOperatorID NOT NULL, 
  start_facility_id integer CONSTRAINT nnWagonStartFacilityID NOT NULL, 
  wagon_model_id    integer CONSTRAINT nnWagonWagonModelID NOT NULL, 
  wagon_year        integer CONSTRAINT nnWagonYear NOT NULL
                            CONSTRAINT ckWagonYear CHECK (wagon_year BETWEEN 1900 AND 2025)
);

CREATE TABLE VehicleOperator (
  vat_number    char(11)  CONSTRAINT pkVehicleOperatorVatNumber PRIMARY KEY
                          CONSTRAINT ckOperatorVATNumber CHECK (REGEXP_LIKE(vat_number, '^[A-Z]{2}[0-9]{9}$')), 
  operator_name varchar(255) CONSTRAINT nnVehicleOperatorOperatorName NOT NULL, 
  short_name    varchar(255) CONSTRAINT nnVehicleOperatorShortName NOT NULL
);

CREATE TABLE Locomotive (
  locomotive_number integer CONSTRAINT pkLocomotiveNumber PRIMARY KEY, 
  operator_id       char(11) CONSTRAINT nnLocomotiveOperatorID NOT NULL, 
  model_id          integer CONSTRAINT nnLocomotiveModelID NOT NULL, 
  start_facility_id integer CONSTRAINT nnLocomotiveStartFacilityID NOT NULL, 
  locomotive_name   varchar(255) CONSTRAINT nnLocomotiveName NOT NULL, 
  locomotive_year   integer CONSTRAINT nnLocomotiveYear NOT NULL
                            CONSTRAINT ckLocomotiveYear CHECK (locomotive_year BETWEEN 1900 AND 2025)
);

CREATE TABLE Gauge (
  gauge_id    integer CONSTRAINT pkGaugeID PRIMARY KEY, 
  gauge_name  varchar(255) CONSTRAINT nnGaugeName NOT NULL, 
  gauge_width integer CONSTRAINT nnGaugeWidth NOT NULL
);

CREATE TABLE LocomotiveModel (
  locomotive_model_id       integer CONSTRAINT pkLocomotiveModelID PRIMARY KEY, 
  engine_id                 integer CONSTRAINT nnLocomotiveModelEngineID NOT NULL, 
  bogie_type_id             integer CONSTRAINT nnLocomotiveModelBogieTypeID NOT NULL, 
  maker_id                  integer CONSTRAINT nnLocomotiveModelMakerID NOT NULL, 
  locomotive_model_name     varchar(255) CONSTRAINT nnLocomotiveModelName NOT NULL, 
  number_of_bogies          integer CONSTRAINT nnLocomotiveModelNumberOfBogies NOT NULL, 
  locomotive_model_power    integer CONSTRAINT nnLocomotiveModelPower NOT NULL, 
  locomotive_model_length   integer CONSTRAINT nnLocomotiveModelLength NOT NULL, 
  locomotive_model_width    integer CONSTRAINT nnLocomotiveModelWidth NOT NULL, 
  locomotive_model_weight   integer CONSTRAINT nnLocomotiveModelWeight NOT NULL, 
  maximum_speed             integer CONSTRAINT nnLocomotiveModelMaximumSpeed NOT NULL, 
  operational_speed         integer CONSTRAINT nnLocomotiveModelOperationalSpeed NOT NULL, 
  locomotive_model_traction integer CONSTRAINT nnLocomotiveModelTraction NOT NULL
);

CREATE TABLE Engine (
  engine_id      integer CONSTRAINT pkEngineID PRIMARY KEY, 
  engine_type_id integer CONSTRAINT nnEngineEngineTypeID NOT NULL
);

CREATE TABLE DieselEngine (
  engine_id     integer CONSTRAINT pkDieselEngineEngineID PRIMARY KEY, 
  fuel_capacity integer CONSTRAINT nnDieselEngineFuelCapacity NOT NULL
);

CREATE TABLE EngineType (
  engine_type_id   integer CONSTRAINT pkEngineTypeID PRIMARY KEY, 
  engine_type_name varchar(255) CONSTRAINT nnEngineTypeName NOT NULL
);

CREATE TABLE EletricEngine (
  engine_id integer CONSTRAINT pkEletricEngineEngineID PRIMARY KEY, 
  voltage   integer CONSTRAINT nnEletricEngineVoltage NOT NULL, 
  frequency integer CONSTRAINT nnEletricEngineFrequency NOT NULL
);

CREATE TABLE LineSegment (
  line_segment_id     integer CONSTRAINT pkLineSegmentID PRIMARY KEY, 
  line_id             integer CONSTRAINT nnLineSegmentLineID NOT NULL, 
  is_electrified      varchar(3) CONSTRAINT nnLineSegmentIsElectrified NOT NULL, 
  maximum_weight      integer CONSTRAINT nnLineSegmentMaximumWeight NOT NULL, 
  line_segment_lenght integer CONSTRAINT nnLineSegmentLength NOT NULL, 
  number_of_tracks    integer CONSTRAINT nnLineSegmentNumberOfTracks NOT NULL, 
  line_segment_order  integer CONSTRAINT nnLineSegmentOrder NOT NULL, 
  siding_id           integer
);

CREATE TABLE RailwayLine (
  line_id          integer CONSTRAINT pkRailwayLineID PRIMARY KEY, 
  owner_id         char(11) CONSTRAINT nnRailwayLineOwnerID NOT NULL, 
  start_station_id integer CONSTRAINT nnRailwayLineStartStationID NOT NULL, 
  end_station_id   integer CONSTRAINT nnRailwayLineEndStationID NOT NULL, 
  gauge_id         integer CONSTRAINT nnRailwayLineGaugeID NOT NULL, 
  line_name        varchar(255) CONSTRAINT nnRailwayLineLineName NOT NULL
);

CREATE TABLE LineOwner (
  vat_number char(11) CONSTRAINT pkOwnerVatNumber PRIMARY KEY
                      CONSTRAINT ckOwnerVATNumber CHECK (REGEXP_LIKE(vat_number, '^[A-Z]{2}[0-9]{9}$')), 
  owner_name varchar(255) CONSTRAINT nnOwnerName NOT NULL, 
  short_name varchar(255) CONSTRAINT nnOwnerShortName NOT NULL
);

CREATE TABLE Facility (
  facility_id   integer CONSTRAINT pkFacilityID PRIMARY KEY, 
  facility_name varchar(255) CONSTRAINT nnFacilityName NOT NULL, 
  is_intermodal varchar(3) CONSTRAINT nnFacilityIsIntermodal NOT NULL
);

CREATE TABLE BogieType (
  bogie_type_id   integer CONSTRAINT pkBogieTypeID PRIMARY KEY, 
  bogie_type_name varchar(255) CONSTRAINT nnBogieTypeName NOT NULL
);

CREATE TABLE Maker (
  maker_id   integer CONSTRAINT pkMakerID PRIMARY KEY, 
  maker_name varchar(255) CONSTRAINT nnMakerName NOT NULL
);

CREATE TABLE Train (
  train_id                  integer CONSTRAINT pkTrainID PRIMARY KEY, 
  operator_id               char(11) CONSTRAINT nnTrainOperatorID NOT NULL, 
  path_id                   integer CONSTRAINT nnTrainPathID NOT NULL, 
  train_departure_date_time date CONSTRAINT nnTrainDepartureDate NOT NULL,
  max_length                integer CONSTRAINT nnTrainMaxLength NOT NULL
);

CREATE TABLE Freight (
  freight_id              integer CONSTRAINT pkFreightID PRIMARY KEY, 
  freight_date            date CONSTRAINT nnFreightDate NOT NULL, 
  origin_facility_id      integer CONSTRAINT nnFreightOriginFacilityID NOT NULL, 
  destination_facility_id integer CONSTRAINT nnFreightDestinationFacilityID NOT NULL
);

CREATE TABLE LocomotiveModelGauge (
  locomotive_model_id integer, 
  gauge_id            integer, 
  PRIMARY KEY (locomotive_model_id, 
  gauge_id)
);

CREATE TABLE WagonModelGauge (
  wagon_model_id integer, 
  gauge_id       integer, 
  PRIMARY KEY (wagon_model_id, 
  gauge_id)
);

CREATE TABLE PathFacility (
  path_id             integer, 
  facility_id         integer, 
  path_facility_order integer CONSTRAINT nnPathFacilityOrder NOT NULL, 
  PRIMARY KEY (path_id, facility_id)
);

CREATE TABLE TrainPath (
  path_id integer CONSTRAINT pkTrainPathID PRIMARY KEY
);

CREATE TABLE TrainFreight (
  train_id   integer, 
  freight_id integer, 
  PRIMARY KEY (train_id, 
  freight_id)
);

CREATE TABLE FreightWagon (
  freight_id integer, 
  wagon_id   char(9), 
  PRIMARY KEY (freight_id, 
  wagon_id)
);

CREATE TABLE Infrastructure (
  infrastructure_id   integer CONSTRAINT pkInfrastructureID PRIMARY KEY, 
  infrastructure_name varchar(255) CONSTRAINT nnInfrastructureName NOT NULL
);

CREATE TABLE FacilityInfrastructure (
  facility_id       integer, 
  infrastructure_id integer, 
  PRIMARY KEY (facility_id, 
  infrastructure_id)
);

CREATE TABLE Siding (
  siding_id       integer CONSTRAINT pkSidingID PRIMARY KEY, 
  siding_position integer CONSTRAINT nnSidingPosition NOT NULL, 
  siding_length   integer CONSTRAINT nnSidingLength NOT NULL
);

CREATE TABLE TrainLocomotive (
  train_id      integer, 
  locomotive_id integer, 
  PRIMARY KEY (train_id, 
  locomotive_id)
);

CREATE TABLE TrainRecord (
  record_id           integer CONSTRAINT pkTrainRecordID PRIMARY KEY, 
  train_id            integer CONSTRAINT nnTrainRecordTrainID NOT NULL, 
  facility_id         integer CONSTRAINT nnTrainRecordFacilityID NOT NULL, 
  arrival_date_time   date CONSTRAINT nnTrainRecordArrivalDateTime NOT NULL, 
  departure_date_time date
);

CREATE TABLE Route (
  route_id          integer CONSTRAINT pkRouteID PRIMARY KEY, 
  train_id          integer CONSTRAINT nnRouteTrainID NOT NULL, 
  route_type_id     integer CONSTRAINT nnRouteTypeID NOT NULL, 
  start_facility_id integer CONSTRAINT nnRouteStartFacilityID NOT NULL, 
  end_facility_id   integer CONSTRAINT nnRouteEndFacilityID NOT NULL
);

CREATE TABLE RouteType (
  route_type_id   integer CONSTRAINT pkRouteTypeID PRIMARY KEY, 
  route_type_name varchar(255) CONSTRAINT nnRouteTypeName NOT NULL
);

-- FOREIGN KEYS --

ALTER TABLE WagonModel              ADD CONSTRAINT fkWagonModelWagonTypeID                    FOREIGN KEY (wagon_type_id)           REFERENCES WagonType (wagon_type_id);
ALTER TABLE Engine                  ADD CONSTRAINT fkEngineEngineTypeID                       FOREIGN KEY (engine_type_id)          REFERENCES EngineType (engine_type_id);
ALTER TABLE DieselEngine            ADD CONSTRAINT fkDieselEngineEngineID                     FOREIGN KEY (engine_id)               REFERENCES Engine (engine_id);
ALTER TABLE EletricEngine           ADD CONSTRAINT fkEletricEngineEngineID                    FOREIGN KEY (engine_id)               REFERENCES Engine (engine_id);
ALTER TABLE LocomotiveModel         ADD CONSTRAINT fkLocomotiveModelEngineID                  FOREIGN KEY (engine_id)               REFERENCES Engine (engine_id);
ALTER TABLE Locomotive              ADD CONSTRAINT fkLocomotiveModelID                        FOREIGN KEY (model_id)                REFERENCES LocomotiveModel (locomotive_model_id);
ALTER TABLE TrainLocomotive         ADD CONSTRAINT fkTrainLocomotiveLocomotiveNumber          FOREIGN KEY (locomotive_id)           REFERENCES Locomotive (locomotive_number);
ALTER TABLE TrainLocomotive         ADD CONSTRAINT fkTrainLocomotiveTrainID                   FOREIGN KEY (train_id)                REFERENCES Train (train_id);
ALTER TABLE WagonModelGauge         ADD CONSTRAINT fkWagonModelGaugeWagonModelID              FOREIGN KEY (wagon_model_id)          REFERENCES WagonModel (wagon_model_id);
ALTER TABLE WagonModelGauge         ADD CONSTRAINT fkWagonModelGaugeGaugeID                   FOREIGN KEY (gauge_id)                REFERENCES Gauge (gauge_id);
ALTER TABLE LocomotiveModelGauge    ADD CONSTRAINT fkLocomotiveModelGaugeGaugeID              FOREIGN KEY (gauge_id)                REFERENCES Gauge (gauge_id);
ALTER TABLE LocomotiveModelGauge    ADD CONSTRAINT fkLocomotiveModelGaugeLocomotiveModelID    FOREIGN KEY (locomotive_model_id)     REFERENCES LocomotiveModel (locomotive_model_id);
ALTER TABLE Train                   ADD CONSTRAINT fkTrainPathID                              FOREIGN KEY (path_id)                 REFERENCES TrainPath (path_id);
ALTER TABLE PathFacility            ADD CONSTRAINT fkPathFacilityPathID                       FOREIGN KEY (path_id)                 REFERENCES TrainPath (path_id);
ALTER TABLE PathFacility            ADD CONSTRAINT fkPathFacilityFacilityID                   FOREIGN KEY (facility_id)             REFERENCES Facility (facility_id);
ALTER TABLE FacilityInfrastructure  ADD CONSTRAINT fkFacilityInfrastructureFacilityID         FOREIGN KEY (facility_id)             REFERENCES Facility (facility_id);
ALTER TABLE FacilityInfrastructure  ADD CONSTRAINT fkFacilityInfrastructureInfrastructureID   FOREIGN KEY (infrastructure_id)       REFERENCES Infrastructure (infrastructure_id);
ALTER TABLE FreightWagon            ADD CONSTRAINT fkFreightWagonWagonNumber                  FOREIGN KEY (wagon_id)                REFERENCES Wagon (wagon_number);
ALTER TABLE FreightWagon            ADD CONSTRAINT fkFreightWagonFreightID                    FOREIGN KEY (freight_id)              REFERENCES Freight (freight_id);
ALTER TABLE TrainFreight            ADD CONSTRAINT fkTrainFreightFreightID                    FOREIGN KEY (freight_id)              REFERENCES Freight (freight_id);
ALTER TABLE TrainFreight            ADD CONSTRAINT fkTrainFreightTrainID                      FOREIGN KEY (train_id)                REFERENCES Train (train_id);
ALTER TABLE RailwayLine             ADD CONSTRAINT fkRailwayLineOwnerID                       FOREIGN KEY (owner_id)                REFERENCES LineOwner (vat_number);
ALTER TABLE LineSegment             ADD CONSTRAINT fkLineSegmentLineID                        FOREIGN KEY (line_id)                 REFERENCES RailwayLine (line_id);
ALTER TABLE LineSegment             ADD CONSTRAINT fkLineSegmentSidingID                      FOREIGN KEY (siding_id)               REFERENCES Siding (siding_id);
ALTER TABLE RailwayLine             ADD CONSTRAINT fkRailwayLineStartStationID                FOREIGN KEY (start_station_id)        REFERENCES Facility (facility_id);
ALTER TABLE RailwayLine             ADD CONSTRAINT fkRailwayLineEndStationID                  FOREIGN KEY (end_station_id)          REFERENCES Facility (facility_id);
ALTER TABLE LocomotiveModel         ADD CONSTRAINT fkLocomotiveModelBogieTypeID               FOREIGN KEY (bogie_type_id)           REFERENCES BogieType (bogie_type_id);
ALTER TABLE WagonModel              ADD CONSTRAINT fkWagonModelBogieTypeID                    FOREIGN KEY (bogie_type_id)           REFERENCES BogieType (bogie_type_id);
ALTER TABLE LocomotiveModel         ADD CONSTRAINT fkLocomotiveModelMakerID                   FOREIGN KEY (maker_id)                REFERENCES Maker (maker_id);
ALTER TABLE WagonModel              ADD CONSTRAINT fkWagonModelMakerID                        FOREIGN KEY (maker_id)                REFERENCES Maker (maker_id);
ALTER TABLE Wagon                   ADD CONSTRAINT fkWagonOperatorVATNumber                   FOREIGN KEY (operator_id)             REFERENCES VehicleOperator (vat_number);
ALTER TABLE Train                   ADD CONSTRAINT fkTrainOperatorVATNumber                   FOREIGN KEY (operator_id)             REFERENCES VehicleOperator (vat_number);
ALTER TABLE Locomotive              ADD CONSTRAINT fkLocomotiveOperatorVATNumber              FOREIGN KEY (operator_id)             REFERENCES VehicleOperator (vat_number);
ALTER TABLE RailwayLine             ADD CONSTRAINT fkRailwayLineGaugeID                       FOREIGN KEY (gauge_id)                REFERENCES Gauge (gauge_id);
ALTER TABLE Wagon                   ADD CONSTRAINT fkWagonWagonModelID                        FOREIGN KEY (wagon_model_id)          REFERENCES WagonModel (wagon_model_id);
ALTER TABLE Freight                 ADD CONSTRAINT fkFreightOriginFacilityID                  FOREIGN KEY (origin_facility_id)      REFERENCES Facility (facility_id);
ALTER TABLE Freight                 ADD CONSTRAINT fkFreightDestinationFacilityID             FOREIGN KEY (destination_facility_id) REFERENCES Facility (facility_id);
ALTER TABLE TrainRecord             ADD CONSTRAINT fkTrainRecordTrainID                       FOREIGN KEY (train_id)                REFERENCES Train (train_id);
ALTER TABLE TrainRecord             ADD CONSTRAINT fkTrainRecordFacilityID                    FOREIGN KEY (facility_id)             REFERENCES Facility (facility_id);
ALTER TABLE Route                   ADD CONSTRAINT fkRouteTrainID                             FOREIGN KEY (train_id)                REFERENCES Train (train_id);
ALTER TABLE Route                   ADD CONSTRAINT fkRouteStartStationID                      FOREIGN KEY (start_facility_id)       REFERENCES Facility (facility_id);
ALTER TABLE Route                   ADD CONSTRAINT fkRouteEndStationID                        FOREIGN KEY (end_facility_id)         REFERENCES Facility (facility_id);
ALTER TABLE Route                   ADD CONSTRAINT fkRouteRouteTypeID                         FOREIGN KEY (route_type_id)           REFERENCES RouteType (route_type_id);
ALTER TABLE Locomotive              ADD CONSTRAINT fkLocomotiveStartFacilityID                FOREIGN KEY (start_facility_id)       REFERENCES Facility (facility_id);
ALTER TABLE Wagon                   ADD CONSTRAINT fkWagonStartFacilityID                     FOREIGN KEY (start_facility_id)       REFERENCES Facility (facility_id);

-- UNIQUES --

CREATE UNIQUE INDEX unWagonTypeName ON WagonType (LOWER(TRIM(wagon_type_name)));
CREATE UNIQUE INDEX unWagonModelName ON WagonModel (LOWER(TRIM(wagon_model_name)));
CREATE UNIQUE INDEX unVeichleOperatorName ON VehicleOperator (LOWER(TRIM(operator_name)));
CREATE UNIQUE INDEX unVeichleShortName ON VehicleOperator (LOWER(TRIM(short_name)));
CREATE UNIQUE INDEX unGaugeName ON Gauge (LOWER(TRIM(gauge_name)));
CREATE UNIQUE INDEX unLocomotiveModelName ON LocomotiveModel (LOWER(TRIM(locomotive_model_name)));
CREATE UNIQUE INDEX unEngineTypeName ON EngineType (LOWER(TRIM(engine_type_name)));
CREATE UNIQUE INDEX unRailwayLineName ON RailwayLine (LOWER(TRIM(line_name)));
CREATE UNIQUE INDEX unLineOwnerName ON LineOwner (LOWER(TRIM(owner_name)));
CREATE UNIQUE INDEX unLineShortName ON LineOwner (LOWER(TRIM(short_name)));
CREATE UNIQUE INDEX unFacilityName ON Facility (LOWER(TRIM(facility_name)));
CREATE UNIQUE INDEX unBogieTypeName ON BogieType (LOWER(TRIM(bogie_type_name)));
CREATE UNIQUE INDEX unMakerName ON Maker (LOWER(TRIM(maker_name)));
CREATE UNIQUE INDEX unInfrastructureName ON Infrastructure (LOWER(TRIM(infrastructure_name)));
CREATE UNIQUE INDEX unRouteTypeName ON RouteType (LOWER(TRIM(route_type_name)));