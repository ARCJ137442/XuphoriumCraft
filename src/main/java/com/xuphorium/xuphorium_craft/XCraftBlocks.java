package com.xuphorium.xuphorium_craft;

import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.WorldServer;

import net.minecraftforge.common.MinecraftForge;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.BlockFluidClassic;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.ItemMeshDefinition;

import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.Block;

import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyBool;

import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;

import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenerator;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import net.minecraft.init.Blocks;

import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;

import net.minecraft.creativetab.CreativeTabs;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@XuphoriumCraftElements.ModElement.Tag
public class XCraftBlocks extends XuphoriumCraftElements.ModElement
{
	public static class WorldGenOreBall extends WorldGenerator
	{
		public boolean mustFull;
		
		public WorldGenOreBall(boolean mustFull)
		{
			super();
			this.mustFull=mustFull;
		}
		
		public boolean generate(World worldIn,Random rand,BlockPos position)
		{
			return this.generate(worldIn,Blocks.STONE.getDefaultState(),X_ORE.getDefaultState(),X_EMERALD_BLOCK.getDefaultState(), rand, position);
		}
		
		public boolean generate(World worldIn,IBlockState stone,IBlockState ore,IBlockState center,Random rand,BlockPos position)
		{
			int x=position.getX();
			int y=position.getY();
			int z=position.getZ();
			int canReplaceCount=0;
			BlockPos currentPos;
			BlockPos[] succeedPos=new BlockPos[19];
			int i=0;
			//XCraftBlocks.LOGGER.info("Generating X Ores By ("+stone.toString()+","+ore.toString()+","+center.toString()+")");
			//Detect
			for(int ix=-1;ix<2;ix++)
			{
				for(int iy=-1;iy<2;iy++)
				{
					for(int iz=-1;iz<2;iz++)
					{
						if(Math.abs(ix)+Math.abs(iy)+Math.abs(iz)>2) continue;
						currentPos=new BlockPos(x+ix,y+iy,z+iz);
						if(worldIn.getBlockState(currentPos).getBlock()==stone.getBlock())
						{
							canReplaceCount++;
							succeedPos[i]=currentPos;
							i++;
						}
					}
				}
			}
			//Generate
			if(!this.mustFull||canReplaceCount<19) return false;
			//XCraftBlocks.LOGGER.info("Generated X Ores At "+position.toString()+",As "+this.center.getBlock().getUnlocalizedName());
			for(BlockPos pos : succeedPos)
			{
				if(pos.equals(position)) worldIn.setBlockState(position,center,2);
				else worldIn.setBlockState(pos,ore,2);
			}
			return true;
		}
	}
	
	public static void testSummonXBOSS(World world,int x,int y,int z,IBlockState state,int power,Block neighborBlock,BlockPos fromPos)
	{
		if(power<15) return;
		BlockPos currentPos=new BlockPos(x,y,z);
		BlockPos currentPos2=new BlockPos(x,y+1,z);
		BlockPos currentPos3=new BlockPos(x,y+2,z);
		
		if(world.getBlockState(currentPos).getBlock()==X_BLOCK&&
		   world.getBlockState(currentPos2).getBlock()==X_BLOCK_ADVANCED&&
		   world.getBlockState(currentPos3).getBlock()==Blocks.OBSERVER)
		{
			if(!world.isRemote)
			{
				//World Event
				world.setBlockToAir(currentPos);
				world.setBlockToAir(currentPos2);
				world.setBlockToAir(currentPos3);
				world.addWeatherEffect(new EntityLightningBolt(world,x,y,z,false));
				//Summon Entity
				Entity entityToSpawn=new XBoss.EntityXBoss(world);
				entityToSpawn.setLocationAndAngles(x+0.5,y,z+0.5,world.rand.nextFloat()*360F,0.0F);
				world.spawnEntity(entityToSpawn);
				world.createExplosion(entityToSpawn,x,y,z,5,true);
			}
		}
	}
	
	public static boolean isXOre(Block block)
	{
		return (block==X_ORE||block==X_ORE_EMERALD||block==X_ORE_NETHER||block==X_ORE_END||block==X_ORE_BEDROCK);
	}
	
	public static boolean getIsReplaceable(World world,BlockPos pos,IBlockState state)
	{
		return state.getBlock().isReplaceable(world,pos);
	}
	
	public static boolean getIsReplaceable(World world,BlockPos pos)
	{
		return world.getBlockState(pos).getBlock().isReplaceable(world,pos);
	}
	
	@GameRegistry.ObjectHolder(XuphoriumCraft.MODID+":x_ore")
	public static final Block X_ORE=null;
	
	@GameRegistry.ObjectHolder(XuphoriumCraft.MODID+":x_ore_emerald")
	public static final Block X_ORE_EMERALD=null;
	
	@GameRegistry.ObjectHolder(XuphoriumCraft.MODID+":x_ore_nether")
	public static final Block X_ORE_NETHER=null;
	
	@GameRegistry.ObjectHolder(XuphoriumCraft.MODID+":x_ore_end")
	public static final Block X_ORE_END=null;
	
	@GameRegistry.ObjectHolder(XuphoriumCraft.MODID+":x_ore_bedrock")
	public static final Block X_ORE_BEDROCK=null;
	
	@GameRegistry.ObjectHolder(XuphoriumCraft.MODID+":x_block")
	public static final Block X_BLOCK=null;
	
	@GameRegistry.ObjectHolder(XuphoriumCraft.MODID+":x_block_advanced")
	public static final Block X_BLOCK_ADVANCED=null;
	
	@GameRegistry.ObjectHolder(XuphoriumCraft.MODID+":x_crystal_block")
	public static final Block X_CRYSTAL_BLOCK=null;
	
	@GameRegistry.ObjectHolder(XuphoriumCraft.MODID+":x_dust_block")
	public static final Block X_DUST_BLOCK=null;
	
	@GameRegistry.ObjectHolder(XuphoriumCraft.MODID+":x_path")
	public static final Block X_PATH=null;
	
	@GameRegistry.ObjectHolder(XuphoriumCraft.MODID+":x_reactive_core")
	public static final Block X_REACTIVE_CORE=null;
	
	@GameRegistry.ObjectHolder(XuphoriumCraft.MODID+":x_explosive")
	public static final Block X_EXPLOSIVE=null;
	
	@GameRegistry.ObjectHolder(XuphoriumCraft.MODID+":x_curer")
	public static final Block X_CURER=null;
	
	@GameRegistry.ObjectHolder(XuphoriumCraft.MODID+":x_dust_generater")
	public static final Block X_DUST_GENERATER=null;
	
	@GameRegistry.ObjectHolder(XuphoriumCraft.MODID+":x_liquid")
	public static final Block X_LIQUID=null;
	
	@GameRegistry.ObjectHolder(XuphoriumCraft.MODID+":x_glass")
	public static final Block X_GLASS=null;
	
	@GameRegistry.ObjectHolder(XuphoriumCraft.MODID+":x_diamond_block")
	public static final Block X_DIAMOND_BLOCK=null;
	
	@GameRegistry.ObjectHolder(XuphoriumCraft.MODID+":x_emerald_block")
	public static final Block X_EMERALD_BLOCK=null;
	
	@GameRegistry.ObjectHolder(XuphoriumCraft.MODID+":x_ruby_block")
	public static final Block X_RUBY_BLOCK=null;
	
	@GameRegistry.ObjectHolder(XuphoriumCraft.MODID+":x_transporter")
	public static final Block X_TRANSPORTER=null;
	
	@GameRegistry.ObjectHolder(XuphoriumCraft.MODID+":x_metal_block")
	public static final Block X_METAL_BLOCK=null;
	
	@GameRegistry.ObjectHolder(XuphoriumCraft.MODID+":x_covalent_solid")
	public static final Block X_COVALENT_SOLID=null;
	
	@GameRegistry.ObjectHolder(XuphoriumCraft.MODID+":x_phase_fiber_block")
	public static final Block X_PHASE_FIBER_BLOCK=null;
	
	@GameRegistry.ObjectHolder(XuphoriumCraft.MODID+":x_phase_web_block")
	public static final Block X_PHASE_WEB_BLOCK=null;
	
	//Fluid
	@GameRegistry.ObjectHolder(XuphoriumCraft.MODID+":x_fluid")
	public static final Block X_FLUID=null;
	
	@GameRegistry.ObjectHolder(XuphoriumCraft.MODID+":x_fluid")
	public static final Item X_FLUID_ITEM=null;
	
	private Fluid X_FLUID_Fluid;
	
	//Oxygen
	@GameRegistry.ObjectHolder(XuphoriumCraft.MODID+":x_oxygen")
	public static final Block X_OXYGEN=null;
	
	@GameRegistry.ObjectHolder(XuphoriumCraft.MODID+":x_oxygen")
	public static final Item X_OXYGEN_ITEM=null;
	
	private Fluid X_OXYGEN_Fluid;
	
	public XCraftBlocks(XuphoriumCraftElements instance)
	{
		super(instance,3);
		X_FLUID_Fluid=new Fluid("x_fluid",
						new ResourceLocation(XuphoriumCraft.MODID+":blocks/x_fluid_still"),
						new ResourceLocation(XuphoriumCraft.MODID+":blocks/x_fluid")
						).setDensity(1280).setViscosity(768).setGaseous(false).setTemperature(300).setRarity(EnumRarity.UNCOMMON);
		X_OXYGEN_Fluid=new Fluid("x_oxygen",
						new ResourceLocation(XuphoriumCraft.MODID+":blocks/x_oxygen"),
						new ResourceLocation(XuphoriumCraft.MODID+":blocks/x_oxygen")
						).setDensity(-512).setViscosity(256).setGaseous(true).setTemperature(324).setRarity(EnumRarity.RARE);
						
	}

	@Override
	public void initElements()
	{
		//Ore
		elements.blocks.add(XOre::new);
		elements.items.add(()->new ItemBlock(X_ORE).setRegistryName(X_ORE.getRegistryName()));
		//Ore_Emerald
		elements.blocks.add(XOreEmerald::new);
		elements.items.add(()->new ItemBlock(X_ORE_EMERALD).setRegistryName(X_ORE_EMERALD.getRegistryName()));
		//Ore_Nether
		elements.blocks.add(XOreNether::new);
		elements.items.add(()->new ItemBlock(X_ORE_NETHER).setRegistryName(X_ORE_NETHER.getRegistryName()));
		//Ore_End
		elements.blocks.add(XOreEnd::new);
		elements.items.add(()->new ItemBlock(X_ORE_END).setRegistryName(X_ORE_END.getRegistryName()));
		//Ore
		elements.blocks.add(XOreBedrock::new);
		elements.items.add(()->new ItemBlock(X_ORE_BEDROCK).setRegistryName(X_ORE_BEDROCK.getRegistryName()));
		//Block
		elements.blocks.add(XBlock::new);
		elements.items.add(ItemXBlock::new);
		//Block_Advanced
		elements.blocks.add(XBlockAdvanced::new);
		elements.items.add(ItemXBlockAdvanced::new);
		//Dust_Block
		elements.blocks.add(XDustBlock::new);
		elements.items.add(()->new ItemBlock(X_DUST_BLOCK).setRegistryName(X_DUST_BLOCK.getRegistryName()));
		//Path
		elements.blocks.add(XPath::new);
		elements.items.add(()->new ItemBlock(X_PATH).setRegistryName(X_PATH.getRegistryName()));
		//Reactive_Core
		elements.blocks.add(XReactiveCore::new);
		elements.items.add(()->new ItemBlock(X_REACTIVE_CORE).setRegistryName(X_REACTIVE_CORE.getRegistryName()));
		//Explosive
		elements.blocks.add(XExplosive::new);
		elements.items.add(()->new ItemBlock(X_EXPLOSIVE).setRegistryName(X_EXPLOSIVE.getRegistryName()));
		//Curer
		elements.blocks.add(XCurer::new);
		elements.items.add(()->new ItemBlock(X_CURER).setRegistryName(X_CURER.getRegistryName()));
		//Dust_Generater
		elements.blocks.add(XDustGenerater::new);
		elements.items.add(ItemXDustGenerater::new);
		//Liquid
		elements.blocks.add(XLiquid::new);
		elements.items.add(ItemXLiquid::new);
		//Glass
		elements.blocks.add(XGlass::new);
		elements.items.add(()->new ItemBlock(X_GLASS).setRegistryName(X_GLASS.getRegistryName()));
		//Diamond_Block
		elements.blocks.add(XDiamondBlock::new);
		elements.items.add(()->new ItemBlock(X_DIAMOND_BLOCK).setRegistryName(X_DIAMOND_BLOCK.getRegistryName()));
		//Emerald_Block
		elements.blocks.add(XEmeraldBlock::new);
		elements.items.add(()->new ItemBlock(X_EMERALD_BLOCK).setRegistryName(X_EMERALD_BLOCK.getRegistryName()));
		//Ruby_Block
		elements.blocks.add(XRubyBlock::new);
		elements.items.add(()->new ItemBlock(X_RUBY_BLOCK).setRegistryName(X_RUBY_BLOCK.getRegistryName()));
		//Crystal_Block
		elements.blocks.add(XCrystalBlock::new);
		elements.items.add(()->new ItemBlock(X_CRYSTAL_BLOCK).setRegistryName(X_CRYSTAL_BLOCK.getRegistryName()));
		//Transporter
		elements.blocks.add(XTransporter::new);
		elements.items.add(()->new ItemBlock(X_TRANSPORTER).setRegistryName(X_TRANSPORTER.getRegistryName()));
		//Metal_Block
		elements.blocks.add(XMetalBlock::new);
		elements.items.add(()->new ItemBlock(X_METAL_BLOCK).setRegistryName(X_METAL_BLOCK.getRegistryName()));
		//Covalent_Solid
		elements.blocks.add(XCovalentSolid::new);
		elements.items.add(()->new ItemBlock(X_COVALENT_SOLID).setRegistryName(X_COVALENT_SOLID.getRegistryName()));
		//Phase_Fiber_Block
		elements.blocks.add(XPhaseFiberBlock::new);
		elements.items.add(()->new ItemBlock(X_PHASE_FIBER_BLOCK).setRegistryName(X_PHASE_FIBER_BLOCK.getRegistryName()));
		//Phase_Web_Block
		elements.blocks.add(XPhaseWebBlock::new);
		elements.items.add(()->new ItemBlock(X_PHASE_WEB_BLOCK).setRegistryName(X_PHASE_WEB_BLOCK.getRegistryName()));
		//Fluid
		elements.blocks.add(()->new BlockFluidClassic(X_FLUID_Fluid,Material.WATER)
		{}.setUnlocalizedName("x_fluid").setRegistryName("x_fluid"));
		elements.items.add(()->new ItemBlock(X_FLUID).setRegistryName("x_fluid"));
		//Oxygen
		elements.blocks.add(()->new BlockFluidClassic(X_OXYGEN_Fluid,Material.WATER)
		{}.setUnlocalizedName("x_oxygen").setRegistryName("x_oxygen"));
		elements.items.add(()->new ItemBlock(X_OXYGEN).setRegistryName("x_oxygen"));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event)
	{
		//====Inventory Models====//
		int i;
		//Ore,OreNether,OreEnd
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_ORE),0,new ModelResourceLocation(XuphoriumCraft.MODID+":x_ore","inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_ORE_EMERALD),0,new ModelResourceLocation(XuphoriumCraft.MODID+":x_ore_emerald","inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_ORE_NETHER),0,new ModelResourceLocation(XuphoriumCraft.MODID+":x_ore_nether","inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_ORE_END),0,new ModelResourceLocation(XuphoriumCraft.MODID+":x_ore_end","inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_ORE_BEDROCK),0,new ModelResourceLocation(XuphoriumCraft.MODID+":x_ore_bedrock","inventory"));
		//Block,BlockAdvanced
		for(i=0;i<16;i++)
		{
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_BLOCK),i,new ModelResourceLocation(XuphoriumCraft.MODID+":x_block_"+i,"inventory"));
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_BLOCK_ADVANCED),i,new ModelResourceLocation(XuphoriumCraft.MODID+":x_block_advanced_"+i,"inventory"));
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_LIQUID),i,new ModelResourceLocation(XuphoriumCraft.MODID+":x_liquid_"+i,"inventory"));
		}
		//CrystalBlock
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_CRYSTAL_BLOCK),0,new ModelResourceLocation(XuphoriumCraft.MODID+":x_crystal_block","inventory"));
		//DustBlock
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_DUST_BLOCK),0,new ModelResourceLocation(XuphoriumCraft.MODID+":x_dust_block","inventory"));
		//Path
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_PATH),0,new ModelResourceLocation(XuphoriumCraft.MODID+":x_path","inventory"));
		//ReactiveCore
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_REACTIVE_CORE),0,new ModelResourceLocation(XuphoriumCraft.MODID+":x_reactive_core","inventory"));
		//Explosive
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_EXPLOSIVE),0,new ModelResourceLocation(XuphoriumCraft.MODID+":x_explosive","inventory"));
		//Curer
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_CURER),0,new ModelResourceLocation(XuphoriumCraft.MODID+":x_curer","inventory"));
		//Dust_Generater
		for(i=0;i<4;i++) ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_DUST_GENERATER),i,new ModelResourceLocation(XuphoriumCraft.MODID+":x_dust_generater_"+i,"inventory"));
		//Glass
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_GLASS),0,new ModelResourceLocation(XuphoriumCraft.MODID+":x_glass","inventory"));
		//Diamond_Block
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_DIAMOND_BLOCK),0,new ModelResourceLocation(XuphoriumCraft.MODID+":x_diamond_block","inventory"));
		//Emerald_Block
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_EMERALD_BLOCK),0,new ModelResourceLocation(XuphoriumCraft.MODID+":x_emerald_block","inventory"));
		//Ruby_Block
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_RUBY_BLOCK),0,new ModelResourceLocation(XuphoriumCraft.MODID+":x_ruby_block","inventory"));
		//Metal_Block
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_METAL_BLOCK),0,new ModelResourceLocation(XuphoriumCraft.MODID+":x_metal_block","inventory"));
		//Covalent_Solid
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_COVALENT_SOLID),0,new ModelResourceLocation(XuphoriumCraft.MODID+":x_covalent_solid","inventory"));
		//Phase_Fiber_Block
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_PHASE_FIBER_BLOCK),0,new ModelResourceLocation(XuphoriumCraft.MODID+":x_phase_fiber_block","inventory"));
		//Phase_Web_Block
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_PHASE_WEB_BLOCK),0,new ModelResourceLocation(XuphoriumCraft.MODID+":x_phase_web_block","inventory"));
		//Transporter
		for(i=0;i<7;i++)
		{
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_TRANSPORTER),i,new ModelResourceLocation(XuphoriumCraft.MODID+":x_transporter","inventory"));
		}
		//Fluid
		ModelBakery.registerItemVariants(X_FLUID_ITEM);
		ModelLoader.setCustomMeshDefinition(X_FLUID_ITEM,new ItemMeshDefinition()
		{
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack)
			{
				return new ModelResourceLocation(XuphoriumCraft.MODID+":x_fluid","x_fluid");
			}
		});
		ModelLoader.setCustomStateMapper(X_FLUID,new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return new ModelResourceLocation(XuphoriumCraft.MODID+":x_fluid","x_fluid");
			}
		});
		//Oxygen
		ModelBakery.registerItemVariants(X_OXYGEN_ITEM);
		ModelLoader.setCustomMeshDefinition(X_OXYGEN_ITEM,new ItemMeshDefinition()
		{
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack)
			{
				return new ModelResourceLocation(XuphoriumCraft.MODID+":x_oxygen","x_oxygen");
			}
		});
		ModelLoader.setCustomStateMapper(X_OXYGEN,new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return new ModelResourceLocation(XuphoriumCraft.MODID+":x_oxygen","x_oxygen");
			}
		});
	}

	@Override
	public void init(FMLInitializationEvent event)
	{
	}

	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		//EventBus
		MinecraftForge.EVENT_BUS.register(this);
		//Fluid
		FluidRegistry.registerFluid(X_FLUID_Fluid);
		FluidRegistry.addBucketForFluid(X_FLUID_Fluid);
		//Oxygen
		FluidRegistry.registerFluid(X_OXYGEN_Fluid);
		FluidRegistry.addBucketForFluid(X_OXYGEN_Fluid);
	}

	@Override
	public void serverLoad(FMLServerStartingEvent event)
	{
	}
	
	public WorldGenOreBall worldGeneratorBall=new WorldGenOreBall(true);

	//fill ~-20 ~-10 ~-20 ~20 ~10 ~10 air 0 replace stone
	@Override
	public void generateWorld(Random random,int chunkX,int chunkZ,World world,int dimID,IChunkGenerator chunkGenerator,IChunkProvider chunkProvider)
	{
		if(world.isRemote) return;
		int i;
		BlockPos pos;
		IBlockState centerState;
		//dim ore
		switch(dimID)
		{
			case 0:
				centerState=getRandomizedXOreBlock(random,dimID,X_ORE).getDefaultState();
				for(i=0;i<1;i++)
				{
					pos=new BlockPos(chunkX+random.nextInt(13)+1,random.nextInt(4)+9,chunkZ+random.nextInt(13)+1);
					worldGeneratorBall.generate(world,Blocks.STONE.getDefaultState(),X_ORE.getDefaultState(),centerState,random,pos);
				}
			break;
			case -1:
				centerState=getRandomizedXOreBlock(random,dimID,X_ORE_NETHER).getDefaultState();
				for(i=0;i<5;i++)
				{
					pos=new BlockPos(chunkX+random.nextInt(13)+1,random.nextInt(253)+1,chunkZ+random.nextInt(13)+1);
					worldGeneratorBall.generate(world,Blocks.NETHERRACK.getDefaultState(),X_ORE_NETHER.getDefaultState(),centerState,random,pos);
				}
			break;
			case 1:
				centerState=getRandomizedXOreBlock(random,dimID,X_ORE_END).getDefaultState();
				for(i=0;i<9;i++)
				{
					pos=new BlockPos(chunkX+random.nextInt(13)+1,random.nextInt(253)+1,chunkZ+random.nextInt(13)+1);
					worldGeneratorBall.generate(world,Blocks.END_STONE.getDefaultState(),X_ORE_END.getDefaultState(),centerState,random,pos);
				}
			break;
		}
		//bedrock
		for(i=0;i<16;i++)
		{
			pos=new BlockPos(chunkX+random.nextInt(13)+1,random.nextInt(4)+1,chunkZ+random.nextInt(13)+1);
			if(world.getBlockState(pos).getBlock()==Blocks.BEDROCK) world.setBlockState(pos,X_ORE_BEDROCK.getDefaultState(),2);
		}
	}
	
	public static Block getRandomizedXOreBlock(Random random,int dimensionID,Block defaultBlock)
	{
		int randInt=random.nextInt(100);
		switch(dimensionID)
		{
			case XuphoriumCraft.DIMENSION_ID_OVERWORLD:
				if(randInt<20) return X_ORE_EMERALD;
				if(randInt<40) return X_FLUID;
				if(randInt<70) return Blocks.WATER;
				else return Blocks.LAVA;
			case XuphoriumCraft.DIMENSION_ID_THE_NETHER:
				if(randInt<70) return Blocks.LAVA;
				if(randInt<80) return Blocks.NETHER_BRICK;
				if(randInt<87) return Blocks.OBSIDIAN;
				if(randInt<94) return Blocks.RED_NETHER_BRICK;
				if(randInt<99) return Blocks.GLOWSTONE;
				return X_LIQUID;
			case XuphoriumCraft.DIMENSION_ID_THE_END:
				if(randInt<50) return Blocks.END_BRICKS;
				if(randInt<85) return Blocks.OBSIDIAN;
				if(randInt<88) return X_FLUID;
				if(randInt<90) return X_LIQUID;
				return X_OXYGEN;
		}
		return defaultBlock;
	}
	
	//============Current Events============//
	@SubscribeEvent
	public boolean onEntityDestroyBlock(LivingDestroyBlockEvent event)
	{
		Block block=event.getState().getBlock();
		if(event.getEntity() instanceof EntityDragon)
		{
			if(isXOre(block)||
				block==X_OXYGEN||
			    block==X_CRYSTAL_BLOCK||
				block==XCraftMaterials.X_ALPHA_METAL||
				block==XCraftMaterials.X_DELTA_METAL||
				block==XCraftMaterials.X_GAMMA_METAL||
				block==XCraftMaterials.X_OMEGA_METAL)
			{
				if(event.isCancelable()&&!event.isCanceled()) event.setCanceled(true);
				return false;
			}
		}
		return true;
	}
	
	//============Current Classes============//
	//========Block-Common========//
	public static class XCraftBlockCommon extends Block
	{
		public XCraftBlockCommon(Material material,String name,SoundType soundType)
		{
			super(material);
			this.setRegistryName(name);
			this.setUnlocalizedName(name);
			this.setSoundType(soundType);
			this.setCreativeTab(XuphoriumCraft.CREATIVE_TAB);
		}
	}
	
	public static class XCraftItemBlockCommon extends ItemBlock
	{
		public boolean placeNeedSneaking=false;
		
		public XCraftItemBlockCommon(Block block)
		{
			super(block);
			setRegistryName(block.getRegistryName());
		}
		
		public XCraftItemBlockCommon(Block block,boolean hasSubtypes)
		{
			this(block);
			this.setHasSubtypes(hasSubtypes);
		}
		
		public XCraftItemBlockCommon(Block block,boolean hasSubtypes,boolean placeNeedSneaking)
		{
			this(block,hasSubtypes);
			this.placeNeedSneaking=placeNeedSneaking;
		}
		
		/**
		 * Called when a Block is right-clicked with this Item
		 */
		public EnumActionResult onItemUse(EntityPlayer player,World worldIn,BlockPos pos,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ)
		{
			if(this.placeNeedSneaking&&!player.isSneaking()) return EnumActionResult.FAIL;
			return super.onItemUse(player,worldIn,pos,hand,facing,hitX,hitY,hitZ);
		}
	}
	
	//========X-Block========//
	public static class ItemXBlock extends XCraftItemBlockCommon
	{
		public ItemXBlock()
		{
			super(X_BLOCK,true);
		}
		
		protected ItemXBlock(Block block,boolean hasSubTypes)
		{
			super(block,hasSubTypes);
		}
		
		public int getMetadata(int damage)
		{
			return damage;
		}
	}

	public static class XBlock extends XCraftBlockCommon
	{
		public static final PropertyInteger LEVEL=PropertyInteger.create("level",0,15);
		
		//Use for child class
		protected XBlock(Material material,String name,SoundType soundType,
		              float hardness,float resistance,float lightLevel,int lightOpacity)
		{
			super(material,name,soundType);
			this.setHardness(hardness);
			this.setResistance(resistance);
			this.setLightLevel(lightLevel);
			this.setLightOpacity(lightOpacity);
			this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL,Integer.valueOf(0)));
		}
		
		//main
		public XBlock()
		{
			this(Material.IRON,"x_block",SoundType.STONE,5.12F,64F,0F,255);
			this.setHarvestLevel("pickaxe",3);
		}
		
		public int getMetaFromState(IBlockState state)
		{
			return state.getValue(LEVEL).intValue();
		}
		
		public IBlockState getStateFromMeta(int meta)
		{
			return this.getDefaultState().withProperty(LEVEL,meta);
		}

		public ItemStack getItem(World worldIn,BlockPos pos,IBlockState state)
		{
			return new ItemStack(this,1,state.getBlock().getMetaFromState(state));
		}
		
		public int damageDropped(IBlockState state)
		{
			return state.getValue(LEVEL);
		}
		
		public void getSubBlocks(CreativeTabs itemIn,NonNullList<ItemStack> items)
		{
			for (int i=0;i<16;i++) items.add(new ItemStack(this,1,i));
		}

		protected BlockStateContainer createBlockState()
		{
			return new BlockStateContainer(this,new IProperty[]{LEVEL});
		}

		@SideOnly(Side.CLIENT)
		@Override
		public BlockRenderLayer getBlockLayer()
		{
			return BlockRenderLayer.CUTOUT;
		}

		@Override
		public boolean isBeaconBase(IBlockAccess worldObj,BlockPos pos,BlockPos beacon)
		{
			return true;
		}
		
		@Override
		public void neighborChanged(IBlockState state,World world,BlockPos pos,Block neighborBlock,BlockPos fromPos)
		{
			super.neighborChanged(state,world,pos,neighborBlock,fromPos);
			int power=world.isBlockIndirectlyGettingPowered(pos);
			if(power>0) XCraftBlocks.testSummonXBOSS(world,pos.getX(),pos.getY(),pos.getZ(),state,world.isBlockIndirectlyGettingPowered(pos),neighborBlock,fromPos);
		}
	}
	
	//========X-Block-Advanced========//
	public static class ItemXBlockAdvanced extends ItemXBlock
	{
		public ItemXBlockAdvanced()
		{
			super(X_BLOCK_ADVANCED,true);
		}
	}

	public static class XBlockAdvanced extends XBlock
	{
		public XBlockAdvanced()
		{
			super(Material.IRON,"x_block_advanced",SoundType.METAL,5.12F,64F,0F,255);
			setHarvestLevel("pickaxe",4);
		}

		@Override
		public boolean canConnectRedstone(IBlockState state,IBlockAccess world,BlockPos pos,EnumFacing side)
		{
			return true;
		}
		
		@Override
		public void neighborChanged(IBlockState state,World world,BlockPos pos,Block neighborBlock,BlockPos fromPos)
		{
			super.neighborChanged(state,world,pos,neighborBlock,fromPos);
			double cx=(double)pos.getX()+0.5;
			double cy=(double)pos.getY()+0.5;
			double cz=(double)pos.getZ()+0.5;
			int power=world.isBlockIndirectlyGettingPowered(pos);
			int level=world.getBlockState(pos).getValue(LEVEL);
			if(power>0)
			{
				//punch
				XBoss.punchEntities(world,null,cx,cy,cz,1+(level+1)*0.5,1+(1+power)*0.125,0.1);
				//particle
				if((world instanceof WorldServer))
				{
					((WorldServer)world).spawnParticle(EnumParticleTypes.CRIT_MAGIC,
							cx,cy,cz,
							8*(1+level/4),
							0.5,0.5,0.5,
							0
					);
				}
			}
		}
		
		public static boolean XEnergyUnitUse(World world,BlockPos pos,IBlockState state,ItemStack stack,EnumFacing facing,float hitX,float hitY,float hitZ)
		{
			if(world.isRemote) return false;
			Item item=stack.getItem();
			if(state.getBlock()==X_BLOCK_ADVANCED&&item==XCraftMaterials.X_ENERGY_UNIT)
			{
				int power=item.getDamage(stack)-6;//+6~-6
				int level=state.getValue(LEVEL);
				int count=stack.getCount();
				if(count<1) return false;
				if(power==0&&level>0)
				{
					power=(level-level%count)/count;
					level=level%count;
					//l=15,c=5(l%c=0) -> p=(15-15%5)/5=+2,l=0;
					//l=13,c=4(l%c=1) -> p=(13-13%4)/4=+3,l=1;
					//l=12,c=7(l%c=5) -> p=(12-12%7)/7=+1,l=5;
					//l=9,c=12(l%c=4) -> p=(9-9%12)/12=0,l=9;9%12=9
					//y>x => x%y=x => (x-x%y)/y=0
					if(power>0&&power<7)//-6<=p<=6
					{
						world.setBlockState(pos,X_BLOCK_ADVANCED.getDefaultState().withProperty(LEVEL,Integer.valueOf(level)));
						stack.setItemDamage(power+6);
						return true;
					}
				}
				else if(power!=0)
				{
					//sum<0: such as p=-4,l=2,s=-2 -> l=0,p=-2;p=-3,l=1 -> l=0,p=-2
					//sum>15: such as p=+6,l=12,s=18 -> l=15,p=+3;p=+2,l=14 -> l=15,p=+1
					//0<=sum<=15: such as p=-4,l=9,s=5 -> l=5,p=0;p=3,l=10 -> l=13,p=0
					int sum=level+power*count;
					if(sum>=0&&sum<16)
					{
						power=0;
						level=sum;
					}
					else
					{
						int abxLevel=power>0?(15-level):level;
						if(power>0)//p+
						{
							//l=7,p=+4,c=2(s=15) -> p=0,l=15;
							//l=12,	p=+3,	c=2(s=18,(l-l%c)/c=2)	->	p=+2,	l=14;
							//l=3,	p=+6,	c=5(s=33,(l-l%c)/c=2)	->	p=+3,	l=13;
							//l=7,	p=+3,	c=4(s=19,(l-l%c)/c=2)	->	p=+3,	l=13;
							//l=5,	p=+3,	c=5(s=20,l=c)			->	p=+2,	l=0;
							//l=7,	p=+2,	c=13(s=33,l<c)			->	Error;
							if(level+count<16)
							{
								level=15-abxLevel%count;//l1%c=l2
								power-=(abxLevel/count);
							}
						}
						else if(level-count>=0)//p-
						{
							//l=6,p=-2,c=2(s=2) -> p=0,l=2;
							//l=7,	p=-3,	c=3(s=-2,(l-l%c)/c=2)	->	p=-2,	l=1;
							//l=11,	p=-6,	c=3(s=-7,(l-l%c)/c=2)	->	p=-3,	l=2;
							//l=3,	p=-2,	c=2(s=-1,(l-l%c)/c=1)	->	p=-1,	l=1;
							//l=6,	p=-2,	c=6(s=-6,l=c)			->	p=+1,	l=0;
							//l=3,	p=-1,	c=4(s=-4,l<c)			->	Error;
							if(power+(abxLevel/count)<7)//p<=6
							{
								level%=count;//l1%c=l2
								power+=(abxLevel/count);
							}
						}
					}
					if(power!=item.getDamage(stack)-6&&level!=state.getValue(LEVEL))
					{
						world.setBlockState(pos,X_BLOCK_ADVANCED.getDefaultState().withProperty(LEVEL,Integer.valueOf(level)));
						stack.setItemDamage(power+6);
						return true;
					}
				}
			}
			return false;
		}
		
		public boolean onBlockActivated(World world,BlockPos pos,IBlockState state,EntityPlayer player,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ)
		{
			return XEnergyUnitUse(world,pos,state,player.getHeldItem(hand),facing,hitX,hitY,hitZ);
		}
	}

	//========X-Dust-Block========//
	public static class XDustBlock extends XCraftBlockCommon
	{
		public XDustBlock()
		{
			super(Material.SAND,"x_dust_block",SoundType.CLOTH);
			this.setHarvestLevel("shovel",2);
			this.setHardness(4.096F);
			this.setResistance(24F);
			this.setLightLevel(0);
			this.setLightOpacity(255);
		}

		@SideOnly(Side.CLIENT)
		@Override
		public BlockRenderLayer getBlockLayer()
		{
			return BlockRenderLayer.SOLID;
		}
		
		//Copy From BlockFalling
		public static boolean fallInstantly;
		
		public void onBlockAdded(World worldIn,BlockPos pos,IBlockState state)
		{
			worldIn.scheduleUpdate(pos,this,this.tickRate(worldIn));
		}
		
		public void neighborChanged(IBlockState state,World worldIn,BlockPos pos,Block blockIn,BlockPos fromPos)
		{
			worldIn.scheduleUpdate(pos,this,this.tickRate(worldIn));
		}

		public void updateTick(World worldIn,BlockPos pos,IBlockState state,Random rand)
		{
			 if (!worldIn.isRemote)
			{
				this.checkFallable(worldIn,pos);
			}
		}

		private void checkFallable(World worldIn,BlockPos pos)
		{
			if ((worldIn.isAirBlock(pos.down()) || canFallThrough(worldIn.getBlockState(pos.down()))) && pos.getY() >= 0)
			{
				if (!fallInstantly && worldIn.isAreaLoaded(pos.add(-32,-32,-32),pos.add(32,32,32)))
				{
					if (!worldIn.isRemote)
					{
						EntityFallingBlock entityfallingblock=new EntityFallingBlock(worldIn,(double)pos.getX() + 0.5D,(double)pos.getY(),(double)pos.getZ() + 0.5D,worldIn.getBlockState(pos));
						this.onStartFalling(entityfallingblock);
						worldIn.spawnEntity(entityfallingblock);
					}
				}
				else
				{
					IBlockState state=worldIn.getBlockState(pos);
					worldIn.setBlockToAir(pos);
					BlockPos blockpos;

					for (blockpos=pos.down();
					     (worldIn.isAirBlock(blockpos) || canFallThrough(worldIn.getBlockState(blockpos))) && blockpos.getY() > 0;
					     blockpos=blockpos.down())
					{
						
					}

					if (blockpos.getY() > 0)
					{
						worldIn.setBlockState(blockpos.up(),state); //Forge: Fix loss of state information during world gen.
					}
				}
			}
		}

		protected void onStartFalling(EntityFallingBlock fallingEntity)
		{
		}
		
		public int tickRate(World worldIn)
		{
			return 2;
		}

		public static boolean canFallThrough(IBlockState state)
		{
			Block block=state.getBlock();
			Material material=state.getMaterial();
			return block == Blocks.FIRE || material == Material.AIR || material == Material.WATER || material == Material.LAVA;
		}

		public void onEndFalling(World worldIn,BlockPos pos,IBlockState p_176502_3_,IBlockState p_176502_4_)
		{
		}

		public void onBroken(World worldIn,BlockPos pos)
		{
		}
	}
	
	//========X-Ore-Common========//
	public static class XOreCommon extends XCraftBlockCommon
	{
		public XOreCommon(String name,int harvestLevel,float multiHardness)
		{
			super(Material.ROCK,name,SoundType.STONE);
			setHarvestLevel("pickaxe",harvestLevel);
			setLightLevel(0F);
			setLightOpacity(255);
			setHardness(multiHardness*5.12F);
			setResistance(multiHardness*32F);
		}

		@SideOnly(Side.CLIENT)
		@Override
		
		public BlockRenderLayer getBlockLayer()
		{
			return BlockRenderLayer.CUTOUT;
		}
	}
	
	//========X-Ore========//
	public static class XOre extends XOreCommon
	{
		public XOre()
		{
			super("x_ore",XuphoriumCraft.HARVEST_LEVEL_DIAMOND_PICKAXE,1);
		}
		
		public int getExpDrop(IBlockState state,IBlockAccess world,BlockPos pos,int fortune)
		{
			return Math.max(fortune,0);
		}
	}
	
	//========X-Ore-Emerald========//
	public static class XOreEmerald extends XOreCommon
	{
		public XOreEmerald()
		{
			super("x_ore_emerald",XuphoriumCraft.HARVEST_LEVEL_IRON_PICKAXE,0.75F);
		}

		@Override
		public void getDrops(NonNullList<ItemStack> drops,IBlockAccess world,BlockPos pos,IBlockState state,int fortune)
		{
			int randInt=(world instanceof World ? ((World)world).rand : RANDOM).nextInt(100);
			Item itemMain=XCraftMaterials.X_EMERALD;
			if(randInt==0) itemMain=XCraftMaterials.X_DIAMOND;
			else if(randInt==1) itemMain=XCraftMaterials.X_RUBY;
			else if(randInt==2) itemMain=XCraftMaterials.X_CRYSTAL_CORE;
			drops.add(new ItemStack(itemMain,1));
			drops.add(new ItemStack(Blocks.COBBLESTONE,1));
		}
		
		public int getExpDrop(IBlockState state,IBlockAccess world,BlockPos pos,int fortune)
		{
			Random rand=world instanceof World?((World)world).rand:new Random();
			return MathHelper.getInt(rand, 0, fortune+2);
		}
	}
	
	//========X-Ore-Nether========//
	public static class XOreNether extends XOreCommon
	{
		public XOreNether()
		{
			super("x_ore_nether",XuphoriumCraft.HARVEST_LEVEL_IRON_PICKAXE,0.75F);
		}
		
		@Override
		public void getDrops(NonNullList<ItemStack> drops,IBlockAccess world,BlockPos pos,IBlockState state,int fortune)
		{
			drops.add(new ItemStack(XCraftMaterials.X_DUST,1));
			drops.add(new ItemStack(Blocks.NETHERRACK,1));
		}
	}
	
	//========X-Ore-End========//
	public static class XOreEnd extends XOreCommon
	{
		public XOreEnd()
		{
			super("x_ore_end",XuphoriumCraft.HARVEST_LEVEL_X_PICKAXE,4);
		}

		@SideOnly(Side.CLIENT)
		
		@Override
		public void getDrops(NonNullList<ItemStack> drops,IBlockAccess world,BlockPos pos,IBlockState state,int fortune)
		{
			drops.add(new ItemStack(XCraftMaterials.X_PEARL,1));
			drops.add(new ItemStack(Blocks.END_STONE,1));
		}
/*
		@Override
		public boolean removedByPlayer(IBlockState state,World world,BlockPos pos,EntityPlayer entity,boolean willHarvest)
		{
			boolean retval=super.removedByPlayer(state,world,pos,entity,willHarvest);
			int x=pos.getX();
			int y=pos.getY();
			int z=pos.getZ();
			return retval;
		}*/
	}
	
	//========X-Ore-Bedrock========//
	public static class XOreBedrock extends XOreCommon
	{
		public XOreBedrock()
		{
			super("x_ore_bedrock",XuphoriumCraft.HARVEST_LEVEL_IRON_PICKAXE,3);
			setResistance(1);
		}
		
		@SideOnly(Side.CLIENT)
		
		@Override
		public void getDrops(NonNullList<ItemStack> drops,IBlockAccess world,BlockPos pos,IBlockState state,int fortune)
		{
			drops.add(new ItemStack(XCraftMaterials.X_DUST,1));
		}
		
		/**
		 * Called when this Block is destroyed by an Explosion
		 */
		public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn)
		{
			if (!worldIn.isRemote)
			{
				this.explode(worldIn,pos,null);
				this.returnToBedRock(worldIn,pos);
			}
		}
		
		/**
		 * Called after a player destroys this Block - the posiiton pos may no longer hold the state indicated.
		 */
		public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state)
		{
			this.returnToBedRock(worldIn, pos);
		}
		
		public void explode(World worldIn, BlockPos pos, EntityLivingBase igniter)
		{
			if(!worldIn.isRemote) worldIn.newExplosion(igniter,pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,2,true,false);
		}
		
		public void returnToBedRock(World worldIn, BlockPos pos)
		{
			if(!worldIn.isRemote) worldIn.setBlockState(pos,Blocks.BEDROCK.getDefaultState());
		}
	}
	
	//========X-Path========//
	public static class XPath extends XCraftBlockCommon
	{
		protected static final AxisAlignedBB X_PATH_AABB=new AxisAlignedBB(0.0D,0.0D,0.0D,1.0D,1.0D,1.0D);//0.9375 or it dosn't work
		
		protected static final float SLIPPERINESS_FOR_ENTITY_LIVING_BASE=1.099F;
		protected static final float SLIPPERINESS_FOR_ENTITY_ITEM=1.0205F;
		protected static final float SLIPPERINESS_FOR_ENTITY_FISH_HOOK=1.0870F;
		
		public XPath()
		{
			super(Material.IRON,"x_path",SoundType.METAL);
			this.setHarvestLevel("pickaxe",2);
			this.setHardness(2.56F);
			this.setResistance(32F);
			this.setLightLevel(0.0625F);
			this.setLightOpacity(255);
			this.setDefaultSlipperiness(1.1F);
		}
		
		public boolean isOpaqueCube(IBlockState state)
		{
			return false;
		}

		public boolean isFullCube(IBlockState state)
		{
			return false;
		}

		public AxisAlignedBB getBoundingBox(IBlockState state,IBlockAccess source,BlockPos pos)
		{
			return X_PATH_AABB;
		}
/*
		@Override
		public int getWeakPower(IBlockState state,IBlockAccess baccess,BlockPos pos,EnumFacing side)
		{
			return 0;
		}

		@Override
		public int getStrongPower(IBlockState state,IBlockAccess baccess,BlockPos pos,EnumFacing side)
		{
			return 0;
		}

		@Override
		public boolean canConnectRedstone(IBlockState state,IBlockAccess world,BlockPos pos,EnumFacing side)
		{
			return true;
		}
		*/
		public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn,IBlockState state,BlockPos pos,EnumFacing face)
		{
			return face==EnumFacing.DOWN?BlockFaceShape.SOLID:BlockFaceShape.UNDEFINED;
		}
		
		public float getSlipperiness(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable Entity entity)
		{
			if(entity instanceof EntityLivingBase) return SLIPPERINESS_FOR_ENTITY_LIVING_BASE;
			if(entity instanceof EntityItem) return SLIPPERINESS_FOR_ENTITY_ITEM;
			if(entity instanceof EntityFishHook) return SLIPPERINESS_FOR_ENTITY_FISH_HOOK;
			return slipperiness;
		}
	}
	
	//========X-Reactive-Core========//
	public static class XReactiveCore extends XCraftBlockCommon
	{
		public XReactiveCore()
		{
			super(Material.IRON,"x_reactive_core",SoundType.METAL);
			this.setHarvestLevel("pickaxe",5);
			this.setHardness(7.68F);
			this.setResistance(256F);
			this.setLightLevel(0.5F);
			this.setLightOpacity(0);
		}

		@SideOnly(Side.CLIENT)
		@Override
		public BlockRenderLayer getBlockLayer()
		{
			return BlockRenderLayer.SOLID;
		}
		
		@Override
		public int getWeakPower(IBlockState state,IBlockAccess baccess,BlockPos pos,EnumFacing side)
		{
			return 0;
		}

		@Override
		public int getStrongPower(IBlockState state,IBlockAccess baccess,BlockPos pos,EnumFacing side)
		{
			return 0;
		}

		@Override
		public boolean canConnectRedstone(IBlockState state,IBlockAccess world,BlockPos pos,EnumFacing side)
		{
			return true;
		}
		
		@Override
		public void neighborChanged(IBlockState state,World world,BlockPos pos,Block neighborBlock,BlockPos fromPos)
		{
			super.neighborChanged(state,world,pos,neighborBlock,fromPos);
			int power=world.isBlockIndirectlyGettingPowered(pos);
			if(power>0) reactive(world,pos);
		}
		
		public void reactive(World world,BlockPos pos)
		{
			//List<XCraftReactions.ReactionItem> reactiveItems=getUpsideItems(world,pos);
			XCraftReactions.ReactionContainer container=XCraftReactions.detectContainer(world,pos);
			if(container!=null/*&&!reactiveItems.isEmpty()*/)
			{
				XuphoriumCraft.LOGGER.info("has "+container.getName()+" in "+pos.toString());
				/*List<XCraftReactions.ReactionItem> newItems=XCraftReactions.reactiveAndReturnNew(reactiveItems,container);
				//Change Old Item
				ItemStack changedItem;
				if(!reactiveItems.isEmpty())
				{
					for(XCraftReactions.ReactionItem item : reactiveItems)
					{
						if(item.source!=null)
						{
							if(item.count<=0) item.source.setDead();
							else if(item.count!=item.source.getCount())
							{
								changedItem=item.source.getItem().copy();
								changedItem.setCount(item.count);
								item.source.setItem(changedItem);
							}
						}
					}
				}
				if(!newItems.isEmpty())
				{
					respawnItems();
				}*/
			}
		}
		
		public void respawnItems(World world,BlockPos pos,XCraftReactions.ReactionItemContainer items)
		{
			Item item;
			int count,spawnCount,limit;
			EntityItem spawnItem;
			if(items==null||items.isEmpty()) return;
			for(int i=0;i<items.typeCount();i++)
			{
				item=items.getItemByIndex(i);
				count=items.getCountByIndex(i);
				if(item!=null&&count>0)
				{
					limit=item.getItemStackLimit();
					for(;count>0;count-=limit)
					{
						spawnCount=Math.min(limit,count);
						spawnItem=new EntityItem(world,pos.getX()+0.5,pos.getY(),pos.getZ()+0.5,new ItemStack(item,spawnCount));
						world.spawnEntity(spawnItem);
					}
				}
			}
		}
		
		public void setDeadItems(List<EntityItem> items)
		{
			for(EntityItem item : items)
			{
				item.setDead();
			}
		}
		
		public List<XCraftReactions.ReactionItem> getUpsideItems(World world,BlockPos pos)
		{
			List<Entity> entities=world.loadedEntityList;
			List<XCraftReactions.ReactionItem> result=new ArrayList<XCraftReactions.ReactionItem>();
			for(Entity entity : entities)
			{
				if(entity instanceof EntityItem)
				{
					EntityItem itemE=(EntityItem)entity;
					double dx=pos.getX()+0.5-itemE.posX;
					double dy=pos.getY()+0.5-itemE.posY;
					double dz=pos.getZ()+0.5-itemE.posZ;
					if(dx*dx+dy*dy+dz*dz<=1)
					{
						result.add(XCraftReactions.ReactionItem.fromEntityItem(itemE));
					}
				}
			}
			return result;
		}
		
		public List<EntityItem> getUpsideEntityItems(World world,BlockPos pos)
		{
			List<Entity> entities=world.loadedEntityList;
			List<EntityItem> result=new ArrayList<EntityItem>();
			for(Entity entity : entities)
			{
				if(entity instanceof EntityItem)
				{
					EntityItem itemE=(EntityItem)entity;
					double dx=pos.getX()+0.5-itemE.posX;
					double dy=pos.getY()+0.5-itemE.posY;
					double dz=pos.getZ()+0.5-itemE.posZ;
					if(dx*dx+dy*dy+dz*dz<=1)
					{
						result.add(itemE);
					}
				}
			}
			return result;
		}
	}
	
	//========X-Explosive========//
	public static class XExplosive extends XCraftBlockCommon
	{
		public XExplosive()
		{
			super(Material.IRON,"x_explosive",SoundType.METAL);
			this.setHarvestLevel("pickaxe",6);
			this.setHardness(2.56F);
			this.setResistance(16F);
			this.setLightLevel(8F);
			this.setLightOpacity(0);
		}

		@SideOnly(Side.CLIENT)
		@Override
		public BlockRenderLayer getBlockLayer()
		{
			return BlockRenderLayer.SOLID;
		}
		
		@Override
		public int getWeakPower(IBlockState state,IBlockAccess baccess,BlockPos pos,EnumFacing side)
		{
			return 0;
		}

		@Override
		public int getStrongPower(IBlockState state,IBlockAccess baccess,BlockPos pos,EnumFacing side)
		{
			return 0;
		}

		@Override
		public boolean canConnectRedstone(IBlockState state,IBlockAccess world,BlockPos pos,EnumFacing side)
		{
			return true;
		}
		
		@Override
		public void neighborChanged(IBlockState state,World world,BlockPos pos,Block neighborBlock,BlockPos fromPos)
		{
			super.neighborChanged(state,world,pos,neighborBlock,fromPos);
			int power=world.isBlockIndirectlyGettingPowered(pos);
			if(power>0) XExplosiveExplode(world,pos,(int)Math.ceil(power/2),true);
		}
		
		public static void XExplosiveExplode(World world,BlockPos pos,int range,boolean limited)
		{
			XExplosiveExplode(world,pos,range,limited,false);
		}
		
		public static void XExplosiveExplode(World world,BlockPos pos,int range,boolean limited,boolean spawnParticle)
		{
			world.setBlockToAir(pos);
			
			for(int dx=-range;dx<=range;dx++)
			{
				for(int dy=-range;dy<=range;dy++)
				{
					for(int dz=-range;dz<=range;dz++)
					{
						if(limited&&Math.abs(dx)+Math.abs(dy)+Math.abs(dz)>range) continue;
						XuphoriumCraft.blockReaction(new BlockPos(pos.getX()+dx,pos.getY()+dy,pos.getZ()+dz),world,spawnParticle);
					}
				}
			}
		}
	}
	
	//========X-Curer========//
	public static class XCurer extends XCraftBlockCommon
	{
		public XCurer()
		{
			super(Material.IRON,"x_curer",SoundType.METAL);
			this.setHarvestLevel("pickaxe",6);
			this.setHardness(5.12F);
			this.setResistance(256F);
			this.setLightLevel(4F);
			this.setLightOpacity(255);
		}

		@SideOnly(Side.CLIENT)
		@Override
		public BlockRenderLayer getBlockLayer()
		{
			return BlockRenderLayer.SOLID;
		}
		
		@Override
		public int getWeakPower(IBlockState state,IBlockAccess baccess,BlockPos pos,EnumFacing side)
		{
			return 0;
		}

		@Override
		public int getStrongPower(IBlockState state,IBlockAccess baccess,BlockPos pos,EnumFacing side)
		{
			return 0;
		}

		@Override
		public boolean canConnectRedstone(IBlockState state,IBlockAccess world,BlockPos pos,EnumFacing side)
		{
			return true;
		}
		
		@Override
		public void neighborChanged(IBlockState state,World world,BlockPos pos,Block neighborBlock,BlockPos fromPos)
		{
			super.neighborChanged(state,world,pos,neighborBlock,fromPos);
			int power=world.isBlockIndirectlyGettingPowered(pos);
			if(power>0) cure(world,pos,power);
		}
		
		protected void cure(World world,BlockPos pos,int power)
		{
			List<Entity> entities=world.loadedEntityList.subList(0,world.loadedEntityList.size());
			for(Entity entity : entities)
			{
				if(entity instanceof EntityItem)
				{
					EntityItem itemE=(EntityItem)entity;
					double dx=pos.getX()+0.5-itemE.posX;
					double dy=pos.getY()+0.5-itemE.posY;
					double dz=pos.getZ()+0.5-itemE.posZ;
					if(dx*dx+dy*dy+dz*dz<=power*power)
					{
						Item item=itemE.getItem().getItem();//EntityItem -> ItemStack -> ?
						int metaData=itemE.getItem().getMetadata();
						BlockPos blockPos=new BlockPos((int)Math.round(itemE.posX-0.5),(int)Math.round(itemE.posY-0.5),(int)Math.round(itemE.posZ-0.5));
						if(item instanceof ItemBlock)
						{
							ItemBlock itemBlock=(ItemBlock)item;
							Block block=itemBlock.getBlock();
							if(XCraftBlocks.getIsReplaceable(world,blockPos))
							{
								world.setBlockState(blockPos,block.getStateFromMeta(metaData),2);
								//Error NPE:block.getStateForPlacement(world,blockPos,EnumFacing.DOWN,(float)itemE.posX,(float)itemE.posY,(float)itemE.posZ,metaData,null,EnumHand.MAIN_HAND)
								itemE.getItem().shrink(1);
								itemE.setItem(itemE.getItem());//Update
							}
						}
					}
				}
			}
		}
	}
	
	//========X-Dust-Generater========//
	public static class ItemXDustGenerater extends XCraftItemBlockCommon
	{
		public ItemXDustGenerater()
		{
			super(X_DUST_GENERATER,true);
		}
		
		public int getMetadata(int damage)
		{
			return damage;
		}
	}
	
	public static class XDustGenerater extends XCraftBlockCommon
	{
		public static final PropertyInteger LEVEL=PropertyInteger.create("level",0,3);
		
		public XDustGenerater()
		{
			super(Material.IRON,"x_dust_generater",SoundType.METAL);
			this.setHarvestLevel("pickaxe",1);
			this.setHardness(1.28F);
			this.setResistance(64F);
			this.setLightLevel(0F);
			this.setLightOpacity(0);
			this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL,0));
			this.setTickRandomly(true);
		}

		@SideOnly(Side.CLIENT)
		@Override
		public BlockRenderLayer getBlockLayer()
		{
			return BlockRenderLayer.TRANSLUCENT;
		}
		
		public boolean isOpaqueCube(IBlockState state)
		{
			return false;
		}

		public boolean isFullCube(IBlockState state)
		{
			return false;
		}

		protected BlockStateContainer createBlockState()
		{
			return new BlockStateContainer(this,new IProperty[]{LEVEL});
		}
		
		public int getMetaFromState(IBlockState state)
		{
			return state.getValue(LEVEL).intValue();
		}
		
		public IBlockState getStateFromMeta(int meta)
		{
			return this.getDefaultState().withProperty(LEVEL,meta);
		}

		public ItemStack getItem(World worldIn,BlockPos pos,IBlockState state)
		{
			return new ItemStack(this,1,state.getBlock().getMetaFromState(state));
		}
		
		public boolean onBlockActivated(World world,BlockPos pos,IBlockState state,EntityPlayer playerIn,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ)
		{
			if(world.isRemote||!world.isAreaLoaded(pos,1)) return true;// Forge: prevent loading unloaded chunks when checking neighbor's light
			if((int)state.getValue(LEVEL)>2)
			{
				world.setBlockState(pos,this.getDefaultState(),2);
				EntityItem entityToSpawn=new EntityItem(world,pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,new ItemStack(XCraftMaterials.X_DUST));
				entityToSpawn.setPickupDelay(10);
				world.spawnEntity(entityToSpawn);
				return true;
			}
			
			return false;
		}
		
		public int damageDropped(IBlockState state)
		{
			return (int)state.getValue(LEVEL);
		}
/*
		@Override
		public void getDrops(NonNullList<ItemStack> drops,IBlockAccess world,BlockPos pos,IBlockState state,int fortune)
		{
			drops.add(this.getItem(world,pos,state));
		}
		*/
		/*@Override
		public int getWeakPower(IBlockState state,IBlockAccess baccess,BlockPos pos,EnumFacing side)
		{
			return 0;
		}

		@Override
		public int getStrongPower(IBlockState state,IBlockAccess baccess,BlockPos pos,EnumFacing side)
		{
			return 0;
		}

		@Override
		public boolean canConnectRedstone(IBlockState state,IBlockAccess world,BlockPos pos,EnumFacing side)
		{
			return true;
		}
		
		@Override
		public void neighborChanged(IBlockState state,World world,BlockPos pos,Block neighborBlock,BlockPos fromPos)
		{
			if(world.isRemote||!world.isAreaLoaded(pos,1)) return;// Forge: prevent loading unloaded chunks when checking neighbor's light
			super.neighborChanged(state,world,pos,neighborBlock,fromPos);
			int power=world.isBlockIndirectlyGettingPowered(pos);
			if(Math.random()*1000>1000-power*power)
			{
				if(state.getBlock()==X_DUST_GENERATER)
				{
					int level=(int)state.getValue(LEVEL);
					if(level<3) grow(world,pos,level);
				}
			}
		}*/

		@Override
		public void updateTick(World world,BlockPos pos,IBlockState state,Random random)
		{
			if(world.isRemote||!world.isAreaLoaded(pos,1)) return;// Forge: prevent loading unloaded chunks when checking neighbor's light
			super.updateTick(world,pos,state,random);
			int level=(int)state.getValue(LEVEL);
			if(level<3&&Math.random()*level<1) grow(world,pos,level);
		}
		
		protected void grow(World world,BlockPos pos,int level)
		{
			world.setBlockState(pos,this.getDefaultState().withProperty(LEVEL,level+1),3);
		}
	}
	
	//========X-Liquid========//
	public static class ItemXLiquid extends XCraftItemBlockCommon
	{
		public ItemXLiquid()
		{
			super(X_LIQUID,true);
		}
		
		public int getMetadata(int damage)
		{
			return damage;
		}
	}

	public static class XLiquid extends XCraftBlockCommon
	{
		public static final PropertyInteger LEVEL=PropertyInteger.create("level",0,15);
		protected static final AxisAlignedBB[] LIQUID_AABB=new AxisAlignedBB[]
		{
			new AxisAlignedBB(0.0D,0.0D,0.0D,1.0D,0.0625D,1.0D),
			new AxisAlignedBB(0.0D,0.0D,0.0D,1.0D,0.125D,1.0D),
			new AxisAlignedBB(0.0D,0.0D,0.0D,1.0D,0.1875D,1.0D),
			new AxisAlignedBB(0.0D,0.0D,0.0D,1.0D,0.25D,1.0D),
			new AxisAlignedBB(0.0D,0.0D,0.0D,1.0D,0.3125D,1.0D),
			new AxisAlignedBB(0.0D,0.0D,0.0D,1.0D,0.375D,1.0D),
			new AxisAlignedBB(0.0D,0.0D,0.0D,1.0D,0.4375D,1.0D),
			new AxisAlignedBB(0.0D,0.0D,0.0D,1.0D,0.5D,1.0D),
			new AxisAlignedBB(0.0D,0.0D,0.0D,1.0D,0.5625D,1.0D),
			new AxisAlignedBB(0.0D,0.0D,0.0D,1.0D,0.625D,1.0D),
			new AxisAlignedBB(0.0D,0.0D,0.0D,1.0D,0.6875D,1.0D),
			new AxisAlignedBB(0.0D,0.0D,0.0D,1.0D,0.75D,1.0D),
			new AxisAlignedBB(0.0D,0.0D,0.0D,1.0D,0.8125D,1.0D),
			new AxisAlignedBB(0.0D,0.0D,0.0D,1.0D,0.875D,1.0D),
			new AxisAlignedBB(0.0D,0.0D,0.0D,1.0D,0.9375D,1.0D),
			new AxisAlignedBB(0.0D,0.0D,0.0D,1.0D,1.0D,1.0D)
		};
		
		public XLiquid()
		{
			super(Material.ROCK,"x_liquid",SoundType.STONE);
			this.setHarvestLevel("pickaxe",7);
			this.setHardness(51.2F);
			this.setResistance(512F);
			this.setLightLevel(0.125F);
			this.setLightOpacity(0);
			this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL,Integer.valueOf(0)));
		}
		
		public boolean isOpaqueCube(IBlockState state)
		{
			return false;
		}

		public boolean isFullCube(IBlockState state)
		{
			return false;
		}
		
		@Override
		public boolean isPassable(@Nonnull IBlockAccess world,@Nonnull BlockPos pos)
		{
			return true;
		}
		
		public int getMetaFromState(IBlockState state)
		{
			return ((Integer)state.getValue(LEVEL)).intValue();
		}
		
		public IBlockState getStateFromMeta(int meta)
		{
			return this.getDefaultState().withProperty(LEVEL,meta);
		}

		public ItemStack getItem(World worldIn,BlockPos pos,IBlockState state)
		{
			return new ItemStack(this,1,state.getBlock().getMetaFromState(state));
		}
		
		public int damageDropped(IBlockState state)
		{
			return (int)state.getValue(LEVEL);
		}
		
		public void getSubBlocks(CreativeTabs itemIn,NonNullList<ItemStack> items)
		{
			for (int i=0;i<16;i++) items.add(new ItemStack(this,1,i));
		}

		protected BlockStateContainer createBlockState()
		{
			return new BlockStateContainer(this,new IProperty[]{LEVEL});
		}

		public AxisAlignedBB getBoundingBox(IBlockState state,IBlockAccess source,BlockPos pos)
		{
			return LIQUID_AABB[((Integer)state.getValue(LEVEL)).intValue()];
		}

		@SideOnly(Side.CLIENT)
		@Override
		public BlockRenderLayer getBlockLayer()
		{
			return BlockRenderLayer.TRANSLUCENT;//CUTOUT
		}

		@Override
		public void getDrops(NonNullList<ItemStack> drops,IBlockAccess world,BlockPos pos,IBlockState state,int fortune)
		{
			int level=(int)state.getValue(LEVEL);
			for(int i=0;i<level+1;i++) drops.add(new ItemStack(XCraftMaterials.X_DUST,1));
		}
		
		public void onEntityCollidedWithBlock(World worldIn,BlockPos pos,IBlockState state,Entity entityIn)
		{
			entityIn.motionX*=0.5D;
			entityIn.motionZ*=0.5D;
		}
		
		@Override
		public int tickRate(@Nonnull World world)
		{
			return 16;
		}

		@Override
		public void onBlockAdded(World world,BlockPos pos,IBlockState state)
		{
			super.onBlockAdded(world,pos,state);
			world.scheduleUpdate(pos,this,this.tickRate(world));
		}
		
		@Override
		public void neighborChanged(@Nonnull IBlockState state,@Nonnull World world,@Nonnull BlockPos pos,@Nonnull Block neighborBlock,@Nonnull BlockPos neighbourPos)
		{
			world.scheduleUpdate(pos,this,this.tickRate(world));
		}

		@Override
		public void updateTick(World world,BlockPos pos,IBlockState state,Random random)
		{
			super.updateTick(world,pos,state,random);
			flow(world,pos);
			world.scheduleUpdate(pos,this,this.tickRate(world));
		}
		
		protected void flow(World world,BlockPos pos)
		{
			if(world.isRemote||!world.isAreaLoaded(pos,1)) return;// Forge: prevent loading unloaded chunks when checking neighbor's light
			IBlockState state=world.getBlockState(pos);
			int level=(int)state.getValue(LEVEL);
			int sumL;
			BlockPos downPos=new BlockPos(pos.getX(),pos.getY()-1,pos.getZ());
			IBlockState downState=world.getBlockState(downPos);
			int downLevel;
			ArrayList<BlockPos> sidePos=new ArrayList<BlockPos>();
			BlockPos p;
			//Flow Down
			if(getIsReplaceable(world,downPos))
			{
				world.setBlockState(downPos,state,2);
				world.setBlockToAir(pos);
				return;
			}
			else if(getIsAnotherLiquid(downState))
			{
				downLevel=(int)downState.getValue(LEVEL);
				if(overrideLevel(world,pos,level,downPos,downLevel)>0) return;
			}
			//Flow Side
			for(int xd=-1;xd<2;xd++)
			{
				for(int zd=-1;zd<2;zd++)
				{
					if(Math.abs(xd)+Math.abs(zd)==1)
					{
						p=new BlockPos(pos.getX()+xd,pos.getY(),pos.getZ()+zd);
						if(getIsReplaceable(world,p)||getIsAnotherLiquid(world,p))
						{
							level-=spreadLevel(world,pos,p,level,0);//returns:1~16!!!
						}
					}
				}
			}
			world.setBlockState(pos,this.getDefaultState().withProperty(LEVEL,level),2);
		}
		
		//Override(levels:0~15!!!)
		protected int overrideLevel(World world,BlockPos pos,int level,BlockPos targetPos,int targetLevel)
		{
			if(targetLevel<15)//[(<0~15>a+1)+(<0~15>b+1)]-1=<0~15>(a+b+1)
			{
				int sumL=level+targetLevel+1;
				if(sumL<16)
				{
					world.setBlockState(targetPos,this.getDefaultState().withProperty(LEVEL,sumL),2);
					world.setBlockToAir(pos);
					return level+1;
				}
				else
				{
					world.setBlockState(targetPos,this.getDefaultState().withProperty(LEVEL,15),2);
					if(sumL-16<0) world.setBlockToAir(pos);
					else world.setBlockState(pos,this.getDefaultState().withProperty(LEVEL,sumL-16),2);
					return sumL-15;
				}
			}
			return 0;
		}
		
		//Spread(levels:0~15!!!)
		protected int spreadLevel(World world,BlockPos pos,BlockPos targetPos,int level,int maxLevel)
		{
			if(level<=0) return 0;
			IBlockState targetState=world.getBlockState(targetPos);
			int targetLevel=-1;
			if(getIsAnotherLiquid(targetState)) targetLevel=targetState.getValue(LEVEL);
			if(level>maxLevel&&targetLevel+maxLevel+1<level)
			{
				if(targetLevel>=0) return addLevel(world,maxLevel,targetPos,targetLevel);
				world.setBlockState(targetPos,this.getDefaultState().withProperty(LEVEL,maxLevel),2);
				return maxLevel+1;
			}
			return 0;
		}
		
		//Add(levels:0~15!!!)
		protected int addLevel(World world,int levelWillAdd,BlockPos targetPos,int targetLevel)
		{
			if(targetLevel<15)//[(<0~15>a+1)+(<0~15>b+1)]-1=<0~15>(a+b+1)
			{
				int sumL=levelWillAdd+targetLevel+1;
				if(sumL<16)
				{
					world.setBlockState(targetPos,this.getDefaultState().withProperty(LEVEL,sumL),2);
					return levelWillAdd+1;
				}
				else
				{
					world.setBlockState(targetPos,this.getDefaultState().withProperty(LEVEL,15),2);
					return sumL-14;
				}
			}
			return 0;
		}
		
		protected boolean getIsAnotherLiquid(World world,BlockPos pos)
		{
			return getIsAnotherLiquid(world.getBlockState(pos));
		}
		
		protected boolean getIsAnotherLiquid(IBlockState state)
		{
			return state.getBlock()==X_LIQUID;
		}
	}
	
	//========X-Glass========//
	public static class XGlass extends XCraftBlockCommon
	{
		public XGlass()
		{
			super(Material.GLASS,"x_glass",SoundType.GLASS);
			this.setHarvestLevel("pickaxe",3);
			this.setHardness(2.56F);
			this.setResistance(56F);
			this.setLightLevel(0);
			this.setLightOpacity(0);
		}

		@SideOnly(Side.CLIENT)
		@Override
		public BlockRenderLayer getBlockLayer()
		{
			return BlockRenderLayer.CUTOUT;
		}

		public boolean isFullCube(IBlockState state)
		{
			return false;
		}
		
		public boolean isOpaqueCube(IBlockState state)
		{
			return false;
		}
		
		@SideOnly(Side.CLIENT)
		public boolean shouldSideBeRendered(IBlockState blockState,IBlockAccess blockAccess,BlockPos pos,EnumFacing side)
		{
			IBlockState iblockstate=blockAccess.getBlockState(pos.offset(side));
			Block block=iblockstate.getBlock();

			if (blockState!=iblockstate) return true;
			if (block==this) return false;

			return block==this?false:super.shouldSideBeRendered(blockState,blockAccess,pos,side);
		}/* 

		protected boolean canSilkHarvest()
		{
			return true;
		} */
	}
	
	//========X-Transporter========//
	public static class XTransporter extends XCraftBlockCommon
	{
		protected static final AxisAlignedBB X_TRANSPORTER_AABB=new AxisAlignedBB(0.0D,0.0D,0.0D,1.0D,0.9375D,1.0D);
		
		public static final PropertyDirection FACING=PropertyDirection.create("facing",EnumFacing.Plane.HORIZONTAL);
		public static final PropertyBool ACTIVE=PropertyBool.create("active");
		
		public XTransporter()
		{
			super(Material.IRON,"x_transporter",SoundType.STONE);
			this.setHarvestLevel("pickaxe",2);
			this.setHardness(5.12F);
			this.setResistance(32F);
			this.setLightLevel(0F);
			this.setLightOpacity(255);
			this.setDefaultState(this.blockState.getBaseState().withProperty(ACTIVE,Boolean.valueOf(false)));
		}
		
		public IBlockState withRotation(IBlockState state,Rotation rot)
		{
			return state.withProperty(FACING,rot.rotate((EnumFacing)state.getValue(FACING)));
		}
		
		public IBlockState withMirror(IBlockState state,Mirror mirrorIn)
		{
			return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
		}
		
		public IBlockState getStateForPlacement(World worldIn,BlockPos pos,EnumFacing facing,float hitX,float hitY,float hitZ,int meta,EntityLivingBase placer)
		{
			boolean flag=worldIn.isBlockPowered(pos);
			return this.getDefaultState().withProperty(FACING,placer.getHorizontalFacing()).withProperty(ACTIVE,Boolean.valueOf(flag));
		}
		
		public IBlockState getStateFromMeta(int meta)
		{
			return this.getDefaultState().withProperty(FACING,EnumFacing.getHorizontal(meta)).withProperty(ACTIVE,Boolean.valueOf((meta&4)!=0));
		}
		
		public int getMetaFromState(IBlockState state)
		{
			int i=0;
			i=i|((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
			if(((Boolean)state.getValue(ACTIVE)).booleanValue()) i|=4;
			return i;
		}

		protected BlockStateContainer createBlockState()
		{
			return new BlockStateContainer(this,new IProperty[] {FACING,ACTIVE});
		}

		public AxisAlignedBB getBoundingBox(IBlockState state,IBlockAccess source,BlockPos pos)
		{
			return X_TRANSPORTER_AABB;
		}
		
		public void onEntityCollidedWithBlock(World worldIn,BlockPos pos,IBlockState state,Entity entityIn)
		{
			if(((Boolean)state.getValue(ACTIVE)).booleanValue())
			{
				EnumFacing facing=(EnumFacing)state.getValue(FACING);
				if(facing.getFrontOffsetX()!=0) entityIn.motionX=facing.getFrontOffsetX()*0.2D;
				if(facing.getFrontOffsetZ()!=0) entityIn.motionZ=facing.getFrontOffsetZ()*0.2D;
			}
		}
		
		@Override
		public int getWeakPower(IBlockState state,IBlockAccess baccess,BlockPos pos,EnumFacing side)
		{
			return 0;
		}

		@Override
		public int getStrongPower(IBlockState state,IBlockAccess baccess,BlockPos pos,EnumFacing side)
		{
			return 0;
		}

		@Override
		public boolean canConnectRedstone(IBlockState state,IBlockAccess world,BlockPos pos,EnumFacing side)
		{
			return true;
		}
		
		public void neighborChanged(IBlockState state,World worldIn,BlockPos pos,Block blockIn,BlockPos fromPos)
		{
			if(!worldIn.isRemote)
			{
				boolean flag=worldIn.isBlockPowered(pos);
				if(((Boolean)state.getValue(ACTIVE)).booleanValue()!=flag)
				{
					worldIn.setBlockState(pos,state.withProperty(ACTIVE,Boolean.valueOf(flag)),2);
				}
			}
		}
	}
	
	//========X-Resource-Block-Common========//
	public static class XResourceBlockCommon extends XCraftBlockCommon
	{
		public XResourceBlockCommon(String name)
		{
			this(name,Material.IRON);
		}
		
		public XResourceBlockCommon(String name,Material material)
		{
			super(material,name,SoundType.METAL);
		}

		@SideOnly(Side.CLIENT)
		@Override
		public BlockRenderLayer getBlockLayer()
		{
			return BlockRenderLayer.SOLID;
		}

		@Override
		public boolean isBeaconBase(IBlockAccess worldObj,BlockPos pos,BlockPos beacon)
		{
			return true;
		}
	}
	
	//========X-Diamond-Block========//
	public static class XDiamondBlock extends XResourceBlockCommon
	{
		public XDiamondBlock()
		{
			super("x_diamond_block");
			setHarvestLevel("pickaxe",4);
			setHardness(7.68F);
			setResistance(256F);
			setLightLevel(0.0625F);
			setLightOpacity(63);
		}
	}
	
	//========X-Emerald-Block========//
	public static class XEmeraldBlock extends XResourceBlockCommon
	{
		public XEmeraldBlock()
		{
			super("x_emerald_block");
			setHarvestLevel("pickaxe",4);
			setHardness(7.68F);
			setResistance(256F);
			setLightLevel(0.125F);
			setLightOpacity(127);
		}
	}
	
	//========X-Ruby-Block========//
	public static class XRubyBlock extends XResourceBlockCommon
	{
		public XRubyBlock()
		{
			super("x_ruby_block");
			setHarvestLevel("pickaxe",4);
			setHardness(7.68F);
			setResistance(256F);
			setLightLevel(0.25F);
			setLightOpacity(255);
		}
	}
	
	//========X-Metal-Block========//
	public static class XMetalBlock extends XResourceBlockCommon
	{
		public XMetalBlock()
		{
			super("x_metal_block");
			setHarvestLevel("pickaxe",5);
			setHardness(32F);
			setResistance(512F);
			setLightLevel(0.125F);
			setLightOpacity(255);
		}
		
		@Override
		public void getDrops(NonNullList<ItemStack> drops,IBlockAccess world,BlockPos pos,IBlockState state,int fortune)
		{
			for(int i=0;i<8;i++) drops.add(new ItemStack(XCraftMaterials.X_COVALENT_SHARD,1));
		}
	}
	
	//========X-Covalent-Solid========//
	public static class XCovalentSolid extends XResourceBlockCommon
	{
		public XCovalentSolid()
		{
			super("x_covalent_solid");
			setHardness(51.2F);
			setResistance(1024F);
			setLightLevel(0F);
			setLightOpacity(127);
		}
		
		@Override
		public void getDrops(NonNullList<ItemStack> drops,IBlockAccess world,BlockPos pos,IBlockState state,int fortune)
		{
			for(int i=0;i<8;i++) drops.add(new ItemStack(XCraftMaterials.X_COVALENT_SHARD,1));
		}
	}
	
	//========X-Phase-Fiber-Block========//
	public static class XPhaseFiberBlock extends XResourceBlockCommon
	{
		public XPhaseFiberBlock()
		{
			super("x_phase_fiber_block");
			setHardness(64F);
			setResistance(1024F);
			setLightLevel(0.375F);
			setLightOpacity(0);
		}
		
		@Override
		public void getDrops(NonNullList<ItemStack> drops,IBlockAccess world,BlockPos pos,IBlockState state,int fortune)
		{
			for(int i=0;i<4;i++) drops.add(new ItemStack(XCraftMaterials.X_PHASE_FIBER,1));
		}
	}
	
	//========X-Phase-Web-Block========//
	public static class XPhaseWebBlock extends XResourceBlockCommon
	{
		public XPhaseWebBlock()
		{
			super("x_phase_web_block");
			setHardness(81.92F);
			setResistance(2048F);
			setLightLevel(0.5F);
			setLightOpacity(0);
		}
		
		@Override
		public void getDrops(NonNullList<ItemStack> drops,IBlockAccess world,BlockPos pos,IBlockState state,int fortune)
		{
			for(int i=0;i<4;i++) drops.add(new ItemStack(XCraftMaterials.X_PHASE_WEB,1));
		}
	}
	
	//========X-Crystal-Block========//
	public static class XCrystalBlock extends XResourceBlockCommon
	{
		public XCrystalBlock()
		{
			super("x_crystal_block");
			this.setHarvestLevel("pickaxe",4);
			this.setHardness(8.192F);
			this.setResistance(512F);
			this.setLightLevel(0.25F);
			this.setLightOpacity(0);
			this.setTickRandomly(true);
		}
		
		@Override
		public void getDrops(NonNullList<ItemStack> drops,IBlockAccess world,BlockPos pos,IBlockState state,int fortune)
		{
			for(int i=0;i<8;i++) drops.add(new ItemStack(XCraftMaterials.X_CRYSTAL,1));
		}
		
		public void updateTick(World worldIn,BlockPos pos,IBlockState state,Random rand)
		{
			super.updateTick(worldIn,pos,state,rand);
			
			if(!worldIn.isAreaLoaded(pos,1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
			//Teleport
			int lSum=getNearbyCrystalBlockLevelSum(worldIn,pos);
			if(lSum<2||lSum>3)
			{
				if(this.moveToNearbyVoid(worldIn,pos,state)) worldIn.playSound(null,pos.getX()+0.5,pos.getX()+0.5,pos.getX()+0.5, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.BLOCKS, 0.75F, 1F);
			}
		}
		
		protected int getNearbyCrystalBlockLevelSum(World world,BlockPos pos)
		{
			int result=0;
			IBlockState mb;
			for(int dx=-1;dx<2;dx++)
			{
				for(int dy=-1;dy<2;dy++)
				{
					for(int dz=-1;dz<2;dz++)
					{
						if(Math.abs(dx)+Math.abs(dy)+Math.abs(dz)==1)
						{
							mb=world.getBlockState(new BlockPos(pos.getX()+dx,pos.getY()+dy,pos.getZ()+dz));
							if(mb.getBlock()==X_CRYSTAL_BLOCK)
							{
								result++;
							}
						}
					}
				}
			}
			return result;
		}
		
		protected boolean moveToNearbyVoid(World world,BlockPos pos,IBlockState state)
		{
			ArrayList<BlockPos> currentPos=new ArrayList<BlockPos>();
			BlockPos mp;
			IBlockState mb;
			int dx,dy,dz;
			for(dx=-1;dx<2;dx++)
			{
				for(dy=-1;dy<2;dy++)
				{
					for(dz=-1;dz<2;dz++)
					{
						if(Math.abs(dx)+Math.abs(dy)+Math.abs(dz)==1)
						{
							mp=new BlockPos(pos.getX()+dx,pos.getY()+dy,pos.getZ()+dz);
							mb=world.getBlockState(mp);
							if(mb.getBlock().isReplaceable(world,mp))
							{
								currentPos.add(mp);
							}
						}
					}
				}
			}
			if(currentPos.size()>0)
			{
				mp=currentPos.get((int)Math.floor(Math.random()*currentPos.size()));
				if(!world.isRemote)
				{
					world.setBlockState(mp,state,2);
					world.setBlockToAir(pos);
				}
				return true;
			}
			return false;
		}
	}
}
