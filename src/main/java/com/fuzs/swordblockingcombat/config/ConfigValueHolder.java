package com.fuzs.swordblockingcombat.config;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;

import java.util.Map;
import java.util.Set;

public class ConfigValueHolder {

    public static final SwordBlocking SWORD_BLOCKING = new SwordBlocking();
    public static final ClassicCombat CLASSIC_COMBAT = new ClassicCombat();
    public static final MaterialChanger MATERIAL_CHANGER = new MaterialChanger();
    public static final ModernCombat MODERN_COMBAT = new ModernCombat();
    public static final FoodBuffs FOOD_BUFFS = new FoodBuffs();
    public static final EnchantmentEnhancer ENCHANTMENT_ENHANCER = new EnchantmentEnhancer();

    public static class SwordBlocking {

        public Set<Item> exclude;
        public Set<Item> include;
        public float blocked;
        public boolean damageSword;
        public boolean deflectProjectiles;
        public int blockDelay;
    }

    public static class ClassicCombat {

        public boolean removeCooldown;
        public boolean boostSharpness;
        public boolean attackingAllowsSprinting;
        public boolean sweepingRequired;
        public boolean noSweepingSmoke;
    }

    public static class MaterialChanger {

        public Map<Item, Map<String, AttributeModifier>> attributes;
    }

    public static class ModernCombat {

        public boolean noProjectileResistance;
        public boolean noAttackPenalty;
        public Map<Item, Double> itemDelay;
        public int shieldDelay;
        public boolean boostImpaling;
        public boolean dispenseTridents;
    }

    public static class EnchantmentEnhancer {
    }

    public static class FoodBuffs {

        public FoodTicker foodTicker;
        public int regenDelay;
        public int regenThreshold;
        public boolean drainFood;
        public int eatingSpeed;
        public float sprintingLevel;

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
    }

}
