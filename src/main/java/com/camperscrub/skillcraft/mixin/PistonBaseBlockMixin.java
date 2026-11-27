package com.camperscrub.skillcraft.mixin;

import com.camperscrub.skillcraft.event.BlocksPushedCallback;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(PistonBaseBlock.class)
public abstract class PistonBaseBlockMixin {

    /** Once the piston has worked out what it will move (and the move is allowed), tell listeners. */
    @WrapOperation(method = "moveBlocks", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/piston/PistonStructureResolver;resolve()Z"))
    private boolean skillcraft$afterResolve(PistonStructureResolver resolver, Operation<Boolean> original,
                                            @Local(argsOnly = true) Level level) {
        boolean canMove = original.call(resolver);
        if (canMove && !level.isClientSide()) {
            Direction direction = resolver.getPushDirection();
            List<BlockPos> from = List.copyOf(resolver.getToPush());
            List<BlockPos> to = new ArrayList<>(from.size());
            for (BlockPos pos : from) {
                to.add(pos.relative(direction));
            }
            BlocksPushedCallback.EVENT.invoker().onBlocksPushed(level, from, to, List.copyOf(resolver.getToDestroy()));
        }
        return canMove;
    }
}
