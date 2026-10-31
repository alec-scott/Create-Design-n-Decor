package dev.lopyluna.dndecor.content.blocks.full_belt;

import com.zurrtum.create.content.kinetics.belt.BeltBlock;
import com.zurrtum.create.content.kinetics.belt.BeltBlockEntity;
import dev.lopyluna.dndecor.register.DnDecorBETypes;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class FullBeltBlock extends BeltBlock {
    public FullBeltBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends BeltBlockEntity> getBlockEntityType() {
        return DnDecorBETypes.BELT.get();
    }
}
