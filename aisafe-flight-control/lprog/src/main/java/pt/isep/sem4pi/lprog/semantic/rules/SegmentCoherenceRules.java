package pt.isep.sem4pi.lprog.semantic.rules;

import pt.isep.sem4pi.lprog.error.DSLError;
import pt.isep.sem4pi.lprog.model.FlightLeg;
import pt.isep.sem4pi.lprog.model.FlightPlan;
import pt.isep.sem4pi.lprog.model.FlightSegment;

import java.util.ArrayList;
import java.util.List;

/**
 * Segment coherence rules (section 3.4.5 — Segment coherence):
 *
 *  R12 — Segment start and end coordinates must be different.
 *
 *  R13 — Altitude values must be positive (start and end coordinates).
 *
 *  R14 — Wind speed must be non-negative.
 *
 * Note on "segments must belong to exactly one leg" (R15 in the spec):
 *   This is structurally guaranteed by the domain model — each FlightSegment
 *   is added to exactly one FlightLeg during visitor construction and is
 *   never shared. No runtime check is needed; this is documented here for
 *   traceability.
 */
public class SegmentCoherenceRules implements SemanticRule {

    @Override
    public List<DSLError> validate(FlightPlan plan) {
        List<DSLError> errors = new ArrayList<>();
        List<FlightLeg> legs = plan.getLegs();

        if (legs == null) return errors;

        for (int i = 0; i < legs.size(); i++) {
            FlightLeg leg = legs.get(i);
            int legNum = i + 1;

            if (leg.getSegments() == null) continue;

            for (int j = 0; j < leg.getSegments().size(); j++) {
                FlightSegment seg = leg.getSegments().get(j);
                int segNum = j + 1;
                String ctx = "Leg " + legNum + ", Segmento " + segNum + ": ";

                // R12 — start != end
                if (seg.getStart() != null && seg.getEnd() != null
                        && seg.getStart().equalsCoord(seg.getEnd())) {
                    errors.add(build(seg.getLine(), 0,
                            ctx + "as coordenadas de início e fim não " +
                                    "podem ser iguais."));
                }

                // R13 — altitudes must be positive
                if (seg.getStart() != null && seg.getStart().getAltitude() <= 0) {
                    errors.add(build(seg.getLine(), 0,
                            ctx + "a altitude da coordenada de início deve " +
                                    "ser positiva (valor: " +
                                    seg.getStart().getAltitude() + ")."));
                }
                if (seg.getEnd() != null && seg.getEnd().getAltitude() <= 0) {
                    errors.add(build(seg.getLine(), 0,
                            ctx + "a altitude da coordenada de fim deve " +
                                    "ser positiva (valor: " +
                                    seg.getEnd().getAltitude() + ")."));
                }

                // R14 — wind speed non-negative (0 is valid — no wind)
                if (seg.getWindSpeed() < 0) {
                    errors.add(build(seg.getLine(), 0,
                            ctx + "a velocidade do vento não pode ser " +
                                    "negativa (valor: " + seg.getWindSpeed() + ")."));
                }
            }
        }

        return errors;
    }

    private DSLError build(int line, int col, String msg) {
        return new DSLError(DSLError.ErrorType.SEMANTIC, line, col, msg);
    }
}