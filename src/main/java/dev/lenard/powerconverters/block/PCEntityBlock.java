package dev.lenard.powerconverters.block;

import dev.lenard.powerconverters.blockentity.AbstractConverterBlockEntity;
import dev.lenard.powerconverters.blockentity.ITickableConverter;
import dev.lenard.powerconverters.readout.BridgeReadout;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Generic block shell for every block in this mod. All of them are plain full cubes whose
 * behaviour lives entirely in their block entity, so a single class covers the lot.
 */
public class PCEntityBlock extends Block implements EntityBlock {

    private final Supplier<BlockEntityType<? extends BlockEntity>> typeSupplier;

    public PCEntityBlock(Properties properties, Supplier<BlockEntityType<? extends BlockEntity>> typeSupplier) {
        super(properties);
        this.typeSupplier = typeSupplier;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return typeSupplier.get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        return (lvl, pos, st, be) -> {
            if (be instanceof ITickableConverter tickable) {
                tickable.serverTick();
            }
        };
    }

    /**
     * Right-clicking any block in the mod reports the attached bridge's contents.
     *
     * <p>Sent to the action bar rather than chat: the expected use is clicking repeatedly to
     * watch a buffer fill, and each new reading replaces the last instead of flooding chat.
     */
    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        // Empty hand only. Otherwise consuming the click would stop the player placing a
        // block or using a wrench against one of ours, which is far more annoying than
        // having to swap to an empty slot to read the buffer.
        if (!player.getItemInHand(hand).isEmpty()) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        // The action bar renders a single line, so fold the readout into one component
        // rather than letting each line overwrite the previous.
        MutableComponent message = Component.empty();
        boolean first = true;
        for (Component line : BridgeReadout.describe(level.getBlockEntity(pos))) {
            if (!first) {
                message.append(Component.literal("  "));
            }
            message.append(line);
            first = false;
        }

        player.displayClientMessage(message, true);
        return InteractionResult.CONSUME;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        // A bridge may have been placed or broken next to us; drop the cached lookup.
        if (level.getBlockEntity(pos) instanceof AbstractConverterBlockEntity converter) {
            converter.invalidateBridgeCache();
        }
    }
}
