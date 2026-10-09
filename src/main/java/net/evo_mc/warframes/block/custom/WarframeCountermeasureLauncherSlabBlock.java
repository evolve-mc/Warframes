package net.evo_mc.warframes.block.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class WarframeCountermeasureLauncherSlabBlock extends WarframeCountermeasureLauncherBlock {

    public static final BooleanProperty OUTER = BooleanProperty.create("outer");

    private static final VoxelShape DOWN_SHAPE = Block.box(0, 0, 0, 16, 8, 16);
    private static final VoxelShape UP_SHAPE = Block.box(0, 8, 0, 16, 16, 16);
    private static final VoxelShape NORTH_SHAPE = Block.box(0, 0, 0, 16, 16, 8);
    private static final VoxelShape SOUTH_SHAPE = Block.box(0, 0, 8, 16, 16, 16);
    private static final VoxelShape WEST_SHAPE = Block.box(0, 0, 0, 8, 16, 16);
    private static final VoxelShape EAST_SHAPE = Block.box(8, 0, 0, 16, 16, 16);

    public WarframeCountermeasureLauncherSlabBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.UP)
                .setValue(OUTER, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(OUTER);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return defaultBlockState()
                .setValue(FACING, pContext.getClickedFace())
                .setValue(OUTER, false);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Direction facing = pState.getValue(FACING);
        Direction side = pState.getValue(OUTER) ? facing : facing.getOpposite();
        return switch (side) {
            case DOWN -> DOWN_SHAPE;
            case UP -> UP_SHAPE;
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
        };
    }

    public static BlockState flipFace(BlockState state) {
        return state
                .setValue(FACING, state.getValue(FACING).getOpposite())
                .setValue(OUTER, !state.getValue(OUTER));
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
        pTooltip.add(Component.translatable("tooltip.warframes.wrench_countermeasure_slab")
                .withStyle(ChatFormatting.GRAY));
    }
}