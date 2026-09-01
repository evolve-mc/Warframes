package net.evo_mc.warframes.block.custom;

import net.minecraft.util.StringRepresentable;

public enum WarframeSlabType implements StringRepresentable {
    TOP("top"),
    BOTTOM("bottom"),
    NORTH("north"),
    SOUTH("south"),
    EAST("east"),
    WEST("west"),
    DOUBLE("double");

    private final String name;

    WarframeSlabType(String pName) {
        this.name = pName;
    }

    public String toString() {
        return this.name;
    }

    public String getSerializedName() {
        return this.name;
    }
}