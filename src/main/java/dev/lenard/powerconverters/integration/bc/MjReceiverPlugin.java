package dev.lenard.powerconverters.integration.bc;

import buildcraft.api.mj.IMjConnector;
import buildcraft.api.mj.IMjReceiver;
import buildcraft.api.mj.MjCapabilityHelper;
import dev.lenard.powerconverters.blockentity.AbstractConverterBlockEntity;
import dev.lenard.powerconverters.energy.EnergyUnits;
import dev.lenard.powerconverters.integration.CapabilityPlugin;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Receives BuildCraft MJ and feeds it into the Energy Bridge.
 *
 * <p>All quantities crossing this boundary are BuildCraft's internal microjoules, where
 * {@code MjAPI.MJ == 1_000_000} equals one MJ.
 */
final class MjReceiverPlugin implements IMjReceiver, CapabilityPlugin {

    private final AbstractConverterBlockEntity owner;
    private final MjCapabilityHelper caps;

    MjReceiverPlugin(AbstractConverterBlockEntity owner) {
        this.owner = owner;
        // Let BuildCraft's own helper answer the connector/receiver capabilities, so we
        // automatically satisfy whatever set of caps its pipes probe for.
        this.caps = new MjCapabilityHelper(this);
    }

    // ---------------------------------------------------------------------
    // IMjReceiver
    // ---------------------------------------------------------------------

    @Override
    public boolean canConnect(IMjConnector other) {
        return true;
    }

    @Override
    public long getPowerRequested() {
        return EnergyUnits.baseToMicroMj(owner.bridgeInsert(Long.MAX_VALUE, true));
    }

    /**
     * @return the microjoules that could <b>not</b> be accepted. BuildCraft's convention is to
     *     return the unused remainder, not the amount taken; verified against
     *     {@code MjBattery.addPowerChecking}, which returns its full input when full.
     */
    @Override
    public long receivePower(long microJoules, IFluidHandler.FluidAction action) {
        if (microJoules <= 0) {
            return 0L;
        }

        long wouldAccept = owner.bridgeInsert(EnergyUnits.microMjToBase(microJoules), true);
        long acceptedMicro = EnergyUnits.baseToMicroMj(wouldAccept);
        if (acceptedMicro <= 0) {
            return microJoules;
        }

        if (action == IFluidHandler.FluidAction.EXECUTE) {
            owner.bridgeInsert(EnergyUnits.microMjToBase(acceptedMicro), false);
        }
        return microJoules - acceptedMicro;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    // ---------------------------------------------------------------------
    // CapabilityPlugin
    // ---------------------------------------------------------------------

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return caps.getCapability(cap, side);
    }
}
