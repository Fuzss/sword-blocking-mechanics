package com.fuzs.swordblockingmechanics.mixin.client.accessor;

import net.minecraft.client.gui.IngameGui;
import net.minecraft.util.math.RayTraceResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(IngameGui.class)
public interface IIngameGuiAccessor {

    @Invoker
    boolean callIsTargetNamedMenuProvider(RayTraceResult rayTraceIn);

}
