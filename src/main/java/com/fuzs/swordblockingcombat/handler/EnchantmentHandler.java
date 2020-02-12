package com.fuzs.swordblockingcombat.handler;

import com.google.common.collect.Lists;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

public class EnchantmentHandler {

    private static final EnchantmentType ACTUAL_WEAPON = EnchantmentType.create("ACTUAL_WEAPON", item -> item instanceof SwordItem || item instanceof AxeItem || item instanceof TridentItem);
    private static final EnchantmentType ARROW_LAUNCHER = EnchantmentType.create("ARROW_LAUNCHER", item -> item instanceof CrossbowItem || item instanceof BowItem);

    public EnchantmentHandler() {

        BiConsumer<List<String>, EnchantmentType> adjustType = (enchantments, type) -> enchantments.forEach(enchantment ->
                Optional.ofNullable(ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchantment))).ifPresent(it -> it.type = type));
        adjustType.accept(Lists.newArrayList("sharpness", "smite", "bane_of_arthropods", "knockback", "fire_aspect", "looting", "sweeping"), ACTUAL_WEAPON);
        adjustType.accept(Lists.newArrayList("flame", "punch", "power", "infinity", "piercing", "multishot", "quick_charge"), ARROW_LAUNCHER);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onCriticalHit(final CriticalHitEvent evt) {

        // hijacking this event to be able to make modifications to the current attack damage
        int sharpness = EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, evt.getPlayer().getHeldItemMainhand());
        int impaling = EnchantmentHelper.getEnchantmentLevel(Enchantments.IMPALING, evt.getPlayer().getHeldItemMainhand());
        float f = (float) evt.getPlayer().getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue();

        if (sharpness > 1 || impaling > 0) {
            evt.setResult(Event.Result.ALLOW);
        }

        // every level of sharpness adds 1.0F attack damage
        if (sharpness > 1) {

            float previous = evt.getDamageModifier() - evt.getOldDamageModifier();
            float damage = -0.5F + sharpness * 0.5F;
            evt.setDamageModifier((evt.getOldDamageModifier() - 1.0F + damage) / f + 1.0F + previous);
        }

        // makes impaling work on all mobs in water or rain, not just those classified as water creatures
        if (impaling > 0 && evt.getTarget() instanceof LivingEntity && ((LivingEntity) evt.getTarget()).getCreatureAttribute()
                != CreatureAttribute.WATER && evt.getTarget().isInWaterRainOrBubbleColumn()) {

            float previous = evt.getDamageModifier() - evt.getOldDamageModifier();
            evt.setDamageModifier((evt.getOldDamageModifier() - 1.0F + impaling * 2.5F) / f + 1.0F + previous);
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onArrowNock(final ArrowNockEvent evt) {

        // don't require any arrows when shooting a bow with infinity
        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, evt.getBow()) > 0) {

            evt.getPlayer().setActiveHand(evt.getHand());
            evt.setAction(new ActionResult<>(ActionResultType.SUCCESS, evt.getBow()));
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onItemUseEnd(final LivingEntityUseItemEvent.Stop evt) {

        // infinity enchantment for crossbows
        ItemStack stack = evt.getItem();
        if (stack.isCrossbowStack() && EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0
                && !((PlayerEntity) evt.getEntityLiving()).abilities.isCreativeMode) {

            int i = stack.getUseDuration() - evt.getDuration();
            float f = (float) i / (float) CrossbowItem.getChargeTime(stack);
            if (f >= 1.0F && !CrossbowItem.isCharged(stack)) {

                ItemStack ammo = evt.getEntityLiving().findAmmo(stack);
                if (!ammo.isEmpty()) {
                    if (ammo.getItem() == Items.ARROW) {
                        ammo.grow(1);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onItemUseTick(final LivingEntityUseItemEvent.Tick evt) {

        if (evt.getItem().getItem() instanceof BowItem) {

            // quick charge enchantment for bows
            int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.QUICK_CHARGE, evt.getItem());
            evt.setDuration(evt.getDuration() - i);
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onEntityJoinWorld(final EntityJoinWorldEvent evt) {

        if (evt.getEntity() instanceof AbstractArrowEntity) {

            final AbstractArrowEntity abstractarrowentity = (AbstractArrowEntity) evt.getEntity();
            if (abstractarrowentity.getShooter() instanceof PlayerEntity) {

                PlayerEntity player = (PlayerEntity) abstractarrowentity.getShooter();
                ItemStack bow = player.getActiveItemStack();
                ItemStack crossbow = player.getHeldItem(player.getActiveHand());

                if (bow.getItem() instanceof BowItem) {
                    this.addBowArrowStats(abstractarrowentity, bow);
                } else if (crossbow.getItem() instanceof CrossbowItem) {
                    this.addCrossbowArrowStats(abstractarrowentity, crossbow);
                }
            }
        }
    }

    private void addCrossbowArrowStats(AbstractArrowEntity abstractarrowentity, ItemStack stack) {

        // power enchantment for crossbows
        int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
        if (j > 0) {
            abstractarrowentity.setDamage(abstractarrowentity.getDamage() + (double)j * 0.5D + 0.5D);
        }

        // punch enchantment for crossbows
        int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
        if (k > 0) {
            abstractarrowentity.setKnockbackStrength(k);
        }

        // flame enchantment for crossbows
        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0) {
            abstractarrowentity.setFire(100);
        }

        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0 && abstractarrowentity instanceof ArrowEntity
                && ((ArrowEntity) abstractarrowentity).getArrowStack().getItem() == Items.ARROW) {
            abstractarrowentity.pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
        }
    }

    private void addBowArrowStats(AbstractArrowEntity abstractarrowentity, ItemStack stack) {

        // piercing enchantment for bows
        int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.PIERCING, stack);
        if (i > 0) {
            abstractarrowentity.func_213872_b((byte) i);
        }
    }

}
