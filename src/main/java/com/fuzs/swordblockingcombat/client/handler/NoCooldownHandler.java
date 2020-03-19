package com.fuzs.swordblockingcombat.client.handler;

import com.fuzs.swordblockingcombat.config.ConfigBuildHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.VideoSettingsScreen;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiPredicate;

@OnlyIn(Dist.CLIENT)
public class NoCooldownHandler {

    private boolean hasOptiFine;

    private final Minecraft mc = Minecraft.getInstance();
    private final FirstPersonRenderer itemRenderer = new FirstPersonRenderer(this.mc);

    private Entity pointedEntity;
    private int ticksSinceLastSwing;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderGameOverlay(final RenderGameOverlayEvent.Pre evt) {

        boolean hide = ConfigBuildHandler.HIDE_ATTACK_INDICATOR.get();
        if ((ConfigBuildHandler.REMOVE_ATTACK_COOLDOWN.get() || hide) && this.mc.player != null) {

            boolean crosshairs = evt.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS;
            if (crosshairs || evt.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {

                // disable attack indicator from rendering
                this.ticksSinceLastSwing = this.mc.player.ticksSinceLastSwing;
                this.mc.player.ticksSinceLastSwing = (int) Math.ceil(this.mc.player.getCooldownPeriod());

                // disable attack indicator from rendering when pointing at a living entity
                if (hide && crosshairs) {

                    this.pointedEntity = this.mc.pointedEntity;
                    this.mc.pointedEntity = null;
                }
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderGameOverlay(final RenderGameOverlayEvent.Post evt) {

        boolean hide = ConfigBuildHandler.HIDE_ATTACK_INDICATOR.get();
        if ((ConfigBuildHandler.REMOVE_ATTACK_COOLDOWN.get() || hide) && this.mc.player != null) {

            boolean crosshairs = evt.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS;
            if (crosshairs || evt.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {

                // disable attack indicator from rendering
                this.mc.player.ticksSinceLastSwing = this.ticksSinceLastSwing;

                // disable attack indicator from rendering when pointing at a living entity
                if (hide && crosshairs) {

                    this.mc.pointedEntity = this.pointedEntity;
                }
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onGuiInit(final GuiScreenEvent.InitGuiEvent.Post evt) {

        // no easy way to detect OptiFine, so this has to throw an exception once
        if (!this.hasOptiFine && ConfigBuildHandler.HIDE_ATTACK_INDICATOR.get() && evt.getGui() instanceof VideoSettingsScreen) {

            try {

                // disable attack indicator button in video settings screen
                ((VideoSettingsScreen) evt.getGui()).optionsRowList.children().stream().flatMap(it -> it.children().stream()).filter(it -> it instanceof OptionButton)
                        .map(it -> (OptionButton) it).filter(it -> it.enumOptions.equals(AbstractOption.ATTACK_INDICATOR)).findFirst().ifPresent(it -> it.active = false);
            } catch (NoSuchFieldError ignored) {

                this.hasOptiFine = true;
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onItemTooltip(final ItemTooltipEvent evt) {

        // remove attack speed entry for every tooltip containing it
        if (ConfigBuildHandler.NO_COOLDOWN_TOOLTIP.get()) {

            evt.getToolTip().removeIf(component -> component.toString().contains("attribute.name.generic.attackSpeed"));
        }

        if (ConfigBuildHandler.BOOST_SHARPNESS.get()) {

            BiPredicate<Object, String> translation = (component, sequence) -> component instanceof TranslationTextComponent
                    && ((TranslationTextComponent) component).getKey().contains(sequence);

            // modify attack damage to account for sharpness adding 1.0F instead of mostly 0.5F damage
            int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, evt.getItemStack());
            if (i > 1) {

                Optional<Object[]> optionalFormatArgs = Optional.empty();
                for (ITextComponent component : evt.getToolTip()) {

                    optionalFormatArgs = component.getSiblings().stream()
                            .filter(it -> translation.test(it, "attribute.modifier.equals."))
                            .map(it -> ((TranslationTextComponent) it).getFormatArgs())
                            .filter(it -> Arrays.stream(it).anyMatch(ti -> translation.test(ti, "attribute.name.generic.attackDamage")))
                            .findFirst();
                    if (optionalFormatArgs.isPresent()) {
                        break;
                    }
                }

                optionalFormatArgs.ifPresent(formatArgs -> {

                    if (formatArgs.length == 2 && formatArgs[0] instanceof String) {

                        final float oldDamage = Float.parseFloat((String) formatArgs[0]);
                        final float damage = -0.5F + i * 0.5F;
                        formatArgs[0] = ItemStack.DECIMALFORMAT.format(oldDamage + damage);
                    }
                });
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent evt) {

        if (ConfigBuildHandler.REMOVE_ATTACK_COOLDOWN.get() && evt.phase == TickEvent.Phase.END) {

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

        if (ConfigBuildHandler.REMOVE_ATTACK_COOLDOWN.get() && evt.phase == TickEvent.Phase.START) {

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