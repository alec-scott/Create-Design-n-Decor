package dev.lopyluna.dndecor.content.blocks.full_belt;

import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.client.catnip.animation.AnimationTickHolder;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SpriteShiftEntry;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.content.kinetics.belt.BeltRenderer;
import com.zurrtum.create.client.content.kinetics.belt.BeltRenderer.BeltRenderState;
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationManager;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.zurrtum.create.content.kinetics.belt.BeltBlock;
import com.zurrtum.create.content.kinetics.belt.BeltBlockEntity;
import com.zurrtum.create.content.kinetics.belt.BeltPart;
import com.zurrtum.create.content.kinetics.belt.BeltSlope;
import dev.lopyluna.dndecor.register.DnDecorBlocks;
import dev.lopyluna.dndecor.register.client.DnDecorPartialModels;
import dev.lopyluna.dndecor.register.client.DnDecorSpriteShifts;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class FullBeltRenderer extends BeltRenderer {

	public FullBeltRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	// PORT-NOTE(26.1): Create Fly's BeltRenderer does the extract/submit work (rotation, pulley, items); without Flywheel
	// only the belt surfaces are swapped for D&D's models and scrolling textures, using the same scroll maths
	@Override
	public void extractRenderState(BeltBlockEntity be, BeltRenderState state, float tickProgress, Vec3 cameraPos, @Nullable CrumblingOverlay crumblingOverlay) {
		super.extractRenderState(be, state, tickProgress, cameraPos, crumblingOverlay);
		Level level = be.getLevel();
		if (VisualizationManager.supportsVisualization(level) || state.top == null) return;
		BlockState blockState = state.blockState;
		if (!blockState.is(DnDecorBlocks.BELT.get())) return;

		BeltSlope beltSlope = blockState.getValue(BeltBlock.SLOPE);
		BeltPart part = blockState.getValue(BeltBlock.PART);
		Direction facing = blockState.getValue(BeltBlock.HORIZONTAL_FACING);
		AxisDirection axisDirection = facing.getAxisDirection();
		boolean downward = beltSlope == BeltSlope.DOWNWARD;
		boolean upward = beltSlope == BeltSlope.UPWARD;
		boolean diagonal = downward || upward;
		boolean start = part == BeltPart.START;
		boolean end = part == BeltPart.END;
		boolean sideways = beltSlope == BeltSlope.SIDEWAYS;
		boolean alongX = facing.getAxis() == Direction.Axis.X;
		if (downward || beltSlope == BeltSlope.VERTICAL && axisDirection == AxisDirection.POSITIVE) {
			boolean b = start;
			start = end;
			end = b;
		}

		DyeColor color = be.color.orElse(null);
		CardinalLighting cardinalLighting = SmartBlockEntityRenderer.getCardinalLighting(level);
		float speed = be.getSpeed();
		boolean needScroll = speed != 0 || color != null;
		double scroll = 0;
		if (needScroll) {
			float time = AnimationTickHolder.getRenderTime(level) * axisDirection.getStep();
			if (diagonal && (downward ^ alongX) || !sideways && !diagonal && alongX || sideways && axisDirection == AxisDirection.NEGATIVE) speed = -speed;
			scroll = speed * time / (31.5 * 16);
		}
		for (boolean bottom : Iterate.trueAndFalse) {
			if (diagonal && bottom) continue;
			SuperByteBuffer beltBuffer = CachedBuffers.partial(getBeltPartial(diagonal, start, end, bottom), blockState);
			if (needScroll) {
				SpriteShiftEntry spriteShift = getSpriteShiftEntry(color, diagonal, bottom);
				float scrollMulti = diagonal ? 3f / 8f : 0.5f;
				float spriteSize = spriteShift.getTarget().getV1() - spriteShift.getTarget().getV0();
				double s = scroll + (bottom ? 0.5 : 0.0);
				beltBuffer.shiftUVScrolling(spriteShift, (float) ((s - Math.floor(s)) * spriteSize * scrollMulti));
			}
			var extracted = beltBuffer.cardinalLighting(cardinalLighting).light(state.lightCoords).extractRenderState();
			if (bottom) state.bottom = extracted;
			else state.top = extracted;
		}
	}

	public static SpriteShiftEntry getSpriteShiftEntry(DyeColor color, boolean diagonal, boolean bottom) {
		if (color != null) return (diagonal ? DnDecorSpriteShifts.DYED_DIAGONAL_BELTS : bottom ? DnDecorSpriteShifts.DYED_OFFSET_BELTS : DnDecorSpriteShifts.DYED_BELTS).get(color);
		else return diagonal ? DnDecorSpriteShifts.BELT_DIAGONAL : bottom ? DnDecorSpriteShifts.BELT_OFFSET : DnDecorSpriteShifts.BELT;
	}

	public static PartialModel getBeltPartial(boolean diagonal, boolean start, boolean end, boolean bottom) {
		if (diagonal) {
			if (start) return DnDecorPartialModels.BELT_DIAGONAL_START;
			if (end) return DnDecorPartialModels.BELT_DIAGONAL_END;
			return DnDecorPartialModels.BELT_DIAGONAL_MIDDLE;
		} else if (bottom) {
			if (start) return DnDecorPartialModels.BELT_START_BOTTOM;
			if (end) return DnDecorPartialModels.BELT_END_BOTTOM;
			return DnDecorPartialModels.BELT_MIDDLE_BOTTOM;
		} else {
			if (start) return DnDecorPartialModels.BELT_START;
			if (end) return DnDecorPartialModels.BELT_END;
			return DnDecorPartialModels.BELT_MIDDLE;
		}
	}
}
