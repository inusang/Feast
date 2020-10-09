package org.watp.umc.feast;

import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.watp.umc.feast.inventory.DairyMachineContainer;
import org.watp.umc.feast.inventory.OvenContainer;
import org.watp.umc.feast.network.NetWorking;
import org.watp.umc.feast.proxy.ClientProxy;
import org.watp.umc.feast.proxy.IProxy;
import org.watp.umc.feast.proxy.ServerProxy;
import org.watp.umc.feast.registry.BlockRegistry;
import org.watp.umc.feast.registry.ContainerTypeRegistry;
import org.watp.umc.feast.registry.ItemRegistry;
import org.watp.umc.feast.registry.TileEntityRegistry;
import org.watp.umc.feast.tileentity.DairyMachineTileEntity;
import org.watp.umc.feast.tileentity.OvenTileEntity;

@Mod(value=Feast.MODID)
public class Feast
{
    public static final Logger LOGGER = LogManager.getLogger();
    
    public static final String MODID="feast";

    public static IProxy proxy=(IProxy) DistExecutor.safeRunForDist(()->ClientProxy::new, ()->ServerProxy::new);
    
    public Feast() {
    	ItemRegistry.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    	BlockRegistry.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
    	ContainerTypeRegistry.containers.register(FMLJavaModLoadingContext.get().getModEventBus());
    	TileEntityRegistry.TileEntities.register(FMLJavaModLoadingContext.get().getModEventBus());
    	
    	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    	/*
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
		*/
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
    	NetWorking.registerMessage();
    	//proxy.init();
    }
    
    /*
    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("examplemod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }*/
    
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
            
        }
    }
    
	public static class Items {
	   public static final Item OVEN=null;
	   public static final Item DAIRY_MACHINE=null;

	    public static final Item WHEATMEAL=null;
	    public static final Item SALT=null;

	    public static final Item CREAM=null;
	    public static final Item BUTTER=null;
	    public static final Item CHEESE=null;
		public static final Item APPLE_PIE=null;
		public static final Item POTATO_BEEF_PIE=null;
		public static final Item PIZZA_PART=null;
		public static final Item COD_BAR=null;
		public static final Item SALMON_ROLL=null;
	}

	@ObjectHolder(Feast.MODID)
	public static class Blocks {
		public static final Block OVEN=null;
		public static final Block DAIRY_MACHINE=null;
	}

	@ObjectHolder(Feast.MODID)
	public static class ContainerTypes {
		public static final ContainerType<OvenContainer> OVEN=null;
		public static final ContainerType<DairyMachineContainer> DAIRY_MACHINE=null;
	}

	@ObjectHolder(Feast.MODID)
	public static class TileEntities {
		public static final TileEntityType<OvenTileEntity> OVEN=null;
		public static final TileEntityType<DairyMachineTileEntity> DAIRY_MACHINE=null;
	}
}
