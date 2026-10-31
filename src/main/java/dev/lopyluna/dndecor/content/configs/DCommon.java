package dev.lopyluna.dndecor.content.configs;

import dev.lopyluna.dndecor.port.catnip.config.ConfigBase;

@SuppressWarnings("NullableProblems")
public class DCommon extends ConfigBase {
    public final ConfigGroup common = group(0,
            "common", "Configs for the General Game");

    @Override
    public String getName() {
        return "common";
    }
}
