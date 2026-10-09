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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
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

public class WarframeAirIntakeBlock extends Block implements WarframeMimicable, WarframeExtendableBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final int MAX_SIZE = 5;
    public static final IntegerProperty SIZE = IntegerProperty.create("size", 1, MAX_SIZE);

    private static final int[][][] NORTH_BOXES = {
            {
                    {0, 0, 0, 16, 2, 16},
                    {0, 2, 0, 2, 16, 16},
                    {14, 2, 0, 16, 16, 16},
                    {2, 14, 0, 14, 16, 16}
            },
            {
                    {0, 0, 6, 16, 2, 16},
                    {0, 2, 6, 2, 16, 16},
                    {14, 2, 6, 16, 16, 16},
                    {0, 4, 4, 2, 16, 6},
                    {14, 4, 4, 16, 16, 6},
                    {0, 8, 2, 2, 16, 4},
                    {14, 8, 2, 16, 16, 4},
                    {0, 12, 0, 2, 16, 2},
                    {14, 12, 0, 16, 16, 2},
                    {2, 14, 0, 14, 16, 16}
            },
            {
                    {0, 0, 12, 16, 2, 16},
                    {0, 2, 12, 2, 16, 16},
                    {14, 2, 12, 16, 16, 16},
                    {0, 4, 8, 2, 16, 12},
                    {14, 4, 8, 16, 16, 12},
                    {0, 8, 4, 2, 16, 8},
                    {14, 8, 4, 16, 16, 8},
                    {0, 12, 0, 2, 16, 4},
                    {14, 12, 0, 16, 16, 4},
                    {2, 14, 0, 14, 16, 16}
            },
            {
                    {0, 0, 10, 16, 2, 16},
                    {0, 2, 10, 2, 16, 16},
                    {14, 2, 10, 16, 16, 16},
                    {0, 4, 4, 2, 16, 10},
                    {14, 4, 4, 16, 16, 10},
                    {0, 8, -2, 2, 16, 4},
                    {14, 8, -2, 16, 16, 4},
                    {0, 12, -8, 2, 16, -2},
                    {14, 12, -8, 16, 16, -2},
                    {2, 14, -8, 14, 16, 16}
            },
            {
                    {0, 0, 8, 16, 2, 16},
                    {0, 2, 8, 2, 16, 16},
                    {14, 2, 8, 16, 16, 16},
                    {0, 4, 0, 2, 16, 8},
                    {14, 4, 0, 16, 16, 8},
                    {0, 8, -8, 2, 16, 0},
                    {14, 8, -8, 16, 16, 0},
                    {0, 12, -16, 2, 16, -8},
                    {14, 12, -16, 16, 16, -8},
                    {2, 14, -16, 14, 16, 16}
            }
    };

    private static final VoxelShape[][] SHAPES = new VoxelShape[MAX_SIZE][4];

    static {
        for (int i = 0; i < MAX_SIZE; i++) {
            for (Direction facing : Direction.Plane.HORIZONTAL) {
                VoxelShape shape = Shapes.empty();
                for (int[] box : NORTH_BOXES[i]) {
                    shape = Shapes.or(shape, rotate(box, facing));
                }
                SHAPES[i][facing.get2DDataValue()] = shape.optimize();
            }
        }
    }

    private static VoxelShape rotate(int[] box, Direction facing) {
        int x1 = box[0], y1 = box[1], z1 = box[2], x2 = box[3], y2 = box[4], z2 = box[5];
        return switch (facing) {
            case SOUTH -> Block.box(16 - x2, y1, 16 - z2, 16 - x1, y2, 16 - z1);
            case EAST -> Block.box(16 - z2, y1, x1, 16 - z1, y2, x2);
            case WEST -> Block.box(z1, y1, 16 - x2, z2, y2, 16 - x1);
            default -> Block.box(x1, y1, z1, x2, y2, z2);
        };
    }

    public WarframeAirIntakeBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(SIZE, 1));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, SIZE);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    public int getLayers(BlockState state) {
        return state.getValue(SIZE);
    }

    @Override
    public BlockState setLayers(BlockState state, int layers) {
        return state.setValue(SIZE, layers);
    }

    @Override
    public int getMaxLayers() {
        return MAX_SIZE;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer,
                                 InteractionHand pHand, BlockHitResult pHit) {
        return mimicUse(pState, pLevel, pPos, pPlayer, pHand, pHit);
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
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        mimicOnRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPES[pState.getValue(SIZE) - 1][pState.getValue(FACING).get2DDataValue()];
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
        pTooltip.add(Component.translatable("tooltip.warframes.wrench_hint")
                .withStyle(ChatFormatting.GRAY));
    }
}