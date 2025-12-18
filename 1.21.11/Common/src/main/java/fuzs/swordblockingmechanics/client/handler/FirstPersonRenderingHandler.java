package fuzs.swordblockingmechanics.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fuzs.puzzleslib.api.client.renderer.v1.RenderStateExtraData;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.swordblockingmechanics.SwordBlockingMechanics;
import fuzs.swordblockingmechanics.config.ClientConfig;
import fuzs.swordblockingmechanics.handler.SwordBlockingHandler;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class FirstPersonRenderingHandler {
    public static final ContextKey<Boolean> IS_BLOCKING_RENDER_PROPERTY_KEY = new ContextKey<>(SwordBlockingMechanics.id(
            "is_blocking"));

    public static void onExtractRenderState(Entity entity, EntityRenderState entityRenderState, float partialTick) {
        if (entity instanceof LivingEntity player && entityRenderState instanceof HumanoidRenderState) {
            RenderStateExtraData.set(entityRenderState,
                    IS_BLOCKING_RENDER_PROPERTY_KEY,
                    SwordBlockingHandler.isActiveItemStackBlocking(player));
        }
    }

    public static EventResult onRenderBothHands(ItemInHandRenderer itemInHandRenderer, InteractionHand interactionHand, AbstractClientPlayer player, HumanoidArm humanoidArm, ItemStack itemStack, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int combinedLight, float partialTick, float interpolatedPitch, float swingProgress, float equipProgress) {
        if (player.getUsedItemHand() == interactionHand && SwordBlockingHandler.isActiveItemStackBlocking(player)) {
            poseStack.pushPose();
            boolean isHandSideRight = humanoidArm == HumanoidArm.RIGHT;
            itemInHandRenderer.applyItemArmTransform(poseStack, humanoidArm, equipProgress);
            if (SwordBlockingMechanics.CONFIG.get(ClientConfig.class).interactAnimations) {
                itemInHandRenderer.applyItemArmAttackTransform(poseStack, humanoidArm, swingProgress);
            }

            applyBlockTransform(poseStack, humanoidArm);
            itemInHandRenderer.renderItem(player,
                    itemStack,
                    isHandSideRight ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND :
                            ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
                    poseStack,
                    submitNodeCollector,
                    combinedLight);
            poseStack.popPose();
            return EventResult.INTERRUPT;
        } else {
            return EventResult.PASS;
        }
    }

    private static void applyBlockTransform(PoseStack poseStack, HumanoidArm humanoidArm) {
        int direction = humanoidArm == HumanoidArm.RIGHT ? 1 : -1;
        poseStack.translate(direction * -0.14142136F, 0.08F, 0.14142136F);
        poseStack.mulPose(Axis.XP.rotationDegrees(-102.25F));
        poseStack.mulPose(Axis.YP.rotationDegrees(direction * 13.365F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(direction * 78.05F));
    }
}
