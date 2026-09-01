package net.evo_mc.warframes.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class WarframeVerticalHalfSlabStabilizerBlock extends Block implements SimpleWaterloggedBlock {
    public static final EnumProperty<WarframeFacingType> FACING =
            EnumProperty.create("facing", WarframeFacingType.class);
    public static final EnumProperty<WarframeCornerType> CORNER =
            EnumProperty.create("corner", WarframeCornerType.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape NORTH_AABB = Block.box(4.0D, 0.0D, 0.0D, 12.0D, 16.0D, 8.0D);
    protected static final VoxelShape SOUTH_AABB = Block.box(4.0D, 0.0D, 8.0D, 12.0D, 16.0D, 16.0D);
    protected static final VoxelShape EAST_AABB = Block.box(8.0D, 0.0D, 4.0D, 16.0D, 16.0D, 12.0D);
    protected static final VoxelShape WEST_AABB = Block.box(0.0D, 0.0D, 4.0D, 8.0D, 16.0D, 12.0D);

    protected static final VoxelShape NORTHEAST_AABB = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D);
    protected static final VoxelShape NORTHWEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 8.0D);
    protected static final VoxelShape SOUTHEAST_AABB = Block.box(8.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape SOUTHWEST_AABB = Block.box(0.0D, 0.0D, 8.0D, 8.0D, 16.0D, 16.0D);

    public WarframeVerticalHalfSlabStabilizerBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, WarframeFacingType.NORTH)
                .setValue(CORNER, WarframeCornerType.NONE)
                .setValue(WATERLOGGED, Boolean.valueOf(false)));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        WarframeFacingType facing = pState.getValue(FACING);

        if (facing == WarframeFacingType.CORNER) {
            switch (pState.getValue(CORNER)) {
                case NORTHEAST:
                    return NORTHEAST_AABB;
                case NORTHWEST:
                    return NORTHWEST_AABB;
                case SOUTHEAST:
                    return SOUTHEAST_AABB;
                case SOUTHWEST:
                default:
                    return SOUTHWEST_AABB;
            }
        }

        switch (facing) {
            case NORTH:
                return NORTH_AABB;
            case SOUTH:
                return SOUTH_AABB;
            case EAST:
                return EAST_AABB;
            case WEST:
            default:
                return WEST_AABB;
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

        FluidState fluidstate = pContext.getLevel().getFluidState(blockpos);
        boolean waterlogged = fluidstate.getType() == Fluids.WATER;

        if (clickedFace == Direction.SOUTH) {
            if (relX < 0.3D) {
                return cornerState(WarframeCornerType.NORTHWEST, waterlogged);
            } else if (relX > 0.7D) {
                return cornerState(WarframeCornerType.NORTHEAST, waterlogged);
            } else {
                return facingState(WarframeFacingType.NORTH, waterlogged);
            }
        } else if (clickedFace == Direction.NORTH) {
            if (relX < 0.3D) {
                return cornerState(WarframeCornerType.SOUTHWEST, waterlogged);
            } else if (relX > 0.7D) {
                return cornerState(WarframeCornerType.SOUTHEAST, waterlogged);
            } else {
                return facingState(WarframeFacingType.SOUTH, waterlogged);
            }
        } else if (clickedFace == Direction.EAST) {
            if (relZ < 0.3D) {
                return cornerState(WarframeCornerType.NORTHWEST, waterlogged);
            } else if (relZ > 0.7D) {
                return cornerState(WarframeCornerType.SOUTHWEST, waterlogged);
            } else {
                return facingState(WarframeFacingType.WEST, waterlogged);
            }
        } else if (clickedFace == Direction.WEST) {
            if (relZ < 0.3D) {
                return cornerState(WarframeCornerType.NORTHEAST, waterlogged);
            } else if (relZ > 0.7D) {
                return cornerState(WarframeCornerType.SOUTHEAST, waterlogged);
            } else {
                return facingState(WarframeFacingType.EAST, waterlogged);
            }
        }

        boolean nearNorth = relZ < 0.3D;
        boolean nearSouth = relZ > 0.7D;

        if (nearNorth) {
            if (relX < 0.3D) {
                return cornerState(WarframeCornerType.NORTHWEST, waterlogged);
            } else if (relX > 0.7D) {
                return cornerState(WarframeCornerType.NORTHEAST, waterlogged);
            } else {
                return facingState(WarframeFacingType.NORTH, waterlogged);
            }
        } else if (nearSouth) {
            if (relX < 0.3D) {
                return cornerState(WarframeCornerType.SOUTHWEST, waterlogged);
            } else if (relX > 0.7D) {
                return cornerState(WarframeCornerType.SOUTHEAST, waterlogged);
            } else {
                return facingState(WarframeFacingType.SOUTH, waterlogged);
            }
        } else {
            if (relX < 0.5D) {
                return facingState(WarframeFacingType.WEST, waterlogged);
            } else {
                return facingState(WarframeFacingType.EAST, waterlogged);
            }
        }
    }

    private BlockState cornerState(WarframeCornerType corner, boolean waterlogged) {
        return this.defaultBlockState()
                .setValue(FACING, WarframeFacingType.CORNER)
                .setValue(CORNER, corner)
                .setValue(WATERLOGGED, Boolean.valueOf(waterlogged));
    }

    private BlockState facingState(WarframeFacingType facing, boolean waterlogged) {
        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(CORNER, WarframeCornerType.NONE)
                .setValue(WATERLOGGED, Boolean.valueOf(waterlogged));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, CORNER, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }
}