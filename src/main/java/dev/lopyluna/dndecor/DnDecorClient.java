package dev.lopyluna.dndecor;

import com.zurrtum.create.client.catnip.lang.FontHelper;
import com.zurrtum.create.client.foundation.item.ItemDescription;
import com.zurrtum.create.client.foundation.item.KineticStats;
import com.zurrtum.create.client.foundation.item.TooltipModifier;
import dev.lopyluna.dndecor.port.create.foundation.data.CreateRegistrate;
import dev.lopyluna.dndecor.port.registrate.util.entry.RegistryEntry;
import dev.lopyluna.dndecor.register.client.DnDecorPartialModels;
import dev.lopyluna.dndecor.register.client.DnDecorSpriteShifts;
import com.zurrtum.create.client.AllExtensions;
import com.zurrtum.create.content.kinetics.belt.BeltBlock;
import com.zurrtum.create.content.kinetics.belt.BeltBlockEntity;
import dev.lopyluna.dndecor.register.DnDecorBlocks;
import net.minecraft.core.BlockPos;
import java.util.HashSet;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

public class DnDecorClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        DnDecorPartialModels.init();
        DnDecorSpriteShifts.init();
        CreateRegistrate.registerBlockModels();
        registerTooltips();
        registerMultiPosDestruction();
    }

    // PORT-NOTE(26.1): was the blocks' NeoForge client extensions (RenderProperties); Create Fly keeps these in AllExtensions
    private static void registerMultiPosDestruction() {
        AllExtensions.MULTI_POS.put(DnDecorBlocks.BOILER_STRUCTURE.get(), (level, pos, state, progress) -> {
            var struc = DnDecorBlocks.BOILER_STRUCTURE.get();
            if (!struc.stillValid(level, pos, state, false)) return null;
            HashSet<BlockPos> set = new HashSet<>(struc.getAttachedPositions(level, pos, state));
            set.remove(pos);
            return set;
        });
        AllExtensions.MULTI_POS.put(DnDecorBlocks.BELT.get(), (level, pos, state, progress) -> {
            if (level.getBlockEntity(pos) instanceof BeltBlockEntity belt) return new HashSet<>(BeltBlock.getBeltChain(level, belt.getController()));
            return null;
        });
    }

    // PORT-NOTE(26.1): was REG.setTooltipModifierFactory in common code; Create Fly's tooltip classes are client only
    private static void registerTooltips() {
        for (RegistryEntry<Item, Item> entry : DnDecor.REG.<Item, Item>getAll(Registries.ITEM)) {
            Item item = entry.get();
            if (TooltipModifier.REGISTRY.get(item) != null) continue;
            TooltipModifier.REGISTRY.register(item, new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                    .andThen(TooltipModifier.mapNull(KineticStats.create(item))));
        }
    }
}
