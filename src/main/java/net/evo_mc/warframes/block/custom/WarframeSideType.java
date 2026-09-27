package net.evo_mc.warframes.block.custom;

import net.minecraft.util.StringRepresentable;

public enum WarframeSideType implements StringRepresentable {
    NONE("none"),
    LEFT("left"),
    RIGHT("right");

    private final String name;

    WarframeSideType(String pName) {
        this.name = pName;
    }

    public String toString() {
        return this.name;
    }

    public String getSerializedName() {
        return this.name;
    }
}