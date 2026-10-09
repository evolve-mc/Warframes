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
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class WarframeHalfStairBlock extends Block implements SimpleWaterloggedBlock, WarframeMimicable {
    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
    public static final EnumProperty<WarframeHalfStairPosition> POSITION =
            EnumProperty.create("position", WarframeHalfStairPosition.class);
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
                .setValue(HALF, Half.BOTTOM)
                .setValue(POSITION, WarframeHalfStairPosition.NORTH_LEFT)
                .setValue(WATERLOGGED, Boolean.valueOf(false)));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        boolean top = pState.getValue(HALF) == Half.TOP;
        return switch (pState.getValue(POSITION)) {
            case NORTHWEST -> top ? NORTHWEST_TOP : NORTHWEST_BOTTOM;
            case NORTHEAST -> top ? NORTHEAST_TOP : NORTHEAST_BOTTOM;
            case SOUTHEAST -> top ? SOUTHEAST_TOP : SOUTHEAST_BOTTOM;
            case SOUTHWEST -> top ? SOUTHWEST_TOP : SOUTHWEST_BOTTOM;
            case NORTH_LEFT -> top ? NORTH_TOP_LEFT : NORTH_BOTTOM_LEFT;
            case NORTH_RIGHT -> top ? NORTH_TOP_RIGHT : NORTH_BOTTOM_RIGHT;
            case SOUTH_LEFT -> top ? SOUTH_TOP_LEFT : SOUTH_BOTTOM_LEFT;
            case SOUTH_RIGHT -> top ? SOUTH_TOP_RIGHT : SOUTH_BOTTOM_RIGHT;
            case EAST_LEFT -> top ? EAST_TOP_LEFT : EAST_BOTTOM_LEFT;
            case EAST_RIGHT -> top ? EAST_TOP_RIGHT : EAST_BOTTOM_RIGHT;
            case WEST_LEFT -> top ? WEST_TOP_LEFT : WEST_BOTTOM_LEFT;
            case WEST_RIGHT -> top ? WEST_TOP_RIGHT : WEST_BOTTOM_RIGHT;
        };
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

        Half half;
        WarframeHalfStairPosition position;

        if (clickedFace == Direction.UP || clickedFace == Direction.DOWN) {
            half = clickedFace == Direction.DOWN ? Half.TOP : Half.BOTTOM;
            position = switch (pContext.getHorizontalDirection()) {
                case SOUTH -> relX > 0.5D ? WarframeHalfStairPosition.SOUTH_LEFT : WarframeHalfStairPosition.SOUTH_RIGHT;
                case EAST -> relZ > 0.5D ? WarframeHalfStairPosition.EAST_RIGHT : WarframeHalfStairPosition.EAST_LEFT;
                case WEST -> relZ > 0.5D ? WarframeHalfStairPosition.WEST_LEFT : WarframeHalfStairPosition.WEST_RIGHT;
                default -> relX > 0.5D ? WarframeHalfStairPosition.NORTH_RIGHT : WarframeHalfStairPosition.NORTH_LEFT;
            };
        } else {
            double u;
            WarframeHalfStairPosition lowCorner;
            WarframeHalfStairPosition highCorner;
            WarframeHalfStairPosition lowSide;
            WarframeHalfStairPosition highSide;

            switch (clickedFace) {
                case SOUTH:
                    u = relX;
                    lowCorner = WarframeHalfStairPosition.NORTHWEST;
                    highCorner = WarframeHalfStairPosition.NORTHEAST;
                    lowSide = WarframeHalfStairPosition.NORTH_LEFT;
                    highSide = WarframeHalfStairPosition.NORTH_RIGHT;
                    break;
                case NORTH:
                    u = relX;
                    lowCorner = WarframeHalfStairPosition.SOUTHWEST;
                    highCorner = WarframeHalfStairPosition.SOUTHEAST;
                    lowSide = WarframeHalfStairPosition.SOUTH_RIGHT;
                    highSide = WarframeHalfStairPosition.SOUTH_LEFT;
                    break;
                case EAST:
                    u = relZ;
                    lowCorner = WarframeHalfStairPosition.NORTHWEST;
                    highCorner = WarframeHalfStairPosition.SOUTHWEST;
                    lowSide = WarframeHalfStairPosition.WEST_RIGHT;
                    highSide = WarframeHalfStairPosition.WEST_LEFT;
                    break;
                case WEST:
                default:
                    u = relZ;
                    lowCorner = WarframeHalfStairPosition.NORTHEAST;
                    highCorner = WarframeHalfStairPosition.SOUTHEAST;
                    lowSide = WarframeHalfStairPosition.EAST_LEFT;
                    highSide = WarframeHalfStairPosition.EAST_RIGHT;
                    break;
            }

            double v = relY;
            half = v > 0.5D ? Half.TOP : Half.BOTTOM;

            boolean isLeftRegion = u < v && u < (1.0D - v);
            boolean isRightRegion = u > v && u > (1.0D - v);

            if (isLeftRegion) {
                position = lowCorner;
            } else if (isRightRegion) {
                position = highCorner;
            } else {
                position = u < 0.5D ? lowSide : highSide;
            }
        }

        return this.defaultBlockState()
                .setValue(HALF, half)
                .setValue(POSITION, position)
                .setValue(WATERLOGGED, Boolean.valueOf(waterlogged));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(HALF, POSITION, WATERLOGGED);
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