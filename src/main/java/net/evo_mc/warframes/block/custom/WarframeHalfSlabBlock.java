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
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class WarframeHalfSlabBlock extends Block implements SimpleWaterloggedBlock, WarframeMimicable {
    public static final EnumProperty<WarframeEdgePosition> POSITION = EnumProperty.create("position", WarframeEdgePosition.class);
    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape NORTH_TOP = Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);
    protected static final VoxelShape NORTH_BOTTOM = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 8.0D);
    protected static final VoxelShape SOUTH_TOP = Block.box(0.0D, 8.0D, 8.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape SOUTH_BOTTOM = Block.box(0.0D, 0.0D, 8.0D, 16.0D, 8.0D, 16.0D);
    protected static final VoxelShape EAST_TOP = Block.box(8.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape EAST_BOTTOM = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    protected static final VoxelShape WEST_TOP = Block.box(0.0D, 8.0D, 0.0D, 8.0D, 16.0D, 16.0D);
    protected static final VoxelShape WEST_BOTTOM = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 8.0D, 16.0D);
    protected static final VoxelShape NORTHEAST_PILLAR = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D);
    protected static final VoxelShape NORTHWEST_PILLAR = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 8.0D);
    protected static final VoxelShape SOUTHEAST_PILLAR = Block.box(8.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape SOUTHWEST_PILLAR = Block.box(0.0D, 0.0D, 8.0D, 8.0D, 16.0D, 16.0D);

    public WarframeHalfSlabBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(POSITION, WarframeEdgePosition.NORTH)
                .setValue(HALF, Half.BOTTOM)
                .setValue(WATERLOGGED, Boolean.valueOf(false)));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        boolean isTop = pState.getValue(HALF) == Half.TOP;
        return switch (pState.getValue(POSITION)) {
            case NORTH -> isTop ? NORTH_TOP : NORTH_BOTTOM;
            case SOUTH -> isTop ? SOUTH_TOP : SOUTH_BOTTOM;
            case EAST -> isTop ? EAST_TOP : EAST_BOTTOM;
            case WEST -> isTop ? WEST_TOP : WEST_BOTTOM;
            case NORTHEAST -> NORTHEAST_PILLAR;
            case NORTHWEST -> NORTHWEST_PILLAR;
            case SOUTHEAST -> SOUTHEAST_PILLAR;
            case SOUTHWEST -> SOUTHWEST_PILLAR;
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

        if (clickedFace == Direction.NORTH) {
            if (relX < 0.33D) {
                return cornerState(WarframeEdgePosition.SOUTHWEST, waterlogged);
            } else if (relX > 0.67D) {
                return cornerState(WarframeEdgePosition.SOUTHEAST, waterlogged);
            }
        } else if (clickedFace == Direction.EAST) {
            if (relZ < 0.33D) {
                return cornerState(WarframeEdgePosition.NORTHWEST, waterlogged);
            } else if (relZ > 0.67D) {
                return cornerState(WarframeEdgePosition.SOUTHWEST, waterlogged);
            }
        } else if (clickedFace == Direction.SOUTH) {
            if (relX > 0.67D) {
                return cornerState(WarframeEdgePosition.NORTHEAST, waterlogged);
            } else if (relX < 0.33D) {
                return cornerState(WarframeEdgePosition.NORTHWEST, waterlogged);
            }
        } else if (clickedFace == Direction.WEST) {
            if (relZ > 0.67D) {
                return cornerState(WarframeEdgePosition.SOUTHEAST, waterlogged);
            } else if (relZ < 0.33D) {
                return cornerState(WarframeEdgePosition.NORTHEAST, waterlogged);
            }
        }

        WarframeEdgePosition position = switch (pContext.getHorizontalDirection()) {
            case SOUTH -> WarframeEdgePosition.SOUTH;
            case EAST -> WarframeEdgePosition.EAST;
            case WEST -> WarframeEdgePosition.WEST;
            default -> WarframeEdgePosition.NORTH;
        };

        boolean isTop = clickedFace == Direction.DOWN
                || (clickedFace != Direction.UP && relY > 0.5D);

        return this.defaultBlockState()
                .setValue(POSITION, position)
                .setValue(HALF, isTop ? Half.TOP : Half.BOTTOM)
                .setValue(WATERLOGGED, Boolean.valueOf(waterlogged));
    }

    private BlockState cornerState(WarframeEdgePosition corner, boolean waterlogged) {
        return this.defaultBlockState()
                .setValue(POSITION, corner)
                .setValue(HALF, Half.BOTTOM)
                .setValue(WATERLOGGED, Boolean.valueOf(waterlogged));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(POSITION, HALF, WATERLOGGED);
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