package org.dennisromano.dailyexchange.application;

import org.dennisromano.dailyexchange.domain.model.MarketState;
import org.dennisromano.dailyexchange.domain.model.ShockEvent;
import org.dennisromano.dailyexchange.domain.model.SimulationResult;
import org.dennisromano.dailyexchange.domain.ports.PricingStrategy;
import org.dennisromano.dailyexchange.domain.ports.ShockProvider;
import org.dennisromano.dailyexchange.domain.ports.VolatilityStrategy;

import java.util.Optional;
import java.util.random.RandomGenerator;

/**
 * Market simulator that orchestrates various mathematical engines to simulate market dynamics.
 * 
 * <p>This class coordinates the interaction between different stochastic processes to create
 * a realistic market simulation. It manages:</p>
 * <ul>
 *   <li>Price evolution through a pricing strategy (e.g., Geometric Brownian Motion)</li>
 *   <li>Volatility dynamics through a volatility strategy (e.g., Mean-Reverting process)</li>
 *   <li>Random shock events through a shock provider</li>
 *   <li>Quantity fluctuations to simulate trading volume changes</li>
 * </ul>
 * 
 * <p>The simulator implements a discrete-time stepping approach where each step represents
 * one trading day. At each step, it:</p>
 * <ol>
 *   <li>Checks for shock events and adjusts volatility accordingly</li>
 *   <li>Calculates new volatility using the volatility strategy</li>
 *   <li>Calculates new price using the pricing strategy</li>
 *   <li>Adjusts quantity with random variations</li>
 *   <li>Returns the new market state</li>
 * </ol>
 * 
 * @author Dennis Romano
 * @version 1.0
 * @since 1.0
 */
public class MarketSimulator {

    /** Strategy for calculating volatility evolution */
    private final VolatilityStrategy volatilityStrategy;
    
    /** Strategy for calculating price evolution */
    private final PricingStrategy pricingStrategy;
    
    /** Provider for generating random shock events */
    private final ShockProvider shockProvider;
    
    /** Random number generator for quantity variations */
    private final RandomGenerator rng;

    /** Target drift (expected return) for the market */
    private final double targetMu;

    /**
     * Constructs a new MarketSimulator with the specified strategies and parameters.
     * 
     * @param volatilityStrategy the strategy to calculate volatility evolution over time
     * @param pricingStrategy the strategy to calculate price evolution over time
     * @param shockProvider the provider to generate random shock events
     * @param rng the random number generator for quantity variations
     * @param targetMu the target drift (expected annual return) for drift calculations
     * 
     * @throws NullPointerException if any of the object parameters is null
     */
    public MarketSimulator(
            VolatilityStrategy volatilityStrategy,
            PricingStrategy pricingStrategy,
            ShockProvider shockProvider,
            RandomGenerator rng,
            double targetMu
    ) {
        this.volatilityStrategy = volatilityStrategy;
        this.pricingStrategy = pricingStrategy;
        this.shockProvider = shockProvider;
        this.rng = rng;
        this.targetMu = targetMu;
    }

    /**
     * Executes a single simulation step, advancing the market by one time period.
     * 
     * <p>This method implements the core simulation logic:</p>
     * <ol>
     *   <li><b>Shock Check:</b> Determines if a shock event occurs and increases volatility if so</li>
     *   <li><b>Volatility Update:</b> Calculates new volatility using the volatility strategy</li>
     *   <li><b>Drift Calculation:</b> Uses the target mu (drift is already adjusted in GBM)</li>
     *   <li><b>Price Update:</b> Calculates new price using the pricing strategy</li>
     *   <li><b>Quantity Update:</b> Applies random variation (±4%) to the quantity</li>
     *   <li><b>State Creation:</b> Creates new market state with updated values</li>
     * </ol>
     * 
     * <p><b>Important:</b> The Geometric Brownian Motion (GBM) pricing strategy automatically
     * applies the Itô correction (-0.5σ²), so the effective drift will be slightly lower
     * than the target mu.</p>
     * 
     * @param state the current market state before the simulation step
     * @return a {@link SimulationResult} containing the new market state and any shock event that occurred
     * 
     * @throws NullPointerException if state is null
     * @throws IllegalArgumentException if state contains invalid values
     */
    public SimulationResult next(int day, MarketState state) {
        double sigma = state.sigma();
        ShockEvent shockEvent = null;

        // 1. Check if a shock event occurs
        final Optional<ShockEvent> shock = shockProvider.generateShock();
        if (shock.isPresent()) {
            shockEvent = shock.get();
            // Increase volatility based on shock intensity
            sigma += shockEvent.intensity();
        }

        // 2. Calculate new volatility using the volatility engine
        sigma = volatilityStrategy.calculateNextVolatility(sigma);

        // 3. Use target mu (GBM already applies the -0.5*sigma² adjustment)
        final  double mu = targetMu;

        // 4. Calculate new price using the pricing engine
        final double currentPrice = state.price();
        final double newPrice = pricingStrategy.calculateNextPrice(currentPrice, mu, sigma);
        
        // 5. Calculate effective drift after GBM adjustment (for informational purposes)
        final double effectiveDrift = mu - 0.5 * sigma * sigma;

        // 6. Calculate random quantity variation (±4%)
        final  double quantityVariation = (rng.nextDouble() - 0.5) * state.quantity() * 0.04;
        final  double newQuantity = state.quantity() + quantityVariation;

        // 7. Create new state, saving previous price to calculate variation
        final MarketState newState = new MarketState(
            day,
            newPrice,           // new price
            currentPrice,       // previous price (for calculating variation)
            newQuantity, 
            sigma, 
            effectiveDrift     // effective annualized drift
        );

        return new SimulationResult(newState, shockEvent);
    }
}
