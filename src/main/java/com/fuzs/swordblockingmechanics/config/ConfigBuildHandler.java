package com.fuzs.swordblockingmechanics.config;

import com.google.common.collect.Lists;
import net.minecraft.client.settings.AttackIndicatorStatus;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class ConfigBuildHandler {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // sword blocking
    public static final ForgeConfigSpec.DoubleValue BLOCKED_AMOUNT;
    public static final ForgeConfigSpec.BooleanValue DAMAGE_SWORD;
    public static final ForgeConfigSpec.BooleanValue DEFLECT_PROJECTILES;
    public static final ForgeConfigSpec.DoubleValue WALKING_MODIFIER;
    public static final ForgeConfigSpec.IntValue PARRY_WINDOW;
    public static final ForgeConfigSpec.BooleanValue REQUIRE_BOTH_HANDS;
    // combat test
    public static final ForgeConfigSpec.EnumValue<AttackIndicatorStatus> SHIELD_INDICATOR;

    static {

        BUILDER.comment("Re-adds sword blocking in a very configurable way.");
        BUILDER.push("sword_blocking");
        BLOCKED_AMOUNT = ConfigBuildHandler.BUILDER.comment("Percentage an incoming attack will be reduced by when blocking.").defineInRange("Blocked Damage Ratio", 0.5, 0.0, 1.0);
        DAMAGE_SWORD = ConfigBuildHandler.BUILDER.comment("Damage sword when blocking an attack depending on the amount of damage blocked. Sword is only damaged when at least three damage points have been blocked, just like a shield.").define("Damage Sword", false);
        DEFLECT_PROJECTILES = ConfigBuildHandler.BUILDER.comment("Incoming projectiles such as arrows or tridents will ricochet while blocking.").define("Deflect Projectiles", false);
        WALKING_MODIFIER = ConfigBuildHandler.BUILDER.comment("Percentage to slow down movement to while blocking.").defineInRange("Walking Modifier", 0.2, 0.0, Integer.MAX_VALUE);
        PARRY_WINDOW = ConfigBuildHandler.BUILDER.comment("Amount of ticks after starting to block in which an attack will be completely nullified like when blocking with a shield.").defineInRange("Parry Window", 10, 0, 72000);
        REQUIRE_BOTH_HANDS = ConfigBuildHandler.BUILDER.comment("Blocking requires both hands, meaning the hand not holding the sword must be empty.").define("Require Two Hands", false);
        BUILDER.pop();
    }

}