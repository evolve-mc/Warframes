package net.evo_mc.warframes.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class WarframeQuarterSlabBlock extends Block implements SimpleWaterloggedBlock {
    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
    public static final EnumProperty<WarframeCornerVerticalType> CORNER =
            EnumProperty.create("corner", WarframeCornerVerticalType.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;


    protected static final VoxelShape NORTHEAST_TOP = Block.box(8.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);
    protected static final VoxelShape NORTHEAST_BOTTOM = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 8.0D, 8.0D);
    protected static final VoxelShape NORTHWEST_TOP = Block.box(0.0D, 8.0D, 0.0D, 8.0D, 16.0D, 8.0D);
    protected static final VoxelShape NORTHWEST_BOTTOM = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 8.0D, 8.0D);
    protected static final VoxelShape SOUTHEAST_TOP = Block.box(8.0D, 8.0D, 8.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape SOUTHEAST_BOTTOM = Block.box(8.0D, 0.0D, 8.0D, 16.0D, 8.0D, 16.0D);
    protected static final VoxelShape SOUTHWEST_TOP = Block.box(0.0D, 8.0D, 8.0D, 8.0D, 16.0D, 16.0D);
    protected static final VoxelShape SOUTHWEST_BOTTOM = Block.box(0.0D, 0.0D, 8.0D, 8.0D, 8.0D, 16.0D);

    public WarframeQuarterSlabBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(HALF, Half.BOTTOM)
                .setValue(CORNER, WarframeCornerVerticalType.NORTHEAST)
                .setValue(WATERLOGGED, Boolean.valueOf(false)));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        boolean isTop = pState.getValue(HALF) == Half.TOP;
        switch (pState.getValue(CORNER)) {
            case NORTHEAST:
                return isTop ? NORTHEAST_TOP : NORTHEAST_BOTTOM;
            case NORTHWEST:
                return isTop ? NORTHWEST_TOP : NORTHWEST_BOTTOM;
            case SOUTHEAST:
                return isTop ? SOUTHEAST_TOP : SOUTHEAST_BOTTOM;
            case SOUTHWEST:
            default:
                return isTop ? SOUTHWEST_TOP : SOUTHWEST_BOTTOM;
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

        WarframeCornerVerticalType quadrant;
        Half half;

        if (clickedFace == Direction.UP || clickedFace == Direction.DOWN) {
            half = clickedFace == Direction.DOWN ? Half.TOP : Half.BOTTOM;
            boolean east = relX > 0.5D;
            boolean south = relZ > 0.5D;
            quadrant = east
                    ? (south ? WarframeCornerVerticalType.SOUTHEAST : WarframeCornerVerticalType.NORTHEAST)
                    : (south ? WarframeCornerVerticalType.SOUTHWEST : WarframeCornerVerticalType.NORTHWEST);
        } else {
            half = relY > 0.5D ? Half.TOP : Half.BOTTOM;
            boolean right;
            switch (clickedFace) {
                case SOUTH:
                    right = relX > 0.5D;
                    quadrant = right ? WarframeCornerVerticalType.NORTHEAST : WarframeCornerVerticalType.NORTHWEST;
                    break;
                case NORTH:
                    right = relX < 0.5D;
                    quadrant = right ? WarframeCornerVerticalType.SOUTHWEST : WarframeCornerVerticalType.SOUTHEAST;
                    break;
                case EAST:
                    right = relZ < 0.5D;
                    quadrant = right ? WarframeCornerVerticalType.NORTHWEST : WarframeCornerVerticalType.SOUTHWEST;
                    break;
                case WEST:
                default:
                    right = relZ > 0.5D;
                    quadrant = right ? WarframeCornerVerticalType.SOUTHEAST : WarframeCornerVerticalType.NORTHEAST;
                    break;
            }
        }

        return this.defaultBlockState()
                .setValue(HALF, half)
                .setValue(CORNER, quadrant)
                .setValue(WATERLOGGED, Boolean.valueOf(waterlogged));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(HALF, CORNER, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }
}