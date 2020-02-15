package com.fuzs.swordblockingcombat.common;

import com.fuzs.swordblockingcombat.config.ConfigValueHolder;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClassicCombatHandler {

    public ClassicCombatHandler() {

        // change back values to pre combat update era
//        ((SwordItem) Items.DIAMOND_SWORD).attackDamage = 7.0F;
//        ((SwordItem) Items.IRON_SWORD).attackDamage = 6.0F;
//        ((SwordItem) Items.STONE_SWORD).attackDamage = 5.0F;
//        ((SwordItem) Items.WOODEN_SWORD).attackDamage = 4.0F;
//        ((SwordItem) Items.GOLDEN_SWORD).attackDamage = 4.0F;
//        ((ToolItem) Items.DIAMOND_AXE).attackDamage = 6.0F;
//        ((ToolItem) Items.IRON_AXE).attackDamage = 5.0F;
//        ((ToolItem) Items.STONE_AXE).attackDamage = 4.0F;
//        ((ToolItem) Items.WOODEN_AXE).attackDamage = 3.0F;
//        ((ToolItem) Items.GOLDEN_AXE).attackDamage = 3.0F;
//        ((ToolItem) Items.DIAMOND_PICKAXE).attackDamage = 5.0F;
//        ((ToolItem) Items.IRON_PICKAXE).attackDamage = 4.0F;
//        ((ToolItem) Items.STONE_PICKAXE).attackDamage = 3.0F;
//        ((ToolItem) Items.WOODEN_PICKAXE).attackDamage = 2.0F;
//        ((ToolItem) Items.GOLDEN_PICKAXE).attackDamage = 2.0F;
//        ((ToolItem) Items.DIAMOND_SHOVEL).attackDamage = 4.0F;
//        ((ToolItem) Items.IRON_SHOVEL).attackDamage = 3.0F;
//        ((ToolItem) Items.STONE_SHOVEL).attackDamage = 2.0F;
//        ((ToolItem) Items.WOODEN_SHOVEL).attackDamage = 1.0F;
//        ((ToolItem) Items.GOLDEN_SHOVEL).attackDamage = 1.0F;
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onAttackEntity(final AttackEntityEvent evt) {

        // disable cooldown right before every attack
        evt.getPlayer().ticksSinceLastSwing = (int) Math.ceil(evt.getPlayer().getCooldownPeriod());
    }

    public static void doSweeping(boolean flag, PlayerEntity player, Entity targetEntity, float f) {

        if (flag && (EnchantmentHelper.getSweepingDamageRatio(player) > 0 || !ConfigValueHolder.CLASSIC_COMBAT.sweepingRequired)) {

            float f3 = 1.0F + EnchantmentHelper.getSweepingDamageRatio(player) * f;
            for (LivingEntity livingentity : player.world.getEntitiesWithinAABB(LivingEntity.class, targetEntity.getBoundingBox().grow(1.0D, 0.25D, 1.0D))) {

                if (livingentity != player && livingentity != targetEntity && !player.isOnSameTeam(livingentity) && (!(livingentity instanceof ArmorStandEntity) || !((ArmorStandEntity) livingentity).hasMarker()) && player.getDistanceSq(livingentity) < 9.0D) {

                    livingentity.knockBack(player, 0.4F, MathHelper.sin(player.rotationYaw * ((float) Math.PI / 180F)), -MathHelper.cos(player.rotationYaw * ((float) Math.PI / 180F)));
                    livingentity.attackEntityFrom(DamageSource.causePlayerDamage(player), f3);
                }
            }

            player.world.playSound(null, player.func_226277_ct_(), player.func_226278_cu_(), player.func_226281_cx_(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
            if (!ConfigValueHolder.CLASSIC_COMBAT.noSweepingSmoke) {

                player.spawnSweepParticles();
            }
        }
    }

}
