package com.fuzs.swordblockingcombat.common.handler;

import com.fuzs.materialmaster.api.SyncProvider;
import com.fuzs.swordblockingcombat.config.ConfigBuildHandler;
import com.google.common.collect.Maps;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.*;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Predicate;

public class CombatTestHandler {

    @SyncProvider(path = {"combat_test", "Item Delay List"})
    public static Map<Item, Double> itemDelay = Maps.newHashMap();

    public CombatTestHandler() {

        if (ConfigBuildHandler.DISPENSE_TRIDENT.get()) {

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
        if (evt.getSource().isProjectile() && (ConfigBuildHandler.NO_PROJECTILE_RESISTANCE.get() ||
                evt.getSource().getTrueSource() == null && evt.getAmount() == 0.0F)) {

            evt.getEntity().hurtResistantTime = 0;
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onProjectileImpact(final ProjectileImpactEvent evt) {

        if (ConfigBuildHandler.BETTER_PROJECTILES.get() && evt.getEntity() instanceof ProjectileItemEntity) {

            ProjectileItemEntity projectileItemEntity = (ProjectileItemEntity) evt.getEntity();
            if (evt.getRayTraceResult().getType() == RayTraceResult.Type.BLOCK) {

                // enable item projectiles to pass through blocks without a collision shape
                World world = projectileItemEntity.getEntityWorld();
                BlockPos pos = ((BlockRayTraceResult) evt.getRayTraceResult()).getPos();
                if (world.getBlockState(pos).getCollisionShape(world, pos).isEmpty()) {

                    evt.setCanceled(true);
                }
            } else if (evt.getRayTraceResult().getType() == RayTraceResult.Type.ENTITY && projectileItemEntity.getThrower() == null) {

                // enable knockback for item projectiles fired from dispensers by making true source not be null
                Entity target = ((EntityRayTraceResult) evt.getRayTraceResult()).getEntity();
                target.attackEntityFrom(DamageSource.causeThrownDamage(projectileItemEntity, projectileItemEntity), 0.0F);
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent evt) {

        if (ConfigBuildHandler.FAST_SWITCHING.get() && evt.phase == TickEvent.Phase.START) {

            // switching items no longer triggers the attack cooldown
            PlayerEntity player = evt.player;
            ItemStack itemstack = player.getHeldItemMainhand();
            if (!ItemStack.areItemStacksEqual(player.itemStackMainHand, itemstack)) {

                player.itemStackMainHand = itemstack.copy();
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onLivingKnockBack(final LivingKnockBackEvent evt) {

        if (!ConfigBuildHandler.UPWARDS_KNOCKBACK.get()) {

            return;
        }

        LivingEntity entity = evt.getEntityLiving();
        float strength = evt.getOriginalStrength();
        // makes knockback resistance a scale instead of being random
        strength = (float)(strength * (1.0 - entity.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getValue()));
        if (strength > 0.0F) {

            entity.isAirBorne = true;
            Vec3d vec3d = entity.getMotion();
            Vec3d vec3d1 = (new Vec3d(evt.getOriginalRatioX(), 0.0, evt.getOriginalRatioZ())).normalize().scale(strength);
            // upwards knockback
            entity.setMotion(vec3d.x / 2.0 - vec3d1.x, entity.onGround ? Math.min(0.4, strength) : Math.max(0.4, vec3d.y + strength / 2.0F), vec3d.z / 2.0 - vec3d1.z);
        }

        evt.setCanceled(true);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onItemUseStart(final LivingEntityUseItemEvent.Start evt) {

        // remove shield activation delay
        if (!ConfigBuildHandler.SHIELD_DELAY.get() && evt.getItem().getUseAction() == UseAction.BLOCK) {

            evt.setDuration(evt.getItem().getUseDuration() - 5);
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

        // add delay after using an item
        if (entityLiving instanceof PlayerEntity) {

            Item item = stack.getItem();
            if (useDuration.test(item.getUseDuration(stack))) {

                Double delay = itemDelay.get(item);
                if (delay != null) {

                    ((PlayerEntity) entityLiving).getCooldownTracker().setCooldown(item, delay.intValue());
                }
            }
        }
    }

    public static float addEnchantmentDamage(PlayerEntity player, Entity targetEntity) {

        // makes impaling work on all mobs in water or rain, not just those classified as water creatures
        int impaling = EnchantmentHelper.getEnchantmentLevel(Enchantments.IMPALING, player.getHeldItemMainhand());
        if (impaling > 0 && targetEntity instanceof LivingEntity && ((LivingEntity) targetEntity).getCreatureAttribute()
                != CreatureAttribute.WATER && targetEntity.isInWaterRainOrBubbleColumn()) {

            return impaling * 2.5F;
        }

        return 0;
    }

}
