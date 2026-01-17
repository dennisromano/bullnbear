package org.dennisromano.dailyexchange.infrastructure.generators;

import org.dennisromano.dailyexchange.domain.model.ShockEvent;
import org.dennisromano.dailyexchange.domain.ports.ShockProvider;

import java.util.List;
import java.util.Optional;
import java.util.random.RandomGenerator;

/**
 * Probability-based implementation of {@link ShockProvider} that generates random market shocks.
 * 
 * <p>This implementation uses a simple stochastic approach where each simulation step has
 * a fixed probability of generating a shock event. When a shock occurs, the provider
 * randomly selects an event type and assigns a random intensity within a specified range.</p>
 * 
 * <p><b>How It Works:</b></p>
 * <ol>
 *   <li>On each call to {@link #generateShock()}, roll a random number</li>
 *   <li>If the number falls within the probability threshold, generate a shock</li>
 *   <li>Randomly select a shock title from the predefined list</li>
 *   <li>Randomly assign an intensity between {@link #MIN_INTENSITY} and {@link #MAX_INTENSITY}</li>
 *   <li>Return the shock event wrapped in an Optional</li>
 * </ol>
 * 
 * <p><b>Configuration:</b></p>
 * <ul>
 *   <li><b>Probability:</b> 0.001 (0.1%) is a typical value, meaning approximately 1 shock
 *       every 1000 days (about 4 years of trading days)</li>
 *   <li><b>Intensity Range:</b> 0.3 to 0.6, representing a 30-60% spike in volatility</li>
 * </ul>
 * 
 * <p><b>Thread Safety:</b></p>
 * <p>This class is thread-safe if the provided {@link RandomGenerator} is thread-safe
 * or if instances are not shared across threads.</p>
 * 
 * @author Dennis Romano
 * @version 1.0
 * @since 1.0
 * @see ShockProvider
 * @see ShockEvent
 */
public class StochasticShockProvider implements ShockProvider {

    /** The probability of a shock occurring in any given time step */
    private final double probability;
    
    /** Random number generator for shock occurrence and parameters */
    private final RandomGenerator rng;

    /**
     * List of possible shock event titles representing different crisis scenarios.
     * 
     * <p>These titles are based on realistic market-disrupting events that could
     * cause significant volatility spikes in financial markets.</p>
     */
    private static final List<String> SHOCK_TITLES = List.of(
            "NORVIK-11 Virus Pandemic",
            "Global Cyber Attack",
            "US Sovereign Default",
            "Global Energy Crisis",
            "Big Tech Collapse",
            "Systemic Banking Collapse",
            "Global Trade War",
            "Speculative Bubble Burst",
            "Emerging Markets Currency Crisis"
    );

    /** Minimum shock intensity (30% volatility increase) */
    private static final double MIN_INTENSITY = 0.3;
    
    /** Maximum shock intensity (60% volatility increase) */
    private static final double MAX_INTENSITY = 0.6;

    /**
     * Constructs a new StochasticShockProvider with the specified parameters.
     * 
     * <p><b>Recommended Probability Values:</b></p>
     * <ul>
     *   <li>0.0001 (0.01%): Very rare shocks, ~1 per 40 years</li>
     *   <li>0.001 (0.1%): Rare shocks, ~1 per 4 years (recommended)</li>
     *   <li>0.01 (1%): Frequent shocks, ~2-3 per year</li>
     * </ul>
     * 
     * @param probability the probability of a shock occurring in each time step (must be between 0 and 1)
     * @param rng the random number generator to use for shock generation
     * 
     * @throws IllegalArgumentException if probability is not in the range [0, 1]
     */
    public StochasticShockProvider(double probability, RandomGenerator rng) {
        if (probability < 0 || probability > 1) {
            throw new IllegalArgumentException("Probability must be between 0 and 1");
        }
        this.probability = probability;
        this.rng = rng;
    }

    /**
     * Generates a potential shock event based on probability.
     * 
     * <p>This method implements a simple Bernoulli trial to determine if a shock occurs.
     * If a shock is generated:</p>
     * <ol>
     *   <li>A random intensity is chosen uniformly between {@link #MIN_INTENSITY} and {@link #MAX_INTENSITY}</li>
     *   <li>A random title is selected from {@link #SHOCK_TITLES}</li>
     *   <li>A new {@link ShockEvent} is created and returned</li>
     * </ol>
     * 
     * <p><b>Statistical Properties:</b></p>
     * <ul>
     *   <li>Expected number of shocks in N steps: N × probability</li>
     *   <li>Time between shocks follows a geometric distribution</li>
     *   <li>All shock types have equal probability of selection</li>
     *   <li>Intensity follows a uniform distribution in [MIN, MAX]</li>
     * </ul>
     * 
     * @return an {@link Optional} containing a {@link ShockEvent} if a shock occurs,
     *         or {@link Optional#empty()} if no shock occurs (the typical case)
     */
    @Override
    public Optional<ShockEvent> generateShock() {
        // Check if a shock occurs (Bernoulli trial)
        if (rng.nextDouble() >= probability) {
            return Optional.empty();
        }

        // Generate random intensity between MIN and MAX
        double intensity = rng.nextDouble(MIN_INTENSITY, MAX_INTENSITY);
        
        // Select a random title
        String title = SHOCK_TITLES.get(rng.nextInt(SHOCK_TITLES.size()));

        return Optional.of(new ShockEvent(title, intensity));
    }
}
