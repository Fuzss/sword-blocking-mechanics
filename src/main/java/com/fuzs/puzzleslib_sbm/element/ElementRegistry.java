package com.fuzs.puzzleslib_sbm.element;

import com.fuzs.puzzleslib_sbm.PuzzlesLib;
import com.fuzs.puzzleslib_sbm.config.ConfigManager;
import com.fuzs.puzzleslib_sbm.element.side.IClientElement;
import com.fuzs.puzzleslib_sbm.element.side.ICommonElement;
import com.fuzs.puzzleslib_sbm.element.side.IServerElement;
import com.fuzs.puzzleslib_sbm.element.side.ISidedElement;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * registry for elements
 */
@SuppressWarnings("unused")
public class ElementRegistry {

    /**
     * empty element needed when adding config entries to {@link com.fuzs.puzzleslib_sbm.config.ConfigManager} manually
     */
    public static final AbstractElement EMPTY = AbstractElement.createEmpty(new ResourceLocation(PuzzlesLib.MODID, "empty"));
    /**
     * general storage for elements of all mods for performing actions on all of them
     */
    private static final BiMap<ResourceLocation, AbstractElement> ELEMENTS = HashBiMap.create();
    /**
     * all elements belonging to the active mod, will be cleared after those elements have been added to {@link #ELEMENTS}
     * use tree map for alphabetical sorting cause why not
     */
    private static final TreeMap<String, AbstractElement> MOD_ELEMENTS = Maps.newTreeMap();

    /**
     * register an element
     * @param key identifier for this element
     * @param supplier supplier for element to be registered
     * @return <code>element</code>
     * @param <T> make sure element also extends ISidedElement
     */
    public static <T extends AbstractElement & ISidedElement> AbstractElement register(String key, Supplier<T> supplier) {

        return register(key, supplier, FMLEnvironment.dist);
    }

    /**
     * register an element
     * @param key identifier for this element
     * @param supplier supplier for element to be registered
     * @param dist physical side to register on
     * @return <code>element</code>
     * @param <T> make sure element also extends ISidedElement
     */
    @Nullable
    public static <T extends AbstractElement & ISidedElement> AbstractElement register(String key, Supplier<T> supplier, Dist dist) {

        if (dist == FMLEnvironment.dist) {

            AbstractElement element = supplier.get();

            assert element instanceof ICommonElement || FMLEnvironment.dist.isClient() || element instanceof IServerElement : "Unable to register element: " + "Trying to register client element for server side";
            assert element instanceof ICommonElement || FMLEnvironment.dist.isDedicatedServer() || element instanceof IClientElement : "Unable to register element: " + "Trying to register server element for client side";

            MOD_ELEMENTS.put(key, element);

            return element;
        }

        return null;
    }

    /**
     * register an element, overload this to set mod namespace
     * every element must be sided, meaning must somehow implement {@link ISidedElement}
     * @param namespace namespace of registering mod
     * @param key identifier for this element
     * @param supplier supplier for element to be registered
     * @param dist physical side to register on
     * @return <code>element</code>
     * @param <T> make sure element also extends ISidedElement
     */
    @Nullable
    protected static <T extends AbstractElement & ISidedElement> AbstractElement register(String namespace, String key, Supplier<T> supplier, Dist dist) {

        if (dist == FMLEnvironment.dist) {

            AbstractElement element = supplier.get();

            assert element instanceof ICommonElement || FMLEnvironment.dist.isClient() || element instanceof IServerElement : "Unable to register element: " + "Trying to register client element for server side";
            assert element instanceof ICommonElement || FMLEnvironment.dist.isDedicatedServer() || element instanceof IClientElement : "Unable to register element: " + "Trying to register server element for client side";

            ELEMENTS.put(new ResourceLocation(namespace, key), element);
            return element;
        }

        return null;
    }

    /**
     * @param element element to get name for
     * @return name set in elements registry
     */
    public static ResourceLocation getRegistryName(AbstractElement element) {

        return ELEMENTS.inverse().get(element);
    }

    /**
     * get an element from another mod which uses this registry
     * @param namespace namespace of owning mod
     * @param key key for element to get
     * @return optional element
     */
    public static Optional<AbstractElement> get(String namespace, String key) {

        return get(new ResourceLocation(namespace, key));
    }

    /**
     * get an element from another mod which uses this registry
     * @param name name of element to get
     * @return optional element
     */
    public static Optional<AbstractElement> get(ResourceLocation name) {

        return Optional.ofNullable(ELEMENTS.get(name));
    }

    /**
     * to be used by other mods using this library
     * @param name name of element to get
     * @param path path for config value
     * @return the config value
     */
    public static <T> Optional<T> getConfigValue(ResourceLocation name, String... path) {

        Optional<AbstractElement> element = get(name);
        if (element.isPresent()) {

            return getConfigValue(element.get(), path);
        }

        PuzzlesLib.LOGGER.error("Unable to get config value: " + "Invalid element name");
        return Optional.empty();
    }

    /**
     * to be used from inside of this mod
     * @param element element to get value from
     * @param path path for config value
     * @return the config value
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> getConfigValue(AbstractElement element, String... path) {

        return Optional.ofNullable((T) ConfigManager.get().getConfigValue(element, path));
    }

    /**
     * generate general config section for controlling elements, setup individual config sections and collect events to be registered in {@link #load}
     * @param namespace namespace of active mod
     */
    public static void setup(String namespace) {

        assert !MOD_ELEMENTS.isEmpty() : "Unable to setup elements for " + namespace + ": " + "No elements registered";

        // add to main elements storage
        for (Map.Entry<String, AbstractElement> entry : MOD_ELEMENTS.entrySet()) {

            ResourceLocation elementName = new ResourceLocation(namespace, entry.getKey());
            ELEMENTS.put(elementName, entry.getValue().setRegistryName(elementName));
        }

        setupGeneralSide(namespace, getElementsAtSide(MOD_ELEMENTS.values(), element -> element instanceof ICommonElement), ModConfig.Type.COMMON, FMLEnvironment.dist);
        setupGeneralSide(namespace, getElementsAtSide(MOD_ELEMENTS.values(), element -> element instanceof IClientElement), ModConfig.Type.CLIENT, Dist.CLIENT);
        setupGeneralSide(namespace, getElementsAtSide(MOD_ELEMENTS.values(), element -> element instanceof IServerElement), ModConfig.Type.SERVER, Dist.DEDICATED_SERVER);

        getAllElements(namespace).forEach(AbstractElement::setup);
    }

    /**
     * also clears elements from <code>elements</code>
     * @param elements all elements for this mod
     * @param isCurrentSide instanceof check for {@link com.fuzs.puzzleslib_sbm.element.side.ISidedElement}
     * @return all elements for this mod for active side
     */
    private static Collection<AbstractElement> getElementsAtSide(Collection<AbstractElement> elements, Predicate<AbstractElement> isCurrentSide) {

        Set<AbstractElement> elementsAtSide = Sets.newHashSet();
        for (Iterator<AbstractElement> iterator = elements.iterator(); iterator.hasNext();) {

            AbstractElement element = iterator.next();
            if (isCurrentSide.test(element)) {

                elementsAtSide.add(element);
                iterator.remove();
            }
        }

        return elementsAtSide;
    }

    /**
     * setup general section with control over individual elements for all config types
     * @param namespace namespace of active mod
     * @param elements all elements for this mod
     * @param type config type to create general category for
     * @param dist physical side this element can only be registered on
     */
    private static void setupGeneralSide(String namespace, Collection<AbstractElement> elements, ModConfig.Type type, Dist dist) {

        if (!elements.isEmpty()) {

            assert dist == FMLEnvironment.dist : "Unable to setup element: " + "Sided element registered on wrong side";

            // create dummy element
            AbstractElement general = AbstractElement.createEmpty(new ResourceLocation(namespace, "general"));
            ConfigManager.builder().create(general, type, builder -> elements.forEach(element -> element.setupGeneralConfig(builder)));
        }
    }

    /**
     * @param namespace modid to get elements for
     * @return elements for <code>namespace</code> as set
     */
    public static Set<AbstractElement> getAllElements(String namespace) {

        return ELEMENTS.entrySet().stream()
                .filter(entry -> entry.getKey().getNamespace().equals(namespace))
                .map(Map.Entry::getValue)
                .collect(Collectors.toSet());
    }

    /**
     * execute load for common and both sides, also register events
     * which sided elements to load is defined by provided event instance
     * loads all elements, no matter which mod they're from
     * @param evt event type
     */
    public static void load(ParallelDispatchEvent evt) {

        ELEMENTS.values().forEach(element -> element.load(evt));
    }

}
