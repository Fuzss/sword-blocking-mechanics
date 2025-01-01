package fuzs.swordblockingmechanics.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.event.v1.entity.player.MovementInputUpdateCallback;
import fuzs.puzzleslib.api.client.event.v1.gui.RenderGuiLayerEvents;
import fuzs.puzzleslib.api.client.event.v1.renderer.RenderHandEvents;
import fuzs.swordblockingmechanics.client.handler.AttackIndicatorInGuiHandler;
import fuzs.swordblockingmechanics.client.handler.FirstPersonRenderingHandler;
import fuzs.swordblockingmechanics.client.handler.MovementSlowdownHandler;
import net.minecraft.world.InteractionHand;

public class SwordBlockingMechanicsClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        RenderHandEvents.MAIN_HAND.register(FirstPersonRenderingHandler.onRenderHand(InteractionHand.MAIN_HAND));
        RenderHandEvents.OFF_HAND.register(
                FirstPersonRenderingHandler.onRenderHand(InteractionHand.OFF_HAND)::onRenderMainHand);
        MovementInputUpdateCallback.EVENT.register(MovementSlowdownHandler::onMovementInputUpdate);
        RenderGuiLayerEvents.before(RenderGuiLayerEvents.CROSSHAIR).register(
                AttackIndicatorInGuiHandler::onBeforeRenderGuiLayer);
        RenderGuiLayerEvents.after(RenderGuiLayerEvents.CROSSHAIR).register(
                AttackIndicatorInGuiHandler.onAfterRenderGuiLayer(RenderGuiLayerEvents.CROSSHAIR));
        RenderGuiLayerEvents.before(RenderGuiLayerEvents.HOTBAR).register(
                AttackIndicatorInGuiHandler::onBeforeRenderGuiLayer);
        RenderGuiLayerEvents.after(RenderGuiLayerEvents.HOTBAR).register(
                AttackIndicatorInGuiHandler.onAfterRenderGuiLayer(RenderGuiLayerEvents.HOTBAR));
    }
}
