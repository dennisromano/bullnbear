package org.dennisromano.dailyexchange.infrastructure.mathematics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Random;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.*;

class MeanRevertingVolatilityStrategyTest {
    private static final double TARGET_SIGMA = 0.20;
    private static final double KAPPA = 2.0;
    private static final double VOL_OF_VOL = 0.15;
    private static final double DT_DAILY = 1.0 / 252.0;
    private static final double MIN_VOLATILITY = 0.05;

    private MeanRevertingVolatilityStrategy strategy;
    private RandomGenerator fixedRng;

    @BeforeEach
    void setUp() {
        fixedRng = new Random(123);
        strategy = new MeanRevertingVolatilityStrategy(TARGET_SIGMA, KAPPA, VOL_OF_VOL, DT_DAILY, fixedRng);
    }

    @Test
    @DisplayName("Constructor should validate all input parameters")
    void constructorValidation() {
        assertAll("Invalid parameters",
                () -> assertThrows(IllegalArgumentException.class, () -> new MeanRevertingVolatilityStrategy(0, KAPPA, VOL_OF_VOL, DT_DAILY, fixedRng)),
                () -> assertThrows(IllegalArgumentException.class, () -> new MeanRevertingVolatilityStrategy(TARGET_SIGMA, -1, VOL_OF_VOL, DT_DAILY, fixedRng)),
                () -> assertThrows(IllegalArgumentException.class, () -> new MeanRevertingVolatilityStrategy(TARGET_SIGMA, KAPPA, -0.1, DT_DAILY, fixedRng)),
                () -> assertThrows(IllegalArgumentException.class, () -> new MeanRevertingVolatilityStrategy(TARGET_SIGMA, KAPPA, VOL_OF_VOL, 0, fixedRng))
        );
    }

    @Test
    @DisplayName("Should revert towards mean when shock is zero")
    void shouldRevertToMeanWithoutShock() {
        final RandomGenerator zeroRng = new RandomGenerator() {
            @Override public double nextGaussian() { return 0.0; }
            @Override public long nextLong() { return 0; }
        };

        final MeanRevertingVolatilityStrategy stableStrategy = new MeanRevertingVolatilityStrategy(TARGET_SIGMA, KAPPA, VOL_OF_VOL, DT_DAILY, zeroRng);

        final double highVol = 0.40;
        final double nextHigh = stableStrategy.calculateNextVolatility(highVol);
        assertTrue(nextHigh < highVol, "Volatility should decrease towards target");

        final double lowVol = 0.10;
        final double nextLow = stableStrategy.calculateNextVolatility(lowVol);
        assertTrue(nextLow > lowVol, "Volatility should increase towards target");
    }

    @Test
    @DisplayName("Should enforce the MIN_VOLATILITY floor")
    void shouldEnforceFloor() {
        final RandomGenerator negativeShockRng = new RandomGenerator() {
            @Override public double nextGaussian() { return -100.0; }
            @Override public long nextLong() { return 0; }
        };

        final MeanRevertingVolatilityStrategy floorStrategy = new MeanRevertingVolatilityStrategy(TARGET_SIGMA, KAPPA, VOL_OF_VOL, DT_DAILY, negativeShockRng);

        final double result = floorStrategy.calculateNextVolatility(0.06);
        assertEquals(MIN_VOLATILITY, result, "Should not go below 5% floor");
    }

    @ParameterizedTest
    @CsvSource({
            "0.20, 0.20",
            "0.30, 0.299206",
            "0.10, 0.100793"
    })
    @DisplayName("Deterministic step calculation check")
    void mathematicalAccuracyCheck(double current, double expectedApprox) {
        final RandomGenerator zeroRng = new RandomGenerator() {
            @Override public double nextGaussian() { return 0.0; }
            @Override public long nextLong() { return 0; }
        };
        final MeanRevertingVolatilityStrategy mathStrategy = new MeanRevertingVolatilityStrategy(TARGET_SIGMA, KAPPA, VOL_OF_VOL, DT_DAILY, zeroRng);

        final double actual = mathStrategy.calculateNextVolatility(current);
        assertEquals(expectedApprox, actual, 1e-5);
    }

    @Test
    @DisplayName("Long-term simulation should hover around target sigma")
    void longTermMeanReversionSim() {
        double currentVol = 0.80;
        final int burnIn = 1000;
        final int observations = 10000;
        double sum = 0;

        for (int i = 0; i < burnIn; i++) {
            currentVol = strategy.calculateNextVolatility(currentVol);
        }

        for (int i = 0; i < observations; i++) {
            currentVol = strategy.calculateNextVolatility(currentVol);
            sum += currentVol;
        }

        double averageVol = sum / observations;

        assertEquals(TARGET_SIGMA, averageVol, 0.05,
                "Average volatility " + averageVol + " is too far from target " + TARGET_SIGMA);
    }
}