package org.dennisromano.dailyexchange.infrastructure.rest.service;

import org.dennisromano.dailyexchange.domain.model.SimulationConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DailyExchangeSimulatorTest {
    SimulationConfig simulationConfig;

    @BeforeEach
    public void setUp() {
        simulationConfig = new SimulationConfig(30, 252.0, 100.0, 2000000.0, 0.20, 0.08, 2.0, 0.15, 0.001);
    }

    @Test
    public void testCalculateSimulation() {

    }
}
