package fuzs.swordblockingmechanics.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.event.v1.RenderHandCallback;
import fuzs.swordblockingmechanics.client.handler.FirstPersonRenderingHandler;

public class SwordBlockingMechanicsClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerHandlers();
    }

    private static void registerHandlers() {
        RenderHandCallback.EVENT.register(FirstPersonRenderingHandler::onRenderHand);
    }
}
