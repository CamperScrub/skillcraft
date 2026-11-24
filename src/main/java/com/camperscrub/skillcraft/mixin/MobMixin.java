package com.camperscrub.skillcraft.mixin;

import com.camperscrub.skillcraft.event.MobSpawnedCallback;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin {

    @Inject(method = "finalizeSpawn", at = @At("HEAD"))
    private void skillcraft$onFinalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                            EntitySpawnReason reason, SpawnGroupData groupData,
                                            CallbackInfoReturnable<SpawnGroupData> cir) {
        MobSpawnedCallback.EVENT.invoker().onMobSpawned((Mob) (Object) this, reason);
    }
}