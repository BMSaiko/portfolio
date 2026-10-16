// invalid_physical_climb_descend.dsl
//
// This flight plan passes all DSL validation (lexical, syntactic, semantic)
// but fails the C physical test component because:
//   - Segment 1 (CLIMB): end altitude (100 m) < start altitude (5000 m) → wrong direction
//   - Segment 3 (DESCEND): end altitude (8000 m) > start altitude (1000 m) → wrong direction
//
// Expected result: PHYSICAL_FAILURE
//   [leg[0].segment[0].altitude] CLIMB segment must have end altitude > start altitude
//   [leg[0].segment[2].altitude] DESCEND segment must have end altitude < start altitude

flight PHYS001 {
    type: regular;

    leg {
        departure: LIS;
        arrival:   OPO;

        route: LIS -> OPO;

        segment {
            mode: climb;
            start: (38.7223, -9.1393, 5000 m);
            end:   (39.5,    -8.5,    100 m);
        }

        segment {
            mode: cruise;
            start: (39.5, -8.5,  100 m);
            end:   (40.5, -8.6,  100 m);
        }

        segment {
            mode: descend;
            start: (40.5,    -8.6,    1000 m);
            end:   (41.2356, -8.6806, 8000 m);
        }

        fuel: 5000 kg;
    }
}
