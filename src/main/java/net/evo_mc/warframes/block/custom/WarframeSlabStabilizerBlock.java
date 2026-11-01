package net.evo_mc.warframes.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import java.util.List;

import javax.annotation.Nullable;

public class WarframeSlabStabilizerBlock extends Block implements WarframeMimicable {
    public static final EnumProperty<WarframeHorizontalSlabPositionDouble> TYPE = EnumProperty.create("type", WarframeHorizontalSlabPositionDouble.class);

    protected static final VoxelShape BOTTOM_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    protected static final VoxelShape TOP_AABB = Block.box(0.0D, 8.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape CENTER_AABB = Block.box(0.0D, 4.0D, 0.0D, 16.0D, 12.0D, 16.0D);

    public WarframeSlabStabilizerBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(TYPE, WarframeHorizontalSlabPositionDouble.BOTTOM));
    }

    public boolean useShapeForLightOcclusion(BlockState pState) {
        return pState.getValue(TYPE) != WarframeHorizontalSlabPositionDouble.DOUBLE;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(TYPE);
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        switch (pState.getValue(TYPE)) {
            case DOUBLE:
                return Shapes.block();
            case TOP:
                return TOP_AABB;
            case CENTER:
                return CENTER_AABB;
            default:
                return BOTTOM_AABB;
        }
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockPos blockpos = pContext.getClickedPos();
        BlockState blockstate = pContext.getLevel().getBlockState(blockpos);
        if (blockstate.is(this) && (blockstate.getValue(TYPE) == WarframeHorizontalSlabPositionDouble.TOP || blockstate.getValue(TYPE) == WarframeHorizontalSlabPositionDouble.BOTTOM)) {
            return blockstate.setValue(TYPE, WarframeHorizontalSlabPositionDouble.DOUBLE);
        } else {
            double relY = pContext.getClickLocation().y - (double) blockpos.getY();

            WarframeHorizontalSlabPositionDouble type;
            if (relY < 0.3D) {
                type = WarframeHorizontalSlabPositionDouble.BOTTOM;
            } else if (relY > 0.7D) {
                type = WarframeHorizontalSlabPositionDouble.TOP;
            } else {
                type = WarframeHorizontalSlabPositionDouble.CENTER;
            }

            return this.defaultBlockState()
                    .setValue(TYPE, type);
        }
    }

    public boolean canBeReplaced(BlockState pState, BlockPlaceContext pUseContext) {
        ItemStack itemstack = pUseContext.getItemInHand();
        WarframeHorizontalSlabPositionDouble type = pState.getValue(TYPE);
        if ((type == WarframeHorizontalSlabPositionDouble.TOP || type == WarframeHorizontalSlabPositionDouble.BOTTOM) && itemstack.is(this.asItem())) {
            if (pUseContext.replacingClickedOnBlock()) {
                boolean flag = pUseContext.getClickLocation().y - (double) pUseContext.getClickedPos().getY() > 0.5D;
                Direction direction = pUseContext.getClickedFace();
                if (type == WarframeHorizontalSlabPositionDouble.BOTTOM) {
                    return direction == Direction.UP || flag && direction.getAxis().isHorizontal();
                } else {
                    return direction == Direction.DOWN || !flag && direction.getAxis().isHorizontal();
                }
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        switch (pType) {
            case LAND:
                return false;
            case WATER:
                return pLevel.getFluidState(pPos).is(FluidTags.WATER);
            case AIR:
                return false;
            default:
                return false;
        }
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
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        super.onPlace(pState, pLevel, pPos, pOldState, pIsMoving);
        if (!pLevel.isClientSide()) {
            pLevel.scheduleTick(pPos, this, 1);
        }
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        super.tick(pState, pLevel, pPos, pRandom);
        if (net.minecraftforge.fml.ModList.get().isLoaded("valkyrien_warium")) {
            net.mcreator.valkyrienwarium.procedures.HorizontalStabilizerScriptProcedure.execute(
                    pLevel, pPos.getX(), pPos.getY(), pPos.getZ(), pState);
        }
        pLevel.scheduleTick(pPos, this, 1);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.warframes.symmetric_aerofoil").withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("tooltip.warframes.provides_stabilization").withStyle(ChatFormatting.GRAY));
    }
}