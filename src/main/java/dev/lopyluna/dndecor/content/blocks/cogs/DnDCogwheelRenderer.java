package dev.lopyluna.dndecor.content.blocks.cogs;

import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.zurrtum.create.content.kinetics.simpleRelays.BracketedKineticBlockEntity;
import com.zurrtum.create.content.kinetics.simpleRelays.ICogWheel;
import dev.lopyluna.dndecor.register.client.DnDecorPartialModels;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.*;
import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityVisual.rotationOffset;
import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityVisual.shouldOffset;

// PORT-NOTE(26.1): extract/submit, following Create Fly's BracketedKineticBlockEntityRenderer with D&D's dyed and
// custom cog models (submit is inherited). Only used without Flywheel visualization, as before.
public class DnDCogwheelRenderer extends BracketedKineticBlockEntityRenderer {

	public DnDCogwheelRenderer(Context context) {
		super(context);
	}

	@Override
	public void extractRenderState(BracketedKineticBlockEntity be, BracketedKineticRenderState state, float tickProgress,
								   Vec3 cameraPos, @Nullable CrumblingOverlay crumblingOverlay) {
		if (!(be.getBlockState().getBlock() instanceof DnDCogWheelBlock block)) {
			super.extractRenderState(be, state, tickProgress, cameraPos, crumblingOverlay);
			return;
		}
		Level level = SmartBlockEntityRenderer.extractBase(be, state, crumblingOverlay);
		CardinalLighting cardinalLighting = SmartBlockEntityRenderer.getCardinalLighting(level);
		Axis axis = getRotationAxisOf(state.blockState);
		Direction facing = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
		boolean large = ICogWheel.isLargeCog(state.blockState);
		PartialModel model = large ? DnDecorPartialModels.DYED_LARGE_COGWHEEL.get(block.color) : DnDecorPartialModels.DYED_COGWHEEL.get(block.color);
		if (block.customModel != null) model = block.customModel;

		int color = getTintColor(be);
		float progress = getProgress(be, level);
		float offset;
		state.shaft = null;
		state.shaftAngle = null;
		if (large) {
			float shaftOffset;
			if (shouldOffset(axis, state.blockPos)) {
				offset = shaftOffset = 22.5f;
			} else {
				offset = 11.25f;
				shaftOffset = 0;
			}
			state.shaftAngle = getRotateAngle(progress, shaftOffset, facing);
			state.shaft = CachedBuffers.partialFacingVertical(AllPartialModels.COGWHEEL_SHAFT, state.blockState, facing)
				.cardinalLighting(cardinalLighting).light(state.lightCoords).color(color).extractRenderState();
		} else {
			offset = rotationOffset(state.blockState, axis, state.blockPos);
		}
		state.angle = getRotateAngle(progress, offset, facing);
		state.model = CachedBuffers.partialFacingVertical(model, state.blockState, facing)
			.cardinalLighting(cardinalLighting).light(state.lightCoords).color(color).extractRenderState();
	}
}
