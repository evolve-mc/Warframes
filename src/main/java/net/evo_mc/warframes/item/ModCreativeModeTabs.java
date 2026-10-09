package net.evo_mc.warframes.item;

import net.evo_mc.warframes.Warframes;
import net.evo_mc.warframes.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;


public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Warframes.MOD_ID);

    public static final RegistryObject<CreativeModeTab> WARFRAMES_TAB = CREATIVE_MODE_TABS.register("warframes_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.WARFRAMES_ICON.get()))
                    .title(Component.translatable("creativetab.warframes_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.WRENCH.get());

                        //Standard Blocks
                        pOutput.accept(ModBlocks.AIRFRAME_BLOCK.get());
                        pOutput.accept(ModBlocks.AIRFRAME_STAIRS.get());
                        pOutput.accept(ModBlocks.AIRFRAME_CENTERED_STAIRS.get());
                        pOutput.accept(ModBlocks.AIRFRAME_HALF_STAIRS.get());
                        pOutput.accept(ModBlocks.AIRFRAME_SLAB.get());
                        pOutput.accept(ModBlocks.AIRFRAME_HALF_SLAB.get());
                        pOutput.accept(ModBlocks.AIRFRAME_QUARTER_SLAB.get());
                        pOutput.accept(ModBlocks.AIRFRAME_PANEL.get());
                        pOutput.accept(ModBlocks.AIRFRAME_HALF_PANEL.get());
                        pOutput.accept(ModBlocks.AIRFRAME_NOSE_CONE.get());
                        pOutput.accept(ModBlocks.AIRFRAME_STEPPED_PYRAMID.get());
                        pOutput.accept(ModBlocks.AIRFRAME_BOARD.get());
                        pOutput.accept(ModBlocks.AIRFRAME_AIR_INTAKE.get());
                        pOutput.accept(ModBlocks.AIRFRAME_COMPRESSOR_INTAKE_COWL.get());


                        //Layer Blocks
                        pOutput.accept(ModBlocks.AIRFRAME_LAYER.get());
                        pOutput.accept(ModBlocks.AIRFRAME_CENTERED_LAYER.get());
                        pOutput.accept(ModBlocks.AIRFRAME_HALF_LAYER.get());
                        pOutput.accept(ModBlocks.AIRFRAME_SIDE_STRIP_LAYER.get());
                        pOutput.accept(ModBlocks.AIRFRAME_EDGE_STRIP_LAYER.get());
                        pOutput.accept(ModBlocks.AIRFRAME_CORE_STRIP_LAYER.get());
                        pOutput.accept(ModBlocks.AIRFRAME_POINT_LAYER.get());


                        //Functional Blocks
                        pOutput.accept(ModBlocks.AIRFRAME_DOOR.get());
                        pOutput.accept(ModBlocks.AIRFRAME_HALF_DOOR.get());
                        pOutput.accept(ModBlocks.AIRFRAME_TRAPDOOR.get());
                        pOutput.accept(ModBlocks.AIRFRAME_LADDER.get());
                        pOutput.accept(ModBlocks.AIRFRAME_BUTTON.get());
                        pOutput.accept(ModBlocks.AIRFRAME_LEVER.get());
                        pOutput.accept(ModBlocks.AIRFRAME_CHEST.get());


                        //Airfoil Blocks
                        pOutput.accept(ModBlocks.AIRFRAME_SLAB_WING.get());
                        pOutput.accept(ModBlocks.AIRFRAME_STEPPED_SLAB_WING.get());
                        pOutput.accept(ModBlocks.AIRFRAME_HALF_SLAB_WING.get());
                        pOutput.accept(ModBlocks.AIRFRAME_SLAB_WING_LAYER.get());

                        pOutput.accept(ModBlocks.AIRFRAME_SLAB_STABILIZER.get());
                        pOutput.accept(ModBlocks.AIRFRAME_STEPPED_SLAB_STABILIZER.get());
                        pOutput.accept(ModBlocks.AIRFRAME_HALF_SLAB_STABILIZER.get());
                        pOutput.accept(ModBlocks.AIRFRAME_SLAB_STABILIZER_LAYER.get());

                        pOutput.accept(ModBlocks.AIRFRAME_VERTICAL_SLAB_STABILIZER.get());
                        pOutput.accept(ModBlocks.AIRFRAME_VERTICAL_STEPPED_SLAB_STABILIZER.get());
                        pOutput.accept(ModBlocks.AIRFRAME_VERTICAL_HALF_SLAB_STABILIZER.get());
                        pOutput.accept(ModBlocks.AIRFRAME_VERTICAL_SLAB_STABILIZER_LAYER.get());

                        //Control Surface Blocks
                        pOutput.accept(ModBlocks.AIRFRAME_CONTROL_SURFACE.get());
                        pOutput.accept(ModBlocks.AIRFRAME_STEPPED_CONTROL_SURFACE.get());
                        pOutput.accept(ModBlocks.AIRFRAME_HALF_CONTROL_SURFACE.get());
                        pOutput.accept(ModBlocks.AIRFRAME_HORIZONTAL_AIR_BRAKE.get());

                        pOutput.accept(ModBlocks.AIRFRAME_VERTICAL_CONTROL_SURFACE.get());
                        pOutput.accept(ModBlocks.AIRFRAME_VERTICAL_STEPPED_CONTROL_SURFACE.get());
                        pOutput.accept(ModBlocks.AIRFRAME_VERTICAL_HALF_CONTROL_SURFACE.get());
                        pOutput.accept(ModBlocks.AIRFRAME_VERTICAL_AIR_BRAKE.get());

                        //Functional Warium Blocks
                        pOutput.accept(ModBlocks.AIRFRAME_COUNTERMEASURE_LAUNCHER.get());
                        pOutput.accept(ModBlocks.AIRFRAME_COUNTERMEASURE_LAUNCHER_SLAB.get());
                        pOutput.accept(ModBlocks.AIRFRAME_LANDING_GEAR_BAY.get());
                        pOutput.accept(ModBlocks.AIRFRAME_SLAB_LANDING_GEAR_BAY.get());
                        pOutput.accept(ModBlocks.AIRFRAME_SLIM_LANDING_GEAR_BAY.get());
                        pOutput.accept(ModBlocks.AIRFRAME_RAM_SCOOP_RADIATOR.get());
                        pOutput.accept(ModBlocks.AIRFRAME_RAM_SCOOP_RADIATOR_SLAB.get());
                        pOutput.accept(ModBlocks.AIRFRAME_COVERED_AUTOCANNON_BARREL.get());
                        pOutput.accept(ModBlocks.AIRFRAME_COVERED_ROTARY_AUTOCANNON_BARREL.get());

                        //Heavyframe Standard Blocks
                        pOutput.accept(ModBlocks.HEAVYFRAME_BLOCK.get());
                        pOutput.accept(ModBlocks.HEAVYFRAME_STAIRS.get());
                        pOutput.accept(ModBlocks.HEAVYFRAME_CENTERED_STAIRS.get());
                        pOutput.accept(ModBlocks.HEAVYFRAME_HALF_STAIRS.get());
                        pOutput.accept(ModBlocks.HEAVYFRAME_SLAB.get());
                        pOutput.accept(ModBlocks.HEAVYFRAME_HALF_SLAB.get());
                        pOutput.accept(ModBlocks.HEAVYFRAME_QUARTER_SLAB.get());
                        pOutput.accept(ModBlocks.HEAVYFRAME_PANEL.get());
                        pOutput.accept(ModBlocks.HEAVYFRAME_HALF_PANEL.get());
                        pOutput.accept(ModBlocks.HEAVYFRAME_NOSE_CONE.get());
                        pOutput.accept(ModBlocks.HEAVYFRAME_STEPPED_PYRAMID.get());
                        pOutput.accept(ModBlocks.HEAVYFRAME_BOARD.get());

                        //Heavyframe Layer Blocks
                        pOutput.accept(ModBlocks.HEAVYFRAME_LAYER.get());
                        pOutput.accept(ModBlocks.HEAVYFRAME_CENTERED_LAYER.get());
                        pOutput.accept(ModBlocks.HEAVYFRAME_HALF_LAYER.get());
                        pOutput.accept(ModBlocks.HEAVYFRAME_SIDE_STRIP_LAYER.get());
                        pOutput.accept(ModBlocks.HEAVYFRAME_EDGE_STRIP_LAYER.get());
                        pOutput.accept(ModBlocks.HEAVYFRAME_CORE_STRIP_LAYER.get());
                        pOutput.accept(ModBlocks.HEAVYFRAME_POINT_LAYER.get());

                        //Heavyframe Functional Blocks
                        pOutput.accept(ModBlocks.HEAVYFRAME_DOOR.get());
                        pOutput.accept(ModBlocks.HEAVYFRAME_HALF_DOOR.get());
                        pOutput.accept(ModBlocks.HEAVYFRAME_TRAPDOOR.get());
                        pOutput.accept(ModBlocks.HEAVYFRAME_LADDER.get());
                        pOutput.accept(ModBlocks.HEAVYFRAME_BUTTON.get());
                        pOutput.accept(ModBlocks.HEAVYFRAME_LEVER.get());
                        pOutput.accept(ModBlocks.HEAVYFRAME_CHEST.get());

                        //Heavyframe Functional Warium Blocks
                        pOutput.accept(ModBlocks.HEAVYFRAME_COVERED_AUTOCANNON_BARREL.get());
                        pOutput.accept(ModBlocks.HEAVYFRAME_COVERED_ROTARY_AUTOCANNON_BARREL.get());



                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}