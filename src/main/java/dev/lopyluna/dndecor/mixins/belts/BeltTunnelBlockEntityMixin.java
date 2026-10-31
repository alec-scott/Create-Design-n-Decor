package dev.lopyluna.dndecor.mixins.belts;

import com.zurrtum.create.AllTransfer;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.content.kinetics.belt.BeltBlock;
import com.zurrtum.create.content.logistics.tunnel.BeltTunnelBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// PORT-NOTE(26.1): the andesite tunnel's inventory lookup moved into Create Fly's AllTransfer.register (lambda 6)
@Mixin(value = AllTransfer.class, remap = false)
public class BeltTunnelBlockEntityMixin {
    @WrapOperation(method = "lambda$register$6",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$BlockStateBase;is(Lnet/minecraft/world/level/block/Block;)Z"))
    private static boolean registerCapabilities(BlockBehaviour.BlockStateBase state, Block block, Operation<Boolean> original) {
        return block == AllBlocks.BELT ? state.getBlock() instanceof BeltBlock : original.call(state, block);
    }
}
