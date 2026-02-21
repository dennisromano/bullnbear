package org.dennisromano.dailyexchange.infrastructure.rest.mapper;

import org.dennisromano.dailyexchange.domain.model.MarketReport;
import org.dennisromano.dailyexchange.domain.model.SimulationConfig;
import org.dennisromano.dailyexchange.domain.model.SimulationResult;
import org.dennisromano.dailyexchange.domain.ports.MarketReportPort;
import org.dennisromano.dailyexchange.infrastructure.rest.dto.DailyExchangeRequest;
import org.dennisromano.dailyexchange.infrastructure.rest.dto.DailyExchangeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DailyExchangeMapper {

    /** Company/protocol identifier displayed in the report header */
    private static final String COMPANY = "PROTOCOL";

    @Autowired
    private MarketReportPort marketReportPort;

    public SimulationConfig toDomain(DailyExchangeRequest dto) {
        if (dto == null) return null;

        return new SimulationConfig(
                dto.simulationDays(),
                dto.tradingDaysPerYear(),
                dto.initialPrice(),
                dto.initialQuantity(),
                dto.targetVolatility(),
                dto.targetMu(),
                dto.kappa(),
                dto.volOfVol(),
                dto.shockProbability()
        );
    }

    public DailyExchangeResponse toResponse(SimulationResult simulationResult) {
        if (simulationResult == null) return null;

        final MarketReport report = marketReportPort.generateReport(simulationResult.state(), simulationResult.shockEvent());

        return new DailyExchangeResponse(
                report.day(),
                COMPANY,
                report.operationType(),
                report.volatility(),
                report.variation(),
                report.price(),
                report.quantity(),
                report.capital(),
                report.shockEventTitle(),
                report.shockEventIntensity()
        );
    }
}
