// Valid flight plan with wind data in segments
// Tests wind speed declaration in segment blocks

flight WND001 {
    type: regular;

    leg {
        departure: LIS at 2025-10-01 09:00;
        arrival: CDG at 2025-10-01 12:30;

        route: LIS -> CDG;

        segment {
            mode: climb;
            start: (38.7223, -9.1393, 100 m);
            end:   (40.0, -7.0, 10000 m);
            wind: 15.5 knot;
        }

        segment {
            mode: cruise;
            start: (40.0, -7.0, 10000 m);
            end:   (45.0, 0.0, 10000 m);
            wind: 25.0 knot;
        }

        segment {
            mode: cruise;
            start: (45.0, 0.0, 10000 m);
            end:   (48.0, 1.5, 10000 m);
            wind: 10.0 knot;
        }

        segment {
            mode: descend;
            start: (48.0, 1.5, 10000 m);
            end:   (49.0097, 2.5483, 100 m);
            wind: 5.0 knot;
        }

        fuel: 18000 kg;
    }
}
