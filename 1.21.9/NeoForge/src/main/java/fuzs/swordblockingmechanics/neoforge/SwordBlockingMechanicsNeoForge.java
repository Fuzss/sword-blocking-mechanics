package fuzs.swordblockingmechanics.neoforge;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import fuzs.swordblockingmechanics.SwordBlockingMechanics;
import fuzs.swordblockingmechanics.data.ModItemTagProvider;
import net.neoforged.fml.common.Mod;

@Mod(SwordBlockingMechanics.MOD_ID)
public class SwordBlockingMechanicsNeoForge {

    public SwordBlockingMechanicsNeoForge() {
        ModConstructor.construct(SwordBlockingMechanics.MOD_ID, SwordBlockingMechanics::new);
        DataProviderHelper.registerDataProviders(SwordBlockingMechanics.MOD_ID, ModItemTagProvider::new);
    }
}
