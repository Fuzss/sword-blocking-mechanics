package com.fuzs.swordblockingcombat.client;

import com.fuzs.swordblockingcombat.config.ConfigValueHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.VideoSettingsScreen;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Arrays;
import java.util.function.BiPredicate;

@OnlyIn(Dist.CLIENT)
public class NoCooldownHandler {

    private final Minecraft mc = Minecraft.getInstance();
    private final FirstPersonRenderer itemRenderer = new FirstPersonRenderer(this.mc);

    private Entity pointedEntity;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderGameOverlay(final RenderGameOverlayEvent.Pre evt) {

        if (ConfigValueHolder.CLASSIC_COMBAT.removeCooldown && this.mc.player != null) {

            boolean flag = evt.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS;
            if (flag || evt.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {

                // disable attack indicator from rendering
                this.mc.player.ticksSinceLastSwing = (int) Math.ceil(this.mc.player.getCooldownPeriod());

                // disable attack indicator from rendering when pointing at a living entity
                if (flag) {
                    this.pointedEntity = this.mc.pointedEntity;
                    this.mc.pointedEntity = null;
                }
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderGameOverlay(final RenderGameOverlayEvent.Post evt) {

        if (ConfigValueHolder.CLASSIC_COMBAT.removeCooldown && evt.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {

            // disable attack indicator from rendering when pointing at a living entity
            this.mc.pointedEntity = this.pointedEntity;
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onGuiInit(final GuiScreenEvent.InitGuiEvent.Post evt) {

        if (ConfigValueHolder.CLASSIC_COMBAT.removeCooldown && evt.getGui() instanceof VideoSettingsScreen) {

            // disable attack indicator button in video settings screen
            VideoSettingsScreen screen = (VideoSettingsScreen) evt.getGui();
            screen.optionsRowList.children().stream().flatMap(it -> it.children().stream()).filter(it -> it instanceof OptionButton)
                    .map(it -> (OptionButton) it).filter(it -> it.enumOptions.equals(AbstractOption.ATTACK_INDICATOR)).findFirst().ifPresent(it -> it.active = false);
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onItemTooltip(final ItemTooltipEvent evt) {

        BiPredicate<Object, String> translation = (component, sequence) -> component instanceof TranslationTextComponent
                && ((TranslationTextComponent) component).getKey().contains(sequence);

        // remove attack speed entry for every tooltip containing it
        if (ConfigValueHolder.CLASSIC_COMBAT.removeCooldown) {

            evt.getToolTip().removeIf(component -> component.getSiblings().stream()
                    .filter(it -> translation.test(it, "attribute.modifier.equals."))
                    .map(it -> ((TranslationTextComponent) it).getFormatArgs())
                    .anyMatch(it -> Arrays.stream(it).anyMatch(ti -> translation.test(ti, "attribute.name.generic.attackSpeed"))));
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent evt) {

        if (ConfigValueHolder.CLASSIC_COMBAT.removeCooldown && evt.phase == TickEvent.Phase.END) {

            if (this.mc.world != null && this.mc.player != null && !this.mc.isGamePaused()) {

                // calculate equipped progress in our own item renderer where it's not reset occasionally
                this.mc.player.ticksSinceLastSwing = (int) Math.ceil(this.mc.player.getCooldownPeriod());
                this.itemRenderer.tick();
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderTick(final TickEvent.RenderTickEvent evt) {

        if (ConfigValueHolder.CLASSIC_COMBAT.removeCooldown && evt.phase == TickEvent.Phase.START) {

            this.syncProgress(this.mc.getFirstPersonRenderer());
        }
    }

    private void syncProgress(FirstPersonRenderer itemRenderer) {

        itemRenderer.equippedProgressMainHand = this.itemRenderer.equippedProgressMainHand;
        itemRenderer.equippedProgressOffHand = this.itemRenderer.equippedProgressOffHand;
        itemRenderer.prevEquippedProgressMainHand = this.itemRenderer.prevEquippedProgressMainHand;
        itemRenderer.prevEquippedProgressOffHand = this.itemRenderer.prevEquippedProgressOffHand;
    }

}
