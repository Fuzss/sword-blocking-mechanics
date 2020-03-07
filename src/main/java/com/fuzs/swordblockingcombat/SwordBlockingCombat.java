package com.fuzs.swordblockingcombat;

import com.fuzs.swordblockingcombat.client.GrassSwingHandler;
import com.fuzs.swordblockingcombat.client.NoCooldownHandler;
import com.fuzs.swordblockingcombat.client.RenderBlockingHandler;
import com.fuzs.swordblockingcombat.common.ClassicCombatHandler;
import com.fuzs.swordblockingcombat.common.CombatFoodHandler;
import com.fuzs.swordblockingcombat.common.InitiateBlockHandler;
import com.fuzs.swordblockingcombat.common.ModernCombatHandler;
import com.fuzs.swordblockingcombat.config.ConfigBuildHandler;
import com.fuzs.swordblockingcombat.config.ConfigSyncManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({"WeakerAccess", "unused"})
@Mod(SwordBlockingCombat.MODID)
public class SwordBlockingCombat {

    public static final String MODID = "swordblockingcombat";
    public static final String NAME = "Sword Blocking Combat";
    public static final Logger LOGGER = LogManager.getLogger(SwordBlockingCombat.NAME);

    public SwordBlockingCombat() {

        // general setup
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);

        // config setup
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigBuildHandler.SPEC, MODID + ".toml");
        FMLJavaModLoadingContext.get().getModEventBus().addListener(new ConfigSyncManager()::onModConfig);
    }

    private void onCommonSetup(final FMLCommonSetupEvent evt) {

        // sword blocking
        MinecraftForge.EVENT_BUS.register(new InitiateBlockHandler());
        // food buffs
        MinecraftForge.EVENT_BUS.register(new CombatFoodHandler());
        // classic combat
        MinecraftForge.EVENT_BUS.register(new ClassicCombatHandler());
        // modern combat
        MinecraftForge.EVENT_BUS.register(new ModernCombatHandler());
    }

    private void onClientSetup(final FMLClientSetupEvent evt) {

        // sword blocking
        MinecraftForge.EVENT_BUS.register(new RenderBlockingHandler());
        // classic combat
        MinecraftForge.EVENT_BUS.register(new NoCooldownHandler());
        // modern combat
        MinecraftForge.EVENT_BUS.register(new GrassSwingHandler());
    }

}
