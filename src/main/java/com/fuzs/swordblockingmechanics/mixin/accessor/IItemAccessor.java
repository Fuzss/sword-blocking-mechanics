package com.fuzs.swordblockingmechanics.mixin.accessor;

import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Item.class)
public interface IItemAccessor {

    @Accessor
    void setMaxStackSize(int maxStackSize);

}
