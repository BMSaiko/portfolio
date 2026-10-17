package pt.isep.sem4pi.lprog;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes focados exclusivamente na Validação Semântica (SemanticValidator).
 * Assumem que os ficheiros já passaram sem erros lexicais ou sintáticos.
 */
public class SemanticValidatorTest {

    // ── Helper ───────────────────────────────────────────────────────────────

    /**
     * Um plano de voo 100% válido, com 2 Legs perfeitas, para testar a continuidade.
     */
    private String createValidMultiLegFlight() {
        return """
                flight TP1000 {
                    type: regular;
                    leg {
                        departure: OPO at 2026-05-10 10:00;
                        arrival: LIS at 2026-05-10 11:00;
                        route: OPO -> LIS;
                        segment { mode: cruise; start: (41.2, -8.6, 10000 m); end: (38.7, -9.1, 10000 m); wind: 270 15 knot; }
                        fuel: 3000 kg;
                    }
                    leg {
                        departure: LIS at 2026-05-10 12:00;
                        arrival: MAD at 2026-05-10 14:00;
                        route: LIS -> MAD;
                        segment { mode: cruise; start: (38.7, -9.1, 10000 m); end: (40.4, -3.5, 10000 m); }
                        fuel: 4000 kg;
                    }
                }
                """;
    }

    // ── 1. Caminho Feliz ─────────────────────────────────────────────────────

    @Test
    public void testValidFlightPlanWithMultipleLegs_ShouldPass() {
        var result = new FlightDSLValidator().validateString(createValidMultiLegFlight());
        assertTrue(result.isValid(), "Um plano de voo com 2 legs perfeitamente encadeadas não deve dar erros semânticos.");
    }

    // ── 2. Regra: ID do voo tem de ser único ─────────────────────────────────

    @Test
    public void testDuplicateFlightId_ShouldThrowError() {
        String dsl = createValidMultiLegFlight() + "\n" + createValidMultiLegFlight();
        var result = new FlightDSLValidator().validateString(dsl);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.getMessage().contains("tem de ser único")));
    }

    // ── 3. Regra: Combustível tem de ser positivo ────────────────────────────

    @Test
    public void testZeroOrNegativeFuel_ShouldThrowError() {
        String dsl = createValidMultiLegFlight().replace("fuel: 3000 kg;", "fuel: -50 kg;");
        var result = new FlightDSLValidator().validateString(dsl);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.getMessage().contains("estritamente positiva")));
    }

    // ── 4. Regra: Coerência da Rota vs Departure/Arrival ─────────────────────

    @Test
    public void testRouteOriginMismatch_ShouldThrowError() {
        String dsl = createValidMultiLegFlight().replace("route: OPO -> LIS;", "route: LIS -> LIS;");
        var result = new FlightDSLValidator().validateString(dsl);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.getMessage().contains("origem da rota")));
    }

    @Test
    public void testRouteDestinationMismatch_ShouldThrowError() {
        String dsl = createValidMultiLegFlight().replace("route: LIS -> MAD;", "route: LIS -> OPO;");
        var result = new FlightDSLValidator().validateString(dsl);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.getMessage().contains("destino da rota")));
    }

    // ── 5. Regra: O mesmo aeroporto não pode ser visitado 2 vezes no voo ─────

    @Test
    public void testAirportVisitedTwiceInSameFlight_ShouldThrowError() {
        String dsl = createValidMultiLegFlight()
                .replace("arrival: MAD", "arrival: OPO")
                .replace("route: LIS -> MAD;", "route: LIS -> OPO;");
        var result = new FlightDSLValidator().validateString(dsl);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.getMessage().contains("não pode ser visitado mais do que uma vez")));
    }

    // ── 6. Regra: Coordenadas Início e Fim do Segmento ───────────────────────

    @Test
    public void testSegmentStartAndEndCoordinatesAreTheSame_ShouldThrowError() {
        String dsl = createValidMultiLegFlight().replace("end: (38.7, -9.1, 10000 m);", "end: (41.2, -8.6, 10000 m);");
        var result = new FlightDSLValidator().validateString(dsl);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.getMessage().contains("não podem ser iguais")));
    }

    // ── 7. Regra: Altitudes e Vento positivos ────────────────────────────────

    @Test
    public void testNegativeAltitudesOrWind_ShouldThrowError() {
        String dsl = createValidMultiLegFlight()
                .replace("10000 m", "-50 m")
                .replace("wind: 270 15 knot;", "wind: 270 -10 knot;");
        var result = new FlightDSLValidator().validateString(dsl);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.getMessage().contains("altitude da coordenada de início")));
        assertTrue(result.getErrors().stream().anyMatch(e -> e.getMessage().contains("velocidade do vento não pode ser")));
    }

    // ── 8. Regra: Continuidade Espacial entre Legs ───────────────────────────

    @Test
    public void testLegSequenceAirportMismatch_ShouldThrowError() {
        String dsl = createValidMultiLegFlight().replace("departure: LIS at 2026-05-10 12:00;", "departure: BCN at 2026-05-10 12:00;");
        var result = new FlightDSLValidator().validateString(dsl);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.getMessage().contains("deve coincidir com o aeroporto de partida da Leg 2")));
    }

    // ── 9. Regra: Continuidade Temporal entre Legs (Viagem no tempo) ─────────

    @Test
    public void testLegSequenceTimeConflict_ShouldThrowError() {
        String dsl = createValidMultiLegFlight().replace("departure: LIS at 2026-05-10 12:00;", "departure: LIS at 2026-05-10 09:00;");
        var result = new FlightDSLValidator().validateString(dsl);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.getMessage().contains("deve preceder a hora de partida da Leg 2")));
    }
}