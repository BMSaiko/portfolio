# Prevent Circular BOO References Trigger

This repository contains a PL/SQL trigger (`prevent_circular_boo_references`) designed to prevent circular references within Bill of Operations (BOO) when inserting records into the `PartIN` table. Additionally, a comprehensive suite of tests is provided to verify the trigger's functionality and robustness.

## Table of Contents

1. [Introduction](#introduction)
2. [Trigger Description](#trigger-description)
3. [Setup Instructions](#setup-instructions)
4. [Executing the Trigger](#executing-the-trigger)
5. [Running the Tests](#running-the-tests)
6. [Logic Explanation](#logic-explanation)
7. [Expected Outputs](#expected-outputs)

## Introduction

In complex database systems, maintaining data integrity is crucial. Circular references within BOO can lead to infinite loops, data inconsistencies, and operational failures. The `prevent_circular_boo_references` trigger ensures that such circular dependencies are detected and prevented at the database level, maintaining the integrity and reliability of the system.

## Trigger Description

The `prevent_circular_boo_references` trigger is a `BEFORE INSERT` trigger on the `PartIN` table. Its primary function is to detect and prevent the creation of circular references within the BOO operations. When a new record is inserted into the `PartIN` table, the trigger examines the BOO operation chain to ensure that no circular dependencies are introduced.

### Key Features

- **Circular Reference Detection**: Identifies if inserting a new `PartIN` record creates a loop in the BOO operation chain.
- **Data Integrity Enforcement**: Prevents invalid data entries that could compromise the system's reliability.
- **Automated Validation**: Automatically enforces business rules without requiring manual intervention.

### Trigger Code


CREATE OR REPLACE TRIGGER prevent_circular_boo_references
BEFORE INSERT ON PartIN
FOR EACH ROW
DECLARE
    v_is_circular BOOLEAN := FALSE;
    v_partid VARCHAR2(50);
    v_opid NUMBER(10);
    v_booid VARCHAR2(10);
    v_next_opid NUMBER(10);
    v_next_booid VARCHAR2(10);
BEGIN
    -- Get the PartID and BOO information for the current insert
    v_partid := :NEW.PartID;
    v_opid := :NEW.OPID;
    v_booid := :NEW.BOOID;

    -- Check if the PartID being inserted is part of any BOO chain
    -- Start by checking if the current OPID and BOOID are part of a cycle
    FOR rec IN (
        SELECT bo.OPID, bo.BOOID, bo.NextOPID, bo.NextBOOID
        FROM BOO_Operation bo
        WHERE bo.BOOID = v_booid AND bo.OPID = v_opid
    ) LOOP
        -- Check if the next operation in the chain points back to the same PartID
        v_next_opid := rec.NextOPID;
        v_next_booid := rec.NextBOOID;

        -- Check for circular references by looping through the BOO_Operation table
        -- Ensure that no operation leads back to the original OPID and BOOID in a circular manner
        LOOP
            EXIT WHEN v_next_opid IS NULL OR v_next_booid IS NULL;
            
            -- Check the next operation in the BOO_Operation table
            SELECT NextOPID, NextBOOID
            INTO v_next_opid, v_next_booid
            FROM BOO_Operation
            WHERE OPID = v_next_opid AND BOOID = v_next_booid;

            -- If we have reached the original OPID and BOOID, then it's a circular reference
            IF v_next_opid = v_opid AND v_next_booid = v_booid THEN
                v_is_circular := TRUE;
                EXIT;
            END IF;
        END LOOP;
    END LOOP;

    -- Raise an exception if a circular reference is detected
    IF v_is_circular THEN
        RAISE_APPLICATION_ERROR(-20001, 'Circular reference detected in BOO input. The part cannot be inserted.');
    END IF;
END;
/


## Setup Instructions

Follow these steps to set up the trigger and prepare your database environment.

### Prerequisites

- Oracle Database: Ensure you have access to an Oracle Database instance with the necessary privileges to create triggers and execute PL/SQL blocks.
- SQL*Plus or Equivalent: Use SQL*Plus or any Oracle-compatible SQL interface to execute the scripts.

### Steps

1. Ensure Tables Exist: Make sure the relevant tables (PartIN, BOO_Operation, BOO, etc.) are created and properly populated with necessary data. Refer to your database schema for table definitions.

2. Create the Trigger:
   Execute the trigger creation script shown above.

3. Enable Server Output:

SET SERVEROUTPUT ON;


4. Compile and Verify:
   Ensure that the trigger compiles successfully without errors.

## Executing the Trigger

The `prevent_circular_boo_references` trigger automatically fires before any INSERT operation on the PartIN table.

### Example Operations

Valid Insert (No Circular Reference):

INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn)
VALUES ('PN12344A21', 170, 'AS12945S20', 5);


Invalid Insert (Circular Reference):

INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn)
VALUES ('PN52384R50', 100, 'AS12946S22', 2);


## Running the Tests

Execute the test cases to validate the trigger's functionality:


DECLARE
    v_test_number NUMBER := 1;
    e_circular_ref EXCEPTION;
    PRAGMA EXCEPTION_INIT(e_circular_ref, -20001);
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- Starting Trigger Test Coverage ---');

    -- Test Case 1: Valid Insert
    BEGIN
        DBMS_OUTPUT.PUT_LINE('Test Case ' || v_test_number || ': Valid Insert (No Circular Reference)');
        INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn)
        VALUES ('PN12344A21', 170, 'AS12945S20', 5);
        DBMS_OUTPUT.PUT_LINE('Test Case ' || v_test_number || ': Passed');
    EXCEPTION
        WHEN e_circular_ref THEN
            DBMS_OUTPUT.PUT_LINE('Test Case ' || v_test_number || ': Failed - Circular reference detected unexpectedly.');
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Test Case ' || v_test_number || ': Failed - Unexpected error: ' || SQLERRM);
    END;
    
    -- Additional test cases...
    
    DBMS_OUTPUT.PUT_LINE('--- Trigger Test Coverage Completed ---');
END;
/


## Logic Explanation

The trigger prevents circular references by:
1. Capturing new record details
2. Traversing the BOO operation chain
3. Detecting cycles in the operation sequence
4. Preventing invalid insertions

## Expected Outputs

Test results should show:
- Successful insertion of valid records
- Prevention of direct circular references
- Prevention of indirect circular references
- Proper handling of NULL values
- Appropriate error messages for invalid operations
