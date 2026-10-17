# USBD26: Material Availability Check for Orders

## User Story

**As a Production Manager,**
I want to verify if we have the necessary materials and components in stock to fulfill a given order,
So that we can ensure timely and efficient order processing.

## Acceptance Criteria

1. A PL/SQL function is developed to assess stock levels against the requirements of a specific order.
2. The function returns a clear indication of whether the stock is sufficient or identifies shortages.
3. The function accounts for minimum stock levels and reserved quantities.
4. Comprehensive testing is conducted with various orders to validate the function's accuracy.

## Overview

This module contains SQL scripts to check material availability for fulfilling orders. It includes functions to evaluate stock levels and generate detailed reports on material requirements.

## Files

- **`check_order_1_materials.sql`**:

  - Sets up a test scenario with sufficient stock levels.
  - Checks if all materials are available for Order #1.
  - Generates a detailed report of material requirements.
- **`check_order_1_insufficient_materials.sql`**:

  - Simulates a scenario with insufficient stock levels.
  - Checks material availability for Order #1 with reduced stock.
  - Generates a detailed report highlighting material shortages.

## Usage

1. **Setup**: Ensure your database is configured with the necessary tables and data as per the provided scripts.
2. **Run Tests**: Execute the SQL scripts in your Oracle database environment.
   - Use `check_order_1_materials.sql` to test with sufficient stock.
   - Use `check_order_1_insufficient_materials.sql` to test with insufficient stock.
3. **Review Output**: Check the DBMS output for messages indicating material availability and review the detailed reports for specific material requirements and shortages.

## Testing

- The scripts are designed to be run in an Oracle database environment with PL/SQL support.
- Ensure `DBMS_OUTPUT` is enabled to view the output messages.
- Adjust stock levels in the `ExternalPart` table as needed to simulate different scenarios.

## Conclusion

These scripts provide a robust mechanism for verifying material availability against order requirements, helping ensure efficient order processing and inventory management.
