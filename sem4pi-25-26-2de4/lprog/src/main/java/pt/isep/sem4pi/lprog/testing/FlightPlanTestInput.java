package pt.isep.sem4pi.lprog.testing;

import java.util.Collections;
import java.util.List;

/**
 * Data Transfer Object representing the JSON payload sent to the C
 * {@code flight_tester} process via stdin.
 *
 * <p>All values are already normalised to SI units (metres, kilograms) by
 * {@link FlightPlanTestInputBuilder} before this object is created.
 *
 * <p>JSON structure produced:
 * <pre>
 * {
 *   "flightId": "TP100",
 *   "legs": [
 *     {
 *       "index": 0,
 *       "departureAirport": "LPPT",
 *       "arrivalAirport":   "LPPR",
 *       "fuelKg": 14500.0,
 *       "segments": [
 *         { "index": 0, "mode": "CLIMB",
 *           "startLat": 38.78, "startLon": -9.13, "startAltM": 0.0,
 *           "endLat":   38.85, "endLon":  -9.00, "endAltM": 8000.0 },
 *         ...
 *       ]
 *     }
 *   ]
 * }
 * </pre>
 */
public class FlightPlanTestInput {

    /* ── Nested: Segment ─────────────────────────────────────────────────── */

    public static class Segment {
        private final int    index;
        private final String mode;       /* CLIMB | CRUISE | DESCEND */
        private final double startLat;
        private final double startLon;
        private final double startAltM;
        private final double endLat;
        private final double endLon;
        private final double endAltM;

        public Segment(int index, String mode,
                       double startLat, double startLon, double startAltM,
                       double endLat,   double endLon,   double endAltM) {
            this.index     = index;
            this.mode      = mode.toUpperCase();
            this.startLat  = startLat;
            this.startLon  = startLon;
            this.startAltM = startAltM;
            this.endLat    = endLat;
            this.endLon    = endLon;
            this.endAltM   = endAltM;
        }

        public int    getIndex()     { return index;     }
        public String getMode()      { return mode;      }
        public double getStartLat()  { return startLat;  }
        public double getStartLon()  { return startLon;  }
        public double getStartAltM() { return startAltM; }
        public double getEndLat()    { return endLat;    }
        public double getEndLon()    { return endLon;    }
        public double getEndAltM()   { return endAltM;   }
    }

    /* ── Nested: Leg ─────────────────────────────────────────────────────── */

    public static class Leg {
        private final int           index;
        private final String        departureAirport;
        private final String        arrivalAirport;
        private final double        fuelKg;
        private final List<Segment> segments;

        public Leg(int index, String departureAirport, String arrivalAirport,
                   double fuelKg, List<Segment> segments) {
            this.index            = index;
            this.departureAirport = departureAirport;
            this.arrivalAirport   = arrivalAirport;
            this.fuelKg           = fuelKg;
            this.segments         = Collections.unmodifiableList(segments);
        }

        public int           getIndex()            { return index;            }
        public String        getDepartureAirport()  { return departureAirport; }
        public String        getArrivalAirport()    { return arrivalAirport;   }
        public double        getFuelKg()            { return fuelKg;           }
        public List<Segment> getSegments()          { return segments;         }
    }

    /* ── Top-level ───────────────────────────────────────────────────────── */

    private final String    flightId;
    private final List<Leg> legs;

    public FlightPlanTestInput(String flightId, List<Leg> legs) {
        this.flightId = flightId;
        this.legs     = Collections.unmodifiableList(legs);
    }

    public String    getFlightId() { return flightId; }
    public List<Leg> getLegs()     { return legs;     }

    /* ── JSON serialisation ──────────────────────────────────────────────── */

    /**
     * Serialises this DTO to a compact JSON string suitable for writing to
     * the C process stdin.  Uses hand-rolled serialisation to avoid any
     * external JSON library dependency in the {@code lprog} module.
     */
    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"flightId\":").append(quoted(flightId));
        sb.append(",\"legs\":[");

        for (int l = 0; l < legs.size(); l++) {
            if (l > 0) sb.append(',');
            Leg leg = legs.get(l);
            sb.append("{\"index\":").append(leg.getIndex());
            sb.append(",\"departureAirport\":").append(quoted(leg.getDepartureAirport()));
            sb.append(",\"arrivalAirport\":").append(quoted(leg.getArrivalAirport()));
            sb.append(",\"fuelKg\":").append(leg.getFuelKg());
            sb.append(",\"segments\":[");

            List<Segment> segs = leg.getSegments();
            for (int s = 0; s < segs.size(); s++) {
                if (s > 0) sb.append(',');
                Segment seg = segs.get(s);
                sb.append("{\"index\":").append(seg.getIndex());
                sb.append(",\"mode\":").append(quoted(seg.getMode()));
                sb.append(",\"startLat\":").append(seg.getStartLat());
                sb.append(",\"startLon\":").append(seg.getStartLon());
                sb.append(",\"startAltM\":").append(seg.getStartAltM());
                sb.append(",\"endLat\":").append(seg.getEndLat());
                sb.append(",\"endLon\":").append(seg.getEndLon());
                sb.append(",\"endAltM\":").append(seg.getEndAltM());
                sb.append('}');
            }
            sb.append("]}");
        }
        sb.append("]}");
        return sb.toString();
    }

    private static String quoted(String s) {
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
