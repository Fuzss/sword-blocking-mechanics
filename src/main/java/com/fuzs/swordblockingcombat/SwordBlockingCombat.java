package com.fuzs.swordblockingcombat;

import com.fuzs.materialmaster.api.MaterialMasterReference;
import com.fuzs.swordblockingcombat.capability.CapabilityController;
import com.fuzs.swordblockingcombat.client.handler.GrassSwingHandler;
import com.fuzs.swordblockingcombat.client.handler.NoCooldownHandler;
import com.fuzs.swordblockingcombat.client.handler.RenderBlockingHandler;
import com.fuzs.swordblockingcombat.common.handler.ClassicCombatHandler;
import com.fuzs.swordblockingcombat.common.handler.CombatTestHandler;
import com.fuzs.swordblockingcombat.common.handler.FoodRegenHandler;
import com.fuzs.swordblockingcombat.common.handler.InitiateBlockHandler;
import com.fuzs.swordblockingcombat.config.ConfigBuildHandler;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumMap;
import java.util.function.Consumer;

@SuppressWarnings({"WeakerAccess", "unused"})
@Mod(SwordBlockingCombat.MODID)
public class SwordBlockingCombat {

    public static final String MODID = "swordblockingcombat";
    public static final String NAME = "Sword Blocking Combat";
    public static final Logger LOGGER = LogManager.getLogger(SwordBlockingCombat.NAME);

    private static final EnumMap<ModConfig.Type, ModConfig> CONFIGS = new EnumMap<>(ModConfig.Type.class);

    public SwordBlockingCombat() {

        // general setup
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onInterModEnqueue);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onLoadComplete);

        // config setup
        registerConfig(ModConfig.Type.COMMON, ConfigBuildHandler.SPEC);
        CONFIGS.values().forEach(ModLoadingContext.get().getActiveContainer()::addConfig);
    }

    private void onCommonSetup(final FMLCommonSetupEvent evt) {

        CapabilityController.register();
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

    private void onInterModEnqueue(final InterModEnqueueEvent evt) {

        // register mod to be searched for sync providers
        InterModComms.sendTo(MODID, MaterialMasterReference.MODID, MaterialMasterReference.REGISTER_SYNC_PROVIDER,
                () -> null);
        // register mod to be searched for sync providers
        InterModComms.sendTo(MODID, MaterialMasterReference.MODID, MaterialMasterReference.REGISTER_CONFIG_PROVIDER,
                () -> CONFIGS.get(ModConfig.Type.COMMON));
    }

    private void onLoadComplete(final FMLLoadCompleteEvent evt) {

        // add listener to config events
        InterModComms.getMessages(MODID)
                .filter(message -> message.getMethod().equals(MaterialMasterReference.RETURN_CONFIG_EVENT))
                .map(message -> (Consumer<? extends Event>) message.getMessageSupplier().get())
                .forEach(FMLJavaModLoadingContext.get().getModEventBus()::addListener);
    }

    private static void registerConfig(ModConfig.Type type, ForgeConfigSpec spec) {

        CONFIGS.put(type, new ModConfig(type, spec, ModLoadingContext.get().getActiveContainer()));
    }

}
