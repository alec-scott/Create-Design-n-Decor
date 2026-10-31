package dev.lopyluna.dndecor.content.blocks;

import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;
import com.zurrtum.create.content.kinetics.millstone.MillstoneBlockEntity;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import dev.lopyluna.dndecor.DnDecor;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class MillstoneTypeRenderer extends KineticBlockEntityRenderer<MillstoneBlockEntity> {
    public MillstoneTypeRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(MillstoneBlockEntity be, BlockState state) {
        if (state.getBlock() instanceof MillstoneTypeBlock block && block.id != null && !block.id.isEmpty())
            return CachedBuffers.partial(PartialModel.of(DnDecor.loc("block/" + block.id + "_millstone/inner")), state);
        return CachedBuffers.partial(AllPartialModels.MILLSTONE_COG, state);
    }
}
