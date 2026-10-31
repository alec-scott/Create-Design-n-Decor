package dev.lopyluna.dndecor.register.helpers;

import com.mojang.datafixers.util.Pair;
import dev.lopyluna.dndecor.DnDecor;
import dev.lopyluna.dndecor.register.DnDecorBETypes;
import dev.lopyluna.dndecor.register.DnDecorBlocks;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;

import java.util.*;
import java.util.function.Function;

@SuppressWarnings({"unchecked", "unused"})
public class DnDecorRemapHelper { //container
    private static final List<ResourceKey<Registry<?>>> BLOCK_ITEM = asRegistryList(Registries.BLOCK, Registries.ITEM);
    private static final List<ResourceKey<Registry<?>>> BE_BLOCK_ITEM = asRegistryList(Registries.BLOCK_ENTITY_TYPE, Registries.BLOCK, Registries.ITEM);
    private static final List<ResourceKey<Registry<?>>> BE_TYPE = asRegistryList(Registries.BLOCK_ENTITY_TYPE);

    private static final Map<String, Pair<List<ResourceKey<Registry<?>>>, Function<Registry<?>, Identifier>>> reMap = new HashMap<>();

    static {
        reMap.put("colored_storage_container", Pair.of(BE_BLOCK_ITEM,
                r -> r.key() == Registries.BLOCK_ENTITY_TYPE ? DnDecorBETypes.DYED_CONTAINER.getId() : DnDecorBlocks.CONTAINER.getId()));
        reMap.put("deepslate_tiles", Pair.of(BLOCK_ITEM, r -> DnDecorBlocks.DEEPSLATE_TILES.getId()));
        reMap.put("red_deepslate_tiles", Pair.of(BLOCK_ITEM, r -> DnDecorBlocks.DYED_DEEPSLATE_TILES.get(DyeColor.RED).getId()));
    }


    // PORT-NOTE(26.1): was a NeoForge RegisterEvent listener; vanilla registries take aliases directly. Call after
    // registration.
    public static void remap() {
        for (var registry : List.of(BuiltInRegistries.BLOCK, BuiltInRegistries.ITEM, BuiltInRegistries.BLOCK_ENTITY_TYPE)) {
            reMap.forEach((key, pair) -> {
                if (pair.getFirst().contains(registry.key())) ((MappedRegistry<?>) registry).addAlias(DnDecor.loc(key), pair.getSecond().apply(registry));
            });
        }
    }

    @SafeVarargs
    private static List<ResourceKey<Registry<?>>> asRegistryList(ResourceKey<? extends Registry<?>>... keys) {
        var list = new ArrayList<ResourceKey<Registry<?>>>();
        for (var key : keys) list.add((ResourceKey<Registry<?>>) key);
        return list;
    }
}
