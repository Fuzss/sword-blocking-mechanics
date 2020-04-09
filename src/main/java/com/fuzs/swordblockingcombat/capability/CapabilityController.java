package com.fuzs.swordblockingcombat.capability;

import com.fuzs.swordblockingcombat.SwordBlockingCombat;
import com.fuzs.swordblockingcombat.capability.storage.ITridentSlot;
import com.fuzs.swordblockingcombat.capability.storage.TridentSlot;
import com.fuzs.swordblockingcombat.capability.util.CapabilityDispatcher;
import com.fuzs.swordblockingcombat.capability.util.CapabilityStorage;
import com.fuzs.swordblockingcombat.registry.SwordBlockingRegistry;
import com.google.common.base.CaseFormat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;

@Mod.EventBusSubscriber(modid = SwordBlockingCombat.MODID)
public class CapabilityController {

    public static void register() {

        CapabilityManager.INSTANCE.register(ITridentSlot.class, new CapabilityStorage<>(), TridentSlot::new);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void onAttachCapabilitiesEntity(final AttachCapabilitiesEvent<Entity> evt) {

        if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER && evt.getObject() instanceof TridentEntity) {

            evt.addCapability(SwordBlockingRegistry.locate(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, Capabilities.TRIDENT_SLOT_NAME)), new CapabilityDispatcher<>(new TridentSlot(), Capabilities.TRIDENT_SLOT));
        }
    }

}
