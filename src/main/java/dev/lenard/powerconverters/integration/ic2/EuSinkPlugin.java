package dev.lenard.powerconverters.integration.ic2;

import dev.lenard.powerconverters.PCConfig;
import dev.lenard.powerconverters.blockentity.AbstractConverterBlockEntity;
import dev.lenard.powerconverters.energy.EnergyUnits;
import dev.lenard.powerconverters.integration.LifecyclePlugin;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

/**
 * Accepts EU from IC2's EnergyNet on behalf of a block entity, and feeds it to the bridge.
 *
 * <p>This is a standalone delegate rather than being implemented on the block entity itself,
 * so that no IC2 type is ever resolved on an installation without IC2. That is safe because
 * IC2's EnergyNet only ever talks to registered tiles through these interfaces, verified by
 * disassembling {@code EnergyNetGlobal}/{@code EnergyNetLocal}: they locate a tile purely via
 * {@code ILocation} and never cast it to a BlockEntity.
 */
final class EuSinkPlugin implements IEnergySink, LifecyclePlugin {

    private final AbstractConverterBlockEntity owner;
    private boolean joined;

    EuSinkPlugin(AbstractConverterBlockEntity owner) {
        this.owner = owner;
    }

    // ---------------------------------------------------------------------
    // LifecyclePlugin
    // ---------------------------------------------------------------------

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

    // ---------------------------------------------------------------------
    // ILocation
    // ---------------------------------------------------------------------

    @Override
    public Level getWorldObj() {
        return owner.getLevel();
    }

    @Override
    public BlockPos getPosition() {
        return owner.getBlockPos();
    }

    // ---------------------------------------------------------------------
    // IEnergySink
    // ---------------------------------------------------------------------

    @Override
    public boolean canAcceptEnergy(IEnergyEmitter emitter, Direction side) {
        return true;
    }

    @Override
    public int getSinkTier() {
        return PCConfig.euTier();
    }

    @Override
    public int getRequestedEnergy() {
        return (int) Math.min(Integer.MAX_VALUE, EnergyUnits.baseToEu(owner.bridgeInsert(Long.MAX_VALUE, true)));
    }

    /**
     * @return the EU that could <b>not</b> be accepted. IC2's convention is to return the
     *     remainder, verified against {@code LuminatorTileEntity.acceptEnergy}, which ends in
     *     {@code return amount - accepted}.
     */
    @Override
    public int acceptEnergy(Direction side, int amount, int voltage) {
        if (amount <= 0) {
            return 0;
        }

        long wouldAccept = owner.bridgeInsert(EnergyUnits.euToBase(amount), true);
        int acceptedEu = (int) Math.min(Integer.MAX_VALUE, EnergyUnits.baseToEu(wouldAccept));
        if (acceptedEu <= 0) {
            return amount;
        }

        owner.bridgeInsert(EnergyUnits.euToBase(acceptedEu), false);
        return amount - acceptedEu;
    }
}
