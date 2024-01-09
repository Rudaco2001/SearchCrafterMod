package com.rudaco.searchcrafter.screen;

import com.rudaco.searchcrafter.SearchCrafter;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {


    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, SearchCrafter.MOD_ID);
    public static final RegistryObject<MenuType<SearchCrafterTableMenu>> SEARCH_CRAFTER_TABLE_MENU =  MENUS.register("search_crafter_table_menu", () -> IForgeMenuType.create(SearchCrafterTableMenu::new));



    public static void register(IEventBus eventBus){
        ModMenuTypes.MENUS.register(eventBus);
    }

    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory){
        return ModMenuTypes.MENUS.register(name, ()-> IForgeMenuType.create(factory));
    }

}
