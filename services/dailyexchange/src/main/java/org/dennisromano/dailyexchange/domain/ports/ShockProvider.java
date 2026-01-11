package org.dennisromano.dailyexchange.domain.ports;

import org.dennisromano.dailyexchange.domain.model.ShockEvent;

import java.util.Optional;

/**
 * Provider interface for generating random market shock events.
 * 
 * <p>This interface defines the contract for shock generators that simulate rare,
 * high-impact events in financial markets. These events represent unexpected occurrences
 * that cause sudden spikes in market volatility and significant price movements.</p>
 * 
 * <p><b>Examples of Real-World Shock Events:</b></p>
 * <ul>
 *   <li>Global pandemics (COVID-19, Spanish Flu)</li>
 *   <li>Financial crises (2008 housing crisis, 1987 Black Monday)</li>
 *   <li>Geopolitical events (wars, terrorist attacks, political upheavals)</li>
 *   <li>Natural disasters (earthquakes, tsunamis, hurricanes)</li>
 *   <li>Corporate failures (Lehman Brothers, Enron)</li>
 *   <li>Technology disruptions (cyber attacks, infrastructure failures)</li>
 * </ul>
 * 
 * <p><b>Implementation Strategies:</b></p>
 * <ul>
 *   <li><b>Stochastic:</b> Random generation based on probability distribution</li>
 *   <li><b>Poisson Process:</b> Time-based arrival of shocks</li>
 *   <li><b>Historical:</b> Replaying actual historical shock events</li>
 *   <li><b>Scenario-Based:</b> Predefined shock scenarios for stress testing</li>
 * </ul>
 * 
 * @author Dennis Romano
 * @version 1.0
 * @since 1.0
 * @see ShockEvent
 * @see org.dennisromano.dailyexchange.infrastructure.generators.StochasticShockProvider
 */
public interface ShockProvider {
    
    /**
     * Attempts to generate a shock event for the current time step.
     * 
     * <p>This method is called once per simulation step to determine whether a shock
     * event occurs. The decision is typically based on:</p>
     * <ul>
     *   <li>Random probability (e.g., 0.1% chance per day)</li>
     *   <li>Current market conditions</li>
     *   <li>Time since last shock</li>
     *   <li>Predefined scenario schedule</li>
     * </ul>
     * 
     * <p>If a shock occurs, the method returns an {@link Optional} containing a
     * {@link ShockEvent} with:</p>
     * <ul>
     *   <li><b>Title:</b> A descriptive name of the event</li>
     *   <li><b>Intensity:</b> The magnitude of impact on volatility (typically 0.3 to 0.6)</li>
     * </ul>
     * 
     * <p>If no shock occurs (the typical case), the method returns {@link Optional#empty()}.</p>
     * 
     * <p><b>Usage Example:</b></p>
     * <pre>
     * Optional&lt;ShockEvent&gt; shock = shockProvider.generateShock();
     * if (shock.isPresent()) {
     *     ShockEvent event = shock.get();
     *     currentVolatility += event.intensity();
     *     System.out.println("Shock: " + event.title());
     * }
     * </pre>
     * 
     * <p><b>Thread Safety:</b></p>
     * <p>Implementations should be thread-safe if used in concurrent simulations.
     * Consider using ThreadLocal random generators or synchronization as needed.</p>
     * 
     * @return an {@link Optional} containing a {@link ShockEvent} if a shock occurs,
     *         or {@link Optional#empty()} if no shock occurs
     */
    Optional<ShockEvent> generateShock();
}
