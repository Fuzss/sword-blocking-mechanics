package fuzs.swordblockingmechanics.common.mixin.client;

import fuzs.puzzleslib.common.api.client.renderer.v1.RenderStateExtraData;
import fuzs.swordblockingmechanics.common.SwordBlockingMechanics;
import fuzs.swordblockingmechanics.common.client.handler.FirstPersonRenderingHandler;
import fuzs.swordblockingmechanics.common.config.ClientConfig;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
abstract class HumanoidModelMixin<T extends HumanoidRenderState> extends EntityModel<T> {
    @Shadow
    @Final
    public ModelPart rightArm;
    @Shadow
    @Final
    public ModelPart leftArm;

    protected HumanoidModelMixin(ModelPart root) {
        super(root);
    }

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/client/model/HumanoidModel;setupAttackAnimation(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V"))
    public void setupAnim(T state, CallbackInfo callback) {
        if (state.isUsingItem && RenderStateExtraData.getOrDefault(state,
                FirstPersonRenderingHandler.IS_BLOCKING_RENDER_PROPERTY_KEY,
                false)) {
            InteractionHand interactionHand =
                    state.mainArm == HumanoidArm.RIGHT ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            if (state.useItemHand == interactionHand) {
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
