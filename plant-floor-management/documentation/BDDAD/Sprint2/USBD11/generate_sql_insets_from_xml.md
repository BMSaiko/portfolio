# SQL Insert Generator from XML

## Description

This Python script processes three XML files to generate SQL `INSERT` statements intended to populate an updated Oracle database. It extracts data related to parts, products, operations, and their relationships, ensuring data integrity by organizing the inserts within a transaction block. The script accommodates new changes in the XML structure and adapts the insert statements to align with the updated database schema.

## Features

- **Parse Parts and Products**: Reads parts and products information from `Dataset_S2_parts_V03a.xml`, handling multiple part types including `Product`, `Component`, `Intermediate Product`, and `Raw Material`.
- **Parse Operation Types**: Extracts operation types and associated workstation types from `Dataset_S2_operations_V01.xml`.
- **Parse BOO (Bill of Operations)**: Processes operations and their inputs from `Dataset_S2_boo_V03.xml`.
- **Handle Product Families**: Utilizes the `family` field from the XML to map and insert data into the `ProductFamily` table, avoiding duplicate entries.
- **Generate SQL Script**: Compiles all `INSERT` statements into `inserts.sql` within a PL/SQL transaction block to maintain data integrity.
- **Prevent Duplications**: Ensures that entries in tables like `ProductFamily`, `Component`, `IntermediateProduct`, and `RawMaterial` are inserted only once.
- **Adapt to Database Schema Changes**: Adjusts insert statements to match the updated database schema, including new foreign key constraints and table structures.

## Requirements

- Python 3.6 or higher

## Installation

### Clone the Repository


git clone https://github.com/your-username/your-repo.git
cd your-repo


### Ensure Python is Installed

Verify that Python 3.6 or higher is installed:


python --version


If Python is not installed, download and install it from the official website.

## Usage

### Prepare XML Files

Ensure the following XML files are present in the script directory:

- Dataset_S2_parts_V03a.xml
- Dataset_S2_operations_V01.xml
- Dataset_S2_boo_V03.xml

These files should conform to the expected structure as illustrated in the provided example.

### Run the Script

Execute the script using Python:


python your_script.py


Replace your_script.py with the actual filename of the script.

## Output

After execution, an inserts.sql file will be generated containing all SQL INSERT statements organized within a PL/SQL transaction block. This script can be executed in your Oracle database environment to populate the necessary tables while ensuring data integrity.

### Sample Output (inserts.sql)


BEGIN
    -- Transaction Start

    INSERT INTO Part (PartID, Description) VALUES ('ROOT', 'Root Part');
    INSERT INTO Part (PartID, Description) VALUES ('PN12344A21', 'Screw M6 35 mm');
    INSERT INTO Part (PartID, Description) VALUES ('PN52384R50', '300x300 mm 5 mm stainless steel sheet');
    -- ... more INSERT statements ...

    INSERT INTO ProductFamily (PFID, Name) VALUES (130, 'Family 130');
    INSERT INTO Product (Name, PFID, PartID) VALUES ('La Belle 22 5l pot', 130, 'AS12945T22');
    INSERT INTO WorkstationTypes (WTID, Name) VALUES ('WT01', 'Workstation WT01');
    INSERT INTO Operation (OPID, Name) VALUES (1001, 'Operation 1');
    INSERT INTO OperationWorkstationTypes (WTID, OPID) VALUES ('WT01', 1001);
    INSERT INTO BOO (BOOID, PartID) VALUES ('BO01', 'PN12344A21');
    INSERT INTO BOO_Operation (OPID, BOOID) VALUES (1001, 'BO01');
    INSERT INTO PartIN (PartID, OPID, BOOID, QuantityIn) VALUES ('PN52384R50', 1001, 'BO01', 5);

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/
-- Transaction End


## Script Structure

### parse_part_list(xml_file)

**Purpose**: Parses the parts XML file to extract and generate INSERT statements for the Part, ProductFamily, Product, Component, IntermediateProduct, and RawMaterial tables.

**Key Operations**:
- Inserts a root part entry.
- Handles different part types and ensures no duplicate entries in related tables.
- Maps the family field to ProductFamily.

### parse_operation_type_list(xml_file)

**Purpose**: Parses the operation types XML file to extract and generate INSERT statements for the Operation, WorkstationTypes, and OperationWorkstationTypes tables.

**Key Operations**:
- Inserts operations and associated workstation types.
- Prevents duplicate workstation type entries.

### parse_boo_list(xml_file, boo_id_map)

**Purpose**: Parses the BOO XML file to extract and generate INSERT statements for the BOO, BOO_Operation, and PartIN tables.

**Key Operations**:
- Assigns unique BOOID values.
- Links operations to BOO entries and handles part inputs.

### write_inserts_to_sql(inserts, output_file)

**Purpose**: Writes all collected INSERT statements into an SQL file within a PL/SQL transaction block.

**Key Operations**:
- Organizes inserts in an order that respects foreign key constraints.
- Ensures atomicity with COMMIT and handles exceptions with ROLLBACK.

### main()

**Purpose**: Orchestrates the parsing of XML files and the generation of the SQL script.

**Key Operations**:
- Defines input XML files and output SQL file.
- Aggregates all INSERT statements from different parsing functions.
- Executes the writing of the SQL script.

## Database Schema

The script is designed to work with the following Oracle database schema. Ensure that all tables and constraints are properly set up before executing the generated inserts.sql script.

### Tables

- Address
- BOO
- BOO_Operation
- Client
- ClientType
- Component
- Country
- IntermediateProduct
- Operation
- OperationWorkstationTypes
- Order
- OrderProduct
- Part
- PartIN
- Product
- ProductFamily
- ProductionOrder
- RawMaterial
- Workstations
- WorkstationTypes

### Constraints

The script respects all primary and foreign key constraints defined in the database schema. It ensures that data is inserted in the correct order to maintain referential integrity.

## Author

**Name**: Bruno Silva  
**Contact**: 1221514@isep.ipp.pt

## Support

If you encounter any issues or have questions, please contact 1221514@isep.ipp.pt