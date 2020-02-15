package com.fuzs.swordblockingcombat.asm;

import com.fuzs.swordblockingcombat.common.ClassicCombatHandler;
import com.fuzs.swordblockingcombat.common.ModernCombatHandler;
import com.fuzs.swordblockingcombat.config.ConfigValueHolder;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;

import javax.smartcardio.ATR;
import java.util.Map;

@SuppressWarnings("unused")
public class Hooks {

    /**
     * make attacking an entity in {@link net.minecraft.item.ToolItem} only consume one durability point
     */
    public static int hitEntityAmount(ToolItem instance) {
        return ModernCombatHandler.hitEntityAmount(instance);
    }

    public static boolean doSweeping(boolean flag, PlayerEntity player, Entity targetEntity, float f) {

        ClassicCombatHandler.doSweeping(flag, player, targetEntity, f);
        return false;
    }

    public static Multimap<String, AttributeModifier> adjustAttributeMap(Multimap<String, AttributeModifier> multimap, EquipmentSlotType equipmentSlot, ItemStack stack) {

        Map<Item, Map<String, AttributeModifier>> itemMap = ConfigValueHolder.MATERIAL_CHANGER.attributes;
        if (itemMap != null && equipmentSlot == EquipmentSlotType.MAINHAND) {

            Map<String, AttributeModifier> attributeMap = itemMap.get(stack.getItem());
            if (attributeMap != null) {

                multimap.putAll(Multimaps.forMap(attributeMap));
            }
        }

        return multimap;
    }

    public static float addEnchantmentDamage() {
        return 0.0F;
    }

}
