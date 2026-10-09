package net.evo_mc.warframes.block.custom;

import net.minecraft.util.StringRepresentable;

public enum WarframeHalfStairPosition implements StringRepresentable {
    NORTH_LEFT("north_left"),
    NORTH_RIGHT("north_right"),
    SOUTH_LEFT("south_left"),
    SOUTH_RIGHT("south_right"),
    EAST_LEFT("east_left"),
    EAST_RIGHT("east_right"),
    WEST_LEFT("west_left"),
    WEST_RIGHT("west_right"),
    NORTHEAST("northeast"),
    NORTHWEST("northwest"),
    SOUTHEAST("southeast"),
    SOUTHWEST("southwest");

    private final String name;

    WarframeHalfStairPosition(String name) {
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