package dev.lenard.powerconverters;

import dev.lenard.powerconverters.block.PCEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class PCBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, PowerConverters.MOD_ID);

    private static BlockBehaviour.Properties machineProperties() {
        return BlockBehaviour.Properties.of(Material.METAL)
                .strength(3.0F, 6.0F)
                .requiresCorrectToolForDrops()
                .sound(SoundType.METAL);
    }

    public static final RegistryObject<Block> ENERGY_BRIDGE = BLOCKS.register("energy_bridge",
            () -> new PCEntityBlock(machineProperties(), () -> PCBlockEntities.ENERGY_BRIDGE.get()));

    public static final RegistryObject<Block> FE_CONSUMER = BLOCKS.register("fe_consumer",
            () -> new PCEntityBlock(machineProperties(), () -> PCBlockEntities.FE_CONSUMER.get()));

    public static final RegistryObject<Block> FE_PRODUCER = BLOCKS.register("fe_producer",
            () -> new PCEntityBlock(machineProperties(), () -> PCBlockEntities.FE_PRODUCER.get()));

    public static final RegistryObject<Block> EU_CONSUMER = BLOCKS.register("eu_consumer",
            () -> new PCEntityBlock(machineProperties(), () -> PCBlockEntities.EU_CONSUMER.get()));

    public static final RegistryObject<Block> EU_PRODUCER = BLOCKS.register("eu_producer",
            () -> new PCEntityBlock(machineProperties(), () -> PCBlockEntities.EU_PRODUCER.get()));

    public static final RegistryObject<Block> MJ_CONSUMER = BLOCKS.register("mj_consumer",
            () -> new PCEntityBlock(machineProperties(), () -> PCBlockEntities.MJ_CONSUMER.get()));

    public static final RegistryObject<Block> MJ_PRODUCER = BLOCKS.register("mj_producer",
            () -> new PCEntityBlock(machineProperties(), () -> PCBlockEntities.MJ_PRODUCER.get()));

    private PCBlocks() {
    }
}
