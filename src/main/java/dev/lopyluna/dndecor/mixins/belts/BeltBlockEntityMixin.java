package dev.lopyluna.dndecor.mixins.belts;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.content.kinetics.belt.BeltBlock;
import com.zurrtum.create.content.kinetics.belt.BeltBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = BeltBlockEntity.class, remap = false)
public abstract class BeltBlockEntityMixin {

    @WrapOperation(method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"))
    public boolean tick(BlockState state, Object block, Operation<Boolean> original) {
        return block == AllBlocks.BELT ? state.getBlock() instanceof BeltBlock : original.call(state, block);
    }

    @WrapOperation(method = "hasPulley",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"))
    public boolean hasPulley(BlockState state, Object block, Operation<Boolean> original) {
        return block == AllBlocks.BELT ? state.getBlock() instanceof BeltBlock : original.call(state, block);
    }
}
