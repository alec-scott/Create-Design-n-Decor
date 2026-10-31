package dev.lopyluna.dndecor.port.create.foundation.block;

import dev.lopyluna.dndecor.port.create.foundation.data.CreateRegistrate;
import dev.lopyluna.dndecor.port.registrate.util.entry.BlockEntry;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.WeatheringCopperFullBlock;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static dev.lopyluna.dndecor.port.create.foundation.data.TagGen.pickaxeOnly;

/**
 * A reduced port of upstream Create's {@code CopperBlockSet}: full-block variants only, the only kind D&D uses.
 *
 * <p>PORT-NOTE(26.1): Create Fly has no CopperBlockSet. Oxidation and waxing go through Fabric's
 * {@link OxidizableBlocksRegistry} in {@link #registerAll()}, called after registration. Models, loot and recipes are
 * the committed generated resources.</p>
 */
public class CopperBlockSet {
    private static final List<CopperBlockSet> ALL = new ArrayList<>();

    private final Map<WeatherState, BlockEntry<Block>> unwaxed = new EnumMap<>(WeatherState.class);
    private final Map<WeatherState, BlockEntry<Block>> waxed = new EnumMap<>(WeatherState.class);

    public CopperBlockSet(CreateRegistrate registrate, String name) {
        for (WeatherState state : WeatherState.values()) {
            String prefix = state == WeatherState.UNAFFECTED ? "" : state.name().toLowerCase(Locale.ROOT) + "_";
            unwaxed.put(state, registrate.block(prefix + name, p -> (Block) new WeatheringCopperFullBlock(state, p))
                    .initialProperties(() -> Blocks.COPPER_BLOCK)
                    .transform(pickaxeOnly())
                    .simpleItem()
                    .register());
            waxed.put(state, registrate.block("waxed_" + prefix + name, Block::new)
                    .initialProperties(() -> Blocks.COPPER_BLOCK)
                    .transform(pickaxeOnly())
                    .simpleItem()
                    .register());
        }
        ALL.add(this);
    }

    public BlockEntry<Block> getStandard() {
        return unwaxed.get(WeatherState.UNAFFECTED);
    }

    public BlockEntry<Block> get(WeatherState state, boolean isWaxed) {
        return (isWaxed ? waxed : unwaxed).get(state);
    }

    public static void registerAll() {
        WeatherState[] states = WeatherState.values();
        for (CopperBlockSet set : ALL) {
            for (int i = 0; i < states.length; i++) {
                if (i + 1 < states.length)
                    OxidizableBlocksRegistry.registerNextStage(set.unwaxed.get(states[i]).get(), set.unwaxed.get(states[i + 1]).get());
                OxidizableBlocksRegistry.registerWaxable(set.unwaxed.get(states[i]).get(), set.waxed.get(states[i]).get());
            }
        }
    }
}
