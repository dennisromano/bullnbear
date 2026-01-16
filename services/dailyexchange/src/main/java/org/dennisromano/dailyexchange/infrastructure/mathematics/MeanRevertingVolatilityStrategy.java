package org.dennisromano.dailyexchange.infrastructure.mathematics;

import org.dennisromano.dailyexchange.domain.ports.VolatilityStrategy;

import java.util.random.RandomGenerator;

/**
 * Implementation of a Mean-Reverting volatility model based on the Ornstein-Uhlenbeck process.
 * 
 * <p>This strategy models volatility as a stochastic process that tends to revert to a
 * long-term average level over time. This captures the empirical observation that extreme
 * volatility levels (both high and low) are temporary and markets eventually return to
 * "normal" volatility conditions.</p>
 * 
 * <h2>Mathematical Formula</h2>
 * <p>The volatility evolution follows:</p>
 * <pre>
 * σ(t+dt) = σ(t) + κ(σ̄ - σ(t))dt + ξ√dt·ε
 * </pre>
 * 
 * <p>where:</p>
 * <ul>
 *   <li><b>σ(t)</b> = current volatility</li>
 *   <li><b>σ̄</b> = long-term target volatility (equilibrium level)</li>
 *   <li><b>κ</b> = speed of mean reversion (higher = faster return to target)</li>
 *   <li><b>ξ</b> = volatility of volatility (how much volatility itself fluctuates)</li>
 *   <li><b>dt</b> = time step in years</li>
 *   <li><b>ε</b> = random shock from standard normal distribution N(0,1)</li>
 * </ul>
 * 
 * <h2>Key Properties</h2>
 * <ul>
 *   <li><b>Mean Reversion:</b> Volatility gravitates toward the target level</li>
 *   <li><b>Stochastic:</b> Random fluctuations prevent deterministic convergence</li>
 *   <li><b>Half-Life:</b> Time to revert halfway ≈ ln(2)/κ</li>
 *   <li><b>Stationary Distribution:</b> Long-term volatility distribution is normal around σ̄</li>
 * </ul>
 * 
 * <h2>Understanding the Components</h2>
 * <ul>
 *   <li><b>κ(σ̄ - σ)dt:</b> The "pull" toward the target. If σ &gt; σ̄, this is negative
 *       (pulling down). If σ &lt; σ̄, this is positive (pulling up).</li>
 *   <li><b>ξ√dt·ε:</b> Random noise that keeps volatility from settling exactly at σ̄.
 *       This captures the unpredictable nature of market volatility.</li>
 * </ul>
 * 
 * <h2>Parameter Guidelines</h2>
 * <ul>
 *   <li><b>σ̄ (target):</b> 0.15-0.25 for equities (15-25% annual volatility)</li>
 *   <li><b>κ (speed):</b> 1.0-5.0 (higher means faster mean reversion)
 *     <ul>
 *       <li>κ = 2.0 → half-life ≈ 4 months</li>
 *       <li>κ = 5.0 → half-life ≈ 1.5 months</li>
 *     </ul>
 *   </li>
 *   <li><b>ξ (vol of vol):</b> 0.10-0.30 (typically similar to σ̄)</li>
 * </ul>
 * 
 * <h2>Example Behavior</h2>
 * <pre>
 * Suppose: σ̄ = 0.20, κ = 2.0, ξ = 0.15, dt = 1/252
 * 
 * If current σ = 0.40 (high volatility):
 *   Mean reversion pull = 2.0 × (0.20 - 0.40) × 1/252 = -0.0016
 *   → Volatility is pulled down toward 0.20
 * 
 * If current σ = 0.10 (low volatility):
 *   Mean reversion pull = 2.0 × (0.20 - 0.10) × 1/252 = +0.0008
 *   → Volatility is pulled up toward 0.20
 * </pre>
 * 
 * @author Dennis Romano
 * @version 1.0
 * @since 1.0
 * @see VolatilityStrategy
 */
public class MeanRevertingVolatilityStrategy implements VolatilityStrategy {

    /** Long-term target volatility level (σ̄) */
    private final double targetSigma;
    
    /** Speed of mean reversion (κ) - how fast volatility returns to target */
    private final double kappa;
    
    /** Volatility of volatility (ξ) - how much volatility itself fluctuates */
    private final double volOfVol;
    
    /** Time step in years */
    private final double dt;
    
    /** Random number generator for volatility shocks */
    private final RandomGenerator rng;
    
    /** Minimum volatility floor to prevent negative or unrealistically low values (5%) */
    private static final double MIN_VOLATILITY = 0.05;

    /**
     * Constructs a new Mean-Reverting volatility strategy.
     * 
     * <p><b>Recommended Parameter Combinations:</b></p>
     * <table border="1">
     *   <tr>
     *     <th>Market Type</th>
     *     <th>targetSigma</th>
     *     <th>kappa</th>
     *     <th>volOfVol</th>
     *   </tr>
     *   <tr>
     *     <td>Low-volatility stocks</td>
     *     <td>0.15</td>
     *     <td>2.0</td>
     *     <td>0.10</td>
     *   </tr>
     *   <tr>
     *     <td>Average equities</td>
     *     <td>0.20</td>
     *     <td>2.0</td>
     *     <td>0.15</td>
     *   </tr>
     *   <tr>
     *     <td>High-volatility assets</td>
     *     <td>0.30</td>
     *     <td>1.5</td>
     *     <td>0.20</td>
     *   </tr>
     *   <tr>
     *     <td>Cryptocurrencies</td>
     *     <td>0.50</td>
     *     <td>1.0</td>
     *     <td>0.30</td>
     *   </tr>
     * </table>
     * 
     * @param targetSigma the long-term target volatility (σ̄, must be positive)
     * @param kappa the speed of mean reversion (κ, must be non-negative, typically 1-5)
     * @param volOfVol the volatility of volatility (ξ, must be non-negative)
     * @param dt the time step in years (must be positive, typically 1/252 for daily)
     * @param rng the random number generator for volatility shocks
     * 
     * @throws IllegalArgumentException if targetSigma is not positive
     * @throws IllegalArgumentException if kappa is negative
     * @throws IllegalArgumentException if volOfVol is negative
     * @throws IllegalArgumentException if dt is not positive
     */
    public MeanRevertingVolatilityStrategy(
            double targetSigma,
            double kappa,
            double volOfVol,
            double dt,
            RandomGenerator rng) {
        
        if (targetSigma <= 0) {
            throw new IllegalArgumentException("targetSigma must be positive");
        }
        if (kappa < 0) {
            throw new IllegalArgumentException("kappa cannot be negative");
        }
        if (volOfVol < 0) {
            throw new IllegalArgumentException("volOfVol cannot be negative");
        }
        if (dt <= 0) {
            throw new IllegalArgumentException("dt must be positive");
        }
        
        this.targetSigma = targetSigma;
        this.kappa = kappa;
        this.volOfVol = volOfVol;
        this.dt = dt;
        this.rng = rng;
    }

    /**
     * Calculates the next volatility value using the mean-reverting process.
     * 
     * <p>The calculation proceeds in three steps:</p>
     * <ol>
     *   <li><b>Generate random shock:</b> Draw ε from N(0,1) and scale by ξ√dt</li>
     *   <li><b>Calculate mean reversion:</b> κ(σ̄ - σ)dt (pull toward target)</li>
     *   <li><b>Update volatility:</b> σ_new = σ + mean_reversion + shock</li>
     *   <li><b>Apply floor:</b> Ensure σ_new ≥ MIN_VOLATILITY</li>
     * </ol>
     * 
     * <p><b>Example Calculation:</b></p>
     * <pre>
     * σ = 0.30, σ̄ = 0.20, κ = 2.0, ξ = 0.15, dt = 1/252
     * 
     * shock = nextGaussian() × 0.15 × √(1/252)
     *       ≈ nextGaussian() × 0.00945
     * 
     * meanReversion = 2.0 × (0.20 - 0.30) × 1/252
     *               = 2.0 × (-0.10) × 0.00397
     *               ≈ -0.000794
     * 
     * If shock = 0.01 (from ε ≈ 1.06):
     *   σ_new = 0.30 + (-0.000794) + 0.01 = 0.309206
     * 
     * If shock = -0.02 (from ε ≈ -2.12):
     *   σ_new = 0.30 + (-0.000794) + (-0.02) = 0.279206
     * </pre>
     * 
     * <p><b>Volatility Floor:</b></p>
     * <p>The method enforces a minimum volatility of {@link #MIN_VOLATILITY} (5%) to prevent:</p>
     * <ul>
     *   <li>Negative volatility values (which are mathematically impossible)</li>
     *   <li>Unrealistically low volatility that doesn't match market behavior</li>
     *   <li>Numerical instability in downstream calculations</li>
     * </ul>
     * 
     * @param currentSigma the current volatility level (must be non-negative)
     * @return the calculated next volatility, guaranteed to be at least {@link #MIN_VOLATILITY}
     * 
     * @throws IllegalArgumentException if currentSigma is negative
     */
    @Override
    public double calculateNextVolatility(double currentSigma) {
        if (currentSigma < 0) {
            throw new IllegalArgumentException("Current volatility cannot be negative");
        }

        // Gaussian shock on volatility (stochastic component)
        double volShock = rng.nextGaussian() * volOfVol * Math.sqrt(dt);
        
        // Mean reversion: pull toward targetSigma (deterministic component)
        double meanReversion = kappa * (targetSigma - currentSigma) * dt;
        
        // New volatility = current + mean reversion + random shock
        double newSigma = currentSigma + meanReversion + volShock;

        // Enforce minimum volatility floor to prevent negative or too-low values
        return Math.max(MIN_VOLATILITY, newSigma);
    }
}
