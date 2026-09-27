package net.evo_mc.warframes.block;

import net.evo_mc.warframes.Warframes;
import net.evo_mc.warframes.block.custom.*;
import net.evo_mc.warframes.block.entity.ModBlockEntities;
import net.evo_mc.warframes.item.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
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
            SoundType.METAL,
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
            () -> new WarframeBlock(BlockBehaviour.Properties.copy(Blocks.BASALT).sound(SoundType.NETHERITE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_SLAB = registerBlock("airframe_slab",
            () -> new WarframeSlabBlock(BlockBehaviour.Properties.copy(Blocks.BASALT).sound(SoundType.NETHERITE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_SLAB_WING = registerBlock("airframe_slab_wing",
            () -> new WarframeSlabWingBlock(BlockBehaviour.Properties.copy(Blocks.BASALT).sound(SoundType.NETHERITE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_LAYER = registerBlock("airframe_layer",
            () -> new WarframeLayerBlock(BlockBehaviour.Properties.copy(Blocks.BASALT).sound(SoundType.NETHERITE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_STAIRS = registerBlock("airframe_stairs",
            () -> new WarframeStairsBlock(() -> AIRFRAME_BLOCK.get().defaultBlockState(),BlockBehaviour.Properties.copy(Blocks.BASALT).sound(SoundType.NETHERITE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_HALF_SLAB = registerBlock("airframe_half_slab",
            () -> new WarframeHalfSlabBlock(BlockBehaviour.Properties.copy(Blocks.BASALT).sound(SoundType.NETHERITE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_QUARTER_SLAB = registerBlock("airframe_quarter_slab",
            () -> new WarframeQuarterSlabBlock(BlockBehaviour.Properties.copy(Blocks.BASALT).sound(SoundType.NETHERITE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_CENTERED_LAYER = registerBlock("airframe_centered_layer",
            () -> new WarframeCenteredLayerBlock(BlockBehaviour.Properties.copy(Blocks.BASALT).sound(SoundType.NETHERITE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_EDGE_SLICE = registerBlock("airframe_edge_slice",
            () -> new WarframeEdgeSliceBlock(BlockBehaviour.Properties.copy(Blocks.BASALT).sound(SoundType.NETHERITE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_CENTERED_SLICE = registerBlock("airframe_centered_slice",
            () -> new WarframeCenteredSliceBlock(BlockBehaviour.Properties.copy(Blocks.BASALT).sound(SoundType.NETHERITE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_SLAB_STABILIZER = registerBlock("airframe_slab_stabilizer",
            () -> new WarframeSlabStabilizerBlock(BlockBehaviour.Properties.copy(Blocks.BASALT).sound(SoundType.NETHERITE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_VERTICAL_SLAB_STABILIZER = registerBlock("airframe_vertical_slab_stabilizer",
            () -> new WarframeVerticalSlabStabilizerBlock(BlockBehaviour.Properties.copy(Blocks.BASALT).sound(SoundType.NETHERITE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_HALF_SLAB_WING = registerBlock("airframe_half_slab_wing",
            () -> new WarframeHalfSlabWingBlock(BlockBehaviour.Properties.copy(Blocks.BASALT).sound(SoundType.NETHERITE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_HALF_SLAB_STABILIZER = registerBlock("airframe_half_slab_stabilizer",
            () -> new WarframeHalfSlabStabilizerBlock(BlockBehaviour.Properties.copy(Blocks.BASALT).sound(SoundType.NETHERITE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_VERTICAL_HALF_SLAB_STABILIZER = registerBlock("airframe_vertical_half_slab_stabilizer",
            () -> new WarframeVerticalHalfSlabStabilizerBlock(BlockBehaviour.Properties.copy(Blocks.BASALT).sound(SoundType.NETHERITE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_CENTERED_STAIRS = registerBlock("airframe_centered_stairs",
            () -> new WarframeCenteredStairsBlock(BlockBehaviour.Properties.copy(Blocks.BASALT).sound(SoundType.NETHERITE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_POINT = registerBlock("airframe_point",
            () -> new WarframePointBlock(BlockBehaviour.Properties.copy(Blocks.BASALT).sound(SoundType.NETHERITE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_HALF_STAIRS = registerBlock("airframe_half_stairs",
            () -> new WarframeHalfStairBlock(BlockBehaviour.Properties.copy(Blocks.BASALT).sound(SoundType.NETHERITE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_DOOR = registerBlock("airframe_door",
            () -> new WarframeDoorBlock(WARFRAME_IRON_SOUND,BlockBehaviour.Properties.copy(Blocks.OAK_DOOR).sound(SoundType.NETHERITE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_TRAPDOOR = registerBlock("airframe_trapdoor",
            () -> new WarframeTrapdoorBlock(WARFRAME_IRON_SOUND,BlockBehaviour.Properties.copy(Blocks.OAK_TRAPDOOR).sound(SoundType.NETHERITE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_LADDER = registerBlock("airframe_ladder",
            () -> new WarframeLadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER).sound(SoundType.NETHERITE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_BUTTON = registerBlock("airframe_button",
            () -> new WarframeButtonBlock(BlockBehaviour.Properties.copy(Blocks.STONE_BUTTON).sound(SoundType.NETHERITE_BLOCK).noOcclusion(),BlockSetType.STONE, 20, false));
    public static final RegistryObject<Block> AIRFRAME_LEVER = registerBlock("airframe_lever",
            () -> new WarframeLeverBlock(BlockBehaviour.Properties.copy(Blocks.LEVER).sound(SoundType.NETHERITE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_CHEST = registerBlock("airframe_chest",
            () -> new WarframeChestBlock(BlockBehaviour.Properties.copy(Blocks.CHEST).sound(SoundType.NETHERITE_BLOCK).noOcclusion(),ModBlockEntities.WARFRAME_CHEST_BLOCK_ENTITY::get));
    public static final RegistryObject<Block> AIRFRAME_CONTROL_SURFACE = registerBlock("airframe_control_surface",
            () -> new WarframeControlSurfaceBlock(BlockBehaviour.Properties.copy(Blocks.ANVIL).sound(SoundType.NETHERITE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_VERTICAL_CONTROL_SURFACE = registerBlock("airframe_vertical_control_surface",
            () -> new WarframeVerticalControlSurfaceBlock(BlockBehaviour.Properties.copy(Blocks.ANVIL).sound(SoundType.NETHERITE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_SLAB_STABILIZER_LAYER = registerBlock("airframe_slab_stabilizer_layer",
            () -> new WarframeSlabStabilizerLayerBlock(BlockBehaviour.Properties.copy(Blocks.BASALT).sound(SoundType.NETHERITE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_SLAB_WING_LAYER = registerBlock("airframe_slab_wing_layer",
            () -> new WarframeSlabWingLayerBlock(BlockBehaviour.Properties.copy(Blocks.BASALT).sound(SoundType.NETHERITE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_VERTICAL_SLAB_STABILIZER_LAYER = registerBlock("airframe_vertical_slab_stabilizer_layer",
            () -> new WarframeVerticalSlabStabilizerLayerBlock(BlockBehaviour.Properties.copy(Blocks.BASALT).sound(SoundType.NETHERITE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_STEPPED_CONTROL_SURFACE = registerBlock("airframe_stepped_control_surface",
            () -> new WarframeSteppedControlSurfaceBlock(BlockBehaviour.Properties.copy(Blocks.ANVIL).sound(SoundType.NETHERITE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_HALF_CONTROL_SURFACE = registerBlock("airframe_half_control_surface",
            () -> new WarframeHalfControlSurfaceBlock(BlockBehaviour.Properties.copy(Blocks.ANVIL).sound(SoundType.NETHERITE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_STEPPED_VERTICAL_CONTROL_SURFACE = registerBlock("airframe_stepped_vertical_control_surface",
            () -> new WarframeSteppedVerticalControlSurfaceBlock(BlockBehaviour.Properties.copy(Blocks.ANVIL).sound(SoundType.NETHERITE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_VERTICAL_HALF_CONTROL_SURFACE = registerBlock("airframe_vertical_half_control_surface",
            () -> new WarframeVerticalHalfControlSurfaceBlock(BlockBehaviour.Properties.copy(Blocks.ANVIL).sound(SoundType.NETHERITE_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_AIR_BRAKE = registerBlock("airframe_air_brake",
            () -> new WarframeAirBrakeBlock(BlockBehaviour.Properties.copy(Blocks.ANVIL).sound(SoundType.NETHERITE_BLOCK).noOcclusion()));


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
