package fuzs.swordblockingmechanics.client.handler;

import fuzs.puzzleslib.api.client.event.v1.gui.RenderGuiLayerEvents;
import fuzs.swordblockingmechanics.SwordBlockingMechanics;
import fuzs.swordblockingmechanics.config.ClientConfig;
import fuzs.swordblockingmechanics.handler.SwordBlockingHandler;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.Nullable;

public class AttackIndicatorInGuiHandler {
    public static final ResourceLocation GUI_ICONS_LOCATION = SwordBlockingMechanics.id("textures/gui/icons.png");

    @Nullable
    private static AttackIndicatorStatus attackIndicator = null;

    public static void onBeforeRenderGui(Gui gui, GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!SwordBlockingMechanics.CONFIG.get(ClientConfig.class).renderParryIndicator) return;
        if (attackIndicator == null && SwordBlockingHandler.getParryStrengthScale(gui.minecraft.player) != 0.0) {
            attackIndicator = gui.minecraft.options.attackIndicator().get();
            gui.minecraft.options.attackIndicator().set(AttackIndicatorStatus.OFF);
        }
    }

    public static void onAfterRenderGui(Gui gui, GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (attackIndicator != null) {
            gui.minecraft.options.attackIndicator().set(attackIndicator);
            attackIndicator = null;
        }
    }

    public static RenderGuiLayerEvents.After onAfterRenderGuiLayer(ResourceLocation resourceLocation) {
        return (Gui gui, GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
            onAfterRenderGuiLayer(resourceLocation, gui, guiGraphics, deltaTracker);
        };
    }

    public static void onAfterRenderGuiLayer(ResourceLocation resourceLocation, Gui gui, GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        // reset to old value; don't just leave this disabled as it'll change the vanilla setting permanently in options.txt, which no mod should do imo
        if (attackIndicator != null) {
            int screenWidth = guiGraphics.guiWidth();
            int screenHeight = guiGraphics.guiHeight();
            double parryStrengthScale = Math.abs(SwordBlockingHandler.getParryStrengthScale(gui.minecraft.player));
            if (resourceLocation.equals(RenderGuiLayerEvents.CROSSHAIR) &&
                    attackIndicator == AttackIndicatorStatus.CROSSHAIR) {
                if (gui.minecraft.options.getCameraType().isFirstPerson()) {
                    int posX = screenWidth / 2 - 8;
                    int posY = screenHeight / 2 - 7 + 16;
                    int textureHeight = (int) (parryStrengthScale * 15.0);
                    guiGraphics.blit(RenderType::crosshair, GUI_ICONS_LOCATION, posX, posY, 54, 0, 16, 14, 256, 256);
                    guiGraphics.blit(RenderType::crosshair,
                            GUI_ICONS_LOCATION,
                            posX,
                            posY + 14 - textureHeight,
                            70,
                            14 - textureHeight,
                            16,
                            textureHeight,
                            256,
                            256);
                }
            } else if (resourceLocation.equals(RenderGuiLayerEvents.HOTBAR) &&
                    attackIndicator == AttackIndicatorStatus.HOTBAR) {
                int posX;
                if (gui.minecraft.player.getMainArm() == HumanoidArm.LEFT) {
                    posX = screenWidth / 2 - 91 - 22;
                } else {
                    posX = screenWidth / 2 + 91 + 6;
                }
                int posY = screenHeight - 20;
                int textureHeight = (int) (parryStrengthScale * 19.0F);
                guiGraphics.blit(RenderType::guiTextured, GUI_ICONS_LOCATION, posX, posY, 0, 0, 18, 18, 256, 256);
                guiGraphics.blit(RenderType::guiTextured,
                        GUI_ICONS_LOCATION,
                        posX,
                        posY + 18 - textureHeight,
                        18,
                        18 - textureHeight,
                        18,
                        textureHeight,
                        256,
                        256);
            }
        }
    }
}
