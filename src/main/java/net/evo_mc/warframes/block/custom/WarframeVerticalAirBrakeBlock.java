package net.evo_mc.warframes.block.custom;

import net.evo_mc.warframes.block.entity.WarframeVerticalAirBrakeBlockEntity;
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
import net.minecraft.world.level.LevelAccessor;
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
import net.mcreator.valkyrienwarium.procedures.VerticalAirBrakeTickProcedure;

public class WarframeVerticalAirBrakeBlock extends BaseEntityBlock implements EntityBlock, WarframeMimicable {
    public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 2);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<WarframeVerticalSlabPosition> AEROFOIL =
            EnumProperty.create("aerofoil", WarframeVerticalSlabPosition.class);

    public WarframeVerticalAirBrakeBlock(Properties props) {
        super(props.sound(SoundType.ANVIL).strength(1.0F, 0.25F).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(ANIMATION, 0)
                .setValue(AEROFOIL, WarframeVerticalSlabPosition.CENTER));
    }

    public String getModelName(BlockState state) {
        return "airframe_vertical_air_brake";
    }

    private static Direction rightOf(Direction facing) {
        return facing.getClockWise();
    }

    private static WarframeVerticalSlabPosition aerofoilFor(Direction facing, Direction worldSide) {
        return rightOf(facing) == worldSide ? WarframeVerticalSlabPosition.RIGHT : WarframeVerticalSlabPosition.LEFT;
    }

    private static double[] widthBand(Direction facing, WarframeVerticalSlabPosition aerofoil) {
        if (aerofoil == WarframeVerticalSlabPosition.CENTER) {
            return new double[] {5.0D, 11.0D};
        }
        Direction lowSide = facing.getAxis() == Direction.Axis.Z ? Direction.WEST : Direction.NORTH;
        Direction right = rightOf(facing);
        Direction resolvedSide = aerofoil == WarframeVerticalSlabPosition.RIGHT ? right : right.getOpposite();
        return resolvedSide == lowSide ? new double[] {0.0D, 6.0D} : new double[] {10.0D, 16.0D};
    }

    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        WarframeVerticalSlabPosition aerofoil = state.getValue(AEROFOIL);
        double[] band = widthBand(facing, aerofoil);

        if (facing.getAxis() == Direction.Axis.Z) {
            return box(band[0], 0.0D, 0.0D, band[1], 16.0D, 16.0D);
        } else {
            return box(0.0D, 0.0D, band[0], 16.0D, 16.0D, band[1]);
        }
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WarframeVerticalAirBrakeBlockEntity(pos, state);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ANIMATION, AEROFOIL);
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
            VerticalAirBrakeTickProcedure.execute((LevelAccessor) world, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), state);
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
}