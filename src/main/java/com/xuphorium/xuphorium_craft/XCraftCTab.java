/**
  *此mod元素始终处于锁定状态。在下面的方法中输入您的代码。
  *如果不需要这些方法，可以将其删除
  *是基类XuphoriumCraftElements.ModElement的重写。
  *
  *您也可以在此类中注册新事件。
  *
  *由于此类已加载到mod元素列表中，因此需要扩展ModElement类。 如果删除此扩展语句或删除构造函数，编译将失败。
  *
  *如果要创建一个普通的独立类，请在“工作区”->“源”菜单。
  *
  *如果更改工作区软件包，modid或前缀，则需要手动使该文件适应这些更改或重新制作。
  */
  
package com.xuphorium.xuphorium_craft;

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

@XuphoriumCraftElements.ModElement.Tag
public class XCraftCTab extends XuphoriumCraftElements.ModElement
{
	//============Constructor============//
	public XCraftCTab(XuphoriumCraftElements instance)
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
				new ResourceLocation("xuphorium_craft:x_block_"+(i+1)),
				new ResourceLocation("xuphorium_craft"),
				new ItemStack(XCraftBlocks.X_BLOCK,1,i+1),
				new Object[]
				{
					"XXX",
					"X X",
					"XXX",
					'X',new ItemStack(XCraftBlocks.X_BLOCK,1,i)
				});
			GameRegistry.addShapelessRecipe(
				new ResourceLocation("xuphorium_craft:x_block_resolution_"+(15-i)),
				new ResourceLocation("xuphorium_craft"),
				new ItemStack(XCraftBlocks.X_BLOCK,8,14-i),
				Ingredient.fromStacks(new ItemStack(XCraftBlocks.X_BLOCK,1,15-i)));
		}
		//X_ROD
		for(i=0;i<2;i++)
		{
			GameRegistry.addShapelessRecipe(
				new ResourceLocation("xuphorium_craft:x_rod_t"+i),
				new ResourceLocation("xuphorium_craft"),
				new ItemStack(XCraftMaterials.X_ROD,1,1-i),
				Ingredient.fromStacks(new ItemStack(XCraftMaterials.X_ROD,1,i)));
		}
		//X_ENERGY_UNIT
		GameRegistry.addShapedRecipe(
			new ResourceLocation("xuphorium_craft:x_energy_unit_0"),
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
					new ResourceLocation("xuphorium_craft:x_energy_unit_"+(i+j)+"_mix_"+(i-j)),
					new ResourceLocation("x_energy_unit_"+(i+j+6)),
					new ItemStack(XCraftMaterials.X_ENERGY_UNIT,2,i+6),
					Ingredient.fromStacks(new ItemStack(XCraftMaterials.X_ENERGY_UNIT,1,i+j+6)),
					Ingredient.fromStacks(new ItemStack(XCraftMaterials.X_ENERGY_UNIT,1,i-j+6))
				);
			}
		}
		//X_TRANSPORTER
		GameRegistry.addShapedRecipe(
			new ResourceLocation("xuphorium_craft:x_transporter"),
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
			new ResourceLocation("xuphorium_craft:x_diamond_block"),
			null,
			new ItemStack(XCraftBlocks.X_DIAMOND_BLOCK),
			new Object[]
			{
				"###",
				"#F#",
				"###",
				'#',new ItemStack(XCraftMaterials.X_DIAMOND),
				'F',new ItemStack(XCraftTools.X_ITEM)
			});
		GameRegistry.addShapelessRecipe(
			new ResourceLocation("xuphorium_craft:x_diamond_from_block"),
			new ResourceLocation("x_diamond"),
			new ItemStack(XCraftMaterials.X_DIAMOND,8),
			Ingredient.fromStacks(new ItemStack(XCraftBlocks.X_DIAMOND_BLOCK)),
			Ingredient.fromStacks(new ItemStack(XCraftTools.X_ITEM))
		);
		//Emerald
		GameRegistry.addShapedRecipe(
			new ResourceLocation("xuphorium_craft:x_emerald_block"),
			null,
			new ItemStack(XCraftBlocks.X_EMERALD_BLOCK),
			new Object[]
			{
				"###",
				"#F#",
				"###",
				'#',new ItemStack(XCraftMaterials.X_EMERALD),
				'F',new ItemStack(XCraftTools.X_ITEM)
			});
		GameRegistry.addShapelessRecipe(
			new ResourceLocation("xuphorium_craft:x_emerald_from_block"),
			new ResourceLocation("x_emerald"),
			new ItemStack(XCraftMaterials.X_EMERALD,8),
			Ingredient.fromStacks(new ItemStack(XCraftBlocks.X_EMERALD_BLOCK)),
			Ingredient.fromStacks(new ItemStack(XCraftTools.X_ITEM))
		);
		//Ruby
		GameRegistry.addShapedRecipe(
			new ResourceLocation("xuphorium_craft:x_ruby_block"),
			null,
			new ItemStack(XCraftBlocks.X_RUBY_BLOCK),
			new Object[]
			{
				"###",
				"#F#",
				"###",
				'#',new ItemStack(XCraftMaterials.X_RUBY),
				'F',new ItemStack(XCraftTools.X_ITEM)
			});
		GameRegistry.addShapelessRecipe(
			new ResourceLocation("xuphorium_craft:x_ruby_from_block"),
			new ResourceLocation("x_ruby"),
			new ItemStack(XCraftMaterials.X_RUBY,8),
			Ingredient.fromStacks(new ItemStack(XCraftBlocks.X_RUBY_BLOCK)),
			Ingredient.fromStacks(new ItemStack(XCraftTools.X_ITEM))
		);
		//Advanced
		GameRegistry.addShapedRecipe(
			new ResourceLocation("xuphorium_craft:x_metal_block"),
			null,
			new ItemStack(XCraftBlocks.X_METAL_BLOCK),
			new Object[]
			{
				"###",
				"#F#",
				"###",
				'#',new ItemStack(XCraftMaterials.X_METAL),
				'F',new ItemStack(XCraftTools.X_ITEM)
			});
		GameRegistry.addShapedRecipe(
			new ResourceLocation("xuphorium_craft:x_covalent_solid"),
			null,
			new ItemStack(XCraftBlocks.X_COVALENT_SOLID),
			new Object[]
			{
				"###",
				"#F#",
				"###",
				'#',new ItemStack(XCraftMaterials.X_COVALENT_SHARD),
				'F',new ItemStack(XCraftTools.X_ITEM)
			});
		GameRegistry.addShapedRecipe(
			new ResourceLocation("xuphorium_craft:x_phase_fiber_block"),
			null,
			new ItemStack(XCraftBlocks.X_PHASE_FIBER_BLOCK),
			new Object[]
			{
				"%%",
				"%%",
				'%',new ItemStack(XCraftMaterials.X_PHASE_FIBER)
			});
		GameRegistry.addShapedRecipe(
			new ResourceLocation("xuphorium_craft:x_phase_web_block"),
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

	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(this);
	}
}
