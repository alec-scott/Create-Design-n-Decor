package dev.lopyluna.dndecor;

import com.zurrtum.create.Create;
import dev.lopyluna.dndecor.port.create.foundation.data.AssetLookup;
import dev.lopyluna.dndecor.port.create.foundation.data.BlockStateGen;
import dev.lopyluna.dndecor.port.registrate.providers.DataGenContext;
import dev.lopyluna.dndecor.port.registrate.providers.RegistrateBlockstateProvider;
import dev.lopyluna.dndecor.port.registrate.util.nullness.NonNullBiConsumer;
import dev.lopyluna.dndecor.content.blocks.cogs.DnDCogWheelBlock;
import dev.lopyluna.dndecor.content.blocks.metal_supports.MetalSupportBlock;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import dev.lopyluna.dndecor.port.porting_lib.models.generators.ConfiguredModel;
import dev.lopyluna.dndecor.port.porting_lib.models.generators.ModelFile;

import static dev.lopyluna.dndecor.register.DnDecorBlocks.getBlockModel;

public class DnDecorBlockStateGen {

    public static NonNullBiConsumer<DataGenContext<Block, DnDCogWheelBlock>, RegistrateBlockstateProvider> cogwheelBlockState(boolean large){
        return (c, p) -> {
            String location = "block/";

            p.models().withExistingParent("block/" + c.getName() + "/block_shaftless", Identifier.fromNamespaceAndPath("create", large ? "block/large_cogwheel_shaftless":"block/cogwheel_shaftless"))
                    .texture(large ? "4":"1_2", DnDecor.loc(location + c.getName()))
                    .texture("particle", DnDecor.loc(location + c.getName()));
            p.models().withExistingParent("block/" + c.getName() + "/block", Identifier.withDefaultNamespace("air"))
                    .texture("particle", DnDecor.loc(location + c.getName()));
            p.models().withExistingParent("block/" + c.getName() + "/item", Identifier.fromNamespaceAndPath("create", large ? "block/large_cogwheel" : "block/cogwheel"))
                    .texture(large ? "4":"1_2", DnDecor.loc(location + c.getName()))
                    .texture("particle", DnDecor.loc(location + c.getName()));

            BlockStateGen.axisBlock(c, p, getBlockModel(true, c, p));
        };
    }

    public static NonNullBiConsumer<DataGenContext<Block, MetalSupportBlock>, RegistrateBlockstateProvider> metalSupportBlockState(){
        return (c, p) -> p.getVariantBuilder(c.get()).forAllStates(s -> {
            Direction.Axis axis = s.getValue(MetalSupportBlock.HORIZONTAL_AXIS);
            boolean top = s.getValue(MetalSupportBlock.TOP);


            ModelFile topModel = AssetLookup.partialBaseModel(c,p,"top");
            ModelFile bottomModel = AssetLookup.partialBaseModel(c,p,"bottom");

             return ConfiguredModel.builder()
                    .modelFile(top?topModel:bottomModel)
                    .rotationY(axis == Direction.Axis.Z ? 0 : 90)
                    .build();
        });
    }




}
