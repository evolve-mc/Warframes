package net.evo_mc.warframes.block.custom;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public interface WarframeLayered {

    @Nullable
    default BlockState removeLayer(BlockState state, BlockHitResult hitResult) {
        if (!state.hasProperty(BlockStateProperties.LAYERS)) {
            return null;
        }
        int layers = state.getValue(BlockStateProperties.LAYERS);
        return layers > 1 ? state.setValue(BlockStateProperties.LAYERS, layers - 1) : Blocks.AIR.defaultBlockState();
    }
}