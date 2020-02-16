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
    static final ForgeConfigSpec.BooleanValue DEFLECT_PROJECTILES;
    static final ForgeConfigSpec.IntValue BLOCK_DELAY;
    static final ForgeConfigSpec.ConfigValue<List<String>> EXCLUDE;
    static final ForgeConfigSpec.ConfigValue<List<String>> INCLUDE;
    // classic combat
    static final ForgeConfigSpec.BooleanValue REMOVE_ATTACK_COOLDOWN;
    static final ForgeConfigSpec.BooleanValue BOOST_SHARPNESS;
    static final ForgeConfigSpec.BooleanValue SPRINT_WHILE_ATTACKING;
    // material changer
    static final ForgeConfigSpec.ConfigValue<List<String>> ATTACK_DAMAGE;
    static final ForgeConfigSpec.ConfigValue<List<String>> ATTACK_SPEED;
    static final ForgeConfigSpec.ConfigValue<List<String>> MAX_STACK_SIZE;
    static final ForgeConfigSpec.ConfigValue<List<String>> TOOL_DURABILITY;
    // modern combat
    static final ForgeConfigSpec.BooleanValue NO_PROJECTILE_RESISTANCE;
    static final ForgeConfigSpec.BooleanValue NO_AXE_ATTACK_PENALTY;
    static final ForgeConfigSpec.ConfigValue<List<String>> ITEM_DELAY;
    static final ForgeConfigSpec.BooleanValue SWEEPING_REQUIRED;
    static final ForgeConfigSpec.BooleanValue NO_SWEEPING_SMOKE;
    static final ForgeConfigSpec.IntValue SHIELD_DELAY;
    static final ForgeConfigSpec.BooleanValue BOOST_IMPALING;
    static final ForgeConfigSpec.BooleanValue DISPENSE_TRIDENT;
    // food buffs
    static final ForgeConfigSpec.EnumValue<ConfigValueHolder.FoodBuffs.FoodTicker> FOOD_TICKER;
    static final ForgeConfigSpec.IntValue REGEN_DELAY;
    static final ForgeConfigSpec.IntValue REGEN_THRESHOLD;
    static final ForgeConfigSpec.BooleanValue DRAIN_FOOD;
    static final ForgeConfigSpec.IntValue EATING_SPEED;
    // enchantment enhancements

    static {

        BUILDER.comment("Re-adds sword blocking in a very configurable way.");
        BUILDER.push("sword_blocking");
        BLOCKED = ConfigBuildHandler.BUILDER.comment("Percentage an incoming attack will be reduced by when blocking.").defineInRange("Blocked Damage", 0.5, 0.0, 1.0);
        DAMAGE_SWORD = ConfigBuildHandler.BUILDER.comment("Damage sword when blocking an attack depending on the amount of damage blocked. Sword is only damaged when at least three damage points have been blocked, just like a shield.").define("Damage Sword", false);
        DEFLECT_PROJECTILES = ConfigBuildHandler.BUILDER.comment("Incoming projectiles such as arrows or tridents will ricochet while blocking.").define("Deflect Projectiles", false);
        BLOCK_DELAY = ConfigBuildHandler.BUILDER.comment("Amount of ticks after which blocking using a sword is effective.").defineInRange("Warm-Up Delay", 0, 0, 72000);
        EXCLUDE = ConfigBuildHandler.BUILDER.comment("Swords to exclude from blocking. Intended for modded swords that already have their own right-click function. Format for every entry is \"<namespace>:<id>\".").define("Blocking Exclusion List", Lists.newArrayList());
        INCLUDE = ConfigBuildHandler.BUILDER.comment("Items to include for blocking. Intended for modded swords that don't extend vanilla swords. Format for every entry is \"<namespace>:<id>\".").define("Blocking Inclusion List", Lists.newArrayList());
        BUILDER.pop();

        BUILDER.comment("Restores pre-combat update combat mechanics.");
        BUILDER.push("classic_combat");
        REMOVE_ATTACK_COOLDOWN = ConfigBuildHandler.BUILDER.comment("Completely remove the attack cooldown as if it never even existed in the first place.").define("Remove Attack Cooldown", true);
        BOOST_SHARPNESS = ConfigBuildHandler.BUILDER.comment("Boost sharpness enchantment to add +1.0 attack damage per level instead of +0.5 damage.").define("Boost Sharpness", true);
        SPRINT_WHILE_ATTACKING = ConfigBuildHandler.BUILDER.comment("Don't automatically stop sprinting when attacking.").define("Sprint While Attacking", true);
        BUILDER.pop();

        BUILDER.comment("Allows changing various stats of items, mainly for restoring pre-combat update values.");
        BUILDER.push("material_changer");
        ATTACK_DAMAGE = ConfigBuildHandler.BUILDER.comment("Specify a value ADDED to the attack damage attribute for any item. Format for every entry is \"<namespace>:<id>,<value>\".").define("Attack Damage List", Lists.newArrayList("minecraft:diamond_sword,1.0", "minecraft:diamond_axe,-2.0", "minecraft:diamond_pickaxe,1.0", "minecraft:diamond_shovel,-0.5", "minecraft:diamond_hoe,3.0", "minecraft:iron_sword,1.0", "minecraft:iron_axe,-2.0", "minecraft:iron_pickaxe,1.0", "minecraft:iron_shovel,-0.5", "minecraft:iron_hoe,2.0", "minecraft:stone_sword,1.0", "minecraft:stone_axe,-2.0", "minecraft:stone_pickaxe,1.0", "minecraft:stone_shovel,-0.5", "minecraft:stone_hoe,1.0", "minecraft:wooden_sword,1.0", "minecraft:wooden_axe,-2.0", "minecraft:wooden_pickaxe,1.0", "minecraft:wooden_shovel,-0.5", "minecraft:golden_sword,1.0", "minecraft:golden_axe,-2.0", "minecraft:golden_pickaxe,1.0", "minecraft:golden_shovel,-0.5"));
        ATTACK_SPEED = ConfigBuildHandler.BUILDER.comment("Specify a value ADDED to the attack speed attribute for any item. Not compatible with \"classic_combat\" features. Format for every entry is \"<namespace>:<id>,<value>\".").define("Attack Speed List", Lists.newArrayList());
        MAX_STACK_SIZE = ConfigBuildHandler.BUILDER.comment("Specify the max stack size for any item. Value has to be between 0 and 64. Format for every entry is \"<namespace>:<id>,<value>\".").define("Max Stack Size List", Lists.newArrayList("minecraft:snowball,64", "minecraft:ender_eye,16"));
        TOOL_DURABILITY = ConfigBuildHandler.BUILDER.comment("Change the durability for any tool. Setting it to 0 will make the tool unbreakable. Format for every entry is \"<namespace>:<id>,<value>\".").define("Tool Durability List", Lists.newArrayList());
        BUILDER.pop();

        BUILDER.comment("Introduces various changes from current combat snapshots.");
        BUILDER.push("modern_combat");
        NO_PROJECTILE_RESISTANCE = ConfigBuildHandler.BUILDER.comment("Disables the default 0.5 second damage immunity when hit by a projectile. This makes it possible for entities to be hit by multiple projectiles at once, e. g. from a multishot enchanted crossbow.").define("No Projectile Immunity", true);
        NO_AXE_ATTACK_PENALTY = ConfigBuildHandler.BUILDER.comment("Only damages axes by 1 durability instead of 2 when attacking so they properly be used as weapons.").define("No Axe Attack Penalty", true);
        ITEM_DELAY = ConfigBuildHandler.BUILDER.comment("Items to add a delay in ticks to after using. Doesn't override a delay an item has by default.").define("Item Delay List", Lists.newArrayList("minecraft:snowball,4", "minecraft:egg,4"));
        SWEEPING_REQUIRED = ConfigBuildHandler.BUILDER.comment("Is the sweeping edge enchantment required to perform a sweep attack.").define("Require Sweeping Edge", true);
        NO_SWEEPING_SMOKE = ConfigBuildHandler.BUILDER.comment("Prevent particles created by a sweep attack from appearing.").define("No Sweeping Particles", true);
        SHIELD_DELAY = ConfigBuildHandler.BUILDER.comment("Amount of ticks after which blocking using a shield is effective. Used to be 5 ticks before combat snapshots.").defineInRange("Shield Warm-Up Delay", 0, 0, 72000);
        BOOST_IMPALING = ConfigBuildHandler.BUILDER.comment("Makes the impaling enchantment apply when attack any creature in contact with rain or water; not just to aquatic mobs.").define("Boost Impaling", true);
        DISPENSE_TRIDENT = ConfigBuildHandler.BUILDER.comment("Allow tridents to be fired from dispensers.").define("Dispense Tridents", true);
        BUILDER.pop();

        BUILDER.comment("Changes the way the player heals from food.");
        BUILDER.push("food_buffs");
        FOOD_TICKER = ConfigBuildHandler.BUILDER.comment("\"CLASSIC\" option restores the pre-combat update system, \"COMBAT\" option introduces the changes from current combat snapshots, \"CUSTOM\" allows custom values to be supplied.").defineEnum("Food Ticker", ConfigValueHolder.FoodBuffs.FoodTicker.CLASSIC);
        REGEN_DELAY = ConfigBuildHandler.BUILDER.comment("Amount of ticks between regenerating when enough food is present. Only applies when \"Food Ticker\" is set to \"CUSTOM\".").defineInRange("Regeneration Delay", 80, 0, Integer.MAX_VALUE);
        REGEN_THRESHOLD = ConfigBuildHandler.BUILDER.comment("Food level required to be able to regenerate health. Only applies when \"Food Ticker\" is set to \"CUSTOM\".").defineInRange("Regeneration Food Level", 18, 0, 20);
        DRAIN_FOOD = ConfigBuildHandler.BUILDER.comment("Drain food instead of saturation when regenerating. Only applies when \"Food Ticker\" is set to \"CUSTOM\".").define("Regenerate From Food", false);
        EATING_SPEED = ConfigBuildHandler.BUILDER.comment("Amount of ticks it takes to consume a food item. Vanilla default is 32.").defineInRange("Eating Speed", 40, 0, 72000);
        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

}