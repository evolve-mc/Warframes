package net.evo_mc.warframes.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class WarframeEdgeSliceBlock extends Block implements SimpleWaterloggedBlock {
    public static final int MAX_LAYERS = 8;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
    public static final EnumProperty<WarframeCornerType> CORNER = EnumProperty.create("corner", WarframeCornerType.class);
    public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public WarframeEdgeSliceBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(HALF, Half.BOTTOM)
                .setValue(CORNER, WarframeCornerType.NONE)
                .setValue(LAYERS, Integer.valueOf(1))
                .setValue(WATERLOGGED, Boolean.valueOf(false)));
    }

    private VoxelShape getEdgeShape(BlockState pState) {
        int layers = pState.getValue(LAYERS);
        double thickness = layers * 2.0D;

        WarframeCornerType corner = pState.getValue(CORNER);
        if (corner != WarframeCornerType.NONE) {
            switch (corner) {
                case NORTHEAST:
                    return Block.box(16.0D - thickness, 0.0D, 0.0D, 16.0D, 16.0D, thickness);
                case NORTHWEST:
                    return Block.box(0.0D, 0.0D, 0.0D, thickness, 16.0D, thickness);
                case SOUTHEAST:
                    return Block.box(16.0D - thickness, 0.0D, 16.0D - thickness, 16.0D, 16.0D, 16.0D);
                case SOUTHWEST:
                default:
                    return Block.box(0.0D, 0.0D, 16.0D - thickness, thickness, 16.0D, 16.0D);
            }
        }

        boolean isTop = pState.getValue(HALF) == Half.TOP;
        double yMin = isTop ? 16.0D - thickness : 0.0D;
        double yMax = isTop ? 16.0D : thickness;

        switch (pState.getValue(FACING)) {
            case NORTH:
                return Block.box(0.0D, yMin, 0.0D, 16.0D, yMax, thickness);
            case SOUTH:
                return Block.box(0.0D, yMin, 16.0D - thickness, 16.0D, yMax, 16.0D);
            case EAST:
                return Block.box(16.0D - thickness, yMin, 0.0D, 16.0D, yMax, 16.0D);
            case WEST:
            default:
                return Block.box(0.0D, yMin, 0.0D, thickness, yMax, 16.0D);
        }
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return getEdgeShape(pState);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return getEdgeShape(pState);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState pState) {
        return true;
    }

    @Override
    public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return pState.getValue(LAYERS) == MAX_LAYERS ? 0.2F : 1.0F;
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }
        return !pState.canSurvive(pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    @Override
    public boolean canBeReplaced(BlockState pState, BlockPlaceContext pUseContext) {
        return !pUseContext.isSecondaryUseActive()
                && pUseContext.getItemInHand().getItem() == this.asItem()
                && pState.getValue(LAYERS) < MAX_LAYERS
                ? true
                : super.canBeReplaced(pState, pUseContext);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockPos blockpos = pContext.getClickedPos();
        BlockState existing = pContext.getLevel().getBlockState(blockpos);

        if (existing.is(this)) {
            int i = existing.getValue(LAYERS);
            return existing.setValue(LAYERS, Integer.valueOf(Math.min(MAX_LAYERS, i + 1)));
        }

        Direction clickedFace = pContext.getClickedFace();
        Vec3 hit = pContext.getClickLocation();
        double relX = hit.x - (double) blockpos.getX();
        double relY = hit.y - (double) blockpos.getY();
        double relZ = hit.z - (double) blockpos.getZ();

        FluidState fluidstate = pContext.getLevel().getFluidState(blockpos);
        boolean waterlogged = fluidstate.getType() == Fluids.WATER;

        if (clickedFace == Direction.NORTH) {
            if (relX < 0.2D) {
                return cornerState(WarframeCornerType.SOUTHWEST, waterlogged);
            } else if (relX > 0.8D) {
                return cornerState(WarframeCornerType.SOUTHEAST, waterlogged);
            }
        } else if (clickedFace == Direction.EAST) {
            if (relZ < 0.2D) {
                return cornerState(WarframeCornerType.NORTHWEST, waterlogged);
            } else if (relZ > 0.8D) {
                return cornerState(WarframeCornerType.SOUTHWEST, waterlogged);
            }
        } else if (clickedFace == Direction.SOUTH) {
            if (relX > 0.8D) {
                return cornerState(WarframeCornerType.NORTHEAST, waterlogged);
            } else if (relX < 0.2D) {
                return cornerState(WarframeCornerType.NORTHWEST, waterlogged);
            }
        } else if (clickedFace == Direction.WEST) {
            if (relZ > 0.8D) {
                return cornerState(WarframeCornerType.SOUTHEAST, waterlogged);
            } else if (relZ < 0.2D) {
                return cornerState(WarframeCornerType.NORTHEAST, waterlogged);
            }
        }

        Direction facing = pContext.getHorizontalDirection();
        boolean isTop = clickedFace == Direction.DOWN
                || (clickedFace != Direction.UP && relY > 0.5D);
        Half half = isTop ? Half.TOP : Half.BOTTOM;

        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(HALF, half)
                .setValue(CORNER, WarframeCornerType.NONE)
                .setValue(LAYERS, Integer.valueOf(1))
                .setValue(WATERLOGGED, Boolean.valueOf(waterlogged));
    }

    private BlockState cornerState(WarframeCornerType corner, boolean waterlogged) {
        return this.defaultBlockState()
                .setValue(CORNER, corner)
                .setValue(LAYERS, Integer.valueOf(1))
                .setValue(WATERLOGGED, Boolean.valueOf(waterlogged));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, HALF, CORNER, LAYERS, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }
}