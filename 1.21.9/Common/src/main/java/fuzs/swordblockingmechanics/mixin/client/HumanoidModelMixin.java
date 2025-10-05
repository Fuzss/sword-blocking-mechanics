package fuzs.swordblockingmechanics.mixin.client;

import fuzs.puzzleslib.api.client.renderer.v1.RenderStateExtraData;
import fuzs.swordblockingmechanics.SwordBlockingMechanics;
import fuzs.swordblockingmechanics.client.handler.FirstPersonRenderingHandler;
import fuzs.swordblockingmechanics.config.ClientConfig;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
abstract class HumanoidModelMixin<T extends HumanoidRenderState> extends EntityModel<T> {
    @Shadow
    public ModelPart rightArm;
    @Shadow
    public ModelPart leftArm;

    protected HumanoidModelMixin(ModelPart root) {
        super(root);
    }

    @Inject(method = "setupAnim",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/model/HumanoidModel;setupAttackAnimation(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;F)V"))
    public void setupAnim(T renderState, CallbackInfo callback) {
        if (renderState instanceof AvatarRenderState && renderState.isUsingItem) {
            if (RenderStateExtraData.getOrDefault(renderState,
                    FirstPersonRenderingHandler.IS_BLOCKING_RENDER_PROPERTY_KEY,
                    false)) {
                InteractionHand interactionHand =
                        renderState.mainArm == HumanoidArm.RIGHT ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
                if (renderState.useItemHand == interactionHand) {
                    this.rightArm.xRot = this.rightArm.xRot - Mth.PI * 2.0F / 10.0F;
                    if (SwordBlockingMechanics.CONFIG.get(ClientConfig.class).simpleBlockingPose) {
                        this.rightArm.yRot = -Mth.PI / 6.0F;
                    }
                } else {
                    this.leftArm.xRot = this.leftArm.xRot - Mth.PI * 2.0F / 10.0F;
                    if (SwordBlockingMechanics.CONFIG.get(ClientConfig.class).simpleBlockingPose) {
                        this.leftArm.yRot = Mth.PI / 6.0F;
                    }
                }
            }
        }
    }
}
