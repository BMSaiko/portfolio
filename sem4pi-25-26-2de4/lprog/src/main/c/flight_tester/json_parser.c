/* ─────────────────────────────────────────────────────────────────────────────
 * json_parser.c
 *
 * Minimal JSON reader/writer for the flight_tester component.
 * See json_parser.h for the full design notes.
 * ───────────────────────────────────────────────────────────────────────────── */

#include "json_parser.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

/* Global error buffer */
char parse_error[512] = {0};

/* ── Internal helpers ─────────────────────────────────────────────────────── */

/* Skip whitespace, return pointer to first non-space character */
static const char *skip_ws(const char *p) {
    while (*p && isspace((unsigned char)*p)) p++;
    return p;
}

/* Expect a literal character; advance pointer or set error */
static const char *expect_char(const char *p, char c) {
    p = skip_ws(p);
    if (*p != c) {
        snprintf(parse_error, sizeof(parse_error),
                 "Expected '%c' but found '%c' (context: \"%.20s\")", c, *p, p);
        return NULL;
    }
    return p + 1;
}

/* Parse a JSON string into buf (max len).  Returns pointer after closing '"'. */
static const char *parse_string(const char *p, char *buf, size_t len) {
    p = skip_ws(p);
    if (*p != '"') {
        snprintf(parse_error, sizeof(parse_error),
                 "Expected '\"' for string, found '%c'", *p);
        return NULL;
    }
    p++; /* skip opening quote */
    size_t i = 0;
    while (*p && *p != '"') {
        if (*p == '\\') {
            p++; /* skip escape char — we only need basic strings */
        }
        if (i < len - 1) buf[i++] = *p;
        p++;
    }
    buf[i] = '\0';
    if (*p != '"') {
        snprintf(parse_error, sizeof(parse_error), "Unterminated string");
        return NULL;
    }
    return p + 1; /* skip closing quote */
}

/* Parse a JSON number (integer or float) into *val. */
static const char *parse_number(const char *p, double *val) {
    p = skip_ws(p);
    char *end;
    *val = strtod(p, &end);
    if (end == p) {
        snprintf(parse_error, sizeof(parse_error),
                 "Expected number at \"%.20s\"", p);
        return NULL;
    }
    return end;
}

/* Parse a JSON integer into *val. */
static const char *parse_int(const char *p, int *val) {
    p = skip_ws(p);
    char *end;
    long lv = strtol(p, &end, 10);
    if (end == p) {
        snprintf(parse_error, sizeof(parse_error),
                 "Expected integer at \"%.20s\"", p);
        return NULL;
    }
    *val = (int)lv;
    return end;
}

/* Skip a JSON value (string, number, true/false/null, object, array) */
static const char *skip_value(const char *p);

static const char *skip_object(const char *p) {
    p = expect_char(p, '{');
    if (!p) return NULL;
    p = skip_ws(p);
    if (*p == '}') return p + 1;
    while (*p) {
        char dummy[MAX_STRING_LEN];
        p = parse_string(p, dummy, sizeof(dummy));
        if (!p) return NULL;
        p = expect_char(p, ':');
        if (!p) return NULL;
        p = skip_value(p);
        if (!p) return NULL;
        p = skip_ws(p);
        if (*p == '}') return p + 1;
        p = expect_char(p, ',');
        if (!p) return NULL;
        p = skip_ws(p);
    }
    snprintf(parse_error, sizeof(parse_error), "Unterminated object");
    return NULL;
}

static const char *skip_array(const char *p) {
    p = expect_char(p, '[');
    if (!p) return NULL;
    p = skip_ws(p);
    if (*p == ']') return p + 1;
    while (*p) {
        p = skip_value(p);
        if (!p) return NULL;
        p = skip_ws(p);
        if (*p == ']') return p + 1;
        p = expect_char(p, ',');
        if (!p) return NULL;
        p = skip_ws(p);
    }
    snprintf(parse_error, sizeof(parse_error), "Unterminated array");
    return NULL;
}

static const char *skip_value(const char *p) {
    p = skip_ws(p);
    if (*p == '"') {
        char dummy[MAX_STRING_LEN];
        return parse_string(p, dummy, sizeof(dummy));
    }
    if (*p == '{') return skip_object(p);
    if (*p == '[') return skip_array(p);
    if (strncmp(p, "true",  4) == 0) return p + 4;
    if (strncmp(p, "false", 5) == 0) return p + 5;
    if (strncmp(p, "null",  4) == 0) return p + 4;
    /* number */
    double dummy;
    return parse_number(p, &dummy);
}

/* ── Segment parser ───────────────────────────────────────────────────────── */

static const char *parse_segment(const char *p, Segment *seg) {
    p = expect_char(p, '{'); if (!p) return NULL;
    p = skip_ws(p);

    while (p && *p != '}') {
        char key[MAX_STRING_LEN];
        p = parse_string(p, key, sizeof(key)); if (!p) return NULL;
        p = expect_char(p, ':');               if (!p) return NULL;
        p = skip_ws(p);

        if      (strcmp(key, "mode")       == 0) p = parse_string(p, seg->mode, sizeof(seg->mode));
        else if (strcmp(key, "index")      == 0) p = parse_int(p, &seg->index);
        else if (strcmp(key, "startLat")   == 0) p = parse_number(p, &seg->startLat);
        else if (strcmp(key, "startLon")   == 0) p = parse_number(p, &seg->startLon);
        else if (strcmp(key, "startAltM")  == 0) p = parse_number(p, &seg->startAltM);
        else if (strcmp(key, "endLat")     == 0) p = parse_number(p, &seg->endLat);
        else if (strcmp(key, "endLon")     == 0) p = parse_number(p, &seg->endLon);
        else if (strcmp(key, "endAltM")    == 0) p = parse_number(p, &seg->endAltM);
        else                                     p = skip_value(p);

        if (!p) return NULL;
        p = skip_ws(p);
        if (*p == ',') { p++; p = skip_ws(p); }
    }
    return expect_char(p, '}');
}

/* ── Leg parser ───────────────────────────────────────────────────────────── */

static const char *parse_leg(const char *p, Leg *leg) {
    p = expect_char(p, '{'); if (!p) return NULL;
    p = skip_ws(p);

    while (p && *p != '}') {
        char key[MAX_STRING_LEN];
        p = parse_string(p, key, sizeof(key)); if (!p) return NULL;
        p = expect_char(p, ':');               if (!p) return NULL;
        p = skip_ws(p);

        if (strcmp(key, "index") == 0) {
            p = parse_int(p, &leg->index);
        } else if (strcmp(key, "departureAirport") == 0) {
            p = parse_string(p, leg->departureAirport, sizeof(leg->departureAirport));
        } else if (strcmp(key, "arrivalAirport") == 0) {
            p = parse_string(p, leg->arrivalAirport, sizeof(leg->arrivalAirport));
        } else if (strcmp(key, "fuelKg") == 0) {
            p = parse_number(p, &leg->fuelKg);
        } else if (strcmp(key, "segments") == 0) {
            /* Parse array of segment objects */
            p = expect_char(p, '['); if (!p) return NULL;
            p = skip_ws(p);
            while (p && *p != ']') {
                if (leg->segmentCount >= MAX_SEGMENTS) {
                    snprintf(parse_error, sizeof(parse_error),
                             "Too many segments in leg %d (max %d)", leg->index, MAX_SEGMENTS);
                    return NULL;
                }
                p = parse_segment(p, &leg->segments[leg->segmentCount++]);
                if (!p) return NULL;
                p = skip_ws(p);
                if (*p == ',') { p++; p = skip_ws(p); }
            }
            p = expect_char(p, ']');
        } else {
            p = skip_value(p);
        }

        if (!p) return NULL;
        p = skip_ws(p);
        if (*p == ',') { p++; p = skip_ws(p); }
    }
    return expect_char(p, '}');
}

/* ── Public API ───────────────────────────────────────────────────────────── */

char *read_stdin(void) {
    char *buf = malloc(MAX_INPUT_BYTES);
    if (!buf) return NULL;

    size_t total = 0;
    int c;
    while ((c = getchar()) != EOF) {
        if (total >= MAX_INPUT_BYTES - 1) {
            free(buf);
            snprintf(parse_error, sizeof(parse_error),
                     "Input exceeds maximum size of %d bytes", MAX_INPUT_BYTES);
            return NULL;
        }
        buf[total++] = (char)c;
    }
    buf[total] = '\0';
    return buf;
}

int parse_flight_input(const char *json, FlightInput *out) {
    memset(out, 0, sizeof(*out));
    parse_error[0] = '\0';

    const char *p = skip_ws(json);
    p = expect_char(p, '{'); if (!p) return 0;
    p = skip_ws(p);

    while (p && *p != '}') {
        char key[MAX_STRING_LEN];
        p = parse_string(p, key, sizeof(key)); if (!p) return 0;
        p = expect_char(p, ':');               if (!p) return 0;
        p = skip_ws(p);

        if (strcmp(key, "flightId") == 0) {
            p = parse_string(p, out->flightId, sizeof(out->flightId));
        } else if (strcmp(key, "legs") == 0) {
            p = expect_char(p, '['); if (!p) return 0;
            p = skip_ws(p);
            while (p && *p != ']') {
                if (out->legCount >= MAX_LEGS) {
                    snprintf(parse_error, sizeof(parse_error),
                             "Too many legs (max %d)", MAX_LEGS);
                    return 0;
                }
                p = parse_leg(p, &out->legs[out->legCount++]);
                if (!p) return 0;
                p = skip_ws(p);
                if (*p == ',') { p++; p = skip_ws(p); }
            }
            p = expect_char(p, ']');
        } else {
            p = skip_value(p);
        }

        if (!p) return 0;
        p = skip_ws(p);
        if (*p == ',') { p++; p = skip_ws(p); }
    }

    if (!p) return 0;
    return 1;
}

/* ── Output serialiser ────────────────────────────────────────────────────── */

/* Escape a string for safe JSON output */
static void print_json_string(const char *s) {
    putchar('"');
    while (*s) {
        switch (*s) {
            case '"':  printf("\\\""); break;
            case '\\': printf("\\\\"); break;
            case '\n': printf("\\n");  break;
            case '\r': printf("\\r");  break;
            case '\t': printf("\\t");  break;
            default:   putchar(*s);    break;
        }
        s++;
    }
    putchar('"');
}

void write_output(const ValidationOutput *out) {
    printf("{\"passed\":%s,\"issues\":[", out->passed ? "true" : "false");
    for (int i = 0; i < out->issueCount; i++) {
        if (i > 0) putchar(',');
        printf("{\"field\":");
        print_json_string(out->issues[i].field);
        printf(",\"message\":");
        print_json_string(out->issues[i].message);
        putchar('}');
    }
    printf("]}\n");
    fflush(stdout);
}