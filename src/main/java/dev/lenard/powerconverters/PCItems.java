package dev.lenard.powerconverters;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class PCItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, PowerConverters.MOD_ID);

    private static RegistryObject<Item> blockItem(String name, Supplier<? extends Block> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(PowerConverters.TAB)));
    }

    public static final RegistryObject<Item> ENERGY_BRIDGE = blockItem("energy_bridge", PCBlocks.ENERGY_BRIDGE);
    public static final RegistryObject<Item> FE_CONSUMER = blockItem("fe_consumer", PCBlocks.FE_CONSUMER);
    public static final RegistryObject<Item> FE_PRODUCER = blockItem("fe_producer", PCBlocks.FE_PRODUCER);
    public static final RegistryObject<Item> EU_CONSUMER = blockItem("eu_consumer", PCBlocks.EU_CONSUMER);
    public static final RegistryObject<Item> EU_PRODUCER = blockItem("eu_producer", PCBlocks.EU_PRODUCER);
    public static final RegistryObject<Item> MJ_CONSUMER = blockItem("mj_consumer", PCBlocks.MJ_CONSUMER);
    public static final RegistryObject<Item> MJ_PRODUCER = blockItem("mj_producer", PCBlocks.MJ_PRODUCER);

    private PCItems() {
    }
}
