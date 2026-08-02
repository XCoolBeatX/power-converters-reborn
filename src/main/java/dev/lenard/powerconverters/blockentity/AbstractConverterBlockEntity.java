package dev.lenard.powerconverters.blockentity;

import dev.lenard.powerconverters.energy.TransferBudget;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Shared behaviour for every consumer and producer: locate the Energy Bridge this block is
 * attached to, and enforce the per-tick throughput limit.
 *
 * <p>A converter attaches to a bridge simply by being placed against any of its six faces,
 * which keeps the original mod's build-it-however-you-like feel. The lookup result is cached
 * and invalidated on neighbour changes so we are not doing six block entity lookups per tick.
 */
public abstract class AbstractConverterBlockEntity extends BlockEntity {

    private final TransferBudget budget = new TransferBudget();

    @Nullable
    private BlockPos cachedBridgePos;
    private boolean cacheValid;

    protected AbstractConverterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /** Called by the block when a neighbouring block changes, so a moved bridge is picked up. */
    public void invalidateBridgeCache() {
        cacheValid = false;
        cachedBridgePos = null;
    }

    @Nullable
    protected EnergyBridgeBlockEntity getBridge() {
        Level level = getLevel();
        if (level == null || level.isClientSide) {
            return null;
        }

        if (cacheValid && cachedBridgePos != null) {
            BlockEntity cached = level.getBlockEntity(cachedBridgePos);
            if (cached instanceof EnergyBridgeBlockEntity bridge && !bridge.isRemoved()) {
                return bridge;
            }
            // The cached bridge is gone; fall through and rescan.
            invalidateBridgeCache();
        }

        for (Direction direction : Direction.values()) {
            BlockPos neighbour = getBlockPos().relative(direction);
            if (level.getBlockEntity(neighbour) instanceof EnergyBridgeBlockEntity bridge) {
                cachedBridgePos = neighbour;
                cacheValid = true;
                return bridge;
            }
        }

        // Remember that there is no bridge, so an unattached converter is cheap to tick.
        cacheValid = true;
        cachedBridgePos = null;
        return null;
    }

    // ---------------------------------------------------------------------
    // Helpers used by the per-system integrations. All amounts are internal units.
    // ---------------------------------------------------------------------

    /** @return units actually accepted by the bridge, already clamped to the tick budget */
    public long bridgeInsert(long base, boolean simulate) {
        EnergyBridgeBlockEntity bridge = getBridge();
        if (bridge == null || base <= 0 || level == null) {
            return 0L;
        }
        long allowed = Math.min(base, budget.remaining(level.getGameTime()));
        if (allowed <= 0) {
            return 0L;
        }
        long accepted = bridge.insertBase(allowed, simulate);
        if (!simulate) {
            budget.consume(level.getGameTime(), accepted);
        }
        return accepted;
    }

    /** @return units actually drawn from the bridge, already clamped to the tick budget */
    public long bridgeExtract(long base, boolean simulate) {
        EnergyBridgeBlockEntity bridge = getBridge();
        if (bridge == null || base <= 0 || level == null) {
            return 0L;
        }
        long allowed = Math.min(base, budget.remaining(level.getGameTime()));
        if (allowed <= 0) {
            return 0L;
        }
        long extracted = bridge.extractBase(allowed, simulate);
        if (!simulate) {
            budget.consume(level.getGameTime(), extracted);
        }
        return extracted;
    }

    /** Stored contents of the attached bridge, or 0 when unattached. */
    public long bridgeStored() {
        EnergyBridgeBlockEntity bridge = getBridge();
        return bridge == null ? 0L : bridge.getStoredBase();
    }

    /** Capacity of the attached bridge, or 0 when unattached. */
    public long bridgeCapacity() {
        EnergyBridgeBlockEntity bridge = getBridge();
        return bridge == null ? 0L : bridge.getCapacityBase();
    }

    public boolean hasBridge() {
        return getBridge() != null;
    }

    /** Public accessor for the readout, which needs to reach the bridge from a converter. */
    @Nullable
    public EnergyBridgeBlockEntity getAttachedBridge() {
        return getBridge();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        invalidateBridgeCache();
    }
}
