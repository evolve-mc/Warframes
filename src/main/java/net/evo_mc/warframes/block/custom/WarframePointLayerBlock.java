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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WarframePointLayerBlock extends WarframeLayeredBlock {
    public static final EnumProperty<WarframeHorizontalSlabPosition> HALF =
            EnumProperty.create("half", WarframeHorizontalSlabPosition.class);
    public static final EnumProperty<WarframeCompassPosition> POSITION =
            EnumProperty.create("position", WarframeCompassPosition.class);

    protected static final VoxelShape[] NORTHEAST_BOTTOM = new VoxelShape[]{
            Shapes.empty(),
            Block.box(14.0D, 0.0D, 0.0D, 16.0D, 2.0D, 2.0D),
            Block.box(12.0D, 0.0D, 0.0D, 16.0D, 4.0D, 4.0D),
            Block.box(10.0D, 0.0D, 0.0D, 16.0D, 6.0D, 6.0D),
            Block.box(8.0D, 0.0D, 0.0D, 16.0D, 8.0D, 8.0D),
            Block.box(6.0D, 0.0D, 0.0D, 16.0D, 10.0D, 10.0D),
            Block.box(4.0D, 0.0D, 0.0D, 16.0D, 12.0D, 12.0D),
            Block.box(2.0D, 0.0D, 0.0D, 16.0D, 14.0D, 14.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    protected static final VoxelShape[] NORTHEAST_CENTER = new VoxelShape[]{
            Shapes.empty(),
            Block.box(14.0D, 7.0D, 0.0D, 16.0D, 9.0D, 2.0D),
            Block.box(12.0D, 6.0D, 0.0D, 16.0D, 10.0D, 4.0D),
            Block.box(10.0D, 5.0D, 0.0D, 16.0D, 11.0D, 6.0D),
            Block.box(8.0D, 4.0D, 0.0D, 16.0D, 12.0D, 8.0D),
            Block.box(6.0D, 3.0D, 0.0D, 16.0D, 13.0D, 10.0D),
            Block.box(4.0D, 2.0D, 0.0D, 16.0D, 14.0D, 12.0D),
            Block.box(2.0D, 1.0D, 0.0D, 16.0D, 15.0D, 14.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    protected static final VoxelShape[] NORTHEAST_TOP = new VoxelShape[]{
            Shapes.empty(),
            Block.box(14.0D, 14.0D, 0.0D, 16.0D, 16.0D, 2.0D),
            Block.box(12.0D, 12.0D, 0.0D, 16.0D, 16.0D, 4.0D),
            Block.box(10.0D, 10.0D, 0.0D, 16.0D, 16.0D, 6.0D),
            Block.box(8.0D, 8.0D, 0.0D, 16.0D, 16.0D, 8.0D),
            Block.box(6.0D, 6.0D, 0.0D, 16.0D, 16.0D, 10.0D),
            Block.box(4.0D, 4.0D, 0.0D, 16.0D, 16.0D, 12.0D),
            Block.box(2.0D, 2.0D, 0.0D, 16.0D, 16.0D, 14.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    protected static final VoxelShape[] NORTHWEST_BOTTOM = new VoxelShape[]{
            Shapes.empty(),
            Block.box(0.0D, 0.0D, 0.0D, 2.0D, 2.0D, 2.0D),
            Block.box(0.0D, 0.0D, 0.0D, 4.0D, 4.0D, 4.0D),
            Block.box(0.0D, 0.0D, 0.0D, 6.0D, 6.0D, 6.0D),
            Block.box(0.0D, 0.0D, 0.0D, 8.0D, 8.0D, 8.0D),
            Block.box(0.0D, 0.0D, 0.0D, 10.0D, 10.0D, 10.0D),
            Block.box(0.0D, 0.0D, 0.0D, 12.0D, 12.0D, 12.0D),
            Block.box(0.0D, 0.0D, 0.0D, 14.0D, 14.0D, 14.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    protected static final VoxelShape[] NORTHWEST_CENTER = new VoxelShape[]{
            Shapes.empty(),
            Block.box(0.0D, 7.0D, 0.0D, 2.0D, 9.0D, 2.0D),
            Block.box(0.0D, 6.0D, 0.0D, 4.0D, 10.0D, 4.0D),
            Block.box(0.0D, 5.0D, 0.0D, 6.0D, 11.0D, 6.0D),
            Block.box(0.0D, 4.0D, 0.0D, 8.0D, 12.0D, 8.0D),
            Block.box(0.0D, 3.0D, 0.0D, 10.0D, 13.0D, 10.0D),
            Block.box(0.0D, 2.0D, 0.0D, 12.0D, 14.0D, 12.0D),
            Block.box(0.0D, 1.0D, 0.0D, 14.0D, 15.0D, 14.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    protected static final VoxelShape[] NORTHWEST_TOP = new VoxelShape[]{
            Shapes.empty(),
            Block.box(0.0D, 14.0D, 0.0D, 2.0D, 16.0D, 2.0D),
            Block.box(0.0D, 12.0D, 0.0D, 4.0D, 16.0D, 4.0D),
            Block.box(0.0D, 10.0D, 0.0D, 6.0D, 16.0D, 6.0D),
            Block.box(0.0D, 8.0D, 0.0D, 8.0D, 16.0D, 8.0D),
            Block.box(0.0D, 6.0D, 0.0D, 10.0D, 16.0D, 10.0D),
            Block.box(0.0D, 4.0D, 0.0D, 12.0D, 16.0D, 12.0D),
            Block.box(0.0D, 2.0D, 0.0D, 14.0D, 16.0D, 14.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    protected static final VoxelShape[] SOUTHEAST_BOTTOM = new VoxelShape[]{
            Shapes.empty(),
            Block.box(14.0D, 0.0D, 14.0D, 16.0D, 2.0D, 16.0D),
            Block.box(12.0D, 0.0D, 12.0D, 16.0D, 4.0D, 16.0D),
            Block.box(10.0D, 0.0D, 10.0D, 16.0D, 6.0D, 16.0D),
            Block.box(8.0D, 0.0D, 8.0D, 16.0D, 8.0D, 16.0D),
            Block.box(6.0D, 0.0D, 6.0D, 16.0D, 10.0D, 16.0D),
            Block.box(4.0D, 0.0D, 4.0D, 16.0D, 12.0D, 16.0D),
            Block.box(2.0D, 0.0D, 2.0D, 16.0D, 14.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    protected static final VoxelShape[] SOUTHEAST_CENTER = new VoxelShape[]{
            Shapes.empty(),
            Block.box(14.0D, 7.0D, 14.0D, 16.0D, 9.0D, 16.0D),
            Block.box(12.0D, 6.0D, 12.0D, 16.0D, 10.0D, 16.0D),
            Block.box(10.0D, 5.0D, 10.0D, 16.0D, 11.0D, 16.0D),
            Block.box(8.0D, 4.0D, 8.0D, 16.0D, 12.0D, 16.0D),
            Block.box(6.0D, 3.0D, 6.0D, 16.0D, 13.0D, 16.0D),
            Block.box(4.0D, 2.0D, 4.0D, 16.0D, 14.0D, 16.0D),
            Block.box(2.0D, 1.0D, 2.0D, 16.0D, 15.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    protected static final VoxelShape[] SOUTHEAST_TOP = new VoxelShape[]{
            Shapes.empty(),
            Block.box(14.0D, 14.0D, 14.0D, 16.0D, 16.0D, 16.0D),
            Block.box(12.0D, 12.0D, 12.0D, 16.0D, 16.0D, 16.0D),
            Block.box(10.0D, 10.0D, 10.0D, 16.0D, 16.0D, 16.0D),
            Block.box(8.0D, 8.0D, 8.0D, 16.0D, 16.0D, 16.0D),
            Block.box(6.0D, 6.0D, 6.0D, 16.0D, 16.0D, 16.0D),
            Block.box(4.0D, 4.0D, 4.0D, 16.0D, 16.0D, 16.0D),
            Block.box(2.0D, 2.0D, 2.0D, 16.0D, 16.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    protected static final VoxelShape[] SOUTHWEST_BOTTOM = new VoxelShape[]{
            Shapes.empty(),
            Block.box(0.0D, 0.0D, 14.0D, 2.0D, 2.0D, 16.0D),
            Block.box(0.0D, 0.0D, 12.0D, 4.0D, 4.0D, 16.0D),
            Block.box(0.0D, 0.0D, 10.0D, 6.0D, 6.0D, 16.0D),
            Block.box(0.0D, 0.0D, 8.0D, 8.0D, 8.0D, 16.0D),
            Block.box(0.0D, 0.0D, 6.0D, 10.0D, 10.0D, 16.0D),
            Block.box(0.0D, 0.0D, 4.0D, 12.0D, 12.0D, 16.0D),
            Block.box(0.0D, 0.0D, 2.0D, 14.0D, 14.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    protected static final VoxelShape[] SOUTHWEST_CENTER = new VoxelShape[]{
            Shapes.empty(),
            Block.box(0.0D, 7.0D, 14.0D, 2.0D, 9.0D, 16.0D),
            Block.box(0.0D, 6.0D, 12.0D, 4.0D, 10.0D, 16.0D),
            Block.box(0.0D, 5.0D, 10.0D, 6.0D, 11.0D, 16.0D),
            Block.box(0.0D, 4.0D, 8.0D, 8.0D, 12.0D, 16.0D),
            Block.box(0.0D, 3.0D, 6.0D, 10.0D, 13.0D, 16.0D),
            Block.box(0.0D, 2.0D, 4.0D, 12.0D, 14.0D, 16.0D),
            Block.box(0.0D, 1.0D, 2.0D, 14.0D, 15.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    protected static final VoxelShape[] SOUTHWEST_TOP = new VoxelShape[]{
            Shapes.empty(),
            Block.box(0.0D, 14.0D, 14.0D, 2.0D, 16.0D, 16.0D),
            Block.box(0.0D, 12.0D, 12.0D, 4.0D, 16.0D, 16.0D),
            Block.box(0.0D, 10.0D, 10.0D, 6.0D, 16.0D, 16.0D),
            Block.box(0.0D, 8.0D, 8.0D, 8.0D, 16.0D, 16.0D),
            Block.box(0.0D, 6.0D, 6.0D, 10.0D, 16.0D, 16.0D),
            Block.box(0.0D, 4.0D, 4.0D, 12.0D, 16.0D, 16.0D),
            Block.box(0.0D, 2.0D, 2.0D, 14.0D, 16.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    protected static final VoxelShape[] NORTH_BOTTOM = new VoxelShape[]{
            Shapes.empty(),
            Block.box(7.0D, 0.0D, 0.0D, 9.0D, 2.0D, 2.0D),
            Block.box(6.0D, 0.0D, 0.0D, 10.0D, 4.0D, 4.0D),
            Block.box(5.0D, 0.0D, 0.0D, 11.0D, 6.0D, 6.0D),
            Block.box(4.0D, 0.0D, 0.0D, 12.0D, 8.0D, 8.0D),
            Block.box(3.0D, 0.0D, 0.0D, 13.0D, 10.0D, 10.0D),
            Block.box(2.0D, 0.0D, 0.0D, 14.0D, 12.0D, 12.0D),
            Block.box(1.0D, 0.0D, 0.0D, 15.0D, 14.0D, 14.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    protected static final VoxelShape[] NORTH_CENTER = new VoxelShape[]{
            Shapes.empty(),
            Block.box(7.0D, 7.0D, 0.0D, 9.0D, 9.0D, 2.0D),
            Block.box(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 4.0D),
            Block.box(5.0D, 5.0D, 0.0D, 11.0D, 11.0D, 6.0D),
            Block.box(4.0D, 4.0D, 0.0D, 12.0D, 12.0D, 8.0D),
            Block.box(3.0D, 3.0D, 0.0D, 13.0D, 13.0D, 10.0D),
            Block.box(2.0D, 2.0D, 0.0D, 14.0D, 14.0D, 12.0D),
            Block.box(1.0D, 1.0D, 0.0D, 15.0D, 15.0D, 14.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    protected static final VoxelShape[] NORTH_TOP = new VoxelShape[]{
            Shapes.empty(),
            Block.box(7.0D, 14.0D, 0.0D, 9.0D, 16.0D, 2.0D),
            Block.box(6.0D, 12.0D, 0.0D, 10.0D, 16.0D, 4.0D),
            Block.box(5.0D, 10.0D, 0.0D, 11.0D, 16.0D, 6.0D),
            Block.box(4.0D, 8.0D, 0.0D, 12.0D, 16.0D, 8.0D),
            Block.box(3.0D, 6.0D, 0.0D, 13.0D, 16.0D, 10.0D),
            Block.box(2.0D, 4.0D, 0.0D, 14.0D, 16.0D, 12.0D),
            Block.box(1.0D, 2.0D, 0.0D, 15.0D, 16.0D, 14.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    protected static final VoxelShape[] SOUTH_BOTTOM = new VoxelShape[]{
            Shapes.empty(),
            Block.box(7.0D, 0.0D, 14.0D, 9.0D, 2.0D, 16.0D),
            Block.box(6.0D, 0.0D, 12.0D, 10.0D, 4.0D, 16.0D),
            Block.box(5.0D, 0.0D, 10.0D, 11.0D, 6.0D, 16.0D),
            Block.box(4.0D, 0.0D, 8.0D, 12.0D, 8.0D, 16.0D),
            Block.box(3.0D, 0.0D, 6.0D, 13.0D, 10.0D, 16.0D),
            Block.box(2.0D, 0.0D, 4.0D, 14.0D, 12.0D, 16.0D),
            Block.box(1.0D, 0.0D, 2.0D, 15.0D, 14.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    protected static final VoxelShape[] SOUTH_CENTER = new VoxelShape[]{
            Shapes.empty(),
            Block.box(7.0D, 7.0D, 14.0D, 9.0D, 9.0D, 16.0D),
            Block.box(6.0D, 6.0D, 12.0D, 10.0D, 10.0D, 16.0D),
            Block.box(5.0D, 5.0D, 10.0D, 11.0D, 11.0D, 16.0D),
            Block.box(4.0D, 4.0D, 8.0D, 12.0D, 12.0D, 16.0D),
            Block.box(3.0D, 3.0D, 6.0D, 13.0D, 13.0D, 16.0D),
            Block.box(2.0D, 2.0D, 4.0D, 14.0D, 14.0D, 16.0D),
            Block.box(1.0D, 1.0D, 2.0D, 15.0D, 15.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    protected static final VoxelShape[] SOUTH_TOP = new VoxelShape[]{
            Shapes.empty(),
            Block.box(7.0D, 14.0D, 14.0D, 9.0D, 16.0D, 16.0D),
            Block.box(6.0D, 12.0D, 12.0D, 10.0D, 16.0D, 16.0D),
            Block.box(5.0D, 10.0D, 10.0D, 11.0D, 16.0D, 16.0D),
            Block.box(4.0D, 8.0D, 8.0D, 12.0D, 16.0D, 16.0D),
            Block.box(3.0D, 6.0D, 6.0D, 13.0D, 16.0D, 16.0D),
            Block.box(2.0D, 4.0D, 4.0D, 14.0D, 16.0D, 16.0D),
            Block.box(1.0D, 2.0D, 2.0D, 15.0D, 16.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    protected static final VoxelShape[] EAST_BOTTOM = new VoxelShape[]{
            Shapes.empty(),
            Block.box(14.0D, 0.0D, 7.0D, 16.0D, 2.0D, 9.0D),
            Block.box(12.0D, 0.0D, 6.0D, 16.0D, 4.0D, 10.0D),
            Block.box(10.0D, 0.0D, 5.0D, 16.0D, 6.0D, 11.0D),
            Block.box(8.0D, 0.0D, 4.0D, 16.0D, 8.0D, 12.0D),
            Block.box(6.0D, 0.0D, 3.0D, 16.0D, 10.0D, 13.0D),
            Block.box(4.0D, 0.0D, 2.0D, 16.0D, 12.0D, 14.0D),
            Block.box(2.0D, 0.0D, 1.0D, 16.0D, 14.0D, 15.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    protected static final VoxelShape[] EAST_CENTER = new VoxelShape[]{
            Shapes.empty(),
            Block.box(14.0D, 7.0D, 7.0D, 16.0D, 9.0D, 9.0D),
            Block.box(12.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D),
            Block.box(10.0D, 5.0D, 5.0D, 16.0D, 11.0D, 11.0D),
            Block.box(8.0D, 4.0D, 4.0D, 16.0D, 12.0D, 12.0D),
            Block.box(6.0D, 3.0D, 3.0D, 16.0D, 13.0D, 13.0D),
            Block.box(4.0D, 2.0D, 2.0D, 16.0D, 14.0D, 14.0D),
            Block.box(2.0D, 1.0D, 1.0D, 16.0D, 15.0D, 15.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    protected static final VoxelShape[] EAST_TOP = new VoxelShape[]{
            Shapes.empty(),
            Block.box(14.0D, 14.0D, 7.0D, 16.0D, 16.0D, 9.0D),
            Block.box(12.0D, 12.0D, 6.0D, 16.0D, 16.0D, 10.0D),
            Block.box(10.0D, 10.0D, 5.0D, 16.0D, 16.0D, 11.0D),
            Block.box(8.0D, 8.0D, 4.0D, 16.0D, 16.0D, 12.0D),
            Block.box(6.0D, 6.0D, 3.0D, 16.0D, 16.0D, 13.0D),
            Block.box(4.0D, 4.0D, 2.0D, 16.0D, 16.0D, 14.0D),
            Block.box(2.0D, 2.0D, 1.0D, 16.0D, 16.0D, 15.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    protected static final VoxelShape[] WEST_BOTTOM = new VoxelShape[]{
            Shapes.empty(),
            Block.box(0.0D, 0.0D, 7.0D, 2.0D, 2.0D, 9.0D),
            Block.box(0.0D, 0.0D, 6.0D, 4.0D, 4.0D, 10.0D),
            Block.box(0.0D, 0.0D, 5.0D, 6.0D, 6.0D, 11.0D),
            Block.box(0.0D, 0.0D, 4.0D, 8.0D, 8.0D, 12.0D),
            Block.box(0.0D, 0.0D, 3.0D, 10.0D, 10.0D, 13.0D),
            Block.box(0.0D, 0.0D, 2.0D, 12.0D, 12.0D, 14.0D),
            Block.box(0.0D, 0.0D, 1.0D, 14.0D, 14.0D, 15.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    protected static final VoxelShape[] WEST_CENTER = new VoxelShape[]{
            Shapes.empty(),
            Block.box(0.0D, 7.0D, 7.0D, 2.0D, 9.0D, 9.0D),
            Block.box(0.0D, 6.0D, 6.0D, 4.0D, 10.0D, 10.0D),
            Block.box(0.0D, 5.0D, 5.0D, 6.0D, 11.0D, 11.0D),
            Block.box(0.0D, 4.0D, 4.0D, 8.0D, 12.0D, 12.0D),
            Block.box(0.0D, 3.0D, 3.0D, 10.0D, 13.0D, 13.0D),
            Block.box(0.0D, 2.0D, 2.0D, 12.0D, 14.0D, 14.0D),
            Block.box(0.0D, 1.0D, 1.0D, 14.0D, 15.0D, 15.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    protected static final VoxelShape[] WEST_TOP = new VoxelShape[]{
            Shapes.empty(),
            Block.box(0.0D, 14.0D, 7.0D, 2.0D, 16.0D, 9.0D),
            Block.box(0.0D, 12.0D, 6.0D, 4.0D, 16.0D, 10.0D),
            Block.box(0.0D, 10.0D, 5.0D, 6.0D, 16.0D, 11.0D),
            Block.box(0.0D, 8.0D, 4.0D, 8.0D, 16.0D, 12.0D),
            Block.box(0.0D, 6.0D, 3.0D, 10.0D, 16.0D, 13.0D),
            Block.box(0.0D, 4.0D, 2.0D, 12.0D, 16.0D, 14.0D),
            Block.box(0.0D, 2.0D, 1.0D, 14.0D, 16.0D, 15.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    protected static final VoxelShape[] CENTER_BOTTOM = new VoxelShape[]{
            Shapes.empty(),
            Block.box(7.0D, 0.0D, 7.0D, 9.0D, 2.0D, 9.0D),
            Block.box(6.0D, 0.0D, 6.0D, 10.0D, 4.0D, 10.0D),
            Block.box(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D),
            Block.box(4.0D, 0.0D, 4.0D, 12.0D, 8.0D, 12.0D),
            Block.box(3.0D, 0.0D, 3.0D, 13.0D, 10.0D, 13.0D),
            Block.box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D),
            Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    protected static final VoxelShape[] CENTER_CENTER = new VoxelShape[]{
            Shapes.empty(),
            Block.box(7.0D, 7.0D, 7.0D, 9.0D, 9.0D, 9.0D),
            Block.box(6.0D, 6.0D, 6.0D, 10.0D, 10.0D, 10.0D),
            Block.box(5.0D, 5.0D, 5.0D, 11.0D, 11.0D, 11.0D),
            Block.box(4.0D, 4.0D, 4.0D, 12.0D, 12.0D, 12.0D),
            Block.box(3.0D, 3.0D, 3.0D, 13.0D, 13.0D, 13.0D),
            Block.box(2.0D, 2.0D, 2.0D, 14.0D, 14.0D, 14.0D),
            Block.box(1.0D, 1.0D, 1.0D, 15.0D, 15.0D, 15.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    protected static final VoxelShape[] CENTER_TOP = new VoxelShape[]{
            Shapes.empty(),
            Block.box(7.0D, 14.0D, 7.0D, 9.0D, 16.0D, 9.0D),
            Block.box(6.0D, 12.0D, 6.0D, 10.0D, 16.0D, 10.0D),
            Block.box(5.0D, 10.0D, 5.0D, 11.0D, 16.0D, 11.0D),
            Block.box(4.0D, 8.0D, 4.0D, 12.0D, 16.0D, 12.0D),
            Block.box(3.0D, 6.0D, 3.0D, 13.0D, 16.0D, 13.0D),
            Block.box(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D),
            Block.box(1.0D, 2.0D, 1.0D, 15.0D, 16.0D, 15.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
    };

    public WarframePointLayerBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(HALF, WarframeHorizontalSlabPosition.BOTTOM)
                .setValue(POSITION, WarframeCompassPosition.NORTHEAST));
    }

    @Override
    protected VoxelShape calculateShape(BlockState state) {
        WarframeHorizontalSlabPosition half = state.getValue(HALF);
        VoxelShape[] shapes = switch (state.getValue(POSITION)) {
            case NORTHEAST -> byHalf(half, NORTHEAST_TOP, NORTHEAST_CENTER, NORTHEAST_BOTTOM);
            case NORTHWEST -> byHalf(half, NORTHWEST_TOP, NORTHWEST_CENTER, NORTHWEST_BOTTOM);
            case SOUTHEAST -> byHalf(half, SOUTHEAST_TOP, SOUTHEAST_CENTER, SOUTHEAST_BOTTOM);
            case SOUTHWEST -> byHalf(half, SOUTHWEST_TOP, SOUTHWEST_CENTER, SOUTHWEST_BOTTOM);
            case NORTH -> byHalf(half, NORTH_TOP, NORTH_CENTER, NORTH_BOTTOM);
            case SOUTH -> byHalf(half, SOUTH_TOP, SOUTH_CENTER, SOUTH_BOTTOM);
            case EAST -> byHalf(half, EAST_TOP, EAST_CENTER, EAST_BOTTOM);
            case WEST -> byHalf(half, WEST_TOP, WEST_CENTER, WEST_BOTTOM);
            case CENTER -> byHalf(half, CENTER_TOP, CENTER_CENTER, CENTER_BOTTOM);
        };
        return shapes[state.getValue(LAYERS)];
    }

    private static VoxelShape[] byHalf(WarframeHorizontalSlabPosition half, VoxelShape[] top, VoxelShape[] center, VoxelShape[] bottom) {
        return switch (half) {
            case TOP -> top;
            case CENTER -> center;
            case BOTTOM -> bottom;
        };
    }

    @Override
    protected BlockState getPlacementOrientation(BlockPlaceContext pContext, BlockState base) {
        BlockPos blockpos = pContext.getClickedPos();
        Direction clickedFace = pContext.getClickedFace();
        Vec3 hit = pContext.getClickLocation();
        double relX = hit.x - (double) blockpos.getX();
        double relY = hit.y - (double) blockpos.getY();
        double relZ = hit.z - (double) blockpos.getZ();

        WarframeHorizontalSlabPosition half;
        WarframeCompassPosition position;

        if (clickedFace == Direction.UP || clickedFace == Direction.DOWN) {
            half = clickedFace == Direction.DOWN ? WarframeHorizontalSlabPosition.TOP : WarframeHorizontalSlabPosition.BOTTOM;
            boolean eastEdge = relX > 0.67D;
            boolean westEdge = relX < 0.33D;
            boolean southEdge = relZ > 0.67D;
            boolean northEdge = relZ < 0.33D;

            if ((eastEdge || westEdge) && (northEdge || southEdge)) {
                position = eastEdge
                        ? (southEdge ? WarframeCompassPosition.SOUTHEAST : WarframeCompassPosition.NORTHEAST)
                        : (southEdge ? WarframeCompassPosition.SOUTHWEST : WarframeCompassPosition.NORTHWEST);
            } else if (northEdge) {
                position = WarframeCompassPosition.NORTH;
            } else if (southEdge) {
                position = WarframeCompassPosition.SOUTH;
            } else if (eastEdge) {
                position = WarframeCompassPosition.EAST;
            } else if (westEdge) {
                position = WarframeCompassPosition.WEST;
            } else {
                position = WarframeCompassPosition.CENTER;
            }
        } else {
            if (relY > 0.67D) {
                half = WarframeHorizontalSlabPosition.TOP;
            } else if (relY < 0.33D) {
                half = WarframeHorizontalSlabPosition.BOTTOM;
            } else {
                half = WarframeHorizontalSlabPosition.CENTER;
            }

            position = switch (clickedFace) {
                case SOUTH -> relX > 0.67D ? WarframeCompassPosition.NORTHEAST
                        : relX < 0.33D ? WarframeCompassPosition.NORTHWEST
                        : WarframeCompassPosition.NORTH;
                case NORTH -> relX < 0.33D ? WarframeCompassPosition.SOUTHWEST
                        : relX > 0.67D ? WarframeCompassPosition.SOUTHEAST
                        : WarframeCompassPosition.SOUTH;
                case EAST -> relZ < 0.33D ? WarframeCompassPosition.NORTHWEST
                        : relZ > 0.67D ? WarframeCompassPosition.SOUTHWEST
                        : WarframeCompassPosition.WEST;
                default -> relZ > 0.67D ? WarframeCompassPosition.SOUTHEAST
                        : relZ < 0.33D ? WarframeCompassPosition.NORTHEAST
                        : WarframeCompassPosition.EAST;
            };
        }

        return base
                .setValue(HALF, half)
                .setValue(POSITION, position);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(HALF, POSITION);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.warframes.layer")
                .withStyle(ChatFormatting.GRAY));
    }
}