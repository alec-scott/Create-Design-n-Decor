package dev.lopyluna.dndecor.content.blocks.container;

import net.minecraft.core.component.DataComponents;
import com.zurrtum.create.AllItems;
import com.zurrtum.create.AllSoundEvents;
import com.zurrtum.create.api.connectivity.ConnectivityHandler;
import com.zurrtum.create.content.equipment.wrench.IWrenchable;
import com.zurrtum.create.foundation.block.IBE;
import com.zurrtum.create.foundation.item.ItemHelper;
import dev.lopyluna.dndecor.register.DnDecorBETypes;
import dev.lopyluna.dndecor.register.DnDecorBlocks;
import com.zurrtum.create.infrastructure.items.ItemInventoryProvider;
import net.minecraft.world.Container;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;


@SuppressWarnings({"NullableProblems", "unused"})
public class DyedContainerBlock extends Block implements IWrenchable, IBE<DyedContainerBE>, ItemInventoryProvider<DyedContainerBE> {

    // PORT-NOTE(26.1): Create Fly exposes block inventories through ItemInventoryProvider (was a NeoForge capability)
    @Override
    public @Nullable Container getInventory(LevelAccessor world, BlockPos pos, BlockState state, DyedContainerBE blockEntity, @Nullable Direction context) {
        return blockEntity.getInventory();
    }
    public static final Property<Direction.Axis> HORIZONTAL_AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final BooleanProperty LARGE = BooleanProperty.create("large");

    public final boolean solidColor;
    public final DyeColor color;
    public DyedContainerBlock(Properties properties, boolean solidColor, DyeColor color) {
        super(properties);
        this.solidColor = solidColor;
        this.color = color;
        registerDefaultState(defaultBlockState().setValue(LARGE, false));
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(HORIZONTAL_AXIS, LARGE));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        var player = ctx.getPlayer();
        if (player == null || !player.isShiftKeyDown()) {
            var axis = getBlockAxis(ctx.getLevel().getBlockState(ctx.getClickedPos().relative(ctx.getClickedFace().getOpposite())));
            if (axis != null) return defaultBlockState().setValue(HORIZONTAL_AXIS, axis);
        }
        return defaultBlockState().setValue(HORIZONTAL_AXIS, ctx.getHorizontalDirection().getAxis());
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        if (pOldState.getBlock() == pState.getBlock()) return;
        if (pIsMoving) return;
        if (isContainerReplacement(pOldState, pState)) return;
        withBlockEntityDo(pLevel, pPos, DyedContainerBE::updateConnectivity);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        var i = stack.getItem();
        if (i instanceof DyeItem dyeItem) {
            var flag = new AtomicBoolean(false);
            var color = stack.getOrDefault(DataComponents.DYE, DyeColor.WHITE);
            withBlockEntityDo(level, pos, be -> flag.set(be.setColor(color, false)));
            if (flag.get()) {
                level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0f, 1.1f - level.getRandom().nextFloat() * .2f);
                return InteractionResult.SUCCESS;
            }
        } else if (stack.is(AllItems.SAND_PAPER)) {
            var flag = new AtomicBoolean(false);
            withBlockEntityDo(level, pos, be -> flag.set(be.setColor(null, true)));
            if (flag.get()) {
                level.playSound(null, pos, AllSoundEvents.SANDING_SHORT.getMainEvent(), SoundSource.BLOCKS, 0.8f, 1.1f - level.getRandom().nextFloat() * .2f);
                level.playSound(null, pos, SoundEvents.NETHERITE_BLOCK_BREAK, SoundSource.BLOCKS, 0.1f, 0.7f - level.getRandom().nextFloat() * .2f);
                return InteractionResult.SUCCESS;
            }
        } else if (stack.is(Items.WATER_BUCKET) || stack.is(Items.WET_SPONGE)) {
            var flag = new AtomicBoolean(false);
            withBlockEntityDo(level, pos, be -> flag.set(be.setColor(null, true)));
            if (flag.get()) {
                level.playSound(null, pos, SoundEvents.AXOLOTL_SPLASH, SoundSource.BLOCKS, 0.8f, 1.5f - level.getRandom().nextFloat() * .25f);
                return InteractionResult.SUCCESS;
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (context.getClickedFace().getAxis().isVertical()) {
            if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof DyedContainerBE be) {
                ConnectivityHandler.splitMulti(be);
                be.removeController(true);
            }
            state = state.setValue(LARGE, false);
        }
        return IWrenchable.super.onWrenched(state, context);
    }

    // PORT-NOTE(26.1): onRemove is gone. Drops and splitting are ItemVaultBlockEntity.preRemoveSideEffects; recolouring
    // keeps the block entity because every container block shares its type.

    private static boolean isContainerReplacement(BlockState oldState, BlockState newState) {
        return oldState.hasBlockEntity() && newState.hasBlockEntity()
                && oldState.getBlock() instanceof DyedContainerBlock
                && newState.getBlock() instanceof DyedContainerBlock;
    }

    public static DyeColor getColor(BlockState state) {
        return state.getBlock() instanceof DyedContainerBlock block ? block.color : null;
    }
    public static boolean isSolidColor(BlockState state) {
        return state.getBlock() instanceof DyedContainerBlock block && block.solidColor;
    }

    public static boolean isContainer(BlockState state) {
        return state.getBlock() instanceof DyedContainerBlock block &&
                (block.color == null ? DnDecorBlocks.CONTAINER : block.solidColor ? DnDecorBlocks.DYED_SOLID_CONTAINERS.get(block.color) : DnDecorBlocks.DYED_CONTAINERS.get(block.color)).has(state);
    }

    @Nullable
    public static Direction.Axis getBlockAxis(BlockState state) {
        if (!isContainer(state)) return null;
        return state.getValue(HORIZONTAL_AXIS);
    }

    public static Direction.Axis getBlockAxisNon(BlockState state) {
        return state.getValue(HORIZONTAL_AXIS);
    }

    public static boolean isLarge(BlockState state) {
        if (!isContainer(state)) return false;
        return state.getValue(LARGE);
    }

    public static boolean isLargeNon(BlockState state) {
        return state.getValue(LARGE);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(HORIZONTAL_AXIS, rot.rotate(Direction.fromAxisAndDirection(state.getValue(HORIZONTAL_AXIS), Direction.AxisDirection.POSITIVE)).getAxis());
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state;
    }

    // PORT-NOTE(26.1): the multi-place sound silencing used NeoForge's entity-aware getSoundType and persistent data;
    // dropped as in Create Fly's item vault

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos, Direction direction) {
        return ItemHelper.calcRedstoneFromBlockEntity(this, pLevel, pPos);
    }

    @Override
    public BlockEntityType<DyedContainerBE> getBlockEntityType() {
        return DnDecorBETypes.DYED_CONTAINER.get();
    }

    @Override
    public Class<DyedContainerBE> getBlockEntityClass() {
        return DyedContainerBE.class;
    }
}
