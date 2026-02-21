package org.dennisromano.dailyexchange.infrastructure.reporting;

import org.dennisromano.dailyexchange.domain.model.MarketReport;
import org.dennisromano.dailyexchange.domain.model.MarketState;
import org.dennisromano.dailyexchange.domain.model.ShockEvent;
import org.dennisromano.dailyexchange.domain.ports.MarketReportPort;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

/**
 * Console-based market state formatter using Italian locale conventions.
 * 
 * <p>This implementation of {@link MarketReportPort} formats market data for console output
 * using Italian number formatting conventions (Euro currency, comma for decimals, period for
 * thousands separator).</p>
 * 
 * <p>The formatted output includes:</p>
 * <ul>
 *   <li><b>Market Trend:</b> BULL (price increase) or BEAR (price decrease)</li>
 *   <li><b>Volatility:</b> Current market volatility as a percentage</li>
 *   <li><b>Variation:</b> Price change from previous period as a percentage</li>
 *   <li><b>Price:</b> Current price per unit in Euros</li>
 *   <li><b>Quantity:</b> Number of units in circulation</li>
 *   <li><b>Capital:</b> Total market capitalization (price × quantity)</li>
 * </ul>
 * 
 * <h2>Example Output</h2>
 * <pre>
 * day: 1085
 * Volatility: 21,35%
 * Variation: 1,24%
 * Price: € 103,45
 * Quantity: 2.045.320
 * Capital: € 211.588.344,00
 * </pre>
 * 
 * <h2>Thread Safety</h2>
 * <p>This class is thread-safe. Number formatters are stored in {@link ThreadLocal}
 * variables to ensure each thread has its own formatter instance, avoiding
 * synchronization issues.</p>
 * 
 * @author Dennis Romano
 * @version 1.0
 * @since 1.0
 * @see MarketReportPort
 * @see MarketState
 */
@Component
public class MarketReporter implements MarketReportPort {
    
    /**
     * Thread-local currency formatter for Italian locale.
     * Uses Euro symbol and Italian decimal/thousands separators.
     */
    private static final ThreadLocal<NumberFormat> CURRENCY_FORMAT = ThreadLocal.withInitial(() -> NumberFormat.getCurrencyInstance(Locale.ITALY));

    /**
     * Thread-local number formatter for Italian locale.
     * Uses Italian decimal/thousands separators for quantity display.
     */
    private static final ThreadLocal<NumberFormat> NUMBER_FORMAT = ThreadLocal.withInitial(() -> NumberFormat.getNumberInstance(Locale.ITALY));

    /**
     * Thread-local percentage formatter for Italian locale.
     * Configured to always show exactly 2 decimal places.
     */
    private static final ThreadLocal<NumberFormat> PERCENT_FORMAT = ThreadLocal.withInitial(() -> {
        NumberFormat nf = NumberFormat.getPercentInstance(Locale.ITALY);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        return nf;
    });

    /**
     * Formats the market state into a human-readable console report.
     * 
     * <p>The formatting process:</p>
     * <ol>
     *   <li><b>Determine Market Trend:</b> Calculate price variation and classify as BULL or BEAR</li>
     *   <li><b>Calculate Capital:</b> Multiply price by quantity for total market cap</li>
     *   <li><b>Format Numbers:</b> Apply locale-specific formatting to all values</li>
     *   <li><b>Build Report:</b> Construct multi-line string with all formatted data</li>
     * </ol>
     * 
     * <p><b>Market Trend Classification:</b></p>
     * <ul>
     *   <li><b>BULL:</b> Price variation ≥ 0 (price increased or stayed same)</li>
     *   <li><b>BEAR:</b> Price variation &lt; 0 (price decreased)</li>
     * </ul>
     * 
     * <p><b>Number Formatting Examples:</b></p>
     * <ul>
     *   <li>Currency: 103.45 → "€ 103,45" or 1234567.89 → "€ 1.234.567,89"</li>
     *   <li>Number: 2045320 → "2.045.320"</li>
     *   <li>Percent: 0.2135 → "21,35%" or -0.0124 → "-1,24%"</li>
     * </ul>
     * 
     * <p><b>Thread Safety Note:</b></p>
     * <p>This method is thread-safe because it uses thread-local formatters.
     * Each thread gets its own formatter instances, preventing concurrent
     * modification issues.</p>
     * 
     * @param state the market state to format (must not be null)
     * @return a formatted string containing all market information, ready for report
     * 
     * @throws NullPointerException if state is null
     */
    @Override
    public MarketReport generateReport(MarketState state, ShockEvent shockEvent) {
        final NumberFormat currency = CURRENCY_FORMAT.get();
        final NumberFormat number = NUMBER_FORMAT.get();
        final NumberFormat percent = PERCENT_FORMAT.get();

        // Calculate the ACTUAL price variation from the previous step
        final double priceVariation = state.priceVariation();
        
        // Determine if it's a BULL market (growth) or BEAR market (decline) based on variation
        final String operationType = priceVariation >= 0 ? "BULL" : "BEAR";
        
        // Calculate market capitalization (price × quantity)
        final double capital = state.price() * state.quantity();

        // Format all values according to Italian locale
        final String day = String.valueOf(state.day());
        final String outCapital = currency.format(capital);
        final String outPrice = currency.format(state.price());
        final String outQuantity = number.format(state.quantity());
        final String outVariation = percent.format(priceVariation);
        final String outSigma = percent.format(state.sigma());

        final Optional<ShockEvent> shock = Optional.ofNullable(shockEvent);
        final String shockEventTitle = shock.map(ShockEvent::title).orElse(null);
        final String shockEventIntensity = shock.map(s -> String.valueOf(s.intensity())).orElse(null);

        return new MarketReport(
                day,
                "PROTOCOL",
                operationType,
                outSigma,
                outVariation,
                outPrice,
                outQuantity,
                outCapital,
                shockEventTitle,
                shockEventIntensity
        );
    }
}
