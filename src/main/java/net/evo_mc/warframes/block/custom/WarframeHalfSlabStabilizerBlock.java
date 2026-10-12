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
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class WarframeHalfSlabStabilizerBlock extends Block implements WarframeMimicable {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<WarframeHorizontalSlabPosition> TYPE =
            EnumProperty.create("type", WarframeHorizontalSlabPosition.class);


    protected static final VoxelShape NORTH_TOP = Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D);
    protected static final VoxelShape NORTH_BOTTOM = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 8.0D);
    protected static final VoxelShape NORTH_CENTER = Block.box(0.0D, 4.0D, 0.0D, 16.0D, 12.0D, 8.0D);
    protected static final VoxelShape SOUTH_TOP = Block.box(0.0D, 8.0D, 8.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape SOUTH_BOTTOM = Block.box(0.0D, 0.0D, 8.0D, 16.0D, 8.0D, 16.0D);
    protected static final VoxelShape SOUTH_CENTER = Block.box(0.0D, 4.0D, 8.0D, 16.0D, 12.0D, 16.0D);
    protected static final VoxelShape EAST_TOP = Block.box(8.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape EAST_BOTTOM = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    protected static final VoxelShape EAST_CENTER = Block.box(8.0D, 4.0D, 0.0D, 16.0D, 12.0D, 16.0D);
    protected static final VoxelShape WEST_TOP = Block.box(0.0D, 8.0D, 0.0D, 8.0D, 16.0D, 16.0D);
    protected static final VoxelShape WEST_BOTTOM = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 8.0D, 16.0D);
    protected static final VoxelShape WEST_CENTER = Block.box(0.0D, 4.0D, 0.0D, 8.0D, 12.0D, 16.0D);

    public WarframeHalfSlabStabilizerBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(TYPE, WarframeHorizontalSlabPosition.BOTTOM));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        WarframeHorizontalSlabPosition type = pState.getValue(TYPE);
        switch (pState.getValue(FACING)) {
            case NORTH:
                return type == WarframeHorizontalSlabPosition.TOP ? NORTH_TOP : type == WarframeHorizontalSlabPosition.CENTER ? NORTH_CENTER : NORTH_BOTTOM;
            case SOUTH:
                return type == WarframeHorizontalSlabPosition.TOP ? SOUTH_TOP : type == WarframeHorizontalSlabPosition.CENTER ? SOUTH_CENTER : SOUTH_BOTTOM;
            case EAST:
                return type == WarframeHorizontalSlabPosition.TOP ? EAST_TOP : type == WarframeHorizontalSlabPosition.CENTER ? EAST_CENTER : EAST_BOTTOM;
            case WEST:
            default:
                return type == WarframeHorizontalSlabPosition.TOP ? WEST_TOP : type == WarframeHorizontalSlabPosition.CENTER ? WEST_CENTER : WEST_BOTTOM;
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockPos blockpos = pContext.getClickedPos();
        Vec3 hit = pContext.getClickLocation();
        double relY = hit.y - (double) blockpos.getY();

        Direction facing = pContext.getHorizontalDirection();
        WarframeHorizontalSlabPosition type;
        if (relY < 0.3D) {
            type = WarframeHorizontalSlabPosition.BOTTOM;
        } else if (relY > 0.7D) {
            type = WarframeHorizontalSlabPosition.TOP;
        } else {
            type = WarframeHorizontalSlabPosition.CENTER;
        }

        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(TYPE, type);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, TYPE);
    }



    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer,
                                 InteractionHand pHand, BlockHitResult pHitResult) {
        return mimicUse(pState, pLevel, pPos, pPlayer, pHand, pHitResult);
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
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        mimicSetPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.warframes.symmetric_aerofoil").withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("tooltip.warframes.provides_stabilization").withStyle(ChatFormatting.GRAY));
    }
}