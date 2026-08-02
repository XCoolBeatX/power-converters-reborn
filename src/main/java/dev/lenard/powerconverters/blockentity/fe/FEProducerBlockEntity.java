package dev.lenard.powerconverters.blockentity.fe;

import dev.lenard.powerconverters.PCBlockEntities;
import dev.lenard.powerconverters.blockentity.AbstractConverterBlockEntity;
import dev.lenard.powerconverters.blockentity.EnergyBridgeBlockEntity;
import dev.lenard.powerconverters.blockentity.ITickableConverter;
import dev.lenard.powerconverters.energy.EnergyUnits;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Emits Forge Energy drawn from the attached Energy Bridge.
 *
 * <p>Serves FE both ways round, because the ecosystem is split on which side drives a
 * transfer: it actively pushes into adjacent receivers every tick (what most machines and
 * Thermal conduits expect), and also exposes an extractable {@link IEnergyStorage} for the
 * networks that prefer to pull.
 */
public class FEProducerBlockEntity extends AbstractConverterBlockEntity
        implements IEnergyStorage, ITickableConverter {

    private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> this);

    public FEProducerBlockEntity(BlockPos pos, BlockState state) {
        super(PCBlockEntities.FE_PRODUCER.get(), pos, state);
    }

    @Override
    public void serverTick() {
        if (level == null || !hasBridge()) {
            return;
        }

        for (Direction direction : Direction.values()) {
            BlockPos neighbourPos = getBlockPos().relative(direction);
            BlockEntity neighbour = level.getBlockEntity(neighbourPos);
            if (neighbour == null || neighbour instanceof EnergyBridgeBlockEntity) {
                continue;
            }

            neighbour.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).ifPresent(target -> {
                if (!target.canReceive()) {
                    return;
                }
                int availableFe = (int) Math.min(Integer.MAX_VALUE,
                        EnergyUnits.baseToFe(bridgeExtract(Long.MAX_VALUE, true)));
                if (availableFe <= 0) {
                    return;
                }
                int accepted = target.receiveEnergy(availableFe, false);
                if (accepted > 0) {
                    bridgeExtract(EnergyUnits.feToBase(accepted), false);
                }
            });
        }
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (maxExtract <= 0) {
            return 0;
        }

        long wouldExtract = bridgeExtract(EnergyUnits.feToBase(maxExtract), true);
        int extractedFe = (int) EnergyUnits.baseToFe(wouldExtract);
        if (extractedFe <= 0) {
            return 0;
        }

        if (!simulate) {
            bridgeExtract(EnergyUnits.feToBase(extractedFe), false);
        }
        return extractedFe;
    }

    @Override
    public int getEnergyStored() {
        return (int) Math.min(Integer.MAX_VALUE, EnergyUnits.baseToFe(bridgeStored()));
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) Math.min(Integer.MAX_VALUE, EnergyUnits.baseToFe(bridgeCapacity()));
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return false;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energyCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyCap.invalidate();
    }
}
