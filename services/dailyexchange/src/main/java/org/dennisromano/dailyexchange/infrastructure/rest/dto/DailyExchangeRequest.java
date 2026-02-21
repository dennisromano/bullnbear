package org.dennisromano.dailyexchange.infrastructure.rest.dto;

public record DailyExchangeRequest(
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