package net.evo_mc.warframes.block.custom;

import net.evo_mc.warframes.block.entity.WarframeMimicBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class WarframeDoorBlock extends DoorBlock implements WarframeMimicable {
    public static final BooleanProperty SEAMED = BooleanProperty.create("seamed");

    public WarframeDoorBlock(BlockSetType pType, Properties pProperties) {
        super(pProperties, pType);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(SEAMED, Boolean.valueOf(true)));
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer,
                                 InteractionHand pHand, BlockHitResult pHitResult) {
        if (pPlayer.isShiftKeyDown()) {
            if (!pLevel.isClientSide) {
                pLevel.setBlock(pPos, pState.setValue(SEAMED, !pState.getValue(SEAMED)), 3);
            }
            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        }

        InteractionResult mimicResult = mimicUse(pState, pLevel, pPos, pPlayer, pHand, pHitResult);
        if (mimicResult != InteractionResult.PASS) {
            return mimicResult;
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHitResult);
    }

    @Override
    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        mimicPlayerWillDestroy(pLevel, pPos, pState, pPlayer);

        BlockPos otherHalfPos = pState.getValue(HALF) == DoubleBlockHalf.LOWER ? pPos.above() : pPos.below();
        BlockState otherHalfState = pLevel.getBlockState(otherHalfPos);
        if (otherHalfState.is(this)) {
            if (pLevel.getBlockEntity(otherHalfPos) instanceof WarframeMimicBlockEntity otherMimic) {
                if (pPlayer.getAbilities().instabuild) {
                    otherMimic.clearContents();
                } else {
                    otherMimic.drops();
                    otherMimic.clearContents();
                }
            }
        }

        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos,
                         BlockState pNewState, boolean pMovedByPiston) {
        mimicOnRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(SEAMED);
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        if (pState.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockState belowState = pLevel.getBlockState(pPos.below());
            return belowState.is(this);
        }
        return true;
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