package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod(modid = ExampleMod.MODID, name = ExampleMod.NAME, version = ExampleMod.VERSION)
public class ExampleMod
{
    public static final String MODID = "examplemod";
    public static final String NAME = "Example Mod";
    public static final String VERSION = "1.0";

    private final Minecraft mc = Minecraft.getMinecraft();

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
                Entity attacker = event.getSource().getImmediateSource();
                if (attacker instanceof EntityLivingBase && !event.getSource().isProjectile()) {
                    EntityPlayerSP playerSP = this.mc.player;
                    EntityLivingBase livingAttacker = (EntityLivingBase)attacker;
                    livingAttacker.knockBack(playerSP, 0.5F, playerSP.posX - livingAttacker.posX, playerSP.posZ - livingAttacker.posZ);
                }
            }
        }
    }

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            if (isBlocking()) {
                this.mc.player.setActiveHand(EnumHand.MAIN_HAND);
            }
        }
    }

    @SubscribeEvent
    public void renderSpecificHand(RenderSpecificHandEvent event) {
        if (isBlocking()) {
            ItemStack heldMainStack = this.mc.player.getHeldItem(EnumHand.MAIN_HAND);
            ItemStack heldOffStack = this.mc.player.getHeldItem(EnumHand.OFF_HAND);
            if (heldOffStack.getItemUseAction() == EnumAction.NONE) {
                if (event.getHand() == EnumHand.MAIN_HAND) {
                    event.setCanceled(true);
                }
                GlStateManager.pushMatrix();
                transformFirstPersonItem();
                renderBlocking();
                GlStateManager.scale(2.5F, 2.5F, 2.5F);
                this.mc.getItemRenderer().renderItemSide(this.mc.player, heldMainStack, ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, false);
                GlStateManager.popMatrix();
            }
        }
    }

    /**
     * Returns if the player is blocking.
     */
    private boolean isBlocking() {
        boolean blocking = false;
        if (this.mc.player != null) {
            boolean fullStrength = this.mc.player.getCooledAttackStrength(1.0F) == 1.0F;
            ItemStack heldStack = this.mc.player.getHeldItem(EnumHand.MAIN_HAND);
            if (!heldStack.isEmpty()) {
                if (heldStack.getItem() instanceof ItemSword && mc.gameSettings.keyBindUseItem.isKeyDown() && fullStrength) {
                    blocking = true;
                }
            }
        }
        return blocking;
    }

    /**
     * Renders the ite in blocking position.
     */
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
    private void transformFirstPersonItem()
    {
        GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
        GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(0.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(0.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(0.4F, 0.4F, 0.4F);
    }
}
