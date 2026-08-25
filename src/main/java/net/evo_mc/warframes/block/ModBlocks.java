package net.evo_mc.warframes.block;

import net.evo_mc.warframes.Warframes;
import net.evo_mc.warframes.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Warframes.MOD_ID);


    public static final RegistryObject<Block> AIRFRAME_BLOCK = registerBlock("airframe_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.ANVIL).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_SLAB_WING = registerBlock("airframe_slab_wing",
            () -> new WarframeSlabWingBlock(BlockBehaviour.Properties.copy(Blocks.ANVIL).noOcclusion()));
    public static final RegistryObject<Block> AIRFRAME_LAYER = registerBlock("airframe_layer",
            () -> new WarframeLayerBlock(BlockBehaviour.Properties.copy(Blocks.ANVIL).noOcclusion()));

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
