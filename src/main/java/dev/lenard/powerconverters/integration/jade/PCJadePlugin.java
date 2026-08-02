package dev.lenard.powerconverters.integration.jade;

import dev.lenard.powerconverters.block.PCEntityBlock;
import dev.lenard.powerconverters.blockentity.AbstractConverterBlockEntity;
import dev.lenard.powerconverters.blockentity.EnergyBridgeBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

/**
 * Jade integration.
 *
 * <p>Jade discovers this class by scanning for {@link WailaPlugin}, so nothing here is loaded
 * when Jade is absent; there is no need for a ModList guard.
 */
@WailaPlugin
public class PCJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        // The bridge itself, and any converter, which reports the bridge it is attached to.
        registration.registerBlockDataProvider(BridgeProvider.INSTANCE, EnergyBridgeBlockEntity.class);
        registration.registerBlockDataProvider(BridgeProvider.INSTANCE, AbstractConverterBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        // Every block in this mod shares one class, so a single registration covers them all.
        registration.registerBlockComponent(BridgeProvider.INSTANCE, PCEntityBlock.class);
    }
}
