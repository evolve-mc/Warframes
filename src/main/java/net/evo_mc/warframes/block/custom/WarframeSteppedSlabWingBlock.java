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

public class WarframeSteppedSlabWingBlock extends Block implements WarframeMimicable {
    public static final DirectionProperty FACING = DirectionProperty.create("orientation", Direction.Plane.HORIZONTAL);
    public static final EnumProperty<WarframeSideType> SIDE =
            EnumProperty.create("side", WarframeSideType.class, WarframeSideType.LEFT, WarframeSideType.RIGHT);
    public static final EnumProperty<WarframeHorizontalSlabPositionDouble> TYPE =
            EnumProperty.create("type", WarframeHorizontalSlabPositionDouble.class,
                    WarframeHorizontalSlabPositionDouble.BOTTOM, WarframeHorizontalSlabPositionDouble.CENTER, WarframeHorizontalSlabPositionDouble.TOP);

    private final Map<BlockState, VoxelShape> shapesCache;

    public WarframeSteppedSlabWingBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(SIDE, WarframeSideType.LEFT)
                .setValue(TYPE, WarframeHorizontalSlabPositionDouble.BOTTOM));
        this.shapesCache = this.getShapeForEachState(WarframeSteppedSlabWingBlock::calculateShape);
    }

    private static VoxelShape calculateShape(BlockState state) {
        Direction facing = state.getValue(FACING);
        Direction notchSide = state.getValue(SIDE) == WarframeSideType.LEFT
                ? facing.getCounterClockWise()
                : facing.getClockWise();
        Direction notchDepth = facing.getOpposite();

        double minY;
        double maxY;
        switch (state.getValue(TYPE)) {
            case TOP -> {
                minY = 8.0D;
                maxY = 16.0D;
            }
            case CENTER -> {
                minY = 4.0D;
                maxY = 12.0D;
            }
            default -> {
                minY = 0.0D;
                maxY = 8.0D;
            }
        }

        VoxelShape fullHalf = sectionBox(notchSide.getOpposite(), null, minY, maxY);
        VoxelShape stepQuarter = sectionBox(notchSide, notchDepth.getOpposite(), minY, maxY);
        return Shapes.or(fullHalf, stepQuarter);
    }

    private static VoxelShape sectionBox(Direction first, @Nullable Direction second, double minY, double maxY) {
        double[] min = {0.0D, minY, 0.0D};
        double[] max = {16.0D, maxY, 16.0D};
        restrictToHalf(min, max, first);
        if (second != null) {
            restrictToHalf(min, max, second);
        }
        return Block.box(min[0], min[1], min[2], max[0], max[1], max[2]);
    }

    private static void restrictToHalf(double[] min, double[] max, Direction direction) {
        int index = direction.getAxis().ordinal();
        if (direction.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            min[index] = 8.0D;
        } else {
            max[index] = 8.0D;
        }
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
        pBuilder.add(FACING, SIDE, TYPE);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockPos blockpos = pContext.getClickedPos();
        Direction clickedFace = pContext.getClickedFace();
        Direction facing = clickedFace.getAxis().isHorizontal()
                ? clickedFace.getOpposite()
                : pContext.getHorizontalDirection();

        Vec3 hit = pContext.getClickLocation();
        double relX = hit.x - (double) blockpos.getX();
        double relY = hit.y - (double) blockpos.getY();
        double relZ = hit.z - (double) blockpos.getZ();

        Direction right = facing.getClockWise();
        double along = right.getAxis().choose(relX, relY, relZ);
        boolean clickedRight = right.getAxisDirection() == Direction.AxisDirection.POSITIVE
                ? along > 0.5D
                : along < 0.5D;
        WarframeSideType side = clickedRight ? WarframeSideType.LEFT : WarframeSideType.RIGHT;

        WarframeHorizontalSlabPositionDouble type;
        if (clickedFace == Direction.UP) {
            type = WarframeHorizontalSlabPositionDouble.BOTTOM;
        } else if (clickedFace == Direction.DOWN) {
            type = WarframeHorizontalSlabPositionDouble.TOP;
        } else if (relY < 0.3D) {
            type = WarframeHorizontalSlabPositionDouble.BOTTOM;
        } else if (relY > 0.7D) {
            type = WarframeHorizontalSlabPositionDouble.TOP;
        } else {
            type = WarframeHorizontalSlabPositionDouble.CENTER;
        }

        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(SIDE, side)
                .setValue(TYPE, type);
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
            net.mcreator.valkyrienwarium.procedures.HorizontalWingScriptProcedure.execute(
                    pLevel, pPos.getX(), pPos.getY(), pPos.getZ(), pState);
        }
        pLevel.scheduleTick(pPos, this, 1);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.warframes.asymmetric_aerofoil").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("tooltip.warframes.provides_lift").withStyle(ChatFormatting.GRAY));
    }
}