package xuphorium_craft;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;

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

import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.Item;

import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.ItemMeshDefinition;

import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.Block;

import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyBool;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;

import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;

import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Rotation;
import net.minecraft.util.Mirror;

import net.minecraft.init.Blocks;

import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.material.Material;
import net.minecraft.block.SoundType;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;

import net.minecraft.creativetab.CreativeTabs;

import java.util.HashMap;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
/* 
import com.xuphorium.xuphorium_craft.*;
import com.xuphorium.xuphorium_craft.common.*;
import com.xuphorium.xuphorium_craft.proxy.*;
import com.xuphorium.xuphorium_craft.entity.*;
import com.xuphorium.xuphorium_craft.block.*;
import com.xuphorium.xuphorium_craft.item.*; */

@XuphoriumCraftElements.ModElement.Tag
public class XCraftBlocks extends XuphoriumCraftElements.ModElement
{
	public static Logger LOGGER;
	
	public static class WorldGenOreBall extends WorldGenerator
	{
		public boolean mustFull=true;
		public IBlockState stone;
		public IBlockState ore;
		public IBlockState center;
		
		public WorldGenOreBall(IBlockState stone,IBlockState ore,boolean mustFull)
		{
			this(stone,ore,ore,mustFull);
		}
		
		public WorldGenOreBall(IBlockState stone,IBlockState ore,IBlockState center,boolean mustFull)
		{
			super();
			this.stone=stone;
			this.ore=ore;
			this.center=center;
			this.mustFull=mustFull;
		}
		
		public WorldGenOreBall(IBlockState stone,IBlockState ore)
		{
			this(stone,ore,true);
		}
		
		public WorldGenOreBall(IBlockState stone,IBlockState ore,IBlockState center)
		{
			this(stone,ore,center,true);
		}
		
		public WorldGenOreBall randomize(Random rand,IBlockState randOre,IBlockState randCenter)
		{
			if(rand.nextInt(10)*rand.nextInt(10)<rand.nextInt(10)+rand.nextInt(10))
			{
				this.ore=randOre;
				this.center=randCenter;
			}
			return this;
		}
		
		public boolean generate(World worldIn,Random rand,BlockPos position)
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
						if(worldIn.getBlockState(currentPos).getBlock()==this.stone.getBlock())
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
			XCraftBlocks.LOGGER.info("Generated X Ores At "+position.toString()+",As "+this.center.getBlock().getUnlocalizedName());
			for(BlockPos pos : succeedPos)
			{
				if(pos.equals(position)) worldIn.setBlockState(position,this.center,2);
				else worldIn.setBlockState(pos,this.ore,2);
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
		
		if(world.getBlockState(currentPos).getBlock()==X_BLOCK.getDefaultState().getBlock()&&
		   world.getBlockState(currentPos2).getBlock()==X_BLOCK_ADVANCED.getDefaultState().getBlock()&&
		   world.getBlockState(currentPos3).getBlock()==Blocks.OBSERVER.getDefaultState().getBlock())
		{
			if(!world.isRemote)
			{
				Entity entityToSpawn=new XBoss.EntityXBoss(world);
				if (entityToSpawn!=null)
				{
					world.setBlockToAir(currentPos);
					world.setBlockToAir(currentPos2);
					world.setBlockToAir(currentPos3);
					world.addWeatherEffect(new EntityLightningBolt(world,x,y,z,false));
					world.createExplosion(null,x,y,z,4,true);
					entityToSpawn.setLocationAndAngles(x+0.5,y,z+0.5,world.rand.nextFloat()*360F,0.0F);
					world.spawnEntity(entityToSpawn);
				}
			}
		}
	}
	
	public static boolean isXOre(Block block)
	{
		return (block==X_ORE||block==X_ORE_NETHER||block==X_ORE_END);
	}
		
	public static boolean getIsReplaceable(World world,BlockPos pos,IBlockState state)
	{
		return state.getBlock().isReplaceable(world,pos);
	}
	
	public static boolean getIsReplaceable(World world,BlockPos pos)
	{
		return world.getBlockState(pos).getBlock().isReplaceable(world,pos);
	}
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_ore")
	public static final Block X_ORE=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_ore_nether")
	public static final Block X_ORE_NETHER=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_ore_end")
	public static final Block X_ORE_END=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_block")
	public static final Block X_BLOCK=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_block_advanced")
	public static final Block X_BLOCK_ADVANCED=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_crystal_block")
	public static final Block X_CRYSTAL_BLOCK=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_dust_block")
	public static final Block X_DUST_BLOCK=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_path")
	public static final Block X_PATH=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_reactive_core")
	public static final Block X_REACTIVE_CORE=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_explosive")
	public static final Block X_EXPLOSIVE=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_curer")
	public static final Block X_CURER=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_dust_generater")
	public static final Block X_DUST_GENERATER=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_liquid")
	public static final Block X_LIQUID=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_glass")
	public static final Block X_GLASS=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_diamond_block")
	public static final Block X_DIAMOND_BLOCK=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_emerald_block")
	public static final Block X_EMERALD_BLOCK=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_ruby_block")
	public static final Block X_RUBY_BLOCK=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_transporter")
	public static final Block X_TRANSPORTER=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_metal_block")
	public static final Block X_METAL_BLOCK=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_covalent_solid")
	public static final Block X_COVALENT_SOLID=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_phase_fiber_block")
	public static final Block X_PHASE_FIBER_BLOCK=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_phase_web_block")
	public static final Block X_PHASE_WEB_BLOCK=null;
	
	//Fluid
	@GameRegistry.ObjectHolder("xuphorium_craft:x_fluid")
	public static final Block X_FLUID=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_fluid")
	public static final Item X_FLUID_ITEM=null;
	
	private Fluid X_FLUID_Fluid;
	
	//Oxygen
	@GameRegistry.ObjectHolder("xuphorium_craft:x_oxygen")
	public static final Block X_OXYGEN=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_oxygen")
	public static final Item X_OXYGEN_ITEM=null;
	
	private Fluid X_OXYGEN_Fluid;
	
	public XCraftBlocks(XuphoriumCraftElements instance)
	{
		super(instance,3);
		X_FLUID_Fluid=new Fluid("x_fluid",
						new ResourceLocation("xuphorium_craft:blocks/x_fluid_still"),
						new ResourceLocation("xuphorium_craft:blocks/x_fluid")
						).setDensity(1024).setViscosity(1024).setGaseous(false);
		X_OXYGEN_Fluid=new Fluid("x_oxygen",
						new ResourceLocation("xuphorium_craft:blocks/x_oxygen"),
						new ResourceLocation("xuphorium_craft:blocks/x_oxygen")
						).setDensity(-768).setViscosity(1024).setGaseous(true);
						
	}

	@Override
	public void initElements()
	{
		//Ore
		elements.blocks.add(()->new XOre());
		elements.items.add(()->new ItemBlock(X_ORE).setRegistryName(X_ORE.getRegistryName()));
		//Ore_Nether
		elements.blocks.add(()->new XOreNether());
		elements.items.add(()->new ItemBlock(X_ORE_NETHER).setRegistryName(X_ORE_NETHER.getRegistryName()));
		//Ore_End
		elements.blocks.add(()->new XOreEnd());
		elements.items.add(()->new ItemBlock(X_ORE_END).setRegistryName(X_ORE_END.getRegistryName()));
		//Block
		elements.blocks.add(()->new XBlock());
		elements.items.add(()->new ItemXBlock());
		//Block_Advanced
		elements.blocks.add(()->new XBlockAdvanced());
		elements.items.add(()->new ItemXBlockAdvanced());
		//Crystal_Block
		elements.blocks.add(()->new XCrystalBlock());
		elements.items.add(()->new ItemXCrystalBlock());
		//Dust_Block
		elements.blocks.add(()->new XDustBlock());
		elements.items.add(()->new ItemBlock(X_DUST_BLOCK).setRegistryName(X_DUST_BLOCK.getRegistryName()));
		//Path
		elements.blocks.add(()->new XPath());
		elements.items.add(()->new ItemBlock(X_PATH).setRegistryName(X_PATH.getRegistryName()));
		//Reactive_Core
		elements.blocks.add(()->new XReactiveCore());
		elements.items.add(()->new ItemBlock(X_REACTIVE_CORE).setRegistryName(X_REACTIVE_CORE.getRegistryName()));
		//Explosive
		elements.blocks.add(()->new XExplosive());
		elements.items.add(()->new ItemBlock(X_EXPLOSIVE).setRegistryName(X_EXPLOSIVE.getRegistryName()));
		//Curer
		elements.blocks.add(()->new XCurer());
		elements.items.add(()->new ItemBlock(X_CURER).setRegistryName(X_CURER.getRegistryName()));
		//Dust_Generater
		elements.blocks.add(()->new XDustGenerater());
		elements.items.add(()->new ItemXDustGenerater());
		//Liquid
		elements.blocks.add(()->new XLiquid());
		elements.items.add(()->new ItemXLiquid());
		//Glass
		elements.blocks.add(()->new XGlass());
		elements.items.add(()->new ItemBlock(X_GLASS).setRegistryName(X_GLASS.getRegistryName()));
		//Diamond_Block
		elements.blocks.add(()->new XDiamondBlock());
		elements.items.add(()->new ItemBlock(X_DIAMOND_BLOCK).setRegistryName(X_DIAMOND_BLOCK.getRegistryName()));
		//Emerald_Block
		elements.blocks.add(()->new XEmeraldBlock());
		elements.items.add(()->new ItemBlock(X_EMERALD_BLOCK).setRegistryName(X_EMERALD_BLOCK.getRegistryName()));
		//Ruby_Block
		elements.blocks.add(()->new XRubyBlock());
		elements.items.add(()->new ItemBlock(X_RUBY_BLOCK).setRegistryName(X_RUBY_BLOCK.getRegistryName()));
		//Transporter
		elements.blocks.add(()->new XTransporter());
		elements.items.add(()->new ItemBlock(X_TRANSPORTER).setRegistryName(X_TRANSPORTER.getRegistryName()));
		//Metal_Block
		elements.blocks.add(()->new XMetalBlock());
		elements.items.add(()->new ItemBlock(X_METAL_BLOCK).setRegistryName(X_METAL_BLOCK.getRegistryName()));
		//Covalent_Solid
		elements.blocks.add(()->new XCovalentSolid());
		elements.items.add(()->new ItemBlock(X_COVALENT_SOLID).setRegistryName(X_COVALENT_SOLID.getRegistryName()));
		//Phase_Fiber_Block
		elements.blocks.add(()->new XPhaseFiberBlock());
		elements.items.add(()->new ItemBlock(X_PHASE_FIBER_BLOCK).setRegistryName(X_PHASE_FIBER_BLOCK.getRegistryName()));
		//Phase_Web_Block
		elements.blocks.add(()->new XPhaseWebBlock());
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
		int i;
		//Ore,OreNether,OreEnd
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_ORE),0,new ModelResourceLocation("xuphorium_craft:x_ore","inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_ORE_NETHER),0,new ModelResourceLocation("xuphorium_craft:x_ore_nether","inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_ORE_END),0,new ModelResourceLocation("xuphorium_craft:x_ore_end","inventory"));
		//Block,BlockAdvanced
		for(i=0;i<16;i++)
		{
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_BLOCK),i,new ModelResourceLocation("xuphorium_craft:x_block_"+i,"inventory"));
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_BLOCK_ADVANCED),i,new ModelResourceLocation("xuphorium_craft:x_block_advanced_"+i,"inventory"));
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_LIQUID),i,new ModelResourceLocation("xuphorium_craft:x_liquid_"+i,"inventory"));
		}
		//CrystalBlock
		for(i=0;i<8;i++) ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_CRYSTAL_BLOCK),i,new ModelResourceLocation("xuphorium_craft:x_crystal_block_"+i,"inventory"));//Path
		//DustBlock
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_DUST_BLOCK),0,new ModelResourceLocation("xuphorium_craft:x_dust_block","inventory"));
		//Path
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_PATH),0,new ModelResourceLocation("xuphorium_craft:x_path","inventory"));
		//ReactiveCore
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_REACTIVE_CORE),0,new ModelResourceLocation("xuphorium_craft:x_reactive_core","inventory"));
		//Explosive
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_EXPLOSIVE),0,new ModelResourceLocation("xuphorium_craft:x_explosive","inventory"));
		//Curer
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_CURER),0,new ModelResourceLocation("xuphorium_craft:x_curer","inventory"));
		//Dust_Generater
		for(i=0;i<4;i++) ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_DUST_GENERATER),i,new ModelResourceLocation("xuphorium_craft:x_dust_generater_"+i,"inventory"));
		//Glass
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_GLASS),0,new ModelResourceLocation("xuphorium_craft:x_glass","inventory"));
		//Diamond_Block
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_DIAMOND_BLOCK),0,new ModelResourceLocation("xuphorium_craft:x_diamond_block","inventory"));
		//Emerald_Block
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_EMERALD_BLOCK),0,new ModelResourceLocation("xuphorium_craft:x_emerald_block","inventory"));
		//Ruby_Block
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_RUBY_BLOCK),0,new ModelResourceLocation("xuphorium_craft:x_ruby_block","inventory"));
		//Metal_Block
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_METAL_BLOCK),0,new ModelResourceLocation("xuphorium_craft:x_metal_block","inventory"));
		//Covalent_Solid
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_COVALENT_SOLID),0,new ModelResourceLocation("xuphorium_craft:x_covalent_solid","inventory"));
		//Phase_Fiber_Block
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_PHASE_FIBER_BLOCK),0,new ModelResourceLocation("xuphorium_craft:x_phase_fiber_block","inventory"));
		//Phase_Web_Block
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_PHASE_WEB_BLOCK),0,new ModelResourceLocation("xuphorium_craft:x_phase_web_block","inventory"));
		//Transporter
		for(i=0;i<7;i++)
		{
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_TRANSPORTER),i,new ModelResourceLocation("xuphorium_craft:x_transporter","inventory"));
		}
		//Fluid
		ModelBakery.registerItemVariants(X_FLUID_ITEM);
		ModelLoader.setCustomMeshDefinition(X_FLUID_ITEM,new ItemMeshDefinition()
		{
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack)
			{
				return new ModelResourceLocation("xuphorium_craft:x_fluid","x_fluid");
			}
		});
		ModelLoader.setCustomStateMapper(X_FLUID,new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return new ModelResourceLocation("xuphorium_craft:x_fluid","x_fluid");
			}
		});
		//Oxygen
		ModelBakery.registerItemVariants(X_OXYGEN_ITEM);
		ModelLoader.setCustomMeshDefinition(X_OXYGEN_ITEM,new ItemMeshDefinition()
		{
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack)
			{
				return new ModelResourceLocation("xuphorium_craft:x_oxygen","x_oxygen");
			}
		});
		ModelLoader.setCustomStateMapper(X_OXYGEN,new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return new ModelResourceLocation("xuphorium_craft:x_oxygen","x_oxygen");
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
		//Logger
		LOGGER=event.getModLog();
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

	//fill ~-20 ~-10 ~-20 ~20 ~10 ~10 air 0 replace stone
	@Override
	public void generateWorld(Random random,int chunkX,int chunkZ,World world,int dimID,IChunkGenerator cg,IChunkProvider cp)
	{
		switch(dimID)
		{
			case 0:
			for(int i=0;i<3;i++)
			{
				int x=chunkX+random.nextInt(14)+1;
				int y=random.nextInt(6)+9;
				int z=chunkZ+random.nextInt(14)+1;
				new WorldGenOreBall(
					Blocks.STONE.getDefaultState(),
					X_ORE.getDefaultState()
					).randomize(random,X_BLOCK.getDefaultState(),X_FLUID.getDefaultState()).generate(world,random,new BlockPos(x,y,z));
				//generateOreBall(world,x,y,z,X_ORE.getDefaultState(),Blocks.STONE.getDefaultState().getBlock());
			}
			break;
			case -1:
			for(int i=0;i<16;i++)
			{
				int x=chunkX+random.nextInt(14)+1;
				int y=random.nextInt(253)+1;
				int z=chunkZ+random.nextInt(14)+1;
				new WorldGenOreBall(
					Blocks.NETHERRACK.getDefaultState(),
					X_ORE_NETHER.getDefaultState()
					).generate(world,random,new BlockPos(x,y,z));
				//generateOreBall(world,x,y,z,X_ORE_NETHER.getDefaultState(),Blocks.NETHERRACK.getDefaultState().getBlock());
			}
			break;
			case 1:
			for(int i=0;i<7;i++)
			{
				int x=chunkX+random.nextInt(14)+1;
				int y=random.nextInt(253)+1;
				int z=chunkZ+random.nextInt(14)+1;
				new WorldGenOreBall(
					Blocks.END_STONE.getDefaultState(),
					X_ORE_END.getDefaultState()
					).randomize(random,X_CRYSTAL_BLOCK.getDefaultState().withProperty(XCrystalBlock.LEVEL,Integer.valueOf(random.nextInt(8))),X_OXYGEN.getDefaultState()).generate(world,random,new BlockPos(x,y,z));
				//generateOreBall(world,x,y,z,X_ORE_END.getDefaultState(),Blocks.END_STONE.getDefaultState().getBlock());
			}
			break;
		}
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
			   block==X_METAL_BLOCK||
			   block==X_COVALENT_SOLID||
			   block==X_PHASE_FIBER_BLOCK||
			   block==X_PHASE_WEB_BLOCK||
			   block==X_EXPLOSIVE||
			   block==X_REACTIVE_CORE||
			   block==X_GLASS);
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
	}
	
	//========X-Block========//
	public static class ItemXBlock extends XCraftItemBlockCommon
	{
		public ItemXBlock()
		{
			super(X_BLOCK,true);
		}
		
		public int getMetadata(int damage)
		{
			return damage;
		}
	}

	public static class XBlock extends XCraftBlockCommon
	{
		public static final PropertyInteger LEVEL=PropertyInteger.create("level",0,15);
		
		public XBlock()
		{
			super(Material.IRON,"x_block",SoundType.STONE);
			this.setHarvestLevel("pickaxe",3);
			this.setHardness(5.12F);
			this.setResistance(128F);
			this.setLightLevel(0F);
			this.setLightOpacity(255);
			this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL,Integer.valueOf(0)));	
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
		
		@Override
		public void neighborChanged(IBlockState state,World world,BlockPos pos,Block neighborBlock,BlockPos fromPos)
		{
			super.neighborChanged(state,world,pos,neighborBlock,fromPos);
			int power=world.isBlockIndirectlyGettingPowered(pos);
			XCraftBlocks.testSummonXBOSS(world,pos.getX(),pos.getY(),pos.getZ(),state,world.isBlockIndirectlyGettingPowered(pos),neighborBlock,fromPos);
		}
	}
	
	//========X-Block-Advanced========//
	public static class ItemXBlockAdvanced extends XCraftItemBlockCommon
	{
		public ItemXBlockAdvanced()
		{
			super(X_BLOCK_ADVANCED,true);
		}
		
		public int getMetadata(int damage)
		{
			return damage;
		}
	}

	public static class XBlockAdvanced extends XCraftBlockCommon
	{
		public static final PropertyInteger LEVEL=PropertyInteger.create("level",0,15);
		
		public XBlockAdvanced()
		{
			super(Material.IRON,"x_block_advanced",SoundType.METAL);
			setHarvestLevel("pickaxe",4);
			setHardness(5.12F);
			setResistance(256F);
			setLightLevel(0F);
			setLightOpacity(255);
			setDefaultState(this.blockState.getBaseState().withProperty(LEVEL,Integer.valueOf(1)));	
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
			for (int i=0;i<16;i++)
			{
				items.add(new ItemStack(this,1,i));
			}
		}

		protected BlockStateContainer createBlockState()
		{
			return new BlockStateContainer(this,new IProperty[]{LEVEL});
		}

		@SideOnly(Side.CLIENT)
		@Override
		public BlockRenderLayer getBlockLayer()
		{
			return BlockRenderLayer.SOLID;
		}

		@Override
		public boolean canConnectRedstone(IBlockState state,IBlockAccess world,BlockPos pos,EnumFacing side)
		{
			return true;
		}
		
		/*
		@Override
		public void neighborChanged(IBlockState state,World world,BlockPos pos,Block neighborBlock,BlockPos fromPos)
		{
			super.neighborChanged(state,world,pos,neighborBlock,fromPos);
			int x=pos.getX();
			int y=pos.getY();
			int z=pos.getZ();
			Block block=this;
			int power=world.isBlockIndirectlyGettingPowered(pos);
		}*/
		
		public boolean onBlockActivated(World world,BlockPos pos,IBlockState state,EntityPlayer player,EnumHand hand,EnumFacing facing,float hitX,float hitY,float hitZ)
		{
			ItemStack stack=player.getHeldItem(hand);
			Item item=stack.getItem();
			if(state.getBlock()==X_BLOCK_ADVANCED&&
			   item==XCraftMaterials.X_ENERGY_UNIT)
			{
				int power=item.getDamage(stack)-6;//+6~-6
				int level=(int)state.getValue(LEVEL);
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
					if(power>0)
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
								power-=(int)(abxLevel/count);
							}
						}
						else//p-
						{
							//l=6,p=-2,c=2(s=2) -> p=0,l=2;
							//l=7,	p=-3,	c=3(s=-2,(l-l%c)/c=2)	->	p=-2,	l=1;
							//l=11,	p=-6,	c=3(s=-7,(l-l%c)/c=2)	->	p=-3,	l=2;
							//l=3,	p=-2,	c=2(s=-1,(l-l%c)/c=1)	->	p=-1,	l=1;
							//l=6,	p=-2,	c=6(s=-6,l=c)			->	p=+1,	l=0;
							//l=3,	p=-1,	c=4(s=-4,l<c)			->	Error;
							if(level-count>=0)
							{
								level%=count;//l1%c=l2
								power+=(int)(abxLevel/count);
							}
						}
					}
					if(power!=item.getDamage(stack)-6&&
					   level!=(int)state.getValue(LEVEL))
					{
						world.setBlockState(pos,X_BLOCK_ADVANCED.getDefaultState().withProperty(LEVEL,Integer.valueOf(level)));
						stack.setItemDamage(power+6);
						return true;
					}
				}
			}
			return false;
		}
	}
	
	//========X-Crystal-Block========//
	public static class ItemXCrystalBlock extends XCraftItemBlockCommon
	{
		public ItemXCrystalBlock()
		{
			super(X_CRYSTAL_BLOCK,true);
		}
		
		public int getMetadata(int damage)
		{
			return damage;
		}
	}

	public static class XCrystalBlock extends XCraftBlockCommon
	{
		public static final PropertyInteger LEVEL=PropertyInteger.create("level",0,7);
		
		public XCrystalBlock()
		{
			super(Material.IRON,"x_crystal_block",SoundType.GLASS);
			this.setHarvestLevel("pickaxe",4);
			this.setHardness(8.192F);
			this.setResistance(512F);
			this.setLightLevel(0.25F);
			this.setLightOpacity(0);
			this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL,Integer.valueOf(0)));	
			this.setTickRandomly(true);
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
			for (int i=0;i<8;i++) items.add(new ItemStack(this,1,i));
		}

		protected BlockStateContainer createBlockState()
		{
			return new BlockStateContainer(this,new IProperty[]{LEVEL});
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

		public void updateTick(World worldIn,BlockPos pos,IBlockState state,Random rand)
		{
			if(!worldIn.isRemote)
			{
				super.updateTick(worldIn,pos,state,rand);

				if(!worldIn.isAreaLoaded(pos,1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
				int level=state.getValue(LEVEL)+1;
				if(rand.nextInt(9-level)==0)
				{
					if(level*2>getNearbyCrystalBlockLevelSum(worldIn,pos))
					{
						this.moveToNearbyVoid(worldIn,pos,state);
					}
				}
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
								result+=(int)mb.getValue(LEVEL)+1;
							}
						}
					}
				}
			}
			return result;
		}
		
		protected void moveToNearbyVoid(World world,BlockPos pos,IBlockState state)
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
				mb=world.getBlockState(mp);
				for(dx=-1;dx<2;dx++)
				{
					for(dy=-1;dy<2;dy++)
					{
						for(dz=-1;dz<2;dz++)
						{
							world.spawnParticle(EnumParticleTypes.END_ROD,pos.getX()+0.5+dx*0.25,pos.getY()+0.5+dy*0.25,pos.getZ()+0.5+dz*0.25,0,0,0);
						}
					}
				}
				world.setBlockState(mp,state,2);
				world.setBlockToAir(pos);
			}
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
				int i=32;

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

					for (blockpos=pos.down(); (worldIn.isAirBlock(blockpos) || canFallThrough(worldIn.getBlockState(blockpos))) && blockpos.getY() > 0; blockpos=blockpos.down())
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
			setResistance(multiHardness*64F);
		}

		@SideOnly(Side.CLIENT)
		@Override
		public BlockRenderLayer getBlockLayer()
		{
			return BlockRenderLayer.SOLID;
		}

		@Override
		public boolean removedByPlayer(IBlockState state,World world,BlockPos pos,EntityPlayer entity,boolean willHarvest)
		{
			boolean retval=super.removedByPlayer(state,world,pos,entity,willHarvest);
			int x=pos.getX();
			int y=pos.getY();
			int z=pos.getZ();
			EntityItem entityToSpawn;
			if((state.getBlock()==X_ORE||
				state.getBlock()==X_ORE_END)&&
				entity.getHeldItemMainhand().getItem()==XCraftTools.X_ITEM)
			{
				if(!world.isRemote)
				{
					for(int i=0;i<4;i++)
					{
						entityToSpawn=new EntityItem(world,x+0.5,y+0.5,z+0.5,new ItemStack(XCraftMaterials.X_DUST,1));
						entityToSpawn.setPickupDelay(10);
						world.spawnEntity(entityToSpawn);
					}
				}
			}
			return retval;
		}
	}
	
	//========X-Ore========//
	public static class XOre extends XOreCommon
	{
		public XOre()
		{
			super("x_ore",3,1);
		}
		
		public int getExpDrop(IBlockState state,IBlockAccess world,BlockPos pos,int fortune)
		{
			return fortune>0?fortune:0;
		}
	}
	
	//========X-Ore-Nether========//
	public static class XOreNether extends XOreCommon
	{
		public XOreNether()
		{
			super("x_ore_nether",2,0.5F);
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
			super("x_ore_end",4,2);
		}

		@SideOnly(Side.CLIENT)
		@Override
		public BlockRenderLayer getBlockLayer()
		{
			return BlockRenderLayer.SOLID;
		}

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
	
	//========X-Path========//
	public static class XPath extends XCraftBlockCommon
	{
		protected static final AxisAlignedBB X_PATH_AABB=new AxisAlignedBB(0.0D,0.0D,0.0D,1.0D,1.0D,1.0D);//0.9375 or it dosn't work
	
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
				XCraftBlocks.LOGGER.info("has "+container.getName()+" in "+pos.toString());
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
			if(power>0) explode(world,pos,power);
		}
		
		protected void explode(World world,BlockPos pos,int level)
		{
			world.setBlockToAir(pos);
			
			BlockPos tP;
			for(int dx=-level;dx<=level;dx++)
			{
				for(int dy=-level;dy<=level;dy++)
				{
					for(int dz=-level;dz<=level;dz++)
					{
						tP=new BlockPos(pos.getX()+dx,pos.getY()+dy,pos.getZ()+dz);
						XuphoriumCraft.blockReaction(tP,world);
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
			List<Entity> entities=world.loadedEntityList;
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
						Item item=itemE.getItem().getItem();
						BlockPos blockPos=new BlockPos((int)Math.round(itemE.posX-0.5),(int)Math.round(itemE.posY-0.5),(int)Math.round(itemE.posZ-0.5));
						if(item instanceof ItemBlock)
						{
							Block block=((ItemBlock)item).getBlock();
							if(block!=null&&XCraftBlocks.getIsReplaceable(world,blockPos))
							{
								world.setBlockState(blockPos,block.getDefaultState());
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
			this.setHarvestLevel("pickaxe",5);
			this.setHardness(1.28F);
			this.setResistance(64F);
			this.setLightLevel(0.25F);
			this.setLightOpacity(0);
			this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL,Integer.valueOf(0)));	
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
		}

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
				if(facing.getFrontOffsetX()!=0) entityIn.motionX=facing.getFrontOffsetX()*0.1D;
				if(facing.getFrontOffsetZ()!=0) entityIn.motionZ=facing.getFrontOffsetZ()*0.1D;
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
			super(Material.IRON,name,SoundType.METAL);
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
}
