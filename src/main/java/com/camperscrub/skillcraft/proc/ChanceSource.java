package com.camperscrub.skillcraft.proc;

import net.minecraft.util.RandomSource;

/**
 * Where random numbers come from. Production code uses the game's random; tests pass a seeded
 * {@link java.util.Random} or a fixed list of numbers, so results are repeatable.
 */
@FunctionalInterface
public interface ChanceSource {

    /** A random number from 0 (inclusive) to 1 (exclusive). */
    double nextDouble();

    static ChanceSource of(RandomSource random) {
        return random::nextDouble;
    }
}
