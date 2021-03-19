package com.fuzs.swordblockingmechanics.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    public void getUseDuration(CallbackInfoReturnable<Integer> callbackInfo) {

        if (this.getUseAction() == UseAction.DRINK) {

            callbackInfo.setReturnValue(20);
        }
    }

    @Shadow
    public abstract UseAction getUseAction();

}
