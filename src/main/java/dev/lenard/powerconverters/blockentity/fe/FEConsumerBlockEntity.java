package dev.lenard.powerconverters.blockentity.fe;

import dev.lenard.powerconverters.PCBlockEntities;
import dev.lenard.powerconverters.blockentity.AbstractConverterBlockEntity;
import dev.lenard.powerconverters.energy.EnergyUnits;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Accepts Forge Energy from any adjacent FE source (Thermal conduits, Mekanism cables, AE2,
 * Flux Networks, ...) and deposits it into the attached Energy Bridge.
 */
public class FEConsumerBlockEntity extends AbstractConverterBlockEntity implements IEnergyStorage {

    private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> this);

    public FEConsumerBlockEntity(BlockPos pos, BlockState state) {
        super(PCBlockEntities.FE_CONSUMER.get(), pos, state);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (maxReceive <= 0) {
            return 0;
        }

        // Ask first, then commit only a whole number of FE. Truncating here rather than
        // rounding is what stops a back-to-back converter loop from minting energy.
        long wouldAccept = bridgeInsert(EnergyUnits.feToBase(maxReceive), true);
        int acceptedFe = (int) EnergyUnits.baseToFe(wouldAccept);
        if (acceptedFe <= 0) {
            return 0;
        }

        if (!simulate) {
            bridgeInsert(EnergyUnits.feToBase(acceptedFe), false);
        }
        return acceptedFe;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
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
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
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
