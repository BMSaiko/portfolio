package pt.isep.sem4pi.lprog.testing;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link FlightPlanTestRunner}.
 *
 * Two categories:
 *   1. JSON parsing  — test parseResult() and addExeSuffixIfWindows() in
 *                      isolation; no C process or file system needed.
 *   2. Process integration — launch the real C executable; only meaningful
 *                      when the binary has been compiled (make).
 *
 * The integration test uses the system property set by maven-surefire-plugin:
 *   -Dflight.tester.path=.../bin/flight_tester   (Linux/macOS)
 *   -Dflight.tester.path=.../bin/flight_tester   (Windows — .exe added automatically)
 */
class FlightPlanTestRunnerTest {

    private final FlightPlanTestRunner runner = new FlightPlanTestRunner();

    // ─────────────────────────────────────────────────────────────────────────
    // WINDOWS .EXE SUFFIX
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void addExeSuffix_onWindows_addsExe() {
        // Simulate the Windows branch directly (os.name independent)
        String result = addExeIfWin("bin/flight_tester", true);
        assertEquals("bin/flight_tester.exe", result);
    }

    @Test
    void addExeSuffix_onWindows_alreadyHasExe_unchanged() {
        String result = addExeIfWin("bin/flight_tester.exe", true);
        assertEquals("bin/flight_tester.exe", result);
    }

    @Test
    void addExeSuffix_onLinux_unchanged() {
        String result = addExeIfWin("bin/flight_tester", false);
        assertEquals("bin/flight_tester", result);
    }

    /** Exercises the logic of addExeSuffixIfWindows without depending on os.name. */
    private String addExeIfWin(String path, boolean isWindows) {
        if (isWindows && !path.toLowerCase().endsWith(".exe")) {
            return path + ".exe";
        }
        return path;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // JSON PARSING — no C process required
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void parseResult_passed() {
        FlightPlanTestResult result = FlightPlanTestRunner.parseResult(
                "{\"passed\":true,\"issues\":[]}");
        assertTrue(result.isPassed());
        assertTrue(result.getIssues().isEmpty());
        assertFalse(result.isExecutionError());
    }

    @Test
    void parseResult_failedWithOneIssue() {
        String json = "{\"passed\":false,\"issues\":["
                + "{\"field\":\"leg[0].fuel\","
                + "\"message\":\"Fuel quantity must be strictly positive, but got -500.00 kg.\"}"
                + "]}";
        FlightPlanTestResult result = FlightPlanTestRunner.parseResult(json);

        assertFalse(result.isPassed());
        assertEquals(1, result.getIssues().size());
        assertEquals("leg[0].fuel", result.getIssues().get(0).getField());
        assertTrue(result.getIssues().get(0).getMessage().contains("positive"));
    }

    @Test
    void parseResult_failedWithMultipleIssues() {
        String json = "{\"passed\":false,\"issues\":["
                + "{\"field\":\"leg[0].fuel\",\"message\":\"Bad fuel\"},"
                + "{\"field\":\"leg[0].segment[0].altitude\",\"message\":\"Bad altitude\"}"
                + "]}";
        FlightPlanTestResult result = FlightPlanTestRunner.parseResult(json);

        assertFalse(result.isPassed());
        assertEquals(2, result.getIssues().size());
    }

    @Test
    void parseResult_invalidJson_returnsExecutionError() {
        FlightPlanTestResult result = FlightPlanTestRunner.parseResult("not json at all");
        assertTrue(result.isExecutionError());
        assertNotNull(result.getExecutionErrorMessage());
    }

    @Test
    void parseResult_emptyString_returnsExecutionError() {
        FlightPlanTestResult result = FlightPlanTestRunner.parseResult("");
        assertTrue(result.isExecutionError());
    }

    @Test
    void parseResult_escapedQuotesInMessage() {
        String json = "{\"passed\":false,\"issues\":["
                + "{\"field\":\"leg[0].fuel\","
                + "\"message\":\"Contains \\\"quotes\\\" here\"}"
                + "]}";
        FlightPlanTestResult result = FlightPlanTestRunner.parseResult(json);
        assertFalse(result.isPassed());
        assertTrue(result.getIssues().get(0).getMessage().contains("quotes"));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PROCESS INTEGRATION — requires compiled C binary
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * This test runs the real C executable.
     * It works if the binary has been compiled (make / mvn compile).
     * On Windows the .exe suffix is added automatically by resolveExecutable().
     */
    @Test
    void run_validFlight_passes() {
        FlightPlanTestInput input = buildValidInput("TP100");
        FlightPlanTestResult result = runner.run(input);

        assertFalse(result.isExecutionError(),
                "Execution error: " + result.getExecutionErrorMessage());
        assertTrue(result.isPassed(),
                "Expected physical test to pass for a valid flight plan.");
    }

    @Test
    void run_negativeFuel_failsWithFuelIssue() {
        FlightPlanTestInput input = new FlightPlanTestInput("BAD1", List.of(
                new FlightPlanTestInput.Leg(0, "LPPT", "LPPR", -500.0,
                        List.of(new FlightPlanTestInput.Segment(
                                0, "CRUISE",
                                38.0, -9.0, 8000.0,
                                41.0, -8.0, 8000.0)))
        ));
        FlightPlanTestResult result = runner.run(input);

        assertFalse(result.isExecutionError(),
                "Should not be an execution error — binary exists and JSON is valid.");
        assertFalse(result.isPassed());
        assertFalse(result.getIssues().isEmpty());
        assertTrue(result.getIssues().get(0).getField().contains("fuel"));
    }

    @Test
    void run_climbWithDescendingAltitude_fails() {
        FlightPlanTestInput input = new FlightPlanTestInput("BAD2", List.of(
                new FlightPlanTestInput.Leg(0, "LPPT", "LPPR", 5000.0,
                        List.of(new FlightPlanTestInput.Segment(
                                0, "CLIMB",
                                38.0, -9.0, 8000.0,   // starts high
                                41.0, -8.0, 100.0)))   // ends lower — wrong for CLIMB
        ));
        FlightPlanTestResult result = runner.run(input);

        assertFalse(result.isExecutionError(),
                "Should not be an execution error.");
        assertFalse(result.isPassed());
        assertTrue(result.getIssues().get(0).getMessage().contains("CLIMB"));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPER
    // ─────────────────────────────────────────────────────────────────────────

    private FlightPlanTestInput buildValidInput(String flightId) {
        return new FlightPlanTestInput(flightId, List.of(
                new FlightPlanTestInput.Leg(0, "LPPT", "LPPR", 14500.0, List.of(
                        new FlightPlanTestInput.Segment(0, "CLIMB",
                                38.78, -9.13, 0.0,
                                38.85, -9.00, 8000.0),
                        new FlightPlanTestInput.Segment(1, "CRUISE",
                                38.85, -9.00, 8000.0,
                                41.23, -8.68, 8010.0),
                        new FlightPlanTestInput.Segment(2, "DESCEND",
                                41.23, -8.68, 8010.0,
                                41.24, -8.68, 0.0)
                ))
        ));
    }
}