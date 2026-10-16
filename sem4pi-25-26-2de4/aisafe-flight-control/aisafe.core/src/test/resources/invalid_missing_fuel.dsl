// Invalid flight plan example — syntactic errors
// Used in FlightDSLParserTest
// US083 — Flight DSL Specification and Validation

flight AA123 {
    type: regular;

    leg {
        departure: OPO;
        arrival: MAD;

        route: OPO -> MAD;

        segment {
            mode: climb;
            start: (41.262891, -8.68522, 69 m);
            end:   (42.0, -8.01, 9249 m);
        }

        segment {
            mode: cruise;
            start: (42.0, -8.01, 9249 m);
            end:   (41.0, -4.3, 9249 m);
        }

        segment {
            mode: descend;
            start: (41.0, -4.3, 9249 m);
            end:   (40.4895, -3.5643, 610 m);
        }

        // ERROR — missing semicolon after fuel value
        fuel: 40200 kg
    }
// ERROR — missing closing brace for flight block
