package dev.lopyluna.dndecor.content.blocks.text_plate;

import com.zurrtum.create.AllItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.infrastructure.component.ClipboardEntry;
import com.zurrtum.create.foundation.block.IBE;
import dev.lopyluna.dndecor.register.DnDecorBETypes;
import dev.lopyluna.dndecor.register.DnDecorShapes;
import dev.lopyluna.dndecor.register.helpers.list_providers.MaterialTypeProvider;
import com.zurrtum.create.catnip.math.VoxelShaper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.Nullable;

@SuppressWarnings("NullableProblems")
public class TextPlateBlock extends Block implements IBE<TextPlateBE>, SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty EXTENDED = BlockStateProperties.EXTENDED;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;

    public static final VoxelShaper SHAPE_FLOOR = DnDecorShapes.shape(2.5, 0, 2, 13.5, 2, 14).forHorizontal(Direction.NORTH);
    public static final VoxelShaper SHAPE_CEIL = DnDecorShapes.shape(2.5, 14, 2, 13.5, 16, 14).forHorizontal(Direction.NORTH);
    public static final VoxelShaper SHAPE_WALL = DnDecorShapes.shape(2.5, 2, 14, 13.5, 14, 16).forHorizontal(Direction.NORTH);

    public static final VoxelShaper SHAPE_DOUBLE_FLOOR = DnDecorShapes.shape(0, 0, 2, 16, 2, 14).forHorizontal(Direction.NORTH);
    public static final VoxelShaper SHAPE_DOUBLE_CEIL = DnDecorShapes.shape(0, 14, 2, 16, 16, 14).forHorizontal(Direction.NORTH);
    public static final VoxelShaper SHAPE_DOUBLE_WALL = DnDecorShapes.shape(0, 2, 14, 16, 14, 16).forHorizontal(Direction.NORTH);

    public TextPlateBlock(Properties properties) {
        super(properties.noTerrainParticles());
        registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false).setValue(EXTENDED, false).setValue(FACING, Direction.NORTH).setValue(FACE, AttachFace.FLOOR));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        var extended = state.getValue(EXTENDED);
        var facing = state.getValue(FACING);
        var face = state.getValue(FACE);
        return switch (face) {
            case FLOOR -> (extended ? SHAPE_DOUBLE_FLOOR : SHAPE_FLOOR).get(facing);
            case WALL -> (extended ? SHAPE_DOUBLE_WALL : SHAPE_WALL).get(facing);
            case CEILING -> (extended ? SHAPE_DOUBLE_CEIL : SHAPE_CEIL).get(facing);
        };
    }

    public InteractionResult applyDye(Level level, BlockPos pos, DyeColor color) {
        if (!(level.getBlockEntity(pos) instanceof TextPlateBE be)) return InteractionResult.PASS;
        if (be.colorBase == color && be.metalBase == null) return InteractionResult.PASS;
        be.colorBase = color;
        be.metalBase = null;
        level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0f, 1.1f - level.getRandom().nextFloat() * .2f);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.isEmpty()) return InteractionResult.TRY_WITH_EMPTY_HAND;
        var item = stack.getItem();
        if (item instanceof DyeItem dyeItem) {
            var flag = true;
            var color = stack.getOrDefault(DataComponents.DYE, DyeColor.WHITE);
            if (level.getBlockEntity(pos) instanceof TextPlateBE be) {
                if (be.dyedText == color) {
                    if (be.colorBase == color && be.metalBase == null) flag = false;
                    else be.colorBase = color;
                    be.metalBase = null;
                } else be.dyedText = color;
            }
            if (flag) {
                level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0f, 1.1f - level.getRandom().nextFloat() * .2f);
                return InteractionResult.SUCCESS;
            }
        } else if (stack.is(Items.GLOW_INK_SAC)) {
            var flag = true;
            if (level.getBlockEntity(pos) instanceof TextPlateBE be) {
                if (be.glowing) flag = false;
                else be.glowing = true;
            }
            if (flag) {
                level.playSound(null, pos, SoundEvents.GLOW_INK_SAC_USE, SoundSource.BLOCKS, 1.0f, 1.1f - level.getRandom().nextFloat() * .2f);
                return InteractionResult.SUCCESS;
            }
        } else for (var type : MaterialTypeProvider.metalTypes) {
            var metal = type.get();
            var ing = metal.getIngredient();

            if ((ing != null && ing.test(stack)) || (metal.tag != null && stack.is(metal.tag))) {
                var flag = true;
                if (level.getBlockEntity(pos) instanceof TextPlateBE be) {
                    if (be.metalBase != null && be.metalBase.id.equals(metal.id)) flag = false;
                    else be.metalBase = metal;
                    be.colorBase = DyeColor.WHITE;
                }
                if (flag) {
                    level.playSound(null, pos, metal.sound.getHitSound(), SoundSource.BLOCKS, 1.0f, 1.1f - level.getRandom().nextFloat() * .2f);
                    level.playSound(null, pos, metal.sound.getPlaceSound(), SoundSource.BLOCKS, 0.5f, 1.4f - level.getRandom().nextFloat() * .2f);
                    level.playSound(null, pos, metal.sound.getBreakSound(), SoundSource.BLOCKS, 0.5f, 1.1f - level.getRandom().nextFloat() * .2f);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        boolean isClipboard = stack.is(AllItems.CLIPBOARD);
        var component = stack.getHoverName();
        if (isClipboard) {
            var entries = ClipboardEntry.getLastViewedEntries(stack);
            if (!entries.isEmpty()) component = entries.getFirst().text;
        }

        var text = component.getString();
        if (!(level.getBlockEntity(pos) instanceof TextPlateBE be)) return InteractionResult.TRY_WITH_EMPTY_HAND;
        var leftDir = be.face == AttachFace.WALL ? be.facing.getClockWise() : be.facing.getCounterClockWise();
        var rightDir = leftDir.getOpposite();

        var leftMost = pos;
        while (level.getBlockEntity(leftMost.relative(leftDir)) instanceof TextPlateBE leftBE && level.isLoaded(leftBE.getBlockPos()) && leftBE.face.equals(be.face) && leftBE.facing.equals(be.facing)) leftMost = leftBE.getBlockPos();

        var curPos = leftMost;
        int idx = 0;
        var suc = 0;
        var len = text.length();
        while (true) {
            if (!level.isLoaded(curPos)) break;
            if (idx >= len) break;
            if (!(level.getBlockEntity(curPos) instanceof TextPlateBE relBE)) break;
            if (!(relBE.face.equals(be.face) && relBE.facing.equals(be.facing))) break;
            String out;
            char c = text.charAt(idx);
            if (relBE.extended) {
                if (idx+1 >= len) {
                    String newText = (relBE.text.length() > 1) ? "" + c + relBE.text.charAt(1) : String.valueOf(c);
                    if (!relBE.text.equals(newText)) {
                        relBE.text = newText;
                        suc++;
                    }
                    break;
                }
                out = "" + c + text.charAt(idx + 1);
                idx++;
            } else out = String.valueOf(c);

            if (!relBE.text.equals(out)) {
                relBE.text = out;
                suc++;
                level.playLocalSound(pos, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundSource.BLOCKS, 0.6F, 0.85F, false);
            }
            idx++;
            curPos = curPos.relative(rightDir);
        }
        return suc > 0 ? InteractionResult.SUCCESS : InteractionResult.FAIL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        var level = ctx.getLevel();
        var pos = ctx.getClickedPos();
        var fluidstate = level.getFluidState(pos);
        var flag = fluidstate.getType() == Fluids.WATER;
        var state = defaultBlockState().setValue(WATERLOGGED, flag);

        var dir = ctx.getClickedFace();
        var face = dir == Direction.UP ? AttachFace.FLOOR : dir == Direction.DOWN ? AttachFace.CEILING : AttachFace.WALL;
        if (face == AttachFace.WALL) state = state.setValue(FACING, dir);
        else state = state.setValue(FACING, ctx.getHorizontalDirection());
        return state.setValue(FACE, face).setValue(EXTENDED, ctx.getPlayer() != null && ctx.getPlayer().isShiftKeyDown());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack block) {
        level.playLocalSound(pos, SoundEvents.ITEM_FRAME_PLACE, SoundSource.BLOCKS, 0.6F, 0.7F, false);
        super.setPlacedBy(level, pos, state, placer, block);
        if (placer == null) return;
        var stack = placer.getItemInHand(placer.getUsedItemHand() == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        var item = stack.getItem();
        if (item instanceof DyeItem dyeItem && level.getBlockEntity(pos) instanceof TextPlateBE be) {
            var color = stack.getOrDefault(DataComponents.DYE, DyeColor.WHITE);
            if (be.colorBase == color) return;
            be.colorBase = color;
        } else for (var type : MaterialTypeProvider.metalTypes) {
            var metal = type.get();
            var ing = metal.getIngredient();
            if (((ing != null && ing.test(stack)) || (metal.tag != null && stack.is(metal.tag))) && level.getBlockEntity(pos) instanceof TextPlateBE be) {
                if (be.metalBase != null && be.metalBase.id.equals(metal.id)) return;
                be.metalBase = metal;
            }
        }
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        updateBE(level, pos, state);
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(state, level, pos, neighbor);
        updateBE(level, pos, state);
    }

    @Override
    public BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction facing, BlockPos facingPos, BlockState facingState, RandomSource random) {
        if (state.getValue(WATERLOGGED)) tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        updateBE(level, pos, state);
        return super.updateShape(state, level, tickAccess, pos, facing, facingPos, facingState, random);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(WATERLOGGED, EXTENDED, FACING, FACE));
    }

    public void updateBE(LevelReader level, BlockPos pos, @Nullable BlockState state) {
        if (level.getBlockEntity(pos) instanceof TextPlateBE be) be.update(state);
    }

    // PORT-NOTE(26.1): vanilla's getSoundType has no position, so the plate's metal sound only applies to breaking
    // (spawnDestroyParticles); other sounds use the block's default
    public static SoundType getPlateSound(LevelReader level, BlockPos pos) {
        var result = SoundType.NETHERITE_BLOCK;
        if (level.getBlockEntity(pos) instanceof TextPlateBE be && be.metalBase != null) result = be.metalBase.sound;
        return result;
    }

    @Override
    protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {
        if (!state.isAir()) {
            var sound = getPlateSound(level, pos);
            level.playLocalSound(pos, sound.getBreakSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F, false);
        }
        level.playLocalSound(pos, SoundEvents.ITEM_FRAME_BREAK, SoundSource.BLOCKS, 0.6F, 0.7F, false);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public Class<TextPlateBE> getBlockEntityClass() {
        return TextPlateBE.class;
    }

    @Override
    public BlockEntityType<? extends TextPlateBE> getBlockEntityType() {
        return DnDecorBETypes.TEXT_PLATE.get();
    }
}
