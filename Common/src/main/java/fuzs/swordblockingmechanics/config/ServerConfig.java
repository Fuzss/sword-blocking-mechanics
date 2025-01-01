package fuzs.swordblockingmechanics.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import fuzs.swordblockingmechanics.handler.SwordBlockingHandler;

public class ServerConfig implements ConfigCore {
    static final String BLOCKING_CATEGORY = "blocking";
    static final String PARRYING_CATEGORY = "parrying";

    @Config(description = "Allow blocking with swords, which will reduce most incoming attacks by 50% and render a parry animation.")
    public boolean allowBlockingAndParrying = true;
    @Config(description = "Prioritize usable off-hand items over sword blocking from the main hand. Items not recognized by default can be included in a dedicated item tag.")
    public boolean prioritizeOffHand = true;
    @Config(category = BLOCKING_CATEGORY, description = "Percentage an incoming attack will be reduced by when blocking.")
    @Config.DoubleRange(min = 0.0, max = 1.0)
    public double blockedDamage = 0.5;
    @Config(category = BLOCKING_CATEGORY, description = "Damage sword when blocking an attack depending on the amount of damage blocked. Sword is only damaged when at least three damage points have been blocked, just like a shield.")
    public boolean damageSwordOnBlock = false;
    @Config(category = BLOCKING_CATEGORY, description = "Percentage to reduce knockback by while sword blocking.")
    @Config.DoubleRange(min = 0.0, max = 1.0)
    public double knockbackReduction = 0.2;
    @Config(description = "Arc of available protection depending on what angle the attack is coming from and where the player is looking (means the lower this angle the closer you need to be facing your attacker).")
    @Config.DoubleRange(min = 0.0, max = 360.0)
    public double protectionArc = 360.0;
    @Config(category = PARRYING_CATEGORY, description = "Amount of ticks after starting to block in which an attack will be completely nullified like when blocking with a shield.")
    @Config.IntRange(min = 0, max = SwordBlockingHandler.DEFAULT_ITEM_USE_DURATION)
    public int parryWindow = 10;
    @Config(category = PARRYING_CATEGORY, description = "The strength a parried attacker will be knocked back by.")
    @Config.DoubleRange(min = 0.1, max = 5.0)
    public double parryKnockbackStrength = 0.5;
    @Config(category = PARRYING_CATEGORY, description = "Damage sword when successfully parrying depending on the amount of damage blocked. Sword is only damaged when at least three damage points have been parried, just like a shield.")
    public boolean damageSwordOnParry = false;
    @Config(description = "Blocking requires both hands, meaning the hand not holding the sword must be empty.")
    public boolean requireBothHands = false;
    @Config(category = BLOCKING_CATEGORY, description = "Incoming projectiles such as arrows or tridents will ricochet while blocking.")
    public boolean deflectProjectiles = false;
    @Config(description = "Percentage to slow down movement to while blocking.")
    @Config.DoubleRange(min = 0.0, max = 1.0)
    public double blockingSlowdown = 0.2;
    @Config(description = "The minimum attack strength required to be able to start blocking.")
    @Config.DoubleRange(min = 0.0, max = 1.0)
    public double requiredAttackStrength = 0.0;
}
