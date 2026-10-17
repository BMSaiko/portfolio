package eapli.aisafe.aircontrolmanagement.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AirControlArea}.
 *
 * NOTE: AirControlAreaCode e GeoBoundary são usados como instâncias reais onde
 * equals/hashCode estão envolvidos. Mockito não consegue fazer stub desses métodos.
 * GeoBoundary continua a ser mockado onde só a sua referência é necessária.
 */
class AirControlAreaTest {

    // Instâncias reais — seguras para usar com equals/hashCode
    private AirControlAreaCode codeA;
    private AirControlAreaCode codeB;

    // GeoBoundary apenas é passado; nunca comparado por equals nestes testes
    private GeoBoundary validBoundary;

    @BeforeEach
    void setUp() {
        codeA         = AirControlAreaCode.of("LPPC");
        codeB         = AirControlAreaCode.of("LPPT");
        validBoundary = mock(GeoBoundary.class);
    }

    // -----------------------------------------------------------------------
    // Constructor – happy path
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Deve criar AirControlArea com argumentos válidos")
    void shouldCreateAreaWithValidArguments() {
        AirControlArea area = new AirControlArea(codeA, "Lisboa TMA", 500.0, validBoundary);

        assertEquals("Lisboa TMA",  area.name());
        assertEquals(500.0,         area.minimumFuelRequired(), 1e-9);
        assertEquals(codeA,         area.identity());
        assertEquals(validBoundary, area.geoBoundary());
    }

    @Test
    @DisplayName("Deve aceitar combustível mínimo igual a zero")
    void shouldAcceptZeroMinimumFuel() {
        assertDoesNotThrow(() ->
                new AirControlArea(codeA, "Porto CTR", 0.0, validBoundary));
    }

    // -----------------------------------------------------------------------
    // Constructor – null / blank validations
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Deve lançar exceção quando o código é null")
    void shouldThrowWhenCodeIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new AirControlArea(null, "Lisboa TMA", 500.0, validBoundary));

        assertTrue(ex.getMessage().toLowerCase().contains("code") ||
                ex.getMessage().toLowerCase().contains("null"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o nome é null")
    void shouldThrowWhenNameIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new AirControlArea(codeA, null, 500.0, validBoundary));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o nome está em branco")
    void shouldThrowWhenNameIsBlank() {
        assertThrows(IllegalArgumentException.class, () ->
                new AirControlArea(codeA, "   ", 500.0, validBoundary));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o nome está vazio")
    void shouldThrowWhenNameIsEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
                new AirControlArea(codeA, "", 500.0, validBoundary));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o combustível mínimo é negativo")
    void shouldThrowWhenMinimumFuelIsNegative() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                new AirControlArea(codeA, "Lisboa TMA", -1.0, validBoundary));

        assertTrue(ex.getMessage().toLowerCase().contains("negative") ||
                ex.getMessage().toLowerCase().contains("fuel"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o boundary é null")
    void shouldThrowWhenBoundaryIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                new AirControlArea(codeA, "Lisboa TMA", 500.0, null));
    }

    // -----------------------------------------------------------------------
    // equals / hashCode  — usa instâncias reais de AirControlAreaCode
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Dois objetos com o mesmo código devem ser iguais")
    void shouldBeEqualWhenSameCode() {
        AirControlArea a1 = new AirControlArea(codeA, "Area A", 100.0, validBoundary);
        AirControlArea a2 = new AirControlArea(codeA, "Area B", 200.0, mock(GeoBoundary.class));

        assertEquals(a1, a2);
    }

    @Test
    @DisplayName("Dois objetos com códigos diferentes não devem ser iguais")
    void shouldNotBeEqualWhenDifferentCode() {
        AirControlArea a1 = new AirControlArea(codeA, "Area A", 100.0, validBoundary);
        AirControlArea a2 = new AirControlArea(codeB, "Area A", 100.0, validBoundary);

        assertNotEquals(a1, a2);
    }

    @Test
    @DisplayName("Deve ser igual a si mesmo (reflexividade)")
    void shouldBeEqualToItself() {
        AirControlArea area = new AirControlArea(codeA, "Lisboa TMA", 300.0, validBoundary);
        assertEquals(area, area);
    }

    @Test
    @DisplayName("Não deve ser igual a null")
    void shouldNotBeEqualToNull() {
        AirControlArea area = new AirControlArea(codeA, "Lisboa TMA", 300.0, validBoundary);
        assertNotEquals(null, area);
    }

    @Test
    @DisplayName("Não deve ser igual a objeto de tipo diferente")
    void shouldNotBeEqualToDifferentType() {
        AirControlArea area = new AirControlArea(codeA, "Lisboa TMA", 300.0, validBoundary);
        assertNotEquals("uma string qualquer", area);
    }

    @Test
    @DisplayName("Dois objetos iguais devem ter o mesmo hashCode")
    void equalObjectsShouldHaveSameHashCode() {
        AirControlArea a1 = new AirControlArea(codeA, "Area A", 100.0, validBoundary);
        AirControlArea a2 = new AirControlArea(codeA, "Area B", 200.0, mock(GeoBoundary.class));

        assertEquals(a1.hashCode(), a2.hashCode());
    }

    // -----------------------------------------------------------------------
    // sameAs
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("sameAs deve retornar true para áreas com o mesmo código")
    void sameAsShouldReturnTrueForSameCode() {
        AirControlArea a1 = new AirControlArea(codeA, "Area A", 100.0, validBoundary);
        AirControlArea a2 = new AirControlArea(codeA, "Area A", 100.0, validBoundary);

        assertTrue(a1.sameAs(a2));
    }

    @Test
    @DisplayName("sameAs deve retornar false para áreas com códigos distintos")
    void sameAsShouldReturnFalseForDifferentCode() {
        AirControlArea a1 = new AirControlArea(codeA, "Area A", 100.0, validBoundary);
        AirControlArea a2 = new AirControlArea(codeB, "Area A", 100.0, validBoundary);

        assertFalse(a1.sameAs(a2));
    }

    @Test
    @DisplayName("sameAs deve retornar false para objeto não-AirControlArea")
    void sameAsShouldReturnFalseForNonArea() {
        AirControlArea area = new AirControlArea(codeA, "Lisboa TMA", 300.0, validBoundary);
        assertFalse(area.sameAs("not an area"));
    }

    @Test
    @DisplayName("sameAs deve retornar false para null")
    void sameAsShouldReturnFalseForNull() {
        AirControlArea area = new AirControlArea(codeA, "Lisboa TMA", 300.0, validBoundary);
        assertFalse(area.sameAs(null));
    }

    // -----------------------------------------------------------------------
    // toString
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("toString deve conter o nome")
    void toStringShouldContainName() {
        AirControlArea area = new AirControlArea(codeA, "Lisboa TMA", 450.0, validBoundary);

        assertTrue(area.toString().contains("Lisboa TMA"), "toString deve conter o nome");
    }

    @Test
    @DisplayName("toString deve conter o combustível mínimo formatado (locale-safe)")
    void toStringShouldContainFuel() {
        // Gera o valor esperado com o mesmo Locale que String.format usa por omissão,
        // evitando falhas por separador decimal ('.' vs ',') dependente do SO.
        double fuel = 450.5;
        String expectedFuel = String.format("%.1f", fuel); // "450.5" ou "450,5" conforme Locale

        AirControlArea area = new AirControlArea(codeA, "Lisboa TMA", fuel, validBoundary);

        assertTrue(area.toString().contains(expectedFuel),
                "toString deve conter '" + expectedFuel + "'");
    }

    @Test
    @DisplayName("toString deve conter o código da área")
    void toStringShouldContainAreaCode() {
        AirControlArea area = new AirControlArea(codeA, "Lisboa TMA", 100.0, validBoundary);

        assertTrue(area.toString().contains("LPPC"), "toString deve conter o código LPPC");
    }
}