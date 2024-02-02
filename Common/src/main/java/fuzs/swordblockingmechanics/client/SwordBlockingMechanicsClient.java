package fuzs.swordblockingmechanics.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.event.v1.MovementInputUpdateCallback;
import fuzs.puzzleslib.api.client.event.v1.RenderGuiElementEvents;
import fuzs.puzzleslib.api.client.event.v1.RenderHandCallback;
import fuzs.swordblockingmechanics.client.handler.AttackIndicatorInGuiHandler;
import fuzs.swordblockingmechanics.client.handler.FirstPersonRenderingHandler;
import fuzs.swordblockingmechanics.client.handler.MovementSlowdownHandler;

public class SwordBlockingMechanicsClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerHandlers();
    }

    private static void registerHandlers() {
        RenderHandCallback.EVENT.register(FirstPersonRenderingHandler::onRenderHand);
        MovementInputUpdateCallback.EVENT.register(MovementSlowdownHandler::onMovementInputUpdate);
        RenderGuiElementEvents.before(RenderGuiElementEvents.CROSSHAIR).register(AttackIndicatorInGuiHandler::onBeforeRenderGuiElement);
        RenderGuiElementEvents.after(RenderGuiElementEvents.CROSSHAIR).register((minecraft, guiGraphics, tickDelta, screenWidth, screenHeight) -> AttackIndicatorInGuiHandler.onAfterRenderGuiElement(RenderGuiElementEvents.CROSSHAIR, minecraft, guiGraphics, tickDelta, screenWidth, screenHeight));
        RenderGuiElementEvents.before(RenderGuiElementEvents.HOTBAR).register(AttackIndicatorInGuiHandler::onBeforeRenderGuiElement);
        RenderGuiElementEvents.after(RenderGuiElementEvents.HOTBAR).register((minecraft, guiGraphics, tickDelta, screenWidth, screenHeight) -> AttackIndicatorInGuiHandler.onAfterRenderGuiElement(RenderGuiElementEvents.HOTBAR, minecraft, guiGraphics, tickDelta, screenWidth, screenHeight));
    }
}
