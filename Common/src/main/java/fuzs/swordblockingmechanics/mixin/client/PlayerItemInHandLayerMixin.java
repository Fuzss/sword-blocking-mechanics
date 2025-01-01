package fuzs.swordblockingmechanics.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.api.client.util.v1.RenderPropertyKey;
import fuzs.swordblockingmechanics.SwordBlockingMechanics;
import fuzs.swordblockingmechanics.client.handler.FirstPersonRenderingHandler;
import fuzs.swordblockingmechanics.client.helper.AdvancedBlockingRenderer;
import fuzs.swordblockingmechanics.config.ClientConfig;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerItemInHandLayer.class)
abstract class PlayerItemInHandLayerMixin<S extends PlayerRenderState, M extends EntityModel<S> & ArmedModel & HeadedModel> extends ItemInHandLayer<S, M> {
    @Shadow
    @Final
    private ItemRenderer itemRenderer;

    public PlayerItemInHandLayerMixin(RenderLayerParent<S, M> renderer, ItemRenderer itemRenderer) {
        super(renderer, itemRenderer);
    }

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    protected void renderArmWithItem(S renderState, @Nullable BakedModel itemModel, ItemStack itemStack, ItemDisplayContext itemDisplayContext, HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo callback) {
        if (SwordBlockingMechanics.CONFIG.get(ClientConfig.class).simpleBlockingPose) return;
        if (!itemStack.isEmpty() && renderState.isUsingItem) {
            InteractionHand interactionHand =
                    humanoidArm == renderState.mainArm ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            if (renderState.useItemHand == interactionHand && RenderPropertyKey.getRenderProperty(renderState,
                    FirstPersonRenderingHandler.IS_BLOCKING_RENDER_PROPERTY_KEY)) {
                BakedModel bakedModel = humanoidArm == HumanoidArm.RIGHT ? renderState.rightHandItemModel :
                        renderState.leftHandItemModel;
                AdvancedBlockingRenderer.renderBlockingWithSword(this.itemRenderer,
                        this.getParentModel(),
                        itemStack,
                        itemDisplayContext,
                        humanoidArm,
                        poseStack,
                        bufferSource,
                        packedLight,
                        bakedModel);
                callback.cancel();
            }
        }
    }
}
