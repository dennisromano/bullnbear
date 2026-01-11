package org.dennisromano.dailyexchange.domain.model;

/**
 * Encapsulates the result of a single simulation step in the market simulation.
 * 
 * <p>This record combines the new market state after a simulation step with any
 * shock event that may have occurred during that step. It serves as the return
 * type for the {@link org.dennisromano.dailyexchange.application.MarketSimulator#next(MarketState)}
 * method.</p>
 * 
 * <p>The simulation result provides:</p>
 * <ul>
 *   <li><b>State:</b> The updated market state reflecting all changes from the simulation step</li>
 *   <li><b>Shock Event:</b> Information about any shock that occurred (null if no shock)</li>
 * </ul>
 * 
 * <p>This design allows the simulation to communicate both the normal evolution of
 * the market and any exceptional events in a single, cohesive object.</p>
 * 
 * <p><b>Usage Example:</b></p>
 * <pre>
 * SimulationResult result = simulator.next(currentState);
 * MarketState newState = result.state();
 * if (result.shockEvent() != null) {
 *     System.out.println("Shock occurred: " + result.shockEvent().title());
 * }
 * </pre>
 * 
 * @param state the new market state after the simulation step (must not be null)
 * @param shockEvent the shock event that occurred during this step, or null if no shock occurred
 * 
 * @author Dennis Romano
 * @version 1.0
 * @since 1.0
 */
public record SimulationResult(
        MarketState state,
        ShockEvent shockEvent
) {
    /**
     * Canonical constructor with validation.
     * 
     * <p>This constructor ensures that the simulation result always contains
     * a valid market state. The shock event is allowed to be null, as shocks
     * are rare occurrences.</p>
     * 
     * @throws IllegalArgumentException if state is null
     */
    public SimulationResult {
        if (state == null) {
            throw new IllegalArgumentException("State cannot be null");
        }
    }
}
