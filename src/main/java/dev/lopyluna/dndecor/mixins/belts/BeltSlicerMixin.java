package dev.lopyluna.dndecor.mixins.belts;

import com.zurrtum.create.content.kinetics.belt.item.BeltConnectorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.InteractionResult;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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

    @WrapOperation(method = "useWrench",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"))
    private static boolean useWrench(BlockState state, Object block, Operation<Boolean> original) {
        return block == AllBlocks.BELT ? state.getBlock() instanceof BeltBlock : original.call(state, block);
    }
    @WrapOperation(method = "useConnector",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"))
    private static boolean useConnector(BlockState state, Object block, Operation<Boolean> original) {
        return block == AllBlocks.BELT ? state.getBlock() instanceof BeltBlock : original.call(state, block);
    }

    @WrapOperation(method = "useWrench",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z"))
    private static boolean useWrenchIsIn(ItemStack stack, Object instance, Operation<Boolean> original, BlockState state) {
        return instance == AllItems.BELT_CONNECTOR ?
                state.getBlock() instanceof FullBeltBlock ? original.call(stack, (Object) DnDecorItems.BELT_CONNECTOR.get()) : original.call(stack, instance) : original.call(stack, instance);
    }
    @WrapOperation(method = "useWrench",
            at = @At(value = "INVOKE", target = "Lcom/zurrtum/create/content/kinetics/belt/item/BeltConnectorItem;getDefaultInstance()Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack useWrenchStack(BeltConnectorItem instance, Operation<ItemStack> original, BlockState state) {
        return instance == AllItems.BELT_CONNECTOR ?
                state.getBlock() instanceof FullBeltBlock ? DnDecorItems.BELT_CONNECTOR.get().getDefaultInstance() : original.call(instance) : original.call(instance);
    }
    @WrapOperation(method = "useConnector",
            at = @At(value = "INVOKE", target = "Lcom/zurrtum/create/content/kinetics/belt/item/BeltConnectorItem;getDefaultInstance()Lnet/minecraft/world/item/ItemStack;"))
    private static ItemStack useConnectorStack(BeltConnectorItem instance, Operation<ItemStack> original, BlockState state) {
        return instance == AllItems.BELT_CONNECTOR ?
                state.getBlock() instanceof FullBeltBlock ? DnDecorItems.BELT_CONNECTOR.get().getDefaultInstance() : original.call(instance) : original.call(instance);
    }
}
