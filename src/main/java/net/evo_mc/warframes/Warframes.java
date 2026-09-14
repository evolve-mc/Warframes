package net.evo_mc.warframes;

import com.mojang.logging.LogUtils;
import net.evo_mc.warframes.block.ModBlocks;
import net.evo_mc.warframes.block.custom.WarframeMimicable;
import net.evo_mc.warframes.block.entity.ModBlockEntities;
import net.evo_mc.warframes.block.entity.WarframeMimicBlockEntity;
import net.evo_mc.warframes.block.entity.renderer.WarframeMimicBakedModel;
import net.evo_mc.warframes.item.ModCreativeModeTabs;
import net.evo_mc.warframes.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

@Mod(Warframes.MOD_ID)
public class Warframes {
    public static final String MOD_ID = "warframes";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Warframes(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        ModCreativeModeTabs.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.WRENCH);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.MIMIC_TEST_BLOCK.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_BLOCK.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_SLAB.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_SLAB_WING.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_LAYER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_STAIRS.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_HALF_SLAB.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_QUARTER_SLAB.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_CENTERED_LAYER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_EDGE_SLICE.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_CENTERED_SLICE.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_SLAB_STABILIZER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_VERTICAL_SLAB_STABILIZER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_HALF_SLAB_WING.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_HALF_SLAB_STABILIZER.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.AIRFRAME_VERTICAL_HALF_SLAB_STABILIZER.get(), RenderType.cutout());
            });
        }

        @SubscribeEvent
        public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
            var models = event.getModels();

            models.replaceAll((location, originalModel) -> {
                if (!location.getNamespace().equals(Warframes.MOD_ID)) {
                    return originalModel;
                }
                var block = ForgeRegistries.BLOCKS.getValue(new net.minecraft.resources.ResourceLocation(Warframes.MOD_ID, location.getPath()));
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
                    ModBlocks.MIMIC_TEST_BLOCK.get(),
                    ModBlocks.AIRFRAME_BLOCK.get(),
                    ModBlocks.AIRFRAME_SLAB.get(),
                    ModBlocks.AIRFRAME_SLAB_WING.get(),
                    ModBlocks.AIRFRAME_LAYER.get(),
                    ModBlocks.AIRFRAME_STAIRS.get(),
                    ModBlocks.AIRFRAME_HALF_SLAB.get(),
                    ModBlocks.AIRFRAME_QUARTER_SLAB.get(),
                    ModBlocks.AIRFRAME_CENTERED_LAYER.get(),
                    ModBlocks.AIRFRAME_EDGE_SLICE.get(),
                    ModBlocks.AIRFRAME_CENTERED_SLICE.get(),
                    ModBlocks.AIRFRAME_SLAB_STABILIZER.get(),
                    ModBlocks.AIRFRAME_VERTICAL_SLAB_STABILIZER.get(),
                    ModBlocks.AIRFRAME_HALF_SLAB_WING.get(),
                    ModBlocks.AIRFRAME_HALF_SLAB_STABILIZER.get(),
                    ModBlocks.AIRFRAME_VERTICAL_HALF_SLAB_STABILIZER.get());
        }
    }
}