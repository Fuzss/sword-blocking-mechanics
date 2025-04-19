package fuzs.swordblockingmechanics.neoforge.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import fuzs.swordblockingmechanics.SwordBlockingMechanics;
import fuzs.swordblockingmechanics.client.SwordBlockingMechanicsClient;
import fuzs.swordblockingmechanics.data.client.ModLanguageProvider;
import fuzs.swordblockingmechanics.neoforge.data.ModSoundDefinitionProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = SwordBlockingMechanics.MOD_ID, dist = Dist.CLIENT)
public class SwordBlockingMechanicsNeoForgeClient {

    public SwordBlockingMechanicsNeoForgeClient() {
        ClientModConstructor.construct(SwordBlockingMechanics.MOD_ID, SwordBlockingMechanicsClient::new);
        DataProviderHelper.registerDataProviders(SwordBlockingMechanics.MOD_ID, ModLanguageProvider::new,
                ModSoundDefinitionProvider::new
        );
    }
}
