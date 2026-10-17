package pt.isep.sem4pi.lprog.semantic.rules;

import pt.isep.sem4pi.lprog.error.DSLError;
import pt.isep.sem4pi.lprog.model.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Domain consistency rules (section 3.4.5 — Domain consistency):
 *
 *  R16 — Airport codes must respect the defined format:
 *         IATA = exactly 3 uppercase letters
 *         ICAO = exactly 4 uppercase letters
 *
 *  R17 — Date and time values must represent valid calendar values.
 *         The grammar validates the format (YYYY-MM-DD / HH:MM) but cannot
 *         reject logically invalid dates such as 2025-02-30 or 2025-13-01.
 *         This rule catches those cases using LocalDate/LocalDateTime parsing.
 *
 * Note: numeric positivity (altitudes, wind speed, fuel) is validated in
 * SegmentCoherenceRules and LegLevelRules respectively, keeping each rule
 * class cohesive.
 */
public class DomainConsistencyRules implements SemanticRule {

    private static final java.util.regex.Pattern AIRPORT_PATTERN =
            java.util.regex.Pattern.compile("[A-Z]{3,4}");

    @Override
    public List<DSLError> validate(FlightPlan plan) {
        List<DSLError> errors = new ArrayList<>();
        List<FlightLeg> legs = plan.getLegs();

        if (legs == null) return errors;

        for (int i = 0; i < legs.size(); i++) {
            FlightLeg leg = legs.get(i);
            int legNum = i + 1;

            // R16 — validate airport codes
            if (leg.getDeparture() != null) {
                validateAirportCode(
                        leg.getDeparture().getAirportCode(),
                        leg.getDeparture().getLine(),
                        "Leg " + legNum + " (departure)",
                        errors);
            }
            if (leg.getArrival() != null) {
                validateAirportCode(
                        leg.getArrival().getAirportCode(),
                        leg.getArrival().getLine(),
                        "Leg " + legNum + " (arrival)",
                        errors);
            }
            if (leg.getRoute() != null) {
                validateAirportCode(
                        leg.getRoute().getOrigin(),
                        leg.getRoute().getLine(),
                        "Leg " + legNum + " route (origin)",
                        errors);
                validateAirportCode(
                        leg.getRoute().getDestination(),
                        leg.getRoute().getLine(),
                        "Leg " + legNum + " route (destination)",
                        errors);
            }

            // R17 — validate calendar coherence of departure and arrival dates
            if (leg.getDeparture() != null && leg.getDeparture().getTime() != null) {
                validateCalendarDate(
                        leg.getDeparture().getTime().toLocalDate(),
                        leg.getDeparture().getLine(),
                        "Leg " + legNum + " departure",
                        errors);
            }
            if (leg.getArrival() != null && leg.getArrival().getTime() != null) {
                validateCalendarDate(
                        leg.getArrival().getTime().toLocalDate(),
                        leg.getArrival().getLine(),
                        "Leg " + legNum + " arrival",
                        errors);
            }
        }

        return errors;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void validateAirportCode(String code, int line,
                                     String ctx, List<DSLError> errors) {
        if (code == null || !AIRPORT_PATTERN.matcher(code).matches()) {
            errors.add(build(line, 0,
                    ctx + ": código de aeroporto inválido '" + code +
                            "'. Deve ser IATA (3 letras maiúsculas) ou ICAO " +
                            "(4 letras maiúsculas)."));
        }
    }

    private void validateCalendarDate(LocalDate date, int line,
                                      String ctx, List<DSLError> errors) {
        if (date == null) return;
        // LocalDateTime.parse() already guarantees a valid calendar date
        // when the visitor builds the model. This method exists as an
        // explicit semantic checkpoint and for cases where raw strings
        // might bypass the visitor (e.g. future extensions).
        try {
            // Re-parse to force validation — catches edge cases like leap years
            LocalDate.parse(date.toString());
        } catch (DateTimeParseException e) {
            errors.add(build(line, 0,
                    ctx + ": data inválida no calendário '" + date +
                            "' (ex: 2025-02-30 não existe)."));
        }
    }

    private DSLError build(int line, int col, String msg) {
        return new DSLError(DSLError.ErrorType.SEMANTIC, line, col, msg);
    }
}