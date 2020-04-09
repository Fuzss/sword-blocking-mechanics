package com.fuzs.swordblockingcombat.capability;

import com.fuzs.swordblockingcombat.capability.storage.ITridentSlot;
import com.fuzs.swordblockingcombat.capability.storage.TridentSlot;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class Capabilities {

    @CapabilityInject(ITridentSlot.class)
    public static final Capability<TridentSlot> TRIDENT_SLOT = null;
    public static final String TRIDENT_SLOT_NAME = "InventorySlot";

}
