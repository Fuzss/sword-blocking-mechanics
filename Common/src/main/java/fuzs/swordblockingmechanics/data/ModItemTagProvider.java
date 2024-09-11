package fuzs.swordblockingmechanics.data;

import fuzs.puzzleslib.api.data.v2.AbstractTagProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.swordblockingmechanics.init.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;

public class ModItemTagProvider extends AbstractTagProvider.Items {

    public ModItemTagProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        this.tag(ModRegistry.CAN_PERFORM_SWORD_BLOCKING_ITEM_TAG).addTag(ItemTags.SWORDS);
        this.tag(ModRegistry.OVERRIDES_SWORD_IN_OFFHAND_BLOCKING_ITEM_TAG);
    }
}
