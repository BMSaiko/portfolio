// Valid charter flight plan — OPO to LHR (long haul, single leg)
// Tests charter type and longer distance

flight CHR001 {
    type: charter;

    leg {
        departure: OPO at 2025-08-10 22:00;
        arrival: LHR at 2025-08-11 00:30;

        route: OPO -> LHR;

        segment {
            mode: climb;
            start: (41.2356, -8.6806, 70 m);
            end:   (42.0, -9.0, 11000 m);
        }

        segment {
            mode: cruise;
            start: (42.0, -9.0, 11000 m);
            end:   (48.0, -3.0, 11000 m);
        }

        segment {
            mode: descend;
            start: (48.0, -3.0, 11000 m);
            end:   (51.4700, -0.4543, 10 m);
        }

        fuel: 85000 kg;
    }
}
