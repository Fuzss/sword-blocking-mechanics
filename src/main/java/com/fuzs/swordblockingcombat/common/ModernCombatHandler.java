package com.fuzs.swordblockingcombat.common;

import com.fuzs.swordblockingcombat.config.ConfigValueHolder;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.*;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class ModernCombatHandler {

    public ModernCombatHandler() {

        if (ConfigValueHolder.MODERN_COMBAT.dispenseTridents) {

            DispenserBlock.registerDispenseBehavior(Items.TRIDENT, new ProjectileDispenseBehavior() {

                /**
                 * Return the projectile entity spawned by this dispense behavior.
                 */
                @Override
                @Nonnull
                protected IProjectile getProjectileEntity(@Nonnull World world, @Nonnull IPosition position, @Nonnull ItemStack stack) {

                    TridentEntity tridentEntity = new TridentEntity(EntityType.TRIDENT, world);
                    tridentEntity.setPosition(position.getX(), position.getY(), position.getZ());
                    tridentEntity.pickupStatus = AbstractArrowEntity.PickupStatus.ALLOWED;
                    if (stack.attemptDamageItem(1, world.getRandom(), null)) {

                        stack.shrink(1);
                    }
                    if (stack.getItem() == Items.TRIDENT || stack.isEmpty()) {

                        tridentEntity.thrownStack = stack.copy();
                    }
                    return tridentEntity;
                }
            });
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onLivingHurt(final LivingHurtEvent evt) {

        // immediately reset damage immunity after being hit by any projectile
        if (ConfigValueHolder.MODERN_COMBAT.noProjectileResistance && evt.getSource().isProjectile()) {
            evt.getEntity().hurtResistantTime = 0;
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onItemUseStart(final LivingEntityUseItemEvent.Start evt) {

        if (evt.getItem().getItem() instanceof ShieldItem) {

            evt.setDuration(evt.getItem().getUseDuration() + ConfigValueHolder.MODERN_COMBAT.shieldDelay);
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onItemUseEnd(final PlayerInteractEvent.RightClickItem evt) {

        this.addItemCooldown(evt.getEntityLiving(), evt.getItemStack(), value -> value == 0);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onItemUseFinish(final LivingEntityUseItemEvent.Finish evt) {

        this.addItemCooldown(evt.getEntityLiving(), evt.getItem(), value -> value > 0);
    }

    private void addItemCooldown(LivingEntity entityLiving, ItemStack stack, Predicate<Integer> useDuration) {

        if (entityLiving instanceof PlayerEntity) {

            Item item = stack.getItem();
            if (useDuration.test(item.getUseDuration(stack))) {

                Double delay = ConfigValueHolder.MODERN_COMBAT.itemDelay.get(item);
                if (delay != null) {

                    ((PlayerEntity) entityLiving).getCooldownTracker().setCooldown(item, delay.intValue());
                }
            }
        }
    }

    public static int hitEntityAmount(ToolItem instance) {
        return ConfigValueHolder.MODERN_COMBAT.noAttackPenalty && instance instanceof AxeItem ? 1 : 2;
    }

    public static void doSweeping(boolean flag, PlayerEntity player, Entity targetEntity, float damage) {

        if (flag && (!ConfigValueHolder.MODERN_COMBAT.sweepingRequired || EnchantmentHelper.getSweepingDamageRatio(player) > 0)) {

            float f3 = 1.0F + EnchantmentHelper.getSweepingDamageRatio(player) * damage;
            for (LivingEntity livingentity : player.world.getEntitiesWithinAABB(LivingEntity.class, targetEntity.getBoundingBox().grow(1.0D, 0.25D, 1.0D))) {

                if (livingentity != player && livingentity != targetEntity && !player.isOnSameTeam(livingentity) && (!(livingentity instanceof ArmorStandEntity) || !((ArmorStandEntity) livingentity).hasMarker()) && player.getDistanceSq(livingentity) < 9.0D) {

                    livingentity.knockBack(player, 0.4F, MathHelper.sin(player.rotationYaw * ((float) Math.PI / 180F)), -MathHelper.cos(player.rotationYaw * ((float) Math.PI / 180F)));
                    livingentity.attackEntityFrom(DamageSource.causePlayerDamage(player), f3);
                }
            }

            player.world.playSound(null, player.func_226277_ct_(), player.func_226278_cu_(), player.func_226281_cx_(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
            if (!ConfigValueHolder.MODERN_COMBAT.noSweepingSmoke) {

                player.spawnSweepParticles();
            }
        }
    }

    public static float addEnchantmentDamage(PlayerEntity player, Entity targetEntity) {

        if (ConfigValueHolder.MODERN_COMBAT.boostImpaling) {

            // makes impaling work on all mobs in water or rain, not just those classified as water creatures
            int impaling = EnchantmentHelper.getEnchantmentLevel(Enchantments.IMPALING, player.getHeldItemMainhand());
            if (impaling > 0 && targetEntity instanceof LivingEntity && ((LivingEntity) targetEntity).getCreatureAttribute()
                    != CreatureAttribute.WATER && targetEntity.isInWaterRainOrBubbleColumn()) {

                return impaling * 2.5F;
            }
        }

        return 0;
    }

}
