package com.fuzs.swordblockingcombat.common;

import com.fuzs.swordblockingcombat.common.helper.ItemBlockingHelper;
import com.fuzs.swordblockingcombat.config.ConfigValueHolder;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.function.Predicate;

public class InitiateBlockHandler {

    private final ItemBlockingHelper blockingHelper = new ItemBlockingHelper();

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRightClickItem(final PlayerInteractEvent.RightClickItem evt) {

        PlayerEntity player = evt.getPlayer();
        if (ItemBlockingHelper.getCanStackBlock(evt.getItemStack())) {

            ItemStack stack = player.getHeldItemOffhand();
            Predicate<ItemStack> noAction = item -> item.getItem().getUseAction(item) == UseAction.NONE;
            Predicate<ItemStack> food = foodItem -> foodItem.getItem().getFood() != null && !player.canEat(foodItem.getItem().getFood().canEatWhenFull());
            Predicate<ItemStack> bow = bowItem -> {
                UseAction action = bowItem.getItem().getUseAction(bowItem);
                return (action == UseAction.BOW || action == UseAction.CROSSBOW) && player.findAmmo(bowItem).isEmpty();
            };
            Predicate<ItemStack> spear = tridentItem -> {
                UseAction action = tridentItem.getItem().getUseAction(tridentItem);
                return action == UseAction.SPEAR && (tridentItem.getDamage() >= tridentItem.getMaxDamage() - 1 || EnchantmentHelper.getRiptideModifier(tridentItem) > 0 && !player.isWet());
            };

            if (noAction.test(stack) || food.test(stack) || bow.test(stack) || spear.test(stack)) {

                player.setActiveHand(evt.getHand());
                evt.setCancellationResult(ActionResultType.SUCCESS);
                evt.setCanceled(true);
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onItemUseStart(final LivingEntityUseItemEvent.Start evt) {

        if (evt.getEntityLiving() instanceof PlayerEntity && ItemBlockingHelper.getCanStackBlock(evt.getItem())) {

            evt.setDuration(this.blockingHelper.swordUseDuration);
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onLivingAttack(final LivingAttackEvent evt) {

        if (ConfigValueHolder.SWORD_BLOCKING.deflectProjectiles && evt.getEntityLiving() instanceof PlayerEntity && evt.getSource().getImmediateSource() instanceof AbstractArrowEntity
                && this.blockingHelper.getIsBlocking((PlayerEntity) evt.getEntityLiving())) {

            evt.setCanceled(true);
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onLivingHurt(final LivingHurtEvent evt) {

        if (evt.getEntityLiving() instanceof PlayerEntity) {

            PlayerEntity player = (PlayerEntity) evt.getEntityLiving();
            float damage = evt.getAmount();

            if (damage > 0.0F && this.blockingHelper.getIsBlocking(player)) {

                this.blockingHelper.damageSword(player, damage);

                if (!evt.getSource().isUnblockable()) {

                    float reducedAmount = (1.0F + evt.getAmount()) * (1.0F - ConfigValueHolder.SWORD_BLOCKING.blocked);
                    if (reducedAmount <= 1.0F) {
                        reducedAmount = 0.0F;
                    }

                    evt.setAmount(Math.min(evt.getAmount(), reducedAmount));
                }
            }
        }
    }

}
