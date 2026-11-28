package com.camperscrub.skillcraft.mixin;

import com.camperscrub.skillcraft.ability.BreakSpeedBoosts;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerMixin {

    /** Applies ability speed boosts. Runs on both sides, so the client and server agree on break times. */
    @ModifyReturnValue(method = "getDestroySpeed", at = @At("RETURN"))
    private float skillcraft$boostDestroySpeed(float speed, @Local(argsOnly = true) BlockState state) {
        return speed * BreakSpeedBoosts.multiplier((Player) (Object) this, state);
    }
}
