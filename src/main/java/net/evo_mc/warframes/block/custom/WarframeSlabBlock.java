package net.evo_mc.warframes.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;



public class WarframeSlabBlock extends Block implements SimpleWaterloggedBlock {
    public static final EnumProperty<WarframeSlabType> TYPE = EnumProperty.create("type", WarframeSlabType.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape BOTTOM_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    protected static final VoxelShape TOP_AABB = Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D);
    protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape WEST_AABB  = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape EAST_AABB  = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D);

    public WarframeSlabBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(TYPE, WarframeSlabType.BOTTOM).setValue(WATERLOGGED, Boolean.valueOf(false)));
    }

    public boolean useShapeForLightOcclusion(BlockState pState) {
        return pState.getValue(TYPE) != WarframeSlabType.DOUBLE;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(TYPE, WATERLOGGED);
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        WarframeSlabType slabtype = pState.getValue(TYPE);
        switch (slabtype) {
            case DOUBLE:
                return Shapes.block();
            case TOP:
                return TOP_AABB;
            default:
                return BOTTOM_AABB;
            case NORTH:
                return NORTH_AABB;
            case SOUTH:
                return SOUTH_AABB;
            case EAST:
                return EAST_AABB;
            case WEST:
                return WEST_AABB;
        }
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockPos blockpos = pContext.getClickedPos();
        BlockState blockstate = pContext.getLevel().getBlockState(blockpos);
        if (blockstate.is(this) && blockstate.getValue(TYPE) != WarframeSlabType.DOUBLE) {
            return blockstate.setValue(TYPE, WarframeSlabType.DOUBLE)
                    .setValue(WATERLOGGED, Boolean.valueOf(false));
        }
        FluidState fluidstate = pContext.getLevel().getFluidState(blockpos);
        BlockState base = this.defaultBlockState()
                .setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
        Direction clickedFace = pContext.getClickedFace();
        Vec3 hit = pContext.getClickLocation();
        double relX = hit.x - (double) blockpos.getX();
        double relY = hit.y - (double) blockpos.getY();
        double relZ = hit.z - (double) blockpos.getZ();
        if (clickedFace == Direction.NORTH || clickedFace == Direction.SOUTH) {
            WarframeSlabType fallback = clickedFace == Direction.NORTH ? WarframeSlabType.NORTH : WarframeSlabType.SOUTH;
            if (relY > 0.7D) {
                return base.setValue(TYPE, WarframeSlabType.TOP);
            } else if (relY < 0.3D) {
                return base.setValue(TYPE, WarframeSlabType.BOTTOM);
            } else if (relX < 0.2D) {
                return base.setValue(TYPE, WarframeSlabType.EAST);
            } else if (relX > 0.8D) {
                return base.setValue(TYPE, WarframeSlabType.WEST);
            } else {
                return base.setValue(TYPE, fallback);
            }
        } else if (clickedFace == Direction.EAST || clickedFace == Direction.WEST) {
            WarframeSlabType fallback = clickedFace == Direction.EAST ? WarframeSlabType.EAST : WarframeSlabType.WEST;
            if (relY > 0.7D) {
                return base.setValue(TYPE, WarframeSlabType.TOP);
            } else if (relY < 0.3D) {
                return base.setValue(TYPE, WarframeSlabType.BOTTOM);
            } else if (relZ < 0.2D) {
                return base.setValue(TYPE, WarframeSlabType.SOUTH);
            } else if (relZ > 0.8D) {
                return base.setValue(TYPE, WarframeSlabType.NORTH);
            } else {
                return base.setValue(TYPE, fallback);
            }
        } else {
            WarframeSlabType fallback = clickedFace == Direction.UP ? WarframeSlabType.BOTTOM : WarframeSlabType.TOP;
            if (relX < 0.2D) {
                return base.setValue(TYPE, WarframeSlabType.EAST);
            } else if (relX > 0.8D) {
                return base.setValue(TYPE, WarframeSlabType.WEST);
            } else if (relZ < 0.2D) {
                return base.setValue(TYPE, WarframeSlabType.SOUTH);
            } else if (relZ > 0.8D) {
                return base.setValue(TYPE, WarframeSlabType.NORTH);
            } else {
                return base.setValue(TYPE, fallback);
            }
        }
    }

    public boolean canBeReplaced(BlockState pState, BlockPlaceContext pUseContext) {
        ItemStack itemstack = pUseContext.getItemInHand();
        WarframeSlabType slabtype = pState.getValue(TYPE);
        if (slabtype != WarframeSlabType.DOUBLE && itemstack.is(this.asItem())) {
            if (pUseContext.replacingClickedOnBlock()) {
                boolean flag = pUseContext.getClickLocation().y - (double) pUseContext.getClickedPos().getY() > 0.5D;
                Direction direction = pUseContext.getClickedFace();
                if (slabtype == WarframeSlabType.BOTTOM) {
                    return direction == Direction.UP || flag && direction.getAxis().isHorizontal();
                } else {
                    return direction == Direction.DOWN || !flag && direction.getAxis().isHorizontal();
                }
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    public boolean placeLiquid(LevelAccessor pLevel, BlockPos pPos, BlockState pState, FluidState pFluidState) {
        return pState.getValue(TYPE) != WarframeSlabType.DOUBLE ? SimpleWaterloggedBlock.super.placeLiquid(pLevel, pPos, pState, pFluidState) : false;
    }

    public boolean canPlaceLiquid(BlockGetter pLevel, BlockPos pPos, BlockState pState, Fluid pFluid) {
        return pState.getValue(TYPE) != WarframeSlabType.DOUBLE ? SimpleWaterloggedBlock.super.canPlaceLiquid(pLevel, pPos, pState, pFluid) : false;
    }


    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }

        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        switch (pType) {
            case LAND:
                return false;
            case WATER:
                return pLevel.getFluidState(pPos).is(FluidTags.WATER);
            case AIR:
                return false;
            default:
                return false;
        }
    }
}


