package com.fuzs.swordblockingcombat.asm;

import com.fuzs.swordblockingcombat.client.GrassSwingHandler;
import com.fuzs.swordblockingcombat.common.ClassicCombatHandler;
import com.fuzs.swordblockingcombat.common.ModernCombatHandler;
import com.fuzs.swordblockingcombat.config.ConfigValueHolder;
import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * this class is mostly used for redirecting hooks to their appropriate place
 */
@SuppressWarnings("unused")
public class Hooks {

    /**
     * make attacking an entity in {@link net.minecraft.item.ToolItem} only consume one durability point
     */
    public static int hitEntityAmount(ToolItem toolItem) {

        return ConfigValueHolder.MODERN_COMBAT.noAttackPenalty && toolItem instanceof AxeItem ? 1 : 2;
    }

    /**
     * disable sweeping attack in by default in {@link net.minecraft.entity.player.PlayerEntity#attackTargetEntityWithCurrentItem}
     * might be executed if sweeping edge enchantment is present; sweeping particles are removed though
     */
    public static void doSweeping(boolean flag, PlayerEntity player, Entity targetEntity, float damage) {

        ClassicCombatHandler.doSweeping(flag, player, targetEntity, damage);
    }

    /**
     * set the player to be sprinting again if it has been disabled before in {@link net.minecraft.entity.player.PlayerEntity#attackTargetEntityWithCurrentItem}
     */
    public static void restoreSprinting(PlayerEntity player, int knockback) {

        if (ConfigValueHolder.CLASSIC_COMBAT.attackingAllowsSprinting) {

            ClassicCombatHandler.restoreSprinting(player, knockback);
        }
    }

    /**
     * add custom attributes to an item when the attribute mmap is generated in {@link net.minecraft.item.Item#getAttributeModifiers}
     * attributes already present will be modified to ensure the item tooltip displaying values properly
     */
    public static Multimap<String, AttributeModifier> adjustAttributeMap(Multimap<String, AttributeModifier> multimap, EquipmentSlotType equipmentSlot, ItemStack stack) {

        Map<Item, Map<String, AttributeModifier>> itemMap = ConfigValueHolder.MATERIAL_CHANGER.attributes;
        if (itemMap != null && equipmentSlot == EquipmentSlotType.MAINHAND) {

            Map<String, AttributeModifier> attributeMap = itemMap.get(stack.getItem());
            if (attributeMap != null) {

                attributeMap.forEach((key, value) -> {

                    List<AttributeModifier> matchingAttributes = multimap.entries().stream().filter(set -> set.getKey().equals(key)).map(Map.Entry::getValue).collect(Collectors.toList());
                    Optional<AttributeModifier> matchingID = matchingAttributes.stream().filter(attributeModifier -> attributeModifier.getID().equals(value.getID())).findFirst();
                    if (matchingID.isPresent()) {

                        matchingID.get().amount += value.getAmount();
                    } else {

                        multimap.put(key, value);
                    }
                });
            }
        }

        return multimap;
    }

    /**
     * modify damage dealt to an entity in {@link net.minecraft.entity.player.PlayerEntity#attackTargetEntityWithCurrentItem}
     */
    public static float addEnchantmentDamage(float damage, PlayerEntity player, Entity targetEntity) {

        if (ConfigValueHolder.CLASSIC_COMBAT.boostSharpness) {

            damage += ClassicCombatHandler.addEnchantmentDamage(player);
        }

        if (ConfigValueHolder.MODERN_COMBAT.boostImpaling) {

            damage += ModernCombatHandler.addEnchantmentDamage(player, targetEntity);
        }

        return damage;
    }

    /**
     * overwrite returned use duration for food items in {@link net.minecraft.item.Item#getUseDuration}
     */
    @SuppressWarnings("ConstantConditions")
    public static int getFoodDuration(Item item) {

        int speed = ConfigValueHolder.FOOD_BUFFS.eatingSpeed;
        return item.getFood().isFastEating() ? speed / 2 : speed;
    }

    /**
     * overwrite minimum food level required for sprinting to start in {@link net.minecraft.client.entity.player.ClientPlayerEntity#livingTick}
     */
    @OnlyIn(Dist.CLIENT)
    public static float getSprintingLevel(float level) {

        return ConfigValueHolder.FOOD_BUFFS.sprintingLevel != 3.0F ? ConfigValueHolder.FOOD_BUFFS.sprintingLevel : level;
    }

    /**
     * run block ray tracing a second time to only check for blocks with a collision shape in {@link net.minecraft.client.renderer.GameRenderer#getMouseOver}
     * the result is then used to ray trace for an entity
     */
    @OnlyIn(Dist.CLIENT)
    public static double rayTraceCollidingBlocks(float partialTicks, Entity entity, double d0, double d1) {

        return ConfigValueHolder.MODERN_COMBAT.swingThroughGrass ? GrassSwingHandler.rayTraceCollidingBlocks(partialTicks, entity, d0) : d1;
    }

    /**
     * save Minecraft#objectMouseOver and Minecraft#pointedEntity for a couple of ticks if they'd be empty otherwise
     * hook is applied at the end of {@link net.minecraft.client.renderer.GameRenderer#getMouseOver}
     */
    @OnlyIn(Dist.CLIENT)
    public static void applyCoyoteTime() {

        if (ConfigValueHolder.MODERN_COMBAT.coyoteTimer > 0) {

            GrassSwingHandler.applyCoyoteTime();
        }
    }

    /**
     * change arm swing animation to better emphasize the rhythm of attacks in {@link net.minecraft.entity.LivingEntity#getSwingProgress}
     */
    @OnlyIn(Dist.CLIENT)
    public static float getSwingProgress(float swingProgress, LivingEntity entity, float partialTickTime) {

        return ConfigValueHolder.MODERN_COMBAT.swingAnimation ? GrassSwingHandler.getSwingProgress(swingProgress, entity, partialTickTime) : swingProgress;
    }

    /**
     * adjust entity ray tracing predicate to only match entities that are alive in {@link net.minecraft.client.renderer.GameRenderer#getMouseOver}
     */
    @OnlyIn(Dist.CLIENT)
    public static Predicate<Entity> getEntityRayTraceFilter(Predicate<Entity> predicate) {

        return ConfigValueHolder.MODERN_COMBAT.attackAlive ? entity -> predicate.test(entity) && entity.isAlive() : predicate;
    }

}
