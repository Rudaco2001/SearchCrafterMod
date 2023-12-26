package com.rudaco.searchcrafter.block.entity;

import com.rudaco.searchcrafter.SearchCrafter;
import com.rudaco.searchcrafter.block.ModBlocks;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.rmi.registry.Registry;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SearchCrafter.MOD_ID);

    public static final RegistryObject<BlockEntityType<SearchCrafterTable>> SEARCH_CRAFTING_TABLE = BLOCK_ENTITIES.register("search_crafting_table", ()-> BlockEntityType.Builder.of(SearchCrafterTable::new, ModBlocks.SEARCH_CRAFTER_BLOCK.get()).build(null));
    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }

}
