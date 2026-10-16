// Invalid flight plan example — contains intentional errors for testing
// Used in FlightDSLParserTest and SemanticValidatorTest
// US083 — Flight DSL Specification and Validation

// ERROR 1 [SEMANTIC] — invalid flight type (should be 'regular' or 'charter')
flight XX999 {
    type: express;

    leg {
        departure: OPO;
        arrival: MAD;

        // ERROR 2 [SEMANTIC] — route origin does not match departure airport
        route: LIS -> MAD;

        // ERROR 3 [SEMANTIC] — segment start and end are identical
        segment {
            mode: climb;
            start: (41.262891, -8.68522, 69 m);
            end:   (41.262891, -8.68522, 69 m);
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

        // ERROR 4 [SEMANTIC] — fuel quantity must be strictly positive
        fuel: -500 kg;
    }
}
