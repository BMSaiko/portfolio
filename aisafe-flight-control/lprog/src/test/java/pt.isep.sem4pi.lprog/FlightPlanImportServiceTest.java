package pt.isep.sem4pi.lprog;

import org.junit.jupiter.api.Test;
import pt.isep.sem4pi.lprog.application.FlightPlanImportService;
import pt.isep.sem4pi.lprog.application.ImportResult;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FlightPlanImportServiceTest {

    @Test
    void shouldImportValidFlightPlan() {

        FlightPlanImportService service = new FlightPlanImportService();

        ImportResult result = service.importFlightPlan(Path.of("src/main/resources/examples/valid_opo_mad.dsl"));

        assertTrue(result.isSuccess());
        assertTrue(result.getErrors().isEmpty());
        assertFalse(result.getFlightPlans().isEmpty());

        var flightPlan = result.getFlightPlans().get(0);

        assertEquals("AA123", flightPlan.getId());
        assertEquals("regular", flightPlan.getType());
        assertFalse(flightPlan.getLegs().isEmpty());
    }

    @Test
    void shouldImportValidMultiLegFlightPlan() {

        FlightPlanImportService service = new FlightPlanImportService();

        ImportResult result = service.importFlightPlan(Path.of("src/main/resources/examples/valid_lis_fco_vit.dsl"));

        assertTrue(result.isSuccess());
        assertTrue(result.getErrors().isEmpty());
        assertFalse(result.getFlightPlans().isEmpty());
        assertTrue(result.getFlightPlans().get(0).getLegs().size() >= 2);
    }

    @Test
    void shouldRejectInvalidMissingFuelFlightPlan() {

        FlightPlanImportService service = new FlightPlanImportService();

        ImportResult result = service.importFlightPlan(Path.of("src/main/resources/examples/invalid_missing_fuel.dsl"));

        assertFalse(result.isSuccess());
        assertFalse(result.getErrors().isEmpty());
        assertTrue(result.getFlightPlans().isEmpty());
    }

    @Test
    void shouldRejectInvalidSemanticFlightPlan() {

        FlightPlanImportService service = new FlightPlanImportService();

        ImportResult result = service.importFlightPlan(Path.of("src/main/resources/examples/invalid_semantic_errors.dsl"));

        assertFalse(result.isSuccess());
        assertFalse(result.getErrors().isEmpty());
        assertTrue(result.getFlightPlans().isEmpty());
    }
}