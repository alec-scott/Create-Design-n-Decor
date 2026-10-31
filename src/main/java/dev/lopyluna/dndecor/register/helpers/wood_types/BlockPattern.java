package dev.lopyluna.dndecor.register.helpers.wood_types;

import com.zurrtum.create.content.decoration.palettes.ConnectedPillarBlock;
import com.zurrtum.create.client.foundation.block.connected.*;
import dev.lopyluna.dndecor.port.registrate.providers.DataGenContext;
import dev.lopyluna.dndecor.port.registrate.providers.RegistrateBlockstateProvider;
import dev.lopyluna.dndecor.port.registrate.providers.RegistrateRecipeProvider;
import dev.lopyluna.dndecor.port.registrate.util.nullness.NonNullBiConsumer;
import dev.lopyluna.dndecor.port.registrate.util.nullness.NonNullFunction;
import dev.lopyluna.dndecor.port.registrate.util.nullness.NonNullSupplier;
import dev.lopyluna.dndecor.DnDecor;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import dev.lopyluna.dndecor.port.porting_lib.models.generators.ConfiguredModel;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static dev.lopyluna.dndecor.register.helpers.wood_types.BlockPartial.ALL_PARTIALS;
import static dev.lopyluna.dndecor.register.helpers.wood_types.BlockPartial.FOR_POLISHED;

@SuppressWarnings("unused")
public class BlockPattern {
    public static final BlockPattern
            CUT = create("cut", PatternNameType.PREFIX, ALL_PARTIALS),
            BRICKS = create("cut_bricks", PatternNameType.WRAP, ALL_PARTIALS).textures("brick"),
            SMALL_BRICKS = create("small_bricks", PatternNameType.WRAP, ALL_PARTIALS).textures("small_brick"),
            POLISHED = create("polished_cut", PatternNameType.PREFIX, FOR_POLISHED).textures("polished", "slab"),
            LAYERED = create("layered", PatternNameType.PREFIX).blockStateFactory(p -> p::cubeColumn)
                    .textures("layered", "cap")
                    .connectedTextures(v -> new HorizontalCTBehaviour(ct(v, CTs.LAYERED), ct(v, CTs.CAP))),
            PILLAR = create("pillar", PatternNameType.SUFFIX).blockStateFactory(p -> p::pillar)
                    .block(ConnectedPillarBlock::new)
                    .textures("pillar", "cap")
                    .connectedTextures(v -> new RotatedPillarCTBehaviour(ct(v, CTs.PILLAR), ct(v, CTs.CAP)))
            ;

    public static final BlockPattern[] VANILLA_RANGE = { CUT, POLISHED, BRICKS, SMALL_BRICKS, LAYERED, PILLAR };
    public static final BlockPattern[] STANDARD_RANGE = { CUT, POLISHED, BRICKS, SMALL_BRICKS, LAYERED, PILLAR };

    static final String TEXTURE_LOCATION = "block/palettes/stone_types/%s/%s";

    private PatternNameType nameType;
    private String[] textures;
    private String id;
    private boolean isTranslucent;
    private TagKey<Block>[] blockTags;
    private TagKey<Item>[] itemTags;
    @SuppressWarnings("all")
    private Optional<Function<String, ConnectedTextureBehaviour>> ctFactory;

    private IPatternBlockStateGenerator blockStateGenerator;
    private NonNullFunction<BlockBehaviour.Properties, ? extends Block> blockFactory;
    private NonNullFunction<NonNullSupplier<Block>, NonNullBiConsumer<DataGenContext<Block, ? extends Block>, RegistrateRecipeProvider>> additionalRecipes;
    private BlockPartial<? extends Block>[] partials;


    private static BlockPattern create(String name, PatternNameType nameType,
                                              BlockPartial<?>... partials) {
        BlockPattern pattern = new BlockPattern();
        pattern.id = name;
        pattern.ctFactory = Optional.empty();
        pattern.nameType = nameType;
        pattern.partials = partials;
        pattern.additionalRecipes = $ -> NonNullBiConsumer.noop();
        pattern.isTranslucent = false;
        pattern.blockFactory = Block::new;
        pattern.textures = new String[] { name };
        pattern.blockStateGenerator = p -> p::cubeAll;
        return pattern;
    }

    @SuppressWarnings("all")
    public IPatternBlockStateGenerator getBlockStateGenerator() {
        return blockStateGenerator;
    }

    public boolean isTranslucent() {
        return isTranslucent;
    }

    public TagKey<Block>[] getBlockTags() {
        return blockTags;
    }

    public TagKey<Item>[] getItemTags() {
        return itemTags;
    }

    public NonNullFunction<BlockBehaviour.Properties, ? extends Block> getBlockFactory() {
        return blockFactory;
    }

    public BlockPartial<? extends Block>[] getPartials() {
        return partials;
    }

    public String getTexture(int index) {
        return textures[index];
    }

    public void addRecipes(NonNullSupplier<Block> baseBlock, DataGenContext<Block, ? extends Block> c, RegistrateRecipeProvider p) {
        additionalRecipes.apply(baseBlock).accept(c, p);
    }

    public Optional<Supplier<ConnectedTextureBehaviour>> createCTBehaviour(String variant) {
        return ctFactory.map(d -> () -> d.apply(variant));
    }

    private BlockPattern blockStateFactory(IPatternBlockStateGenerator factory) {
        blockStateGenerator = factory;
        return this;
    }

    private BlockPattern textures(String... textures) {
        this.textures = textures;
        return this;
    }

    private BlockPattern block(NonNullFunction<BlockBehaviour.Properties, ? extends Block> blockFactory) {
        this.blockFactory = blockFactory;
        return this;
    }

    private BlockPattern connectedTextures(Function<String, ConnectedTextureBehaviour> factory) {
        this.ctFactory = Optional.of(factory);
        return this;
    }

    @SuppressWarnings("all")
    public IBlockStateProvider cubeAll(String variant) {
        Identifier all = toLocation(variant, textures[0]);
        return (ctx, prov) -> {}; // PORT-NOTE(26.1): datagen only
    }

    @SuppressWarnings("all")
    public IBlockStateProvider cubeBottomTop(String variant) {
        Identifier side = toLocation(variant, textures[0]);
        Identifier bottom = toLocation(variant, textures[1]);
        Identifier top = toLocation(variant, textures[2]);
        return (ctx, prov) -> {}; // PORT-NOTE(26.1): datagen only
    }

    @SuppressWarnings("all")
    public IBlockStateProvider pillar(String variant) {
        Identifier side = toLocation(variant, textures[0]);
        Identifier end = toLocation(variant, textures[1]);
        return (ctx, prov) -> {}; // PORT-NOTE(26.1): datagen only
    }

    @SuppressWarnings("all")
    public IBlockStateProvider cubeColumn(String variant) {
        Identifier side = toLocation(variant, textures[0]);
        Identifier end = toLocation(variant, textures[1]);
        return (ctx, prov) -> {}; // PORT-NOTE(26.1): datagen only
    }
    protected String createName(String variant) {
        if (nameType == PatternNameType.WRAP) {
            String[] split = id.split("_");
            if (split.length == 2) {
                String formatString = "%s_%s_%s";
                return String.format(formatString, split[0], variant, split[1]);
            }
        }
        String formatString = "%s_%s";
        return nameType == PatternNameType.SUFFIX ? String.format(formatString, variant, id) : String.format(formatString, id, variant);
    }
    protected static Identifier toLocation(String variant, String texture) {
        return DnDecor.loc(String.format(TEXTURE_LOCATION, texture, variant + (texture.equals("cut") ? "_" : "_cut_") + texture));
    }
    protected static CTSpriteShiftEntry ct(String variant, CTs texture) {
        Identifier resLoc = texture.srcFactory.apply(variant);
        Identifier resLocTarget = texture.targetFactory.apply(variant);
        return CTSpriteShifter.getCT(texture.type, resLoc, Identifier.fromNamespaceAndPath(resLocTarget.getNamespace(), resLocTarget.getPath() + "_connected"));
    }
    @FunctionalInterface
    interface IPatternBlockStateGenerator extends Function<BlockPattern, Function<String, IBlockStateProvider>> { }
    @FunctionalInterface
    interface IBlockStateProvider extends NonNullBiConsumer<DataGenContext<Block, ? extends Block>, RegistrateBlockstateProvider> { }

    enum PatternNameType {
        PREFIX, SUFFIX, WRAP
    }
    public enum CTs {
        PILLAR(AllCTTypes.RECTANGLE, s -> toLocation(s, "pillar")),
        CAP(AllCTTypes.OMNIDIRECTIONAL, s -> toLocation(s, "cap")),
        LAYERED(AllCTTypes.HORIZONTAL_KRYPPERS, s -> toLocation(s, "layered"));
        public final CTType type;
        private final Function<String, Identifier> srcFactory;
        private final Function<String, Identifier> targetFactory;
        CTs(CTType type, Function<String, Identifier> factory) {
            this(type, factory, factory);
        }
        CTs(CTType type, Function<String, Identifier> srcFactory,
            Function<String, Identifier> targetFactory) {
            this.type = type;
            this.srcFactory = srcFactory;
            this.targetFactory = targetFactory;
        }
    }
}
