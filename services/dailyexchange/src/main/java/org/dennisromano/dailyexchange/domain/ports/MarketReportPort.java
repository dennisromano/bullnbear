package org.dennisromano.dailyexchange.domain.ports;

import org.dennisromano.dailyexchange.domain.model.MarketState;

/**
 * Port interface for formatting and reporting market state information.
 * 
 * <p>This interface follows the Hexagonal Architecture (Ports and Adapters) pattern,
 * defining a boundary between the domain logic and the infrastructure layer. It allows
 * the domain to remain independent of specific output formats or reporting mechanisms.</p>
 * 
 * <p>Implementations of this interface are responsible for:</p>
 * <ul>
 *   <li>Converting raw market data into human-readable format</li>
 *   <li>Applying locale-specific formatting (currency, decimals, percentages)</li>
 *   <li>Determining the presentation structure and style</li>
 *   <li>Calculating derived metrics (e.g., total capital, variations)</li>
 * </ul>
 * 
 * <p><b>Example Implementations:</b></p>
 * <ul>
 *   <li>Console reporter (text output)</li>
 *   <li>JSON reporter (API responses)</li>
 *   <li>HTML reporter (web dashboards)</li>
 *   <li>CSV reporter (data export)</li>
 * </ul>
 * 
 * @author Dennis Romano
 * @version 1.0
 * @since 1.0
 */
public interface MarketReportPort {
    
    /**
     * Formats the market state into a human-readable string representation.
     * 
     * <p>This method takes the raw market state data and converts it into a formatted
     * string suitable for display to end users. The exact format depends on the
     * implementation, but typically includes:</p>
     * <ul>
     *   <li>Current price with currency formatting</li>
     *   <li>Price variation as percentage</li>
     *   <li>Current volatility as percentage</li>
     *   <li>Quantity with appropriate number formatting</li>
     *   <li>Total market capital (price × quantity)</li>
     *   <li>Market trend indicators (BULL/BEAR)</li>
     * </ul>
     * 
     * @param state the market state to format (must not be null)
     * @return a formatted string representation ready for display
     * 
     * @throws NullPointerException if state is null (implementation-dependent)
     */
    String format(MarketState state);
}
