
SET SERVEROUTPUT ON;

DECLARE
    l_facility_cursor    SYS_REFCURSOR;
    l_facility_id        Facility.facility_id%TYPE;
    l_facility_name      Facility.facility_name%TYPE;
    l_records_found      BOOLEAN := FALSE;
BEGIN
    DBMS_OUTPUT.PUT_LINE('--- Testing USBD30: Facilities with Grain Silos ONLY (No Warehouses) ---');

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
