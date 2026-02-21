package org.dennisromano.dailyexchange.infrastructure.rest.service;

import org.dennisromano.dailyexchange.application.MarketSimulator;
import org.dennisromano.dailyexchange.domain.model.MarketState;
import org.dennisromano.dailyexchange.domain.model.SimulationConfig;
import org.dennisromano.dailyexchange.domain.model.SimulationResult;
import org.dennisromano.dailyexchange.domain.ports.*;
import org.dennisromano.dailyexchange.infrastructure.generators.StochasticShockProvider;
import org.dennisromano.dailyexchange.infrastructure.mathematics.GeometricBrownianMotionStrategy;
import org.dennisromano.dailyexchange.infrastructure.mathematics.MeanRevertingVolatilityStrategy;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

@Service
public class DailyExchangeSimulator implements DailyExchangeInputPort {

    @Override
    public List<SimulationResult> calculateSimulation(SimulationConfig simulationConfig) {
        final MarketSimulator simulator = getMarketSimulator(simulationConfig);

        final MarketState state = new MarketState(
                0,
                simulationConfig.initialPrice(),
                simulationConfig.initialPrice(),
                simulationConfig.initialQuantity(),
                simulationConfig.targetVolatility(),
                simulationConfig.targetMu()
        );

        final List<SimulationResult> simulationResults = new ArrayList<>();
        for (int day = 1; day <= simulationConfig.simulationDays(); day++) {
            final SimulationResult result = simulator.next(day, state);
            simulationResults.add(result);
        }

        return simulationResults;
    }

    private static @NonNull MarketSimulator getMarketSimulator(SimulationConfig simulationConfig) {
        final RandomGenerator rng = RandomGenerator.getDefault();
        final double dt = 1.0 / simulationConfig.tradingDaysPerYear();

        // Create mathematical engines (using interfaces for flexibility)
        final VolatilityStrategy volatilityStrategy = new MeanRevertingVolatilityStrategy(
                simulationConfig.targetVolatility(),
                simulationConfig.kappa(),
                simulationConfig.volOfVol(),
                dt,
                rng
        );

        final PricingStrategy pricingStrategy = new GeometricBrownianMotionStrategy(dt, rng);
        final ShockProvider shockProvider = new StochasticShockProvider(simulationConfig.shockProbability(), rng);

        return new MarketSimulator(
                volatilityStrategy,
                pricingStrategy,
                shockProvider,
                rng,
                simulationConfig.targetMu()
        );
    }
}

