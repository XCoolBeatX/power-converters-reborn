package dev.lenard.powerconverters;

import dev.lenard.powerconverters.blockentity.EnergyBridgeBlockEntity;
import dev.lenard.powerconverters.blockentity.bc.MJConsumerBlockEntity;
import dev.lenard.powerconverters.blockentity.bc.MJProducerBlockEntity;
import dev.lenard.powerconverters.blockentity.fe.FEConsumerBlockEntity;
import dev.lenard.powerconverters.blockentity.fe.FEProducerBlockEntity;
import dev.lenard.powerconverters.blockentity.ic2.EUConsumerBlockEntity;
import dev.lenard.powerconverters.blockentity.ic2.EUProducerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Block entity types.
 *
 * <p>Every type is registered unconditionally, even the IC2 and BuildCraft ones. That is safe
 * because none of these classes reference another mod's types directly; the integration lives
 * behind lazily created delegates, so nothing from IC2 or BuildCraft is class-loaded unless
 * that mod is actually present.
 */
public final class PCBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, PowerConverters.MOD_ID);

    public static final RegistryObject<BlockEntityType<EnergyBridgeBlockEntity>> ENERGY_BRIDGE =
            BLOCK_ENTITIES.register("energy_bridge", () -> BlockEntityType.Builder
                    .of(EnergyBridgeBlockEntity::new, PCBlocks.ENERGY_BRIDGE.get())
                    .build(null));

    public static final RegistryObject<BlockEntityType<FEConsumerBlockEntity>> FE_CONSUMER =
            BLOCK_ENTITIES.register("fe_consumer", () -> BlockEntityType.Builder
                    .of(FEConsumerBlockEntity::new, PCBlocks.FE_CONSUMER.get())
                    .build(null));

    public static final RegistryObject<BlockEntityType<FEProducerBlockEntity>> FE_PRODUCER =
            BLOCK_ENTITIES.register("fe_producer", () -> BlockEntityType.Builder
                    .of(FEProducerBlockEntity::new, PCBlocks.FE_PRODUCER.get())
                    .build(null));

    public static final RegistryObject<BlockEntityType<EUConsumerBlockEntity>> EU_CONSUMER =
            BLOCK_ENTITIES.register("eu_consumer", () -> BlockEntityType.Builder
                    .of(EUConsumerBlockEntity::new, PCBlocks.EU_CONSUMER.get())
                    .build(null));

    public static final RegistryObject<BlockEntityType<EUProducerBlockEntity>> EU_PRODUCER =
            BLOCK_ENTITIES.register("eu_producer", () -> BlockEntityType.Builder
                    .of(EUProducerBlockEntity::new, PCBlocks.EU_PRODUCER.get())
                    .build(null));

    public static final RegistryObject<BlockEntityType<MJConsumerBlockEntity>> MJ_CONSUMER =
            BLOCK_ENTITIES.register("mj_consumer", () -> BlockEntityType.Builder
                    .of(MJConsumerBlockEntity::new, PCBlocks.MJ_CONSUMER.get())
                    .build(null));

    public static final RegistryObject<BlockEntityType<MJProducerBlockEntity>> MJ_PRODUCER =
            BLOCK_ENTITIES.register("mj_producer", () -> BlockEntityType.Builder
                    .of(MJProducerBlockEntity::new, PCBlocks.MJ_PRODUCER.get())
                    .build(null));

    private PCBlockEntities() {
    }
}
