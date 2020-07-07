package xuphorium_craft.item;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ActionResult;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;

import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;

import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.IProjectile;

import net.minecraft.potion.PotionEffect;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemTool;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.EnumAction;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;

import net.minecraft.creativetab.CreativeTabs;

import net.minecraft.init.SoundEvents;

import java.util.HashMap;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import xuphorium_craft.*;
import xuphorium_craft.common.*;
import xuphorium_craft.proxy.*;
import xuphorium_craft.entity.*;
import xuphorium_craft.block.*;
import xuphorium_craft.item.*;

@XuphoriumCraftElements.ModElement.Tag
public class XCraftTools extends XuphoriumCraftElements.ModElement
{
	public static Logger LOGGER;
	
	public static void teleportBlock(World worldIn,BlockPos pos,int range)
	{
		IBlockState iblockstate=worldIn.getBlockState(pos);
		for(int i=0;i<256;++i)
		{
			BlockPos blockpos=pos.add(worldIn.rand.nextInt(range)-worldIn.rand.nextInt(range),worldIn.rand.nextInt(range)-worldIn.rand.nextInt(range),worldIn.rand.nextInt(range)-worldIn.rand.nextInt(range));
			if(worldIn.isAirBlock(blockpos))
			{
				if(worldIn.isRemote)
				{
					for(int j=0;j<128;++j)
					{
						double d0=worldIn.rand.nextDouble();
						float f=(worldIn.rand.nextFloat()-0.5F)*0.2F;
						float f1=(worldIn.rand.nextFloat()-0.5F)*0.2F;
						float f2=(worldIn.rand.nextFloat()-0.5F)*0.2F;
						double d1=(double)blockpos.getX()+(double)(pos.getX()-blockpos.getX())*d0+(worldIn.rand.nextDouble()-0.5D)+0.5D;
						double d2=(double)blockpos.getY()+(double)(pos.getY()-blockpos.getY())*d0+worldIn.rand.nextDouble()-0.5D;
						double d3=(double)blockpos.getZ()+(double)(pos.getZ()-blockpos.getZ())*d0+(worldIn.rand.nextDouble()-0.5D)+0.5D;
						worldIn.spawnParticle(EnumParticleTypes.PORTAL,d1,d2,d3,(double)f,(double)f1,(double)f2);
					}
				}
				else
				{
					worldIn.setBlockState(blockpos,iblockstate,2);
					worldIn.setBlockToAir(pos);
				}
				return;
			}
		}
	}
	
	public static void addToolBoostEffects(EntityPlayer player)
	{
		player.addPotionEffect(new PotionEffect(MobEffects.SPEED,5,4,false,false));
		player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST,5,4,false,false));
	}
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_item")
	public static final Item X_ITEM=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_food")
	public static final Item X_FOOD=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_magnet")
	public static final Item X_MAGNET=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_sword")
	public static final Item X_SWORD=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_axe")
	public static final Item X_AXE=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_pickaxe")
	public static final Item X_PICKAXE=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_shovel")
	public static final Item X_SHOVEL=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_hoe")
	public static final Item X_HOE=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_shield")
	public static final Item X_SHIELD=null;
	
	@GameRegistry.ObjectHolder("xuphorium_craft:x_shield_powered")
	public static final Item X_SHIELD_POWERED=null;
	
	public XCraftTools(XuphoriumCraftElements instance)
	{
		super(instance,2);
	}

	@Override
	public void initElements()
	{
		elements.items.add(()->new XItem());
		elements.items.add(()->new XFood());
		elements.items.add(()->new XMagnet());
		elements.items.add(()->new XSword());
		elements.items.add(()->new XAxe());
		elements.items.add(()->new XPickaxe());
		elements.items.add(()->new XShovel());
		elements.items.add(()->new XHoe());
		elements.items.add(()->new XShield());
		elements.items.add(()->new XShieldPowered());
	}

	@Override
	public void registerModels(ModelRegistryEvent event)
	{
		for(int i=0;i<4;i++)
		{
			ModelLoader.setCustomModelResourceLocation(X_ITEM,i,new ModelResourceLocation("xuphorium_craft:x_item_"+i,"inventory"));
			ModelLoader.setCustomModelResourceLocation(X_SWORD,i,new ModelResourceLocation("xuphorium_craft:x_sword_"+i,"inventory"));
		}
		ModelLoader.setCustomModelResourceLocation(X_FOOD,0,new ModelResourceLocation("xuphorium_craft:x_food","inventory"));
		
		ModelLoader.setCustomModelResourceLocation(X_MAGNET,0,new ModelResourceLocation("xuphorium_craft:x_magnet","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_MAGNET,1,new ModelResourceLocation("xuphorium_craft:x_magnet_powered","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_MAGNET,2,new ModelResourceLocation("xuphorium_craft:x_magnet_powered_extra","inventory"));
		
		ModelLoader.setCustomModelResourceLocation(X_AXE,0,new ModelResourceLocation("xuphorium_craft:x_axe","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_PICKAXE,0,new ModelResourceLocation("xuphorium_craft:x_pickaxe","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_SHOVEL,0,new ModelResourceLocation("xuphorium_craft:x_shovel","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_HOE,0,new ModelResourceLocation("xuphorium_craft:x_hoe","inventory"));
		
		ModelLoader.setCustomModelResourceLocation(X_SHIELD,0,new ModelResourceLocation("xuphorium_craft:x_shield","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_SHIELD_POWERED,0,new ModelResourceLocation("xuphorium_craft:x_shield_powered","inventory"));
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
	}

	@Override
	public void serverLoad(FMLServerStartingEvent event)
	{
	}
	
	//============Current Events============//
	@SubscribeEvent
	public boolean onEntityAttacked(LivingAttackEvent event)
	{
		if(event!=null&&event.getEntityLiving()!=null)
		{
			DamageSource source=event.getSource();
			EntityLivingBase entityLiving=event.getEntityLiving();
			ItemStack shield=null;
			//Detect Shield
			if(entityLiving.getHeldItemMainhand().getItem()==X_SHIELD_POWERED)
			{
				shield=entityLiving.getHeldItemMainhand();
			}
			else if(entityLiving.getHeldItemOffhand().getItem()==X_SHIELD_POWERED)
			{
				shield=entityLiving.getHeldItemOffhand();
			}
			//Shield Blocking
			if(!source.canHarmInCreative()&&shield!=null)
			{
				EntityLivingBase entityAttacker=null;
				if(source.getTrueSource() instanceof EntityLivingBase) entityAttacker=(EntityLivingBase)source.getTrueSource();
				ItemStack attackerWeapon=null;
				
				if(entityAttacker!=null) attackerWeapon=entityAttacker.getHeldItemMainhand();
				boolean randomDefence=(attackerWeapon!=null&&attackerWeapon.getItem()==X_SWORD);
				if(!randomDefence||randomDefence&&Math.random()>Math.random())
				{
					entityLiving.playSound(SoundEvents.BLOCK_ANVIL_LAND,1.0F,1.0F);
					shield.damageItem((int)(event.getAmount()/(1+Math.random())),entityLiving);
					if(event.isCancelable()&&!event.isCanceled()) event.setCanceled(true);
					return false;
				}
			}
		}
		return true;
	}
	
	//============Current Classes============//
	//========Tool-Common========//
	public static class XCraftToolCommon extends XCraft.XCraftItemCommon
	{
		public XCraftToolCommon(String name,int maxDamage,int maxStackSize)
		{
			super(name,maxDamage,maxStackSize);
		}
		
		public XCraftToolCommon(String name,int maxDamage,int maxStackSize,boolean hasSubtypes)
		{
			this(name,maxDamage,maxStackSize);
			this.setHasSubtypes(hasSubtypes);
		}

		@Override
		public int getItemEnchantability()
		{
			return 64;
		}

		@Override
		public int getMaxItemUseDuration(ItemStack par1ItemStack)
		{
			return 0;
		}
	}

	//========X-Item========//
	public static class XItem extends XCraftToolCommon
	{
		public XItem()
		{
			super("x_item",0,16,true);
		}
		
		public void getSubItems(CreativeTabs tab,NonNullList<ItemStack> items)
		{
			if(this.isInCreativeTab(tab))
			{
				items.add(new ItemStack(this,1,0));
				items.add(new ItemStack(this,1,1));
				items.add(new ItemStack(this,1,2));
				items.add(new ItemStack(this,1,3));
			}
		}

		@Override
		public int getMaxItemUseDuration(ItemStack stack)
		{
			switch(this.getMetadata(stack))
			{
				case 0:return 8;
				case 1:return 0;
				case 2:return 16;
				case 3:return 4;
				default:return 0;
			}
		}
		
		public boolean canHarvestBlock(IBlockState blockIn)
		{
			Block block=blockIn.getBlock();
			return (block==XCraftBlocks.X_LIQUID);
		}

		@Override
		public boolean isEnchantable(ItemStack stack)
		{
			return this.getItemStackLimit(stack)==1;
		}

		@Override
		public int getItemEnchantability()
		{
			return 80;
		}

		@Override
		public float getDestroySpeed(ItemStack stack,IBlockState state)
		{
			Block block=state.getBlock();
			float result=super.getDestroySpeed(stack,state);
			if(XCraftBlocks.isXOre(block)) result*=5;
			if(block==XCraftBlocks.X_LIQUID) result*=12;
			else
			{
				Material material=state.getMaterial();
				result=material!=Material.PLANTS&&material!=Material.VINE&&material!=Material.CORAL&&material!=Material.LEAVES&&material!=Material.GOURD?1.0F:1.25F;
			}
			if(this.getMetadata(stack)==1)result*=3;
			return result;
		}
		
		@Override
		public EnumActionResult onItemUseFirst(EntityPlayer player,World world,BlockPos pos,EnumFacing side,float hitX,float hitY,float hitZ,EnumHand hand)
		{
			ItemStack itemstack=player.getHeldItem(hand);
			if(player.isSneaking()) return EnumActionResult.PASS;
			int x=pos.getX();
			int y=pos.getY();
			int z=pos.getZ();
			double dx=x+0.5-player.posX;
			double dy=y+0.5-player.posY;
			double dz=z+0.5-player.posZ;
			switch(this.getMetadata(itemstack))
			{
				case 0:
					world.addWeatherEffect(new EntityLightningBolt(world,x,y,z,false));
					break;
				case 2:
					XCraftTools.teleportBlock(world,pos,8);
					break;
				case 3:
					player.motionX+=dx/2;
					player.motionY+=dy/2;
					player.motionZ+=dz/2;
					break;
			}
			return EnumActionResult.PASS;
		}
		
		public ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer player,EnumHand hand)
		{
			ActionResult<ItemStack> ar=super.onItemRightClick(world,player,hand);
			ItemStack itemstack=ar.getResult();
			if(player.isSneaking()) itemstack.setItemDamage((itemstack.getItem().getDamage(itemstack)+1)%4);
			return ar;
		}

		@Override
		public void onUpdate(ItemStack itemstack,World world,Entity entity,int par4,boolean par5)
		{
			super.onUpdate(itemstack,world,entity,par4,par5);
			if(entity instanceof EntityLivingBase)
			{
				EntityLivingBase entityLiving=(EntityLivingBase)entity;
				if(entityLiving.getHeldItemMainhand().equals(itemstack))
				{
					entityLiving.addPotionEffect(new PotionEffect(MobEffects.SPEED,10,4,true,true));
					entityLiving.addPotionEffect(new PotionEffect(MobEffects.HASTE,10,2,true,true));
					entityLiving.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST,10,2,true,true));
					entityLiving.addPotionEffect(new PotionEffect(MobEffects.STRENGTH,10,2,true,true));
					entityLiving.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION,300,1,true,true));
					entityLiving.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE,10,1,true,true));
					entityLiving.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING,10,1,true,true));
				}
			}
		}
	}
	
	//========X-Food========//
	public static class XFood extends ItemFood
	{
		public XFood()
		{
			super(0,0.3F,false);
			setUnlocalizedName("x_food");
			setRegistryName("x_food");
			setCreativeTab(XCraft.CREATIVE_TAB);
			setMaxStackSize(16);
		}

		@Override
		public EnumAction getItemUseAction(ItemStack par1ItemStack)
		{
			return EnumAction.EAT;
		}

		@Override
		protected void onFoodEaten(ItemStack itemStack,World world,EntityPlayer player)
		{
			player.addPotionEffect(new PotionEffect(MobEffects.SATURATION,(int)Math.round(Math.random()*10),1,true,true));
			player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION,(int)Math.round(Math.random()*10),2,true,true));
		}
	}
	
	//========X-Magnet========//
	public static class XMagnet extends XCraftToolCommon
	{
		public XMagnet()
		{
			super("x_magnet",0,1,true);
			this.setContainerItem(this);
		}
		
		public void getSubItems(CreativeTabs tab,NonNullList<ItemStack> items)
		{
			if(this.isInCreativeTab(tab))
			{
				items.add(new ItemStack(this,1,0));
				items.add(new ItemStack(this,1,1));
				items.add(new ItemStack(this,1,2));
			}
		}

		@Override
		public int getItemEnchantability()
		{
			return 0;
		}

		@Override
		public int getMaxItemUseDuration(ItemStack par1ItemStack)
		{
			return 0;
		}

		@Override
		public float getDestroySpeed(ItemStack par1ItemStack,IBlockState par2Block)
		{
			return 1F;
		}
		
		public static double m_abs(double x)
		{
			if(x==0)return 0;return Math.abs(x)<1?1/x:x;
		}
		
		public static double m_abX(double x)
		{
			if(x==0)return 0;return Math.abs(x)>1?1/x:0;
		}
		
		public static double sgn(double x)
		{
			return x==0?0:(x>0?1:-1);
		}
		
		public static double[] calculateGravityVector(double dx,double dy,double dz,double value)
		{
			//From Newton's universal gravitation formula
			double distanceSquare=dx*dx+dy*dy+dz*dz;
			if(distance<=0) return new double[]{0,0,0};
			return new double[]{
				value*(dx/distanceSquare),
				value*(dy/distanceSquare),
				value*(dz/distanceSquare)
				};
		}
		
		public ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer player,EnumHand hand)
		{
			ActionResult<ItemStack> ar=super.onItemRightClick(world,player,hand);
			ItemStack itemstack=ar.getResult();
			if(player.isSneaking())itemstack.setItemDamage((itemstack.getItem().getDamage(itemstack)+1)%3);
			return ar;
		}

		@Override
		public void onUpdate(ItemStack itemstack,World world,Entity entity,int par4,boolean par5)
		{
			super.onUpdate(itemstack,world,entity,par4,par5);
			int level=this.getMetadata(itemstack);
			if(entity instanceof EntityLivingBase&&level>0)
			{
				if(((EntityLivingBase)entity).getHeldItemMainhand().equals(itemstack)||
				   ((EntityLivingBase)entity).getHeldItemOffhand().equals(itemstack))
				{
					List<Entity> entities=world.loadedEntityList;
					double dx,dy,dz,distance;
					double[] gravityForce;
					for(Entity item : entities)
					{
						if(item instanceof EntityItem||item instanceof EntityXPOrb||level>1&&item instanceof IProjectile)
						{
							dx=entity.posX-item.posX;
							dy=entity.posY-item.posY;
							dz=entity.posZ-item.posZ;
							gravityForce=calculateGravityVector(dx,dy,dz,-1)//1 means the mess of player
							item.motionX+=gravityForce[0];//rot.x
							item.motionY+=gravityForce[1];//rot.y
							item.motionZ+=gravityForce[2];//rot.z
						}
					}
				}
			}
		}
	}
	
	//========X-Sword========//
	public static class XSword extends ItemSword
	{
		public XSword()
		{
			super(XCraft.xSwordMaterial);
			this.setUnlocalizedName("x_sword");
			this.setRegistryName("x_sword");
			this.setCreativeTab(XCraft.CREATIVE_TAB);
			this.setHasSubtypes(true);
			this.setMaxDamage(0);
			//this.maxStackSize=1;
		}
		
		public void getSubItems(CreativeTabs tab,NonNullList<ItemStack> items)
		{
			if(this.isInCreativeTab(tab))for(int i=0;i<4;++i)items.add(new ItemStack(this,1,i));
		}
		
        public Item getRepairItem()
        {
			return XCraftMaterials.X_INGOT;
		}
		
		public Set<String> getToolClasses(ItemStack stack)
		{
			HashMap<String,Integer> result=new HashMap<String,Integer>();
			result.put("sword",3);
			return result.keySet();
		}

		@Override
		public boolean isEnchantable(ItemStack stack)
		{
			return this.getItemStackLimit(stack)==1;
		}
		
		@Override
		public ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer player,EnumHand hand)
		{
			ActionResult<ItemStack> retval=super.onItemRightClick(world,player,hand);
			ItemStack itemstack=retval.getResult();
			int x=(int)player.posX;
			int y=(int)player.posY;
			int z=(int)player.posZ;
			XCraftTools.addToolBoostEffects(player);
			if(player.isSneaking())itemstack.setItemDamage((itemstack.getItem().getDamage(itemstack)+1)%4);
			return retval;
		}

		@Override
		public boolean hitEntity(ItemStack itemstack,EntityLivingBase entity,EntityLivingBase entity2)
		{
			super.hitEntity(itemstack,entity,entity2);
			int x=(int)entity.posX;
			int y=(int)entity.posY;
			int z=(int)entity.posZ;
			World world=entity.world;
			
			List<Entity> entities;
			double dx,dy,dz,distance,nearestDistance;
			if(!(entity2 instanceof EntityCreature)) return true;
			EntityCreature entityCreature2=(EntityCreature)entity2;
			if(entityCreature2!=null)
			{
				EntityCreature nearestEntity;
				switch(this.getMetadata(itemstack))
				{
					case 1:
						entityCreature2.setAttackTarget(entityCreature2);
						break;
					case 2:
						entities=world.loadedEntityList;
						nearestDistance=0;
						nearestEntity=null;
						for(Entity entity3 : entities)
						{
							if(entity3==null) continue;
							if(entity3 instanceof EntityCreature)
							{
								dx=entityCreature2.posX-entity3.posX;
								dy=entityCreature2.posY-entity3.posY;
								dz=entityCreature2.posZ-entity3.posZ;
								distance=Math.sqrt(dx*dx+dy*dy+dz*dz);
								if(distance==0) continue;
								if(nearestEntity==null||distance<nearestDistance)
								{
									nearestEntity=(EntityCreature)entity3;
									nearestDistance=distance;
								}
							}
						}
						nearestEntity.setAttackTarget(entityCreature2);
						entityCreature2.setAttackTarget(nearestEntity);
						break;
					case 3:
						entities=world.loadedEntityList;
						for(Entity entity4 : entities)
						{
							if(entity4 instanceof EntityCreature)
							{
								dx=entityCreature2.posX-entity4.posX;
								dy=entityCreature2.posY-entity4.posY;
								dz=entityCreature2.posZ-entity4.posZ;
								if(Math.sqrt(dx*dx+dy*dy+dz*dz)<=16)((EntityCreature)entity4).setAttackTarget(entityCreature2);
							}
						}
						break;
				}
			}
			return true;
		}

		@Override
		public float getDestroySpeed(ItemStack stack,IBlockState state)
		{
			float result=(this.getMetadata(stack)==0?3:1)*super.getDestroySpeed(stack,state);
			//result*=2-this.getMetadata(stack)/this.getMaxDamage();
			return result;
		}/*
		
		@Override
		public float getAttackDamage()
		{
			return (this.getMetadata(this.getDefaultInstance())==0?2:1)*super.getAttackDamage();
		}*/
		
		@Override
		public boolean onBlockDestroyed(ItemStack stack,World worldIn,IBlockState state,BlockPos pos,EntityLivingBase entityLiving)
		{
			if(this.getMetadata(stack)==0)return true;
			return super.onBlockDestroyed(stack,worldIn,state,pos,entityLiving);
		}
	}
	
	//========Tool-Common-2========//
	public static class XCraftToolCommon2 extends ItemTool
	{
		protected String toolClass=null;
		
		public XCraftToolCommon2(String name,float attackDamage,float attackSpeed,Set<Block> effectiveItemsSet)
		{
			super(XCraft.xToolsMaterial,effectiveItemsSet);
			this.setUnlocalizedName(name);
			this.setRegistryName(name);
			this.setCreativeTab(XCraft.CREATIVE_TAB);
			this.attackDamage=attackDamage;
			this.attackSpeed=attackSpeed;
		}

		@Override
		public float getDestroySpeed(ItemStack stack,IBlockState state)
		{
			return (float)1.5*super.getDestroySpeed(stack,state);
		}
		
        public Item getRepairItem()
        {
			return XCraftMaterials.X_INGOT;
		}
		
		@Override
		public ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer player,EnumHand hand)
		{
			ActionResult<ItemStack> retval=super.onItemRightClick(world,player,hand);
			ItemStack itemstack=retval.getResult();
			XCraftTools.addToolBoostEffects(player);
			return retval;
		}
		
		public Set<String> getToolClasses(ItemStack stack)
		{
			if(this.toolClass==null)return super.getToolClasses(stack);
			HashMap<String,Integer> result=new HashMap<String,Integer>();
			//if(this.toolClass==null)return result.keySet();
			result.put(this.toolClass,6);
			return result.keySet();
		}
	}
	
	//========X-Axe========//
	public static class XAxe extends XCraftToolCommon2
	{
		public XAxe()
		{
			super("x_axe",12.8f,-3.14f,XCraft.xAxeEffectiveItemsSet);
		}
		
		public boolean canCurrentHarvest(ItemStack stack,IBlockState state)
		{
			Material material=state.getMaterial();
			return (material!=Material.WOOD&&material!=Material.PLANTS&&material!=Material.VINE);
		}

		@Override
		public float getDestroySpeed(ItemStack stack,IBlockState state)
		{
			float result=canCurrentHarvest(stack,state)?super.getDestroySpeed(stack,state):this.efficiency;
			result*=2-this.getMetadata(stack)/this.getMaxDamage();
			return result;
		}
	}
	
	//========X-Pickaxe========//
	public static class XPickaxe extends ItemPickaxe
	{
		public XPickaxe()
		{
			super(XCraft.xToolsMaterial);
			this.setUnlocalizedName("x_pickaxe");
			this.setRegistryName("x_pickaxe");
			this.setCreativeTab(XCraft.CREATIVE_TAB);
			this.attackDamage=6.4f;
			this.attackSpeed=-2.4f;
		}
		
		public Set<String> getToolClasses(ItemStack stack)
		{
			HashMap<String,Integer> result=new HashMap<String,Integer>();
			result.put("pickaxe",6);
			return result.keySet();
		}

		@Override
		public float getDestroySpeed(ItemStack stack,IBlockState state)
		{
			return super.getDestroySpeed(stack,state)*(2-this.getMetadata(stack)/this.getMaxDamage());
		}
		
        public Item getRepairItem()
        {
			return XCraftMaterials.X_INGOT;
		}
		
		@Override
		public ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer player,EnumHand hand)
		{
			ActionResult<ItemStack> retval=super.onItemRightClick(world,player,hand);
			ItemStack itemstack=retval.getResult();
			XCraftTools.addToolBoostEffects(player);
			return retval;
		}
	}
	
	//========X-Shovel========//
	public static class XShovel extends ItemSpade
	{
		public XShovel()
		{
			super(XCraft.xToolsMaterial);
			this.setUnlocalizedName("x_shovel");
			this.setRegistryName("x_shovel");
			this.setCreativeTab(XCraft.CREATIVE_TAB);
			this.attackDamage=4.8f;
			this.attackSpeed=-1.6f;
		}
		
		public Set<String> getToolClasses(ItemStack stack)
		{
			HashMap<String,Integer> result=new HashMap<String,Integer>();
			result.put("spade",6);
			return result.keySet();
		}

		@Override
		public float getDestroySpeed(ItemStack stack,IBlockState state)
		{
			Block block=state.getBlock();
			float result=super.getDestroySpeed(stack,state);
			if(block==XCraftBlocks.X_DUST_BLOCK) result*=3;
			result*=2-this.getMetadata(stack)/this.getMaxDamage();
			return result;
		}
		
        public Item getRepairItem()
        {
			return XCraftMaterials.X_INGOT;
		}
		
		@Override
		public ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer player,EnumHand hand)
		{
			ActionResult<ItemStack> retval=super.onItemRightClick(world,player,hand);
			ItemStack itemstack=retval.getResult();
			XCraftTools.addToolBoostEffects(player);
			return retval;
		}
	}
	
	//========X-Hoe========//
	public static class XHoe extends ItemHoe
	{
		public XHoe()
		{
			super(XCraft.xToolsMaterial);
			this.setUnlocalizedName("x_hoe");
			this.setRegistryName("x_hoe");
			this.setCreativeTab(XCraft.CREATIVE_TAB);
			//this.attackDamage=2.4f;
			//this.attackSpeed=-0.4f;
		}
		
		public Set<String> getToolClasses(ItemStack stack)
		{
			HashMap<String,Integer> result=new HashMap<String,Integer>();
			result.put("hoe",6);
			return result.keySet();
		}
		
        public Item getRepairItem()
        {
			return XCraftMaterials.X_INGOT;
		}
		
		@Override
		public ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer player,EnumHand hand)
		{
			ActionResult<ItemStack> retval=super.onItemRightClick(world,player,hand);
			ItemStack itemstack=retval.getResult();
			XCraftTools.addToolBoostEffects(player);
			return retval;
		}
	}
	
	//========X-Shield========//
	public static class XShield extends XCraftToolCommon
	{
		public XShield()
		{
			super("x_shield",4096,1);
		}
		
		protected XShield(String name)
		{
			super(name,4096,1);
		}
		
        public Item getRepairItem()
        {
			return XCraftMaterials.X_INGOT;
		}

		@Override
		public boolean isEnchantable(ItemStack stack)
		{
			return true;
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer player,EnumHand hand)
		{
			ActionResult<ItemStack> ar=super.onItemRightClick(world,player,hand);
			ItemStack itemstack=ar.getResult();
			player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE,64,3,false,true));
			player.addPotionEffect(new PotionEffect(MobEffects.SPEED,64,3,true,true));
			player.addPotionEffect(new PotionEffect(MobEffects.GLOWING,64,1,true,true));
			Item item=itemstack.getItem();
			if(player.isSneaking()) XShield.trunMode(item==X_SHIELD?X_SHIELD_POWERED:X_SHIELD,itemstack,world,player,hand);
			return ar;
		}
		
		public static void trunMode(Item willTurnTo,ItemStack stack,World world,EntityPlayer player,EnumHand hand)
		{
			ItemStack newstack=new ItemStack(willTurnTo,stack.getCount(),stack.getItemDamage());
			
		}
	}
	
	public static class XShieldPowered extends XShield
	{
		public XShieldPowered()
		{
			super("x_shield_powered");
		}

		@Override
		public ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer player,EnumHand hand)
		{
			return super.onItemRightClick(world,player,hand);
		}
		
		public EnumAction getItemUseAction(ItemStack stack)
		{
			return EnumAction.BLOCK;
		}
		
		@Override
		public void onUpdate(ItemStack itemstack,World world,Entity entity,int par4,boolean par5)
		{
			super.onUpdate(itemstack,world,entity,par4,par5);
			if(entity instanceof EntityLivingBase)
			{
				EntityLivingBase entityLiving=((EntityLivingBase)entity);
				ItemStack shield=null;
				if(entityLiving.getHeldItemMainhand().getItem()==X_SHIELD_POWERED) shield=entityLiving.getHeldItemMainhand();
				else if(entityLiving.getHeldItemOffhand().getItem()==X_SHIELD_POWERED) shield=entityLiving.getHeldItemOffhand();
				if(shield!=null)
				{
					entityLiving.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE,64,3,false,true));
					entityLiving.addPotionEffect(new PotionEffect(MobEffects.SPEED,64,3,true,true));
					entityLiving.addPotionEffect(new PotionEffect(MobEffects.GLOWING,64,1,true,true));
					if(Math.random()*100<1&&shield.getItemDamage()>0)
					{
						shield.setItemDamage(shield.getItemDamage()-1);
					}
				}
			}
		}
	}
}
