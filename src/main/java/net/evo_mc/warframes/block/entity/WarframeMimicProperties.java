package net.evo_mc.warframes.block.entity;

import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraft.world.level.block.state.BlockState;

public class WarframeMimicProperties {
    public static final ModelProperty<BlockState> MIMICKED_BLOCK = new ModelProperty<>();
    public static final ModelProperty<Integer> TEXTURE_CYCLE_OFFSET = new ModelProperty<>();
}