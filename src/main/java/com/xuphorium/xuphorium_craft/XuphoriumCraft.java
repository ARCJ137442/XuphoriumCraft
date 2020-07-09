/**
  * MCreator注意：
  *
  *如果您锁定基本mod元素文件，则可以编辑此文件和代理文件并且它们不会被覆盖。 如果您更改mod包或modid，则您需要将这些更改手动应用于此文件。
  *如果使用基本mod元素，则不会更改@Mod批注中的设置文件也锁定，因此在这种情况下，您需要在此处手动设置它们。
  *将XuphoriumCraftElements对象和此类的所有调用保留在此类中INTACT，以保留由MCreator生成的mod元素的功能。
  *如果您未在Workspace设置中锁定基本mod元素文件，则此文件将在每个版本上重新生成。
  */
package xuphorium_craft;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.biome.Biome;
import net.minecraft.potion.Potion;
import net.minecraft.item.Item;
import net.minecraft.block.Block;

import java.util.function.Supplier;

@Mod(modid = XuphoriumCraft.MODID, version = XuphoriumCraft.VERSION)
public class XuphoriumCraft
{
	public static final String MODID = "xuphorium_craft";
	public static final String VERSION = "1.0.0";
	public static final SimpleNetworkWrapper PACKET_HANDLER = NetworkRegistry.INSTANCE.newSimpleChannel("xuphorium_craft");
	@SidedProxy(clientSide = "xuphorium_craft.XuphoriumCraftClientProxy", serverSide = "xuphorium_craft.XuphoriumCraftServerProxy")
	public static XuphoriumCraftIProxy proxy;
	@Mod.Instance(MODID)
	public static XuphoriumCraft instance;
	public XuphoriumCraftElements elements = new XuphoriumCraftElements();

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(this);
		GameRegistry.registerWorldGenerator(elements, 5);
		GameRegistry.registerFuelHandler(elements);
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new XuphoriumCraftElements.GuiHandler());
		elements.preInit(event);
		MinecraftForge.EVENT_BUS.register(elements);
		elements.getElements().forEach(element -> element.preInit(event));
		proxy.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event)
	{
		elements.getElements().forEach(element -> element.init(event));
		proxy.init(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.postInit(event);
	}

	@Mod.EventHandler
	public void serverLoad(FMLServerStartingEvent event)
	{
		elements.getElements().forEach(element -> element.serverLoad(event));
		proxy.serverLoad(event);
	}

	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event)
	{
		event.getRegistry().registerAll(elements.getBlocks().stream().map(Supplier::get).toArray(Block[]::new));
	}

	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().registerAll(elements.getItems().stream().map(Supplier::get).toArray(Item[]::new));
	}

	@SubscribeEvent
	public void registerBiomes(RegistryEvent.Register<Biome> event)
	{
		event.getRegistry().registerAll(elements.getBiomes().stream().map(Supplier::get).toArray(Biome[]::new));
	}

	@SubscribeEvent
	public void registerEntities(RegistryEvent.Register<EntityEntry> event)
	{
		event.getRegistry().registerAll(elements.getEntities().stream().map(Supplier::get).toArray(EntityEntry[]::new));
	}

	@SubscribeEvent
	public void registerPotions(RegistryEvent.Register<Potion> event)
	{
		event.getRegistry().registerAll(elements.getPotions().stream().map(Supplier::get).toArray(Potion[]::new));
	}

	@SubscribeEvent
	public void registerSounds(RegistryEvent.Register<net.minecraft.util.SoundEvent> event)
	{
		elements.registerSounds(event);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event)
	{
		elements.getElements().forEach(element -> element.registerModels(event));
	}
	static
	{
		FluidRegistry.enableUniversalBucket();
	}
}
