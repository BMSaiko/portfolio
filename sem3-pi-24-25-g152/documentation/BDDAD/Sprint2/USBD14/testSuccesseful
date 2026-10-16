-- ****************************************************************************
-- Script SQL para demonstração da realização de uma funçao
-- 
-- Este script é responsável pela criação de um produto generico que usa todas as operações usadas numa fabrica
--
-- Passos incluídos neste script:
-- 1. Restaurar a base de dados, para nao ter problemas de unique contrains

-- 2. Inserção de dados de exemplo em cada uma das tabelas para o funcionamento do sistema.

-- 3. colocar a função.

-- 4. Execução da função e exibição dos resultados.
-- 
-- Observação: Este script deve ser executado numa Base de Dados de Desenvolvimento.
-- 
-- Desenvolvido por: vitor
-- ****************************************************************************

-- Passo 1: Restaurar a base de dados   
DROP TABLE Address CASCADE CONSTRAINTS;
DROP TABLE BOO CASCADE CONSTRAINTS;
DROP TABLE BOO_Operation CASCADE CONSTRAINTS;
DROP TABLE Client CASCADE CONSTRAINTS;
DROP TABLE ClientType CASCADE CONSTRAINTS;
DROP TABLE Component CASCADE CONSTRAINTS;
DROP TABLE Country CASCADE CONSTRAINTS;
DROP TABLE IntermediateProduct CASCADE CONSTRAINTS;
DROP TABLE Operation CASCADE CONSTRAINTS;
DROP TABLE OperationType CASCADE CONSTRAINTS;
DROP TABLE "Order" CASCADE CONSTRAINTS;
DROP TABLE OrderProduct CASCADE CONSTRAINTS;
DROP TABLE Part CASCADE CONSTRAINTS;
DROP TABLE PartIN CASCADE CONSTRAINTS;
DROP TABLE PartOut CASCADE CONSTRAINTS;
DROP TABLE Product CASCADE CONSTRAINTS;
DROP TABLE ProductFamily CASCADE CONSTRAINTS;
DROP TABLE ProductionOrder CASCADE CONSTRAINTS;
DROP TABLE RawMaterial CASCADE CONSTRAINTS;
DROP TABLE Workstations CASCADE CONSTRAINTS;
DROP TABLE WorkstationTypes CASCADE CONSTRAINTS;
DROP TABLE WSTypes_OperationTypes CASCADE CONSTRAINTS;

-- Passo 2: Criar as tabelas
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
  OPID  number(10) NOT NULL, 
  BOOID varchar2(10) NOT NULL, 
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
CREATE TABLE IntermediateProduct (
  PartID varchar2(50) NOT NULL, 
  PRIMARY KEY (PartID));
CREATE TABLE Operation (
  OPID              number(10) , 
  Name              varchar2(255) NOT NULL, 
  OperationTypeOTID number(10) NOT NULL, 
  NextOPID          number(10), 
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
  PRIMARY KEY (OID, 
  ProductPartID));
CREATE TABLE Part (
  PartID      varchar2(50) NOT NULL, 
  Description varchar2(255) NOT NULL, 
  PRIMARY KEY (PartID));
CREATE TABLE PartIN (
  PartID     varchar2(50) NOT NULL, 
  QuantityIn number(10) NOT NULL, 
  Unit       varchar2(50) NOT NULL, 
  OPID       number(10) NOT NULL, 
  PRIMARY KEY (PartID, 
  OPID));
CREATE TABLE PartOut (
  PartID   varchar2(50) NOT NULL, 
  Quantity number(10) NOT NULL, 
  Unit     varchar2(50) NOT NULL, 
  OPID     number(10) NOT NULL, 
  PRIMARY KEY (PartID, 
  OPID));
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
  PrOrID    varchar2(10) NOT NULL, 
  OID       number(10) NOT NULL, 
  ProductID varchar2(50) NOT NULL, 
  PRIMARY KEY (PrOrID));
CREATE TABLE RawMaterial (
  PartID varchar2(50) NOT NULL, 
  PRIMARY KEY (PartID));
CREATE TABLE Workstations (
  WSID        number(4) , 
  Name        varchar2(50) NOT NULL, 
  Description varchar2(255) NOT NULL, 
  WTID        varchar2(5) NOT NULL, 
  PRIMARY KEY (WSID));
CREATE TABLE WorkstationTypes (
  WTID varchar2(5) NOT NULL, 
  Name varchar2(255) NOT NULL, 
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
ALTER TABLE Product ADD CONSTRAINT FKProduct541091 FOREIGN KEY (PartID) REFERENCES Part (PartID);
ALTER TABLE BOO ADD CONSTRAINT FKBOO426475 FOREIGN KEY (PartID) REFERENCES Part (PartID);
ALTER TABLE BOO_Operation ADD CONSTRAINT FKBOO_Operat961240 FOREIGN KEY (OPID) REFERENCES Operation (OPID);
ALTER TABLE BOO_Operation ADD CONSTRAINT FKBOO_Operat227172 FOREIGN KEY (BOOID) REFERENCES BOO (BOOID);
ALTER TABLE ProductionOrder ADD CONSTRAINT FKProduction955882 FOREIGN KEY (OID, ProductID) REFERENCES OrderProduct (OID, ProductPartID);
ALTER TABLE WSTypes_OperationTypes ADD CONSTRAINT FKWSTypes_Op693016 FOREIGN KEY (WTID) REFERENCES WorkstationTypes (WTID);
ALTER TABLE Address ADD CONSTRAINT FKAddress919413 FOREIGN KEY (CountryCode) REFERENCES Country (CountryCode);
ALTER TABLE Address ADD CONSTRAINT FKAddress947374 FOREIGN KEY (ClientNIF) REFERENCES Client (NIF);
ALTER TABLE PartIN ADD CONSTRAINT FKPartIN185078 FOREIGN KEY (PartID) REFERENCES Part (PartID);
ALTER TABLE RawMaterial ADD CONSTRAINT FKRawMateria259253 FOREIGN KEY (PartID) REFERENCES Part (PartID);
ALTER TABLE Component ADD CONSTRAINT FKComponent422018 FOREIGN KEY (PartID) REFERENCES Part (PartID);
ALTER TABLE IntermediateProduct ADD CONSTRAINT FKIntermedia545503 FOREIGN KEY (PartID) REFERENCES Part (PartID);
ALTER TABLE Operation ADD CONSTRAINT FKOperation437580 FOREIGN KEY (OperationTypeOTID) REFERENCES OperationType (OTID);
ALTER TABLE WSTypes_OperationTypes ADD CONSTRAINT FKWSTypes_Op385854 FOREIGN KEY (OperationTypeOTID) REFERENCES OperationType (OTID);
ALTER TABLE PartOut ADD CONSTRAINT FKPartOut56300 FOREIGN KEY (PartID) REFERENCES Part (PartID);
ALTER TABLE PartOut ADD CONSTRAINT FKPartOut264178 FOREIGN KEY (OPID) REFERENCES Operation (OPID);
ALTER TABLE PartIN ADD CONSTRAINT FKPartIN994390 FOREIGN KEY (OPID) REFERENCES Operation (OPID);
ALTER TABLE Operation ADD CONSTRAINT FKOperation710659 FOREIGN KEY (NextOPID) REFERENCES Operation (OPID);


-- Passo 3: Inserir dados genéricos de um produto nao existente
BEGIN
        
    -- Criando o produto Generico
    INSERT INTO ProductFamily (PFID, Name) VALUES (130, 'Family 130');
    
    INSERT INTO WorkstationTypes (WTID, Name) VALUES ('A4578', 'Workstation A4578');
    INSERT INTO WorkstationTypes (WTID, Name) VALUES ('A4588', 'Workstation A4588');
    INSERT INTO WorkstationTypes (WTID, Name) VALUES ('A4598', 'Workstation A4598');
    INSERT INTO WorkstationTypes (WTID, Name) VALUES ('C5637', 'Workstation C5637');
    INSERT INTO WorkstationTypes (WTID, Name) VALUES ('S3271', 'Workstation S3271');
    INSERT INTO WorkstationTypes (WTID, Name) VALUES ('T3452', 'Workstation T3452');
    INSERT INTO WorkstationTypes (WTID, Name) VALUES ('K3675', 'Workstation K3675');
    INSERT INTO WorkstationTypes (WTID, Name) VALUES ('D9123', 'Workstation D9123');
    INSERT INTO WorkstationTypes (WTID, Name) VALUES ('Q3547', 'Workstation Q3547');
    INSERT INTO WorkstationTypes (WTID, Name) VALUES ('Q5478', 'Workstation Q5478');
    
    -- Inserir OperationType
    INSERT INTO OperationType (OTID, Description) VALUES (5647, 'Disc cutting');
    INSERT INTO OperationType (OTID, Description) VALUES (5649, 'Initial pot base pressing');
    INSERT INTO OperationType (OTID, Description) VALUES (5651, 'Final pot base pressing');
    INSERT INTO OperationType (OTID, Description) VALUES (5653, 'Pot base finishing');
    INSERT INTO OperationType (OTID, Description) VALUES (5655, 'Lid pressing');
    INSERT INTO OperationType (OTID, Description) VALUES (5657, 'Lid finishing');
    INSERT INTO OperationType (OTID, Description) VALUES (5659, 'Pot handles riveting');
    INSERT INTO OperationType (OTID, Description) VALUES (5661, 'Lid handle screw');
    INSERT INTO OperationType (OTID, Description) VALUES (5663, 'Pot test and packaging');
    INSERT INTO OperationType (OTID, Description) VALUES (5665, 'Handle welding');
    INSERT INTO OperationType (OTID, Description) VALUES (5667, 'Lid polishing');
    INSERT INTO OperationType (OTID, Description) VALUES (5669, 'Pot base polishing');
    INSERT INTO OperationType (OTID, Description) VALUES (5671, 'Teflon painting');
    INSERT INTO OperationType (OTID, Description) VALUES (5681, 'Initial pan base pressing');
    INSERT INTO OperationType (OTID, Description) VALUES (5682, 'Final pan base pressing');
    INSERT INTO OperationType (OTID, Description) VALUES (5683, 'Pan base finishing');
    INSERT INTO OperationType (OTID, Description) VALUES (5685, 'Handle gluing');
    INSERT INTO OperationType (OTID, Description) VALUES (5688, 'Pan test and packaging');
    
    
    INSERT INTO Part (PartID, Description) 
    VALUES ('P12345', 'Produto para testar todos os tipos de estações');
    
    INSERT INTO Product (PartID, Name, PFID) 
    VALUES ('P12345', 'Produto Teste', 130);
    
    
    -- Criando o BOO
    INSERT INTO BOO (BOOID, PartID) 
    VALUES ('BOO123', 'P12345');
    
    -- Criando as operações
    INSERT INTO Operation (OPID, Name, OperationTypeOTID) VALUES (991, 'Operação A4578', 5647);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID) VALUES (992, 'Operação A4588', 5649);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID) VALUES (993, 'Operação A4598', 5647);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID) VALUES (994, 'Operação C5637', 5653);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID) VALUES (995, 'Operação S3271', 5659);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID) VALUES (996, 'Operação T3452', 5661);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID) VALUES (997, 'Operação K3675', 5663);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID) VALUES (998, 'Operação D9123', 5665);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID) VALUES (999, 'Operação Q3547', 5667);
    INSERT INTO Operation (OPID, Name, OperationTypeOTID) VALUES (9910, 'Operação Q5478', 5671);
    
    
    -- Associando as operações ao BOO
    INSERT INTO BOO_Operation (BOOID, OPID) VALUES ('BOO123', 991);
    INSERT INTO BOO_Operation (BOOID, OPID) VALUES ('BOO123', 992);
    INSERT INTO BOO_Operation (BOOID, OPID) VALUES ('BOO123', 993);
    INSERT INTO BOO_Operation (BOOID, OPID) VALUES ('BOO123', 994);
    INSERT INTO BOO_Operation (BOOID, OPID) VALUES ('BOO123', 995);
    INSERT INTO BOO_Operation (BOOID, OPID) VALUES ('BOO123', 996);
    INSERT INTO BOO_Operation (BOOID, OPID) VALUES ('BOO123', 997);
    INSERT INTO BOO_Operation (BOOID, OPID) VALUES ('BOO123', 998);
    INSERT INTO BOO_Operation (BOOID, OPID) VALUES ('BOO123', 999);
    INSERT INTO BOO_Operation (BOOID, OPID) VALUES ('BOO123', 9910);
    
    -- Associando os tipos de operação às estações de trabalho
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('A4578', 5647);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('A4588', 5649);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('A4598', 5647);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('C5637', 5653);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('S3271', 5659);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('T3452', 5661);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('K3675', 5663);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('D9123', 5665);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('Q3547', 5667);
    INSERT INTO WSTypes_OperationTypes (WTID, OperationTypeOTID) VALUES ('Q5478', 5671);

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

-- Passo 4: Criar a função
CREATE OR REPLACE FUNCTION get_products_using_all_workstation_types
RETURN SYS_REFCURSOR
AS
    result_cursor SYS_REFCURSOR;
BEGIN
    OPEN result_cursor FOR
    SELECT Product.PartID
    FROM Product
    WHERE NOT EXISTS (
        SELECT WTID
        FROM WorkstationTypes
        MINUS
    
        SELECT DISTINCT WorkstationTypes.WTID
        FROM BOO
        JOIN BOO_Operation ON BOO.BOOID = BOO_Operation.BOOID
        JOIN Operation ON BOO_Operation.OPID = Operation.OPID
    	JOIN OperationType ON Operation.OperationTypeOTID = OperationType.OTID
    	JOIN WSTypes_OperationTypes ON OperationType.OTID = WSTypes_OperationTypes.OperationTypeOTID
        JOIN WorkstationTypes ON WSTypes_OperationTypes.WTID = WorkstationTypes.WTID
        WHERE BOO.PartID = Product.PartID
    );

    RETURN result_cursor;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        DBMS_OUTPUT.PUT_LINE('Nenhum produto encontrado que utilize todos os tipos de estações de trabalho.');
        RETURN NULL;
END;
/


-- Passo 5: Chamar a função e obter os resultados 
DECLARE
    products_cursor SYS_REFCURSOR;
    product_id Product.PartID%TYPE;
    is_empty BOOLEAN := TRUE;
    
BEGIN
    -- Chama a função
    products_cursor := get_products_using_all_workstation_types;

    -- Tenta buscar os resultados do cursor
    LOOP
        FETCH products_cursor INTO product_id;
        EXIT WHEN products_cursor%NOTFOUND;

        -- Se entrar aqui, significa que há pelo menos um registro
        is_empty := FALSE;
        DBMS_OUTPUT.PUT_LINE('Product PartID: ' || product_id);
    END LOOP;

    -- Verifica se o cursor estava vazio
    IF is_empty THEN
        DBMS_OUTPUT.PUT_LINE('Nenhum produto encontrado que utilize todos os tipos de estações de trabalho.');
    END IF;

    -- Fecha o cursor
    CLOSE products_cursor;
END;
/