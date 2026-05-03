package fuzs.swordblockingmechanics.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import fuzs.puzzleslib.common.api.client.renderer.v1.RenderStateExtraData;
import fuzs.swordblockingmechanics.SwordBlockingMechanics;
import fuzs.swordblockingmechanics.client.handler.FirstPersonRenderingHandler;
import fuzs.swordblockingmechanics.client.helper.AdvancedBlockingRenderer;
import fuzs.swordblockingmechanics.config.ClientConfig;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerItemInHandLayer.class)
abstract class PlayerItemInHandLayerMixin<S extends AvatarRenderState, M extends EntityModel<S> & ArmedModel<S> & HeadedModel> extends ItemInHandLayer<S, M> {

    public PlayerItemInHandLayerMixin(RenderLayerParent<S, M> renderLayerParent) {
        super(renderLayerParent);
    }

    @Inject(method = "submitArmWithItem(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lnet/minecraft/client/renderer/item/ItemStackRenderState;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/HumanoidArm;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V",
            at = @At("HEAD"),
            cancellable = true)
    protected void submitArmWithItem(S state, ItemStackRenderState item, ItemStack itemStack, HumanoidArm arm, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, CallbackInfo callback) {
        if (SwordBlockingMechanics.CONFIG.get(ClientConfig.class).simpleBlockingPose) {
            return;
        }

        if (!item.isEmpty() && state.isUsingItem) {
            InteractionHand interactionHand =
                    arm == state.mainArm ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            if (state.useItemHand == interactionHand && RenderStateExtraData.getOrDefault(state,
                    FirstPersonRenderingHandler.IS_BLOCKING_RENDER_PROPERTY_KEY,
                    false)) {
                AdvancedBlockingRenderer.submitBlockingWithSword(state,
                        this.getParentModel(),
                        item,
                        arm,
                        poseStack,
                        submitNodeCollector,
                        lightCoords);
                callback.cancel();
            }
        }
    }
}
