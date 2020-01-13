package com.fuzs.swordblockingcombat.handler;

import com.fuzs.swordblockingcombat.helper.EligibleItemHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class InitiateBlockHandler {

    private static ItemStack activeItemStack = ItemStack.EMPTY;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem evt) {

        if (EligibleItemHelper.isItemEligible(evt.getItemStack())) {
            PlayerEntity player = evt.getPlayer();
            ItemStack stack = player.getHeldItemOffhand();
            Item item = stack.getItem();
            if (item.getUseAction(stack) == UseAction.NONE || item.getFood() != null && !player.canEat(item.getFood().canEatWhenFull())) {
                player.setActiveHand(evt.getHand());
                evt.setCancellationResult(ActionResultType.SUCCESS);
                evt.setCanceled(true);
            }
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onItemUseStart(LivingEntityUseItemEvent.Start evt) {

        if (EligibleItemHelper.isItemEligible(evt.getItem())) {
            evt.setDuration(72000);
            activeItemStack = evt.getItem();
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent evt) {

        if (evt.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) evt.getEntityLiving();
            if (!evt.getSource().isUnblockable() && EligibleItemHelper.isItemEligible(player.getActiveItemStack()) && evt.getAmount() > 0.0F) {
                float amount = (1.0F + evt.getAmount()) * ConfigBuildHandler.GENERAL_CONFIG.blocked.get().floatValue();
                evt.setAmount(Math.min(evt.getAmount(), amount));
            }
        }

    }

    protected void damageShield(PlayerEntity player, float damage) {
        final ItemStack activeItemStack = player.getActiveItemStack();
        if (damage >= 3.0F && activeItemStack.isShield(player)) {
            int i = 1 + MathHelper.floor(damage);
            Hand hand = player.getActiveHand();
            activeItemStack.damageItem(i, player, (p_213833_1_) -> {
                p_213833_1_.sendBreakAnimation(hand);
                net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, activeItemStack, hand);
            });
            if (activeItemStack.isEmpty()) {
                if (hand == Hand.MAIN_HAND) {
                    player.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
                } else {
                    player.setItemStackToSlot(EquipmentSlotType.OFFHAND, ItemStack.EMPTY);
                }
                // activeItemStack = ItemStack.EMPTY;
                player.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + player.world.rand.nextFloat() * 0.4F);
            }
        }

    }

    public static boolean isBlocking(PlayerEntity player) {
        return activeItemStack == player.getActiveItemStack();
    }

}
