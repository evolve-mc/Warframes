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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class WarframePointBlock extends Block implements SimpleWaterloggedBlock, WarframeMimicable {
    public static final EnumProperty<WarframeHalfType> HALF =
            EnumProperty.create("half", WarframeHalfType.class);
    public static final EnumProperty<WarframeFacingType> FACING =
            EnumProperty.create("facing", WarframeFacingType.class);
    public static final EnumProperty<WarframeCornerType> CORNER =
            EnumProperty.create("corner", WarframeCornerType.class);
    public static final IntegerProperty LAYERS = IntegerProperty.create("layers", 1, 8);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final int MAX_LAYERS = 8;

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

    public WarframePointBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(HALF, WarframeHalfType.BOTTOM)
                .setValue(FACING, WarframeFacingType.CORNER)
                .setValue(CORNER, WarframeCornerType.NORTHEAST)
                .setValue(LAYERS, Integer.valueOf(1))
                .setValue(WATERLOGGED, Boolean.valueOf(false)));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        WarframeHalfType half = pState.getValue(HALF);
        WarframeFacingType facing = pState.getValue(FACING);
        WarframeCornerType corner = pState.getValue(CORNER);
        int layers = pState.getValue(LAYERS);

        if (facing == WarframeFacingType.CORNER) {
            switch (corner) {
                case NORTHEAST:
                    return (half == WarframeHalfType.TOP ? NORTHEAST_TOP
                            : half == WarframeHalfType.CENTER ? NORTHEAST_CENTER
                            : NORTHEAST_BOTTOM)[layers];
                case NORTHWEST:
                    return (half == WarframeHalfType.TOP ? NORTHWEST_TOP
                            : half == WarframeHalfType.CENTER ? NORTHWEST_CENTER
                            : NORTHWEST_BOTTOM)[layers];
                case SOUTHEAST:
                    return (half == WarframeHalfType.TOP ? SOUTHEAST_TOP
                            : half == WarframeHalfType.CENTER ? SOUTHEAST_CENTER
                            : SOUTHEAST_BOTTOM)[layers];
                case SOUTHWEST:
                default:
                    return (half == WarframeHalfType.TOP ? SOUTHWEST_TOP
                            : half == WarframeHalfType.CENTER ? SOUTHWEST_CENTER
                            : SOUTHWEST_BOTTOM)[layers];
            }
        }

        if (facing == WarframeFacingType.NONE) {
            return (half == WarframeHalfType.TOP ? CENTER_TOP
                    : half == WarframeHalfType.CENTER ? CENTER_CENTER
                    : CENTER_BOTTOM)[layers];
        }

        switch (facing) {
            case NORTH:
                return (half == WarframeHalfType.TOP ? NORTH_TOP
                        : half == WarframeHalfType.CENTER ? NORTH_CENTER
                        : NORTH_BOTTOM)[layers];
            case SOUTH:
                return (half == WarframeHalfType.TOP ? SOUTH_TOP
                        : half == WarframeHalfType.CENTER ? SOUTH_CENTER
                        : SOUTH_BOTTOM)[layers];
            case EAST:
                return (half == WarframeHalfType.TOP ? EAST_TOP
                        : half == WarframeHalfType.CENTER ? EAST_CENTER
                        : EAST_BOTTOM)[layers];
            case WEST:
            default:
                return (half == WarframeHalfType.TOP ? WEST_TOP
                        : half == WarframeHalfType.CENTER ? WEST_CENTER
                        : WEST_BOTTOM)[layers];
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return getShape(pState, pLevel, pPos, pContext);
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return getShape(pState, pReader, pPos, CollisionContext.empty());
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState pState) {
        return true;
    }

    @Override
    public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return pState.getValue(LAYERS) == MAX_LAYERS ? 0.2F : 1.0F;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockPos blockpos = pContext.getClickedPos();
        BlockState existing = pContext.getLevel().getBlockState(blockpos);

        if (existing.is(this)) {
            int layers = existing.getValue(LAYERS);
            if (layers < MAX_LAYERS) {
                return existing.setValue(LAYERS, Integer.valueOf(layers + 1));
            }
            return existing;
        }

        Direction clickedFace = pContext.getClickedFace();
        Vec3 hit = pContext.getClickLocation();
        double relX = hit.x - (double) blockpos.getX();
        double relY = hit.y - (double) blockpos.getY();
        double relZ = hit.z - (double) blockpos.getZ();

        FluidState fluidstate = pContext.getLevel().getFluidState(blockpos);
        boolean waterlogged = fluidstate.getType() == Fluids.WATER;

        WarframeHalfType half;
        WarframeCornerType corner = WarframeCornerType.NONE;
        WarframeFacingType facing;

        if (clickedFace == Direction.UP || clickedFace == Direction.DOWN) {
            half = clickedFace == Direction.DOWN ? WarframeHalfType.TOP : WarframeHalfType.BOTTOM;
            boolean eastEdge = relX > 0.7D;
            boolean westEdge = relX < 0.3D;
            boolean southEdge = relZ > 0.7D;
            boolean northEdge = relZ < 0.3D;

            if ((eastEdge || westEdge) && (northEdge || southEdge)) {
                facing = WarframeFacingType.CORNER;
                corner = eastEdge
                        ? (southEdge ? WarframeCornerType.SOUTHEAST : WarframeCornerType.NORTHEAST)
                        : (southEdge ? WarframeCornerType.SOUTHWEST : WarframeCornerType.NORTHWEST);
            } else if (northEdge) {
                facing = WarframeFacingType.NORTH;
            } else if (southEdge) {
                facing = WarframeFacingType.SOUTH;
            } else if (eastEdge) {
                facing = WarframeFacingType.EAST;
            } else if (westEdge) {
                facing = WarframeFacingType.WEST;
            } else {
                facing = WarframeFacingType.NONE;
            }
        } else {
            if (relY > 0.7D) {
                half = WarframeHalfType.TOP;
            } else if (relY < 0.3D) {
                half = WarframeHalfType.BOTTOM;
            } else {
                half = WarframeHalfType.CENTER;
            }

            switch (clickedFace) {
                case SOUTH:
                    if (relX > 0.7D) {
                        facing = WarframeFacingType.CORNER;
                        corner = WarframeCornerType.NORTHEAST;
                    } else if (relX < 0.3D) {
                        facing = WarframeFacingType.CORNER;
                        corner = WarframeCornerType.NORTHWEST;
                    } else {
                        facing = WarframeFacingType.NORTH;
                    }
                    break;
                case NORTH:
                    if (relX < 0.3D) {
                        facing = WarframeFacingType.CORNER;
                        corner = WarframeCornerType.SOUTHWEST;
                    } else if (relX > 0.7D) {
                        facing = WarframeFacingType.CORNER;
                        corner = WarframeCornerType.SOUTHEAST;
                    } else {
                        facing = WarframeFacingType.SOUTH;
                    }
                    break;
                case EAST:
                    if (relZ < 0.3D) {
                        facing = WarframeFacingType.CORNER;
                        corner = WarframeCornerType.NORTHWEST;
                    } else if (relZ > 0.7D) {
                        facing = WarframeFacingType.CORNER;
                        corner = WarframeCornerType.SOUTHWEST;
                    } else {
                        facing = WarframeFacingType.WEST;
                    }
                    break;
                case WEST:
                default:
                    if (relZ > 0.7D) {
                        facing = WarframeFacingType.CORNER;
                        corner = WarframeCornerType.SOUTHEAST;
                    } else if (relZ < 0.3D) {
                        facing = WarframeFacingType.CORNER;
                        corner = WarframeCornerType.NORTHEAST;
                    } else {
                        facing = WarframeFacingType.EAST;
                    }
                    break;
            }
        }

        return this.defaultBlockState()
                .setValue(HALF, half)
                .setValue(FACING, facing)
                .setValue(CORNER, corner)
                .setValue(LAYERS, Integer.valueOf(1))
                .setValue(WATERLOGGED, Boolean.valueOf(waterlogged));
    }

    @Override
    public boolean canBeReplaced(BlockState pState, BlockPlaceContext pUseContext) {
        return !pUseContext.isSecondaryUseActive()
                && pUseContext.getItemInHand().getItem() == this.asItem()
                && pState.getValue(LAYERS) < MAX_LAYERS
                ? true
                : super.canBeReplaced(pState, pUseContext);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(HALF, FACING, CORNER, LAYERS, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }
        return !pState.canSurvive(pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
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