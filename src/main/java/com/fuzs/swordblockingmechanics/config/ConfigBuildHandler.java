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
    public static final ForgeConfigSpec.BooleanValue THROWABLES_DELAY;
    public static final ForgeConfigSpec.BooleanValue EATING_INTERRUPTION;
    public static final ForgeConfigSpec.BooleanValue SHIELD_KNOCKBACK;
    public static final ForgeConfigSpec.BooleanValue SHIELD_DELAY;
    public static final ForgeConfigSpec.BooleanValue PASS_THROUGH_THROWABLES;
    public static final ForgeConfigSpec.BooleanValue FAST_SWITCHING;
    public static final ForgeConfigSpec.EnumValue<AttackIndicatorStatus> SHIELD_INDICATOR;
    public static final ForgeConfigSpec.BooleanValue FAST_DRINKING;
    public static final ForgeConfigSpec.ConfigValue<List<String>> HIDE_OFFHAND;

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

        BUILDER.comment("Introduces various tweaks from Combat Test Snapshots and other popular combat suggestions.");
        BUILDER.push("combat_test");
        THROWABLES_DELAY = ConfigBuildHandler.BUILDER.comment("Add a delay of 4 ticks between throwing snowballs or eggs, just like with ender pearls.").define("Throwables Delay", true);
        EATING_INTERRUPTION = ConfigBuildHandler.BUILDER.comment("Eating and drinking both are interrupted if the player receives damage.").define("Eating Interruption", true);
        SHIELD_KNOCKBACK = ConfigBuildHandler.BUILDER.comment("Fix a vanilla bug (MC-147694) which prevents attackers from receiving knockback when their attack is blocked.").define("Shield Knockback", true);
        SHIELD_DELAY = ConfigBuildHandler.BUILDER.comment("Skip the 5 tick activation cooldown when using a shield.").define("Remove Shield Warm-Up Delay", true);
        PASS_THROUGH_THROWABLES = ConfigBuildHandler.BUILDER.comment("Throwables such as snowballs, eggs and ender pearls pass through blocks without a collision shape like grass and flowers.").define("Pass-Through Throwables", true);
        FAST_SWITCHING = ConfigBuildHandler.BUILDER.comment("The attack timer is unaffected by switching items.").define("Fast Tool Switching", true);
        SHIELD_INDICATOR = ConfigBuildHandler.BUILDER.comment("Show a shield indicator similar to the attack indicator when actively blocking.").defineEnum("Shield Indicator", AttackIndicatorStatus.CROSSHAIR);
        FAST_DRINKING = ConfigBuildHandler.BUILDER.comment("It only takes 20 ticks to drink liquid items instead of 32 or 40.").define("Fast Drinking", true);
        HIDE_OFFHAND = ConfigBuildHandler.BUILDER.comment("Specify items to not be rendered when held in the offhand.", "Format for every entry is \"<namespace>:<path>\". Path may use single asterisk as wildcard parameter.").define("Hide Offhand", Lists.newArrayList("minecraft:totem_of_undying"));
        BUILDER.pop();
    }

}