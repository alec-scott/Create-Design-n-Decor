package dev.lopyluna.dndecor.content.blocks.stepped_lever;

import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.content.redstone.analogLever.AnalogLeverBlock;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import dev.lopyluna.dndecor.register.client.DnDecorPartialModels;
import com.zurrtum.create.catnip.math.AngleHelper;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;

@SuppressWarnings("unused")
public class SteppedLeverRenderer extends SafeBlockEntityRenderer<SteppedLeverBE> {
    public SteppedLeverRenderer(BlockEntityRendererProvider.Context context) {
    }

    protected void renderSafe(SteppedLeverBE be, float pt, PoseStack ms, MultiBufferSource bs, int light, int overlay) {
        var leverState = be.getBlockState();
        var state = be.clientState.getValue(pt);
        var vb = bs.getBuffer(RenderTypes.cutoutMipped());
        var handle = CachedBuffers.partial(DnDecorPartialModels.STEPPED_LEVER_HANDLE, leverState);
        var angle = (((state / 15f * 135f) - 22.5f) / 180f) * (float) Math.PI;

        transform(handle, leverState).translate(8/16f, 4/16f, 8/16f).rotate(Direction.EAST.getAxis(), angle).translate(-8/16f, -4/16f, -8/16f);
        handle.light(light).renderInto(ms, vb);
    }

    private SuperByteBuffer transform(SuperByteBuffer buffer, BlockState leverState) {
        var face = leverState.getValue(AnalogLeverBlock.FACE);
        var rX = face == AttachFace.FLOOR ? 0f : (face == AttachFace.WALL ? 90f : 180f);
        var rY = AngleHelper.horizontalAngle(leverState.getValue(AnalogLeverBlock.FACING));
        buffer.rotateCentered((rY / 180f) * (float) Math.PI, Direction.UP);
        buffer.rotateCentered((rX / 180f) * (float) Math.PI, Direction.EAST);
        return buffer;
    }
}