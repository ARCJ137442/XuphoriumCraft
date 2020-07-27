package com.xuphorium.xuphorium_craft;

import com.google.common.collect.Multimap;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.item.EntityItem;

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

@XuphoriumCraftElements.ModElement.Tag
public class XCraftTools extends XuphoriumCraftElements.ModElement
{
	public static Logger LOGGER;
	
	//================Static Global Variable&Function================//
	public static void teleportBlock(World worldIn,BlockPos pos,int range)
	{
		IBlockState iblockstate=worldIn.getBlockState(pos);
		for(int i=0;i<256;++i)
		{
			BlockPos blockpos=pos.add(worldIn.rand.nextInt(range)-worldIn.rand.nextInt(range),worldIn.rand.nextInt(range)-worldIn.rand.nextInt(range),worldIn.rand.nextInt(range)-worldIn.rand.nextInt(range));
			if(worldIn.isAirBlock(blockpos))
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
					worldIn.spawnParticle(EnumParticleTypes.PORTAL,d1,d2,d3,f,f1,f2);
				}
				if(!worldIn.isRemote)
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
		if(player.world.isRemote) return;
		player.addPotionEffect(new PotionEffect(MobEffects.SPEED,5,4,false,false));
		player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST,5,4,false,false));
	}
	
	//================Register About================//
	@GameRegistry.ObjectHolder("xuphorium_craft:x_item")
	public static final Item X_ITEM=null;
	
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
		elements.items.add(XItem::new);
		elements.items.add(XMagnet::new);
		elements.items.add(XSword::new);
		elements.items.add(XAxe::new);
		elements.items.add(XPickaxe::new);
		elements.items.add(XShovel::new);
		elements.items.add(XHoe::new);
		elements.items.add(XShield::new);
	}

	@Override
	public void registerModels(ModelRegistryEvent event)
	{
		ModelLoader.setCustomModelResourceLocation(X_ITEM,0,new ModelResourceLocation("xuphorium_craft:x_item","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_SWORD,0,new ModelResourceLocation("xuphorium_craft:x_sword","inventory"));
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
						entityLiving.world.playSound(null,entityLiving.posX,entityLiving.posY,entityLiving.posZ,SoundEvents.BLOCK_ANVIL_LAND,SoundCategory.NEUTRAL,0.75F,1F / (XShield.getItemRand().nextFloat() * 0.4F + 0.8F));
						for(int i=0;i<damage&&XShield.isUsable(shieldStack);i++) shieldStack.damageItem(1,entityLiving);
						//Entity's Effect
						if(!entityLiving.world.isRemote)
						{
							entityLiving.addPotionEffect(new PotionEffect(MobEffects.GLOWING,20,1,true,false));
							XBoss.punchEntities(entityLiving.world,entityLiving,2,0.1);
						}
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
		//========Static Variable========//
		public static final int NUM_MODES=6;
		
		public static final int MODE_ID_DEFAULT=0;
		public static final int MODE_ID_LIGHTNING=1;
		public static final int MODE_ID_TELEPORTATION=2;
		public static final int MODE_ID_DRAG=3;
		public static final int MODE_ID_RAY=4;
		public static final int MODE_ID_BULLET=5;
		
		//========Register About========//
		public XItem()
		{
			super("x_item",0,1,true);
			setContainerItem(this);
			//Model by Mode
			this.addPropertyOverride(new ResourceLocation("mode"),new IItemPropertyGetter()
			{
				@SideOnly(Side.CLIENT)
				public float apply(ItemStack stack,@Nullable World worldIn,@Nullable EntityLivingBase entityIn)
				{
					return (float)getItemMode(stack);
				}
			});
			BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this,XuphoriumCraft.BehaviorXItemDispenseItem.getInstance());
		}
		
		public void getSubItems(CreativeTabs tab,NonNullList<ItemStack> items)
		{
			ItemStack stack;
			if(this.isInCreativeTab(tab)) for(int i=0;i<NUM_MODES;++i)
			{
				stack=new ItemStack(this,1,0);
				setItemMode(stack,i);
				items.add(stack);
			}
		}
		
		public Multimap<String,AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot)
		{
			Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);
			if (equipmentSlot == EntityEquipmentSlot.MAINHAND)
			{
				multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier",4, 0));
				multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier",-0.5, 0));
			}
			return multimap;
		}
		
		@Override
		public int getMaxItemUseDuration(ItemStack stack)
		{
			return getCooldownByMode(XItem.getItemMode(stack));
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
			if(getItemMode(stack)==MODE_ID_DEFAULT) result*=3;
			return result;
		}
		
		//========Static Function========//
		public static int getItemMode(ItemStack stack)
		{
			return XuphoriumCraft.XCraftItemCommon.getItemNBTMode(stack);
		}
		
		public static boolean setItemMode(ItemStack stack,int mode)
		{
			return XuphoriumCraft.XCraftItemCommon.setItemNBTMode(stack,mode);
		}
		
		public static int getCooldownByMode(int mode)
		{
			switch(mode)
			{
				case MODE_ID_LIGHTNING: return 15;
				case MODE_ID_TELEPORTATION: return 20;
				case MODE_ID_DRAG: return 7;
				case MODE_ID_RAY: return 40;
				case MODE_ID_BULLET: return 30;
				default: return 0;
			}
		}
		
		public static Item getNeedMaterialItem(int mode)
		{
			switch(mode)
			{
				case MODE_ID_LIGHTNING: return XCraftMaterials.X_DIAMOND;
				case MODE_ID_TELEPORTATION: return XCraftMaterials.X_EMERALD;
				case MODE_ID_DRAG: return XCraftMaterials.X_INGOT;
				case MODE_ID_RAY: return XCraftMaterials.X_RUBY;
				case MODE_ID_BULLET: return XCraftMaterials.X_EYE;
				default:return null;
			}
		}
		
		public static boolean modeUseWithoutBlock(ItemStack stack)
		{
			return modeUseWithoutBlock(getItemMode(stack));
		}
		
		public static boolean modeUseWithoutBlock(int mode)
		{
			return (mode==MODE_ID_DEFAULT||mode==MODE_ID_RAY||mode==MODE_ID_BULLET);
		}
		
		public static boolean modeCanBeAutomatize(ItemStack stack)
		{
			return modeCanBeAutomatize(getItemMode(stack));
		}
		
		public static boolean modeCanBeAutomatize(int mode)
		{
			return (mode==MODE_ID_LIGHTNING||mode==MODE_ID_TELEPORTATION||mode==MODE_ID_RAY);
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
		 * @return The Succeeded Mode or -1 ( Item usage failed ) ,-2 ( Invalid item usage mode ),-3 ( Invalid params )
		 */
		public static int XItemUseWithoutCD(World world,EntityPlayer player,BlockPos pos,int mode,EnumFacing side,float hitX,float hitY,float hitZ)
		{
			//Check Params
			if(player==null&&pos==null) return -3;
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
				case MODE_ID_LIGHTNING:
					if(pos==null) break;
					if(!world.isRemote) world.addWeatherEffect(new EntityLightningBolt(world,x,y,z,false));
					return mode;
				case MODE_ID_TELEPORTATION:
					if(pos==null) break;
					XCraftTools.teleportBlock(world,pos,8);
					world.playSound(null,x,y,z,SoundEvents.ENTITY_ENDERMEN_TELEPORT,SoundCategory.NEUTRAL,0.75F,1F / (itemRand.nextFloat()*0.4F+0.8F));
					return mode;
				case MODE_ID_DRAG:
					if(player==null||pos==null) break;
					//x -> dx ,y -> dy ,z -> dz
					x=x+0.5-player.posX;
					y=y+0.5-player.posY;
					z=z+0.5-player.posZ;
					double distanceValue=1+x*x+y*y+z*z;
					player.motionX+=1.25*(x*Math.abs(x))/distanceValue;
					player.motionY+=1.25*(y*Math.abs(y))/distanceValue;
					player.motionZ+=1.25*(z*Math.abs(z))/distanceValue;
					world.playSound(null,player.posX,player.posY,player.posZ,SoundEvents.ENTITY_ENDERPEARL_THROW,SoundCategory.NEUTRAL,0.75F,1F / (itemRand.nextFloat()*0.4F+0.8F));
					return mode;
				case MODE_ID_RAY:
					if(player==null) XBoss.redRayHurtNearbyEntities(world,x,y,z,8,3,false,false);
					else XBoss.redRayHurtNearbyEntities(world,player,12,-2,true,false);
					return mode;
				case MODE_ID_BULLET:
					if(player==null||world.isRemote) break;
					Vec3d lookVec=player.getLookVec();
					XBoss.EntityXBossBullet bullet=new XBoss.EntityXBossBullet(world,player,
						player.posX+lookVec.x,player.posY+player.getEyeHeight()+lookVec.y,player.posZ+lookVec.z,
							player.motionX+lookVec.x*0.02,player.motionY+(lookVec.y-1.125)*0.0005,player.motionZ+lookVec.z*0.02);
					bullet.setVelocity(lookVec.x,lookVec.y,lookVec.z);
					world.spawnEntity(bullet);
					world.playSound(null,bullet.posX,bullet.posY,bullet.posZ,SoundEvents.ENTITY_ENDERPEARL_THROW,SoundCategory.NEUTRAL,0.5F,0.4F / (itemRand.nextFloat()*0.4F+0.8F));
					return mode;
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
			if(!world.isRemote)
			{
				int cd=getCooldownByMode(result);//Returns -1 means usage failed.
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
			if(world.isRemote) return super.onItemRightClick(world,player,hand);
			//Get Stack
			ItemStack itemstack=player.getHeldItem(hand);
			if(hand!=EnumHand.MAIN_HAND||!player.isSneaking())
			{
				//Some Mode Uses Without Block
				if(XItem.modeUseWithoutBlock(XItem.getItemMode(itemstack)))
				{
					XItemUse(world,player,null,itemstack,null,(float)player.posX,(float)player.posY,(float)player.posZ,true);
					return new ActionResult<ItemStack>(EnumActionResult.SUCCESS,itemstack);
				}
			}
			//Turn Mode ( metadata <=> mode ,Need Materials) ,only MAIN HAND
			else if(player.isSneaking()&&!player.getCooldownTracker().hasCooldown(X_ITEM))
			{
				int oldMode=getItemMode(itemstack),newMode=(oldMode+1)%NUM_MODES;
				//If in CreativeMode
				if(player.capabilities.isCreativeMode)
				{
					directTurnPlayerItemMode(player,itemstack,oldMode,newMode,null,false);
					return new ActionResult<ItemStack>(EnumActionResult.SUCCESS,itemstack);
				}
				//Else than
				while(newMode!=oldMode)
				{
					//Check Materials
					ItemStack material=XuphoriumCraft.findItemInPlayerInventory(player,getNeedMaterialItem(newMode));
					if(getNeedMaterialItem(newMode)==null||!material.isEmpty())
					{
						//Turn Mode Succeeded
						directTurnPlayerItemMode(player,itemstack,oldMode,newMode,material,true);
						return new ActionResult<ItemStack>(EnumActionResult.SUCCESS,itemstack);
					}
					//If this mode can't be turn to
					newMode=(newMode+1)%NUM_MODES;
				}
				//Turn Mode Failed
				player.sendMessage(new TextComponentString(TextFormatting.RED+"No material to turn mode"));
			}
			return new ActionResult<ItemStack>(EnumActionResult.FAIL,itemstack);
		}
		
		public static void directTurnPlayerItemMode(EntityPlayer player,ItemStack itemStack,int oldMode,int newMode,ItemStack newModeMaterial,boolean checkMaterial)
		{
			//setMode
			setItemMode(itemStack,newMode);
			//TRANSLATED CLIENT MESSAGE
			player.sendMessage(new TextComponentString(TextFormatting.GOLD+"Now turn to mode #"+newMode));
			//Cooldown
			player.getCooldownTracker().setCooldown(X_ITEM,20);
			//==If Check Material==//
			if(!checkMaterial) return;
			//Load out old Materials
			if(getNeedMaterialItem(oldMode)!=null) player.addItemStackToInventory(new ItemStack(getNeedMaterialItem(oldMode),1,0));
			//Load in new Materials
			if(!newModeMaterial.isEmpty())
			{
				newModeMaterial.shrink(1);
				if(newModeMaterial.isEmpty()) player.inventory.deleteStack(newModeMaterial);
			}
		}

		@Override
		public void onUpdate(ItemStack itemstack,World world,Entity entity,int itemSlot,boolean isSelected)
		{
			super.onUpdate(itemstack,world,entity,itemSlot,isSelected);
			if(!world.isRemote&&entity instanceof EntityLivingBase)
			{
				EntityLivingBase entityLivingBase=(EntityLivingBase)entity;
				if(entityLivingBase.getHeldItemMainhand().equals(itemstack))
				{
					entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.SPEED,10,4,true,true));
					entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.HASTE,10,2,true,true));
					entityLivingBase.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST,10,2,true,true));
				}
			}
		}
	}
	
	//================X-Magnet================//
	public static class XMagnet extends XCraftToolCommon
	{
		//========Static Variable & Function========//
		public static final int NUM_MODES=4;
		
		public static final int MODE_ID_DEFAULT=0;
		public static final int MODE_ID_NEGATIVE=1;
		public static final int MODE_ID_POWERED=2;
		public static final int MODE_ID_POWERED_EXTRA=3;
		
		public static int getItemMode(ItemStack stack)
		{
			return stack.getMetadata();
		}
		
		public static boolean setItemMode(ItemStack stack,int mode)
		{
			stack.setItemDamage(mode);
			return true;
		}
		
		public static boolean modeCanAffectProjectiles(int mode)
		{
			return mode==MODE_ID_NEGATIVE||mode==MODE_ID_POWERED_EXTRA;
		}
		
		public static double getForceValueByMode(int mode)
		{
			return (mode==MODE_ID_NEGATIVE)?-1:(mode-1);
		}
		
		public static Item getNeedMaterialItem(int mode)
		{
			switch(mode)
			{
				case MODE_ID_NEGATIVE: return XCraftMaterials.X_EYE;
				case MODE_ID_POWERED: return XCraftMaterials.X_INGOT;
				case MODE_ID_POWERED_EXTRA: return XCraftMaterials.X_DIAMOND;
				default:return null;
			}
		}
		
		public static double[] calculateGravityVector(double dx,double dy,double dz,double value)
		{
			return calculateGravityVector(dx,dy,dz,value,0);
		}
		
		public static double[] calculateGravityVector(double dx,double dy,double dz,double value,double distanceSquareOffset)
		{
			//From Newton's universal gravitation formula
			//From point to Origin
			double distanceSquare=dx*dx+dy*dy+dz*dz+distanceSquareOffset;//Avoid from divide zero
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
		
		//============Item Events============//
		public ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer player,EnumHand hand)
		{
			ItemStack itemstack=player.getHeldItem(hand);
			//Turn Mode with Cooldown ,only MAIN HAND
			if(!world.isRemote&&player.isSneaking()&&hand==EnumHand.MAIN_HAND&&!player.getCooldownTracker().hasCooldown(X_MAGNET))
			{
				int oldMode=getItemMode(itemstack),newMode=(oldMode+1)%NUM_MODES;
				//If in CreativeMode
				if(player.capabilities.isCreativeMode)
				{
					directTurnPlayerItemMode(player,itemstack,oldMode,newMode,null,false);
					return new ActionResult<ItemStack>(EnumActionResult.SUCCESS,itemstack);
				}
				//Else than
				while(newMode!=oldMode)
				{
					//Check Materials
					ItemStack material=XuphoriumCraft.findItemInPlayerInventory(player,getNeedMaterialItem(newMode));
					if(getNeedMaterialItem(newMode)==null||!material.isEmpty())
					{
						//Turn Mode Succeeded
						directTurnPlayerItemMode(player,itemstack,oldMode,newMode,material,true);
						return new ActionResult<ItemStack>(EnumActionResult.SUCCESS,itemstack);
					}
					//If this mode can't be turn to
					newMode=(newMode+1)%NUM_MODES;
				}
				//Turn Mode Failed
				player.sendMessage(new TextComponentString(TextFormatting.RED+"No material to turn mode"));
			}
			return new ActionResult<>(EnumActionResult.FAIL,itemstack);
		}
		
		public static void directTurnPlayerItemMode(EntityPlayer player,ItemStack itemStack,int oldMode,int newMode,ItemStack newModeMaterial,boolean checkMaterial)
		{
			//setMode
			setItemMode(itemStack,newMode);
			//TRANSLATED CLIENT MESSAGE
			player.sendMessage(new TextComponentString(TextFormatting.GOLD+"Now turn to mode #"+newMode));
			//Cooldown
			player.getCooldownTracker().setCooldown(X_MAGNET,20);
			//==If Check Material==//
			if(!checkMaterial) return;
			//Load out old Materials
			if(getNeedMaterialItem(oldMode)!=null) player.addItemStackToInventory(new ItemStack(getNeedMaterialItem(oldMode),1,0));
			//Load in new Materials
			if(!newModeMaterial.isEmpty())
			{
				newModeMaterial.shrink(1);
				if(newModeMaterial.isEmpty()) player.inventory.deleteStack(newModeMaterial);
			}
		}

		@Override
		public void onUpdate(ItemStack itemstack,World world,Entity entity,int par4,boolean par5)
		{
			super.onUpdate(itemstack,world,entity,par4,par5);
			int mode=getItemMode(itemstack);
			//judge mode
			if(mode==MODE_ID_DEFAULT) return;
			//start gravityForce
			if(entity instanceof EntityLivingBase&&mode>0)
			{
				if(((EntityLivingBase)entity).getHeldItemMainhand().equals(itemstack)||
				   ((EntityLivingBase)entity).getHeldItemOffhand().equals(itemstack))
				{
					xMagnetCreateGravity(world,entity,mode);
				}
			}
		}
		
		public static void xMagnetCreateGravity(World world,Entity entity,int mode)
		{
			List<Entity> entities=world.loadedEntityList;
			double[] gravityForce;
			for(Entity entity1 : entities)
			{
				if(entity1 instanceof EntityItem||entity1 instanceof EntityXPOrb||
						modeCanAffectProjectiles(mode)&&(
								entity1 instanceof IProjectile||entity1 instanceof EntityFireball
						))
				{
					gravityForce=calculateGravityVector(entity1.posX-entity.posX,
							entity1.posY-entity.posY,
							entity1.posZ-entity.posZ,
							getForceValueByMode(mode),
							1
					);//1 means the scale of force
					entity1.motionX+=gravityForce[0];
					entity1.motionY+=gravityForce[1];
					entity1.motionZ+=gravityForce[2];
				}
			}
		}
	}
	
	//================X-Sword================//
	public static class XSword extends ItemSword
	{
		//========Static Variable & Function========//
		public static final int NUM_MODES=4;
		
		public static final int MODE_ID_DEFAULT=0;
		public static final int MODE_ID_SELF_ATTACK=1;
		public static final int MODE_ID_OTHER_ATTACK=2;
		public static final int MODE_ID_AREA_ATTACK=3;
		
		public static int getSwordMode(ItemStack stack)
		{
			return XuphoriumCraft.XCraftItemCommon.getItemNBTMode(stack);
		}
		
		public static boolean setSwordMode(ItemStack stack,int mode)
		{
			return XuphoriumCraft.XCraftItemCommon.setItemNBTMode(stack,mode);
		}
		
		public static int getCooldownByMode(int mode)
		{
			switch(mode)
			{
				case MODE_ID_SELF_ATTACK: return 20;
				case MODE_ID_OTHER_ATTACK: return 30;
				case MODE_ID_AREA_ATTACK: return 40;
				default: return 0;
			}
		}
		
		public static Item getNeedMaterialItem(int mode)
		{
			switch(mode)
			{
				case MODE_ID_SELF_ATTACK: return XCraftMaterials.X_DIAMOND;
				case MODE_ID_OTHER_ATTACK: return XCraftMaterials.X_EMERALD;
				case MODE_ID_AREA_ATTACK: return XCraftMaterials.X_RUBY;
				default:return null;
			}
		}
		
		/**
		 * If swordStack==null ,than don't use cooldown
		 */
		public static int swordEnrageTarget(World world,EntityLivingBase attacker,EntityLiving target,ItemStack swordStack,int mode,boolean generateParticle)
		{
			int result=swordEnrageTarget(world,attacker,target,mode,generateParticle);
			if(swordStack!=null&&attacker instanceof EntityPlayer)
			{
				if(result>0)
				{
					int cd=getCooldownByMode(mode);
					if(cd>0) ((EntityPlayer)attacker).getCooldownTracker().setCooldown(swordStack.getItem(),cd);
				}
			}
			return result;
		}
		
		/**
		 * @return succeed mode or -1
		 */
		public static int swordEnrageTarget(World world,EntityLivingBase attacker,EntityLiving target,int mode,boolean generateParticle)
		{
			EntityLiving nearestEntity;
			List<Entity> entities;
			double dx,dy,dz,distanceSquare,nearestDistanceSquare;
			switch(mode)
			{
				case MODE_ID_SELF_ATTACK:
					target.setAttackTarget(target);
					//Particle
					if(world instanceof WorldServer)
					{
						((WorldServer)world).spawnParticle(EnumParticleTypes.END_ROD,
								target.posX,target.posY,target.posZ,16,
								target.width*0.5,target.height*0.5,target.width*0.5,
								0
						);
					}
					return mode;
				case MODE_ID_OTHER_ATTACK:
					entities=world.loadedEntityList;
					nearestDistanceSquare=0;
					nearestEntity=null;
					for(Entity entity3 : entities)
					{
						if(entity3==target||!(entity3 instanceof EntityLiving)) continue;
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
					//Target
					if(nearestEntity!=null) nearestEntity.setAttackTarget(target);
					target.setAttackTarget(nearestEntity);
					//Particle
					if(generateParticle) XuphoriumCraft.generateParticleRay(world,nearestEntity,target,32,EnumParticleTypes.END_ROD);
					return mode;
				case MODE_ID_AREA_ATTACK:
					entities=world.loadedEntityList;
					for(Entity entity4 : entities)
					{
						if(entity4!=target&&entity4 instanceof EntityLiving)
						{
							dx=target.posX-entity4.posX;
							dy=target.posY-entity4.posY;
							dz=target.posZ-entity4.posZ;
							if(dx*dx+dy*dy+dz*dz<=256)
							{
								//Target
								((EntityLiving)entity4).setAttackTarget(target);
								//Particle
								if(generateParticle) XuphoriumCraft.generateParticleRay(world,entity4,target,32,EnumParticleTypes.END_ROD);
							}
						}
					}
					return mode;
			}
			return -1;
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
			this.addPropertyOverride(new ResourceLocation("mode"),new IItemPropertyGetter()
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
			float result=(getSwordMode(stack)==MODE_ID_DEFAULT?3:1)*super.getDestroySpeed(stack,state);
			//result*=2-this.getMetadata(stack)/this.getMaxDamage();
			return result;
		}
		/*
		@Override
		public float getAttackDamage()
		{
			return (getSwordMode(this.getDefaultInstance())==MODE_ID_DEFAULT?2:1)*super.getAttackDamage();
		}*/
		
		//========Item Events========//
		/*@Override
		public boolean onBlockDestroyed(ItemStack stack,World worldIn,IBlockState state,BlockPos pos,EntityLivingBase entityLiving)
		{
			return super.onBlockDestroyed(stack,worldIn,state,pos,entityLiving);
		}*/
		
		@Override
		public ActionResult<ItemStack> onItemRightClick(World world,EntityPlayer player,EnumHand hand)
		{
			ItemStack itemstack=player.getHeldItem(hand);
			//Turn Mode with Cooldown ,only MAIN HAND
			if(!world.isRemote&&hand==EnumHand.MAIN_HAND&&player.isSneaking()&&!player.getCooldownTracker().hasCooldown(X_SWORD))
			{
				int oldMode=getSwordMode(itemstack),newMode=(oldMode+1)%NUM_MODES;
				if(newMode!=oldMode)
				{
					setSwordMode(itemstack,newMode);
					//TODO: TRANSLATED CLIENT MESSAGE
					player.sendMessage(new TextComponentString(TextFormatting.GOLD+"Now turn to mode #"+newMode));
					player.getCooldownTracker().setCooldown(X_SWORD,20);
					return new ActionResult<ItemStack>(EnumActionResult.SUCCESS,itemstack);
				}
			}
			//else XCraftTools.addToolBoostEffects(player);
			return new ActionResult<ItemStack>(EnumActionResult.FAIL,itemstack);
		}

		@Override
		public boolean hitEntity(ItemStack itemstack,EntityLivingBase target,EntityLivingBase attacker)
		{
			World world=attacker.world;
			boolean result=super.hitEntity(itemstack,attacker,target);
			if(world.isRemote) return result;
			if(target!=null&&target instanceof EntityLiving&&!(attacker instanceof EntityPlayer&&((EntityPlayer)attacker).getCooldownTracker().hasCooldown(X_SWORD)))
			{
				swordEnrageTarget(world,attacker,(EntityLiving)target,getSwordMode(itemstack),true);
			}
			return result;
		}
		
		@Override
		public boolean itemInteractionForEntity(ItemStack itemstack,EntityPlayer player,EntityLivingBase target,EnumHand hand)
		{
			if(target.world.isRemote) return false;
			//Enrage Target Without Attack
			if(target instanceof EntityLiving&&!player.getCooldownTracker().hasCooldown(X_SWORD))
			{
				swordEnrageTarget(target.world,player,(EntityLiving)target,getSwordMode(itemstack),true);
				return true;
			}
			return super.itemInteractionForEntity(itemstack,player,target,hand);
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
			if(this.toolClass==null) return super.getToolClasses(stack);
			HashMap<String,Integer> result=new HashMap<String,Integer>();
			result.put(this.toolClass,4);
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
			this.addPropertyOverride(new ResourceLocation("deactivated"),new IItemPropertyGetter()
			{
				@SideOnly(Side.CLIENT)
				public float apply(ItemStack stack,@Nullable World worldIn,@Nullable EntityLivingBase entityIn)
				{
					return XShield.isUsable(stack)?0.0F:1.0F;
				}
			});
		}
		
		public static java.util.Random getItemRand()
		{
			return itemRand;
		}

		public ItemStack getDefaultInstance()
		{
			return getDefaultStack();
		}
		
		public static ItemStack getDefaultStack(int amount)
		{
			ItemStack result=new ItemStack(X_SHIELD,1);
			result.addEnchantment(Enchantments.MENDING,1);
			return result;
		}
		
		public static ItemStack getDefaultStack()
		{
			return getDefaultStack(1);
		}
		
		public static boolean isUsable(ItemStack stack)
		{
			return stack.getItemDamage()<stack.getMaxDamage()-1;
		}
		
		/**
		 * Return whether this item is repairable in an anvil.
		 *
		 * @param toRepair the {@code ItemStack} being repaired
		 * @param repair the {@code ItemStack} being used to perform the repair
		 */
		public boolean getIsRepairable(ItemStack toRepair,ItemStack repair)
		{
			Item item=repair.getItem();
			return (item==XCraftMaterials.X_CATALYST||
					item==XCraftMaterials.X_CRYSTAL||
					item==XCraftMaterials.X_CRYSTAL_LEFT||
					item==XCraftMaterials.X_CRYSTAL_RIGHT
			)||super.getIsRepairable(toRepair,repair);
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
