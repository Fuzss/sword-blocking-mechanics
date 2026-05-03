package fuzs.swordblockingmechanics.client.handler;

import fuzs.swordblockingmechanics.SwordBlockingMechanics;
import fuzs.swordblockingmechanics.config.ClientConfig;
import fuzs.swordblockingmechanics.handler.SwordBlockingHandler;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

public class AttackIndicatorInGuiHandler {
    public static final Identifier GUI_ICONS_LOCATION = SwordBlockingMechanics.id("textures/gui/icons.png");

    @Nullable
    private static AttackIndicatorStatus attackIndicator = null;
    private static double oldParryStrengthScale;

    public static void onEndClientTick(Minecraft minecraft) {
        if (minecraft.player != null) {
            oldParryStrengthScale = SwordBlockingHandler.getParryStrengthScale(minecraft.player);
        }
    }

    private static double getParryStrengthScale(Player player, float partialTick) {
        double scale = SwordBlockingHandler.getParryStrengthScale(player);
        return Mth.lerp(partialTick, oldParryStrengthScale, scale);
    }

    public static void onBeforeRenderGui(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
        if (!SwordBlockingMechanics.CONFIG.get(ClientConfig.class).renderParryIndicator) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (attackIndicator == null && minecraft.player != null) {
            float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
            if (getParryStrengthScale(minecraft.player, partialTick) != 0.0) {
                attackIndicator = minecraft.options.attackIndicator().get();
                minecraft.options.attackIndicator().set(AttackIndicatorStatus.OFF);
            }
        }
    }

    public static void onAfterRenderGui(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
        if (attackIndicator != null) {
            Options options = Minecraft.getInstance().options;
            options.attackIndicator().set(attackIndicator);
            attackIndicator = null;
        }
    }

    public static void renderCrosshairBlockingIndicator(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (attackIndicator == AttackIndicatorStatus.CROSSHAIR && minecraft.player != null) {
            if (minecraft.options.getCameraType().isFirstPerson()) {
                int posX = guiGraphics.guiWidth() / 2 - 8;
                int posY = guiGraphics.guiHeight() / 2 - 7 + 16;
                float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
                double parryStrengthScale = getParryStrengthScale(minecraft.player, partialTick);
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

    public static void renderHotbarBlockingIndicator(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (attackIndicator == AttackIndicatorStatus.HOTBAR && minecraft.player != null) {
            int posX;
            if (minecraft.player.getMainArm() == HumanoidArm.LEFT) {
                posX = guiGraphics.guiWidth() / 2 - 91 - 22;
            } else {
                posX = guiGraphics.guiWidth() / 2 + 91 + 6;
            }

            int posY = guiGraphics.guiHeight() - 20;
            float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
            double parryStrengthScale = getParryStrengthScale(minecraft.player, partialTick);
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
