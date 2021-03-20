package com.fuzs.puzzleslib_sbm.config;


import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * internal storage for registered config entries
 * @param <S> config value of a certain type
 * @param <T> type for value
 * @param <R> return type after applying transformer
 */
public class ConfigValueData<S extends ForgeConfigSpec.ConfigValue<T>, T, R> {

    /**
     * config value entry
     */
    private final S value;
    /**
     * config path for value
     */
    final String path;
    /**
     * config type of this entry
     */
    final ModConfig.Type type;
    /**
     * action to perform when the entry is updated
     */
    private final Consumer<R> sync;
    /**
     * transformation to apply when returning value, usually {@link Function#identity}
     */
    private final Function<T, R> transformer;

    /**
     * new entry storage
     */
    ConfigValueData(S value, ModConfig.Type type, Consumer<R> sync, Function<T, R> transformer) {

        this.value = value;
        this.path = String.join(".", value.getPath());
        this.type = type;
        this.sync = sync;
        this.transformer = transformer;
    }

    /**
     * modify a config value so the config file is updated as well and sync afterwards
     * @param operator action to apply to config value
     */
    public void modifyConfigValue(UnaryOperator<T> operator) {

        this.value.set(operator.apply(this.getRawValue()));
        this.sync();
    }

    /**
     * @return current value from entry
     */
    public R getValue() {

        return this.transformer.apply(this.value.get());
    }

    /**
     * @return current raw value from entry
     */
    public T getRawValue() {

        return this.value.get();
    }

    /**
     * get value from config value and supply it to consumer
     */
    void sync() {

        this.sync.accept(this.getValue());
    }

}
