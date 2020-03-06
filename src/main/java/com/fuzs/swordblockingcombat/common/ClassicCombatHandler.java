package com.fuzs.swordblockingcombat.common;

import com.fuzs.swordblockingcombat.config.ConfigValueHolder;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClassicCombatHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onAttackEntity(final AttackEntityEvent evt) {

        // disable cooldown right before every attack
        if (ConfigValueHolder.CLASSIC_COMBAT.removeCooldown) {

            evt.getPlayer().ticksSinceLastSwing = (int) Math.ceil(evt.getPlayer().getCooldownPeriod());
        }
    }

    public static float addEnchantmentDamage(PlayerEntity player) {

        if (ConfigValueHolder.CLASSIC_COMBAT.boostSharpness) {

            // every level of sharpness adds 1.0F attack damage
            int sharpness = EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, player.getHeldItemMainhand());
            if (sharpness > 1) {

                return -0.5F + sharpness * 0.5F;
            }
        }

        return 0;
    }

    public static void restoreSprinting(PlayerEntity player, int knockback) {

        // knockback modifier will have changed +1 if the player was sprinting originally, therefor we now reset the sprinting flag
        // it's possible that if there's still an attack cooldown left +1 wasn't added and sprinting won't be restored,
        // but this is what classic combat is here to disable in the first place
        if (ConfigValueHolder.CLASSIC_COMBAT.attackingAllowsSprinting && EnchantmentHelper.getKnockbackModifier(player) < knockback) {

            player.setSprinting(true);
        }
    }

    public static void doSweeping(boolean flag, PlayerEntity player, Entity targetEntity, float damage) {

        if (flag && (!ConfigValueHolder.CLASSIC_COMBAT.sweepingRequired || EnchantmentHelper.getSweepingDamageRatio(player) > 0)) {

            float f3 = 1.0F + EnchantmentHelper.getSweepingDamageRatio(player) * damage;
            for (LivingEntity livingentity : player.world.getEntitiesWithinAABB(LivingEntity.class, targetEntity.getBoundingBox().grow(1.0D, 0.25D, 1.0D))) {

                if (livingentity != player && livingentity != targetEntity && !player.isOnSameTeam(livingentity) && (!(livingentity instanceof ArmorStandEntity) || !((ArmorStandEntity) livingentity).hasMarker()) && player.getDistanceSq(livingentity) < 9.0D) {

                    livingentity.knockBack(player, 0.4F, MathHelper.sin(player.rotationYaw * ((float) Math.PI / 180F)), -MathHelper.cos(player.rotationYaw * ((float) Math.PI / 180F)));
                    livingentity.attackEntityFrom(DamageSource.causePlayerDamage(player), f3);
                }
            }

            player.world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
            if (!ConfigValueHolder.CLASSIC_COMBAT.noSweepingSmoke) {

                player.spawnSweepParticles();
            }
        }
    }

}
