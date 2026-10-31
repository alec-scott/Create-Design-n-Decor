package dev.lopyluna.dndecor.mixins;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.content.kinetics.crusher.CrushingWheelBlock;
import com.zurrtum.create.content.kinetics.crusher.CrushingWheelControllerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = CrushingWheelControllerBlock.class, remap = false)
public class CrushingWheelControllerBlockMixin {

    @WrapOperation(method = "lambda$updateSpeed$0",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$BlockStateBase;is(Lnet/minecraft/world/level/block/Block;)Z"))
    private static boolean updateSpeed(BlockBehaviour.BlockStateBase state, Block block, Operation<Boolean> original) {
        return block == AllBlocks.CRUSHING_WHEEL ? state.getBlock() instanceof CrushingWheelBlock : original.call(state, block);
    }
}
