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
            f.facility_id IN (
                SELECT fi.facility_id
                FROM FacilityInfrastructure fi
                JOIN Infrastructure i ON fi.infrastructure_id = i.infrastructure_id
                WHERE i.infrastructure_name = 'grain silo'
            )
            AND
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