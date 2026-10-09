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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WarframeLayerBlock extends WarframeLayeredBlock {
    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.values());

    public WarframeLayerBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.DOWN));
    }

    @Override
    protected VoxelShape calculateShape(BlockState state) {
        double height = state.getValue(LAYERS) * 2.0D;
        return switch (state.getValue(FACING)) {
            case DOWN -> Block.box(0.0D, 16.0D - height, 0.0D, 16.0D, 16.0D, 16.0D);
            case UP -> Block.box(0.0D, 0.0D, 0.0D, 16.0D, height, 16.0D);
            case NORTH -> Block.box(0.0D, 0.0D, 16.0D - height, 16.0D, 16.0D, 16.0D);
            case SOUTH -> Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, height);
            case EAST -> Block.box(0.0D, 0.0D, 0.0D, height, 16.0D, 16.0D);
            case WEST -> Block.box(16.0D - height, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
        };
    }

    @Override
    protected BlockState getPlacementOrientation(BlockPlaceContext pContext, BlockState base) {
        return base.setValue(FACING, pContext.getClickedFace());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(FACING);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.warframes.layer")
                .withStyle(ChatFormatting.GRAY));
    }
}