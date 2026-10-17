package pt.isep.sem4pi.lprog.semantic.rules;

import pt.isep.sem4pi.lprog.error.DSLError;
import pt.isep.sem4pi.lprog.model.FlightLeg;
import pt.isep.sem4pi.lprog.model.FlightPlan;
import pt.isep.sem4pi.lprog.model.Route;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Route coherence rules (section 3.4.5 — Route coherence):
 *
 *  R9  — Route origin must match the first leg's departure airport.
 *
 *  R10 — Route destination must match the last leg's arrival airport.
 *
 *  R11 — In a flight, the same airport cannot be visited twice.
 *         Only arrival airports are tracked after the initial departure,
 *         so a normal stopover (arrive at X, depart from X) is valid —
 *         the airport is counted only once.
 */
public class RouteCoherenceRules implements SemanticRule {

    @Override
    public List<DSLError> validate(FlightPlan plan) {
        List<DSLError> errors = new ArrayList<>();
        List<FlightLeg> legs = plan.getLegs();

        if (legs == null || legs.isEmpty()) return errors;

        FlightLeg firstLeg = legs.get(0);
        FlightLeg lastLeg  = legs.get(legs.size() - 1);
        Route route        = firstLeg.getRoute();

        // R9 — route origin == first leg departure
        if (route != null && firstLeg.getDeparture() != null) {
            String routeOrigin   = route.getOrigin();
            String firstDep      = firstLeg.getDeparture().getAirportCode();

            if (!routeOrigin.equals(firstDep)) {
                errors.add(build(route.getLine(), 0,
                        "A origem da rota (" + routeOrigin + ") deve " +
                                "coincidir com o aeroporto de partida da primeira " +
                                "Leg (" + firstDep + ")."));
            }
        }

        // R10 — route destination == last leg arrival
        Route lastRoute = lastLeg.getRoute();
        if (lastRoute != null && lastLeg.getArrival() != null) {
            String routeDest = lastRoute.getDestination();
            String lastArr   = lastLeg.getArrival().getAirportCode();

            if (!routeDest.equals(lastArr)) {
                errors.add(build(lastRoute.getLine(), 0,
                        "O destino da rota (" + routeDest + ") deve " +
                                "coincidir com o aeroporto de chegada da última " +
                                "Leg (" + lastArr + ")."));
            }
        }

        // R11 — no airport visited twice
        // Seed with the first departure; then track each arrival.
        // A stopover (arrive + depart same airport) counts as one visit.
        Set<String> visited = new HashSet<>();
        visited.add(firstLeg.getDeparture().getAirportCode());

        for (FlightLeg leg : legs) {
            if (leg.getArrival() == null) continue;
            String arr = leg.getArrival().getAirportCode();
            if (!visited.add(arr)) {
                errors.add(build(leg.getArrival().getLine(), 0,
                        "O aeroporto '" + arr + "' não pode ser visitado " +
                                "mais do que uma vez no mesmo voo."));
            }
        }

        return errors;
    }

    private DSLError build(int line, int col, String msg) {
        return new DSLError(DSLError.ErrorType.SEMANTIC, line, col, msg);
    }
}