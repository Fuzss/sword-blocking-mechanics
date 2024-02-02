package fuzs.swordblockingmechanics.neoforge.core;

import fuzs.swordblockingmechanics.core.CommonAbstractions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;

public class NeoForgeAbstractions implements CommonAbstractions {

    @Override
    public void onPlayerDestroyItem(Player player, ItemStack stack, @Nullable InteractionHand hand) {
        EventHooks.onPlayerDestroyItem(player, stack, hand);
    }
}
