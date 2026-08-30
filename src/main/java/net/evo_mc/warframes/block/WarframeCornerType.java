package net.evo_mc.warframes.block;

import net.minecraft.util.StringRepresentable;

public enum WarframeCornerType implements StringRepresentable {
    NONE("none"),
    NORTHEAST("northeast"),
    NORTHWEST("northwest"),
    SOUTHEAST("southeast"),
    SOUTHWEST("southwest");

    private final String name;

    WarframeCornerType(String pName) {
        this.name = pName;
    }

    public String toString() {
        return this.name;
    }

    public String getSerializedName() {
        return this.name;
    }
}