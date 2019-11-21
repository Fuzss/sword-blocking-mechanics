package com.fuzs.swordblockingcombat.handler;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RenderBlockingHandler {

    private final Minecraft mc = Minecraft.getInstance();

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderLiving(RenderLivingEvent.Pre evt) {

        if (evt.getEntity() instanceof AbstractClientPlayerEntity) {
            AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) evt.getEntity();
            if (player.getActiveItemStack().getItem() instanceof SwordItem) {
                PlayerModel model = (PlayerModel) evt.getRenderer().getEntityModel();
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
    public void renderSpecificHand(RenderSpecificHandEvent evt) {

        ItemStack stack = evt.getItemStack();
        if (stack.getItem() instanceof SwordItem) {
            ClientPlayerEntity player = this.mc.player;
            if (player.isHandActive() && player.getActiveHand() == evt.getHand()) {
                GlStateManager.pushMatrix();
                boolean rightHanded = (evt.getHand() == Hand.MAIN_HAND ? player.getPrimaryHand() : player.getPrimaryHand().opposite()) == HandSide.RIGHT;
                this.transformSideFirstPerson(rightHanded ? 1.0F : -1.0F, evt.getEquipProgress());
                this.mc.getFirstPersonRenderer().renderItemSide(player, stack, rightHanded ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !rightHanded);
                GlStateManager.popMatrix();
                evt.setCanceled(true);
            }
        }

    }

    private void transformSideFirstPerson(float side, float equippedProg) {

        GlStateManager.translatef(side * 0.56F, -0.52F + equippedProg * -0.6F, -0.72F);
        GlStateManager.translatef(side * -0.14142136F, 0.08F, 0.14142136F);
        GlStateManager.rotatef(-102.25F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotatef(side * 13.365F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(side * 78.05F, 0.0F, 0.0F, 1.0F);

    }

}
