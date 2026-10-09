package net.evo_mc.warframes.block;

import net.evo_mc.warframes.Warframes;
import net.evo_mc.warframes.block.custom.*;
import net.evo_mc.warframes.item.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Warframes.MOD_ID);

    public static final BlockSetType WARFRAME_IRON_SOUND = BlockSetType.register(new BlockSetType(
            "metal_sound",
            true,
            SoundType.NETHERITE_BLOCK,
            SoundEvents.IRON_DOOR_CLOSE,
            SoundEvents.IRON_DOOR_OPEN,
            SoundEvents.IRON_TRAPDOOR_CLOSE,
            SoundEvents.IRON_TRAPDOOR_OPEN,
            SoundEvents.METAL_PRESSURE_PLATE_CLICK_OFF,
            SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON,
            SoundEvents.STONE_BUTTON_CLICK_OFF,
            SoundEvents.STONE_BUTTON_CLICK_ON
    ));

    public static final RegistryObject<Block> AIRFRAME_BLOCK = registerBlock("airframe_block",
            () -> new WarframeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_SLAB = registerBlock("airframe_slab",
            () -> new WarframeSlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_SLAB_WING = registerBlock("airframe_slab_wing",
            () -> new WarframeSlabWingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.ANVIL).noOcclusion().strength(1.0f)));
    public static final RegistryObject<Block> AIRFRAME_LAYER = registerBlock("airframe_layer",
            () -> new WarframeLayerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_STAIRS = registerBlock("airframe_stairs",
            () -> new WarframeStairsBlock(() -> AIRFRAME_BLOCK.get().defaultBlockState(),BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_HALF_SLAB = registerBlock("airframe_half_slab",
            () -> new WarframeHalfSlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_QUARTER_SLAB = registerBlock("airframe_quarter_slab",
            () -> new WarframeQuarterSlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_CENTERED_LAYER = registerBlock("airframe_centered_layer",
            () -> new WarframeCenteredLayerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_EDGE_STRIP_LAYER = registerBlock("airframe_edge_strip_layer",
            () -> new WarframeEdgeStripLayerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_CORE_STRIP_LAYER = registerBlock("airframe_core_strip_layer",
            () -> new WarframeCoreStripLayerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_SLAB_STABILIZER = registerBlock("airframe_slab_stabilizer",
            () -> new WarframeSlabStabilizerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.ANVIL).noOcclusion().strength(1.0f)));
    public static final RegistryObject<Block> AIRFRAME_VERTICAL_SLAB_STABILIZER = registerBlock("airframe_vertical_slab_stabilizer",
            () -> new WarframeVerticalSlabStabilizerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.ANVIL).noOcclusion().strength(1.0f)));
    public static final RegistryObject<Block> AIRFRAME_HALF_SLAB_WING = registerBlock("airframe_half_slab_wing",
            () -> new WarframeHalfSlabWingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.ANVIL).noOcclusion().strength(1.0f)));
    public static final RegistryObject<Block> AIRFRAME_HALF_SLAB_STABILIZER = registerBlock("airframe_half_slab_stabilizer",
            () -> new WarframeHalfSlabStabilizerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.ANVIL).noOcclusion().strength(1.0f)));
    public static final RegistryObject<Block> AIRFRAME_VERTICAL_HALF_SLAB_STABILIZER = registerBlock("airframe_vertical_half_slab_stabilizer",
            () -> new WarframeVerticalHalfSlabStabilizerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.ANVIL).noOcclusion().strength(1.0f)));
    public static final RegistryObject<Block> AIRFRAME_CENTERED_STAIRS = registerBlock("airframe_centered_stairs",
            () -> new WarframeCenteredStairsBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_POINT_LAYER = registerBlock("airframe_point_layer",
            () -> new WarframePointLayerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_HALF_STAIRS = registerBlock("airframe_half_stairs",
            () -> new WarframeHalfStairBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_DOOR = registerBlock("airframe_door",
            () -> new WarframeDoorBlock(WARFRAME_IRON_SOUND,BlockBehaviour.Properties.copy(Blocks.OAK_DOOR).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_TRAPDOOR = registerBlock("airframe_trapdoor",
            () -> new WarframeTrapdoorBlock(WARFRAME_IRON_SOUND,BlockBehaviour.Properties.copy(Blocks.OAK_TRAPDOOR).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_LADDER = registerBlock("airframe_ladder",
            () -> new WarframeLadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_BUTTON = registerBlock("airframe_button",
            () -> new WarframeButtonBlock(BlockBehaviour.Properties.copy(Blocks.STONE_BUTTON).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f),WARFRAME_IRON_SOUND, 20, false));
    public static final RegistryObject<Block> AIRFRAME_LEVER = registerBlock("airframe_lever",
            () -> new WarframeLeverBlock(BlockBehaviour.Properties.copy(Blocks.LEVER).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_CHEST = registerBlock("airframe_chest",
            () -> new WarframeChestBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_CONTROL_SURFACE = registerBlock("airframe_control_surface",
            () -> new WarframeControlSurfaceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.ANVIL).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_VERTICAL_CONTROL_SURFACE = registerBlock("airframe_vertical_control_surface",
            () -> new WarframeVerticalControlSurfaceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.ANVIL).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_SLAB_STABILIZER_LAYER = registerBlock("airframe_slab_stabilizer_layer",
            () -> new WarframeSlabStabilizerLayerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.ANVIL).noOcclusion().strength(1.0f)));
    public static final RegistryObject<Block> AIRFRAME_SLAB_WING_LAYER = registerBlock("airframe_slab_wing_layer",
            () -> new WarframeSlabWingLayerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.ANVIL).noOcclusion().strength(1.0f)));
    public static final RegistryObject<Block> AIRFRAME_VERTICAL_SLAB_STABILIZER_LAYER = registerBlock("airframe_vertical_slab_stabilizer_layer",
            () -> new WarframeVerticalSlabStabilizerLayerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.ANVIL).noOcclusion().strength(1.0f)));
    public static final RegistryObject<Block> AIRFRAME_STEPPED_CONTROL_SURFACE = registerBlock("airframe_stepped_control_surface",
            () -> new WarframeSteppedControlSurfaceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.ANVIL).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_HALF_CONTROL_SURFACE = registerBlock("airframe_half_control_surface",
            () -> new WarframeHalfControlSurfaceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.ANVIL).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_VERTICAL_STEPPED_CONTROL_SURFACE = registerBlock("airframe_vertical_stepped_control_surface",
            () -> new WarframeVerticalSteppedControlSurfaceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.ANVIL).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_VERTICAL_HALF_CONTROL_SURFACE = registerBlock("airframe_vertical_half_control_surface",
            () -> new WarframeVerticalHalfControlSurfaceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.ANVIL).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_HORIZONTAL_AIR_BRAKE = registerBlock("airframe_horizontal_air_brake",
            () -> new WarframeHorizontalAirBrakeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.ANVIL).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_VERTICAL_AIR_BRAKE = registerBlock("airframe_vertical_air_brake",
            () -> new WarframeVerticalAirBrakeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.ANVIL).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_PANEL = registerBlock("airframe_panel",
            () -> new WarframePanelBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_NOSE_CONE = registerBlock("airframe_nose_cone",
            () -> new WarframeNoseConeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_STEPPED_PYRAMID = registerBlock("airframe_stepped_pyramid",
            () -> new WarframeSteppedPyramidBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_BOARD = registerBlock("airframe_board",
            () -> new WarframeBoardBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_HALF_LAYER = registerBlock("airframe_half_layer",
            () -> new WarframeHalfLayerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_HALF_PANEL = registerBlock("airframe_half_panel",
            () -> new WarframeHalfPanelBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_SIDE_STRIP_LAYER = registerBlock("airframe_side_strip_layer",
            () -> new WarframeSideStripLayerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_STEPPED_SLAB_WING = registerBlock("airframe_stepped_slab_wing",
            () -> new WarframeSteppedSlabWingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.ANVIL).noOcclusion().strength(1.0f)));
    public static final RegistryObject<Block> AIRFRAME_STEPPED_SLAB_STABILIZER = registerBlock("airframe_stepped_slab_stabilizer",
            () -> new WarframeSteppedSlabStabilizerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.ANVIL).noOcclusion().strength(1.0f)));
    public static final RegistryObject<Block> AIRFRAME_VERTICAL_STEPPED_SLAB_STABILIZER = registerBlock("airframe_vertical_stepped_slab_stabilizer",
            () -> new WarframeVerticalSteppedSlabStabilizerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.ANVIL).noOcclusion().strength(1.0f)));
    public static final RegistryObject<Block> AIRFRAME_HALF_DOOR = registerBlock("airframe_half_door",
            () -> new WarframeHalfDoorBlock(WARFRAME_IRON_SOUND, BlockBehaviour.Properties.copy(Blocks.OAK_DOOR).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_COUNTERMEASURE_LAUNCHER = registerBlock("airframe_countermeasure_launcher",
            () -> new WarframeCountermeasureLauncherBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.ANVIL).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_SLAB_LANDING_GEAR_BAY = registerBlock("airframe_slab_landing_gear_bay",
            () -> new WarframeSlabLandingGearBayBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.ANVIL).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_LANDING_WHEEL = BLOCKS.register("airframe_landing_wheel",
            () -> new WarframeLandingWheelBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f).noLootTable()));
    public static final RegistryObject<Block> AIRFRAME_SLIM_LANDING_GEAR_BAY = registerBlock("airframe_slim_landing_gear_bay",
            () -> new WarframeSlimLandingGearBayBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.ANVIL).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_LANDING_GEAR_BAY = registerBlock("airframe_landing_gear_bay",
            () -> new WarframeFullLandingGearBayBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.ANVIL).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_LANDING_STRUT = BLOCKS.register("airframe_landing_strut",
            () -> new WarframeLandingStrutBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f).noCollission().noLootTable()));
    public static final RegistryObject<Block> AIRFRAME_COUNTERMEASURE_LAUNCHER_SLAB = registerBlock("airframe_countermeasure_launcher_slab",
            () -> new WarframeCountermeasureLauncherSlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.ANVIL).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_RAM_SCOOP_RADIATOR = registerBlock("airframe_ram_scoop_radiator",
            () -> new WarframeScoopRadiatorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_RAM_SCOOP_RADIATOR_SLAB = registerBlock("airframe_ram_scoop_radiator_slab",
            () -> new WarframeHalfScoopRadiatorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_COVERED_AUTOCANNON_BARREL = registerBlock("airframe_covered_autocannon_barrel",
            () -> new WarframeCoveredAutocannonBarrelBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_COVERED_ROTARY_AUTOCANNON_BARREL = registerBlock("airframe_covered_rotary_autocannon_barrel",
            () -> new WarframeCoveredAutocannonBarrelBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_AIR_INTAKE = registerBlock("airframe_air_intake",
            () -> new WarframeAirIntakeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f)));
    public static final RegistryObject<Block> AIRFRAME_COMPRESSOR_INTAKE_COWL = registerBlock("airframe_compressor_intake_cowl",
            () -> new WarframeCompressorIntakeCowlBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(4.0f)));



    public static final RegistryObject<Block> HEAVYFRAME_BLOCK = registerBlock("heavyframe_block",
            () -> new WarframeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(15.0f)));
    public static final RegistryObject<Block> HEAVYFRAME_SLAB = registerBlock("heavyframe_slab",
            () -> new WarframeSlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(15.0f)));
    public static final RegistryObject<Block> HEAVYFRAME_HALF_SLAB = registerBlock("heavyframe_half_slab",
            () -> new WarframeHalfSlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(15.0f)));
    public static final RegistryObject<Block> HEAVYFRAME_QUARTER_SLAB = registerBlock("heavyframe_quarter_slab",
            () -> new WarframeQuarterSlabBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(15.0f)));
    public static final RegistryObject<Block> HEAVYFRAME_STAIRS = registerBlock("heavyframe_stairs",
            () -> new WarframeStairsBlock(() -> HEAVYFRAME_BLOCK.get().defaultBlockState(),BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(15.0f)));
    public static final RegistryObject<Block> HEAVYFRAME_HALF_STAIRS = registerBlock("heavyframe_half_stairs",
            () -> new WarframeHalfStairBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(15.0f)));
    public static final RegistryObject<Block> HEAVYFRAME_PANEL = registerBlock("heavyframe_panel",
            () -> new WarframePanelBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(15.0f)));
    public static final RegistryObject<Block> HEAVYFRAME_HALF_PANEL = registerBlock("heavyframe_half_panel",
            () -> new WarframeHalfPanelBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(15.0f)));
    public static final RegistryObject<Block> HEAVYFRAME_BOARD = registerBlock("heavyframe_board",
            () -> new WarframeBoardBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(15.0f)));
    public static final RegistryObject<Block> HEAVYFRAME_CENTERED_STAIRS = registerBlock("heavyframe_centered_stairs",
            () -> new WarframeCenteredStairsBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(15.0f)));
    public static final RegistryObject<Block> HEAVYFRAME_STEPPED_PYRAMID = registerBlock("heavyframe_stepped_pyramid",
            () -> new WarframeSteppedPyramidBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(15.0f)));
    public static final RegistryObject<Block> HEAVYFRAME_NOSE_CONE = registerBlock("heavyframe_nose_cone",
            () -> new WarframeNoseConeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(15.0f)));
    public static final RegistryObject<Block> HEAVYFRAME_LAYER = registerBlock("heavyframe_layer",
            () -> new WarframeLayerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(15.0f)));
    public static final RegistryObject<Block> HEAVYFRAME_CENTERED_LAYER = registerBlock("heavyframe_centered_layer",
            () -> new WarframeCenteredLayerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(15.0f)));
    public static final RegistryObject<Block> HEAVYFRAME_HALF_LAYER = registerBlock("heavyframe_half_layer",
            () -> new WarframeHalfLayerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(15.0f)));
    public static final RegistryObject<Block> HEAVYFRAME_EDGE_STRIP_LAYER = registerBlock("heavyframe_edge_strip_layer",
            () -> new WarframeEdgeStripLayerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(15.0f)));
    public static final RegistryObject<Block> HEAVYFRAME_SIDE_STRIP_LAYER = registerBlock("heavyframe_side_strip_layer",
            () -> new WarframeSideStripLayerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(15.0f)));
    public static final RegistryObject<Block> HEAVYFRAME_CORE_STRIP_LAYER = registerBlock("heavyframe_core_strip_layer",
            () -> new WarframeCoreStripLayerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(15.0f)));
    public static final RegistryObject<Block> HEAVYFRAME_POINT_LAYER = registerBlock("heavyframe_point_layer",
            () -> new WarframePointLayerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(15.0f)));
    public static final RegistryObject<Block> HEAVYFRAME_TRAPDOOR = registerBlock("heavyframe_trapdoor",
            () -> new WarframeTrapdoorBlock(WARFRAME_IRON_SOUND,BlockBehaviour.Properties.copy(Blocks.OAK_TRAPDOOR).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(15.0f)));
    public static final RegistryObject<Block> HEAVYFRAME_DOOR = registerBlock("heavyframe_door",
            () -> new WarframeDoorBlock(WARFRAME_IRON_SOUND,BlockBehaviour.Properties.copy(Blocks.OAK_DOOR).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(15.0f)));
    public static final RegistryObject<Block> HEAVYFRAME_HALF_DOOR = registerBlock("heavyframe_half_door",
            () -> new WarframeHalfDoorBlock(WARFRAME_IRON_SOUND, BlockBehaviour.Properties.copy(Blocks.OAK_DOOR).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(15.0f)));
    public static final RegistryObject<Block> HEAVYFRAME_LADDER = registerBlock("heavyframe_ladder",
            () -> new WarframeLadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(15.0f)));
    public static final RegistryObject<Block> HEAVYFRAME_BUTTON = registerBlock("heavyframe_button",
            () -> new WarframeButtonBlock(BlockBehaviour.Properties.copy(Blocks.STONE_BUTTON).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(15.0f),WARFRAME_IRON_SOUND, 20, false));
    public static final RegistryObject<Block> HEAVYFRAME_LEVER = registerBlock("heavyframe_lever",
            () -> new WarframeLeverBlock(BlockBehaviour.Properties.copy(Blocks.LEVER).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(15.0f)));
    public static final RegistryObject<Block> HEAVYFRAME_CHEST = registerBlock("heavyframe_chest",
            () -> new WarframeChestBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(15.0f)));
    public static final RegistryObject<Block> HEAVYFRAME_COVERED_AUTOCANNON_BARREL = registerBlock("heavyframe_covered_autocannon_barrel",
            () -> new WarframeCoveredAutocannonBarrelBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(15.0f)));
    public static final RegistryObject<Block> HEAVYFRAME_COVERED_ROTARY_AUTOCANNON_BARREL = registerBlock("heavyframe_covered_rotary_autocannon_barrel",
            () -> new WarframeCoveredAutocannonBarrelBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.NETHERITE_BLOCK).noOcclusion().strength(15.0f)));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends  Block>RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}