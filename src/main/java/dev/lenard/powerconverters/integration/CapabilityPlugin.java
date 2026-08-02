package dev.lenard.powerconverters.integration;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A block entity's capability support for one foreign energy system.
 *
 * <p>This interface deliberately mentions only vanilla and Forge types. Implementations that
 * touch another mod's classes live in their own package and are constructed through a factory
 * that is only reached once {@code ModList} confirms the mod is present, so the JVM never has
 * to resolve those classes on an installation without that mod.
 */
public interface CapabilityPlugin {

    @NotNull
    <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side);

    /** Invalidate any LazyOptionals handed out, when the owning block entity goes away. */
    default void invalidate() {
    }
}
