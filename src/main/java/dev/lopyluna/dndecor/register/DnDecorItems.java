package dev.lopyluna.dndecor.register;

import com.zurrtum.create.AllItems;
import dev.lopyluna.dndecor.port.registrate.util.entry.ItemEntry;
import dev.lopyluna.dndecor.DnDecor;
import dev.lopyluna.dndecor.content.items.FullBeltConnectorItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.level.ItemLike;

import static dev.lopyluna.dndecor.port.registrate.providers.RegistrateRecipeProvider.has;
import static dev.lopyluna.dndecor.DnDecor.REG;


@SuppressWarnings("unused")
public class DnDecorItems {

    public static final ItemEntry<FullBeltConnectorItem> BELT_CONNECTOR = REG.item("belt_connector", FullBeltConnectorItem::new)
            .lang("Mechanical Belt (Full)").register();

    protected static String getItemName(ItemLike pItemLike) {
        return BuiltInRegistries.ITEM.getKey(pItemLike.asItem()).getPath();
    }

    public static void register() {}
}
