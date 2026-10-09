package net.evo_mc.warframes.block.custom;

import net.evo_mc.warframes.block.entity.WarframeHalfLayerBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;


import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class WarframeHalfLayerBlock extends Block implements WarframeMultiMimicable, WarframeLayered, SimpleWaterloggedBlock {
    public static final int MAX_LAYERS = 8;
    public static final int NEGATIVE_HALF = 0;
    public static final int POSITIVE_HALF = 1;

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    public static final IntegerProperty NEGATIVE_LAYERS = IntegerProperty.create("negative_layers", 0, MAX_LAYERS);
    public static final IntegerProperty POSITIVE_LAYERS = IntegerProperty.create("positive_layers", 0, MAX_LAYERS);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private final Map<BlockState, VoxelShape> shapesCache;

    public WarframeHalfLayerBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.UP)
                .setValue(AXIS, Direction.Axis.X)
                .setValue(NEGATIVE_LAYERS, 0)
                .setValue(POSITIVE_LAYERS, 0)
                .setValue(WATERLOGGED, false));
        this.shapesCache = this.getShapeForEachState(WarframeHalfLayerBlock::calculateShape);
    }

    public static IntegerProperty getLayersProperty(int half) {
        return half == NEGATIVE_HALF ? NEGATIVE_LAYERS : POSITIVE_LAYERS;
    }

    public static int getLayers(BlockState state, int half) {
        return state.getValue(getLayersProperty(half));
    }

    public static Direction getHalfDirection(BlockState state, int half) {
        return Direction.fromAxisAndDirection(state.getValue(AXIS),
                half == NEGATIVE_HALF ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE);
    }

    private static int getHalfIndex(Direction halfDirection) {
        return halfDirection.getAxisDirection() == Direction.AxisDirection.NEGATIVE ? NEGATIVE_HALF : POSITIVE_HALF;
    }

    private static boolean isFull(BlockState state) {
        return getLayers(state, NEGATIVE_HALF) == MAX_LAYERS && getLayers(state, POSITIVE_HALF) == MAX_LAYERS;
    }

    private static VoxelShape calculateShape(BlockState state) {
        Direction attached = state.getValue(FACING).getOpposite();
        if (attached.getAxis() == state.getValue(AXIS)) {
            return Shapes.empty();
        }
        VoxelShape shape = Shapes.empty();
        for (int half = 0; half < 2; half++) {
            int layers = getLayers(state, half);
            if (layers > 0) {
                shape = Shapes.or(shape, halfBox(attached, getHalfDirection(state, half), layers * 2.0D));
            }
        }
        return shape;
    }

    private static VoxelShape halfBox(Direction attached, Direction half, double thickness) {
        double[] min = {0.0D, 0.0D, 0.0D};
        double[] max = {16.0D, 16.0D, 16.0D};
        int attachedIndex = attached.getAxis().ordinal();
        if (attached.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            min[attachedIndex] = 16.0D - thickness;
        } else {
            max[attachedIndex] = thickness;
        }
        int halfIndex = half.getAxis().ordinal();
        if (half.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            min[halfIndex] = 8.0D;
        } else {
            max[halfIndex] = 8.0D;
        }
        return Block.box(min[0], min[1], min[2], max[0], max[1], max[2]);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return this.shapesCache.get(pState);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return isFull(pState) ? Shapes.block() : Shapes.empty();
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState pState) {
        return true;
    }

    private static Direction getClickedHalf(Direction face, Vec3 relative) {
        return switch (face.getAxis()) {
            case Y -> pickHalf(relative.x, relative.z, Direction.Axis.X, Direction.Axis.Z);
            case Z -> pickHalf(relative.x, relative.y, Direction.Axis.X, Direction.Axis.Y);
            case X -> pickHalf(relative.z, relative.y, Direction.Axis.Z, Direction.Axis.Y);
        };
    }

    private static Direction pickHalf(double first, double second, Direction.Axis firstAxis, Direction.Axis secondAxis) {
        double firstOffset = first - 0.5D;
        double secondOffset = second - 0.5D;
        if (Math.abs(firstOffset) >= Math.abs(secondOffset)) {
            return Direction.fromAxisAndDirection(firstAxis,
                    firstOffset >= 0.0D ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE);
        }
        return Direction.fromAxisAndDirection(secondAxis,
                secondOffset >= 0.0D ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE);
    }

    private static Vec3 stepInside(Vec3 click, Direction face) {
        return click.subtract(face.getStepX() * 0.001D, face.getStepY() * 0.001D, face.getStepZ() * 0.001D);
    }

    private static int getHitHalf(BlockState state, BlockPos pos, Vec3 click, Direction face) {
        Vec3 inside = stepInside(click, face);
        double coordinate = state.getValue(AXIS).choose(
                inside.x - pos.getX(), inside.y - pos.getY(), inside.z - pos.getZ());
        return coordinate < 0.5D ? NEGATIVE_HALF : POSITIVE_HALF;
    }

    @Override
    public boolean canBeReplaced(BlockState pState, BlockPlaceContext pUseContext) {
        if (!pUseContext.getItemInHand().is(this.asItem())) {
            return false;
        }
        Level level = pUseContext.getLevel();
        Direction face = pUseContext.getClickedFace();
        Vec3 click = pUseContext.getClickLocation();
        BlockPos hitPos = BlockPos.containing(stepInside(click, face));

        if (level.getBlockState(hitPos) == pState) {
            if (pUseContext.isSecondaryUseActive()) {
                return false;
            }
            int layers = getLayers(pState, getHitHalf(pState, hitPos, click, face));
            return layers > 0 && layers < MAX_LAYERS;
        }

        if (face != pState.getValue(FACING)) {
            return false;
        }
        BlockPos targetPos = hitPos.relative(face);
        Direction half = getClickedHalf(face, click.subtract(targetPos.getX(), targetPos.getY(), targetPos.getZ()));
        return half.getAxis() == pState.getValue(AXIS) && getLayers(pState, getHalfIndex(half)) == 0;

    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Level level = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();
        BlockState existing = level.getBlockState(pos);
        Direction face = pContext.getClickedFace();
        Vec3 click = pContext.getClickLocation();

        if (!existing.is(this)) {
            Direction half = getClickedHalf(face, click.subtract(pos.getX(), pos.getY(), pos.getZ()));
            return this.defaultBlockState()
                    .setValue(FACING, face)
                    .setValue(AXIS, half.getAxis())
                    .setValue(getLayersProperty(getHalfIndex(half)), 1)
                    .setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER);
        }

        if (pContext.replacingClickedOnBlock()) {
            int half = getHitHalf(existing, pos, click, face);
            int layers = getLayers(existing, half);
            if (layers > 0 && layers < MAX_LAYERS) {
                return existing.setValue(getLayersProperty(half), layers + 1);
            }
            return null;
        }

        if (face != existing.getValue(FACING)) {
            return null;
        }
        Direction half = getClickedHalf(face, click.subtract(pos.getX(), pos.getY(), pos.getZ()));
        if (half.getAxis() != existing.getValue(AXIS)) {
            return null;
        }
        int halfIndex = getHalfIndex(half);
        if (getLayers(existing, halfIndex) != 0) {
            return null;
        }
        return existing.setValue(getLayersProperty(halfIndex), 1);
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return true;
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState,
                                  LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }
        return pState;
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, AXIS, NEGATIVE_LAYERS, POSITIVE_LAYERS, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new WarframeHalfLayerBlockEntity(pPos, pState);
    }

    @Override
    public int getMimicSlotPresence(BlockState state, int slot) {
        return getLayers(state, slot);
    }

    @Override
    public int getMimicSlotForHit(BlockState state, BlockPos pos, BlockHitResult hitResult) {
        return getHitHalf(state, pos, hitResult.getLocation(), hitResult.getDirection());
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer,
                                 InteractionHand pHand, BlockHitResult pHitResult) {
        return multiMimicUse(pState, pLevel, pPos, pPlayer, pHand, pHitResult);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        multiMimicSetPlacedBy(pLevel, pPos, pState, pPlacer);
    }

    @Override
    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        multiMimicPlayerWillDestroy(pLevel, pPos, pPlayer);
        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos,
                         BlockState pNewState, boolean pMovedByPiston) {
        multiMimicOnRemove(pState, pLevel, pPos, pNewState);
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockState removeLayer(BlockState state, BlockHitResult hitResult) {
        int half = getHitHalf(state, hitResult.getBlockPos(), hitResult.getLocation(), hitResult.getDirection());
        int layers = getLayers(state, half);
        if (layers <= 0) {
            return null;
        }
        BlockState reduced = state.setValue(getLayersProperty(half), layers - 1);
        if (getLayers(reduced, NEGATIVE_HALF) == 0 && getLayers(reduced, POSITIVE_HALF) == 0) {
            return Blocks.AIR.defaultBlockState();
        }
        return reduced;
    }

    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.warframes.layer")
                .withStyle(ChatFormatting.GRAY));
    }
}