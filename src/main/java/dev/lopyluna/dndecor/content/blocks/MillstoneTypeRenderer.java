package dev.lopyluna.dndecor.content.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;
import com.zurrtum.create.client.content.kinetics.base.SingleKineticRenderState;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.zurrtum.create.content.kinetics.millstone.MillstoneBlockEntity;
import dev.lopyluna.dndecor.DnDecor;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

// PORT-NOTE(26.1): extract/submit renderer, as Create Fly's MillstoneRenderer with the stone type's inner model
public class MillstoneTypeRenderer implements BlockEntityRenderer<MillstoneBlockEntity, SingleKineticRenderState> {
    public MillstoneTypeRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public SingleKineticRenderState createRenderState() {
        return new SingleKineticRenderState();
    }

    @Override
    public void extractRenderState(MillstoneBlockEntity be, SingleKineticRenderState state, float partialTicks, Vec3 cameraPosition, @Nullable CrumblingOverlay breakProgress) {
        Level level = SmartBlockEntityRenderer.extractBase(be, state, breakProgress);
        state.model = getRotatedModel(state.blockState).cardinalLighting(level).light(state.lightCoords)
                .color(KineticBlockEntityRenderer.getTintColor(be)).extractRenderState();
        state.angle = KineticBlockEntityRenderer.getRotateAngleWithoutBeOffset(be, state, level);
    }

    @Override
    public void submit(SingleKineticRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState camera) {
        state.submit(matrices, queue);
    }

    protected SuperByteBuffer getRotatedModel(BlockState state) {
        if (state.getBlock() instanceof MillstoneTypeBlock block && block.id != null && !block.id.isEmpty())
            return CachedBuffers.partial(PartialModel.of(DnDecor.loc("block/" + block.id + "_millstone/inner")), state);
        return CachedBuffers.partial(AllPartialModels.MILLSTONE_COG, state);
    }
}
