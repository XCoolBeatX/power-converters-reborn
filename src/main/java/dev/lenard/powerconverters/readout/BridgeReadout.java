package dev.lenard.powerconverters.readout;

import dev.lenard.powerconverters.blockentity.AbstractConverterBlockEntity;
import dev.lenard.powerconverters.blockentity.EnergyBridgeBlockEntity;
import dev.lenard.powerconverters.energy.EnergyUnits;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Formats an Energy Bridge's contents for display.
 *
 * <p>Shared by the right-click readout and the Jade overlay so the two can never drift apart.
 * The buffer is shown in all three currencies at once, since the whole point of the block is
 * that those are the same energy viewed three ways.
 */
public final class BridgeReadout {

    private static final DecimalFormat WHOLE =
            new DecimalFormat("#,##0", DecimalFormatSymbols.getInstance(Locale.ROOT));
    private static final DecimalFormat DECIMAL =
            new DecimalFormat("#,##0.##", DecimalFormatSymbols.getInstance(Locale.ROOT));

    private BridgeReadout() {
    }

    /**
     * @param be either an Energy Bridge or a converter attached to one
     * @return the lines to display, never empty
     */
    public static List<Component> describe(@Nullable BlockEntity be) {
        List<Component> lines = new ArrayList<>();

        EnergyBridgeBlockEntity bridge = resolveBridge(be);
        if (bridge == null) {
            lines.add(Component.translatable("readout.powerconverters.no_bridge")
                    .withStyle(ChatFormatting.RED));
            return lines;
        }

        lines.addAll(describeBuffer(bridge.getStoredBase(), bridge.getCapacityBase()));
        return lines;
    }

    /** Formats a buffer directly, for callers that already have the raw numbers (Jade). */
    public static List<Component> describeBuffer(long storedBase, long capacityBase) {
        List<Component> lines = new ArrayList<>();

        long fe = EnergyUnits.baseToFe(storedBase);
        long capFe = EnergyUnits.baseToFe(capacityBase);
        double percent = capacityBase <= 0 ? 0.0D : (storedBase * 100.0D) / capacityBase;

        lines.add(Component.translatable("readout.powerconverters.buffer",
                        WHOLE.format(fe), WHOLE.format(capFe), DECIMAL.format(percent))
                .withStyle(ChatFormatting.AQUA));

        lines.add(Component.translatable("readout.powerconverters.equivalent",
                        WHOLE.format(EnergyUnits.baseToEu(storedBase)),
                        DECIMAL.format(EnergyUnits.baseToMicroMj(storedBase)
                                / (double) EnergyUnits.MICRO_MJ_PER_MJ))
                .withStyle(ChatFormatting.GRAY));

        return lines;
    }

    @Nullable
    private static EnergyBridgeBlockEntity resolveBridge(@Nullable BlockEntity be) {
        if (be instanceof EnergyBridgeBlockEntity bridge) {
            return bridge;
        }
        if (be instanceof AbstractConverterBlockEntity converter) {
            return converter.getAttachedBridge();
        }
        return null;
    }
}
