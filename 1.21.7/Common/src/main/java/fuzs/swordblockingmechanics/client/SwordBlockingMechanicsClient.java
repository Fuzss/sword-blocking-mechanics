package fuzs.swordblockingmechanics.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.event.v1.gui.RenderGuiEvents;
import fuzs.puzzleslib.api.client.event.v1.gui.RenderGuiLayerEvents;
import fuzs.puzzleslib.api.client.event.v1.renderer.ExtractRenderStateCallback;
import fuzs.puzzleslib.api.client.event.v1.renderer.RenderHandEvents;
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
        RenderGuiLayerEvents.after(RenderGuiLayerEvents.CROSSHAIR)
                .register(AttackIndicatorInGuiHandler.onAfterRenderGuiLayer(RenderGuiLayerEvents.CROSSHAIR));
        RenderGuiLayerEvents.after(RenderGuiLayerEvents.HOTBAR)
                .register(AttackIndicatorInGuiHandler.onAfterRenderGuiLayer(RenderGuiLayerEvents.HOTBAR));
        RenderHandEvents.BOTH.register(FirstPersonRenderingHandler::onRenderBothHands);
        ExtractRenderStateCallback.EVENT.register(FirstPersonRenderingHandler::onExtractRenderState);
    }
}
