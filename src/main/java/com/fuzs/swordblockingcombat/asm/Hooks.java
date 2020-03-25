package com.fuzs.swordblockingcombat.asm;

import com.fuzs.swordblockingcombat.client.handler.GrassSwingHandler;
import com.fuzs.swordblockingcombat.common.handler.ClassicCombatHandler;
import com.fuzs.swordblockingcombat.common.handler.CombatTestHandler;
import com.fuzs.swordblockingcombat.config.ConfigBuildHandler;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ToolItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * this class is mostly used for redirecting hooks to their appropriate place
 */
@SuppressWarnings("unused")
public class Hooks {

    @OnlyIn(Dist.CLIENT)
    public static LivingEntity armorLayerEntity;

    /**
     * make attacking an entity in {@link net.minecraft.item.ToolItem} only consume one durability point
     */
    public static int hitEntityAmount(ToolItem toolItem) {

        return ConfigBuildHandler.NO_AXE_ATTACK_PENALTY.get() && toolItem instanceof AxeItem ? 1 : 2;
    }

    /**
     * disable sweeping attack in by default in {@link net.minecraft.entity.player.PlayerEntity#attackTargetEntityWithCurrentItem}
     * might be executed if sweeping edge enchantment is present; sweeping particles are removed though
     */
    public static void doSweeping(boolean flag, PlayerEntity player, Entity targetEntity, float damage) {

        ClassicCombatHandler.doSweeping(flag, player, targetEntity, damage);
    }

    /**
     * set the player to be sprinting again if it has been disabled before in {@link net.minecraft.entity.player.PlayerEntity#attackTargetEntityWithCurrentItem}
     */
    public static void restoreSprinting(PlayerEntity player, int knockback) {

        if (ConfigBuildHandler.SPRINT_WHILE_ATTACKING.get()) {

            ClassicCombatHandler.restoreSprinting(player, knockback);
        }
    }

    /**
     * modify damage dealt to an entity in {@link net.minecraft.entity.player.PlayerEntity#attackTargetEntityWithCurrentItem}
     */
    public static float addEnchantmentDamage(float damage, PlayerEntity player, Entity targetEntity) {

        if (ConfigBuildHandler.BOOST_SHARPNESS.get()) {

            damage += ClassicCombatHandler.addEnchantmentDamage(player);
        }

        if (ConfigBuildHandler.BOOST_IMPALING.get()) {

            damage += CombatTestHandler.addEnchantmentDamage(player, targetEntity);
        }

        return damage;
    }

    /**
     * overwrite returned use duration for food items in {@link net.minecraft.item.Item#getUseDuration}
     */
    @SuppressWarnings("ConstantConditions")
    public static int getFoodDuration(Item item) {

        int speed = ConfigBuildHandler.EATING_SPEED.get();
        return item.getFood().isFastEating() ? speed / 2 : speed;
    }

    /**
     * overwrite minimum food level required for sprinting to start in {@link net.minecraft.client.entity.player.ClientPlayerEntity#livingTick}
     */
    @OnlyIn(Dist.CLIENT)
    public static float getSprintingLevel(float level) {

        float f = ConfigBuildHandler.SPRINTING_LEVEL.get();
        return f != 6.0F ? f : level;
    }

    /**
     * run block ray tracing a second time to only check for blocks with a collision shape in {@link net.minecraft.client.renderer.GameRenderer#getMouseOver}
     * the result is then used to ray trace for an entity
     */
    @OnlyIn(Dist.CLIENT)
    public static double rayTraceCollidingBlocks(float partialTicks, Entity entity, double d0, double d1) {

        return ConfigBuildHandler.SWING_THROUGH_GRASS.get() ? GrassSwingHandler.rayTraceCollidingBlocks(partialTicks, entity, d0) : d1;
    }

    /**
     * save Minecraft#objectMouseOver and Minecraft#pointedEntity for a couple of ticks if they'd be empty otherwise
     * hook is applied at the end of {@link net.minecraft.client.renderer.GameRenderer#getMouseOver}
     */
    @OnlyIn(Dist.CLIENT)
    public static void applyCoyoteTime() {

        if (ConfigBuildHandler.COYOTE_TIME.get() > 0) {

            GrassSwingHandler.applyCoyoteTime();
        }
    }

    /**
     * change arm swing animation to better emphasize the rhythm of attacks in {@link net.minecraft.entity.LivingEntity#getSwingProgress}
     */
    @OnlyIn(Dist.CLIENT)
    public static float getSwingProgress(float swingProgress, LivingEntity entity, float partialTickTime) {

        return ConfigBuildHandler.SWING_ANIMATION.get() ? GrassSwingHandler.getSwingProgress(swingProgress, entity, partialTickTime) : swingProgress;
    }

    /**
     * change armor model to turn red on hit in net.minecraft.client.renderer.entity.layers.ArmorLayer#renderArmorPart
     * and net.minecraft.client.renderer.entity.layers.ArmorLayer#renderArmor
     */
    @OnlyIn(Dist.CLIENT)
    public static int getArmorLayerOverlay(int overlay) {

        return ConfigBuildHandler.RED_ARMOR.get() && armorLayerEntity != null ? LivingRenderer.getPackedOverlay(armorLayerEntity, 0.0F) : overlay;
    }

    /**
     * make fishing bobber cause an attack when landing on a living entity in net.minecraft.entity.projectile.FishingBobberEntity#checkCollision
     */
    public static void onFishingBobberCollision(FishingBobberEntity bobber, PlayerEntity angler, Entity caughtEntity) {

        if (ConfigBuildHandler.OLD_FISHING_ROD.get() && caughtEntity instanceof LivingEntity) {

            caughtEntity.attackEntityFrom(DamageSource.causeThrownDamage(bobber, angler), 0.0F);
        }
    }

    /**
     * add slight upwards motion when pulling an entity in net.minecraft.entity.projectile.FishingBobberEntity#bringInHookedEntity
     */
    public static Vec3d getCaughtEntityMotion(Vec3d vec3d) {

        if (ConfigBuildHandler.OLD_FISHING_ROD.get()) {

            double x = vec3d.getX() * 10.0,  y = vec3d.getY() * 10.0, z = vec3d.getZ() * 10.0;
            vec3d = vec3d.add(0.0, Math.pow(x * x + y * y + z * z, 0.25) * 0.08, 0.0);
        }
        return vec3d;
    }

}
