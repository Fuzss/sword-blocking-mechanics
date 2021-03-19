package com.fuzs.swordblockingmechanics.client.handler;

import com.fuzs.swordblockingmechanics.SwordBlockingMechanics;
import com.fuzs.swordblockingmechanics.config.ConfigBuildHandler;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.settings.AttackIndicatorStatus;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class GrassSwingHandler {

    private static final ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation(SwordBlockingMechanics.MODID, "textures/gui/icons.png");

//    @SyncProvider(path = {"combat_test", "Hide Offhand"})
    public static Set<Item> hiddenItems = Sets.newHashSet();

    private final Minecraft mc = Minecraft.getInstance();
    private AttackIndicatorStatus attackIndicator = AttackIndicatorStatus.OFF;
    private Item activeItem = Items.AIR;
    private boolean hide;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderHand(final RenderHandEvent evt) {

        if (evt.getHand() != Hand.OFF_HAND) {

            return;
        }

        Item item = evt.getItemStack().getItem();
        if (item != this.activeItem) {

            this.activeItem = item;
            this.hide = hiddenItems.contains(item);
        }

        if (this.hide) {

            evt.setCanceled(true);
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderGameOverlay(final RenderGameOverlayEvent evt) {

        boolean crosshair = evt.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS && ConfigBuildHandler.SHIELD_INDICATOR.get() == AttackIndicatorStatus.CROSSHAIR;
        boolean hotbar = evt.getType() == RenderGameOverlayEvent.ElementType.HOTBAR && ConfigBuildHandler.SHIELD_INDICATOR.get() == AttackIndicatorStatus.HOTBAR;
        if (!crosshair && !hotbar || this.mc.player == null || this.mc.playerController == null || !this.mc.player.isActiveItemStackBlocking()
                || this.mc.playerController.getCurrentGameType() == GameType.SPECTATOR && !this.mc.ingameGUI.isTargetNamedMenuProvider(this.mc.objectMouseOver)) {

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
                    // rendering on top of each other for transparency reasons
                    AbstractGui.blit(width, height, 54, 0, 16, 14, 256, 256);
                    AbstractGui.blit(width, height, 70, 0, 16, 14, 256, 256);
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
                AbstractGui.blit(width, height, 18, 0, 18, 18, 256, 256);

                RenderSystem.disableRescaleNormal();
                RenderSystem.disableBlend();
            }
        }
    }

}
