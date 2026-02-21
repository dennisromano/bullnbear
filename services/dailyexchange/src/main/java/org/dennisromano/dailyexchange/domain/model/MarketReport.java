package org.dennisromano.dailyexchange.domain.model;

public record MarketReport(
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
) {}