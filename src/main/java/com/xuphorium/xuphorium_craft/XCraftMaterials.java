package com.xuphorium.xuphorium_craft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import net.minecraftforge.fml.common.registry.GameRegistry;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.World;

import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumActionResult;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import net.minecraft.block.state.IBlockState;

import net.minecraft.creativetab.CreativeTabs;

import net.minecraft.potion.PotionEffect;

import net.minecraft.init.MobEffects;

@XuphoriumCraftElements.ModElement.Tag
public class XCraftMaterials extends XuphoriumCraftElements.ModElement
{
	@GameRegistry.ObjectHolder("xuphorium_craft:x_dust")
	public static final Item X_DUST=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_ingot")
	public static final Item X_INGOT=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_nugget")
	public static final Item X_NUGGET=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_rod")
	public static final Item X_ROD=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_catalyst")
	public static final Item X_CATALYST=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_crystal_left")
	public static final Item X_CRYSTAL_LEFT=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_crystal_right")
	public static final Item X_CRYSTAL_RIGHT=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_crystal")
	public static final Item X_CRYSTAL=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_crystal_core")
	public static final Item X_CRYSTAL_CORE=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_diamond")
	public static final Item X_DIAMOND=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_emerald")
	public static final Item X_EMERALD=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_ruby")
	public static final Item X_RUBY=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_pearl")
	public static final Item X_PEARL=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_eye")
	public static final Item X_EYE=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_star")
	public static final Item X_STAR=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_metal")
	public static final Item X_METAL=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_ion_piece")
	public static final Item X_ION_PIECE=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_covalent_shard")
	public static final Item X_COVALENT_SHARD=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_phase_fiber")
	public static final Item X_PHASE_FIBER=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_phase_web")
	public static final Item X_PHASE_WEB=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_energy_unit")
	public static final Item X_ENERGY_UNIT=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_alpha_metal")
	public static final Block X_ALPHA_METAL=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_gamma_metal")
	public static final Block X_GAMMA_METAL=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_delta_metal")
	public static final Block X_DELTA_METAL=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_omega_metal")
	public static final Block X_OMEGA_METAL=null;
	
	public XCraftMaterials(XuphoriumCraftElements instance)
	{
		super(instance,1);
	}

	@Override
	public void initElements()
	{
		//I
		elements.items.add(XDust::new);
		elements.items.add(XIngot::new);
		elements.items.add(XNugget::new);
		elements.items.add(XRod::new);
		//II
		elements.items.add(XCatalyst::new);
		elements.items.add(XCrystalLeft::new);
		elements.items.add(XCrystalRight::new);
		elements.items.add(XCrystal::new);
		elements.items.add(XCrystalCore::new);
		elements.items.add(XDiamond::new);
		elements.items.add(XEmerald::new);
		elements.items.add(XRuby::new);
		elements.items.add(XPearl::new);
		elements.items.add(XEye::new);
		elements.items.add(XStar::new);
		//III - Metal,Ion,Covalent,PhaseFiber,EnergyUnit
		elements.items.add(XMetal::new);
		elements.items.add(XIonPiece::new);
		elements.items.add(XCovalentShard::new);
		elements.items.add(XPhaseFiber::new);
		elements.items.add(XPhaseWeb::new);
		elements.items.add(XEnergyUnit::new);
		//IV - X_METALS(A,G,D,O)
		//Block_
		elements.blocks.add(()->new BlockXMetalsCommon("x_alpha_metal"));
		elements.items.add(()->new XMetalsCommon(X_ALPHA_METAL));
		elements.blocks.add(()->new BlockXMetalsCommon("x_gamma_metal"));
		elements.items.add(()->new XMetalsCommon(X_GAMMA_METAL));
		elements.blocks.add(()->new BlockXMetalsCommon("x_delta_metal"));
		elements.items.add(()->new XMetalsCommon(X_DELTA_METAL));
		elements.blocks.add(()->new BlockXMetalsCommon("x_omega_metal"));
		elements.items.add(()->new XMetalsCommon(X_OMEGA_METAL));
	}

	@Override
	public void registerModels(ModelRegistryEvent event)
	{
		//I
		ModelLoader.setCustomModelResourceLocation(X_DUST,0,new ModelResourceLocation("xuphorium_craft:x_dust","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_INGOT,0,new ModelResourceLocation("xuphorium_craft:x_ingot","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_NUGGET,0,new ModelResourceLocation("xuphorium_craft:x_nugget","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_ROD,0,new ModelResourceLocation("xuphorium_craft:x_rod_0","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_ROD,1,new ModelResourceLocation("xuphorium_craft:x_rod_1","inventory"));
		//II
		ModelLoader.setCustomModelResourceLocation(X_CATALYST,0,new ModelResourceLocation("xuphorium_craft:x_catalyst","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_CRYSTAL_LEFT,0,new ModelResourceLocation("xuphorium_craft:x_crystal_left","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_CRYSTAL_RIGHT,0,new ModelResourceLocation("xuphorium_craft:x_crystal_right","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_CRYSTAL,0,new ModelResourceLocation("xuphorium_craft:x_crystal","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_CRYSTAL_CORE,0,new ModelResourceLocation("xuphorium_craft:x_crystal_core","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_DIAMOND,0,new ModelResourceLocation("xuphorium_craft:x_diamond","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_EMERALD,0,new ModelResourceLocation("xuphorium_craft:x_emerald","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_RUBY,0,new ModelResourceLocation("xuphorium_craft:x_ruby","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_PEARL,0,new ModelResourceLocation("xuphorium_craft:x_pearl","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_EYE,0,new ModelResourceLocation("xuphorium_craft:x_eye","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_STAR,0,new ModelResourceLocation("xuphorium_craft:x_star","inventory"));
		//III
		ModelLoader.setCustomModelResourceLocation(X_METAL,0,new ModelResourceLocation("xuphorium_craft:x_metal","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_ION_PIECE,0,new ModelResourceLocation("xuphorium_craft:x_ion_piece","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_COVALENT_SHARD,0,new ModelResourceLocation("xuphorium_craft:x_covalent_shard","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_PHASE_FIBER,0,new ModelResourceLocation("xuphorium_craft:x_phase_fiber","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_PHASE_WEB,0,new ModelResourceLocation("xuphorium_craft:x_phase_web","inventory"));
		int i;
		for(i=-6;i<7;i++)
		{
			ModelLoader.setCustomModelResourceLocation(X_ENERGY_UNIT,i+6,new ModelResourceLocation("xuphorium_craft:x_energy_unit_"+((i==0?"":(i>0?"p":"n"))+Math.abs(i)),"inventory"));
		}
		//
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_ALPHA_METAL),0,new ModelResourceLocation("xuphorium_craft:x_alpha_metal","inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_GAMMA_METAL),0,new ModelResourceLocation("xuphorium_craft:x_gamma_metal","inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_DELTA_METAL),0,new ModelResourceLocation("xuphorium_craft:x_delta_metal","inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(X_OMEGA_METAL),0,new ModelResourceLocation("xuphorium_craft:x_omega_metal","inventory"));
	}

	@Override
	public int addFuel(ItemStack fuel)
	{
		//if(fuel.getItem()==new ItemStack(X_FUEL,1).getItem()) return 16384;
		return 0;
	}

	@Override
	public void init(FMLInitializationEvent event)
	{
	}

	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
	}

	@Override
	public void serverLoad(FMLServerStartingEvent event)
	{
	}
	
	//============Current Classes============//
	//========Material-Common========//
	public static class XCraftMaterialCommon extends XuphoriumCraft.XCraftItemCommon
	{
		public XCraftMaterialCommon(String name,int maxDamage,int maxStackSize)
		{
			super(name,maxDamage,maxStackSize);
		}

		@Override
		public int getItemEnchantability()
		{
			return 64;
		}

		@Override
		public float getDestroySpeed(ItemStack par1ItemStack,IBlockState par2Block)
		{
			return 1.25F;
		}

		@Override
		public int getMaxItemUseDuration(ItemStack par1ItemStack)
		{
			return 0;
		}
	}

	//========X-Dust========//
	public static class XDust extends XCraftMaterialCommon
	{
		public XDust()
		{
			super("x_dust",0,64);
			BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this,XuphoriumCraft.BehaviorXDustDispenseItem.getInstance());
		}

		@Override
		public int getMaxItemUseDuration(ItemStack itemstack)
		{
			return 0;
		}

		@Override
		public EnumActionResult onItemUseFirst(EntityPlayer player,World world,BlockPos pos,EnumFacing side,float hitX,float hitY,float hitZ,EnumHand hand)
		{
			if(!world.isRemote)
			{
				ItemStack itemstack=player.getHeldItem(hand);
				int x=pos.getX();
				int y=pos.getY();
				int z=pos.getZ();
				if(!player.getCooldownTracker().hasCooldown(this))
				{
					if(player.getHeldItem(hand).getItem()==new ItemStack(X_DUST,1).getItem())
					{
						if(XuphoriumCraft.blockReaction(x,y,z,world)&&player instanceof EntityPlayer)
						{
							if(!player.capabilities.isCreativeMode) itemstack.shrink(1);
							player.getCooldownTracker().setCooldown(this,10);
							return EnumActionResult.SUCCESS;
						}
					}
				}
			}
			return EnumActionResult.PASS;
		}
	}

	//========X-Ingot========//
	public static class XIngot extends XCraftMaterialCommon
	{
		public XIngot()
		{
			super("x_ingot",0,64);
		}
	}

	//========X-Nugget========//
	public static class XNugget extends XCraftMaterialCommon
	{
		public XNugget()
		{
			super("x_nugget",0,64);
		}
	}

	//========X-Rod========//
	public static class XRod extends XCraftMaterialCommon
	{
		public XRod()
		{
			super("x_rod",0,32);
			this.setHasSubtypes(true);
		}
		
		public void getSubItems(CreativeTabs tab,NonNullList<ItemStack> items)
		{
			if(this.isInCreativeTab(tab))
			{
				items.add(new ItemStack(this,1,0));
				items.add(new ItemStack(this,1,1));
			}
		}

		@Override
		public float getDestroySpeed(ItemStack par1ItemStack,IBlockState par2Block)
		{
			return super.getDestroySpeed(par1ItemStack,par2Block)*(float)1.25;
		}
	}

	//========X-Catalyst========//
	public static class XCatalyst extends XCraftMaterialCommon
	{
		public XCatalyst()
		{
			super("x_catalyst",0,48);
		}

		@Override
		public int getMaxItemUseDuration(ItemStack itemstack)
		{
			return 0;
		}

		@Override
		public EnumActionResult onItemUseFirst(EntityPlayer entity,World world,BlockPos pos,EnumFacing side,float hitX,float hitY,float hitZ,EnumHand hand)
		{
			ItemStack itemstack=entity.getHeldItem(hand);
			int x=pos.getX();
			int y=pos.getY();
			int z=pos.getZ();
			if(entity.getHeldItemMainhand().getItem()==new ItemStack(X_DUST,1).getItem())
			{
				if(XuphoriumCraft.blockReaction(x,y,z,world)&&entity instanceof EntityPlayer) ((EntityLivingBase)entity).getHeldItemMainhand().shrink(1);
			}
			return EnumActionResult.PASS;
		}
	}
	
	//========X-Crystal-Common========//
	public static class XCrystalCommon extends XCraftMaterialCommon
	{
		public XCrystalCommon(String name)
		{
			super(name,0,48);
		}
	}
	
	//========X-Crystal-Left========//
	public static class XCrystalLeft extends XCrystalCommon
	{
		public XCrystalLeft()
		{
			super("x_crystal_left");
		}
	}
	
	//========X-Crystal-Right========//
	public static class XCrystalRight extends XCrystalCommon
	{
		public XCrystalRight()
		{
			super("x_crystal_right");
		}
	}
	
	//========X-Crystal========//
	public static class XCrystal extends XCrystalCommon
	{
		public XCrystal()
		{
			super("x_crystal");
		}
	}
	
	//========X-Crystal-Core========//
	public static class XCrystalCore extends XCrystalCommon
	{
		public XCrystalCore()
		{
			super("x_crystal_core");
		}
	}

	//========X-Diamond========//
	public static class XDiamond extends XCraftMaterialCommon
	{
		public XDiamond()
		{
			super("x_diamond",0,32);
		}
	}

	//========X-Emerald========//
	public static class XEmerald extends XCraftMaterialCommon
	{
		public XEmerald()
		{
			super("x_emerald",0,32);
		}
	}

	//========X-Ruby========//
	public static class XRuby extends XCraftMaterialCommon
	{
		public XRuby()
		{
			super("x_ruby",0,32);
		}
	}

	//========X-Pearl========//
	public static class XPearl extends XCraftMaterialCommon
	{
		public XPearl()
		{
			super("x_pearl",0,8);
		}
	}

	//========X-Eye========//
	public static class XEye extends XCraftMaterialCommon
	{
		public XEye()
		{
			super("x_eye",0,8);
		}
/*
		@Override
		public ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer player,EnumHand hand)
		{
			ActionResult<ItemStack> ar=super.onItemRightClick(world,player,hand);
			ItemStack itemstack=ar.getResult();
			
			return ar;
		}

		@Override
		public EnumActionResult onItemUseFirst(EntityPlayer entity,World world,BlockPos pos,EnumFacing side,float hitX,float hitY,float hitZ,EnumHand hand)
		{
			ItemStack itemstack=entity.getHeldItem(hand);
			int x=pos.getX();
			int y=pos.getY();
			int z=pos.getZ();
			if(entity instanceof EntityLivingBase)
			{
				if(((EntityLivingBase)entity).getHeldItemMainhand().getItem()==new ItemStack(X_DUST,1).getItem())
				{
					if(XuphoriumCraft.blockReaction(x,y,z,world)&&entity instanceof EntityPlayer) ((EntityLivingBase)entity).getHeldItemMainhand().shrink(1);
				}
			}
			return EnumActionResult.PASS;
		}*/
	}

	//========X-Star========//
	public static class XStar extends XCraftMaterialCommon
	{
		public XStar()
		{
			super("x_star",0,4);
		}

		@Override
		public void onUpdate(ItemStack itemstack,World world,Entity entity,int par4,boolean par5)
		{
			super.onUpdate(itemstack,world,entity,par4,par5);
			int x=(int)entity.posX;
			int y=(int)entity.posY;
			int z=(int)entity.posZ;
			if(entity instanceof EntityLivingBase&&
				(((EntityLivingBase)entity).getHeldItemMainhand().equals(itemstack)||
				((EntityLivingBase)entity).getHeldItemMainhand().equals(itemstack)))
			{
				((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.SPEED,10,2,true,true));
				((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.HASTE,10,2,true,true));
				((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST,10,2,true,true));
				((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION,300,1,true,true));
				((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE,10,1,true,true));
				((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING,10,1,true,true));
				if(entity.isSneaking())
				{
					if(entity.motionY<0) entity.motionY*=0;
				}
			}
		}
	}
	
	//========X-Metal========//
	public static class XMetal extends XCraftMaterialCommon
	{
		public XMetal()
		{
			super("x_metal",0,16);
		}
	}
	
	//========X-Ion-Piece========//
	public static class XIonPiece extends XCraftMaterialCommon
	{
		public XIonPiece()
		{
			super("x_ion_piece",0,48);
		}
	}
	
	//========X-Covalent-Shard========//
	public static class XCovalentShard extends XCraftMaterialCommon
	{
		public XCovalentShard()
		{
			super("x_covalent_shard",0,32);
		}
	}
	
	//========X-Phase-Fiber========//
	public static class XPhaseFiber extends XCraftMaterialCommon
	{
		public XPhaseFiber()
		{
			super("x_phase_fiber",0,32);
		}
	}
	
	//========X-Phase-Web========//
	public static class XPhaseWeb extends XCraftMaterialCommon
	{
		public XPhaseWeb()
		{
			super("x_phase_web",0,24);
		}
	}
	
	//========X-Energy-Unit========//
	public static class XEnergyUnit extends XCraftMaterialCommon
	{
		public XEnergyUnit()
		{
			super("x_energy_unit",0,16);
			this.setHasSubtypes(true);
			BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this,XuphoriumCraft.BehaviorXEnergyUnitDispenseItem.getInstance());
		}
		
		public void getSubItems(CreativeTabs tab,NonNullList<ItemStack> items)
		{
			if(this.isInCreativeTab(tab))
			{
				for(int i=0;i<13;i++)
				{
					items.add(new ItemStack(this,1,i));
				}
			}
		}
	}
	
	//========X-Metals(Alpha,Gamma,Delta,Omega)========//
	public static class XMetalsCommon extends XCraftBlocks.XCraftItemBlockCommon
	{
		public XMetalsCommon(Block block)
		{
			this(block,0,8);
		}
		
		public XMetalsCommon(Block block,int maxDamage,int maxStackSize)
		{
			super(block,false,true);
			this.setMaxDamage(maxDamage);
			this.maxStackSize=maxStackSize;
			this.setCreativeTab(XuphoriumCraft.CREATIVE_TAB);
		}
		
		@Override
		public int getItemEnchantability()
		{
			return 64;
		}
		
		@Override
		public float getDestroySpeed(ItemStack par1ItemStack,IBlockState par2Block)
		{
			return 1.25F;
		}
		
		@Override
		public int getMaxItemUseDuration(ItemStack par1ItemStack)
		{
			return 0;
		}
	}
	
	public static class BlockXMetalsCommon extends XCraftBlocks.XResourceBlockCommon
	{
		public BlockXMetalsCommon(String name)
		{
			super(name);
			this.setHardness(128F);
			this.setResistance(2048F);
			this.setLightLevel(0.0625F);
			this.setLightOpacity(0);
			this.setDefaultSlipperiness(0.7F);
		}
	}
}
