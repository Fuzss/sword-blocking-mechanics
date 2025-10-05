package fuzs.swordblockingmechanics.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.client.renderer.v1.RenderPropertyKey;
import fuzs.swordblockingmechanics.SwordBlockingMechanics;
import fuzs.swordblockingmechanics.client.handler.FirstPersonRenderingHandler;
import fuzs.swordblockingmechanics.client.helper.AdvancedBlockingRenderer;
import fuzs.swordblockingmechanics.config.ClientConfig;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerItemInHandLayer.class)
abstract class PlayerItemInHandLayerMixin<S extends PlayerRenderState, M extends EntityModel<S> & ArmedModel & HeadedModel> extends ItemInHandLayer<S, M> {

    public PlayerItemInHandLayerMixin(RenderLayerParent<S, M> renderLayerParent) {
        super(renderLayerParent);
    }

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    protected void renderArmWithItem(S renderState, ItemStackRenderState itemStackRenderState, HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo callback) {
        if (SwordBlockingMechanics.CONFIG.get(ClientConfig.class).simpleBlockingPose) return;
        if (!itemStackRenderState.isEmpty() && renderState.isUsingItem) {
            InteractionHand interactionHand =
                    humanoidArm == renderState.mainArm ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            if (renderState.useItemHand == interactionHand && RenderPropertyKey.getOrDefault(renderState,
                    FirstPersonRenderingHandler.IS_BLOCKING_RENDER_PROPERTY_KEY,
                    false)) {
                AdvancedBlockingRenderer.renderBlockingWithSword(this.getParentModel(),
                        itemStackRenderState,
                        humanoidArm,
                        poseStack,
                        bufferSource,
                        packedLight);
                callback.cancel();
            }
        }
    }
}
