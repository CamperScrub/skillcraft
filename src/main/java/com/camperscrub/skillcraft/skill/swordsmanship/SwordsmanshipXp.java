package com.camperscrub.skillcraft.skill.swordsmanship;

import com.camperscrub.skillcraft.xp.MobXpTable;
import net.minecraft.world.entity.EntityTypes;

public final class SwordsmanshipXp {

    private SwordsmanshipXp() {
    }

    public static MobXpTable mobTable() {
        return MobXpTable.builder()
                .defaultMultiplier(1.0)                 // all other animals, villagers, modded mobs
                // Bosses (SkillCraft values: full health counts, see design doc)
                .boss(EntityTypes.ENDER_DRAGON, 2)
                .boss(EntityTypes.WITHER, 1.2)
                .boss(EntityTypes.WARDEN, 0.72)
                // Hostile mobs (mcMMO values)
                .mob(EntityTypes.PIGLIN_BRUTE, 4.5)
                .mob(EntityTypes.CREEPER, 4).mob(EntityTypes.WITHER_SKELETON, 4).mob(EntityTypes.PHANTOM, 4)
                .mob(EntityTypes.ELDER_GUARDIAN, 4).mob(EntityTypes.RAVAGER, 4).mob(EntityTypes.HOGLIN, 4)
                .mob(EntityTypes.BREEZE, 4)
                .mob(EntityTypes.SKELETON, 3).mob(EntityTypes.HUSK, 3).mob(EntityTypes.CAVE_SPIDER, 3)
                .mob(EntityTypes.SILVERFISH, 3).mob(EntityTypes.BLAZE, 3).mob(EntityTypes.GHAST, 3)
                .mob(EntityTypes.ZOMBIFIED_PIGLIN, 3).mob(EntityTypes.VINDICATOR, 3).mob(EntityTypes.EVOKER, 3)
                .mob(EntityTypes.ZOGLIN, 2.5)
                .mob(EntityTypes.ZOMBIE, 2).mob(EntityTypes.ZOMBIE_VILLAGER, 2).mob(EntityTypes.SPIDER, 2)
                .mob(EntityTypes.SLIME, 2).mob(EntityTypes.MAGMA_CUBE, 2).mob(EntityTypes.ENDERMITE, 2)
                .mob(EntityTypes.SHULKER, 2).mob(EntityTypes.STRAY, 2).mob(EntityTypes.BOGGED, 2)
                .mob(EntityTypes.PILLAGER, 2).mob(EntityTypes.PIGLIN, 2).mob(EntityTypes.POLAR_BEAR, 2)
                .mob(EntityTypes.IRON_GOLEM, 2)
                .mob(EntityTypes.ENDERMAN, 1).mob(EntityTypes.DROWNED, 1).mob(EntityTypes.GUARDIAN, 1)
                .mob(EntityTypes.WITCH, 0.1)
                // Animals with their own values (mcMMO)
                .mob(EntityTypes.AXOLOTL, 1.75).mob(EntityTypes.FROG, 1.7)
                .mob(EntityTypes.GOAT, 1.5).mob(EntityTypes.BEE, 1.5).mob(EntityTypes.GLOW_SQUID, 1.5)
                .mob(EntityTypes.DOLPHIN, 1.3)
                .mob(EntityTypes.MOOSHROOM, 1.2).mob(EntityTypes.CAMEL, 1.2).mob(EntityTypes.STRIDER, 1.2)
                .mob(EntityTypes.ZOMBIE_HORSE, 1.2)
                .mob(EntityTypes.TURTLE, 1.1).mob(EntityTypes.ARMADILLO, 1.1).mob(EntityTypes.SNIFFER, 1.1)
                .mob(EntityTypes.ALLAY, 0.2).mob(EntityTypes.TADPOLE, 0.2)
                // Never give XP
                .noXp(EntityTypes.SNOW_GOLEM)
                .noXp(EntityTypes.ARMOR_STAND)
                .build();
    }
}