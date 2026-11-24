package com.camperscrub.skillcraft.xp;

import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Combat XP multipliers per mob type. Mobs not listed use the default multiplier.
 */
public final class MobXpTable {

    private final Map<EntityType<?>, Double> multipliers;
    private final Set<EntityType<?>> bosses;
    private final double defaultMultiplier;

    private MobXpTable(Builder builder) {
        this.multipliers = Map.copyOf(builder.multipliers);
        this.bosses = Set.copyOf(builder.bosses);
        this.defaultMultiplier = builder.defaultMultiplier;
    }

    public static Builder builder() {
        return new Builder();
    }

    public double multiplierFor(EntityType<?> type) {
        return multipliers.getOrDefault(type, defaultMultiplier);
    }

    public boolean isBoss(EntityType<?> type) {
        return bosses.contains(type);
    }

    public static final class Builder {
        private final Map<EntityType<?>, Double> multipliers = new HashMap<>();
        private final Set<EntityType<?>> bosses = new HashSet<>();
        private double defaultMultiplier = 1.0;

        private Builder() {
        }

        public Builder mob(EntityType<?> type, double multiplier) {
            if (multiplier < 0) throw new IllegalArgumentException("Multiplier cannot be negative, got " + multiplier);
            multipliers.put(type, multiplier);
            return this;
        }

        public Builder boss(EntityType<?> type, double multiplier) {
            bosses.add(type);
            return mob(type, multiplier);
        }

        /**
         * Mobs that never give combat XP.
         */
        public Builder noXp(EntityType<?> type) {
            multipliers.put(type, 0.0);
            return this;
        }

        public Builder defaultMultiplier(double multiplier) {
            if (multiplier < 0) throw new IllegalArgumentException("Multiplier cannot be negative, got " + multiplier);
            this.defaultMultiplier = multiplier;
            return this;
        }

        public MobXpTable build() {
            return new MobXpTable(this);
        }
    }
}