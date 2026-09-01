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

public class WarframeQuarterSlabBlock extends Block implements SimpleWaterloggedBlock {
    public static final EnumProperty<WarframeHalfType> HALF =
            EnumProperty.create("half", WarframeHalfType.class);
    public static final EnumProperty<WarframeFacingType> FACING =
            EnumProperty.create("facing", WarframeFacingType.class);
    public static final EnumProperty<WarframeCornerType> CORNER =
            EnumProperty.create("corner", WarframeCornerType.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape NORTHEAST_TOP = Block.box(8.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);
    protected static final VoxelShape NORTHEAST_BOTTOM = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 8.0D, 8.0D);
    protected static final VoxelShape NORTHEAST_CENTER = Block.box(8.0D, 4.0D, 0.0D, 16.0D, 12.0D, 8.0D);
    protected static final VoxelShape NORTHWEST_TOP = Block.box(0.0D, 8.0D, 0.0D, 8.0D, 16.0D, 8.0D);
    protected static final VoxelShape NORTHWEST_BOTTOM = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 8.0D, 8.0D);
    protected static final VoxelShape NORTHWEST_CENTER = Block.box(0.0D, 4.0D, 0.0D, 8.0D, 12.0D, 8.0D);
    protected static final VoxelShape SOUTHEAST_TOP = Block.box(8.0D, 8.0D, 8.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape SOUTHEAST_BOTTOM = Block.box(8.0D, 0.0D, 8.0D, 16.0D, 8.0D, 16.0D);
    protected static final VoxelShape SOUTHEAST_CENTER = Block.box(8.0D, 4.0D, 8.0D, 16.0D, 12.0D, 16.0D);
    protected static final VoxelShape SOUTHWEST_TOP = Block.box(0.0D, 8.0D, 8.0D, 8.0D, 16.0D, 16.0D);
    protected static final VoxelShape SOUTHWEST_BOTTOM = Block.box(0.0D, 0.0D, 8.0D, 8.0D, 8.0D, 16.0D);
    protected static final VoxelShape SOUTHWEST_CENTER = Block.box(0.0D, 4.0D, 8.0D, 8.0D, 12.0D, 16.0D);

    protected static final VoxelShape NORTH_TOP = Block.box(4.0D, 8.0D, 0.0D, 12.0D, 16.0D, 8.0D);
    protected static final VoxelShape NORTH_BOTTOM = Block.box(4.0D, 0.0D, 0.0D, 12.0D, 8.0D, 8.0D);
    protected static final VoxelShape NORTH_CENTER = Block.box(4.0D, 4.0D, 0.0D, 12.0D, 12.0D, 8.0D);
    protected static final VoxelShape SOUTH_TOP = Block.box(4.0D, 8.0D, 8.0D, 12.0D, 16.0D, 16.0D);
    protected static final VoxelShape SOUTH_BOTTOM = Block.box(4.0D, 0.0D, 8.0D, 12.0D, 8.0D, 16.0D);
    protected static final VoxelShape SOUTH_CENTER = Block.box(4.0D, 4.0D, 8.0D, 12.0D, 12.0D, 16.0D);
    protected static final VoxelShape EAST_TOP = Block.box(8.0D, 8.0D, 4.0D, 16.0D, 16.0D, 12.0D);
    protected static final VoxelShape EAST_BOTTOM = Block.box(8.0D, 0.0D, 4.0D, 16.0D, 8.0D, 12.0D);
    protected static final VoxelShape EAST_CENTER = Block.box(8.0D, 4.0D, 4.0D, 16.0D, 12.0D, 12.0D);
    protected static final VoxelShape WEST_TOP = Block.box(0.0D, 8.0D, 4.0D, 8.0D, 16.0D, 12.0D);
    protected static final VoxelShape WEST_BOTTOM = Block.box(0.0D, 0.0D, 4.0D, 8.0D, 8.0D, 12.0D);
    protected static final VoxelShape WEST_CENTER = Block.box(0.0D, 4.0D, 4.0D, 8.0D, 12.0D, 12.0D);

    protected static final VoxelShape CENTER_TOP = Block.box(4.0D, 8.0D, 4.0D, 12.0D, 16.0D, 12.0D);
    protected static final VoxelShape CENTER_BOTTOM = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 8.0D, 12.0D);

    public WarframeQuarterSlabBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(HALF, WarframeHalfType.BOTTOM)
                .setValue(FACING, WarframeFacingType.CORNER)
                .setValue(CORNER, WarframeCornerType.NORTHEAST)
                .setValue(WATERLOGGED, Boolean.valueOf(false)));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        WarframeHalfType half = pState.getValue(HALF);
        WarframeFacingType facing = pState.getValue(FACING);
        WarframeCornerType corner = pState.getValue(CORNER);

        if (facing == WarframeFacingType.CORNER) {
            switch (corner) {
                case NORTHEAST:
                    return half == WarframeHalfType.TOP ? NORTHEAST_TOP
                            : half == WarframeHalfType.CENTER ? NORTHEAST_CENTER
                            : NORTHEAST_BOTTOM;
                case NORTHWEST:
                    return half == WarframeHalfType.TOP ? NORTHWEST_TOP
                            : half == WarframeHalfType.CENTER ? NORTHWEST_CENTER
                            : NORTHWEST_BOTTOM;
                case SOUTHEAST:
                    return half == WarframeHalfType.TOP ? SOUTHEAST_TOP
                            : half == WarframeHalfType.CENTER ? SOUTHEAST_CENTER
                            : SOUTHEAST_BOTTOM;
                case SOUTHWEST:
                default:
                    return half == WarframeHalfType.TOP ? SOUTHWEST_TOP
                            : half == WarframeHalfType.CENTER ? SOUTHWEST_CENTER
                            : SOUTHWEST_BOTTOM;
            }
        }

        if (facing == WarframeFacingType.NONE) {
            return half == WarframeHalfType.TOP ? CENTER_TOP : CENTER_BOTTOM;
        }

        switch (facing) {
            case NORTH:
                return half == WarframeHalfType.TOP ? NORTH_TOP
                        : half == WarframeHalfType.CENTER ? NORTH_CENTER
                        : NORTH_BOTTOM;
            case SOUTH:
                return half == WarframeHalfType.TOP ? SOUTH_TOP
                        : half == WarframeHalfType.CENTER ? SOUTH_CENTER
                        : SOUTH_BOTTOM;
            case EAST:
                return half == WarframeHalfType.TOP ? EAST_TOP
                        : half == WarframeHalfType.CENTER ? EAST_CENTER
                        : EAST_BOTTOM;
            case WEST:
            default:
                return half == WarframeHalfType.TOP ? WEST_TOP
                        : half == WarframeHalfType.CENTER ? WEST_CENTER
                        : WEST_BOTTOM;
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction clickedFace = pContext.getClickedFace();
        BlockPos blockpos = pContext.getClickedPos();
        Vec3 hit = pContext.getClickLocation();
        double relX = hit.x - (double) blockpos.getX();
        double relY = hit.y - (double) blockpos.getY();
        double relZ = hit.z - (double) blockpos.getZ();

        FluidState fluidstate = pContext.getLevel().getFluidState(blockpos);
        boolean waterlogged = fluidstate.getType() == Fluids.WATER;

        WarframeHalfType half;
        WarframeCornerType corner = WarframeCornerType.NONE;
        WarframeFacingType facing;

        if (clickedFace == Direction.UP || clickedFace == Direction.DOWN) {
            half = clickedFace == Direction.DOWN ? WarframeHalfType.TOP : WarframeHalfType.BOTTOM;
            boolean eastEdge = relX > 0.7D;
            boolean westEdge = relX < 0.3D;
            boolean southEdge = relZ > 0.7D;
            boolean northEdge = relZ < 0.3D;

            if ((eastEdge || westEdge) && (northEdge || southEdge)) {
                facing = WarframeFacingType.CORNER;
                corner = eastEdge
                        ? (southEdge ? WarframeCornerType.SOUTHEAST : WarframeCornerType.NORTHEAST)
                        : (southEdge ? WarframeCornerType.SOUTHWEST : WarframeCornerType.NORTHWEST);
            } else if (northEdge) {
                facing = WarframeFacingType.NORTH;
            } else if (southEdge) {
                facing = WarframeFacingType.SOUTH;
            } else if (eastEdge) {
                facing = WarframeFacingType.EAST;
            } else if (westEdge) {
                facing = WarframeFacingType.WEST;
            } else {
                facing = WarframeFacingType.NONE;
            }
        } else {
            if (relY > 0.7D) {
                half = WarframeHalfType.TOP;
            } else if (relY < 0.3D) {
                half = WarframeHalfType.BOTTOM;
            } else {
                half = WarframeHalfType.CENTER;
            }

            switch (clickedFace) {
                case SOUTH:
                    if (relX > 0.7D) {
                        facing = WarframeFacingType.CORNER;
                        corner = WarframeCornerType.NORTHEAST;
                    } else if (relX < 0.3D) {
                        facing = WarframeFacingType.CORNER;
                        corner = WarframeCornerType.NORTHWEST;
                    } else {
                        facing = WarframeFacingType.NORTH;
                    }
                    break;
                case NORTH:
                    if (relX < 0.3D) {
                        facing = WarframeFacingType.CORNER;
                        corner = WarframeCornerType.SOUTHWEST;
                    } else if (relX > 0.7D) {
                        facing = WarframeFacingType.CORNER;
                        corner = WarframeCornerType.SOUTHEAST;
                    } else {
                        facing = WarframeFacingType.SOUTH;
                    }
                    break;
                case EAST:
                    if (relZ < 0.3D) {
                        facing = WarframeFacingType.CORNER;
                        corner = WarframeCornerType.NORTHWEST;
                    } else if (relZ > 0.7D) {
                        facing = WarframeFacingType.CORNER;
                        corner = WarframeCornerType.SOUTHWEST;
                    } else {
                        facing = WarframeFacingType.WEST;
                    }
                    break;
                case WEST:
                default:
                    if (relZ > 0.7D) {
                        facing = WarframeFacingType.CORNER;
                        corner = WarframeCornerType.SOUTHEAST;
                    } else if (relZ < 0.3D) {
                        facing = WarframeFacingType.CORNER;
                        corner = WarframeCornerType.NORTHEAST;
                    } else {
                        facing = WarframeFacingType.EAST;
                    }
                    break;
            }
        }

        return this.defaultBlockState()
                .setValue(HALF, half)
                .setValue(FACING, facing)
                .setValue(CORNER, corner)
                .setValue(WATERLOGGED, Boolean.valueOf(waterlogged));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(HALF, FACING, CORNER, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }
}