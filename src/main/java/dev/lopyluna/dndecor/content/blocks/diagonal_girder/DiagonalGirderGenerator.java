package dev.lopyluna.dndecor.content.blocks.diagonal_girder;

import dev.lopyluna.dndecor.port.create.foundation.data.SpecialBlockStateGen;
import dev.lopyluna.dndecor.port.registrate.providers.DataGenContext;
import dev.lopyluna.dndecor.port.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import dev.lopyluna.dndecor.port.porting_lib.models.generators.ModelFile;

import static dev.lopyluna.dndecor.port.create.foundation.data.AssetLookup.partialBaseModel;

public class DiagonalGirderGenerator extends SpecialBlockStateGen {

    @Override
    protected int getXRotation(BlockState state) {
        return 0;
    }

    @Override
    protected int getYRotation(BlockState state) {
        return switch (state.getValue(DiagonalGirderBlock.FACING)) {
            case NORTH -> 270;
            case SOUTH -> 90;
            case WEST -> 180;
            case EAST, UP, DOWN -> 0;
        };
    }

    @Override
    public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
        return state.getValue(DiagonalGirderBlock.FACING_UP) ? partialBaseModel(ctx, prov, "up") : partialBaseModel(ctx, prov);
    }

}