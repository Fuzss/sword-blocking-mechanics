package com.fuzs.swordblockingmechanics.client.element;

import com.fuzs.puzzleslib_sbm.element.extension.ElementExtension;
import com.fuzs.puzzleslib_sbm.element.side.IClientElement;
import com.fuzs.swordblockingmechanics.SwordBlockingMechanics;
import com.fuzs.swordblockingmechanics.element.CombatTestElement;
import com.fuzs.swordblockingmechanics.mixin.client.accessor.IIngameGuiAccessor;
import com.fuzs.swordblockingmechanics.util.AttackIndicatorHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.Hand;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.ForgeConfigSpec;

public class CombatTestExtension extends ElementExtension<CombatTestElement> implements IClientElement {

    private final Minecraft mc = Minecraft.getInstance();

    private boolean shieldIndicator;
    private boolean hideOffHand;

    public CombatTestExtension(CombatTestElement parent) {

        super(parent);
    }

    @Override
    public void setupClient() {

        this.addListener(this::onRenderHand);
        this.addListener(this::onRenderGameOverlay);
    }

    @Override
    public void setupClientConfig(ForgeConfigSpec.Builder builder) {

        addToConfig(builder.comment("Show a shield indicator similar to the attack indicator when actively blocking.").define("Shield Indicator", true), v -> this.shieldIndicator = v);
        addToConfig(builder.comment("Items specified in the \"" + SwordBlockingMechanics.MODID + ":off_hand_render_blacklist\" item tag will not be rendered when held in the offhand.").define("Hide Off-Hand", true), v -> this.hideOffHand = v);
    }

    private void onRenderHand(final RenderHandEvent evt) {

        if (this.hideOffHand && evt.getHand() == Hand.OFF_HAND) {

            if (evt.getItemStack().getItem().isIn(CombatTestElement.OFF_HAND_RENDER_BLACKLIST_TAG)) {

                evt.setCanceled(true);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void onRenderGameOverlay(final RenderGameOverlayEvent evt) {

        if (this.shieldIndicator && this.mc.player.isActiveItemStackBlocking()) {

            boolean isPreRendering = evt instanceof RenderGameOverlayEvent.Pre;
            MatrixStack matrixStack = evt.getMatrixStack();
            switch (AttackIndicatorHelper.getActiveIndicator(evt.getType())) {

                case CROSSHAIR:

                    if (this.mc.playerController.getCurrentGameType() != GameType.SPECTATOR || ((IIngameGuiAccessor) this.mc.ingameGUI).callIsTargetNamedMenuProvider(this.mc.objectMouseOver)) {

                        GameSettings gamesettings = this.mc.gameSettings;
                        if (gamesettings.getPointOfView().func_243192_a() && (!gamesettings.showDebugInfo || this.mc.player.hasReducedDebug() || gamesettings.reducedDebugInfo)) {

                            AttackIndicatorHelper.renderCrosshairIndicator(isPreRendering, (width, height) -> this.drawCrosshairIcon(matrixStack, width, height));
                        }
                    }

                    break;
                case HOTBAR:

                    AttackIndicatorHelper.renderHotbarIndicator(isPreRendering, (width, height) -> this.drawHotbarIcon(matrixStack, width, height));
                    break;
            }
        }
    }

    private void drawCrosshairIcon(MatrixStack matrixStack, int width, int height) {

        // rendering on top of each other for transparency reasons
        AbstractGui.blit(matrixStack, width, height, 54, 0, 16, 14, 256, 256);
        AbstractGui.blit(matrixStack, width, height, 70, 0, 16, 14, 256, 256);
    }

    private void drawHotbarIcon(MatrixStack matrixStack, int width, int height) {

        AbstractGui.blit(matrixStack, width, height, 18, 0, 18, 18, 256, 256);
    }

}
