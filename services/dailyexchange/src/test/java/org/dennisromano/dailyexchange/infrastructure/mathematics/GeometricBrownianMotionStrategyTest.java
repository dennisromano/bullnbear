package org.dennisromano.dailyexchange.infrastructure.mathematics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Random;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.*;

class GeometricBrownianMotionStrategyTest {
    private static final double DT_DAILY = 1.0 / 252.0;
    private GeometricBrownianMotionStrategy strategy;
    private RandomGenerator fixedRng;

    @BeforeEach
    void setUp() {
        fixedRng = new Random(42);
        strategy = new GeometricBrownianMotionStrategy(DT_DAILY, fixedRng);
    }

    @Test
    @DisplayName("Should throw exception if dt is non-positive during construction")
    void constructorThrowsExceptionForInvalidDt() {
        assertThrows(IllegalArgumentException.class, () -> new GeometricBrownianMotionStrategy(0, fixedRng));
        assertThrows(IllegalArgumentException.class, () -> new GeometricBrownianMotionStrategy(-1.0, fixedRng));
    }

    @Test
    @DisplayName("Should throw exception for non-positive current price")
    void calculateNextPriceThrowsExceptionForInvalidPrice() {
        assertThrows(IllegalArgumentException.class, () -> strategy.calculateNextPrice(0, 0.05, 0.2));
        assertThrows(IllegalArgumentException.class, () -> strategy.calculateNextPrice(-100, 0.05, 0.2));
    }

    @Test
    @DisplayName("Should return a deterministic value when epsilon is known")
    void calculateNextPriceMatchesMathematicalExpectation() {
        final RandomGenerator zeroRng = new RandomGenerator() {
            @Override public double nextGaussian() { return 0.0; }
            @Override public long nextLong() { return 0; }
        };

        final GeometricBrownianMotionStrategy deterministicStrategy = new GeometricBrownianMotionStrategy(DT_DAILY, zeroRng);

        final double s0 = 100.0;
        final double mu = 0.10;
        final double sigma = 0.20;

        final double expected = s0 * Math.exp((mu - 0.5 * Math.pow(sigma, 2)) * DT_DAILY);
        final double actual = deterministicStrategy.calculateNextPrice(s0, mu, sigma);

        assertEquals(expected, actual, 1e-10);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.1, 0.5, 2.0})
    @DisplayName("Price should always stay positive regardless of volatility")
    void priceShouldAlwaysStayPositive(double highSigma) {
        double price = 10.0;

        for (int i = 0; i < 100; i++) {
            price = strategy.calculateNextPrice(price, 0.05, highSigma);
            assertTrue(price > 0, "Price became non-positive at iteration " + i);
        }
    }

    @Test
    @DisplayName("Higher volatility should generally lead to larger price swings")
    void volatilityComparison() {
        final double s0 = 100.0;
        final double mu = 0.0;

        final RandomGenerator mockRng = new RandomGenerator() {
            @Override public double nextGaussian() { return 1.0; }
            @Override public long nextLong() { return 0; }
        };

        final GeometricBrownianMotionStrategy testStrategy = new GeometricBrownianMotionStrategy(DT_DAILY, mockRng);

        final double lowVolPrice = testStrategy.calculateNextPrice(s0, mu, 0.1);  // 10% vol
        final double highVolPrice = testStrategy.calculateNextPrice(s0, mu, 0.5); // 50% vol

        assertTrue(highVolPrice > lowVolPrice);
    }
}