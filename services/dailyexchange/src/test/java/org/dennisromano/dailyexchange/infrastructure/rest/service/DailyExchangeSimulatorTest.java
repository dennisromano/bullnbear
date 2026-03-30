package org.dennisromano.dailyexchange.infrastructure.rest.service;

import org.dennisromano.dailyexchange.domain.model.SimulationConfig;
import org.dennisromano.dailyexchange.domain.model.SimulationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DailyExchangeSimulatorTest {
    private DailyExchangeSimulator simulator;
    private SimulationConfig defaultConfig;

    @BeforeEach
    void setUp() {
        simulator = new DailyExchangeSimulator();
        defaultConfig = new SimulationConfig(
                10,
                252,
                100.0,
                1000.0,
                0.20,
                0.05,
                2.0,
                0.15,
                0.01
        );
    }

    @Test
    @DisplayName("Should return the exact number of simulation days requested")
    void shouldReturnCorrectNumberOfDays() {
        final List<SimulationResult> results = simulator.calculateSimulation(defaultConfig);

        assertNotNull(results);
        assertEquals(10, results.size(), "Simulation should have 10 steps as configured");

        for (int i = 0; i < results.size(); i++) {
            assertEquals(i + 1, results.get(i).state().day(), "Day sequence mismatch at index " + i);
        }
    }

    @Test
    @DisplayName("Should handle single day simulation")
    void shouldHandleSingleDaySimulation() {
        final SimulationConfig singleDayConfig = new SimulationConfig(
                1, 252, 100.0, 1000.0, 0.2, 0.05, 2.0, 0.15, 0.01
        );

        final List<SimulationResult> results = simulator.calculateSimulation(singleDayConfig);

        assertEquals(1, results.size());
        assertEquals(1, results.getFirst().state().day());
    }

    @Test
    @DisplayName("Should propagate initial values to the first simulation step")
    void shouldPropagateInitialValues() {
        final List<SimulationResult> results = simulator.calculateSimulation(defaultConfig);
        final SimulationResult firstStep = results.getFirst();

        assertAll("First step integrity",
                () -> assertNotNull(firstStep.state()),
                () -> assertEquals(1, firstStep.state().day()),
                () -> assertTrue(firstStep.state().price() > 0, "Price should be positive")
        );
    }

    @Test
    @DisplayName("Should throw exception if config is null")
    void shouldThrowExceptionOnNullConfig() {
        assertThrows(NullPointerException.class, () -> simulator.calculateSimulation(null));
    }
}