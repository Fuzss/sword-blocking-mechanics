package com.fuzs.swordblockingmechanics.handler;

import com.fuzs.swordblockingmechanics.config.ConfigBuildHandler;
import com.fuzs.swordblockingmechanics.element.SwordBlockingElement;
import com.fuzs.swordblockingmechanics.util.BlockingHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class InitiateBlockHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRightClickItem(final PlayerInteractEvent.RightClickItem evt) {

        PlayerEntity player = evt.getPlayer();
        if (BlockingHelper.canItemStackBlock(evt.getItemStack())) {

            ItemStack stack = player.getHeldItemOffhand();
            if (evt.getHand() == Hand.MAIN_HAND && !stack.isEmpty()) {

                if (stack.getItem().isIn(SwordBlockingElement.OFF_HAND_BLACKLIST_TAG)) {

                    return;
                } else if (!this.canActivateBlocking(stack, player)) {

                    return;
                }
            }

            Hand oppositeHand = evt.getHand() == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
            if (!ConfigBuildHandler.REQUIRE_BOTH_HANDS.get() || player.getHeldItem(oppositeHand).isEmpty()) {

                evt.setCanceled(true);
                player.setActiveHand(evt.getHand());
                // cause reequip animation, but don't swing hand, not to be confused with ActionResultType#SUCCESS
                evt.setCancellationResult(ActionResultType.CONSUME);
            }
        }
    }

    private boolean canActivateBlocking(ItemStack stack, PlayerEntity player) {

        switch (stack.getUseAction()) {

            case BLOCK:

                return false;
            case EAT:
            case DRINK:

                return stack.getItem().getFood() == null || !player.canEat(stack.getItem().getFood().canEatWhenFull());
            case BOW:
            case CROSSBOW:

                return player.findAmmo(stack).isEmpty();
            case SPEAR:

                return stack.getDamage() >= stack.getMaxDamage() - 1 || EnchantmentHelper.getRiptideModifier(stack) > 0 && !player.isWet();
        }

        return true;
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onItemUseStart(final LivingEntityUseItemEvent.Start evt) {

        if (evt.getEntityLiving() instanceof PlayerEntity && BlockingHelper.canItemStackBlock(evt.getItem())) {

            evt.setDuration(72000);
        }
    }

    @SuppressWarnings({"unused", "ConstantConditions"})
    @SubscribeEvent
    public void onLivingAttack(final LivingAttackEvent evt) {

        if (evt.getEntityLiving().getEntityWorld().isRemote || !(evt.getEntityLiving() instanceof PlayerEntity)) {

            return;
        }

        PlayerEntity player = (PlayerEntity) evt.getEntityLiving();
        DamageSource damageSource = evt.getSource();
        if (BlockingHelper.isActiveItemStackBlocking(player)) {

            if (BlockingHelper.getBlockUseDuration(player) < ConfigBuildHandler.PARRY_WINDOW.get()) {

                float damageAmount = evt.getAmount();
                if (damageAmount > 0.0F && BlockingHelper.canBlockDamageSource(player, damageSource)) {

                    BlockingHelper.dealDamageToSword(player, damageAmount);
                    if (!damageSource.isProjectile()) {

                        if (damageSource.getImmediateSource() instanceof LivingEntity) {

                            LivingEntity entity = (LivingEntity) damageSource.getImmediateSource();
                            entity.applyKnockback(0.5F, player.getPosX() - entity.getPosX(), player.getPosZ() - entity.getPosZ());
                        }
                    }

                    // play shield block sound on client
                    player.world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SwordBlockingElement.ITEM_SWORD_BLOCK_SOUND, player.getSoundCategory(), 1.0F, 0.8F + player.world.rand.nextFloat() * 0.4F);
                    evt.setCanceled(true);
                }
            }

            boolean isNotPiercing = damageSource.getImmediateSource() instanceof AbstractArrowEntity && ((AbstractArrowEntity) damageSource.getImmediateSource()).getPierceLevel() == 0;
            if (ConfigBuildHandler.DEFLECT_PROJECTILES.get() && isNotPiercing) {

                evt.setCanceled(true);
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onLivingHurt(final LivingHurtEvent evt) {

        if (evt.getEntityLiving() instanceof PlayerEntity) {

            PlayerEntity player = (PlayerEntity) evt.getEntityLiving();
            float damageAmount = evt.getAmount();
            if (damageAmount > 0.0F && BlockingHelper.isActiveItemStackBlocking(player)) {

                BlockingHelper.dealDamageToSword(player, damageAmount);
                if (!evt.getSource().isUnblockable()) {

                    float reducedAmount = 1.0F + evt.getAmount() * (1.0F - ConfigBuildHandler.BLOCKED_AMOUNT.get().floatValue());
                    if (reducedAmount <= 1.0F) {

                        reducedAmount = 0.0F;
                    }

                    evt.setAmount(Math.min(evt.getAmount(), reducedAmount));
                }
            }
        }
    }

}
