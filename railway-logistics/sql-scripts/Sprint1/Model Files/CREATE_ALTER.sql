-- Drop Tables --

DROP TABLE "Line"                   CASCADE CONSTRAINTS PURGE;
DROP TABLE LineSegment              CASCADE CONSTRAINTS PURGE;
DROP TABLE Station                  CASCADE CONSTRAINTS PURGE;
DROP TABLE Locomotive               CASCADE CONSTRAINTS PURGE;
DROP TABLE Wagon                    CASCADE CONSTRAINTS PURGE;
DROP TABLE DieselEngine             CASCADE CONSTRAINTS PURGE;
DROP TABLE EngineType               CASCADE CONSTRAINTS PURGE;
DROP TABLE WagonType                CASCADE CONSTRAINTS PURGE;
DROP TABLE Gauge                    CASCADE CONSTRAINTS PURGE;
DROP TABLE "Owner"                  CASCADE CONSTRAINTS PURGE;
DROP TABLE Operator                 CASCADE CONSTRAINTS PURGE;
DROP TABLE WagonModel               CASCADE CONSTRAINTS PURGE;
DROP TABLE ElectricEngine           CASCADE CONSTRAINTS PURGE;
DROP TABLE LocomotiveModel          CASCADE CONSTRAINTS PURGE;
DROP TABLE Engine                   CASCADE CONSTRAINTS PURGE;
DROP TABLE BogieType                CASCADE CONSTRAINTS PURGE;
DROP TABLE Maker                    CASCADE CONSTRAINTS PURGE;

-- Create Tables --

CREATE TABLE "Line" (
    line_id integer                 CONSTRAINT pkLineID PRIMARY KEY,
    owner_id char(11)               CONSTRAINT nnLineOwnerID NOT NULL,
    start_station_id integer        CONSTRAINT nnLineStartStationID NOT NULL,
    end_station_id integer          CONSTRAINT nnLineEndStationID NOT NULL,
    gauge_id integer                CONSTRAINT nnLineGaugeID NOT NULL,
    "name" varchar(255)             CONSTRAINT nnLineName NOT NULL
);

CREATE TABLE BogieType (
    bogie_type_id integer           CONSTRAINT pkBogieTypeID PRIMARY KEY,
    "name" varchar(255)             CONSTRAINT nnBogieTypeName NOT NULL
);

CREATE TABLE Maker (
    maker_id integer                CONSTRAINT pkMakerID PRIMARY KEY,
    "name" varchar(255)             CONSTRAINT nnMakerName NOT NULL
);

CREATE TABLE LineSegment (
    line_segment_id integer         CONSTRAINT pkLineSegmentID PRIMARY KEY,
    line_id integer                 CONSTRAINT nnLineSegmentLineID NOT NULL,
    is_electrified varchar(3)       CONSTRAINT nnLineSegmentIsElectrified NOT NULL,
    maximum_weight integer          CONSTRAINT nnLineSegmentMaximumWeight NOT NULL,
    "length" integer                CONSTRAINT nnLineSegmentLength NOT NULL,
    number_of_tracks integer        CONSTRAINT nnLineSegmentNumberofTracks NOT NULL,
    "order" integer                 CONSTRAINT nnLineSegmentOrder NOT NULL
);

CREATE TABLE Station (
    station_id integer              CONSTRAINT pkStationID PRIMARY KEY,
    "name" varchar(255)             CONSTRAINT nnStationName NOT NULL
);

CREATE TABLE Locomotive (
    "number" integer                CONSTRAINT pkLocomotiveNumber PRIMARY KEY,
    "name" varchar(255)             CONSTRAINT nnLocomotiveName NOT NULL,
    "year" integer                  CONSTRAINT nnLocomotiveYear NOT NULL
                                    CONSTRAINT ckLocomotiveYear CHECK ("year" BETWEEN 1900 AND 2025),
    operator_id char(11)            CONSTRAINT nnLocomotiveOperatorID NOT NULL,
    model_id integer                CONSTRAINT nnLocomotiveModelID NOT NULL
);

CREATE TABLE Wagon (
    "number" varchar(9)             CONSTRAINT pkWagonNumber PRIMARY KEY
                                    CONSTRAINT ckWagonNumber CHECK (REGEXP_LIKE("number", '^[0-9]{3} [0-9] [0-9]{3}$')),
    operator_id char(11)            CONSTRAINT nnWagonOperatorID NOT NULL,
    wagon_model_id integer          CONSTRAINT nnWagonWagonModelID NOT NULL,
    "year" integer                  CONSTRAINT nnWagonYear NOT NULL
                                    CONSTRAINT ckWagonYear CHECK ("year" BETWEEN 1900 AND 2025)
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
    vat_number char(11)             CONSTRAINT pkOwnerVATNumber PRIMARY KEY
                                    CONSTRAINT ckOwnerVATNumber CHECK (REGEXP_LIKE(vat_number, '^[A-Z]{2}[0-9]{9}$')),
    "name" varchar(255)             CONSTRAINT nnOwnerName NOT NULL,
    short_name varchar(255)         CONSTRAINT nnOwnerShortName NOT NULL
);

CREATE TABLE Operator (
    vat_number char(11)             CONSTRAINT pkOperatorVATNumber PRIMARY KEY
                                    CONSTRAINT ckOperatorVATNumber CHECK (REGEXP_LIKE(vat_number, '^[A-Z]{2}[0-9]{9}$')),
    "name" varchar(255)             CONSTRAINT nnOperatorName NOT NULL,
    short_name varchar(255)         CONSTRAINT nnOperatorShortName NOT NULL
);

CREATE TABLE WagonModel (
    wagon_model_id integer          CONSTRAINT pkWagonModelID PRIMARY KEY,
    gauge_id integer                CONSTRAINT nnWagonModelGaugeID NOT NULL,
    wagon_type_id integer           CONSTRAINT nnWagonModelWagonTypeID NOT NULL,
    "name" varchar(255)             CONSTRAINT nnWagonModelName NOT NULL,
    number_of_bogies integer        CONSTRAINT nnWagonModelNumberOfBogies NOT NULL,
    payload integer                 CONSTRAINT nnWagonModelPayload NOT NULL,
    maximum_speed integer           CONSTRAINT nnWagonModelMaximumSpeed NOT NULL,
    "length" integer                CONSTRAINT nnWagonModelLength NOT NULL,
    width integer                   CONSTRAINT nnWagonModelWidth NOT NULL,
    height integer                  CONSTRAINT nnWagonModelHeight NOT NULL,
    volume integer                  CONSTRAINT nnWagonModelVolume NOT NULL,
    maker_id integer                CONSTRAINT nnWagonModelMaker NOT NULL,
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
    bogie_type_id integer           CONSTRAINT nnLocomotiveModelBogie NOT NULL,
    maker_id integer                CONSTRAINT nnLocomotiveModelMaker NOT NULL,
    power integer                   CONSTRAINT nnLocomotiveModelPower NOT NULL,
    "weight" integer                CONSTRAINT nnLocomotiveModelWeight NOT NULL,
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

-- Alter Tables (add foreign keys) --

ALTER TABLE LineSegment         ADD CONSTRAINT fkLineSegmentLine                FOREIGN KEY (line_id)               REFERENCES "Line"(line_id);
ALTER TABLE "Line"              ADD CONSTRAINT fkLineOwner                      FOREIGN KEY (owner_id)              REFERENCES "Owner"(vat_number);
ALTER TABLE "Line"              ADD CONSTRAINT fkLineStartStation               FOREIGN KEY (start_station_id)      REFERENCES Station(station_id);
ALTER TABLE "Line"              ADD CONSTRAINT fkLineEndStation                 FOREIGN KEY (end_station_id)        REFERENCES Station(station_id);
ALTER TABLE "Line"              ADD CONSTRAINT fkLineGauge                      FOREIGN KEY (gauge_id)              REFERENCES Gauge(gauge_id);
ALTER TABLE Locomotive          ADD CONSTRAINT fkLocomotiveOperator             FOREIGN KEY (operator_id)           REFERENCES Operator(vat_number);
ALTER TABLE Locomotive          ADD CONSTRAINT fkLocomotiveModel                FOREIGN KEY (model_id)              REFERENCES LocomotiveModel(locomotive_model_id);
ALTER TABLE Wagon               ADD CONSTRAINT fkWagonOperator                  FOREIGN KEY (operator_id)           REFERENCES Operator(vat_number);
ALTER TABLE Wagon               ADD CONSTRAINT fkWagonWagonModel                FOREIGN KEY (wagon_model_id)        REFERENCES WagonModel(wagon_model_id);
ALTER TABLE Engine              ADD CONSTRAINT fkEngineEngineTypeID             FOREIGN KEY (engine_type_id)        REFERENCES EngineType(engine_type_id);
ALTER TABLE DieselEngine        ADD CONSTRAINT fkDieselEngineEngineID           FOREIGN KEY (engine_id)             REFERENCES Engine(engine_id);
ALTER TABLE WagonModel          ADD CONSTRAINT fkWagonModelGauge                FOREIGN KEY (gauge_id)              REFERENCES Gauge(gauge_id);
ALTER TABLE WagonModel          ADD CONSTRAINT fkWagonModelWagonType            FOREIGN KEY (wagon_type_id)         REFERENCES WagonType(wagon_type_id);
ALTER TABLE ElectricEngine      ADD CONSTRAINT fkElectricEngineEngineID         FOREIGN KEY (engine_id)             REFERENCES Engine(engine_id);
ALTER TABLE LocomotiveModel     ADD CONSTRAINT fkLocomotiveModelEngineID        FOREIGN KEY (engine_id)             REFERENCES Engine(engine_id);
ALTER TABLE LocomotiveModel     ADD CONSTRAINT fkLocomotiveModelGaugeID         FOREIGN KEY (gauge_id)              REFERENCES Gauge(gauge_id);
ALTER TABLE LocomotiveModel     ADD CONSTRAINT fkLocomotiveModelBogieTypeID     FOREIGN KEY (bogie_type_id)         REFERENCES BogieType(bogie_type_id);
ALTER TABLE LocomotiveModel     ADD CONSTRAINT fkLocomotiveModelMaker           FOREIGN KEY (maker_id)              REFERENCES Maker(maker_id);
ALTER TABLE WagonModel          ADD CONSTRAINT fkWagonModelMaker                FOREIGN KEY (maker_id)              REFERENCES Maker(maker_id);