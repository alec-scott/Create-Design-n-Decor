package dev.lopyluna.dndecor.mixins.belts;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.content.kinetics.belt.BeltBlock;
import com.zurrtum.create.content.logistics.tunnel.BeltTunnelBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = BeltTunnelBlock.class, remap = false)
public class BeltTunnelBlockMixin {

    @WrapOperation(method = "isValidPositionForPlacement",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"))
    private static boolean isValidPositionForPlacement(BlockState state, Object block, Operation<Boolean> original) {
        return block == AllBlocks.BELT ? state.getBlock() instanceof BeltBlock : original.call(state, block);
    }
    @WrapOperation(method = "getTunnelState",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"))
    private static boolean getTunnelState(BlockState state, Object block, Operation<Boolean> original) {
        return block == AllBlocks.BELT ? state.getBlock() instanceof BeltBlock : original.call(state, block);
    }
    @WrapOperation(method = "hasValidOutput",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"))
    private static boolean hasValidOutput(BlockState state, Object block, Operation<Boolean> original) {
        return block == AllBlocks.BELT ? state.getBlock() instanceof BeltBlock : original.call(state, block);
    }
}
