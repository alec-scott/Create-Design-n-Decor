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
        return switch (result) {
            case SUCCESS -> InteractionResult.SUCCESS;
            case CONSUME -> InteractionResult.CONSUME;
            case CONSUME_PARTIAL -> InteractionResult.CONSUME_PARTIAL;
            case SUCCESS_NO_ITEM_USED -> InteractionResult.TRY_WITH_EMPTY_HAND;
            case PASS -> InteractionResult.PASS;
            case FAIL -> InteractionResult.FAIL;
        };
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
