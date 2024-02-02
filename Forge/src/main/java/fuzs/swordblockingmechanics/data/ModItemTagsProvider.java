package fuzs.swordblockingmechanics.data;

import fuzs.puzzleslib.api.data.v1.AbstractTagProvider;
import fuzs.swordblockingmechanics.init.ModRegistry;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

public class ModItemTagsProvider extends AbstractTagProvider.Items {

    public ModItemTagsProvider(GatherDataEvent evt, String modId) {
        super(evt, modId);
    }

    @Override
    protected void addTags() {
//        this.tag(ModRegistry.CAN_PERFORM_SWORD_BLOCKING_ITEM_TAG).addTag(ItemTags.SWORDS);
        this.tag(ModRegistry.OVERRIDES_SWORD_IN_OFFHAND_BLOCKING_ITEM_TAG);
    }
}
