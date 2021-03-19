package com.fuzs.swordblockingmechanics.element;

import com.fuzs.puzzleslib_sbm.PuzzlesLib;
import com.fuzs.puzzleslib_sbm.element.extension.ClientExtensibleElement;
import com.fuzs.swordblockingmechanics.SwordBlockingMechanics;
import com.fuzs.swordblockingmechanics.client.element.SwordBlockingExtension;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ObjectHolder;

public class SwordBlockingElement extends ClientExtensibleElement<SwordBlockingExtension> {

    @ObjectHolder(SwordBlockingMechanics.MODID + ":" + "item.sword.block")
    public static final SoundEvent ITEM_SWORD_BLOCK_SOUND = null;

    public static final Tags.IOptionalNamedTag<Item> EXCLUDED_SWORDS_TAG = ItemTags.createOptional(new ResourceLocation(SwordBlockingMechanics.MODID, "blocking/excluded_swords"));
    public static final Tags.IOptionalNamedTag<Item> INCLUDED_SWORDS_TAG = ItemTags.createOptional(new ResourceLocation(SwordBlockingMechanics.MODID, "blocking/included_swords"));
    public static final Tags.IOptionalNamedTag<Item> OFF_HAND_BLACKLIST_TAG = ItemTags.createOptional(new ResourceLocation(SwordBlockingMechanics.MODID, "blocking/off_hand_blacklist"));

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
    }

}
