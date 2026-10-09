package net.evo_mc.warframes.block.entity;

import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public interface WarframeMimicStorage {

    @Nullable
    BlockState getMimickedBlock();

    void setMimickedBlock(BlockState state);

    void clearMimickedBlock();

    void suppressDrops();

    void drops();
}