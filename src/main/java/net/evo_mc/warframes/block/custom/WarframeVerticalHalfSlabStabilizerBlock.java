package net.evo_mc.warframes.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import java.util.List;

import javax.annotation.Nullable;

public class WarframeVerticalHalfSlabStabilizerBlock extends Block implements WarframeMimicable {
    public static final EnumProperty<WarframeEdgePosition> FACING =
            EnumProperty.create("facing", WarframeEdgePosition.class);

    protected static final VoxelShape NORTH_AABB = Block.box(4.0D, 0.0D, 0.0D, 12.0D, 16.0D, 8.0D);
    protected static final VoxelShape SOUTH_AABB = Block.box(4.0D, 0.0D, 8.0D, 12.0D, 16.0D, 16.0D);
    protected static final VoxelShape EAST_AABB = Block.box(8.0D, 0.0D, 4.0D, 16.0D, 16.0D, 12.0D);
    protected static final VoxelShape WEST_AABB = Block.box(0.0D, 0.0D, 4.0D, 8.0D, 16.0D, 12.0D);

    protected static final VoxelShape NORTHEAST_AABB = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D);
    protected static final VoxelShape NORTHWEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 8.0D);
    protected static final VoxelShape SOUTHEAST_AABB = Block.box(8.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape SOUTHWEST_AABB = Block.box(0.0D, 0.0D, 8.0D, 8.0D, 16.0D, 16.0D);

    public WarframeVerticalHalfSlabStabilizerBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, WarframeEdgePosition.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return switch (pState.getValue(FACING)) {
            case NORTH -> NORTH_AABB;
            case SOUTH -> SOUTH_AABB;
            case EAST -> EAST_AABB;
            case WEST -> WEST_AABB;
            case NORTHEAST -> NORTHEAST_AABB;
            case NORTHWEST -> NORTHWEST_AABB;
            case SOUTHEAST -> SOUTHEAST_AABB;
            case SOUTHWEST -> SOUTHWEST_AABB;
        };
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction clickedFace = pContext.getClickedFace();
        BlockPos blockpos = pContext.getClickedPos();
        Vec3 hit = pContext.getClickLocation();
        double relX = hit.x - (double) blockpos.getX();
        double relZ = hit.z - (double) blockpos.getZ();


        WarframeEdgePosition facing;
        if (clickedFace == Direction.SOUTH) {
            facing = relX < 0.3D ? WarframeEdgePosition.NORTHWEST
                    : relX > 0.7D ? WarframeEdgePosition.NORTHEAST
                    : WarframeEdgePosition.NORTH;
        } else if (clickedFace == Direction.NORTH) {
            facing = relX < 0.3D ? WarframeEdgePosition.SOUTHWEST
                    : relX > 0.7D ? WarframeEdgePosition.SOUTHEAST
                    : WarframeEdgePosition.SOUTH;
        } else if (clickedFace == Direction.EAST) {
            facing = relZ < 0.3D ? WarframeEdgePosition.NORTHWEST
                    : relZ > 0.7D ? WarframeEdgePosition.SOUTHWEST
                    : WarframeEdgePosition.WEST;
        } else if (clickedFace == Direction.WEST) {
            facing = relZ < 0.3D ? WarframeEdgePosition.NORTHEAST
                    : relZ > 0.7D ? WarframeEdgePosition.SOUTHEAST
                    : WarframeEdgePosition.EAST;
        } else if (relZ < 0.3D) {
            facing = relX < 0.3D ? WarframeEdgePosition.NORTHWEST
                    : relX > 0.7D ? WarframeEdgePosition.NORTHEAST
                    : WarframeEdgePosition.NORTH;
        } else if (relZ > 0.7D) {
            facing = relX < 0.3D ? WarframeEdgePosition.SOUTHWEST
                    : relX > 0.7D ? WarframeEdgePosition.SOUTHEAST
                    : WarframeEdgePosition.SOUTH;
        } else {
            facing = relX < 0.5D ? WarframeEdgePosition.WEST : WarframeEdgePosition.EAST;
        }

        return this.defaultBlockState()
                .setValue(FACING, facing);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
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