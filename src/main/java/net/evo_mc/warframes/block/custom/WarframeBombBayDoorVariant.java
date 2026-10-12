package net.evo_mc.warframes.block.custom;

import net.minecraft.util.StringRepresentable;

public enum WarframeBombBayDoorVariant implements StringRepresentable {
    SPLIT("split", "airframe_bomb_bay_door_double"),
    TRAPDOOR("trapdoor", "airframe_bomb_bay_door_single"),
    LONG("long", "airframe_bomb_bay_door_single_long");

    private final String name;
    private final String modelName;

    WarframeBombBayDoorVariant(String name, String modelName) {
        this.name = name;
        this.modelName = modelName;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public String getModelName() {
        return modelName;
    }

    public static WarframeBombBayDoorVariant byIndex(int index) {
        WarframeBombBayDoorVariant[] values = values();
        return values[Math.max(0, Math.min(values.length - 1, index))];
    }
}