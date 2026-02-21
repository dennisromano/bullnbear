package org.dennisromano.dailyexchange.infrastructure.rest.dto;

public record DailyExchangeResponse(
    String day,
    String companyName,
    String operationType ,
    String volatility,
    String variation,
    String price,
    String quantity,
    String capital,
    String shockEventTitle,
    String shockEventIntensity
) { }