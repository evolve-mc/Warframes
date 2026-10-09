package net.evo_mc.warframes.block.custom;

import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public final class WarframeLandingGearProperties {

    public static final int MAX_VARIANT = 10;

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final IntegerProperty VARIANT = IntegerProperty.create("variant", 1, MAX_VARIANT);
    public static final EnumProperty<WarframeHorizontalSlabPosition> HALF = EnumProperty.create("half", WarframeHorizontalSlabPosition.class);

    private WarframeLandingGearProperties() {
    }
}