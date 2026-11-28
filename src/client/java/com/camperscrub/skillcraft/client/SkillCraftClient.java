package com.camperscrub.skillcraft.client;

import com.camperscrub.skillcraft.SkillCraft;
import com.camperscrub.skillcraft.client.hud.XpBarHud;
import com.camperscrub.skillcraft.client.screen.SkillsScreen;
import com.camperscrub.skillcraft.data.PlayerSkills;
import com.camperscrub.skillcraft.data.SkillAttachments;
import com.camperscrub.skillcraft.network.ReadyAbilityPayload;
import com.camperscrub.skillcraft.skill.mining.SuperBreaker;
import com.camperscrub.skillcraft.tree.SkillPointRules;
import com.camperscrub.skillcraft.xp.LinearTailSettings;
import com.camperscrub.skillcraft.xp.LinearTailXpCurve;
import com.camperscrub.skillcraft.xp.XpChangeTracker;
import com.camperscrub.skillcraft.xp.XpCurve;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import org.lwjgl.sdl.SDLScancode;

import java.util.List;

/** Client-side composition root: everything that only exists on the player's computer. */
public class SkillCraftClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Shared logic (later the server will send its curve settings instead of using the defaults)
        XpCurve curve = new LinearTailXpCurve(LinearTailSettings.DEFAULTS);
        SkillPointRules pointRules = new SkillPointRules();
        XpChangeTracker tracker = new XpChangeTracker();

        // Keybinds (shown under "SkillCraft" in Options > Controls)
        KeyMapping.Category category = KeyMapping.Category.register(SkillCraft.id("main"));
        KeyMapping openSkills = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.skillcraft.open_skills", InputConstants.Type.KEYBOARD, SDLScancode.SDL_SCANCODE_K, category));
        KeyMapping readyAbility = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.skillcraft.ready_ability", InputConstants.Type.KEYBOARD, SDLScancode.SDL_SCANCODE_R, category));

        // Super abilities: client copy of the server's ability phases, and the break speed boost
        new ClientAbilities(List.of(new SuperBreaker())).register();

        // Every client tick: watch for XP gains and handle key presses
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) {
                tracker.reset(); // left the world
                return;
            }
            tracker.tick(client.player.getAttachedOrElse(SkillAttachments.PLAYER_SKILLS, PlayerSkills.EMPTY));

            while (openSkills.consumeClick()) {
                client.gui.setScreen(new SkillsScreen(curve, pointRules));
            }
            while (readyAbility.consumeClick()) {
                ClientPlayNetworking.send(ReadyAbilityPayload.INSTANCE);
            }
        });

        // HUD: the XP bar, with vanilla's boss bars moved down while it's showing
        XpBarHud xpBar = new XpBarHud(tracker, curve);

        HudElementRegistry.replaceElement(VanillaHudElements.BOSS_BAR, original -> (graphics, deltaTracker) -> {
            graphics.pose().pushMatrix();
            graphics.pose().translate(0, xpBar.verticalSpaceUsed());
            original.extractRenderState(graphics, deltaTracker);
            graphics.pose().popMatrix();
        });

        HudElementRegistry.attachElementAfter(VanillaHudElements.BOSS_BAR, SkillCraft.id("xp_bar"), xpBar);
    }
}
