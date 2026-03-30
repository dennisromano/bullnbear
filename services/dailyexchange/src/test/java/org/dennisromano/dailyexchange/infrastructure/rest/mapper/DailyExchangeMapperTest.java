package org.dennisromano.dailyexchange.infrastructure.rest.mapper;

import org.dennisromano.dailyexchange.domain.model.*;
import org.dennisromano.dailyexchange.domain.ports.MarketReportPort;
import org.dennisromano.dailyexchange.infrastructure.rest.dto.DailyExchangeRequest;
import org.dennisromano.dailyexchange.infrastructure.rest.dto.DailyExchangeResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DailyExchangeMapperTest {

    @Mock
    private MarketReportPort marketReportPort;

    @InjectMocks
    private DailyExchangeMapper mapper;

    @Test
    @DisplayName("Should map DailyExchangeRequest to SimulationConfig correctly")
    void shouldMapToDomain() {
        final DailyExchangeRequest dto = new DailyExchangeRequest(
                365, 252, 100.0, 10000.0,
                0.20, 0.05, 2.0, 0.15, 0.01
        );

        final SimulationConfig config = mapper.toDomain(dto);

        assertNotNull(config);
        assertAll("Mapping fields validation",
                () -> assertEquals(365, config.simulationDays()),
                () -> assertEquals(252, config.tradingDaysPerYear()),
                () -> assertEquals(100.0, config.initialPrice()),
                () -> assertEquals(0.20, config.targetVolatility()),
                () -> assertEquals(0.01, config.shockProbability())
        );
    }

    @Test
    @DisplayName("Should return null when mapping a null request")
    void shouldReturnNullForNullRequest() {
        assertNull(mapper.toDomain(null));
    }

    @Test
    @DisplayName("Should map SimulationResult to DailyExchangeResponse using MarketReportPort")
    void shouldMapToResponse() {
        final MarketState state = new MarketState(1, 105.0, 100.0, 1000.0, 0.20, 0.05);
        final ShockEvent shock = new ShockEvent("Test Shock", 0.1);
        final SimulationResult result = new SimulationResult(state, shock);

        final MarketReport mockedReport = new MarketReport(
                "1", "PROTOCOL", "BULL", "20,00%", "5,00%",
                "€ 105,00", "1.000", "€ 105.000,00", "Test Shock", "0.1"
        );

        when(marketReportPort.generateReport(state, shock)).thenReturn(mockedReport);

        final DailyExchangeResponse response = mapper.toResponse(result);

        assertNotNull(response);
        assertEquals("PROTOCOL", response.companyName());
        assertEquals("BULL", response.operationType());
        assertEquals("€ 105.000,00", response.capital());
        assertEquals("Test Shock", response.shockEventTitle());

        verify(marketReportPort, times(1)).generateReport(state, shock);
    }

    @Test
    @DisplayName("Should return null when mapping a null simulation result")
    void shouldReturnNullForNullResult() {
        assertNull(mapper.toResponse(null));
    }
}