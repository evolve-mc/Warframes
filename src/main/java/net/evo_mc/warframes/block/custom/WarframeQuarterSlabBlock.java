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
import net.minecraft.world.level.block.Block;
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
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class WarframeQuarterSlabBlock extends Block implements SimpleWaterloggedBlock, WarframeMimicable {
    public static final EnumProperty<WarframeHorizontalSlabPosition> HALF =
            EnumProperty.create("half", WarframeHorizontalSlabPosition.class);
    public static final EnumProperty<WarframeCompassPosition> POSITION =
            EnumProperty.create("position", WarframeCompassPosition.class);
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
                .setValue(HALF, WarframeHorizontalSlabPosition.BOTTOM)
                .setValue(POSITION, WarframeCompassPosition.NORTHEAST)
                .setValue(WATERLOGGED, Boolean.valueOf(false)));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        WarframeHorizontalSlabPosition half = pState.getValue(HALF);
        return switch (pState.getValue(POSITION)) {
            case NORTHEAST -> byHalf(half, NORTHEAST_TOP, NORTHEAST_CENTER, NORTHEAST_BOTTOM);
            case NORTHWEST -> byHalf(half, NORTHWEST_TOP, NORTHWEST_CENTER, NORTHWEST_BOTTOM);
            case SOUTHEAST -> byHalf(half, SOUTHEAST_TOP, SOUTHEAST_CENTER, SOUTHEAST_BOTTOM);
            case SOUTHWEST -> byHalf(half, SOUTHWEST_TOP, SOUTHWEST_CENTER, SOUTHWEST_BOTTOM);
            case NORTH -> byHalf(half, NORTH_TOP, NORTH_CENTER, NORTH_BOTTOM);
            case SOUTH -> byHalf(half, SOUTH_TOP, SOUTH_CENTER, SOUTH_BOTTOM);
            case EAST -> byHalf(half, EAST_TOP, EAST_CENTER, EAST_BOTTOM);
            case WEST -> byHalf(half, WEST_TOP, WEST_CENTER, WEST_BOTTOM);
            case CENTER -> half == WarframeHorizontalSlabPosition.TOP ? CENTER_TOP : CENTER_BOTTOM;
        };
    }

    private static VoxelShape byHalf(WarframeHorizontalSlabPosition half, VoxelShape top, VoxelShape center, VoxelShape bottom) {
        return switch (half) {
            case TOP -> top;
            case CENTER -> center;
            case BOTTOM -> bottom;
        };
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

        WarframeHorizontalSlabPosition half;
        WarframeCompassPosition position;

        if (clickedFace == Direction.UP || clickedFace == Direction.DOWN) {
            half = clickedFace == Direction.DOWN ? WarframeHorizontalSlabPosition.TOP : WarframeHorizontalSlabPosition.BOTTOM;
            boolean eastEdge = relX > 0.67D;
            boolean westEdge = relX < 0.33D;
            boolean southEdge = relZ > 0.67D;
            boolean northEdge = relZ < 0.33D;

            if ((eastEdge || westEdge) && (northEdge || southEdge)) {
                position = eastEdge
                        ? (southEdge ? WarframeCompassPosition.SOUTHEAST : WarframeCompassPosition.NORTHEAST)
                        : (southEdge ? WarframeCompassPosition.SOUTHWEST : WarframeCompassPosition.NORTHWEST);
            } else if (northEdge) {
                position = WarframeCompassPosition.NORTH;
            } else if (southEdge) {
                position = WarframeCompassPosition.SOUTH;
            } else if (eastEdge) {
                position = WarframeCompassPosition.EAST;
            } else if (westEdge) {
                position = WarframeCompassPosition.WEST;
            } else {
                position = WarframeCompassPosition.CENTER;
            }
        } else {
            if (relY > 0.67D) {
                half = WarframeHorizontalSlabPosition.TOP;
            } else if (relY < 0.33D) {
                half = WarframeHorizontalSlabPosition.BOTTOM;
            } else {
                half = WarframeHorizontalSlabPosition.CENTER;
            }

            position = switch (clickedFace) {
                case SOUTH -> relX > 0.67D ? WarframeCompassPosition.NORTHEAST
                        : relX < 0.33D ? WarframeCompassPosition.NORTHWEST
                        : WarframeCompassPosition.NORTH;
                case NORTH -> relX < 0.33D ? WarframeCompassPosition.SOUTHWEST
                        : relX > 0.67D ? WarframeCompassPosition.SOUTHEAST
                        : WarframeCompassPosition.SOUTH;
                case EAST -> relZ < 0.33D ? WarframeCompassPosition.NORTHWEST
                        : relZ > 0.67D ? WarframeCompassPosition.SOUTHWEST
                        : WarframeCompassPosition.WEST;
                default -> relZ > 0.67D ? WarframeCompassPosition.SOUTHEAST
                        : relZ < 0.33D ? WarframeCompassPosition.NORTHEAST
                        : WarframeCompassPosition.EAST;
            };
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