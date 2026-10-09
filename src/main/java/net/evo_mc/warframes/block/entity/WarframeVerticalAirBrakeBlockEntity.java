package net.evo_mc.warframes.block.entity;

import net.evo_mc.warframes.block.custom.WarframeVerticalAirBrakeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationController.State;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WarframeVerticalAirBrakeBlockEntity extends BlockEntity implements GeoBlockEntity, WarframeMimicStorage {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private String prevAnim = "0";

    @Nullable
    private BlockState mimickedBlock = null;

    private boolean dropsSuppressed = false;

    public WarframeVerticalAirBrakeBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.WARFRAME_VERTICAL_AIR_BRAKE.get(), pos, state);
    }

    public WarframeVerticalAirBrakeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    private PlayState procedurePredicate(software.bernie.geckolib.core.animation.AnimationState<WarframeVerticalAirBrakeBlockEntity> event) {
        String animation = String.valueOf(this.getBlockState().getValue(WarframeVerticalAirBrakeBlock.ANIMATION));

        if (!animation.equals("0") && event.getController().getAnimationState() == State.STOPPED
                || !animation.equals(prevAnim) && !animation.equals("0")) {
            if (!animation.equals(prevAnim)) {
                event.getController().forceAnimationReset();
            }
            event.getController().setAnimation(RawAnimation.begin().thenPlay(animation));
            if (event.getController().getAnimationState() == State.STOPPED) {
                this.level.setBlock(this.getBlockPos(),
                        this.getBlockState().setValue(WarframeVerticalAirBrakeBlock.ANIMATION, 0), 3);
                event.getController().forceAnimationReset();
            }
        } else if (animation.equals("0")) {
            prevAnim = "0";
            return PlayState.STOP;
        }

        prevAnim = animation;
        return PlayState.CONTINUE;
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::procedurePredicate));
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
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
        if(level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL_IMMEDIATE);
        }
    }

    @Override
    public void suppressDrops() {
        this.dropsSuppressed = true;
    }

    @Override
    public void drops() {
        if(dropsSuppressed || level == null || mimickedBlock == null) {
            return;
        }
        ItemStack stack = new ItemStack(mimickedBlock.getBlock());
        if(!stack.isEmpty()) {
            Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if(mimickedBlock != null) {
            pTag.put("mimickedState", NbtUtils.writeBlockState(mimickedBlock));
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if(pTag.contains("mimickedState", Tag.TAG_COMPOUND)) {
            BlockState loaded = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), pTag.getCompound("mimickedState"));
            this.mimickedBlock = loaded.isAir() ? null : loaded;
        } else if(pTag.contains("mimickedBlock")) {
            Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(pTag.getString("mimickedBlock")));
            this.mimickedBlock = block != null ? block.defaultBlockState() : null;
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
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }
}