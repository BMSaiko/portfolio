// Invalid flight plan — same airport visited twice across legs
// Semantic error: LIS appears as departure in leg 1 and arrival in leg 2

flight ERR003 {
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
        departure: MAD;
        arrival: LIS;

        route: MAD -> LIS;

        segment {
            mode: climb;
            start: (40.4895, -3.5643, 610 m);
            end:   (40.0, -5.0, 9000 m);
        }

        segment {
            mode: cruise;
            start: (40.0, -5.0, 9000 m);
            end:   (39.5, -7.5, 9000 m);
        }

        segment {
            mode: descend;
            start: (39.5, -7.5, 9000 m);
            end:   (38.7223, -9.1393, 100 m);
        }

        fuel: 4500 kg;
    }
}
