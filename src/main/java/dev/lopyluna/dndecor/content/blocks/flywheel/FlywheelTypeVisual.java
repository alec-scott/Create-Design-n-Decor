package dev.lopyluna.dndecor.content.blocks.flywheel;

import com.zurrtum.create.content.kinetics.flywheel.FlywheelBlockEntity;
import com.zurrtum.create.client.content.kinetics.flywheel.FlywheelVisual;
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationContext;
import com.zurrtum.create.client.flywheel.lib.instance.InstanceTypes;
import com.zurrtum.create.client.flywheel.lib.model.Models;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;

public class FlywheelTypeVisual extends FlywheelVisual {

    public FlywheelTypeVisual(PartialModel model, VisualizationContext context, FlywheelBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);
        instancerProvider().instancer(InstanceTypes.TRANSFORMED, Models.partial(model)).stealInstance(wheel);
    }
}
