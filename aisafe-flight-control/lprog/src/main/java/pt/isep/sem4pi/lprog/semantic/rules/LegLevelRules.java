package pt.isep.sem4pi.lprog.semantic.rules;

import pt.isep.sem4pi.lprog.error.DSLError;
import pt.isep.sem4pi.lprog.model.FlightLeg;
import pt.isep.sem4pi.lprog.model.FlightPlan;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Leg-level semantic rules (section 3.4.5 — Leg-level rules):
 *
 *  R3 — Each leg must contain exactly one departure and one arrival.
 *        Structurally guaranteed by the grammar; validated here to produce
 *        a clear domain-level error if the model is somehow incomplete.
 *
 *  R4 — Each leg must contain at least one segment.
 *        The grammar uses segmentBlock+, so this is a safety net.
 *
 *  R5 — Fuel quantity must be strictly positive.
 *
 *  R6 — Departure time must precede arrival time within the same leg.
 *        (intra-leg time coherence — distinct from inter-leg sequencing)
 */
public class LegLevelRules implements SemanticRule {

    @Override
    public List<DSLError> validate(FlightPlan plan) {
        List<DSLError> errors = new ArrayList<>();

        List<FlightLeg> legs = plan.getLegs();

        if (legs == null || legs.isEmpty()) {
            errors.add(build(plan.getLine(), plan.getColumn(),
                    "O plano de voo '" + plan.getId() +
                            "' deve ter pelo menos uma Leg."));
            return errors;
        }

        for (int i = 0; i < legs.size(); i++) {
            FlightLeg leg = legs.get(i);
            int legNum = i + 1;

            // R3 — departure and arrival must be present
            if (leg.getDeparture() == null) {
                errors.add(build(plan.getLine(), 0,
                        "Leg " + legNum + ": departure em falta."));
            }
            if (leg.getArrival() == null) {
                errors.add(build(plan.getLine(), 0,
                        "Leg " + legNum + ": arrival em falta."));
            }

            // R4 — at least one segment
            if (leg.getSegments() == null || leg.getSegments().isEmpty()) {
                errors.add(build(
                        leg.getDeparture() != null
                                ? leg.getDeparture().getLine() : plan.getLine(),
                        0,
                        "Leg " + legNum + ": deve ter pelo menos um segmento."));
            }

            // R5 — fuel strictly positive
            if (leg.getFuel() != null && leg.getFuel().getQuantity() <= 0) {
                errors.add(build(leg.getFuel().getLine(),
                        leg.getFuel().getColumn(),
                        "Leg " + legNum + ": a quantidade de combustível deve " +
                                "ser estritamente positiva (valor: " +
                                leg.getFuel().getQuantity() + " " +
                                leg.getFuel().getUnit() + ")."));
            }

            // R6 — intra-leg: departure time before arrival time
            if (leg.getDeparture() != null && leg.getArrival() != null) {
                LocalDateTime depTime = leg.getDeparture().getTime();
                LocalDateTime arrTime = leg.getArrival().getTime();
                if (depTime != null && arrTime != null
                        && !depTime.isBefore(arrTime)) {
                    errors.add(build(leg.getDeparture().getLine(), 0,
                            "Leg " + legNum + ": a hora de partida (" + depTime +
                                    ") deve preceder a hora de chegada (" + arrTime + ")."));
                }
            }
        }

        return errors;
    }

    private DSLError build(int line, int col, String msg) {
        return new DSLError(DSLError.ErrorType.SEMANTIC, line, col, msg);
    }
}
