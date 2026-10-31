package dev.lopyluna.dndecor.content.blocks.boiler;

import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.LevelReader;
import com.zurrtum.create.content.equipment.wrench.IWrenchable;
import com.zurrtum.create.foundation.block.LandingEffectControlBlock;
import dev.lopyluna.dndecor.register.DnDecorBlocks;
import dev.lopyluna.dndecor.register.DnDecorShapes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.Nullable;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("NullableProblems")
public class BoilerStructureBlock extends Block implements IWrenchable, LandingEffectControlBlock {
    public static final EnumProperty<BoilerBlock.BoilerSize> SIZE = BoilerBlock.SIZE;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    public BoilerStructureBlock(Properties properties) {
        super(properties.noLootTable().noOcclusion().dynamicShape().pushReaction(PushReaction.DESTROY));
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext ctx) {
        var level = ctx.getLevel();
        if (level.isClientSide()) return InteractionResult.PASS;
        var pos = ctx.getClickedPos();
        if (stillValid(level, pos, state, false)) {
            var masterPos = getMaster(level, pos, state);
            var masterState = level.getBlockState(masterPos);
            if (masterState.getBlock() instanceof IWrenchable block) {
                assert ctx.getPlayer() != null;
                return block.onSneakWrenched(masterState, new UseOnContext(ctx.getPlayer(), ctx.getHand(), new BlockHitResult(ctx.getClickLocation(), ctx.getClickedFace(), masterPos, ctx.isInside())));
            }
        }
        level.removeBlock(pos, false);
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext ctx) {
        var level = ctx.getLevel();
        if (level.isClientSide()) return InteractionResult.PASS;
        var pos = ctx.getClickedPos();
        if (stillValid(level, pos, state, false)) {
            var masterPos = getMaster(level, pos, state);
            var masterState = level.getBlockState(masterPos);
            if (masterState.getBlock() instanceof IWrenchable block) {
                assert ctx.getPlayer() != null;
                return block.onWrenched(masterState, new UseOnContext(ctx.getPlayer(), ctx.getHand(), new BlockHitResult(ctx.getClickLocation(), ctx.getClickedFace(), masterPos, ctx.isInside())));
            }
        }
        level.removeBlock(pos, false);
        return InteractionResult.PASS;
    }


    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        var size = state.getValue(SIZE);
        if (size == BoilerBlock.BoilerSize.LARGE) return Shapes.block();
        return switch (size) {
            case SMALL -> Shapes.empty();
            case NORMAL -> DnDecorShapes.SIDE_8PX.get(state.getValue(FACING).getOpposite());
            case MEDIUM -> DnDecorShapes.SIDE_12PX.get(state.getValue(FACING).getOpposite());
            default -> Shapes.block();
        };
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state) {
        return Shapes.empty();
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING, SIZE));
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        var masterPos = getMaster(level, pos, state);
        var masterState = level.getBlockState(masterPos);
        if (masterState.getBlock() instanceof BoilerBlock block) return new ItemStack(block);
        return ItemStack.EMPTY;
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (state.canSurvive(level, pos)) return super.updateShape(state, level, tickAccess, pos, direction, neighborPos, neighborState, random);
        if (level instanceof Level world && !world.isClientSide() && !world.getBlockTicks().hasScheduledTick(pos, this)) world.scheduleTick(pos, this, 1);
        return state;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(SIZE) == BoilerBlock.BoilerSize.SMALL) return false;
        var dir = state.getValue(FACING);
        var target = level.getBlockState(pos.relative(dir));
        if (target.getBlock() instanceof BoilerBlock) return target.getValue(BoilerBlock.SIZE) == state.getValue(SIZE);
        return false;
    }


    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1f;
    }

    @Override
    protected int getLightDampening(BlockState state) {
        return 0;
    }
    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        return state.getValue(SIZE) != BoilerBlock.BoilerSize.LARGE;
    }
    @Override
    protected boolean canBeReplaced(BlockState state, Fluid fluid) {
        return state.getValue(SIZE) != BoilerBlock.BoilerSize.LARGE;
    }
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (stillValid(level, pos, state, false)) {
            var masterPos = getMaster(level, pos, state);
            level.destroyBlockProgress(masterPos.hashCode(), masterPos, -1);
            if (!level.isClientSide() && player.isCreative()) level.destroyBlock(masterPos, false, player);
        }
        return super.playerWillDestroy(level, pos, state, player);
    }
    @Override
    public void popExperience(ServerLevel level, BlockPos pos, int amount) {
    }
    @Override
    protected boolean skipRendering(BlockState state, BlockState adjacentState, Direction side) {
        return adjacentState.getBlock() instanceof BoilerStructureBlock || super.skipRendering(state, adjacentState, side);
    }
    // PORT-NOTE(26.1): NeoForge's shouldDisplayFluidOverlay, the entity-aware getSoundType (the master boiler's sound)
    // and the hit/destroy particle hooks have no Fabric equivalent; dropped as in Create Fly's water wheel structure.
    // The break overlay across the boiler is DnDecorClient's MultiPosDestructionHandler.

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        if (movedByPiston) return;
        var newState = level.getBlockState(pos);
        if (state.getBlock() == newState.getBlock()) return;
        if (!newState.isAir()) return;
        if (!stillValid(level, pos, state, false)) return;
        level.destroyBlock(getMaster(level, pos, state), true);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return true;
    }

    public BlockPos getMaster(BlockGetter pLevel, BlockPos pPos, BlockState pState) {
        var pos = pPos.relative(pState.getValue(FACING));
        var state = pLevel.getBlockState(pos);
        if (state.getBlock() instanceof BoilerStructureBlock block) return block.getMaster(pLevel, pos, state);
        return pos;
    }

    public boolean stillValid(BlockGetter level, BlockPos pos, BlockState state, boolean directlyAdjacent) {
        if (!state.is(this)) return false;
        var dir = state.getValue(FACING);
        var tPos = pos.relative(dir);
        var tState = level.getBlockState(tPos);
        if (!directlyAdjacent && stillValid(level, tPos, tState, true)) return true;
        return tState.getBlock() instanceof BoilerBlock
                && tState.getValue(BoilerBlock.SIZE) == state.getValue(SIZE)
                && tState.getValue(BoilerBlock.AXIS) != dir.getAxis();
    }

    public Set<BlockPos> getAttachedPositions(BlockGetter level, BlockPos pos, BlockState state) {
        HashSet<BlockPos> set = new HashSet<>();
        if (!stillValid(level, pos, state, false)) return set;
        var masterPos = getMaster(level, pos, state);
        var masterState = level.getBlockState(masterPos);
        if (!(masterState.getBlock() instanceof BoilerBlock)) return set;
        var axis = masterState.getValue(BoilerBlock.AXIS);
        for (var dir : Direction.values()) {
            if (dir.getAxis() == axis) continue;
            var structurePos = masterPos.relative(dir);
            if (level.getBlockState(structurePos).getBlock() instanceof BoilerStructureBlock) set.add(structurePos);
        }
        set.add(masterPos);
        return set;
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (!stillValid(pLevel, pPos, pState, false)) pLevel.setBlockAndUpdate(pPos, Blocks.AIR.defaultBlockState());
    }

    @Override
    public boolean addLandingEffects(BlockState state, ServerLevel level, BlockPos pos, LivingEntity entity, double distance) {
        return true;
    }

}
