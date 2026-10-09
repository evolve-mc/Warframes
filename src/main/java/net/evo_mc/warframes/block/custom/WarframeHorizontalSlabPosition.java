package net.evo_mc.warframes.block.custom;

import net.minecraft.util.StringRepresentable;

public enum WarframeHorizontalSlabPosition implements StringRepresentable {
    TOP("top"),
    CENTER("center"),
    BOTTOM("bottom");

    private final String name;

    WarframeHorizontalSlabPosition(String name) {
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