package dev.lopyluna.dndecor.mixins.belts;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.content.kinetics.belt.BeltBlock;
import com.zurrtum.create.content.kinetics.belt.BeltBlockEntity;
import dev.lopyluna.dndecor.port.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Debug(export=true)
@Mixin(value = BeltBlockEntity.class, remap = false)
public abstract class BeltBlockEntityMixin {

    @WrapOperation(method = "tick()V",
            at = @At(value = "INVOKE", target = "Lcom/tterrag/registrate/util/entry/BlockEntry;has(Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    public boolean tick(BlockEntry<?> instance, BlockState state, Operation<Boolean> original) {
        return instance == AllBlocks.BELT ? state.getBlock() instanceof BeltBlock : original.call(instance, state);
    }

    @WrapOperation(method = "hasPulley()Z",
            at = @At(value = "INVOKE", target = "Lcom/tterrag/registrate/util/entry/BlockEntry;has(Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    public boolean hasPulley(BlockEntry<?> instance, BlockState state, Operation<Boolean> original) {
        return instance == AllBlocks.BELT ? state.getBlock() instanceof BeltBlock : original.call(instance, state);
    }
}
