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
                            ModBlocks.AIRFRAME_VERTICAL_HALF_SLAB_STABILIZER.get()
                    ).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}