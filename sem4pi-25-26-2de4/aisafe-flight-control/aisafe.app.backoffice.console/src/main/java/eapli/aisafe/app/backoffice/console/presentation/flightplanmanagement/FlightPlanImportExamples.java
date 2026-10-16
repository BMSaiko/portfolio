package eapli.aisafe.app.backoffice.console.presentation.flightplanmanagement;

/**
 * Utility class providing DSL file examples and aircraft model listings
 * for the flight plan import UI.
 */
public final class FlightPlanImportExamples {

    private FlightPlanImportExamples() {
        // utility class
    }

    /**
     * Returns a description of available DSL example files.
     */
    public static String availableExamples() {
        return "\nAvailable DSL example files:\n"
                + "  - valid_opo_mad.dsl    (OPO -> MAD, valid)\n"
                + "  - valid_lis_fco_vit.dsl (LIS -> FCO -> VIT, valid)\n"
                + "  - invalid_flight.dsl   (intentionally invalid)\n";
    }

    /**
     * Returns a list of registered aircraft models.
     */
    public static String availableAircraftModels() {
        return "\nAvailable Aircraft Models:\n"
                + "  - BOEING-737 (Boeing)\n"
                + "  - 777-200ER (Boeing)\n"
                + "  - A380 (Airbus)\n";
    }

    /**
     * Returns example DSL input for a simple flight.
     */
    public static String exampleDsl() {
        return "\nExample DSL format:\n"
                + "  flight FP001 {\n"
                + "      type: regular;\n"
                + "      leg {\n"
                + "          departure: LIS;\n"
                + "          arrival: OPO;\n"
                + "          route: LIS -> OPO;\n"
                + "          segment {\n"
                + "              mode: climb;\n"
                + "              start: (38.7223, -9.1393, 100 m);\n"
                + "              end:   (39.0, -8.5, 10000 m);\n"
                + "          }\n"
                + "          segment {\n"
                + "              mode: cruise;\n"
                + "              start: (39.0, -8.5, 10000 m);\n"
                + "              end:   (41.0, -8.7, 10000 m);\n"
                + "          }\n"
                + "          segment {\n"
                + "              mode: descend;\n"
                + "              start: (41.0, -8.7, 10000 m);\n"
                + "              end:   (41.2356, -8.6806, 70 m);\n"
                + "          }\n"
                + "          fuel: 5000 kg;\n"
                + "      }\n"
                + "  }\n";
    }
}
