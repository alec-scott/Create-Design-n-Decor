package dev.lopyluna.dndecor;

import com.zurrtum.create.client.catnip.lang.FontHelper;
import com.zurrtum.create.client.foundation.item.ItemDescription;
import com.zurrtum.create.client.foundation.item.KineticStats;
import com.zurrtum.create.client.foundation.item.TooltipModifier;
import dev.lopyluna.dndecor.port.create.foundation.data.CreateRegistrate;
import dev.lopyluna.dndecor.port.registrate.util.entry.RegistryEntry;
import dev.lopyluna.dndecor.register.client.DnDecorPartialModels;
import dev.lopyluna.dndecor.register.client.DnDecorSpriteShifts;
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
