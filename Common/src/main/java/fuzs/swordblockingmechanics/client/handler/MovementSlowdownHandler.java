package fuzs.swordblockingmechanics.client.handler;

import fuzs.swordblockingmechanics.SwordBlockingMechanics;
import fuzs.swordblockingmechanics.config.ServerConfig;
import fuzs.swordblockingmechanics.handler.SwordBlockingHandler;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;

public class MovementSlowdownHandler {

    public static void onMovementInputUpdate(LocalPlayer player, Input input) {
        double blockingSlowdown = SwordBlockingMechanics.CONFIG.get(ServerConfig.class).blockingSlowdown;
        if (blockingSlowdown == 0.2) return;
        if (SwordBlockingHandler.isActiveItemStackBlocking(player) && !player.isPassenger()) {
            input.forwardImpulse /= 0.2F;
            input.leftImpulse /= 0.2F;
            input.forwardImpulse *= (float) blockingSlowdown;
            input.leftImpulse *= (float) blockingSlowdown;
        }
    }
}
