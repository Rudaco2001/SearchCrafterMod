package com.rudaco.searchcrafter;

import com.mojang.logging.LogUtils;
import com.rudaco.searchcrafter.block.ModBlocks;
import com.rudaco.searchcrafter.block.custom.RendererBlock;
import com.rudaco.searchcrafter.block.entity.ModBlockEntities;
import com.rudaco.searchcrafter.item.ModItems;
import com.rudaco.searchcrafter.network.MySimpleChannel;
import com.rudaco.searchcrafter.screen.ModMenuTypes;
import com.rudaco.searchcrafter.screen.SearchCrafterTableScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SearchCrafter.MOD_ID)
public class SearchCrafter
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "searchcrafter";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();


    // Creates a new BlockItem with the id "examplemod:example_block", combining the namespace and path

    public SearchCrafter()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        ModBlockEntities.register(modEventBus);

        ModMenuTypes.register(modEventBus);

    }


    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        MySimpleChannel.register();

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }



    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
            MenuScreens.register(ModMenuTypes.SEARCH_CRAFTER_TABLE_MENU.get(), SearchCrafterTableScreen::new);
        }
        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event){
            event.registerBlockEntityRenderer(ModBlockEntities.SEARCH_CRAFTING_TABLE.get(), RendererBlock::new);
        }

    }


}
