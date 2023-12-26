package com.rudaco.searchcrafter.item;

import com.rudaco.searchcrafter.SearchCrafter;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SearchCrafter.MOD_ID);


    private static final RegistryObject<Item> SUNANIUM = ITEMS.register("sunanium", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));




    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }



}
