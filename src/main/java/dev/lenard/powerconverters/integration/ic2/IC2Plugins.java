package dev.lenard.powerconverters.integration.ic2;

import dev.lenard.powerconverters.blockentity.AbstractConverterBlockEntity;
import dev.lenard.powerconverters.integration.LifecyclePlugin;

/**
 * Entry point for the IndustrialCraft integration.
 *
 * <p>Callers must check {@code PowerConverters.isIc2Loaded()} before touching this class.
 */
public final class IC2Plugins {

    private IC2Plugins() {
    }

    public static LifecyclePlugin createSink(AbstractConverterBlockEntity owner) {
        return new EuSinkPlugin(owner);
    }

    public static LifecyclePlugin createSource(AbstractConverterBlockEntity owner) {
        return new EuSourcePlugin(owner);
    }
}
