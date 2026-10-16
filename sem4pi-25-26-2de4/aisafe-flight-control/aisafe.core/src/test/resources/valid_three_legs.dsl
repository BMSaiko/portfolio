// Valid three-leg flight plan — LIS -> FCO -> VIT -> OPO
// Tests complex multi-leg routing with 4 airports

flight TP999 {
    type: regular;

    leg {
        departure: LIS at 2025-09-01 06:00;
        arrival: FCO at 2025-09-01 10:30;

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
        departure: FCO at 2025-09-01 14:00;
        arrival: VIT at 2025-09-01 16:15;

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

    leg {
        departure: VIT at 2025-09-01 18:00;
        arrival: OPO at 2025-09-01 19:30;

        route: VIT -> OPO;

        segment {
            mode: climb;
            start: (42.8833, -2.6333, 506 m);
            end:   (42.5, -4.0, 8000 m);
        }

        segment {
            mode: cruise;
            start: (42.5, -4.0, 8000 m);
            end:   (41.5, -7.0, 8000 m);
        }

        segment {
            mode: descend;
            start: (41.5, -7.0, 8000 m);
            end:   (41.2356, -8.6806, 70 m);
        }

        fuel: 5500 kg;
    }
}
