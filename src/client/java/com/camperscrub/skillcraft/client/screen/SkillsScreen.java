package com.camperscrub.skillcraft.client.screen;

import com.camperscrub.skillcraft.data.PlayerSkills;
import com.camperscrub.skillcraft.data.SkillAttachments;
import com.camperscrub.skillcraft.feedback.SkillStyle;
import com.camperscrub.skillcraft.skill.Skill;
import com.camperscrub.skillcraft.tree.SkillPointRules;
import com.camperscrub.skillcraft.xp.XpCurve;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Shows every skill's level and progress. Opened with the "Open Skills" key (K by default). */
public final class SkillsScreen extends Screen {

    private static final int ROW_WIDTH = 240;
    private static final int ROW_HEIGHT = 38;
    private static final int ROW_GAP = 6;
    private static final int PADDING = 8;
    private static final int BAR_HEIGHT = 5;

    private static final int PANEL = 0x88000000;     // translucent black
    private static final int BAR_EMPTY = 0xFF222222;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int GREY = 0xFFAAAAAA;
    private static final int GREEN = 0xFF55FF55;

    private final XpCurve curve;
    private final SkillPointRules pointRules;

    public SkillsScreen(XpCurve curve, SkillPointRules pointRules) {
        super(Component.translatable("skillcraft.screen.skills.title"));
        this.curve = curve;
        this.pointRules = pointRules;
    }

    @Override
    public boolean isPauseScreen() {
        return false; // like the inventory: the game keeps running behind it
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        if (minecraft == null || minecraft.player == null) {
            return;
        }
        PlayerSkills skills = minecraft.player.getAttachedOrElse(SkillAttachments.PLAYER_SKILLS, PlayerSkills.EMPTY);

        Skill[] all = Skill.values();
        int listHeight = all.length * ROW_HEIGHT + (all.length - 1) * ROW_GAP;
        int left = width / 2 - ROW_WIDTH / 2;
        int top = height / 2 - listHeight / 2;

        graphics.centeredText(font, title, width / 2, top - 20, WHITE);

        int totalLevel = 0;
        for (int i = 0; i < all.length; i++) {
            int y = top + i * (ROW_HEIGHT + ROW_GAP);
            totalLevel += drawRow(graphics, all[i], skills.xp(all[i]), left, y);
        }

        graphics.centeredText(font, Component.translatable("skillcraft.screen.skills.total", totalLevel),
                width / 2, top + listHeight + 10, GREY);
    }

    /** Draws one skill's row and returns its level. */
    private int drawRow(GuiGraphicsExtractor graphics, Skill skill, long xp, int left, int y) {
        int level = curve.levelForXp(xp);
        boolean maxed = level == curve.maxLevel();
        int colour = 0xFF000000 | SkillStyle.rgb(skill);
        int right = left + ROW_WIDTH;

        // Panel with a coloured stripe on the left
        graphics.fill(left, y, right, y + ROW_HEIGHT, PANEL);
        graphics.fill(left, y, left + 2, y + ROW_HEIGHT, colour);

        // Line 1: name (left) and level (right)
        graphics.text(font, SkillStyle.name(skill), left + PADDING, y + 5, WHITE);
        Component levelText = Component.translatable("skillcraft.screen.skills.level", level);
        graphics.text(font, levelText, right - PADDING - font.width(levelText), y + 5, WHITE);

        // Line 2: progress bar
        int barLeft = left + PADDING;
        int barWidth = ROW_WIDTH - 2 * PADDING;
        int barY = y + 17;
        graphics.fill(barLeft, barY, barLeft + barWidth, barY + BAR_HEIGHT, BAR_EMPTY);
        int filled = Math.round(barWidth * curve.progressToNextLevel(xp));
        graphics.fill(barLeft, barY, barLeft + filled, barY + BAR_HEIGHT, colour);

        // Line 3: XP detail (left) and skill points (right)
        Component detail = maxed
                ? Component.translatable("skillcraft.screen.skills.maxed", formatted(xp))
                : Component.translatable("skillcraft.screen.skills.progress",
                formatted(curve.xpIntoLevel(xp)), formatted(curve.xpSpanOfLevel(xp)));
        graphics.text(font, detail, barLeft, y + 26, GREY);
        Component points = Component.translatable("skillcraft.screen.skills.points", pointRules.pointsAt(level));
        graphics.text(font, points, right - PADDING - font.width(points), y + 26, GREEN);

        return level;
    }

    private static String formatted(long number) {
        return String.format("%,d", number);
    }
}