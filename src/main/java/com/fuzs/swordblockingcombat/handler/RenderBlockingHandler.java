package com.fuzs.swordblockingcombat.handler;

import com.fuzs.swordblockingcombat.helper.EligibleItemHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RenderBlockingHandler {

    private final Minecraft mc = Minecraft.getInstance();
    private EligibleItemHelper eligibleItem = new EligibleItemHelper();

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderLiving(RenderLivingEvent.Pre<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> evt) {

        if (evt.getEntity() instanceof AbstractClientPlayerEntity) {

            AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) evt.getEntity();
            if (player.isHandActive() && this.eligibleItem.test(player.getActiveItemStack(), player.getActiveHand())) {

                PlayerModel<AbstractClientPlayerEntity> model = evt.getRenderer().getEntityModel();
                boolean left1 = player.getActiveHand() == Hand.OFF_HAND && player.getPrimaryHand() == HandSide.RIGHT;
                boolean left2 = player.getActiveHand() == Hand.MAIN_HAND && player.getPrimaryHand() == HandSide.LEFT;
                if (left1 || left2) {

                    if (model.leftArmPose == BipedModel.ArmPose.ITEM) {

                        model.leftArmPose = BipedModel.ArmPose.BLOCK;
                    }
                } else {

                    if (model.rightArmPose == BipedModel.ArmPose.ITEM) {

                        model.rightArmPose = BipedModel.ArmPose.BLOCK;
                    }
                }
            }
        }
    }

    @SuppressWarnings({"unused", "deprecation"})
    @SubscribeEvent
    public void onRenderHand(RenderHandEvent evt) {

        ClientPlayerEntity player = this.mc.player;
        if (player != null && player.isHandActive() && player.getActiveHand() == evt.getHand()) {

            ItemStack stack = evt.getItemStack();
            if (this.eligibleItem.test(stack, evt.getHand())) {

                FirstPersonRenderer renderer = this.mc.getFirstPersonRenderer();
                MatrixStack matrixStack = evt.getMatrixStack();
                matrixStack.func_227860_a_();

                boolean rightHanded = (evt.getHand() == Hand.MAIN_HAND ? player.getPrimaryHand() : player.getPrimaryHand().opposite()) == HandSide.RIGHT;
                float equippedProg = evt.getHand() == Hand.MAIN_HAND ? 1.0F - MathHelper.lerp(evt.getPartialTicks(), renderer.prevEquippedProgressMainHand, renderer.equippedProgressMainHand) :
                        1.0F - MathHelper.lerp(evt.getPartialTicks(), renderer.prevEquippedProgressOffHand, renderer.equippedProgressOffHand);

                this.transformSideFirstPerson(matrixStack, rightHanded ? 1.0F : -1.0F, equippedProg);
                renderer.func_228397_a_(player, stack, rightHanded ? net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND :
                        net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !rightHanded, matrixStack, evt.getBuffers(), evt.getLight());

                matrixStack.func_227865_b_();
                evt.setCanceled(true);
            }
        }
    }

    private void transformSideFirstPerson(MatrixStack matrixStack, float side, float equippedProg) {

        matrixStack.func_227861_a_(side * 0.56F, -0.52F + equippedProg * -0.6F, -0.72F);
        matrixStack.func_227861_a_(side * -0.14142136F, 0.08F, 0.14142136F);
        matrixStack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(-102.25F));
        matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(side * 13.365F));
        matrixStack.func_227863_a_(Vector3f.field_229183_f_.func_229187_a_(side * 78.05F));
    }

}
