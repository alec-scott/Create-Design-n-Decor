package dev.lopyluna.dndecor.events;

import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.content.kinetics.belt.BeltBlock;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.CachedInventoryBehaviour;
import dev.lopyluna.dndecor.register.DnDecorBETypes;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

// PORT-NOTE(26.1): was a NeoForge RegisterCapabilitiesEvent listener; Create Fly exposes inventories through Fabric's
// transfer API with a cached behaviour (as AllTransfer does for Create's own belt and millstone). The dyed container
// is a block-level ItemInventoryProvider instead.
public class DnDecorCommon {

    public static void registerCapabilities() {
        registerItemSide(DnDecorBETypes.BELT.get(), be -> {
            if (!BeltBlock.canTransportObjects(be.getBlockState())) return null;
            if (!be.isRemoved() && be.itemHandler == null) be.initializeItemHandler();
            return be.itemHandler;
        });

        for (var entry : DnDecorBETypes.STONE_TYPE_MILLSTONES.toArray()) {
            var type = entry.get();
            // Create's own andesite millstone is already registered by Create Fly
            if (type == com.zurrtum.create.AllBlockEntityTypes.MILLSTONE) continue;
            registerItemSide(type, be -> be.capability);
        }
    }

    private static <T extends SmartBlockEntity> void registerItemSide(BlockEntityType<T> type, Function<T, @Nullable Container> factory) {
        BlockEntityBehaviour.add(type, (T be) -> new CachedInventoryBehaviour<>(be, factory));
        ItemStorage.SIDED.registerForBlockEntity(CachedInventoryBehaviour::get, type);
    }
}
