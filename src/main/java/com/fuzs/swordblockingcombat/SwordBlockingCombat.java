package com.fuzs.swordblockingcombat;

import com.fuzs.materialmaster.api.PropertyProviderUtils;
import com.fuzs.swordblockingcombat.client.handler.GrassSwingHandler;
import com.fuzs.swordblockingcombat.client.handler.NoCooldownHandler;
import com.fuzs.swordblockingcombat.client.handler.RenderBlockingHandler;
import com.fuzs.swordblockingcombat.common.handler.ClassicCombatHandler;
import com.fuzs.swordblockingcombat.common.handler.FoodRegenHandler;
import com.fuzs.swordblockingcombat.common.handler.InitiateBlockHandler;
import com.fuzs.swordblockingcombat.common.handler.CombatTestHandler;
import com.fuzs.swordblockingcombat.config.ConfigBuildHandler;
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
        PropertyProviderUtils.registerModProvider();

        // config setup
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigBuildHandler.SPEC, MODID + ".toml");
    }

    private void onCommonSetup(final FMLCommonSetupEvent evt) {

        // sword blocking
        MinecraftForge.EVENT_BUS.register(new InitiateBlockHandler());
        // food buffs
        MinecraftForge.EVENT_BUS.register(new FoodRegenHandler());
        // classic combat
        MinecraftForge.EVENT_BUS.register(new ClassicCombatHandler());
        // combat test
        MinecraftForge.EVENT_BUS.register(new CombatTestHandler());
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
