package net.evo_mc.warframes;


import net.evo_mc.warframes.block.ModBlocks;
import net.evo_mc.warframes.block.custom.WarframeBoardBlock;
import net.evo_mc.warframes.block.custom.WarframeHalfLayerBlock;
import net.evo_mc.warframes.block.custom.WarframeHalfPanelBlock;
import net.evo_mc.warframes.block.custom.WarframeMimicable;
import net.evo_mc.warframes.block.entity.ModBlockEntities;
import net.evo_mc.warframes.block.entity.WarframeMimicBlockEntity;
import net.evo_mc.warframes.block.entity.WarframeMultiMimicBlockEntity;
import net.evo_mc.warframes.block.entity.renderer.*;
import net.evo_mc.warframes.config.WarframesServerConfig;
import net.evo_mc.warframes.item.ModCreativeModeTabs;
import net.evo_mc.warframes.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(Warframes.MOD_ID)
public class Warframes {
    public static final String MOD_ID = "warframes";


    public Warframes(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        ModCreativeModeTabs.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);



        modEventBus.addListener(this::addCreative);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, WarframesServerConfig.SPEC);
    }


    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.WRENCH);
        }
    }



    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_BLOCK.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_SLAB.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_SLAB_WING.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_LAYER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_STAIRS.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_HALF_SLAB.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_QUARTER_SLAB.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_CENTERED_LAYER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_EDGE_STRIP_LAYER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_CORE_STRIP_LAYER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_SLAB_STABILIZER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_VERTICAL_SLAB_STABILIZER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_HALF_SLAB_WING.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_HALF_SLAB_STABILIZER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_VERTICAL_HALF_SLAB_STABILIZER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_CENTERED_STAIRS.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_POINT_LAYER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_HALF_STAIRS.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_DOOR.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_TRAPDOOR.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_LADDER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_BUTTON.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_LEVER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_SLAB_WING_LAYER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_SLAB_STABILIZER_LAYER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_VERTICAL_SLAB_STABILIZER_LAYER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_PANEL.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_NOSE_CONE.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_STEPPED_PYRAMID.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_BOARD.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_HALF_LAYER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_HALF_PANEL.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_SIDE_STRIP_LAYER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_STEPPED_SLAB_WING.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_STEPPED_SLAB_STABILIZER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_VERTICAL_STEPPED_SLAB_STABILIZER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_HALF_DOOR.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_COUNTERMEASURE_LAUNCHER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_COUNTERMEASURE_LAUNCHER_SLAB.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_LANDING_WHEEL.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_SLAB_LANDING_GEAR_BAY.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_SLIM_LANDING_GEAR_BAY.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_LANDING_GEAR_BAY.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_RAM_SCOOP_RADIATOR.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_RAM_SCOOP_RADIATOR_SLAB.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_COVERED_AUTOCANNON_BARREL.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_COVERED_ROTARY_AUTOCANNON_BARREL.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_AIR_INTAKE.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_COMPRESSOR_INTAKE_COWL.get(), RenderType.cutout());

                ItemBlockRenderTypes.setRenderLayer(ModBlocks.HEAVYFRAME_BLOCK.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.HEAVYFRAME_SLAB.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.HEAVYFRAME_HALF_SLAB.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.HEAVYFRAME_QUARTER_SLAB.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.HEAVYFRAME_STAIRS.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.HEAVYFRAME_HALF_STAIRS.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.HEAVYFRAME_PANEL.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.HEAVYFRAME_HALF_PANEL.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.HEAVYFRAME_BOARD.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.HEAVYFRAME_CENTERED_STAIRS.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.HEAVYFRAME_STEPPED_PYRAMID.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.HEAVYFRAME_NOSE_CONE.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.HEAVYFRAME_LAYER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.HEAVYFRAME_CENTERED_LAYER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.HEAVYFRAME_HALF_LAYER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.HEAVYFRAME_EDGE_STRIP_LAYER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.HEAVYFRAME_SIDE_STRIP_LAYER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.HEAVYFRAME_CORE_STRIP_LAYER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.HEAVYFRAME_POINT_LAYER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.HEAVYFRAME_TRAPDOOR.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.HEAVYFRAME_DOOR.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.HEAVYFRAME_HALF_DOOR.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.HEAVYFRAME_LADDER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.HEAVYFRAME_BUTTON.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.HEAVYFRAME_LEVER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.HEAVYFRAME_COVERED_AUTOCANNON_BARREL.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.HEAVYFRAME_COVERED_ROTARY_AUTOCANNON_BARREL.get(), RenderType.cutout());
            });
        }

        @SubscribeEvent
        public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
            var models = event.getModels();

            models.replaceAll((location, originalModel) -> {
                if (originalModel instanceof WarframeMultiMimicBakedModel || originalModel instanceof WarframeMimicBakedModel) {
                    return originalModel;
                }
                if (!location.getNamespace().equals(Warframes.MOD_ID)) {
                    return originalModel;
                }
                var block = ForgeRegistries.BLOCKS.getValue(new net.minecraft.resources.ResourceLocation(Warframes.MOD_ID, location.getPath()));
                if (block instanceof WarframeBoardBlock) {
                    return new WarframeBoardMimicBakedModel(originalModel);
                }
                if (block instanceof WarframeHalfLayerBlock || block instanceof WarframeHalfPanelBlock) {
                    return new WarframeHalfLayerMimicBakedModel(originalModel);
                }
                if (block instanceof WarframeMimicable) {
                    return new WarframeMimicBakedModel(originalModel);
                }
                return originalModel;
            });
        }

        @SubscribeEvent
        public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {
            event.register((state, level, pos, tintIndex) -> {
                        if (level != null && pos != null && level.getBlockEntity(pos) instanceof WarframeMimicBlockEntity mimicBlockEntity) {
                            BlockState mimicked = mimicBlockEntity.getMimickedBlock();
                            if (mimicked != null) {
                                return Minecraft.getInstance().getBlockColors().getColor(mimicked, level, pos, tintIndex);
                            }
                        }
                        return -1;
                    },
                    ModBlocks.AIRFRAME_BLOCK.get(),
                    ModBlocks.AIRFRAME_SLAB.get(),
                    ModBlocks.AIRFRAME_SLAB_WING.get(),
                    ModBlocks.AIRFRAME_SLAB_WING_LAYER.get(),
                    ModBlocks.AIRFRAME_LAYER.get(),
                    ModBlocks.AIRFRAME_STAIRS.get(),
                    ModBlocks.AIRFRAME_HALF_SLAB.get(),
                    ModBlocks.AIRFRAME_QUARTER_SLAB.get(),
                    ModBlocks.AIRFRAME_CENTERED_LAYER.get(),
                    ModBlocks.AIRFRAME_EDGE_STRIP_LAYER.get(),
                    ModBlocks.AIRFRAME_CORE_STRIP_LAYER.get(),
                    ModBlocks.AIRFRAME_SLAB_STABILIZER.get(),
                    ModBlocks.AIRFRAME_SLAB_STABILIZER_LAYER.get(),
                    ModBlocks.AIRFRAME_VERTICAL_SLAB_STABILIZER.get(),
                    ModBlocks.AIRFRAME_VERTICAL_SLAB_STABILIZER_LAYER.get(),
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
                    ModBlocks.AIRFRAME_PANEL.get(),
                    ModBlocks.AIRFRAME_NOSE_CONE.get(),
                    ModBlocks.AIRFRAME_STEPPED_PYRAMID.get(),
                    ModBlocks.AIRFRAME_SIDE_STRIP_LAYER.get(),
                    ModBlocks.AIRFRAME_STEPPED_SLAB_WING.get(),
                    ModBlocks.AIRFRAME_STEPPED_SLAB_STABILIZER.get(),
                    ModBlocks.AIRFRAME_VERTICAL_STEPPED_SLAB_STABILIZER.get(),
                    ModBlocks.AIRFRAME_HALF_DOOR.get(),
                    ModBlocks.AIRFRAME_COUNTERMEASURE_LAUNCHER.get(),
                    ModBlocks.AIRFRAME_COUNTERMEASURE_LAUNCHER_SLAB.get(),
                    ModBlocks.AIRFRAME_LANDING_WHEEL.get(),
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
                    ModBlocks.HEAVYFRAME_COVERED_ROTARY_AUTOCANNON_BARREL.get());


            event.register((state, level, pos, tintIndex) -> {
                        if (level != null && pos != null && tintIndex >= 0
                                && level.getBlockEntity(pos) instanceof WarframeMultiMimicBlockEntity mimicBlockEntity) {
                            int slot = WarframeMultiMimicBakedModel.tintSlot(tintIndex);
                            if (slot < mimicBlockEntity.getSlotCount()) {
                                BlockState mimicked = mimicBlockEntity.getMimickedBlock(slot);
                                if (mimicked != null) {
                                    return Minecraft.getInstance().getBlockColors().getColor(mimicked, level, pos,
                                            WarframeMultiMimicBakedModel.tintSource(tintIndex));
                                }
                            }
                        }
                        return -1;
                    },
                    ModBlocks.AIRFRAME_BOARD.get(),
                    ModBlocks.AIRFRAME_HALF_LAYER.get(),
                    ModBlocks.AIRFRAME_HALF_PANEL.get(),

                    ModBlocks.HEAVYFRAME_BOARD.get(),
                    ModBlocks.HEAVYFRAME_HALF_LAYER.get(),
                    ModBlocks.HEAVYFRAME_HALF_PANEL.get());
        }



        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(ModBlockEntities.WARFRAME_CHEST.get(), WarframeChestRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.WARFRAME_CONTROL_SURFACE.get(), WarframeControlSurfaceRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.WARFRAME_VERTICAL_CONTROL_SURFACE.get(), WarframeVerticalControlSurfaceRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.WARFRAME_HORIZONTAL_AIR_BRAKE.get(), WarframeHorizontalAirBrakeRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.WARFRAME_VERTICAL_AIR_BRAKE.get(), WarframeVerticalAirBrakeRenderer::new);
        }



    }


}