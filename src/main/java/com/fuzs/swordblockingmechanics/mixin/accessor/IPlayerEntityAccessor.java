package com.fuzs.swordblockingmechanics.mixin.accessor;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerEntity.class)
public interface IPlayerEntityAccessor {

    @Accessor
    ItemStack getItemStackMainHand();

    @Accessor
    void setItemStackMainHand(ItemStack itemStackMainHand);

}
