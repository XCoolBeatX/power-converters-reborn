package dev.lenard.powerconverters.integration.ic2;

import dev.lenard.powerconverters.PCConfig;
import dev.lenard.powerconverters.blockentity.AbstractConverterBlockEntity;
import dev.lenard.powerconverters.energy.EnergyUnits;
import dev.lenard.powerconverters.integration.LifecyclePlugin;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergySource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

/**
 * Supplies EU to IC2's EnergyNet out of the bridge.
 *
 * <p>EU is pull-based: the EnergyNet asks {@link #getProvidedEnergy()} and then calls
 * {@link #consumeEnergy(int)} for what it actually took, so no ticker is needed.
 */
final class EuSourcePlugin implements IEnergySource, LifecyclePlugin {

    private final AbstractConverterBlockEntity owner;
    private boolean joined;

    EuSourcePlugin(AbstractConverterBlockEntity owner) {
        this.owner = owner;
    }

    @Override
    public void join() {
        if (!joined && owner.getLevel() != null && !owner.getLevel().isClientSide) {
            EnergyNet.INSTANCE.addTile(this);
            joined = true;
        }
    }

    @Override
    public void leave() {
        if (joined) {
            EnergyNet.INSTANCE.removeTile(this);
            joined = false;
        }
    }

    @Override
    public Level getWorldObj() {
        return owner.getLevel();
    }

    @Override
    public BlockPos getPosition() {
        return owner.getBlockPos();
    }

    @Override
    public boolean canEmitEnergy(IEnergyAcceptor acceptor, Direction side) {
        return true;
    }

    @Override
    public int getSourceTier() {
        return PCConfig.euTier();
    }

    /** One packet's worth at our configured tier, which is what the net expects here. */
    @Override
    public int getMaxEnergyOutput() {
        return EnergyNet.INSTANCE.getPowerFromTier(PCConfig.euTier());
    }

    @Override
    public int getProvidedEnergy() {
        long available = owner.bridgeExtract(Long.MAX_VALUE, true);
        long availableEu = EnergyUnits.baseToEu(available);
        // Never offer more than a single packet at our tier, or the net will split it oddly.
        return (int) Math.min(getMaxEnergyOutput(), Math.min(Integer.MAX_VALUE, availableEu));
    }

    @Override
    public void consumeEnergy(int amount) {
        if (amount > 0) {
            owner.bridgeExtract(EnergyUnits.euToBase(amount), false);
        }
    }
}
