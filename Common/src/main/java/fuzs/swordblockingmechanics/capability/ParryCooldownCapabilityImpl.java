package fuzs.swordblockingmechanics.capability;

import fuzs.swordblockingmechanics.SwordBlockingMechanics;
import fuzs.swordblockingmechanics.config.ServerConfig;
import fuzs.swordblockingmechanics.handler.SwordBlockingHandler;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class ParryCooldownCapabilityImpl implements ParryCooldownCapability {
    private final Player player;
    private int cooldownTicks;

    public ParryCooldownCapabilityImpl(Player player) {
        this.player = player;
    }

    @Override
    public double getCooldownProgress() {
        return Mth.clamp(1.0 - this.cooldownTicks / (double) SwordBlockingMechanics.CONFIG.get(ServerConfig.class).parryWindow, 0.0, 1.0);
    }

    @Override
    public void resetCooldownTicks() {
        if (this.cooldownTicks <= 0) {
            int currentUseDuration = SwordBlockingHandler.DEFAULT_ITEM_USE_DURATION - this.player.getUseItemRemainingTicks();
            this.cooldownTicks = Math.min(currentUseDuration, SwordBlockingMechanics.CONFIG.get(ServerConfig.class).parryWindow);
        }
    }

    @Override
    public void tick() {
        if (this.cooldownTicks > 0 && !SwordBlockingHandler.isActiveItemStackBlocking(this.player)) {
            this.cooldownTicks -= 2;
        }
    }
}
