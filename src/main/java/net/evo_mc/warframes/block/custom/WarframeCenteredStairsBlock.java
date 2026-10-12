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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class WarframeCenteredStairsBlock extends Block implements WarframeMimicable {

    public static final EnumProperty<WarframeCenteredStairPosition> POSITION =
            EnumProperty.create("position", WarframeCenteredStairPosition.class);


    protected static final VoxelShape DOWN_BASE = Block.box(0, 0, 0, 16, 8, 16);
    protected static final VoxelShape UP_BASE = Block.box(0, 8, 0, 16, 16, 16);
    protected static final VoxelShape NORTH_BASE = Block.box(0, 0, 0, 16, 16, 8);
    protected static final VoxelShape SOUTH_BASE = Block.box(0, 0, 8, 16, 16, 16);
    protected static final VoxelShape EAST_BASE = Block.box(8, 0, 0, 16, 16, 16);
    protected static final VoxelShape WEST_BASE = Block.box(0, 0, 0, 8, 16, 16);

    protected static final VoxelShape DOWN_X_STRIP = Block.box(0, 8, 4, 16, 16, 12);
    protected static final VoxelShape DOWN_Z_STRIP = Block.box(4, 8, 0, 12, 16, 16);
    protected static final VoxelShape UP_X_STRIP = Block.box(0, 0, 4, 16, 8, 12);
    protected static final VoxelShape UP_Z_STRIP = Block.box(4, 0, 0, 12, 8, 16);
    protected static final VoxelShape NORTH_Y_STRIP = Block.box(4, 0, 8, 12, 16, 16);
    protected static final VoxelShape NORTH_Z_STRIP = Block.box(0, 4, 8, 16, 12, 16);
    protected static final VoxelShape SOUTH_Y_STRIP = Block.box(4, 0, 0, 12, 16, 8);
    protected static final VoxelShape SOUTH_Z_STRIP = Block.box(0, 4, 0, 16, 12, 8);
    protected static final VoxelShape EAST_Y_STRIP = Block.box(0, 0, 4, 8, 16, 12);
    protected static final VoxelShape EAST_X_STRIP = Block.box(0, 4, 0, 8, 12, 16);
    protected static final VoxelShape WEST_Y_STRIP = Block.box(8, 0, 4, 16, 16, 12);
    protected static final VoxelShape WEST_X_STRIP = Block.box(8, 4, 0, 16, 12, 16);

    protected static final VoxelShape DOWN_X = Shapes.or(DOWN_BASE, DOWN_X_STRIP);
    protected static final VoxelShape DOWN_Z = Shapes.or(DOWN_BASE, DOWN_Z_STRIP);
    protected static final VoxelShape UP_X = Shapes.or(UP_BASE, UP_X_STRIP);
    protected static final VoxelShape UP_Z = Shapes.or(UP_BASE, UP_Z_STRIP);
    protected static final VoxelShape NORTH_Y = Shapes.or(NORTH_BASE, NORTH_Y_STRIP);
    protected static final VoxelShape NORTH_Z = Shapes.or(NORTH_BASE, NORTH_Z_STRIP);
    protected static final VoxelShape SOUTH_Y = Shapes.or(SOUTH_BASE, SOUTH_Y_STRIP);
    protected static final VoxelShape SOUTH_Z = Shapes.or(SOUTH_BASE, SOUTH_Z_STRIP);
    protected static final VoxelShape EAST_Y = Shapes.or(EAST_BASE, EAST_Y_STRIP);
    protected static final VoxelShape EAST_X = Shapes.or(EAST_BASE, EAST_X_STRIP);
    protected static final VoxelShape WEST_Y = Shapes.or(WEST_BASE, WEST_Y_STRIP);
    protected static final VoxelShape WEST_X = Shapes.or(WEST_BASE, WEST_X_STRIP);

    public WarframeCenteredStairsBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(POSITION, WarframeCenteredStairPosition.DOWN_X));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return switch (pState.getValue(POSITION)) {
            case DOWN_X -> DOWN_X;
            case DOWN_Z -> DOWN_Z;
            case UP_X -> UP_X;
            case UP_Z -> UP_Z;
            case NORTH_Y -> NORTH_Y;
            case NORTH_Z -> NORTH_Z;
            case SOUTH_Y -> SOUTH_Y;
            case SOUTH_Z -> SOUTH_Z;
            case EAST_Y -> EAST_Y;
            case EAST_X -> EAST_X;
            case WEST_Y -> WEST_Y;
            case WEST_X -> WEST_X;
        };
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState pState) {
        return true;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction side = pContext.getClickedFace().getOpposite();
        Direction clickedFace = pContext.getClickedFace();
        Direction.Axis excluded = side.getAxis();

        Vec3 hit = pContext.getClickLocation();
        BlockPos blockpos = pContext.getClickedPos();
        double relX = hit.x - (double) blockpos.getX();
        double relY = hit.y - (double) blockpos.getY();
        double relZ = hit.z - (double) blockpos.getZ();

        Direction.Axis axis;

        if (excluded == Direction.Axis.Y) {
            axis = pContext.getHorizontalDirection().getAxis();
        } else if (clickedFace == Direction.NORTH || clickedFace == Direction.SOUTH) {
            boolean isVerticalWedge = (relY > relX) == (relY > 1.0D - relX);
            axis = isVerticalWedge ? Direction.Axis.Y : Direction.Axis.Z;
        } else {
            boolean isVerticalWedge = (relY > relZ) == (relY > 1.0D - relZ);
            axis = isVerticalWedge ? Direction.Axis.Y : Direction.Axis.X;
        }


        return this.defaultBlockState()
                .setValue(POSITION, toPosition(side, axis));

    }

    private static WarframeCenteredStairPosition toPosition(Direction side, Direction.Axis axis) {
        return switch (side) {
            case DOWN -> axis == Direction.Axis.X ? WarframeCenteredStairPosition.DOWN_X : WarframeCenteredStairPosition.DOWN_Z;
            case UP -> axis == Direction.Axis.X ? WarframeCenteredStairPosition.UP_X : WarframeCenteredStairPosition.UP_Z;
            case NORTH -> axis == Direction.Axis.Y ? WarframeCenteredStairPosition.NORTH_Y : WarframeCenteredStairPosition.NORTH_Z;
            case SOUTH -> axis == Direction.Axis.Y ? WarframeCenteredStairPosition.SOUTH_Y : WarframeCenteredStairPosition.SOUTH_Z;
            case EAST -> axis == Direction.Axis.Y ? WarframeCenteredStairPosition.EAST_Y : WarframeCenteredStairPosition.EAST_X;
            case WEST -> axis == Direction.Axis.Y ? WarframeCenteredStairPosition.WEST_Y : WarframeCenteredStairPosition.WEST_X;
        };
    }



    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(POSITION);
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
}