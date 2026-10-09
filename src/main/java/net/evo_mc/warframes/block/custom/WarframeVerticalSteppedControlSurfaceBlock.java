package net.evo_mc.warframes.block.custom;

import net.evo_mc.warframes.block.entity.WarframeVerticalControlSurfaceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.mcreator.valkyrienwarium.procedures.VerticalControlSurfaceTickProcedure;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.item.TooltipFlag;
import java.util.List;

public class WarframeVerticalSteppedControlSurfaceBlock extends BaseEntityBlock implements EntityBlock, WarframeMimicable, WarframeExtendableBlock {
    public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 4);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<WarframeVerticalSlabPosition> AEROFOIL =
            EnumProperty.create("aerofoil", WarframeVerticalSlabPosition.class);
    public static final EnumProperty<WarframeHorizontalSlabPosition> STEP =
            EnumProperty.create("step", WarframeHorizontalSlabPosition.class);

    public static final int MAX_LAYERS = 4;
    public static final IntegerProperty LAYERS = IntegerProperty.create("layers", 1, MAX_LAYERS);

    private static final String[] MODEL_NAMES_TOP = new String[MAX_LAYERS + 1];
    private static final String[] MODEL_NAMES_BOTTOM = new String[MAX_LAYERS + 1];
    static {
        MODEL_NAMES_TOP[1] = "airframe_vertical_stepped_control_surface_top_layer1";
        MODEL_NAMES_TOP[2] = "airframe_vertical_stepped_control_surface_top_layer2";
        MODEL_NAMES_TOP[3] = "airframe_vertical_stepped_control_surface_top_layer3";
        MODEL_NAMES_TOP[4] = "airframe_vertical_stepped_control_surface_top_layer4";

        MODEL_NAMES_BOTTOM[1] = "airframe_vertical_stepped_control_surface_bottom_layer1";
        MODEL_NAMES_BOTTOM[2] = "airframe_vertical_stepped_control_surface_bottom_layer2";
        MODEL_NAMES_BOTTOM[3] = "airframe_vertical_stepped_control_surface_bottom_layer3";
        MODEL_NAMES_BOTTOM[4] = "airframe_vertical_stepped_control_surface_bottom_layer4";
    }

    public WarframeVerticalSteppedControlSurfaceBlock(Properties props) {
        super(props.sound(SoundType.ANVIL).strength(1.0F, 0.25F).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(ANIMATION, 0)
                .setValue(AEROFOIL, WarframeVerticalSlabPosition.CENTER)
                .setValue(STEP, WarframeHorizontalSlabPosition.BOTTOM)
                .setValue(LAYERS, 2));
    }

    public String getModelName(BlockState state) {
        int layers = state.getValue(LAYERS);
        WarframeHorizontalSlabPosition step = state.getValue(STEP);
        return step == WarframeHorizontalSlabPosition.TOP ? MODEL_NAMES_TOP[layers] : MODEL_NAMES_BOTTOM[layers];
    }

    @Override
    public int getLayers(BlockState state) {
        return state.getValue(LAYERS);
    }

    @Override
    public BlockState setLayers(BlockState state, int layers) {
        return state.setValue(LAYERS, layers);
    }

    @Override
    public int getMaxLayers() {
        return MAX_LAYERS;
    }

    private static Direction rightOf(Direction facing) {
        return facing.getClockWise();
    }

    private static WarframeVerticalSlabPosition aerofoilFor(Direction facing, Direction worldSide) {
        return rightOf(facing) == worldSide ? WarframeVerticalSlabPosition.RIGHT : WarframeVerticalSlabPosition.LEFT;
    }

    private static double[] widthBand(Direction facing, WarframeVerticalSlabPosition aerofoil) {
        if (aerofoil == WarframeVerticalSlabPosition.CENTER) {
            return new double[] {6.0D, 10.0D};
        }
        Direction lowSide = facing.getAxis() == Direction.Axis.Z ? Direction.WEST : Direction.NORTH;
        Direction right = rightOf(facing);
        Direction resolvedSide = aerofoil == WarframeVerticalSlabPosition.RIGHT ? right : right.getOpposite();
        return resolvedSide == lowSide ? new double[] {2.0D, 6.0D} : new double[] {10.0D, 14.0D};
    }

    private static VoxelShape makeBox(Direction facing, double[] band, double lenMin, double lenMax, double yMin, double yMax) {
        if (facing.getAxis() == Direction.Axis.Z) {
            return net.minecraft.world.level.block.Block.box(band[0], yMin, lenMin, band[1], yMax, lenMax);
        } else {
            return net.minecraft.world.level.block.Block.box(lenMin, yMin, band[0], lenMax, yMax, band[1]);
        }
    }

    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        int layers = state.getValue(LAYERS);
        double totalDepth = 8.0D * layers;
        double wideDepth = 8.0D * (layers - 1);

        Direction facing = state.getValue(FACING);
        WarframeVerticalSlabPosition aerofoil = state.getValue(AEROFOIL);
        WarframeHorizontalSlabPosition step = state.getValue(STEP);

        double[] band = widthBand(facing, aerofoil);

        double stepYMin = step == WarframeHorizontalSlabPosition.TOP ? 8.0D : 0.0D;
        double stepYMax = stepYMin + 8.0D;

        VoxelShape wideBox;
        VoxelShape narrowBox;

        switch (facing) {
            case SOUTH, EAST -> {
                wideBox = wideDepth > 0 ? makeBox(facing, band, 0.0D, wideDepth, 0.0D, 16.0D) : Shapes.empty();
                narrowBox = makeBox(facing, band, wideDepth, totalDepth, stepYMin, stepYMax);
            }
            default -> { // NORTH, WEST
                wideBox = wideDepth > 0 ? makeBox(facing, band, 16.0D - wideDepth, 16.0D, 0.0D, 16.0D) : Shapes.empty();
                narrowBox = makeBox(facing, band, 16.0D - totalDepth, 16.0D - wideDepth, stepYMin, stepYMax);
            }
        }

        return Shapes.join(wideBox, narrowBox, BooleanOp.OR);
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WarframeVerticalControlSurfaceBlockEntity(pos, state);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ANIMATION, AEROFOIL, STEP, LAYERS);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Vec3 hit = context.getClickLocation();
        BlockPos blockpos = context.getClickedPos();
        double relX = hit.x - (double) blockpos.getX();
        double relY = hit.y - (double) blockpos.getY();
        double relZ = hit.z - (double) blockpos.getZ();

        Direction facing = context.getHorizontalDirection().getOpposite();

        WarframeVerticalSlabPosition aerofoil;
        if (facing.getAxis() == Direction.Axis.Z) {
            if (relX < 0.33D) {
                aerofoil = aerofoilFor(facing, Direction.WEST);
            } else if (relX > 0.67D) {
                aerofoil = aerofoilFor(facing, Direction.EAST);
            } else {
                aerofoil = WarframeVerticalSlabPosition.CENTER;
            }
        } else {
            if (relZ < 0.33D) {
                aerofoil = aerofoilFor(facing, Direction.NORTH);
            } else if (relZ > 0.67D) {
                aerofoil = aerofoilFor(facing, Direction.SOUTH);
            } else {
                aerofoil = WarframeVerticalSlabPosition.CENTER;
            }
        }

        WarframeHorizontalSlabPosition step = relY < 0.5D ? WarframeHorizontalSlabPosition.BOTTOM : WarframeHorizontalSlabPosition.TOP;

        return this.defaultBlockState().setValue(FACING, facing).setValue(AEROFOIL, aerofoil).setValue(STEP, step);
    }

    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(state, world, pos, oldState, moving);
        world.scheduleTick(pos, this, 1);
    }

    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.tick(state, world, pos, random);
        if (net.minecraftforge.fml.ModList.get().isLoaded("valkyrien_warium")) {
            VerticalControlSurfaceTickProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ(), state);
        }
        world.scheduleTick(pos, this, 1);
    }

    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return mimicUse(state, world, pos, player, hand, hit);
    }

    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);
        mimicSetPlacedBy(world, pos, state, placer, stack);
    }

    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        mimicPlayerWillDestroy(world, pos, state, player);
        super.playerWillDestroy(world, pos, state, player);
    }

    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moving) {
        if (state.getBlock() != newState.getBlock()) {
            mimicOnRemove(state, world, pos, newState, moving);
        }
        super.onRemove(state, world, pos, newState, moving);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.warframes.wrench_hint")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.warframes.size_physics")
                .withStyle(ChatFormatting.GRAY));
    }
}