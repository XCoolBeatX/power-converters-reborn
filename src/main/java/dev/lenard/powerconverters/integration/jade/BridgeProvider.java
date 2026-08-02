package dev.lenard.powerconverters.integration.jade;

import dev.lenard.powerconverters.PowerConverters;
import dev.lenard.powerconverters.blockentity.AbstractConverterBlockEntity;
import dev.lenard.powerconverters.blockentity.EnergyBridgeBlockEntity;
import dev.lenard.powerconverters.readout.BridgeReadout;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

/**
 * Live hover readout of the attached bridge's buffer.
 *
 * <p>The numbers have to be gathered server-side: a converter resolves its bridge by scanning
 * neighbours, and the client has no reliable view of that. So the values are packed into the
 * server data tag and simply rendered on the client.
 */
public enum BridgeProvider implements IBlockComponentProvider, IServerDataProvider<BlockEntity> {

    INSTANCE;

    private static final ResourceLocation UID = new ResourceLocation(PowerConverters.MOD_ID, "bridge");

    private static final String NBT_STORED = "PCStored";
    private static final String NBT_CAPACITY = "PCCapacity";
    private static final String NBT_ATTACHED = "PCAttached";

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public void appendServerData(CompoundTag data, ServerPlayer player, Level level, BlockEntity be, boolean detailed) {
        EnergyBridgeBlockEntity bridge = null;
        if (be instanceof EnergyBridgeBlockEntity direct) {
            bridge = direct;
        } else if (be instanceof AbstractConverterBlockEntity converter) {
            bridge = converter.getAttachedBridge();
        }

        if (bridge == null) {
            data.putBoolean(NBT_ATTACHED, false);
            return;
        }

        data.putBoolean(NBT_ATTACHED, true);
        data.putLong(NBT_STORED, bridge.getStoredBase());
        data.putLong(NBT_CAPACITY, bridge.getCapacityBase());
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (!data.contains(NBT_ATTACHED)) {
            return;
        }

        if (!data.getBoolean(NBT_ATTACHED)) {
            tooltip.add(Component.translatable("readout.powerconverters.no_bridge"));
            return;
        }

        for (Component line : BridgeReadout.describeBuffer(data.getLong(NBT_STORED), data.getLong(NBT_CAPACITY))) {
            tooltip.add(line);
        }
    }
}
