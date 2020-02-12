package com.fuzs.swordblockingcombat.helper;

import com.fuzs.swordblockingcombat.handler.ConfigBuildHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class EligibleItemHelper {

    private Item activeItemMain;
    private Item activeItemOff;
    private boolean cachedResultMain;
    private boolean cachedResultOff;

    private Set<Item> exclude;
    private Set<Item> include;

    public boolean test(ItemStack stack, Hand hand) {

        Item item = stack.getItem();
        if (hand == null) {
            return this.isEligible(item);
        }

        Item cache = hand == Hand.MAIN_HAND ? this.activeItemMain : this.activeItemOff;
        if (!item.equals(cache)) {

            if (hand == Hand.MAIN_HAND) {

                this.activeItemMain = item;
                this.cachedResultMain = this.isEligible(item);
            } else {

                this.activeItemOff = item;
                this.cachedResultOff = this.isEligible(item);
            }
        }

        return hand == Hand.MAIN_HAND ? this.cachedResultMain : this.cachedResultOff;
    }

    private boolean isEligible(Item item) {

        if (this.getExclusionSet().contains(item)) {
            return false;
        }

        if (item instanceof SwordItem) {
            return true;
        }

        return this.getInclusionSet().contains(item);
    }

    private Set<Item> getExclusionSet() {

        if (this.exclude == null) {

            this.exclude = new HashSet<>();
            this.build(ConfigBuildHandler.GENERAL_CONFIG.exclude.get(), this.exclude);
        }

        return this.exclude;
    }

    private Set<Item> getInclusionSet() {

        if (this.include == null) {

            this.include = new HashSet<>();
            this.build(ConfigBuildHandler.GENERAL_CONFIG.include.get(), this.include);
        }

        return this.include;
    }

    private void build(List<String> list, Set<Item> set) {

        for (String s : list) {

            String[] s1 = s.split(":");
            Optional<ResourceLocation> locationOptional = Optional.empty();
            if (s1.length == 1) {

                locationOptional = Optional.of(new ResourceLocation(s1[0]));
            } else if (s1.length == 2) {

                locationOptional = Optional.of(new ResourceLocation(s1[0], s1[1]));
            }

            locationOptional.flatMap(location -> Optional.ofNullable(ForgeRegistries.ITEMS.getValue(location))).ifPresent(set::add);
        }
    }

}
