package net.evo_mc.warframes.block;

import net.minecraft.util.StringRepresentable;

public enum WarframeSlabType implements StringRepresentable {
    TOP("top"),
    BOTTOM("bottom"),
    CENTER("center"),
    DOUBLE("double");

    private final String name;

    private WarframeSlabType(String pName) {
        this.name = pName;
    }

    public String toString() {
        return this.name;
    }

    public String getSerializedName() {
        return this.name;
    }
}