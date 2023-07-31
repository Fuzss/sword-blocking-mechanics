package fuzs.swordblockingmechanics;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.swordblockingmechanics.data.ModItemTagsProvider;
import fuzs.swordblockingmechanics.data.ModLanguageProvider;
import fuzs.swordblockingmechanics.data.ModSoundDefinitionProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod(SwordBlockingMechanics.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SwordBlockingMechanicsForge {

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        ModConstructor.construct(SwordBlockingMechanics.MOD_ID, SwordBlockingMechanics::new);
    }

    @SubscribeEvent
    public static void onGatherData(final GatherDataEvent evt) {
        evt.getGenerator().addProvider(true, new ModItemTagsProvider(evt, SwordBlockingMechanics.MOD_ID));
        evt.getGenerator().addProvider(true, new ModLanguageProvider(evt, SwordBlockingMechanics.MOD_ID));
        evt.getGenerator().addProvider(true, new ModSoundDefinitionProvider(evt, SwordBlockingMechanics.MOD_ID));
    }
}
