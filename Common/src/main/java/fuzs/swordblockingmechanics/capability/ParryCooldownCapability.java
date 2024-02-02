package fuzs.swordblockingmechanics.capability;

import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.swordblockingmechanics.SwordBlockingMechanics;
import fuzs.swordblockingmechanics.config.ServerConfig;
import fuzs.swordblockingmechanics.handler.SwordBlockingHandler;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class ParryCooldownCapability extends CapabilityComponent<Player> {
    private int cooldownTicks;

    public double getCooldownProgress() {
        return Mth.clamp(1.0 - this.cooldownTicks / (double) SwordBlockingMechanics.CONFIG.get(ServerConfig.class).parryWindow, 0.0, 1.0);
    }

    public void resetCooldownTicks() {
        if (this.cooldownTicks == 0) {
            int currentUseDuration = SwordBlockingHandler.DEFAULT_ITEM_USE_DURATION - this.getHolder().getUseItemRemainingTicks();
            this.setCooldownTicks(Math.min(currentUseDuration, SwordBlockingMechanics.CONFIG.get(ServerConfig.class).parryWindow));
        }
    }

    public void tick() {
        if (this.cooldownTicks > 0 && !SwordBlockingHandler.isActiveItemStackBlocking(this.getHolder())) {
            this.setCooldownTicks(this.cooldownTicks - 2);
        }
    }

    private void setCooldownTicks(int cooldownTicks) {
        if (this.cooldownTicks != cooldownTicks) {
            this.cooldownTicks = cooldownTicks;
            this.setChanged();
        }
    }

    public boolean isCooldownActive() {
        return this.getCooldownProgress() < 1.0;
    }
}
