package com.example.examplemod;

import net.java.games.input.Keyboard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = ExampleMod.MODID, name = ExampleMod.NAME, version = ExampleMod.VERSION)
public class ExampleMod
{
    public static final String MODID = "examplemod";
    public static final String NAME = "Example Mod";
    public static final String VERSION = "1.0";

    private static Logger logger;
    private final Minecraft mc = Minecraft.getMinecraft();

    private float prevEquippedProgress;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
    @SubscribeEvent
    public void preRenderLiving(RenderLivingEvent.Pre event) {
        EntityLivingBase entity = event.getEntity();
        if (entity == mc.player) {
            ItemStack heldStack = mc.player.getHeldItem(EnumHand.MAIN_HAND);
            if (heldStack.getItem() instanceof ItemSword && mc.gameSettings.keyBindUseItem.isKeyDown()) {
                entity.swingingHand = EnumHand.MAIN_HAND;
                entity.swingProgress = 1F;
            }
        }
    } */

    @SubscribeEvent
    public void livingHurt(LivingHurtEvent event) {
        if (isBlocking()) {
            float damageAmount = event.getAmount();
            if (!event.getSource().isUnblockable() && damageAmount > 0.0F) {
                damageAmount = (1.0F + damageAmount) * 0.5F;
                event.setAmount(damageAmount);
            }
        }
    }

    @SubscribeEvent
    public void renderSpecificHand(RenderSpecificHandEvent event) {
        if (mc.player != null) {
            EntityPlayerSP abstractclientplayer = mc.player;
            ItemStack heldStack = mc.player.getHeldItem(EnumHand.MAIN_HAND);
            if (heldStack.getItem() instanceof ItemSword && mc.gameSettings.keyBindUseItem.isKeyDown()) {
                event.setCanceled(true);
                float partialTicks = event.getPartialTicks();
                float f2 = abstractclientplayer.prevRotationPitch + (abstractclientplayer.rotationPitch - abstractclientplayer.prevRotationPitch) * partialTicks;
                float f3 = abstractclientplayer.prevRotationYaw + (abstractclientplayer.rotationYaw - abstractclientplayer.prevRotationYaw) * partialTicks;
                func_178101_a(f2, f3);
                func_178109_a(abstractclientplayer);
                func_178110_a(abstractclientplayer, partialTicks);
                GlStateManager.enableRescaleNormal();
                GlStateManager.pushMatrix();
                float equippedProgress = event.getEquipProgress();
                float f = 1.0F - (this.prevEquippedProgress + (equippedProgress - this.prevEquippedProgress) * partialTicks);
                prevEquippedProgress = equippedProgress;
                transformFirstPersonItem(event.getEquipProgress(), 0.0F);
                renderBlocking();
                ItemRenderer renderer = new ItemRenderer(mc);
                GlStateManager.scale(2.5F, 2.5F, 2.5F);
                renderer.renderItemSide(this.mc.player, heldStack, ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, false);
                GlStateManager.popMatrix();
                GlStateManager.disableRescaleNormal();
                RenderHelper.disableStandardItemLighting();
            }
        }
    }

    private boolean isBlocking() {
        boolean blocking = false;
        EntityPlayerSP abstractclientplayer = this.mc.player;
        if (abstractclientplayer != null) {
            ItemStack heldStack = abstractclientplayer.getHeldItem(EnumHand.MAIN_HAND);
            if (!heldStack.isEmpty()) {
                if (heldStack.getItem() instanceof ItemSword && mc.gameSettings.keyBindUseItem.isKeyDown()) {
                    blocking = true;
                }
            }
        }
        return blocking;
    }

    private void renderBlocking()
    {
        GlStateManager.translate(-0.5F, 0.2F, 0.0F);
        GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(15.0F, 0.0F, 1.0F, 0.0F);
    }

    /**
     * Performs transformations prior to the rendering of a held item in first person.
     */
    private void transformFirstPersonItem(float equipProgress, float swingProgress)
    {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        float f = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        float f1 = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
        GlStateManager.rotate(f * -20.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f1 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
    }

    private void func_178101_a(float angle, float p_178101_2_)
    {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(angle, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(p_178101_2_, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    private void func_178109_a(EntityPlayerSP clientPlayer)
    {
        int i = this.mc.world.getCombinedLight(new BlockPos(clientPlayer.posX, clientPlayer.posY + (double)clientPlayer.getEyeHeight(), clientPlayer.posZ), 0);
        float f = (float)(i & 65535);
        float f1 = (float)(i >> 16);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, f, f1);
    }

    private void func_178110_a(EntityPlayerSP entityplayerspIn, float partialTicks)
    {
        float f = entityplayerspIn.prevRenderArmPitch + (entityplayerspIn.renderArmPitch - entityplayerspIn.prevRenderArmPitch) * partialTicks;
        float f1 = entityplayerspIn.prevRenderArmYaw + (entityplayerspIn.renderArmYaw - entityplayerspIn.prevRenderArmYaw) * partialTicks;
        GlStateManager.rotate((entityplayerspIn.rotationPitch - f) * 0.1F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate((entityplayerspIn.rotationYaw - f1) * 0.1F, 0.0F, 1.0F, 0.0F);
    }
}
