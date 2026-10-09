package net.evo_mc.warframes.block.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class WarframeHalfScoopRadiatorBlock extends WarframeScoopRadiatorBlock {

    public static final EnumProperty<WarframeScoopPosition> POSITION = EnumProperty.create("position", WarframeScoopPosition.class);

    private static final VoxelShape DOWN_SHAPE = Block.box(0, 0, 0, 16, 9, 16);
    private static final VoxelShape UP_SHAPE = Block.box(0, 7, 0, 16, 16, 16);
    private static final VoxelShape NORTH_SHAPE = Block.box(0, 0, 0, 16, 16, 9);
    private static final VoxelShape SOUTH_SHAPE = Block.box(0, 0, 7, 16, 16, 16);
    private static final VoxelShape WEST_SHAPE = Block.box(0, 0, 0, 9, 16, 16);
    private static final VoxelShape EAST_SHAPE = Block.box(7, 0, 0, 16, 16, 16);

    public WarframeHalfScoopRadiatorBlock(Properties pProperties) {
        super(pProperties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POSITION, WarframeScoopPosition.BOTTOM));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(POSITION);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction clicked = pContext.getClickedFace();
        Direction towardPlayer = pContext.getHorizontalDirection().getOpposite();

        if (clicked == Direction.UP) {
            return defaultBlockState().setValue(FACING, towardPlayer).setValue(POSITION, WarframeScoopPosition.BOTTOM);
        }
        if (clicked == Direction.DOWN) {
            return defaultBlockState().setValue(FACING, towardPlayer).setValue(POSITION, WarframeScoopPosition.TOP);
        }
        if (towardPlayer.getAxis() != clicked.getAxis()) {
            Direction side = clicked.getOpposite();
            WarframeScoopPosition position = side == towardPlayer.getCounterClockWise()
                    ? WarframeScoopPosition.LEFT
                    : WarframeScoopPosition.RIGHT;
            return defaultBlockState().setValue(FACING, towardPlayer).setValue(POSITION, position);
        }

        BlockPos clickedPos = pContext.getClickedPos();
        Vec3 hit = pContext.getClickLocation();
        double relX = hit.x - clickedPos.getX();
        double relY = hit.y - clickedPos.getY();
        double relZ = hit.z - clickedPos.getZ();

        double u;
        if (clicked == Direction.NORTH) {
            u = 1.0 - relX;
        } else if (clicked == Direction.SOUTH) {
            u = relX;
        } else if (clicked == Direction.EAST) {
            u = 1.0 - relZ;
        } else {
            u = relZ;
        }

        boolean verticalWedge = (relY > u) == (relY > 1.0 - u);
        WarframeScoopPosition position;
        if (verticalWedge) {
            position = relY > 0.5 ? WarframeScoopPosition.TOP : WarframeScoopPosition.BOTTOM;
        } else {
            position = u > 0.5 ? WarframeScoopPosition.LEFT : WarframeScoopPosition.RIGHT;
        }
        return defaultBlockState().setValue(FACING, clicked).setValue(POSITION, position);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Direction facing = pState.getValue(FACING);
        Direction side = switch (pState.getValue(POSITION)) {
            case BOTTOM -> Direction.DOWN;
            case TOP -> Direction.UP;
            case LEFT -> facing.getCounterClockWise();
            case RIGHT -> facing.getClockWise();
        };
        return switch (side) {
            case DOWN -> DOWN_SHAPE;
            case UP -> UP_SHAPE;
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
        };
    }

    @Override
    protected void tickRadiator(ServerLevel level, BlockPos pos, BlockState state) {
        net.mcreator.valkyrienwarium.procedures.RamScoopRadiatorTickProcedure.execute(
                level, pos.getX(), pos.getY(), pos.getZ(), state);
    }


}