package net.evo_mc.warframes.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class WarframeStairsBlock extends StairBlock {
    public static final EnumProperty<WarframeCornerVerticalType> VERTICAL =
            EnumProperty.create("vertical", WarframeCornerVerticalType.class);
    protected static final VoxelShape VERTICAL_NORTHEAST = Shapes.or(
            Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 8.0D)
    );
    protected static final VoxelShape VERTICAL_NORTHWEST = Shapes.or(
            Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D),
            Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D)
    );
    protected static final VoxelShape VERTICAL_SOUTHEAST = Shapes.or(
            Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
            Block.box(0.0D, 0.0D, 8.0D, 8.0D, 16.0D, 16.0D)
    );
    protected static final VoxelShape VERTICAL_SOUTHWEST = Shapes.or(
            Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D),
            Block.box(8.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D)
    );

    public WarframeStairsBlock(Supplier<BlockState> state, BlockBehaviour.Properties properties) {
        super(state, properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(FACING, Direction.NORTH)
                .setValue(HALF, net.minecraft.world.level.block.state.properties.Half.BOTTOM)
                .setValue(SHAPE, net.minecraft.world.level.block.state.properties.StairsShape.STRAIGHT)
                .setValue(WATERLOGGED, Boolean.valueOf(false))
                .setValue(VERTICAL, WarframeCornerVerticalType.NONE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder); // adds FACING, HALF, SHAPE, WATERLOGGED
        pBuilder.add(VERTICAL);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        switch (pState.getValue(VERTICAL)) {
            case NORTHEAST:
                return VERTICAL_NORTHEAST;
            case NORTHWEST:
                return VERTICAL_NORTHWEST;
            case SOUTHEAST:
                return VERTICAL_SOUTHEAST;
            case SOUTHWEST:
                return VERTICAL_SOUTHWEST;
            default:
                return super.getShape(pState, pLevel, pPos, pContext);
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction clickedFace = pContext.getClickedFace();
        BlockPos blockpos = pContext.getClickedPos();
        Vec3 hit = pContext.getClickLocation();
        double relX = hit.x - (double) blockpos.getX();
        double relZ = hit.z - (double) blockpos.getZ();

        if (clickedFace == Direction.NORTH) {
            if (relX < 0.2D) {
                return verticalState(pContext, WarframeCornerVerticalType.SOUTHWEST);
            } else if (relX > 0.8D) {
                return verticalState(pContext, WarframeCornerVerticalType.SOUTHEAST);
            }
        } else if (clickedFace == Direction.EAST) {
            if (relZ < 0.2D) {
                return verticalState(pContext, WarframeCornerVerticalType.NORTHWEST);
            } else if (relZ > 0.8D) {
                return verticalState(pContext, WarframeCornerVerticalType.SOUTHWEST);
            }
        } else if (clickedFace == Direction.SOUTH) {
            if (relX > 0.8D) {
                return verticalState(pContext, WarframeCornerVerticalType.NORTHEAST);
            } else if (relX < 0.2D) {
                return verticalState(pContext, WarframeCornerVerticalType.NORTHWEST);
            }
        } else if (clickedFace == Direction.WEST) {
            if (relZ > 0.8D) {
                return verticalState(pContext, WarframeCornerVerticalType.SOUTHEAST);
            } else if (relZ < 0.2D) {
                return verticalState(pContext, WarframeCornerVerticalType.NORTHEAST);
            }
        }

        BlockState stateForPlacement = super.getStateForPlacement(pContext);
        return stateForPlacement == null ? null : stateForPlacement.setValue(VERTICAL, WarframeCornerVerticalType.NONE);
    }

    private BlockState verticalState(BlockPlaceContext pContext, WarframeCornerVerticalType corner) {
        FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
        return this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(HALF, net.minecraft.world.level.block.state.properties.Half.BOTTOM)
                .setValue(SHAPE, net.minecraft.world.level.block.state.properties.StairsShape.STRAIGHT)
                .setValue(VERTICAL, corner)
                .setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
    }
}