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

    private static final long READIED_TICKS = (long) AbilityTimings.READIED_SECONDS * AbilityTimings.TICKS_PER_SECOND;
    private static final long COOLDOWN_TICKS = (long) AbilityTimings.COOLDOWN_SECONDS * AbilityTimings.TICKS_PER_SECOND;

    private final List<SuperAbility> abilities;
    private final SkillLevels levels;
    private final AbilityStatusListener status;
    private final Map<UUID, Map<String, Tracked>> timers = new HashMap<>();

    public AbilityService(List<SuperAbility> abilities, SkillLevels levels, AbilityStatusListener status) {
        this.abilities = List.copyOf(abilities);
        this.levels = levels;
        this.status = status;
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
                tracked.timer.ready(now, READIED_TICKS);
                changed(player, ability, tracked, now);
                message(player, Component.translatable("skillcraft.ability.readied", ability.name()), ChatFormatting.GREEN);
            }
            case READIED -> {
                tracked.timer.lower();
                changed(player, ability, tracked, now);
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
        long now = now(player);
        int level = levels.level(player, ability.skill());
        if (tracked.timer.activate(now, activeTicks(level), COOLDOWN_TICKS)) {
            ability.start(player, level);
            changed(player, ability, tracked, now);
            message(player, Component.translatable("skillcraft.ability.activated", ability.name()), ChatFormatting.GOLD);
        }
    }

    /** True while {@code ability} is running for {@code player}. */
    public boolean isActive(ServerPlayer player, SuperAbility ability) {
        Map<String, Tracked> mine = timers.get(player.getUUID());
        Tracked tracked = mine == null ? null : mine.get(ability.id());
        return tracked != null && tracked.timer.phase(now(player)) == AbilityPhase.ACTIVE;
    }

    /** The break speed boost from the player's active abilities, as a {@link BreakSpeedBoost}. */
    public float breakSpeedMultiplier(ServerPlayer player, BlockState state) {
        float multiplier = 1;
        for (SuperAbility ability : abilities) {
            if (isActive(player, ability)) {
                multiplier *= ability.breakSpeedMultiplier(state);
            }
        }
        return multiplier;
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
                AbilityPhase ended = tracked.lastPhase;
                if (tracked.timer.phase(now) != ended) {
                    changed(player, ability, tracked, now);
                    onPhaseEnded(player, ability, ended);
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

    /** Remembers the new phase and tells the status listener (and so the client). */
    private void changed(ServerPlayer player, SuperAbility ability, Tracked tracked, long now) {
        tracked.lastPhase = tracked.timer.phase(now);
        long active = activeTicks(levels.level(player, ability.skill()));
        status.onStatus(player, ability, tracked.lastPhase, tracked.timer.ticksLeft(now), active);
    }

    private static long activeTicks(int level) {
        return (long) AbilityTimings.durationSeconds(level) * AbilityTimings.TICKS_PER_SECOND;
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
