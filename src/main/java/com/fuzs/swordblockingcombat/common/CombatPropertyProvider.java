package com.fuzs.swordblockingcombat.common;

import com.fuzs.materialmaster.api.provider.AbstractPropertyProvider;
import com.fuzs.swordblockingcombat.SwordBlockingCombat;
import com.fuzs.swordblockingcombat.config.ConfigBuildHandler;
import com.google.common.collect.Maps;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.Map;
import java.util.UUID;

public class CombatPropertyProvider extends AbstractPropertyProvider {

    @Override
    public boolean isEnabled() {

        return ConfigBuildHandler.SNOWBALL_STACKSIZE.get();
    }

    @Override
    public String getName() {

        return SwordBlockingCombat.MODID;
    }

    @Override
    public Map<Item, Double> getStackSize() {

        Map<Item, Double> stackSize = Maps.newHashMap();
        stackSize.put(Items.SNOWBALL, 64.0);

        return stackSize;
    }

    @Override
    protected UUID getMainhandModifierId() {

        return UUID.fromString("4909ECB1-A269-4F8F-983C-6592BCD8AF2A");
    }

    @Override
    protected UUID[] getArmorModifierIds() {

        return new UUID[]{UUID.fromString("F1A6B9CB-1149-4179-A3BD-A8A6BE8F6184"), UUID.fromString("C42250EC-6A99-4F20-BBFD-6D8E38C575C7"), UUID.fromString("B644B4AC-DC0D-4975-940C-8D648C2C0F3A"), UUID.fromString("B18AC50F-5F55-49EE-A2DF-92B647D1AD97")};
    }

}
