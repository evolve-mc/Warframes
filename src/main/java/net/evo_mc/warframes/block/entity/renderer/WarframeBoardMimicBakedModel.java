package net.evo_mc.warframes.block.entity.renderer;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.client.model.IQuadTransformer;

import javax.annotation.Nullable;

public class WarframeBoardMimicBakedModel extends WarframeMultiMimicBakedModel {

    private static final float EDGE = 1.0F / 16.0F + 0.001F;

    public WarframeBoardMimicBakedModel(BakedModel originalModel) {
        super(originalModel);
    }

    private static boolean hasFace(BlockState state, Direction face) {
        BooleanProperty property = PipeBlock.PROPERTY_BY_DIRECTION.get(face);
        return state.hasProperty(property) && state.getValue(property);
    }

    @Override
    protected int getOwningSlot(BlockState state, BakedQuad quad) {
        Direction owner = findOwningFace(state, quad);
        return owner != null ? owner.get3DDataValue() : -1;
    }

    @Override
    protected boolean isHiddenQuad(BlockState state, BakedQuad quad, int slot) {
        Direction owner = Direction.from3DDataValue(slot);
        Direction quadDirection = quad.getDirection();
        return quadDirection.getAxis() != owner.getAxis() && hasFace(state, quadDirection);
    }

    @Nullable
    private Direction findOwningFace(BlockState state, BakedQuad quad) {
        int[] vertices = quad.getVertices();
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;
        for (int i = 0; i < 4; i++) {
            int offset = i * IQuadTransformer.STRIDE + IQuadTransformer.POSITION;
            float x = Float.intBitsToFloat(vertices[offset]);
            float y = Float.intBitsToFloat(vertices[offset + 1]);
            float z = Float.intBitsToFloat(vertices[offset + 2]);
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            minZ = Math.min(minZ, z);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
            maxZ = Math.max(maxZ, z);
        }

        Direction.Axis normalAxis = quad.getDirection().getAxis();
        Direction fallback = null;
        for (Direction face : Direction.values()) {
            if (!hasFace(state, face)) {
                continue;
            }
            boolean within = switch (face) {
                case DOWN -> maxY <= EDGE;
                case UP -> minY >= 1.0F - EDGE;
                case NORTH -> maxZ <= EDGE;
                case SOUTH -> minZ >= 1.0F - EDGE;
                case WEST -> maxX <= EDGE;
                case EAST -> minX >= 1.0F - EDGE;
            };
            if (!within) {
                continue;
            }
            if (face.getAxis() != normalAxis) {
                return face;
            }
            if (fallback == null) {
                fallback = face;
            }
        }
        return fallback;
    }
}