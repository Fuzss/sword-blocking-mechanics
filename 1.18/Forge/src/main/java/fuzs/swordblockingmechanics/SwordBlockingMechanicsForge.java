package fuzs.swordblockingmechanics;

import fuzs.puzzleslib.api.capability.v2.ForgeCapabilityHelper;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.swordblockingmechanics.capability.ParryCooldownCapability;
import fuzs.swordblockingmechanics.data.ModItemTagsProvider;
import fuzs.swordblockingmechanics.data.ModLanguageProvider;
import fuzs.swordblockingmechanics.data.ModSoundDefinitionProvider;
import fuzs.swordblockingmechanics.init.ModRegistry;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod(SwordBlockingMechanics.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SwordBlockingMechanicsForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(SwordBlockingMechanics.MOD_ID, SwordBlockingMechanics::new);
        registerCapabilities();
    }

    private static void registerCapabilities() {
        ForgeCapabilityHelper.setCapabilityToken(ModRegistry.PARRY_COOLDOWN_CAPABILITY, new CapabilityToken<ParryCooldownCapability>() {});
    }

    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent evt) {
        evt.getGenerator().addProvider(new ModItemTagsProvider(evt, SwordBlockingMechanics.MOD_ID));
        evt.getGenerator().addProvider(new ModLanguageProvider(evt, SwordBlockingMechanics.MOD_ID));
        evt.getGenerator().addProvider(new ModSoundDefinitionProvider(evt, SwordBlockingMechanics.MOD_ID));
    }
}
