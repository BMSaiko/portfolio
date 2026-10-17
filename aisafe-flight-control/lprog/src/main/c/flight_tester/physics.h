#ifndef PHYSICS_H
#define PHYSICS_H

/* ─────────────────────────────────────────────────────────────────────────────
 * physics.h
 *
 * Physical coherence checks for flight plan validation (US085).
 *
 * Scope (per §3.4.6 of the project requirements):
 *   • Fuel consumption estimation and Breguet range calculations are OUT of
 *     scope.  The checks here are simplified physical coherence rules only.
 *
 * Checks performed:
 *   • Fuel quantity is strictly positive.
 *   • All altitude values are non-negative.
 *   • Segment start and end coordinates are distinct.
 *   • CLIMB  segments: end altitude > start altitude.
 *   • DESCEND segments: end altitude < start altitude.
 *   • CRUISE  segments: |end altitude − start altitude| ≤ CRUISE_ALT_TOLERANCE_M.
 * ───────────────────────────────────────────────────────────────────────────── */

#include "json_parser.h"

/* Cruise segments may vary by at most this many metres in altitude */
#define CRUISE_ALT_TOLERANCE_M 50.0

/* Minimum separation (in degrees) between start and end coordinates to be
 * considered "distinct" — approximately 11 m at the equator */
#define COORD_DISTINCT_THRESHOLD 0.0001

/**
 * Validates all legs of the flight input.
 * Issues are appended to out->issues; out->passed is set accordingly.
 */
void validate_flight(const FlightInput *input, ValidationOutput *out);

/* ── Individual check functions (exposed for unit testing) ───────────────── */

void check_fuel(const Leg *leg, int legIndex, ValidationOutput *out);
void check_segment(const Segment *seg, int legIndex, int segIndex, ValidationOutput *out);

#endif /* PHYSICS_H */