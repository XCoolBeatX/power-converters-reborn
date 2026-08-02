package dev.lenard.powerconverters.blockentity.ic2;

import dev.lenard.powerconverters.PowerConverters;
import dev.lenard.powerconverters.blockentity.AbstractConverterBlockEntity;
import dev.lenard.powerconverters.integration.LifecyclePlugin;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Shared lifecycle for the EU converters.
 *
 * <p>Unlike Forge Energy and MJ, IC2 has no capability system: a tile must explicitly join the
 * EnergyNet and, just as importantly, leave it again. Missing the leave call on either removal
 * or chunk unload leaks a stale tile into the net and produces the classic IC2 ghost-machine
 * behaviour, so both paths are covered here.
 */
public abstract class AbstractEuBlockEntity extends AbstractConverterBlockEntity {

    @Nullable
    private LifecyclePlugin plugin;
    private boolean pluginResolved;

    protected AbstractEuBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /** Only ever called once IC2 is known to be present. */
    protected abstract LifecyclePlugin createPlugin();

    @Nullable
    private LifecyclePlugin plugin() {
        if (!pluginResolved) {
            pluginResolved = true;
            if (PowerConverters.isIc2Loaded()) {
                plugin = createPlugin();
            }
        }
        return plugin;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level == null || level.isClientSide) {
            return;
        }

        // Do not join the EnergyNet inline. During chunk load the surrounding cables and
        // machines may not exist yet, so registering here can build a partial grid. IC2's own
        // tiles sidestep this by deferring through a world callback; scheduling onto the
        // server thread achieves the same thing without depending on IC2 internals.
        MinecraftServer server = level.getServer();
        if (server == null) {
            joinNet();
            return;
        }
        server.execute(() -> {
            if (!isRemoved()) {
                joinNet();
            }
        });
    }

    private void joinNet() {
        LifecyclePlugin current = plugin();
        if (current != null) {
            current.join();
        }
    }

    @Override
    public void setRemoved() {
        leaveNet();
        super.setRemoved();
    }

    @Override
    public void onChunkUnloaded() {
        leaveNet();
        super.onChunkUnloaded();
    }

    private void leaveNet() {
        // Deliberately does not go through plugin(), so that unloading a block on an
        // installation without IC2 never triggers plugin creation.
        if (plugin != null) {
            plugin.leave();
        }
    }
}
