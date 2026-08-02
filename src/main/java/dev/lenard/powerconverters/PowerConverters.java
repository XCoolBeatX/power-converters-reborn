package dev.lenard.powerconverters;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * Power Converters Reborn.
 *
 * <p>A ground-up reimplementation for Minecraft 1.19.2 of the classic Power Converters mod,
 * which stopped at 1.7.10. Energy is bridged between IndustrialCraft 2 (EU), BuildCraft (MJ)
 * and Forge Energy (FE) through a shared buffer block.
 */
@Mod(PowerConverters.MOD_ID)
public class PowerConverters {

    public static final String MOD_ID = "powerconverters";

    public static final Logger LOGGER = LogUtils.getLogger();

    /** IC2 Classic's mod id, per its mods.toml. */
    public static final String IC2_MOD_ID = "ic2";

    /**
     * BuildCraft CE is split into several modules; the MJ API lives in the library module,
     * which every other BuildCraft module depends on, so this is the right thing to test for.
     */
    public static final String BUILDCRAFT_MOD_ID = "buildcraftlib";

    public static final CreativeModeTab TAB = new CreativeModeTab(MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(PCBlocks.ENERGY_BRIDGE.get());
        }
    };

    public PowerConverters() {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();

        PCBlocks.BLOCKS.register(modBus);
        PCItems.ITEMS.register(modBus);
        PCBlockEntities.BLOCK_ENTITIES.register(modBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, PCConfig.SPEC);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public static boolean isIc2Loaded() {
        return ModList.get().isLoaded(IC2_MOD_ID);
    }

    public static boolean isBuildCraftLoaded() {
        return ModList.get().isLoaded(BUILDCRAFT_MOD_ID);
    }
}
