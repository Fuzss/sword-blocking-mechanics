package com.fuzs.swordblockingcombat.helper;

import com.fuzs.swordblockingcombat.handler.ConfigBuildHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EligibleItemHelper {

    private static Set<ResourceLocation> exclude;
    private static Set<ResourceLocation> include;

    public static boolean isItemEligible(ItemStack stack) {

        ResourceLocation location = stack.getItem().getRegistryName();

        if (location == null) {
            return false;
        }

        if (contains(getExclusionSet(), location)) {
            return false;
        }

        if (stack.getItem() instanceof SwordItem) {
            return true;
        }

        return contains(getInclusionSet(), location);

    }

    private static boolean contains(Set<ResourceLocation> set, ResourceLocation location) {
        return set.stream().filter(it -> it.getNamespace().equals(location.getNamespace())).map(ResourceLocation::getPath)
                .collect(Collectors.toSet()).contains(location.getPath());
    }

    private static Set<ResourceLocation> getExclusionSet() {

        if (exclude == null) {
            exclude = new HashSet<>();
            build(ConfigBuildHandler.GENERAL_CONFIG.exclude.get(), exclude);
        }

        return exclude;

    }

    private static Set<ResourceLocation> getInclusionSet() {

        if (include == null) {
            include = new HashSet<>();
            build(ConfigBuildHandler.GENERAL_CONFIG.include.get(), include);
        }

        return include;

    }

    private static void build(List<String> list, Set<ResourceLocation> set) {

        for (String s : list) {
            String[] s1 = s.split(":");
            if (s1.length == 2) {
                ResourceLocation location = new ResourceLocation(s1[0], s1[1]);
                if (ForgeRegistries.ITEMS.containsKey(location)) {
                    set.add(location);
                }
            }
        }

    }

}
