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

            int ticker = ConfigBuildHandler.FoodTicker.valueOf(ConfigBuildHandler.GENERAL_CONFIG.foodTicker.get().toString()).ordinal();
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

            boolean naturalRegen = player.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION);
            if (ticker == 1) {
                this.tickClassic(player, difficulty, naturalRegen);
            } else if (ticker == 2) {
                this.tickCombat(player, difficulty, naturalRegen);
            }
        }

        private void tickClassic(PlayerEntity player, Difficulty difficulty, boolean naturalRegen) {

            if (naturalRegen && this.foodLevel >= 18 && player.shouldHeal()) {
                ++this.foodTimer;
                if (this.foodTimer >= 80) {
                    player.heal(1.0F);
                    this.addExhaustion(3.0F);
                    this.foodTimer = 0;
                }
            } else if (this.foodLevel <= 0) {
                ++this.foodTimer;
                if (this.foodTimer >= 80) {
                    if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
                        player.attackEntityFrom(DamageSource.STARVE, 1.0F);
                    }

                    this.foodTimer = 0;
                }
            } else {
                this.foodTimer = 0;
            }
        }

        private void tickCombat(PlayerEntity player, Difficulty difficulty, boolean naturalRegen) {

            if (naturalRegen && this.foodLevel >= 6 && player.shouldHeal()) {
                ++this.foodTimer;
                if (this.foodTimer >= 60) {
                    player.heal(1.0F);
                    this.foodLevel = Math.max(this.foodLevel - 1, 0);
                    this.foodTimer = 0;
                }
            } else if (this.foodLevel <= 0) {
                ++this.foodTimer;
                if (this.foodTimer >= 60) {
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
