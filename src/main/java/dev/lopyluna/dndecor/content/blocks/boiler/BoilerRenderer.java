package dev.lopyluna.dndecor.content.blocks.boiler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

// PORT-NOTE(26.1): extract/submit renderer. The boiler is bigger than its block, so it is drawn here with light
// averaged over the surrounding blocks.
@SuppressWarnings("unused")
public class BoilerRenderer implements BlockEntityRenderer<BoilerBlockEntity, BoilerRenderer.State> {
    public BoilerRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(BoilerBlockEntity be, State renderState, float pt, Vec3 cameraPos, @Nullable CrumblingOverlay crumblingOverlay) {
        Level level = SmartBlockEntityRenderer.extractBase(be, renderState, crumblingOverlay);
        renderState.block = null;
        if (level == null) return;
        var state = renderState.blockState;
        if (!(state.getBlock() instanceof BoilerBlock)) return;
        var pos = renderState.blockPos;
        var axis = state.getValue(BoilerBlock.AXIS);

        int totalBlock = 0;
        int bestSky = 0;
        int samples = 0;

        for (int a = -1; a <= 1; a++) for (int b = -1; b <= 1; b++) {
            var samplePos = axis == Direction.Axis.Y ? pos.offset(a, 0, b) : axis == Direction.Axis.Z ? pos.offset(a, b, 0) : pos.offset(0, a, b);
            var sampleState = level.getBlockState(samplePos);
            if (sampleState.isSolidRender()) continue;

            totalBlock += level.getBrightness(LightLayer.BLOCK, samplePos);
            bestSky = Math.max(bestSky, level.getBrightness(LightLayer.SKY, samplePos));
            samples++;
        }

        int avgLight = samples == 0 ? renderState.lightCoords : LightCoordsUtil.pack(totalBlock / samples, bestSky);
        renderState.block = CachedBuffers.block(state).cardinalLighting(level).light(avgLight).extractRenderState();
    }

    @Override
    public void submit(State state, PoseStack ps, SubmitNodeCollector queue, CameraRenderState cameraState) {
        if (state.block != null) state.block.submit(ps, queue);
    }

    public static class State extends BlockEntityRenderState {
        public @Nullable SuperByteBufferRenderState block;
    }
}
