package com.camperscrub.skillcraft.event;

import com.camperscrub.skillcraft.SkillCraft;
import com.camperscrub.skillcraft.data.PlacedBlockTracker;
import com.camperscrub.skillcraft.xp.BlockXpTable;
import com.camperscrub.skillcraft.xp.XpAward;
import com.camperscrub.skillcraft.xp.XpService;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * Turns "a block was broken" into XP awards. Holds no rules of its own.
 */
public final class BlockBreakXpHandler {

    private final XpService xpService;
    private final PlacedBlockTracker placedBlocks;
    private final List<BlockXpTable> tables;

    public BlockBreakXpHandler(XpService xpService, PlacedBlockTracker placedBlocks, List<BlockXpTable> tables) {
        this.xpService = xpService;
        this.placedBlocks = placedBlocks;
        this.tables = List.copyOf(tables);
    }

    public void register() {
        PlayerBlockBreakEvents.AFTER.register(this::onBlockBroken);
    }

    private void onBlockBroken(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        boolean wasPlaced = placedBlocks.unmark(level, pos);
        if (wasPlaced || player.isCreative()) {
            return;
        }
        for (BlockXpTable table : tables) {
            long amount = table.xpFor(state);
            if (amount == 0) {
                continue;
            }
            if (table.requiresCorrectTool() && !player.hasCorrectToolForDrops(state)) {
                continue;
            }
            XpAward award = xpService.award(player, table.skill(), amount);
            // TEMPORARY until we have on-screen feedback:
            SkillCraft.LOGGER.info("+{} {} XP (level {})", award.gained(), award.skill().id(), award.newLevel());
        }
    }
}