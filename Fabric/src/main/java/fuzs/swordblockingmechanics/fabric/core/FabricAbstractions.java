package fuzs.swordblockingmechanics.fabric.core;

import fuzs.swordblockingmechanics.core.CommonAbstractions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class FabricAbstractions implements CommonAbstractions {

    @Override
    public void onPlayerDestroyItem(Player player, ItemStack stack, @Nullable InteractionHand hand) {
        // NO-OP
    }
}
