package com.fuzs.swordblockingcombat.client.handler;

import com.fuzs.swordblockingcombat.config.ConfigBuildHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class GrassSwingHandler {

    private static final Minecraft mc = Minecraft.getInstance();
    private int leftClickCounter;

    private static int objectMouseOverTimer;
    private static EntityRayTraceResult objectMouseOver;
    private static Entity pointedEntity;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent evt) {

        if (ConfigBuildHandler.HOLD_ATTACK.get() && evt.phase == TickEvent.Phase.END) {

            if (this.leftClickCounter <= 0) {

                if (mc.gameSettings.keyBindAttack.isKeyDown() && mc.objectMouseOver != null && mc.objectMouseOver.getType() != RayTraceResult.Type.MISS) {

                    // same tick value for checking attack strength as in PlayerEntity#attackTargetEntityWithCurrentItem
                    if (ConfigBuildHandler.REMOVE_ATTACK_COOLDOWN.get() || mc.player != null && mc.player.getCooledAttackStrength(0.5F) == 1.0F) {

                        mc.clickMouse();
                        this.leftClickCounter = 10;
                    }
                }

            } else {

                this.leftClickCounter--;
            }
        }
    }

    public static double rayTraceCollidingBlocks(float partialTicks, Entity entity, double blockReachDistance) {

        RayTraceResult objectMouseOver = rayTraceBlocks(entity, blockReachDistance, partialTicks);
        Vec3d vec3d = entity.getEyePosition(partialTicks);

        return objectMouseOver.getHitVec().squareDistanceTo(vec3d);
    }

    private static RayTraceResult rayTraceBlocks(Entity entity, double blockReachDistance, float partialTicks) {

        // uses collider block mode instead of outline
        Vec3d vec3d = entity.getEyePosition(partialTicks);
        Vec3d vec3d1 = entity.getLook(partialTicks);
        Vec3d vec3d2 = vec3d.add(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
        return entity.world.rayTraceBlocks(new RayTraceContext(vec3d, vec3d2, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity));
    }

    public static void applyCoyoteTime() {

        if (mc.objectMouseOver != null && mc.objectMouseOver.getType() == RayTraceResult.Type.ENTITY) {

            Entity entity = ((EntityRayTraceResult) mc.objectMouseOver).getEntity();
            EntitySize size = entity.getSize(entity.getPose());
            if ((!ConfigBuildHandler.COYOTE_SMALL.get() || size.width * size.width * size.height < 1.0F) && entity.isAlive()) {

                objectMouseOver = (EntityRayTraceResult) mc.objectMouseOver;
                objectMouseOverTimer = ConfigBuildHandler.COYOTE_TIME.get();
                pointedEntity = mc.pointedEntity;
            }
        } else if (objectMouseOverTimer > 0 && objectMouseOver.getEntity().isAlive()) {

            mc.objectMouseOver = objectMouseOver;
            mc.pointedEntity = pointedEntity;
            objectMouseOverTimer--;
        }
    }

    public static float getSwingProgress(float swingProgress, LivingEntity entity, float partialTickTime) {

        float cooldown = 1.0F;
        if (entity instanceof PlayerEntity) {

            cooldown = ((PlayerEntity) entity).getCooledAttackStrength(partialTickTime);
        }

        return ((swingProgress > 0.4F) && ((ConfigBuildHandler.REMOVE_ATTACK_COOLDOWN.get() ? swingProgress : cooldown) < 0.95F)) ?
                (0.4F + (0.6F * (float) Math.pow((swingProgress - 0.4F) / 0.6F, 4.0))) : swingProgress;
    }

}
