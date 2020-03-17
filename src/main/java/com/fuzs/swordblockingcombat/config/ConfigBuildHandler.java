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
    static final ForgeConfigSpec.DoubleValue WALKING_MODIFIER;
    static final ForgeConfigSpec.ConfigValue<List<String>> EXCLUDE;
    static final ForgeConfigSpec.ConfigValue<List<String>> INCLUDE;
    // classic combat
    static final ForgeConfigSpec.BooleanValue REMOVE_ATTACK_COOLDOWN;
    static final ForgeConfigSpec.BooleanValue NO_COOLDOWN_TOOLTIP;
    static final ForgeConfigSpec.BooleanValue DISABLE_ATTACK_INDICATOR;
    static final ForgeConfigSpec.BooleanValue BOOST_SHARPNESS;
    static final ForgeConfigSpec.BooleanValue SPRINT_WHILE_ATTACKING;
    static final ForgeConfigSpec.BooleanValue SWEEPING_REQUIRED;
    static final ForgeConfigSpec.BooleanValue NO_SWEEPING_SMOKE;
    // material changer
    static final ForgeConfigSpec.ConfigValue<List<String>> ATTACK_DAMAGE;
    static final ForgeConfigSpec.ConfigValue<List<String>> ATTACK_SPEED;
    static final ForgeConfigSpec.ConfigValue<List<String>> MAX_STACK_SIZE;
    static final ForgeConfigSpec.ConfigValue<List<String>> TOOL_DURABILITY;
    // modern combat
    static final ForgeConfigSpec.BooleanValue NO_PROJECTILE_RESISTANCE;
    static final ForgeConfigSpec.BooleanValue NO_AXE_ATTACK_PENALTY;
    static final ForgeConfigSpec.ConfigValue<List<String>> ITEM_DELAY;
    static final ForgeConfigSpec.IntValue SHIELD_DELAY;
    static final ForgeConfigSpec.BooleanValue BOOST_IMPALING;
    static final ForgeConfigSpec.BooleanValue DISPENSE_TRIDENT;
    static final ForgeConfigSpec.BooleanValue SWING_THROUGH_GRASS;
    static final ForgeConfigSpec.IntValue COYOTE_TIME;
    static final ForgeConfigSpec.BooleanValue COYOTE_SMALL;
    static final ForgeConfigSpec.BooleanValue HOLD_ATTACK;
    static final ForgeConfigSpec.DoubleValue FIST_STRENGTH;
    static final ForgeConfigSpec.BooleanValue SWING_ANIMATION;
    static final ForgeConfigSpec.BooleanValue HIT_ONLY_ALIVE;
    static final ForgeConfigSpec.BooleanValue BETTER_PROJECTILES;
    static final ForgeConfigSpec.BooleanValue FAST_SWITCHING;
    static final ForgeConfigSpec.BooleanValue UPWARDS_KNOCKBACK;
    // food buffs
    static final ForgeConfigSpec.EnumValue<ConfigValueHolder.FoodBuffs.FoodTicker> FOOD_TICKER;
    static final ForgeConfigSpec.IntValue REGEN_DELAY;
    static final ForgeConfigSpec.IntValue REGEN_THRESHOLD;
    static final ForgeConfigSpec.BooleanValue DRAIN_FOOD;
    static final ForgeConfigSpec.IntValue EATING_SPEED;
    static final ForgeConfigSpec.IntValue SPRINTING_LEVEL;

    static {

        BUILDER.comment("Re-adds sword blocking in a very configurable way.");
        BUILDER.push("sword_blocking");
        BLOCKED = ConfigBuildHandler.BUILDER.comment("Percentage an incoming attack will be reduced by when blocking.").defineInRange("Blocked Damage Ratio", 0.5, 0.0, 1.0);
        DAMAGE_SWORD = ConfigBuildHandler.BUILDER.comment("Damage sword when blocking an attack depending on the amount of damage blocked. Sword is only damaged when at least three damage points have been blocked, just like a shield.").define("Damage Sword", false);
        DEFLECT_PROJECTILES = ConfigBuildHandler.BUILDER.comment("Incoming projectiles such as arrows or tridents will ricochet while blocking.").define("Deflect Projectiles", false);
        BLOCK_DELAY = ConfigBuildHandler.BUILDER.comment("Amount of ticks after which blocking using a sword is effective.").defineInRange("Warm-Up Delay", 0, 0, 72000);
        WALKING_MODIFIER = ConfigBuildHandler.BUILDER.comment("Percentage to slow down movement to while blocking.").defineInRange("Walking Modifier", 0.2, 0.0, Float.MAX_VALUE);
        EXCLUDE = ConfigBuildHandler.BUILDER.comment("Swords to exclude from blocking. Intended for modded swords that already have their own right-click function. Format for every entry is \"<namespace>:<id>\".").define("Blocking Exclusion List", Lists.newArrayList());
        INCLUDE = ConfigBuildHandler.BUILDER.comment("Items to include for blocking. Intended for modded swords that don't extend vanilla swords. Format for every entry is \"<namespace>:<id>\".").define("Blocking Inclusion List", Lists.newArrayList());
        BUILDER.pop();

        BUILDER.comment("Restores pre-combat update combat mechanics.");
        BUILDER.push("classic_combat");
        REMOVE_ATTACK_COOLDOWN = ConfigBuildHandler.BUILDER.comment("Completely remove the attack cooldown as if it never even existed in the first place.").define("Remove Attack Cooldown", true);
        NO_COOLDOWN_TOOLTIP = ConfigBuildHandler.BUILDER.comment("Remove \"Attack Speed\" attribute from tooltips.").define("No Attack Speed Tooltip", true);
        DISABLE_ATTACK_INDICATOR = ConfigBuildHandler.BUILDER.comment("Prevent attack indicator from showing regardless of what's been set in \"Video Settings\".").define("Disable Attack Indicator", true);
        BOOST_SHARPNESS = ConfigBuildHandler.BUILDER.comment("Boost sharpness enchantment to add +1.0 attack damage per level instead of +0.5 damage.").define("Boost Sharpness", true);
        SPRINT_WHILE_ATTACKING = ConfigBuildHandler.BUILDER.comment("Don't automatically stop sprinting when attacking.").define("Sprint While Attacking", true);
        SWEEPING_REQUIRED = ConfigBuildHandler.BUILDER.comment("Is the sweeping edge enchantment required to perform a sweep attack.").define("Require Sweeping Edge", true);
        NO_SWEEPING_SMOKE = ConfigBuildHandler.BUILDER.comment("Prevent particles created by a sweep attack from appearing.").define("No Sweeping Particles", false);
        BUILDER.pop();

        BUILDER.comment("Allows changing various stats of items, mainly for restoring pre-combat update values.");
        BUILDER.push("material_changer");
        ATTACK_DAMAGE = ConfigBuildHandler.BUILDER.comment("Specify a value ADDED to the attack damage attribute for any item. Format for every entry is \"<namespace>:<id>,<value>\".").define("Attack Damage List", Lists.newArrayList("minecraft:diamond_sword,1.0", "minecraft:diamond_axe,-2.0", "minecraft:diamond_pickaxe,1.0", "minecraft:diamond_shovel,-0.5", "minecraft:diamond_hoe,3.0", "minecraft:iron_sword,1.0", "minecraft:iron_axe,-3.0", "minecraft:iron_pickaxe,1.0", "minecraft:iron_shovel,-0.5", "minecraft:iron_hoe,2.0", "minecraft:stone_sword,1.0", "minecraft:stone_axe,-4.0", "minecraft:stone_pickaxe,1.0", "minecraft:stone_shovel,-0.5", "minecraft:stone_hoe,1.0", "minecraft:wooden_sword,1.0", "minecraft:wooden_axe,-3.0", "minecraft:wooden_pickaxe,1.0", "minecraft:wooden_shovel,-0.5", "minecraft:golden_sword,1.0", "minecraft:golden_axe,-3.0", "minecraft:golden_pickaxe,1.0", "minecraft:golden_shovel,-0.5"));
        ATTACK_SPEED = ConfigBuildHandler.BUILDER.comment("Specify a value ADDED to the attack speed attribute for any item. Not compatible with \"classic_combat\" features. Format for every entry is \"<namespace>:<id>,<value>\".").define("Attack Speed List", Lists.newArrayList());
        MAX_STACK_SIZE = ConfigBuildHandler.BUILDER.comment("Specify the max stack size for any item. Value has to be between 0 and 64. Format for every entry is \"<namespace>:<id>,<value>\".").define("Max Stack Size List", Lists.newArrayList("minecraft:snowball,64", "minecraft:ender_eye,16"));
        TOOL_DURABILITY = ConfigBuildHandler.BUILDER.comment("Change the durability for any tool. Setting it to 0 will make the tool unbreakable. Format for every entry is \"<namespace>:<id>,<value>\".").define("Tool Durability List", Lists.newArrayList());
        BUILDER.pop();

        BUILDER.comment("Introduces various changes from current combat snapshots.");
        BUILDER.push("modern_combat");
        NO_PROJECTILE_RESISTANCE = ConfigBuildHandler.BUILDER.comment("Disables the default 0.5 second damage immunity when hit by a projectile. This makes it possible for entities to be hit by multiple projectiles at once, e. g. from a multishot enchanted crossbow.").define("No Projectile Immunity", true);
        NO_AXE_ATTACK_PENALTY = ConfigBuildHandler.BUILDER.comment("Only damages axes by 1 durability instead of 2 when attacking so they properly be used as weapons.").define("No Axe Attack Penalty", true);
        ITEM_DELAY = ConfigBuildHandler.BUILDER.comment("Items to add a delay in ticks to after using. Doesn't override a delay an item has by default.").define("Item Delay List", Lists.newArrayList("minecraft:snowball,4", "minecraft:egg,4"));
        SHIELD_DELAY = ConfigBuildHandler.BUILDER.comment("Amount of ticks after which blocking using a shield is effective. Used to be 5 ticks before combat snapshots.").defineInRange("Shield Warm-Up Delay", 0, 0, 72000);
        BOOST_IMPALING = ConfigBuildHandler.BUILDER.comment("Makes the impaling enchantment apply when attack any creature in contact with rain or water; not just to aquatic mobs.").define("Boost Impaling", true);
        DISPENSE_TRIDENT = ConfigBuildHandler.BUILDER.comment("Allow tridents to be fired from dispensers.").define("Dispense Tridents", true);
        SWING_THROUGH_GRASS = ConfigBuildHandler.BUILDER.comment("Hit mobs through blocks without a collision shape such as grass without breaking the block.").define("Swing Through Grass", true);
        COYOTE_TIME = ConfigBuildHandler.BUILDER.comment("Amount of ticks a mob can still be interacted with after no longer aiming at it.").defineInRange("Coyote Time", 0, 0, Integer.MAX_VALUE);
        COYOTE_SMALL = ConfigBuildHandler.BUILDER.comment("Make \"Coyote Time\" only work on small mobs.").define("Coyote Small Mobs", false);
        HOLD_ATTACK = ConfigBuildHandler.BUILDER.comment("Hold down the attack key to attack automatically whenever possible.").define("Hold Attack Button", false);
        FIST_STRENGTH = ConfigBuildHandler.BUILDER.comment("Base attack damage of the player. Attack strength of weapons and tools is added on top.").defineInRange("Fist Attack Strength", 1.0, 0.0, 2048.0);
        SWING_ANIMATION = ConfigBuildHandler.BUILDER.comment("Improved arm swing animation to emphasize the rhythm of the attacks.").define("Better Swing Animation", true);
        HIT_ONLY_ALIVE = ConfigBuildHandler.BUILDER.comment("Ray tracing will no longer target entities that have already died.").define("Hit Only Alive", true);
        BETTER_PROJECTILES = ConfigBuildHandler.BUILDER.comment("Item projectiles like snowballs and ender pearls pass through blocks without a collision shape and deal knockback to players.").define("Improve Item Projectiles", true);
        FAST_SWITCHING = ConfigBuildHandler.BUILDER.comment("The attack timer is unaffected by switching items.").define("Fast Tool Switching", true);
        UPWARDS_KNOCKBACK = ConfigBuildHandler.BUILDER.comment("Turns knockback resistance into a scale instead of being random and makes knockback have an upwards tendency.").define("Upwards Knockback", false);
        BUILDER.pop();

        BUILDER.comment("Changes the way the player heals from food.");
        BUILDER.push("food_buffs");
        FOOD_TICKER = ConfigBuildHandler.BUILDER.comment("\"CLASSIC\" option restores the pre-combat update system, \"COMBAT\" option introduces the changes from current combat snapshots, \"CUSTOM\" allows custom values to be supplied.").defineEnum("Food Ticker", ConfigValueHolder.FoodBuffs.FoodTicker.CLASSIC);
        REGEN_DELAY = ConfigBuildHandler.BUILDER.comment("Amount of ticks between regenerating when enough food is present. Only applies when \"Food Ticker\" is set to \"CUSTOM\".").defineInRange("Regeneration Delay", 80, 0, Integer.MAX_VALUE);
        REGEN_THRESHOLD = ConfigBuildHandler.BUILDER.comment("Food level required to be able to regenerate health. Only applies when \"Food Ticker\" is set to \"CUSTOM\".").defineInRange("Regeneration Food Level", 18, 0, 20);
        DRAIN_FOOD = ConfigBuildHandler.BUILDER.comment("Drain food instead of saturation when regenerating. Only applies when \"Food Ticker\" is set to \"CUSTOM\".").define("Regenerate From Food", false);
        EATING_SPEED = ConfigBuildHandler.BUILDER.comment("Amount of ticks it takes to consume a food item. Vanilla default is 32.").defineInRange("Eating Speed", 40, 0, 72000);
        SPRINTING_LEVEL = ConfigBuildHandler.BUILDER.comment("Food level from which on sprinting is disabled. Set to \"-1\" to always allow sprinting.").defineInRange("Sprinting Level", -1, -1, 20);
        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

}