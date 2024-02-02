package fuzs.swordblockingmechanics.capability;

import fuzs.puzzleslib.api.capability.v2.data.CapabilityComponent;
import net.minecraft.world.entity.player.Player;

public interface ParryCooldownCapability extends CapabilityComponent {

    double getCooldownProgress();

    void setCooldownTicks();

    void tick(Player player);

    default boolean isCooldownActive() {
        return this.getCooldownProgress() < 1.0;
    }
}
