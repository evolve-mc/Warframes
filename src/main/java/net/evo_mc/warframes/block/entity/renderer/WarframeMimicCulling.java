package net.evo_mc.warframes.block.entity.renderer;

import net.evo_mc.warframes.block.custom.WarframeMultiMimicable;
import net.evo_mc.warframes.block.entity.WarframeMimicStorage;
import net.evo_mc.warframes.block.entity.WarframeMultiMimicBlockEntity;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nullable;

public final class WarframeMimicCulling {

    public static final ModelProperty<Neighbor[]> NEIGHBORS = new ModelProperty<>();

    private static final float EPSILON = 0.001F;
    private static final float PROBE_DEPTH = 0.01F;

    public record Neighbor(BlockState effectiveState, VoxelShape faceShape) {
    }

    private WarframeMimicCulling() {
    }

    public static ModelData withNeighbors(BlockAndTintGetter level, BlockPos pos, ModelData modelData) {
        Neighbor[] neighbors = new Neighbor[6];
        boolean any = false;
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);
            if (neighborState.isAir()) {
                continue;
            }
            BlockState effective = effectiveState(level, neighborPos, neighborState);
            if (effective == null) {
                continue;
            }
            VoxelShape face = neighborState.getShape(level, neighborPos).getFaceShape(direction.getOpposite());
            if (face.isEmpty()) {
                continue;
            }
            neighbors[direction.get3DDataValue()] = new Neighbor(effective, face);
            any = true;
        }
        return any ? modelData.derive().with(NEIGHBORS, neighbors).build() : modelData;
    }

    @Nullable
    private static BlockState effectiveState(BlockAndTintGetter level, BlockPos neighborPos, BlockState neighborState) {
        BlockEntity blockEntity = level.getBlockEntity(neighborPos);
        if (blockEntity instanceof WarframeMimicStorage storage) {
            return storage.getMimickedBlock();
        }
        if (blockEntity instanceof WarframeMultiMimicBlockEntity multi
                && neighborState.getBlock() instanceof WarframeMultiMimicable mimicable) {
            BlockState shared = null;
            for (int slot = 0; slot < multi.getSlotCount(); slot++) {
                if (mimicable.getMimicSlotPresence(neighborState, slot) <= 0) {
                    continue;
                }
                BlockState mimicked = multi.getMimickedBlock(slot);
                if (mimicked == null || (shared != null && !mimicked.is(shared.getBlock()))) {
                    return null;
                }
                shared = mimicked;
            }
            return shared;
        }
        return neighborState;
    }

    private static float[] quadBounds(BakedQuad quad) {
        float[] bounds = {Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE};
        int[] vertices = quad.getVertices();
        for (int i = 0; i < 4; i++) {
            int offset = i * IQuadTransformer.STRIDE + IQuadTransformer.POSITION;
            for (int axis = 0; axis < 3; axis++) {
                float value = Float.intBitsToFloat(vertices[offset + axis]);
                bounds[axis] = Math.min(bounds[axis], value);
                bounds[axis + 3] = Math.max(bounds[axis + 3], value);
            }
        }
        return bounds;
    }

    private static boolean shrinkOtherAxes(float[] bounds, int faceAxis) {
        for (int axis = 0; axis < 3; axis++) {
            if (axis == faceAxis) {
                continue;
            }
            bounds[axis] += EPSILON;
            bounds[axis + 3] -= EPSILON;
            if (bounds[axis] >= bounds[axis + 3]) {
                return false;
            }
        }
        return true;
    }

    public static boolean isCulled(BakedQuad quad, BlockState mimic, @Nullable Neighbor[] neighbors) {
        if (neighbors == null) {
            return false;
        }
        Direction face = quad.getDirection();
        Neighbor neighbor = neighbors[face.get3DDataValue()];
        if (neighbor == null || !mimic.skipRendering(neighbor.effectiveState(), face)) {
            return false;
        }

        float[] bounds = quadBounds(quad);
        int faceAxis = face.getAxis().ordinal();
        float plane = face.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0F : 0.0F;
        if (Math.abs(bounds[faceAxis] - plane) > EPSILON || Math.abs(bounds[faceAxis + 3] - plane) > EPSILON) {
            return false;
        }
        if (!shrinkOtherAxes(bounds, faceAxis)) {
            return false;
        }
        bounds[faceAxis] = 0.0F;
        bounds[faceAxis + 3] = 1.0F;

        VoxelShape quadShape = Shapes.box(bounds[0], bounds[1], bounds[2], bounds[3], bounds[4], bounds[5]);
        return !Shapes.joinIsNotEmpty(quadShape, neighbor.faceShape(), BooleanOp.ONLY_FIRST);
    }

    public static boolean isQuadCoveredBy(BakedQuad quad, VoxelShape shape) {
        if (shape.isEmpty()) {
            return false;
        }
        Direction face = quad.getDirection();
        float[] bounds = quadBounds(quad);
        int faceAxis = face.getAxis().ordinal();
        if (bounds[faceAxis + 3] - bounds[faceAxis] > EPSILON) {
            return false;
        }
        if (!shrinkOtherAxes(bounds, faceAxis)) {
            return false;
        }
        float plane = bounds[faceAxis];
        if (face.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            bounds[faceAxis] = plane;
            bounds[faceAxis + 3] = plane + PROBE_DEPTH;
        } else {
            bounds[faceAxis] = plane - PROBE_DEPTH;
            bounds[faceAxis + 3] = plane;
        }

        VoxelShape probe = Shapes.box(bounds[0], bounds[1], bounds[2], bounds[3], bounds[4], bounds[5]);
        return !Shapes.joinIsNotEmpty(probe, shape, BooleanOp.ONLY_FIRST);
    }
}