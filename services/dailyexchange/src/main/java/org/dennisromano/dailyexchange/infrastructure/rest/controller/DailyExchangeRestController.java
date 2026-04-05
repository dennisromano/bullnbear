package org.dennisromano.dailyexchange.infrastructure.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.dennisromano.dailyexchange.domain.model.SimulationConfig;
import org.dennisromano.dailyexchange.domain.model.SimulationResult;
import org.dennisromano.dailyexchange.domain.ports.DailyExchangeInputPort;
import org.dennisromano.dailyexchange.infrastructure.rest.dto.DailyExchangeRequest;
import org.dennisromano.dailyexchange.infrastructure.rest.dto.DailyExchangeResponse;
import org.dennisromano.dailyexchange.infrastructure.rest.mapper.DailyExchangeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dailyexchange")
@Tag(name = "Daily Exchange", description = "Core engine for generating synthetic market data")
public class DailyExchangeRestController {

    @Autowired
    private DailyExchangeInputPort dailyExchangeInputPort;

    @Autowired
    private DailyExchangeMapper dailyExchangeMapper;

    @Operation(
        summary = "Run market simulation",
        description = "Executes a stochastic market simulation based on mean-reversion and volatility parameters. Returns a day-by-day list of price and quantity results."
    )
    @ApiResponse(responseCode = "200", description = "Simulation completed successfully")
    @ApiResponse(responseCode = "400", description = "Invalid simulation parameters")
    @PostMapping("/simulate")
    public ResponseEntity<List<DailyExchangeResponse>> getSimulate(@RequestBody DailyExchangeRequest request) {
        final SimulationConfig simulationConfig = dailyExchangeMapper.toDomain(request);
        final List<SimulationResult> data = dailyExchangeInputPort.calculateSimulation(simulationConfig);

        final List<DailyExchangeResponse> response = data.stream()
                .map(result -> dailyExchangeMapper.toResponse(result))
                .toList();

        return ResponseEntity.ok(response);
    }
}