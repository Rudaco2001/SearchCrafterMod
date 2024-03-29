package com.rudaco.searchcrafter.block;

import com.rudaco.searchcrafter.SearchCrafter;
import com.rudaco.searchcrafter.block.custom.SearchCrafterTableBlock;
import com.rudaco.searchcrafter.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
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

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SearchCrafter.MOD_ID);


    public static final RegistryObject<Block> SUNANIUM_BLOCK = registerBlock("sunanium_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.SAND).strength(3f)), CreativeModeTab.TAB_MISC);

    public static final RegistryObject<Block> SEARCH_CRAFTER_BLOCK = registerBlock("search_crafter_block", () -> new SearchCrafterTableBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_WOOD).noOcclusion()), CreativeModeTab.TAB_MISC);

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, CreativeModeTab tab){
        RegistryObject<T> object = BLOCKS.register(name, block);
        registerBlockItem(name, object, tab);
        return object;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block, CreativeModeTab tab){

        return ModItems.ITEMS.register(name, ()-> new BlockItem(block.get(), new Item.Properties().tab(tab)));
    }



    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
