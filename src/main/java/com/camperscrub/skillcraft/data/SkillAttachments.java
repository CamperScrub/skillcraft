package com.camperscrub.skillcraft.data;

import com.camperscrub.skillcraft.SkillCraft;
import com.camperscrub.skillcraft.skill.Skill;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.*;

public final class SkillAttachments {

    private SkillAttachments() {
    }

    // --- Save format: readable and robust, keyed by skill id ---

    private static final Codec<Skill> SKILL_CODEC = Codec.STRING.comapFlatMap(
            id -> Skill.byId(id)
                    .map(DataResult::success)
                    .orElseGet(() -> DataResult.error(() -> "Unknown skill id: " + id)),
            Skill::id);

    private static final Codec<PlayerSkills> SAVE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(SKILL_CODEC, Codec.LONG)
                    .optionalFieldOf("xp", Map.of())
                    .forGetter(PlayerSkills::totalXp)
    ).apply(instance, PlayerSkills::new));

    // --- Network format: compact, one number per skill in enum order ---

    private static final StreamCodec<ByteBuf, PlayerSkills> SYNC_CODEC =
            ByteBufCodecs.VAR_LONG.apply(ByteBufCodecs.list())
                    .map(SkillAttachments::fromOrderedList, SkillAttachments::toOrderedList);

    // --- The attachment itself (must come AFTER the codecs; see below) ---

    public static final AttachmentType<PlayerSkills> PLAYER_SKILLS = AttachmentRegistry.create(
            SkillCraft.id("player_skills"),
            builder -> builder
                    .initializer(() -> PlayerSkills.EMPTY)
                    .persistent(SAVE_CODEC)
                    .copyOnDeath()
                    .syncWith(SYNC_CODEC, AttachmentSyncPredicate.targetOnly()));

    // --- Placed blocks (per chunk, server only) ---

    private static final Codec<PlacedBlocks> PLACED_BLOCKS_CODEC = Codec.LONG.listOf().xmap(
            list -> new PlacedBlocks(Set.copyOf(list)),
            placed -> List.copyOf(placed.positions()));

    public static final AttachmentType<PlacedBlocks> PLACED_BLOCKS = AttachmentRegistry.create(
            SkillCraft.id("placed_blocks"),
            builder -> builder.persistent(PLACED_BLOCKS_CODEC));

    // --- Spawn origin (per mob, server only) ---

    private static final Codec<SpawnOrigin> SPAWN_ORIGIN_CODEC = Codec.STRING.comapFlatMap(
            id -> SpawnOrigin.byId(id)
                    .map(DataResult::success)
                    .orElseGet(() -> DataResult.error(() -> "Unknown spawn origin: " + id)),
            SpawnOrigin::id);

    public static final AttachmentType<SpawnOrigin> SPAWN_ORIGIN = AttachmentRegistry.create(
            SkillCraft.id("spawn_origin"),
            builder -> builder
                    .persistent(SPAWN_ORIGIN_CODEC)
                    .copyOnDeath()); // on mobs, this also covers conversions like zombie → drowned

    /**
     * Call once from onInitialize so the attachment is registered at startup.
     */
    public static void init() {
    }

    private static List<Long> toOrderedList(PlayerSkills skills) {
        return Arrays.stream(Skill.values()).map(skills::xp).toList();
    }

    private static PlayerSkills fromOrderedList(List<Long> values) {
        Skill[] skills = Skill.values();
        Map<Skill, Long> map = new EnumMap<>(Skill.class);
        for (int i = 0; i < Math.min(skills.length, values.size()); i++) {
            map.put(skills[i], values.get(i));
        }
        return new PlayerSkills(map);
    }
}