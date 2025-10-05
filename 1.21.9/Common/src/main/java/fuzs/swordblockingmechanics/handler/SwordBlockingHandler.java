package fuzs.swordblockingmechanics.handler;

import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.api.event.v1.data.MutableDouble;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import fuzs.puzzleslib.api.item.v2.ItemHelper;
import fuzs.swordblockingmechanics.SwordBlockingMechanics;
import fuzs.swordblockingmechanics.attachment.ParryCooldown;
import fuzs.swordblockingmechanics.config.ServerConfig;
import fuzs.swordblockingmechanics.init.ModRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SwordBlockingHandler {
    public static final int DEFAULT_ITEM_USE_DURATION = 72_000;

    public static EventResultHolder<InteractionResult> onUseItem(Player player, Level level, InteractionHand hand) {
        if (!SwordBlockingMechanics.CONFIG.get(ServerConfig.class).allowBlockingAndParrying) {
            return EventResultHolder.pass();
        }
        if (player.getItemInHand(hand).is(ModRegistry.CAN_PERFORM_SWORD_BLOCKING_ITEM_TAG)) {
            if (!SwordBlockingMechanics.CONFIG.get(ServerConfig.class).prioritizeOffHand ||
                    hand != InteractionHand.MAIN_HAND || canActivateBlocking(player, player.getOffhandItem())) {
                InteractionHand otherHand =
                        hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
                if (!SwordBlockingMechanics.CONFIG.get(ServerConfig.class).requireBothHands ||
                        player.getItemInHand(otherHand).isEmpty()) {
                    if (player.getAttackStrengthScale(0.0F) >=
                            SwordBlockingMechanics.CONFIG.get(ServerConfig.class).requiredAttackStrength) {
                        player.startUsingItem(hand);
                        // cause reequip animation, but don't swing hand, not to be confused with InteractionResult#SUCCESS; this is also what shields do
                        return EventResultHolder.interrupt(InteractionResult.CONSUME);
                    }
                }
            }
        }
        return EventResultHolder.pass();
    }

    public static EventResult onUseItemStart(LivingEntity entity, ItemStack stack, MutableInt remainingUseDuration) {
        if (!SwordBlockingMechanics.CONFIG.get(ServerConfig.class).allowBlockingAndParrying) return EventResult.PASS;
        if (entity instanceof Player && stack.is(ModRegistry.CAN_PERFORM_SWORD_BLOCKING_ITEM_TAG)) {
            remainingUseDuration.accept(DEFAULT_ITEM_USE_DURATION);
        }
        return EventResult.PASS;
    }

    public static EventResult onUseItemStop(LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        if (!SwordBlockingMechanics.CONFIG.get(ServerConfig.class).allowBlockingAndParrying) return EventResult.PASS;
        if (entity instanceof Player player && stack.is(ModRegistry.CAN_PERFORM_SWORD_BLOCKING_ITEM_TAG)) {
            ParryCooldown.resetCooldownTicks(player);
        }
        return EventResult.PASS;
    }

    public static EventResult onLivingAttack(LivingEntity entity, DamageSource damageSource, float damageAmount) {

        if (entity.level().isClientSide() || !(entity instanceof Player player) || !isActiveItemStackBlocking(player)) {
            return EventResult.PASS;
        }

        if (damageAmount > 0.0F && canBlockDamageSource(player, damageSource)) {

            boolean parryIsActive = getParryStrengthScale(player) > 0.0;
            if (parryIsActive || SwordBlockingMechanics.CONFIG.get(ServerConfig.class).deflectProjectiles &&
                    damageSource.is(DamageTypeTags.IS_PROJECTILE)) {

                if (parryIsActive && SwordBlockingMechanics.CONFIG.get(ServerConfig.class).damageSwordOnParry ||
                        !parryIsActive && SwordBlockingMechanics.CONFIG.get(ServerConfig.class).damageSwordOnBlock) {

                    hurtSwordInUse(player, damageAmount);
                }

                if (parryIsActive && !damageSource.is(DamageTypeTags.IS_PROJECTILE) &&
                        damageSource.getDirectEntity() instanceof LivingEntity directEntity) {

                    directEntity.knockback(SwordBlockingMechanics.CONFIG.get(ServerConfig.class).parryKnockbackStrength,
                            player.getX() - directEntity.getX(),
                            player.getZ() - directEntity.getZ());
                }

                player.level()
                        .playSound(null,
                                player.getX(),
                                player.getY(),
                                player.getZ(),
                                ModRegistry.ITEM_SWORD_BLOCK_SOUND_EVENT.value(),
                                player.getSoundSource(),
                                1.0F,
                                0.8F + player.level().getRandom().nextFloat() * 0.4F);

                return EventResult.INTERRUPT;
            }
        }

        return EventResult.PASS;
    }

    public static EventResult onLivingHurt(LivingEntity entity, DamageSource source, MutableFloat amount) {
        if (entity instanceof Player player && isActiveItemStackBlocking(player)) {
            if (canBlockDamageSource(player, source) && amount.getAsFloat() > 0.0F) {
                if (SwordBlockingMechanics.CONFIG.get(ServerConfig.class).damageSwordOnBlock) {
                    hurtSwordInUse(player, amount.getAsFloat());
                }
                double damageAfterBlock = 1.0 + amount.getAsFloat() *
                        (1.0 - SwordBlockingMechanics.CONFIG.get(ServerConfig.class).blockedDamage);
                amount.mapFloat(v -> Math.min(v, (float) Math.floor(damageAfterBlock)));
            }
        }
        return EventResult.PASS;
    }

    public static EventResult onLivingKnockBack(LivingEntity entity, MutableDouble knockbackStrength, MutableDouble ratioX, MutableDouble ratioZ) {
        if (entity instanceof Player player && isActiveItemStackBlocking(player)) {
            float knockBackMultiplier =
                    1.0F - (float) SwordBlockingMechanics.CONFIG.get(ServerConfig.class).knockbackReduction;
            if (knockBackMultiplier == 0.0F) {
                return EventResult.INTERRUPT;
            } else {
                knockbackStrength.mapDouble((double v) -> v * knockBackMultiplier);
            }
        }
        return EventResult.PASS;
    }

    private static boolean canBlockDamageSource(Player player, DamageSource source) {
        Entity entity = source.getDirectEntity();
        if (entity instanceof AbstractArrow arrow) {
            if (arrow.getPierceLevel() > 0) {
                return false;
            }
        }
        if (!source.is(DamageTypeTags.BYPASSES_ARMOR)) {
            Vec3 position = source.getSourcePosition();
            if (position != null) {
                Vec3 viewVector = player.getViewVector(1.0F);
                position = position.vectorTo(player.position()).normalize();
                position = new Vec3(position.x, 0.0, position.z);
                return position.dot(viewVector) < -Math.cos(
                        SwordBlockingMechanics.CONFIG.get(ServerConfig.class).protectionArc * Math.PI * 0.5 / 180.0);
            }
        }
        return false;
    }

    public static boolean isActiveItemStackBlocking(Player player) {
        if (!SwordBlockingMechanics.CONFIG.get(ServerConfig.class).allowBlockingAndParrying) return false;
        return player.isUsingItem() && player.getUseItem().is(ModRegistry.CAN_PERFORM_SWORD_BLOCKING_ITEM_TAG);
    }

    public static double getParryStrengthScale(Player player) {
        ParryCooldown parryCooldown = ModRegistry.PARRY_COOLDOWN_ATTACHMENT_TYPE.getOrDefault(player,
                ParryCooldown.ZERO);
        if (parryCooldown.isCooldownActive()) {
            return -parryCooldown.getCooldownProgress();
        } else if (isActiveItemStackBlocking(player)) {
            double currentUseDuration = DEFAULT_ITEM_USE_DURATION - player.getUseItemRemainingTicks();
            double parryStrengthScale =
                    1.0 - currentUseDuration / SwordBlockingMechanics.CONFIG.get(ServerConfig.class).parryWindow;
            return Mth.clamp(parryStrengthScale, 0.0, 1.0);
        } else {
            return 0.0;
        }
    }

    private static void hurtSwordInUse(Player player, float damageAmount) {
        if (damageAmount >= 3.0F) {
            int lostDurability = 1 + Mth.floor(damageAmount);
            InteractionHand interactionHand = player.getUsedItemHand();
            ItemHelper.hurtAndBreak(player.getUseItem(), lostDurability, player, interactionHand);
            if (player.getUseItem().isEmpty()) {
                if (interactionHand == InteractionHand.MAIN_HAND) {
                    player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                } else {
                    player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                }

                player.stopUsingItem();
                player.playSound(SoundEvents.ITEM_BREAK.value(), 0.8F, 0.8F + player.level().random.nextFloat() * 0.4F);
            }
        }
    }

    public static boolean canActivateBlocking(Player player, ItemStack itemStack) {
        if (itemStack.is(ModRegistry.OVERRIDES_SWORD_IN_OFFHAND_BLOCKING_ITEM_TAG)) return false;
        return switch (itemStack.getUseAnimation()) {
            case BLOCK, SPYGLASS, BRUSH -> false;
            case EAT, DRINK -> !itemStack.has(DataComponents.FOOD) ||
                    !player.canEat(itemStack.get(DataComponents.FOOD).canAlwaysEat());
            case BOW, CROSSBOW -> player.getProjectile(itemStack).isEmpty();
            case SPEAR -> itemStack.getDamageValue() >= itemStack.getMaxDamage() - 1 ||
                    EnchantmentHelper.getTridentSpinAttackStrength(itemStack, player) > 0.0F &&
                            !player.isInWaterOrRain();
            case TOOT_HORN -> player.getCooldowns().isOnCooldown(itemStack);
            default -> true;
        };
    }
}
