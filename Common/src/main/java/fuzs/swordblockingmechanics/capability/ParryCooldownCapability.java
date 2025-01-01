package fuzs.swordblockingmechanics.capability;

import fuzs.puzzleslib.api.capability.v2.data.CapabilityComponent;

public interface ParryCooldownCapability extends CapabilityComponent {

    double getCooldownProgress();

    void resetCooldownTicks();

    void tick();

    default boolean isCooldownActive() {
        return this.getCooldownProgress() < 1.0;
    }
}
