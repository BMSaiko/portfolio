// Valid flight plan example — LIS to FCO to VIT (multi-leg)
// US083 — Flight DSL Specification and Validation

flight TP456 {
    type: regular;

    leg {
        departure: LIS at 2025-07-10 09:00;
        arrival: FCO at 2025-07-10 13:30;

        route: LIS -> FCO;

        segment {
            mode: climb;
            start: (38.7223, -9.1393, 100 m);
            end:   (39.5, -8.0, 10000 m);
        }

        segment {
            mode: cruise;
            start: (39.5, -8.0, 10000 m);
            end:   (42.0, 5.0, 10000 m);
        }

        segment {
            mode: descend;
            start: (42.0, 5.0, 10000 m);
            end:   (41.9429, 12.4964, 55 m);
        }

        fuel: 12000 kg;
    }

    leg {
        departure: FCO at 2025-07-10 15:00;
        arrival: VIT at 2025-07-10 17:15;

        route: FCO -> VIT;

        segment {
            mode: climb;
            start: (41.9429, 12.4964, 55 m);
            end:   (42.0, 10.0, 9000 m);
        }

        segment {
            mode: cruise;
            start: (42.0, 10.0, 9000 m);
            end:   (42.8, 2.5, 9000 m);
        }

        segment {
            mode: descend;
            start: (42.8, 2.5, 9000 m);
            end:   (42.8833, -2.6333, 506 m);
        }

        fuel: 8000 kg;
    }
}
