package com.fuzs.swordblockingcombat.handler;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MiscellaneousHandler {

    public MiscellaneousHandler() {

        // new stack size as in combat snapshots
        Items.SNOWBALL.maxStackSize = 64;
        // change back values to pre combat update era
        ((SwordItem) Items.DIAMOND_SWORD).attackDamage = 7.0F;
        ((SwordItem) Items.IRON_SWORD).attackDamage = 6.0F;
        ((SwordItem) Items.STONE_SWORD).attackDamage = 5.0F;
        ((SwordItem) Items.WOODEN_SWORD).attackDamage = 4.0F;
        ((SwordItem) Items.GOLDEN_SWORD).attackDamage = 4.0F;
        ((ToolItem) Items.DIAMOND_AXE).attackDamage = 6.0F;
        ((ToolItem) Items.IRON_AXE).attackDamage = 5.0F;
        ((ToolItem) Items.STONE_AXE).attackDamage = 4.0F;
        ((ToolItem) Items.WOODEN_AXE).attackDamage = 3.0F;
        ((ToolItem) Items.GOLDEN_AXE).attackDamage = 3.0F;
        ((ToolItem) Items.DIAMOND_PICKAXE).attackDamage = 5.0F;
        ((ToolItem) Items.IRON_PICKAXE).attackDamage = 4.0F;
        ((ToolItem) Items.STONE_PICKAXE).attackDamage = 3.0F;
        ((ToolItem) Items.WOODEN_PICKAXE).attackDamage = 2.0F;
        ((ToolItem) Items.GOLDEN_PICKAXE).attackDamage = 2.0F;
        ((ToolItem) Items.DIAMOND_SHOVEL).attackDamage = 4.0F;
        ((ToolItem) Items.IRON_SHOVEL).attackDamage = 3.0F;
        ((ToolItem) Items.STONE_SHOVEL).attackDamage = 2.0F;
        ((ToolItem) Items.WOODEN_SHOVEL).attackDamage = 1.0F;
        ((ToolItem) Items.GOLDEN_SHOVEL).attackDamage = 1.0F;
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onAttackEntity(final AttackEntityEvent evt) {

        // disable cooldown right before every attack
        evt.getPlayer().ticksSinceLastSwing = (int) Math.ceil(evt.getPlayer().getCooldownPeriod());
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onCriticalHit(final CriticalHitEvent evt) {

        // prevent sweeping from taking effect unless the enchantment is in place
        if (EnchantmentHelper.getSweepingDamageRatio(evt.getPlayer()) == 0.0F) {
            evt.getPlayer().onGround = false;
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onLivingHurt(final LivingHurtEvent evt) {

        // immediately reset damage immunity after being hit by any projectile
        if (ConfigBuildHandler.GENERAL_CONFIG.noProjectileResistance.get() && evt.getSource().isProjectile()) {
            evt.getEntity().hurtResistantTime = 0;
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onItemUseEnd(final PlayerInteractEvent.RightClickItem evt) {

        if (evt.getEntityLiving() instanceof PlayerEntity) {

            Item item = evt.getItemStack().getItem();
            if (item instanceof SnowballItem || item instanceof EggItem) {

                ((PlayerEntity) evt.getEntityLiving()).getCooldownTracker().setCooldown(item, 4);
            }
        }
    }

}
