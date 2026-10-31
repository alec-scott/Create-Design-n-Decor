package dev.lopyluna.dndecor;

import com.mojang.logging.LogUtils;
import com.zurrtum.create.content.kinetics.base.IRotate;
import dev.lopyluna.dndecor.port.create.foundation.data.CreateRegistrate;
import dev.lopyluna.dndecor.port.registrate.util.entry.RegistryEntry;
import dev.lopyluna.dndecor.register.*;
import dev.lopyluna.dndecor.register.helpers.DnDecorRemapHelper;
import dev.lopyluna.dndecor.events.DnDecorCommon;
import dev.lopyluna.dndecor.events.DnDecorServer;
import com.zurrtum.create.client.catnip.lang.LangBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class DnDecor implements ModInitializer {
    public static final String NAME = "Design n' Decor";
    public static final String MOD_ID = "dndecor";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static boolean DYE_DEPOT;
    public static final boolean LOAD_ALL_METALS = true;

    public static final CreateRegistrate REG = CreateRegistrate.create(MOD_ID)
            .defaultCreativeTab((ResourceKey<CreativeModeTab>) null);
    // PORT-NOTE(26.1): the tooltip modifier factory moved to DnDecorClient (Create Fly's tooltip classes are client only)

    @Override
    public void onInitialize() {

        AllMetalTypes.register();
        DYE_DEPOT = FabricLoader.getInstance().isModLoaded("dye_depot");

        DnDecorStoneTypes.register(REG);
        DnDecorLangPartial.init();
        DnDecorTags.init();
        DnDecorItems.register();
        DnDecorBlocks.register();
        DnDecorBETypes.register();
        DnDecorCreativeTabs.register();
        DnDecorMountedStorageTypes.register();

        DnDecorConfigs.register();

        REG.register();
        dev.lopyluna.dndecor.port.create.CreateTransforms.apply();
        DnDecorRemapHelper.remap();
        dev.lopyluna.dndecor.port.create.foundation.block.CopperBlockSet.registerAll();
        DnDecorCommon.registerCapabilities();
        DnDecorServer.register();
        DnDecorInventoryIdentifiers.registerDefaults();
    }

    // PORT-NOTE(26.1): the base tab's contents, filled from its displayItems generator (was a tab contents event)
    public static void addCreative(CreativeModeTab.Output event) {
        List<ItemStack> stacks = new ArrayList<>();
            for (RegistryEntry<Item, Item> entry : REG.getAll(Registries.ITEM)) {
                Item item = entry.get();
                if (item instanceof BlockItem) continue;
                if (item instanceof BucketItem) continue;
                var stack = item.getDefaultInstance();
                if (stack.isEmpty()) continue;
                if (stack.is(DnDecorTags.ItemTags.PALETTE_BLOCKS.tag)) {
                    stacks.add(stack);
                    continue;
                }
                event.accept(stack);
            }
            for (RegistryEntry<Block, Block> entry : REG.getAll(Registries.BLOCK)) {
                var block = entry.get();
                var item = block.asItem();
                var stack = item.getDefaultInstance();
                if (stack.isEmpty()) continue;
                if (stack.is(DnDecorTags.ItemTags.PALETTE_BLOCKS.tag)) {
                    stacks.add(stack);
                    continue;
                }
                event.accept(stack);
            }
            for (var stack : stacks) event.accept(stack);
    }

    public static LangBuilder lang() {
        return new LangBuilder(MOD_ID);
    }

    public static Identifier loc(String loc) {
        return Identifier.fromNamespaceAndPath(MOD_ID, loc);
    }
    public static Identifier mcLoc(String loc) {
        return Identifier.withDefaultNamespace(loc);
    }
    public static Identifier emptyLoc() {
        return Identifier.fromNamespaceAndPath(MOD_ID, "empty");
    }

}
