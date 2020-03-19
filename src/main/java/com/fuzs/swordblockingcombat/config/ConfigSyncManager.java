package com.fuzs.swordblockingcombat.config;

import com.fuzs.materialmaster.api.PropertyProviderUtils;
import com.fuzs.materialmaster.api.builder.EntryCollectionBuilder;
import com.fuzs.swordblockingcombat.SwordBlockingCombat;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraftforge.fml.config.ModConfig;

import java.util.Map;
import java.util.Set;

public class ConfigSyncManager {

    private final EntryCollectionBuilder<Item> parser = PropertyProviderUtils.createItemBuilder();

    public static Set<Item> exclude = Sets.newHashSet();
    public static Set<Item> include = Sets.newHashSet();
    public static Map<Item, Double> itemDelay = Maps.newHashMap();

    public void onModConfig(final ModConfig.ModConfigEvent evt) {

        if (evt.getConfig().getModId().equals(SwordBlockingCombat.MODID)) {

            this.sync();
        }
    }

    private void sync() {

        exclude = this.parser.buildEntrySetWithCondition(ConfigBuildHandler.BLOCKING_EXCLUDE.get(),
                item -> item instanceof SwordItem, "No instance of SwordItem");
        include = this.parser.buildEntrySetWithCondition(ConfigBuildHandler.BLOCKING_INCLUDE.get(),
                item -> !(item instanceof SwordItem), "Instance of SwordItem");
        itemDelay = this.parser.buildEntryMapWithCondition(ConfigBuildHandler.ITEM_DELAY.get(),
                (item, value) -> value >= 0.0 && value <= 72000.0, "Delay out of bounds");
    }

}
