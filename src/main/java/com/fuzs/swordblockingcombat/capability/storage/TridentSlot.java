package com.fuzs.swordblockingcombat.capability.storage;

import com.fuzs.swordblockingcombat.capability.Capabilities;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class TridentSlot implements ITridentSlot {

    private int slot = -1;

    @Override
    public void setSlot(int slot) {

        this.slot = slot;
    }

    @Override
    public int getSlot() {

        return this.slot;
    }

    @Override
    public CompoundNBT serializeNBT() {

        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt(Capabilities.TRIDENT_SLOT_NAME, this.slot);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {

        this.slot = nbt.getInt(Capabilities.TRIDENT_SLOT_NAME);
    }

    public boolean addToInventory(PlayerInventory inventory, ItemStack stack) {

        int slot = this.getSlotInInventory(inventory);

        if (this.isOffhand(slot)) {

            inventory.offHandInventory.set(0, stack);
        } else {

            return inventory.add(slot, stack);
        }

        return true;
    }

    private int getSlotInInventory(PlayerInventory inventory) {

        if (this.getSlot() != -1) {

            int slot = inventory.getCurrentItem().isEmpty() ? inventory.currentItem : -1;
            if (this.isSlotAvailable(inventory)) {

                slot = this.getSlot();
            }

            return slot;
        }

        return -1;
    }

    private boolean isSlotAvailable(PlayerInventory inventory) {

        return this.isOffhand(this.getSlot()) ? inventory.offHandInventory.get(0).isEmpty() : inventory.getStackInSlot(this.slot).isEmpty();
    }

    private boolean isOffhand(int slot) {

        return slot == 40;
    }

}
