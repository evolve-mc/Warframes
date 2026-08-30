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
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.WRENCH.get()))
                .title(Component.translatable("creativetab.warframes_tab"))
                .displayItems((pParameters, pOutput) -> {
                    pOutput.accept(ModItems.WRENCH.get());


                    pOutput.accept(ModBlocks.AIRFRAME_BLOCK.get());
                    pOutput.accept(ModBlocks.AIRFRAME_SLAB.get());
                    pOutput.accept(ModBlocks.AIRFRAME_SLAB_WING.get());
                    pOutput.accept(ModBlocks.AIRFRAME_LAYER.get());
                    pOutput.accept(ModBlocks.AIRFRAME_STAIRS.get());
                    pOutput.accept(ModBlocks.AIRFRAME_HALF_SLAB.get());
                    pOutput.accept(ModBlocks.AIRFRAME_QUARTER_SLAB.get());
                    pOutput.accept(ModBlocks.AIRFRAME_CENTERED_LAYER.get());
                    pOutput.accept(ModBlocks.AIRFRAME_EDGE_SLICE.get());
                    pOutput.accept(ModBlocks.AIRFRAME_CENTERED_SLICE.get());
                })
                .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
