package org.dennisromano.dailyexchange.domain.ports;

/**
 * Strategy interface for calculating stochastic volatility evolution in financial markets.
 * 
 * <p>This interface defines the contract for volatility engines that model how market
 * volatility (the standard deviation of returns) changes over time. Unlike constant
 * volatility models, these strategies recognize that volatility itself is dynamic and
 * follows its own stochastic process.</p>
 * 
 * <p><b>Why Model Volatility Separately?</b></p>
 * <p>Empirical observations of financial markets show that:</p>
 * <ul>
 *   <li>Volatility clusters: high volatility periods tend to be followed by high volatility</li>
 *   <li>Volatility mean-reverts: extreme volatility levels tend to return to a long-term average</li>
 *   <li>Volatility smiles: options with different strikes imply different volatilities</li>
 *   <li>Leverage effect: volatility tends to increase when prices fall</li>
 * </ul>
 * 
 * <p><b>Common Implementations:</b></p>
 * <ul>
 *   <li><b>Mean-Reverting (Ornstein-Uhlenbeck):</b> Volatility oscillates around a long-term average</li>
 *   <li><b>Heston Model:</b> Square-root diffusion process ensuring positive volatility</li>
 *   <li><b>GARCH:</b> Volatility depends on past returns and volatilities</li>
 *   <li><b>SABR:</b> Stochastic Alpha Beta Rho model for interest rates</li>
 * </ul>
 * 
 * <p><b>Mathematical Foundation:</b></p>
 * <p>Volatility strategies typically model volatility as a stochastic differential equation (SDE):</p>
 * <pre>
 * dσ = κ(σ̄ - σ)dt + ξ dW
 * </pre>
 * where:
 * <ul>
 *   <li>σ = current volatility</li>
 *   <li>σ̄ = long-term mean volatility</li>
 *   <li>κ = speed of mean reversion</li>
 *   <li>ξ = volatility of volatility</li>
 *   <li>dW = Wiener process</li>
 * </ul>
 * 
 * @author Dennis Romano
 * @version 1.0
 * @since 1.0
 * @see org.dennisromano.dailyexchange.infrastructure.mathematics.MeanRevertingVolatilityStrategy
 */
public interface VolatilityStrategy {
    
    /**
     * Calculates the next volatility value based on the current volatility and the stochastic model.
     * 
     * <p>This method advances the volatility by one time step according to the implemented
     * stochastic process. The calculation typically involves:</p>
     * <ol>
     *   <li>Generating random numbers from an appropriate distribution</li>
     *   <li>Applying mean-reversion forces (if applicable)</li>
     *   <li>Adding stochastic fluctuations (volatility of volatility)</li>
     *   <li>Ensuring the result stays within valid bounds (usually positive)</li>
     * </ol>
     * 
     * <p><b>Key Considerations:</b></p>
     * <ul>
     *   <li><b>Positivity:</b> Volatility must remain non-negative. Implementations should
     *       enforce a minimum threshold or use processes that guarantee positivity</li>
     *   <li><b>Stability:</b> The process should be numerically stable and avoid explosions</li>
     *   <li><b>Realism:</b> The dynamics should match empirical volatility behavior</li>
     *   <li><b>Efficiency:</b> Calculation should be fast enough for long simulations</li>
     * </ul>
     * 
     * <p><b>Implementation Notes:</b></p>
     * <ul>
     *   <li>Time step (dt) and model parameters are typically configured at construction</li>
     *   <li>Random number generation should use a seeded generator for reproducibility</li>
     *   <li>Consider using variance (σ²) instead of volatility for some models (e.g., Heston)</li>
     * </ul>
     * 
     * <p><b>Usage Example:</b></p>
     * <pre>
     * VolatilityStrategy strategy = new MeanRevertingVolatilityStrategy(...);
     * double currentVol = 0.20; // 20% annual volatility
     * double nextVol = strategy.calculateNextVolatility(currentVol);
     * // nextVol might be 0.205 or 0.195, depending on random fluctuations
     * </pre>
     * 
     * @param currentSigma the current volatility (standard deviation of returns, typically annual)
     * @return the calculated next volatility after one time step, guaranteed to be non-negative
     * 
     * @throws IllegalArgumentException if currentSigma is negative
     */
    double calculateNextVolatility(double currentSigma);
}
