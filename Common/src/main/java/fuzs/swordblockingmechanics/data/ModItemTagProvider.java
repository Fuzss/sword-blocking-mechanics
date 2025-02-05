package fuzs.swordblockingmechanics.data;

import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.data.v2.tags.AbstractTagProvider;
import fuzs.swordblockingmechanics.init.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;

public class ModItemTagProvider extends AbstractTagProvider<Item> {

    public ModItemTagProvider(DataProviderContext context) {
        super(Registries.ITEM, context);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        this.tag(ModRegistry.CAN_PERFORM_SWORD_BLOCKING_ITEM_TAG).addTag(ItemTags.SWORDS);
        this.tag(ModRegistry.OVERRIDES_SWORD_IN_OFFHAND_BLOCKING_ITEM_TAG);
    }
}
