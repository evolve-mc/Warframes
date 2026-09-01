package net.evo_mc.warframes.block.custom;

import net.minecraft.util.StringRepresentable;

public enum WarframeSlabWingType implements StringRepresentable {
    TOP("top"),
    BOTTOM("bottom"),
    CENTER("center"),
    DOUBLE("double");

    private final String name;

    WarframeSlabWingType(String pName) {
        this.name = pName;
    }

    public String toString() {
        return this.name;
    }

    public String getSerializedName() {
        return this.name;
    }
}