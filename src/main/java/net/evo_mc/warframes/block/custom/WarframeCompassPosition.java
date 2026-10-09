package net.evo_mc.warframes.block.custom;

import net.minecraft.util.StringRepresentable;

public enum WarframeCompassPosition implements StringRepresentable {
    NORTH("north"),
    SOUTH("south"),
    EAST("east"),
    WEST("west"),
    NORTHEAST("northeast"),
    NORTHWEST("northwest"),
    SOUTHEAST("southeast"),
    SOUTHWEST("southwest"),
    CENTER("center");

    private final String name;

    WarframeCompassPosition(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}