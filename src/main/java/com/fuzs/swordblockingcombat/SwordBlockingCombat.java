package com.fuzs.swordblockingcombat;

import com.fuzs.swordblockingcombat.handler.*;
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

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigBuildHandler.SPEC, MODID + ".toml");
    }

    private void onCommonSetup(final FMLCommonSetupEvent evt) {

        MinecraftForge.EVENT_BUS.register(new InitiateBlockHandler());
        MinecraftForge.EVENT_BUS.register(new CombatFoodHandler());
        MinecraftForge.EVENT_BUS.register(new EnchantmentHandler());
        MinecraftForge.EVENT_BUS.register(new MiscellaneousHandler());
    }

    private void onClientSetup(final FMLClientSetupEvent evt) {

        MinecraftForge.EVENT_BUS.register(new RenderBlockingHandler());
        MinecraftForge.EVENT_BUS.register(new NoCooldownHandler());
    }

}
