package dev.lenard.powerconverters.energy;

import dev.lenard.powerconverters.PCConfig;

/**
 * Per-tick throughput limiter, in internal units.
 *
 * <p>Rate limiting cannot be done by simply capping each call, because a neighbouring cable or
 * energy network is free to call into a converter many times within one tick; capping per call
 * would let throughput scale with how chatty the neighbour happens to be. Instead the budget
 * is keyed on the game time, and resets by itself the first time it is touched in a new tick.
 * That needs no ticker and works identically for pushed and pulled energy.
 */
public final class TransferBudget {

    private long tickStamp = Long.MIN_VALUE;
    private long usedThisTick;

    /** @return how much may still move this tick, in internal units */
    public long remaining(long gameTime) {
        if (gameTime != tickStamp) {
            tickStamp = gameTime;
            usedThisTick = 0L;
        }
        return Math.max(0L, capacityPerTick() - usedThisTick);
    }

    /** Records actual usage. Only call once the transfer is genuinely committed. */
    public void consume(long gameTime, long amount) {
        if (gameTime != tickStamp) {
            tickStamp = gameTime;
            usedThisTick = 0L;
        }
        usedThisTick += amount;
    }

    private static long capacityPerTick() {
        return EnergyUnits.feToBase(PCConfig.maxTransferFePerTick());
    }
}
