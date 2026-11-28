package com.camperscrub.skillcraft.skill.mining;

import com.camperscrub.skillcraft.SkillCraft;
import com.camperscrub.skillcraft.ability.SuperAbility;
import com.camperscrub.skillcraft.skill.Skill;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Mining's super ability. While active: near-instant mining (here) and bonus Mining Yield
 * ({@code AbilityYield}, wired in SkillCraft). Ore Sense comes next.
 */
public final class SuperBreaker implements SuperAbility {

    private static final Identifier SPEED_BOOST = SkillCraft.id("super_breaker_speed");
    /** +400% break speed, so 5× normal: most pickaxe blocks break in one to three ticks. */
    private static final double SPEED_MULTIPLIER = 4.0;

    @Override
    public String id() {
        return "super_breaker";
    }

    @Override
    public Skill skill() {
        return Skill.MINING;
    }

    @Override
    public boolean isTool(ItemStack stack) {
        return stack.is(ItemTags.PICKAXES);
    }

    @Override
    public boolean activatesOn(BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_PICKAXE);
    }

    @Override
    public void start(ServerPlayer player, int level) {
        AttributeInstance breakSpeed = player.getAttribute(Attributes.BLOCK_BREAK_SPEED);
        if (breakSpeed != null && !breakSpeed.hasModifier(SPEED_BOOST)) {
            breakSpeed.addTransientModifier(new AttributeModifier(SPEED_BOOST, SPEED_MULTIPLIER,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
    }

    @Override
    public void end(ServerPlayer player) {
        AttributeInstance breakSpeed = player.getAttribute(Attributes.BLOCK_BREAK_SPEED);
        if (breakSpeed != null) {
            breakSpeed.removeModifier(SPEED_BOOST);
        }
    }
}