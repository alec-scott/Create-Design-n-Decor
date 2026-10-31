package dev.lopyluna.dndecor.content.blocks.text_plate;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zurrtum.create.catnip.math.AngleHelper;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.flywheel.lib.transform.TransformStack;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.lopyluna.dndecor.register.client.DnDecorPartialModels;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Style;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.Nullable;

import java.awt.Color;

// PORT-NOTE(26.1): extract/submit renderer (was a SafeBlockEntityRenderer). Colours and the plate model are worked out
// in extractRenderState; the text is one outlined submitText call (was drawInBatch plus drawInBatch8xOutline).
@SuppressWarnings("unused")
public class TextPlateRenderer implements BlockEntityRenderer<TextPlateBE, TextPlateRenderer.State> {
    private final Minecraft mc = Minecraft.getInstance();

    public TextPlateRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(TextPlateBE be, State state, float pt, Vec3 cameraPos, @Nullable CrumblingOverlay crumblingOverlay) {
        Level level = SmartBlockEntityRenderer.extractBase(be, state, crumblingOverlay);
        int l = state.lightCoords;
        state.face = be.face;
        state.facing = be.facing;

        state.model = CachedBuffers.partial(DnDecorPartialModels.getTextPlateModel(be.metalBase, be.colorBase, be.extended), state.blockState)
                .rotateCenteredDegrees(90, Axis.XP).translate(0, -0.01/16f, 0).cardinalLighting(level).light(l).extractRenderState();

        var text = be.text;
        state.text = FormattedCharSequence.forward(text, Style.EMPTY);
        state.charWidth = mc.font.width(text);

        var col = be.dyedText.getTextColor();
        var hex = col;
        var hexBg = col;
        CardinalLighting lighting = level == null ? null : SmartBlockEntityRenderer.getCardinalLighting(level);
        var shade = lighting == null ? 1f : be.face == AttachFace.WALL ? lighting.byFace(be.facing) : be.face == AttachFace.CEILING ? lighting.byFace(Direction.DOWN) : lighting.byFace(Direction.UP);
        if (!be.glowing) hex = ARGB.color(ARGB.alpha(hex),
                (int)(ARGB.red(hex) * shade), (int)(ARGB.green(hex) * shade), (int)(ARGB.blue(hex) * shade));

        double m = be.glowing ? 0.4 : 0.2;
        int r = (int)((double)ARGB.red(hexBg) * m);
        int g = (int)((double)ARGB.green(hexBg) * m);
        int b = (int)((double)ARGB.blue(hexBg) * m);
        int t = (int) (36 * (be.glowing ? 1 : m/0.4));
        if (r < t && g < t && b < t) {
            float[] hsv = Color.RGBtoHSB(r, g, b, null);
            hsv[0] = (hsv[0] + 0.5f) % 1.0f;
            int rgb = Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]);
            r = (rgb >> 16) & 0xFF;
            g = (rgb >> 8) & 0xFF;
            b = rgb & 0xFF;

            r = 255 - r*2;
            g = 255 - g*2;
            b = 255 - b*2;
        }
        hexBg = ARGB.color(0, r, g, b);
        if (!be.glowing) hexBg = ARGB.color(ARGB.alpha(hexBg),
                (int)(ARGB.red(hexBg) * shade), (int)(ARGB.green(hexBg) * shade), (int)(ARGB.blue(hexBg) * shade));

        state.textColor = hex;
        state.outlineColor = hexBg;
        state.textLight = be.glowing ? LightCoordsUtil.FULL_BRIGHT : l;
    }

    @Override
    public void submit(State state, PoseStack ms, SubmitNodeCollector queue, CameraRenderState cameraState) {
        ms.pushPose();
        var msr = TransformStack.of(ms);
        var face = state.face;
        var rX = face == AttachFace.FLOOR ? 270f : face == AttachFace.WALL ? 0f : 90f;
        var rY = AngleHelper.horizontalAngle(state.facing);
        msr.center().rotateYDegrees(rY).rotateXDegrees(rX).rotateZDegrees(face == AttachFace.WALL ? 0 : 180).uncenter();

        state.model.submit(ms, queue);

        msr.center();
        float scale = 1 / 16f;
        ms.translate(0.5f/16f, 0, -6/16f);
        ms.scale(scale, -scale, scale);
        ms.translate(state.charWidth / -2f, -4, 0);
        queue.submitText(ms, 0, 0, state.text, false, Font.DisplayMode.NORMAL, state.textLight, state.textColor, 0, state.outlineColor);
        ms.popPose();
    }

    public static class State extends BlockEntityRenderState {
        public @UnknownNullability SuperByteBufferRenderState model;
        public @UnknownNullability FormattedCharSequence text;
        public AttachFace face = AttachFace.FLOOR;
        public Direction facing = Direction.NORTH;
        public float charWidth;
        public int textColor;
        public int outlineColor;
        public int textLight;
    }
}
