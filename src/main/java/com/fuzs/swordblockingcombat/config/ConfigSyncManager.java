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
        ConfigValueHolder.SWORD_BLOCKING.damageAmount = ConfigBuildHandler.DAMAGE_AMOUNT.get();
        // modern combat
        ConfigValueHolder.MODERN_COMBAT.noProjectileResistance = ConfigBuildHandler.NO_PROJECTILE_RESISTANCE.get();
        ConfigValueHolder.MODERN_COMBAT.itemDelay = this.parser.buildItemMapWithCondition(ConfigBuildHandler.ITEM_DELAY.get(),
                d -> d >= 0.0 && d <= 72000.0, "Item delay out of bounds");
        // food buffs
        ConfigValueHolder.FOOD_BUFFS.foodTicker = ConfigBuildHandler.FOOD_TICKER.get();
        ConfigValueHolder.FOOD_BUFFS.regenDelay = ConfigBuildHandler.REGEN_DELAY.get();
        ConfigValueHolder.FOOD_BUFFS.regenThreshold = ConfigBuildHandler.REGEN_THRESHOLD.get();
        ConfigValueHolder.FOOD_BUFFS.drainFood = ConfigBuildHandler.DRAIN_FOOD.get();
        // classic combat
        ConfigValueHolder.CLASSIC_COMBAT.sweepingRequired = ConfigBuildHandler.SWEEPING_REQUIRED.get();
        ConfigValueHolder.CLASSIC_COMBAT.noSweepingSmoke = ConfigBuildHandler.NO_SWEEPING_SMOKE.get();
        // material changer
        ConfigValueHolder.MATERIAL_CHANGER.attributes = this.syncAttributeMap();
        this.syncStackSize();
        this.syncDurability();
    }

    private Map<Item, Map<String, AttributeModifier>> syncAttributeMap() {

        Map<Item, Map<String, AttributeModifier>> map = Maps.newHashMap();
        this.parser.buildAttributeMap(ConfigBuildHandler.ATTACK_DAMAGE.get(), StringListParser.AttributeModifierType.ATTACK_DAMAGE, map);
        this.parser.buildAttributeMap(ConfigBuildHandler.ATTACK_SPEED.get(), StringListParser.AttributeModifierType.ATTACK_SPEED, map);
        return map;
    }

    private void syncStackSize() {

        this.parser.buildItemMapWithCondition(ConfigBuildHandler.MAX_STACK_SIZE.get(), d -> d >= 0.0 && d <= 64.0,
                "Stack size out of bounds").forEach((key, value) -> key.maxStackSize = value.intValue());
    }

    private void syncDurability() {

        this.parser.buildItemMapWithCondition(ConfigBuildHandler.TOOL_DURABILITY.get(), d -> d >= 0.0,
                "Durability out of bounds").forEach((key, value) -> key.maxDamage = value.intValue());
    }

}
