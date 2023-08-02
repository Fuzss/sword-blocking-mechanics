package fuzs.swordblockingmechanics.init;

import fuzs.puzzleslib.api.capability.v2.CapabilityController;
import fuzs.puzzleslib.api.capability.v2.data.CapabilityKey;
import fuzs.puzzleslib.api.capability.v2.data.PlayerRespawnCopyStrategy;
import fuzs.puzzleslib.api.init.v2.RegistryManager;
import fuzs.puzzleslib.api.init.v2.RegistryReference;
import fuzs.swordblockingmechanics.SwordBlockingMechanics;
import fuzs.swordblockingmechanics.capability.ParryCooldownCapability;
import fuzs.swordblockingmechanics.capability.ParryCooldownCapabilityImpl;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModRegistry {
    static final RegistryManager REGISTRY = RegistryManager.instant(SwordBlockingMechanics.MOD_ID);
    public static final RegistryReference<SoundEvent> ITEM_SWORD_BLOCK_SOUND_EVENT = REGISTRY.registerSoundEvent("item.sword.block");

    public static final TagKey<Item> CAN_PERFORM_SWORD_BLOCKING_ITEM_TAG = REGISTRY.registerItemTag("can_perform_sword_blocking");
    public static final TagKey<Item> OVERRIDES_SWORD_IN_OFFHAND_BLOCKING_ITEM_TAG = REGISTRY.registerItemTag("overrides_sword_blocking_in_offhand");

    static final CapabilityController CAPABILITIES = CapabilityController.from(SwordBlockingMechanics.MOD_ID);
    public static final CapabilityKey<ParryCooldownCapability> PARRY_COOLDOWN_CAPABILITY = CAPABILITIES.registerPlayerCapability("parry_cooldown", ParryCooldownCapability.class, ParryCooldownCapabilityImpl::new, PlayerRespawnCopyStrategy.NEVER);

    public static void touch() {

    }
}
