package org.dennisromano.dailyexchange.infrastructure.reporting;

import org.dennisromano.dailyexchange.domain.model.MarketReport;
import org.dennisromano.dailyexchange.domain.model.MarketState;
import org.dennisromano.dailyexchange.domain.model.ShockEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MarketReporterTest {

    private MarketReporter reporter;

    @BeforeEach
    void setUp() {
        reporter = new MarketReporter();
    }

    @Test
    @DisplayName("Should format positive variation as BULL using Italian locale")
    void shouldFormatPositiveVariationAsBull() {
        final MarketState state = new MarketState(42, 110.0, 100.0, 5000.0, 0.20, 0.05);
        final MarketReport report = reporter.generateReport(state, null);

        assertAll("BULL Market Report Validation",
                () -> assertEquals("42", report.day()),
                () -> assertEquals("BULL", report.operationType()),
                () -> assertEquals("20,00%", report.volatility(), "Volatility format mismatch"),
                () -> assertEquals("10,00%", report.variation(), "Price variation format mismatch"),
                () -> assertTrue(report.price().contains("110,00"), "Price should be formatted in Euro"),
                () -> assertEquals("5.000", report.quantity(), "Quantity should use Italian thousands separator")
        );
    }

    @Test
    @DisplayName("Should format negative variation as BEAR")
    void shouldFormatNegativeVariationAsBear() {
        final MarketState state = new MarketState(43, 90.0, 100.0, 5000.0, 0.25, 0.05);
        final MarketReport report = reporter.generateReport(state, null);

        assertEquals("BEAR", report.operationType());
        assertEquals("-10,00%", report.variation());
    }

    @Test
    @DisplayName("Should calculate capital correctly (Price * Quantity)")
    void shouldCalculateCapitalization() {
        final MarketState state = new MarketState(1, 10.50, 10.00, 2000000.0, 0.15, 0.08);
        final MarketReport report = reporter.generateReport(state, null);

        assertTrue(report.capital().contains("21.000.000,00"));
    }

    @Test
    @DisplayName("Should include shock event details when provided")
    void shouldIncludeShockEventDetails() {
        final MarketState state = new MarketState(1, 100.0, 100.0, 1000.0, 0.20, 0.05);
        final ShockEvent shock = new ShockEvent("Pandemic Shock", 0.50);
        final MarketReport report = reporter.generateReport(state, shock);

        assertEquals("Pandemic Shock", report.shockEventTitle());
        assertEquals("0.5", report.shockEventIntensity());
    }

    @Test
    @DisplayName("Should handle edge case: zero price variation")
    void shouldHandleZeroVariation() {
        final MarketState state = new MarketState(5, 100.0, 100.0, 1000.0, 0.15, 0.05);
        final MarketReport report = reporter.generateReport(state, null);

        assertEquals("BULL", report.operationType());
        assertEquals("0,00%", report.variation());
    }
}