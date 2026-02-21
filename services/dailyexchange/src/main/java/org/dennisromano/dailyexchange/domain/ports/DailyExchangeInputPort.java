package org.dennisromano.dailyexchange.domain.ports;

import org.dennisromano.dailyexchange.domain.model.SimulationConfig;
import org.dennisromano.dailyexchange.domain.model.SimulationResult;

import java.util.List;

public interface DailyExchangeInputPort {
    List<SimulationResult> calculateSimulation(SimulationConfig ticker);
}