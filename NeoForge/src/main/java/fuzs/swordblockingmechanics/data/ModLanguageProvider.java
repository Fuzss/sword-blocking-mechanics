package fuzs.swordblockingmechanics.data;

import fuzs.puzzleslib.api.data.v1.AbstractLanguageProvider;
import fuzs.swordblockingmechanics.init.ModRegistry;
import net.minecraftforge.data.event.GatherDataEvent;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(GatherDataEvent evt, String modId) {
        super(evt, modId);
    }

    @Override
    protected void addTranslations() {
        this.add(ModRegistry.ITEM_SWORD_BLOCK_SOUND_EVENT.get(), "Sword blocks");
    }
}
