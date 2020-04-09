package com.fuzs.swordblockingcombat.common.util;

import com.fuzs.materialmaster.api.SyncProvider;
import com.fuzs.swordblockingcombat.config.ConfigBuildHandler;
import com.google.common.collect.Sets;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Set;

public class BlockingItemHelper {

    @SyncProvider(path = {"sword_blocking", "Blocking Exclusion List"})
    public static Set<Item> exclude = Sets.newHashSet();
    @SyncProvider(path = {"sword_blocking", "Blocking Inclusion List"})
    public static Set<Item> include = Sets.newHashSet();

    public static final int SWORD_USE_DURATION = 72000;
    private Item activeItem = Items.AIR;
    private boolean activeBlock;

    public void damageSword(PlayerEntity player, float damage) {

        if (ConfigBuildHandler.DAMAGE_SWORD.get() && damage >= 3.0F) {

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

    public int getBlockUseDuration(PlayerEntity player) {

        return SWORD_USE_DURATION - player.getItemInUseCount();
    }

    public boolean isActiveItemStackBlocking(PlayerEntity player) {

        return player.isHandActive() && this.canItemStackBlock(player.getActiveItemStack());
    }

    public boolean canItemStackBlock(ItemStack stack) {

        Item item = stack.getItem();
        if (item != this.activeItem) {

            this.activeItem = item;
            if (exclude.contains(item)) {

                this.activeBlock = false;
            } else if (item instanceof SwordItem) {

                this.activeBlock = true;
            } else {

                this.activeBlock = include.contains(item);
            }
        }

        return this.activeBlock;
    }

    /**
     * modeled after net.minecraft.entity.LivingEntity#canBlockDamageSource
     */
    public boolean canBlockDamageSource(PlayerEntity player, DamageSource damageSourceIn) {

        Entity entity = damageSourceIn.getImmediateSource();
        if (entity instanceof AbstractArrowEntity) {

            AbstractArrowEntity abstractarrowentity = (AbstractArrowEntity)entity;
            if (abstractarrowentity.getPierceLevel() > 0) {

                return false;
            }
        }

        if (!damageSourceIn.isUnblockable()) {

            Vec3d vec3d2 = damageSourceIn.getDamageLocation();
            if (vec3d2 != null) {

                Vec3d vec3d = player.getLook(1.0F);
                Vec3d vec3d1 = vec3d2.subtractReverse(player.getPositionVec()).normalize();
                vec3d1 = new Vec3d(vec3d1.x, 0.0, vec3d1.z);
                return vec3d1.dotProduct(vec3d) * Math.PI < Math.PI / 3.6; // 100 degrees protection arc
            }
        }

        return false;
    }

}
