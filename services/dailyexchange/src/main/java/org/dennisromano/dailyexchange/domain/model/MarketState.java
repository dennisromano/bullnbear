package org.dennisromano.dailyexchange.domain.model;

/**
 * Represents the complete state of the market at a specific point in time.
 * 
 * <p>This immutable record encapsulates all relevant market information needed for
 * simulation and reporting, including price, quantity, volatility, and drift parameters.</p>
 * 
 * <p>The market state maintains both current and previous price to enable calculation
 * of price variations, which are essential for determining market trends (bull/bear).</p>
 * 
 * <p><b>Invariants:</b></p>
 * <ul>
 *   <li>Price must be positive (price &gt; 0)</li>
 *   <li>Previous price must be positive (previousPrice &gt; 0)</li>
 *   <li>Quantity cannot be negative (quantity ≥ 0)</li>
 *   <li>Volatility (sigma) cannot be negative (sigma ≥ 0)</li>
 * </ul>
 * 
 * @param price the current market price per unit
 * @param previousPrice the previous market price (from the last time step)
 * @param quantity the current quantity of assets in circulation
 * @param sigma the current volatility (standard deviation of returns)
 * @param mu the current drift (expected return rate)
 * 
 * @author Dennis Romano
 * @version 1.0
 * @since 1.0
 */
public record MarketState(
        int day,
        double price,
        double previousPrice,
        double quantity,
        double sigma,
        double mu
) {
    /**
     * Canonical constructor with validation.
     * 
     * <p>This constructor is automatically called when creating a new MarketState
     * and enforces all invariants to ensure data integrity.</p>
     * 
     * @throws IllegalArgumentException if price is not positive
     * @throws IllegalArgumentException if previousPrice is not positive
     * @throws IllegalArgumentException if quantity is negative
     * @throws IllegalArgumentException if sigma is negative
     */
    public MarketState {
        if(day < 0) throw new IllegalArgumentException("Day cannot be negative");
        if (price <= 0) throw new IllegalArgumentException("Price must be positive");
        if (previousPrice <= 0) throw new IllegalArgumentException("Previous price must be positive");
        if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative");
        if (sigma < 0) throw new IllegalArgumentException("Sigma cannot be negative");
    }

    /**
     * Calculates the price variation (return) from the previous time step.
     * 
     * <p>The variation is calculated as the relative change in price:</p>
     * <pre>
     * variation = (price - previousPrice) / previousPrice
     * </pre>
     * 
     * <p>A positive variation indicates a price increase (bull market),
     * while a negative variation indicates a price decrease (bear market).</p>
     * 
     * @return the price variation as a decimal (e.g., 0.05 for 5% increase, -0.03 for 3% decrease)
     */
    public double priceVariation() {
        return (price - previousPrice) / previousPrice;
    }
}
