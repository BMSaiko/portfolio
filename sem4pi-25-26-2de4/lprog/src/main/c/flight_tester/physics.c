/* ─────────────────────────────────────────────────────────────────────────────
 * physics.c
 *
 * Physical coherence checks for flight plan validation.
 * See physics.h for the scope and full list of checks.
 * ───────────────────────────────────────────────────────────────────────────── */

#include "physics.h"

#include <stdio.h>
#include <string.h>
#include <math.h>

/* ── Internal helpers ─────────────────────────────────────────────────────── */

/* Append an issue to the output, respecting the MAX_ISSUES cap */
static void add_issue(ValidationOutput *out,
                      const char *field,
                      const char *message) {
    if (out->issueCount >= MAX_ISSUES) return;
    Issue *issue = &out->issues[out->issueCount++];
    strncpy(issue->field,   field,   sizeof(issue->field)   - 1);
    strncpy(issue->message, message, sizeof(issue->message) - 1);
    issue->field[sizeof(issue->field) - 1]     = '\0';
    issue->message[sizeof(issue->message) - 1] = '\0';
    out->passed = 0;
}

/* ── Fuel check ───────────────────────────────────────────────────────────── */

void check_fuel(const Leg *leg, int legIndex, ValidationOutput *out) {
    char field[MAX_STRING_LEN];
    char message[512];

    snprintf(field, sizeof(field), "leg[%d].fuel", legIndex);

    if (leg->fuelKg <= 0.0) {
        snprintf(message, sizeof(message),
                 "Fuel quantity must be strictly positive, but got %.2f kg.",
                 leg->fuelKg);
        add_issue(out, field, message);
    }
}

/* ── Segment checks ───────────────────────────────────────────────────────── */

void check_segment(const Segment *seg, int legIndex, int segIndex,
                   ValidationOutput *out) {
    char field[MAX_STRING_LEN];
    char message[512];

    /* ── 1. Altitude non-negative ─────────────────────────────────────────── */
    snprintf(field, sizeof(field), "leg[%d].segment[%d].startAltM",
             legIndex, segIndex);
    if (seg->startAltM < 0.0) {
        snprintf(message, sizeof(message),
                 "Start altitude must be >= 0 m, but got %.2f m.",
                 seg->startAltM);
        add_issue(out, field, message);
    }

    snprintf(field, sizeof(field), "leg[%d].segment[%d].endAltM",
             legIndex, segIndex);
    if (seg->endAltM < 0.0) {
        snprintf(message, sizeof(message),
                 "End altitude must be >= 0 m, but got %.2f m.",
                 seg->endAltM);
        add_issue(out, field, message);
    }

    /* ── 2. Start and end coordinates are distinct ────────────────────────── */
    double dLat = fabs(seg->endLat - seg->startLat);
    double dLon = fabs(seg->endLon - seg->startLon);
    double dAlt = fabs(seg->endAltM - seg->startAltM);

    if (dLat < COORD_DISTINCT_THRESHOLD &&
        dLon < COORD_DISTINCT_THRESHOLD &&
        dAlt < COORD_DISTINCT_THRESHOLD) {
        snprintf(field, sizeof(field), "leg[%d].segment[%d].coordinates",
                 legIndex, segIndex);
        snprintf(message, sizeof(message),
                 "Segment start and end coordinates are identical "
                 "(lat=%.6f, lon=%.6f, alt=%.2f m).",
                 seg->startLat, seg->startLon, seg->startAltM);
        add_issue(out, field, message);
        /* No further mode-specific checks make sense if coords are identical */
        return;
    }

    /* ── 3. Mode-specific altitude coherence ─────────────────────────────── */
    snprintf(field, sizeof(field), "leg[%d].segment[%d].altitude",
             legIndex, segIndex);

    if (strcmp(seg->mode, "CLIMB") == 0) {
        if (seg->endAltM <= seg->startAltM) {
            snprintf(message, sizeof(message),
                     "CLIMB segment must have end altitude (%.2f m) "
                     "> start altitude (%.2f m).",
                     seg->endAltM, seg->startAltM);
            add_issue(out, field, message);
        }
    } else if (strcmp(seg->mode, "DESCEND") == 0) {
        if (seg->endAltM >= seg->startAltM) {
            snprintf(message, sizeof(message),
                     "DESCEND segment must have end altitude (%.2f m) "
                     "< start altitude (%.2f m).",
                     seg->endAltM, seg->startAltM);
            add_issue(out, field, message);
        }
    } else if (strcmp(seg->mode, "CRUISE") == 0) {
        double altDiff = fabs(seg->endAltM - seg->startAltM);
        if (altDiff > CRUISE_ALT_TOLERANCE_M) {
            snprintf(message, sizeof(message),
                     "CRUISE segment altitude change (%.2f m) exceeds "
                     "tolerance of %.0f m.",
                     altDiff, CRUISE_ALT_TOLERANCE_M);
            add_issue(out, field, message);
        }
    } else {
        /* Unknown mode — report it but do not block */
        snprintf(field, sizeof(field), "leg[%d].segment[%d].mode",
                 legIndex, segIndex);
        snprintf(message, sizeof(message),
                 "Unknown segment mode \"%s\"; expected CLIMB, CRUISE or DESCEND.",
                 seg->mode);
        add_issue(out, field, message);
    }
}

/* ── Top-level validator ──────────────────────────────────────────────────── */

void validate_flight(const FlightInput *input, ValidationOutput *out) {
    out->passed     = 1;   /* optimistic — set to 0 by add_issue on first error */
    out->issueCount = 0;

    for (int l = 0; l < input->legCount; l++) {
        const Leg *leg = &input->legs[l];

        check_fuel(leg, l, out);

        for (int s = 0; s < leg->segmentCount; s++) {
            check_segment(&leg->segments[s], l, s, out);
        }
    }
}