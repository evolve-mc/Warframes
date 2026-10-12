package net.evo_mc.warframes.block.entity;

import net.evo_mc.warframes.block.custom.WarframeBombBayDoorBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public class WarframeBombBayDoorBlockEntity extends BlockEntity implements GeoBlockEntity, WarframeMimicStorage {
    public static final int CLOSE_DELAY = 50;
    private static final int REFRESH_THRESHOLD = CLOSE_DELAY - 5;
    private static final int MAX_GROUP_SIZE = 32;
    private static final int TRANSITION_TICKS = 2;

    private static final RawAnimation OPEN_ANIM = RawAnimation.begin().thenPlayAndHold("open");
    private static final RawAnimation CLOSE_ANIM = RawAnimation.begin().thenPlayAndHold("close");
    private static final RawAnimation OPENED_ANIM = RawAnimation.begin().thenPlayAndHold("opened");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Nullable
    private BlockState mimickedBlock = null;
    private boolean dropsSuppressed = false;

    private int holdTicks = 0;

    @Nullable
    private Boolean lastSeenOpen = null;
    private boolean hasMoved = false;

    public WarframeBombBayDoorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WARFRAME_BOMB_BAY_DOOR.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, WarframeBombBayDoorBlockEntity door) {
        boolean active = level.hasNeighborSignal(pos) || door.isTriggered(level);

        if (door.holdTicks > 0) {
            door.holdTicks--;
        }

        if (active) {
            if (door.holdTicks < REFRESH_THRESHOLD || !state.getValue(WarframeBombBayDoorBlock.OPEN)) {
                openGroup(level, pos);
            }
        } else if (door.holdTicks == 0 && state.getValue(WarframeBombBayDoorBlock.OPEN)) {
            setOpen(level, pos, state, false);
        }
    }

    private boolean isTriggered(Level level) {
        CompoundTag data = getPersistentData();
        if (!data.contains("ControlX")) {
            return false;
        }
        BlockPos nodePos = BlockPos.containing(data.getDouble("ControlX"), data.getDouble("ControlY"), data.getDouble("ControlZ"));
        if (!level.isLoaded(nodePos)) {
            return false;
        }
        BlockEntity node = level.getBlockEntity(nodePos);
        if (node == null) {
            return false;
        }
        return node.getPersistentData().getDouble("Trigger" + data.getDouble("Channel")) > 0.0;
    }

    private static void openGroup(Level level, BlockPos origin) {
        Set<BlockPos> visited = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        visited.add(origin);
        queue.add(origin);
        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            BlockState currentState = level.getBlockState(current);
            if (!(currentState.getBlock() instanceof WarframeBombBayDoorBlock)) {
                continue;
            }
            if (level.getBlockEntity(current) instanceof WarframeBombBayDoorBlockEntity door) {
                door.holdTicks = CLOSE_DELAY;
            }
            if (!currentState.getValue(WarframeBombBayDoorBlock.OPEN)) {
                setOpen(level, current, currentState, true);
            }
            for (BlockPos next : WarframeBombBayDoorBlock.connectedDoors(level, current, currentState)) {
                if (visited.size() >= MAX_GROUP_SIZE) {
                    break;
                }
                if (visited.add(next)) {
                    queue.add(next);
                }
            }
        }
    }

    private static void setOpen(Level level, BlockPos pos, BlockState state, boolean open) {
        level.setBlock(pos, state.setValue(WarframeBombBayDoorBlock.OPEN, open), Block.UPDATE_ALL);
        level.playSound(null, pos, open ? SoundEvents.IRON_TRAPDOOR_OPEN : SoundEvents.IRON_TRAPDOOR_CLOSE,
                SoundSource.BLOCKS, 1.0F, 0.8F);
    }

    private PlayState animate(AnimationState<WarframeBombBayDoorBlockEntity> state) {
        boolean open = getBlockState().getValue(WarframeBombBayDoorBlock.OPEN);
        if (lastSeenOpen == null) {
            lastSeenOpen = open;
        } else if (open != lastSeenOpen) {
            lastSeenOpen = open;
            hasMoved = true;
        }
        if (!hasMoved) {
            return open ? state.setAndContinue(OPENED_ANIM) : PlayState.STOP;
        }
        return state.setAndContinue(open ? OPEN_ANIM : CLOSE_ANIM);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "doors", TRANSITION_TICKS, this::animate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition.offset(-2, -2, -2), worldPosition.offset(3, 2, 3));
    }

    @Override
    @Nullable
    public BlockState getMimickedBlock() {
        return mimickedBlock;
    }

    @Override
    public void setMimickedBlock(BlockState state) {
        this.mimickedBlock = state;
        setChanged();
        notifyClientOfChange();
    }

    @Override
    public void clearMimickedBlock() {
        this.mimickedBlock = null;
        setChanged();
        notifyClientOfChange();
    }

    private void notifyClientOfChange() {
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL_IMMEDIATE);
        }
    }

    @Override
    public void suppressDrops() {
        this.dropsSuppressed = true;
    }

    @Override
    public void drops() {
        if (dropsSuppressed || level == null || mimickedBlock == null) {
            return;
        }
        ItemStack stack = new ItemStack(mimickedBlock.getBlock());
        if (!stack.isEmpty()) {
            Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (mimickedBlock != null) {
            tag.put("mimickedState", NbtUtils.writeBlockState(mimickedBlock));
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("mimickedState", Tag.TAG_COMPOUND)) {
            BlockState loaded = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), tag.getCompound("mimickedState"));
            this.mimickedBlock = loaded.isAir() ? null : loaded;
        } else {
            this.mimickedBlock = null;
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithFullMetadata();
    }
}