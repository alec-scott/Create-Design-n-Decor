package dev.lopyluna.dndecor.content.blocks.stepped_lever;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.catnip.math.AngleHelper;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.zurrtum.create.content.redstone.analogLever.AnalogLeverBlock;
import dev.lopyluna.dndecor.register.client.DnDecorPartialModels;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.Nullable;

// PORT-NOTE(26.1): extract/submit renderer (was a SafeBlockEntityRenderer); the handle's transforms are captured by
// SuperByteBuffer.extractRenderState, as in Create Fly's AnalogLeverRenderer
@SuppressWarnings("unused")
public class SteppedLeverRenderer implements BlockEntityRenderer<SteppedLeverBE, SteppedLeverRenderer.State> {
    public SteppedLeverRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(SteppedLeverBE be, State renderState, float pt, Vec3 cameraPos, @Nullable CrumblingOverlay crumblingOverlay) {
        Level level = SmartBlockEntityRenderer.extractBase(be, renderState, crumblingOverlay);
        var leverState = renderState.blockState;
        var state = be.clientState.getValue(pt);
        var handle = CachedBuffers.partial(DnDecorPartialModels.STEPPED_LEVER_HANDLE, leverState);
        var angle = (((state / 15f * 135f) - 22.5f) / 180f) * (float) Math.PI;
        transform(handle, leverState).translate(8/16f, 4/16f, 8/16f).rotate(angle, Direction.EAST.getAxis()).translate(-8/16f, -4/16f, -8/16f);
        renderState.handle = handle.cardinalLighting(level).light(renderState.lightCoords).extractRenderState();
    }

    @Override
    public void submit(State state, PoseStack ms, SubmitNodeCollector queue, CameraRenderState cameraState) {
        state.handle.submit(ms, queue);
    }

    private SuperByteBuffer transform(SuperByteBuffer buffer, BlockState leverState) {
        var face = leverState.getValue(AnalogLeverBlock.FACE);
        var rX = face == AttachFace.FLOOR ? 0f : (face == AttachFace.WALL ? 90f : 180f);
        var rY = AngleHelper.horizontalAngle(leverState.getValue(AnalogLeverBlock.FACING));
        buffer.rotateCentered((rY / 180f) * (float) Math.PI, Direction.UP);
        buffer.rotateCentered((rX / 180f) * (float) Math.PI, Direction.EAST);
        return buffer;
    }

    public static class State extends BlockEntityRenderState {
        public @UnknownNullability SuperByteBufferRenderState handle;
    }
}
