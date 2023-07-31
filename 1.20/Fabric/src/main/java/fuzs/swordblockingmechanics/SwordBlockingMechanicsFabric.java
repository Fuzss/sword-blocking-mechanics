package fuzs.swordblockingmechanics;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.fabricmc.api.ModInitializer;

public class SwordBlockingMechanicsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ModConstructor.construct(SwordBlockingMechanics.MOD_ID, SwordBlockingMechanics::new);
    }
}
