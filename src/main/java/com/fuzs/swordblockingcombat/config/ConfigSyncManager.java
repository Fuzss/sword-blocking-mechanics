package com.fuzs.swordblockingcombat.config;

import com.google.common.collect.Maps;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraftforge.fml.config.ModConfig;

import java.util.Map;

public class ConfigSyncManager {

    private final StringListParser parser = new StringListParser();

    public void onModConfig(final ModConfig.ModConfigEvent evt) {

        if (evt.getConfig().getSpec() == ConfigBuildHandler.SPEC) {

            this.sync();
        }
    }

    private void sync() {

        // sword blocking
        ConfigValueHolder.SWORD_BLOCKING.exclude = this.parser.buildItemSetWithCondition(ConfigBuildHandler.EXCLUDE.get(),
                item -> item instanceof SwordItem, "No instance of SwordItem");
        ConfigValueHolder.SWORD_BLOCKING.include = this.parser.buildItemSetWithCondition(ConfigBuildHandler.INCLUDE.get(),
                item -> !(item instanceof SwordItem), "Already is instance of SwordItem");
        ConfigValueHolder.SWORD_BLOCKING.blockDelay = ConfigBuildHandler.BLOCK_DELAY.get();
        ConfigValueHolder.SWORD_BLOCKING.blocked = ConfigBuildHandler.BLOCKED.get().floatValue();
        ConfigValueHolder.SWORD_BLOCKING.damageSword = ConfigBuildHandler.DAMAGE_SWORD.get();
        ConfigValueHolder.SWORD_BLOCKING.deflectProjectiles = ConfigBuildHandler.DEFLECT_PROJECTILES.get();
        // classic combat
        ConfigValueHolder.CLASSIC_COMBAT.removeCooldown = ConfigBuildHandler.REMOVE_ATTACK_COOLDOWN.get();
        ConfigValueHolder.CLASSIC_COMBAT.boostSharpness = ConfigBuildHandler.BOOST_SHARPNESS.get();
        ConfigValueHolder.CLASSIC_COMBAT.attackingAllowsSprinting = ConfigBuildHandler.SPRINT_WHILE_ATTACKING.get();
        // material changer
        ConfigValueHolder.MATERIAL_CHANGER.attributes = this.syncAttributeMap();
        this.syncStackSize();
        this.syncDurability();
        // modern combat
        ConfigValueHolder.MODERN_COMBAT.noProjectileResistance = ConfigBuildHandler.NO_PROJECTILE_RESISTANCE.get();
        ConfigValueHolder.MODERN_COMBAT.noAttackPenalty = ConfigBuildHandler.NO_AXE_ATTACK_PENALTY.get();
        ConfigValueHolder.MODERN_COMBAT.itemDelay = this.parser.buildItemMapWithCondition(ConfigBuildHandler.ITEM_DELAY.get(),
                (item, value) -> value >= 0.0 && value <= 72000.0, "Item delay out of bounds");
        ConfigValueHolder.MODERN_COMBAT.sweepingRequired = ConfigBuildHandler.SWEEPING_REQUIRED.get();
        ConfigValueHolder.MODERN_COMBAT.noSweepingSmoke = ConfigBuildHandler.NO_SWEEPING_SMOKE.get();
        ConfigValueHolder.MODERN_COMBAT.shieldDelay = ConfigBuildHandler.SHIELD_DELAY.get() - 5;
        ConfigValueHolder.MODERN_COMBAT.boostImpaling = ConfigBuildHandler.BOOST_IMPALING.get();
        ConfigValueHolder.MODERN_COMBAT.dispenseTridents = ConfigBuildHandler.DISPENSE_TRIDENT.get();
        // food buffs
        ConfigValueHolder.FOOD_BUFFS.foodTicker = ConfigBuildHandler.FOOD_TICKER.get();
        ConfigValueHolder.FOOD_BUFFS.regenDelay = ConfigBuildHandler.REGEN_DELAY.get();
        ConfigValueHolder.FOOD_BUFFS.regenThreshold = ConfigBuildHandler.REGEN_THRESHOLD.get();
        ConfigValueHolder.FOOD_BUFFS.eatingSpeed = ConfigBuildHandler.EATING_SPEED.get();
        ConfigValueHolder.FOOD_BUFFS.regenThreshold = ConfigBuildHandler.REGEN_THRESHOLD.get();
    }

    private Map<Item, Map<String, AttributeModifier>> syncAttributeMap() {

        Map<Item, Map<String, AttributeModifier>> map = Maps.newHashMap();
        this.parser.buildAttributeMap(ConfigBuildHandler.ATTACK_DAMAGE.get(), StringListParser.AttributeModifierType.ATTACK_DAMAGE, map);
        this.parser.buildAttributeMap(ConfigBuildHandler.ATTACK_SPEED.get(), StringListParser.AttributeModifierType.ATTACK_SPEED, map);
        return map;
    }

    private void syncStackSize() {

        this.parser.buildItemMapWithCondition(ConfigBuildHandler.MAX_STACK_SIZE.get(), (item, value) -> value >= 0.0 && value <= 64.0,
                "Stack size out of bounds").forEach((key, value) -> key.maxStackSize = value.intValue());
    }

    private void syncDurability() {

        this.parser.buildItemMapWithCondition(ConfigBuildHandler.TOOL_DURABILITY.get(), (item, value) -> item.isDamageable() && value >= 0.0,
                "Item can't be damaged or durability out of bounds").forEach((key, value) -> key.maxDamage = value.intValue());
    }

}
