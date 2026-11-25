package com.camperscrub.skillcraft;

import com.camperscrub.skillcraft.command.SkillCraftCommand;
import com.camperscrub.skillcraft.data.*;
import com.camperscrub.skillcraft.event.BlockBreakXpHandler;
import com.camperscrub.skillcraft.event.BlockPlacedCallback;
import com.camperscrub.skillcraft.event.CombatXpHandler;
import com.camperscrub.skillcraft.event.MobSpawnedCallback;
import com.camperscrub.skillcraft.skill.excavation.ExcavationXp;
import com.camperscrub.skillcraft.skill.mining.MiningXp;
import com.camperscrub.skillcraft.skill.swordsmanship.SwordsmanshipXp;
import com.camperscrub.skillcraft.skill.woodcutting.WoodcuttingXp;
import com.camperscrub.skillcraft.xp.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SkillCraft implements ModInitializer {
    public static final String MOD_ID = "skillcraft";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        SkillAttachments.init();

        XpCurve curve = new LinearTailXpCurve(LinearTailSettings.DEFAULTS);
        XpService xpService = new XpService(new XpRules(curve));
        PlacedBlockTracker placedBlocks = new ChunkPlacedBlockTracker();
        BlockPlacedCallback.EVENT.register(placedBlocks::markPlaced);

        new BlockBreakXpHandler(xpService, placedBlocks, List.of(
                MiningXp.table(),
                WoodcuttingXp.table(),
                ExcavationXp.table()
        )).register();

        SpawnOriginTracker spawnOrigins = new AttachmentSpawnOriginTracker();
        MobSpawnedCallback.EVENT.register(spawnOrigins::record);

        new CombatXpHandler(xpService, new CombatXpRules(0.5), SwordsmanshipXp.mobTable(), spawnOrigins).register();

        new SkillCraftCommand(xpService, curve).register();
    }
}
