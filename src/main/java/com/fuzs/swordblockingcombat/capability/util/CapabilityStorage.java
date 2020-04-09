package com.fuzs.swordblockingcombat.capability.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

public class CapabilityStorage<T> implements Capability.IStorage<T> {

    @Override
    public INBT writeNBT(Capability<T> capability, T instance, Direction side) {

        if (instance instanceof INBTSerializable) {

            return ((INBTSerializable<?>) instance).serializeNBT();
        }

        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {

        if (nbt instanceof CompoundNBT) {

            ((INBTSerializable<INBT>) instance).deserializeNBT(nbt);
        }
    }

}