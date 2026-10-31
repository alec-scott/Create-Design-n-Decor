package dev.lopyluna.dndecor.register;

import dev.lopyluna.dndecor.port.create.ExistingEntries;
import com.zurrtum.create.*;
import com.zurrtum.create.client.content.decoration.encasing.EncasedCTBehaviour;
import com.zurrtum.create.content.decoration.palettes.ConnectedGlassPaneBlock;
import com.zurrtum.create.content.decoration.palettes.WindowBlock;
import com.zurrtum.create.content.kinetics.belt.BeltBlock;
import com.zurrtum.create.client.infrastructure.model.BeltModel;
import com.zurrtum.create.content.kinetics.crusher.CrushingWheelBlock;
import com.zurrtum.create.content.kinetics.flywheel.FlywheelBlock;
import com.zurrtum.create.content.kinetics.millstone.MillstoneBlock;
import com.zurrtum.create.client.infrastructure.model.BracketedKineticBlockModel;
import com.zurrtum.create.content.logistics.vault.ItemVaultBlock;
import com.zurrtum.create.content.trains.display.FlapDisplayBlock;
import dev.lopyluna.dndecor.port.create.foundation.block.CopperBlockSet;
import dev.lopyluna.dndecor.port.create.foundation.block.DyedBlockList;
import dev.lopyluna.dndecor.port.create.ItemUseOverrides;
import com.zurrtum.create.client.foundation.block.connected.*;
import dev.lopyluna.dndecor.port.create.foundation.data.AssetLookup;
import dev.lopyluna.dndecor.port.create.foundation.data.BlockStateGen;
import dev.lopyluna.dndecor.port.create.foundation.data.CreateRegistrate;
import dev.lopyluna.dndecor.port.create.foundation.data.SharedProperties;
import dev.lopyluna.dndecor.port.registrate.providers.DataGenContext;
import dev.lopyluna.dndecor.port.registrate.providers.RegistrateBlockstateProvider;
import dev.lopyluna.dndecor.port.registrate.providers.RegistrateRecipeProvider;
import dev.lopyluna.dndecor.port.registrate.util.DataIngredient;
import dev.lopyluna.dndecor.port.registrate.util.entry.BlockEntry;
import dev.lopyluna.dndecor.DnDecor;
import dev.lopyluna.dndecor.content.blocks.*;
import dev.lopyluna.dndecor.content.blocks.beam.BeamBlock;
import dev.lopyluna.dndecor.content.blocks.beam.BeamCTBehaviour;
import dev.lopyluna.dndecor.content.blocks.boiler.BoilerBlock;
import dev.lopyluna.dndecor.content.blocks.boiler.BoilerStructureBlock;
import dev.lopyluna.dndecor.content.blocks.breaker_switch.BreakerSwitchBlock;
import dev.lopyluna.dndecor.content.blocks.catwalk.CatwalkBlock;
import dev.lopyluna.dndecor.content.blocks.catwalk.CatwalkCTBehaviour;
import dev.lopyluna.dndecor.content.blocks.cogs.DnDCogWheelBlock;
import dev.lopyluna.dndecor.content.blocks.cogs.DnDCogwheelBlockItem;
import dev.lopyluna.dndecor.content.blocks.container.DyedContainerBlock;
import dev.lopyluna.dndecor.content.blocks.container.DyedContainerCTBehaviour;
import dev.lopyluna.dndecor.content.blocks.container.DyedContainerItem;
import dev.lopyluna.dndecor.content.blocks.diagonal_girder.DiagonalGirderBlock;
import dev.lopyluna.dndecor.content.blocks.flywheel.FreeSpinBlock;
import dev.lopyluna.dndecor.content.blocks.frontlight.Frontlight;
import dev.lopyluna.dndecor.content.blocks.frontlight.FrontlightBlock;
import dev.lopyluna.dndecor.content.blocks.full_belt.FullBeltBlock;
import dev.lopyluna.dndecor.content.blocks.lamp.LampBlock;
import dev.lopyluna.dndecor.content.blocks.metal_supports.DiagonalMetalSupportBlock;
import dev.lopyluna.dndecor.content.blocks.metal_supports.DiagonalMetalSupportCtBehavior;
import dev.lopyluna.dndecor.content.blocks.metal_supports.MetalSupportBlock;
import dev.lopyluna.dndecor.content.blocks.stepped_lever.SteppedLeverBlock;
import dev.lopyluna.dndecor.content.blocks.text_plate.TextPlateBlock;
import dev.lopyluna.dndecor.content.configs.server.kinetics.DStress;
import dev.lopyluna.dndecor.content.entries.BoltEntry;
import dev.lopyluna.dndecor.register.client.DnDecorPartialModels;
import dev.lopyluna.dndecor.register.client.DnDecorSpriteShifts;
import dev.lopyluna.dndecor.register.helpers.list_providers.MaterialTypeProvider;
import dev.lopyluna.dndecor.register.helpers.list_providers.MetalTypeBlockList;
import dev.lopyluna.dndecor.register.helpers.list_providers.MetalTypeBoltBlockList;
import dev.lopyluna.dndecor.register.helpers.list_providers.StoneTypeBlockList;
import com.zurrtum.create.catnip.data.Iterate;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import dev.lopyluna.dndecor.port.porting_lib.models.generators.ConfiguredModel;
import dev.lopyluna.dndecor.port.porting_lib.models.generators.ModelFile;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;

import static dev.lopyluna.dndecor.port.create.CreateTransforms.displaySource;
import static dev.lopyluna.dndecor.port.create.CreateTransforms.displayTarget;
import static dev.lopyluna.dndecor.port.create.CreateTransforms.mountedItemStorage;
import static dev.lopyluna.dndecor.port.create.foundation.data.CreateRegistrate.casingConnectivity;
import static dev.lopyluna.dndecor.port.create.foundation.data.CreateRegistrate.connectedTextures;
import static dev.lopyluna.dndecor.port.create.foundation.data.ModelGen.customItemModel;
import static dev.lopyluna.dndecor.port.create.foundation.data.TagGen.axeOrPickaxe;
import static dev.lopyluna.dndecor.port.create.foundation.data.TagGen.pickaxeOnly;
import static dev.lopyluna.dndecor.port.registrate.providers.RegistrateRecipeProvider.has;
import static dev.lopyluna.dndecor.DnDecor.DYE_DEPOT;
import static dev.lopyluna.dndecor.DnDecor.REG;
import static dev.lopyluna.dndecor.register.DnDecorTags.commonItemTag;
import static dev.lopyluna.dndecor.register.DnDecorTags.optionalTag;
import static dev.lopyluna.dndecor.register.helpers.BlockTransgender.*;

@SuppressWarnings({"removal", "deprecation", "SameParameterValue", "unused", "Convert2Diamond"})
public class DnDecorBlocks {

    public static TagKey<Item> darkMetalDecorTag = optionalTag(BuiltInRegistries.ITEM, DnDecor.loc("dark_metal_decor"));

    public static final BlockEntry<FullBeltBlock> BELT = REG.block("belt", FullBeltBlock::new)
            .properties(p -> p.sound(SoundType.WOOL).strength(0.8f).mapColor(MapColor.COLOR_GRAY))
            .transform(axeOrPickaxe())
            .transform(DStress.setNoImpact())
            .transform(displaySource(AllDisplaySources.ITEM_NAMES))
            .onRegister(CreateRegistrate.blockModel(() -> BeltModel::new))
            .register();

    public static final BlockEntry<TextPlateBlock> TEXT_PLATE = REG.block("text_plate", TextPlateBlock::new)
            .initialProperties(() -> Blocks.LEVER)
            .transform(axeOrPickaxe())
            .tag(AllBlockTags.SAFE_NBT).item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<BreakerSwitchBlock> BREAKER_SWITCH = REG.block("breaker_switch", BreakerSwitchBlock::new)
            .initialProperties(() -> Blocks.LEVER)
            .transform(axeOrPickaxe())
            .tag(AllBlockTags.SAFE_NBT)
            .onRegister(ItemUseOverrides::addBlock)
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<SteppedLeverBlock> STEPPED_LEVER = REG.block("stepped_lever", SteppedLeverBlock::new)
            .initialProperties(() -> Blocks.LEVER)
            .transform(axeOrPickaxe())
            .tag(AllBlockTags.SAFE_NBT)
            .onRegister(ItemUseOverrides::addBlock)
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<MetalSupportBlock> METAL_SUPPORT = REG.block("metal_support", MetalSupportBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(BlockBehaviour.Properties::noOcclusion)
            .transform(pickaxeOnly())
            .onRegister(connectedTextures(() -> new VerticalCtBehavior(DnDecorSpriteShifts.METAL_SUPPORT)))
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<DiagonalMetalSupportBlock> DIAGONAL_METAL_SUPPORT = REG.block("diagonal_metal_support", DiagonalMetalSupportBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(BlockBehaviour.Properties::noOcclusion)
            .transform(pickaxeOnly())
            .onRegister(connectedTextures(() -> new DiagonalMetalSupportCtBehavior(DnDecorSpriteShifts.DIAGONAL_METAL_SUPPORT)))
            .item()
            .transform(customItemModel())
            .register();

    public static final DyedBlockList<FlapDisplayBlock> DYED_DISPLAY_BOARDS = new DyedBlockList<>(color -> {
        String colorName = color.getSerializedName();
        return REG.block(colorName + "_display_board", FlapDisplayTypeBlock::new)
                .initialProperties(SharedProperties::softMetal)
                .properties(p -> p.mapColor(color.getMapColor()))
                .transform(pickaxeOnly()).asOptional()
                .transform(DStress.setNoImpact())
                .transform(displayTarget(AllDisplayTargets.DISPLAY_BOARD))
                .item()
                .tag(commonItemTag("create/display_boards"), commonItemTag("create/dyed_display_boards")).asOptional()
                .transform(customItemModel())
                .register();
    });

    public static final DyedBlockList<FlywheelBlock> DYED_FLYWHEELS = new DyedBlockList<>(color -> {
        String colorName = color.getSerializedName();
        return REG.block(colorName + "_flywheel", p -> new FreeSpinBlock(color, FreeSpinBlock.Type.FLYWHEEL, p))
                .initialProperties(SharedProperties::softMetal)
                .properties(p -> p.noOcclusion().mapColor(color.getMapColor()))
                .transform(axeOrPickaxe()).asOptional()
                .transform(DStress.setNoImpact())
                .item()
                .tag(commonItemTag("create/flywheels"), commonItemTag("create/dyed_flywheels")).asOptional()
                .transform(customItemModel())
                .register();
    });

    public static final BlockEntry<FreeSpinBlock> LARGE_FAN = REG.block("large_fan", p -> new FreeSpinBlock(FreeSpinBlock.Type.LARGE_FAN, p))
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.noOcclusion().mapColor(DyeColor.LIGHT_GRAY))
            .transform(axeOrPickaxe())
            .transform(DStress.setNoImpact())
            .item()
            .tag(DnDecorTags.modItemTag("large_fans"))
            .transform(customItemModel())
            .register();

    public static final DyedBlockList<FreeSpinBlock> DYED_LARGE_FANS = new DyedBlockList<FreeSpinBlock>(color -> {
        String colorName = color.getSerializedName();
        return REG.block(colorName + "_large_fan", p -> new FreeSpinBlock(color, FreeSpinBlock.Type.LARGE_FAN, p))
                .initialProperties(SharedProperties::softMetal)
                .properties(p -> p.noOcclusion().mapColor(color.getMapColor()))
                .transform(axeOrPickaxe()).asOptional()
                .transform(DStress.setNoImpact())
                .item()
                .tag(DnDecorTags.modItemTag("large_fans"), DnDecorTags.modItemTag("dyed_large_fans")).asOptional()
                .transform(customItemModel())
                .register();
    });

    public static final BlockEntry<DnDCogWheelBlock> DARK_METAL_COGWHEEL = REG.block("dark_metal_cogwheel", p -> new DnDCogWheelBlock(DnDecorPartialModels.DARK_METAL_COGWHEEL,false,p))
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.noOcclusion().mapColor(MapColor.COLOR_BLACK).sound(SoundType.NETHERITE_BLOCK))
            .transform(axeOrPickaxe())
            .transform(DStress.setNoImpact())
            .onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))
            .item(DnDCogwheelBlockItem::new)
            .tag(DnDecorTags.modItemTag("cogwheel"))
            .transform(customItemModel())
            .register();

    public static final BlockEntry<DnDCogWheelBlock> LARGE_DARK_METAL_COGWHEEL = REG.block("large_dark_metal_cogwheel", p -> new DnDCogWheelBlock(DnDecorPartialModels.LARGE_DARK_METAL_COGWHEEL,true,p))
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.noOcclusion().mapColor(MapColor.COLOR_BLACK).sound(SoundType.NETHERITE_BLOCK))
            .transform(axeOrPickaxe())
            .transform(DStress.setNoImpact())
            .onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))
            .item(DnDCogwheelBlockItem::new)
            .tag(DnDecorTags.modItemTag("large_cogwheel"))
            .transform(customItemModel())
            .register();

    public static final BlockEntry<DnDCogWheelBlock> INDUSTRIAL_COGWHEEL = REG.block("industrial_cogwheel", p -> new DnDCogWheelBlock(DnDecorPartialModels.INDUSTRIAL_COGWHEEL,false,p))
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.noOcclusion().sound(SoundType.NETHERITE_BLOCK).mapColor(DyeColor.GRAY))
            .transform(axeOrPickaxe())
            .transform(DStress.setNoImpact())
            .item(DnDCogwheelBlockItem::new)
            .tag(DnDecorTags.modItemTag("industrial_cogwheel"))
            .transform(customItemModel())
            .register();

    public static final BlockEntry<DnDCogWheelBlock> LARGE_INDUSTRIAL_COGWHEEL = REG.block("large_industrial_cogwheel", p -> new DnDCogWheelBlock(DnDecorPartialModels.LARGE_INDUSTRIAL_COGWHEEL,true,p))
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.noOcclusion().sound(SoundType.NETHERITE_BLOCK).mapColor(DyeColor.GRAY))
            .transform(axeOrPickaxe())
            .transform(DStress.setNoImpact())
            .item(DnDCogwheelBlockItem::new)
            .tag(DnDecorTags.modItemTag("large_industrial_cogwheel"))
            .transform(customItemModel())
            .register();

    public static final DyedBlockList<DnDCogWheelBlock> DYED_INDUSTRIAL_COGWHEELS = new DyedBlockList<DnDCogWheelBlock>(color -> {
        String colorName = color.getSerializedName();
        var modSupport = isDyeDepotColor(color);
        return REG.block(colorName + "_industrial_cogwheel", p -> new DnDCogWheelBlock(DnDecorPartialModels.DYED_GEAR.get(color),false, p))
                .initialProperties(SharedProperties::softMetal)
                .properties(p -> p.noOcclusion().sound(SoundType.NETHERITE_BLOCK).mapColor(color.getMapColor()))
                .transform(axeOrPickaxe()).asOptional()
                .transform(DStress.setNoImpact()).item(DnDCogwheelBlockItem::new).tag(DnDecorTags.modItemTag("industrial_cogwheels"), DnDecorTags.modItemTag("dyed_industrial_cogwheels")).asOptional()
                .transform(customItemModel())
                .register();
    });

    public static final DyedBlockList<DnDCogWheelBlock> DYED_LARGE_INDUSTRIAL_COGWHEELS = new DyedBlockList<>(color -> {
        String colorName = color.getSerializedName();
        return REG.block(colorName + "_large_industrial_cogwheel", p -> new DnDCogWheelBlock(DnDecorPartialModels.DYED_LARGE_GEAR.get(color),true, p))
                .initialProperties(SharedProperties::softMetal)
                .properties(p -> p.noOcclusion().sound(SoundType.NETHERITE_BLOCK).mapColor(color.getMapColor()))
                .transform(axeOrPickaxe()).asOptional()
                .transform(DStress.setNoImpact()).item(DnDCogwheelBlockItem::new).tag(DnDecorTags.modItemTag("large_industrial_cogwheel"), DnDecorTags.modItemTag("dyed_large_industrial_cogwheel")).asOptional()
                .transform(customItemModel())
                .register();
    });

    public static final DyedBlockList<DnDCogWheelBlock> DYED_COGWHEELS = new DyedBlockList<>(color -> {
        String colorName = color.getSerializedName();
        return REG.block(colorName + "_cogwheel", p -> new DnDCogWheelBlock(color,false, p))
                .initialProperties(SharedProperties::softMetal)
                .properties(p -> p.noOcclusion().mapColor(color.getMapColor()).sound(SoundType.NETHERITE_BLOCK))
                .transform(axeOrPickaxe()).asOptional()
                .transform(DStress.setNoImpact())
                .onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))
                .item(DnDCogwheelBlockItem::new).tag(DnDecorTags.modItemTag("cogwheel"), DnDecorTags.modItemTag("dyed_cogwheel")).asOptional()
                .transform(customItemModel())
                .register();
    });

    public static final DyedBlockList<DnDCogWheelBlock> DYED_LARGE_COGWHEELS = new DyedBlockList<>(color -> {
        String colorName = color.getSerializedName();
        return REG.block(colorName + "_large_cogwheel", p -> new DnDCogWheelBlock(color,true, p))
                .initialProperties(SharedProperties::softMetal)
                .properties(p -> p.noOcclusion().mapColor(color.getMapColor()).sound(SoundType.NETHERITE_BLOCK))
                .transform(axeOrPickaxe()).asOptional()
                .transform(DStress.setNoImpact())
                .onRegister(CreateRegistrate.blockModel(() -> BracketedKineticBlockModel::new))
                .item(DnDCogwheelBlockItem::new).tag(DnDecorTags.modItemTag("large_cogwheel"), DnDecorTags.modItemTag("dyed_large_cogwheel")).asOptional()
                .transform(customItemModel())
                .register();
    });

    public static final BlockEntry<OrnateGrateBlock> ORNATE_GRATE = REG.block("ornate_grate", OrnateGrateBlock::new)
            .transform(ornateConnected(() -> omni("ornate_grate")))
            .initialProperties(SharedProperties::wooden)
            .properties(p -> p.sound(SoundType.WOOD).mapColor(MapColor.TERRACOTTA_GRAY)).transform(axeOrPickaxe())
            .tag(AllBlockTags.FAN_TRANSPARENT)
            .lang("Ornate Grate")
            .item()
            .transform(b -> b.build())
            .register();

    public static final BlockEntry<WindowBlock> ORNATE_IRON_GLASS =
            customWindowBlock("ornate_iron_glass", () -> omni("palettes/ornate_iron_glass"), () -> omni("palettes/ornate_iron_glass_end"), () -> () -> null, false, () -> MapColor.TERRACOTTA_LIGHT_GRAY).register();

    public static final BlockEntry<ConnectedGlassPaneBlock> ORNATE_IRON_GLASS_PANE =
            customWindowPane("ornate_iron_glass", ORNATE_IRON_GLASS, () -> omni("palettes/ornate_iron_glass"), () -> () -> null).register();

    public static final StoneTypeBlockList<CrushingWheelBlock> STONE_TYPE_CRUSHING_WHEELS = new StoneTypeBlockList<>((block, id) -> {
        if (id.equals("andesite")) return ExistingEntries.block(REG, AllBlocks.CRUSHING_WHEEL);
        return REG.block(id + "_crushing_wheel", p -> new CrushingWheelTypeBlock(block, p))
                .properties(p -> p.mapColor(block.get().defaultMapColor()).sound(block.get().defaultBlockState().getSoundType()))
                .initialProperties(SharedProperties::stone)
                .properties(BlockBehaviour.Properties::noOcclusion)
                .transform(pickaxeOnly())
                .transform(DStress.setImpact(8.0))
                .item()
                .tag(commonItemTag("create/crushing_wheels"))
                .transform(customItemModel())
                .register();
    });

    public static final StoneTypeBlockList<MillstoneBlock> STONE_TYPE_MILLSTONE = new StoneTypeBlockList<>((block, id) -> {
        if (id.equals("andesite")) return ExistingEntries.block(REG, AllBlocks.MILLSTONE);
        return REG.block(id + "_millstone", p -> new MillstoneTypeBlock(id, block, p))
                .properties(p -> p.mapColor(block.get().defaultMapColor()).sound(block.get().defaultBlockState().getSoundType()))
                .initialProperties(SharedProperties::stone)
                .transform(pickaxeOnly())
                .transform(DStress.setImpact(4.0))
                .item()
                .tag(commonItemTag("create/millstones"))
                .transform(customItemModel())
                .register();
    });

    public static final BlockEntry<Block> INDUSTRIAL_PLATING_BLOCK = REG.block("industrial_plating_block", Block::new)
            .transform(layeredConnected(() -> omni("industrial_plating_block_side"), () -> omni("industrial_plating_block")))
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .properties(BlockBehaviour.Properties::requiresCorrectToolForDrops)
            .transform(pickaxeOnly())
            .tag(AllBlockTags.WRENCH_PICKUP)
            .simpleItem()
            .lang("Block of Industrial Plating")
            .register();

    public static final BlockEntry<WrenchConnectedPillarBlock> LARGE_METAL_GIRDER = REG.block("large_metal_girder", WrenchConnectedPillarBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY).sound(SoundType.NETHERITE_BLOCK)).transform(pickaxeOnly())
            .onRegister(connectedTextures(() -> new RotatedPillarCTBehaviour(rectangle("large_girder"), omni("large_girder_top"))))
            .item().tag(DnDecorTags.modItemTag("large_metal_girder_decor")).build()
            .register();

    public static final DyedBlockList<WrenchConnectedPillarBlock> DYED_LARGE_METAL_GIRDER = new DyedBlockList<WrenchConnectedPillarBlock>(color -> {
        var baseID = "large_metal_girder";
        var colorID = color.getSerializedName();
        var blockID = colorID + "_" + baseID;
        return REG.block(blockID, WrenchConnectedPillarBlock::new)
                .initialProperties(SharedProperties::softMetal)
                .properties(p -> p.mapColor(color.getMapColor()).sound(SoundType.NETHERITE_BLOCK)).transform(pickaxeOnly()).asOptional()
                .onRegister(connectedTextures(() -> new RotatedPillarCTBehaviour(rectangle("large_girders/"+colorID), omni("large_girders/"+colorID+"_top"))))
                .item().tag(DnDecorTags.modItemTag("large_metal_girder_decor"), DnDecorTags.modItemTag("dyed_large_metal_girder_decor")).asOptional().build()
                .register();
    });

    public static final MetalTypeBlockList<WrenchConnectedPillarBlock> METAL_TYPE_SHEET_METAL = new MetalTypeBlockList<WrenchConnectedPillarBlock>(type -> {
        var metal = type.get();
        if (!DnDecor.LOAD_ALL_METALS && metal.modIDs.equals(MaterialTypeProvider.NA)) return null;
        var baseID = "sheet_metal";
        var blockID = metal.id + "_" + baseID;
        var builder = REG.block(blockID, WrenchConnectedPillarBlock::new)
                .initialProperties(SharedProperties::softMetal)
                .properties(p -> p.mapColor(metal.color).sound(metal.sound))
                .transform(pickaxeOnly())
                .onRegister(connectedTextures(() -> new RotatedPillarCTBehaviour(rectangle("sheet_metals/"+metal.id), omni("sheet_metals/"+metal.id+"_top"))));

        builder = builder;
        var itemBuilder = builder.item();

        if (metal.equals(AllMetalTypes.NETHERITE)) itemBuilder = itemBuilder.properties(Item.Properties::fireResistant);
        if (metal.equals(AllMetalTypes.IRON) || metal.equals(AllMetalTypes.ANDESITE) || metal.equals(AllMetalTypes.COPPER) || metal.equals(AllMetalTypes.INDUSTRIAL))
            itemBuilder = itemBuilder.tag(DnDecorTags.modItemTag("sheet_metal_decor"));

        return itemBuilder.build().register();
    });

    public static final DyedBlockList<WrenchConnectedPillarBlock> DYED_SHEET_METAL = new DyedBlockList<WrenchConnectedPillarBlock>(color -> {
        var baseID = "sheet_metal";
        var colorID = color.getSerializedName();
        var blockID = colorID + "_" + baseID;
        return REG.block(blockID, WrenchConnectedPillarBlock::new)
                .initialProperties(SharedProperties::softMetal)
                .properties(p -> p.mapColor(color.getMapColor()).sound(SoundType.NETHERITE_BLOCK)).transform(pickaxeOnly()).asOptional()
                .onRegister(connectedTextures(() -> new RotatedPillarCTBehaviour(rectangle("sheet_metals/"+colorID), omni("sheet_metals/"+colorID+"_top"))))
                .item().tag(DnDecorTags.modItemTag("sheet_metal_decor"), DnDecorTags.modItemTag("dyed_sheet_metal_decor")).asOptional().build()
                .register();
    });

    public static final BlockEntry<BeamBlock> BEAM = REG.block("beam", BeamBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.noOcclusion().sound(SoundType.NETHERITE_BLOCK).mapColor(MapColor.COLOR_GRAY))
            .properties(BlockBehaviour.Properties::requiresCorrectToolForDrops)
            .onRegister(connectedTextures(() -> new BeamCTBehaviour(horizontalKryppers("beam/beam"), vertical("beam/beam_top_z"), horizontalKryppers("beam/beam_top_x"))))
            .transform(pickaxeOnly())
            .item()
            .build()
            .lang("Beam")
            .register();

    public static final BlockEntry<DiagonalGirderBlock> DIAGONAL_GIRDER = REG.block("diagonal_girder", DiagonalGirderBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY))
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .properties(BlockBehaviour.Properties::noOcclusion).transform(axeOrPickaxe())
            .tag(AllBlockTags.WRENCH_PICKUP)
            .lang("Diagonal Girder")
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<Block> ZINC_BRICKS = REG.block("zinc_bricks", Block::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.sound(SoundType.METAL).mapColor(MapColor.GLOW_LICHEN)).transform(pickaxeOnly())
            .lang("Zinc Bricks")
            .simpleItem()
            .register();

    public static final BlockEntry<Block> ZINC_CHECKER_TILES = REG.block("zinc_checker_tiles", Block::new)
            .transform(connected(() -> omni("zinc_checker_tiles")))
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.sound(SoundType.METAL).mapColor(MapColor.GLOW_LICHEN)).transform(pickaxeOnly())
            .lang("Zinc Checker Tiles")
            .simpleItem()
            .register();

    public static final BlockEntry<Block> STONE_METAL = REG.block("stone_metal", Block::new)
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_CYAN).sound(DnDecorSoundTypes.METAL_HEAVY).strength(1.5f,2f))
            .onRegister(connectedTextures(() -> new EncasedCTBehaviour(omni("stone_metal"))))
            .onRegister(casingConnectivity((block, cc) -> cc.makeCasing(block, omni("stone_metal"))))
            .transform(pickaxeOnly())
            .item().tag(DnDecorTags.modItemTag("stone_metal_decor")).build()
            .register();

    public static final DyedBlockList<Block> DYED_STONE_METAL = new DyedBlockList<>(color -> {
        var baseID = "stone_metal";
        var colorID = color.getSerializedName();
        var blockID = colorID + "_" + baseID;
        // PORT-NOTE(26.1): lazy, CT sprite shifts are client only (built eagerly this crashed dedicated servers)
        com.google.common.base.Supplier<CTSpriteShiftEntry> ct = com.google.common.base.Suppliers.memoize(() -> omni(baseID + "/" + colorID));
        return REG.block(blockID, Block::new)
                .properties(p -> p.mapColor(color.getMapColor()).sound(DnDecorSoundTypes.METAL_HEAVY).strength(1.5f,2f))
                .onRegister(connectedTextures(() -> new EncasedCTBehaviour(ct.get())))
                .onRegister(casingConnectivity((block, cc) -> cc.makeCasing(block, ct.get())))
                .transform(pickaxeOnly()).asOptional()
                .item().tag(DnDecorTags.modItemTag("stone_metal_decor"), DnDecorTags.modItemTag("dyed_stone_metal_decor")).asOptional().build()
                .register();
    });

    public static final BlockEntry<Block> DEEPSLATE_TILES = REG.block("small_deepslate_tiles",Block::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.DEEPSLATE).sound(SoundType.POLISHED_DEEPSLATE))
            .onRegister(connectedTextures(() -> new EncasedCTBehaviour(omni("small_deepslate_tiles"))))
            .onRegister(casingConnectivity((block, cc) -> cc.makeCasing(block, omni("small_deepslate_tiles"))))
            .transform(pickaxeOnly())
            .item().tag(DnDecorTags.modItemTag("deepslate_tiles_decor")).build()
            .register();

    public static final DyedBlockList<Block> DYED_DEEPSLATE_TILES = new DyedBlockList<>(color -> {
        var baseID = "small_deepslate_tiles";
        var colorID = color.getSerializedName();
        var blockID = colorID + "_" + baseID;
        // PORT-NOTE(26.1): lazy, CT sprite shifts are client only (built eagerly this crashed dedicated servers)
        com.google.common.base.Supplier<CTSpriteShiftEntry> ct = com.google.common.base.Suppliers.memoize(() -> omni(baseID + "/" + colorID));
        return REG.block(blockID, Block::new)
                .initialProperties(SharedProperties::stone)
                .properties(p -> p.mapColor(color.getMapColor()).sound(SoundType.POLISHED_DEEPSLATE))
                .onRegister(connectedTextures(() -> new EncasedCTBehaviour(ct.get())))
                .onRegister(casingConnectivity((block, cc) -> cc.makeCasing(block, ct.get())))
                .transform(pickaxeOnly()).asOptional()
                .item().tag(DnDecorTags.modItemTag("deepslate_tiles_decor"), DnDecorTags.modItemTag("dyed_deepslate_tiles_decor")).asOptional().build()
                .register();
    });

    public static final DyedBlockList<VelvetBlock> DYED_VELVET_BLOCKS = new DyedBlockList<>(color -> velvetBlock(color.getSerializedName(), color.getMapColor(), color));

    public static final BlockEntry<DyedContainerBlock> CONTAINER = REG.block("container", p -> new DyedContainerBlock(p, false, null))
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.TERRACOTTA_BLUE).sound(SoundType.NETHERITE_BLOCK).explosionResistance(1200))
            .transform(pickaxeOnly())
            .onRegister(connectedTextures(DyedContainerCTBehaviour::new))
            .transform(mountedItemStorage(DnDecorMountedStorageTypes.CONTAINER))
            .item(DyedContainerItem::new)
            .tag(DnDecorTags.modItemTag("containers_decor"), DnDecorTags.modItemTag("non_solid_containers_decor"))
            .build()
            .register();

    public static final DyedBlockList<DyedContainerBlock> DYED_CONTAINERS = new DyedBlockList<>(DnDecorBlocks::dyedContainer);
    public static final DyedBlockList<DyedContainerBlock> DYED_SOLID_CONTAINERS = new DyedBlockList<>(DnDecorBlocks::dyedContainerSolid);

    private static BlockEntry<DyedContainerBlock> dyedContainerSolid(DyeColor color) {
        return dyedContainer(color, true);
    }
    private static BlockEntry<DyedContainerBlock> dyedContainer(DyeColor color) {
        return dyedContainer(color, false);
    }
    private static BlockEntry<DyedContainerBlock> dyedContainer(DyeColor color, boolean solid) {
        var id = color.getSerializedName();
        return REG.block((solid ? "solid_" : "") + id + "_container", p -> new DyedContainerBlock(p, solid, color))
                .initialProperties(SharedProperties::softMetal)
                .properties(p -> p.mapColor(color).sound(SoundType.NETHERITE_BLOCK).explosionResistance(1200))
                .transform(pickaxeOnly()).asOptional().onRegister(connectedTextures(DyedContainerCTBehaviour::new))
                .transform(mountedItemStorage(DnDecorMountedStorageTypes.CONTAINER))
                .item(DyedContainerItem::new)
                .tag(DnDecorTags.modItemTag("containers_decor"), solid ? DnDecorTags.modItemTag("solid_containers_decor") : DnDecorTags.modItemTag("non_solid_containers_decor"), DnDecorTags.modItemTag("dyed_containers_decor")).asOptional()
                .build()
                .register();
    }

    public static TagKey<Block> stairsBlockTag = optionalTag(BuiltInRegistries.BLOCK, Identifier.withDefaultNamespace("stairs"));
    public static TagKey<Item> stairsItemTag = optionalTag(BuiltInRegistries.ITEM, Identifier.withDefaultNamespace("stairs"));
    public static TagKey<Block> slabsBlockTag = optionalTag(BuiltInRegistries.BLOCK, Identifier.withDefaultNamespace("slabs"));
    public static TagKey<Item> slabsItemTag = optionalTag(BuiltInRegistries.ITEM, Identifier.withDefaultNamespace("slabs"));

    public static final BlockEntry<Block> DARK_METAL_BLOCK = REG.block("dark_metal_block", Block::new)
            .properties(p -> p.mapColor(MapColor.COLOR_BLACK).sound(SoundType.NETHERITE_BLOCK).strength(0.5f,1.5f))
            .transform(pickaxeOnly())
            .tag(AllBlockTags.WRENCH_PICKUP)
            .item()
            .tag(darkMetalDecorTag, DnDecorTags.modItemTag("dark_metal_block_decor"))
            .build()
            .register();

    public static final DyedBlockList<Block> DYED_DARK_METAL_BLOCK = new DyedBlockList<>(color -> {
        var baseID = "dark_metal_block";
        var colorID = color.getSerializedName();
        var blockID = colorID + "_" + baseID;
        return REG.block(blockID, Block::new)
                .properties(p -> p.mapColor(color.getMapColor()).sound(SoundType.NETHERITE_BLOCK).strength(0.5f,1.5f))
                .transform(pickaxeOnly()).asOptional()
                .item().tag(DnDecorTags.modItemTag(baseID + "_decor"), DnDecorTags.modItemTag(baseID + "_dyed_decor")).asOptional().build()
                .register();
    });

    public static final BlockEntry<Block> DARK_METAL_PLATING = REG.block("dark_metal_plating", Block::new)
            .properties(p -> p.mapColor(MapColor.COLOR_BLACK).sound(SoundType.NETHERITE_BLOCK).strength(0.5f,1.5f))
            .onRegister(connectedTextures(() -> new EncasedCTBehaviour(omni("dark_metal_plating"))))
            .onRegister(casingConnectivity((block, cc) -> cc.make(block, omni("dark_metal_plating"))))
            .transform(pickaxeOnly())
            .tag(AllBlockTags.WRENCH_PICKUP)
            .item()
            .tag(darkMetalDecorTag, DnDecorTags.modItemTag("dark_metal_plating_decor"))
            .build()
            .register();

    public static final DyedBlockList<Block> DYED_DARK_METAL_PLATING = new DyedBlockList<>(color -> {
        var baseID = "dark_metal_plating";
        var colorID = color.getSerializedName();
        var blockID = colorID + "_" + baseID;
        // PORT-NOTE(26.1): lazy, CT sprite shifts are client only (built eagerly this crashed dedicated servers)
        com.google.common.base.Supplier<CTSpriteShiftEntry> ct = com.google.common.base.Suppliers.memoize(() -> omni("dark_metal/" + colorID));
        return REG.block(blockID, Block::new)
                .properties(p -> p.mapColor(color.getMapColor()).sound(SoundType.NETHERITE_BLOCK).strength(0.5f,1.5f))
                .onRegister(connectedTextures(() -> new EncasedCTBehaviour(ct.get())))
                .onRegister(casingConnectivity((block, cc) -> cc.make(block, ct.get())))
                .transform(pickaxeOnly()).asOptional()
                .item().tag(DnDecorTags.modItemTag(baseID + "_decor"), DnDecorTags.modItemTag(baseID + "_dyed_decor")).asOptional().build()
                .register();
    });

    public static final BlockEntry<SlabBlock> DARK_METAL_SLAB = REG.block("dark_metal_block_slab", SlabBlock::new)
            .properties(p -> p.mapColor(MapColor.COLOR_BLACK).sound(SoundType.NETHERITE_BLOCK).strength(0.5f,1.5f))
            .transform(pickaxeOnly())
            .tag(stairsBlockTag)
            .tag(AllBlockTags.WRENCH_PICKUP)
            .item()
            .tag(slabsItemTag)
            .build()
            .register();

    public static final BlockEntry<StairBlock> DARK_METAL_STAIRS = REG.block("dark_metal_block_stairs", p -> new StairBlock(DARK_METAL_BLOCK.getDefaultState(), p))
            .properties(p -> p.mapColor(MapColor.COLOR_BLACK).sound(SoundType.NETHERITE_BLOCK).strength(0.5f,1.5f))
            .transform(pickaxeOnly())
            .tag(stairsBlockTag)
            .item()
            .tag(darkMetalDecorTag, stairsItemTag)
            .build()
            .register();

    public static final BlockEntry<Block> DARK_METAL_BRICKS = REG.block("dark_metal_bricks", Block::new)
            .properties(p -> p.mapColor(MapColor.COLOR_BLACK).sound(SoundType.NETHERITE_BLOCK).strength(0.5f,1.5f))
            .transform(pickaxeOnly())
            .tag(AllBlockTags.WRENCH_PICKUP)
            .item()
            .tag(darkMetalDecorTag)
            .build()
            .register();

    public static final BlockEntry<SlabBlock> DARK_METAL_BRICK_SLAB = REG.block("dark_metal_brick_slab", SlabBlock::new)
            .properties(p -> p.mapColor(MapColor.COLOR_BLACK).sound(SoundType.NETHERITE_BLOCK).strength(0.5f,1.5f))
            .transform(pickaxeOnly())
            .tag(stairsBlockTag)
            .tag(AllBlockTags.WRENCH_PICKUP)
            .item()
            .tag(slabsItemTag)
            .build()
            .register();

    public static final BlockEntry<StairBlock> DARK_METAL_BRICK_STAIRS = REG.block("dark_metal_brick_stairs", p -> new StairBlock(DARK_METAL_BLOCK.getDefaultState(), p))
            .properties(p -> p.mapColor(MapColor.COLOR_BLACK).sound(SoundType.NETHERITE_BLOCK).strength(0.5f,1.5f))
            .transform(pickaxeOnly())
            .tag(stairsBlockTag)
            .item()
            .tag(darkMetalDecorTag, stairsItemTag)
            .build()
            .register();

    public static final BlockEntry<FrontlightBlock> BRASS_FRONTLIGHT = REG.block("brass_frontlight", FrontlightBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.noOcclusion().sound(SoundType.NETHERITE_BLOCK).mapColor(MapColor.TERRACOTTA_YELLOW).lightLevel(FrontlightBlock::getLight))
            .transform(pickaxeOnly())
            .simpleItem()
            .register();

    @SuppressWarnings("all")
    public static final MetalTypeBlockList<FrontlightBlock> METAL_TYPE_FRONTLIGHTS = new MetalTypeBlockList<FrontlightBlock>(type -> {
        var metal = type.get();
        if (metal.equals(AllMetalTypes.BRASS)) return BRASS_FRONTLIGHT;
        if (!DnDecor.LOAD_ALL_METALS && metal.modIDs.equals(MaterialTypeProvider.NA)) return null;

        var builder = REG.block(metal.id + "_frontlight", FrontlightBlock::new)
                .initialProperties(SharedProperties::softMetal)
                .properties(p -> p.noOcclusion().sound(SoundType.COPPER_BULB).mapColor(metal.color).lightLevel(FrontlightBlock::getLight))
                .transform(pickaxeOnly());

        builder = builder;
        if (metal.equals(AllMetalTypes.NETHERITE)) builder = builder.item().properties(p -> p.fireResistant()).build();
        else builder = builder.simpleItem();
        return builder.register();
    });

    public static final MetalTypeBlockList<LampBlock> METAL_TYPE_LAMPS = new MetalTypeBlockList<LampBlock>(type -> {
        var metal = type.get();
        if (!DnDecor.LOAD_ALL_METALS && metal.modIDs.equals(MaterialTypeProvider.NA)) return null;
        var builder = REG.block(metal.id + "_lamp", LampBlock::new)
                .initialProperties(SharedProperties::softMetal)
                .properties(p -> p.sound(SoundType.COPPER_BULB).mapColor(metal.color).lightLevel(LampBlock::getLight))
                .transform(pickaxeOnly());

        builder = builder;
        if (metal.equals(AllMetalTypes.NETHERITE)) builder = builder.item().properties(Item.Properties::fireResistant).build();
        else builder = builder.simpleItem();
        return builder.register();
    });

    public static final BlockEntry<BoilerStructureBlock> BOILER_STRUCTURE = REG.block("boiler_structure", BoilerStructureBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .transform(pickaxeOnly())
            .register();

    public static final MetalTypeBlockList<BoilerBlock> METAL_TYPE_BOILERS = new MetalTypeBlockList<BoilerBlock>(type -> {
        var metal = type.get();
        if (!DnDecor.LOAD_ALL_METALS && metal.modIDs.equals(MaterialTypeProvider.NA)) return null;

        var builder = REG.block(metal.id + "_boiler", p -> new BoilerBlock(metal, p))
                .initialProperties(SharedProperties::softMetal)
                .properties(p -> p.sound(metal.sound).mapColor(metal.color))
                .transform(pickaxeOnly());

        builder = builder;
        if (metal.equals(AllMetalTypes.NETHERITE)) builder = builder.item().properties(Item.Properties::fireResistant).build();
        else builder = builder.simpleItem();
        return builder.register();
    });

    public static final CopperBlockSet COPPER_FLOORS = new CopperBlockSet(REG, "copper_floor");

    @SuppressWarnings("unchecked")
    public static final MetalTypeBlockList<Block> METAL_TYPE_FLOORS = new MetalTypeBlockList<Block>(type -> {
        var metal = type.get();
        if (!DnDecor.LOAD_ALL_METALS && metal.modIDs.equals(MaterialTypeProvider.NA)) return null;
        if (metal == AllMetalTypes.COPPER) return (BlockEntry<Block>) COPPER_FLOORS.getStandard();

        var builder = REG.block(metal.id + "_floor", Block::new)
                .initialProperties(SharedProperties::softMetal)
                .properties(p -> p.sound(metal.sound).mapColor(metal.color))
                .transform(pickaxeOnly());

        builder = builder;
        if (metal.equals(AllMetalTypes.NETHERITE)) builder = builder.item().properties(Item.Properties::fireResistant).build();
        else builder = builder.simpleItem();
        return builder.register();
    });

    public static final MetalTypeBlockList<CatwalkBlock> METAL_TYPE_CATWALKS = new MetalTypeBlockList<CatwalkBlock>(type -> {
        var metal = type.get();
        if (metal.requireMods()) return null;
        if (!DnDecor.LOAD_ALL_METALS && metal.modIDs.equals(MaterialTypeProvider.NA)) return null;

        var builder = REG.block(metal.id + "_catwalk", CatwalkBlock::new)
                .initialProperties(SharedProperties::softMetal)
                .properties(p -> p.noOcclusion().sound(SoundType.COPPER_GRATE).mapColor(metal.color))
                .transform(pickaxeOnly()).onRegister(connectedTextures(() -> new CatwalkCTBehaviour(
                        omni("catwalks/" + metal.id + "_catwalk"),
                        omni("catwalks/" + metal.id + "_catwalk_bottom"),
                        omni("catwalks/" + metal.id + "_catwalk_inner")))
                );
        builder = builder;
        if (metal.equals(AllMetalTypes.NETHERITE)) builder = builder.item().properties(Item.Properties::fireResistant).build();
        else builder = builder.simpleItem();
        return builder.register();
    });

    public static final MetalTypeBlockList<LargeChain> METAL_TYPE_LARGE_CHAINS = new MetalTypeBlockList<LargeChain>(type -> {
        var metal = type.get();
        if (metal.requireMods()) return null;
        if (!DnDecor.LOAD_ALL_METALS && metal.modIDs.equals(MaterialTypeProvider.NA)) return null;

        var builder = REG.block("large_" + metal.id + "_chain", LargeChain::new)
                .initialProperties(SharedProperties::softMetal)
                .properties(p -> p.sound(DnDecorSoundTypes.CHAIN_HEAVY).mapColor(metal.color))
                .transform(pickaxeOnly()).tag(AllBlockTags.BRITTLE, BlockTags.CLIMBABLE);
        builder = builder;
        if (metal.equals(AllMetalTypes.NETHERITE)) builder = builder.item().properties(Item.Properties::fireResistant).build();
        else builder = builder.simpleItem();
        return builder.register();
    });

    public static final MetalTypeBoltBlockList<?> METAL_TYPE_BOLTS = new MetalTypeBoltBlockList<>(type -> {
        var metal = type.get();
        if (!DnDecor.LOAD_ALL_METALS && metal.modIDs.equals(MaterialTypeProvider.NA)) return null;
        return new BoltEntry<>(metal);
    });

    private static CTSpriteShiftEntry horizontal(String blockTextureName, String connectedTextureName) {
        return getCT(AllCTTypes.HORIZONTAL, blockTextureName, connectedTextureName);
    }
    private static CTSpriteShiftEntry horizontalKryppers(String blockTextureName, String connectedTextureName) {
        return getCT(AllCTTypes.HORIZONTAL_KRYPPERS, blockTextureName, connectedTextureName);
    }
    private static CTSpriteShiftEntry vertical(String blockTextureName, String connectedTextureName) {
        return getCT(AllCTTypes.VERTICAL, blockTextureName, connectedTextureName);
    }
    private static CTSpriteShiftEntry rectangle(String blockTextureName, String connectedTextureName) {
        return getCT(AllCTTypes.RECTANGLE, blockTextureName, connectedTextureName);
    }
    private static CTSpriteShiftEntry cross(String blockTextureName, String connectedTextureName) {
        return getCT(AllCTTypes.CROSS, blockTextureName, connectedTextureName);
    }
    private static CTSpriteShiftEntry roof(String blockTextureName, String connectedTextureName) {
        return getCT(AllCTTypes.ROOF, blockTextureName, connectedTextureName);
    }
    private static CTSpriteShiftEntry roofStair(String blockTextureName, String connectedTextureName) {
        return getCT(AllCTTypes.ROOF, blockTextureName, connectedTextureName);
    }
    private static CTSpriteShiftEntry omni(String blockTextureName, String connectedTextureName) {
        return getCT(AllCTTypes.OMNIDIRECTIONAL, blockTextureName, connectedTextureName);
    }

    private static CTSpriteShiftEntry horizontal(String texture) {
        return getCT(AllCTTypes.HORIZONTAL, texture, texture);
    }
    private static CTSpriteShiftEntry horizontalKryppers(String texture) {
        return getCT(AllCTTypes.HORIZONTAL_KRYPPERS, texture, texture);
    }
    private static CTSpriteShiftEntry vertical(String texture) {
        return getCT(AllCTTypes.VERTICAL, texture, texture);
    }
    private static CTSpriteShiftEntry rectangle(String texture) {
        return getCT(AllCTTypes.RECTANGLE, texture, texture);
    }
    private static CTSpriteShiftEntry cross(String texture) {
        return getCT(AllCTTypes.CROSS, texture, texture);
    }
    private static CTSpriteShiftEntry roof(String texture) {
        return getCT(AllCTTypes.ROOF, texture, texture);
    }
    private static CTSpriteShiftEntry roofStair(String texture) {
        return getCT(AllCTTypes.ROOF, texture, texture);
    }
    private static CTSpriteShiftEntry omni(String texture) {
        return getCT(AllCTTypes.OMNIDIRECTIONAL, texture, texture);
    }

    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName, String connectedTextureName) {
        return CTSpriteShifter.getCT(type, DnDecor.loc("block/" + blockTextureName),
                DnDecor.loc("block/" + connectedTextureName + "_connected"));
    }

    protected static String getItemName(ItemLike pItemLike) {
        return BuiltInRegistries.ITEM.getKey(pItemLike.asItem()).getPath();
    }

    public static <T extends Block> Function<BlockState, ModelFile> getBlockModel(boolean customItem, DataGenContext<Block, T> c, RegistrateBlockstateProvider p) {
        return AssetLookup.partialBaseModel(c, p); // PORT-NOTE(26.1): datagen only; the shim has no standardModel
    }

    public static ArrayList<String> DD_DYES = null;

    public static boolean isDyeDepotColor(DyeColor color) {
        // PORT-NOTE(26.1): Dye Depot has no 26.1 Fabric build, so its extra colours are never present.
        return false;
    }

    public static RecipeOutput doesRequireDyeDepot(RegistrateRecipeProvider p, DyeColor dye) {
        var requireDyeDepot = DnDecorBlocks.isDyeDepotColor(dye);
        return p;
    }

    public static void register() {
    }
}