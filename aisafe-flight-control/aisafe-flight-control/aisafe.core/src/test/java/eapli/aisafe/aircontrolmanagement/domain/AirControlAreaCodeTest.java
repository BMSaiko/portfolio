package eapli.aisafe.aircontrolmanagement.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AirControlAreaCodeTest {

    @Test
    void ensureValidCodeIsCreated() {
        assertDoesNotThrow(() -> AirControlAreaCode.of("LPPC"));
    }

    @Test
    void ensureEmptyCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                AirControlAreaCode.of(""));
    }

    @Test
    void ensureNullCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                AirControlAreaCode.of(null));
    }

    // A classe exige maiúsculas — lowercase é rejeitado
    @Test
    void ensureLowercaseCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                AirControlAreaCode.of("lppc"));
    }

    @Test
    void ensureValidCodeValueIsStored() {
        assertEquals("LPPC", AirControlAreaCode.of("LPPC").value());
    }

    @Test
    void ensureEqualCodesAreEqual() {
        assertEquals(AirControlAreaCode.of("LPPC"), AirControlAreaCode.of("LPPC"));
    }

    @Test
    void ensureDifferentCodesAreNotEqual() {
        assertNotEquals(AirControlAreaCode.of("LPPC"), AirControlAreaCode.of("LPPT"));
    }

    @Test
    void ensureMinimumLengthCodeIsAccepted() {
        // 2 caracteres — limite mínimo
        assertDoesNotThrow(() -> AirControlAreaCode.of("LP"));
    }

    @Test
    void ensureMaximumLengthCodeIsAccepted() {
        // 10 caracteres — limite máximo
        assertDoesNotThrow(() -> AirControlAreaCode.of("LPPC123456"));
    }

    @Test
    void ensureTooShortCodeIsRejected() {
        // 1 caracter — abaixo do mínimo
        assertThrows(IllegalArgumentException.class, () ->
                AirControlAreaCode.of("L"));
    }

    @Test
    void ensureTooLongCodeIsRejected() {
        // 11 caracteres — acima do máximo
        assertThrows(IllegalArgumentException.class, () ->
                AirControlAreaCode.of("LPPC1234567"));
    }
}