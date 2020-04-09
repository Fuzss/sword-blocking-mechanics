package com.fuzs.swordblockingcombat.common.handler;

import com.fuzs.swordblockingcombat.config.ConfigBuildHandler;
import com.google.common.collect.Sets;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Set;

public class ClassicCombatHandler {

    private static final Set<SoundEvent> PLAYER_ATTACK_SOUNDS = Sets.newHashSet(SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundEvents.ENTITY_PLAYER_ATTACK_WEAK);

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onAttackEntity(final AttackEntityEvent evt) {

        // disable cooldown right before every attack
        if (ConfigBuildHandler.REMOVE_ATTACK_COOLDOWN.get()) {

            evt.getPlayer().ticksSinceLastSwing = (int) Math.ceil(evt.getPlayer().getCooldownPeriod());
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onCriticalHit(final CriticalHitEvent evt) {

        // prevent sweeping from taking effect unless the enchantment is in place, onGround flag is reset next tick anyways
        if (ConfigBuildHandler.SWEEPING_REQUIRED.get() && EnchantmentHelper.getSweepingDamageRatio(evt.getPlayer()) == 0.0F) {

            evt.getPlayer().onGround = false;
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onPlaySoundAtEntity(final PlaySoundAtEntityEvent evt) {

        // disable Combat Update player attack sounds
        if (ConfigBuildHandler.NO_ATTACK_SOUNDS.get() && PLAYER_ATTACK_SOUNDS.contains(evt.getSound())) {

            evt.setCanceled(true);
        }
    }

    public static float addEnchantmentDamage(PlayerEntity player) {

        // every level of sharpness adds 1.25 attack damage again
        int sharpness = EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, player.getHeldItemMainhand());
        if (sharpness > 0) {

            return -0.5F + sharpness * 0.75F;
        }

        return 0;
    }

    public static void onFishingBobberCollision(FishingBobberEntity bobber, PlayerEntity angler, Entity caughtEntity) {

        if (caughtEntity instanceof LivingEntity) {

            caughtEntity.attackEntityFrom(DamageSource.causeThrownDamage(bobber, angler), 0.0F);
        }
    }

    public static Vec3d getCaughtEntityMotion(Vec3d vec3d) {

        double x = vec3d.getX() * 10.0, y = vec3d.getY() * 10.0, z = vec3d.getZ() * 10.0;
        return vec3d.add(0.0, Math.pow(x * x + y * y + z * z, 0.25) * 0.08, 0.0);
    }

}
