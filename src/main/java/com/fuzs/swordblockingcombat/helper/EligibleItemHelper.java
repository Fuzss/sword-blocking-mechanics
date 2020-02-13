package com.fuzs.swordblockingcombat.helper;

import com.fuzs.swordblockingcombat.handler.ConfigBuildHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class EligibleItemHelper {

    private static Set<Item> exclude;
    private static Set<Item> include;

    public static boolean check(ItemStack stack) {

        Item item = stack.getItem();
        if (getExclusionSet().contains(item)) {
            return false;
        }

        if (item instanceof SwordItem) {
            return true;
        }

        return getInclusionSet().contains(item);
    }

    private static Set<Item> getExclusionSet() {

        if (exclude == null) {

            exclude = new HashSet<>();
            build(ConfigBuildHandler.GENERAL_CONFIG.exclude.get(), exclude);
        }

        return exclude;
    }

    private static Set<Item> getInclusionSet() {

        if (include == null) {

            include = new HashSet<>();
            build(ConfigBuildHandler.GENERAL_CONFIG.include.get(), include);
        }

        return include;
    }

    private static void build(List<String> locations, Set<Item> set) {

        for (String entry : locations) {

            String[] s = entry.split(":");
            Optional<ResourceLocation> locationOptional = Optional.empty();
            if (s.length == 1) {

                locationOptional = Optional.of(new ResourceLocation(s[0]));
            } else if (s.length == 2) {

                locationOptional = Optional.of(new ResourceLocation(s[0], s[1]));
            }

            locationOptional.flatMap(location -> Optional.ofNullable(ForgeRegistries.ITEMS.getValue(location))).ifPresent(set::add);
        }
    }

}
