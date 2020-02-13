package com.fuzs.swordblockingcombat.handler;

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
        if (ConfigBuildHandler.GENERAL_CONFIG.foodTicker.get() != ConfigBuildHandler.FoodTicker.DEFAULT && evt.getEntity() instanceof PlayerEntity) {
            ((PlayerEntity) evt.getEntity()).foodStats = new CombatFoodStats(((PlayerEntity) evt.getEntity()).foodStats);
        }
    }

    private static class CombatFoodStats extends FoodStats {

        private CombatFoodStats(FoodStats oldStats) {

            this.foodLevel = oldStats.foodLevel;
            this.foodSaturationLevel = oldStats.foodSaturationLevel;
            this.foodExhaustionLevel = oldStats.foodExhaustionLevel;
            this.foodTimer = oldStats.foodTimer;
            this.prevFoodLevel = oldStats.prevFoodLevel;
        }

        @Override
        public void tick(PlayerEntity player) {

            Difficulty difficulty = player.world.getDifficulty();
            this.prevFoodLevel = this.foodLevel;
            if (this.foodExhaustionLevel > 4.0F) {
                this.foodExhaustionLevel -= 4.0F;
                if (this.foodSaturationLevel > 0.0F) {
                    this.foodSaturationLevel = Math.max(this.foodSaturationLevel - 1.0F, 0.0F);
                } else if (difficulty != Difficulty.PEACEFUL) {
                    this.foodLevel = Math.max(this.foodLevel - 1, 0);
                }
            }

            boolean combat = ConfigBuildHandler.GENERAL_CONFIG.foodTicker.get() == ConfigBuildHandler.FoodTicker.COMBAT;
            int delay = combat ? 60 : 80, threshold = combat ? 6 : 18;
            boolean naturalRegen = player.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION);
            if (naturalRegen && this.foodLevel >= threshold && player.shouldHeal()) {
                ++this.foodTimer;
                if (this.foodTimer >= delay) {
                    player.heal(1.0F);
                    if (combat) {
                        this.foodLevel = Math.max(this.foodLevel - 1, 0);
                    } else {
                        this.addExhaustion(6.0F); // was 3.0F originally
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
