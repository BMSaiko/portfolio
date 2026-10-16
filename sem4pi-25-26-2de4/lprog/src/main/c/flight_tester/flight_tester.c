/* ─────────────────────────────────────────────────────────────────────────────
 * flight_tester.c
 *
 * US085 — Physical test component for flight plan validation.
 *
 * Protocol
 * ────────
 *   stdin  → single JSON object (FlightInput) produced by FlightPlanTestRunner
 *   stdout → single JSON object (ValidationOutput)
 *   exit 0 → all physical checks passed
 *   exit 1 → one or more physical issues found
 *   exit 2 → input parse error (malformed JSON)
 *
 * Build
 * ─────
 *   make          (produces bin/flight_tester)
 *   make test     (runs the built-in smoke tests)
 *   make clean
 * ───────────────────────────────────────────────────────────────────────────── */

#include "json_parser.h"
#include "physics.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/* ── Smoke-test helpers (activated by: flight_tester --test) ─────────────── */

#ifdef ENABLE_TESTS

static int tests_run    = 0;
static int tests_passed = 0;

#define ASSERT(cond, name)                                      \
    do {                                                        \
        tests_run++;                                            \
        if (cond) {                                             \
            tests_passed++;                                     \
            fprintf(stderr, "  PASS  %s\n", name);             \
        } else {                                                \
            fprintf(stderr, "  FAIL  %s  (line %d)\n",         \
                    name, __LINE__);                            \
        }                                                       \
    } while (0)

/* ── Test: valid single-leg flight ────────────────────────────────────────── */
static void test_valid_flight(void) {
    const char *json =
        "{"
        "\"flightId\":\"TP100\","
        "\"legs\":["
        "  {\"index\":0,\"departureAirport\":\"LPPT\","
        "   \"arrivalAirport\":\"LPPR\",\"fuelKg\":14500,"
        "   \"segments\":["
        "     {\"index\":0,\"mode\":\"CLIMB\","
        "      \"startLat\":38.78,\"startLon\":-9.13,\"startAltM\":0,"
        "      \"endLat\":38.85,\"endLon\":-9.00,\"endAltM\":8000},"
        "     {\"index\":1,\"mode\":\"CRUISE\","
        "      \"startLat\":38.85,\"startLon\":-9.00,\"startAltM\":8000,"
        "      \"endLat\":41.23,\"endLon\":-8.68,\"endAltM\":8010},"
        "     {\"index\":2,\"mode\":\"DESCEND\","
        "      \"startLat\":41.23,\"startLon\":-8.68,\"startAltM\":8010,"
        "      \"endLat\":41.24,\"endLon\":-8.68,\"endAltM\":0}"
        "   ]}"
        "]}";

    FlightInput input;
    ASSERT(parse_flight_input(json, &input) == 1, "valid_flight: parse ok");
    ASSERT(strcmp(input.flightId, "TP100") == 0,  "valid_flight: flightId");
    ASSERT(input.legCount == 1,                    "valid_flight: legCount");
    ASSERT(input.legs[0].segmentCount == 3,        "valid_flight: segmentCount");
    ASSERT(input.legs[0].fuelKg == 14500.0,        "valid_flight: fuelKg");

    ValidationOutput out;
    validate_flight(&input, &out);
    ASSERT(out.passed == 1,      "valid_flight: passed");
    ASSERT(out.issueCount == 0,  "valid_flight: no issues");
}

/* ── Test: negative fuel ──────────────────────────────────────────────────── */
static void test_negative_fuel(void) {
    const char *json =
        "{"
        "\"flightId\":\"BAD1\","
        "\"legs\":[{\"index\":0,\"departureAirport\":\"LPPT\","
        "  \"arrivalAirport\":\"LPPR\",\"fuelKg\":-100,"
        "  \"segments\":[{\"index\":0,\"mode\":\"CRUISE\","
        "    \"startLat\":38.0,\"startLon\":-9.0,\"startAltM\":8000,"
        "    \"endLat\":41.0,\"endLon\":-8.0,\"endAltM\":8000}]}]}";

    FlightInput input;
    ASSERT(parse_flight_input(json, &input) == 1, "negative_fuel: parse ok");

    ValidationOutput out;
    validate_flight(&input, &out);
    ASSERT(out.passed == 0,     "negative_fuel: failed");
    ASSERT(out.issueCount >= 1, "negative_fuel: has issues");
    ASSERT(strstr(out.issues[0].field, "fuel") != NULL, "negative_fuel: field contains 'fuel'");
}

/* ── Test: CLIMB with descending altitude ─────────────────────────────────── */
static void test_climb_wrong_direction(void) {
    const char *json =
        "{"
        "\"flightId\":\"BAD2\","
        "\"legs\":[{\"index\":0,\"departureAirport\":\"LPPT\","
        "  \"arrivalAirport\":\"LPPR\",\"fuelKg\":5000,"
        "  \"segments\":[{\"index\":0,\"mode\":\"CLIMB\","
        "    \"startLat\":38.0,\"startLon\":-9.0,\"startAltM\":8000,"
        "    \"endLat\":41.0,\"endLon\":-8.0,\"endAltM\":100}]}]}";

    FlightInput input;
    ASSERT(parse_flight_input(json, &input) == 1, "climb_wrong_dir: parse ok");

    ValidationOutput out;
    validate_flight(&input, &out);
    ASSERT(out.passed == 0,     "climb_wrong_dir: failed");
    ASSERT(out.issueCount >= 1, "climb_wrong_dir: has issues");
    ASSERT(strstr(out.issues[0].message, "CLIMB") != NULL, "climb_wrong_dir: message mentions CLIMB");
}

/* ── Test: DESCEND with ascending altitude ────────────────────────────────── */
static void test_descend_wrong_direction(void) {
    const char *json =
        "{"
        "\"flightId\":\"BAD3\","
        "\"legs\":[{\"index\":0,\"departureAirport\":\"LPPT\","
        "  \"arrivalAirport\":\"LPPR\",\"fuelKg\":5000,"
        "  \"segments\":[{\"index\":0,\"mode\":\"DESCEND\","
        "    \"startLat\":38.0,\"startLon\":-9.0,\"startAltM\":100,"
        "    \"endLat\":41.0,\"endLon\":-8.0,\"endAltM\":8000}]}]}";

    FlightInput input;
    ASSERT(parse_flight_input(json, &input) == 1, "descend_wrong_dir: parse ok");

    ValidationOutput out;
    validate_flight(&input, &out);
    ASSERT(out.passed == 0,     "descend_wrong_dir: failed");
    ASSERT(out.issueCount >= 1, "descend_wrong_dir: has issues");
    ASSERT(strstr(out.issues[0].message, "DESCEND") != NULL, "descend_wrong_dir: message mentions DESCEND");
}

/* ── Test: CRUISE with large altitude change ──────────────────────────────── */
static void test_cruise_altitude_change(void) {
    const char *json =
        "{"
        "\"flightId\":\"BAD4\","
        "\"legs\":[{\"index\":0,\"departureAirport\":\"LPPT\","
        "  \"arrivalAirport\":\"LPPR\",\"fuelKg\":5000,"
        "  \"segments\":[{\"index\":0,\"mode\":\"CRUISE\","
        "    \"startLat\":38.0,\"startLon\":-9.0,\"startAltM\":8000,"
        "    \"endLat\":41.0,\"endLon\":-8.0,\"endAltM\":9500}]}]}";

    FlightInput input;
    ASSERT(parse_flight_input(json, &input) == 1, "cruise_alt_change: parse ok");

    ValidationOutput out;
    validate_flight(&input, &out);
    ASSERT(out.passed == 0,     "cruise_alt_change: failed");
    ASSERT(out.issueCount >= 1, "cruise_alt_change: has issues");
    ASSERT(strstr(out.issues[0].message, "CRUISE") != NULL, "cruise_alt_change: message mentions CRUISE");
}

/* ── Test: identical start/end coordinates ────────────────────────────────── */
static void test_identical_coordinates(void) {
    const char *json =
        "{"
        "\"flightId\":\"BAD5\","
        "\"legs\":[{\"index\":0,\"departureAirport\":\"LPPT\","
        "  \"arrivalAirport\":\"LPPR\",\"fuelKg\":5000,"
        "  \"segments\":[{\"index\":0,\"mode\":\"CRUISE\","
        "    \"startLat\":38.0,\"startLon\":-9.0,\"startAltM\":8000,"
        "    \"endLat\":38.0,\"endLon\":-9.0,\"endAltM\":8000}]}]}";

    FlightInput input;
    ASSERT(parse_flight_input(json, &input) == 1, "identical_coords: parse ok");

    ValidationOutput out;
    validate_flight(&input, &out);
    ASSERT(out.passed == 0,     "identical_coords: failed");
    ASSERT(out.issueCount >= 1, "identical_coords: has issues");
}

/* ── Test: malformed JSON ─────────────────────────────────────────────────── */
static void test_malformed_json(void) {
    const char *json = "{ this is not valid json !!!";
    FlightInput input;
    int result = parse_flight_input(json, &input);
    ASSERT(result == 0, "malformed_json: parse fails");
    ASSERT(parse_error[0] != '\0', "malformed_json: error message set");
}

static void run_all_tests(void) {
    fprintf(stderr, "\n=== flight_tester smoke tests ===\n");
    test_valid_flight();
    test_negative_fuel();
    test_climb_wrong_direction();
    test_descend_wrong_direction();
    test_cruise_altitude_change();
    test_identical_coordinates();
    test_malformed_json();
    fprintf(stderr, "\nResults: %d / %d passed\n\n", tests_passed, tests_run);
    exit(tests_passed == tests_run ? 0 : 1);
}

#endif /* ENABLE_TESTS */

/* ── Main ─────────────────────────────────────────────────────────────────── */

int main(int argc, char *argv[]) {

#ifdef ENABLE_TESTS
    if (argc == 2 && strcmp(argv[1], "--test") == 0) {
        run_all_tests();
    }
#else
    (void)argc; (void)argv;
#endif

    /* ── Step 1: Read JSON from stdin ───────────────────────────────────── */
    char *json_in = read_stdin();
    if (!json_in) {
        fprintf(stderr, "flight_tester: failed to read stdin: %s\n", parse_error);
        exit(2);
    }

    /* ── Step 2: Parse the flight input ─────────────────────────────────── */
    FlightInput input;
    if (!parse_flight_input(json_in, &input)) {
        fprintf(stderr, "flight_tester: JSON parse error: %s\n", parse_error);
        free(json_in);
        exit(2);
    }
    free(json_in);

    /* ── Step 3: Run physical validation ─────────────────────────────────── */
    ValidationOutput out;
    validate_flight(&input, &out);

    /* ── Step 4: Write result to stdout ──────────────────────────────────── */
    write_output(&out);

    return out.passed ? 0 : 1;
}