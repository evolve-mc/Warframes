package net.evo_mc.warframes.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class WarframeHalfSlabBlock extends Block implements SimpleWaterloggedBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<WarframeCornerVerticalType> VERTICAL =
            EnumProperty.create("vertical", WarframeCornerVerticalType.class);

    protected static final VoxelShape NORTH_TOP = Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);
    protected static final VoxelShape NORTH_BOTTOM = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 8.0D);
    protected static final VoxelShape SOUTH_TOP = Block.box(0.0D, 8.0D, 8.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape SOUTH_BOTTOM = Block.box(0.0D, 0.0D, 8.0D, 16.0D, 8.0D, 16.0D);
    protected static final VoxelShape EAST_TOP = Block.box(8.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape EAST_BOTTOM = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    protected static final VoxelShape WEST_TOP = Block.box(0.0D, 8.0D, 0.0D, 8.0D, 16.0D, 16.0D);
    protected static final VoxelShape WEST_BOTTOM = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 8.0D, 16.0D);
    protected static final VoxelShape VERTICAL_NORTHEAST = Block.box(0.0D, 0.0D, 8.0D, 8.0D, 16.0D, 16.0D);
    protected static final VoxelShape VERTICAL_NORTHWEST = Block.box(8.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape VERTICAL_SOUTHEAST = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 8.0D);
    protected static final VoxelShape VERTICAL_SOUTHWEST = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D);

    public WarframeHalfSlabBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(HALF, Half.BOTTOM)
                .setValue(VERTICAL, WarframeCornerVerticalType.NONE)
                .setValue(WATERLOGGED, Boolean.valueOf(false)));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        WarframeCornerVerticalType vertical = pState.getValue(VERTICAL);
        switch (vertical) {
            case NORTHEAST:
                return VERTICAL_NORTHEAST;
            case NORTHWEST:
                return VERTICAL_NORTHWEST;
            case SOUTHEAST:
                return VERTICAL_SOUTHEAST;
            case SOUTHWEST:
                return VERTICAL_SOUTHWEST;
            default:
                return getSideShape(pState.getValue(FACING), pState.getValue(HALF));
        }
    }

    private VoxelShape getSideShape(Direction facing, Half half) {
        boolean isTop = half == Half.TOP;
        switch (facing) {
            case NORTH:
                return isTop ? NORTH_TOP : NORTH_BOTTOM;
            case SOUTH:
                return isTop ? SOUTH_TOP : SOUTH_BOTTOM;
            case EAST:
                return isTop ? EAST_TOP : EAST_BOTTOM;
            case WEST:
            default:
                return isTop ? WEST_TOP : WEST_BOTTOM;
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
                return verticalState(pContext, WarframeCornerVerticalType.NORTHEAST);
            } else if (relX > 0.8D) {
                return verticalState(pContext, WarframeCornerVerticalType.NORTHWEST);
            }
        } else if (clickedFace == Direction.EAST) {
            if (relZ < 0.2D) {
                return verticalState(pContext, WarframeCornerVerticalType.SOUTHEAST);
            } else if (relZ > 0.8D) {
                return verticalState(pContext, WarframeCornerVerticalType.NORTHEAST);
            }
        } else if (clickedFace == Direction.SOUTH) {
            if (relX > 0.8D) {
                return verticalState(pContext, WarframeCornerVerticalType.SOUTHWEST);
            } else if (relX < 0.2D) {
                return verticalState(pContext, WarframeCornerVerticalType.SOUTHEAST);
            }
        } else if (clickedFace == Direction.WEST) {
            if (relZ > 0.8D) {
                return verticalState(pContext, WarframeCornerVerticalType.NORTHWEST);
            } else if (relZ < 0.2D) {
                return verticalState(pContext, WarframeCornerVerticalType.SOUTHWEST);
            }
        }

        Direction facing = pContext.getHorizontalDirection();
        boolean isTop = clickedFace == Direction.DOWN
                || (clickedFace != Direction.UP && hit.y - (double) blockpos.getY() > 0.5D);
        Half half = isTop ? Half.TOP : Half.BOTTOM;

        FluidState fluidstate = pContext.getLevel().getFluidState(blockpos);
        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(HALF, half)
                .setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER))
                .setValue(VERTICAL, WarframeCornerVerticalType.NONE);
    }

    private BlockState verticalState(BlockPlaceContext pContext, WarframeCornerVerticalType corner) {
        FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
        return this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(HALF, Half.BOTTOM)
                .setValue(VERTICAL, corner)
                .setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, HALF, VERTICAL, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }
}