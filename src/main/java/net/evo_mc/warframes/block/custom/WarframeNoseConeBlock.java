package net.evo_mc.warframes.block.custom;

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
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class WarframeNoseConeBlock extends Block implements WarframeMimicable, WarframeExtendableBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public static final int MAX_LAYERS = 8;
    public static final int DEFAULT_LAYERS = 5;
    public static final IntegerProperty LAYERS = IntegerProperty.create("layers", 1, MAX_LAYERS);

    private static final double[] BASE_HALF_WIDTH = {0.0D, 1.0D, 2.0D, 3.0D, 4.0D, 5.0D, 6.0D, 7.0D, 8.0D};
    private static final double[] TIP_HALF_WIDTH = {0.0D, 0.0D, 0.0D, 1.0D, 2.0D, 3.0D, 4.0D, 5.0D, 6.0D};

    private static final VoxelShape[][] SHAPES = new VoxelShape[MAX_LAYERS + 1][Direction.values().length];
    static {
        for (int layers = 1; layers <= MAX_LAYERS; layers++) {
            for (Direction direction : Direction.values()) {
                VoxelShape shape = section(direction, BASE_HALF_WIDTH[layers], 0.0D, 10.0D);
                if (TIP_HALF_WIDTH[layers] > 0.0D) {
                    shape = Shapes.or(shape, section(direction, TIP_HALF_WIDTH[layers], 10.0D, 16.0D));
                }
                SHAPES[layers][direction.ordinal()] = shape;
            }
        }
    }

    private static VoxelShape section(Direction direction, double halfWidth, double base, double tip) {
        double min = 8.0D - halfWidth;
        double max = 8.0D + halfWidth;
        return switch (direction) {
            case UP -> Block.box(min, base, min, max, tip, max);
            case DOWN -> Block.box(min, 16.0D - tip, min, max, 16.0D - base, max);
            case NORTH -> Block.box(min, min, 16.0D - tip, max, max, 16.0D - base);
            case SOUTH -> Block.box(min, min, base, max, max, tip);
            case EAST -> Block.box(base, min, min, tip, max, max);
            case WEST -> Block.box(16.0D - tip, min, min, 16.0D - base, max, max);
        };
    }

    public WarframeNoseConeBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.UP)
                .setValue(LAYERS, DEFAULT_LAYERS));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPES[pState.getValue(LAYERS)][pState.getValue(FACING).ordinal()];
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction direction = pContext.getClickedFace();
        return this.defaultBlockState().setValue(FACING, direction);
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, LAYERS);
    }

    @Override
    public int getLayers(BlockState state) {
        return state.getValue(LAYERS);
    }

    @Override
    public BlockState setLayers(BlockState state, int layers) {
        return state.setValue(LAYERS, layers);
    }

    @Override
    public int getMaxLayers() {
        return MAX_LAYERS;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer,
                                 InteractionHand pHand, BlockHitResult pHitResult) {
        InteractionResult mimicResult = mimicUse(pState, pLevel, pPos, pPlayer, pHand, pHitResult);
        if (mimicResult != InteractionResult.PASS) {
            return mimicResult;
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHitResult);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        mimicSetPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
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
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
        pTooltip.add(Component.translatable("tooltip.warframes.wrench_hint")
                .withStyle(ChatFormatting.GRAY));
    }
}