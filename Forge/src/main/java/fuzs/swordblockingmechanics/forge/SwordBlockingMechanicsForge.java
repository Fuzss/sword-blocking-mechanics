package fuzs.swordblockingmechanics.forge;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.forge.api.capability.v3.ForgeCapabilityHelper;
import fuzs.swordblockingmechanics.SwordBlockingMechanics;
import fuzs.swordblockingmechanics.init.ModRegistry;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod(SwordBlockingMechanics.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SwordBlockingMechanicsForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(SwordBlockingMechanics.MOD_ID, SwordBlockingMechanics::new);
        registerCapabilities();
    }

    private static void registerCapabilities() {
        ForgeCapabilityHelper.setCapabilityToken(ModRegistry.PARRY_COOLDOWN_CAPABILITY, new CapabilityToken<>() {
            // NO-OP
        });
    }
}
