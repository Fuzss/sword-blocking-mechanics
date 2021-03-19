package com.fuzs.swordblockingmechanics.util;

import com.fuzs.swordblockingmechanics.config.ConfigBuildHandler;
import com.fuzs.swordblockingmechanics.element.SwordBlockingElement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class BlockingHelper {

    public static int getBlockUseDuration(PlayerEntity player) {

        return 72000 - player.getItemInUseCount();
    }

    public static boolean isActiveItemStackBlocking(PlayerEntity player) {

        return player.isHandActive() && canItemStackBlock(player.getActiveItemStack());
    }

    public static boolean canItemStackBlock(ItemStack blockingStack) {

        Item blockingItem = blockingStack.getItem();
        if (blockingItem.isIn(SwordBlockingElement.EXCLUDED_SWORDS_TAG)) {

            return false;
        } else if (blockingItem instanceof SwordItem) {

            return true;
        } else {

            return blockingItem.isIn(SwordBlockingElement.INCLUDED_SWORDS_TAG);
        }
    }

    // modeled after LivingEntity#canBlockDamageSource
    public static boolean canBlockDamageSource(PlayerEntity player, DamageSource damageSourceIn) {

        Entity sourceEntity = damageSourceIn.getImmediateSource();
        // check for piercing arrow
        if (sourceEntity instanceof AbstractArrowEntity) {

            AbstractArrowEntity abstractarrowentity = (AbstractArrowEntity) sourceEntity;
            if (abstractarrowentity.getPierceLevel() > 0) {

                return false;
            }
        }

        if (!damageSourceIn.isUnblockable()) {

            Vector3d damageLocation = damageSourceIn.getDamageLocation();
            if (damageLocation != null) {

                Vector3d damageDirection = damageLocation.subtractReverse(player.getPositionVec()).normalize();
                damageDirection = new Vector3d(damageDirection.x, 0.0, damageDirection.z);
                Vector3d playerLook = player.getLook(1.0F);

                // 100 degrees protection arc
                return damageDirection.dotProduct(playerLook) * Math.PI < Math.PI / 3.6;
            }
        }

        return false;
    }

    public static void dealDamageToSword(PlayerEntity player, float damage) {

        if (ConfigBuildHandler.DAMAGE_SWORD.get() && damage >= 3.0F) {

            ItemStack swordStack = player.getActiveItemStack();
            Hand activeHand = player.getActiveHand();
            int damageAmount = 1 + MathHelper.floor(damage);
            swordStack.damageItem(damageAmount, player, entity -> {

                entity.sendBreakAnimation(activeHand);
                net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, swordStack, activeHand);
            });

            if (swordStack.isEmpty()) {

                EquipmentSlotType swordSlotType = activeHand == Hand.MAIN_HAND ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND;
                player.setItemStackToSlot(swordSlotType, ItemStack.EMPTY);
                player.resetActiveHand();
                player.playSound(SoundEvents.ENTITY_ITEM_BREAK, 0.8F, 0.8F + player.world.rand.nextFloat() * 0.4F);
            }
        }
    }

}
