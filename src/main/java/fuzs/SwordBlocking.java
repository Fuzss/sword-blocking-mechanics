package fuzs;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = SwordBlocking.MODID, name = SwordBlocking.NAME, version = SwordBlocking.VERSION)
public class SwordBlocking
{
    public static final String MODID = "swordblocking";
    public static final String NAME = "Sword Blocking";
    public static final String VERSION = "1.0";

    private final Minecraft mc = Minecraft.getMinecraft();

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void preRenderLiving(RenderLivingEvent.Pre event) {
        EntityLivingBase entity = event.getEntity();
        if (entity instanceof EntityPlayer) {
            if (isBlocking((EntityPlayer) entity)) {
                entity.swingProgressInt = 2;
                entity.swingArm(EnumHand.MAIN_HAND);
            }
        }
    }

    @SubscribeEvent
    public void livingAttack(LivingAttackEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            if (isBlocking(player)) {
                float damageAmount = event.getAmount();
                if (!event.getSource().isUnblockable() && damageAmount > 0.0F) {
                    event.setCanceled(true);
                    Entity attacker = event.getSource().getImmediateSource();
                    if (attacker instanceof EntityLivingBase && !event.getSource().isProjectile()) {
                        EntityLivingBase livingAttacker = (EntityLivingBase) attacker;
                        livingAttacker.knockBack(player, 0.5F, player.posX - livingAttacker.posX, player.posZ - livingAttacker.posZ);
                    }
                    playSound(player);
                    player.spawnSweepParticles();
                    damageSword(player, damageAmount);
                    player.resetCooldown();
                }
            }
        }
    }

    private void playSound(EntityPlayer player) {
        //player.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0F, 0.8F + player.world.rand.nextFloat() * 0.4F);
        player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ITEM_SHIELD_BLOCK, player.getSoundCategory(), 1.0F, 0.8F + player.world.rand.nextFloat() * 0.4F);
    }

    private void damageSword(EntityPlayer player, float damage)
    {
        if (damage >= 3.0F)
        {
            ItemStack copyBeforeUse = player.getHeldItem(EnumHand.MAIN_HAND).copy();
            int i = 1 + MathHelper.floor(damage);
            player.getHeldItem(EnumHand.MAIN_HAND).damageItem(i, player);

            if (player.getHeldItem(EnumHand.MAIN_HAND).isEmpty())
            {
                net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, copyBeforeUse, EnumHand.MAIN_HAND);
                player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
                player.renderBrokenItemStack(player.getHeldItem(EnumHand.MAIN_HAND));
            }
        }
    }

/**
    @SubscribeEvent
    public void livingHurt(LivingHurtEvent event) {
        if (isBlocking()) {
            float damageAmount = event.getAmount();
            if (!event.getSource().isUnblockable() && damageAmount > 0.0F) {
                //damageAmount = (1.0F + damageAmount) * 0.5F;
                damageAmount = 0.0F;
                event.setAmount(damageAmount);
                Entity attacker = event.getSource().getImmediateSource();
                if (attacker instanceof EntityLivingBase && !event.getSource().isProjectile()) {
                    EntityLivingBase livingAttacker = (EntityLivingBase) attacker;
                    Entity player = event.getEntity();
                    livingAttacker.knockBack(player, 0.5F, player.posX - livingAttacker.posX, player.posZ - livingAttacker.posZ);
                }
            }
        }
    }

    @SubscribeEvent
    public void livingKnockBack(LivingKnockBackEvent event) {
        if (isBlocking() && event.getEntity() instanceof EntityPlayer) {
            event.setCanceled(true);
        }
    } */

    @SubscribeEvent
    public void renderSpecificHand(RenderSpecificHandEvent event) {
        if (this.mc.player != null) {
            EntityPlayerSP playerSP = this.mc.player;
            if (isBlocking(playerSP)) {
                ItemStack heldMainStack = playerSP.getHeldItem(EnumHand.MAIN_HAND);
                if (event.getHand() == EnumHand.MAIN_HAND) {
                    event.setCanceled(true);
                }
                GlStateManager.pushMatrix();
                transformFirstPersonItem();
                renderBlocking();
                GlStateManager.scale(2.5F, 2.5F, 2.5F);
                this.mc.getItemRenderer().renderItemSide(playerSP, heldMainStack, ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, false);
                GlStateManager.popMatrix();
            }
        }
    }

    /**
     * Returns if the player is blocking.
     */
    private boolean isBlocking(EntityPlayer player) {
        boolean blocking = false;
        boolean fullStrength = player.getCooledAttackStrength(1.0F) == 1.0F;
        ItemStack heldMainStack = player.getHeldItem(EnumHand.MAIN_HAND);
        ItemStack heldOffStack = player.getHeldItem(EnumHand.OFF_HAND);
        if (!heldMainStack.isEmpty() && heldOffStack.getItemUseAction() == EnumAction.NONE) {
            if (heldMainStack.getItem() instanceof ItemSword && mc.gameSettings.keyBindUseItem.isKeyDown() && fullStrength) {
                blocking = true;
            }
        }
        return blocking;
    }

    /**
     * Renders the item in blocking position.
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
