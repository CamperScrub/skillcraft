package com.camperscrub.skillcraft.xp;

import net.minecraft.world.entity.player.Player;

/** Notified after XP is awarded and saved. */
@FunctionalInterface
public interface XpListener {
    void onXpAwarded(Player player, XpAward award);
}