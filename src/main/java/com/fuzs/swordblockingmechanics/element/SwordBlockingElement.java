package com.fuzs.swordblockingmechanics.element;

import com.fuzs.puzzleslib_sbm.PuzzlesLib;
import com.fuzs.puzzleslib_sbm.element.extension.ClientExtensibleElement;
import com.fuzs.swordblockingmechanics.SwordBlockingMechanics;
import com.fuzs.swordblockingmechanics.client.element.SwordBlockingExtension;
import com.fuzs.swordblockingmechanics.util.BlockingHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.registries.ObjectHolder;

public class SwordBlockingElement extends ClientExtensibleElement<SwordBlockingExtension> {

    @ObjectHolder(SwordBlockingMechanics.MODID + ":" + "item.sword.block")
    public static final SoundEvent ITEM_SWORD_BLOCK_SOUND = null;

    public static final Tags.IOptionalNamedTag<Item> EXCLUDED_SWORDS_TAG = ItemTags.createOptional(new ResourceLocation(SwordBlockingMechanics.MODID, "blocking/excluded_swords"));
    public static final Tags.IOptionalNamedTag<Item> INCLUDED_SWORDS_TAG = ItemTags.createOptional(new ResourceLocation(SwordBlockingMechanics.MODID, "blocking/included_swords"));
    public static final Tags.IOptionalNamedTag<Item> OFF_HAND_BLACKLIST_TAG = ItemTags.createOptional(new ResourceLocation(SwordBlockingMechanics.MODID, "blocking/off_hand_blacklist"));

    private boolean prioritizeOffHand;
    private float blockedDamage;
    private boolean damageSword;
    private boolean deflectProjectiles;
    public int parryWindow;
    public boolean requireBothHands;

    public SwordBlockingElement() {

        super(element -> new SwordBlockingExtension((SwordBlockingElement) element));
    }

    @Override
    public String[] getDescription() {

        return new String[]{"Re-adds sword blocking in a very configurable way."};
    }

    @Override
    public void setupCommon() {

        PuzzlesLib.getRegistryManager().register("item.sword.block", new SoundEvent(new ResourceLocation(SwordBlockingMechanics.MODID, "item.sword.block")));
        this.addListener(this::onRightClickItem);
        this.addListener(this::onItemUseStart);
        this.addListener(this::onLivingAttack);
        this.addListener(this::onLivingHurt);
    }

    @Override
    public void setupCommonConfig(ForgeConfigSpec.Builder builder) {

        addToConfig(builder.comment("Prioritize usable off-hand items over sword blocking.", "Items not recognized by default can be included using the \"" + SwordBlockingMechanics.MODID + ":blocking/off_hand_blacklist\" item tag.").define("Prioritize Off-Hand", true), v -> this.prioritizeOffHand = v);
        addToConfig(builder.comment("Percentage an incoming attack will be reduced by when blocking.").defineInRange("Blocked Damage", 0.5, 0.0, 1.0), v -> this.blockedDamage = v, Double::floatValue);
        addToConfig(builder.comment("Damage sword when blocking an attack depending on the amount of damage blocked. Sword is only damaged when at least three damage points have been blocked, just like a shield.").define("Damage Sword", false), v -> this.damageSword = v);
        addToConfig(builder.comment("Incoming projectiles such as arrows or tridents will ricochet while blocking.").define("Deflect Projectiles", false), v -> this.deflectProjectiles = v);
        addToConfig(builder.comment("Amount of ticks after starting to block in which an attack will be completely nullified like when blocking with a shield.").defineInRange("Parry Window", 10, 0, 72000), v -> this.parryWindow = v);
        addToConfig(builder.comment("Blocking requires both hands, meaning the hand not holding the sword must be empty.").define("Require Both Hands", false), v -> this.requireBothHands = v);
    }

    private void onRightClickItem(final PlayerInteractEvent.RightClickItem evt) {

        PlayerEntity player = evt.getPlayer();
        if (BlockingHelper.canItemStackBlock(evt.getItemStack())) {

            ItemStack stack = player.getHeldItemOffhand();
            if (this.prioritizeOffHand && evt.getHand() == Hand.MAIN_HAND && !stack.isEmpty()) {

                if (stack.getItem().isIn(SwordBlockingElement.OFF_HAND_BLACKLIST_TAG)) {

                    return;
                } else if (!BlockingHelper.canActivateBlocking(stack, player)) {

                    return;
                }
            }

            Hand oppositeHand = evt.getHand() == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
            if (!this.requireBothHands || player.getHeldItem(oppositeHand).isEmpty()) {

                evt.setCanceled(true);
                player.setActiveHand(evt.getHand());
                // cause reequip animation, but don't swing hand, not to be confused with ActionResultType#SUCCESS
                evt.setCancellationResult(ActionResultType.CONSUME);
            }
        }
    }

    private void onItemUseStart(final LivingEntityUseItemEvent.Start evt) {

        if (evt.getEntityLiving() instanceof PlayerEntity && BlockingHelper.canItemStackBlock(evt.getItem())) {

            // default use duration for items
            evt.setDuration(72000);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void onLivingAttack(final LivingAttackEvent evt) {

        if (evt.getEntityLiving().getEntityWorld().isRemote || !(evt.getEntityLiving() instanceof PlayerEntity)) {

            return;
        }

        PlayerEntity player = (PlayerEntity) evt.getEntityLiving();
        DamageSource damageSource = evt.getSource();
        if (BlockingHelper.isActiveItemStackBlocking(player)) {

            if (BlockingHelper.getBlockUseDuration(player) < this.parryWindow) {

                float damageAmount = evt.getAmount();
                if (damageAmount > 0.0F && BlockingHelper.canBlockDamageSource(player, damageSource)) {

                    evt.setCanceled(true);
                    BlockingHelper.dealDamageToSword(player, damageAmount, this.damageSword);
                    if (!damageSource.isProjectile()) {

                        if (damageSource.getImmediateSource() instanceof LivingEntity) {

                            LivingEntity entity = (LivingEntity) damageSource.getImmediateSource();
                            entity.applyKnockback(0.5F, player.getPosX() - entity.getPosX(), player.getPosZ() - entity.getPosZ());
                        }
                    }

                    // send shield block sound to client
                    player.world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), ITEM_SWORD_BLOCK_SOUND, player.getSoundCategory(), 1.0F, 0.8F + player.world.rand.nextFloat() * 0.4F);
                }
            }

            boolean isNotPiercing = damageSource.getImmediateSource() instanceof AbstractArrowEntity && ((AbstractArrowEntity) damageSource.getImmediateSource()).getPierceLevel() == 0;
            if (this.deflectProjectiles && isNotPiercing) {

                evt.setCanceled(true);
            }
        }
    }

    private void onLivingHurt(final LivingHurtEvent evt) {

        if (evt.getEntityLiving() instanceof PlayerEntity) {

            PlayerEntity player = (PlayerEntity) evt.getEntityLiving();
            float damageAmount = evt.getAmount();
            if (damageAmount > 0.0F && BlockingHelper.isActiveItemStackBlocking(player)) {

                BlockingHelper.dealDamageToSword(player, damageAmount, this.damageSword);
                if (!evt.getSource().isUnblockable()) {

                    float reducedDamage = 1.0F + evt.getAmount() * (1.0F - this.blockedDamage);
                    if (reducedDamage <= 1.0F) {

                        reducedDamage = 0.0F;
                    }

                    evt.setAmount(Math.min(evt.getAmount(), reducedDamage));
                }
            }
        }
    }

}
