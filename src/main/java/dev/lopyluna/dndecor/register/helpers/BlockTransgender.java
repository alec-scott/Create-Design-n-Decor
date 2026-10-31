package dev.lopyluna.dndecor.register.helpers;

import com.zurrtum.create.AllBlockTags;
import com.zurrtum.create.Create;
import com.zurrtum.create.client.content.decoration.encasing.EncasedCTBehaviour;
import com.zurrtum.create.content.decoration.palettes.ConnectedGlassBlock;
import com.zurrtum.create.content.decoration.palettes.ConnectedGlassPaneBlock;
import com.zurrtum.create.content.decoration.palettes.GlassPaneBlock;
import com.zurrtum.create.content.decoration.palettes.WindowBlock;
import com.zurrtum.create.client.foundation.block.connected.CTSpriteShiftEntry;
import com.zurrtum.create.client.foundation.block.connected.ConnectedTextureBehaviour;
import com.zurrtum.create.client.foundation.block.connected.GlassPaneCTBehaviour;
import com.zurrtum.create.client.foundation.block.connected.HorizontalCTBehaviour;
import dev.lopyluna.dndecor.port.create.foundation.data.AssetLookup;
import dev.lopyluna.dndecor.port.create.foundation.data.BlockStateGen;
import dev.lopyluna.dndecor.port.create.foundation.data.CreateRegistrate;
import dev.lopyluna.dndecor.port.create.foundation.data.SharedProperties;
import dev.lopyluna.dndecor.port.registrate.builders.BlockBuilder;
import dev.lopyluna.dndecor.port.registrate.builders.ItemBuilder;
import dev.lopyluna.dndecor.port.registrate.providers.DataGenContext;
import dev.lopyluna.dndecor.port.registrate.providers.RegistrateBlockstateProvider;
import dev.lopyluna.dndecor.port.registrate.providers.RegistrateRecipeProvider;
import dev.lopyluna.dndecor.port.registrate.providers.loot.RegistrateBlockLootTables;
import dev.lopyluna.dndecor.port.registrate.util.DataIngredient;
import dev.lopyluna.dndecor.port.registrate.util.entry.BlockEntry;
import dev.lopyluna.dndecor.port.registrate.util.nullness.NonNullBiConsumer;
import dev.lopyluna.dndecor.port.registrate.util.nullness.NonNullConsumer;
import dev.lopyluna.dndecor.port.registrate.util.nullness.NonNullFunction;
import dev.lopyluna.dndecor.port.registrate.util.nullness.NonNullUnaryOperator;
import dev.lopyluna.dndecor.DnDecor;
import dev.lopyluna.dndecor.DnDecorUtils;
import dev.lopyluna.dndecor.content.blocks.OrnateGrateBlock;
import dev.lopyluna.dndecor.content.blocks.VelvetBlock;
import dev.lopyluna.dndecor.register.DnDecorBlocks;
import dev.lopyluna.dndecor.register.DnDecorTags;
import net.minecraft.core.BlockPos;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import dev.lopyluna.dndecor.port.porting_lib.models.generators.ModelFile;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;

import java.util.function.Function;
import java.util.function.Supplier;

import static dev.lopyluna.dndecor.port.create.foundation.data.CreateRegistrate.casingConnectivity;
import static dev.lopyluna.dndecor.port.create.foundation.data.CreateRegistrate.connectedTextures;
import static dev.lopyluna.dndecor.port.registrate.providers.RegistrateRecipeProvider.has;
import static dev.lopyluna.dndecor.DnDecor.REG;

@SuppressWarnings({"removal", "unused"})
public class BlockTransgender {

    public static BlockEntry<VelvetBlock> velvetBlock(String colorId, MapColor mapColor, DyeColor dye) {
        return REG.block(colorId + "_velvet_block", p -> new VelvetBlock(p, dye))
                .initialProperties(() -> Blocks.WHITE_WOOL)
                .properties(p -> p.mapColor(mapColor)).properties(p -> p.sound(SoundType.WOOL))
                .properties(p -> p.strength(0.5f,1.5f))
                .tag(AllBlockTags.WINDMILL_SAILS).asOptional()
                .item()
                .tag(DnDecorTags.modItemTag("dyed_velvet_block")).asOptional()
                .build()
                .register();
    }

    public static <B extends Block> NonNullUnaryOperator<BlockBuilder<B, CreateRegistrate>> connected(
            Supplier<CTSpriteShiftEntry> ct) {
        return b -> b.initialProperties(SharedProperties::stone)
                .onRegister(connectedTextures(() -> new EncasedCTBehaviour(ct.get())))
                .onRegister(casingConnectivity((block, cc) -> cc.makeCasing(block, ct.get())));
    }

    public static <B extends Block> NonNullUnaryOperator<BlockBuilder<B, CreateRegistrate>> layeredConnected(
            Supplier<CTSpriteShiftEntry> ct, Supplier<CTSpriteShiftEntry> ct2) {
        return b -> b.initialProperties(SharedProperties::stone)
                .onRegister(connectedTextures(() -> new HorizontalCTBehaviour(ct.get(), ct2.get())))
                .onRegister(casingConnectivity((block, cc) -> cc.makeCasing(block, ct.get())));
    }

    public static <B extends OrnateGrateBlock> NonNullUnaryOperator<BlockBuilder<B, CreateRegistrate>> ornateConnected(
            Supplier<CTSpriteShiftEntry> ct) {
        return b -> b.initialProperties(SharedProperties::stone)
                .onRegister(connectedTextures(() -> new EncasedCTBehaviour(ct.get())))
                .onRegister(casingConnectivity((block, cc) -> cc.makeCasing(block, ct.get())));
    }

    public static <B extends Block> NonNullUnaryOperator<BlockBuilder<B, CreateRegistrate>> block(Supplier<CTSpriteShiftEntry> ct ) {
        return b -> b.initialProperties(SharedProperties::stone)
                .onRegister(connectedTextures(() -> new EncasedCTBehaviour(ct.get())))
                .onRegister(casingConnectivity((block, cc) -> cc.makeCasing
                        (block, ct.get())))
                .item()
                .build();
    }

    public static <B extends Block> NonNullUnaryOperator<BlockBuilder<B, CreateRegistrate>> blockV2( Supplier<CTSpriteShiftEntry> ct, Supplier<CTSpriteShiftEntry> ct2 ) {
        return b -> b.initialProperties(SharedProperties::stone)
                .onRegister(connectedTextures(() -> new HorizontalCTBehaviour(ct.get(), ct2.get())))
                .onRegister(casingConnectivity((block, cc) -> cc.makeCasing(block, ct.get())))
                .item()
                .build();
    }

    public static BlockBuilder<WindowBlock, CreateRegistrate> customWindowBlock(String name, Supplier<CTSpriteShiftEntry> ct,
                                                            Supplier<Supplier<?>> renderType, boolean translucent, Supplier<MapColor> color) {
        NonNullFunction<String, Identifier> end_texture = n -> DnDecor.loc(palettesDir() + name + "_end");
        NonNullFunction<String, Identifier> side_texture = n -> DnDecor.loc(palettesDir() + n);
        return windowBlock(name, ct, null, renderType, translucent, end_texture, side_texture, color);
    }

    public static BlockBuilder<WindowBlock, CreateRegistrate> customWindowBlock(String name, Supplier<CTSpriteShiftEntry> ct, Supplier<CTSpriteShiftEntry> ct2,
                    Supplier<Supplier<?>> renderType, boolean translucent, Supplier<MapColor> color) {
        NonNullFunction<String, Identifier> end_texture = n -> DnDecor.loc(palettesDir() + name + "_end");
        NonNullFunction<String, Identifier> side_texture = n -> DnDecor.loc(palettesDir() + n);
        return windowBlock(name, ct, ct2, renderType, translucent, end_texture, side_texture, color);
    }

    public static BlockBuilder<WindowBlock, CreateRegistrate> windowBlock(String name,
                    Supplier<CTSpriteShiftEntry> ct, Supplier<CTSpriteShiftEntry> ct2, Supplier<Supplier<?>> renderType, boolean translucent,
                    NonNullFunction<String, Identifier> endTexture, NonNullFunction<String, Identifier> sideTexture, Supplier<MapColor> color) {
        return REG.block(name, p -> new WindowBlock(p, translucent))
                .onRegister(ct == null ? $ -> {
                } : connectedTextures(() -> ct2 != null ? new HorizontalCTBehaviour(ct.get(), ct2.get()) : new HorizontalCTBehaviour(ct.get())))
                .initialProperties(() -> Blocks.GLASS)
                .properties(BlockTransgender::glassProperties)
                .properties(p -> p.mapColor(color.get()))
                .tag(BlockTags.IMPERMEABLE)
                .simpleItem();
    }

    public static BlockEntry<ConnectedGlassBlock> framedGlass(String name, Supplier<ConnectedTextureBehaviour> behaviour) {
        return REG.block(name, ConnectedGlassBlock::new)
                .onRegister(connectedTextures(behaviour))
                .initialProperties(() -> Blocks.GLASS)
                .properties(BlockTransgender::glassProperties)
                .tag(net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags.GLASS_BLOCKS_COLORLESS, BlockTags.IMPERMEABLE)
                .item()
                .tag(net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags.GLASS_BLOCKS_COLORLESS)
                .build()
                .register();
    }

    public static BlockBuilder<ConnectedGlassPaneBlock, CreateRegistrate> customWindowPane(String name, Supplier<? extends Block> parent, Supplier<CTSpriteShiftEntry> ctshift, Supplier<Supplier<?>> renderType) {
        var topTexture = DnDecor.loc(palettesDir() + name + "_pane_top");
        var sideTexture = DnDecor.loc(palettesDir() + name);
        return connectedGlassPane(name, parent, ctshift, sideTexture, sideTexture, topTexture, renderType, false);
    }

    public static BlockEntry<ConnectedGlassPaneBlock> framedGlassPane(String name, Supplier<? extends Block> parent, Supplier<CTSpriteShiftEntry> ctshift) {
        var sideTexture = DnDecor.loc(palettesDir() + name);
        var itemSideTexture = DnDecor.loc(palettesDir() + name);
        var topTexture = DnDecor.loc(palettesDir() + name + "_pane_top");
        Supplier<Supplier<?>> renderType = () -> () -> null;
        return connectedGlassPane(name, parent, ctshift, sideTexture, itemSideTexture, topTexture, renderType, true).register();
    }

    private static BlockBuilder<ConnectedGlassPaneBlock, CreateRegistrate> connectedGlassPane(String name, Supplier<? extends Block> parent, Supplier<CTSpriteShiftEntry> ctshift,
        Identifier sideTexture, Identifier itemSideTexture, Identifier topTexture, Supplier<Supplier<?>> renderType, boolean colorless) {
        NonNullConsumer<? super ConnectedGlassPaneBlock> connectedTextures = ctshift == null ? $ -> {
        } : connectedTextures(() -> new GlassPaneCTBehaviour(ctshift.get()));
        String CGPparents = "block/connected_glass_pane/";
        String prefix = name + "_pane_";

        Function<RegistrateBlockstateProvider, ModelFile> post =
                getPaneModelProvider(CGPparents, prefix, "post", sideTexture, topTexture),
                side = getPaneModelProvider(CGPparents, prefix, "side", sideTexture, topTexture),
                sideAlt = getPaneModelProvider(CGPparents, prefix, "side_alt", sideTexture, topTexture),
                noSide = getPaneModelProvider(CGPparents, prefix, "noside", sideTexture, topTexture),
                noSideAlt = getPaneModelProvider(CGPparents, prefix, "noside_alt", sideTexture, topTexture);

        NonNullBiConsumer<DataGenContext<Block, ConnectedGlassPaneBlock>, RegistrateBlockstateProvider> stateProvider =
                (c, p) -> {}; // PORT-NOTE(26.1): datagen only

        return glassPane(name, parent, itemSideTexture, topTexture, ConnectedGlassPaneBlock::new, renderType, connectedTextures, stateProvider, colorless);
    }

    private static Function<RegistrateBlockstateProvider, ModelFile> getPaneModelProvider(String CGPparents, String prefix, String partial, Identifier sideTexture, Identifier topTexture) {
        return p -> p.models().withExistingParent(prefix + partial, Identifier.fromNamespaceAndPath("create", CGPparents + partial))
                .texture("pane", sideTexture).texture("edge", topTexture);
    }

    private static <G extends GlassPaneBlock> BlockBuilder<G, CreateRegistrate> glassPane(String name, Supplier<? extends Block> parent, Identifier sideTexture,
        Identifier topTexture, NonNullFunction<BlockBehaviour.Properties, G> factory, Supplier<Supplier<?>> renderType,
        NonNullConsumer<? super G> connectedTextures, NonNullBiConsumer<DataGenContext<Block, G>, RegistrateBlockstateProvider> stateProvider, boolean colorless) {
        name += "_pane";
        ItemBuilder<BlockItem, BlockBuilder<G, CreateRegistrate>> itemBuilder = REG.block(name, factory)
                .onRegister(connectedTextures)
                .initialProperties(() -> Blocks.GLASS_PANE)
                .properties(p -> p.mapColor(parent.get().defaultMapColor()))
                .item();
        if (colorless) itemBuilder.tag(ConventionalItemTags.GLASS_PANES, ConventionalItemTags.GLASS_PANES_COLORLESS);
        else itemBuilder.tag(ConventionalItemTags.GLASS_PANES);
        BlockBuilder<G, CreateRegistrate> blockBuilder = itemBuilder.build();
        if (colorless) blockBuilder.tag(ConventionalBlockTags.GLASS_PANES, ConventionalBlockTags.GLASS_PANES_COLORLESS);
        else blockBuilder.tag(ConventionalBlockTags.GLASS_PANES);
        return blockBuilder;
    }

    private static BlockBehaviour.Properties glassProperties(BlockBehaviour.Properties p) {
        return p.isValidSpawn(BlockTransgender::never)
                .isRedstoneConductor(BlockTransgender::never)
                .isSuffocating(BlockTransgender::never)
                .isViewBlocking(BlockTransgender::never);
    }

    private static boolean never(BlockState state, BlockGetter getter, BlockPos pos) {
        return false;
    }
    private static Boolean never(BlockState state, BlockGetter getter, BlockPos pos, EntityType<?> type) {
        return false;
    }

    private static String palettesDir() {
        return "block/palettes/";
    }
}
