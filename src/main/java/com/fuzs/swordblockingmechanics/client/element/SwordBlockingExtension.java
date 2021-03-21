package com.fuzs.swordblockingmechanics.client.element;

import com.fuzs.puzzleslib_sbm.element.extension.ElementExtension;
import com.fuzs.puzzleslib_sbm.element.side.IClientElement;
import com.fuzs.swordblockingmechanics.client.renderer.entity.layers.BlockingHeldItemLayer;
import com.fuzs.swordblockingmechanics.element.SwordBlockingElement;
import com.fuzs.swordblockingmechanics.mixin.client.accessor.IFirstPersonRendererAccessor;
import com.fuzs.swordblockingmechanics.mixin.client.accessor.IIngameGuiAccessor;
import com.fuzs.swordblockingmechanics.mixin.client.accessor.ILivingRendererAccessor;
import com.fuzs.swordblockingmechanics.util.AttackIndicatorHelper;
import com.fuzs.swordblockingmechanics.util.BlockingHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.Map;

@SuppressWarnings("ConstantConditions")
public class SwordBlockingExtension extends ElementExtension<SwordBlockingElement> implements IClientElement {

    private final Minecraft mc = Minecraft.getInstance();

    public boolean oldBlockingPose;
    private float blockingSlowdown;

    public SwordBlockingExtension(SwordBlockingElement parent) {

        super(parent);
    }

    @Override
    public void setupClient() {

        this.addListener(this::onRenderHand);
        this.addListener(this::onInputUpdate);
        // don't want to mess up AttackIndicatorHelper when the event is cancelled by another mod
        this.addListener(this::onRenderGameOverlay, EventPriority.LOW);
    }

    @Override
    public void loadClient() {

        Map<String, PlayerRenderer> skinMap = Minecraft.getInstance().getRenderManager().getSkinMap();
        for (PlayerRenderer renderer : skinMap.values()) {

            ((ILivingRendererAccessor) renderer).getLayerRenderers().removeIf(layerRenderer -> layerRenderer instanceof HeldItemLayer);
            renderer.addLayer(new BlockingHeldItemLayer(renderer));
        }
    }

    @Override
    public void setupClientConfig(ForgeConfigSpec.Builder builder) {

        addToConfig(builder.comment("Use old third-person pose when blocking with a sword.").define("Old Blocking Pose", false), v -> this.oldBlockingPose = v);
        addToConfig(builder.comment("Percentage to slow down movement to while blocking.").defineInRange("Blocking Slowdown", 0.2, 0.0, 1.0), v -> this.blockingSlowdown = v, Double::floatValue);
    }

    @SuppressWarnings("ConstantConditions")
    private void onRenderHand(final RenderHandEvent evt) {

        ClientPlayerEntity player = this.mc.player;
        ItemStack stack = evt.getItemStack();
        if (player.getActiveHand() == evt.getHand() && BlockingHelper.isActiveItemStackBlocking(player)) {

            evt.setCanceled(true);
            FirstPersonRenderer itemRenderer = this.mc.getFirstPersonRenderer();
            MatrixStack matrixStack = evt.getMatrixStack();

            matrixStack.push();
            boolean isMainHand = evt.getHand() == Hand.MAIN_HAND;
            HandSide handSide = isMainHand ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
            boolean isHandSideRight = handSide == HandSide.RIGHT;

            ((IFirstPersonRendererAccessor) itemRenderer).invokeTransformSideFirstPerson(matrixStack, handSide, evt.getEquipProgress());
            this.transformBlockFirstPerson(matrixStack, handSide);
            ItemCameraTransforms.TransformType transformTypeIn = isHandSideRight ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND;
            itemRenderer.renderItemSide(player, stack, transformTypeIn, !isHandSideRight, matrixStack, evt.getBuffers(), evt.getLight());
            matrixStack.pop();
        }
    }

    private void onInputUpdate(final InputUpdateEvent evt) {

        if (this.blockingSlowdown != 0.2F && !evt.getPlayer().isPassenger() && BlockingHelper.isActiveItemStackBlocking(evt.getPlayer())) {

            evt.getMovementInput().moveStrafe *= 5.0F * this.blockingSlowdown;
            evt.getMovementInput().moveForward *= 5.0F * this.blockingSlowdown;
        }
    }

    private void onRenderGameOverlay(final RenderGameOverlayEvent evt) {

        if (this.parent.parryWindow != 0) {

            float blockDuration = (float) BlockingHelper.getBlockUseDuration(this.mc.player) / this.parent.parryWindow;
            if (blockDuration < 1.0F && BlockingHelper.isActiveItemStackBlocking(this.mc.player)) {

                boolean isPreRendering = evt instanceof RenderGameOverlayEvent.Pre;
                AttackIndicatorHelper.disableAttackIndicator(isPreRendering);
                if (!isPreRendering) {

                    MatrixStack matrixStack = evt.getMatrixStack();
                    switch (AttackIndicatorHelper.getActiveIndicator(evt.getType())) {

                        case CROSSHAIR:

                            if (this.mc.playerController.getCurrentGameType() != GameType.SPECTATOR || ((IIngameGuiAccessor) this.mc.ingameGUI).callIsTargetNamedMenuProvider(this.mc.objectMouseOver)) {

                                GameSettings gamesettings = this.mc.gameSettings;
                                if (gamesettings.getPointOfView().func_243192_a() && (!gamesettings.showDebugInfo || this.mc.player.hasReducedDebug() || gamesettings.reducedDebugInfo)) {

                                    AttackIndicatorHelper.renderCrosshairIcon((width, height) -> this.drawCrosshairIcon(matrixStack, width, height, blockDuration));
                                }
                            }

                            break;
                        case HOTBAR:

                            AttackIndicatorHelper.renderHotbarIcon((width, height) -> this.drawHotbarIcon(matrixStack, width, height, blockDuration));
                            break;
                    }
                }
            }
        }
    }

    private void transformBlockFirstPerson(MatrixStack matrixStack, HandSide hand) {

        int sideSignum = hand == HandSide.RIGHT ? 1 : -1;
        // values taken from Minecraft snapshot 15w33b
        matrixStack.translate(sideSignum * -0.14142136F, 0.08F, 0.14142136F);
        matrixStack.rotate(Vector3f.XP.rotationDegrees(-102.25F));
        matrixStack.rotate(Vector3f.YP.rotationDegrees(sideSignum * 13.365F));
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(sideSignum * 78.05F));
    }

    private void drawCrosshairIcon(MatrixStack matrixStack, int width, int height, float blockDuration) {

        // rendering on top of each other for transparency reasons
        int renderHeight = (int) (15.0F * blockDuration);
        AbstractGui.blit(matrixStack, width, height, 54, 0, 16, 14, 256, 256);
        AbstractGui.blit(matrixStack, width, height + renderHeight, 70, renderHeight, 16, 14 - renderHeight, 256, 256);
    }

    private void drawHotbarIcon(MatrixStack matrixStack, int width, int height, float blockDuration) {

        int renderHeight = (int) (19.0F * blockDuration);
        AbstractGui.blit(matrixStack, width, height, 0, 0, 18, 18, 256, 256);
        AbstractGui.blit(matrixStack, width, height + renderHeight, 18, renderHeight, 18, 18 - renderHeight, 256, 256);
    }

}
