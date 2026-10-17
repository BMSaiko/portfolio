// Valid flight plan example — LIS to FRA (single leg)
// Used in ImportFlightPlanControllerTest

flight FP_TEST_001 {
    type: regular;

    leg {
        departure: LIS at 2025-06-15 08:30;
        arrival: FRA at 2025-06-15 12:45;

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

        fuel: 15000 kg;
    }
}
