package net.evo_mc.warframes.block.custom;

import net.minecraft.util.StringRepresentable;

public enum WarframeVerticalSlabPosition implements StringRepresentable {
    LEFT("left"),
    RIGHT("right"),
    CENTER("center");

    private final String name;

    WarframeVerticalSlabPosition(String pName) {
        this.name = pName;
    }

    public String toString() {
        return this.name;
    }

    public String getSerializedName() {
        return this.name;
    }
}