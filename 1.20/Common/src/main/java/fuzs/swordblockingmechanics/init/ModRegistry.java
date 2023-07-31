package fuzs.swordblockingmechanics.init;

import fuzs.puzzleslib.api.init.v2.RegistryManager;
import fuzs.puzzleslib.api.init.v2.RegistryReference;
import fuzs.swordblockingmechanics.SwordBlockingMechanics;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModRegistry {
    static final RegistryManager REGISTRY = RegistryManager.instant(SwordBlockingMechanics.MOD_ID);
    public static final RegistryReference<SoundEvent> ITEM_SWORD_BLOCK_SOUND_EVENT = REGISTRY.registerSoundEvent("item.sword.block");

    public static final TagKey<Item> CAN_PERFORM_SWORD_BLOCKING_ITEM_TAG = REGISTRY.registerItemTag("can_perform_sword_blocking");
    public static final TagKey<Item> OVERRIDES_SWORD_IN_OFFHAND_BLOCKING_ITEM_TAG = REGISTRY.registerItemTag("overrides_sword_blocking_in_offhand");

    public static void touch() {

    }
}
