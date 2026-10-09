package net.evo_mc.warframes.block.custom;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class WarframeLandingWheelBlock extends Block {

    public static final DirectionProperty FACING = WarframeLandingGearProperties.FACING;
    public static final IntegerProperty VARIANT = WarframeLandingGearProperties.VARIANT;
    public static final EnumProperty<WarframeHorizontalSlabPosition> HALF = WarframeLandingGearProperties.HALF;

    private static final double[][] NORTH_BOXES = {
            {6, 0, 3, 10, 10, 13},
            {2, 0, 3, 14, 10, 13},
            {5, 0, 0, 11, 16, 16},
            {-1, 0, 0, 17, 16, 16},
            {6, 0, 2, 10, 12, 14},
            {6, 0, 2, 10, 12, 14},
            {12, 0, 2, 16, 12, 14},
            {12, 0, 2, 16, 12, 14},
            {7, 0, 0, 13, 16, 16},
            {7, 0, 0, 13, 16, 16}
    };

    private static final VoxelShape[][] SHAPES = new VoxelShape[NORTH_BOXES.length][4];

    static {
        for (int i = 0; i < NORTH_BOXES.length; i++) {
            boolean flipped = i == 5 || i == 7 || i == 9;
            for (Direction facing : Direction.Plane.HORIZONTAL) {
                Direction rotation = flipped ? facing.getOpposite() : facing;
                SHAPES[i][facing.get2DDataValue()] = rotate(NORTH_BOXES[i], rotation);
            }
        }
    }

    public WarframeLandingWheelBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(VARIANT, 1)
                .setValue(HALF, WarframeHorizontalSlabPosition.TOP));
    }

    private static VoxelShape rotate(double[] b, Direction facing) {
        return switch (facing) {
            case SOUTH -> Block.box(16 - b[3], b[1], 16 - b[5], 16 - b[0], b[4], 16 - b[2]);
            case EAST -> Block.box(16 - b[5], b[1], b[0], 16 - b[2], b[4], b[3]);
            case WEST -> Block.box(b[2], b[1], 16 - b[3], b[5], b[4], 16 - b[0]);
            default -> Block.box(b[0], b[1], b[2], b[3], b[4], b[5]);
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, VARIANT, HALF);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPES[pState.getValue(VARIANT) - 1][pState.getValue(FACING).get2DDataValue()];
    }


    @Override
    public ItemStack getCloneItemStack(BlockGetter pLevel, BlockPos pPos, BlockState pState) {
        BlockPos bayPos = WarframeLandingGearBayBlock.findBayAbove(pLevel, pPos);
        return bayPos == null ? ItemStack.EMPTY : new ItemStack(pLevel.getBlockState(bayPos).getBlock());
    }

    @Override
    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return true;
    }

    @Override
    public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return 1.0f;
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock,
                                BlockPos pNeighborPos, boolean pMovedByPiston) {
        super.neighborChanged(pState, pLevel, pPos, pNeighborBlock, pNeighborPos, pMovedByPiston);
        if (pLevel.isClientSide() || !pNeighborPos.equals(pPos.above())) {
            return;
        }
        Block above = pLevel.getBlockState(pPos.above()).getBlock();
        if (!(above instanceof WarframeLandingGearBayBlock) && !(above instanceof WarframeLandingStrutBlock)) {
            pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        }
    }
}