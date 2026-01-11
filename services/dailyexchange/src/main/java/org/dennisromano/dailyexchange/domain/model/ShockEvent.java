package org.dennisromano.dailyexchange.domain.model;

/**
 * Represents a market shock event that can significantly impact market dynamics.
 * 
 * <p>Shock events are rare, unexpected occurrences that cause sudden increases in
 * market volatility. Examples include pandemics, financial crises, geopolitical events,
 * or major corporate failures.</p>
 * 
 * <p>Each shock event is characterized by:</p>
 * <ul>
 *   <li><b>Title:</b> A descriptive name of the event (e.g., "Global Pandemic", "Banking Crisis")</li>
 *   <li><b>Intensity:</b> The magnitude of the shock's impact on volatility (typically 0.3 to 0.6)</li>
 * </ul>
 * 
 * <p>The intensity value represents the immediate increase in volatility that occurs
 * when the shock happens. For example, an intensity of 0.4 means that 40% is added
 * to the current volatility level.</p>
 * 
 * <p><b>Invariants:</b></p>
 * <ul>
 *   <li>Title must not be null or blank</li>
 *   <li>Intensity must be non-negative (intensity ≥ 0)</li>
 * </ul>
 * 
 * @param title the descriptive name of the shock event
 * @param intensity the magnitude of volatility increase caused by the shock
 * 
 * @author Dennis Romano
 * @version 1.0
 * @since 1.0
 */
public record ShockEvent(
        String title,
        double intensity
) {
    /**
     * Canonical constructor with validation.
     * 
     * <p>This constructor enforces all invariants to ensure that shock events
     * are always created with valid data.</p>
     * 
     * @throws IllegalArgumentException if title is null or blank
     * @throws IllegalArgumentException if intensity is negative
     */
    public ShockEvent {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or blank");
        }
        if (intensity < 0) {
            throw new IllegalArgumentException("Intensity cannot be negative");
        }
    }
}
