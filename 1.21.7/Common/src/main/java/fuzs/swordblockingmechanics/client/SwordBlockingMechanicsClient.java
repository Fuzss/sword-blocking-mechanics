package fuzs.swordblockingmechanics.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.core.v1.context.GuiLayersContext;
import fuzs.puzzleslib.api.client.event.v1.gui.RenderGuiEvents;
import fuzs.puzzleslib.api.client.event.v1.renderer.ExtractRenderStateCallback;
import fuzs.puzzleslib.api.client.event.v1.renderer.RenderHandEvents;
import fuzs.swordblockingmechanics.SwordBlockingMechanics;
import fuzs.swordblockingmechanics.client.handler.AttackIndicatorInGuiHandler;
import fuzs.swordblockingmechanics.client.handler.FirstPersonRenderingHandler;

public class SwordBlockingMechanicsClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        RenderGuiEvents.BEFORE.register(AttackIndicatorInGuiHandler::onBeforeRenderGui);
        RenderGuiEvents.AFTER.register(AttackIndicatorInGuiHandler::onAfterRenderGui);
        RenderHandEvents.BOTH.register(FirstPersonRenderingHandler::onRenderBothHands);
        ExtractRenderStateCallback.EVENT.register(FirstPersonRenderingHandler::onExtractRenderState);
    }

    @Override
    public void onRegisterGuiLayers(GuiLayersContext context) {
        context.registerGuiLayer(GuiLayersContext.CROSSHAIR,
                SwordBlockingMechanics.id("crosshair_blocking_indicator"),
                AttackIndicatorInGuiHandler::renderCrosshairBlockingIndicator);
        context.registerGuiLayer(GuiLayersContext.HOTBAR,
                SwordBlockingMechanics.id("hotbar_blocking_indicator"),
                AttackIndicatorInGuiHandler::renderHotbarBlockingIndicator);
    }
}
