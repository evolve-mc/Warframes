package net.evo_mc.warframes.block.custom;

import net.minecraft.util.StringRepresentable;

public enum WarframeSideStripPosition implements StringRepresentable {
    DOWN_X("down_x"),
    DOWN_Z("down_z"),
    UP_X("up_x"),
    UP_Z("up_z"),
    NORTH_X("north_x"),
    NORTH_Y("north_y"),
    SOUTH_X("south_x"),
    SOUTH_Y("south_y"),
    EAST_Y("east_y"),
    EAST_Z("east_z"),
    WEST_Y("west_y"),
    WEST_Z("west_z");

    private final String name;

    WarframeSideStripPosition(String name) {
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