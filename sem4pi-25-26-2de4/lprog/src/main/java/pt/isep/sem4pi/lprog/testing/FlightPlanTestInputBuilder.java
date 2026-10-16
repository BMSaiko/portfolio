package pt.isep.sem4pi.lprog.testing;

import pt.isep.sem4pi.lprog.model.FlightPlan;
import pt.isep.sem4pi.lprog.model.FlightLeg;
import pt.isep.sem4pi.lprog.model.FlightSegment;
import pt.isep.sem4pi.lprog.model.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class FlightPlanTestInputBuilder {

    // package-private (sem modificador) → acessível nos testes do mesmo pacote
    static final double JET_A1_DENSITY_KG_PER_L = 0.804;
    static final double FT_TO_M = 1.0 / 3.28084;
    static final double KM_TO_M = 1000.0;

    public static FlightPlanTestInput from(FlightPlan plan) {
        if (plan == null) return null;

        List<FlightPlanTestInput.Leg> inputLegs = new ArrayList<>();
        List<FlightLeg> legs = plan.getLegs();

        for (int l = 0; l < legs.size(); l++) {
            FlightLeg leg = legs.get(l);

            double fuelKg = 0.0;
            if (leg.getFuel() != null) {
                fuelKg = normaliseFuel(leg.getFuel().getQuantity(), leg.getFuel().getUnit());
            }

            List<FlightPlanTestInput.Segment> inputSegments = new ArrayList<>();
            List<FlightSegment> segments = leg.getSegments();

            for (int s = 0; s < segments.size(); s++) {
                FlightSegment seg = segments.get(s);
                Coordinate start = seg.getStart();
                Coordinate end = seg.getEnd();

                // O DSL usa 'ft' como unidade por defeito quando não especificada
                String altitudeUnit = "ft";

                double startLat  = (start != null) ? start.getLat()                                   : 0.0;
                double startLon  = (start != null) ? start.getLon()                                   : 0.0;
                double startAltM = (start != null) ? normaliseAltitude(start.getAltitude(), altitudeUnit) : 0.0;

                double endLat  = (end != null) ? end.getLat()                                   : 0.0;
                double endLon  = (end != null) ? end.getLon()                                   : 0.0;
                double endAltM = (end != null) ? normaliseAltitude(end.getAltitude(), altitudeUnit) : 0.0;

                String mode = (seg.getMode() != null) ? seg.getMode().toUpperCase() : "CRUISE";

                inputSegments.add(new FlightPlanTestInput.Segment(
                        s, mode,
                        startLat, startLon, startAltM,
                        endLat,   endLon,   endAltM
                ));
            }

            String departureAirport = (leg.getDeparture() != null) ? leg.getDeparture().getAirportCode() : "";
            String arrivalAirport   = (leg.getArrival()   != null) ? leg.getArrival().getAirportCode()   : "";

            inputLegs.add(new FlightPlanTestInput.Leg(
                    l, departureAirport, arrivalAirport, fuelKg, inputSegments
            ));
        }

        return new FlightPlanTestInput(plan.getId(), inputLegs);
    }

    // package-private → acessível nos testes sem ser API pública
    static double normaliseAltitude(double value, String unit) {
        if (unit == null) return value;
        return switch (unit.toLowerCase().trim()) {
            case "ft" -> value * FT_TO_M;
            case "km" -> value * KM_TO_M;
            default   -> value; // "m" — já em SI
        };
    }

    // package-private → acessível nos testes sem ser API pública
    static double normaliseFuel(double value, String unit) {
        if (unit == null) return value;
        return switch (unit.toLowerCase().trim()) {
            case "l", "liter", "litro" -> value * JET_A1_DENSITY_KG_PER_L;
            default                    -> value; // "kg" — já em SI
        };
    }
}