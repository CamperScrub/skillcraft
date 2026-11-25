package com.camperscrub.skillcraft.client.hud;

import com.camperscrub.skillcraft.data.PlayerSkills;
import com.camperscrub.skillcraft.data.SkillAttachments;
import com.camperscrub.skillcraft.feedback.SkillStyle;
import com.camperscrub.skillcraft.skill.Skill;
import com.camperscrub.skillcraft.xp.XpChangeTracker;
import com.camperscrub.skillcraft.xp.XpCurve;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import java.util.Optional;

/** The skill XP bar at the top of the screen (see design doc: Level-up feedback). */
public final class XpBarHud implements HudElement {

    private static final int SHOW_TICKS = 60;          // fully visible for 3 seconds
    private static final int FADE_TICKS = 20;          // then fades over 1 second
    private static final int MIN_VISIBLE_ALPHA = 5;    // see note below
    private static final int BAR_WIDTH = 182;          // same width as vanilla's boss bar
    private static final int BAR_HEIGHT = 5;
    private static final int TEXT_Y = 4;
    private static final int BAR_Y = 15;
    private static final int TOTAL_HEIGHT = 22; // label + bar + a little gap

    private final XpChangeTracker tracker;
    private final XpCurve curve;

    public XpBarHud(XpChangeTracker tracker, XpCurve curve) {
        this.tracker = tracker;
        this.curve = curve;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft client = Minecraft.getInstance();
        Optional<Skill> active = tracker.activeSkill();
        if (client.player == null || active.isEmpty()) {
            return;
        }
        int alpha = alphaFor(tracker.ticksSinceGain());
        if (alpha < MIN_VISIBLE_ALPHA) {
            return;
        }

        Skill skill = active.get();
        long xp = client.player.getAttachedOrElse(SkillAttachments.PLAYER_SKILLS, PlayerSkills.EMPTY).xp(skill);
        int level = curve.levelForXp(xp);
        boolean maxed = level == curve.maxLevel();
        float progress = curve.progressToNextLevel(xp);

        int centerX = graphics.guiWidth() / 2;
        int left = centerX - BAR_WIDTH / 2;

        Component label = SkillStyle.name(skill)
                .append(Component.literal("  Lv " + level).withStyle(ChatFormatting.WHITE))
                .append(Component.literal(maxed ? "  MAX" : "  (" + (int) (progress * 100) + "%)")
                        .withStyle(maxed ? ChatFormatting.GOLD : ChatFormatting.GRAY))
                .append(Component.literal(String.format("  +%,d XP", tracker.recentGain()))
                        .withStyle(ChatFormatting.GREEN));
        graphics.centeredText(client.font, label, centerX, TEXT_Y, withAlpha(0xFFFFFF, alpha));

        // Dark frame, then the filled part in the skill's colour
        graphics.fill(left - 1, BAR_Y - 1, left + BAR_WIDTH + 1, BAR_Y + BAR_HEIGHT + 1, withAlpha(0x000000, alpha * 3 / 4));
        int filled = Math.round(BAR_WIDTH * progress);
        graphics.fill(left, BAR_Y, left + filled, BAR_Y + BAR_HEIGHT, withAlpha(SkillStyle.rgb(skill), alpha));
    }

    /** 255 while showing, then fading to 0. */
    private static int alphaFor(int ticksSinceGain) {
        if (ticksSinceGain <= SHOW_TICKS) {
            return 255;
        }
        int fadeTick = ticksSinceGain - SHOW_TICKS;
        if (fadeTick >= FADE_TICKS) {
            return 0;
        }
        return 255 - fadeTick * 255 / FADE_TICKS;
    }

    /** Combines a 0xRRGGBB colour with an alpha (0–255) into 0xAARRGGBB. */
    private static int withAlpha(int rgb, int alpha) {
        return (alpha << 24) | (rgb & 0xFFFFFF);
    }

    private static final int SLIDE_TICKS = 6; // boss bars slide back up over 0.3 s, after the fade

    /** How far down other top-of-screen elements should move right now (0 when hidden). */
    public int verticalSpaceUsed() {
        if (Minecraft.getInstance().player == null || tracker.activeSkill().isEmpty()) {
            return 0;
        }
        int ticks = tracker.ticksSinceGain();
        int fadeEnds = SHOW_TICKS + FADE_TICKS;
        if (ticks <= fadeEnds) {
            return TOTAL_HEIGHT;                       // our bar is still visible (even faintly): keep the space
        }
        int slideTick = ticks - fadeEnds;
        if (slideTick >= SLIDE_TICKS) {
            return 0;                                  // fully back in place
        }
        return TOTAL_HEIGHT * (SLIDE_TICKS - slideTick) / SLIDE_TICKS;
    }
}