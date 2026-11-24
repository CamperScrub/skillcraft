package com.camperscrub.skillcraft.event;

import com.camperscrub.skillcraft.SkillCraft;
import com.camperscrub.skillcraft.data.SpawnOriginTracker;
import com.camperscrub.skillcraft.skill.Skill;
import com.camperscrub.skillcraft.xp.CombatXpRules;
import com.camperscrub.skillcraft.xp.MobXpTable;
import com.camperscrub.skillcraft.xp.XpAward;
import com.camperscrub.skillcraft.xp.XpService;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

/**
 * Turns sword hits and kills into Swordsmanship XP. Holds no formulas of its own.
 */
public final class CombatXpHandler {

    private final XpService xpService;
    private final CombatXpRules rules;
    private final MobXpTable mobs;
    private final SpawnOriginTracker spawnOrigins;

    // Health just before each hit. Weak keys: an entity that's removed from the world gets cleaned up automatically.
    private final Map<LivingEntity, Float> healthBeforeHit = new WeakHashMap<>();

    public CombatXpHandler(XpService xpService, CombatXpRules rules, MobXpTable mobs, SpawnOriginTracker spawnOrigins) {
        this.xpService = xpService;
        this.rules = rules;
        this.mobs = mobs;
        this.spawnOrigins = spawnOrigins;
    }

    public void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(this::rememberHealth);
        ServerLivingEntityEvents.AFTER_DAMAGE.register(this::onDamaged);
        ServerLivingEntityEvents.AFTER_DEATH.register(this::onDeath);
    }

    private boolean rememberHealth(LivingEntity target, DamageSource source, float amount) {
        healthBeforeHit.put(target, target.getHealth());
        return true; // we only watch; never cancel damage
    }

    /**
     * A hit that didn't kill.
     */
    private void onDamaged(LivingEntity target, DamageSource source, float baseDamage, float damageTaken, boolean blocked) {
        Float before = healthBeforeHit.remove(target);
        if (before == null) {
            return;
        }
        swordAttacker(source).ifPresent(player -> awardHit(player, target, before - target.getHealth()));
    }

    /**
     * The killing blow: it removed all remaining health, and may earn the kill bonus.
     */
    private void onDeath(LivingEntity target, DamageSource source) {
        Float before = healthBeforeHit.remove(target);
        swordAttacker(source).ifPresent(player -> {
            if (before != null) {
                awardHit(player, target, before);
            }
            awardKillBonus(player, target);
        });
    }

    /**
     * The player who caused this damage with a sword in melee, if there is one.
     */
    private Optional<ServerPlayer> swordAttacker(DamageSource source) {
        if (source.getEntity() instanceof ServerPlayer player
                && source.getDirectEntity() == player          // melee, not an arrow or thrown trident
                && player.getMainHandItem().is(ItemTags.SWORDS)
                && !player.isCreative()) {
            return Optional.of(player);
        }
        return Optional.empty();
    }

    private void awardHit(ServerPlayer player, LivingEntity target, double healthRemoved) {
        if (target instanceof Player) {
            return; // no PvP XP yet
        }
        double multiplier = mobs.multiplierFor(target.getType()) * xpFactor(target);
        long xp = rules.hitXp(healthRemoved, multiplier);
        award(player, xp, "hit");
    }

    private void awardKillBonus(ServerPlayer player, LivingEntity target) {
        if (target instanceof Player) {
            return;
        }
        double multiplier = mobs.multiplierFor(target.getType()) * xpFactor(target);
        long xp = rules.killBonus(target.getMaxHealth(), multiplier, mobs.isBoss(target.getType()));
        award(player, xp, "kill bonus");
    }

    private void award(ServerPlayer player, long xp, String reason) {
        if (xp <= 0) {
            return;
        }
        XpAward award = xpService.award(player, Skill.SWORDSMANSHIP, xp);
        // TEMPORARY until we have on-screen feedback:
        SkillCraft.LOGGER.info("+{} swordsmanship XP ({}) (level {})", award.gained(), reason, award.newLevel());
    }

    /**
     * How much of the normal XP this mob is worth (farm mobs, pets, player-built golems).
     */
    private double xpFactor(LivingEntity target) {
        if (target instanceof TamableAnimal pet && pet.isTame()) {
            return 0;
        }
        if (target instanceof IronGolem golem && golem.isPlayerCreated()) {
            return 0;
        }
        double factor = rules.originFactor(spawnOrigins.originOf(target));
        if (target.getType() == EntityTypes.ENDERMAN
                && target instanceof Mob mob
                && mob.getTarget() != null
                && mob.getTarget().getType() == EntityTypes.ENDERMITE) {
            factor = Math.min(factor, CombatXpRules.FARM_FACTOR); // End farm lure
        }
        return factor;
    }
}