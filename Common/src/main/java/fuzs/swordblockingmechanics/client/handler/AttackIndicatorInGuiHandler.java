package fuzs.swordblockingmechanics.client.handler;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.client.event.v1.RenderGuiElementEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.swordblockingmechanics.SwordBlockingMechanics;
import fuzs.swordblockingmechanics.config.ClientConfig;
import fuzs.swordblockingmechanics.handler.SwordBlockingHandler;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.Nullable;

public class AttackIndicatorInGuiHandler {
    public static final ResourceLocation GUI_ICONS_LOCATION = SwordBlockingMechanics.id("textures/gui/icons.png");

    @Nullable
    private static AttackIndicatorStatus attackIndicator = null;

    public static EventResult onBeforeRenderGuiElement(Minecraft minecraft, PoseStack poseStack, float tickDelta, int screenWidth, int screenHeight) {
        if (!SwordBlockingMechanics.CONFIG.get(ClientConfig.class).renderParryIndicator) return EventResult.PASS;
        if (attackIndicator == null && SwordBlockingHandler.getParryStrengthScale(minecraft.player) != 0.0) {
            attackIndicator = minecraft.options.attackIndicator;
            minecraft.options.attackIndicator = AttackIndicatorStatus.OFF;
        }
        return EventResult.PASS;
    }

    public static void onAfterRenderGuiElement(RenderGuiElementEvents.GuiOverlay guiOverlay, Minecraft minecraft, PoseStack poseStack, float tickDelta, int screenWidth, int screenHeight) {
        // reset to old value; don't just leave this disabled as it'll change the vanilla setting permanently in options.txt, which no mod should do imo
        if (attackIndicator != null) {
            minecraft.options.attackIndicator = attackIndicator;
            attackIndicator = null;
            double parryStrengthScale = Math.abs(SwordBlockingHandler.getParryStrengthScale(minecraft.player));
            if (guiOverlay == RenderGuiElementEvents.CROSSHAIR && minecraft.options.attackIndicator == AttackIndicatorStatus.CROSSHAIR) {
                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                int posX = screenWidth / 2 - 8;
                int posY = screenHeight / 2 - 7 + 16;
                int textureHeight = (int) (parryStrengthScale * 15.0);
                RenderSystem.setShaderTexture(0, GUI_ICONS_LOCATION);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                GuiComponent.blit(poseStack, posX, posY, 54, 0, 16, 14, 256, 256);
                RenderSystem.setShaderTexture(0, GUI_ICONS_LOCATION);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                GuiComponent.blit(poseStack, posX, posY + 14 - textureHeight, 70, 14 - textureHeight, 16, textureHeight, 256, 256);
                RenderSystem.defaultBlendFunc();
            } else if (guiOverlay == RenderGuiElementEvents.HOTBAR && minecraft.options.attackIndicator == AttackIndicatorStatus.HOTBAR) {
                RenderSystem.enableBlend();
                int posX;
                if (minecraft.player.getMainArm() == HumanoidArm.LEFT) {
                    posX = screenWidth / 2 - 91 - 22;
                } else {
                    posX = screenWidth / 2 + 91 + 6;
                }
                int posY = screenHeight - 20;
                int textureHeight = (int) (parryStrengthScale * 19.0F);
                RenderSystem.setShaderTexture(0, GUI_ICONS_LOCATION);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                GuiComponent.blit(poseStack, posX, posY, 0, 0, 18, 18, 256, 256);
                RenderSystem.setShaderTexture(0, GUI_ICONS_LOCATION);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                GuiComponent.blit(poseStack, posX, posY + 18 - textureHeight, 18, 18 - textureHeight, 18, textureHeight, 256, 256);
                RenderSystem.disableBlend();
            }
        }
    }
}
