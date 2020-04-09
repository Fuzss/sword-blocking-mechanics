package com.fuzs.swordblockingcombat.capability.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityDispatcher<T extends INBTSerializable<CompoundNBT>> implements ICapabilitySerializable<CompoundNBT> {

    private final Capability<T> capability;
    private final T storage;

    public CapabilityDispatcher(T storage, Capability<T> capability) {

        this.storage = storage;
        this.capability = capability;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <S> LazyOptional<S> getCapability(@Nonnull Capability<S> capability, @Nullable Direction facing) {

        return capability == this.capability  ? LazyOptional.of(() -> (S) this.storage) : LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {

        return this.storage.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {

        this.storage.deserializeNBT(nbt);
    }

}