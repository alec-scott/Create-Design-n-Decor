package dev.lopyluna.dndecor.mixins.belts.client;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.AllItems;
import com.zurrtum.create.content.kinetics.belt.BeltBlock;
import com.zurrtum.create.content.kinetics.belt.BeltSlicer;
import dev.lopyluna.dndecor.content.blocks.full_belt.FullBeltBlock;
import dev.lopyluna.dndecor.register.DnDecorItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = BeltSlicer.class, remap = false)
public class BeltSlicerMixin {
    @WrapOperation(method = "tickHoveringInformation",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$BlockStateBase;is(Lnet/minecraft/world/level/block/Block;)Z"))
    private static boolean tickHoveringInformation(BlockBehaviour.BlockStateBase state, Block block, Operation<Boolean> original) {
        return block == AllBlocks.BELT ? state.getBlock() instanceof BeltBlock : original.call(state, block);
    }
    @WrapOperation(method = "tickHoveringInformation",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    private static boolean tickHoveringInformationIsIn(ItemStack stack, Item instance, Operation<Boolean> original, @Local(name = "state") BlockState state) {
        return instance == AllItems.BELT_CONNECTOR ?
                state.getBlock() instanceof FullBeltBlock ? original.call(stack, DnDecorItems.BELT_CONNECTOR.get()) : original.call(stack, instance) : original.call(stack, instance);
    }
}
