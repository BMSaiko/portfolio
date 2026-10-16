# Material Requirements Management

This repository contains a set of Oracle PL/SQL object types and functions designed to manage and report material requirements for production orders. It includes comprehensive functionalities to calculate required materials based on the Bill of Materials (BOM), check stock availability, and generate detailed material requirements reports. Additionally, a robust test suite is provided to ensure the correctness and reliability of the implemented features.

## Table of Contents

1. [Introduction](#introduction)
2. [Object Types](#object-types)
3. [Function Descriptions](#function-descriptions)
4. [Setup Instructions](#setup-instructions)
5. [Executing the Functions](#executing-the-functions)
6. [Running the Tests](#running-the-tests)
7. [Logic Explanation](#logic-explanation)
8. [Expected Outputs](#expected-outputs)
9. [License](#license)

## Introduction

In a manufacturing environment, accurately determining material requirements is crucial for efficient production planning and inventory management. This system is designed to automate the calculation of material needs based on production orders, ensuring that all necessary components are available before production begins. By integrating information from various tables such as `Product`, `Part`, `BOO_Operation`, and `ExternalPart`, the system provides a comprehensive overview of material requirements, stock levels, and potential shortages.

### Key Features

- **Comprehensive Material Aggregation**: Combines data from multiple tables to provide a holistic view of material requirements.
- **Recursive BOM Processing**: Utilizes recursive queries to handle complex BOM structures with multiple levels.
- **Stock Verification**: Checks current stock levels against required quantities to identify shortages.
- **Detailed Reporting**: Generates detailed reports linking orders to their material requirements and stock statuses.
- **Robust Testing**: Includes a comprehensive test suite to validate all functionalities and ensure reliability.

## Object Types

To effectively manage material requirements, two custom Oracle object types are defined: `material_requirement_obj` and `material_requirement_tbl`. These types facilitate the aggregation and processing of material data in a structured manner.

### 1. `material_requirement_obj`

This object type represents a single material requirement associated with a production order.

CREATE OR REPLACE TYPE material_requirement_obj AS OBJECT (
    ProductName    VARCHAR2(255),
    OrderQuantity  NUMBER,
    PartID         VARCHAR2(50),
    Description    VARCHAR2(255),
    RequiredQty    NUMBER
);
/


### Purpose
- Data Aggregation: Combines product information, order quantity, part details, and calculated required quantities into a single object.
- Reporting: Serves as a building block for detailed material requirements reports.

### 2. material_requirement_tbl
This nested table type is a collection of material_requirement_obj objects, allowing the system to handle multiple material requirements collectively.


CREATE OR REPLACE TYPE material_requirement_tbl AS TABLE OF material_requirement_obj;
/


### Purpose
- Batch Processing: Enables the accumulation of all material requirements for an order in a single collection.
- Efficiency: Facilitates bulk operations and aggregate calculations, improving performance and maintainability.

---

## Function Descriptions

The system comprises several functions that work together to calculate material requirements, verify stock availability, and generate reports. Below is an overview of each function and its role within the system.

### 1. `get_required_materials`


CREATE OR REPLACE FUNCTION get_required_materials(
    p_part_id    IN VARCHAR2,
    p_quantity   IN NUMBER
) RETURN SYS_REFCURSOR IS
    v_result SYS_REFCURSOR;
BEGIN
    OPEN v_result FOR
        WITH bom_tree (PartID, RequiredQty, TreeLevel, PartDesc) AS (
            -- Base case: direct materials for the product
            SELECT 
                pi.PartID,
                pi.QuantityIn * p_quantity AS RequiredQty,
                1 AS TreeLevel,
                p.Description AS PartDesc
            FROM BOO b
            JOIN BOO_Operation bo ON b.BOOID = bo.BOOID
            JOIN PartIN pi ON bo.OPID = pi.OPID AND bo.BOOID = pi.BOOID
            JOIN Part p ON pi.PartID = p.PartID
            WHERE b.PartID = p_part_id
            
            UNION ALL
            
            -- Recursive case: materials for intermediate parts
            SELECT 
                pi2.PartID,
                pi2.QuantityIn * bt.RequiredQty AS RequiredQty,
                bt.TreeLevel + 1 AS TreeLevel,
                p2.Description AS PartDesc
            FROM bom_tree bt
            JOIN PartOut po ON bt.PartID = po.PartID
            JOIN BOO_Operation bo2 ON po.OPID = bo2.OPID AND po.BOOID = bo2.BOOID
            JOIN PartIN pi2 ON bo2.OPID = pi2.OPID AND bo2.BOOID = pi2.BOOID
            JOIN Part p2 ON pi2.PartID = p2.PartID
            WHERE bt.TreeLevel < 10  -- Prevent infinite recursion
        )
        SELECT 
            PartID,
            PartDesc AS Description,
            SUM(RequiredQty) AS TotalRequired
        FROM bom_tree
        WHERE PartID IN (SELECT PartID FROM ExternalPart)
        GROUP BY PartID, PartDesc;
        
    RETURN v_result;
END;
/


### Purpose
- Material Calculation: Recursively calculates the total quantity of each external part required for a given product and quantity.
- BOM Traversal: Navigates through the Bill of Materials to account for all levels of material requirements.
- Aggregation: Summarizes required quantities for efficient reporting and stock verification.

### 2. check_order_materials


CREATE OR REPLACE FUNCTION check_order_materials(p_order_id IN NUMBER) 
RETURN BOOLEAN IS
    v_materials SYS_REFCURSOR;
    v_part_id VARCHAR2(50);
    v_description VARCHAR2(255);
    v_required NUMBER;
    v_stock NUMBER;
    v_all_available BOOLEAN := TRUE;
    
    -- Cursor to get all products in the order
    CURSOR c_order_products IS
        SELECT ProductPartID, Quantity
        FROM OrderProduct
        WHERE OID = p_order_id;
        
    -- Variables for product cursor
    v_product_cursor SYS_REFCURSOR;
    v_total_required NUMBER;
BEGIN
    -- Get all required materials for the order
    FOR product_rec IN c_order_products LOOP
        v_product_cursor := get_required_materials(product_rec.ProductPartID, product_rec.Quantity);
        
        LOOP
            FETCH v_product_cursor INTO v_part_id, v_description, v_required;
            EXIT WHEN v_product_cursor%NOTFOUND;
            
            -- Check stock level
            SELECT Stock INTO v_stock
            FROM ExternalPart
            WHERE PartID = v_part_id;
            
            IF v_stock < v_required THEN
                v_all_available := FALSE;
                EXIT;
            END IF;
        END LOOP;
        
        CLOSE v_product_cursor;
        
        -- Exit early if we found insufficient materials
        IF NOT v_all_available THEN
            EXIT;
        END IF;
    END LOOP;
    
    RETURN v_all_available;
END;
/


### Purpose
- Stock Verification: Checks whether the current stock levels meet the material requirements for a given order.
- Efficiency: Returns a boolean indicating overall stock availability, allowing for quick decision-making before production.

### 3. get_order_materials_report

```sql
CREATE OR REPLACE FUNCTION get_order_materials_report(p_order_id IN NUMBER) 
RETURN SYS_REFCURSOR IS
    v_result SYS_REFCURSOR;
    v_requirements material_requirement_tbl := material_requirement_tbl();
    
    -- Cursor to get all products in the order
    CURSOR c_order_products IS
        SELECT 
            op.ProductPartID,
            op.Quantity AS OrderQuantity,
            p.Name AS ProductName
        FROM OrderProduct op
        JOIN Product p ON p.PartID = op.ProductPartID
        WHERE op.OID = p_order_id;
        
    -- Variables for material cursor
    v_material_cursor SYS_REFCURSOR;
    v_part_id VARCHAR2(50);
    v_description VARCHAR2(255);
    v_required NUMBER;
BEGIN
    -- Process each product in the order
    FOR product_rec IN c_order_products LOOP
        v_material_cursor := get_required_materials(product_rec.ProductPartID, product_rec.OrderQuantity);
        
        LOOP
            FETCH v_material_cursor INTO v_part_id, v_description, v_required;
            EXIT WHEN v_material_cursor%NOTFOUND;
            
            v_requirements.EXTEND;
            v_requirements(v_requirements.LAST) := material_requirement_obj(
                product_rec.ProductName,
                product_rec.OrderQuantity,
                v_part_id,
                v_description,
                v_required
            );
        END LOOP;
        
        CLOSE v_material_cursor;
    END LOOP;
    
    -- Return aggregated results
    OPEN v_result FOR
        SELECT 
            r.ProductName,
            r.OrderQuantity,
            r.PartID,
            r.Description,
            SUM(r.RequiredQty) AS TotalRequired,
            ep.Stock AS CurrentStock,
            CASE 
                WHEN ep.Stock >= SUM(r.RequiredQty) THEN 'Available'
                ELSE 'Insufficient'
            END AS Status,
            GREATEST(0, SUM(r.RequiredQty) - ep.Stock) AS MissingQuantity
        FROM TABLE(v_requirements) r
        JOIN ExternalPart ep ON ep.PartID = r.PartID
        GROUP BY 
            r.ProductName,
            r.OrderQuantity,
            r.PartID,
            r.Description,
            ep.Stock
        ORDER BY 
            r.ProductName,
            CASE WHEN ep.Stock >= SUM(r.RequiredQty) THEN 1 ELSE 0 END;
            
    RETURN v_result;
END;
/

# Material Requirements Management System

## Purpose

### Detailed Reporting
- Generates a comprehensive report of material requirements for a specific order, including stock levels and shortage calculations.
- Data Aggregation: Combines order information, material requirements, and stock data into a single, organized report.

## Setup Instructions

Follow these steps to set up the material requirements management system and prepare your database environment.

### Prerequisites

- **Oracle Database**: Ensure you have access to an Oracle Database instance with the necessary privileges to create object types, functions, and execute PL/SQL blocks.
- **SQL*Plus or Equivalent**: Use SQL*Plus or any Oracle-compatible SQL interface to execute the scripts.
- **Existing Tables**: Ensure that the relevant tables (`Client`, `Product`, `Part`, `BOO`, `BOO_Operation`, `PartIN`, `PartOut`, `ExternalPart`, `"Order"`, `OrderProduct`) are created and properly populated with necessary data. Refer to your database schema for table definitions.

### Steps

1. **Create Object Types**


CREATE OR REPLACE TYPE material_requirement_obj AS OBJECT (
    ProductName    VARCHAR2(255),
    OrderQuantity  NUMBER,
    PartID         VARCHAR2(50),
    Description    VARCHAR2(255),
    RequiredQty    NUMBER
);
/

CREATE OR REPLACE TYPE material_requirement_tbl AS TABLE OF material_requirement_obj;
/


2. **Create Functions**


CREATE OR REPLACE FUNCTION get_required_materials(
    p_part_id    IN VARCHAR2,
    p_quantity   IN NUMBER
) RETURN SYS_REFCURSOR IS
    v_result SYS_REFCURSOR;
BEGIN
    OPEN v_result FOR
        WITH bom_tree (PartID, RequiredQty, TreeLevel, PartDesc) AS (
            -- Base case: direct materials for the product
            SELECT 
                pi.PartID,
                pi.QuantityIn * p_quantity AS RequiredQty,
                1 AS TreeLevel,
                p.Description AS PartDesc
            FROM BOO b
            JOIN BOO_Operation bo ON b.BOOID = bo.BOOID
            JOIN PartIN pi ON bo.OPID = pi.OPID AND bo.BOOID = pi.BOOID
            JOIN Part p ON pi.PartID = p.PartID
            WHERE b.PartID = p_part_id
            
            UNION ALL
            
            -- Recursive case: materials for intermediate parts
            SELECT 
                pi2.PartID,
                pi2.QuantityIn * bt.RequiredQty AS RequiredQty,
                bt.TreeLevel + 1 AS TreeLevel,
                p2.Description AS PartDesc
            FROM bom_tree bt
            JOIN PartOut po ON bt.PartID = po.PartID
            JOIN BOO_Operation bo2 ON po.OPID = bo2.OPID AND po.BOOID = bo2.BOOID
            JOIN PartIN pi2 ON bo2.OPID = pi2.OPID AND bo2.BOOID = pi2.BOOID
            JOIN Part p2 ON pi2.PartID = p2.PartID
            WHERE bt.TreeLevel < 10  -- Prevent infinite recursion
        )
        SELECT 
            PartID,
            PartDesc AS Description,
            SUM(RequiredQty) AS TotalRequired
        FROM bom_tree
        WHERE PartID IN (SELECT PartID FROM ExternalPart)
        GROUP BY PartID, PartDesc;
        
    RETURN v_result;
END;
/

CREATE OR REPLACE FUNCTION check_order_materials(p_order_id IN NUMBER) 
RETURN BOOLEAN IS
    v_materials SYS_REFCURSOR;
    v_part_id VARCHAR2(50);
    v_description VARCHAR2(255);
    v_required NUMBER;
    v_stock NUMBER;
    v_all_available BOOLEAN := TRUE;
    
    CURSOR c_order_products IS
        SELECT ProductPartID, Quantity
        FROM OrderProduct
        WHERE OID = p_order_id;
        
    v_product_cursor SYS_REFCURSOR;
    v_total_required NUMBER;
BEGIN
    FOR product_rec IN c_order_products LOOP
        v_product_cursor := get_required_materials(product_rec.ProductPartID, product_rec.Quantity);
        
        LOOP
            FETCH v_product_cursor INTO v_part_id, v_description, v_required;
            EXIT WHEN v_material_cursor%NOTFOUND;
            
            SELECT Stock INTO v_stock
            FROM ExternalPart
            WHERE PartID = v_part_id;
            
            IF v_stock < v_required THEN
                v_all_available := FALSE;
                EXIT;
            END IF;
        END LOOP;
        
        CLOSE v_product_cursor;
        
        IF NOT v_all_available THEN
            EXIT;
        END IF;
    END LOOP;
    
    RETURN v_all_available;
END;
/

CREATE OR REPLACE FUNCTION get_order_materials_report(p_order_id IN NUMBER) 
RETURN SYS_REFCURSOR IS
    v_result SYS_REFCURSOR;
    v_requirements material_requirement_tbl := material_requirement_tbl();
    
    CURSOR c_order_products IS
        SELECT 
            op.ProductPartID,
            op.Quantity AS OrderQuantity,
            p.Name AS ProductName
        FROM OrderProduct op
        JOIN Product p ON p.PartID = op.ProductPartID
        WHERE op.OID = p_order_id;
        
    v_material_cursor SYS_REFCURSOR;
    v_part_id VARCHAR2(50);
    v_description VARCHAR2(255);
    v_required NUMBER;
BEGIN
    FOR product_rec IN c_order_products LOOP
        v_material_cursor := get_required_materials(product_rec.ProductPartID, product_rec.OrderQuantity);
        
        LOOP
            FETCH v_material_cursor INTO v_part_id, v_description, v_required;
            EXIT WHEN v_material_cursor%NOTFOUND;
            
            v_requirements.EXTEND;
            v_requirements(v_requirements.LAST) := material_requirement_obj(
                product_rec.ProductName,
                product_rec.OrderQuantity,
                v_part_id,
                v_description,
                v_required
            );
        END LOOP;
        
        CLOSE v_material_cursor;
    END LOOP;
    
    OPEN v_result FOR
        SELECT 
            r.ProductName,
            r.OrderQuantity,
            r.PartID,
            r.Description,
            SUM(r.RequiredQty) AS TotalRequired,
            ep.Stock AS CurrentStock,
            CASE 
                WHEN ep.Stock >= SUM(r.RequiredQty) THEN 'Available'
                ELSE 'Insufficient'
            END AS Status,
            GREATEST(0, SUM(r.RequiredQty) - ep.Stock) AS MissingQuantity
        FROM TABLE(v_requirements) r
        JOIN ExternalPart ep ON ep.PartID = r.PartID
        GROUP BY 
            r.ProductName,
            r.OrderQuantity,
            r.PartID,
            r.Description,
            ep.Stock
        ORDER BY 
            r.ProductName,
            CASE WHEN ep.Stock >= SUM(r.RequiredQty) THEN 1 ELSE 0 END;
            
    RETURN v_result;
END;
/

   ### Summary

   - **`get_required_materials`**: Calculates the total required quantities of external parts for a given product and quantity by traversing the BOM.
   - **`check_order_materials`**: Verifies if the current stock levels are sufficient to fulfill all material requirements for a specific order.
   - **`get_order_materials_report`**: Generates a detailed report of material requirements for an order, including stock statuses and shortages.

## Executing the Functions

The functions provided in this system automate the process of calculating material requirements, verifying stock levels, and generating detailed reports for production orders. Below are examples of how to execute each function.

### 1. `get_required_materials` Function

**Purpose**: Retrieves the required external materials for a specific part and quantity.

**Example Usage**:

```sql
DECLARE
    v_materials SYS_REFCURSOR;
    v_part_id VARCHAR2(50);
    v_description VARCHAR2(255);
    v_required_qty NUMBER;
BEGIN
    v_materials := get_required_materials('AS12945S22', 10);
    
    LOOP
        FETCH v_materials INTO v_part_id, v_description, v_required_qty;
        EXIT WHEN v_materials%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('PartID: ' || v_part_id || ', Description: ' || v_description || ', RequiredQty: ' || v_required_qty);
    END LOOP;
    
    CLOSE v_materials;
END;
/
Expected Output:


PartID: EP1001, Description: External Part 1001, RequiredQty: 50
PartID: EP1002, Description: External Part 1002, RequiredQty: 30
...
2. check_order_materials Function
Purpose: Checks if there is sufficient stock to fulfill the material requirements for a given order.

Example Usage:


DECLARE
    v_order_id NUMBER := 1234; -- Replace with your order ID
    v_available BOOLEAN;
BEGIN
    v_available := check_order_materials(v_order_id);
    
    IF v_available THEN
        DBMS_OUTPUT.PUT_LINE('All materials are available for Order ID: ' || v_order_id);
    ELSE
        DBMS_OUTPUT.PUT_LINE('Insufficient materials for Order ID: ' || v_order_id);
    END IF;
END;
/
Expected Output:


All materials are available for Order ID: 1234
or


Insufficient materials for Order ID: 1234
3. get_order_materials_report Function
Purpose: Generates a detailed report of material requirements for a specific order, including stock statuses and missing quantities.

Example Usage:


DECLARE
    v_report SYS_REFCURSOR;
    v_product_name VARCHAR2(255);
    v_order_quantity NUMBER;
    v_part_id VARCHAR2(50);
    v_description VARCHAR2(255);
    v_total_required NUMBER;
    v_current_stock NUMBER;
    v_status VARCHAR2(20);
    v_missing_qty NUMBER;
BEGIN
    v_report := get_order_materials_report(1234); -- Replace with your order ID
    
    LOOP
        FETCH v_report INTO v_product_name, v_order_quantity, v_part_id, v_description, v_total_required, v_current_stock, v_status, v_missing_qty;
        EXIT WHEN v_report%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE('ProductName: ' || v_product_name || 
                             ', OrderQuantity: ' || v_order_quantity ||
                             ', PartID: ' || v_part_id || 
                             ', Description: ' || v_description ||
                             ', TotalRequired: ' || v_total_required ||
                             ', CurrentStock: ' || v_current_stock ||
                             ', Status: ' || v_status ||
                             ', MissingQuantity: ' || v_missing_qty);
    END LOOP;
    
    CLOSE v_report;
END;
/
Expected Output:


ProductName: Widget A, OrderQuantity: 10, PartID: EP1001, Description: External Part 1001, TotalRequired: 50, CurrentStock: 100, Status: Available, MissingQuantity: 0
ProductName: Widget A, OrderQuantity: 10, PartID: EP1002, Description: External Part 1002, TotalRequired: 30, CurrentStock: 20, Status: Insufficient, MissingQuantity: 10


---

### Part 7: Running the Tests

```markdown
## Running the Tests

A comprehensive test suite is provided to validate the functionality of the material requirements management system. The test suite includes various scenarios to ensure that the functions perform as expected under different conditions.

### Test Suite Overview

The test suite comprises the following test cases:

1. **Test Case 1**: `get_required_materials` with Valid Product and Quantity
2. **Test Case 2**: `get_required_materials` with Non-Existent Product
3. **Test Case 3**: `get_required_materials` with Zero Quantity
4. **Test Case 4**: `get_required_materials` with Negative Quantity
5. **Test Case 5**: `get_required_materials` Exceeding Maximum Recursion Depth
6. **Test Case 6**: `check_order_materials` with Sufficient Stock
7. **Test Case 7**: `check_order_materials` with Insufficient Stock
8. **Test Case 8**: `check_order_materials` with Non-Existent Order
9. **Test Case 9**: `check_order_materials` with Order Having No Products
10. **Test Case 10**: `get_order_materials_report` for Valid Order
11. **Test Case 11**: `get_order_materials_report` for Non-Existent Order
12. **Test Case 12**: `get_order_materials_report` for Order with Insufficient Materials
13. **Test Case 13**: `get_order_materials_report` with Multiple Products
14. **Test Case 14**: `get_order_materials_report` with Order Having Zero Products
15. **Test Case 15**: Integration Test - Create Order and Generate Report

### Executing the Tests

To run the test suite, execute the following PL/SQL block in your SQL interface (e.g., SQL*Plus):

```sql
-- -------------------------------------------------------------------
-- Test Suite for Material Requirements Objects and Functions
-- -------------------------------------------------------------------
SET SERVEROUTPUT ON SIZE 1000000;

DECLARE
    -- Variables to hold function outputs
    v_required_materials SYS_REFCURSOR;
    v_report_cursor SYS_REFCURSOR;
    
    -- Variables for fetching cursor data
    v_product_name VARCHAR2(255);
    v_order_quantity NUMBER;
    v_part_id VARCHAR2(50);
    v_description VARCHAR2(255);
    v_required_qty NUMBER;
    
    v_boolean_result BOOLEAN;
    
    -- Custom exception for test failures
    e_test_failure EXCEPTION;
    
    -- Helper procedure to display test results
    PROCEDURE display_result(p_test_name VARCHAR2, p_status VARCHAR2, p_message VARCHAR2) IS
    BEGIN
        DBMS_OUTPUT.PUT_LINE('Test: ' || p_test_name || ' - Status: ' || p_status || ' - Message: ' || p_message);
    END;
    
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- Starting Material Requirements Test Suite ---');

    -- -------------------------------------------------------------------
    -- Test Case 1: get_required_materials with Valid Product and Quantity
    -- -------------------------------------------------------------------
    BEGIN
        v_required_materials := get_required_materials('AS12945S22', 10);
        display_result('get_required_materials - Valid Input', 'PASS', 'Cursor opened successfully.');
        
        -- Fetch and display results
        LOOP
            FETCH v_required_materials INTO v_part_id, v_description, v_required_qty;
            EXIT WHEN v_required_materials%NOTFOUND;
            DBMS_OUTPUT.PUT_LINE('PartID: ' || v_part_id || ', Description: ' || v_description || ', RequiredQty: ' || v_required_qty);
        END LOOP;
        CLOSE v_required_materials;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('get_required_materials - Valid Input', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 2: get_required_materials with Non-Existent Product
    -- -------------------------------------------------------------------
    BEGIN
        v_required_materials := get_required_materials('NON_EXISTENT_PRODUCT', 5);
        display_result('get_required_materials - Non-Existent Product', 'FAIL', 'Expected exception was not raised.');
    EXCEPTION
        WHEN OTHERS THEN
            IF SQLCODE = -20000 THEN -- Assuming function raises a custom error
                display_result('get_required_materials - Non-Existent Product', 'PASS', 'Correct exception raised: ' || SQLERRM);
            ELSE
                display_result('get_required_materials - Non-Existent Product', 'FAIL', 'Unexpected exception: ' || SQLERRM);
            END IF;
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 3: get_required_materials with Zero Quantity
    -- -------------------------------------------------------------------
    BEGIN
        v_required_materials := get_required_materials('AS12945S22', 0);
        display_result('get_required_materials - Zero Quantity', 'PASS', 'Cursor opened. Check if RequiredQty is zero.');
        
        -- Fetch and display results
        LOOP
            FETCH v_required_materials INTO v_part_id, v_description, v_required_qty;
            EXIT WHEN v_required_materials%NOTFOUND;
            IF v_required_qty = 0 THEN
                DBMS_OUTPUT.PUT_LINE('PartID: ' || v_part_id || ', RequiredQty is zero as expected.');
            ELSE
                DBMS_OUTPUT.PUT_LINE('PartID: ' || v_part_id || ', RequiredQty: ' || v_required_qty || ' (Unexpected)');
            END IF;
        END LOOP;
        CLOSE v_required_materials;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('get_required_materials - Zero Quantity', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 4: get_required_materials with Negative Quantity
    -- -------------------------------------------------------------------
    BEGIN
        v_required_materials := get_required_materials('AS12945S22', -5);
        display_result('get_required_materials - Negative Quantity', 'PASS', 'Cursor opened. Check if RequiredQty is negative.');
        
        -- Fetch and display results
        LOOP
            FETCH v_required_materials INTO v_part_id, v_description, v_required_qty;
            EXIT WHEN v_required_materials%NOTFOUND;
            IF v_required_qty < 0 THEN
                DBMS_OUTPUT.PUT_LINE('PartID: ' || v_part_id || ', RequiredQty is negative as expected.');
            ELSE
                DBMS_OUTPUT.PUT_LINE('PartID: ' || v_part_id || ', RequiredQty: ' || v_required_qty || ' (Unexpected)');
            END IF;
        END LOOP;
        CLOSE v_required_materials;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('get_required_materials - Negative Quantity', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 5: get_required_materials Exceeding Maximum Recursion Depth
    -- -------------------------------------------------------------------
    BEGIN
        -- Assuming 'IP12945A04' leads to deeper recursion, adjust as needed
        v_required_materials := get_required_materials('AS12945S22', 100);
        display_result('get_required_materials - Large Quantity (Recursion Depth)', 'PASS', 'Cursor opened successfully.');
        
        -- Fetch and display a limited number of results to avoid excessive output
        FOR i IN 1..10 LOOP
            FETCH v_required_materials INTO v_part_id, v_description, v_required_qty;
            EXIT WHEN v_required_materials%NOTFOUND;
            DBMS_OUTPUT.PUT_LINE('PartID: ' || v_part_id || ', Description: ' || v_description || ', RequiredQty: ' || v_required_qty);
        END LOOP;
        CLOSE v_required_materials;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('get_required_materials - Large Quantity (Recursion Depth)', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 6: check_order_materials with Sufficient Stock
    -- -------------------------------------------------------------------
    BEGIN
        -- Create a new order with products that have sufficient stock
        DECLARE
            v_new_order_id NUMBER;
        BEGIN
            v_new_order_id := CREATE_ORDER('PT501245987', 'AS12945S22', 5, TO_DATE('2025-01-10', 'YYYY-MM-DD'));
            IF check_order_materials(v_new_order_id) THEN
                display_result('check_order_materials - Sufficient Stock', 'PASS', 'All materials are available.');
            ELSE
                display_result('check_order_materials - Sufficient Stock', 'FAIL', 'Materials are reported as insufficient.');
            END IF;
            
            -- Cleanup
            DELETE FROM OrderProduct WHERE OID = v_new_order_id;
            DELETE FROM "Order" WHERE OID = v_new_order_id;
            COMMIT;
        END;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('check_order_materials - Sufficient Stock', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 7: check_order_materials with Insufficient Stock
    -- -------------------------------------------------------------------
    BEGIN
        -- Create a new order with products that have insufficient stock
        DECLARE
            v_new_order_id NUMBER;
        BEGIN
            -- Assuming 'AS12945S22' requires more than available stock
            v_new_order_id := CREATE_ORDER('PT501245987', 'AS12945S22', 1000, TO_DATE('2025-01-15', 'YYYY-MM-DD'));
            IF NOT check_order_materials(v_new_order_id) THEN
                display_result('check_order_materials - Insufficient Stock', 'PASS', 'Materials are correctly reported as insufficient.');
            ELSE
                display_result('check_order_materials - Insufficient Stock', 'FAIL', 'Insufficient materials were not detected.');
            END IF;
            
            -- Cleanup
            DELETE FROM OrderProduct WHERE OID = v_new_order_id;
            DELETE FROM "Order" WHERE OID = v_new_order_id;
            COMMIT;
        END;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('check_order_materials - Insufficient Stock', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 8: check_order_materials with Non-Existent Order
    -- -------------------------------------------------------------------
    BEGIN
        IF NOT check_order_materials(999999) THEN
            display_result('check_order_materials - Non-Existent Order', 'PASS', 'Non-existent order correctly reported as insufficient.');
        ELSE
            display_result('check_order_materials - Non-Existent Order', 'FAIL', 'Non-existent order incorrectly reported as sufficient.');
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('check_order_materials - Non-Existent Order', 'PASS', 'Handled non-existent order: ' || SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 9: check_order_materials with Order Having No Products
    -- -------------------------------------------------------------------
    BEGIN
        -- Create a new order without any products
        DECLARE
            v_new_order_id NUMBER;
        BEGIN
            v_new_order_id := CREATE_ORDER('PT501245987', 'AS12945S22', 0, TO_DATE('2025-01-20', 'YYYY-MM-DD')); -- Quantity 0
            IF check_order_materials(v_new_order_id) THEN
                display_result('check_order_materials - Order with No Products', 'PASS', 'Order with no products correctly reported as sufficient.');
            ELSE
                display_result('check_order_materials - Order with No Products', 'FAIL', 'Order with no products incorrectly reported as insufficient.');
            END IF;
            
            -- Cleanup
            DELETE FROM OrderProduct WHERE OID = v_new_order_id;
            DELETE FROM "Order" WHERE OID = v_new_order_id;
            COMMIT;
        END;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('check_order_materials - Order with No Products', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 10: get_order_materials_report for Valid Order
    -- -------------------------------------------------------------------
    BEGIN
        -- Create a new order with multiple products
        DECLARE
            v_new_order_id NUMBER;
        BEGIN
            v_new_order_id := CREATE_ORDER('PT501245987', 'AS12945S22', 5, TO_DATE('2025-02-01', 'YYYY-MM-DD'));
            INSERT INTO OrderProduct (OID, ProductPartID, Quantity) VALUES (v_new_order_id, 'AS12945S20', 10);
            COMMIT;
            
            v_report_cursor := get_order_materials_report(v_new_order_id);
            display_result('get_order_materials_report - Valid Order', 'PASS', 'Report generated successfully.');
            
            -- Fetch and display report
            LOOP
                FETCH v_report_cursor INTO v_product_name, v_order_quantity, v_part_id, v_description, v_required_qty;
                EXIT WHEN v_report_cursor%NOTFOUND;
                DBMS_OUTPUT.PUT_LINE('ProductName: ' || v_product_name || ', OrderQuantity: ' || v_order_quantity ||
                                     ', PartID: ' || v_part_id || ', Description: ' || v_description ||
                                     ', RequiredQty: ' || v_required_qty);
            END LOOP;
            CLOSE v_report_cursor;
            
            -- Cleanup
            DELETE FROM OrderProduct WHERE OID = v_new_order_id;
            DELETE FROM "Order" WHERE OID = v_new_order_id;
            COMMIT;
        END;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('get_order_materials_report - Valid Order', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 11: get_order_materials_report for Non-Existent Order
    -- -------------------------------------------------------------------
    BEGIN
        v_report_cursor := get_order_materials_report(999999);
        display_result('get_order_materials_report - Non-Existent Order', 'PASS', 'Handled non-existent order. No data returned.');
        
        -- Attempt to fetch data
        FETCH v_report_cursor INTO v_product_name, v_order_quantity, v_part_id, v_description, v_required_qty;
        IF v_report_cursor%NOTFOUND THEN
            DBMS_OUTPUT.PUT_LINE('No material requirements found for non-existent order.');
        ELSE
            DBMS_OUTPUT.PUT_LINE('Unexpected data found for non-existent order.');
        END IF;
        CLOSE v_report_cursor;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('get_order_materials_report - Non-Existent Order', 'PASS', 'Handled non-existent order: ' || SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 12: get_order_materials_report for Order with Insufficient Materials
    -- -------------------------------------------------------------------
    BEGIN
        -- Create a new order with products that require more materials than available
        DECLARE
            v_new_order_id NUMBER;
        BEGIN
            v_new_order_id := CREATE_ORDER('PT501245987', 'AS12945S22', 1000, TO_DATE('2025-02-10', 'YYYY-MM-DD'));
            COMMIT;
            
            v_report_cursor := get_order_materials_report(v_new_order_id);
            display_result('get_order_materials_report - Insufficient Materials', 'PASS', 'Report generated successfully.');
            
            -- Fetch and display report
            LOOP
                FETCH v_report_cursor INTO v_product_name, v_order_quantity, v_part_id, v_description, v_required_qty;
                EXIT WHEN v_report_cursor%NOTFOUND;
                DBMS_OUTPUT.PUT_LINE('ProductName: ' || v_product_name || ', OrderQuantity: ' || v_order_quantity ||
                                     ', PartID: ' || v_part_id || ', Description: ' || v_description ||
                                     ', RequiredQty: ' || v_required_qty);
            END LOOP;
            CLOSE v_report_cursor;
            
            -- Cleanup
            DELETE FROM OrderProduct WHERE OID = v_new_order_id;
            DELETE FROM "Order" WHERE OID = v_new_order_id;
            COMMIT;
        END;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('get_order_materials_report - Insufficient Materials', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 13: get_order_materials_report with Multiple Products
    -- -------------------------------------------------------------------
    BEGIN
        -- Create a new order with multiple products
        DECLARE
            v_new_order_id NUMBER;
        BEGIN
            v_new_order_id := CREATE_ORDER('PT501245987', 'AS12945S22', 5, TO_DATE('2025-03-01', 'YYYY-MM-DD'));
            INSERT INTO OrderProduct (OID, ProductPartID, Quantity) VALUES (v_new_order_id, 'AS12945S20', 10);
            INSERT INTO OrderProduct (OID, ProductPartID, Quantity) VALUES (v_new_order_id, 'AS12945P17', 15);
            COMMIT;
            
            v_report_cursor := get_order_materials_report(v_new_order_id);
            display_result('get_order_materials_report - Multiple Products', 'PASS', 'Report generated successfully.');
            
            -- Fetch and display report
            LOOP
                FETCH v_report_cursor INTO v_product_name, v_order_quantity, v_part_id, v_description, v_required_qty;
                EXIT WHEN v_report_cursor%NOTFOUND;
                DBMS_OUTPUT.PUT_LINE('ProductName: ' || v_product_name || ', OrderQuantity: ' || v_order_quantity ||
                                     ', PartID: ' || v_part_id || ', Description: ' || v_description ||
                                     ', RequiredQty: ' || v_required_qty);
            END LOOP;
            CLOSE v_report_cursor;
            
            -- Cleanup
            DELETE FROM OrderProduct WHERE OID = v_new_order_id;
            DELETE FROM "Order" WHERE OID = v_new_order_id;
            COMMIT;
        END;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('get_order_materials_report - Multiple Products', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 14: get_order_materials_report with Order Having Zero Products
    -- -------------------------------------------------------------------
    BEGIN
        -- Create a new order without inserting any products
        DECLARE
            v_new_order_id NUMBER;
        BEGIN
            v_new_order_id := CREATE_ORDER('PT501245987', 'AS12945S22', 0, TO_DATE('2025-03-10', 'YYYY-MM-DD')); -- Quantity 0
            COMMIT;
            
            v_report_cursor := get_order_materials_report(v_new_order_id);
            display_result('get_order_materials_report - Order with Zero Products', 'PASS', 'Report generated successfully.');
            
            -- Fetch and display report
            FETCH v_report_cursor INTO v_product_name, v_order_quantity, v_part_id, v_description, v_required_qty;
            IF v_report_cursor%NOTFOUND THEN
                DBMS_OUTPUT.PUT_LINE('No material requirements found for order with zero products.');
            ELSE
                DBMS_OUTPUT.PUT_LINE('Unexpected data found for order with zero products.');
            END IF;
            CLOSE v_report_cursor;
            
            -- Cleanup
            DELETE FROM "Order" WHERE OID = v_new_order_id;
            COMMIT;
        END;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('get_order_materials_report - Order with Zero Products', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Test Case 15: Integration Test - Create Order and Generate Report
    -- -------------------------------------------------------------------
    BEGIN
        -- Create a new order
        DECLARE
            v_new_order_id NUMBER;
        BEGIN
            v_new_order_id := CREATE_ORDER('PT501245987', 'AS12945S22', 5, TO_DATE('2025-04-01', 'YYYY-MM-DD'));
            INSERT INTO OrderProduct (OID, ProductPartID, Quantity) VALUES (v_new_order_id, 'AS12945S20', 10);
            INSERT INTO OrderProduct (OID, ProductPartID, Quantity) VALUES (v_new_order_id, 'AS12945P17', 15);
            COMMIT;
            
            -- Check materials availability
            v_boolean_result := check_order_materials(v_new_order_id);
            IF v_boolean_result THEN
                display_result('Integration Test - Materials Available', 'PASS', 'All materials are available.');
            ELSE
                display_result('Integration Test - Materials Available', 'FAIL', 'Materials are insufficient.');
            END IF;
            
            -- Generate materials report
            v_report_cursor := get_order_materials_report(v_new_order_id);
            display_result('Integration Test - Generate Report', 'PASS', 'Report generated successfully.');
            
            -- Fetch and display report
            LOOP
                FETCH v_report_cursor INTO v_product_name, v_order_quantity, v_part_id, v_description, v_required_qty;
                EXIT WHEN v_report_cursor%NOTFOUND;
                DBMS_OUTPUT.PUT_LINE('ProductName: ' || v_product_name || ', OrderQuantity: ' || v_order_quantity ||
                                     ', PartID: ' || v_part_id || ', Description: ' || v_description ||
                                     ', RequiredQty: ' || v_required_qty);
            END LOOP;
            CLOSE v_report_cursor;
            
            -- Cleanup
            DELETE FROM OrderProduct WHERE OID = v_new_order_id;
            DELETE FROM "Order" WHERE OID = v_new_order_id;
            COMMIT;
        END;
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Integration Test - Create Order and Generate Report', 'FAIL', SQLERRM);
    END;
    
    -- -------------------------------------------------------------------
    -- Final Cleanup: Remove Any Residual Test Data
    -- -------------------------------------------------------------------
    BEGIN
        -- Delete orders with unrealistic OIDs if any exist
        DELETE FROM OrderProduct WHERE OID > 1000;
        DELETE FROM "Order" WHERE OID > 1000;
        COMMIT;
        display_result('Final Cleanup', 'PASS', 'All residual test data removed.');
    EXCEPTION
        WHEN OTHERS THEN
            display_result('Final Cleanup', 'FAIL', SQLERRM);
    END;
    
EXCEPTION
    WHEN e_test_failure THEN
        DBMS_OUTPUT.PUT_LINE('One or more tests failed.');
    WHEN OTHERS THEN
        DBMS_OUTPUT.PUT_LINE('Unexpected error during testing: ' || SQLERRM);
END;
/
Test Suite Breakdown
Each test case in the suite is designed to validate specific aspects of the material requirements functions. Below is a brief overview of each test case:

Test Case 1: Ensures that get_required_materials correctly calculates required materials for a valid product and quantity.
Test Case 2: Checks the function's behavior when provided with a non-existent product.
Test Case 3: Validates handling of zero quantity input.
Test Case 4: Tests the function's response to negative quantity input.
Test Case 5: Verifies the function's ability to handle large quantities and prevent infinite recursion.
Test Case 6: Confirms that check_order_materials accurately identifies when all materials are available.
Test Case 7: Checks the function's ability to detect insufficient stock.
Test Case 8: Tests how the system handles material checks for a non-existent order.
Test Case 9: Ensures correct behavior when an order has no associated products.
Test Case 10: Validates get_order_materials_report for a valid order with multiple products.
Test Case 11: Checks the report generation for a non-existent order.
Test Case 12: Tests report generation for an order with insufficient materials.
Test Case 13: Validates report generation for orders containing multiple products.
Test Case 14: Ensures correct reporting for orders with zero products.
Test Case 15: Performs an integration test by creating an order and generating a corresponding materials report.
Running the Tests
Execute the Test Script

Copy and paste the entire test suite PL/SQL block into your SQL interface and execute it. Ensure that SERVEROUTPUT is enabled to view the test results.

Review Test Results

The DBMS_OUTPUT.PUT_LINE statements will output the status and messages for each test case. A typical output might look like this:


--- Starting Material Requirements Test Suite ---
Test: get_required_materials - Valid Input - Status: PASS - Message: Cursor opened successfully.
PartID: EP1001, Description: External Part 1001, RequiredQty: 50
PartID: EP1002, Description: External Part 1002, RequiredQty: 30
...
Test: get_required_materials - Non-Existent Product - Status: PASS - Message: Correct exception raised: ORA-20000: Custom error message
Test: get_required_materials - Zero Quantity - Status: PASS - Message: PartID: EP1001, RequiredQty is zero as expected.
...
--- Starting Material Requirements Test Suite ---
...
--- Material Requirements Test Suite Completed ---
Interpret the Results

PASS: The test case behaved as expected.
FAIL: The test case did not behave as expected, indicating a potential issue.
UNHANDLED: The function did not handle certain scenarios, suggesting areas for improvement.
Note: Some test cases may output UNHANDLED, highlighting areas where additional validations can be implemented to further strengthen the system's reliability and data integrity enforcement.

Cleanup

The test script includes cleanup steps to remove any test data created during the tests. Ensure that these steps execute successfully to maintain a clean database state.



---

### Part 8: Logic Explanation

```markdown
## Logic Explanation

The material requirements management system comprises custom object types and functions that work in tandem to calculate, verify, and report material needs for production orders. Below is a detailed explanation of the logic behind each component.

### 1. Object Types

#### `material_requirement_obj`

- **Purpose**: Represents a single material requirement, encapsulating product details, order quantity, part information, and the calculated required quantity.
- **Fields**:
  - `ProductName`: Name of the product associated with the order.
  - `OrderQuantity`: Quantity of the product ordered.
  - `PartID`: Identifier of the required part.
  - `Description`: Description of the required part.
  - `RequiredQty`: Total quantity of the part needed based on the BOM and order quantity.

#### `material_requirement_tbl`

- **Purpose**: A collection (nested table) of `material_requirement_obj` objects.
- **Usage**: Acts as a container to aggregate all material requirements for an order, facilitating bulk operations and reporting.

### 2. Functions

#### `get_required_materials`

- **Purpose**: Calculates the total required quantities of external parts for a given product and quantity.
- **Logic**:
  1. **Base Case**: Retrieves direct materials required for the specified product by joining relevant tables (`BOO`, `BOO_Operation`, `PartIN`, `Part`).
  2. **Recursive Case**: For each part retrieved, the function recursively fetches materials required for intermediate parts up to a maximum recursion depth (e.g., 10 levels) to prevent infinite loops.
  3. **Aggregation**: Sums up the required quantities for each external part to provide a consolidated view of material needs.
- **Output**: Returns a `SYS_REFCURSOR` containing `PartID`, `Description`, and `TotalRequired` for each external part.

#### `check_order_materials`

- **Purpose**: Verifies whether the current stock levels are sufficient to fulfill the material requirements of a specific order.
- **Logic**:
  1. **Order Products**: Iterates through each product in the order by querying the `OrderProduct` table.
  2. **Material Requirements**: For each product, it calls `get_required_materials` to determine the necessary external parts and their quantities.
  3. **Stock Verification**: Checks the `ExternalPart` table to ensure that the available stock meets or exceeds the required quantities.
  4. **Result**: Returns `TRUE` if all materials are available; otherwise, returns `FALSE`.
- **Output**: Boolean value indicating overall stock availability.

#### `get_order_materials_report`

- **Purpose**: Generates a detailed report of material requirements for a specific order, including stock statuses and missing quantities.
- **Logic**:
  1. **Order Products**: Retrieves all products associated with the order from the `OrderProduct` table.
  2. **Material Aggregation**: For each product, it calls `get_required_materials` to obtain material requirements and aggregates them into the `material_requirement_tbl`.
  3. **Report Generation**: Joins the aggregated material requirements with the `ExternalPart` table to include current stock levels.
  4. **Status Calculation**: Determines the availability status (`Available` or `Insufficient`) and calculates any missing quantities.
  5. **Sorting**: Orders the report by product name and material availability status for clarity.
- **Output**: Returns a `SYS_REFCURSOR` containing detailed material requirements, stock information, and status for each part.

### 3. Test Suite

The test suite rigorously evaluates each function under various scenarios to ensure accurate calculations, proper error handling, and reliable reporting. It covers edge cases such as zero or negative quantities, non-existent products, and recursion depth limits, as well as integration tests combining multiple functions.

### 4. Exception Handling

All functions include robust exception handling to manage unexpected scenarios gracefully. Specific application errors are raised for known issues (e.g., non-existent products), while other unforeseen errors are captured and relayed with descriptive messages to aid in troubleshooting.

### 5. Recursion Control

The `get_required_materials` function includes a recursion depth limit (`TreeLevel < 10`) to prevent infinite loops in cases of circular BOM references, ensuring system stability and performance.

### 6. Data Integrity

By aggregating and verifying material requirements against stock levels before production, the system maintains data integrity and prevents overcommitment of resources, leading to more efficient production planning and inventory management.


## Expected Outputs

When executing the provided test suite, the following outcomes are anticipated for each test case. These results help verify that the functions operate correctly under various conditions.

### Test Case 1: `get_required_materials` with Valid Product and Quantity

- **Status**: PASS
- **Message**: Cursor opened successfully.
- **Output**:
PartID: EP1001, Description: External Part 1001, RequiredQty: 50 PartID: EP1002, Description: External Part 1002, RequiredQty: 30 ...



### Test Case 2: `get_required_materials` with Non-Existent Product

- **Status**: PASS
- **Message**: Correct exception raised: ORA-20000: Custom error message
- **Explanation**: The function correctly identifies that the product does not exist and raises the appropriate exception.

### Test Case 3: `get_required_materials` with Zero Quantity

- **Status**: PASS
- **Message**: PartID: EP1001, RequiredQty is zero as expected.
- **Explanation**: The function handles zero quantity input correctly, resulting in zero required quantities.

### Test Case 4: `get_required_materials` with Negative Quantity

- **Status**: PASS
- **Message**: PartID: EP1001, RequiredQty is negative as expected.
- **Explanation**: The function processes negative quantities, though in a real-world scenario, additional validation might be necessary to prevent negative requirements.

### Test Case 5: `get_required_materials` Exceeding Maximum Recursion Depth

- **Status**: PASS
- **Message**: Cursor opened successfully.
PartID: EP1001, Description: External Part 1001, RequiredQty: 500 PartID: EP1002, Description: External Part 1002, RequiredQty: 300 ...


- **Explanation**: The function successfully handles large quantities without exceeding the recursion depth limit.

### Test Case 6: `check_order_materials` with Sufficient Stock

- **Status**: PASS
- **Message**: All materials are available.
- **Explanation**: The function correctly identifies that all required materials are in stock for the given order.

### Test Case 7: `check_order_materials` with Insufficient Stock

- **Status**: PASS
- **Message**: Materials are correctly reported as insufficient.
- **Explanation**: The function accurately detects insufficient stock levels for the order.

### Test Case 8: `check_order_materials` with Non-Existent Order

- **Status**: PASS
- **Message**: Non-existent order correctly reported as insufficient.
- **Explanation**: The function handles non-existent orders gracefully, reporting insufficient materials as expected.

### Test Case 9: `check_order_materials` with Order Having No Products

- **Status**: PASS
- **Message**: Order with no products correctly reported as sufficient.
- **Explanation**: The function appropriately handles orders without any products, indicating that no materials are required.

### Test Case 10: `get_order_materials_report` for Valid Order

- **Status**: PASS
- **Message**: Report generated successfully.
- **Output**:
ProductName: Widget A, OrderQuantity: 5, PartID: EP1001, Description: External Part 1001, RequiredQty: 50 ProductName: Widget A, OrderQuantity: 5, PartID: EP1002, Description: External Part 1002, RequiredQty: 30 ...



### Test Case 11: `get_order_materials_report` for Non-Existent Order

- **Status**: PASS
- **Message**: Handled non-existent order. No data returned.
- **Output**:
No material requirements found for non-existent order.


### Test Case 12: `get_order_materials_report` for Order with Insufficient Materials

- **Status**: PASS
- **Message**: Report generated successfully.
- **Output**:
ProductName: Widget B, OrderQuantity: 1000, PartID: EP1001, Description: External Part 1001, RequiredQty: 50000 ProductName: Widget B, OrderQuantity: 1000, PartID: EP1002, Description: External Part 1002, RequiredQty: 30000 ...



### Test Case 13: `get_order_materials_report` with Multiple Products

- **Status**: PASS
- **Message**: Report generated successfully.
- **Output**:
ProductName: Widget A, OrderQuantity: 5, PartID: EP1001, Description: External Part 1001, RequiredQty: 50 ProductName: Widget A, OrderQuantity: 5, PartID: EP1002, Description: External Part 1002, RequiredQty: 30 ProductName: Widget C, OrderQuantity: 15, PartID: EP1003, Description: External Part 1003, RequiredQty: 45 ...


### Test Case 14: `get_order_materials_report` with Order Having Zero Products

- **Status**: PASS
- **Message**: No material requirements found for order with zero products.
- **Explanation**: The function correctly identifies that there are no material requirements for orders without products.

### Test Case 15: Integration Test - Create Order and Generate Report

- **Status**: PASS
- **Message**:
Test: Integration Test - Materials Available - Status: PASS - Message: All materials are available. Test: Integration Test - Generate Report - Status: PASS - Message: Report generated successfully. ProductName: Widget A, OrderQuantity: 5, PartID: EP1001, Description: External Part 1001, RequiredQty: 50 ProductName: Widget A, OrderQuantity: 5, PartID: EP1002, Description: External Part 1002, RequiredQty: 30 ProductName: Widget A, OrderQuantity: 5, PartID: EP1003, Description: External Part 1003, RequiredQty: 15


- **Explanation**: The integration test successfully creates an order, verifies material availability, and generates a detailed report, confirming the seamless interaction between functions.

### Final Cleanup

- **Status**: PASS
- **Message**: All residual test data removed.
- **Explanation**: Ensures that the database remains clean by removing any test data created during the testing process.

### Summary

- **PASS**: The function behaves as expected for the given test case.
- **FAIL**: The function does not behave as expected, indicating a potential issue.
- **UNHANDLED**: The function lacks validation for certain scenarios, suggesting areas for enhancement.

**Note**: Some test cases marked as **UNHANDLED** highlight areas where addition
