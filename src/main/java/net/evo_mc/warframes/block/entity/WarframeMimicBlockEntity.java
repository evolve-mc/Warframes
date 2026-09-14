package net.evo_mc.warframes.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WarframeMimicBlockEntity extends BlockEntity {

    public final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected int getStackLimit(int slot, @NotNull ItemStack stack) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(level != null && !level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    @Nullable
    private BlockState mimickedBlock = null;

    private int textureCycleOffset = 0;

    public WarframeMimicBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.WARFRAME_MIMIC_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    @Nullable
    public BlockState getMimickedBlock() {
        return mimickedBlock;
    }

    public int getTextureCycleOffset() {
        return textureCycleOffset;
    }

    public void setMimickedBlock(BlockState state) {
        this.mimickedBlock = state;
        this.textureCycleOffset = 0;
        setChanged();
        notifyClientOfChange();
        requestModelDataUpdate();
    }

    public void clearMimickedBlock() {
        this.mimickedBlock = null;
        this.textureCycleOffset = 0;
        setChanged();
        notifyClientOfChange();
        requestModelDataUpdate();
    }

    public void cycleTexture() {
        this.textureCycleOffset = (this.textureCycleOffset + 1) % 6;
        setChanged();
        notifyClientOfChange();
        requestModelDataUpdate();
    }

    private void notifyClientOfChange() {
        if(level != null) {
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            } else {
                net.minecraft.client.Minecraft.getInstance().levelRenderer.blockChanged(
                        level, getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    }

    public void clearContents() {
        inventory.setStackInSlot(0, ItemStack.EMPTY);
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(inventory.getSlots());
        for(int i = 0; i < inventory.getSlots(); i++) {
            inv.setItem(i, inventory.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("inventory", inventory.serializeNBT());
        pTag.putInt("textureCycleOffset", textureCycleOffset);
        if(mimickedBlock != null) {
            pTag.putString("mimickedBlock", BuiltInRegistries.BLOCK.getKey(mimickedBlock.getBlock()).toString());
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        inventory.deserializeNBT(pTag.getCompound("inventory"));
        textureCycleOffset = pTag.contains("textureCycleOffset") ? pTag.getInt("textureCycleOffset") : 0;
        if(pTag.contains("mimickedBlock")) {
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
    public ModelData getModelData() {
        return ModelData.builder()
                .with(WarframeMimicProperties.MIMICKED_BLOCK, mimickedBlock)
                .with(WarframeMimicProperties.TEXTURE_CYCLE_OFFSET, textureCycleOffset)
                .build();
    }
}