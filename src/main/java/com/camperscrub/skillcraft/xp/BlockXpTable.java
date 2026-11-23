package com.camperscrub.skillcraft.xp;

import com.camperscrub.skillcraft.skill.Skill;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * How much XP one skill gives for breaking blocks. Entries are checked in order; the first match wins.
 */
public final class BlockXpTable {

    private final Skill skill;
    private final boolean requiresCorrectTool;
    private final List<Entry> entries;

    private BlockXpTable(Builder builder) {
        this.skill = builder.skill;
        this.requiresCorrectTool = builder.requiresCorrectTool;
        this.entries = List.copyOf(builder.entries);
    }

    public static Builder builder(Skill skill) {
        return new Builder(skill);
    }

    public Skill skill() {
        return skill;
    }

    /**
     * If true, XP only counts when the player's tool would make the block drop (no punching ore).
     */
    public boolean requiresCorrectTool() {
        return requiresCorrectTool;
    }

    /**
     * XP for breaking this block, or 0 if this table doesn't cover it.
     */
    public long xpFor(BlockState state) {
        for (Entry entry : entries) {
            if (entry.matches().test(state)) {
                return entry.xp();
            }
        }
        return 0L;
    }

    private record Entry(Predicate<BlockState> matches, long xp) {
    }

    public static final class Builder {
        private final Skill skill;
        private final List<Entry> entries = new ArrayList<>();
        private boolean requiresCorrectTool;

        private Builder(Skill skill) {
            this.skill = skill;
        }

        public Builder block(Block block, long xp) {
            return add(state -> state.is(block), xp);
        }

        public Builder tag(TagKey<Block> tag, long xp) {
            return add(state -> state.is(tag), xp);
        }

        public Builder requiresCorrectTool() {
            this.requiresCorrectTool = true;
            return this;
        }

        private Builder add(Predicate<BlockState> matches, long xp) {
            if (xp <= 0) throw new IllegalArgumentException("XP must be positive, got " + xp);
            entries.add(new Entry(matches, xp));
            return this;
        }

        public BlockXpTable build() {
            return new BlockXpTable(this);
        }

        public Builder blocks(long xp, Block... blocks) {
            for (Block block : blocks) {
                block(block, xp);
            }
            return this;
        }

        /**
         * Blocks that should never give this skill XP, even if a later tag would match them.
         */
        public Builder exclude(Block block) {
            entries.add(new Entry(state -> state.is(block), 0));
            return this;
        }
    }
}