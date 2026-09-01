package net.evo_mc.warframes.block.custom;

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
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class WarframeHalfSlabWingBlock extends Block implements SimpleWaterloggedBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<WarframeHalfSlabWingType> TYPE =
            EnumProperty.create("type", WarframeHalfSlabWingType.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape NORTH_TOP = Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);
    protected static final VoxelShape NORTH_BOTTOM = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 8.0D);
    protected static final VoxelShape NORTH_CENTER = Block.box(0.0D, 4.0D, 0.0D, 16.0D, 12.0D, 8.0D);
    protected static final VoxelShape SOUTH_TOP = Block.box(0.0D, 8.0D, 8.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape SOUTH_BOTTOM = Block.box(0.0D, 0.0D, 8.0D, 16.0D, 8.0D, 16.0D);
    protected static final VoxelShape SOUTH_CENTER = Block.box(0.0D, 4.0D, 8.0D, 16.0D, 12.0D, 16.0D);
    protected static final VoxelShape EAST_TOP = Block.box(8.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape EAST_BOTTOM = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    protected static final VoxelShape EAST_CENTER = Block.box(8.0D, 4.0D, 0.0D, 16.0D, 12.0D, 16.0D);
    protected static final VoxelShape WEST_TOP = Block.box(0.0D, 8.0D, 0.0D, 8.0D, 16.0D, 16.0D);
    protected static final VoxelShape WEST_BOTTOM = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 8.0D, 16.0D);
    protected static final VoxelShape WEST_CENTER = Block.box(0.0D, 4.0D, 0.0D, 8.0D, 12.0D, 16.0D);

    public WarframeHalfSlabWingBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(TYPE, WarframeHalfSlabWingType.BOTTOM)
                .setValue(WATERLOGGED, Boolean.valueOf(false)));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        WarframeHalfSlabWingType type = pState.getValue(TYPE);
        switch (pState.getValue(FACING)) {
            case NORTH:
                return type == WarframeHalfSlabWingType.TOP ? NORTH_TOP : type == WarframeHalfSlabWingType.CENTER ? NORTH_CENTER : NORTH_BOTTOM;
            case SOUTH:
                return type == WarframeHalfSlabWingType.TOP ? SOUTH_TOP : type == WarframeHalfSlabWingType.CENTER ? SOUTH_CENTER : SOUTH_BOTTOM;
            case EAST:
                return type == WarframeHalfSlabWingType.TOP ? EAST_TOP : type == WarframeHalfSlabWingType.CENTER ? EAST_CENTER : EAST_BOTTOM;
            case WEST:
            default:
                return type == WarframeHalfSlabWingType.TOP ? WEST_TOP : type == WarframeHalfSlabWingType.CENTER ? WEST_CENTER : WEST_BOTTOM;
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockPos blockpos = pContext.getClickedPos();
        Vec3 hit = pContext.getClickLocation();
        double relY = hit.y - (double) blockpos.getY();

        FluidState fluidstate = pContext.getLevel().getFluidState(blockpos);
        boolean waterlogged = fluidstate.getType() == Fluids.WATER;

        Direction facing = pContext.getHorizontalDirection();
        WarframeHalfSlabWingType type;
        if (relY < 0.3D) {
            type = WarframeHalfSlabWingType.BOTTOM;
        } else if (relY > 0.7D) {
            type = WarframeHalfSlabWingType.TOP;
        } else {
            type = WarframeHalfSlabWingType.CENTER;
        }

        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(TYPE, type)
                .setValue(WATERLOGGED, Boolean.valueOf(waterlogged));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, TYPE, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }
}