package net.evo_mc.warframes.block.custom;

import net.minecraft.util.StringRepresentable;

public enum WarframeAxis implements StringRepresentable {
    CENTER_X("center_x"),
    CENTER_Y("center_y"),
    CENTER_Z("center_z"),
    NONE("none");

    private final String name;

    WarframeAxis(String pName) {
        this.name = pName;
    }

    public String toString() {
        return this.name;
    }

    public String getSerializedName() {
        return this.name;
    }
}