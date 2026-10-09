package net.evo_mc.warframes.block.entity;

import net.evo_mc.warframes.block.custom.WarframeMultiMimicable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
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
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

import java.util.Arrays;

public abstract class WarframeMultiMimicBlockEntity extends BlockEntity {

    private final int slotCount;
    private final BlockState[] mimickedBlocks;
    private final int[] placedCounts;

    private boolean dropsSuppressed = false;

    protected WarframeMultiMimicBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState, int slotCount) {
        super(pType, pPos, pBlockState);
        this.slotCount = slotCount;
        this.mimickedBlocks = new BlockState[slotCount];
        this.placedCounts = new int[slotCount];
    }

    protected abstract String getSlotKey(int slot);

    public int getSlotCount() {
        return slotCount;
    }

    @Nullable
    public BlockState getMimickedBlock(int slot) {
        return mimickedBlocks[slot];
    }

    public void setMimickedBlock(int slot, BlockState state) {
        this.mimickedBlocks[slot] = state;
        markMimicChanged();
    }

    public void clearMimickedBlock(int slot) {
        this.mimickedBlocks[slot] = null;
        markMimicChanged();
    }

    public int registerPlacement(BlockState state) {
        int grown = -1;
        for(int slot = 0; slot < slotCount; slot++) {
            int count = slotPresence(state, slot);
            if(count > placedCounts[slot] && grown < 0) {
                grown = slot;
            }
            placedCounts[slot] = count;
        }
        setChanged();
        return grown;
    }
    public List<BlockState> releaseRemovedSlots(BlockState state) {
        List<BlockState> released = new ArrayList<>();
        for(int slot = 0; slot < slotCount; slot++) {
            if(mimickedBlocks[slot] != null && slotPresence(state, slot) <= 0) {
                released.add(mimickedBlocks[slot]);
                mimickedBlocks[slot] = null;
            }
        }
        registerPlacement(state);
        if(!released.isEmpty()) {
            markMimicChanged();
        }
        return released;
    }

    private static int slotPresence(BlockState state, int slot) {
        return state.getBlock() instanceof WarframeMultiMimicable mimicable ? mimicable.getMimicSlotPresence(state, slot) : 0;
    }

    private void markMimicChanged() {
        setChanged();
        requestModelDataUpdate();
        if(level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL_IMMEDIATE);
        }
    }

    public void suppressDrops() {
        this.dropsSuppressed = true;
    }

    public void drops() {
        if(dropsSuppressed || level == null) {
            return;
        }
        for(BlockState mimicked : mimickedBlocks) {
            if(mimicked != null) {
                ItemStack stack = new ItemStack(mimicked.getBlock());
                if(!stack.isEmpty()) {
                    Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack);
                }
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putIntArray("placedCounts", placedCounts);
        CompoundTag states = new CompoundTag();
        for(int slot = 0; slot < slotCount; slot++) {
            if(mimickedBlocks[slot] != null) {
                states.put(getSlotKey(slot), NbtUtils.writeBlockState(mimickedBlocks[slot]));
            }
        }
        pTag.put("mimickedStates", states);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        int[] storedCounts = pTag.getIntArray("placedCounts");
        for(int slot = 0; slot < slotCount; slot++) {
            placedCounts[slot] = storedCounts.length == slotCount ? storedCounts[slot] : slotPresence(getBlockState(), slot);
        }
        Arrays.fill(mimickedBlocks, null);
        CompoundTag states = pTag.getCompound("mimickedStates");
        CompoundTag legacyBlocks = pTag.getCompound("mimickedBlocks");
        for(int slot = 0; slot < slotCount; slot++) {
            String key = getSlotKey(slot);
            if(states.contains(key, Tag.TAG_COMPOUND)) {
                BlockState loaded = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), states.getCompound(key));
                mimickedBlocks[slot] = loaded.isAir() ? null : loaded;
            } else if(legacyBlocks.contains(key)) {
                Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(legacyBlocks.getString(key)));
                mimickedBlocks[slot] = block != null ? block.defaultBlockState() : null;
            }
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

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        requestModelDataUpdate();
        if(level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_IMMEDIATE);
        }
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder()
                .with(WarframeMultiMimicProperties.MIMICKED_BLOCKS, mimickedBlocks.clone())
                .build();
    }
}