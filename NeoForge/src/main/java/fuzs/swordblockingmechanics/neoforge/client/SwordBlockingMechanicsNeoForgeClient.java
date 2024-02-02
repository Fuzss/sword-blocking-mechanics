package fuzs.swordblockingmechanics.neoforge.client;

import fuzs.swordblockingmechanics.SwordBlockingMechanics;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.swordblockingmechanics.client.SwordBlockingMechanicsClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = SwordBlockingMechanics.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class SwordBlockingMechanicsNeoForgeClient {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ClientModConstructor.construct(SwordBlockingMechanics.MOD_ID, SwordBlockingMechanicsClient::new);
    }
}
