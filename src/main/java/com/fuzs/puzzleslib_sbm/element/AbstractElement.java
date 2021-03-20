package com.fuzs.puzzleslib_sbm.element;

import com.fuzs.puzzleslib_sbm.PuzzlesLib;
import com.fuzs.puzzleslib_sbm.config.ConfigManager;
import com.fuzs.puzzleslib_sbm.element.side.IClientElement;
import com.fuzs.puzzleslib_sbm.element.side.ICommonElement;
import com.fuzs.puzzleslib_sbm.element.side.IServerElement;
import com.google.common.collect.Lists;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * all features a mod adds are structured into elements which are then registered, this is an abstract version
 */
@SuppressWarnings("unused")
public abstract class AbstractElement extends EventListener implements IConfigurableElement, IRegistryElement<AbstractElement> {

    /**
     * registry name of this element
     */
    private ResourceLocation name;
    /**
     * is this element enabled (are events registered)
     * 1 and 0 for enable / disable, -1 for force disable where reloading the config doesn't have any effect
     */
    private int enabled = this.getDefaultState() ? 1 : 0;
    /**
     * all events registered by this element
     */
    private final List<EventStorage<? extends Event>> eventListeners = Lists.newArrayList();

    @Nonnull
    @Override
    public final ResourceLocation getRegistryName() {

        if (this.name == null) {

            throw new UnsupportedOperationException("Cannot get name for element: " + "Name not set");
        }

        return this.name;
    }

    @Nonnull
    @Override
    public final AbstractElement setRegistryName(@Nonnull ResourceLocation name) {

        if (this.name != null) {

            throw new UnsupportedOperationException("Cannot set name for element: " + "Name already set");
        }

        this.name = name;

        return this;
    }

    @Override
    public final String getDisplayName() {

        return Stream.of(this.getRegistryName().getPath().split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
    }

    @Override
    public boolean getDefaultState() {

        return true;
    }

    @Override
    public String[] isIncompatibleWith() {

        return new String[0];
    }

    /**
     * @return has an incompatible mod been found
     */
    protected final boolean isIncompatibilityPresent() {

        return Stream.of(this.isIncompatibleWith()).anyMatch(modId -> ModList.get().isLoaded(modId));
    }

    @Override
    public final void setupGeneralConfig(ForgeConfigSpec.Builder builder) {

        addToConfig(builder.comment(this.getDescription()).define(this.getDisplayName(), this.getDefaultState()), this::setEnabled);
    }

    /**
     * build element config and get event listeners
     */
    public final void setup() {

        this.setupConfig();
        this.loadAllSides(ICommonElement::setupCommon, IClientElement::setupClient, IServerElement::setupServer);
    }

    /**
     * setup config for all sides
     */
    private void setupConfig() {

        Consumer<ICommonElement> commonConfig = element -> ConfigManager.builder().create(this, ModConfig.Type.COMMON, element::setupCommonConfig, element.getCommonDescription());
        Consumer<IClientElement> clientConfig = element -> ConfigManager.builder().create(this, ModConfig.Type.CLIENT, element::setupClientConfig, element.getClientDescription());
        Consumer<IServerElement> serverConfig = element -> ConfigManager.builder().create(this, ModConfig.Type.SERVER, element::setupServerConfig, element.getServerDescription());
        this.loadAllSides(commonConfig, clientConfig, serverConfig);
    }

    /**
     * @param common consumer if implements {@link ICommonElement}
     * @param client consumer if implements {@link IClientElement}
     * @param server consumer if implements {@link IServerElement}
     */
    private void loadAllSides(Consumer<ICommonElement> common, Consumer<IClientElement> client, Consumer<IServerElement> server) {

        if (this instanceof ICommonElement) {

            common.accept(((ICommonElement) this));
        }

        if (FMLEnvironment.dist.isClient() && this instanceof IClientElement) {

            client.accept(((IClientElement) this));
        }

        if (FMLEnvironment.dist.isDedicatedServer() && this instanceof IServerElement) {

            server.accept(((IServerElement) this));
        }
    }

    /**
     * call sided load methods and register Forge events from internal storage
     * no need to check physical side as the setup event won't be called by Forge anyways
     * @param evt setup event this is called from
     */
    public final void load(ParallelDispatchEvent evt) {

        // don't load anything if an incompatible mod is detected
        if (this.isIncompatibilityPresent()) {

            this.enabled = -1;
            return;
        }

        this.loadSide(evt);
        if (this instanceof ICommonElement) {

            if (evt instanceof FMLCommonSetupEvent) {

                this.reload(this.isEnabled(), true);
            }
        } else if (evt instanceof FMLClientSetupEvent || evt instanceof FMLDedicatedServerSetupEvent) {

            this.reload(this.isEnabled(), true);
        }
    }

    /**
     * initialize sided content, this will always happen, even when the element is not loaded
     * @param evt setup event this is called from
     */
    private void loadSide(ParallelDispatchEvent evt) {

        if (evt instanceof FMLCommonSetupEvent && this instanceof ICommonElement) {

            ((ICommonElement) this).loadCommon();
        } else if (evt instanceof FMLClientSetupEvent && this instanceof IClientElement) {

            ((IClientElement) this).loadClient();
        } else if (evt instanceof FMLDedicatedServerSetupEvent && this instanceof IServerElement) {

            ((IServerElement) this).loadServer();
        }
    }

    /**
     * update status of all reloadable components such as events and everything specified in sided load methods
     * @param firstLoad should unregistering not happen, as nothing has been loaded yet anyways
     */
    private void reload(boolean enabled, boolean firstLoad) {

        if (enabled || this.isAlwaysEnabled()) {

            this.reloadEventListeners(true);
        } else if (!firstLoad) {

            this.reloadEventListeners(false);
            this.loadAllSides(ICommonElement::unloadCommon, IClientElement::unloadClient, IServerElement::unloadServer);
        }
    }

    /**
     * update status of all stored events
     * @param enable should events be loaded, otherwise they're unloaded
     */
    private void reloadEventListeners(boolean enable) {

        if (enable) {

            this.getEventListeners().forEach(EventStorage::register);
        } else {

            this.getEventListeners().forEach(EventStorage::unregister);
        }
    }

    @Override
    public final boolean isEnabled() {

        return this.enabled == 1;
    }

    /**
     * are contents from this element always active
     * @return is always enabled
     */
    protected boolean isAlwaysEnabled() {

        return false;
    }

    /**
     * set {@link #enabled} state, reload when changed
     * @param enabled enabled
     */
    private void setEnabled(boolean enabled) {

        this.setEnabled(enabled ? 1 : 0);
    }

    /**
     * set {@link #enabled} state, reload when changed
     * @param enabled enabled as int
     */
    private void setEnabled(int enabled) {

        if (this.enabled != -1 && this.enabled != enabled) {

            this.reload(enabled == 1, false);
            this.enabled = enabled;
        }
    }

    /**
     * something went wrong using this element, disable until game is restarted
     */
    protected final void setDisabled() {

        this.setEnabled(-1);
        PuzzlesLib.LOGGER.warn("Detected issue in {} element: {}", this.getDisplayName(), "Disabling until game restart");
    }

    @Override
    public final List<EventStorage<? extends Event>> getEventListeners() {

        return this.eventListeners;
    }

    /**
     * empty element needed for some aspects of {@link com.fuzs.puzzleslib_sbm.config.ConfigManager}
     */
    public static AbstractElement createEmpty(ResourceLocation name) {

        return new AbstractElement() {

            @Override
            public String[] getDescription() {

                return new String[0];
            }

        }.setRegistryName(name);
    }

}
