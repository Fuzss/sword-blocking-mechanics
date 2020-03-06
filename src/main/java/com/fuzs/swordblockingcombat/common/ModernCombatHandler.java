package com.fuzs.swordblockingcombat.common;

import com.fuzs.swordblockingcombat.config.ConfigValueHolder;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

import static net.minecraft.world.IBlockReader.func_217300_a;

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

    public static int hitEntityAmount(ToolItem instance) {
        return ConfigValueHolder.MODERN_COMBAT.noAttackPenalty && instance instanceof AxeItem ? 1 : 2;
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

    public static double rayTraceCollidingBlocks(float partialTicks, Entity entity, double blockReachDistance, double originalReach) {

        if (!ConfigValueHolder.MODERN_COMBAT.swingThroughGrass) {
            return originalReach;
        }

        RayTraceResult objectMouseOver = rayTraceBlocks(entity, blockReachDistance, partialTicks);
        Vec3d vec3d = entity.getEyePosition(partialTicks);

        return objectMouseOver.getHitVec().squareDistanceTo(vec3d);
    }

    private static RayTraceResult rayTraceBlocks(Entity entity, double blockReachDistance, float partialTicks) {

        Vec3d vec3d = entity.getEyePosition(partialTicks);
        Vec3d vec3d1 = entity.getLook(partialTicks);
        Vec3d vec3d2 = vec3d.add(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
        return rayTraceBlocks(entity.world, new RayTraceContext(vec3d, vec3d2, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, entity));
    }

    private static BlockRayTraceResult rayTraceBlocks(World world, RayTraceContext context) {

        return func_217300_a(context, (rayTraceContext, pos) -> {
            BlockState blockstate = world.getBlockState(pos);
            IFluidState ifluidstate = world.getFluidState(pos);
            Vec3d vec3d = rayTraceContext.func_222253_b();
            Vec3d vec3d1 = rayTraceContext.func_222250_a();
            BlockRayTraceResult blockraytraceresult = null;
            if (!blockstate.getCollisionShape(world, pos).isEmpty()) {
                VoxelShape voxelshape = rayTraceContext.getBlockShape(blockstate, world, pos);
                blockraytraceresult = world.rayTraceBlocks(vec3d, vec3d1, pos, voxelshape, blockstate);
            }
            VoxelShape voxelshape1 = rayTraceContext.getFluidShape(ifluidstate, world, pos);
            BlockRayTraceResult blockraytraceresult1 = voxelshape1.rayTrace(vec3d, vec3d1, pos);
            double d0 = blockraytraceresult == null ? Double.MAX_VALUE : rayTraceContext.func_222253_b().squareDistanceTo(blockraytraceresult.getHitVec());
            double d1 = blockraytraceresult1 == null ? Double.MAX_VALUE : rayTraceContext.func_222253_b().squareDistanceTo(blockraytraceresult1.getHitVec());
            return d0 <= d1 ? blockraytraceresult : blockraytraceresult1;
        }, rayTraceContext -> {
            Vec3d vec3d = rayTraceContext.func_222253_b().subtract(rayTraceContext.func_222250_a());
            return BlockRayTraceResult.createMiss(rayTraceContext.func_222250_a(), Direction.getFacingFromVector(vec3d.x, vec3d.y, vec3d.z), new BlockPos(rayTraceContext.func_222250_a()));
        });
    }

}
