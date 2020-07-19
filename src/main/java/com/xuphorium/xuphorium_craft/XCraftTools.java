package com.xuphorium.xuphorium_craft;

import net.minecraft.block.BlockDispenser;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
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

import net.minecraftforge.event.entity.living.LivingAttackEvent;

import net.minecraft.world.World;

import net.minecraft.util.math.BlockPos;

import net.minecraft.init.MobEffects;

import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IProjectile;

import net.minecraft.potion.PotionEffect;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;

import net.minecraft.creativetab.CreativeTabs;

import net.minecraft.init.SoundEvents;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/*import com.xuphorium.xuphorium_craft.*;
import com.xuphorium.xuphorium_craft.common.*;
import com.xuphorium.xuphorium_craft.proxy.*;
import com.xuphorium.xuphorium_craft.entity.*;
import com.xuphorium.xuphorium_craft.block.*;
import com.xuphorium.xuphorium_craft.item.*;*/

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
	}

	@Override
	public void registerModels(ModelRegistryEvent event)
	{
		int i;
		for(i=0;i<XItem.NUM_MODES;i++) ModelLoader.setCustomModelResourceLocation(X_ITEM,i,new ModelResourceLocation("xuphorium_craft:x_item_"+i,"inventory"));
		ModelLoader.setCustomModelResourceLocation(X_SWORD,0,new ModelResourceLocation("xuphorium_craft:x_sword","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_FOOD,0,new ModelResourceLocation("xuphorium_craft:x_food","inventory"));
		//X-Magnet
		ModelLoader.setCustomModelResourceLocation(X_MAGNET,0,new ModelResourceLocation("xuphorium_craft:x_magnet","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_MAGNET,1,new ModelResourceLocation("xuphorium_craft:x_magnet_powered_negative","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_MAGNET,2,new ModelResourceLocation("xuphorium_craft:x_magnet_powered","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_MAGNET,3,new ModelResourceLocation("xuphorium_craft:x_magnet_powered_extra","inventory"));
		
		ModelLoader.setCustomModelResourceLocation(X_AXE,0,new ModelResourceLocation("xuphorium_craft:x_axe","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_PICKAXE,0,new ModelResourceLocation("xuphorium_craft:x_pickaxe","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_SHOVEL,0,new ModelResourceLocation("xuphorium_craft:x_shovel","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_HOE,0,new ModelResourceLocation("xuphorium_craft:x_hoe","inventory"));
		
		ModelLoader.setCustomModelResourceLocation(X_SHIELD,0,new ModelResourceLocation("xuphorium_craft:x_shield","inventory"));
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
	//========X Shield Defence========//
	@SubscribeEvent
	public boolean onEntityAttacked(LivingAttackEvent event)
	{
		if(event!=null&&event.getEntityLiving()!=null)
		{
			DamageSource source=event.getSource();
			EntityLivingBase entityLiving=event.getEntityLiving();
			//Detect Player
			EntityPlayer player=null;
			if(entityLiving instanceof EntityPlayer) player=(EntityPlayer)entityLiving;
			//X Boss's Special Logic
			if(entityLiving instanceof XBoss.EntityXBoss)
			{
				//TODO
				return true;
			}
			//Detect Shield
			ItemStack shieldStack=null;
			if(entityLiving.getHeldItemMainhand().getItem()==X_SHIELD) shieldStack=entityLiving.getHeldItemMainhand();
			else if(entityLiving.getHeldItemOffhand().getItem()==X_SHIELD) shieldStack=entityLiving.getHeldItemOffhand();
			//Check Usable & Blocking
			if(!source.canHarmInCreative()&&shieldStack!=null&&XShield.isUsable(shieldStack))
			{
				//Get Attacker Weapon
				EntityLivingBase entityAttacker=null;
				if(source.getTrueSource() instanceof EntityLivingBase) entityAttacker=(EntityLivingBase)source.getTrueSource();
				ItemStack attackerWeapon=null;
				if(entityAttacker!=null) attackerWeapon=entityAttacker.getHeldItemMainhand();
				//Judge Special Chopping
				boolean specialChopping=(attackerWeapon!=null&&attackerWeapon.getItem()==X_SWORD)&&(Math.random()<Math.random());
				//Defence
				if(player==null||!player.getCooldownTracker().hasCooldown(X_SHIELD))
				{
					if(event.isCancelable()&&!event.isCanceled())
					{
						//Play Sound & Damage Item
						int damage=(int)(event.getAmount());
						entityLiving.playSound(SoundEvents.BLOCK_ANVIL_LAND,1.0F,1.0F);
						for(int i=0;i<damage&&XShield.isUsable(shieldStack);i++) shieldStack.damageItem(1,entityLiving);
						//Player's Shield Cooldown
						if(player!=null&&!entityLiving.world.isRemote) player.getCooldownTracker().setCooldown(X_SHIELD,specialChopping?30:5);
						//Defence
						event.setCanceled(true);
					}
					return false;
				}
			}
		}
		return true;
	}
	
	//============Current Classes============//
	//========Tool-Common========//
	public static class XCraftToolCommon extends XuphoriumCraft.XCraftItemCommon
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

	//================X-Item================//
	public static class XItem extends XCraftToolCommon
	{
		//========Register About========//
		public XItem()
		{
			super("x_item",0,16,true);
			BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this,XuphoriumCraft.BehaviorXItemDispenseItem.getInstance());
		}
		
		public void getSubItems(CreativeTabs tab,NonNullList<ItemStack> items)
		{
			if(this.isInCreativeTab(tab))
			{
				for(int i=0;i<XItem.NUM_MODES;i++)
				{
					items.add(new ItemStack(this,1,i));
				}
			}
		}
		
		@Override
		public int getMaxItemUseDuration(ItemStack stack)
		{
			return getCooldownByMetadata(XItem.getItemMode(stack));
		}
		
		public boolean canHarvestBlock(IBlockState blockIn)
		{
			Block block=blockIn.getBlock();
			return (block==XCraftBlocks.X_LIQUID||block==XCraftBlocks.X_EXPLOSIVE||block==XCraftBlocks.X_REACTIVE_CORE);
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
			//Liquid TODO: WILL BE REMOVED
			if(block==XCraftBlocks.X_LIQUID) result*=12;
				//Machine
			else if(block==XCraftBlocks.X_EXPLOSIVE||block==XCraftBlocks.X_REACTIVE_CORE) result*=10;
				//Default
			else
			{
				Material material=state.getMaterial();
				result=material!=Material.PLANTS&&material!=Material.VINE&&material!=Material.CORAL&&material!=Material.LEAVES&&material!=Material.GOURD?1.0F:1.25F;
			}
			if(getItemMode(stack)==1)result*=3;
			return result;
		}
		
		//========Static Variable & Function========//
		public static final int NUM_MODES=5;
		
		public static int getItemMode(ItemStack stack)
		{
			return stack.getMetadata();
		}
		
		public static boolean setItemMode(ItemStack stack,int mode)
		{
			stack.setItemDamage(mode);
			return true;
		}
		
		public static int getCooldownByMetadata(int metadata)
		{
			switch(metadata)
			{
				case 0:return 15;
				case 1:return 0;
				case 2:return 20;
				case 3:return 7;
				case 4:return 80;
				default:return 0;
			}
		}
		
		public static boolean modeUseWithoutBlock(ItemStack stack)
		{
			return modeUseWithoutBlock(getItemMode(stack));
		}
		
		public static boolean modeUseWithoutBlock(int mode)
		{
			return (mode==1||mode==4);
		}
		
		public static boolean modeCanBeAutomatize(ItemStack stack)
		{
			return modeCanBeAutomatize(getItemMode(stack));
		}
		
		public static boolean modeCanBeAutomatize(int mode)
		{
			return (mode==0||mode==2||mode==4);
		}
		
		/**
		 * Without Block Mode (Click at Air) : pos=side=null;
		 * Automatic Mode ( such as Dispenser ) : player=null;
		 * @return The Succeeded Mode
		 */
		public static int XItemUseWithoutCD(World world,EntityPlayer player,BlockPos pos,ItemStack itemstack,EnumFacing side,float hitX,float hitY,float hitZ)
		{
			return XItemUseWithoutCD(world,player,pos,XItem.getItemMode(itemstack),side,hitX,hitY,hitZ);
		}
		
		/**
		 * Without Block Mode (Click at Air) : pos=side=null;
		 * Automatic Mode ( such as Dispenser ) : player=null;
		 * @return The Succeeded Mode or -1 ( Item usage failed ) , -2 ( Invalid item usage mode )
		 */
		public static int XItemUseWithoutCD(World world,EntityPlayer player,BlockPos pos,int mode,EnumFacing side,float hitX,float hitY,float hitZ)
		{
			//Check Valid (Automatic Mode & Without Block Mode)
			if(player==null&&!modeCanBeAutomatize(mode)||
				pos==null&&!modeUseWithoutBlock(mode)
			) return -2;
			//Set Pos
			double x,y,z;
			if(pos!=null)
			{
				x=pos.getX();
				y=pos.getY();
				z=pos.getZ();
			}
			else
			{
				//Not Click Block : pos=player.pos
				x=player.posX;
				y=player.posY;
				z=player.posZ;
			}
			//In Use
			switch(mode)
			{
				case 0:
					if(pos==null) break;
					if(!world.isRemote) world.addWeatherEffect(new EntityLightningBolt(world,x,y,z,false));
					return 0;
				case 2:
					if(pos==null) break;
					XCraftTools.teleportBlock(world,pos,8);
					return 2;
				case 3:
					if(player==null||pos==null) break;
					//x -> dx , y -> dy , z -> dz
					x=(x+0.5-player.posX);
					y=(y+0.5-player.posY);
					z=(z+0.5-player.posZ);
					double distanceValue=1+x*x+y*y+z*z;
					player.motionX+=(x*Math.abs(x))/distanceValue;
					player.motionY+=(y*Math.abs(y))/distanceValue;
					player.motionZ+=(z*Math.abs(z))/distanceValue;
					return 3;
				case 4:
					if(player==null) XBoss.redRayHurtNearbyEntities(world,x,y,z,8,3,false,false);
					else XBoss.redRayHurtNearbyEntities(world,player,12,5,true,false);
					return 4;
			}
			return -1;
		}
		
		public static int XItemUseWithoutCD(World world,EntityPlayer player,int x,int y,int z,ItemStack itemstack,EnumFacing side,float hitX,float hitY,float hitZ)
		{
			return XItemUseWithoutCD(world,player,new BlockPos(x,y,z),itemstack,side,hitX,hitY,hitZ);
		}
		
		//========Item Events========//
		//Click with Block
		/**
		 * Click without Block : uses pos=null
		 * Only Uses for PLAYER'S USAGE!
		 * @return use succeeded or skip by modeUseWithoutBlock.
		 */
		public boolean XItemUse(World world,EntityPlayer player,BlockPos pos,ItemStack itemstack,EnumFacing side,float hitX,float hitY,float hitZ,boolean useCooldown)
		{
			if(!useCooldown||player.getCooldownTracker().hasCooldown(this)) return false;
			//Judge Mode
			if(pos!=null&&XItem.modeUseWithoutBlock(XItem.getItemMode(itemstack))) return true;
			//Use & GetResult
			int result=XItemUseWithoutCD(world,player,pos,itemstack,side,hitX,hitY,hitZ);
			//Set Cooldown
			if(!world.isRemote&&player!=null&&useCooldown)
			{
				int cd=getCooldownByMetadata(result);//Returns -1 means usage failed.
				if(cd>0) player.getCooldownTracker().setCooldown(this,cd);
				return true;
			}
			return false;
		}
		
		@Override
		public EnumActionResult onItemUseFirst(EntityPlayer player,World world,BlockPos pos,EnumFacing side,float hitX,float hitY,float hitZ,EnumHand hand)
		{
			ItemStack itemstack=player.getHeldItem(hand);
			if(player.isSneaking()) return EnumActionResult.FAIL;
			//Direct use Item with Block
			if(XItemUse(world,player,pos,itemstack,side,hitX,hitY,hitZ,true)) return EnumActionResult.SUCCESS;
			return EnumActionResult.FAIL;
		}
		
		//Click without Block
		public ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer player,EnumHand hand)
		{
			ActionResult<ItemStack> actionResult=super.onItemRightClick(world,player,hand);
			//Get Stack
			ItemStack itemstack=actionResult.getResult();
			if(!player.isSneaking())
			{
				//Some Mode Uses Without Block
				if(XItem.modeUseWithoutBlock(XItem.getItemMode(itemstack)))
				{
					XItemUse(world,player,null,itemstack,null,(float)player.posX,(float)player.posY,(float)player.posZ,true);
				}
				return actionResult;
			}
			//Turn Mode ( metadata <=> mode )
			int oldMode=getItemMode(itemstack),newMode=(oldMode+1)%NUM_MODES;
			if(newMode!=oldMode)
			{
				setItemMode(itemstack,newMode);
				//TODO: CLIENT MESSAGE
				player.sendMessage(new TextComponentString(TextFormatting.GOLD+"Now turn to mode "+newMode));
			}
			return actionResult;
		}

		@Override
		public void onUpdate(ItemStack itemstack,World world,Entity entity,int itemSlot,boolean isSelected)
		{
			super.onUpdate(itemstack,world,entity,itemSlot,isSelected);
			if(entity instanceof EntityLivingBase)
			{
				EntityLivingBase entityLivingBase=(EntityLivingBase)entity;
				if(entityLivingBase.getHeldItemMainhand().equals(itemstack))
				{
					entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.SPEED,10,4,true,true));
					entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.HASTE,10,2,true,true));
					entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST,10,2,true,true));
					entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.STRENGTH,10,2,true,true));
					entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION,300,1,true,true));
					entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE,10,1,true,true));
					entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING,10,1,true,true));
				}
			}
		}
	}
	
	//================X-Food================//
	public static class XFood extends ItemFood
	{
		public XFood()
		{
			super(0,0.3F,false);
			setUnlocalizedName("x_food");
			setRegistryName("x_food");
			setCreativeTab(XuphoriumCraft.CREATIVE_TAB);
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
	
	//================X-Magnet================//
	public static class XMagnet extends XCraftToolCommon
	{
		//========Static Variable & Function========//
		public static int NUM_MODES=4;
		
		public static int getItemMode(ItemStack stack)
		{
			return stack.getMetadata();
		}
		
		public static boolean setItemMode(ItemStack stack,int mode)
		{
			stack.setItemDamage(mode);
			return true;
		}
		
		public static double[] calculateGravityVector(double dx,double dy,double dz,double value)
		{
			return calculateGravityVector(dx,dy,dz,value,0);
		}
		
		public static double[] calculateGravityVector(double dx,double dy,double dz,double value,double distanceSquareOffset)
		{
			//From Newton's universal gravitation formula
			//From point to Origin
			double distanceSquare=dx*dx+dy*dy+dz*dz+distanceSquareOffset;//Avoid from zero
			if(distanceSquare<=0) return new double[]{0,0,0};
			return new double[]{
					-value*(dx/distanceSquare),
					-value*(dy/distanceSquare),
					-value*(dz/distanceSquare)
			};
		}
		
		//============Register About============//
		public XMagnet()
		{
			super("x_magnet",0,1,true);
			this.setContainerItem(this);
		}
		
		public void getSubItems(CreativeTabs tab,NonNullList<ItemStack> items)
		{
			if(this.isInCreativeTab(tab)) for(int i=0;i<NUM_MODES;i++) items.add(new ItemStack(this,1,i));
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
/*
		@Override
		public float getDestroySpeed(ItemStack par1ItemStack,IBlockState par2Block)
		{
			return 1F;
		}*/
		
		//========Item Events========//
		public ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer player,EnumHand hand)
		{
			ActionResult<ItemStack> ar=super.onItemRightClick(world,player,hand);
			ItemStack itemstack=ar.getResult();
			//Turn Mode with Cooldown
			if(!world.isRemote&&player.isSneaking()&&!player.getCooldownTracker().hasCooldown(X_MAGNET))
			{
				int oldMode=getItemMode(itemstack),newMode=(oldMode+1)%NUM_MODES;
				if(newMode!=oldMode)
				{
					setItemMode(itemstack,newMode);
					//TODO: TRANSLATED CLIENT MESSAGE
					player.sendMessage(new TextComponentString(TextFormatting.GOLD+"Now turn to mode "+newMode));
					player.getCooldownTracker().setCooldown(X_MAGNET,20);
				}
			}
			return ar;
		}

		@Override
		public void onUpdate(ItemStack itemstack,World world,Entity entity,int par4,boolean par5)
		{
			super.onUpdate(itemstack,world,entity,par4,par5);
			int level=getItemMode(itemstack);
			if(entity instanceof EntityLivingBase&&level>0)
			{
				if(((EntityLivingBase)entity).getHeldItemMainhand().equals(itemstack)||
				   ((EntityLivingBase)entity).getHeldItemOffhand().equals(itemstack))
				{
					List<Entity> entities=world.loadedEntityList;
					double[] gravityForce;
					for(Entity item : entities)
					{
						if(item instanceof EntityItem||item instanceof EntityXPOrb||(level&1)==1&&item instanceof IProjectile)
						{
							gravityForce=calculateGravityVector(item.posX-entity.posX,
									item.posY-entity.posY,
									item.posZ-entity.posZ,
									(level==1)?-1:(level-1),
									1
							);//1 means the scale of force
							item.motionX+=gravityForce[0];
							item.motionY+=gravityForce[1];
							item.motionZ+=gravityForce[2];
						}
					}
				}
			}
		}
	}
	
	//================X-Sword================//
	public static class XSword extends ItemSword
	{
		//========Static Variable & Function========//
		public static int NUM_MODES=4;
		
		/**
		 * Get Sword's Mode by NBT.
		 * @param stack the item stack.
		 * @return the mode,default 0.
		 */
		public static int getSwordMode(ItemStack stack)
		{
			if(stack.hasTagCompound()&&stack.getTagCompound().hasKey("Mode")) return stack.getTagCompound().getInteger("Mode");
			return 0;
		}
		
		/**
		 * Set Sword's Mode with NBT.
		 * @param stack the item stack.
		 * @return if setting successfully.
		 */
		public static boolean setSwordMode(ItemStack stack,int mode)
		{
			NBTTagCompound compound;
			if(stack.hasTagCompound()) compound=stack.getTagCompound();
			else compound=new NBTTagCompound();
			compound.setInteger("Mode",mode);
			stack.setTagCompound(compound);
			return true;
		}
		
		//============Register About============//
		public XSword()
		{
			super(XuphoriumCraft.xSwordMaterial);
			this.setUnlocalizedName("x_sword");
			this.setRegistryName("x_sword");
			this.setCreativeTab(XuphoriumCraft.CREATIVE_TAB);
			this.setHasSubtypes(true);
			this.setMaxDamage(1024);
			//Model by Mode
			this.addPropertyOverride(new ResourceLocation("mode"), new IItemPropertyGetter()
			{
				@SideOnly(Side.CLIENT)
				public float apply(ItemStack stack,@Nullable World worldIn,@Nullable EntityLivingBase entityIn)
				{
					return (float)getSwordMode(stack);
				}
			});
		}
		
		public void getSubItems(CreativeTabs tab,NonNullList<ItemStack> items)
		{
			ItemStack stack;
			if(this.isInCreativeTab(tab)) for(int i=0;i<NUM_MODES;++i)
			{
				stack=new ItemStack(this,1,0);
				setSwordMode(stack,i);
				items.add(stack);
			}
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
		
		//========Item Events========//
		@Override
		public boolean onBlockDestroyed(ItemStack stack,World worldIn,IBlockState state,BlockPos pos,EntityLivingBase entityLiving)
		{
			if(this.getMetadata(stack)==0)return true;
			return super.onBlockDestroyed(stack,worldIn,state,pos,entityLiving);
		}
		
		@Override
		public ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer player,EnumHand hand)
		{
			ActionResult<ItemStack> result=super.onItemRightClick(world,player,hand);
			ItemStack itemstack=result.getResult();
			//Turn Mode with Cooldown
			if(!world.isRemote&&player.isSneaking()&&!player.getCooldownTracker().hasCooldown(X_SWORD))
			{
				int oldMode=getSwordMode(itemstack),newMode=(oldMode+1)%NUM_MODES;
				if(newMode!=oldMode)
				{
					setSwordMode(itemstack,newMode);
					//TODO: TRANSLATED CLIENT MESSAGE
					player.sendMessage(new TextComponentString(TextFormatting.GOLD+"Now turn to mode "+newMode));
					player.getCooldownTracker().setCooldown(X_SWORD,20);
				}
			}
			else XCraftTools.addToolBoostEffects(player);
			return result;
		}

		@Override
		public boolean hitEntity(ItemStack itemstack,EntityLivingBase target,EntityLivingBase attacker)
		{
			World world=attacker.world;
			if(world.isRemote) return true;
			super.hitEntity(itemstack,attacker,target);
			if(target!=null&&target instanceof EntityLiving)
			{
				swordEnrageTarget(world,attacker,(EntityLiving)target,getSwordMode(itemstack));
			}
			return true;
		}
		
		public static void swordEnrageTarget(World world,EntityLivingBase attacker,EntityLiving target,int mode)
		{
			EntityLiving nearestEntity;
			List<Entity> entities;
			double dx,dy,dz,distanceSquare,nearestDistanceSquare;
			switch(mode)
			{
				case 1:
					target.setAttackTarget(target);
					break;
				case 2:
					entities=world.loadedEntityList;
					nearestDistanceSquare=0;
					nearestEntity=null;
					for(Entity entity3 : entities)
					{
						if(entity3==null||entity3==target||!(entity3 instanceof EntityLiving)) continue;
						dx=target.posX-entity3.posX;
						dy=target.posY-entity3.posY;
						dz=target.posZ-entity3.posZ;
						distanceSquare=dx*dx+dy*dy+dz*dz;
						if(nearestEntity==null||distanceSquare<nearestDistanceSquare)
						{
							nearestEntity=(EntityLiving)entity3;
							nearestDistanceSquare=distanceSquare;
						}
					}
					nearestEntity.setAttackTarget(target);
					target.setAttackTarget(nearestEntity);
					break;
				case 3:
					entities=world.loadedEntityList;
					for(Entity entity4 : entities)
					{
						if(entity4 instanceof EntityLiving)
						{
							dx=target.posX-entity4.posX;
							dy=target.posY-entity4.posY;
							dz=target.posZ-entity4.posZ;
							if(dx*dx+dy*dy+dz*dz<=256)((EntityLiving)entity4).setAttackTarget(target);
						}
					}
					break;
			}
		}
		
		@Override
		public boolean itemInteractionForEntity(ItemStack itemstack, EntityPlayer player, EntityLivingBase entity, EnumHand hand)
		{
			//Enrage Target Without Attack
			if(!entity.world.isRemote&&entity!=null&&entity instanceof EntityLiving)
			{
				swordEnrageTarget(entity.world,player,(EntityLiving)entity,getSwordMode(itemstack));
			}
			return false;
			/* if(entity.world.isRemote)
			{
				return false;
			}
			if(entity instanceof net.minecraftforge.common.IShearable)
			{
				net.minecraftforge.common.IShearable target =(net.minecraftforge.common.IShearable)entity;
				BlockPos pos=new BlockPos(entity.posX, entity.posY, entity.posZ);
				if(target.isShearable(itemstack, entity.world, pos))
				{
					java.util.List<ItemStack> drops =target.onSheared(itemstack, entity.world, pos,
							net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(net.minecraft.init.Enchantments.FORTUNE, itemstack));

					java.util.Random rand =new java.util.Random();
					for(ItemStack stack : drops)
					{
						net.minecraft.entity.item.EntityItem ent =entity.entityDropItem(stack, 1.0F);
						ent.motionY +=rand.nextFloat() * 0.05F;
						ent.motionX +=(rand.nextFloat() - rand.nextFloat()) * 0.1F;
						ent.motionZ +=(rand.nextFloat() - rand.nextFloat()) * 0.1F;
					}
					itemstack.damageItem(1, entity);
				}
				return true;
			}
			return false; */
		}
	}
	
	//========Tool-Common-2========//
	public static class XCraftToolCommon2 extends ItemTool
	{
		protected String toolClass=null;
		
		public XCraftToolCommon2(String name,float attackDamage,float attackSpeed,Set<Block> effectiveItemsSet)
		{
			super(XuphoriumCraft.xToolsMaterial,effectiveItemsSet);
			this.setUnlocalizedName(name);
			this.setRegistryName(name);
			this.setCreativeTab(XuphoriumCraft.CREATIVE_TAB);
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
			XCraftTools.addToolBoostEffects(player);
			return super.onItemRightClick(world,player,hand);
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
	
	//================X-Axe================//
	public static class XAxe extends XCraftToolCommon2
	{
		public XAxe()
		{
			super("x_axe",12.8f,-3.14f,XuphoriumCraft.xAxeEffectiveItemsSet);
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
	
	//================X-Pickaxe================//
	public static class XPickaxe extends ItemPickaxe
	{
		public XPickaxe()
		{
			super(XuphoriumCraft.xToolsMaterial);
			this.setUnlocalizedName("x_pickaxe");
			this.setRegistryName("x_pickaxe");
			this.setCreativeTab(XuphoriumCraft.CREATIVE_TAB);
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
			XCraftTools.addToolBoostEffects(player);
			return super.onItemRightClick(world,player,hand);
		}
	}
	
	//================X-Shovel================//
	public static class XShovel extends ItemSpade
	{
		public XShovel()
		{
			super(XuphoriumCraft.xToolsMaterial);
			this.setUnlocalizedName("x_shovel");
			this.setRegistryName("x_shovel");
			this.setCreativeTab(XuphoriumCraft.CREATIVE_TAB);
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
			XCraftTools.addToolBoostEffects(player);
			return super.onItemRightClick(world,player,hand);
		}
	}
	
	//================X-Hoe================//
	public static class XHoe extends ItemHoe
	{
		public XHoe()
		{
			super(XuphoriumCraft.xToolsMaterial);
			this.setUnlocalizedName("x_hoe");
			this.setRegistryName("x_hoe");
			this.setCreativeTab(XuphoriumCraft.CREATIVE_TAB);
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
			XCraftTools.addToolBoostEffects(player);
			return super.onItemRightClick(world,player,hand);
		}
	}
	
	//================X-Shield================//
	public static class XShield extends XCraftToolCommon
	{
		public XShield()
		{
			super("x_shield",1024,1);
			this.addPropertyOverride(new ResourceLocation("deactivated"), new IItemPropertyGetter()
			{
				@SideOnly(Side.CLIENT)
				public float apply(ItemStack stack,@Nullable World worldIn,@Nullable EntityLivingBase entityIn)
				{
					return XShield.isUsable(stack)?0.0F:1.0F;
				}
			});
		}
		
		public static boolean isUsable(ItemStack stack)
		{
			return stack.getItemDamage()<stack.getMaxDamage()-1;
		}
		
		protected XShield(String name,int maxDamage)
		{
			super(name,512,1);
		}
		
		public Item getRepairItem()
		{
			return XCraftMaterials.X_INGOT;
		}
		
		/**
		 * Return whether this item is repairable in an anvil.
		 *
		 * @param toRepair the {@code ItemStack} being repaired
		 * @param repair the {@code ItemStack} being used to perform the repair
		 */
		@Override
		public boolean getIsRepairable(ItemStack toRepair,ItemStack repair)
		{
			Item item=repair.getItem();
			return (item==XCraftMaterials.X_CATALYST||
					item==XCraftMaterials.X_CRYSTAL||
					item==XCraftMaterials.X_CRYSTAL_LEFT||
					item==XCraftMaterials.X_CRYSTAL_RIGHT
			)?true:super.getIsRepairable(toRepair,repair);
		}

		@Override
		public boolean isEnchantable(ItemStack stack)
		{
			return true;
		}
/*
		@Override
		public ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer player,EnumHand hand)
		{
			ItemStack itemstack=player.getHeldItem(hand);
			if(!player.world.isRemote)
			{
				player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE,64,3,false,true));
				player.addPotionEffect(new PotionEffect(MobEffects.SPEED,64,3,true,true));
				player.addPotionEffect(new PotionEffect(MobEffects.GLOWING,64,1,true,true));
			}
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS,itemstack);
		}*/
	}
}
