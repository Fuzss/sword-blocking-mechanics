package com.fuzs.swordblockingcombat.helper;

import com.fuzs.swordblockingcombat.handler.ConfigBuildHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EligibleItemHelper {

    private ItemStack activeItemStackMain = ItemStack.EMPTY;
    private ItemStack activeItemStackOff = ItemStack.EMPTY;
    private boolean cachedResultMain;
    private boolean cachedResultOff;

    private Set<ResourceLocation> exclude;
    private Set<ResourceLocation> include;

    public boolean test(ItemStack stack, Hand hand) {

        if (hand == null) {
            return this.isEligible(stack);
        }

        ResourceLocation location = stack.getItem().getRegistryName();
        ItemStack cache = hand == Hand.MAIN_HAND ? this.activeItemStackMain : this.activeItemStackOff;
        if (location != null && !location.equals(cache.getItem().getRegistryName())) {

            if (hand == Hand.MAIN_HAND) {

                this.activeItemStackMain = stack;
                this.cachedResultMain = this.isEligible(stack);
            } else {

                this.activeItemStackOff = stack;
                this.cachedResultOff = this.isEligible(stack);
            }
            //System.out.println("Re-created: " + location + ", " + hand + ", " + cache.getItem().getRegistryName());
        }

        //System.out.println("Forwarded: " + location);
        return hand == Hand.MAIN_HAND ? this.cachedResultMain : this.cachedResultOff;
    }

    private boolean isEligible(ItemStack stack) {

        ResourceLocation location = stack.getItem().getRegistryName();

        if (location == null) {
            return false;
        }

        if (this.contains(this.getExclusionSet(), location)) {
            return false;
        }

        if (stack.getItem() instanceof SwordItem) {
            return true;
        }

        return this.contains(this.getInclusionSet(), location);

    }

    private boolean contains(Set<ResourceLocation> set, ResourceLocation location) {

        return set.stream().filter(it -> it.getNamespace().equals(location.getNamespace())).map(ResourceLocation::getPath)
                .anyMatch(it -> it.equals(location.getPath()));
    }

    private Set<ResourceLocation> getExclusionSet() {

        if (this.exclude == null) {

            this.exclude = new HashSet<>();
            this.build(ConfigBuildHandler.GENERAL_CONFIG.exclude.get(), this.exclude);
        }

        return this.exclude;

    }

    private Set<ResourceLocation> getInclusionSet() {

        if (this.include == null) {

            this.include = new HashSet<>();
            this.build(ConfigBuildHandler.GENERAL_CONFIG.include.get(), this.include);
        }

        return this.include;

    }

    private void build(List<String> list, Set<ResourceLocation> set) {

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
