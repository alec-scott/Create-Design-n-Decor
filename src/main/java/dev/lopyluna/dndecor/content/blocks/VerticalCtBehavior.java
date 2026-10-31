package dev.lopyluna.dndecor.content.blocks;


import com.zurrtum.create.client.foundation.block.connected.CTSpriteShiftEntry;
import com.zurrtum.create.client.foundation.block.connected.ConnectedTextureBehaviour;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

public class VerticalCtBehavior extends ConnectedTextureBehaviour.Base {
    private final CTSpriteShiftEntry shift;

    public VerticalCtBehavior(CTSpriteShiftEntry shift) {
        this.shift = shift;
    }

    public CTSpriteShiftEntry getShift(BlockState state, Direction direction, @Nullable TextureAtlasSprite sprite) {
        return this.shift;
    }

    protected Direction getUpDirection(BlockAndTintGetter reader, BlockPos pos, BlockState state, Direction face) {
        return Direction.UP;
    }

    public boolean buildContextForOccludedDirections() {
        return super.buildContextForOccludedDirections();
    }
}
