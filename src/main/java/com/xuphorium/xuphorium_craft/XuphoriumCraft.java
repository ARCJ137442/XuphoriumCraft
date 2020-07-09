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
import net.minecraftforge.common.util.EnumHelper;

import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.biome.Biome;
import net.minecraft.potion.Potion;
import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;

import java.util.Set;

import java.util.function.Supplier;

@Mod(modid = XuphoriumCraft.MODID, version = XuphoriumCraft.VERSION)
public class XuphoriumCraft
{
	//============Xuphorium-Craft Global Variables============//
	public static final int[] xArmorHardness=new int[]{4,7,8,5};
	public static final ItemArmor.ArmorMaterial xArmorMaterial=EnumHelper.addArmorMaterial("X","xuphorium_craft:x_armor",25,XuphoriumCraft.xArmorHardness,100,null,4f);
	
	public static final ToolMaterial xSwordMaterial=EnumHelper.addToolMaterial("X_SWORD",3,2048,9f,8f,100);
	public static final ToolMaterial xToolsMaterial=EnumHelper.addToolMaterial("X_TOOLS",6,2048,25.6f,12.8f,64);
	public static final Set<Block> xAxeEffectiveItemsSet=com.google.common.collect.Sets.newHashSet(
							new Block[]{Blocks.PLANKS,Blocks.BOOKSHELF,Blocks.LOG,Blocks.LOG2,Blocks.CHEST,
							Blocks.PUMPKIN,Blocks.LIT_PUMPKIN,Blocks.MELON_BLOCK,Blocks.LADDER,
							Blocks.WOODEN_BUTTON,Blocks.WOODEN_PRESSURE_PLATE});

	public static CreativeTabs CREATIVE_TAB=new CreativeTabs("xuphorium_craft")
	{
		@SideOnly(Side.CLIENT)
		@Override
		public ItemStack getTabIconItem()
		{
			return new ItemStack(XCraftTools.X_ITEM,1);
		}

		@SideOnly(Side.CLIENT)
		public boolean hasSearchBar()
		{
			return false;
		}
	};
	
	public static boolean blockReaction(int x,int y,int z,World world)
	{
		return blockReaction(new BlockPos(x,y,z),world);
	}
	
	public static boolean blockReaction(BlockPos pos,World world)
	{
		IBlockState currentBlock=world.getBlockState(pos);
		IBlockState reactedBlock=getReactedBlock(pos,currentBlock);
		if(reactedBlock!=null)
		{
			world.setBlockState(pos,reactedBlock,3);
			return true;
		}
		return false;
	}
	
	public static IBlockState getReactedBlock(BlockPos currentPos,IBlockState currentBlock)
	{
		double type=Math.random()*100;
		if(blockEquals(currentBlock,Blocks.STONE.getDefaultState()))
		{
			if(type<=50) return Blocks.COAL_ORE.getDefaultState();
			else if(type<=80) return Blocks.IRON_ORE.getDefaultState();
			else if(type<=85) return Blocks.REDSTONE_ORE.getDefaultState();
			else if(type<=90) return Blocks.LAPIS_ORE.getDefaultState();
			else if(type<=95) return Blocks.GOLD_ORE.getDefaultState();
			else if(type<=98) return Blocks.EMERALD_ORE.getDefaultState();
			else if(type<=99) return Blocks.DIAMOND_ORE.getDefaultState();
			else return XCraftBlocks.X_ORE.getDefaultState();
		}
		else if(blockEquals(currentBlock,Blocks.DIRT.getDefaultState())||
				blockEquals(currentBlock,Blocks.DIRT.getStateFromMeta(0))||
				blockEquals(currentBlock,Blocks.DIRT.getStateFromMeta(1))||
				blockEquals(currentBlock,Blocks.DIRT.getStateFromMeta(2)))
		{
			if(type<=48) return Blocks.PLANKS.getStateFromMeta((int)Math.floor(Math.random()*6));
			else if(type<=60) return Blocks.FLOWING_WATER.getDefaultState();
			else if(type<=85) return Blocks.SAND.getStateFromMeta(0);
			else if(type<=96) return Blocks.STONE.getStateFromMeta(0);
			else if(type<=98) return Blocks.GRASS.getDefaultState();
			else return Blocks.MYCELIUM.getDefaultState();
		}
		else if(blockEquals(currentBlock,Blocks.GRASS.getDefaultState()))
		{
			if(type<=60) return Blocks.PLANKS.getStateFromMeta((int)Math.floor(Math.random()*6));
			else if(type<=75) return Blocks.FLOWING_WATER.getDefaultState();
			else if(type<=85) return Blocks.SAND.getStateFromMeta(0);
			else return Blocks.STONE.getStateFromMeta(0);
		}
		return null;
	}
	
	public static boolean blockEquals(IBlockState a,IBlockState b)
	{
		try
		{
			return (a.getBlock()==b.getBlock())&&(a.getBlock().getMetaFromState(a)==b.getBlock().getMetaFromState(b));
		}
		catch (Exception e)
		{
			return (a.getBlock()==b.getBlock());
		}
	}

	@SubscribeEvent
	public void onUseHoe(PlayerInteractEvent.RightClickBlock event)
	{
		
	}
	
	//========Material-Common========//
	public static class XCraftItemCommon extends Item
	{
		public XCraftItemCommon(String name,int maxDamage,int maxStackSize)
		{
			this.setMaxDamage(maxDamage);
			this.maxStackSize=maxStackSize;
			this.setUnlocalizedName(name);
			this.setRegistryName(name);
			this.setCreativeTab(XuphoriumCraft.CREATIVE_TAB);
		}
	}
	
	//============Xuphorium-Craft Start============//
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
