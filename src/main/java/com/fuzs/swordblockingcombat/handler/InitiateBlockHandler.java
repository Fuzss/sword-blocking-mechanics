package com.fuzs.swordblockingcombat.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class InitiateBlockHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem evt) {

        if (evt.getItemStack().getItem() instanceof SwordItem) {
            ItemStack stack = evt.getPlayer().getHeldItemOffhand();
            if (stack.getItem().getUseAction(stack) == UseAction.NONE) {
                evt.getPlayer().setActiveHand(evt.getHand());
                evt.setCancellationResult(ActionResultType.SUCCESS);
                evt.setCanceled(true);
            }
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onItemUseStart(LivingEntityUseItemEvent.Start evt) {

        if (evt.getItem().getItem() instanceof SwordItem) {
            evt.setDuration(72000);
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent evt) {

        if (evt.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) evt.getEntityLiving();
            if (!evt.getSource().isUnblockable() && player.getActiveItemStack().getItem() instanceof SwordItem && evt.getAmount() > 0.0F) {
                evt.setAmount((1.0F + evt.getAmount()) * 0.5F);
            }
        }

    }

}
