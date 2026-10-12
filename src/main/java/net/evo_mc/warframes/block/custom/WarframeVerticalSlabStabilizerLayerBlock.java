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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
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

public class WarframeVerticalSlabStabilizerLayerBlock extends Block implements WarframeMimicable {
    public static final int MAX_LAYERS = 8;
    public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<WarframeVerticalSlabPosition> AEROFOIL =
            EnumProperty.create("aerofoil", WarframeVerticalSlabPosition.class);

    public WarframeVerticalSlabStabilizerLayerBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(LAYERS, Integer.valueOf(1))
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

    private VoxelShape getShapeFor(BlockState pState) {
        Direction facing = pState.getValue(FACING);
        WarframeVerticalSlabPosition aerofoil = pState.getValue(AEROFOIL);
        int layers = pState.getValue(LAYERS);
        double depth = layers * 2.0D;

        double xMin = 0.0D, xMax = 16.0D, zMin = 0.0D, zMax = 16.0D;
        switch (facing) {
            case SOUTH:
                zMin = 0.0D;
                zMax = depth;
                break;
            case NORTH:
                zMin = 16.0D - depth;
                zMax = 16.0D;
                break;
            case EAST:
                xMin = 0.0D;
                xMax = depth;
                break;
            case WEST:
            default:
                xMin = 16.0D - depth;
                xMax = 16.0D;
                break;
        }

        double bandLow, bandHigh;
        if (aerofoil == WarframeVerticalSlabPosition.CENTER) {
            bandLow = 4.0D;
            bandHigh = 12.0D;
        } else {
            Direction lowSide = facing.getAxis() == Direction.Axis.Z ? Direction.WEST : Direction.NORTH;
            Direction resolvedSide = worldSideFor(facing, aerofoil);
            if (resolvedSide == lowSide) {
                bandLow = 0.0D;
                bandHigh = 8.0D;
            } else {
                bandLow = 8.0D;
                bandHigh = 16.0D;
            }
        }

        if (facing.getAxis() == Direction.Axis.Z) {
            xMin = bandLow;
            xMax = bandHigh;
        } else {
            zMin = bandLow;
            zMax = bandHigh;
        }

        return Block.box(xMin, 0.0D, zMin, xMax, 16.0D, zMax);
    }

    public boolean useShapeForLightOcclusion(BlockState pState) {
        return true;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, AEROFOIL, LAYERS);
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return getShapeFor(pState);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return getShapeFor(pState);
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return getShapeFor(pState);
    }

    @Override
    public VoxelShape getVisualShape(BlockState pState, BlockGetter pReader, BlockPos pPos, CollisionContext pContext) {
        return getShapeFor(pState);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockPos blockpos = pContext.getClickedPos();
        BlockState existing = pContext.getLevel().getBlockState(blockpos);

        if (existing.is(this)) {
            int layers = existing.getValue(LAYERS);
            return existing.setValue(LAYERS, Integer.valueOf(Math.min(MAX_LAYERS, layers + 1)));
        }

        Vec3 hit = pContext.getClickLocation();
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
                .setValue(AEROFOIL, aerofoil)
                .setValue(LAYERS, Integer.valueOf(1));
    }

    public boolean canBeReplaced(BlockState pState, BlockPlaceContext pUseContext) {
        return !pUseContext.isSecondaryUseActive()
                && pUseContext.getItemInHand().getItem() == this.asItem()
                && pState.getValue(LAYERS) < MAX_LAYERS
                ? true
                : super.canBeReplaced(pState, pUseContext);
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
        tooltip.add(Component.translatable("tooltip.warframes.layer")
                .withStyle(ChatFormatting.GRAY));
    }
}