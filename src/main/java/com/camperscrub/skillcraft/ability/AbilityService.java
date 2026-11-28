package com.camperscrub.skillcraft.ability;

import com.camperscrub.skillcraft.xp.SkillLevels;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Runs every player's super abilities on the server: readying, activating, ending and cooldowns.
 * Server thread only.
 *
 * <p>Timers are kept by player UUID and never forgotten on logout, so relogging can't skip a
 * cooldown. They reset when the server restarts.
 */
public final class AbilityService {

    /** A timer plus the phase it was in last tick, so the tick can spot when a phase ends. */
    private static final class Tracked {
        final AbilityTimer timer = new AbilityTimer();
        AbilityPhase lastPhase = AbilityPhase.READY;
    }

    private final List<SuperAbility> abilities;
    private final SkillLevels levels;
    private final Map<UUID, Map<String, Tracked>> timers = new HashMap<>();

    public AbilityService(List<SuperAbility> abilities, SkillLevels levels) {
        this.abilities = List.copyOf(abilities);
        this.levels = levels;
    }

    /** The ready key: readies the held tool's ability, or lowers it if already readied. */
    public void pressReadyKey(ServerPlayer player) {
        Optional<SuperAbility> held = abilityForHeldTool(player);
        if (held.isEmpty()) {
            return;
        }
        SuperAbility ability = held.get();
        if (levels.level(player, ability.skill()) < AbilityTimings.UNLOCK_LEVEL) {
            message(player, Component.translatable("skillcraft.ability.locked", ability.name(),
                    AbilityTimings.UNLOCK_LEVEL), ChatFormatting.GRAY);
            return;
        }
        Tracked tracked = tracked(player, ability);
        long now = now(player);
        switch (tracked.timer.phase(now)) {
            case READY -> {
                tracked.timer.ready(now, (long) AbilityTimings.READIED_SECONDS * AbilityTimings.TICKS_PER_SECOND);
                tracked.lastPhase = AbilityPhase.READIED;
                message(player, Component.translatable("skillcraft.ability.readied", ability.name()), ChatFormatting.GREEN);
            }
            case READIED -> {
                tracked.timer.lower();
                tracked.lastPhase = AbilityPhase.READY;
                message(player, Component.translatable("skillcraft.ability.lowered", ability.name()), ChatFormatting.GRAY);
            }
            case COOLDOWN -> {
                long seconds = Math.ceilDiv(tracked.timer.ticksLeft(now), AbilityTimings.TICKS_PER_SECOND);
                message(player, Component.translatable("skillcraft.ability.cooldown", ability.name(),
                        AbilityTimings.minutesAndSeconds(seconds)), ChatFormatting.RED);
            }
            case ACTIVE -> {
                // Already running: nothing to do.
            }
        }
    }

    /** The player started breaking a block: activates their readied ability if this block counts. */
    public void onStartBreaking(ServerPlayer player, BlockState state) {
        Optional<SuperAbility> held = abilityForHeldTool(player);
        if (held.isEmpty() || !held.get().activatesOn(state)) {
            return;
        }
        SuperAbility ability = held.get();
        Tracked tracked = tracked(player, ability);
        int level = levels.level(player, ability.skill());
        long activeTicks = (long) AbilityTimings.durationSeconds(level) * AbilityTimings.TICKS_PER_SECOND;
        long cooldownTicks = (long) AbilityTimings.COOLDOWN_SECONDS * AbilityTimings.TICKS_PER_SECOND;
        if (tracked.timer.activate(now(player), activeTicks, cooldownTicks)) {
            tracked.lastPhase = AbilityPhase.ACTIVE;
            ability.start(player, level);
            message(player, Component.translatable("skillcraft.ability.activated", ability.name()), ChatFormatting.GOLD);
        }
    }

    /** True while {@code ability} is running for {@code player}. */
    public boolean isActive(ServerPlayer player, SuperAbility ability) {
        Map<String, Tracked> mine = timers.get(player.getUUID());
        Tracked tracked = mine == null ? null : mine.get(ability.id());
        return tracked != null && tracked.timer.phase(now(player)) == AbilityPhase.ACTIVE;
    }

    /** Every server tick: notices abilities that just ended, cooldowns that just ran out, and readies that lapsed. */
    public void tick(MinecraftServer server) {
        long now = server.getTickCount();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            Map<String, Tracked> mine = timers.get(player.getUUID());
            if (mine == null) {
                continue;
            }
            for (SuperAbility ability : abilities) {
                Tracked tracked = mine.get(ability.id());
                if (tracked == null) {
                    continue;
                }
                AbilityPhase phase = tracked.timer.phase(now);
                if (phase != tracked.lastPhase) {
                    onPhaseEnded(player, ability, tracked.lastPhase);
                    tracked.lastPhase = phase;
                }
            }
        }
    }

    private void onPhaseEnded(ServerPlayer player, SuperAbility ability, AbilityPhase ended) {
        switch (ended) {
            case ACTIVE -> {
                ability.end(player);
                message(player, Component.translatable("skillcraft.ability.wore_off", ability.name()), ChatFormatting.GRAY);
            }
            case COOLDOWN -> message(player, Component.translatable("skillcraft.ability.refreshed", ability.name()), ChatFormatting.GREEN);
            case READIED -> message(player, Component.translatable("skillcraft.ability.lowered", ability.name()), ChatFormatting.GRAY);
            case READY -> {
            }
        }
    }

    private Optional<SuperAbility> abilityForHeldTool(ServerPlayer player) {
        for (SuperAbility ability : abilities) {
            if (ability.isTool(player.getMainHandItem())) {
                return Optional.of(ability);
            }
        }
        return Optional.empty();
    }

    private Tracked tracked(ServerPlayer player, SuperAbility ability) {
        return timers.computeIfAbsent(player.getUUID(), uuid -> new HashMap<>())
                .computeIfAbsent(ability.id(), id -> new Tracked());
    }

    private static long now(ServerPlayer player) {
        return player.level().getServer().getTickCount();
    }

    private static void message(ServerPlayer player, Component text, ChatFormatting colour) {
        player.sendOverlayMessage(text.copy().withStyle(colour));
    }
}