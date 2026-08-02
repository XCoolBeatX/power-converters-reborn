package dev.lenard.powerconverters.blockentity.ic2;

import dev.lenard.powerconverters.PCBlockEntities;
import dev.lenard.powerconverters.integration.LifecyclePlugin;
import dev.lenard.powerconverters.integration.ic2.IC2Plugins;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/** Accepts EU from IC2 cables and stores it in the Energy Bridge. */
public class EUConsumerBlockEntity extends AbstractEuBlockEntity {

    public EUConsumerBlockEntity(BlockPos pos, BlockState state) {
        super(PCBlockEntities.EU_CONSUMER.get(), pos, state);
    }

    @Override
    protected LifecyclePlugin createPlugin() {
        return IC2Plugins.createSink(this);
    }
}
