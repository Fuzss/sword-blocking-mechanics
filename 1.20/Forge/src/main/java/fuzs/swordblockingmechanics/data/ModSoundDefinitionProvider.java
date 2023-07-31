package fuzs.swordblockingmechanics.data;

import fuzs.puzzleslib.api.data.v1.AbstractSoundDefinitionProvider;
import fuzs.swordblockingmechanics.init.ModRegistry;
import net.minecraftforge.data.event.GatherDataEvent;

public class ModSoundDefinitionProvider extends AbstractSoundDefinitionProvider {

    public ModSoundDefinitionProvider(GatherDataEvent evt, String modId) {
        super(evt, modId);
    }

    @Override
    public void registerSounds() {
        this.add(ModRegistry.ITEM_SWORD_BLOCK_SOUND_EVENT.get(), sound(this.id("item/sword/block1")).volume(0.8), sound(this.id("item/sword/block2")).volume(0.8));
    }
}
