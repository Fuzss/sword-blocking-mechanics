package com.fuzs.swordblockingcombat.common;

import com.fuzs.swordblockingcombat.config.ConfigValueHolder;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.*;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
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
    public void onEntityJoinWorld(final EntityJoinWorldEvent evt) {

        if (evt.getEntity() instanceof PlayerEntity) {

            // make sure another mod hasn't already changed something
            IAttributeInstance attributeInstance = ((PlayerEntity) evt.getEntity()).getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
            if (attributeInstance.getBaseValue() == 1.0) {

                attributeInstance.setBaseValue(ConfigValueHolder.MODERN_COMBAT.fistStrength);
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onLivingHurt(final LivingHurtEvent evt) {

        // immediately reset damage immunity after being hit by any projectile
        if (evt.getSource().isProjectile() && (ConfigValueHolder.MODERN_COMBAT.noProjectileResistance ||
                evt.getSource().getTrueSource() == null && evt.getAmount() == 0.0F)) {

            evt.getEntity().hurtResistantTime = 0;
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onProjectileImpact(final ProjectileImpactEvent evt) {

        if (ConfigValueHolder.MODERN_COMBAT.itemProjectiles && evt.getEntity() instanceof ProjectileItemEntity) {

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
    public void onItemUseStart(final LivingEntityUseItemEvent.Start evt) {

        // remove shield activation delay
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

        // add delay after using an item
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
