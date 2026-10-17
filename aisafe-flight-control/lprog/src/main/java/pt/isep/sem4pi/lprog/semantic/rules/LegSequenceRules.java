package pt.isep.sem4pi.lprog.semantic.rules;

import pt.isep.sem4pi.lprog.error.DSLError;
import pt.isep.sem4pi.lprog.model.FlightLeg;
import pt.isep.sem4pi.lprog.model.FlightPlan;

import java.util.ArrayList;
import java.util.List;

/**
 * Leg sequence coherence rules (section 3.4.5 — Leg sequence coherence):
 *
 *  R7 — The arrival airport of leg N must match the departure airport of leg N+1.
 *
 *  R8 — The arrival time of leg N must precede the departure time of leg N+1.
 */
public class LegSequenceRules implements SemanticRule {

    @Override
    public List<DSLError> validate(FlightPlan plan) {
        List<DSLError> errors = new ArrayList<>();
        List<FlightLeg> legs = plan.getLegs();

        if (legs == null || legs.size() < 2) return errors;

        for (int i = 0; i < legs.size() - 1; i++) {
            FlightLeg current = legs.get(i);
            FlightLeg next    = legs.get(i + 1);
            int currentNum    = i + 1;
            int nextNum       = i + 2;

            // R7 — arrival airport of leg N == departure airport of leg N+1
            if (current.getArrival() != null && next.getDeparture() != null) {
                String arrAirport = current.getArrival().getAirportCode();
                String depAirport = next.getDeparture().getAirportCode();

                if (!arrAirport.equals(depAirport)) {
                    errors.add(build(current.getArrival().getLine(), 0,
                            "O aeroporto de chegada da Leg " + currentNum +
                                    " (" + arrAirport + ") deve coincidir com o " +
                                    "aeroporto de partida da Leg " + nextNum +
                                    " (" + depAirport + ")."));
                }
            }

            // R8 — arrival time of leg N < departure time of leg N+1
            if (current.getArrival() != null && next.getDeparture() != null) {
                var arrTime = current.getArrival().getTime();
                var depTime = next.getDeparture().getTime();

                if (arrTime != null && depTime != null
                        && !arrTime.isBefore(depTime)) {
                    errors.add(build(current.getArrival().getLine(), 0,
                            "A hora de chegada da Leg " + currentNum +
                                    " (" + arrTime + ") deve preceder a hora de " +
                                    "partida da Leg " + nextNum +
                                    " (" + depTime + ")."));
                }
            }
        }

        return errors;
    }

    private DSLError build(int line, int col, String msg) {
        return new DSLError(DSLError.ErrorType.SEMANTIC, line, col, msg);
    }
}