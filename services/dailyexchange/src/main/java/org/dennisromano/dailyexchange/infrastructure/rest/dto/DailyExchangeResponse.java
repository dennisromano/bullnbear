package org.dennisromano.dailyexchange.infrastructure.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents the state of the market for a specific simulation day")
public record DailyExchangeResponse(
        @Schema(description = "The simulation day (e.g., Day 1, Day 2)", example = "Day 42")
        String day,

        @Schema(description = "Name of the simulated company", example = "PROTOCOL")
        String companyName,

        @Schema(description = "Type of market operation performed", example = "BULL")
        String operationType,

        @Schema(description = "Current market volatility level", example = "0.215")
        String volatility,

        @Schema(description = "Price variation compared to the previous day", example = "+1.25%")
        String variation,

        @Schema(description = "Calculated asset price for the current day", example = "105.42")
        String price,

        @Schema(description = "Total quantity of assets traded or available", example = "1000.0")
        String quantity,

        @Schema(description = "Total market capitalization or position value", example = "105420.0")
        String capital,

        @Schema(description = "Title of the shock event, if any occurred", example = "Central Bank Rate Hike")
        String shockEventTitle,

        @Schema(description = "Intensity/Impact of the shock event", example = "0.58")
        String shockEventIntensity
) { }