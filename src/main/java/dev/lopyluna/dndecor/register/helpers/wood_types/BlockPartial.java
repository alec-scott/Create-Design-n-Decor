package dev.lopyluna.dndecor.register.helpers.wood_types;

import dev.lopyluna.dndecor.port.create.foundation.data.CreateRegistrate;
import dev.lopyluna.dndecor.port.registrate.builders.BlockBuilder;
import dev.lopyluna.dndecor.port.registrate.builders.ItemBuilder;
import dev.lopyluna.dndecor.port.registrate.providers.DataGenContext;
import dev.lopyluna.dndecor.port.registrate.providers.RegistrateBlockstateProvider;
import dev.lopyluna.dndecor.port.registrate.providers.RegistrateRecipeProvider;
import dev.lopyluna.dndecor.port.registrate.util.DataIngredient;
import dev.lopyluna.dndecor.port.registrate.util.entry.BlockEntry;
import dev.lopyluna.dndecor.port.registrate.util.nullness.NonnullType;
import dev.lopyluna.dndecor.DnDecor;
import dev.lopyluna.dndecor.register.DnDecorStoneTypes;
import com.zurrtum.create.client.catnip.lang.Lang;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import dev.lopyluna.dndecor.port.porting_lib.models.generators.ModelFile;

import java.util.List;
import java.util.function.Supplier;

import static dev.lopyluna.dndecor.port.create.foundation.data.TagGen.pickaxeOnly;
import static dev.lopyluna.dndecor.register.DnDecorTags.ItemTags.PALETTE_BLOCKS;

@SuppressWarnings("all")
public abstract class BlockPartial<B extends Block> {

    public static final BlockPartial<StairBlock> STAIR = new Stairs();
    public static final BlockPartial<SlabBlock> SLAB = new Slab(false);
    public static final BlockPartial<SlabBlock> UNIQUE_SLAB = new Slab(true);
    public static final BlockPartial<WallBlock> WALL = new Wall();

    public static final BlockPartial<?>[] ALL_PARTIALS = { STAIR, SLAB, WALL };
    public static final BlockPartial<?>[] FOR_POLISHED = { STAIR, UNIQUE_SLAB, WALL };

    private final String name;

    private BlockPartial(String name) {
        this.name = name;
    }

    public @NonnullType BlockBuilder<B, CreateRegistrate> create(String variantName, BlockPattern pattern, BlockEntry<? extends Block> block, DnDecorStoneTypes variant) {
        String patternName = Lang.nonPluralId(pattern.createName(variantName));
        String blockName = patternName + "_" + this.name;
        BlockBuilder<B, CreateRegistrate> blockBuilder = DnDecor.REG
                .block(blockName, p -> createBlock(block, p))
                // PORT-NOTE(26.1): copy the parent's properties through Registrate, which sets the block id 26.1 requires
                .initialProperties(block)
                .transform(b -> transformBlock(b, variantName, pattern));
        ItemBuilder<BlockItem, BlockBuilder<B, CreateRegistrate>> itemBuilder = blockBuilder.item().transform(b -> transformItem(b, variantName, pattern));
        if (canRecycle()) itemBuilder.tag(variant.materialTag);
        itemBuilder.tag(PALETTE_BLOCKS.tag);
        return itemBuilder.build();
    }
    protected Identifier getTexture(String variantName, BlockPattern pattern, int index) {
        return BlockPattern.toLocation(variantName, pattern.getTexture(index));
    }
    protected BlockBuilder<B, CreateRegistrate> transformBlock(BlockBuilder<B, CreateRegistrate> builder, String variantName, BlockPattern pattern) {
        getBlockTags().forEach(builder::tag);
        return builder.transform(pickaxeOnly());
    }
    protected ItemBuilder<BlockItem, BlockBuilder<B, CreateRegistrate>> transformItem(ItemBuilder<BlockItem, BlockBuilder<B, CreateRegistrate>> builder, String variantName, BlockPattern pattern) {
        getItemTags().forEach(builder::tag);
        return builder;
    }
    protected boolean canRecycle() {
        return true;
    }
    protected abstract Iterable<TagKey<Block>> getBlockTags();
    protected abstract Iterable<TagKey<Item>> getItemTags();
    protected abstract B createBlock(Supplier<? extends Block> block, BlockBehaviour.Properties properties);
    protected abstract void createRecipes(DnDecorStoneTypes type, BlockEntry<? extends Block> patternBlock, DataGenContext<Block, ? extends Block> c, RegistrateRecipeProvider p);
    protected abstract void generateBlockState(DataGenContext<Block, B> ctx, RegistrateBlockstateProvider prov, String variantName, BlockPattern pattern, Supplier<? extends Block> block);

    private static class Stairs extends BlockPartial<StairBlock> {
        public Stairs() {
            super("stairs");
        }
        @Override
        protected StairBlock createBlock(Supplier<? extends Block> block, BlockBehaviour.Properties properties) {
            return new StairBlock(block.get().defaultBlockState(), properties);
        }
        @Override
        protected void generateBlockState(DataGenContext<Block, StairBlock> ctx, RegistrateBlockstateProvider prov, String variantName, BlockPattern pattern, Supplier<? extends Block> block) { /* PORT-NOTE(26.1): datagen only; the generated resources are committed */ }
        @Override
        protected Iterable<TagKey<Block>> getBlockTags() {
            return List.of(BlockTags.STAIRS);
        }
        @Override
        protected Iterable<TagKey<Item>> getItemTags() {
            return List.of(ItemTags.STAIRS);
        }
        @Override
        protected void createRecipes(DnDecorStoneTypes type, BlockEntry<? extends Block> patternBlock, DataGenContext<Block, ? extends Block> c, RegistrateRecipeProvider p) { /* PORT-NOTE(26.1): datagen only; the generated resources are committed */ }
    }
    private static class Slab extends BlockPartial<SlabBlock> {
        private final boolean customSide;
        public Slab(boolean customSide) {
            super("slab");
            this.customSide = customSide;
        }
        @Override
        protected SlabBlock createBlock(Supplier<? extends Block> block, BlockBehaviour.Properties properties) {
            return new SlabBlock(properties);
        }
        @Override
        protected boolean canRecycle() {
            return false;
        }
        @Override
        protected void generateBlockState(DataGenContext<Block, SlabBlock> ctx, RegistrateBlockstateProvider prov, String variantName, BlockPattern pattern, Supplier<? extends Block> block) { /* PORT-NOTE(26.1): datagen only; the generated resources are committed */ }
        @Override
        protected Iterable<TagKey<Block>> getBlockTags() {
            return List.of(BlockTags.SLABS);
        }
        @Override
        protected Iterable<TagKey<Item>> getItemTags() {
            return List.of(ItemTags.SLABS);
        }
        @Override
        protected void createRecipes(DnDecorStoneTypes type, BlockEntry<? extends Block> patternBlock, DataGenContext<Block, ? extends Block> c, RegistrateRecipeProvider p) { /* PORT-NOTE(26.1): datagen only; the generated resources are committed */ }
        @Override
        protected BlockBuilder<SlabBlock, CreateRegistrate> transformBlock(BlockBuilder<SlabBlock, CreateRegistrate> builder, String variantName, BlockPattern pattern) {
            return super.transformBlock(builder, variantName, pattern);
        }
    }

    private static class Wall extends BlockPartial<WallBlock> {
        public Wall() {
            super("wall");
        }
        @Override
        protected WallBlock createBlock(Supplier<? extends Block> block, BlockBehaviour.Properties properties) {
            return new WallBlock(properties.forceSolidOn());
        }
        @Override
        protected ItemBuilder<BlockItem, BlockBuilder<WallBlock, CreateRegistrate>> transformItem(
                ItemBuilder<BlockItem, BlockBuilder<WallBlock, CreateRegistrate>> builder, String variantName,
                BlockPattern pattern) {
            return super.transformItem(builder, variantName, pattern);
        }
        @Override
        protected void generateBlockState(DataGenContext<Block, WallBlock> ctx, RegistrateBlockstateProvider prov, String variantName, BlockPattern pattern, Supplier<? extends Block> block) { /* PORT-NOTE(26.1): datagen only; the generated resources are committed */ }
        @Override
        protected Iterable<TagKey<Block>> getBlockTags() {
            return List.of(BlockTags.WALLS);
        }
        @Override
        protected Iterable<TagKey<Item>> getItemTags() {
            return List.of(ItemTags.WALLS);
        }
        @Override
        protected void createRecipes(DnDecorStoneTypes type, BlockEntry<? extends Block> patternBlock, DataGenContext<Block, ? extends Block> c, RegistrateRecipeProvider p) { /* PORT-NOTE(26.1): datagen only; the generated resources are committed */ }
    }
}
