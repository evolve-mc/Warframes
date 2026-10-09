package net.evo_mc.warframes.block.custom;

import net.minecraft.world.level.block.state.BlockState;

public interface WarframeExtendableBlock {
    int getLayers(BlockState state);
    BlockState setLayers(BlockState state, int layers);
    int getMaxLayers();
}