package fuzs.swordblockingmechanics.attachment;

import fuzs.swordblockingmechanics.SwordBlockingMechanics;
import fuzs.swordblockingmechanics.config.ServerConfig;
import fuzs.swordblockingmechanics.handler.SwordBlockingHandler;
import fuzs.swordblockingmechanics.init.ModRegistry;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public record ParryCooldown(int cooldownTicks) {
    public static final ParryCooldown ZERO = new ParryCooldown(0);

    public double getCooldownProgress() {
        return Mth.clamp(
                1.0 - this.cooldownTicks / (double) SwordBlockingMechanics.CONFIG.get(ServerConfig.class).parryWindow,
                0.0,
                1.0);
    }

    public boolean isCooldownActive() {
        return this.getCooldownProgress() < 1.0;
    }

    public static void resetCooldownTicks(Player player) {
        ParryCooldown parryCooldown = ModRegistry.PARRY_COOLDOWN_ATTACHMENT_TYPE.getOrDefault(player, ZERO);
        if (parryCooldown.cooldownTicks <= 0) {
            int currentUseDuration = SwordBlockingHandler.DEFAULT_ITEM_USE_DURATION - player.getUseItemRemainingTicks();
            ParryCooldown newParryCooldown = new ParryCooldown(Math.min(currentUseDuration,
                    SwordBlockingMechanics.CONFIG.get(ServerConfig.class).parryWindow));
            ModRegistry.PARRY_COOLDOWN_ATTACHMENT_TYPE.set(player, newParryCooldown);
        }
    }

    public static void onEndPlayerTick(Player player) {
        ParryCooldown parryCooldown = ModRegistry.PARRY_COOLDOWN_ATTACHMENT_TYPE.getOrDefault(player, ZERO);
        if (parryCooldown.cooldownTicks > 0 && !SwordBlockingHandler.isActiveItemStackBlocking(player)) {
            ParryCooldown newParryCooldown = new ParryCooldown(parryCooldown.cooldownTicks - 2);
            ModRegistry.PARRY_COOLDOWN_ATTACHMENT_TYPE.set(player, newParryCooldown);
        }
    }
}
