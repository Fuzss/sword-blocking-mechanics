package com.fuzs.swordblockingcombat.common.helper;

import com.fuzs.swordblockingcombat.config.ConfigValueHolder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;

public class BlockingItemHelper {

    public final int swordUseDuration = 72000;

    public void damageSword(PlayerEntity player, float damage) {

        if (ConfigValueHolder.SWORD_BLOCKING.damageSword && damage >= 3.0F) {

            ItemStack stack = player.getActiveItemStack();
            Hand hand = player.getActiveHand();
            int i = 1 + MathHelper.floor(damage);

            stack.damageItem(i, player, entity -> {
                entity.sendBreakAnimation(hand);
                net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, stack, hand);
            });

            if (stack.isEmpty()) {

                player.setItemStackToSlot(hand == Hand.MAIN_HAND ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND, ItemStack.EMPTY);
                player.resetActiveHand();
                player.playSound(SoundEvents.ENTITY_ITEM_BREAK, 0.8F, 0.8F + player.world.rand.nextFloat() * 0.4F);
            }
        }
    }

    public boolean isActiveItemStackActuallyBlocking(PlayerEntity player) {

        boolean ready = this.swordUseDuration - player.getItemInUseCount() >= ConfigValueHolder.SWORD_BLOCKING.blockDelay;
        return ready && isActiveItemStackBlocking(player);
    }

    public static boolean isActiveItemStackBlocking(PlayerEntity player) {

        return player.isHandActive() && canItemStackBlock(player.getActiveItemStack());
    }

    public static boolean canItemStackBlock(ItemStack stack) {

        Item item = stack.getItem();
        if (ConfigValueHolder.SWORD_BLOCKING.exclude.contains(item)) {
            return false;
        }

        if (item instanceof SwordItem) {
            return true;
        }

        return ConfigValueHolder.SWORD_BLOCKING.include.contains(item);
    }

}
