package com.fuzs.swordblockingcombat.common.handler;

import com.fuzs.materialmaster.api.SyncProvider;
import com.fuzs.swordblockingcombat.common.util.BlockingItemHelper;
import com.fuzs.swordblockingcombat.config.ConfigBuildHandler;
import com.fuzs.swordblockingcombat.registry.SwordBlockingRegistry;
import com.google.common.collect.Sets;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Set;
import java.util.function.Predicate;

public class InitiateBlockHandler {

    @SyncProvider(path = {"sword_blocking", "Off-Hand Blacklist"})
    public static Set<Item> blacklist = Sets.newHashSet();

    private final BlockingItemHelper blockingHelper = new BlockingItemHelper();

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRightClickItem(final PlayerInteractEvent.RightClickItem evt) {

        PlayerEntity player = evt.getPlayer();
        if (this.blockingHelper.canItemStackBlock(evt.getItemStack())) {

            boolean flag = true;
            ItemStack offhandStack = player.getHeldItemOffhand();
            if (evt.getHand() == Hand.MAIN_HAND && !offhandStack.isEmpty()) {

                flag = !blacklist.contains(offhandStack.getItem());
                Predicate<ItemStack> actionNone = item -> item.getItem().getUseAction(item) == UseAction.NONE;
                Predicate<ItemStack> foodNotHungry = foodItem -> foodItem.getItem().getFood() != null && !player.canEat(foodItem.getItem().getFood().canEatWhenFull());
                Predicate<ItemStack> bowNoAmmo = bowItem -> {
                    UseAction action = bowItem.getItem().getUseAction(bowItem);
                    return (action == UseAction.BOW || action == UseAction.CROSSBOW) && player.findAmmo(bowItem).isEmpty();
                };
                Predicate<ItemStack> spearNoUse = tridentItem -> {
                    UseAction action = tridentItem.getItem().getUseAction(tridentItem);
                    return action == UseAction.SPEAR && (tridentItem.getDamage() >= tridentItem.getMaxDamage() - 1 || EnchantmentHelper.getRiptideModifier(tridentItem) > 0 && !player.isWet());
                };

                flag = flag && (actionNone.test(offhandStack) || foodNotHungry.test(offhandStack) || bowNoAmmo.test(offhandStack) || spearNoUse.test(offhandStack));
            }

            Hand oppositeHand = evt.getHand() == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
            if (flag && (!ConfigBuildHandler.REQUIRE_BOTH_HANDS.get() || player.getHeldItem(oppositeHand).isEmpty())) {

                player.setActiveHand(evt.getHand());
                // cause reequip animation, but don't swing hand
                evt.setCancellationResult(ActionResultType.CONSUME);
                evt.setCanceled(true);
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onItemUseStart(final LivingEntityUseItemEvent.Start evt) {

        if (evt.getEntityLiving() instanceof PlayerEntity && this.blockingHelper.canItemStackBlock(evt.getItem())) {

            evt.setDuration(BlockingItemHelper.SWORD_USE_DURATION);
        }
    }

    @SuppressWarnings({"unused", "ConstantConditions"})
    @SubscribeEvent
    public void onLivingAttack(final LivingAttackEvent evt) {

        if (evt.getEntityLiving().getEntityWorld().isRemote() || !(evt.getEntityLiving() instanceof PlayerEntity)) {

            return;
        }

        DamageSource source = evt.getSource();
        PlayerEntity player = (PlayerEntity) evt.getEntityLiving();
        if (this.blockingHelper.isActiveItemStackBlocking(player)) {

            if (this.blockingHelper.getBlockUseDuration(player) < ConfigBuildHandler.PARRY_WINDOW.get()) {

                float amount = evt.getAmount();
                if (amount > 0.0F && this.blockingHelper.canBlockDamageSource(player, source)) {

                    this.blockingHelper.damageSword(player, amount);
                    if (!source.isProjectile()) {

                        if (source.getImmediateSource() instanceof LivingEntity) {

                            LivingEntity entity = (LivingEntity) source.getImmediateSource();
                            entity.knockBack(player, 0.5F, player.getPosX() - entity.getPosX(), player.getPosZ() - entity.getPosZ());
                        }
                    }

                    // play shield block sound on client
                    player.world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SwordBlockingRegistry.ITEM_SWORD_BLOCK, player.getSoundCategory(), 1.0F, 0.8F + player.world.rand.nextFloat() * 0.4F);
                    evt.setCanceled(true);
                }
            }

            if (ConfigBuildHandler.DEFLECT_PROJECTILES.get() && source.getImmediateSource() instanceof AbstractArrowEntity
                    && ((AbstractArrowEntity) source.getImmediateSource()).getPierceLevel() == 0) {

                evt.setCanceled(true);
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onLivingHurt(final LivingHurtEvent evt) {

        if (evt.getEntityLiving() instanceof PlayerEntity) {

            PlayerEntity player = (PlayerEntity) evt.getEntityLiving();
            float damage = evt.getAmount();
            if (damage > 0.0F && this.blockingHelper.isActiveItemStackBlocking(player)) {

                this.blockingHelper.damageSword(player, damage);
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
