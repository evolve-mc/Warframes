package net.evo_mc.warframes.block.custom;

import net.evo_mc.warframes.block.ModBlocks;
import net.evo_mc.warframes.block.entity.ModBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;
import java.util.List;

public abstract class WarframeLandingGearBayBlock extends Block implements WarframeMimicable {

    public static final DirectionProperty FACING = WarframeLandingGearProperties.FACING;
    public static final BooleanProperty OPEN = WarframeLandingGearProperties.OPEN;
    public static final IntegerProperty VARIANT = WarframeLandingGearProperties.VARIANT;

    public static final int MAX_GEAR_LENGTH = 2;

    private static final String SIGNAL_KEY = "WarframeGearSignal";

    protected WarframeLandingGearBayBlock(Properties pProperties) {
        super(pProperties);
    }

    protected abstract BlockState createWheelState(BlockState bayState, int length);

    protected int getGearLength(CompoundTag data) {
        return 1;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, OPEN, VARIANT);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer,
                                 InteractionHand pHand, BlockHitResult pHit) {
        return mimicUse(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        mimicSetPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
    }

    @Override
    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        mimicPlayerWillDestroy(pLevel, pPos, pState, pPlayer);
        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pState.getBlock() != pNewState.getBlock()) {
            mimicOnRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
            removeGear(pLevel, pPos);
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock,
                                BlockPos pNeighborPos, boolean pMovedByPiston) {
        super.neighborChanged(pState, pLevel, pPos, pNeighborBlock, pNeighborPos, pMovedByPiston);
        if (pLevel.isClientSide() || !pState.getValue(OPEN) || !pNeighborPos.equals(pPos.below())) {
            return;
        }
        if (!isGearPart(pLevel.getBlockState(pNeighborPos))) {
            pLevel.setBlock(pPos, pState.setValue(OPEN, false), Block.UPDATE_ALL);
            pLevel.playSound(null, pPos, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 1f, 1f);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pType) {
        if (pLevel.isClientSide() || pType != ModBlockEntities.WARFRAME_MIMIC_BLOCK_ENTITY.get()) {
            return null;
        }
        return (level, pos, state, blockEntity) -> tickBay(level, pos, state, blockEntity);
    }

    private void tickBay(Level level, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        CompoundTag data = blockEntity.getPersistentData();
        BlockPos nodePos = getNodePos(data);
        boolean signal = level.hasNeighborSignal(pos) || readNodeSignal(serverLevel, nodePos);

        if (signal != data.getBoolean(SIGNAL_KEY)) {
            data.putBoolean(SIGNAL_KEY, signal);
            blockEntity.setChanged();
            if (signal) {
                extend(level, pos, state, getGearLength(data));
            } else {
                retract(level, pos, state);
            }
            return;
        }

        if (!state.getValue(OPEN)) {
            return;
        }

        int length = getGearLength(data);
        if (getCurrentGearLength(level, pos) != length) {
            rebuild(level, pos, state, length);
            return;
        }

        if (nodePos != null && wariumLoaded() && serverLevel.isLoaded(nodePos)) {
            BlockPos wheelPos = pos.below(length);
            BlockState wheelState = level.getBlockState(wheelPos);
            if (wheelState.getBlock() instanceof WarframeLandingWheelBlock) {
                WarframeLandingGearWarium.brake(serverLevel, wheelPos, wheelState, nodePos);
            }
        }
    }

    private void extend(Level level, BlockPos pos, BlockState state, int length) {
        if (state.getValue(OPEN)) {
            return;
        }
        for (int i = 1; i <= length; i++) {
            if (!level.getBlockState(pos.below(i)).isAir()) {
                return;
            }
        }
        level.setBlock(pos.below(length), createWheelState(state, length), Block.UPDATE_ALL);
        for (int i = length - 1; i >= 1; i--) {
            level.setBlock(pos.below(i), ModBlocks.AIRFRAME_LANDING_STRUT.get().defaultBlockState(), Block.UPDATE_ALL);
        }
        level.setBlock(pos, state.setValue(OPEN, true), Block.UPDATE_ALL);
        level.playSound(null, pos, SoundEvents.IRON_TRAPDOOR_OPEN, SoundSource.BLOCKS, 1f, 1f);
    }

    private void retract(Level level, BlockPos pos, BlockState state) {
        if (!state.getValue(OPEN)) {
            return;
        }
        level.setBlock(pos, state.setValue(OPEN, false), Block.UPDATE_ALL);
        removeGear(level, pos);
        level.playSound(null, pos, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 1f, 1f);
    }

    private void rebuild(Level level, BlockPos pos, BlockState state, int length) {
        BlockState closed = state.setValue(OPEN, false);
        level.setBlock(pos, closed, Block.UPDATE_ALL);
        removeGear(level, pos);
        extend(level, pos, closed, length);
    }

    private static void removeGear(Level level, BlockPos bayPos) {
        for (int i = 1; i <= MAX_GEAR_LENGTH; i++) {
            BlockPos partPos = bayPos.below(i);
            if (!isGearPart(level.getBlockState(partPos))) {
                return;
            }
            level.setBlock(partPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        }
    }

    private static int getCurrentGearLength(Level level, BlockPos bayPos) {
        Block below = level.getBlockState(bayPos.below()).getBlock();
        if (below instanceof WarframeLandingStrutBlock) {
            return 2;
        }
        if (below instanceof WarframeLandingWheelBlock) {
            return 1;
        }
        return 0;
    }

    private static boolean isGearPart(BlockState state) {
        return state.getBlock() instanceof WarframeLandingWheelBlock || state.getBlock() instanceof WarframeLandingStrutBlock;
    }

    @Nullable
    public static BlockPos findBayAbove(BlockGetter level, BlockPos partPos) {
        for (int i = 1; i <= MAX_GEAR_LENGTH; i++) {
            BlockPos checkPos = partPos.above(i);
            Block block = level.getBlockState(checkPos).getBlock();
            if (block instanceof WarframeLandingGearBayBlock) {
                return checkPos;
            }
            if (!(block instanceof WarframeLandingStrutBlock)) {
                return null;
            }
        }
        return null;
    }

    public void cycleVariant(Level level, BlockPos bayPos, BlockState bayState, BlockPos wheelPos, BlockState wheelState, int step) {
        int max = WarframeLandingGearProperties.MAX_VARIANT;
        int current = bayState.getValue(VARIANT);
        int next = Math.floorMod(current - 1 + step, max) + 1;
        level.setBlock(bayPos, bayState.setValue(VARIANT, next), Block.UPDATE_ALL);
        level.setBlock(wheelPos, wheelState.setValue(WarframeLandingGearProperties.VARIANT, next), Block.UPDATE_ALL);
        level.playSound(null, wheelPos, SoundEvents.NETHERITE_BLOCK_STEP, SoundSource.BLOCKS, 1f, 1f);
    }

    @Nullable
    private static BlockPos getNodePos(CompoundTag data) {
        if (!data.contains("ControlX")) {
            return null;
        }
        return BlockPos.containing(data.getDouble("ControlX"), data.getDouble("ControlY"), data.getDouble("ControlZ"));
    }

    private static boolean readNodeSignal(ServerLevel level, @Nullable BlockPos nodePos) {
        return nodePos != null
                && wariumLoaded()
                && level.isLoaded(nodePos)
                && WarframeLandingGearWarium.readLandingGear(level, nodePos);
    }

    private static boolean wariumLoaded() {
        return ModList.get().isLoaded("valkyrien_warium");
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
        pTooltip.add(Component.translatable("tooltip.warframes.wrench_hint_wheel")
                .withStyle(ChatFormatting.GRAY));
    }
}

