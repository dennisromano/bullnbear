package org.dennisromano.dailyexchange.domain.ports;

/**
 * Strategy interface for calculating stochastic price evolution in financial markets.
 * 
 * <p>This interface defines the contract for pricing engines that model how asset prices
 * change over time using mathematical stochastic processes. It follows the Strategy pattern,
 * allowing different pricing models to be plugged into the simulation without changing
 * the core domain logic.</p>
 * 
 * <p><b>Common Implementations:</b></p>
 * <ul>
 *   <li><b>Geometric Brownian Motion (GBM):</b> The classic model used in the Black-Scholes
 *       framework, assuming log-normal price distribution</li>
 *   <li><b>Jump Diffusion:</b> Extension of GBM with discontinuous jumps</li>
 *   <li><b>Heston Model:</b> Stochastic volatility model</li>
 *   <li><b>GARCH:</b> Generalized AutoRegressive Conditional Heteroskedasticity</li>
 * </ul>
 * 
 * <p><b>Mathematical Foundation:</b></p>
 * <p>Most pricing strategies are based on stochastic differential equations (SDEs) of the form:</p>
 * <pre>
 * dS = μS dt + σS dW
 * </pre>
 * where:
 * <ul>
 *   <li>S = asset price</li>
 *   <li>μ = drift (expected return)</li>
 *   <li>σ = volatility (standard deviation)</li>
 *   <li>dW = Wiener process (random walk)</li>
 * </ul>
 * 
 * @author Dennis Romano
 * @version 1.0
 * @since 1.0
 * @see org.dennisromano.dailyexchange.infrastructure.mathematics.GeometricBrownianMotionStrategy
 */
public interface PricingStrategy {
    
    /**
     * Calculates the next price based on the current price and stochastic parameters.
     * 
     * <p>This method advances the price by one time step according to the implemented
     * stochastic model. The calculation typically involves:</p>
     * <ol>
     *   <li>Generating random numbers from an appropriate distribution</li>
     *   <li>Applying the drift component (deterministic trend)</li>
     *   <li>Applying the volatility component (random fluctuation)</li>
     *   <li>Combining them according to the model's formula</li>
     * </ol>
     * 
     * <p><b>Implementation Notes:</b></p>
     * <ul>
     *   <li>The method should be thread-safe if used in concurrent contexts</li>
     *   <li>Random number generation should use a provided RandomGenerator for reproducibility</li>
     *   <li>The returned price should always be positive (typical constraint for asset prices)</li>
     *   <li>Time step (dt) is usually configured at construction time, not passed to this method</li>
     * </ul>
     * 
     * @param currentPrice the current asset price (must be positive)
     * @param mu the drift parameter (expected return rate, typically annual)
     * @param sigma the volatility parameter (standard deviation of returns, typically annual)
     * @return the calculated next price after one time step
     * 
     * @throws IllegalArgumentException if currentPrice is not positive
     * @throws IllegalArgumentException if mu or sigma have invalid values (implementation-dependent)
     */
    double calculateNextPrice(double currentPrice, double mu, double sigma);
}
