package net.evo_mc.warframes.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class WarframeHalfDoorBlock extends DoorBlock implements WarframeMimicable {

    public WarframeHalfDoorBlock(BlockSetType pType, Properties pProperties) {
        super(pProperties, pType);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockPos blockpos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        boolean powered = level.hasNeighborSignal(blockpos);
        return this.defaultBlockState()
                .setValue(FACING, pContext.getHorizontalDirection())
                .setValue(HINGE, getHalfDoorHinge(pContext))
                .setValue(POWERED, powered)
                .setValue(OPEN, powered)
                .setValue(HALF, DoubleBlockHalf.LOWER);
    }

    private DoorHingeSide getHalfDoorHinge(BlockPlaceContext pContext) {
        BlockGetter level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        Direction direction = pContext.getHorizontalDirection();

        Direction counterClockwise = direction.getCounterClockWise();
        BlockPos counterClockwisePos = blockpos.relative(counterClockwise);
        BlockState counterClockwiseState = level.getBlockState(counterClockwisePos);

        Direction clockwise = direction.getClockWise();
        BlockPos clockwisePos = blockpos.relative(clockwise);
        BlockState clockwiseState = level.getBlockState(clockwisePos);

        int solidBalance = (counterClockwiseState.isCollisionShapeFullBlock(level, counterClockwisePos) ? -1 : 0)
                + (clockwiseState.isCollisionShapeFullBlock(level, clockwisePos) ? 1 : 0);
        boolean doorCounterClockwise = counterClockwiseState.is(this);
        boolean doorClockwise = clockwiseState.is(this);

        if ((!doorCounterClockwise || doorClockwise) && solidBalance <= 0) {
            if ((!doorClockwise || doorCounterClockwise) && solidBalance >= 0) {
                int stepX = direction.getStepX();
                int stepZ = direction.getStepZ();
                Vec3 hit = pContext.getClickLocation();
                double relX = hit.x - (double) blockpos.getX();
                double relZ = hit.z - (double) blockpos.getZ();
                return (stepX >= 0 || !(relZ < 0.5D))
                        && (stepX <= 0 || !(relZ > 0.5D))
                        && (stepZ >= 0 || !(relX > 0.5D))
                        && (stepZ <= 0 || !(relX < 0.5D))
                        ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
            }
            return DoorHingeSide.LEFT;
        }
        return DoorHingeSide.RIGHT;
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState,
                                  LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        return pState;
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return true;
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock,
                                BlockPos pNeighborPos, boolean pMovedByPiston) {
        boolean powered = pLevel.hasNeighborSignal(pPos);
        if (!this.defaultBlockState().is(pNeighborBlock) && powered != pState.getValue(POWERED)) {
            if (powered != pState.getValue(OPEN)) {
                pLevel.playSound(null, pPos, powered ? this.type().doorOpen() : this.type().doorClose(),
                        SoundSource.BLOCKS, 1.0F, pLevel.getRandom().nextFloat() * 0.1F + 0.9F);
                pLevel.gameEvent(null, powered ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pPos);
            }
            pLevel.setBlock(pPos, pState.setValue(POWERED, powered).setValue(OPEN, powered), 2);
        }
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
}