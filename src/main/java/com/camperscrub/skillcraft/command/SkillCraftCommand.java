package com.camperscrub.skillcraft.command;

import com.camperscrub.skillcraft.SkillCraft;
import com.camperscrub.skillcraft.data.PlayerSkills;
import com.camperscrub.skillcraft.skill.Skill;
import com.camperscrub.skillcraft.xp.XpAward;
import com.camperscrub.skillcraft.xp.XpCurve;
import com.camperscrub.skillcraft.xp.XpService;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.permission.v1.PermissionPredicates;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionLevel;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/** /skillcraft: operator-only commands for testing. */
public final class SkillCraftCommand {

    private static final DynamicCommandExceptionType UNKNOWN_SKILL =
            new DynamicCommandExceptionType(id -> Component.literal("Unknown skill: " + id));

    private final XpService xpService;
    private final XpCurve curve;

    public SkillCraftCommand(XpService xpService, XpCurve curve) {
        this.xpService = xpService;
        this.curve = curve;
    }

    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, selection) -> dispatcher.register(
                literal("skillcraft")
                        .requires(PermissionPredicates.require(SkillCraft.id("command/debug"), PermissionLevel.GAMEMASTERS))
                        .then(literal("show").executes(this::show))
                        .then(literal("addxp")
                                .then(argument("skill", StringArgumentType.word()).suggests(this::suggestSkills)
                                        .then(argument("amount", LongArgumentType.longArg(0)).executes(this::addXp))))
                        .then(literal("setlevel")
                                .then(argument("skill", StringArgumentType.word()).suggests(this::suggestSkills)
                                        .then(argument("level", IntegerArgumentType.integer(0, curve.maxLevel()))
                                                .executes(this::setLevel))))));
    }

    private int show(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        PlayerSkills skills = xpService.skillsOf(player);
        for (Skill skill : Skill.values()) {
            long xp = skills.xp(skill);
            int level = curve.levelForXp(xp);
            context.getSource().sendSuccess(() -> Component.translatable(skill.translationKey())
                    .append(": level " + level + " (" + xp + " XP)"), false);
        }
        return Command.SINGLE_SUCCESS;
    }

    private int addXp(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Skill skill = skillArgument(context);
        long amount = LongArgumentType.getLong(context, "amount");

        XpAward award = xpService.award(player, skill, amount);
        context.getSource().sendSuccess(() -> Component.translatable(skill.translationKey())
                .append(" +" + award.gained() + " XP (now level " + award.newLevel() + ")"), false);
        return Command.SINGLE_SUCCESS;
    }

    private int setLevel(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Skill skill = skillArgument(context);
        int level = IntegerArgumentType.getInteger(context, "level");

        long target = curve.totalXpForLevel(level);
        long current = xpService.skillsOf(player).xp(skill);
        if (target >= current) {
            xpService.award(player, skill, target - current); // going up: behaves like real XP gain
        } else {
            xpService.setTotalXp(player, skill, target);      // going down: just set it
        }
        context.getSource().sendSuccess(() -> Component.translatable(skill.translationKey())
                .append(" set to level " + level), false);
        return Command.SINGLE_SUCCESS;
    }

    private CompletableFuture<Suggestions> suggestSkills(CommandContext<CommandSourceStack> context,
                                                         SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(Arrays.stream(Skill.values()).map(Skill::id), builder);
    }

    private static Skill skillArgument(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String id = StringArgumentType.getString(context, "skill");
        return Skill.byId(id).orElseThrow(() -> UNKNOWN_SKILL.create(id));
    }
}