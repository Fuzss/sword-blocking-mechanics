package fuzs.swordblockingmechanics.neoforge.data;

import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.neoforge.api.client.data.v2.AbstractSoundProvider;
import fuzs.swordblockingmechanics.init.ModRegistry;

public class ModSoundDefinitionProvider extends AbstractSoundProvider {

    public ModSoundDefinitionProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addSounds() {
        this.add(ModRegistry.ITEM_SWORD_BLOCK_SOUND_EVENT.value(),
                sound(this.id("item/sword/block1")).volume(0.8),
                sound(this.id("item/sword/block2")).volume(0.8));
    }
}
