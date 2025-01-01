package fuzs.swordblockingmechanics.client.handler;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import fuzs.puzzleslib.api.client.event.v1.gui.RenderGuiLayerEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.swordblockingmechanics.SwordBlockingMechanics;
import fuzs.swordblockingmechanics.config.ClientConfig;
import fuzs.swordblockingmechanics.handler.SwordBlockingHandler;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.Nullable;

public class AttackIndicatorInGuiHandler {
    public static final ResourceLocation GUI_ICONS_LOCATION = SwordBlockingMechanics.id("textures/gui/icons.png");

    @Nullable
    private static AttackIndicatorStatus attackIndicator = null;

    public static EventResult onBeforeRenderGuiLayer(Minecraft minecraft, GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!SwordBlockingMechanics.CONFIG.get(ClientConfig.class).renderParryIndicator) return EventResult.PASS;
        if (attackIndicator == null && SwordBlockingHandler.getParryStrengthScale(minecraft.player) != 0.0) {
            attackIndicator = minecraft.options.attackIndicator().get();
            minecraft.options.attackIndicator().set(AttackIndicatorStatus.OFF);
        }
        return EventResult.PASS;
    }

    public static RenderGuiLayerEvents.After onAfterRenderGuiLayer(ResourceLocation resourceLocation) {
        return (Minecraft minecraft, GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
            onAfterRenderGuiLayer(resourceLocation, minecraft, guiGraphics, deltaTracker);
        };
    }

    public static void onAfterRenderGuiLayer(ResourceLocation resourceLocation, Minecraft minecraft, GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        // reset to old value; don't just leave this disabled as it'll change the vanilla setting permanently in options.txt, which no mod should do imo
        if (attackIndicator != null) {
            minecraft.options.attackIndicator().set(attackIndicator);
            attackIndicator = null;
            int screenWidth = guiGraphics.guiWidth();
            int screenHeight = guiGraphics.guiHeight();
            double parryStrengthScale = Math.abs(SwordBlockingHandler.getParryStrengthScale(minecraft.player));
            if (resourceLocation.equals(RenderGuiLayerEvents.CROSSHAIR) && minecraft.options.attackIndicator().get() == AttackIndicatorStatus.CROSSHAIR) {
                RenderSystem.enableBlend();
                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                int posX = screenWidth / 2 - 8;
                int posY = screenHeight / 2 - 7 + 16;
                int textureHeight = (int) (parryStrengthScale * 15.0);
                guiGraphics.blit(GUI_ICONS_LOCATION, posX, posY, 54, 0, 16, 14);
                guiGraphics.blit(GUI_ICONS_LOCATION, posX, posY + 14 - textureHeight, 70, 14 - textureHeight, 16, textureHeight);
                RenderSystem.defaultBlendFunc();
                RenderSystem.disableBlend();
            } else if (resourceLocation.equals(RenderGuiLayerEvents.HOTBAR) && minecraft.options.attackIndicator().get() == AttackIndicatorStatus.HOTBAR) {
                RenderSystem.enableBlend();
                int posX;
                if (minecraft.player.getMainArm() == HumanoidArm.LEFT) {
                    posX = screenWidth / 2 - 91 - 22;
                } else {
                    posX = screenWidth / 2 + 91 + 6;
                }
                int posY = screenHeight - 20;
                int textureHeight = (int) (parryStrengthScale * 19.0F);
                guiGraphics.blit(GUI_ICONS_LOCATION, posX, posY, 0, 0, 18, 18);
                guiGraphics.blit(GUI_ICONS_LOCATION, posX, posY + 18 - textureHeight, 18, 18 - textureHeight, 18, textureHeight);
                RenderSystem.disableBlend();
            }
        }
    }
}
