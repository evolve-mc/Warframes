package net.evo_mc.warframes.block.entity.renderer;

import net.evo_mc.warframes.block.custom.WarframeHalfLayerBlock;
import net.evo_mc.warframes.block.custom.WarframeHalfPanelBlock;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.IQuadTransformer;

import javax.annotation.Nullable;

public class WarframeHalfLayerMimicBakedModel extends WarframeMultiMimicBakedModel {

    private static final float EPSILON = 0.001F;

    public WarframeHalfLayerMimicBakedModel(BakedModel originalModel) {
        super(originalModel);
    }

    @Nullable
    @Override
    protected BlockState getSlotOnlyState(BlockState state, int slot) {
        int other = 1 - slot;
        if (state.getBlock() instanceof WarframeHalfLayerBlock) {
            return state.setValue(WarframeHalfLayerBlock.getLayersProperty(other), 0);
        }
        if (state.getBlock() instanceof WarframeHalfPanelBlock) {
            return state.setValue(WarframeHalfPanelBlock.getHalfProperty(other), false);
        }
        return null;
    }

    @Override
    protected int getOwningSlot(BlockState state, BakedQuad quad) {
        if (!state.hasProperty(WarframeHalfLayerBlock.AXIS)) {
            return -1;
        }
        Direction.Axis axis = state.getValue(WarframeHalfLayerBlock.AXIS);
        int[] vertices = quad.getVertices();
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        for (int i = 0; i < 4; i++) {
            int offset = i * IQuadTransformer.STRIDE + IQuadTransformer.POSITION;
            double coordinate = axis.choose(
                    Float.intBitsToFloat(vertices[offset]),
                    Float.intBitsToFloat(vertices[offset + 1]),
                    Float.intBitsToFloat(vertices[offset + 2]));
            min = Math.min(min, coordinate);
            max = Math.max(max, coordinate);
        }

        boolean belowSplit = max <= 0.5D + EPSILON;
        boolean aboveSplit = min >= 0.5D - EPSILON;

        if (belowSplit && aboveSplit) {
            return quad.getDirection().getAxisDirection() == Direction.AxisDirection.POSITIVE
                    ? WarframeHalfLayerBlock.NEGATIVE_HALF
                    : WarframeHalfLayerBlock.POSITIVE_HALF;
        }
        if (belowSplit) {
            return WarframeHalfLayerBlock.NEGATIVE_HALF;
        }
        if (aboveSplit) {
            return WarframeHalfLayerBlock.POSITIVE_HALF;
        }
        return -1;
    }
}