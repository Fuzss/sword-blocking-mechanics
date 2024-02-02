package fuzs.swordblockingmechanics.forge.core;

import fuzs.swordblockingmechanics.core.CommonAbstractions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.Nullable;

public class ForgeAbstractions implements CommonAbstractions {

    @Override
    public void onPlayerDestroyItem(Player player, ItemStack stack, @Nullable InteractionHand hand) {
        ForgeEventFactory.onPlayerDestroyItem(player, stack, hand);
    }
}
