package com.camperscrub.skillcraft.proc;

/** One pseudo-random counter: remembers how many attempts have missed in a row. */
public final class PrdCounter {

    /** Which attempt the next roll is. 1 means "fresh": the chance is C. */
    private int attempt = 1;

    /**
     * Rolls a proc with nominal chance {@code chance}.
     *
     * <p>EXERCISE 3.
     * <ol>
     *   <li>If {@code chance <= 0}, return false without touching the counter (the proc couldn't
     *       have happened, so it mustn't build up).</li>
     *   <li>Look up C with {@link PseudoRandom#startingChance}.</li>
     *   <li>The chance right now is {@code min(1, C * attempt)}.</li>
     *   <li>Hit if {@code source.nextDouble()} is below it: reset {@code attempt} to 1.
     *       Miss: add 1 to {@code attempt}.</li>
     * </ol>
     *
     * <p>Store the attempt number, not the current chance: a player's chance changes as they
     * level up, and the attempt number still works with the new C.
     */
    public boolean roll(double chance, ChanceSource source) {
        if (chance <= 0) {
            return false;                   // couldn't have proced, so don't build up
        }
        double c = PseudoRandom.startingChance(chance);
        double chanceNow = Math.min(1, c * attempt);
        if (source.nextDouble() < chanceNow) {
            attempt = 1;
            return true;
        }
        attempt += 1;
        return false;
    }

    int attempt() {
        return attempt;
    }
}
