package com.fuzs.swordblockingmechanics.mixin.client.accessor;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.util.HandSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FirstPersonRenderer.class)
public interface IFirstPersonRendererAccessor {

    @Invoker
    void invokeTransformSideFirstPerson(MatrixStack matrixStackIn, HandSide handIn, float equippedProg);

}