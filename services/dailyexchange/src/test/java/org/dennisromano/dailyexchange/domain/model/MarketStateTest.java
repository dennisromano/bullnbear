package org.dennisromano.dailyexchange.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class MarketStateTest {

    @Test
    @DisplayName("Should create a valid MarketState when all parameters are correct")
    void shouldCreateValidMarketState() {
        assertDoesNotThrow(() ->
                new MarketState(1, 100.0, 90.0, 1000.0, 0.2, 0.05)
        );
    }

    @ParameterizedTest
    @CsvSource({
            "-1, 100.0, 100.0, 1000.0, 0.2, Day cannot be negative",
            "1, -10.0, 100.0, 1000.0, 0.2, Price must be positive",
            "1, 100.0, 0.0, 1000.0, 0.2, Previous price must be positive",
            "1, 100.0, 100.0, -1.0, 0.2, Quantity cannot be negative",
            "1, 100.0, 100.0, 1000.0, -0.1, Sigma cannot be negative"
    })
    @DisplayName("Should throw IllegalArgumentException for invalid inputs")
    void shouldThrowExceptionForInvalidInputs(int day, double price, double prevPrice, double qty, double sigma, String expectedMessage) {
        final IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new MarketState(day, price, prevPrice, qty, sigma, 0.05)
        );

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should correctly calculate price variation")
    void shouldCalculatePriceVariation() {
        final MarketState state = new MarketState(1, 120.0, 100.0, 1000.0, 0.2, 0.05);
        assertEquals(0.20, state.priceVariation(), 0.00001);
    }
}