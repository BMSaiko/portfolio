
---

# USBD30: List Facilities with Grain Silos but No Warehouses

## 1. Summary

The primary objective of this PL/SQL development is to fulfill the requirement of User Story USBD30: *"As a planner, I want a list of all facilities that have grain silos, but no warehouses."*

To achieve this, a stored PL/SQL function named `GetFacilitiesWithGrainSilosNoWarehouses` was created. This function returns a `SYS_REFCURSOR` containing a list of facilities that meet two specific criteria simultaneously: they must possess the "grain silo" infrastructure, and they must **not** possess the "warehouse" infrastructure.

## 2. Function Implementation

The implementation strategy focuses on using **subqueries** with set operators (`IN` and `NOT IN`).

### 2.1. Function Code

```sql
CREATE OR REPLACE FUNCTION GetFacilitiesWithGrainSilosNoWarehouses
RETURN SYS_REFCURSOR
IS
    result_cursor SYS_REFCURSOR;
BEGIN
    OPEN result_cursor FOR
        SELECT
            f.facility_id,
            f.facility_name
        FROM
            Facility f
        WHERE
            -- Inclusion Criterion: Must have a grain silo
            f.facility_id IN (
                SELECT fi.facility_id
                FROM FacilityInfrastructure fi
                JOIN Infrastructure i ON fi.infrastructure_id = i.infrastructure_id
                WHERE i.infrastructure_name = 'grain silo'
            )
            AND
            -- Exclusion Criterion: Must NOT have a warehouse
            f.facility_id NOT IN (
                SELECT fi.facility_id
                FROM FacilityInfrastructure fi
                JOIN Infrastructure i ON fi.infrastructure_id = i.infrastructure_id
                WHERE i.infrastructure_name = 'warehouse'
            );

    RETURN result_cursor;

EXCEPTION
    WHEN OTHERS THEN
        RAISE;
END GetFacilitiesWithGrainSilosNoWarehouses;
/
```

### 2.2. Logic Explanation

The function filters the `Facility` table using two distinct subqueries in the `WHERE` clause:

1.  **The Inclusion Logic (`IN`):**
    *   The first subquery identifies all `facility_id`s that are associated with the infrastructure named 'grain silo'.
    *   The `IN` operator ensures that the main query selects facilities present in this list.

2.  **The Exclusion Logic (`NOT IN`):**
    *   The second subquery identifies all `facility_id`s that are associated with the infrastructure named 'warehouse'.
    *   The `NOT IN` operator ensures that any facility present in this list is strictly excluded from the final result.

This combination (`A AND NOT B`) effectively isolates the target dataset.

## 3. Testing and Validation Strategy

To ensure the function operates correctly, a specific set of test data was created to cover "edge cases" (boundary conditions), and a PL/SQL anonymous block was used to execute and verify the results.

The strategy covers four specific scenarios:
*   **Positive Scenario (Simple):** A facility with only a silo.
*   **Positive Scenario (Complex):** A facility with a silo and another infrastructure (not a warehouse).
*   **Negative Scenario (Boundary):** A facility with *both* a silo and a warehouse (must be excluded).
*   **Negative Scenario (Simple):** A facility with only a warehouse.

### 3.1. Test Data Setup

Specific `INSERT` statements were executed to create these conditions:

*   **Case 1 (Positive):** `Agro Terminal Norte` (ID 70). Has a grain silo. **Expected:** In output.
*   **Case 2 (Positive):** `Porto Frio de Aveiro` (ID 71). Has a grain silo and a refrigerated area. **Expected:** In output.
*   **Case 3 (Negative/Crucial):** `Terminal de Bobadela` (ID 60). Has a grain silo AND a warehouse. **Expected:** Excluded (due to the warehouse).
*   **Case 4 (Negative):** `Armazém Central` (ID 62). Has only a warehouse. **Expected:** Excluded.

### 3.2. Test Execution

The following anonymous block calls the function and prints the results to the console.

**Execution Code:**

```sql
SET SERVEROUTPUT ON;

DECLARE
    l_facility_cursor    SYS_REFCURSOR;
    l_facility_id        Facility.facility_id%TYPE;
    l_facility_name      Facility.facility_name%TYPE;
    l_records_found      BOOLEAN := FALSE;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- Testing USBD30: Facilities with Grain Silos ONLY (No Warehouses) ---');

    -- Call the function
    l_facility_cursor := GetFacilitiesWithGrainSilosNoWarehouses();

    LOOP
        FETCH l_facility_cursor INTO l_facility_id, l_facility_name;
        EXIT WHEN l_facility_cursor%NOTFOUND;
        
        l_records_found := TRUE;
        
        DBMS_OUTPUT.PUT_LINE(
            '  -> ID: ' || l_facility_id || 
            ' | Name: ' || l_facility_name
        );
    END LOOP;

    IF NOT l_records_found THEN
        DBMS_OUTPUT.PUT_LINE('No facilities found matching the criteria.');
    END IF;

    CLOSE l_facility_cursor;
    DBMS_OUTPUT.PUT_LINE('--- Test completed ---');
END;
/
```

**Expected Result:**
The output must list:
1.  **ID 70** (Agro Terminal Norte)
2.  **ID 71** (Porto Frio de Aveiro)
3.  (And any existing facilities from the original dataset that meet the criteria, such as 'Lousado' or 'Tamel').

Critically, **ID 60** (Terminal de Bobadela) must **NOT** appear in the list, proving that the `NOT IN` logic is working correctly.

## 4. Conclusion

The function `GetFacilitiesWithGrainSilosNoWarehouses` is correctly implemented. It successfully uses `IN` and `NOT IN` subqueries to filter complex many-to-many relationships. The testing strategy confirms that the function correctly includes valid facilities and strictly excludes those that violate the "no warehouse" rule, even if they possess a grain silo.