package org.dennisromano.dailyexchange.application;

import org.dennisromano.dailyexchange.domain.model.MarketState;
import org.dennisromano.dailyexchange.domain.model.ShockEvent;
import org.dennisromano.dailyexchange.domain.model.SimulationResult;
import org.dennisromano.dailyexchange.domain.ports.PricingStrategy;
import org.dennisromano.dailyexchange.domain.ports.ShockProvider;
import org.dennisromano.dailyexchange.domain.ports.VolatilityStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarketSimulatorTest {

    @Mock
    private VolatilityStrategy volatilityStrategy;

    @Mock
    private PricingStrategy pricingStrategy;

    @Mock
    private ShockProvider shockProvider;

    @Mock
    private RandomGenerator rng;

    private MarketSimulator marketSimulator;
    private final double TARGET_MU = 0.05;

    @BeforeEach
    void setUp() {
        marketSimulator = new MarketSimulator(
                volatilityStrategy,
                pricingStrategy,
                shockProvider,
                rng,
                TARGET_MU
        );
    }

    @Test
    @DisplayName("Should cover shock event logic and increase volatility accordingly")
    void nextShouldHandleShockEvent() {
        final MarketState initialState = new MarketState(0, 100.0, 100.0, 1000.0, 0.20, 0.05);
        final ShockEvent mockedShock = new ShockEvent("Market Crash", 0.50);

        when(shockProvider.generateShock()).thenReturn(Optional.of(mockedShock));
        when(volatilityStrategy.calculateNextVolatility(0.70)).thenReturn(0.75);
        when(pricingStrategy.calculateNextPrice(eq(100.0), eq(TARGET_MU), eq(0.75))).thenReturn(95.0);
        when(rng.nextDouble()).thenReturn(0.5);

        final SimulationResult result = marketSimulator.next(1, initialState);
        assertAll("Shock handling validation",
                () -> assertNotNull(result.shockEvent(), "Shock event should be present"),
                () -> assertEquals("Market Crash", result.shockEvent().title()),
                () -> assertEquals(0.75, result.state().sigma(), "Sigma should be the result of the strategy"),
                () -> assertEquals(95.0, result.state().price(), "Price should be the result of the pricing strategy"),
                () -> assertEquals(1000.0, result.state().quantity(), "Quantity should remain unchanged with rng 0.5")
        );

        verify(shockProvider).generateShock();
        verify(volatilityStrategy).calculateNextVolatility(0.70);
    }
}