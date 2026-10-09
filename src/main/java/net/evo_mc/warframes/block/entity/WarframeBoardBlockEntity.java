package net.evo_mc.warframes.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class WarframeBoardBlockEntity extends WarframeMultiMimicBlockEntity {

    public WarframeBoardBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.WARFRAME_BOARD_BLOCK_ENTITY.get(), pPos, pBlockState, 6);
    }

    @Override
    protected String getSlotKey(int slot) {
        return Direction.from3DDataValue(slot).getName();
    }
}