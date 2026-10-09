package net.evo_mc.warframes.block.custom;

import net.evo_mc.warframes.block.ModBlocks;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class WarframeSlabLandingGearBayBlock extends WarframeLandingGearBayBlock {

    public static final EnumProperty<WarframeHorizontalSlabPosition> HALF = WarframeLandingGearProperties.HALF;

    private static final VoxelShape TOP_SHAPE = Block.box(0, 8, 0, 16, 16, 16);
    private static final VoxelShape CENTER_SHAPE = Block.box(0, 4, 0, 16, 12, 16);
    private static final VoxelShape BOTTOM_SHAPE = Block.box(0, 0, 0, 16, 8, 16);

    public WarframeSlabLandingGearBayBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPEN, false)
                .setValue(VARIANT, 1)
                .setValue(HALF, WarframeHorizontalSlabPosition.TOP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(HALF);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return defaultBlockState()
                .setValue(FACING, pContext.getHorizontalDirection())
                .setValue(HALF, getPlacementHalf(pContext));
    }

    private static WarframeHorizontalSlabPosition getPlacementHalf(BlockPlaceContext pContext) {
        Direction face = pContext.getClickedFace();
        if (face == Direction.DOWN) {
            return WarframeHorizontalSlabPosition.TOP;
        }
        if (face == Direction.UP) {
            return WarframeHorizontalSlabPosition.BOTTOM;
        }
        double y = pContext.getClickLocation().y - pContext.getClickedPos().getY();
        if (y > 2.0 / 3.0) {
            return WarframeHorizontalSlabPosition.TOP;
        }
        if (y < 1.0 / 3.0) {
            return WarframeHorizontalSlabPosition.BOTTOM;
        }
        return WarframeHorizontalSlabPosition.CENTER;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return switch (pState.getValue(HALF)) {
            case TOP -> TOP_SHAPE;
            case CENTER -> CENTER_SHAPE;
            case BOTTOM -> BOTTOM_SHAPE;
        };
    }

    @Override
    protected BlockState createWheelState(BlockState bayState, int length) {
        return ModBlocks.AIRFRAME_LANDING_WHEEL.get().defaultBlockState()
                .setValue(WarframeLandingWheelBlock.FACING, bayState.getValue(FACING))
                .setValue(WarframeLandingWheelBlock.VARIANT, bayState.getValue(VARIANT))
                .setValue(WarframeLandingWheelBlock.HALF, bayState.getValue(HALF));
    }


}