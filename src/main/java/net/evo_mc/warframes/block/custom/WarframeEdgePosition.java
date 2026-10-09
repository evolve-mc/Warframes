package net.evo_mc.warframes.block.custom;

import net.minecraft.util.StringRepresentable;

public enum WarframeEdgePosition implements StringRepresentable {
    NORTH("north"),
    SOUTH("south"),
    EAST("east"),
    WEST("west"),
    NORTHEAST("northeast"),
    NORTHWEST("northwest"),
    SOUTHEAST("southeast"),
    SOUTHWEST("southwest");

    private final String name;

    WarframeEdgePosition(String name) {
        this.name = name;
    }

    public boolean isCorner() {
        return this == NORTHEAST || this == NORTHWEST || this == SOUTHEAST || this == SOUTHWEST;
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