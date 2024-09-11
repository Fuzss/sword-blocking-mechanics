package fuzs.swordblockingmechanics.core;

import fuzs.puzzleslib.api.core.v1.ServiceProviderHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface CommonAbstractions {
    CommonAbstractions INSTANCE = ServiceProviderHelper.load(CommonAbstractions.class);

    void onPlayerDestroyItem(Player player, ItemStack stack, @Nullable InteractionHand hand);
}
