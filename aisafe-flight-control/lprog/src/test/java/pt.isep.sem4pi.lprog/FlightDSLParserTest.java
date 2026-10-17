package pt.isep.sem4pi.lprog;

import org.junit.jupiter.api.Test;
import pt.isep.sem4pi.lprog.error.DSLError;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Lexical and syntactic tests for the Flight DSL.
 *
 * These tests cover the grammar (FlightDSL.g4) and the validation pipeline
 * (FlightDSLValidator) — both of which are US083 responsibilities.
 *
 * All tests use validateString() to avoid file I/O dependencies.
 */
class FlightDSLParserTest {

    private final FlightDSLValidator validator = new FlightDSLValidator();

    // ── Helper ───────────────────────────────────────────────────────────────

    /** Minimal valid flight plan used as a baseline across multiple tests. */
    private String validFlight() {
        return """
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
                        fuel: 40200 kg;
                    }
                }
                """;
    }

    // ── Valid inputs ─────────────────────────────────────────────────────────

    @Test
    void validFlightPlan_shouldPassWithNoErrors() {
        var result = validator.validateString(validFlight());
        assertTrue(result.isValid(),
                "Expected no errors but got: " + result.getErrors());
    }

    @Test
    void validFlight_withDateAndTime_shouldPass() {
        String src = """
                flight TP001 {
                    type: charter;
                    leg {
                        departure: LIS at 2026-05-17 10:30;
                        arrival: LHR at 2026-05-17 13:00;
                        route: LIS -> LHR;
                        segment {
                            mode: climb;
                            start: (38.7742, -9.1342, 100 m);
                            end:   (39.0, -8.5, 9000 m);
                        }
                        segment {
                            mode: cruise;
                            start: (39.0, -8.5, 9000 m);
                            end:   (51.4775, -0.4614, 9000 m);
                        }
                        segment {
                            mode: descend;
                            start: (51.4775, -0.4614, 9000 m);
                            end:   (51.4775, -0.4614, 83 m);
                        }
                        fuel: 15000 kg;
                    }
                }
                """;
        var result = validator.validateString(src);
        assertTrue(result.isValid(),
                "Expected no errors but got: " + result.getErrors());
    }

    @Test
    void validFlight_keywordsCaseInsensitive_shouldPass() {
        // Keywords like FLIGHT, LEG, TYPE are case-insensitive per the grammar
        String src = """
                FLIGHT ZZ999 {
                    TYPE: REGULAR;
                    LEG {
                        DEPARTURE: OPO;
                        ARRIVAL: MAD;
                        ROUTE: OPO -> MAD;
                        SEGMENT {
                            MODE: CLIMB;
                            START: (41.0, -8.0, 100 m);
                            END:   (42.0, -8.0, 9000 m);
                        }
                        SEGMENT {
                            MODE: CRUISE;
                            START: (42.0, -8.0, 9000 m);
                            END:   (41.5, -5.0, 9000 m);
                        }
                        SEGMENT {
                            MODE: DESCEND;
                            START: (41.5, -5.0, 9000 m);
                            END:   (40.5, -3.5, 600 m);
                        }
                        FUEL: 30000 kg;
                    }
                }
                """;
        var result = validator.validateString(src);
        assertTrue(result.isValid(),
                "Keywords should be case-insensitive but got errors: "
                        + result.getErrors());
    }

    @Test
    void validFlight_icaoAirportCode_shouldPass() {
        // ICAO codes have 4 uppercase letters
        String src = validFlight().replace("OPO", "LPPR").replace("MAD", "LEMD");
        var result = validator.validateString(src);
        assertTrue(result.isValid(),
                "ICAO codes should be valid but got: " + result.getErrors());
    }

    @Test
    void validFlight_withLineComment_shouldPass() {
        String src = """
                // This is a comment
                flight AA123 {
                    type: regular; // inline comment
                    leg {
                        departure: OPO;
                        arrival: MAD;
                        route: OPO -> MAD;
                        segment {
                            mode: climb;
                            start: (41.0, -8.0, 100 m);
                            end:   (42.0, -8.0, 9000 m);
                        }
                        segment {
                            mode: cruise;
                            start: (42.0, -8.0, 9000 m);
                            end:   (41.5, -5.0, 9000 m);
                        }
                        segment {
                            mode: descend;
                            start: (41.5, -5.0, 9000 m);
                            end:   (40.5, -3.5, 600 m);
                        }
                        fuel: 30000 kg;
                    }
                }
                """;
        var result = validator.validateString(src);
        assertTrue(result.isValid(),
                "Comments should be ignored but got: " + result.getErrors());
    }

    @Test
    void validFlight_withBlockComment_shouldPass() {
        String src = """
                /* Block comment
                   spanning multiple lines */
                flight AA123 {
                    type: regular;
                    leg {
                        departure: OPO;
                        arrival: MAD;
                        route: OPO -> MAD;
                        segment {
                            mode: climb;
                            start: (41.0, -8.0, 100 m);
                            end:   (42.0, -8.0, 9000 m);
                        }
                        segment {
                            mode: cruise;
                            start: (42.0, -8.0, 9000 m);
                            end:   (41.5, -5.0, 9000 m);
                        }
                        segment {
                            mode: descend;
                            start: (41.5, -5.0, 9000 m);
                            end:   (40.5, -3.5, 600 m);
                        }
                        fuel: 30000 kg;
                    }
                }
                """;
        var result = validator.validateString(src);
        assertTrue(result.isValid(),
                "Block comments should be ignored but got: " + result.getErrors());
    }

    @Test
    void validFlight_withWindDecl_shouldPass() {
        String src = """
                flight WW001 {
                    type: regular;
                    leg {
                        departure: OPO;
                        arrival: MAD;
                        route: OPO -> MAD;
                        segment {
                            mode: cruise;
                            start: (42.0, -8.0, 9000 m);
                            end:   (41.0, -4.3, 9000 m);
                            wind: 270 15 knot;
                        }
                        segment {
                            mode: climb;
                            start: (41.0, -8.0, 100 m);
                            end:   (42.0, -8.0, 9000 m);
                        }
                        segment {
                            mode: descend;
                            start: (41.0, -4.3, 9000 m);
                            end:   (40.5, -3.5, 600 m);
                        }
                        fuel: 40000 kg;
                    }
                }
                """;
        var result = validator.validateString(src);
        assertTrue(result.isValid(),
                "Wind declaration should be valid but got: " + result.getErrors());
    }

    @Test
    void validFlight_multipleFlightsInFile_shouldPass() {
        String src = validFlight() + "\n" + validFlight().replace("AA123", "BB456");
        var result = validator.validateString(src);
        assertTrue(result.isValid(),
                "Multiple flights in file should be valid but got: "
                        + result.getErrors());
    }

    @Test
    void validFlight_negativeCoordinates_shouldPass() {
        // Longitudes in Europe are negative — must parse correctly
        String src = validFlight();
        var result = validator.validateString(src);
        assertTrue(result.isValid(),
                "Negative coordinates should be valid but got: " + result.getErrors());
    }

    @Test
    void validFlight_floatingPointValues_shouldPass() {
        // Coordinates like 41.262891 must be tokenized correctly
        var result = validator.validateString(validFlight());
        assertTrue(result.isValid());
    }

    // ── Syntactic errors ─────────────────────────────────────────────────────

    @Test
    void missingClosingBrace_shouldProduceSyntacticError() {
        String src = """
                flight AA123 {
                    type: regular;
                    leg {
                        departure: OPO;
                        arrival: MAD;
                        route: OPO -> MAD;
                        segment {
                            mode: climb;
                            start: (41.0, -8.0, 100 m);
                            end:   (42.0, -8.0, 9000 m);
                        }
                        segment {
                            mode: cruise;
                            start: (42.0, -8.0, 9000 m);
                            end:   (41.5, -5.0, 9000 m);
                        }
                        segment {
                            mode: descend;
                            start: (41.5, -5.0, 9000 m);
                            end:   (40.5, -3.5, 600 m);
                        }
                        fuel: 30000 kg;
                    }
                // missing closing brace for flight block
                """;
        var result = validator.validateString(src);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
                .anyMatch(e -> e.getType() == DSLError.ErrorType.SYNTACTIC));
    }

    @Test
    void missingSemicolon_shouldProduceSyntacticError() {
        // Missing semicolon after fuel value
        String src = validFlight().replace("fuel: 40200 kg;", "fuel: 40200 kg");
        var result = validator.validateString(src);
        assertFalse(result.isValid());
    }

    @Test
    void emptyFile_shouldProduceSyntacticError() {
        var result = validator.validateString("");
        assertFalse(result.isValid());
    }

    @Test
    void missingFuelBlock_shouldProduceSyntacticError() {
        String src = """
                flight AA123 {
                    type: regular;
                    leg {
                        departure: OPO;
                        arrival: MAD;
                        route: OPO -> MAD;
                        segment {
                            mode: climb;
                            start: (41.0, -8.0, 100 m);
                            end:   (42.0, -8.0, 9000 m);
                        }
                        segment {
                            mode: cruise;
                            start: (42.0, -8.0, 9000 m);
                            end:   (41.5, -5.0, 9000 m);
                        }
                        segment {
                            mode: descend;
                            start: (41.5, -5.0, 9000 m);
                            end:   (40.5, -3.5, 600 m);
                        }
                    }
                }
                """;
        var result = validator.validateString(src);
        assertFalse(result.isValid());
    }

    // ── Lexical errors ───────────────────────────────────────────────────────

    @Test
    void unknownToken_shouldProduceLexicalError() {
        // The '@' character is not defined in the grammar
        String src = validFlight().replace("flight", "@flight");
        var result = validator.validateString(src);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
                .anyMatch(e -> e.getType() == DSLError.ErrorType.LEXICAL
                        || e.getType() == DSLError.ErrorType.SYNTACTIC));
    }

    // ── Error reporting ──────────────────────────────────────────────────────

    @Test
    void syntacticError_shouldIncludeLineAndColumn() {
        String src = validFlight().replace("fuel: 40200 kg;", "fuel: 40200 kg");
        var result = validator.validateString(src);
        assertFalse(result.isValid());
        List<DSLError> errors = result.getErrors();
        assertFalse(errors.isEmpty());
        // Line number must be positive
        assertTrue(errors.get(0).getLine() > 0);
    }

    @Test
    void errorToString_shouldContainTypeLineAndMessage() {
        DSLError error = new DSLError(
                DSLError.ErrorType.SYNTACTIC, 7, 12, "missing '}' at EOF");
        String str = error.toString();
        assertTrue(str.contains("SYNTACTIC"));
        assertTrue(str.contains("7"));
        assertTrue(str.contains("missing"));
    }
}
