package fuzs.swordblockingmechanics.fabric.client;

import fuzs.swordblockingmechanics.common.SwordBlockingMechanics;
import fuzs.puzzleslib.common.api.client.core.v1.ClientModConstructor;
import fuzs.swordblockingmechanics.common.client.SwordBlockingMechanicsClient;
import net.fabricmc.api.ClientModInitializer;

public class SwordBlockingMechanicsFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(SwordBlockingMechanics.MOD_ID, SwordBlockingMechanicsClient::new);
    }
}
