package org.dennisromano.dailyexchange.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimulationResultTest {

    @Test
    @DisplayName("Should create SimulationResult when state is provided")
    void shouldCreateSimulationResult() {
        final MarketState state = new MarketState(1, 100.0, 100.0, 1000.0, 0.2, 0.05);
        assertDoesNotThrow(() -> new SimulationResult(state, null));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when state is null")
    void shouldThrowExceptionWhenStateIsNull() {
        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new SimulationResult(null, null)
        );

        assertEquals("State cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should allow null shockEvent")
    void shouldAllowNullShockEvent() {
        final MarketState state = new MarketState(1, 100.0, 100.0, 1000.0, 0.2, 0.05);
        final SimulationResult result = new SimulationResult(state, null);

        assertNull(result.shockEvent(), "ShockEvent is optional and should be null");
        assertNotNull(result.state());
    }
}