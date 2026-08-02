package dev.lenard.powerconverters.blockentity.bc;

import dev.lenard.powerconverters.PCBlockEntities;
import dev.lenard.powerconverters.PowerConverters;
import dev.lenard.powerconverters.blockentity.AbstractConverterBlockEntity;
import dev.lenard.powerconverters.integration.CapabilityPlugin;
import dev.lenard.powerconverters.integration.bc.BCPlugins;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Accepts BuildCraft MJ from adjacent pipes or engines and stores it in the Energy Bridge. */
public class MJConsumerBlockEntity extends AbstractConverterBlockEntity {

    @Nullable
    private CapabilityPlugin plugin;
    private boolean pluginResolved;

    public MJConsumerBlockEntity(BlockPos pos, BlockState state) {
        super(PCBlockEntities.MJ_CONSUMER.get(), pos, state);
    }

    @Nullable
    private CapabilityPlugin plugin() {
        if (!pluginResolved) {
            pluginResolved = true;
            if (PowerConverters.isBuildCraftLoaded()) {
                plugin = BCPlugins.createReceiver(this);
            }
        }
        return plugin;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        CapabilityPlugin current = plugin();
        if (current != null) {
            LazyOptional<T> result = current.getCapability(cap, side);
            if (result.isPresent()) {
                return result;
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        if (plugin != null) {
            plugin.invalidate();
        }
    }
}
