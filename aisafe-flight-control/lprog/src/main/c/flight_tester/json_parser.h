#ifndef JSON_PARSER_H
#define JSON_PARSER_H

/* ─────────────────────────────────────────────────────────────────────────────
 * json_parser.h
 *
 * Minimal, self-contained JSON reader and writer for the flight_tester
 * component.  Only the subset of JSON needed for the Java↔C protocol is
 * supported:
 *
 *   Input  (Java → C):  object with arrays and nested objects, number/string
 *                        leaf values.
 *   Output (C → Java):  { "passed": bool, "issues": [ { "field": str,
 *                          "message": str }, ... ] }
 *
 * Design goals
 * ─────────────────────────────────────────────────────────────────────────────
 *  • No dynamic allocation beyond a single read buffer and result arrays.
 *  • Predictable, bounded memory usage suitable for an exam/project context.
 *  • Clear error reporting via a global parse_error string.
 * ───────────────────────────────────────────────────────────────────────────── */

#include <stddef.h>

/* ── Constants ────────────────────────────────────────────────────────────── */

#define MAX_STRING_LEN   256
#define MAX_SEGMENTS     64
#define MAX_LEGS         16
#define MAX_ISSUES       128
#define MAX_INPUT_BYTES  65536   /* 64 KiB — more than enough for any flight plan */

/* ── Data structures (mirrors the JSON protocol) ─────────────────────────── */

typedef struct {
    char   mode[MAX_STRING_LEN];  /* CLIMB | CRUISE | DESCEND */
    int    index;
    double startLat;
    double startLon;
    double startAltM;
    double endLat;
    double endLon;
    double endAltM;
} Segment;

typedef struct {
    int      index;
    char     departureAirport[MAX_STRING_LEN];
    char     arrivalAirport[MAX_STRING_LEN];
    double   fuelKg;
    Segment  segments[MAX_SEGMENTS];
    int      segmentCount;
} Leg;

typedef struct {
    char  flightId[MAX_STRING_LEN];
    Leg   legs[MAX_LEGS];
    int   legCount;
} FlightInput;

typedef struct {
    char field[MAX_STRING_LEN];
    char message[512];
} Issue;

typedef struct {
    int   passed;               /* 1 = passed, 0 = failed */
    Issue issues[MAX_ISSUES];
    int   issueCount;
} ValidationOutput;

/* ── Global error buffer (set when parse_flight_input returns NULL) ───────── */

extern char parse_error[512];

/* ── Public API ───────────────────────────────────────────────────────────── */

/**
 * Reads all bytes from stdin into a newly-allocated buffer.
 * Caller must free() the returned pointer.
 * Returns NULL on memory or read failure.
 */
char *read_stdin(void);

/**
 * Parses the JSON string produced by FlightPlanTestRunner (Java).
 * Populates *out and returns 1 on success.
 * On failure sets parse_error and returns 0.
 */
int parse_flight_input(const char *json, FlightInput *out);

/**
 * Serialises a ValidationOutput to a JSON string written to stdout.
 * Format: { "passed": <bool>, "issues": [ ... ] }
 */
void write_output(const ValidationOutput *out);

#endif /* JSON_PARSER_H */