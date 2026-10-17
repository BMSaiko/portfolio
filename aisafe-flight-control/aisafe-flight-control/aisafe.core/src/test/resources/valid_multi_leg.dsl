// Valid multi-leg flight plan — LIS -> MAD -> BCN
// Tests leg sequence coherence and multiple segments per leg

flight TP789 {
    type: regular;

    leg {
        departure: LIS at 2025-07-01 07:00;
        arrival: MAD at 2025-07-01 08:15;

        route: LIS -> MAD;

        segment {
            mode: climb;
            start: (38.7223, -9.1393, 100 m);
            end:   (39.5, -7.5, 9000 m);
        }

        segment {
            mode: cruise;
            start: (39.5, -7.5, 9000 m);
            end:   (40.0, -5.0, 9000 m);
        }

        segment {
            mode: descend;
            start: (40.0, -5.0, 9000 m);
            end:   (40.4895, -3.5643, 610 m);
        }

        fuel: 4500 kg;
    }

    leg {
        departure: MAD at 2025-07-01 10:00;
        arrival: BCN at 2025-07-01 11:10;

        route: MAD -> BCN;

        segment {
            mode: climb;
            start: (40.4895, -3.5643, 610 m);
            end:   (41.0, -1.0, 10000 m);
        }

        segment {
            mode: cruise;
            start: (41.0, -1.0, 10000 m);
            end:   (41.5, 1.5, 10000 m);
        }

        segment {
            mode: descend;
            start: (41.5, 1.5, 10000 m);
            end:   (41.3874, 2.1734, 50 m);
        }

        fuel: 3800 kg;
    }
}
