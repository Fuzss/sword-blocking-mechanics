package com.fuzs.puzzleslib_sbm.config;

import com.fuzs.puzzleslib_sbm.PuzzlesLib;
import com.fuzs.puzzleslib_sbm.element.AbstractElement;
import com.fuzs.puzzleslib_sbm.element.ElementRegistry;
import com.fuzs.puzzleslib_sbm.util.INamespaceLocator;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * main config manager for this mod
 */
@SuppressWarnings("unused")
public class ConfigManager implements INamespaceLocator {

    /**
     * singleton instance
     */
    private static ConfigManager instance;

    /**
     * config build helpers for each mod separately since they store the forge builders and specs
     */
    private final Map<String, ConfigBuilder> configBuilders = Maps.newHashMap();
    /**
     * all config entries as a set
     */
    private final Multimap<AbstractElement, ConfigValueData<? extends ForgeConfigSpec.ConfigValue<?>, ?, ?>> configData = HashMultimap.create();
    /**
     * listeners to call when a config is somehow loaded
     */
    private final Multimap<ModConfig.Type, Runnable> configListeners = HashMultimap.create();

    /**
     * this class is a singleton
     */
    private ConfigManager() {

    }

    /**
     * register configs from non-empty builders and add listener from active mod container to {@link #onModConfig}
     * @param path optional directory inside of main config dir
     */
    public void load(String... path) {

        if (path.length > 0) {

            this.getBuilder().moveToFolder(path);
        }

        this.getBuilder().registerConfigs(ModLoadingContext.get());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModConfig);
    }

    /**
     * fires on both loading and reloading, loading phase is required for initial setup
     * @param evt event provided by Forge
     */
    public void onModConfig(final ModConfig.ModConfigEvent evt) {

        String modid = evt.getConfig().getModId();
        ModConfig.Type type = evt.getConfig().getType();
        if (this.getBuilder(modid).isSpecNotValid(type)) {

            PuzzlesLib.LOGGER.error("Unable to get values from " + type.extension() + " config for " + modid + " during " + (evt instanceof ModConfig.Loading ? "loading" : "reloading") + " phase: " + "Config spec not present");
        } else {

            if (this.syncAll(modid, type) && evt instanceof ModConfig.Reloading) {

                PuzzlesLib.LOGGER.info("Reloaded " + type.extension() + " config for mod " + modid);
            }
        }
    }

    /**
     * sync all config entries and notify all listeners
     * @param type config type for this listener
     */
    public void syncAll(ModConfig.Type type) {

        if (this.syncAll(null, type)) {

            PuzzlesLib.LOGGER.info("Reloaded " + type.extension() + " config for all mods");
        }
    }

    /**
     * sync config entries for specific type of config
     * call listeners for type as the config has somehow been loaded
     * @param modid mod to get entries for
     * @param type config type for this listener
     * @return was any data found for syncing
     */
    private boolean syncAll(@Nullable String modid, ModConfig.Type type) {

        Collection<ConfigValueData<? extends ForgeConfigSpec.ConfigValue<?>, ?, ?>> data = this.getAllConfigData(modid, type);
        if (!data.isEmpty()) {

            data.forEach(ConfigValueData::sync);
            this.configListeners.get(type).forEach(Runnable::run);

            return true;
        }

        return false;
    }

    /**
     * @param modid mod to get entries for
     * @param type config type for this listener
     * @return collection of enabled entries only for this mod and type
     */
    private Collection<ConfigValueData<? extends ForgeConfigSpec.ConfigValue<?>, ?, ?>> getAllConfigData(@Nullable String modid, ModConfig.Type type) {

        return this.configData.entries().stream()
                .filter(entry -> modid == null || entry.getKey().getRegistryName().getNamespace().equals(modid))
                .filter(entry -> entry.getKey().isEnabled())
                .map(Map.Entry::getValue)
                .filter(value -> value.type == type)
                .collect(Collectors.toSet());
    }

    /**
     * @param element element this data belongs to
     * @param path individual parts of path for config value
     * @return the config value
     */
    @Nullable
    public Object getConfigValue(AbstractElement element, String... path) {

        assert path.length != 0 : "Unable to get config value: " + "Invalid config path";

        if (element.isEnabled()) {

            String singlePath = Stream.concat(Stream.of(element.getRegistryName().getPath()), Stream.of(path)).collect(Collectors.joining("."));
            return this.getConfigData(element, singlePath).
                    <Object>map(ConfigValueData::getValue)
                    .orElse(null);
        }

        return null;
    }

    /**
     * @param element element this data belongs to
     * @param path individual parts of path for config value
     * @return config data
     */
    private Optional<ConfigValueData<? extends ForgeConfigSpec.ConfigValue<?>, ?, ?>> getConfigData(AbstractElement element, String path) {

        for (ConfigValueData<? extends ForgeConfigSpec.ConfigValue<?>, ?, ?> data : this.configData.get(element)) {

            if (data.path.equals(path)) {

                return Optional.of(data);
            }
        }

        PuzzlesLib.LOGGER.error("Unable to get config value at path \"" + path + "\": " + "No config value found");
        return Optional.empty();
    }

    /**
     * register config entry on both client and server
     * @param entry source config value object
     * @param action action to perform when value changes (is reloaded)
     * @param <S> config value of a certain type
     * @param <T> type for value
     */
    public <S extends ForgeConfigSpec.ConfigValue<T>, T> void registerCommonEntry(S entry, Consumer<T> action) {

        this.registerEntry(entry, ModConfig.Type.COMMON, action, Function.identity());
    }

    /**
     * register config entry on the client
     * @param entry source config value object
     * @param action action to perform when value changes (is reloaded)
     * @param <S> config value of a certain type
     * @param <T> type for value
     */
    public <S extends ForgeConfigSpec.ConfigValue<T>, T> void registerClientEntry(S entry, Consumer<T> action) {

        this.registerEntry(entry, ModConfig.Type.CLIENT, action, Function.identity());
    }

    /**
     * register config entry on the server
     * @param entry source config value object
     * @param action action to perform when value changes (is reloaded)
     * @param <S> config value of a certain type
     * @param <T> type for value
     */
    public <S extends ForgeConfigSpec.ConfigValue<T>, T> void registerServerEntry(S entry, Consumer<T> action) {

        this.registerEntry(entry, ModConfig.Type.SERVER, action, Function.identity());
    }

    /**
     * register config entry for given type
     * @param entry source config value object
     * @param type type of config to register for
     * @param action action to perform when value changes (is reloaded)
     * @param transformer transformation to apply when returning value
     * @param <S> config value of a certain type
     * @param <T> type for value
     * @param <R> final return type of config entry
     */
    private <S extends ForgeConfigSpec.ConfigValue<T>, T, R> void registerEntry(S entry, ModConfig.Type type, Consumer<R> action, Function<T, R> transformer) {

        this.configData.put(ElementRegistry.EMPTY, new ConfigValueData<>(entry, type, action, transformer));
    }

    /**
     * register config entry for active type
     * @param <S> config value of a certain type
     * @param <T> type for value
     * @param entry source config value object
     * @param action action to perform when value changes (is reloaded)
     */
    public <S extends ForgeConfigSpec.ConfigValue<T>, T> void registerEntry(S entry, Consumer<T> action) {

        this.registerEntry(entry, action, Function.identity());
    }

    /**
     * register config entry for active type
     * @param entry source config value object
     * @param action action to perform when value changes (is reloaded)
     * @param transformer transformation to apply when returning value
     * @param <S> config value of a certain type
     * @param <T> type for value
     * @param <R> final return type of config entry
     */
    public <S extends ForgeConfigSpec.ConfigValue<T>, T, R> void registerEntry(S entry, Consumer<R> action, Function<T, R> transformer) {

        Pair<AbstractElement, ModConfig.Type> activeTuple = this.getBuilder().getActiveTuple();
        if (activeTuple == null) {

            PuzzlesLib.LOGGER.error("Unable to register config entry: " + "Active builder is null");
        } else if (this.getBuilder().isSpecNotBuilt(activeTuple.getRight())) {

            this.configData.put(activeTuple.getLeft(), new ConfigValueData<>(entry, activeTuple.getRight(), action, transformer));
        } else {

            PuzzlesLib.LOGGER.error("Unable to register config entry: " + "Config spec already built");
        }
    }

    /**
     * add a listener for when the config is somehow loaded
     * @param listener listener to add
     * @param type config type for this listener
     */
    public void addListener(Runnable listener, ModConfig.Type type) {

        this.configListeners.put(type, listener);
    }

    /**
     * @param type type of config
     * @param modId modid this config belongs to
     * @return config name as if it were generated by Forge itself
     */
    public static String getConfigName(String modId, ModConfig.Type type) {

        return String.format("%s-%s.toml", modId, type.extension());
    }

    /**
     * put config into it's own folder when there are multiples
     * @param type type of config
     * @param modId modid this config belongs to
     * @return name lead by folder
     */
    public static String getConfigNameInFolder(String modId, ModConfig.Type type) {

        return modId + File.separator + getConfigName(modId, type);
    }

    /**
     * @param entries entries to convert to string
     * @param <T> registry element type
     * @return entries as string list
     */
    @SafeVarargs
    public static <T extends IForgeRegistryEntry<T>> List<String> getKeyList(T... entries) {

        return Stream.of(entries)
                .map(IForgeRegistryEntry::getRegistryName)
                .filter(Objects::nonNull)
                .map(ResourceLocation::toString)
                .collect(Collectors.toList());
    }

    /**
     * get builder for active mod, create if not present
     * @return builder for active mod
     */
    private ConfigBuilder getBuilder() {

        return this.getBuilder(this.getActiveNamespace());
    }

    /**
     * get builder for a given mod, create if not present
     * @param modid modid to get builder for
     * @return builder for active mod
     */
    private ConfigBuilder getBuilder(String modid) {

        return this.configBuilders.computeIfAbsent(modid, key -> new ConfigBuilder());
    }

    /**
     * @return instance of this
     */
    public static ConfigManager get() {

        if (instance == null) {

            instance = new ConfigManager();
        }

        return instance;
    }

    /**
     * get builder directly
     * @return builder for active mod
     */
    public static ConfigBuilder builder() {

        return get().getBuilder();
    }

}
