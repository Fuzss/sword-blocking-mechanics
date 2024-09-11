package fuzs.swordblockingmechanics.neoforge.data;

import fuzs.puzzleslib.neoforge.api.data.v2.client.AbstractSoundDefinitionProvider;
import fuzs.puzzleslib.neoforge.api.data.v2.core.ForgeDataProviderContext;
import fuzs.swordblockingmechanics.init.ModRegistry;

public class ModSoundDefinitionProvider extends AbstractSoundDefinitionProvider {

    public ModSoundDefinitionProvider(ForgeDataProviderContext context) {
        super(context);
    }

    @Override
    public void addSoundDefinitions() {
        this.add(ModRegistry.ITEM_SWORD_BLOCK_SOUND_EVENT.value(),
                sound(this.id("item/sword/block1")).volume(0.8),
                sound(this.id("item/sword/block2")).volume(0.8)
        );
    }
}
