package dev.lenard.powerconverters.energy;

import dev.lenard.powerconverters.PCConfig;

/**
 * Unit conversion between the three energy systems this mod bridges.
 *
 * <p>Everything inside the mod is accounted in a single internal unit, the <b>micro-FE</b>
 * (uFE), where {@code 1 FE == 1_000_000 uFE}. Working in a scaled-up base unit rather than
 * whole FE is not premature cleverness, it is required for correctness:
 *
 * <ul>
 *   <li>BuildCraft stores MJ in <b>microjoules</b> ({@code MjAPI.MJ == 1_000_000}). At the
 *       default rate of 10 FE per MJ, a single BuildCraft internal unit is worth 0.00001 FE.
 *       If the buffer counted whole FE, every MJ transfer smaller than 100_000 microjoules
 *       would truncate to zero and the bridge would silently eat power.</li>
 *   <li>Conversely, rounding <em>up</em> anywhere would let a player loop two converters
 *       back to back and duplicate energy out of thin air.</li>
 * </ul>
 *
 * <p>With a 1e6 scale factor every conversion at the default ratios is exact integer maths,
 * and a long still holds roughly 9.2e12 FE, far beyond any realistic buffer size.
 */
public final class EnergyUnits {

    /** Internal base units per single FE. */
    public static final long UFE_PER_FE = 1_000_000L;

    /**
     * BuildCraft's internal units per one MJ. Mirrors {@code MjAPI.MJ}, duplicated here as a
     * plain constant so this class never touches BuildCraft classes and stays loadable when
     * BuildCraft is not installed. Verified against BuildCraft CE 8.0.10.
     */
    public static final long MICRO_MJ_PER_MJ = 1_000_000L;

    private EnergyUnits() {
    }

    // ---------------------------------------------------------------------
    // Forge Energy
    // ---------------------------------------------------------------------

    public static long feToBase(long fe) {
        return fe * UFE_PER_FE;
    }

    /** Truncates toward zero, so a partial FE stays in the buffer rather than being invented. */
    public static long baseToFe(long base) {
        return base / UFE_PER_FE;
    }

    // ---------------------------------------------------------------------
    // IndustrialCraft 2 (EU)
    // ---------------------------------------------------------------------

    public static long euToBase(long eu) {
        return Math.round(eu * PCConfig.fePerEu() * UFE_PER_FE);
    }

    public static long baseToEu(long base) {
        return (long) Math.floor(base / (PCConfig.fePerEu() * UFE_PER_FE));
    }

    // ---------------------------------------------------------------------
    // BuildCraft (MJ, supplied and consumed in microjoules)
    // ---------------------------------------------------------------------

    /** @param microJoules BuildCraft's native unit, i.e. MJ * 1_000_000. */
    public static long microMjToBase(long microJoules) {
        return Math.round(microJoules * PCConfig.fePerMj() * UFE_PER_FE / (double) MICRO_MJ_PER_MJ);
    }

    /** @return BuildCraft's native unit, i.e. MJ * 1_000_000. */
    public static long baseToMicroMj(long base) {
        return (long) Math.floor(base * (double) MICRO_MJ_PER_MJ / (PCConfig.fePerMj() * UFE_PER_FE));
    }
}
