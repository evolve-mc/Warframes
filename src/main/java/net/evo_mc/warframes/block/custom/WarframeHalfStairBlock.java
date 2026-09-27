package net.evo_mc.warframes.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class WarframeHalfStairBlock extends Block implements SimpleWaterloggedBlock, WarframeMimicable {
    public static final EnumProperty<WarframeHalfType> HALF =
            EnumProperty.create("half", WarframeHalfType.class);
    public static final EnumProperty<WarframeFacingType> FACING =
            EnumProperty.create("facing", WarframeFacingType.class);
    public static final EnumProperty<WarframeCornerType> CORNER =
            EnumProperty.create("corner", WarframeCornerType.class);
    public static final EnumProperty<WarframeSideType> SIDE =
            EnumProperty.create("side", WarframeSideType.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape NORTHWEST_BOTTOM = Shapes.or(Block.box(0D, 0D, 8D, 8D, 8D, 16D), Block.box(0D, 0D, 0D, 16D, 8D, 8D));
    protected static final VoxelShape NORTHWEST_TOP = Shapes.or(Block.box(0D, 8D, 8D, 8D, 16D, 16D), Block.box(0D, 8D, 0D, 16D, 16D, 8D));
    protected static final VoxelShape NORTHEAST_BOTTOM = Shapes.or(Block.box(8D, 0D, 8D, 16D, 8D, 16D), Block.box(0D, 0D, 0D, 16D, 8D, 8D));
    protected static final VoxelShape NORTHEAST_TOP = Shapes.or(Block.box(8D, 8D, 8D, 16D, 16D, 16D), Block.box(0D, 8D, 0D, 16D, 16D, 8D));
    protected static final VoxelShape SOUTHEAST_BOTTOM = Shapes.or(Block.box(8D, 0D, 0D, 16D, 8D, 8D), Block.box(0D, 0D, 8D, 16D, 8D, 16D));
    protected static final VoxelShape SOUTHEAST_TOP = Shapes.or(Block.box(8D, 8D, 0D, 16D, 16D, 8D), Block.box(0D, 8D, 8D, 16D, 16D, 16D));
    protected static final VoxelShape SOUTHWEST_BOTTOM = Shapes.or(Block.box(0D, 0D, 0D, 8D, 8D, 8D), Block.box(0D, 0D, 8D, 16D, 8D, 16D));
    protected static final VoxelShape SOUTHWEST_TOP = Shapes.or(Block.box(0D, 8D, 0D, 8D, 16D, 8D), Block.box(0D, 8D, 8D, 16D, 16D, 16D));
    protected static final VoxelShape NORTH_BOTTOM_LEFT = Shapes.or(Block.box(0D, 0D, 8D, 8D, 8D, 16D), Block.box(0D, 0D, 0D, 8D, 16D, 8D));
    protected static final VoxelShape NORTH_BOTTOM_RIGHT = Shapes.or(Block.box(8D, 0D, 8D, 16D, 8D, 16D), Block.box(8D, 0D, 0D, 16D, 16D, 8D));
    protected static final VoxelShape NORTH_TOP_LEFT = Shapes.or(Block.box(0D, 8D, 8D, 8D, 16D, 16D), Block.box(0D, 0D, 0D, 8D, 16D, 8D));
    protected static final VoxelShape NORTH_TOP_RIGHT = Shapes.or(Block.box(8D, 8D, 8D, 16D, 16D, 16D), Block.box(8D, 0D, 0D, 16D, 16D, 8D));
    protected static final VoxelShape SOUTH_BOTTOM_LEFT = Shapes.or(Block.box(8D, 0D, 0D, 16D, 8D, 8D), Block.box(8D, 0D, 8D, 16D, 16D, 16D));
    protected static final VoxelShape SOUTH_BOTTOM_RIGHT = Shapes.or(Block.box(0D, 0D, 0D, 8D, 8D, 8D), Block.box(0D, 0D, 8D, 8D, 16D, 16D));
    protected static final VoxelShape SOUTH_TOP_LEFT = Shapes.or(Block.box(8D, 8D, 0D, 16D, 16D, 8D), Block.box(8D, 0D, 8D, 16D, 16D, 16D));
    protected static final VoxelShape SOUTH_TOP_RIGHT = Shapes.or(Block.box(0D, 8D, 0D, 8D, 16D, 8D), Block.box(0D, 0D, 8D, 8D, 16D, 16D));
    protected static final VoxelShape EAST_BOTTOM_LEFT = Shapes.or(Block.box(0D, 0D, 0D, 8D, 8D, 8D), Block.box(8D, 0D, 0D, 16D, 16D, 8D));
    protected static final VoxelShape EAST_BOTTOM_RIGHT = Shapes.or(Block.box(0D, 0D, 8D, 8D, 8D, 16D), Block.box(8D, 0D, 8D, 16D, 16D, 16D));
    protected static final VoxelShape EAST_TOP_LEFT = Shapes.or(Block.box(0D, 8D, 0D, 8D, 16D, 8D), Block.box(8D, 0D, 0D, 16D, 16D, 8D));
    protected static final VoxelShape EAST_TOP_RIGHT = Shapes.or(Block.box(0D, 8D, 8D, 8D, 16D, 16D), Block.box(8D, 0D, 8D, 16D, 16D, 16D));
    protected static final VoxelShape WEST_BOTTOM_LEFT = Shapes.or(Block.box(8D, 0D, 8D, 16D, 8D, 16D), Block.box(0D, 0D, 8D, 8D, 16D, 16D));
    protected static final VoxelShape WEST_BOTTOM_RIGHT = Shapes.or(Block.box(8D, 0D, 0D, 16D, 8D, 8D), Block.box(0D, 0D, 0D, 8D, 16D, 8D));
    protected static final VoxelShape WEST_TOP_LEFT = Shapes.or(Block.box(8D, 8D, 8D, 16D, 16D, 16D), Block.box(0D, 0D, 8D, 8D, 16D, 16D));
    protected static final VoxelShape WEST_TOP_RIGHT = Shapes.or(Block.box(8D, 8D, 0D, 16D, 16D, 8D), Block.box(0D, 0D, 0D, 8D, 16D, 8D));

    public WarframeHalfStairBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(HALF, WarframeHalfType.BOTTOM)
                .setValue(FACING, WarframeFacingType.NORTH)
                .setValue(CORNER, WarframeCornerType.NONE)
                .setValue(SIDE, WarframeSideType.LEFT)
                .setValue(WATERLOGGED, Boolean.valueOf(false)));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        WarframeHalfType half = pState.getValue(HALF);
        WarframeFacingType facing = pState.getValue(FACING);
        WarframeCornerType corner = pState.getValue(CORNER);
        WarframeSideType side = pState.getValue(SIDE);

        if (facing == WarframeFacingType.CORNER) {
            switch (corner) {
                case NORTHWEST:
                    return half == WarframeHalfType.TOP ? NORTHWEST_TOP : NORTHWEST_BOTTOM;
                case NORTHEAST:
                    return half == WarframeHalfType.TOP ? NORTHEAST_TOP : NORTHEAST_BOTTOM;
                case SOUTHEAST:
                    return half == WarframeHalfType.TOP ? SOUTHEAST_TOP : SOUTHEAST_BOTTOM;
                case SOUTHWEST:
                default:
                    return half == WarframeHalfType.TOP ? SOUTHWEST_TOP : SOUTHWEST_BOTTOM;
            }
        }

        switch (facing) {
            case NORTH:
                if (half == WarframeHalfType.TOP) {
                    return side == WarframeSideType.RIGHT ? NORTH_TOP_RIGHT : NORTH_TOP_LEFT;
                }
                return side == WarframeSideType.RIGHT ? NORTH_BOTTOM_RIGHT : NORTH_BOTTOM_LEFT;
            case SOUTH:
                if (half == WarframeHalfType.TOP) {
                    return side == WarframeSideType.RIGHT ? SOUTH_TOP_RIGHT : SOUTH_TOP_LEFT;
                }
                return side == WarframeSideType.RIGHT ? SOUTH_BOTTOM_RIGHT : SOUTH_BOTTOM_LEFT;
            case EAST:
                if (half == WarframeHalfType.TOP) {
                    return side == WarframeSideType.RIGHT ? EAST_TOP_RIGHT : EAST_TOP_LEFT;
                }
                return side == WarframeSideType.RIGHT ? EAST_BOTTOM_RIGHT : EAST_BOTTOM_LEFT;
            case WEST:
            default:
                if (half == WarframeHalfType.TOP) {
                    return side == WarframeSideType.RIGHT ? WEST_TOP_RIGHT : WEST_TOP_LEFT;
                }
                return side == WarframeSideType.RIGHT ? WEST_BOTTOM_RIGHT : WEST_BOTTOM_LEFT;
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return getShape(pState, pLevel, pPos, pContext);
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
        WarframeFacingType facing;
        WarframeCornerType corner = WarframeCornerType.NONE;
        WarframeSideType side = WarframeSideType.NONE;

        if (clickedFace == Direction.UP || clickedFace == Direction.DOWN) {
            half = clickedFace == Direction.DOWN ? WarframeHalfType.TOP : WarframeHalfType.BOTTOM;

            Direction horizontalFacing = pContext.getHorizontalDirection();
            switch (horizontalFacing) {
                case SOUTH:
                    facing = WarframeFacingType.SOUTH;
                    side = relX > 0.5D ? WarframeSideType.LEFT : WarframeSideType.RIGHT;
                    break;
                case EAST:
                    facing = WarframeFacingType.EAST;
                    side = relZ > 0.5D ? WarframeSideType.RIGHT : WarframeSideType.LEFT;
                    break;
                case WEST:
                    facing = WarframeFacingType.WEST;
                    side = relZ > 0.5D ? WarframeSideType.LEFT : WarframeSideType.RIGHT;
                    break;
                case NORTH:
                default:
                    facing = WarframeFacingType.NORTH;
                    side = relX > 0.5D ? WarframeSideType.RIGHT : WarframeSideType.LEFT;
                    break;
            }
        } else {
            double u;
            WarframeCornerType lowCorner;
            WarframeCornerType highCorner;
            WarframeFacingType oppositeFacing;
            boolean sideFlipped;

            switch (clickedFace) {
                case SOUTH:
                    u = relX;
                    lowCorner = WarframeCornerType.NORTHWEST;
                    highCorner = WarframeCornerType.NORTHEAST;
                    oppositeFacing = WarframeFacingType.NORTH;
                    sideFlipped = false;
                    break;
                case NORTH:
                    u = relX;
                    lowCorner = WarframeCornerType.SOUTHWEST;
                    highCorner = WarframeCornerType.SOUTHEAST;
                    oppositeFacing = WarframeFacingType.SOUTH;
                    sideFlipped = true;
                    break;
                case EAST:
                    u = relZ;
                    lowCorner = WarframeCornerType.NORTHWEST;
                    highCorner = WarframeCornerType.SOUTHWEST;
                    oppositeFacing = WarframeFacingType.WEST;
                    sideFlipped = true;
                    break;
                case WEST:
                default:
                    u = relZ;
                    lowCorner = WarframeCornerType.NORTHEAST;
                    highCorner = WarframeCornerType.SOUTHEAST;
                    oppositeFacing = WarframeFacingType.EAST;
                    sideFlipped = false;
                    break;
            }

            double v = relY;
            half = v > 0.5D ? WarframeHalfType.TOP : WarframeHalfType.BOTTOM;

            boolean isLeftRegion = u < v && u < (1.0D - v);
            boolean isRightRegion = u > v && u > (1.0D - v);

            if (isLeftRegion) {
                facing = WarframeFacingType.CORNER;
                corner = lowCorner;
            } else if (isRightRegion) {
                facing = WarframeFacingType.CORNER;
                corner = highCorner;
            } else {
                facing = oppositeFacing;
                boolean isLowSide = u < 0.5D;
                if (isLowSide != sideFlipped) {
                    side = WarframeSideType.LEFT;
                } else {
                    side = WarframeSideType.RIGHT;
                }
            }
        }

        return this.defaultBlockState()
                .setValue(HALF, half)
                .setValue(FACING, facing)
                .setValue(CORNER, corner)
                .setValue(SIDE, side)
                .setValue(WATERLOGGED, Boolean.valueOf(waterlogged));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(HALF, FACING, CORNER, SIDE, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }
        return !pState.canSurvive(pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer,
                                 InteractionHand pHand, BlockHitResult pHitResult) {
        return mimicUse(pState, pLevel, pPos, pPlayer, pHand, pHitResult);
    }

    @Override
    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        mimicPlayerWillDestroy(pLevel, pPos, pState, pPlayer);
        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos,
                         BlockState pNewState, boolean pMovedByPiston) {
        mimicOnRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        mimicSetPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
    }
}