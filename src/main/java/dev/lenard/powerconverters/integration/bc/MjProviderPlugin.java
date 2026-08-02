package dev.lenard.powerconverters.integration.bc;

import buildcraft.api.mj.IMjConnector;
import buildcraft.api.mj.IMjPassiveProvider;
import buildcraft.api.mj.IMjReceiver;
import buildcraft.api.mj.MjAPI;
import buildcraft.api.mj.MjCapabilityHelper;
import dev.lenard.powerconverters.blockentity.AbstractConverterBlockEntity;
import dev.lenard.powerconverters.energy.EnergyUnits;
import dev.lenard.powerconverters.integration.CapabilityPlugin;
import dev.lenard.powerconverters.integration.PushPlugin;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Supplies BuildCraft MJ out of the Energy Bridge.
 *
 * <p>Serves MJ both ways round. Kinesis pipes pull, via {@link #extractPower}, but machines
 * such as the Quarry never pull — they expect an engine to push into them — so this also
 * pushes into adjacent receivers each tick.
 */
final class MjProviderPlugin implements IMjPassiveProvider, CapabilityPlugin, PushPlugin {

    private final AbstractConverterBlockEntity owner;
    private final MjCapabilityHelper caps;

    MjProviderPlugin(AbstractConverterBlockEntity owner) {
        this.owner = owner;
        this.caps = new MjCapabilityHelper(this);
    }

    @Override
    public boolean canConnect(IMjConnector other) {
        return true;
    }

    /**
     * @param min the smallest useful amount; returning less than this is treated as returning
     *     nothing, so we must not part-fill a request
     * @return microjoules actually provided
     */
    @Override
    public long extractPower(long min, long max, boolean simulate) {
        if (max <= 0) {
            return 0L;
        }

        long available = owner.bridgeExtract(EnergyUnits.microMjToBase(max), true);
        long availableMicro = EnergyUnits.baseToMicroMj(available);
        if (availableMicro <= 0 || availableMicro < min) {
            return 0L;
        }

        if (!simulate) {
            owner.bridgeExtract(EnergyUnits.microMjToBase(availableMicro), false);
        }
        return availableMicro;
    }

    // ---------------------------------------------------------------------
    // Push side, so we behave like an engine rather than needing a wooden kinesis pipe
    // ---------------------------------------------------------------------

    @Override
    public void push() {
        Level level = owner.getLevel();
        if (level == null || level.isClientSide) {
            return;
        }

        for (Direction direction : Direction.values()) {
            BlockPos neighbourPos = owner.getBlockPos().relative(direction);
            BlockEntity neighbour = level.getBlockEntity(neighbourPos);
            if (neighbour == null) {
                continue;
            }

            neighbour.getCapability(MjAPI.CAP_RECEIVER, direction.getOpposite()).ifPresent(receiver -> {
                if (!receiver.canReceive()) {
                    return;
                }

                long wanted = Math.min(receiver.getPowerRequested(),
                        EnergyUnits.baseToMicroMj(owner.bridgeExtract(Long.MAX_VALUE, true)));
                if (wanted <= 0) {
                    return;
                }

                // receivePower returns the unused remainder, so what it kept is the difference.
                long unused = receiver.receivePower(wanted, IFluidHandler.FluidAction.EXECUTE);
                long consumed = wanted - unused;
                if (consumed > 0) {
                    owner.bridgeExtract(EnergyUnits.microMjToBase(consumed), false);
                }
            });
        }
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return caps.getCapability(cap, side);
    }
}
