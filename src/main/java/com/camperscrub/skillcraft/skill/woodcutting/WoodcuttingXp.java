package com.camperscrub.skillcraft.skill.woodcutting;

import com.camperscrub.skillcraft.skill.Skill;
import com.camperscrub.skillcraft.xp.BlockXpTable;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;

public final class WoodcuttingXp {

    private WoodcuttingXp() {}

    public static BlockXpTable table() {
        return BlockXpTable.builder(Skill.WOODCUTTING)
                .tag(ConventionalBlockTags.NATURAL_LOGS, 70) // natural logs and stems; stripped logs don't count
                .build();
    }
}