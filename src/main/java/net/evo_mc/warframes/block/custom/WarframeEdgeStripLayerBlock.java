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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WarframeEdgeStripLayerBlock extends WarframeLayeredBlock {
    public static final EnumProperty<WarframeEdgePosition> POSITION = EnumProperty.create("position", WarframeEdgePosition.class);
    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;

    public WarframeEdgeStripLayerBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(POSITION, WarframeEdgePosition.NORTH)
                .setValue(HALF, Half.BOTTOM));
    }

    @Override
    protected VoxelShape calculateShape(BlockState state) {
        double thickness = state.getValue(LAYERS) * 2.0D;
        WarframeEdgePosition position = state.getValue(POSITION);

        if (position.isCorner()) {
            switch (position) {
                case NORTHEAST:
                    return Block.box(16.0D - thickness, 0.0D, 0.0D, 16.0D, 16.0D, thickness);
                case NORTHWEST:
                    return Block.box(0.0D, 0.0D, 0.0D, thickness, 16.0D, thickness);
                case SOUTHEAST:
                    return Block.box(16.0D - thickness, 0.0D, 16.0D - thickness, 16.0D, 16.0D, 16.0D);
                case SOUTHWEST:
                default:
                    return Block.box(0.0D, 0.0D, 16.0D - thickness, thickness, 16.0D, 16.0D);
            }
        }

        boolean isTop = state.getValue(HALF) == Half.TOP;
        double yMin = isTop ? 16.0D - thickness : 0.0D;
        double yMax = isTop ? 16.0D : thickness;

        switch (position) {
            case NORTH:
                return Block.box(0.0D, yMin, 0.0D, 16.0D, yMax, thickness);
            case SOUTH:
                return Block.box(0.0D, yMin, 16.0D - thickness, 16.0D, yMax, 16.0D);
            case EAST:
                return Block.box(16.0D - thickness, yMin, 0.0D, 16.0D, yMax, 16.0D);
            case WEST:
            default:
                return Block.box(0.0D, yMin, 0.0D, thickness, yMax, 16.0D);
        }
    }

    @Override
    protected BlockState getPlacementOrientation(BlockPlaceContext pContext, BlockState base) {
        BlockPos blockpos = pContext.getClickedPos();
        Direction clickedFace = pContext.getClickedFace();
        Vec3 hit = pContext.getClickLocation();
        double relX = hit.x - (double) blockpos.getX();
        double relY = hit.y - (double) blockpos.getY();
        double relZ = hit.z - (double) blockpos.getZ();

        if (clickedFace == Direction.NORTH) {
            if (relX < 0.33D) {
                return cornerState(base, WarframeEdgePosition.SOUTHWEST);
            } else if (relX > 0.67D) {
                return cornerState(base, WarframeEdgePosition.SOUTHEAST);
            }
        } else if (clickedFace == Direction.EAST) {
            if (relZ < 0.33D) {
                return cornerState(base, WarframeEdgePosition.NORTHWEST);
            } else if (relZ > 0.67D) {
                return cornerState(base, WarframeEdgePosition.SOUTHWEST);
            }
        } else if (clickedFace == Direction.SOUTH) {
            if (relX > 0.67D) {
                return cornerState(base, WarframeEdgePosition.NORTHEAST);
            } else if (relX < 0.33D) {
                return cornerState(base, WarframeEdgePosition.NORTHWEST);
            }
        } else if (clickedFace == Direction.WEST) {
            if (relZ > 0.67D) {
                return cornerState(base, WarframeEdgePosition.SOUTHEAST);
            } else if (relZ < 0.33D) {
                return cornerState(base, WarframeEdgePosition.NORTHEAST);
            }
        }

        WarframeEdgePosition position = switch (pContext.getHorizontalDirection()) {
            case SOUTH -> WarframeEdgePosition.SOUTH;
            case EAST -> WarframeEdgePosition.EAST;
            case WEST -> WarframeEdgePosition.WEST;
            default -> WarframeEdgePosition.NORTH;
        };

        boolean isTop = clickedFace == Direction.DOWN
                || (clickedFace != Direction.UP && relY > 0.5D);

        return base
                .setValue(POSITION, position)
                .setValue(HALF, isTop ? Half.TOP : Half.BOTTOM);
    }

    private static BlockState cornerState(BlockState base, WarframeEdgePosition corner) {
        return base
                .setValue(POSITION, corner)
                .setValue(HALF, Half.BOTTOM);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(POSITION, HALF);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.warframes.layer")
                .withStyle(ChatFormatting.GRAY));
    }
}