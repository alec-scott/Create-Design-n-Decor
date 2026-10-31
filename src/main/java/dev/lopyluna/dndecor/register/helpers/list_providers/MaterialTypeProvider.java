package dev.lopyluna.dndecor.register.helpers.list_providers;

import com.zurrtum.create.AllBlocks;
import net.minecraft.world.level.block.Blocks;
import dev.lopyluna.dndecor.port.registrate.util.DataIngredient;
import dev.lopyluna.dndecor.port.registrate.util.entry.ItemProviderEntry;
import dev.lopyluna.dndecor.port.registrate.util.nullness.NonNullSupplier;
import dev.lopyluna.dndecor.register.DnDecorStoneTypes;
import com.zurrtum.create.client.catnip.lang.Lang;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

import java.util.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class MaterialTypeProvider {

    public static Map<NonNullSupplier<Block>, String> stoneTypesRegister = new HashMap<>();
    public static List<NonNullSupplier<Block>> stoneTypes = new ArrayList<>();

    public static List<NonNullSupplier<MetalType>> metalTypes = new ArrayList<>();

    // PORT-NOTE(26.1): upstream Create's AllPaletteStoneTypes (name -> base block), in its order; Create Fly registers
    // the palette blocks directly in AllBlocks
    private static final Map<String, NonNullSupplier<Block>> CREATE_PALETTE_STONES = new LinkedHashMap<>();
    static {
        CREATE_PALETTE_STONES.put("granite", () -> Blocks.GRANITE);
        CREATE_PALETTE_STONES.put("diorite", () -> Blocks.DIORITE);
        CREATE_PALETTE_STONES.put("andesite", () -> Blocks.ANDESITE);
        CREATE_PALETTE_STONES.put("calcite", () -> Blocks.CALCITE);
        CREATE_PALETTE_STONES.put("dripstone", () -> Blocks.DRIPSTONE_BLOCK);
        CREATE_PALETTE_STONES.put("deepslate", () -> Blocks.DEEPSLATE);
        CREATE_PALETTE_STONES.put("tuff", () -> Blocks.TUFF);
        CREATE_PALETTE_STONES.put("asurine", () -> AllBlocks.ASURINE);
        CREATE_PALETTE_STONES.put("crimsite", () -> AllBlocks.CRIMSITE);
        CREATE_PALETTE_STONES.put("limestone", () -> AllBlocks.LIMESTONE);
        CREATE_PALETTE_STONES.put("ochrum", () -> AllBlocks.OCHRUM);
        CREATE_PALETTE_STONES.put("scoria", () -> AllBlocks.SCORIA);
        CREATE_PALETTE_STONES.put("scorchia", () -> AllBlocks.SCORCHIA);
        CREATE_PALETTE_STONES.put("veridium", () -> AllBlocks.VERIDIUM);
    }

    public static void checkStoneTypeList() {
        for (var stone : DnDecorStoneTypes.values()) {
            if (stone == null) continue;
            if (stone.baseBlock == null) continue;
            if (!MaterialTypeProvider.stoneTypes.contains(stone.baseBlock)) {
                MaterialTypeProvider.stoneTypesRegister.putIfAbsent(stone.baseBlock, Lang.asId(stone.name()).replace("_block", ""));
                MaterialTypeProvider.stoneTypes.add(stone.baseBlock);
            }
        }
        for (var stone : CREATE_PALETTE_STONES.entrySet()) {
            if (!MaterialTypeProvider.stoneTypes.contains(stone.getValue())) {
                MaterialTypeProvider.stoneTypesRegister.putIfAbsent(stone.getValue(), stone.getKey());
                MaterialTypeProvider.stoneTypes.add(stone.getValue());
            }
        }
    }

    public static Optional<NonNullSupplier<MetalType>> getMetalTypeByID(String id) {
        for (var type : metalTypes) if (type.get().id.equals(id)) return Optional.of(type);
        return Optional.empty();
    }

    public static Item getResolvedItem(Identifier loc) {
        var item = BuiltInRegistries.ITEM.getValue(loc);
        if (item == Items.AIR) System.out.println("⚠ Item not yet registered: " + loc + " is " + item);
        return item;
    }

    public static List<String> NA = new ArrayList<>(List.of("NA"));

    public static class MetalType {
        public final String id;
        public final String type;
        public final SoundType sound;
        public final Supplier<Block> block;
        public final MapColor color;
        public final TagKey<Item> tag;

        public final List<Object> itemEntries = new ArrayList<>();
        public final List<String> modIDs;

        public MetalType(String type, SoundType sound, Supplier<Block> block, MapColor color, TagKey<Item> tag) {
            this(type, sound, block, color, null, tag, new ArrayList<>());
        }

        public MetalType(String type, SoundType sound, Supplier<Block> block, MapColor color, TagKey<Item> tag, List<String> modIDs) {
            this(type, sound, block, color, null, tag, modIDs);
        }

        public MetalType(String type, SoundType sound, Supplier<Block> block, MapColor color, NonNullSupplier<ItemLike> item, TagKey<Item> tag) {
            this(type, sound, block, color, item != null ? List.of(item) : List.of(), tag, new ArrayList<>());
        }

        public MetalType(String type, SoundType sound, Supplier<Block> block, MapColor color, ItemProviderEntry<?, ?> item, TagKey<Item> tag) {
            this(type, sound, block, color, item != null ? List.of(item) : List.of(), tag, new ArrayList<>());
        }

        public MetalType(String type, SoundType sound, Supplier<Block> block, MapColor color, NonNullSupplier<ItemLike> item) {
            this(type, sound, block, color, item != null ? List.of(item) : List.of(), null, new ArrayList<>());
        }

        public MetalType(String type, SoundType sound, Supplier<Block> block, MapColor color, ItemProviderEntry<?, ?> item) {
            this(type, sound, block, color, item != null ? List.of(item) : List.of(), null, new ArrayList<>());
        }

        public MetalType(String type, SoundType sound, Supplier<Block> block, MapColor color, List<Object> itemEntries, TagKey<Item> tag, List<String> modIDs) {
            this.id = type.toLowerCase(Locale.ROOT).replace(" ", "_");
            this.type = type;
            this.sound = sound;
            this.block = block;
            this.color = color;
            this.tag = tag;
            this.modIDs = modIDs;
            if (itemEntries != null) this.itemEntries.addAll(itemEntries);
            if (!this.modIDs.contains("NA")) metalTypes.add(() -> this);
        }

        public boolean requireMods() {
            return !this.modIDs.isEmpty();
        }

        public MetalType entries(Object... entries) {
            this.itemEntries.addAll(List.of(entries));
            return this;
        }

        public DataIngredient getIngredient() {
            if (tag != null) return DataIngredient.tag(tag);

            List<ItemStack> stacks = new ArrayList<>();
            for (var entry : itemEntries) {
                var itemLike = resolveItemLike(entry);
                if (itemLike == null || itemLike.asItem() == Items.AIR) continue;
                ItemStack stack = itemLike.asItem().getDefaultInstance();
                if (!stack.isEmpty()) stacks.add(stack);
            }

            if (!stacks.isEmpty()) return DataIngredient.ingredient(Ingredient.of(stacks.stream().map(ItemStack::getItem)), stacks.getFirst().getItem());
            return null;
        }

        private ItemLike resolveItemLike(Object entry) {
            if (entry instanceof ItemProviderEntry<?,?> provider) return provider.get();
            if (entry instanceof Supplier<?> supplier) return supplier.get() instanceof ItemLike il ? il : null;
            return null;
        }
    }
}
