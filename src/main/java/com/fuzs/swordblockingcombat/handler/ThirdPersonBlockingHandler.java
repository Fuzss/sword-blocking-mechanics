package com.fuzs.swordblockingcombat.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;

@SuppressWarnings("unused")
public class ThirdPersonBlockingHandler {

    /**
     * set proper arm angles when blocking using a sword
     * accessed by the asm transformer bundled with this mod
     */
    public static void setArmRotationAngel(RendererModel rightArm, RendererModel leftArm) {

        Minecraft mc = Minecraft.getInstance();
        if (mc.player.getActiveItemStack().getItem() instanceof SwordItem) {
            if (mc.player.getActiveHand() == Hand.OFF_HAND) {
                leftArm.rotateAngleX = leftArm.rotateAngleX * 0.5F - 0.9424779F;
                leftArm.rotateAngleY = ((float)Math.PI / 6F);
            } else {
                rightArm.rotateAngleX = rightArm.rotateAngleX * 0.5F - 0.9424779F;
                rightArm.rotateAngleY = (-(float)Math.PI / 6F);
            }
        }

    }

}
