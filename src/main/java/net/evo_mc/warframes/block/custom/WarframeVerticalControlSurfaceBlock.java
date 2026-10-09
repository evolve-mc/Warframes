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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.mcreator.valkyrienwarium.procedures.VerticalControlSurfaceTickProcedure;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.item.TooltipFlag;
import java.util.List;

public class WarframeVerticalControlSurfaceBlock extends BaseEntityBlock implements EntityBlock, WarframeMimicable, WarframeExtendableBlock {
    public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 4);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<WarframeVerticalSlabPosition> AEROFOIL =
            EnumProperty.create("aerofoil", WarframeVerticalSlabPosition.class);

    public static final int MAX_LAYERS = 13;
    public static final IntegerProperty LAYERS = IntegerProperty.create("layers", 1, MAX_LAYERS);

    private static final String[] MODEL_NAMES = new String[MAX_LAYERS + 1];
    static {
        MODEL_NAMES[1] = "airframe_vertical_control_surface_layer1";
        MODEL_NAMES[2] = "airframe_vertical_control_surface_layer2";
        MODEL_NAMES[3] = "airframe_vertical_control_surface_layer3";
        MODEL_NAMES[4] = "airframe_vertical_control_surface_layer4";
        MODEL_NAMES[5] = "airframe_vertical_control_surface";
        MODEL_NAMES[6] = "airframe_vertical_control_surface_layer6";
        MODEL_NAMES[7] = "airframe_vertical_control_surface_layer7";
        MODEL_NAMES[8] = "airframe_vertical_control_surface_layer8";
        MODEL_NAMES[9] = "airframe_vertical_control_surface_layer9";
        MODEL_NAMES[10] = "airframe_vertical_control_surface_layer10";
        MODEL_NAMES[11] = "airframe_vertical_control_surface_layer11";
        MODEL_NAMES[12] = "airframe_vertical_control_surface_layer12";
        MODEL_NAMES[13] = "airframe_vertical_control_surface_layer13";
    }

    public WarframeVerticalControlSurfaceBlock(Properties props) {
        super(props.sound(SoundType.ANVIL).strength(1.0F, 0.25F).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(ANIMATION, 0)
                .setValue(AEROFOIL, WarframeVerticalSlabPosition.CENTER)
                .setValue(LAYERS, 5));
    }

    public double getLength(BlockState state) {
        return 6.0D + 2.0D * state.getValue(LAYERS);
    }

    public String getModelName(BlockState state) {
        return MODEL_NAMES[state.getValue(LAYERS)];
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

    public static Direction worldSideFor(Direction facing, WarframeVerticalSlabPosition aerofoil) {
        Direction right = rightOf(facing);
        return aerofoil == WarframeVerticalSlabPosition.RIGHT ? right : right.getOpposite();
    }

    private static double[] widthBand(Direction facing, WarframeVerticalSlabPosition aerofoil) {
        if (aerofoil == WarframeVerticalSlabPosition.CENTER) {
            return new double[] {6.0D, 10.0D};
        }
        Direction lowSide = facing.getAxis() == Direction.Axis.Z ? Direction.WEST : Direction.NORTH;
        Direction resolvedSide = worldSideFor(facing, aerofoil);
        return resolvedSide == lowSide ? new double[] {2.0D, 6.0D} : new double[] {10.0D, 14.0D};
    }

    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        double zMax = 16.0D;
        double zMin = 16.0D - getLength(state);

        double xMin, xMax, zMinF, zMaxF;
        switch (state.getValue(FACING)) {
            case SOUTH -> {
                xMin = 0.0D;
                xMax = 16.0D;
                zMinF = 16.0D - zMax;
                zMaxF = 16.0D - zMin;
            }
            case EAST -> {
                xMin = 16.0D - zMax;
                xMax = 16.0D - zMin;
                zMinF = 0.0D;
                zMaxF = 16.0D;
            }
            case WEST -> {
                xMin = zMin;
                xMax = zMax;
                zMinF = 0.0D;
                zMaxF = 16.0D;
            }
            default -> {
                xMin = 0.0D;
                xMax = 16.0D;
                zMinF = zMin;
                zMaxF = zMax;
            }
        }

        Direction facing = state.getValue(FACING);
        WarframeVerticalSlabPosition aerofoil = state.getValue(AEROFOIL);
        double[] band = widthBand(facing, aerofoil);

        if (facing.getAxis() == Direction.Axis.Z) {
            xMin = band[0];
            xMax = band[1];
        } else {
            zMinF = band[0];
            zMaxF = band[1];
        }

        return box(xMin, 0.0D, zMinF, xMax, 16.0D, zMaxF);
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WarframeVerticalControlSurfaceBlockEntity(pos, state);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ANIMATION, AEROFOIL, LAYERS);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Vec3 hit = context.getClickLocation();
        BlockPos blockpos = context.getClickedPos();
        double relX = hit.x - (double) blockpos.getX();
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

        return this.defaultBlockState().setValue(FACING, facing).setValue(AEROFOIL, aerofoil);
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