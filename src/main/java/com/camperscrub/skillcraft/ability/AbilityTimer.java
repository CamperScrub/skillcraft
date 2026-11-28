package com.camperscrub.skillcraft.ability;

/**
 * One player's timer for one super ability. Pure logic: no Minecraft classes.
 *
 * <p>Instead of counting down every tick, it stores the tick each phase ends at. The phase at any
 * moment then follows from comparing {@code now} with those end ticks. {@code now} is the
 * server's tick count, and 0 means "not set".
 */
public final class AbilityTimer {

    private long readiedUntil;
    private long activeUntil;
    private long cooldownUntil;

    /**
     * The phase at tick {@code now}.
     *
     * <p>EXERCISE 1. Check in this order: ACTIVE while {@code now} is before {@code activeUntil},
     * else COOLDOWN while before {@code cooldownUntil}, else READIED while before
     * {@code readiedUntil}, else READY. "Before" means strictly less than: at exactly
     * {@code activeUntil} the ability has ended.
     */
    public AbilityPhase phase(long now) {
        if (now < activeUntil) {
            return AbilityPhase.ACTIVE;
        } else if (now < cooldownUntil) {
            return AbilityPhase.COOLDOWN;
        } else if (now < readiedUntil) {
            return AbilityPhase.READIED;
        }
        return AbilityPhase.READY;
    }

    /**
     * Readies the ability for {@code readiedTicks}.
     *
     * <p>EXERCISE 2a. Only works from READY: set {@code readiedUntil} and return true. In any
     * other phase change nothing and return false.
     */
    public boolean ready(long now, long readiedTicks) {
        if (phase(now) == AbilityPhase.READY) {
            readiedUntil = now + readiedTicks;
            return true;
        }
        return false;
    }

    /** Puts a readied ability down again, so it goes back to READY. */
    public void lower() {
        readiedUntil = 0;
    }

    /**
     * Starts the ability.
     *
     * <p>EXERCISE 2b. Only works from READIED. It runs for {@code activeTicks}, and the cooldown
     * starts when it ends, so {@code cooldownUntil} is {@code activeTicks + cooldownTicks} after
     * now. Clear {@code readiedUntil} and return true. In any other phase change nothing and
     * return false.
     */
    public boolean activate(long now, long activeTicks, long cooldownTicks) {
        if (phase(now) != AbilityPhase.READIED) {
            return false;
        }
        readiedUntil = 0;
        activeUntil = now + activeTicks;
        cooldownUntil = activeUntil + cooldownTicks;
        return true;
    }

    /** Ticks until the current phase ends. 0 when READY. */
    public long ticksLeft(long now) {
        return switch (phase(now)) {
            case ACTIVE -> activeUntil - now;
            case COOLDOWN -> cooldownUntil - now;
            case READIED -> readiedUntil - now;
            case READY -> 0;
        };
    }
}