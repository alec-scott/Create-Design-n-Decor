package dev.lopyluna.dndecor.register;

import com.zurrtum.create.api.stress.BlockStressValues;
import dev.lopyluna.dndecor.content.configs.DClient;
import dev.lopyluna.dndecor.content.configs.DCommon;
import dev.lopyluna.dndecor.content.configs.DServer;
import dev.lopyluna.dndecor.content.configs.server.kinetics.DStress;
import dev.lopyluna.dndecor.port.catnip.config.ConfigBase;
import net.neoforged.fml.config.ModConfig;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.v5.ModConfigEvents;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

import static dev.lopyluna.dndecor.DnDecor.MOD_ID;

@SuppressWarnings("unused")
public class DnDecorConfigs {

    private static final Map<ModConfig.Type, ConfigBase> CONFIGS = new EnumMap<>(ModConfig.Type.class);

    private static DClient client;
    private static DCommon common;
    private static DServer server;

    public static DClient client() { return client; }
    public static DCommon common() { return common; }
    public static DServer server() { return server; }

    public static ConfigBase byType(ModConfig.Type type) {
        return CONFIGS.get(type);
    }

    private static <T extends ConfigBase> T register(Supplier<T> factory, ModConfig.Type side) {
        Pair<T, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(builder -> {
            T config = factory.get();
            config.registerAll(builder);
            return config;
        });

        T config = specPair.getLeft();
        config.specification = specPair.getRight();
        CONFIGS.put(side, config);
        return config;
    }

    public static void register() {
        client = register(DClient::new, ModConfig.Type.CLIENT);
        common = register(DCommon::new, ModConfig.Type.COMMON);
        server = register(DServer::new, ModConfig.Type.SERVER);

        for (Map.Entry<ModConfig.Type, ConfigBase> pair : CONFIGS.entrySet()) ConfigRegistry.INSTANCE.register(MOD_ID, pair.getKey(), pair.getValue().specification);
        ModConfigEvents.loading(MOD_ID).register(DnDecorConfigs::onLoad);
        ModConfigEvents.reloading(MOD_ID).register(DnDecorConfigs::onReload);

        DStress stress = server().kinetics.stressValues;
        BlockStressValues.IMPACTS.registerProvider(stress::getImpact);
        BlockStressValues.CAPACITIES.registerProvider(stress::getCapacity);
    }

    public static void onLoad(ModConfig modConfig) {
        for (ConfigBase config : CONFIGS.values()) if (config.specification == modConfig.getSpec()) config.onLoad();
    }

    public static void onReload(ModConfig modConfig) {
        for (ConfigBase config : CONFIGS.values()) if (config.specification == modConfig.getSpec()) config.onReload();
    }

}
