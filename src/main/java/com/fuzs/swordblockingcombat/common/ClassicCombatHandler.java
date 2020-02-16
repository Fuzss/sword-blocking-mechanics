package com.fuzs.swordblockingcombat.common;

import com.fuzs.swordblockingcombat.config.ConfigValueHolder;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
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

}
