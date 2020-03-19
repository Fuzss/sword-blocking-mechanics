package com.fuzs.swordblockingcombat.common.handler;

import com.fuzs.swordblockingcombat.config.ConfigBuildHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CombatFoodHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onEntityJoinWorld(final EntityJoinWorldEvent evt) {

        // replace food stats with one having some tweaks in the tick method
        if (ConfigBuildHandler.FOOD_TICKER.get().getId() != 0 && evt.getEntity() instanceof PlayerEntity) {
            ((PlayerEntity) evt.getEntity()).foodStats = new CombatFoodStats(((PlayerEntity) evt.getEntity()).foodStats);
        }
    }

    private static class CombatFoodStats extends FoodStats {

        private CombatFoodStats(FoodStats oldStats) {

            this.foodLevel = oldStats.foodLevel;
            this.foodSaturationLevel = oldStats.foodSaturationLevel;
            this.foodExhaustionLevel = oldStats.foodExhaustionLevel;
            this.foodTimer = oldStats.foodTimer;
        }

        @Override
        public void tick(PlayerEntity player) {

            Difficulty difficulty = player.world.getDifficulty();
            if (this.foodExhaustionLevel > 4.0F) {
                this.foodExhaustionLevel -= 4.0F;
                if (this.foodSaturationLevel > 0.0F) {
                    this.foodSaturationLevel = Math.max(this.foodSaturationLevel - 1.0F, 0.0F);
                } else if (difficulty != Difficulty.PEACEFUL) {
                    this.foodLevel = Math.max(this.foodLevel - 1, 0);
                }
            }

            int id = ConfigBuildHandler.FOOD_TICKER.get().getId();
            int delay = id == 2 ? 60 : id == 3 ? ConfigBuildHandler.REGEN_DELAY.get() : 80;
            int threshold = id == 2 ? 6 : id == 3 ? ConfigBuildHandler.REGEN_THRESHOLD.get() : 18;
            boolean naturalRegen = player.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION);
            if (naturalRegen && this.foodLevel >= threshold && player.shouldHeal()) {
                ++this.foodTimer;
                if (this.foodTimer >= delay) {
                    player.heal(1.0F);
                    if (id == 2 || id == 3 && ConfigBuildHandler.DRAIN_FOOD.get()) {
                        this.foodLevel = Math.max(this.foodLevel - 1, 0);
                    } else {
                        this.addExhaustion(3.0F); // is 6.0F in current vanilla
                    }
                    this.foodTimer = 0;
                }
            } else if (this.foodLevel <= 0) {
                ++this.foodTimer;
                if (this.foodTimer >= delay) {
                    if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
                        player.attackEntityFrom(DamageSource.STARVE, 1.0F);
                    }

                    this.foodTimer = 0;
                }
            } else {
                this.foodTimer = 0;
            }
        }

    }

}
