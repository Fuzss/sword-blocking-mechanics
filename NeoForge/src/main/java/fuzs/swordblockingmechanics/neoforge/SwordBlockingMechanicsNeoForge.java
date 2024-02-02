package fuzs.swordblockingmechanics.neoforge;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import fuzs.swordblockingmechanics.SwordBlockingMechanics;
import fuzs.swordblockingmechanics.data.ModItemTagProvider;
import fuzs.swordblockingmechanics.neoforge.data.ModSoundDefinitionProvider;
import fuzs.swordblockingmechanics.data.client.ModLanguageProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

@Mod(SwordBlockingMechanics.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SwordBlockingMechanicsNeoForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(SwordBlockingMechanics.MOD_ID, SwordBlockingMechanics::new);
        DataProviderHelper.registerDataProviders(SwordBlockingMechanics.MOD_ID,
                ModItemTagProvider::new,
                ModLanguageProvider::new,
                ModSoundDefinitionProvider::new
        );
    }
}
