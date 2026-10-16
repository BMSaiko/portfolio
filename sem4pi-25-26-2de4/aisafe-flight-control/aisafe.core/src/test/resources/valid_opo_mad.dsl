// Valid flight plan example — OPO to MAD
// Based on flight AA123 data provided in the project specification
// US083 — Flight DSL Specification and Validation

flight AA123 {
    type: regular;

    leg {
        departure: OPO at 2025-06-20 10:00;
        arrival: MAD at 2025-06-20 11:15;

        route: OPO -> MAD;

        // Climb segment: Porto airport to cruising altitude
        segment {
            mode: climb;
            start: (41.262891, -8.68522, 69 m);
            end:   (42.0, -8.01, 9249 m);
        }

        // Cruise segment: maintain altitude across Spain
        segment {
            mode: cruise;
            start: (42.0, -8.01, 9249 m);
            end:   (41.0, -4.3, 9249 m);
        }

        // Descend segment: approach Madrid airport
        segment {
            mode: descend;
            start: (41.0, -4.3, 9249 m);
            end:   (40.4895, -3.5643, 610 m);
        }

        fuel: 40200 kg;
    }
}
