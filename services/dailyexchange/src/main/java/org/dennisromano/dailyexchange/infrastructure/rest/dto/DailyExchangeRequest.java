package org.dennisromano.dailyexchange.infrastructure.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
   name = "DailyExchangeRequest",
   description = "Parameters for the stochastic market simulation engine (Heston-like model with shock events)"
)
public record DailyExchangeRequest(

   @Schema(description = "Number of trading days to simulate", example = "252")
   int simulationDays,

   @Schema(description = "Used to compute the time step dt = 1/tradingDaysPerYear", example = "252.0")
   double tradingDaysPerYear,

   @Schema(description = "Starting asset price", example = "100.0")
   double initialPrice,

   @Schema(description = "Starting asset quantity", example = "1000.0")
   double initialQuantity,

   @Schema(description = "Long-run target volatility (σ̄)", example = "0.2")
   double targetVolatility,

   @Schema(description = "Target annual drift (expected return)", example = "0.07")
   double targetMu,

   @Schema(description = "Mean-reversion speed for volatility", example = "3.0")
   double kappa,

   @Schema(description = "Volatility of volatility", example = "0.3")
   double volOfVol,

   @Schema(description = "Daily probability of a shock event (0.0–1.0)", example = "0.02")
   double shockProbability
) { }