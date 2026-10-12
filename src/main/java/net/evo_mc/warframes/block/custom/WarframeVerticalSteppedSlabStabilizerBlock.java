package net.evo_mc.warframes.block.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class WarframeVerticalSteppedSlabStabilizerBlock extends Block implements WarframeMimicable {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<WarframeVerticalSlabPosition> AEROFOIL =
            EnumProperty.create("aerofoil", WarframeVerticalSlabPosition.class);
    public static final EnumProperty<WarframeHorizontalSlabPosition> STEP =
            EnumProperty.create("step", WarframeHorizontalSlabPosition.class,
                    WarframeHorizontalSlabPosition.BOTTOM, WarframeHorizontalSlabPosition.TOP);

    private final Map<BlockState, VoxelShape> shapesCache;

    public WarframeVerticalSteppedSlabStabilizerBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(AEROFOIL, WarframeVerticalSlabPosition.CENTER)
                .setValue(STEP, WarframeHorizontalSlabPosition.BOTTOM));
        this.shapesCache = this.getShapeForEachState(WarframeVerticalSteppedSlabStabilizerBlock::calculateShape);
    }

    private static Direction rightOf(Direction facing) {
        return facing.getClockWise();
    }

    private static WarframeVerticalSlabPosition aerofoilFor(Direction facing, Direction worldSide) {
        return rightOf(facing) == worldSide ? WarframeVerticalSlabPosition.RIGHT : WarframeVerticalSlabPosition.LEFT;
    }

    private static double[] thicknessBand(Direction facing, WarframeVerticalSlabPosition aerofoil) {
        if (aerofoil == WarframeVerticalSlabPosition.CENTER) {
            return new double[] {4.0D, 12.0D};
        }
        Direction lowSide = facing.getAxis() == Direction.Axis.Z ? Direction.WEST : Direction.NORTH;
        Direction right = rightOf(facing);
        Direction resolvedSide = aerofoil == WarframeVerticalSlabPosition.RIGHT ? right : right.getOpposite();
        return resolvedSide == lowSide ? new double[] {0.0D, 8.0D} : new double[] {8.0D, 16.0D};
    }

    private static VoxelShape finBox(Direction facing, double[] band, Direction lengthHalf, double yMin, double yMax) {
        double lenMin = lengthHalf.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 8.0D : 0.0D;
        double lenMax = lenMin + 8.0D;
        if (facing.getAxis() == Direction.Axis.Z) {
            return Block.box(band[0], yMin, lenMin, band[1], yMax, lenMax);
        }
        return Block.box(lenMin, yMin, band[0], lenMax, yMax, band[1]);
    }

    private static VoxelShape calculateShape(BlockState state) {
        Direction facing = state.getValue(FACING);
        double[] band = thicknessBand(facing, state.getValue(AEROFOIL));
        double stepYMin = state.getValue(STEP) == WarframeHorizontalSlabPosition.TOP ? 8.0D : 0.0D;
        double stepYMax = stepYMin + 8.0D;

        VoxelShape rear = finBox(facing, band, facing.getOpposite(), 0.0D, 16.0D);
        VoxelShape step = finBox(facing, band, facing, stepYMin, stepYMax);
        return Shapes.or(rear, step);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return this.shapesCache.get(pState);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState pState) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, AEROFOIL, STEP);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Vec3 hit = pContext.getClickLocation();
        BlockPos blockpos = pContext.getClickedPos();
        double relX = hit.x - (double) blockpos.getX();
        double relY = hit.y - (double) blockpos.getY();
        double relZ = hit.z - (double) blockpos.getZ();

        Direction facing = pContext.getHorizontalDirection().getOpposite();

        WarframeVerticalSlabPosition aerofoil;
        if (facing.getAxis() == Direction.Axis.Z) {
            if (relX < 0.33D) {
                aerofoil = aerofoilFor(facing, Direction.WEST);
            } else if (relX > 0.67D) {
                aerofoil = aerofoilFor(facing, Direction.EAST);
            } else {
                aerofoil = WarframeVerticalSlabPosition.CENTER;
            }
        } else {
            if (relZ < 0.33D) {
                aerofoil = aerofoilFor(facing, Direction.NORTH);
            } else if (relZ > 0.67D) {
                aerofoil = aerofoilFor(facing, Direction.SOUTH);
            } else {
                aerofoil = WarframeVerticalSlabPosition.CENTER;
            }
        }

        WarframeHorizontalSlabPosition step = relY < 0.5D ? WarframeHorizontalSlabPosition.BOTTOM : WarframeHorizontalSlabPosition.TOP;

        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(AEROFOIL, aerofoil)
                .setValue(STEP, step);
    }


    @Override
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