package com.camperscrub.skillcraft.feedback;

import com.camperscrub.skillcraft.skill.Skill;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

/** Harmless celebration fireworks for skill milestones. */
public final class MilestoneFireworks {

    private static final int ROCKETS_PER_MILESTONE = 3;
    private static final int ROCKETS_AT_MASTERY = 8;
    private static final double SPREAD = 2.5;   // blocks either side of the player
    private static final int GOLD = 0xFFD700;
    private static final int WHITE = 0xFFFFFF;

    // Rockets we launched. Weak references: an entry disappears once its rocket is gone.
    private final Set<Entity> celebrationRockets = Collections.newSetFromMap(new WeakHashMap<>());
    private final int maxLevel;

    public MilestoneFireworks(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    /** Makes our rockets harmless. Call once at startup. */
    public void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((target, source, amount) ->
                !celebrationRockets.contains(source.getDirectEntity()));
    }

    public void launch(Player player, Skill skill, int milestone) {
        boolean mastery = milestone == maxLevel;
        Level level = player.level();
        RandomSource random = level.getRandom();
        ItemStack pattern = rocketItem(skill, mastery);

        int count = mastery ? ROCKETS_AT_MASTERY : ROCKETS_PER_MILESTONE;
        for (int i = 0; i < count; i++) {
            double x = player.getX() + (random.nextDouble() * 2 - 1) * SPREAD;
            double z = player.getZ() + (random.nextDouble() * 2 - 1) * SPREAD;
            FireworkRocketEntity rocket = new FireworkRocketEntity(level, x, player.getY() + 0.5, z, pattern.copy());
            celebrationRockets.add(rocket);
            level.addFreshEntity(rocket);
        }
    }

    private static ItemStack rocketItem(Skill skill, boolean mastery) {
        int skillColour = SkillStyle.rgb(skill);
        FireworkExplosion burst = new FireworkExplosion(
                mastery ? FireworkExplosion.Shape.STAR : FireworkExplosion.Shape.LARGE_BALL,
                IntList.of(skillColour, GOLD),  // colours
                IntList.of(WHITE),              // fade-to colour
                true,                           // trail
                mastery);                       // twinkle, for mastery only
        ItemStack stack = new ItemStack(Items.FIREWORK_ROCKET);
        stack.set(DataComponents.FIREWORKS, new Fireworks(1, List.of(burst)));
        return stack;
    }
}