package dev.lenard.powerconverters.integration.bc;

import dev.lenard.powerconverters.blockentity.AbstractConverterBlockEntity;
import dev.lenard.powerconverters.integration.CapabilityPlugin;

/**
 * Entry point for the BuildCraft integration.
 *
 * <p>Callers must check {@code PowerConverters.isBuildCraftLoaded()} before touching this
 * class. Every BuildCraft type stays inside this package, so on an installation without
 * BuildCraft nothing here is ever resolved.
 */
public final class BCPlugins {

    private BCPlugins() {
    }

    public static CapabilityPlugin createReceiver(AbstractConverterBlockEntity owner) {
        return new MjReceiverPlugin(owner);
    }

    public static CapabilityPlugin createProvider(AbstractConverterBlockEntity owner) {
        return new MjProviderPlugin(owner);
    }
}
