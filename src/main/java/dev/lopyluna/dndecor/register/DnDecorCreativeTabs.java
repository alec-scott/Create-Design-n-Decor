package dev.lopyluna.dndecor.register;

import dev.lopyluna.dndecor.port.registrate.util.entry.BlockEntry;
import dev.lopyluna.dndecor.register.helpers.list_providers.MaterialTypeProvider;
import net.minecraft.core.HolderLookup;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTabOutput;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import dev.lopyluna.dndecor.DnDecor;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static dev.lopyluna.dndecor.DnDecor.MOD_ID;

@SuppressWarnings("all")
public class DnDecorCreativeTabs {
    public static final ResourceKey<CreativeModeTab> BASE_CREATIVE_TAB = ResourceKey.create(Registries.CREATIVE_MODE_TAB, DnDecor.loc("base"));

    // PORT-NOTE(26.1): Fabric tab registration; tab ordering (was withTabsBefore Create's palettes tab) is Fabric's default
    public static void register() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, BASE_CREATIVE_TAB, FabricCreativeModeTab.builder()
                .title(DnDecorLangPartial.TITLE)
                .icon(DnDecorBlocks.BRASS_FRONTLIGHT::asStack)
                .displayItems((params, output) -> DnDecor.addCreative(output))
                .build());
        CreativeModeTabEvents.modifyOutputEvent(BASE_CREATIVE_TAB).register(DnDecorCreativeTabs::addCreative);
    }


    public static void addCreative(FabricCreativeModeTabOutput event) {
        var holders = event.getContext().holders();

        for (var supplier : MaterialTypeProvider.metalTypes) {
            var metal = supplier.get();
            if (tagHasAny(holders, metal.tag) || !metal.itemEntries.isEmpty()) continue;
            var bolts = DnDecorBlocks.METAL_TYPE_BOLTS.get(supplier);
            if (bolts != null) {
                removeEverywhere(event, bolts.CROSS);
                removeEverywhere(event, bolts.DASH);
                removeEverywhere(event, bolts.DOT);
                removeEverywhere(event, bolts.FLAT);
            }
            removeEverywhere(event, DnDecorBlocks.METAL_TYPE_FLOORS.get(supplier));
            removeEverywhere(event, DnDecorBlocks.METAL_TYPE_FRONTLIGHTS.get(supplier));
            //removeEverywhere(event, DnDecorBlocks.METAL_TYPE_LAMPS.get(supplier));
            removeEverywhere(event, DnDecorBlocks.METAL_TYPE_CATWALKS.get(supplier));
        }
    }


    private static void removeEverywhere(FabricCreativeModeTabOutput event, BlockEntry<? extends Block> like) {
        if (like == null) return;
        var item = like.asItem();
        event.getDisplayStacks().removeIf(s -> s.is(item));
        event.getSearchTabStacks().removeIf(s -> s.is(item));
    }

    private static boolean tagHasAny(HolderLookup.Provider holders, TagKey<Item> tag) {
        return holders.lookupOrThrow(Registries.ITEM).get(tag)
                .map(set -> set.iterator().hasNext())
                .orElse(false);
    }
}
