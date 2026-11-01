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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import java.util.List;

import javax.annotation.Nullable;

public class WarframeVerticalSlabStabilizerBlock extends Block implements WarframeMimicable {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<WarframeVerticalSlabPosition> AEROFOIL = EnumProperty.create("aerofoil", WarframeVerticalSlabPosition.class);

    protected static final VoxelShape WEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D);
    protected static final VoxelShape EAST_AABB = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape CENTER_X_AABB = Block.box(4.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D);
    protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D);
    protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape CENTER_Z_AABB = Block.box(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 12.0D);

    public WarframeVerticalSlabStabilizerBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(AEROFOIL, WarframeVerticalSlabPosition.CENTER));
    }

    private static Direction rightOf(Direction facing) {
        return facing.getClockWise();
    }

    private static WarframeVerticalSlabPosition aerofoilFor(Direction facing, Direction worldSide) {
        return rightOf(facing) == worldSide ? WarframeVerticalSlabPosition.RIGHT : WarframeVerticalSlabPosition.LEFT;
    }

    private static Direction worldSideFor(Direction facing, WarframeVerticalSlabPosition aerofoil) {
        Direction right = rightOf(facing);
        return aerofoil == WarframeVerticalSlabPosition.RIGHT ? right : right.getOpposite();
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Direction facing = pState.getValue(FACING);
        WarframeVerticalSlabPosition aerofoil = pState.getValue(AEROFOIL);

        if (aerofoil == WarframeVerticalSlabPosition.CENTER) {
            return facing.getAxis() == Direction.Axis.Z ? CENTER_X_AABB : CENTER_Z_AABB;
        }

        switch (worldSideFor(facing, aerofoil)) {
            case WEST:
                return WEST_AABB;
            case EAST:
                return EAST_AABB;
            case SOUTH:
                return SOUTH_AABB;
            case NORTH:
            default:
                return NORTH_AABB;
        }
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Vec3 hit = pContext.getClickLocation();
        BlockPos blockpos = pContext.getClickedPos();
        double relX = hit.x - (double) blockpos.getX();
        double relZ = hit.z - (double) blockpos.getZ();


        Direction facing = pContext.getHorizontalDirection().getOpposite();

        WarframeVerticalSlabPosition aerofoil;
        if (facing.getAxis() == Direction.Axis.Z) {
            if (relX < 0.3D) {
                aerofoil = aerofoilFor(facing, Direction.WEST);
            } else if (relX > 0.7D) {
                aerofoil = aerofoilFor(facing, Direction.EAST);
            } else {
                aerofoil = WarframeVerticalSlabPosition.CENTER;
            }
        } else {
            if (relZ < 0.3D) {
                aerofoil = aerofoilFor(facing, Direction.NORTH);
            } else if (relZ > 0.7D) {
                aerofoil = aerofoilFor(facing, Direction.SOUTH);
            } else {
                aerofoil = WarframeVerticalSlabPosition.CENTER;
            }
        }

        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(AEROFOIL, aerofoil);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, AEROFOIL);
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
            net.mcreator.valkyrienwarium.procedures.VerticalWingScriptProcedure.execute(
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