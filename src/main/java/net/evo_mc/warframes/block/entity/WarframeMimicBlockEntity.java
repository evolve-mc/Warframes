package net.evo_mc.warframes.block.entity;

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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

public class WarframeMimicBlockEntity extends BlockEntity implements WarframeMimicStorage {

    @Nullable
    private BlockState mimickedBlock = null;

    private boolean dropsSuppressed = false;

    public WarframeMimicBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.WARFRAME_MIMIC_BLOCK_ENTITY.get(), pPos, pBlockState);
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
        requestModelDataUpdate();
        notifyClientOfChange();
    }

    @Override
    public void clearMimickedBlock() {
        this.mimickedBlock = null;
        setChanged();
        requestModelDataUpdate();
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

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        BlockState previous = mimickedBlock;
        super.onDataPacket(net, pkt);
        if(previous != mimickedBlock) {
            requestModelDataUpdate();
            if(level != null) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_IMMEDIATE);
            }
        }
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder()
                .with(WarframeMimicProperties.MIMICKED_BLOCK, mimickedBlock)
                .build();
    }


}