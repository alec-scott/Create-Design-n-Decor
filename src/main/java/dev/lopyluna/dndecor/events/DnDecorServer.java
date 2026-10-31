package dev.lopyluna.dndecor.events;

import net.minecraft.world.item.DyeColor;
import net.minecraft.core.component.DataComponents;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.content.kinetics.crusher.CrushingWheelBlock;
import com.zurrtum.create.content.kinetics.flywheel.FlywheelBlock;
import com.zurrtum.create.content.kinetics.millstone.MillstoneBlock;
import com.zurrtum.create.content.trains.display.FlapDisplayBlock;
import com.zurrtum.create.foundation.utility.BlockHelper;
import dev.lopyluna.dndecor.port.registrate.util.nullness.NonNullSupplier;
import dev.lopyluna.dndecor.DnDecorUtils;
import dev.lopyluna.dndecor.content.blocks.cogs.DnDCogwheelBlockItem;
import dev.lopyluna.dndecor.content.blocks.flywheel.FreeSpinBlock;
import dev.lopyluna.dndecor.content.blocks.text_plate.TextPlateBlock;
import dev.lopyluna.dndecor.register.DnDecorBlocks;
import dev.lopyluna.dndecor.register.helpers.list_providers.MaterialTypeProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;

import static dev.lopyluna.dndecor.DnDecor.MOD_ID;

@SuppressWarnings("unused")
public class DnDecorServer {

    // PORT-NOTE(26.1): was NeoForge's UseItemOnBlockEvent (ITEM_BEFORE_BLOCK phase); Fabric's UseBlockCallback runs at
    // the same point, before the block's own use handling
    public static void register() {
        UseBlockCallback.EVENT.register(DnDecorServer::onItemUseOnBlock);
    }

    public static InteractionResult onItemUseOnBlock(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
        if (player.isSpectator()) return InteractionResult.PASS;
        var cog = DnDCogwheelBlockItem.onItemUseFirst(player, level, hand, hit);
        if (cog != InteractionResult.PASS && cog != InteractionResult.TRY_WITH_EMPTY_HAND) return cog;
        var blockPos = hit.getBlockPos();
        var state = level.getBlockState(blockPos);
        var face = hit.getDirection();
        var stack = player.getItemInHand(hand);
        var context = new UseOnContext(player, hand, hit);

        if (stack.is(ConventionalItemTags.DYES) || stack.is(Items.SPONGE) || stack.is(Items.WET_SPONGE) || stack.is(Items.WHITE_WOOL) || stack.is(Items.WATER_BUCKET)) {
            var dye = dyeUseOn(level, player, state, blockPos, face, hand, stack, context);
            if (dye.consumesAction()) return dye;
        }

        var item = stack.getItem();
        for (var type : MaterialTypeProvider.stoneTypes) {
            if (item.equals(type.get().asItem())) {
                var stone = stoneUseOn(type, level, player, state, blockPos, face, hand, stack, context);
                if (stone.consumesAction()) return stone;
                break;
            }
        }
        return InteractionResult.PASS;
    }
    public static InteractionResult changeBlock(Level pLevel, BlockPos pPos, BlockState pFrom, BlockState pTo, SoundEvent pSound) {
        return changeBlock(pLevel, pPos, pFrom, pTo, pSound, null, false, null);
    }
    public static InteractionResult changeBlock(Level pLevel, BlockPos pPos, BlockState pFrom, BlockState pTo, SoundEvent pSound, SoundEvent pSound2, boolean pParticles, BlockState pParticleState) {
        var newState = BlockHelper.copyProperties(pFrom, pTo);
        if (!pFrom.equals(newState)) {
            if (pLevel.setBlockAndUpdate(pPos, newState)) {
                var volume = pSound != null && pSound2 != null ? 0.75f : 1.0f;
                if (pSound != null) pLevel.playSound(null, pPos, pSound, SoundSource.BLOCKS, volume, 1.1f - pLevel.getRandom().nextFloat() * .2f);
                if (pSound2 != null) pLevel.playSound(null, pPos, pSound2, SoundSource.BLOCKS, volume, 1.1f - pLevel.getRandom().nextFloat() * .2f);
                if (pParticles && pParticleState != null) pLevel.addDestroyBlockEffect(pPos, pTo);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @SuppressWarnings("deprecation")
    public static InteractionResult stoneUseOn(NonNullSupplier<Block> type, Level pLevel, Player pPlayer, BlockState pState, BlockPos pPos, Direction pFace,
                                               InteractionHand pHand, ItemStack pStack, UseOnContext pContext) {
        var typeState = type.get().defaultBlockState();
        var sound = typeState.getSoundType();
        var block = pState.getBlock();
        if (block instanceof CrushingWheelBlock) return changeBlock(pLevel, pPos, pState, DnDecorBlocks.STONE_TYPE_CRUSHING_WHEELS.get(type).getDefaultState(), sound.getBreakSound(), sound.getPlaceSound(), true, typeState);
        if (block instanceof MillstoneBlock) return changeBlock(pLevel, pPos, pState, DnDecorBlocks.STONE_TYPE_MILLSTONE.get(type).getDefaultState(), sound.getBreakSound(), sound.getPlaceSound(), true, typeState);
        return InteractionResult.PASS;
    }


    public static InteractionResult dyeUseOn(Level pLevel, Player pPlayer, BlockState pState, BlockPos pPos, Direction pFace,
                                             InteractionHand pHand, ItemStack pStack, UseOnContext pContext) {
        var block = pState.getBlock();
        if (pStack.getItem() instanceof DyeItem dyeItem) {
            var color = pStack.getOrDefault(DataComponents.DYE, DyeColor.WHITE);
            return switch (block) {
                case FlywheelBlock flywheelBlock when !(block instanceof FreeSpinBlock b && b.type == FreeSpinBlock.Type.LARGE_FAN) ->
                        changeBlock(pLevel, pPos, pState, DnDecorBlocks.DYED_FLYWHEELS.get(color).getDefaultState(), SoundEvents.DYE_USE);

                case FreeSpinBlock freeSpinBlock ->
                        changeBlock(pLevel, pPos, pState, DnDecorBlocks.DYED_LARGE_FANS.get(color).getDefaultState(), SoundEvents.DYE_USE);

                case FlapDisplayBlock flapDisplayBlock when pPlayer.isShiftKeyDown() ->
                        changeBlock(pLevel, pPos, pState, DnDecorBlocks.DYED_DISPLAY_BOARDS.get(color).getDefaultState(), SoundEvents.DYE_USE);

                case TextPlateBlock textPlate when pPlayer.isShiftKeyDown() ->
                        textPlate.applyDye(pLevel, pPos, color);

                default -> InteractionResult.PASS;
            };
        } else return switch (block) {
            case FlywheelBlock flywheelBlock when !(block instanceof FreeSpinBlock b && b.type == FreeSpinBlock.Type.LARGE_FAN) ->
                    changeBlock(pLevel, pPos, pState, AllBlocks.FLYWHEEL.defaultBlockState(), SoundEvents.SPONGE_ABSORB);

            case FreeSpinBlock freeSpinBlock ->
                    changeBlock(pLevel, pPos, pState, DnDecorBlocks.LARGE_FAN.getDefaultState(), SoundEvents.SPONGE_ABSORB);

            case FlapDisplayBlock flapDisplayBlock ->
                    changeBlock(pLevel, pPos, pState, AllBlocks.DISPLAY_BOARD.defaultBlockState(), SoundEvents.SPONGE_ABSORB);

            default -> InteractionResult.PASS;
        };
    }
}
