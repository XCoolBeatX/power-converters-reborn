package dev.lenard.powerconverters.integration;

/**
 * Integration with an energy system that requires explicit registration rather than
 * capabilities, i.e. IndustrialCraft's EnergyNet.
 *
 * <p>As with {@link CapabilityPlugin}, this interface names no foreign types, so the block
 * entities can hold one without dragging IC2 onto the classpath.
 */
public interface LifecyclePlugin {

    /** Join the foreign network. Called once the block entity has a level. */
    void join();

    /** Leave the foreign network. Must be safe to call when never joined, or twice. */
    void leave();
}
