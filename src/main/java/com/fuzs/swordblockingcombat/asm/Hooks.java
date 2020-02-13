package com.fuzs.swordblockingcombat.asm;

import com.fuzs.swordblockingcombat.handler.ConfigBuildHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ToolItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;

@SuppressWarnings("unused")
public class Hooks {

    public static int hitEntityAmount(ToolItem instance) {
        return instance instanceof AxeItem ? 1 : 2;
    }

    public static boolean doSweeping(boolean flag, PlayerEntity player, Entity targetEntity, float f) {

        if (flag && (EnchantmentHelper.getSweepingDamageRatio(player) > 0 || !ConfigBuildHandler.GENERAL_CONFIG.sweepingRequired.get())) {

            float f3 = 1.0F + EnchantmentHelper.getSweepingDamageRatio(player) * f;
            for (LivingEntity livingentity : player.world.getEntitiesWithinAABB(LivingEntity.class, targetEntity.getBoundingBox().grow(1.0D, 0.25D, 1.0D))) {

                if (livingentity != player && livingentity != targetEntity && !player.isOnSameTeam(livingentity) && (!(livingentity instanceof ArmorStandEntity) || !((ArmorStandEntity) livingentity).hasMarker()) && player.getDistanceSq(livingentity) < 9.0D) {

                    livingentity.knockBack(player, 0.4F, MathHelper.sin(player.rotationYaw * ((float) Math.PI / 180F)), -MathHelper.cos(player.rotationYaw * ((float) Math.PI / 180F)));
                    livingentity.attackEntityFrom(DamageSource.causePlayerDamage(player), f3);
                }
            }

            player.world.playSound(null, player.func_226277_ct_(), player.func_226278_cu_(), player.func_226281_cx_(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
            if (!ConfigBuildHandler.GENERAL_CONFIG.noSweepingSmoke.get()) {

                player.spawnSweepParticles();
            }
        }

        return false;
    }

}
