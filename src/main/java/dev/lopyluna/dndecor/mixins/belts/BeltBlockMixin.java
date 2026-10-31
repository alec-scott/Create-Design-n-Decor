package dev.lopyluna.dndecor.mixins.belts;

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

    @WrapOperation(method = "updateEntityAfterFallOn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$BlockStateBase;is(Lnet/minecraft/world/level/block/Block;)Z"))
    public boolean updateEntityAfterFallOn(BlockBehaviour.BlockStateBase state, Block block, Operation<Boolean> original) {
        return block == AllBlocks.BELT ? state.getBlock() instanceof BeltBlock : original.call(state, block);
    }

    @WrapOperation(method = "canTransportObjects",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$BlockStateBase;is(Lnet/minecraft/world/level/block/Block;)Z"))
    private static boolean canTransportObjects(BlockBehaviour.BlockStateBase state, Block block, Operation<Boolean> original) {
        return block == AllBlocks.BELT ? state.getBlock() instanceof BeltBlock : original.call(state, block);
    }

    @WrapOperation(method = "initBelt",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$BlockStateBase;is(Lnet/minecraft/world/level/block/Block;)Z"))
    private static boolean initBelt(BlockBehaviour.BlockStateBase state, Block block, Operation<Boolean> original) {
        return block == AllBlocks.BELT ? state.getBlock() instanceof BeltBlock : original.call(state, block);
    }

    @WrapOperation(method = "onRemove",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$BlockStateBase;is(Lnet/minecraft/world/level/block/Block;)Z"))
    public boolean onRemove(BlockBehaviour.BlockStateBase state, Block block, Operation<Boolean> original) {
        return block == AllBlocks.BELT ? state.getBlock() instanceof BeltBlock : original.call(state, block);
    }

    @WrapOperation(method = "getBeltChain",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$BlockStateBase;is(Lnet/minecraft/world/level/block/Block;)Z"))
    private static boolean getBeltChain(BlockBehaviour.BlockStateBase state, Block block, Operation<Boolean> original) {
        return block == AllBlocks.BELT ? state.getBlock() instanceof BeltBlock : original.call(state, block);
    }

    @WrapOperation(method = "useItemOn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    private boolean useItemOnIsIn(ItemStack stack, Item instance, Operation<Boolean> original, @Local(argsOnly = true) BlockState state) {
        return instance == AllItems.BELT_CONNECTOR ? state.getBlock() instanceof FullBeltBlock ? original.call(stack, DnDecorItems.BELT_CONNECTOR.get()) : original.call(stack, instance) : original.call(stack, instance);
    }

    @WrapOperation(method = "getRequiredItems",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;getDefaultInstance()Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack getRequiredItemsAsStack(Item instance, Operation<ItemStack> original, @Local(argsOnly = true) BlockState state) {
        return instance == AllItems.BELT_CONNECTOR ? state.getBlock() instanceof FullBeltBlock ? original.call(DnDecorItems.BELT_CONNECTOR.get()) : original.call(instance) : original.call(instance);
    }

    @WrapOperation(method = "getCloneItemStack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;getDefaultInstance()Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack getCloneItemStackAsStack(Item instance, Operation<ItemStack> original, @Local(argsOnly = true) BlockState state) {
        return instance == AllItems.BELT_CONNECTOR ? state.getBlock() instanceof FullBeltBlock ? original.call(DnDecorItems.BELT_CONNECTOR.get()) : original.call(instance) : original.call(instance);
    }
}
