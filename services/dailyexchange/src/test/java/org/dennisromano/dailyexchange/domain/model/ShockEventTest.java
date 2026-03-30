package org.dennisromano.dailyexchange.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ShockEventTest {

    @Test
    @DisplayName("Should create ShockEvent when parameters are valid")
    void shouldCreateValidShockEvent() {
        assertDoesNotThrow(() -> new ShockEvent("Global Pandemic", 0.45));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("Should throw exception when title is blank or empty")
    void shouldThrowExceptionWhenTitleIsBlank(String invalidTitle) {
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ShockEvent(invalidTitle, 0.1)
        );
        assertEquals("Title cannot be null or blank", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when title is null")
    void shouldThrowExceptionWhenTitleIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new ShockEvent(null, 0.1));
    }

    @Test
    @DisplayName("Should throw exception when intensity is negative")
    void shouldThrowExceptionWhenIntensityIsNegative() {
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new ShockEvent("Market Crash", -0.5)
        );
        assertEquals("Intensity cannot be negative", exception.getMessage());
    }
}