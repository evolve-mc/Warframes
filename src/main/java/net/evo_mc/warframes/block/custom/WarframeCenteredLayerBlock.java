package net.evo_mc.warframes.block.custom;

import net.minecraft.ChatFormatting;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WarframeCenteredLayerBlock extends WarframeLayeredBlock {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;

    public WarframeCenteredLayerBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(AXIS, Direction.Axis.Y));
    }

    @Override
    protected VoxelShape calculateShape(BlockState state) {
        double half = state.getValue(LAYERS);
        double min = 8.0D - half;
        double max = 8.0D + half;
        return switch (state.getValue(AXIS)) {
            case X -> Block.box(min, 0.0D, 0.0D, max, 16.0D, 16.0D);
            case Z -> Block.box(0.0D, 0.0D, min, 16.0D, 16.0D, max);
            case Y -> Block.box(0.0D, min, 0.0D, 16.0D, max, 16.0D);
        };
    }

    @Override
    protected BlockState getPlacementOrientation(BlockPlaceContext pContext, BlockState base) {
        Direction clickedFace = pContext.getClickedFace();
        Direction.Axis axis;
        if (clickedFace == Direction.UP || clickedFace == Direction.DOWN) {
            Direction.Axis playerAxis = pContext.getHorizontalDirection().getAxis();
            axis = playerAxis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
        } else {
            axis = Direction.Axis.Y;
        }
        return base.setValue(AXIS, axis);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(AXIS);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.warframes.layer")
                .withStyle(ChatFormatting.GRAY));
    }
}