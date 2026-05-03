package fuzs.swordblockingmechanics.common.client;

import fuzs.puzzleslib.common.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.common.api.client.core.v1.context.GuiLayersContext;
import fuzs.puzzleslib.common.api.client.event.v1.ClientTickEvents;
import fuzs.puzzleslib.common.api.client.event.v1.gui.RenderGuiEvents;
import fuzs.puzzleslib.common.api.client.event.v1.renderer.ExtractEntityRenderStateCallback;
import fuzs.puzzleslib.common.api.client.event.v1.renderer.RenderHandEvents;
import fuzs.swordblockingmechanics.common.SwordBlockingMechanics;
import fuzs.swordblockingmechanics.common.client.handler.AttackIndicatorInGuiHandler;
import fuzs.swordblockingmechanics.common.client.handler.FirstPersonRenderingHandler;

public class SwordBlockingMechanicsClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        RenderGuiEvents.BEFORE.register(AttackIndicatorInGuiHandler::onBeforeRenderGui);
        RenderGuiEvents.AFTER.register(AttackIndicatorInGuiHandler::onAfterRenderGui);
        RenderHandEvents.BOTH.register(FirstPersonRenderingHandler::onRenderBothHands);
        ExtractEntityRenderStateCallback.EVENT.register(FirstPersonRenderingHandler::onExtractEntityRenderState);
        ClientTickEvents.END.register(AttackIndicatorInGuiHandler::onEndClientTick);
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
