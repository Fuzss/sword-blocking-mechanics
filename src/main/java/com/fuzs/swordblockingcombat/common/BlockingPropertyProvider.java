package com.fuzs.swordblockingcombat.common;

import com.fuzs.materialmaster.api.SyncProvider;
import com.fuzs.materialmaster.api.builder.AttributeMapBuilder;
import com.fuzs.materialmaster.api.provider.AbstractPropertyProvider;
import com.fuzs.swordblockingcombat.SwordBlockingCombat;
import com.fuzs.swordblockingcombat.config.ConfigBuildHandler;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.Map;
import java.util.UUID;

@SyncProvider
public class BlockingPropertyProvider extends AbstractPropertyProvider {

    @Override
    public boolean isEnabled() {

        return ConfigBuildHandler.OLD_DAMAGE_VALUES.get();
    }

    @Override
    public String getName() {

        return SwordBlockingCombat.MODID;
    }

    @Override
    public Map<Item, Multimap<String, AttributeModifier>> getAttributes() {

        return AttributeMapBuilder.create(this.getMainhandModifierId(), this.getArmorModifierIds())
                .putAttackDamage(Items.DIAMOND_SWORD, 1.0)
                .putAttackDamage(Items.DIAMOND_AXE, -2.0)
                .putAttackDamage(Items.DIAMOND_PICKAXE, 1.0)
                .putAttackDamage(Items.DIAMOND_SHOVEL, -0.5)
                .putAttackDamage(Items.DIAMOND_HOE, 3.0)
                .putAttackDamage(Items.IRON_SWORD, 1.0)
                .putAttackDamage(Items.IRON_AXE, -3.0)
                .putAttackDamage(Items.IRON_PICKAXE, 1.0)
                .putAttackDamage(Items.IRON_SHOVEL, -0.5)
                .putAttackDamage(Items.IRON_HOE, 2.0)
                .putAttackDamage(Items.STONE_SWORD, 1.0)
                .putAttackDamage(Items.STONE_AXE, -4.0)
                .putAttackDamage(Items.STONE_PICKAXE, 1.0)
                .putAttackDamage(Items.STONE_SHOVEL, -0.5)
                .putAttackDamage(Items.STONE_HOE, 1.0)
                .putAttackDamage(Items.WOODEN_SWORD, 1.0)
                .putAttackDamage(Items.WOODEN_AXE, -3.0)
                .putAttackDamage(Items.WOODEN_PICKAXE, 1.0)
                .putAttackDamage(Items.WOODEN_SHOVEL, -0.5)
                .putAttackDamage(Items.GOLDEN_SWORD, 1.0)
                .putAttackDamage(Items.GOLDEN_AXE, -3.0)
                .putAttackDamage(Items.GOLDEN_PICKAXE, 1.0)
                .putAttackDamage(Items.GOLDEN_SHOVEL, -0.5)
                .build();
    }

    @Override
    protected UUID getMainhandModifierId() {

        return UUID.fromString("45EDD364-66C9-4D8C-82F3-3CCA76012182");
    }

    @Override
    protected UUID[] getArmorModifierIds() {

        return new UUID[]{UUID.fromString("1ED2D9A5-3DE4-400B-A6D1-1A7F465459FD"), UUID.fromString("728D339B-30FA-48E9-ACD5-E503310CA40F"), UUID.fromString("D907C011-BA14-4253-BBA7-058F108526D1"), UUID.fromString("CFEB08B6-EBC2-406B-8002-8E65A121FD2E")};
    }

}
