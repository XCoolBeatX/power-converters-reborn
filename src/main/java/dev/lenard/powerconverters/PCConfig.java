package dev.lenard.powerconverters;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

/**
 * Server-side configuration. Conversion ratios have to match between client and server,
 * so this is a COMMON config rather than a client one.
 */
@Mod.EventBusSubscriber(modid = PowerConverters.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class PCConfig {

    public static final ForgeConfigSpec SPEC;

    private static final ForgeConfigSpec.DoubleValue FE_PER_EU;
    private static final ForgeConfigSpec.DoubleValue FE_PER_MJ;
    private static final ForgeConfigSpec.IntValue BRIDGE_CAPACITY_FE;
    private static final ForgeConfigSpec.IntValue MAX_TRANSFER_FE_PER_TICK;
    private static final ForgeConfigSpec.IntValue EU_TIER;

    // Cached on config load. Read every tick by the converters, so we avoid going through
    // the config spec (which synchronizes) on the hot path.
    private static double fePerEu = 4.0D;
    private static double fePerMj = 10.0D;
    private static int bridgeCapacityFe = 1_000_000;
    private static int maxTransferFePerTick = 20_000;
    private static int euTier = 4;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Conversion ratios. All values are expressed relative to Forge Energy (FE),",
                        "which acts as the neutral hub unit, so EU <-> MJ falls out of the two below.",
                        "Defaults match the long-standing community standard rates.")
               .push("ratios");

        FE_PER_EU = builder
                .comment("How many FE one IndustrialCraft EU is worth. Default 4.0 (1 EU = 4 FE).")
                .defineInRange("fePerEu", 4.0D, 0.001D, 1000.0D);

        FE_PER_MJ = builder
                .comment("How many FE one BuildCraft MJ is worth. Default 10.0 (1 MJ = 10 FE).",
                         "Note BuildCraft stores MJ internally as microjoules; the mod handles that for you.")
                .defineInRange("fePerMj", 10.0D, 0.001D, 1000.0D);

        builder.pop();

        builder.comment("Throughput and storage tuning.").push("throughput");

        BRIDGE_CAPACITY_FE = builder
                .comment("Internal buffer of a single Energy Bridge, in FE.")
                .defineInRange("bridgeCapacityFe", 1_000_000, 1_000, Integer.MAX_VALUE);

        MAX_TRANSFER_FE_PER_TICK = builder
                .comment("Maximum FE-equivalent a single consumer or producer moves per tick.",
                         "This is the throughput limit of one converter block; place more to scale up.")
                .defineInRange("maxTransferFePerTick", 20_000, 1, Integer.MAX_VALUE);

        EU_TIER = builder
                .comment("IC2 voltage tier used by the EU converters.",
                         "1=LV(32) 2=MV(128) 3=HV(512) 4=EV(2048) 5=IV(8192).",
                         "The EU Consumer accepts up to this tier without exploding, and the EU",
                         "Producer emits at this tier, so cables below it will burn up.",
                         "Capped at 13: IC2's EnergyNet rejects any tile registering above that.")
                .defineInRange("euTier", 4, 1, 13);

        builder.pop();

        SPEC = builder.build();
    }

    private PCConfig() {
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading event) {
        bake();
    }

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading event) {
        bake();
    }

    private static void bake() {
        fePerEu = FE_PER_EU.get();
        fePerMj = FE_PER_MJ.get();
        bridgeCapacityFe = BRIDGE_CAPACITY_FE.get();
        maxTransferFePerTick = MAX_TRANSFER_FE_PER_TICK.get();
        euTier = EU_TIER.get();
    }

    public static double fePerEu() {
        return fePerEu;
    }

    public static double fePerMj() {
        return fePerMj;
    }

    public static int bridgeCapacityFe() {
        return bridgeCapacityFe;
    }

    public static int maxTransferFePerTick() {
        return maxTransferFePerTick;
    }

    public static int euTier() {
        return euTier;
    }
}
