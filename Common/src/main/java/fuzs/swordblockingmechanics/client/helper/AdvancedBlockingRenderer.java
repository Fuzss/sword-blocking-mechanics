package fuzs.swordblockingmechanics.client.helper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;
import org.joml.Quaternionf;

public class AdvancedBlockingRenderer {

    public static void renderBlockingWithSword(ArmedModel model, ItemStackRenderState itemStackRenderState, HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        // those transformations are directly ported from Minecraft 1.7, resulting in a pixel-perfect recreation of third-person sword blocking
        // a lot has changed since then (the whole model system has been rewritten twice in 1.8 and 1.9, and had major changes in 1.14 and 1.15),
        // so we reset everything vanilla does now, and apply every single step that was done in 1.7
        // (there were multiple classes and layers involved in 1.7, it is noted down below which class every transformation came from)
        // all this is done in code and not using some custom json model predicate so that every item is supported by default
        poseStack.pushPose();
        model.translateToHand(humanoidArm, poseStack);
        boolean leftHand = humanoidArm == HumanoidArm.LEFT;
        applyItemBlockingTransform(poseStack, leftHand);
        // revert 1.8+ model changes, so we can work on a blank slate
        applyTransformInverse(itemStackRenderState.firstLayer().transform, leftHand, poseStack);
        itemStackRenderState.render(poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    private static void applyItemBlockingTransform(PoseStack poseStack, boolean leftHand) {
        poseStack.translate((leftHand ? 1.0F : -1.0F) / 16.0F, 0.4375F, 0.0625F);
        // blocking
        poseStack.translate(leftHand ? -0.035F : 0.05F, leftHand ? 0.045F : 0.0F, leftHand ? -0.135F : -0.1F);
        poseStack.mulPose(Axis.YP.rotationDegrees((leftHand ? -1.0F : 1.0F) * -50.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(-10.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees((leftHand ? -1.0F : 1.0F) * -60.0F));
        // old item layer
        poseStack.translate(0.0F, 0.1875F, 0.0F);
        // this differs from 1.7 as there was a negative y scale being used, which is not supported on Minecraft 1.16+
        // therefore rotations on X and Y all had to be flipped down the line (and one rotation on X by 180 degrees has been added)
        poseStack.scale(0.625F, 0.625F, 0.625F);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.XN.rotationDegrees(-100.0F));
        poseStack.mulPose(Axis.YN.rotationDegrees(leftHand ? 35.0F : 45.0F));
        // old item renderer
        poseStack.translate(0.0F, -0.3F, 0.0F);
        poseStack.scale(1.5F, 1.5F, 1.5F);
        poseStack.mulPose(Axis.YN.rotationDegrees(50.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(335.0F));
        poseStack.translate(-0.9375F, -0.0625F, 0.0F);
        poseStack.translate(0.5F, 0.5F, 0.25F);
        poseStack.mulPose(Axis.YN.rotationDegrees(180.0F));
        poseStack.translate(0.0F, 0.0F, 0.28125F);
    }

    private static void applyTransformInverse(ItemTransform itemTransform, boolean leftHand, PoseStack poseStack) {
        // this does the exact inverse of ItemTransform::apply which should be applied right after, so that in the end nothing has changed
        if (itemTransform != ItemTransform.NO_TRANSFORM) {
            float angleX = itemTransform.rotation().x();
            float angleY = leftHand ? -itemTransform.rotation().y() : itemTransform.rotation().y();
            float angleZ = leftHand ? -itemTransform.rotation().z() : itemTransform.rotation().z();
            Quaternionf quaternion = new Quaternionf().rotationXYZ(angleX * 0.017453292F,
                    angleY * 0.017453292F,
                    angleZ * 0.017453292F);
            quaternion.conjugate();
            poseStack.scale(1.0F / itemTransform.scale().x(),
                    1.0F / itemTransform.scale().y(),
                    1.0F / itemTransform.scale().z());
            poseStack.mulPose(quaternion);
            poseStack.translate((leftHand ? -1.0F : 1.0F) * -itemTransform.translation().x(),
                    -itemTransform.translation().y(),
                    -itemTransform.translation().z());
        }
    }
}
