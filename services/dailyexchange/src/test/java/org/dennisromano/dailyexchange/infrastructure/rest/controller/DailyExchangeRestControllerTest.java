package org.dennisromano.dailyexchange.infrastructure.rest.controller;

import org.dennisromano.dailyexchange.domain.model.SimulationResult;
import org.dennisromano.dailyexchange.domain.ports.DailyExchangeInputPort;
import org.dennisromano.dailyexchange.infrastructure.rest.dto.DailyExchangeRequest;
import org.dennisromano.dailyexchange.infrastructure.rest.dto.DailyExchangeResponse;
import org.dennisromano.dailyexchange.infrastructure.rest.mapper.DailyExchangeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DailyExchangeRestController.class)
class DailyExchangeRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DailyExchangeInputPort dailyExchangeInputPort;

    @MockitoBean
    private DailyExchangeMapper dailyExchangeMapper;

    private DailyExchangeRequest defaultBody;

    @BeforeEach
    void setUp() {
        defaultBody = new DailyExchangeRequest(30, 252.0, 100.0, 2000000.0, 0.20, 0.08, 2.0, 0.15, 0.001);
        when(dailyExchangeInputPort.calculateSimulation(any())).thenReturn(List.of());
    }

    @Test
    @DisplayName("POST /simulate -> Should return 200 OK")
    void shouldReturn200() throws Exception {
        mockMvc.perform(post("/api/v1/dailyexchange/simulate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(defaultBody)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("POST /simulate -> Should return empty list when service returns empty")
    void shouldReturnEmptyList() throws Exception {
        mockMvc.perform(post("/api/v1/dailyexchange/simulate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(defaultBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("POST /simulate -> Should map results and return populated list")
    void shouldReturnPopulatedList() throws Exception {
        // 1. Prepariamo i dati di mock
        // Simuliamo che il mapper restituisca una configurazione (opzionale se usi any())
        // Simuliamo che il service restituisca un risultato
        SimulationResult mockResult = mock(SimulationResult.class);
        when(dailyExchangeInputPort.calculateSimulation(any()))
                .thenReturn(List.of(mockResult));

        // 2. Mockiamo la chiamata dentro la lambda (fondamentale!)
        DailyExchangeResponse mockResponse = new DailyExchangeResponse("1", "PROTOCOL", "BULL", "0.01", "2.01", "102.1", "2000200.0", "204_220_420", "", "");
        when(dailyExchangeMapper.toResponse(mockResult)).thenReturn(mockResponse);

        // 3. Esecuzione
        mockMvc.perform(post("/api/v1/dailyexchange/simulate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(defaultBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1)); // Ora la lista ha 1 elemento

        // 4. Verifica che la lambda sia stata usata
        verify(dailyExchangeMapper).toResponse(mockResult);
    }
}