package net.evo_mc.warframes.block.custom;

import net.minecraft.util.StringRepresentable;

public enum WarframeScoopPosition implements StringRepresentable {
    BOTTOM("bottom"),
    TOP("top"),
    LEFT("left"),
    RIGHT("right");

    private final String name;

    WarframeScoopPosition(String name) {
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