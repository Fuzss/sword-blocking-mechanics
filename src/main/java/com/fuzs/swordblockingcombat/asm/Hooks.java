package com.fuzs.swordblockingcombat.asm;

import com.fuzs.swordblockingcombat.common.ClassicCombatHandler;
import com.fuzs.swordblockingcombat.common.CombatFoodHandler;
import com.fuzs.swordblockingcombat.common.ModernCombatHandler;
import com.fuzs.swordblockingcombat.config.ConfigValueHolder;
import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * this class is only used for redirecting hooks to their appropriate place
 */
@SuppressWarnings("unused")
public class Hooks {

    /**
     * make attacking an entity in {@link net.minecraft.item.ToolItem} only consume one durability point
     */
    public static int hitEntityAmount(ToolItem instance) {

        return ModernCombatHandler.hitEntityAmount(instance);
    }

    /**
     * disable sweeping attack in by default {@link net.minecraft.entity.player.PlayerEntity#attackTargetEntityWithCurrentItem}
     * might be executed if sweeping edge enchantment is present; sweeping particles are removed though
     */
    public static void doSweeping(boolean flag, PlayerEntity player, Entity targetEntity, float damage) {

        ClassicCombatHandler.doSweeping(flag, player, targetEntity, damage);
    }

    /**
     * set the player to be sprinting again if it has been disabled before in {@link net.minecraft.entity.player.PlayerEntity#attackTargetEntityWithCurrentItem}
     */
    public static void restoreSprinting(PlayerEntity player, int knockback) {

        ClassicCombatHandler.restoreSprinting(player, knockback);
    }

    /**
     * add custom attributes to an item when the attribute mmap is generated in {@link net.minecraft.item.Item#getAttributeModifiers}
     * attributes already present will be modified to ensure the item tooltip displaying values properly
     */
    public static Multimap<String, AttributeModifier> adjustAttributeMap(Multimap<String, AttributeModifier> multimap, EquipmentSlotType equipmentSlot, ItemStack stack) {

        Map<Item, Map<String, AttributeModifier>> itemMap = ConfigValueHolder.MATERIAL_CHANGER.attributes;
        if (itemMap != null && equipmentSlot == EquipmentSlotType.MAINHAND) {

            Map<String, AttributeModifier> attributeMap = itemMap.get(stack.getItem());
            if (attributeMap != null) {

                attributeMap.forEach((key, value) -> {

                    List<AttributeModifier> matchingAttributes = multimap.entries().stream().filter(set -> set.getKey().equals(key)).map(Map.Entry::getValue).collect(Collectors.toList());
                    Optional<AttributeModifier> matchingID = matchingAttributes.stream().filter(attributeModifier -> attributeModifier.getID().equals(value.getID())).findFirst();
                    if (matchingID.isPresent()) {

                        matchingID.get().amount += value.getAmount();
                    } else {

                        multimap.put(key, value);
                    }
                });
            }
        }

        return multimap;
    }

    /**
     * modify damage dealt to an entity in {@link net.minecraft.entity.player.PlayerEntity#attackTargetEntityWithCurrentItem}
     */
    public static float addEnchantmentDamage(float damage, PlayerEntity player, Entity targetEntity) {

        damage += ClassicCombatHandler.addEnchantmentDamage(player);
        damage += ModernCombatHandler.addEnchantmentDamage(player, targetEntity);
        return damage;
    }

    /**
     * overwrite returned use duration for food items in {@link net.minecraft.item.Item#getUseDuration}
     */
    public static int getFoodDuration(Item item) {

        return CombatFoodHandler.getFoodDuration(item);
    }

    /**
     * overwrite minimum food level required for sprinting to start
     */
    public static float getSprintingLevel() {

        return CombatFoodHandler.getSprintingLevel();
    }

    /**
     * run block ray tracing a second time to only check for blocks with a collision shape
     * the result is then used to ray trace for an entity
     */
    public static double rayTraceCollidingBlocks(float partialTicks, Entity entity, double d0, double d1) {

        return ModernCombatHandler.rayTraceCollidingBlocks(partialTicks, entity, d0, d1);
    }

}
