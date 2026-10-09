package net.evo_mc.warframes.block.entity;

import net.evo_mc.warframes.Warframes;
import net.evo_mc.warframes.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Warframes.MOD_ID);

    public static final RegistryObject<BlockEntityType<WarframeMimicBlockEntity>> WARFRAME_MIMIC_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("warframe_mimic_be", () ->
                    BlockEntityType.Builder.of(WarframeMimicBlockEntity::new,
                            ModBlocks.AIRFRAME_BLOCK.get(),
                            ModBlocks.AIRFRAME_SLAB.get(),
                            ModBlocks.AIRFRAME_SLAB_WING.get(),
                            ModBlocks.AIRFRAME_LAYER.get(),
                            ModBlocks.AIRFRAME_STAIRS.get(),
                            ModBlocks.AIRFRAME_HALF_SLAB.get(),
                            ModBlocks.AIRFRAME_QUARTER_SLAB.get(),
                            ModBlocks.AIRFRAME_CENTERED_LAYER.get(),
                            ModBlocks.AIRFRAME_EDGE_STRIP_LAYER.get(),
                            ModBlocks.AIRFRAME_CORE_STRIP_LAYER.get(),
                            ModBlocks.AIRFRAME_SLAB_STABILIZER.get(),
                            ModBlocks.AIRFRAME_VERTICAL_SLAB_STABILIZER.get(),
                            ModBlocks.AIRFRAME_HALF_SLAB_WING.get(),
                            ModBlocks.AIRFRAME_HALF_SLAB_STABILIZER.get(),
                            ModBlocks.AIRFRAME_VERTICAL_HALF_SLAB_STABILIZER.get(),
                            ModBlocks.AIRFRAME_CENTERED_STAIRS.get(),
                            ModBlocks.AIRFRAME_POINT_LAYER.get(),
                            ModBlocks.AIRFRAME_HALF_STAIRS.get(),
                            ModBlocks.AIRFRAME_DOOR.get(),
                            ModBlocks.AIRFRAME_TRAPDOOR.get(),
                            ModBlocks.AIRFRAME_LADDER.get(),
                            ModBlocks.AIRFRAME_BUTTON.get(),
                            ModBlocks.AIRFRAME_LEVER.get(),
                            ModBlocks.AIRFRAME_SLAB_STABILIZER_LAYER.get(),
                            ModBlocks.AIRFRAME_SLAB_WING_LAYER.get(),
                            ModBlocks.AIRFRAME_VERTICAL_SLAB_STABILIZER_LAYER.get(),
                            ModBlocks.AIRFRAME_PANEL.get(),
                            ModBlocks.AIRFRAME_NOSE_CONE.get(),
                            ModBlocks.AIRFRAME_STEPPED_PYRAMID.get(),
                            ModBlocks.AIRFRAME_SIDE_STRIP_LAYER.get(),
                            ModBlocks.AIRFRAME_STEPPED_SLAB_WING.get(),
                            ModBlocks.AIRFRAME_STEPPED_SLAB_STABILIZER.get(),
                            ModBlocks.AIRFRAME_HALF_DOOR.get(),
                            ModBlocks.AIRFRAME_COUNTERMEASURE_LAUNCHER.get(),
                            ModBlocks.AIRFRAME_COUNTERMEASURE_LAUNCHER_SLAB.get(),
                            ModBlocks.AIRFRAME_SLAB_LANDING_GEAR_BAY.get(),
                            ModBlocks.AIRFRAME_SLIM_LANDING_GEAR_BAY.get(),
                            ModBlocks.AIRFRAME_LANDING_GEAR_BAY.get(),
                            ModBlocks.AIRFRAME_RAM_SCOOP_RADIATOR.get(),
                            ModBlocks.AIRFRAME_RAM_SCOOP_RADIATOR_SLAB.get(),
                            ModBlocks.AIRFRAME_COVERED_AUTOCANNON_BARREL.get(),
                            ModBlocks.AIRFRAME_COVERED_ROTARY_AUTOCANNON_BARREL.get(),
                            ModBlocks.AIRFRAME_AIR_INTAKE.get(),
                            ModBlocks.AIRFRAME_COMPRESSOR_INTAKE_COWL.get(),
                            ModBlocks.HEAVYFRAME_BLOCK.get(),
                            ModBlocks.HEAVYFRAME_SLAB.get(),
                            ModBlocks.HEAVYFRAME_HALF_SLAB.get(),
                            ModBlocks.HEAVYFRAME_QUARTER_SLAB.get(),
                            ModBlocks.HEAVYFRAME_STAIRS.get(),
                            ModBlocks.HEAVYFRAME_HALF_STAIRS.get(),
                            ModBlocks.HEAVYFRAME_PANEL.get(),
                            ModBlocks.HEAVYFRAME_CENTERED_STAIRS.get(),
                            ModBlocks.HEAVYFRAME_STEPPED_PYRAMID.get(),
                            ModBlocks.HEAVYFRAME_NOSE_CONE.get(),
                            ModBlocks.HEAVYFRAME_LAYER.get(),
                            ModBlocks.HEAVYFRAME_CENTERED_LAYER.get(),
                            ModBlocks.HEAVYFRAME_EDGE_STRIP_LAYER.get(),
                            ModBlocks.HEAVYFRAME_SIDE_STRIP_LAYER.get(),
                            ModBlocks.HEAVYFRAME_CORE_STRIP_LAYER.get(),
                            ModBlocks.HEAVYFRAME_POINT_LAYER.get(),
                            ModBlocks.HEAVYFRAME_TRAPDOOR.get(),
                            ModBlocks.HEAVYFRAME_DOOR.get(),
                            ModBlocks.HEAVYFRAME_HALF_DOOR.get(),
                            ModBlocks.HEAVYFRAME_LADDER.get(),
                            ModBlocks.HEAVYFRAME_BUTTON.get(),
                            ModBlocks.HEAVYFRAME_LEVER.get(),
                            ModBlocks.HEAVYFRAME_COVERED_AUTOCANNON_BARREL.get(),
                            ModBlocks.HEAVYFRAME_COVERED_ROTARY_AUTOCANNON_BARREL.get()
                    ).build(null));


    public static final RegistryObject<BlockEntityType<WarframeChestBlockEntity>> WARFRAME_CHEST =
            BLOCK_ENTITIES.register("airframe_chest", () ->
                    BlockEntityType.Builder.of(WarframeChestBlockEntity::new,
                            ModBlocks.AIRFRAME_CHEST.get(),
                            ModBlocks.HEAVYFRAME_CHEST.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<WarframeControlSurfaceBlockEntity>> WARFRAME_CONTROL_SURFACE =
            BLOCK_ENTITIES.register("warframe_control_surface", () -> BlockEntityType.Builder.of(
                    WarframeControlSurfaceBlockEntity::new,
                    ModBlocks.AIRFRAME_CONTROL_SURFACE.get(),
                    ModBlocks.AIRFRAME_STEPPED_CONTROL_SURFACE.get(),
                    ModBlocks.AIRFRAME_HALF_CONTROL_SURFACE.get()
            ).build(null));


    public static final RegistryObject<BlockEntityType<WarframeVerticalControlSurfaceBlockEntity>> WARFRAME_VERTICAL_CONTROL_SURFACE =
            BLOCK_ENTITIES.register("warframe_vertical_control_surface", () -> BlockEntityType.Builder.of(
                    WarframeVerticalControlSurfaceBlockEntity::new,
                    ModBlocks.AIRFRAME_VERTICAL_CONTROL_SURFACE.get(),
                    ModBlocks.AIRFRAME_VERTICAL_STEPPED_CONTROL_SURFACE.get(),
                    ModBlocks.AIRFRAME_VERTICAL_HALF_CONTROL_SURFACE.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<WarframeHorizontalAirBrakeBlockEntity>> WARFRAME_HORIZONTAL_AIR_BRAKE =
            BLOCK_ENTITIES.register("warframe_air_brake", () -> BlockEntityType.Builder.of(
                    WarframeHorizontalAirBrakeBlockEntity::new,
                    ModBlocks.AIRFRAME_HORIZONTAL_AIR_BRAKE.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<WarframeVerticalAirBrakeBlockEntity>> WARFRAME_VERTICAL_AIR_BRAKE =
            BLOCK_ENTITIES.register("warframe_vertical_air_brake", () -> BlockEntityType.Builder.of(
                    WarframeVerticalAirBrakeBlockEntity::new,
                    ModBlocks.AIRFRAME_VERTICAL_AIR_BRAKE.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<WarframeBoardBlockEntity>> WARFRAME_BOARD_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("warframe_board_be", () ->
                    BlockEntityType.Builder.of(WarframeBoardBlockEntity::new,
                    ModBlocks.AIRFRAME_BOARD.get(),
                    ModBlocks.HEAVYFRAME_BOARD.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<WarframeHalfLayerBlockEntity>> WARFRAME_HALF_LAYER_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("warframe_half_layer_be", () ->
                    BlockEntityType.Builder.of(WarframeHalfLayerBlockEntity::new,
                    ModBlocks.AIRFRAME_HALF_LAYER.get(),
                    ModBlocks.AIRFRAME_HALF_PANEL.get(),
                    ModBlocks.HEAVYFRAME_HALF_LAYER.get(),
                    ModBlocks.HEAVYFRAME_HALF_PANEL.get()
            ).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}