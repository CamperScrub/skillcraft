package com.camperscrub.skillcraft.ability;

/** Where a super ability is in its cycle. */
public enum AbilityPhase {
    /** Can be readied with the key. */
    READY,
    /** Key pressed; using the tool in the next few seconds activates it. */
    READIED,
    /** Running. */
    ACTIVE,
    /** Used recently; can't be readied until this runs out. */
    COOLDOWN
}