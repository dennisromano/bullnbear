package org.dennisromano.dailyexchange.domain.model;

public record SimulationConfig(
    int simulationDays,
    double tradingDaysPerYear,
    double initialPrice,
    double initialQuantity,
    double targetVolatility,
    double targetMu,
    double kappa,
    double volOfVol,
    double shockProbability
) { }