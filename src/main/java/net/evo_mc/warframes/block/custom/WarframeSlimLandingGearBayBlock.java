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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class WarframeSlimLandingGearBayBlock extends WarframeLandingGearBayBlock {

    private static final VoxelShape SHAPE = Block.box(0, 13, 0, 16, 16, 16);

    public WarframeSlimLandingGearBayBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPEN, false)
                .setValue(VARIANT, 1));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return defaultBlockState().setValue(FACING, pContext.getHorizontalDirection());
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    protected BlockState createWheelState(BlockState bayState, int length) {
        return ModBlocks.AIRFRAME_LANDING_WHEEL.get().defaultBlockState()
                .setValue(WarframeLandingWheelBlock.FACING, bayState.getValue(FACING))
                .setValue(WarframeLandingWheelBlock.VARIANT, bayState.getValue(VARIANT))
                .setValue(WarframeLandingWheelBlock.HALF, WarframeHorizontalSlabPosition.TOP);
    }


}