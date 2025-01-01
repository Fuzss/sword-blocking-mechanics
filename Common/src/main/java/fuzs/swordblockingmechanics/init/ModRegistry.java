package fuzs.swordblockingmechanics.init;

import fuzs.puzzleslib.api.attachment.v4.DataAttachmentRegistry;
import fuzs.puzzleslib.api.attachment.v4.DataAttachmentType;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.api.init.v3.tags.TagFactory;
import fuzs.swordblockingmechanics.SwordBlockingMechanics;
import fuzs.swordblockingmechanics.attachment.ParryCooldown;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;

public class ModRegistry {
    static final RegistryManager REGISTRIES = RegistryManager.from(SwordBlockingMechanics.MOD_ID);
    public static final Holder.Reference<SoundEvent> ITEM_SWORD_BLOCK_SOUND_EVENT = REGISTRIES.registerSoundEvent(
            "item.sword.block");

    static final TagFactory TAGS = TagFactory.make(SwordBlockingMechanics.MOD_ID);
    public static final TagKey<Item> CAN_PERFORM_SWORD_BLOCKING_ITEM_TAG = TAGS.registerItemTag(
            "can_perform_sword_blocking");
    public static final TagKey<Item> OVERRIDES_SWORD_IN_OFFHAND_BLOCKING_ITEM_TAG = TAGS.registerItemTag(
            "overrides_sword_blocking_in_offhand");

    public static final DataAttachmentType<Entity, ParryCooldown> PARRY_COOLDOWN_ATTACHMENT_TYPE = DataAttachmentRegistry.<ParryCooldown>entityBuilder()
            .build(SwordBlockingMechanics.id("parry_cooldown"));

    public static void bootstrap() {
        // NO-OP
    }
}
