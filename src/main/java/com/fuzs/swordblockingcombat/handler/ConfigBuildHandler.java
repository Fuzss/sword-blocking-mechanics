package com.fuzs.swordblockingcombat.handler;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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
        //public final ForgeConfigSpec.IntValue damageAmount;
        public final ForgeConfigSpec.IntValue blockDelay;
        public final ForgeConfigSpec.EnumValue<FoodTicker> foodTicker;
        public final ForgeConfigSpec.BooleanValue noProjectileResistance;

        private GeneralConfig(String name) {

            BUILDER.push(name);

            this.exclude = ConfigBuildHandler.BUILDER.comment("Swords to exclude from blocking. Intended for modded swords that already have a right-click function. Requires a restart to apply.").define("Exclusion List", Lists.newArrayList("examplemod:exampleitem"));
            this.include = ConfigBuildHandler.BUILDER.comment("Items to include for blocking. Intended for modded swords that don't extend vanilla swords. Requires a restart to apply.").define("Inclusion List", Lists.newArrayList("examplemod:exampleitem"));
            this.blocked = ConfigBuildHandler.BUILDER.comment("Percentage an incoming attack will be reduced by when blocking.").defineInRange("Blocked Damage", 0.5, 0.0, 1.0);
            this.damageSword = ConfigBuildHandler.BUILDER.comment("Damage sword when blocking an attack depending on the amount of damage blocked. Sword is only damaged when at least three damage points have been blocked, just like a shield.").define("Damage Sword", false);
            //this.damageAmount = ConfigBuildHandler.BUILDER.comment("Amount a sword will be damaged by after blocking.").defineInRange("Damage Amount", 1, 0, Integer.MAX_VALUE);
            this.blockDelay = ConfigBuildHandler.BUILDER.comment("Amount of ticks after which blocking is effective.").defineInRange("Warm-Up Delay", 0, 0, Integer.MAX_VALUE);
            this.foodTicker = ConfigBuildHandler.BUILDER.comment("Changes the way the player heals from food. \"CLASSIC\" option restores the pre combat update system, \"COMBAT\" option introduces the changes from current combat snapshots.").defineEnum("Food Ticker", FoodTicker.CLASSIC);
            this.noProjectileResistance = ConfigBuildHandler.BUILDER.comment("Disables the default 0.5 second damage immunity when hit by a projectile. This makes it possible for entities to be hit by multiple projectiles at once, e. g. from a multishot enchanted crossbow.").define("No Projectile Immunity", true);

            BUILDER.pop();

        }

    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    @SuppressWarnings("unused")
    public enum FoodTicker {
        DEFAULT, CLASSIC, COMBAT;
    }

}