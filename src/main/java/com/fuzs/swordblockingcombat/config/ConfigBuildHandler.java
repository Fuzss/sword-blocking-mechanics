package com.fuzs.swordblockingcombat.config;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class ConfigBuildHandler {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // sword blocking
    public static final ForgeConfigSpec.DoubleValue BLOCKED_AMOUNT;
    public static final ForgeConfigSpec.BooleanValue DAMAGE_SWORD;
    public static final ForgeConfigSpec.BooleanValue DEFLECT_PROJECTILES;
    public static final ForgeConfigSpec.DoubleValue WALKING_MODIFIER;
    public static final ForgeConfigSpec.ConfigValue<List<String>> BLOCKING_EXCLUDE;
    public static final ForgeConfigSpec.ConfigValue<List<String>> BLOCKING_INCLUDE;
    public static final ForgeConfigSpec.IntValue PARRY_WINDOW;
    // classic combat
    public static final ForgeConfigSpec.BooleanValue REMOVE_ATTACK_COOLDOWN;
    public static final ForgeConfigSpec.BooleanValue NO_COOLDOWN_TOOLTIP;
    public static final ForgeConfigSpec.BooleanValue HIDE_ATTACK_INDICATOR;
    public static final ForgeConfigSpec.BooleanValue BOOST_SHARPNESS;
    public static final ForgeConfigSpec.BooleanValue SPRINT_WHILE_ATTACKING;
    public static final ForgeConfigSpec.BooleanValue SWEEPING_REQUIRED;
    public static final ForgeConfigSpec.BooleanValue NO_SWEEPING_SMOKE;
    public static final ForgeConfigSpec.BooleanValue OLD_DAMAGE_VALUES;
    public static final ForgeConfigSpec.BooleanValue RED_ARMOR;
    public static final ForgeConfigSpec.BooleanValue OLD_FISHING_ROD;
    // combat test
    public static final ForgeConfigSpec.BooleanValue NO_PROJECTILE_RESISTANCE;
    public static final ForgeConfigSpec.BooleanValue SNOWBALL_STACKSIZE;
    public static final ForgeConfigSpec.BooleanValue NO_AXE_ATTACK_PENALTY;
    public static final ForgeConfigSpec.ConfigValue<List<String>> ITEM_DELAY;
    public static final ForgeConfigSpec.BooleanValue SHIELD_DELAY;
    public static final ForgeConfigSpec.BooleanValue BOOST_IMPALING;
    public static final ForgeConfigSpec.BooleanValue DISPENSE_TRIDENT;
    public static final ForgeConfigSpec.BooleanValue SWING_THROUGH_GRASS;
    public static final ForgeConfigSpec.IntValue COYOTE_TIME;
    public static final ForgeConfigSpec.BooleanValue COYOTE_SMALL;
    public static final ForgeConfigSpec.BooleanValue HOLD_ATTACK;
    public static final ForgeConfigSpec.BooleanValue SWING_ANIMATION;
    public static final ForgeConfigSpec.BooleanValue BETTER_PROJECTILES;
    public static final ForgeConfigSpec.BooleanValue FAST_SWITCHING;
    public static final ForgeConfigSpec.BooleanValue UPWARDS_KNOCKBACK;
    public static final ForgeConfigSpec.EnumValue<AttackIndicator> SHIELD_INDICATOR;
    public static final ForgeConfigSpec.BooleanValue HIDE_SHIELD;
    // food buffs
    public static final ForgeConfigSpec.EnumValue<FoodTicker> FOOD_TICKER;
    public static final ForgeConfigSpec.IntValue REGEN_DELAY;
    public static final ForgeConfigSpec.IntValue REGEN_THRESHOLD;
    public static final ForgeConfigSpec.BooleanValue DRAIN_FOOD;
    public static final ForgeConfigSpec.IntValue EATING_SPEED;
    public static final ForgeConfigSpec.IntValue SPRINTING_LEVEL;

    static {

        BUILDER.comment("Re-adds sword blocking in a very configurable way.");
        BUILDER.push("sword_blocking");
        BLOCKED_AMOUNT = ConfigBuildHandler.BUILDER.comment("Percentage an incoming attack will be reduced by when blocking.").defineInRange("Blocked Damage Ratio", 0.5, 0.0, 1.0);
        DAMAGE_SWORD = ConfigBuildHandler.BUILDER.comment("Damage sword when blocking an attack depending on the amount of damage blocked. Sword is only damaged when at least three damage points have been blocked, just like a shield.").define("Damage Sword", false);
        DEFLECT_PROJECTILES = ConfigBuildHandler.BUILDER.comment("Incoming projectiles such as arrows or tridents will ricochet while blocking.").define("Deflect Projectiles", false);
        WALKING_MODIFIER = ConfigBuildHandler.BUILDER.comment("Percentage to slow down movement to while blocking.").defineInRange("Walking Modifier", 0.2, 0.0, Integer.MAX_VALUE);
        BLOCKING_EXCLUDE = ConfigBuildHandler.BUILDER.comment("Swords to exclude from blocking. Intended for modded swords that already have their own right-click function.", "Format for every entry is \"<namespace>:<path>\". Path may use single asterisk as wildcard parameter.").define("Blocking Exclusion List", Lists.newArrayList());
        BLOCKING_INCLUDE = ConfigBuildHandler.BUILDER.comment("Items to include for blocking. Intended for modded swords that don't extend vanilla swords.", "Format for every entry is \"<namespace>:<path>\". Path may use single asterisk as wildcard parameter.").define("Blocking Inclusion List", Lists.newArrayList());
        PARRY_WINDOW = ConfigBuildHandler.BUILDER.comment("Amount of ticks after starting to block in which an attack will be completely nullified like when blocking with a shield.").defineInRange("Parry Window", 20, 0, 72000);
        BUILDER.pop();

        BUILDER.comment("Restores pre-Combat Update combat mechanics.");
        BUILDER.push("classic_combat");
        REMOVE_ATTACK_COOLDOWN = ConfigBuildHandler.BUILDER.comment("Completely remove the attack cooldown as if it never even existed in the first place.").define("Remove Attack Cooldown", true);
        NO_COOLDOWN_TOOLTIP = ConfigBuildHandler.BUILDER.comment("Remove \"Attack Speed\" attribute from tooltips.").define("No Attack Speed Tooltip", true);
        HIDE_ATTACK_INDICATOR = ConfigBuildHandler.BUILDER.comment("Prevent attack indicator from showing regardless of what's been set in \"Video Settings\".").define("Disable Attack Indicator", true);
        BOOST_SHARPNESS = ConfigBuildHandler.BUILDER.comment("Boost sharpness enchantment to add +1.0 attack damage per level instead of +0.5 damage.").define("Boost Sharpness", true);
        SPRINT_WHILE_ATTACKING = ConfigBuildHandler.BUILDER.comment("Don't automatically stop sprinting when attacking.").define("Sprint While Attacking", true);
        SWEEPING_REQUIRED = ConfigBuildHandler.BUILDER.comment("Is the sweeping edge enchantment required to perform a sweep attack.").define("Require Sweeping Edge", true);
        NO_SWEEPING_SMOKE = ConfigBuildHandler.BUILDER.comment("Prevent particles created by a sweep attack from appearing.").define("No Sweeping Particles", false);
        OLD_DAMAGE_VALUES = ConfigBuildHandler.BUILDER.comment("Revert weapon and tool attack damage to old values.").define("Old Damage Values", true);
        RED_ARMOR = ConfigBuildHandler.BUILDER.comment("Armor on entities turns red when they receive damage.").define("Armor Shows Hurt", true);
        OLD_FISHING_ROD = ConfigBuildHandler.BUILDER.comment("Fishing bobbers deal knockback upon hitting an entity, also entities being pulled in are slightly propelled upwards.").define("Old Fishing Bobber Behaviour", true);
        BUILDER.pop();

        BUILDER.comment("Introduces various tweaks from Combat Test Snapshots.");
        BUILDER.push("combat_test");
        NO_PROJECTILE_RESISTANCE = ConfigBuildHandler.BUILDER.comment("Disables the default 0.5 second damage immunity when hit by a projectile. This makes it possible for entities to be hit by multiple projectiles at once, e. g. from a multishot enchanted crossbow.").define("No Projectile Immunity", true);
        SNOWBALL_STACKSIZE = ConfigBuildHandler.BUILDER.comment("Increase snowball stack size to 64.").define("Increase Snowball Stack Size", true);
        NO_AXE_ATTACK_PENALTY = ConfigBuildHandler.BUILDER.comment("Only damages axes by 1 durability instead of 2 when attacking so they properly be used as weapons.").define("No Axe Attack Penalty", true);
        ITEM_DELAY = ConfigBuildHandler.BUILDER.comment("Items to add a delay in ticks to after using. Doesn't override a delay an item has by default.", "Format for every entry is \"<namespace>:<path>,<value>\". Path may use single asterisk as wildcard parameter.").define("Item Delay List", Lists.newArrayList("minecraft:snowball,4", "minecraft:egg,4"));
        SHIELD_DELAY = ConfigBuildHandler.BUILDER.comment("Is blocking using a shield only effective after 5 ticks like before Combat Test Snapshots.").define("Shield Warm-Up Delay", false);
        BOOST_IMPALING = ConfigBuildHandler.BUILDER.comment("Makes the impaling enchantment apply when attack any creature in contact with rain or water; not just to aquatic mobs.").define("Boost Impaling", true);
        DISPENSE_TRIDENT = ConfigBuildHandler.BUILDER.comment("Allow tridents to be fired from dispensers.").define("Dispense Tridents", true);
        SWING_THROUGH_GRASS = ConfigBuildHandler.BUILDER.comment("Hit mobs through blocks without a collision shape such as grass without breaking the block.").define("Swing Through Grass", true);
        COYOTE_TIME = ConfigBuildHandler.BUILDER.comment("Amount of ticks a mob can still be interacted with after no longer aiming at it.").defineInRange("Coyote Time", 0, 0, Integer.MAX_VALUE);
        COYOTE_SMALL = ConfigBuildHandler.BUILDER.comment("Make \"Coyote Time\" only work on small mobs.").define("Coyote Small Mobs", false);
        HOLD_ATTACK = ConfigBuildHandler.BUILDER.comment("Hold down the attack key to attack automatically whenever possible.").define("Hold Attack Button", false);
        SWING_ANIMATION = ConfigBuildHandler.BUILDER.comment("Improved arm swing animation to emphasize the rhythm of the attacks.").define("Better Swing Animation", true);
        BETTER_PROJECTILES = ConfigBuildHandler.BUILDER.comment("Item projectiles like snowballs and ender pearls pass through blocks without a collision shape and deal knockback to players.").define("Improve Item Projectiles", true);
        FAST_SWITCHING = ConfigBuildHandler.BUILDER.comment("The attack timer is unaffected by switching items.").define("Fast Tool Switching", true);
        UPWARDS_KNOCKBACK = ConfigBuildHandler.BUILDER.comment("Turns knockback resistance into a scale instead of being random and makes knockback have an upwards tendency.").define("Upwards Knockback", false);
        SHIELD_INDICATOR = ConfigBuildHandler.BUILDER.comment("Show a shield indicator similar to the attack indicator when actively blocking.").defineEnum("Shield Indicator", AttackIndicator.CROSSHAIR);
        HIDE_SHIELD = ConfigBuildHandler.BUILDER.comment("Prevent shield from rendering when actively blocking. Useful when \"Shield Indicator\" is enabled.").define("Hide Shield", false);
        BUILDER.pop();

        BUILDER.comment("Changes the way the player heals from food.");
        BUILDER.push("food_buffs");
        FOOD_TICKER = ConfigBuildHandler.BUILDER.comment("\"CLASSIC\" option restores the pre-Combat Update system, \"COMBAT\" option introduces the changes from current Combat Test Snapshots, \"CUSTOM\" allows custom values to be supplied.").defineEnum("Food Ticker", FoodTicker.CLASSIC);
        REGEN_DELAY = ConfigBuildHandler.BUILDER.comment("Amount of ticks between regenerating when enough food is present. Only applies when \"Food Ticker\" is set to \"CUSTOM\".").defineInRange("Regeneration Delay", 80, 0, Integer.MAX_VALUE);
        REGEN_THRESHOLD = ConfigBuildHandler.BUILDER.comment("Food level required to be able to regenerate health. Only applies when \"Food Ticker\" is set to \"CUSTOM\".").defineInRange("Regeneration Food Level", 18, 0, 20);
        DRAIN_FOOD = ConfigBuildHandler.BUILDER.comment("Drain food instead of saturation when regenerating. Only applies when \"Food Ticker\" is set to \"CUSTOM\".").define("Regenerate From Food", false);
        EATING_SPEED = ConfigBuildHandler.BUILDER.comment("Amount of ticks it takes to consume a food item. Has been increased to 40 in Combat Test Snapshots.").defineInRange("Eating Speed", 32, 0, 72000);
        SPRINTING_LEVEL = ConfigBuildHandler.BUILDER.comment("Food level from which on sprinting is disabled. Set to \"-1\" to always allow sprinting.").defineInRange("Sprinting Level", 6, -1, 20);
        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    @SuppressWarnings("unused")
    public enum FoodTicker {

        DEFAULT(0), CLASSIC(1), COMBAT(2), CUSTOM(3);

        private final int id;

        FoodTicker(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }
    }

    /**
     * needed here as mirror of {@link net.minecraft.client.settings.AttackIndicatorStatus} to work on a server
     * will be removed when splitting config for common and client
     */
    @SuppressWarnings("unused")
    public enum AttackIndicator {

        OFF, CROSSHAIR, HOTBAR
    }

}