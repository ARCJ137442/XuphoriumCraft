/**
 * This mod element is always locked. Enter your code in the methods below.
 * If you don't need some of these methods,you can remove them as they
 * are overrides of the base class Elementsx_craft.ModElement.
 *
 * You can register new events in this class too.
 *
 * As this class is loaded into mod element list,it NEEDS to extend
 * ModElement class. If you remove this extend statement or remove the
 * constructor,the compilation will fail.
 *
 * If you want to make a plain independent class,create it in
 * "Workspace" -> "Source" menu.
 *
 * If you change workspace package,modid or prefix,you will need
 * to manually adapt this file to these changes or remake it.
 */
package net.mcreator.x_craft;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import net.minecraft.world.World;
import net.minecraft.block.state.IBlockState;

import net.minecraft.item.ItemArmor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;

import net.minecraft.item.crafting.Ingredient;

import net.minecraft.init.Blocks;

import net.minecraft.inventory.EntityEquipmentSlot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import net.minecraft.block.Block;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;

import java.util.Set;

@Elementsx_craft.ModElement.Tag
public class XCraft extends Elementsx_craft.ModElement
{
	//============X-Craft Global Variables============//
	public static final int[] xArmorHardness=new int[]{4,7,8,5};
	public static final ItemArmor.ArmorMaterial xArmorMaterial=EnumHelper.addArmorMaterial("X","x_craft:x_armor",25,XCraft.xArmorHardness,100,null,4f);
	
	public static final ToolMaterial xSwordMaterial=EnumHelper.addToolMaterial("X_SWORD",3,2048,9f,8f,100);
	public static final ToolMaterial xToolsMaterial=EnumHelper.addToolMaterial("X_TOOLS",6,2048,25.6f,12.8f,64);
	public static final Set<Block> xAxeEffectiveItemsSet=com.google.common.collect.Sets.newHashSet(
				new Block[]{Blocks.PLANKS,Blocks.BOOKSHELF,Blocks.LOG,Blocks.LOG2,Blocks.CHEST,
							Blocks.PUMPKIN,Blocks.LIT_PUMPKIN,Blocks.MELON_BLOCK,Blocks.LADDER,
							Blocks.WOODEN_BUTTON,Blocks.WOODEN_PRESSURE_PLATE});

	public static CreativeTabs CREATIVE_TAB=new CreativeTabs("x_craft")
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
	
	//============Constructor============//
	public XCraft(Elementsx_craft instance)
	{
		super(instance,5);
	}

	@Override
	public void init(FMLInitializationEvent event)
	{
		int i;
		//X_BLOCK
		for(i=0;i<15;i++)
		{
			GameRegistry.addShapedRecipe(
				new ResourceLocation("x_craft:x_block_"+(i+1)),
				new ResourceLocation("x_craft"),
				new ItemStack(XCraftBlocks.X_BLOCK,1,i+1),
				new Object[]
				{
					"XXX",
					"X X",
					"XXX",
					'X',new ItemStack(XCraftBlocks.X_BLOCK,1,i)
				});
			GameRegistry.addShapelessRecipe(
				new ResourceLocation("x_craft:x_block_resolution_"+(15-i)),
				new ResourceLocation("x_craft"),
				new ItemStack(XCraftBlocks.X_BLOCK,8,14-i),
				Ingredient.fromStacks(new ItemStack(XCraftBlocks.X_BLOCK,1,15-i)));
		}
		//X_ROD
		for(i=0;i<2;i++)
		{
			GameRegistry.addShapelessRecipe(
				new ResourceLocation("x_craft:x_rod_t"+i),
				new ResourceLocation("x_craft"),
				new ItemStack(XCraftMaterials.X_ROD,1,1-i),
				Ingredient.fromStacks(new ItemStack(XCraftMaterials.X_ROD,1,i)));
		}
		//X_ITEM/X_SWORD
		for(i=0;i<4;i++)
		{
			GameRegistry.addShapelessRecipe(
				new ResourceLocation("x_craft:x_item_t"+(i+1)%4),
				new ResourceLocation("x_item_"+(i+1)%4),
				new ItemStack(XCraftTools.X_ITEM,1,(i+1)%4),
				Ingredient.fromStacks(new ItemStack(XCraftTools.X_ITEM,1,i)));
			GameRegistry.addShapelessRecipe(
				new ResourceLocation("x_craft:x_sword_t"+(i+1)%4),
				new ResourceLocation("x_sword_"+(i+1)%4),
				new ItemStack(XCraftTools.X_SWORD,1,(i+1)%4),
				Ingredient.fromStacks(new ItemStack(XCraftTools.X_SWORD,1,i)));
		}
		//X_ENERGY_UNIT
		GameRegistry.addShapedRecipe(
			new ResourceLocation("x_craft:x_energy_unit_0"),
			null,
			new ItemStack(XCraftMaterials.X_ENERGY_UNIT,4,6),
			new Object[]
			{
				"#E#",
				"*S*",
				"#D#",
				'#',new ItemStack(XCraftMaterials.X_INGOT,1,0),
				'E',new ItemStack(XCraftMaterials.X_EMERALD,1,0),
				'*',new ItemStack(XCraftMaterials.X_DUST,1,0),
				'S',new ItemStack(XCraftMaterials.X_STAR,1,0),
				'D',new ItemStack(XCraftMaterials.X_DIAMOND,1,0)
			});
		for(i=-5;i<6;i++)
		{
			for(int j=1;j<7-Math.abs(i);j++)
			{
				GameRegistry.addShapelessRecipe(
					new ResourceLocation("x_craft:x_energy_unit_"+(i+j)+"_mix_"+(i-j)),
					new ResourceLocation("x_energy_unit_"+(i+j+6)),
					new ItemStack(XCraftMaterials.X_ENERGY_UNIT,2,i+6),
					Ingredient.fromStacks(new ItemStack(XCraftMaterials.X_ENERGY_UNIT,1,i+j+6)),
					Ingredient.fromStacks(new ItemStack(XCraftMaterials.X_ENERGY_UNIT,1,i-j+6))
				);
			}
		}
		//X_TRANSPORTER
		GameRegistry.addShapedRecipe(
				new ResourceLocation("x_craft:x_transporter"),
				null,
				new ItemStack(XCraftBlocks.X_TRANSPORTER,4),
				new Object[]
				{
					"CCC",
					"#P#",
					"###",
					'C',new ItemStack(XCraftMaterials.X_CATALYST),
					'#',new ItemStack(XCraftMaterials.X_INGOT),
					'P',new ItemStack(XCraftBlocks.X_PATH)
				});
		//X_RESOURCE_BLOCKS
		//Diamond
		GameRegistry.addShapedRecipe(
				new ResourceLocation("x_craft:x_diamond_block"),
				null,
				new ItemStack(XCraftBlocks.X_DIAMOND_BLOCK),
				new Object[]
				{
					"###",
					"#F#",
					"###",
					'#',new ItemStack(XCraftMaterials.X_DIAMOND),
					'F',new ItemStack(XCraftMaterials.X_FUEL)
				});
				GameRegistry.addShapelessRecipe(
					new ResourceLocation("x_craft:x_diamond_from_block"),
					new ResourceLocation("x_diamond"),
					new ItemStack(XCraftMaterials.X_DIAMOND,8),
					Ingredient.fromStacks(new ItemStack(XCraftBlocks.X_DIAMOND_BLOCK)),
					Ingredient.fromStacks(new ItemStack(XCraftMaterials.X_FUEL))
				);
		//Emerald
		GameRegistry.addShapedRecipe(
				new ResourceLocation("x_craft:x_emerald_block"),
				null,
				new ItemStack(XCraftBlocks.X_EMERALD_BLOCK),
				new Object[]
				{
					"###",
					"#F#",
					"###",
					'#',new ItemStack(XCraftMaterials.X_EMERALD),
					'F',new ItemStack(XCraftMaterials.X_FUEL)
				});
				GameRegistry.addShapelessRecipe(
					new ResourceLocation("x_craft:x_emerald_from_block"),
					new ResourceLocation("x_emerald"),
					new ItemStack(XCraftMaterials.X_EMERALD,8),
					Ingredient.fromStacks(new ItemStack(XCraftBlocks.X_EMERALD_BLOCK)),
					Ingredient.fromStacks(new ItemStack(XCraftMaterials.X_FUEL))
				);
		//Ruby
		GameRegistry.addShapedRecipe(
				new ResourceLocation("x_craft:x_ruby_block"),
				null,
				new ItemStack(XCraftBlocks.X_RUBY_BLOCK),
				new Object[]
				{
					"###",
					"#F#",
					"###",
					'#',new ItemStack(XCraftMaterials.X_RUBY),
					'F',new ItemStack(XCraftMaterials.X_FUEL)
				});
				GameRegistry.addShapelessRecipe(
					new ResourceLocation("x_craft:x_ruby_from_block"),
					new ResourceLocation("x_ruby"),
					new ItemStack(XCraftMaterials.X_RUBY,8),
					Ingredient.fromStacks(new ItemStack(XCraftBlocks.X_RUBY_BLOCK)),
					Ingredient.fromStacks(new ItemStack(XCraftMaterials.X_FUEL))
				);
		//Advanced
		GameRegistry.addShapedRecipe(
				new ResourceLocation("x_craft:x_metal_block"),
				null,
				new ItemStack(XCraftBlocks.X_METAL_BLOCK),
				new Object[]
				{
					"###",
					"#F#",
					"###",
					'#',new ItemStack(XCraftMaterials.X_METAL),
					'F',new ItemStack(XCraftMaterials.X_FUEL)
				});
		GameRegistry.addShapedRecipe(
				new ResourceLocation("x_craft:x_covalent_solid"),
				null,
				new ItemStack(XCraftBlocks.X_COVALENT_SOLID),
				new Object[]
				{
					"###",
					"#F#",
					"###",
					'#',new ItemStack(XCraftMaterials.X_COVALENT_SHARD),
					'F',new ItemStack(XCraftMaterials.X_FUEL)
				});
		GameRegistry.addShapedRecipe(
				new ResourceLocation("x_craft:x_phase_fiber_block"),
				null,
				new ItemStack(XCraftBlocks.X_PHASE_FIBER_BLOCK),
				new Object[]
				{
					"%%",
					"%%",
					'%',new ItemStack(XCraftMaterials.X_PHASE_FIBER)
				});
		GameRegistry.addShapedRecipe(
				new ResourceLocation("x_craft:x_phase_web_block"),
				null,
				new ItemStack(XCraftBlocks.X_PHASE_WEB_BLOCK),
				new Object[]
				{
					"##",
					"##",
					'#',new ItemStack(XCraftMaterials.X_PHASE_WEB)
				});
		//GameRegistry.addSmelting(new ItemStack(X_DUST_BLOCK,1),new ItemStack(X_BLOCK,1),1F);
		//X_ORE_SMELTING
		GameRegistry.addSmelting(new ItemStack(XCraftBlocks.X_ORE,1),new ItemStack(XCraftMaterials.X_INGOT,1),5F);
	}
	
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

	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(this);
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
			this.setCreativeTab(XCraft.CREATIVE_TAB);
		}
	}
}
