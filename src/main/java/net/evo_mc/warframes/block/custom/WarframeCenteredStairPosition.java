package net.evo_mc.warframes.block.custom;

import net.minecraft.util.StringRepresentable;

public enum WarframeCenteredStairPosition implements StringRepresentable {
    DOWN_X("down_x"),
    DOWN_Z("down_z"),
    UP_X("up_x"),
    UP_Z("up_z"),
    NORTH_Y("north_y"),
    NORTH_Z("north_z"),
    SOUTH_Y("south_y"),
    SOUTH_Z("south_z"),
    EAST_Y("east_y"),
    EAST_X("east_x"),
    WEST_Y("west_y"),
    WEST_X("west_x");

    private final String name;

    WarframeCenteredStairPosition(String name) {
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