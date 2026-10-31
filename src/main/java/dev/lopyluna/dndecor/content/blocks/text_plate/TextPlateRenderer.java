package dev.lopyluna.dndecor.content.blocks.text_plate;

import net.minecraft.client.renderer.rendertype.RenderTypes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.zurrtum.create.client.flywheel.lib.transform.PoseTransformStack;
import com.zurrtum.create.client.flywheel.lib.transform.TransformStack;
import dev.lopyluna.dndecor.register.client.DnDecorPartialModels;
import com.zurrtum.create.catnip.math.AngleHelper;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Style;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.state.properties.AttachFace;

import java.awt.*;

@SuppressWarnings("unused")
public class TextPlateRenderer extends SafeBlockEntityRenderer<TextPlateBE> {
    private final Minecraft mc = Minecraft.getInstance();

    public TextPlateRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    protected void renderSafe(TextPlateBE be, float pt, PoseStack ms, MultiBufferSource bs, int l, int o) {
        ms.pushPose();
        var msr = TransformStack.of(ms);

        msr = transform(msr.center(), be).uncenter();

        ms.pushPose();
        var vb = bs.getBuffer(RenderTypes.cutoutMipped());
        var model = CachedBuffers.partial(DnDecorPartialModels.getTextPlateModel(be.metalBase, be.colorBase, be.extended), be.getBlockState());

        model.rotateCenteredDegrees(90, Axis.XP).translate(0, -0.01/16f, 0).light(l).overlay(o).renderInto(ms, vb);
        ms.popPose();

        msr.center();
        float scale = 1 / 16f;

        var color = be.dyedText;
        var text = be.text;

        ms.pushPose();
        ms.translate(0.5f/16f, 0, -6/16f);
        ms.scale(scale, -scale, scale);
        float charWidth = mc.font.width(text);
        var col = color.getTextColor();
        var hex = col;
        var hexBg = col;
        var shade = mc.level == null ? 1f : be.face == AttachFace.WALL ? mc.level.getShade(be.facing, true) : be.face == AttachFace.CEILING ? mc.level.getShade(Direction.DOWN, true) : mc.level.getShade(Direction.UP, true);
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

        ms.translate((charWidth) / -2f, -4, 0);
        var light = be.glowing ? LightCoordsUtil.FULL_BRIGHT : l;
        drawInWorldString(ms, bs, text, light, hex);
        mc.font.drawInBatch8xOutline(FormattedCharSequence.forward(text, Style.EMPTY.withColor(hex)), 0, 0, hex, hexBg, ms.last().pose(), bs, light);
        ms.popPose();

        ms.popPose();
    }

    private PoseTransformStack transform(PoseTransformStack msr, TextPlateBE be) {
        var face = be.face;
        var rX = face == AttachFace.FLOOR ? 270f : face == AttachFace.WALL ? 0f : 90f;
        var rY = AngleHelper.horizontalAngle(be.facing);
        return msr.rotateYDegrees(rY).rotateXDegrees(rX).rotateZDegrees(face == AttachFace.WALL ? 0 : 180);
    }

    public void drawInWorldString(PoseStack ms, MultiBufferSource buffer, String text, int l, int c) {
        mc.font.drawInBatch(text, 0, 0, c, false, ms.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, l);
    }
}
