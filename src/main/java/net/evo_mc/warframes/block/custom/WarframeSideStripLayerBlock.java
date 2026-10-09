package net.evo_mc.warframes.block.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WarframeSideStripLayerBlock extends WarframeLayeredBlock {
    public static final EnumProperty<WarframeSideStripPosition> POSITION =
            EnumProperty.create("position", WarframeSideStripPosition.class);

    public WarframeSideStripLayerBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(POSITION, WarframeSideStripPosition.DOWN_X));
    }

    @Override
    protected VoxelShape calculateShape(BlockState state) {
        WarframeSideStripPosition stripPosition = state.getValue(POSITION);
        Direction side = sideOf(stripPosition);
        Direction.Axis run = axisOf(stripPosition);
        double size = state.getValue(LAYERS) * 2.0D;
        double[] min = {0.0D, 0.0D, 0.0D};
        double[] max = {16.0D, 16.0D, 16.0D};

        int attachedIndex = side.getAxis().ordinal();
        if (side.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
            min[attachedIndex] = 16.0D - size;
        } else {
            max[attachedIndex] = size;
        }

        for (Direction.Axis axis : Direction.Axis.values()) {
            if (axis != run && axis != side.getAxis()) {
                min[axis.ordinal()] = 8.0D - size / 2.0D;
                max[axis.ordinal()] = 8.0D + size / 2.0D;
            }
        }
        return Block.box(min[0], min[1], min[2], max[0], max[1], max[2]);
    }

    @Override
    protected BlockState getPlacementOrientation(BlockPlaceContext pContext, BlockState base) {
        BlockPos blockpos = pContext.getClickedPos();
        Direction clickedFace = pContext.getClickedFace();
        Direction side = clickedFace.getOpposite();
        Direction.Axis excluded = side.getAxis();

        Vec3 hit = pContext.getClickLocation();
        double relX = hit.x - (double) blockpos.getX();
        double relY = hit.y - (double) blockpos.getY();
        double relZ = hit.z - (double) blockpos.getZ();

        Direction.Axis axis;
        if (excluded == Direction.Axis.Y) {
            axis = pContext.getHorizontalDirection().getAxis();
        } else if (clickedFace == Direction.NORTH || clickedFace == Direction.SOUTH) {
            boolean isVerticalWedge = (relY > relX) == (relY > 1.0D - relX);
            axis = isVerticalWedge ? Direction.Axis.Y : Direction.Axis.X;
        } else {
            boolean isVerticalWedge = (relY > relZ) == (relY > 1.0D - relZ);
            axis = isVerticalWedge ? Direction.Axis.Y : Direction.Axis.Z;
        }

        return base.setValue(POSITION, toPosition(side, axis));
    }

    private static WarframeSideStripPosition toPosition(Direction side, Direction.Axis axis) {
        return switch (side) {
            case DOWN -> axis == Direction.Axis.X ? WarframeSideStripPosition.DOWN_X : WarframeSideStripPosition.DOWN_Z;
            case UP -> axis == Direction.Axis.X ? WarframeSideStripPosition.UP_X : WarframeSideStripPosition.UP_Z;
            case NORTH -> axis == Direction.Axis.Y ? WarframeSideStripPosition.NORTH_Y : WarframeSideStripPosition.NORTH_X;
            case SOUTH -> axis == Direction.Axis.Y ? WarframeSideStripPosition.SOUTH_Y : WarframeSideStripPosition.SOUTH_X;
            case EAST -> axis == Direction.Axis.Y ? WarframeSideStripPosition.EAST_Y : WarframeSideStripPosition.EAST_Z;
            case WEST -> axis == Direction.Axis.Y ? WarframeSideStripPosition.WEST_Y : WarframeSideStripPosition.WEST_Z;
        };
    }

    private static Direction sideOf(WarframeSideStripPosition position) {
        return switch (position) {
            case DOWN_X, DOWN_Z -> Direction.DOWN;
            case UP_X, UP_Z -> Direction.UP;
            case NORTH_X, NORTH_Y -> Direction.NORTH;
            case SOUTH_X, SOUTH_Y -> Direction.SOUTH;
            case EAST_Y, EAST_Z -> Direction.EAST;
            case WEST_Y, WEST_Z -> Direction.WEST;
        };
    }

    private static Direction.Axis axisOf(WarframeSideStripPosition position) {
        return switch (position) {
            case DOWN_X, UP_X, NORTH_X, SOUTH_X -> Direction.Axis.X;
            case NORTH_Y, SOUTH_Y, EAST_Y, WEST_Y -> Direction.Axis.Y;
            case DOWN_Z, UP_Z, EAST_Z, WEST_Z -> Direction.Axis.Z;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(POSITION);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.warframes.layer")
                .withStyle(ChatFormatting.GRAY));
    }
}