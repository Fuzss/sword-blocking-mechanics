package com.fuzs.swordblockingcombat.registry;

import com.fuzs.swordblockingcombat.SwordBlockingCombat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = SwordBlockingCombat.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(SwordBlockingCombat.MODID)
public class SwordBlockingRegistry {

    @ObjectHolder("item.sword.block")
    public static final SoundEvent ITEM_SWORD_BLOCK = null;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void onRegistrySoundEvent(RegistryEvent.Register<SoundEvent> evt) {

        IForgeRegistry<SoundEvent> registry = evt.getRegistry();
        if (!registry.equals(ForgeRegistries.SOUND_EVENTS)) {

            return;
        }

        register(registry, new SoundEvent(locate("item.sword.block")), "item.sword.block");
    }

    @SuppressWarnings("SameParameterValue")
    private static <T extends IForgeRegistryEntry<T>> void register(IForgeRegistry<T> registry, T entry, String name) {

        entry.setRegistryName(locate(name));
        registry.register(entry);
    }

    public static ResourceLocation locate(String name) {

        return new ResourceLocation(SwordBlockingCombat.MODID, name);
    }

}
