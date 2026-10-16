// Invalid flight plan example — syntactic errors
// Used in ImportFlightPlanControllerTest
// Errors: missing semicolon after fuel, missing closing brace

flight FP_TEST_001 {
    type: regular;

    leg {
        departure: LIS;
        arrival: FRA;

        route: LIS -> FRA;

        segment {
            mode: climb;
            start: (38.7223, -9.1393, 100 m);
            end:   (39.5, -8.0, 10000 m);
        }

        segment {
            mode: cruise;
            start: (39.5, -8.0, 10000 m);
            end:   (45.0, 2.0, 10000 m);
        }

        segment {
            mode: descend;
            start: (45.0, 2.0, 10000 m);
            end:   (50.0379, 8.5622, 110 m);
        }

        // ERROR — missing semicolon after fuel value
        fuel: 15000 kg
    }
// ERROR — missing closing brace for flight block
