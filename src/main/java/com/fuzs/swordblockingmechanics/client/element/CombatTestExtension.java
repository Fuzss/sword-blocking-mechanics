package com.fuzs.swordblockingmechanics.client.element;

import com.fuzs.puzzleslib_sbm.element.extension.ElementExtension;
import com.fuzs.puzzleslib_sbm.element.side.IClientElement;
import com.fuzs.swordblockingmechanics.SwordBlockingMechanics;
import com.fuzs.swordblockingmechanics.element.CombatTestElement;
import com.fuzs.swordblockingmechanics.mixin.client.accessor.IIngameGuiAccessor;
import com.fuzs.swordblockingmechanics.util.AttackIndicatorHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.settings.AttackIndicatorStatus;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.common.ForgeConfigSpec;

@SuppressWarnings({"ConstantConditions", "deprecation"})
public class CombatTestExtension extends ElementExtension<CombatTestElement> implements IClientElement {

    private static final ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation(SwordBlockingMechanics.MODID, "textures/gui/icons.png");

    private final Minecraft mc = Minecraft.getInstance();

    private AttackIndicatorStatus shieldIndicator;
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

        addToConfig(builder.comment("Show a shield indicator similar to the attack indicator when actively blocking.").defineEnum("Shield Indicator", AttackIndicatorStatus.CROSSHAIR), v -> this.shieldIndicator = v);
        addToConfig(builder.comment("Items specified in the \"" + SwordBlockingMechanics.MODID + ":off_hand_render_blacklist\" item tag will not be rendered when held in the offhand.").define("Hide Off-Hand", true), v -> this.hideOffHand = v);
    }

    private void onRenderHand(final RenderHandEvent evt) {

        if (this.hideOffHand && evt.getHand() == Hand.OFF_HAND) {

            if (evt.getItemStack().getItem().isIn(CombatTestElement.OFF_HAND_RENDER_BLACKLIST_TAG)) {

                evt.setCanceled(true);
            }
        }
    }

    private void onRenderGameOverlay(final RenderGameOverlayEvent evt) {

        if (this.mc.player.isActiveItemStackBlocking()) {

            boolean isPreRendering = evt instanceof RenderGameOverlayEvent.Pre;
            MatrixStack matrixStack = evt.getMatrixStack();
            switch (AttackIndicatorHelper.getActiveIndicator(evt.getType(), this.shieldIndicator)) {

                case CROSSHAIR:

                    if (this.mc.playerController.getCurrentGameType() != GameType.SPECTATOR || ((IIngameGuiAccessor) this.mc.ingameGUI).callIsTargetNamedMenuProvider(this.mc.objectMouseOver)) {

                        GameSettings gamesettings = this.mc.gameSettings;
                        if (gamesettings.getPointOfView().func_243192_a() && (!gamesettings.showDebugInfo || this.mc.player.hasReducedDebug() || gamesettings.reducedDebugInfo)) {

                            AttackIndicatorHelper.renderShieldIndicator(isPreRendering, () -> this.renderCrosshairIcon(matrixStack));
                        }
                    }

                    break;
                case HOTBAR:

                    AttackIndicatorHelper.renderShieldIndicator(isPreRendering, () -> this.renderHotbarIcon(matrixStack));
                    break;
            }
        }
    }

    private void renderCrosshairIcon(MatrixStack matrixStack) {

        RenderSystem.enableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GUI_ICONS_LOCATION);
        int width = this.mc.getMainWindow().getScaledWidth() / 2 - 8;
        int height = this.mc.getMainWindow().getScaledHeight() / 2 - 7 + 16;
        // rendering on top of each other for transparency reasons
        AbstractGui.blit(matrixStack, width, height, 54, 0, 16, 14, 256, 256);
        AbstractGui.blit(matrixStack, width, height, 70, 0, 16, 14, 256, 256);
        RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();
    }

    private void renderHotbarIcon(MatrixStack matrixStack) {

        RenderSystem.enableRescaleNormal();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GUI_ICONS_LOCATION);
        int width = this.mc.getMainWindow().getScaledWidth() / 2;
        int height = this.mc.getMainWindow().getScaledHeight() - 20;
        width = this.mc.player.getPrimaryHand().opposite() == HandSide.RIGHT ? width - 91 - 22 : width + 91 + 6;
        AbstractGui.blit(matrixStack, width, height, 18, 0, 18, 18, 256, 256);
        RenderSystem.disableRescaleNormal();
        RenderSystem.disableBlend();
    }

}
