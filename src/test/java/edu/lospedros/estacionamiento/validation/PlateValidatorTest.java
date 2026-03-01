package edu.lospedros.estacionamiento.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlateValidatorTest {

    @Test
    void normalizaYValidaPlacaCorrecta() {
        String normalized = PlateValidator.normalize(" ab c12 34 ");
        assertEquals("ABC1234", normalized);
        assertTrue(PlateValidator.isValid(normalized));
    }

    @Test
    void rechazaLongitudInvalida() {
        assertFalse(PlateValidator.isValid("ABC123"));
        assertFalse(PlateValidator.isValid("ABC12345"));
    }

    @Test
    void rechazaCaracteresNoPermitidos() {
        assertFalse(PlateValidator.isValid("ABC-234"));
        assertFalse(PlateValidator.isValid("ABC12@4"));
    }
}
