package dev.lopyluna.dndecor.mixins;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.zurrtum.create.content.kinetics.crusher.CrushingWheelBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = CrushingWheelBlock.class, remap = false)
public abstract class CrushingWheelBlockMixin extends RotatedPillarKineticBlock {
    public CrushingWheelBlockMixin(Properties properties) {
        super(properties);
    }

    @WrapOperation(method = "updateControllers",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$BlockStateBase;is(Lnet/minecraft/world/level/block/Block;)Z"))
    public boolean updateControllersCheckInstance(BlockBehaviour.BlockStateBase state, Block block, Operation<Boolean> original) {
        return block == AllBlocks.CRUSHING_WHEEL ? state.getBlock() instanceof CrushingWheelBlock : original.call(state, block);
    }

    @WrapOperation(method = "canSurvive",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$BlockStateBase;is(Lnet/minecraft/world/level/block/Block;)Z"))
    public boolean canSurviveCheckInstance(BlockBehaviour.BlockStateBase state, Block block, Operation<Boolean> original) {
        return block == AllBlocks.CRUSHING_WHEEL ? state.getBlock() instanceof CrushingWheelBlock : original.call(state, block);
    }
}
