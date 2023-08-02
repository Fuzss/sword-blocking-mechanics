package fuzs.swordblockingmechanics.mixin.client.accessor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemInHandRenderer.class)
public interface ItemInHandRendererAccessor {

    @Invoker("applyItemArmAttackTransform")
    void swordblockingmechanics$callApplyItemArmAttackTransform(PoseStack matrixStackIn, HumanoidArm handIn, float swingProgress);

    @Invoker("applyItemArmTransform")
    void swordblockingmechanics$callApplyItemArmTransform(PoseStack matrixStackIn, HumanoidArm handIn, float equippedProg);
}
