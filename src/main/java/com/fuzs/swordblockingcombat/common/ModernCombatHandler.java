package com.fuzs.swordblockingcombat.common;

import com.fuzs.swordblockingcombat.config.ConfigValueHolder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.function.Predicate;

public class ModernCombatHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onLivingHurt(final LivingHurtEvent evt) {

        // immediately reset damage immunity after being hit by any projectile
        if (ConfigValueHolder.MODERN_COMBAT.noProjectileResistance && evt.getSource().isProjectile()) {
            evt.getEntity().hurtResistantTime = 0;
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onItemUseEnd(final PlayerInteractEvent.RightClickItem evt) {

        this.addItemCooldown(evt.getEntityLiving(), evt.getItemStack(), value -> value == 0);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onItemUseFinish(final LivingEntityUseItemEvent.Finish evt) {

        this.addItemCooldown(evt.getEntityLiving(), evt.getItem(), value -> value > 0);
    }

    public static int hitEntityAmount(ToolItem instance) {
        return instance instanceof AxeItem ? 1 : 2;
    }

    private void addItemCooldown(LivingEntity entityLiving, ItemStack stack, Predicate<Integer> useDuration) {

        if (entityLiving instanceof PlayerEntity) {

            Item item = stack.getItem();
            if (useDuration.test(item.getUseDuration(stack))) {

                Double delay = ConfigValueHolder.MODERN_COMBAT.itemDelay.get(item);
                if (delay != null) {

                    ((PlayerEntity) entityLiving).getCooldownTracker().setCooldown(item, delay.intValue());
                }
            }
        }
    }

}
