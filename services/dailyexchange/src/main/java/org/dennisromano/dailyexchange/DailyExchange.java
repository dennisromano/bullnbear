package org.dennisromano.dailyexchange;

import org.dennisromano.dailyexchange.application.MarketSimulator;
import org.dennisromano.dailyexchange.domain.model.MarketState;
import org.dennisromano.dailyexchange.domain.model.SimulationResult;
import org.dennisromano.dailyexchange.domain.ports.MarketReportPort;
import org.dennisromano.dailyexchange.infrastructure.reporting.ConsoleMarketReporter;
import org.dennisromano.dailyexchange.domain.ports.PricingStrategy;
import org.dennisromano.dailyexchange.infrastructure.mathematics.GeometricBrownianMotionStrategy;
import org.dennisromano.dailyexchange.domain.ports.ShockProvider;
import org.dennisromano.dailyexchange.infrastructure.generators.StochasticShockProvider;
import org.dennisromano.dailyexchange.domain.ports.VolatilityStrategy;
import org.dennisromano.dailyexchange.infrastructure.mathematics.MeanRevertingVolatilityStrategy;

import java.util.random.RandomGenerator;

/**
 * Main application for Wall Street market simulation.
 * 
 * <p>This simulation models a financial market using stochastic mathematical models
 * to simulate realistic price movements, volatility changes, and market shocks.</p>
 * 
 * <h2>Simulation Configuration:</h2>
 * <ul>
 *   <li>Duration: 2520 trading days (approximately 10 years)</li>
 *   <li>Initial Price: €100</li>
 *   <li>Initial Quantity: 2,000,000 units</li>
 *   <li>Target Volatility: 20% annual</li>
 *   <li>Expected Return: 8% annual</li>
 *   <li>Shock Probability: 0.1% per day</li>
 * </ul>
 * 
 * <h2>Mathematical Models Used:</h2>
 * <ul>
 *   <li><b>Geometric Brownian Motion (GBM)</b> for price evolution</li>
 *   <li><b>Mean-Reverting Process</b> for volatility dynamics</li>
 *   <li><b>Stochastic Shocks</b> for rare market events</li>
 * </ul>
 * 
 * @author Dennis Romano
 * @version 1.0
 * @since 1.0
 */
public class DailyExchange {
    
    // Simulation parameters
    /** Total number of trading days to simulate (approximately 10 years) */
    private static final int SIMULATION_DAYS = 2520;
    
    /** Number of trading days in a year (standard for most markets) */
    private static final double TRADING_DAYS_PER_YEAR = 252.0;
    
    /** Time step in years (1 day = 1/252 years) */
    private static final double DT = 1.0 / TRADING_DAYS_PER_YEAR;
    
    // Market parameters
    /** Initial asset price in euros */
    private static final double INITIAL_PRICE = 100.0;
    
    /** Initial quantity of assets in circulation */
    private static final double INITIAL_QUANTITY = 2_000_000.0;
    
    /** Target volatility (20% annual) */
    private static final double TARGET_VOLATILITY = 0.20;
    
    /** Target drift/expected return (8% annual) */
    private static final double TARGET_MU = 0.08;
    
    // Volatility engine parameters
    /** Speed of mean-reversion (how quickly volatility returns to target) */
    private static final double KAPPA = 2.0;
    
    /** Volatility of volatility (how volatile the volatility itself is) */
    private static final double VOL_OF_VOL = 0.15;
    
    // Shock parameters
    /** Probability of a shock event occurring on any given day (0.1%) */
    private static final double SHOCK_PROBABILITY = 0.001;
    
    /**
     * Main entry point for the market simulation.
     * 
     * <p>This method initializes all the mathematical engines and runs the simulation
     * for the specified number of days. Each day's results are printed to the console,
     * including any shock events that occur.</p>
     * 
     * <p>The simulation flow:</p>
     * <ol>
     *   <li>Initialize mathematical engines (volatility, pricing, shock)</li>
     *   <li>Create initial market state</li>
     *   <li>For each trading day:
     *     <ul>
     *       <li>Calculate next market state</li>
     *       <li>Check for shock events</li>
     *       <li>Print daily report</li>
     *     </ul>
     *   </li>
     * </ol>
     */
    void main() {
        final RandomGenerator rng = RandomGenerator.getDefault();

        // Create mathematical engines (using interfaces for flexibility)
        final VolatilityStrategy volatilityStrategy = new MeanRevertingVolatilityStrategy(
                TARGET_VOLATILITY,
                KAPPA,
                VOL_OF_VOL,
                DT,
                rng
        );

        final PricingStrategy pricingStrategy = new GeometricBrownianMotionStrategy(DT, rng);
        final ShockProvider shockProvider = new StochasticShockProvider(SHOCK_PROBABILITY, rng);
        final MarketReportPort formatter = new ConsoleMarketReporter();

        final MarketSimulator simulator = new MarketSimulator(
                volatilityStrategy,
                pricingStrategy,
                shockProvider,
                rng,
                TARGET_MU,
                TARGET_VOLATILITY
        );

        MarketState state = new MarketState(
                INITIAL_PRICE,
                INITIAL_PRICE,
                INITIAL_QUANTITY,
                TARGET_VOLATILITY,
                TARGET_MU
        );

        for (int day = 1; day <= SIMULATION_DAYS; day++) {
            final SimulationResult result = simulator.next(state);
            state = result.state();

            if (result.shockEvent() != null) {
                System.out.println(
                        "⚠️ SHOCK EVENT: "
                        + result.shockEvent().title()
                );
            }

            System.out.println("DAY " + day);
            System.out.println(formatter.format(state));
        }
    }
}
