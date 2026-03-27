package org.dennisromano.dailyexchange.infrastructure.generators;

import org.dennisromano.dailyexchange.domain.model.ShockEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StochasticShockProviderTest {

    private static final double TEST_PROBABILITY = 0.05;

    @Mock
    private RandomGenerator rng;

    private StochasticShockProvider undertest;

    @BeforeEach
    void setUp() {
        undertest = new StochasticShockProvider(TEST_PROBABILITY, rng);
    }

    @Test
    @DisplayName("Should generate a valid ShockEvent when RNG triggers it")
    void shouldGenerateShockEvent() {
        when(rng.nextDouble()).thenReturn(0.02);

        double mockIntensity = 0.55;
        when(rng.nextDouble(0.3, 0.6)).thenReturn(mockIntensity);
        when(rng.nextInt(anyInt())).thenReturn(2);

        Optional<ShockEvent> result = undertest.generateShock();

        assertTrue(result.isPresent());
        result.ifPresent(event -> {
            assertEquals(mockIntensity, event.intensity());
            assertNotNull(event.title());
            assertFalse(event.title().isBlank());
        });

        verify(rng).nextDouble();
        verify(rng).nextDouble(0.3, 0.6);
    }

    @Test
    @DisplayName("Should return empty Optional when RNG is above threshold")
    void shouldReturnEmptyOnNoShock() {
        when(rng.nextDouble()).thenReturn(0.8);

        var result = undertest.generateShock();

        assertTrue(result.isEmpty());
        verify(rng, times(1)).nextDouble();
        verifyNoMoreInteractions(rng);
    }

    @Test
    @DisplayName("Validation: Probability must be in [0, 1]")
    void testConstructorValidation() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new StochasticShockProvider(-0.1, rng)),
                () -> assertThrows(IllegalArgumentException.class, () -> new StochasticShockProvider(1.1, rng))
        );
    }
}