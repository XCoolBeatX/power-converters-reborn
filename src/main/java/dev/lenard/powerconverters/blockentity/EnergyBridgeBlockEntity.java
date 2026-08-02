package dev.lenard.powerconverters.blockentity;

import dev.lenard.powerconverters.PCBlockEntities;
import dev.lenard.powerconverters.PCConfig;
import dev.lenard.powerconverters.energy.EnergyUnits;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The heart of the multiblock. Holds one shared buffer, denominated in the neutral internal
 * unit, that every attached consumer feeds and every attached producer draws from.
 *
 * <p>The bridge itself knows nothing about EU, MJ or FE. That separation is what lets a new
 * energy system be supported by adding a consumer/producer pair and nothing else.
 */
public class EnergyBridgeBlockEntity extends BlockEntity {

    private static final String NBT_STORED = "StoredBase";

    private long storedBase;

    public EnergyBridgeBlockEntity(BlockPos pos, BlockState state) {
        super(PCBlockEntities.ENERGY_BRIDGE.get(), pos, state);
    }

    private long capacityBase() {
        return EnergyUnits.feToBase(PCConfig.bridgeCapacityFe());
    }

    /**
     * @param simulate when true, report what would happen without changing anything
     * @return how much was (or would be) accepted, in internal units
     */
    public long insertBase(long amount, boolean simulate) {
        if (amount <= 0) {
            return 0L;
        }
        long accepted = Math.min(amount, capacityBase() - storedBase);
        if (accepted <= 0) {
            return 0L;
        }
        if (!simulate) {
            storedBase += accepted;
            setChanged();
        }
        return accepted;
    }

    /**
     * @param simulate when true, report what would happen without changing anything
     * @return how much was (or would be) removed, in internal units
     */
    public long extractBase(long amount, boolean simulate) {
        if (amount <= 0) {
            return 0L;
        }
        long extracted = Math.min(amount, storedBase);
        if (extracted <= 0) {
            return 0L;
        }
        if (!simulate) {
            storedBase -= extracted;
            setChanged();
        }
        return extracted;
    }

    public long getStoredBase() {
        return storedBase;
    }

    public long getCapacityBase() {
        return capacityBase();
    }

    /** Convenience for GUIs, Jade/TOP and the debug readout. */
    public long getStoredFe() {
        return EnergyUnits.baseToFe(storedBase);
    }

    // ---------------------------------------------------------------------
    // Persistence
    // ---------------------------------------------------------------------

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        storedBase = tag.getLong(NBT_STORED);
        // A shrunken config must not leave the buffer permanently over capacity.
        storedBase = Math.min(storedBase, capacityBase());
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putLong(NBT_STORED, storedBase);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
