package com.camperscrub.skillcraft.skill.mining;

import com.camperscrub.skillcraft.skill.Skill;
import com.camperscrub.skillcraft.xp.SkillLevels;
import com.camperscrub.skillcraft.yield.YieldSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Mining's level 0 passive: Yield on valuable blocks (the {@code skillcraft:mining_yield} tag),
 * 0.1 per Mining level, so 100 at level 1000.
 */
public final class BountifulVeins implements YieldSource {

    private final SkillLevels levels;

    public BountifulVeins(SkillLevels levels) {
        this.levels = levels;
    }

    /**
     * Yield at a Mining level: 0 → 0, 500 → 50, 812 → 81.2, 1000 → 100.
     *
     * <p>EXERCISE 3. 0.1 per level. Divide by 10.0 rather than multiplying by 0.1: 0.1 can't be
     * stored exactly in a double, so {@code 12 * 0.1} gives 1.2000000000000002, while
     * {@code 12 / 10.0} gives the same double as typing 1.2.
     */
    public static double yieldAtLevel(int level) {
        return level / 10.0;
    }

    @Override
    public double yieldFor(ServerPlayer player, BlockState state) {
        if (!state.is(MiningTags.MINING_YIELD)) {
            return 0;
        }
        return yieldAtLevel(levels.level(player, Skill.MINING));
    }
}