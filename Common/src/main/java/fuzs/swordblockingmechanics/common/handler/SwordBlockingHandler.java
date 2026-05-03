package fuzs.swordblockingmechanics.common.handler;

import fuzs.puzzleslib.common.api.event.v1.core.EventResult;
import fuzs.puzzleslib.common.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.common.api.event.v1.data.MutableDouble;
import fuzs.puzzleslib.common.api.event.v1.data.MutableFloat;
import fuzs.puzzleslib.common.api.event.v1.data.MutableInt;
import fuzs.puzzleslib.common.api.item.v2.ItemHelper;
import fuzs.swordblockingmechanics.common.SwordBlockingMechanics;
import fuzs.swordblockingmechanics.common.attachment.ParryCooldown;
import fuzs.swordblockingmechanics.common.config.ServerConfig;
import fuzs.swordblockingmechanics.common.init.ModRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SwordBlockingHandler {
    public static final int DEFAULT_ITEM_USE_DURATION = 72_000;

    public static EventResultHolder<InteractionResult> onUseItem(Player player, Level level, InteractionHand interactionHand) {
        if (!SwordBlockingMechanics.CONFIG.get(ServerConfig.class).allowBlockingAndParrying) {
            return EventResultHolder.pass();
        }

        if (player.getItemInHand(interactionHand).is(ModRegistry.CAN_PERFORM_SWORD_BLOCKING_ITEM_TAG)) {
            if (!SwordBlockingMechanics.CONFIG.get(ServerConfig.class).prioritizeOffHand
                    || interactionHand != InteractionHand.MAIN_HAND || canStartBlocking(player,
                    player.getOffhandItem())) {
                InteractionHand otherHand = interactionHand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND :
                        InteractionHand.MAIN_HAND;
                if (!SwordBlockingMechanics.CONFIG.get(ServerConfig.class).requireBothHands || player.getItemInHand(
                        otherHand).isEmpty()) {
                    if (player.getAttackStrengthScale(0.0F)
                            >= SwordBlockingMechanics.CONFIG.get(ServerConfig.class).requiredAttackStrength) {
                        player.startUsingItem(interactionHand);
                        // cause reequip animation, but don't swing hand, not to be confused with InteractionResult#SUCCESS; this is also what shields do
                        return EventResultHolder.interrupt(InteractionResult.CONSUME);
                    }
                }
            }
        }

        return EventResultHolder.pass();
    }

    private static boolean canStartBlocking(Player player, ItemStack itemStack) {
        if (itemStack.is(ModRegistry.OVERRIDES_SWORD_IN_OFFHAND_BLOCKING_ITEM_TAG)) {
            return false;
        }

        return switch (itemStack.getUseAnimation()) {
            case BLOCK, SPYGLASS, BRUSH -> false;
            case EAT, DRINK -> !itemStack.has(DataComponents.FOOD) || !player.canEat(itemStack.get(DataComponents.FOOD)
                    .canAlwaysEat());
            case BOW, CROSSBOW -> player.getProjectile(itemStack).isEmpty();
            case TRIDENT -> itemStack.getDamageValue() >= itemStack.getMaxDamage() - 1
                    || EnchantmentHelper.getTridentSpinAttackStrength(itemStack, player) > 0.0F
                    && !player.isInWaterOrRain();
            case TOOT_HORN -> player.getCooldowns().isOnCooldown(itemStack);
            default -> true;
        };
    }

    public static EventResult onUseItemStart(LivingEntity livingEntity, ItemStack itemStack, InteractionHand interactionHand, MutableInt remainingUseDuration) {
        if (!SwordBlockingMechanics.CONFIG.get(ServerConfig.class).allowBlockingAndParrying) {
            return EventResult.PASS;
        }

        if (itemStack.is(ModRegistry.CAN_PERFORM_SWORD_BLOCKING_ITEM_TAG)) {
            remainingUseDuration.accept(DEFAULT_ITEM_USE_DURATION);
        }

        return EventResult.PASS;
    }

    public static EventResult onUseItemStop(LivingEntity livingEntity, ItemStack itemStack, InteractionHand interactionHand, int remainingUseDuration) {
        if (!SwordBlockingMechanics.CONFIG.get(ServerConfig.class).allowBlockingAndParrying) {
            return EventResult.PASS;
        }

        if (livingEntity instanceof Player player && itemStack.is(ModRegistry.CAN_PERFORM_SWORD_BLOCKING_ITEM_TAG)) {
            ParryCooldown.resetCooldownTicks(player);
        }

        return EventResult.PASS;
    }

    public static EventResult onLivingAttack(LivingEntity livingEntity, DamageSource damageSource, float damageAmount) {
        if (livingEntity.level() instanceof ServerLevel serverLevel && isActiveItemStackBlocking(livingEntity)) {
            if (damageAmount > 0.0F && canBlockDamageSource(livingEntity, damageSource)) {
                boolean parryIsActive = livingEntity instanceof Player player && getParryStrengthScale(player) > 0.0;
                if (parryIsActive
                        || SwordBlockingMechanics.CONFIG.get(ServerConfig.class).deflectProjectiles && damageSource.is(
                        DamageTypeTags.IS_PROJECTILE)) {
                    if (parryIsActive && SwordBlockingMechanics.CONFIG.get(ServerConfig.class).damageSwordOnParry
                            || !parryIsActive
                            && SwordBlockingMechanics.CONFIG.get(ServerConfig.class).damageSwordOnBlock) {
                        hurtSwordInUse(livingEntity, damageAmount);
                    }

                    if (parryIsActive && !damageSource.is(DamageTypeTags.IS_PROJECTILE)
                            && damageSource.getDirectEntity() instanceof LivingEntity directEntity) {
                        directEntity.knockback(SwordBlockingMechanics.CONFIG.get(ServerConfig.class).parryKnockbackStrength,
                                livingEntity.getX() - directEntity.getX(),
                                livingEntity.getZ() - directEntity.getZ());
                    }

                    serverLevel.playSound(null,
                            livingEntity.getX(),
                            livingEntity.getY(),
                            livingEntity.getZ(),
                            ModRegistry.ITEM_SWORD_BLOCK_SOUND_EVENT.value(),
                            livingEntity.getSoundSource(),
                            1.0F,
                            0.8F + serverLevel.getRandom().nextFloat() * 0.4F);
                    return EventResult.INTERRUPT;
                }
            }
        }

        return EventResult.PASS;
    }

    public static EventResult onLivingHurt(LivingEntity livingEntity, DamageSource damageSource, MutableFloat damageAmount) {
        if (isActiveItemStackBlocking(livingEntity)) {
            if (canBlockDamageSource(livingEntity, damageSource) && damageAmount.getAsFloat() > 0.0F) {
                if (SwordBlockingMechanics.CONFIG.get(ServerConfig.class).damageSwordOnBlock) {
                    hurtSwordInUse(livingEntity, damageAmount.getAsFloat());
                }

                double damageAfterBlock = 1.0 + damageAmount.getAsFloat() * (1.0 - SwordBlockingMechanics.CONFIG.get(
                        ServerConfig.class).blockedDamage);
                damageAmount.mapAsFloat((Float value) -> Math.min(value, (float) Math.floor(damageAfterBlock)));
            }
        }

        return EventResult.PASS;
    }

    public static EventResult onLivingKnockBack(LivingEntity livingEntity, MutableDouble knockbackStrength, MutableDouble ratioX, MutableDouble ratioZ) {
        if (isActiveItemStackBlocking(livingEntity)) {
            float knockBackMultiplier =
                    1.0F - (float) SwordBlockingMechanics.CONFIG.get(ServerConfig.class).knockbackReduction;
            if (knockBackMultiplier == 0.0F) {
                return EventResult.INTERRUPT;
            } else {
                knockbackStrength.mapAsDouble((double value) -> value * knockBackMultiplier);
            }
        }

        return EventResult.PASS;
    }

    private static boolean canBlockDamageSource(LivingEntity livingEntity, DamageSource damageSource) {
        Entity entity = damageSource.getDirectEntity();
        if (entity instanceof AbstractArrow arrow) {
            if (arrow.getPierceLevel() > 0) {
                return false;
            }
        }

        if (!damageSource.is(DamageTypeTags.BYPASSES_ARMOR)) {
            Vec3 position = damageSource.getSourcePosition();
            if (position != null) {
                Vec3 viewVector = livingEntity.getViewVector(1.0F);
                position = position.vectorTo(livingEntity.position()).normalize();
                position = new Vec3(position.x, 0.0, position.z);
                return position.dot(viewVector) < -Math.cos(
                        SwordBlockingMechanics.CONFIG.get(ServerConfig.class).protectionArc * Math.PI * 0.5 / 180.0);
            }
        }

        return false;
    }

    public static boolean isActiveItemStackBlocking(LivingEntity livingEntity) {
        if (!SwordBlockingMechanics.CONFIG.get(ServerConfig.class).allowBlockingAndParrying) {
            return false;
        }

        return livingEntity.isUsingItem() && livingEntity.getUseItem()
                .is(ModRegistry.CAN_PERFORM_SWORD_BLOCKING_ITEM_TAG);
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

    private static void hurtSwordInUse(LivingEntity livingEntity, float damageAmount) {
        if (damageAmount >= 3.0F) {
            int lostDurability = 1 + Mth.floor(damageAmount);
            InteractionHand interactionHand = livingEntity.getUsedItemHand();
            ItemHelper.hurtAndBreak(livingEntity.getUseItem(), lostDurability, livingEntity, interactionHand);
            if (livingEntity.getUseItem().isEmpty()) {
                if (interactionHand == InteractionHand.MAIN_HAND) {
                    livingEntity.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                } else {
                    livingEntity.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                }

                livingEntity.stopUsingItem();
                livingEntity.playSound(SoundEvents.ITEM_BREAK.value(),
                        0.8F,
                        0.8F + livingEntity.level().getRandom().nextFloat() * 0.4F);
            }
        }
    }
}
