package fuzs.swordblockingmechanics.client.handler;

import fuzs.swordblockingmechanics.SwordBlockingMechanics;
import fuzs.swordblockingmechanics.config.ClientConfig;
import fuzs.swordblockingmechanics.handler.SwordBlockingHandler;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class AttackIndicatorInGuiHandler {
    public static final ResourceLocation GUI_ICONS_LOCATION = SwordBlockingMechanics.id("textures/gui/icons.png");

    @Nullable
    private static AttackIndicatorStatus attackIndicator = null;

    public static void onBeforeRenderGui(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!SwordBlockingMechanics.CONFIG.get(ClientConfig.class).renderParryIndicator) return;
        if (attackIndicator == null
                && SwordBlockingHandler.getParryStrengthScale(Minecraft.getInstance().player) != 0.0) {
            Options options = Minecraft.getInstance().options;
            attackIndicator = options.attackIndicator().get();
            options.attackIndicator().set(AttackIndicatorStatus.OFF);
        }
    }

    public static void onAfterRenderGui(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (attackIndicator != null) {
            Minecraft.getInstance().options.attackIndicator().set(attackIndicator);
            attackIndicator = null;
        }
    }

    public static void renderCrosshairBlockingIndicator(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (attackIndicator == AttackIndicatorStatus.CROSSHAIR) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.options.getCameraType().isFirstPerson()) {
                int posX = guiGraphics.guiWidth() / 2 - 8;
                int posY = guiGraphics.guiHeight() / 2 - 7 + 16;
                double parryStrengthScale = SwordBlockingHandler.getParryStrengthScale(minecraft.player);
                int textureHeight = (int) (Math.abs(parryStrengthScale) * 15.0);
                guiGraphics.blit(RenderPipelines.CROSSHAIR, GUI_ICONS_LOCATION, posX, posY, 54, 0, 16, 14, 256, 256);
                guiGraphics.blit(RenderPipelines.CROSSHAIR,
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
        }
    }

    public static void renderHotbarBlockingIndicator(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (attackIndicator == AttackIndicatorStatus.HOTBAR) {
            Player player = Minecraft.getInstance().player;
            int posX;
            if (player.getMainArm() == HumanoidArm.LEFT) {
                posX = guiGraphics.guiWidth() / 2 - 91 - 22;
            } else {
                posX = guiGraphics.guiWidth() / 2 + 91 + 6;
            }
            int posY = guiGraphics.guiHeight() - 20;
            double parryStrengthScale = SwordBlockingHandler.getParryStrengthScale(player);
            int textureHeight = (int) (Math.abs(parryStrengthScale) * 19.0F);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GUI_ICONS_LOCATION, posX, posY, 0, 0, 18, 18, 256, 256);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED,
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
