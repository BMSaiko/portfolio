
DROP TABLE Address CASCADE CONSTRAINTS;
DROP TABLE BOO CASCADE CONSTRAINTS;
DROP TABLE BOO_Operation CASCADE CONSTRAINTS;
DROP TABLE Client CASCADE CONSTRAINTS;
DROP TABLE ClientType CASCADE CONSTRAINTS;
DROP TABLE Component CASCADE CONSTRAINTS;
DROP TABLE Country CASCADE CONSTRAINTS;
DROP TABLE ExternalPart CASCADE CONSTRAINTS;
DROP TABLE IntermediateProduct CASCADE CONSTRAINTS;
DROP TABLE InternalPart CASCADE CONSTRAINTS;
DROP TABLE Operation CASCADE CONSTRAINTS;
DROP TABLE OperationType CASCADE CONSTRAINTS;
DROP TABLE "Order" CASCADE CONSTRAINTS;
DROP TABLE OrderProduct CASCADE CONSTRAINTS;
DROP TABLE Part CASCADE CONSTRAINTS;
DROP TABLE PartIN CASCADE CONSTRAINTS;
DROP TABLE PartOut CASCADE CONSTRAINTS;
DROP TABLE Offer CASCADE CONSTRAINTS;
DROP TABLE Product CASCADE CONSTRAINTS;
DROP TABLE ProductFamily CASCADE CONSTRAINTS;
DROP TABLE ProductionOrder CASCADE CONSTRAINTS;
DROP TABLE RawMaterial CASCADE CONSTRAINTS;
DROP TABLE supplier CASCADE CONSTRAINTS;
DROP TABLE Unit CASCADE CONSTRAINTS;
DROP TABLE Workstations CASCADE CONSTRAINTS;
DROP TABLE WorkstationTypes CASCADE CONSTRAINTS;
DROP TABLE WSTypes_OperationTypes CASCADE CONSTRAINTS;

CREATE TABLE Address (
  Address     varchar2(255) NOT NULL, 
  ZIP         varchar2(8) NOT NULL, 
  Town        varchar2(255) NOT NULL, 
  CountryCode varchar2(2) NOT NULL, 
  ClientNIF   varchar2(15) NOT NULL, 
  PRIMARY KEY (Address, 
  ZIP));
CREATE TABLE BOO (
  BOOID  varchar2(10) NOT NULL, 
  PartID varchar2(50) NOT NULL, 
  PRIMARY KEY (BOOID));
CREATE TABLE BOO_Operation (
  OPID      number(10) NOT NULL, 
  BOOID     varchar2(10) NOT NULL, 
  NextOPID  number(10), 
  NextBOOID varchar2(10), 
  PRIMARY KEY (OPID, 
  BOOID));
CREATE TABLE Client (
  NIF      varchar2(15) NOT NULL, 
  Name     varchar2(50) NOT NULL, 
  Email    varchar2(255) NOT NULL, 
  Phone    number(10) NOT NULL, 
  IDClient number(10) NOT NULL, 
  CID      varchar2(2) NOT NULL, 
  Status   number(1) DEFAULT 1 NOT NULL, 
  PRIMARY KEY (NIF));
CREATE TABLE ClientType (
  CID  varchar2(2) NOT NULL, 
  Type varchar2(50) NOT NULL, 
  PRIMARY KEY (CID));
CREATE TABLE Component (
  PartID varchar2(50) NOT NULL, 
  PRIMARY KEY (PartID));
CREATE TABLE Country (
  CountryCode varchar2(2) NOT NULL, 
  Name        varchar2(50) NOT NULL, 
  PRIMARY KEY (CountryCode));
CREATE TABLE ExternalPart (
  PartID varchar2(50) NOT NULL, 
  Stock  number(10) NOT NULL,
  minimumStock number(10) DEFAULT 3 NOT NULL,
  Reserved number(10) DEFAULT 0 NOT NULL,
  PRIMARY KEY (PartID));
CREATE TABLE IntermediateProduct (
  PartID varchar2(50) NOT NULL, 
  PRIMARY KEY (PartID));
CREATE TABLE InternalPart (
  PartID varchar2(50) NOT NULL, 
  PRIMARY KEY (PartID));
CREATE TABLE Operation (
  OPID                  number(10) , 
  Name                  varchar2(255) NOT NULL, 
  OperationTypeOTID     number(10) NOT NULL, 
  ExpectedExecutionTime number(10) NOT NULL, 
  PRIMARY KEY (OPID));
CREATE TABLE OperationType (
  OTID        number(10) , 
  Description varchar2(255) NOT NULL, 
  PRIMARY KEY (OTID));
CREATE TABLE "Order" (
  OID          number(10) , 
  DateDelivery date NOT NULL, 
  DateOrder    date NOT NULL, 
  ClientNIF    varchar2(15) NOT NULL, 
  PRIMARY KEY (OID));
CREATE TABLE OrderProduct (
  Quantity      number(10) NOT NULL, 
  OID           number(10) NOT NULL, 
  ProductPartID varchar2(50) NOT NULL, 
  allMatReserved number(1) DEFAULT 0 NOT NULL, 
  PRIMARY KEY (OID, 
  ProductPartID));
CREATE TABLE Part (
  PartID      varchar2(50) NOT NULL, 
  Description varchar2(255) NOT NULL, 
  PRIMARY KEY (PartID));
CREATE TABLE PartIN (
  QuantityIn number(10) NOT NULL, 
  OPID       number(10) NOT NULL, 
  BOOID      varchar2(10) NOT NULL, 
  PartID     varchar2(50) NOT NULL, 
  PRIMARY KEY (OPID, 
  BOOID, 
  PartID));
CREATE TABLE PartOut (
  Quantity number(10) NOT NULL, 
  PartID   varchar2(50) NOT NULL, 
  OPID     number(10) NOT NULL, 
  BOOID    varchar2(10) NOT NULL, 
  PRIMARY KEY (PartID, 
  OPID, 
  BOOID));
CREATE TABLE Offer (
  PartID           varchar2(50) NOT NULL, 
  SupplierID       number(10) NOT NULL,
  UnitCost         float(10) NOT NULL, 
  DateStart        date NOT NULL, 
  DateEnd          date, 
  minimumOrderSize number(10) NOT NULL, 
  PRIMARY KEY (PartID, 
  SupplierID, DateStart));
CREATE TABLE Product (
  Name   varchar2(255) NOT NULL, 
  PFID   number(3) NOT NULL, 
  PartID varchar2(50) NOT NULL, 
  PRIMARY KEY (PartID));
CREATE TABLE ProductFamily (
  PFID number(3) , 
  Name varchar2(50) NOT NULL, 
  PRIMARY KEY (PFID));
CREATE TABLE ProductionOrder (
  PrOrID        varchar2(10) NOT NULL, 
  OID           number(10) NOT NULL, 
  ProductPartID varchar2(50) NOT NULL, 
  PRIMARY KEY (PrOrID));
CREATE TABLE RawMaterial (
  PartID varchar2(50) NOT NULL, 
  UnitID number(4) NOT NULL, 
  PRIMARY KEY (PartID));
CREATE TABLE supplier (
  SupplierID number(10) , 
  Name       varchar2(255) NOT NULL, 
  PRIMARY KEY (SupplierID));
CREATE TABLE Unit (
  UnitID number(4) , 
  Name   varchar2(255) NOT NULL, 
  PRIMARY KEY (UnitID));
CREATE TABLE Workstations (
  WSID        number(4) , 
  Name        varchar2(50) NOT NULL, 
  Description varchar2(255) NOT NULL, 
  WTID        varchar2(5) NOT NULL, 
  PRIMARY KEY (WSID));
CREATE TABLE WorkstationTypes (
  WTID                 varchar2(5) NOT NULL, 
  Name                 varchar2(255) NOT NULL, 
  MaximumExecutionTime number(10) NOT NULL, 
  SetupTime            number(10) NOT NULL, 
  PRIMARY KEY (WTID));
CREATE TABLE WSTypes_OperationTypes (
  WTID              varchar2(5) NOT NULL, 
  OperationTypeOTID number(10) NOT NULL, 
  PRIMARY KEY (WTID, 
  OperationTypeOTID));

ALTER TABLE Workstations ADD CONSTRAINT FKWorkstatio342984 FOREIGN KEY (WTID) REFERENCES WorkstationTypes (WTID);
ALTER TABLE "Order" ADD CONSTRAINT FKOrder455373 FOREIGN KEY (ClientNIF) REFERENCES Client (NIF);
ALTER TABLE Client ADD CONSTRAINT FKClient207010 FOREIGN KEY (CID) REFERENCES ClientType (CID);
ALTER TABLE OrderProduct ADD CONSTRAINT FKOrderProdu492632 FOREIGN KEY (OID) REFERENCES "Order" (OID);
ALTER TABLE OrderProduct ADD CONSTRAINT FKOrderProdu415970 FOREIGN KEY (ProductPartID) REFERENCES Product (PartID);
ALTER TABLE Product ADD CONSTRAINT FKProduct436806 FOREIGN KEY (PFID) REFERENCES ProductFamily (PFID);
ALTER TABLE BOO_Operation ADD CONSTRAINT FKBOO_Operat961240 FOREIGN KEY (OPID) REFERENCES Operation (OPID);
ALTER TABLE BOO_Operation ADD CONSTRAINT FKBOO_Operat227172 FOREIGN KEY (BOOID) REFERENCES BOO (BOOID);
ALTER TABLE ProductionOrder ADD CONSTRAINT FKProduction804538 FOREIGN KEY (OID, ProductPartID) REFERENCES OrderProduct (OID, ProductPartID);
ALTER TABLE WSTypes_OperationTypes ADD CONSTRAINT FKWSTypes_Op693016 FOREIGN KEY (WTID) REFERENCES WorkstationTypes (WTID);
ALTER TABLE Address ADD CONSTRAINT FKAddress919413 FOREIGN KEY (CountryCode) REFERENCES Country (CountryCode);
ALTER TABLE Address ADD CONSTRAINT FKAddress947374 FOREIGN KEY (ClientNIF) REFERENCES Client (NIF);
ALTER TABLE Operation ADD CONSTRAINT FKOperation437580 FOREIGN KEY (OperationTypeOTID) REFERENCES OperationType (OTID);
ALTER TABLE WSTypes_OperationTypes ADD CONSTRAINT FKWSTypes_Op385854 FOREIGN KEY (OperationTypeOTID) REFERENCES OperationType (OTID);
ALTER TABLE ExternalPart ADD CONSTRAINT FKExternalPa66507 FOREIGN KEY (PartID) REFERENCES Part (PartID);
ALTER TABLE Component ADD CONSTRAINT FKComponent647854 FOREIGN KEY (PartID) REFERENCES ExternalPart (PartID);
ALTER TABLE RawMaterial ADD CONSTRAINT FKRawMateria486501 FOREIGN KEY (PartID) REFERENCES ExternalPart (PartID);
ALTER TABLE InternalPart ADD CONSTRAINT FKInternalPa593933 FOREIGN KEY (PartID) REFERENCES Part (PartID);
ALTER TABLE Product ADD CONSTRAINT FKProduct294354 FOREIGN KEY (PartID) REFERENCES InternalPart (PartID);
ALTER TABLE IntermediateProduct ADD CONSTRAINT FKIntermedia298766 FOREIGN KEY (PartID) REFERENCES InternalPart (PartID);
ALTER TABLE BOO ADD CONSTRAINT FKBOO179738 FOREIGN KEY (PartID) REFERENCES InternalPart (PartID);
ALTER TABLE PartOut ADD CONSTRAINT FKPartOut809562 FOREIGN KEY (PartID) REFERENCES InternalPart (PartID);
ALTER TABLE Offer ADD CONSTRAINT FKPlacedOrde158230 FOREIGN KEY (PartID) REFERENCES ExternalPart (PartID);
ALTER TABLE Offer ADD CONSTRAINT FKPlacedOrde171431 FOREIGN KEY (SupplierID) REFERENCES supplier (SupplierID);
ALTER TABLE RawMaterial ADD CONSTRAINT FKRawMateria187550 FOREIGN KEY (UnitID) REFERENCES Unit (UnitID);
ALTER TABLE PartOut ADD CONSTRAINT FKPartOut249189 FOREIGN KEY (OPID, BOOID) REFERENCES BOO_Operation (OPID, BOOID);
ALTER TABLE PartIN ADD CONSTRAINT FKPartIN518976 FOREIGN KEY (OPID, BOOID) REFERENCES BOO_Operation (OPID, BOOID);
ALTER TABLE PartIN ADD CONSTRAINT FKPartIN959241 FOREIGN KEY (PartID) REFERENCES Part (PartID);
ALTER TABLE BOO_Operation ADD CONSTRAINT FKBOO_Operat183133 FOREIGN KEY (NextOPID, NextBOOID) REFERENCES BOO_Operation (OPID, BOOID);

