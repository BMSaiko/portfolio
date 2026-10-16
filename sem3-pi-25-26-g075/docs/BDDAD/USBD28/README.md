
---

# USBD28: List Locomotives Supporting Multiple Gauges

## 1. Summary

The primary objective of this PL/SQL development is to fulfill the requirement of User Story USBD28: *"As a planner, I want a list of all locomotives of a given operator that support multiple gauges."*

To achieve this, a stored PL/SQL function named `LocomotiveWithMoreThanOneGaugeByOperator` was created. This function accepts an operator's VAT number as input and returns a `SYS_REFCURSOR` containing a list of all locomotives belonging to that operator whose mechanical model is designed to operate on more than one type of gauge. Additionally, the function returns the specific count of gauges supported by each locomotive's model.

## 2. Function Implementation

The implementation strategy focuses on using **subqueries** to achieve both filtering and data calculation, demonstrating compliance with advanced SQL data manipulation techniques.

### 2.1. Function Code

```sql
CREATE OR REPLACE FUNCTION LocomotiveWithMoreThanOneGaugeByOperator (
    given_operator_id IN VehicleOperator.vat_number%TYPE
) RETURN SYS_REFCURSOR
IS
    var_cursor SYS_REFCURSOR;
BEGIN
OPEN var_cursor FOR
SELECT
    locomotive.locomotive_number,
    locomotive.locomotive_name,
    locomotive_model.locomotive_model_name,
    vehicle_operator.operator_name,

    (SELECT COUNT(locomotive_model_gauge.gauge_id)
     FROM LocomotiveModelGauge locomotive_model_gauge
     WHERE locomotive_model_gauge.locomotive_model_id = locomotive_model.locomotive_model_id) AS gauge_count

FROM
    Locomotive locomotive
        JOIN
    LocomotiveModel locomotive_model ON locomotive.model_id = locomotive_model.locomotive_model_id
        JOIN
    VehicleOperator vehicle_operator ON locomotive.operator_id = vehicle_operator.vat_number
WHERE
    locomotive.operator_id = given_operator_id
  AND locomotive.model_id IN (
    SELECT
        locomotive_model_id
    FROM
        LocomotiveModelGauge
    GROUP BY
        locomotive_model_id
    HAVING
        COUNT(gauge_id) > 1
);

RETURN var_cursor;

EXCEPTION
    WHEN OTHERS THEN
        RAISE;
END LocomotiveWithMoreThanOneGaugeByOperator;
/
```

### 2.2. Logic Explanation

The function employs two distinct types of subqueries to build the result set:

1.  **Subquery for Filtering (WHERE Clause):**
    The query filters locomotives using the `IN` operator combined with a subquery.
    *   This subquery groups entries in the `LocomotiveModelGauge` table by `model_id`.
    *   It uses the `HAVING COUNT(...) > 1` clause to identify only those models associated with multiple gauges.
    *   This efficiently restricts the main query to select only relevant locomotives.

2.  **Scalar Subquery for Data Retrieval (SELECT Clause):**
    To provide the additional information of *how many* gauges a locomotive supports, a scalar subquery is used directly in the column list.
    *   This subquery executes for each row returned, counting the entries in `LocomotiveModelGauge` that match the current row's model ID.

This dual-subquery approach ensures modular logic and demonstrates adherence to PL/SQL subquery design patterns.

## 3. Testing and Validation Strategy

To ensure the function operates correctly under various conditions, a comprehensive testing strategy was implemented using PL/SQL **anonymous blocks**. This approach allows for direct interaction with the function and verification of its output via `DBMS_OUTPUT`.

The strategy covers three distinct scenarios:
*   **Positive Scenarios:** Testing with operators known to have multi-gauge locomotives.
*   **Negative Scenarios:** Testing with valid operators that have no multi-gauge locomotives.
*   **Invalid Input Scenarios:** Testing with a non-existent operator ID.

### 3.1. Test Data Setup

Specific `INSERT` statements were executed to create the necessary test conditions:
*   **Modified Data:** The existing `Eurosprinter` model (ID 1) was updated to support a second gauge.
*   **New Negative Case:** A new operator (`DHL`) was created with a locomotive (`Pioneer`) whose model is strictly single-gauge.
*   **New Positive Cases:**
    *   A new model (`Alstom Prima H4`) supporting **three** gauges was assigned to a new `Medway` locomotive (`Versatile`).
    *   A new model (`Vossloh Euro 4000`) supporting **two** gauges was assigned to a new `Captrain` locomotive (`Santiago`).

### 3.2. Test Cases

The following anonymous blocks were executed to validate the function.

#### Test Case 1: Medway Operator (Positive Scenario)

*   **Objective:** Verify the function identifies multiple locomotives with different gauge counts (2 and 3) for a single operator.
*   **Execution Code:**
    ```sql
    SET SERVEROUTPUT ON;
    DECLARE
        locomotive_cursor        SYS_REFCURSOR;
        locomotive_number        Locomotive.locomotive_number%TYPE;
        locomotive_name          Locomotive.locomotive_name%TYPE;
        locomotive_model_name    LocomotiveModel.locomotive_model_name%TYPE;
        locomotive_operator_name VehicleOperator.operator_name%TYPE;
        gauge_count              NUMBER;
        var_found_locomotives    BOOLEAN := FALSE;
    BEGIN
        DBMS_OUTPUT.PUT_LINE('--- Operator: Medway ---');
        locomotive_cursor := LocomotiveWithMoreThanOneGaugeByOperator('PT509017800');
        LOOP
            FETCH locomotive_cursor INTO locomotive_number, locomotive_name, locomotive_model_name, locomotive_operator_name, gauge_count;
            EXIT WHEN locomotive_cursor%NOTFOUND;
            var_found_locomotives := TRUE;
            DBMS_OUTPUT.PUT_LINE('  -> No.: ' || locomotive_number || ' | Name: ' || locomotive_name || ' | Model: ' || locomotive_model_name || ' | Gauges: ' || gauge_count);
        END LOOP;
        IF NOT var_found_locomotives THEN
            DBMS_OUTPUT.PUT_LINE('No results found for this operator.');
        END IF;
        CLOSE locomotive_cursor;
        DBMS_OUTPUT.PUT_LINE('--- Test completed ---');
    END;
    /
    ```
*   **Expected Result:** Output of 4 locomotives: 3 `Eurosprinter` (2 gauges) and 1 `Alstom Prima H4` (3 gauges).

#### Test Case 2: Captrain Operator (Positive Scenario)

*   **Objective:** Verify the function works for a different operator with a distinct multi-gauge model.
*   **Execution Code:**
    ```sql
    SET SERVEROUTPUT ON;
    DECLARE
        -- (Variable declarations same as above)
    BEGIN
        DBMS_OUTPUT.PUT_LINE('--- Operator: Captrain ---');
        locomotive_cursor := LocomotiveWithMoreThanOneGaugeByOperator('PT507832388');
        -- (Loop and output logic same as above)
    END;
    /
    ```
*   **Expected Result:** Output of 1 locomotive: "Santiago" (2 gauges).

#### Test Case 3: DHL Operator (Negative Scenario)

*   **Objective:** Confirm the function returns an empty set for an operator that exists but owns only single-gauge locomotives.
*   **Execution Code:**
    ```sql
    SET SERVEROUTPUT ON;
    DECLARE
        -- (Variable declarations same as above)
    BEGIN
        DBMS_OUTPUT.PUT_LINE('--- Operator: DHL (Negative Test) ---');
        locomotive_cursor := LocomotiveWithMoreThanOneGaugeByOperator('PT508123456');
        -- (Loop and output logic same as above)
    END;
    /
    ```
*   **Expected Result:** Message: "No results found for this operator."

#### Test Case 4: Nonexistent Operator (Invalid Input)

*   **Objective:** Ensure graceful handling of invalid IDs.
*   **Execution Code:**
    ```sql
    SET SERVEROUTPUT ON;
    DECLARE
        -- (Variable declarations same as above)
    BEGIN
        DBMS_OUTPUT.PUT_LINE('--- Operator: Nonexistent ---');
        locomotive_cursor := LocomotiveWithMoreThanOneGaugeByOperator('XX000000000');
        -- (Loop and output logic same as above)
    END;
    /
    ```
*   **Expected Result:** Message: "No results found for this operator."

## 4. Conclusion

The function `LocomotiveWithMoreThanOneGaugeByOperator` is implemented by utilizing a **Scalar Subquery in the SELECT clause** and a **filtering Subquery in the WHERE clause**, it strictly adheres to the project's architectural guidelines while providing accurate data on multi-gauge locomotives and their specific gauge counts. The comprehensive test suite confirms its reliability across all scenarios.