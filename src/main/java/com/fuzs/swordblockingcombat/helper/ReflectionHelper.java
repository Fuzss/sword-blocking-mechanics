package com.fuzs.swordblockingcombat.helper;

import com.fuzs.swordblockingcombat.SwordBlockingCombat;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ReflectionHelper {

    private static final String FIRSTPERSONRENDERER_ITEM_STACK_MAIN_HAND = "field_187467_d";
    private static final String FIRSTPERSONRENDERER_ITEM_STACK_OFF_HAND = "field_187468_e";
    private static final String FIRSTPERSONRENDERER_EQUIPPED_PROGRESS_MAIN_HAND = "field_187469_f";
    private static final String FIRSTPERSONRENDERER_PREV_EQUIPPED_PROGRESS_MAIN_HAND = "field_187470_g";
    private static final String FIRSTPERSONRENDERER_EQUIPPED_PROGRESS_OFF_HAND = "field_187471_h";
    private static final String FIRSTPERSONRENDERER_PREV_EQUIPPED_PROGRESS_OFF_HAND = "field_187472_i";

    public static void setEquippedProgressMainHand(FirstPersonRenderer instance, float f) {

        try {
            ObfuscationReflectionHelper.setPrivateValue(FirstPersonRenderer.class, instance, f, FIRSTPERSONRENDERER_EQUIPPED_PROGRESS_MAIN_HAND);
        } catch (Exception e) {
            SwordBlockingCombat.LOGGER.error("setEquippedProgressMainHand() failed", e);
        }

    }

    public static Float getEquippedProgressMainHand(FirstPersonRenderer instance) {

        try {
            return ObfuscationReflectionHelper.getPrivateValue(FirstPersonRenderer.class, instance, FIRSTPERSONRENDERER_EQUIPPED_PROGRESS_MAIN_HAND);
        } catch (Exception e) {
            SwordBlockingCombat.LOGGER.error("getEquippedProgressMainHand() failed", e);
        }
        return 0.0F;

    }

    public static void setPrevEquippedProgressMainHand(FirstPersonRenderer instance, float f) {

        try {
            ObfuscationReflectionHelper.setPrivateValue(FirstPersonRenderer.class, instance, f, FIRSTPERSONRENDERER_PREV_EQUIPPED_PROGRESS_MAIN_HAND);
        } catch (Exception e) {
            SwordBlockingCombat.LOGGER.error("setPrevEquippedProgressMainHand() failed", e);
        }

    }

    public static void setEquippedProgressOffHand(FirstPersonRenderer instance, float f) {

        try {
            ObfuscationReflectionHelper.setPrivateValue(FirstPersonRenderer.class, instance, f, FIRSTPERSONRENDERER_EQUIPPED_PROGRESS_OFF_HAND);
        } catch (Exception e) {
            SwordBlockingCombat.LOGGER.error("setEquippedProgressOffHand() failed", e);
        }

    }

    public static Float getEquippedProgressOffHand(FirstPersonRenderer instance) {

        try {
            return ObfuscationReflectionHelper.getPrivateValue(FirstPersonRenderer.class, instance, FIRSTPERSONRENDERER_EQUIPPED_PROGRESS_OFF_HAND);
        } catch (Exception e) {
            SwordBlockingCombat.LOGGER.error("getEquippedProgressOffHand() failed", e);
        }
        return 0.0F;

    }

    public static void setPrevEquippedProgressOffHand(FirstPersonRenderer instance, float f) {

        try {
            ObfuscationReflectionHelper.setPrivateValue(FirstPersonRenderer.class, instance, f, FIRSTPERSONRENDERER_PREV_EQUIPPED_PROGRESS_OFF_HAND);
        } catch (Exception e) {
            SwordBlockingCombat.LOGGER.error("setPrevEquippedProgressOffHand() failed", e);
        }

    }

    public static void setItemStackMainHand(FirstPersonRenderer instance, ItemStack stack) {

        try {
            ObfuscationReflectionHelper.setPrivateValue(FirstPersonRenderer.class, instance, stack, FIRSTPERSONRENDERER_ITEM_STACK_MAIN_HAND);
        } catch (Exception e) {
            SwordBlockingCombat.LOGGER.error("setItemStackMainHand() failed", e);
        }

    }

    public static void setItemStackOffHand(FirstPersonRenderer instance, ItemStack stack) {

        try {
            ObfuscationReflectionHelper.setPrivateValue(FirstPersonRenderer.class, instance, stack, FIRSTPERSONRENDERER_ITEM_STACK_OFF_HAND);
        } catch (Exception e) {
            SwordBlockingCombat.LOGGER.error("setItemStackOffHand() failed", e);
        }

    }

}
