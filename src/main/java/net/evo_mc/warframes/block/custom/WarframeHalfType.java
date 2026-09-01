package net.evo_mc.warframes.block.custom;

import net.minecraft.util.StringRepresentable;

public enum WarframeHalfType implements StringRepresentable {
    TOP("top"),
    BOTTOM("bottom"),
    CENTER("center"),
    FULL("full");

    private final String name;

    WarframeHalfType(String pName) {
        this.name = pName;
    }

    public String toString() {
        return this.name;
    }

    public String getSerializedName() {
        return this.name;
    }
}