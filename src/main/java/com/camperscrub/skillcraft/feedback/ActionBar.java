package com.camperscrub.skillcraft.feedback;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/** Shows a message above the hotbar. */
@FunctionalInterface
public interface ActionBar {

    void show(Player player, Component message);
}