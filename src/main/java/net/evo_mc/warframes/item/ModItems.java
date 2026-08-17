package net.evo_mc.warframes.item;

import net.evo_mc.warframes.Warframes;
import net.evo_mc.warframes.item.custom.WrenchItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Warframes.MOD_ID);



public static final RegistryObject<Item> WRENCH = ITEMS.register("wrench",
        () -> new WrenchItem(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
