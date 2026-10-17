// Invalid flight plan — leg sequence incoherence
// Semantic error: arrival of leg 1 (MAD) does not match departure of leg 2 (LIS)

flight ERR005 {
    type: regular;

    leg {
        departure: LIS;
        arrival: MAD;

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
        departure: LIS;
        arrival: OPO;

        route: LIS -> OPO;

        segment {
            mode: climb;
            start: (38.7223, -9.1393, 100 m);
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
