package org.dennisromano.dailyexchange.infrastructure.mathematics;

import org.dennisromano.dailyexchange.domain.ports.PricingStrategy;

import java.util.random.RandomGenerator;

/**
 * Implementation of the Geometric Brownian Motion (GBM) pricing model.
 * 
 * <p>Geometric Brownian Motion is the fundamental stochastic process used to model
 * stock prices in the famous Black-Scholes option pricing framework. It assumes that
 * the logarithmic returns of the asset follow a normal distribution.</p>
 * 
 * <h2>Mathematical Formula</h2>
 * <p>The price evolution is given by:</p>
 * <pre>
 * S(t+dt) = S(t) × exp((μ - 0.5σ²)dt + σ√dt·ε)
 * </pre>
 * 
 * <p>where:</p>
 * <ul>
 *   <li><b>S(t)</b> = asset price at time t</li>
 *   <li><b>μ</b> = drift (expected annual return rate)</li>
 *   <li><b>σ</b> = volatility (annual standard deviation of returns)</li>
 *   <li><b>dt</b> = time step (in years, typically 1/252 for daily steps)</li>
 *   <li><b>ε</b> = random shock from standard normal distribution N(0,1)</li>
 * </ul>
 * 
 * <h2>Key Properties</h2>
 * <ul>
 *   <li><b>Log-normality:</b> Prices are always positive, returns are normally distributed</li>
 *   <li><b>Itô Correction:</b> The -0.5σ² term ensures E[S(t+dt)] = S(t)e^(μdt)</li>
 *   <li><b>Independence:</b> Returns in different time periods are independent</li>
 *   <li><b>Continuous paths:</b> No jumps or discontinuities</li>
 * </ul>
 * 
 * <h2>Why the -0.5σ² Term?</h2>
 * <p>This "Itô correction" compensates for the convexity of the exponential function.
 * Without it, the expected value of the price would be biased upward. The correction
 * ensures that the expected price grows at exactly the drift rate μ.</p>
 * 
 * <h2>Limitations</h2>
 * <ul>
 *   <li>Assumes constant volatility (not realistic for real markets)</li>
 *   <li>No jumps or sudden price changes</li>
 *   <li>Returns are independent (ignores autocorrelation)</li>
 *   <li>Normal distribution (real returns have fat tails)</li>
 * </ul>
 * 
 * @author Dennis Romano
 * @version 1.0
 * @since 1.0
 * @see PricingStrategy
 */
public class GeometricBrownianMotionStrategy implements PricingStrategy {

    /** Time step in years (e.g., 1/252 for daily steps) */
    private final double dt;
    
    /** Random number generator for Gaussian shocks */
    private final RandomGenerator rng;

    /**
     * Constructs a new Geometric Brownian Motion pricing strategy.
     * 
     * <p><b>Typical Time Steps:</b></p>
     * <ul>
     *   <li>Daily: dt = 1/252 (252 trading days per year)</li>
     *   <li>Weekly: dt = 1/52</li>
     *   <li>Monthly: dt = 1/12</li>
     *   <li>Hourly: dt = 1/(252×6.5) for 6.5 hour trading days</li>
     * </ul>
     * 
     * @param dt the time step in years (must be positive)
     * @param rng the random number generator for Gaussian shocks
     * 
     * @throws IllegalArgumentException if dt is not positive
     */
    public GeometricBrownianMotionStrategy(double dt, RandomGenerator rng) {
        if (dt <= 0) {
            throw new IllegalArgumentException("dt must be positive");
        }
        this.dt = dt;
        this.rng = rng;
    }

    /**
     * Calculates the next price using the Geometric Brownian Motion formula.
     * 
     * <p>The calculation proceeds in three steps:</p>
     * <ol>
     *   <li><b>Generate random shock:</b> Draw ε from N(0,1)</li>
     *   <li><b>Calculate drift component:</b> (μ - 0.5σ²)dt (deterministic trend)</li>
     *   <li><b>Calculate volatility component:</b> σ√dt·ε (random fluctuation)</li>
     *   <li><b>Apply exponential:</b> S_new = S_current × exp(drift + volatility)</li>
     * </ol>
     * 
     * <p><b>Example Calculation:</b></p>
     * <pre>
     * S = 100, μ = 0.08, σ = 0.20, dt = 1/252
     * drift = (0.08 - 0.5×0.20²) × 1/252 = 0.0002381
     * shock = 0.20 × √(1/252) × ε ≈ 0.0126 × ε
     * S_new = 100 × exp(0.0002381 + 0.0126×ε)
     * 
     * If ε = 1.5 (positive shock):
     * S_new = 100 × exp(0.0002381 + 0.0189) ≈ 101.91
     * 
     * If ε = -1.5 (negative shock):
     * S_new = 100 × exp(0.0002381 - 0.0189) ≈ 98.15
     * </pre>
     * 
     * <p><b>Implementation Details:</b></p>
     * <ul>
     *   <li>Uses {@link RandomGenerator#nextGaussian()} for standard normal samples</li>
     *   <li>Computes both components separately for clarity and debugging</li>
     *   <li>Returns exp(drift + shock) to ensure positivity</li>
     * </ul>
     * 
     * @param currentPrice the current asset price (must be positive)
     * @param mu the drift (expected annual return, e.g., 0.08 for 8%)
     * @param sigma the volatility (annual standard deviation, e.g., 0.20 for 20%)
     * @return the calculated next price, guaranteed to be positive
     * 
     * @throws IllegalArgumentException if currentPrice is not positive
     */
    @Override
    public double calculateNextPrice(double currentPrice, double mu, double sigma) {
        if (currentPrice <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        // Generate Gaussian shock (standard normal distribution)
        double epsilon = rng.nextGaussian();

        // Deterministic component (drift with Itô correction)
        double drift = (mu - 0.5 * sigma * sigma) * dt;
        
        // Stochastic component (volatility shock)
        double shock = sigma * Math.sqrt(dt) * epsilon;

        // Apply the GBM formula
        return currentPrice * Math.exp(drift + shock);
    }
}
