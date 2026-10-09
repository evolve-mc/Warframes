package net.evo_mc.warframes.block.custom;

import net.evo_mc.warframes.block.entity.WarframeControlSurfaceBlockEntity;
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
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.mcreator.valkyrienwarium.procedures.ControlSurfaceTickProcedure;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.item.TooltipFlag;
import java.util.List;

public class WarframeSteppedControlSurfaceBlock extends BaseEntityBlock implements EntityBlock, WarframeMimicable, WarframeExtendableBlock {
    public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 4);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<WarframeHorizontalSlabPosition> HALF =
            EnumProperty.create("half", WarframeHorizontalSlabPosition.class);
    public static final EnumProperty<WarframeSideType> STEP =
            EnumProperty.create("step", WarframeSideType.class);

    public static final int MAX_LAYERS = 4;
    public static final IntegerProperty LAYERS = IntegerProperty.create("layers", 1, MAX_LAYERS);

    private static final String[] MODEL_NAMES_RIGHT = new String[MAX_LAYERS + 1];
    private static final String[] MODEL_NAMES_LEFT = new String[MAX_LAYERS + 1];
    static {
        MODEL_NAMES_RIGHT[1] = "airframe_stepped_control_surface_right_layer1";
        MODEL_NAMES_RIGHT[2] = "airframe_stepped_control_surface_right_layer2";
        MODEL_NAMES_RIGHT[3] = "airframe_stepped_control_surface_right_layer3";
        MODEL_NAMES_RIGHT[4] = "airframe_stepped_control_surface_right_layer4";

        MODEL_NAMES_LEFT[1] = "airframe_stepped_control_surface_left_layer1";
        MODEL_NAMES_LEFT[2] = "airframe_stepped_control_surface_left_layer2";
        MODEL_NAMES_LEFT[3] = "airframe_stepped_control_surface_left_layer3";
        MODEL_NAMES_LEFT[4] = "airframe_stepped_control_surface_left_layer4";
    }

    public WarframeSteppedControlSurfaceBlock(Properties props) {
        super(props.sound(SoundType.ANVIL).strength(1.0F, 0.25F).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(ANIMATION, 0)
                .setValue(HALF, WarframeHorizontalSlabPosition.CENTER)
                .setValue(STEP, WarframeSideType.RIGHT)
                .setValue(LAYERS, 2));
    }

    public String getModelName(BlockState state) {
        int layers = state.getValue(LAYERS);
        WarframeSideType step = state.getValue(STEP);
        return step == WarframeSideType.LEFT ? MODEL_NAMES_LEFT[layers] : MODEL_NAMES_RIGHT[layers];
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

    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        int layers = state.getValue(LAYERS);
        double totalDepth = 8.0D * layers;
        double wideDepth = 8.0D * (layers - 1);

        double yMin, yMax;
        switch (state.getValue(HALF)) {
            case BOTTOM -> { yMin = 2.0D; yMax = 6.0D; }
            case TOP -> { yMin = 10.0D; yMax = 14.0D; }
            default -> { yMin = 6.0D; yMax = 10.0D; }
        }

        Direction facing = state.getValue(FACING);
        WarframeSideType step = state.getValue(STEP);

        VoxelShape wideBox;
        VoxelShape narrowBox;

        switch (facing) {
            case SOUTH -> {
                double stepXMin = step == WarframeSideType.LEFT ? 0.0D : 8.0D;
                double stepXMax = stepXMin + 8.0D;
                wideBox = wideDepth > 0 ? box(0.0D, yMin, 0.0D, 16.0D, yMax, wideDepth) : Shapes.empty();
                narrowBox = box(stepXMin, yMin, wideDepth, stepXMax, yMax, totalDepth);
            }
            case NORTH -> {
                double stepXMin = step == WarframeSideType.LEFT ? 8.0D : 0.0D;
                double stepXMax = stepXMin + 8.0D;
                wideBox = wideDepth > 0 ? box(0.0D, yMin, 16.0D - wideDepth, 16.0D, yMax, 16.0D) : Shapes.empty();
                narrowBox = box(stepXMin, yMin, 16.0D - totalDepth, stepXMax, yMax, 16.0D - wideDepth);
            }
            case WEST -> {
                if (step == WarframeSideType.RIGHT) {
                    wideBox = wideDepth > 0 ? box(16.0D - wideDepth, yMin, 0.0D, 16.0D, yMax, 16.0D) : Shapes.empty();
                    narrowBox = box(16.0D - totalDepth, yMin, 8.0D, 16.0D - wideDepth, yMax, 16.0D);
                } else {
                    wideBox = wideDepth > 0 ? box(16.0D - wideDepth, yMin, 0.0D, 16.0D, yMax, 16.0D) : Shapes.empty();
                    narrowBox = box(16.0D - totalDepth, yMin, 0.0D, 16.0D - wideDepth, yMax, 8.0D);
                }
            }
            default -> {
                wideBox = wideDepth > 0 ? box(0.0D, yMin, 0.0D, wideDepth, yMax, 16.0D) : Shapes.empty();
                if (step == WarframeSideType.RIGHT) {
                    narrowBox = box(wideDepth, yMin, 0.0D, totalDepth, yMax, 8.0D);
                } else {
                    narrowBox = box(wideDepth, yMin, 8.0D, totalDepth, yMax, 16.0D);
                }
            }
        }

        return Shapes.join(wideBox, narrowBox, BooleanOp.OR);
    }


    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WarframeControlSurfaceBlockEntity(pos, state);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ANIMATION, HALF, STEP, LAYERS);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        double clickY = context.getClickLocation().y - context.getClickedPos().getY();

        WarframeHorizontalSlabPosition half;
        if (clickY < 0.3D) {
            half = WarframeHorizontalSlabPosition.BOTTOM;
        } else if (clickY > 0.7D) {
            half = WarframeHorizontalSlabPosition.TOP;
        } else {
            half = WarframeHorizontalSlabPosition.CENTER;
        }

        double clickAcross;
        boolean invert;
        switch (facing) {
            case NORTH -> { clickAcross = context.getClickLocation().x - context.getClickedPos().getX(); invert = true; }
            case SOUTH -> { clickAcross = context.getClickLocation().x - context.getClickedPos().getX(); invert = false; }
            case WEST -> { clickAcross = context.getClickLocation().z - context.getClickedPos().getZ(); invert = false; }
            case EAST -> { clickAcross = context.getClickLocation().z - context.getClickedPos().getZ(); invert = true; }
            default -> { clickAcross = 0; invert = false; }
        }
        boolean isLeftSide = clickAcross < 0.5D;
        if (invert) isLeftSide = !isLeftSide;
        WarframeSideType step = isLeftSide ? WarframeSideType.LEFT : WarframeSideType.RIGHT;

        return this.defaultBlockState().setValue(FACING, facing).setValue(HALF, half).setValue(STEP, step);
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
            ControlSurfaceTickProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ(), state);
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