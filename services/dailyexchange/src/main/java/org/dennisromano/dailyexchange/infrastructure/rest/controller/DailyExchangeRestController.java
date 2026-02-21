package org.dennisromano.dailyexchange.infrastructure.rest.controller;

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
public class DailyExchangeRestController {

    @Autowired
    private DailyExchangeInputPort dailyExchangeInputPort;

    @Autowired
    private DailyExchangeMapper dailyExchangeMapper;

    @PostMapping(value = "/simulate")
    public ResponseEntity<List<DailyExchangeResponse>> getSimulate(@RequestBody DailyExchangeRequest request) {
        final SimulationConfig simulationConfig = dailyExchangeMapper.toDomain(request);
        final List<SimulationResult> data = dailyExchangeInputPort.calculateSimulation(simulationConfig);

        final List<DailyExchangeResponse> response = data.stream()
                .map(result -> dailyExchangeMapper.toResponse(result))
                .toList();

        return ResponseEntity.ok(response);
    }
}