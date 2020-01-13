package com.fuzs.swordblockingcombat.handler;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class ConfigBuildHandler {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final GeneralConfig GENERAL_CONFIG = new GeneralConfig("general");

    public static class GeneralConfig {

        public final ForgeConfigSpec.ConfigValue<List<String>> exclude;
        public final ForgeConfigSpec.ConfigValue<List<String>> include;
        public final ForgeConfigSpec.DoubleValue blocked;
        public final ForgeConfigSpec.BooleanValue damageSword;

        private GeneralConfig(String name) {

            BUILDER.push(name);

            this.exclude = ConfigBuildHandler.BUILDER.comment("Swords to exclude").define("Exclude", Lists.newArrayList("examplemod:exampleitem"));
            this.include = ConfigBuildHandler.BUILDER.comment("Swords to include").define("Include", Lists.newArrayList("examplemod:exampleitem"));
            this.blocked = ConfigBuildHandler.BUILDER.comment("Percentage blocked").defineInRange("Blocked", 0.5, 0.0, 1.0);

            BUILDER.pop();

        }

    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

}