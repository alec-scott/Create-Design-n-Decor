package dev.lopyluna.dndecor;

import com.zurrtum.create.client.foundation.utility.DyeHelper;
import dev.lopyluna.dndecor.register.DnDecorBlocks;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

@SuppressWarnings("unused")
public class DnDecorUtils {

    public static InteractionResult itemResult(InteractionResult result) {
        // PORT-NOTE(26.1): ItemInteractionResult was folded into InteractionResult; kept for callers
        return result;
    }

    public static ItemLike getWool(DyeColor color) {
        var depot = DnDecorBlocks.isDyeDepotColor(color);
        return DyeHelper.getWoolOfDye(color);
    }

    public static boolean randomChance(int chance, Level level) {
        int newChance = Mth.clamp(chance, 0, 100);
        return newChance != 0 && level.getRandom().nextInt(1,  100) <= newChance;
    }

    public static boolean randomChance(double chance, Level level) {
        int newChance = Mth.clamp(((int) chance * 100), 0, 100);
        return newChance != 0 && level.getRandom().nextInt(1,  100) <= newChance;
    }
}
