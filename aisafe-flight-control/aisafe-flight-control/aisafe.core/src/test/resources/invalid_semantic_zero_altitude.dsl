// Invalid flight plan — zero altitude in segment coordinates
// Semantic error: altitudes must be strictly positive

flight ERR006 {
    type: regular;

    leg {
        departure: LIS;
        arrival: OPO;

        route: LIS -> OPO;

        segment {
            mode: climb;
            start: (38.7223, -9.1393, 0 m);
            end:   (39.5, -8.5, 5000 m);
        }

        segment {
            mode: cruise;
            start: (39.5, -8.5, 5000 m);
            end:   (40.5, -8.6, 5000 m);
        }

        segment {
            mode: descend;
            start: (40.5, -8.6, 5000 m);
            end:   (41.2356, -8.6806, 70 m);
        }

        fuel: 5000 kg;
    }
}
