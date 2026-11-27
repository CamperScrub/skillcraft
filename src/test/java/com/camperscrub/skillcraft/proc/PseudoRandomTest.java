package com.camperscrub.skillcraft.proc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class PseudoRandomTest {

    @Test
    void zeroStartingChanceNeverProcs() {
        assertEquals(0, PseudoRandom.averageChance(0));
    }

    @Test
    void startingChanceOfOneAlwaysProcs() {
        assertEquals(1, PseudoRandom.averageChance(1), 1e-9);
    }

    @Test
    void startingChanceOfAHalfAveragesTwoThirds() {
        // attempt 1: 50%; attempt 2: guaranteed. Expected attempts = 0.5*1 + 0.5*2 = 1.5
        assertEquals(1 / 1.5, PseudoRandom.averageChance(0.5), 1e-9);
    }

    @ParameterizedTest(name = "{0} nominal starts at {1}")
    @CsvSource({
            "0.05, 0.00380",
            "0.10, 0.01475",
            "0.25, 0.08474",
            "0.40, 0.20155",
            "0.50, 0.30210",
    })
    void matchesTheDesignDocTable(double nominal, double expected) {
        assertEquals(expected, PseudoRandom.startingChance(nominal), 0.00005);
    }

    @ParameterizedTest
    @CsvSource({"0.01", "0.123", "0.333", "0.75", "0.99"})
    void startingChanceRoundTrips(double nominal) {
        double c = PseudoRandom.startingChance(nominal);
        assertEquals(nominal, PseudoRandom.averageChance(c), 1e-6);
    }

    @Test
    void edgeChances() {
        assertEquals(0, PseudoRandom.startingChance(0));
        assertEquals(0, PseudoRandom.startingChance(-0.5));
        assertEquals(1, PseudoRandom.startingChance(1));
        assertEquals(1, PseudoRandom.startingChance(2));
    }
}
