package net.evo_mc.warframes.block.custom;

import net.evo_mc.warframes.block.entity.ModBlockEntities;
import net.evo_mc.warframes.block.entity.WarframeBombBayDoorBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WarframeBombBayDoorBlock extends BaseEntityBlock implements WarframeMimicable, WarframeExtendableBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<WarframeBombBayDoorVariant> VARIANT =
            EnumProperty.create("variant", WarframeBombBayDoorVariant.class);
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    public static final double THICKNESS = 3.0D;
    public static final double OVERHANG = 8.0D;

    private static final VoxelShape[][] CLOSED_SHAPES = new VoxelShape[WarframeBombBayDoorVariant.values().length][4];
    private static final VoxelShape[][] OPEN_SHAPES = new VoxelShape[WarframeBombBayDoorVariant.values().length][4];

    static {
        double top = 16.0D - THICKNESS;
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            int f = facing.get2DDataValue();

            CLOSED_SHAPES[WarframeBombBayDoorVariant.SPLIT.ordinal()][f] = oriented(facing, 0.0D, top, 0.0D, 16.0D, 16.0D, 16.0D);
            CLOSED_SHAPES[WarframeBombBayDoorVariant.TRAPDOOR.ordinal()][f] = oriented(facing, 0.0D, top, 0.0D, 16.0D, 16.0D, 16.0D);
            CLOSED_SHAPES[WarframeBombBayDoorVariant.LONG.ordinal()][f] = oriented(facing, 0.0D, top, -OVERHANG, 16.0D, 16.0D, 16.0D);

            OPEN_SHAPES[WarframeBombBayDoorVariant.SPLIT.ordinal()][f] = Shapes.or(
                    oriented(facing, -THICKNESS, 8.0D, 0.0D, 0.0D, 16.0D, 16.0D),
                    oriented(facing, 16.0D, 8.0D, 0.0D, 16.0D + THICKNESS, 16.0D, 16.0D));
            OPEN_SHAPES[WarframeBombBayDoorVariant.TRAPDOOR.ordinal()][f] = oriented(facing, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D, 16.0D + THICKNESS);
            OPEN_SHAPES[WarframeBombBayDoorVariant.LONG.ordinal()][f] = oriented(facing, 0.0D, -OVERHANG, 16.0D, 16.0D, 16.0D, 16.0D + THICKNESS);
        }
    }

    public WarframeBombBayDoorBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(VARIANT, WarframeBombBayDoorVariant.SPLIT)
                .setValue(OPEN, false));
    }

    private static VoxelShape oriented(Direction facing, double x1, double y1, double z1, double x2, double y2, double z2) {
        return switch (facing) {
            case EAST -> box(16.0D - z2, y1, x1, 16.0D - z1, y2, x2);
            case SOUTH -> box(16.0D - x2, y1, 16.0D - z2, 16.0D - x1, y2, 16.0D - z1);
            case WEST -> box(z1, y1, 16.0D - x2, z2, y2, 16.0D - x1);
            default -> box(x1, y1, z1, x2, y2, z2);
        };
    }

    public static boolean isLong(BlockState state) {
        return state.getValue(VARIANT) == WarframeBombBayDoorVariant.LONG;
    }

    public static List<BlockPos> connectedDoors(Level level, BlockPos pos, BlockState state) {
        List<BlockPos> result = new ArrayList<>();
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos adjacent = pos.relative(dir);
            if (!level.isLoaded(adjacent)) {
                continue;
            }
            BlockState adjacentState = level.getBlockState(adjacent);
            if (adjacentState.getBlock() instanceof WarframeBombBayDoorBlock) {
                result.add(adjacent);
                continue;
            }
            BlockPos across = pos.relative(dir, 2);
            if (!level.isLoaded(across)) {
                continue;
            }
            BlockState acrossState = level.getBlockState(across);
            if (!(acrossState.getBlock() instanceof WarframeBombBayDoorBlock)) {
                continue;
            }
            boolean reachesAcross = isLong(state) && state.getValue(FACING) == dir;
            boolean reachedFrom = isLong(acrossState) && acrossState.getValue(FACING) == dir.getOpposite();
            if (reachesAcross || reachedFrom) {
                result.add(across);
            }
        }
        return result;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int variant = state.getValue(VARIANT).ordinal();
        int facing = state.getValue(FACING).get2DDataValue();
        return state.getValue(OPEN) ? OPEN_SHAPES[variant][facing] : CLOSED_SHAPES[variant][facing];
    }

    @Override
    public int getLayers(BlockState state) {
        return state.getValue(VARIANT).ordinal() + 1;
    }

    @Override
    public BlockState setLayers(BlockState state, int layers) {
        return state.setValue(VARIANT, WarframeBombBayDoorVariant.byIndex(layers - 1));
    }

    @Override
    public int getMaxLayers() {
        return WarframeBombBayDoorVariant.values().length;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WarframeBombBayDoorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return createTickerHelper(type, ModBlockEntities.WARFRAME_BOMB_BAY_DOOR.get(), WarframeBombBayDoorBlockEntity::serverTick);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, VARIANT, OPEN);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return mimicUse(state, level, pos, player, hand, hit);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        mimicSetPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        mimicPlayerWillDestroy(level, pos, state, player);
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (state.getBlock() != newState.getBlock()) {
            mimicOnRemove(state, level, pos, newState, moving);
        }
        super.onRemove(state, level, pos, newState, moving);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.warframes.wrench_hint").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.warframes.bomb_bay_door_hint").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityContext && entityContext.getEntity() instanceof Projectile) {
            return Shapes.empty();
        }
        return getShape(state, level, pos, context);
    }


}