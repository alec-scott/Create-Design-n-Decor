package dev.lopyluna.dndecor.mixins.belts;

import com.zurrtum.create.content.kinetics.belt.item.BeltConnectorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.InteractionResult;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.AllItems;
import com.zurrtum.create.content.kinetics.belt.BeltBlock;
import dev.lopyluna.dndecor.content.blocks.full_belt.FullBeltBlock;
import dev.lopyluna.dndecor.register.DnDecorItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = BeltBlock.class, remap = false)
public abstract class BeltBlockMixin {

    @WrapOperation(method = "updateEntityMovementAfterFallOn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"))
    public boolean updateEntityAfterFallOn(BlockState state, Object block, Operation<Boolean> original) {
        return block == AllBlocks.BELT ? state.getBlock() instanceof BeltBlock : original.call(state, block);
    }

    @WrapOperation(method = "canTransportObjects",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"))
    private static boolean canTransportObjects(BlockState state, Object block, Operation<Boolean> original) {
        return block == AllBlocks.BELT ? state.getBlock() instanceof BeltBlock : original.call(state, block);
    }

    @WrapOperation(method = "initBelt",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"))
    private static boolean initBelt(BlockState state, Object block, Operation<Boolean> original) {
        return block == AllBlocks.BELT ? state.getBlock() instanceof BeltBlock : original.call(state, block);
    }

    @WrapOperation(method = "affectNeighborsAfterRemoval",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"))
    public boolean onRemove(BlockState state, Object block, Operation<Boolean> original) {
        return block == AllBlocks.BELT ? state.getBlock() instanceof BeltBlock : original.call(state, block);
    }

    @WrapOperation(method = "getBeltChain",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"))
    private static boolean getBeltChain(BlockState state, Object block, Operation<Boolean> original) {
        return block == AllBlocks.BELT ? state.getBlock() instanceof BeltBlock : original.call(state, block);
    }

    @WrapOperation(method = "useItemOn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z"))
    private boolean useItemOnIsIn(ItemStack stack, Object instance, Operation<Boolean> original, @Local(argsOnly = true) BlockState state) {
        return instance == AllItems.BELT_CONNECTOR ? state.getBlock() instanceof FullBeltBlock ? original.call(stack, (Object) DnDecorItems.BELT_CONNECTOR.get()) : original.call(stack, instance) : original.call(stack, instance);
    }

    @WrapOperation(method = "getRequiredItems",
            at = @At(value = "INVOKE", target = "Lcom/zurrtum/create/content/kinetics/belt/item/BeltConnectorItem;getDefaultInstance()Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack getRequiredItemsAsStack(BeltConnectorItem instance, Operation<ItemStack> original, @Local(argsOnly = true) BlockState state) {
        return instance == AllItems.BELT_CONNECTOR ? state.getBlock() instanceof FullBeltBlock ? DnDecorItems.BELT_CONNECTOR.get().getDefaultInstance() : original.call(instance) : original.call(instance);
    }

    @WrapOperation(method = "getCloneItemStack",
            at = @At(value = "INVOKE", target = "Lcom/zurrtum/create/content/kinetics/belt/item/BeltConnectorItem;getDefaultInstance()Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack getCloneItemStackAsStack(BeltConnectorItem instance, Operation<ItemStack> original, @Local(argsOnly = true) BlockState state) {
        return instance == AllItems.BELT_CONNECTOR ? state.getBlock() instanceof FullBeltBlock ? DnDecorItems.BELT_CONNECTOR.get().getDefaultInstance() : original.call(instance) : original.call(instance);
    }
}
