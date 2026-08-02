package dev.lenard.powerconverters.blockentity.ic2;

import dev.lenard.powerconverters.PCBlockEntities;
import dev.lenard.powerconverters.integration.LifecyclePlugin;
import dev.lenard.powerconverters.integration.ic2.IC2Plugins;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/** Supplies EU, drawn from the Energy Bridge, to IC2 cables and machines. */
public class EUProducerBlockEntity extends AbstractEuBlockEntity {

    public EUProducerBlockEntity(BlockPos pos, BlockState state) {
        super(PCBlockEntities.EU_PRODUCER.get(), pos, state);
    }

    @Override
    protected LifecyclePlugin createPlugin() {
        return IC2Plugins.createSource(this);
    }
}
