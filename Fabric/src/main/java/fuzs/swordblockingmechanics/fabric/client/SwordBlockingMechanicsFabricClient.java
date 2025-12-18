package fuzs.swordblockingmechanics.fabric.client;

import fuzs.swordblockingmechanics.SwordBlockingMechanics;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.swordblockingmechanics.client.SwordBlockingMechanicsClient;
import net.fabricmc.api.ClientModInitializer;

public class SwordBlockingMechanicsFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(SwordBlockingMechanics.MOD_ID, SwordBlockingMechanicsClient::new);
    }
}
