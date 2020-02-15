package com.fuzs.swordblockingcombat.config;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class ConfigBuildHandler {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // sword blocking
    static final ForgeConfigSpec.DoubleValue BLOCKED;
    static final ForgeConfigSpec.BooleanValue DAMAGE_SWORD;
    static final ForgeConfigSpec.IntValue DAMAGE_AMOUNT;
    static final ForgeConfigSpec.IntValue BLOCK_DELAY;
    static final ForgeConfigSpec.ConfigValue<List<String>> EXCLUDE;
    static final ForgeConfigSpec.ConfigValue<List<String>> INCLUDE;
    // classic combat
    static final ForgeConfigSpec.BooleanValue SWEEPING_REQUIRED;
    static final ForgeConfigSpec.BooleanValue NO_SWEEPING_SMOKE;
    // material changer
    static final ForgeConfigSpec.ConfigValue<List<String>> ATTACK_DAMAGE;
    static final ForgeConfigSpec.ConfigValue<List<String>> ATTACK_SPEED;
    static final ForgeConfigSpec.ConfigValue<List<String>> MAX_STACK_SIZE;
    static final ForgeConfigSpec.ConfigValue<List<String>> TOOL_DURABILITY;
    // modern combat
    static final ForgeConfigSpec.BooleanValue NO_PROJECTILE_RESISTANCE;
    static final ForgeConfigSpec.ConfigValue<List<String>> ITEM_DELAY;
    // food buffs
    static final ForgeConfigSpec.EnumValue<ConfigValueHolder.FoodBuffs.FoodTicker> FOOD_TICKER;
    static final ForgeConfigSpec.IntValue REGEN_DELAY;
    static final ForgeConfigSpec.IntValue REGEN_THRESHOLD;
    static final ForgeConfigSpec.BooleanValue DRAIN_FOOD;

    static {

        BUILDER.push("sword_blocking");
        BLOCKED = ConfigBuildHandler.BUILDER.comment("Percentage an incoming attack will be reduced by when blocking.").defineInRange("Blocked Damage", 0.5, 0.0, 1.0);
        DAMAGE_SWORD = ConfigBuildHandler.BUILDER.comment("Damage sword when blocking an attack depending on the amount of damage blocked. Sword is only damaged when at least three damage points have been blocked, just like a shield.").define("Damage Sword", false);
        DAMAGE_AMOUNT = ConfigBuildHandler.BUILDER.comment("Amount a sword will be damaged by after blocking.").defineInRange("Damage Amount", 1, 0, Integer.MAX_VALUE);
        BLOCK_DELAY = ConfigBuildHandler.BUILDER.comment("Amount of ticks after which blocking is effective.").defineInRange("Warm-Up Delay", 0, 0, Integer.MAX_VALUE);
        EXCLUDE = ConfigBuildHandler.BUILDER.comment("Swords to exclude from blocking. Intended for modded swords that already have a right-click function. Requires a restart to apply.").define("Exclusion List", Lists.newArrayList("examplemod:exampleitem"));
        INCLUDE = ConfigBuildHandler.BUILDER.comment("Items to include for blocking. Intended for modded swords that don't extend vanilla swords. Requires a restart to apply.").define("Inclusion List", Lists.newArrayList("examplemod:exampleitem"));
        BUILDER.pop();

        BUILDER.push("classic_combat");
        SWEEPING_REQUIRED = ConfigBuildHandler.BUILDER.comment("Is the sweeping edge enchantment required to perform a sweep attack.").define("Require Sweeping Edge", true);
        NO_SWEEPING_SMOKE = ConfigBuildHandler.BUILDER.comment("Prevent particles created by a sweep attack from appearing.").define("No Sweeping Particles", true);
        BUILDER.pop();

        BUILDER.push("material_changer");
        ATTACK_DAMAGE = ConfigBuildHandler.BUILDER.comment("Specify a value ADDED to the attack damage attribute for any item. Format for every entry is \"<namespace>:<id>,<value>\".").define("Attack Damage List", Lists.newArrayList("minecraft:diamond_sword,3.0"));
        ATTACK_SPEED = ConfigBuildHandler.BUILDER.comment("Specify a value ADDED to the attack speed attribute for any item. Not compatible with \"classic_combat\" features. Format for every entry is \"<namespace>:<id>,<value>\".").define("Attack Speed List", Lists.newArrayList());
        MAX_STACK_SIZE = ConfigBuildHandler.BUILDER.comment("Specify the max stack size for any item. Value has to be between 0 and 64. Format for every entry is \"<namespace>:<id>,<value>\".").define("Max Stack Size List", Lists.newArrayList("minecraft:snowball,64", "minecraft:ender_eye,16"));
        TOOL_DURABILITY = ConfigBuildHandler.BUILDER.comment("Change the durability for any tool. Setting it to 0 will make the tool unbreakable. Format for every entry is \"<namespace>:<id>,<value>\".").define("Tool Durability List", Lists.newArrayList());
        BUILDER.pop();

        BUILDER.push("modern_combat");
        NO_PROJECTILE_RESISTANCE = ConfigBuildHandler.BUILDER.comment("Disables the default 0.5 second damage immunity when hit by a projectile. This makes it possible for entities to be hit by multiple projectiles at once, e. g. from a multishot enchanted crossbow.").define("No Projectile Immunity", true);
        ITEM_DELAY = ConfigBuildHandler.BUILDER.comment("Items to add a delay in ticks to after using. Doesn't override a delay an item has by default.").define("Item Delay List", Lists.newArrayList("minecraft:snowball,4", "minecraft:egg,4"));
        BUILDER.pop();

        BUILDER.push("food_buffs");
        FOOD_TICKER = ConfigBuildHandler.BUILDER.comment("Changes the way the player heals from food. \"CLASSIC\" option restores the pre combat update system, \"COMBAT\" option introduces the changes from current combat snapshots.").defineEnum("Food Ticker", ConfigValueHolder.FoodBuffs.FoodTicker.CLASSIC);
        REGEN_DELAY = ConfigBuildHandler.BUILDER.comment("Amount of ticks between regenerating when enough food is present. Only applies when \"Food Ticker\" is set to \"CUSTOM\"").defineInRange("Regeneration Delay", 80, 0, Integer.MAX_VALUE);
        REGEN_THRESHOLD = ConfigBuildHandler.BUILDER.comment("Food level required to be able to regenerate health. Only applies when \"Food Ticker\" is set to \"CUSTOM\"").defineInRange("Regeneration Food Level", 18, 0, 20);
        DRAIN_FOOD = ConfigBuildHandler.BUILDER.comment("Drain food instead of saturation when regenerating. Only applies when \"Food Ticker\" is set to \"CUSTOM\"").define("Regenerate From Food", false);
        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

}