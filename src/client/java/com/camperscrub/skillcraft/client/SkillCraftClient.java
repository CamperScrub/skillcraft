package com.camperscrub.skillcraft.client;

import com.camperscrub.skillcraft.SkillCraft;
import com.camperscrub.skillcraft.client.hud.XpBarHud;
import com.camperscrub.skillcraft.data.PlayerSkills;
import com.camperscrub.skillcraft.data.SkillAttachments;
import com.camperscrub.skillcraft.xp.LinearTailSettings;
import com.camperscrub.skillcraft.xp.LinearTailXpCurve;
import com.camperscrub.skillcraft.xp.XpChangeTracker;
import com.camperscrub.skillcraft.xp.XpCurve;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;

public class SkillCraftClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        XpCurve curve = new LinearTailXpCurve(LinearTailSettings.DEFAULTS);
        XpChangeTracker tracker = new XpChangeTracker();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) {
                tracker.reset(); // left the world
            } else {
                tracker.tick(client.player.getAttachedOrElse(SkillAttachments.PLAYER_SKILLS, PlayerSkills.EMPTY));
            }
        });

        XpBarHud xpBar = new XpBarHud(tracker, curve);

        // Move vanilla's boss bars down while our XP bar is showing
        HudElementRegistry.replaceElement(VanillaHudElements.BOSS_BAR, original -> (graphics, deltaTracker) -> {
            graphics.pose().pushMatrix();
            graphics.pose().translate(0, xpBar.verticalSpaceUsed());
            original.extractRenderState(graphics, deltaTracker);
            graphics.pose().popMatrix();
        });

        HudElementRegistry.attachElementAfter(VanillaHudElements.BOSS_BAR, SkillCraft.id("xp_bar"), xpBar);
    }
}