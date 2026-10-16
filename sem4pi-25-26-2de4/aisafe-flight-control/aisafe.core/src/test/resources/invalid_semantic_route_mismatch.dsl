// Invalid flight plan — route origin does not match departure airport
// Semantic error: route LIS -> FRA but departure is OPO

flight ERR001 {
    type: regular;

    leg {
        departure: OPO;
        arrival: FRA;

        route: LIS -> FRA;

        segment {
            mode: climb;
            start: (41.2356, -8.6806, 70 m);
            end:   (42.0, -8.0, 10000 m);
        }

        segment {
            mode: cruise;
            start: (42.0, -8.0, 10000 m);
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
