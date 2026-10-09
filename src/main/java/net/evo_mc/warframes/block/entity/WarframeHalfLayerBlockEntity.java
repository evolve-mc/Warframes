package net.evo_mc.warframes.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class WarframeHalfLayerBlockEntity extends WarframeMultiMimicBlockEntity {

    private static final String[] HALF_KEYS = {"negative", "positive"};

    public WarframeHalfLayerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.WARFRAME_HALF_LAYER_BLOCK_ENTITY.get(), pPos, pBlockState, 2);
    }

    @Override
    protected String getSlotKey(int slot) {
        return HALF_KEYS[slot];
    }
}