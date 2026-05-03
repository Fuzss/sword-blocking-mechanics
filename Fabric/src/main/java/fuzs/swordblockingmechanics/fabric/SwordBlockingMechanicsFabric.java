package fuzs.swordblockingmechanics.fabric;

import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import fuzs.swordblockingmechanics.common.SwordBlockingMechanics;
import net.fabricmc.api.ModInitializer;

public class SwordBlockingMechanicsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(SwordBlockingMechanics.MOD_ID, SwordBlockingMechanics::new);
    }
}
