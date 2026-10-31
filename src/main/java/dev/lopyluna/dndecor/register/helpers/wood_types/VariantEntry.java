package dev.lopyluna.dndecor.register.helpers.wood_types;

import com.google.common.collect.ImmutableList;
import dev.lopyluna.dndecor.port.create.foundation.data.CreateRegistrate;
import dev.lopyluna.dndecor.port.registrate.builders.BlockBuilder;
import dev.lopyluna.dndecor.port.registrate.builders.ItemBuilder;
import dev.lopyluna.dndecor.port.registrate.providers.ProviderType;
import dev.lopyluna.dndecor.port.registrate.util.DataIngredient;
import dev.lopyluna.dndecor.port.registrate.util.entry.BlockEntry;
import dev.lopyluna.dndecor.port.registrate.util.nullness.NonNullSupplier;
import dev.lopyluna.dndecor.register.DnDecorStoneTypes;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static dev.lopyluna.dndecor.port.create.foundation.data.CreateRegistrate.connectedTextures;
import static dev.lopyluna.dndecor.port.create.foundation.data.TagGen.pickaxeOnly;
import static dev.lopyluna.dndecor.DnDecor.REG;
import static dev.lopyluna.dndecor.register.DnDecorTags.ItemTags.PALETTE_BLOCKS;

@SuppressWarnings("removal")
public class VariantEntry {

    public final ImmutableList<BlockEntry<? extends Block>> registeredBlocks;
    public final ImmutableList<BlockEntry<? extends Block>> registeredPartials;

    public VariantEntry(String name, DnDecorStoneTypes paletteStoneVariants) {
        ImmutableList.Builder<BlockEntry<? extends Block>> registeredBlocks = ImmutableList.builder();
        ImmutableList.Builder<BlockEntry<? extends Block>> registeredPartials = ImmutableList.builder();
        NonNullSupplier<Block> baseBlock = paletteStoneVariants.baseBlock;

        for (BlockPattern pattern : paletteStoneVariants.variantTypes) {
            BlockBuilder<? extends Block, CreateRegistrate> builder =
                    REG.block(pattern.createName(name), pattern.getBlockFactory())
                            .initialProperties(baseBlock)
                            .transform(pickaxeOnly());

            ItemBuilder<BlockItem, ? extends BlockBuilder<? extends Block, CreateRegistrate>> itemBuilder = builder.item();

            TagKey<Block>[] blockTags = pattern.getBlockTags();
            if (blockTags != null) builder.tag(blockTags);
            TagKey<Item>[] itemTags = pattern.getItemTags();
            if (itemTags != null) itemBuilder.tag(itemTags);

            itemBuilder.tag(paletteStoneVariants.materialTag, PALETTE_BLOCKS.tag);

            pattern.createCTBehaviour(name).ifPresent(b -> builder.onRegister(connectedTextures(b)));


            itemBuilder.register();
            BlockEntry<? extends Block> block = builder.register();
            registeredBlocks.add(block);

            for (BlockPartial<? extends Block> partialBlock : pattern.getPartials()) registeredPartials.add(partialBlock.create(name, pattern, block, paletteStoneVariants).register());
        }

        REG.addDataGenerator(ProviderType.RECIPE, p -> p.stonecutting(DataIngredient.tag(paletteStoneVariants.materialTag), RecipeCategory.BUILDING_BLOCKS, baseBlock));
        REG.addDataGenerator(ProviderType.ITEM_TAGS, p -> {
            var block = baseBlock.get().asItem();
            p.addTag(paletteStoneVariants.materialTag).add(block);
            p.addTag(PALETTE_BLOCKS.tag).add(block);
        });

        this.registeredBlocks = registeredBlocks.build();
        this.registeredPartials = registeredPartials.build();
    }
}
