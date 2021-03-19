package com.fuzs.swordblockingmechanics.client.handler;

import com.fuzs.swordblockingmechanics.SwordBlockingMechanics;
import com.fuzs.swordblockingmechanics.config.ConfigBuildHandler;
import com.fuzs.swordblockingmechanics.util.BlockingHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.settings.AttackIndicatorStatus;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class RenderBlockingHandler {

    private static final ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation(SwordBlockingMechanics.MODID, "textures/gui/icons.png");

    private final Minecraft mc = Minecraft.getInstance();
    private AttackIndicatorStatus attackIndicator = AttackIndicatorStatus.OFF;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderLiving(final RenderLivingEvent.Pre<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> evt) {

        if (evt.getEntity() instanceof AbstractClientPlayerEntity) {

            AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) evt.getEntity();
            if (BlockingHelper.isActiveItemStackBlocking(player)) {

                PlayerModel<AbstractClientPlayerEntity> model = evt.getRenderer().getEntityModel();
                boolean left1 = player.getActiveHand() == Hand.OFF_HAND && player.getPrimaryHand() == HandSide.RIGHT;
                boolean left2 = player.getActiveHand() == Hand.MAIN_HAND && player.getPrimaryHand() == HandSide.LEFT;
                BipedModel.ArmPose pose = ConfigBuildHandler.REQUIRE_BOTH_HANDS.get() ? BipedModel.ArmPose.CROSSBOW_CHARGE : BipedModel.ArmPose.BLOCK;
                if (left1 || left2) {

                    if (model.leftArmPose == BipedModel.ArmPose.ITEM) {

                        model.leftArmPose = pose;
                    }
                } else {

                    if (model.rightArmPose == BipedModel.ArmPose.ITEM) {

                        model.rightArmPose = pose;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderHand(final RenderHandEvent evt) {

        ClientPlayerEntity player = this.mc.player;
        ItemStack stack = evt.getItemStack();
        if (player != null && player.getActiveHand() == evt.getHand() && BlockingHelper.isActiveItemStackBlocking(player)) {

            MatrixStack matrixStack = evt.getMatrixStack();
            matrixStack.push();

            boolean rightHanded = (evt.getHand() == Hand.MAIN_HAND ? player.getPrimaryHand() : player.getPrimaryHand().opposite()) == HandSide.RIGHT;
            this.transformSideFirstPerson(matrixStack, rightHanded ? 1.0F : -1.0F, evt.getEquipProgress());
            this.mc.getFirstPersonRenderer().renderItemSide(player, stack, rightHanded ? net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND :
                    net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !rightHanded, matrixStack, evt.getBuffers(), evt.getLight());

            matrixStack.pop();
            evt.setCanceled(true);
        }
    }

    /**
     * values taken from Minecraft snapshot 15w33b
     */
    private void transformSideFirstPerson(MatrixStack matrixStack, float side, float equippedProg) {

        matrixStack.translate(side * 0.56F, -0.52F + equippedProg * -0.6F, -0.72F);
        matrixStack.translate(side * -0.14142136F, 0.08F, 0.14142136F);
        matrixStack.rotate(Vector3f.XP.rotationDegrees(-102.25F));
        matrixStack.rotate(Vector3f.YP.rotationDegrees(side * 13.365F));
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(side * 78.05F));
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderGameOverlay(final RenderGameOverlayEvent evt) {

        // use shield indicator, but don't turn off if it's not enabled as it's a separate setting after all
        AttackIndicatorStatus attackIndicator = ConfigBuildHandler.SHIELD_INDICATOR.get();
        if (attackIndicator == AttackIndicatorStatus.OFF) {

            attackIndicator = AttackIndicatorStatus.CROSSHAIR;
        }

        boolean crosshair = evt.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS && attackIndicator == AttackIndicatorStatus.CROSSHAIR;
        boolean hotbar = evt.getType() == RenderGameOverlayEvent.ElementType.HOTBAR && attackIndicator == AttackIndicatorStatus.HOTBAR;
        if (!crosshair && !hotbar || this.mc.player == null || this.mc.playerController == null
                || this.mc.playerController.getCurrentGameType() == GameType.SPECTATOR && !this.mc.ingameGUI.isTargetNamedMenuProvider(this.mc.objectMouseOver)) {

            return;
        }

        float f = (float) BlockingHelper.getBlockUseDuration(this.mc.player) / (float) ConfigBuildHandler.PARRY_WINDOW.get();
        if (f >= 1.0F || !BlockingHelper.isActiveItemStackBlocking(this.mc.player)) {

            return;
        }

        GameSettings gamesettings = this.mc.gameSettings;
        if (crosshair && gamesettings.thirdPersonView == 0) {

            if (!gamesettings.showDebugInfo || gamesettings.hideGUI || this.mc.player.hasReducedDebug() || gamesettings.reducedDebugInfo) {

                if (evt instanceof RenderGameOverlayEvent.Pre) {

                    this.attackIndicator = gamesettings.attackIndicator;
                    gamesettings.attackIndicator = AttackIndicatorStatus.OFF;
                } else if (evt instanceof RenderGameOverlayEvent.Post) {

                    gamesettings.attackIndicator = this.attackIndicator;
                    this.mc.getTextureManager().bindTexture(GUI_ICONS_LOCATION);
                    RenderSystem.enableBlend();
                    RenderSystem.enableAlphaTest();
                    RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,
                            GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

                    int width = this.mc.getMainWindow().getScaledWidth() / 2 - 8;
                    int height = this.mc.getMainWindow().getScaledHeight() / 2 - 7 + 16;
                    int i = (int) (f * 15.0F);
                    AbstractGui.blit(width, height, 54, 0, 16, 14, 256, 256);
                    AbstractGui.blit(width, height + i, 70, i, 16, 14 - i, 256, 256);
                }
            }
        }

        if (hotbar) {

            if (evt instanceof RenderGameOverlayEvent.Pre) {

                this.attackIndicator = gamesettings.attackIndicator;
                gamesettings.attackIndicator = AttackIndicatorStatus.OFF;
            } else if (evt instanceof RenderGameOverlayEvent.Post) {

                gamesettings.attackIndicator = this.attackIndicator;
                RenderSystem.enableRescaleNormal();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                this.mc.getTextureManager().bindTexture(GUI_ICONS_LOCATION);
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

                int width = this.mc.getMainWindow().getScaledWidth() / 2;
                int height = this.mc.getMainWindow().getScaledHeight() - 20;
                width = this.mc.player.getPrimaryHand().opposite() == HandSide.RIGHT ? width - 91 - 22 : width + 91 + 6;
                int i = (int) (f * 19.0F);
                AbstractGui.blit(width, height, 0, 0, 18, 18, 256, 256);
                AbstractGui.blit(width, height + i, 18, i, 18, 18 - i, 256, 256);

                RenderSystem.disableRescaleNormal();
                RenderSystem.disableBlend();
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onInputUpdate(final InputUpdateEvent evt) {

        double movementModifier = ConfigBuildHandler.WALKING_MODIFIER.get();
        if (movementModifier != 0.2F && !evt.getPlayer().isPassenger() && BlockingHelper.isActiveItemStackBlocking(evt.getPlayer())) {

            evt.getMovementInput().moveStrafe *= 5.0F * movementModifier;
            evt.getMovementInput().moveForward *= 5.0F * movementModifier;
        }
    }

}
