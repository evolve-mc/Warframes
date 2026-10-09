package net.evo_mc.warframes.block.custom;

import net.evo_mc.warframes.block.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;


public class WarframeFullLandingGearBayBlock extends WarframeLandingGearBayBlock {
    public WarframeFullLandingGearBayBlock(Properties pProperties) {
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
    protected int getGearLength(CompoundTag data) {
        return data.getDouble("GearLength") >= 1.0 ? 2 : 1;
    }

    @Override
    protected BlockState createWheelState(BlockState bayState, int length) {
        return ModBlocks.AIRFRAME_LANDING_WHEEL.get().defaultBlockState()
                .setValue(WarframeLandingWheelBlock.FACING, bayState.getValue(FACING))
                .setValue(WarframeLandingWheelBlock.VARIANT, bayState.getValue(VARIANT))
                .setValue(WarframeLandingWheelBlock.HALF, length == 1 ? WarframeHorizontalSlabPosition.BOTTOM : WarframeHorizontalSlabPosition.TOP);
    }

    @Nullable
    @Override
    public MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos) {
        if (!ModList.get().isLoaded("valkyrien_warium")) {
            return null;
        }
        return WarframeLandingGearWarium.createGearConfigMenu(pPos);
    }


}