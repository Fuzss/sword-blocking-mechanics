package com.fuzs.puzzleslib_sbm.element.side;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.ForgeConfigSpec;

/**
 * implement this for elements with client-side capabilities
 */
public interface IClientElement extends ISidedElement {

    /**
     * register client events
     */
    default void setupClient() {

    }

    /**
     * setup for {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent}
     * is always loaded no matter the element's state
     */
    default void loadClient() {

    }

    /**
     * should basically clean up changes made by this element
     */
    default void unloadClient() {

    }

    /**
     * build client config
     *
     * @param builder builder for client config
     */
    default void setupClientConfig(ForgeConfigSpec.Builder builder) {

    }

    /**
     * @return description for this elements client config section
     */
    default String[] getClientDescription() {

        return new String[0];
    }

    /**
     * @return Minecraft client instance
     */
    static Minecraft getMc() {

        return Minecraft.getInstance();
    }

}
