package fuzs.swordblockingmechanics.init;

import fuzs.puzzleslib.api.capability.v3.CapabilityController;
import fuzs.puzzleslib.api.capability.v3.data.EntityCapabilityKey;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.api.init.v3.tags.BoundTagFactory;
import fuzs.swordblockingmechanics.SwordBlockingMechanics;
import fuzs.swordblockingmechanics.capability.ParryCooldownCapability;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public class ModRegistry {
    static final RegistryManager REGISTRY = RegistryManager.from(SwordBlockingMechanics.MOD_ID);
    public static final Holder.Reference<SoundEvent> ITEM_SWORD_BLOCK_SOUND_EVENT = REGISTRY.registerSoundEvent(
            "item.sword.block");

    static final BoundTagFactory TAGS = BoundTagFactory.make(SwordBlockingMechanics.MOD_ID);
    public static final TagKey<Item> CAN_PERFORM_SWORD_BLOCKING_ITEM_TAG = TAGS.registerItemTag(
            "can_perform_sword_blocking");
    public static final TagKey<Item> OVERRIDES_SWORD_IN_OFFHAND_BLOCKING_ITEM_TAG = TAGS.registerItemTag(
            "overrides_sword_blocking_in_offhand");

    static final CapabilityController CAPABILITIES = CapabilityController.from(SwordBlockingMechanics.MOD_ID);
    public static final EntityCapabilityKey<Player, ParryCooldownCapability> PARRY_COOLDOWN_CAPABILITY = CAPABILITIES.registerEntityCapability(
            "parry_cooldown",
            ParryCooldownCapability.class,
            ParryCooldownCapability::new,
            Player.class
    );

    public static void touch() {

    }
}
