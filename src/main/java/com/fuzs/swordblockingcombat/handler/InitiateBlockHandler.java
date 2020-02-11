package com.fuzs.swordblockingcombat.handler;

import com.fuzs.swordblockingcombat.helper.EligibleItemHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.function.Predicate;

public class InitiateBlockHandler {

    private static final int SWORD_USE_DURATION = 72000;

    private EligibleItemHelper eligibleItem = new EligibleItemHelper();

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onAttackEntity(final AttackEntityEvent evt) {

        evt.getPlayer().ticksSinceLastSwing = (int) Math.ceil(evt.getPlayer().getCooldownPeriod());
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRightClickItem(final PlayerInteractEvent.RightClickItem evt) {

        PlayerEntity player = evt.getPlayer();
        if (this.eligibleItem.test(evt.getItemStack(), evt.getHand())) {

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

        if (this.eligibleItem.test(evt.getItem(), evt.getEntityLiving().getActiveHand())) {

            evt.setDuration(SWORD_USE_DURATION);
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onProjectileImpact(final ProjectileImpactEvent.Arrow evt) {

        final AbstractArrowEntity arrow = evt.getArrow();

        if (evt.getRayTraceResult().getType() == RayTraceResult.Type.ENTITY) {
            EntityRayTraceResult rayTrace = (EntityRayTraceResult) evt.getRayTraceResult();
            if (rayTrace.getEntity() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) rayTrace.getEntity();

                if (this.isBlocking(player)) {
                    Vec3d playerVec3 = player.getLookVec();
//                    arrow.knockBack(player, 0.5F, player.func_226277_ct_() - player.func_226277_ct_(), player.func_226281_cx_() - player.func_226281_cx_());

                    arrow.shoot(playerVec3.x, playerVec3.y, playerVec3.z, 1.1F, 0.05F);

                    arrow.shootingEntity = player.getUniqueID();

                    evt.setCanceled(true);
                }
            }
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onLivingHurt(final LivingHurtEvent evt) {

        if (evt.getEntityLiving() instanceof PlayerEntity) {

            PlayerEntity player = (PlayerEntity) evt.getEntityLiving();
            float damage = evt.getAmount();

            if (damage > 0.0F && this.isBlocking(player)) {

                this.damageSword(player, damage);

                if (!evt.getSource().isUnblockable()) {

                    float reducedAmount = (1.0F + evt.getAmount()) * (1.0F - ConfigBuildHandler.GENERAL_CONFIG.blocked.get().floatValue());
                    if (reducedAmount <= 1.0F) {
                        reducedAmount = 0.0F;
                    }

                    evt.setAmount(Math.min(evt.getAmount(), reducedAmount));
                }
            }
        }
    }

    private void damageSword(PlayerEntity player, float damage) {

        if (ConfigBuildHandler.GENERAL_CONFIG.damageSword.get() && damage >= 3.0F) {

            ItemStack stack = player.getActiveItemStack();
            Hand hand = player.getActiveHand();
            int i = 1 + MathHelper.floor(damage);

            stack.damageItem(i, player, entity -> {
                entity.sendBreakAnimation(hand);
                net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, stack, hand);
            });

            if (stack.isEmpty()) {

                player.setItemStackToSlot(hand == Hand.MAIN_HAND ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND, ItemStack.EMPTY);
                player.playSound(SoundEvents.ENTITY_ITEM_BREAK, 0.8F, 0.8F + player.world.rand.nextFloat() * 0.4F);
            }
        }
    }

    private boolean isBlocking(PlayerEntity player) {

        boolean ready = SWORD_USE_DURATION - player.getItemInUseCount() >= ConfigBuildHandler.GENERAL_CONFIG.blockDelay.get();
        return ready && this.eligibleItem.test(player.getActiveItemStack(), player.getActiveHand());
    }

}
