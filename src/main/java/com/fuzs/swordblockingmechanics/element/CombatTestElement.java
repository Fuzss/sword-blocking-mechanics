package com.fuzs.swordblockingmechanics.element;

import com.fuzs.puzzleslib_sbm.element.extension.ClientExtensibleElement;
import com.fuzs.swordblockingmechanics.SwordBlockingMechanics;
import com.fuzs.swordblockingmechanics.client.element.CombatTestExtension;
import com.fuzs.swordblockingmechanics.mixin.accessor.IItemAccessor;
import com.fuzs.swordblockingmechanics.mixin.accessor.IPlayerEntityAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class CombatTestElement extends ClientExtensibleElement<CombatTestExtension> {

    public static final Tags.IOptionalNamedTag<Item> OFF_HAND_RENDER_BLACKLIST_TAG = ItemTags.createOptional(new ResourceLocation(SwordBlockingMechanics.MODID, "off_hand_render_blacklist"));

    private boolean throwablesDelay;
    private boolean eatingInterruption;
    private boolean noShieldDelay;
    private boolean passThroughThrowables;
    private boolean fastSwitching;
    public boolean fastDrinking;

    public CombatTestElement() {

        super(element -> new CombatTestExtension((CombatTestElement) element));
    }

    @Override
    public String[] getDescription() {

        return new String[]{"Introduces various tweaks from Combat Test Snapshots and some popular community suggestions."};
    }

    @Override
    public void setupCommon() {

        this.addListener(this::onProjectileImpact);
        this.addListener(this::onPlayerTick);
        this.addListener(this::onItemUseStart);
        this.addListener(this::onRightClickItem);
        this.addListener(this::onLivingDamage);
    }

    @Override
    public void unloadCommon() {

        this.setMaxStackSize(false);
    }

    @Override
    public void setupCommonConfig(ForgeConfigSpec.Builder builder) {

        addToConfig(builder.comment("Increase snowball and egg stack size from 16 to 64, and potion stack size from 1 to 16.").define("Increase Stack Size", true), this::setMaxStackSize);
        addToConfig(builder.comment("Add a delay of 4 ticks between throwing snowballs or eggs, just like with ender pearls.").define("Throwables Delay", true), v -> this.throwablesDelay = v);
        addToConfig(builder.comment("Eating and drinking both are interrupted if the player receives damage.").define("Eating Interruption", true), v -> this.eatingInterruption = v);
        addToConfig(builder.comment("Skip 5 tick warm-up delay when activating a shield.").define("Remove Shield Delay", true), v -> this.noShieldDelay = v);
        addToConfig(builder.comment("Throwables such as snowballs, eggs and ender pearls pass through blocks without a collision shape like grass and flowers.").define("Pass-Through Throwables", true), v -> this.passThroughThrowables = v);
        addToConfig(builder.comment("Attack cooldown is unaffected by switching items.").define("Fast Tool Switching", true), v -> this.fastSwitching = v);
        addToConfig(builder.comment("It only takes 20 ticks to drink liquid foods instead of 32 or 40.").define("Fast Drinking", true), v -> this.fastDrinking = v);
    }

    private void onProjectileImpact(final ProjectileImpactEvent evt) {

        if (this.passThroughThrowables && evt.getEntity() instanceof ProjectileItemEntity) {

            ProjectileItemEntity projectile = (ProjectileItemEntity) evt.getEntity();
            if (evt.getRayTraceResult().getType() == RayTraceResult.Type.BLOCK) {

                // enable item projectiles to pass through blocks without a collision shape
                World world = projectile.getEntityWorld();
                BlockPos pos = ((BlockRayTraceResult) evt.getRayTraceResult()).getPos();
                if (world.getBlockState(pos).getCollisionShape(world, pos).isEmpty()) {

                    evt.setCanceled(true);
                }
            }
        }
    }

    private void onPlayerTick(TickEvent.PlayerTickEvent evt) {

        if (this.fastSwitching && evt.phase == TickEvent.Phase.START) {

            // switching items no longer triggers the attack cooldown
            PlayerEntity player = evt.player;
            ItemStack itemstack = player.getHeldItemMainhand();
            if (!ItemStack.areItemStacksEqual(((IPlayerEntityAccessor) player).getItemStackMainHand(), itemstack)) {

                ((IPlayerEntityAccessor) player).setItemStackMainHand(itemstack.copy());
            }
        }
    }

    private void onItemUseStart(final LivingEntityUseItemEvent.Start evt) {

        if (this.noShieldDelay && evt.getItem().getUseAction() == UseAction.BLOCK) {

            // remove shield activation delay
            evt.setDuration(evt.getItem().getUseDuration() - 5);
        }

        if (this.fastDrinking && evt.getItem().getUseAction() == UseAction.DRINK) {

            evt.setDuration(20);
        }
    }

    private void onRightClickItem(final PlayerInteractEvent.RightClickItem evt) {

        if (this.throwablesDelay) {

            LivingEntity entity = evt.getEntityLiving();
            Item item = evt.getItemStack().getItem();
            if (entity instanceof PlayerEntity && (item instanceof SnowballItem || item instanceof EggItem)) {

                // add delay after using an item
                ((PlayerEntity) entity).getCooldownTracker().setCooldown(item, 4);
            }
        }
    }

    private void onLivingDamage(final LivingDamageEvent evt) {

        if (this.eatingInterruption) {

            LivingEntity entity = evt.getEntityLiving();
            UseAction useAction = entity.getActiveItemStack().getUseAction();
            if (useAction == UseAction.EAT || useAction == UseAction.DRINK) {

                entity.resetActiveHand();
            }
        }
    }

    private void setMaxStackSize(boolean increase) {

        int throwablesAmount = increase ? 64 : 16;
        int potionAmount = increase ? 16 : 1;
        ((IItemAccessor) Items.SNOWBALL).setMaxStackSize(throwablesAmount);
        ((IItemAccessor) Items.EGG).setMaxStackSize(throwablesAmount);
        ((IItemAccessor) Items.POTION).setMaxStackSize(potionAmount);
    }

}
