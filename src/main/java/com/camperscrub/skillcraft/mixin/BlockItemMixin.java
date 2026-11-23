package com.camperscrub.skillcraft.mixin;

import com.camperscrub.skillcraft.event.BlockPlacedCallback;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {

    @Inject(method = "placeBlock", at = @At("RETURN"))
    private void skillcraft$afterPlaceBlock(BlockPlaceContext context, BlockState state,
                                            CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            return; // placement failed
        }
        Level level = context.getLevel();
        if (level.isClientSide()) {
            return; // only the server tracks placed blocks
        }
        BlockPlacedCallback.EVENT.invoker().onBlockPlaced(level, context.getClickedPos());
    }
}