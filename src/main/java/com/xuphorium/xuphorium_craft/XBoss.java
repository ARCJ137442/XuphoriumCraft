package com.xuphorium.xuphorium_craft;

import net.minecraft.entity.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.BossInfo;

import net.minecraft.util.math.RayTraceResult;

import net.minecraft.item.ItemStack;
import net.minecraft.inventory.EntityEquipmentSlot;

import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.projectile.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.ai.*;

import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.Minecraft;

import net.minecraft.potion.PotionEffect;

import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.init.Enchantments;

import java.util.ArrayList;
import java.util.List;
/* 
import com.xuphorium.xuphorium_craft.*;
import com.xuphorium.xuphorium_craft.common.*;
import com.xuphorium.xuphorium_craft.proxy.*;
import com.xuphorium.xuphorium_craft.entity.*;
import com.xuphorium.xuphorium_craft.block.*;
import com.xuphorium.xuphorium_craft.item.*; */

@XuphoriumCraftElements.ModElement.Tag
public class XBoss extends XuphoriumCraftElements.ModElement
{
	//============Register About============//
	public static final int ENTITYID=5;
	public static final int ENTITYID_RANGED=6;

	public XBoss(XuphoriumCraftElements instance)
	{
		super(instance,2);
	}

	@Override
	public void initElements()
	{
		elements.entities.add(()->EntityEntryBuilder.create().entity(EntityXBoss.class).id(new ResourceLocation(XuphoriumCraft.MODID,"x_boss"),ENTITYID).name("x_boss").tracker(64,1,true).egg(-3355444,-16711681).build());
		elements.entities.add(()->EntityEntryBuilder.create().entity(EntityXBossBullet.class).id(new ResourceLocation(XuphoriumCraft.MODID,"x_boss_bullet"),ENTITYID_RANGED).name("x_boss_bullet").tracker(64,1,true).build());
	}
	
	@SideOnly(Side.CLIENT)
	//@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityXBoss.class,renderManager -> {
			RenderBiped customRender=new RenderBiped(renderManager,new ModelBiped(),0.5f)
			{
				protected ResourceLocation getEntityTexture(Entity entity)
				{
					return new ResourceLocation("xuphorium_craft:textures/entities/x_boss.png");
				}
			};
			customRender.addLayer(new net.minecraft.client.renderer.entity.layers.LayerHeldItem(customRender));
			customRender.addLayer(new net.minecraft.client.renderer.entity.layers.LayerBipedArmor(customRender)
			{
				protected void initArmor()
				{
					this.modelLeggings=new ModelBiped();
					this.modelArmor=new ModelBiped();
				}
			});
			return customRender;
		});
		RenderingRegistry.registerEntityRenderingHandler(
			EntityXBossBullet.class,renderManager ->new RenderSnowball<EntityXBossBullet>(
				renderManager,null,Minecraft.getMinecraft().getRenderItem()
			)
			{
				public ItemStack getStackToRender(EntityXBossBullet entity)
				{
					return new ItemStack(XCraftMaterials.X_PEARL,1);
				}
			});
	}
	
	//====X-Boss's Punch====//
	public static void punchEntities(World world,Entity host,double range,double distanceSquareOffset)
	{
		punchEntities(world,host,host.posX,host.posY,host.posZ,range,distanceSquareOffset);
	}

	public static void punchEntities(World world,Entity host,double range,double value,double distanceSquareOffset)
	{
		punchEntities(world,host,host.posX,host.posY,host.posZ,range,value,distanceSquareOffset);
	}

	public static void punchEntities(World world,Entity host,double x,double y,double z,double range,double value)
	{
		punchEntities(world,host,x,y,z,range,value,0);
	}
	
	public static void punchEntities(World world,Entity host,double x,double y,double z,double range,double value,double distanceSquareOffset)
	{
		List<Entity> entities=world.loadedEntityList;
		double dx,dy,dz,dSquare;
		for(Entity entity : entities)
		{
			if(entity==host) continue;
			dx=entity.posX-x;
			dy=entity.posY-y;
			dz=entity.posZ-z;
			dSquare=dx*dx+dy*dy+dz*dz+distanceSquareOffset;
			if(dx*dy*dz!=0&&dSquare<=range*range)
			{
				entity.motionX+=value*dx/dSquare;
				entity.motionY+=value*dy/dSquare;
				entity.motionZ+=value*dz/dSquare;
			}
		}
	}
	
	//====X-Boss's Red Ray====//
	/**
	*@see #redRayHurtNearbyEntities(World,EntityLivingBase,double,double,double,double,double,boolean,boolean)
	 */
	public static void redRayHurtNearbyEntities(World world,double x,double y,double z,
	                                            double radiusFlag,double attackDamageFlag,
	                                            boolean ignorePlayer,boolean ignoreXBoss)
	{
		redRayHurtNearbyEntities(world,null,x,y,z,radiusFlag,attackDamageFlag,ignorePlayer,ignoreXBoss);
	}
	
	/**
	*@see #redRayHurtNearbyEntities(World,EntityLivingBase,double,double,double,double,double,boolean,boolean)
	 */
	public static void redRayHurtNearbyEntities(World world,double x,double y,double z,double radiusFlag,double attackDamageFlag)
	{
		redRayHurtNearbyEntities(world,x,y,z,radiusFlag,attackDamageFlag,false,false);
	}
	
	/**
	*@see #redRayHurtNearbyEntities(World,EntityLivingBase,double,double,double,double,double,boolean,boolean)
	 */
	public static void redRayHurtNearbyEntities(World world,EntityLivingBase attacker,
	                                            double radiusFlag,double attackDamageFlag,
	                                            boolean ignorePlayer,boolean ignoreXBoss)
	{
		redRayHurtNearbyEntities(world,attacker,attacker.posX,attacker.posY+attacker.getEyeHeight(),attacker.posZ,radiusFlag,attackDamageFlag,ignorePlayer,ignoreXBoss);
	}
	
	/**
	*@param attackDamageFlag
	* positive number : const damage as the flag.
	* -1 : uses distance as damage.
	* <-1 : uses as attacker's attack (need attacker!=null,else than uses -1).
	*@param radiusFlag
	* positive number : uses distance to select target.
	* 0 : random select target all the list.
	* negative number : random select target in distance.
	 */
	public static void redRayHurtNearbyEntities(World world,EntityLivingBase attacker,double sourceX,double sourceY,double sourceZ,
	                                            double radiusFlag,double attackDamageFlag,
	                                            boolean ignorePlayer,boolean ignoreXBoss)
	{
		//damage : The same as attackDamageFlag
		double damage=attackDamageFlag;
		if(attacker!=null&&attackDamageFlag<-1) damage=-2;//means uses as attacker's attack
		else if(attackDamageFlag<-1) damage=-1;
		try
		{
			List<Entity> entities=world.loadedEntityList;
			List<EntityLivingBase> toAttackEntities=new ArrayList<>();
			//Get
			for(Entity entity: entities)
			{
				if(entity instanceof EntityLivingBase)
				{
					//Ignore Special Entities
					if(entity==attacker||ignoreXBoss&&entity instanceof EntityXBoss||ignorePlayer&&entity instanceof EntityPlayer) continue;
					toAttackEntities.add((EntityLivingBase)entity);
				}
			}
			try
			{
				for(EntityLivingBase entity: toAttackEntities)
				{
					//Attack
					double distanceSquare=entity.getDistanceSq(sourceX,sourceY,sourceZ);
					if((radiusFlag==0&&Math.random()<Math.random())||
							radiusFlag!=0&&distanceSquare<=radiusFlag*radiusFlag&&(radiusFlag>0||Math.random()<Math.random()))
					{
						//damage -> damageFlag
						damage=(attackDamageFlag==-1||damage==-1)?Math.sqrt(distanceSquare):damage;
						//Damage the Target
						if(attacker!=null) redRayHurtEntity(attacker,sourceX,sourceY,sourceZ,entity,damage);
						else redRayHurtEntity(sourceX,sourceY,sourceZ,entity,damage);
					}
				}
			}
			catch(Exception exception)
			{
				XuphoriumCraft.LOGGER.error(exception.toString());
				exception.printStackTrace();
			}
		}
		catch(Exception exception)
		{
			//XuphoriumCraft.LOGGER.error(exception.toString());
			//exception.printStackTrace();
		}
	}
	
	public static void redRayHurtNearbyEntities(World world,EntityLivingBase attacker,double radiusFlag,double attackDamageFlag)
	{
		redRayHurtNearbyEntities(world,attacker,radiusFlag,attackDamageFlag,false,false);
	}
	
	/**
	*@see #redRayHurtEntity(EntityLivingBase,double,double,double,EntityLivingBase,double)
	 */
	public static void redRayHurtEntity(EntityLivingBase attacker,EntityLivingBase target,double distanceFlag)
	{
		redRayHurtEntity(attacker,attacker.posX,attacker.posY+attacker.getEyeHeight(),attacker.posZ,target,distanceFlag);
	}
	
	/**
	*@param distanceFlag '=0'"' : attack as attacker. '>0' : static damage. '<0' : damage by distance
	 */
	public static void redRayHurtEntity(EntityLivingBase attacker,
	                                    double sourceX,double sourceY,double sourceZ,
	                                    EntityLivingBase target,double distanceFlag)
	{
		if(target==null||target.isDead) return;
		//Particle
		generateRedWay(target.world,sourceX,sourceY,sourceZ,target.posX,target.posY,target.posZ);
		//Attack Entity as Direct Attack
		if(distanceFlag<0)
		{
			if(attacker instanceof EntityPlayer)
			{
				((EntityPlayer)attacker).attackTargetEntityWithCurrentItem(target);
				((EntityPlayer)attacker).resetCooldown();
			}
			else attacker.attackEntityAsMob(target);
		}
		//Attack Entity as Magic
		else target.attackEntityFrom(DamageSource.causeMobDamage(attacker),distanceFlag>0?(float)distanceFlag:((float)(Math.abs(distanceFlag))));
	}
	
	public static void redRayHurtEntity(double x,double y,double z,EntityLivingBase target,double damage)
	{
		if(target==null||target.isDead) return;
		//Particle
		generateRedWay(target.world,x,y,z,target.posX,target.posY,target.posZ);
		//Attack Entity as Magic
		target.attackEntityFrom(DamageSource.causeMobDamage(null),(float)damage);
	}
	
	public static void generateRedWay(World world,double x1,double y1,double z1,double x2,double y2,double z2)
	{
		generateRedWay(world,x1,y1,z1,x2,y2,z2,32);
	}
	
	public static void generateRedWay(World world,double x1,double y1,double z1,double x2,double y2,double z2,int numParticle)
	{
		XuphoriumCraft.generateParticleRay(world,x1,y1,z1,x2,y2,z2,numParticle,EnumParticleTypes.REDSTONE);
	}

	public static void rangedAttackXBossBullet(EntityLivingBase attacker,Entity target,double shootPower,boolean isBossBullet)
	{
		double dx=target.posX-attacker.posX;
		double dy=(target.posY+target.getEyeHeight())-(attacker.posY+attacker.getEyeHeight());
		double dz=target.posZ-attacker.posZ;
		double distance=MathHelper.sqrt(dx*dx+dy*dy+dz*dz);
		dx*=shootPower/distance;
		dy*=shootPower/distance;
		dz*=shootPower/distance;
		EntityXBossBullet bullet=new EntityXBossBullet(attacker.world,attacker,
				attacker.posX+dx,attacker.posY+attacker.getEyeHeight()+dy,attacker.posZ+dz,
				dx*0.001,dy*0.001,dz*0.001,isBossBullet);
		bullet.setVelocity(dx,dy,dz);
		attacker.world.spawnEntity(bullet);
	}
	
	public static void xBossRangedNearbyBulletAttack(EntityLivingBase attacker,Entity target,double minDistanceSqr,double maxDistanceSqr,double shootPower,boolean isBossBullet)
	{
		//Area Splash
		List<Entity> entities=attacker.world.loadedEntityList;
		List<EntityLivingBase> toAttackEntities=new ArrayList<>();
		double distanceSq;
		for(Entity entity: entities)
		{
			if(entity instanceof EntityLivingBase&&entity.getUniqueID()!=attacker.getUniqueID())
			{
				distanceSq=entity.getDistanceSq(target.posX,target.posY,target.posZ);
				if(distanceSq>minDistanceSqr&&distanceSq<maxDistanceSqr) toAttackEntities.add((EntityLivingBase)entity);
			}
		}
		for(EntityLivingBase entity: toAttackEntities)
		{
			//Attack
			rangedAttackXBossBullet(attacker,entity,shootPower,isBossBullet);
		}
	}
	
	//====Tool Functions====//
	
	//============Entity Classes============//
	public static class EntityXBoss extends EntityMob implements IRangedAttackMob
	{
		public static final ResourceLocation LOOT_TABLE_DEATH=LootTableList.register(new ResourceLocation("xuphorium_craft:entities/x_boss"));
		
		protected int randomHurtTick=20;
		protected int modeTick=200;
		public int defenceCooldown=0;

		public EntityXBoss(World world)
		{
			super(world);
			this.setSize(0.6f,1.8f);
			this.experienceValue=128;
			this.isImmuneToFire=true;
			this.setCustomNameTag("X-BOSS");
			this.setAlwaysRenderNameTag(true);
			this.enablePersistence();
			this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND,getWeapon());
			this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND,getXMagnet());
			this.setItemStackToSlot(EntityEquipmentSlot.HEAD,getHelmet());
			this.setItemStackToSlot(EntityEquipmentSlot.CHEST,getChestplate());
			this.setItemStackToSlot(EntityEquipmentSlot.LEGS,getLeggings());
			this.setItemStackToSlot(EntityEquipmentSlot.FEET,getBoots());
		}
		
		@Override
		protected ResourceLocation getLootTable()
		{
			return XBoss.EntityXBoss.LOOT_TABLE_DEATH;
		}
		
		public static ItemStack getWeapon()
		{
			ItemStack result=new ItemStack(XCraftTools.X_ITEM);
			XCraftTools.XItem.setItemMode(result,XCraftTools.XItem.MODE_ID_RAY);
			appendWeaponEnchants(result);
			return result;
		}
		
		public static void appendWeaponEnchants(ItemStack stack)
		{
			stack.addEnchantment(Enchantments.SHARPNESS,5);
			stack.addEnchantment(Enchantments.SMITE,5);
			stack.addEnchantment(Enchantments.BANE_OF_ARTHROPODS,5);
			stack.addEnchantment(Enchantments.KNOCKBACK,2);
			stack.addEnchantment(Enchantments.LOOTING,10);
			stack.addEnchantment(Enchantments.SWEEPING,3);
			stack.addEnchantment(Enchantments.SILK_TOUCH,1);
			stack.addEnchantment(Enchantments.UNBREAKING,3);
			stack.addEnchantment(Enchantments.MENDING,1);
		}
		
		public static ItemStack getShield()
		{
			ItemStack result=new ItemStack(XCraftTools.X_SHIELD);
			result.addEnchantment(Enchantments.PROTECTION,10);
			result.addEnchantment(Enchantments.SHARPNESS,5);
			result.addEnchantment(Enchantments.UNBREAKING,3);
			result.addEnchantment(Enchantments.MENDING,1);
			return result;
		}
		
		public static ItemStack getHelmet()
		{
			ItemStack result=new ItemStack(XArmor.X_HELMET);
			result.addEnchantment(Enchantments.DEPTH_STRIDER,3);
			result.addEnchantment(Enchantments.RESPIRATION,3);
			result.addEnchantment(Enchantments.UNBREAKING,3);
			result.addEnchantment(Enchantments.MENDING,1);
			return result;
		}
		
		public static ItemStack getChestplate()
		{
			ItemStack result=new ItemStack(XArmor.X_CHESTPLATE);
			result.addEnchantment(Enchantments.UNBREAKING,3);
			result.addEnchantment(Enchantments.MENDING,1);
			return result;
		}
		
		public static ItemStack getLeggings()
		{
			ItemStack result=new ItemStack(XArmor.X_LEGGINGS);
			result.addEnchantment(Enchantments.UNBREAKING,3);
			result.addEnchantment(Enchantments.MENDING,1);
			return result;
		}
		
		public static ItemStack getBoots()
		{
			ItemStack result=new ItemStack(XArmor.X_BOOTS);
			result.addEnchantment(Enchantments.DEPTH_STRIDER,3);
			result.addEnchantment(Enchantments.RESPIRATION,3);
			result.addEnchantment(Enchantments.UNBREAKING,3);
			result.addEnchantment(Enchantments.MENDING,1);
			return result;
		}
		
		public static ItemStack getXItemBullet()
		{
			ItemStack result=new ItemStack(XCraftTools.X_ITEM);
			XCraftTools.XItem.setItemMode(result,XCraftTools.XItem.MODE_ID_BULLET);
			appendWeaponEnchants(result);
			return result;
		}
		
		public static ItemStack getXMagnet()
		{
			ItemStack result=new ItemStack(XCraftTools.X_MAGNET);
			XCraftTools.XMagnet.setItemMode(result,XCraftTools.XMagnet.MODE_ID_NEGATIVE);
			result.addEnchantment(Enchantments.UNBREAKING,3);
			result.addEnchantment(Enchantments.MENDING,1);
			return result;
		}
		
		public static final Class[] nearestAttackableTargets=new Class[]
		{
				EntityWither.class,EntitySkeletonHorse.class,EntityZombieHorse.class,
				EntityWitch.class,EntityVillager.class,EntityEvoker.class,EntityIllusionIllager.class,EntityVindicator.class,EntityZombieVillager.class,
				EntityGolem.class,EntityEnderman.class,EntityEndermite.class,
				EntityAnimal.class,EntityPlayer.class
		};
		
		@Override
		protected void initEntityAI()
		{
			this.tasks.addTask(1,new EntityAISwimming(this));
			this.tasks.addTask(2,new EntityXBossAIAttackRangedOverridden(this,10,30,64));
			this.tasks.addTask(3,new EntityXBossAIAttackMelee(this,1,true));
			this.tasks.addTask(4,new EntityAIMoveTowardsTarget(this,1.5D,64F));
			this.tasks.addTask(5,new EntityAIMoveTowardsRestriction(this,1.0D));
			this.tasks.addTask(6,new EntityAIWatchClosest(this,XCreeper.EntityXCreeper.class,(float)16));
			this.tasks.addTask(7,new EntityAILeapAtTarget(this,(float)0.8));
			this.targetTasks.addTask(6,new EntityAIHurtByTarget(this,true));
			int i=7;
			for(Class entityClass : nearestAttackableTargets) this.targetTasks.addTask(i++,new EntityAINearestAttackableTarget(this,entityClass,false,false));
		}
		
		public EnumCreatureAttribute getCreatureAttribute()
		{
			return EnumCreatureAttribute.UNDEFINED;
		}
		
		protected boolean canDespawn()
		{
			return false;
		}
		
		public net.minecraft.util.SoundEvent getAmbientSound()
		{
			return null;
		}
		
		public net.minecraft.util.SoundEvent getHurtSound(DamageSource ds)
		{
			return SoundEvents.ENTITY_PLAYER_HURT;
		}
		
		public net.minecraft.util.SoundEvent getDeathSound()
		{
			return SoundEvents.ENTITY_PLAYER_DEATH;
		}
		
		protected float getSoundVolume()
		{
			return 1.0F;
		}
		
		@Override
		protected void applyEntityAttributes()
		{
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(0D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30D);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4D);
			this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64D);
		}
		
		//====Boss Bar====//
		public boolean isNonBoss()
		{
			return false;
		}
		
		protected final BossInfoServer bossInfo=new BossInfoServer(this.getDisplayName(),BossInfo.Color.BLUE,BossInfo.Overlay.PROGRESS);
		
		public void addTrackingPlayer(EntityPlayerMP player)
		{
			super.addTrackingPlayer(player);
			this.bossInfo.addPlayer(player);
		}
		
		public void removeTrackingPlayer(EntityPlayerMP player)
		{
			super.removeTrackingPlayer(player);
			this.bossInfo.removePlayer(player);
		}
		
		public void setSwingingArms(boolean swingingArms)
		{
		
		}
		
		//========Events========//
		public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty,IEntityLivingData livingdata)
		{
			super.onInitialSpawn(difficulty,livingdata);
			//Add Effect
			this.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST,1000000,2,false,true));
			this.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION,1000000,2,false,true));
			this.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY,200,1,true,true));
			this.addPotionEffect(new PotionEffect(MobEffects.GLOWING,200,1,true,true));
			this.addPotionEffect(new PotionEffect(MobEffects.LEVITATION,20,16,true,true));
			return livingdata;
		}
		
		public boolean attackEntityFrom(DamageSource source,float amount)
		{
			//====Block Damage====//
			//Immune Directly
			if(source==DamageSource.CACTUS||source==DamageSource.DROWN||source==DamageSource.LIGHTNING_BOLT||
				source==DamageSource.MAGIC||source==DamageSource.LAVA||source==DamageSource.FIREWORKS||
				source==DamageSource.IN_WALL||source==DamageSource.HOT_FLOOR||source==DamageSource.FALLING_BLOCK||
				source==DamageSource.DRAGON_BREATH||source==DamageSource.CRAMMING||source==DamageSource.WITHER
			) return false;
			//Immune Thorns Damage
			if(source instanceof EntityDamageSource&&((EntityDamageSource)source).getIsThornsDamage()) return false;
			//Defence
			if(source.getImmediateSource() instanceof EntityArrow||source==DamageSource.FALL||!this.isRangedMode()&&this.defenceCooldown<=0)
			{
				if(!this.isRangedMode()) this.defenceCooldown+=(int)amount*4;
				this.playSound(SoundEvents.BLOCK_ANVIL_LAND,0.75F,1.0F);
				XBoss.punchEntities(world,this,this.posX,this.posY,this.posZ,4,2,0.5);
				return false;
			}
			//====Suffer From Damage====//
			/*//Potion Effects
			this.clearActivePotions();
			this.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST,1000000,2,true,false));
			this.addPotionEffect(new PotionEffect(MobEffects.STRENGTH,40,2,true,false));
			this.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY,40,1,true,false));*/
			//Spawn Bullet
			if(this.isRangedMode())
			{
				if(!world.isRemote)
				{
					double dx,dz;
					EntityXBossBullet bullet;
					for(dx=-1D;dx<2;dx+=2)
					{
						for(dz=-1D;dz<2;dz+=2)
						{
							bullet=new EntityXBossBullet(world,this,this.posX+dx,0,this.posZ+dz,dx*0.01,-0.0375,dz*0.01);
							bullet.setVelocity(dx,0,dz);
							world.spawnEntity(bullet);
						}
					}
				}
			}
			else redRayHurtNearbyEntities(world,this,8,-2,false,true);
			return super.attackEntityFrom(source,amount);
		}
		
		public boolean attackEntityAsMob(Entity entityIn)
		{
			boolean flag=super.attackEntityAsMob(entityIn);
			try
			{
				if(flag)
				{
					if(entityIn instanceof EntityLivingBase)
					{
						EntityLivingBase entityL=(EntityLivingBase)entityIn;
						entityL.motionY+=0.875;
						world.addWeatherEffect(new EntityLightningBolt(world,entityL.posX-0.5,entityL.posY,entityL.posZ-0.5,true));
					}
				}
				return flag;
			}
			catch(Exception ignored)
			{
			
			}
			return flag;
		}
		
		public void onUpdate()
		{
			super.onUpdate();
			//Particle
			if(this.world.isRemote) this.world.spawnParticle(
					EnumParticleTypes.ENCHANTMENT_TABLE, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height - 0.25D, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5D) * 2.0D);
			//Boss Bar
			this.bossInfo.setPercent(this.getHealth()/this.getMaxHealth());
			//Mode Switch
			if(--this.modeTick<-200)
			{
				//The lower the HP percentage, the longer you stay in melee mode.
				this.modeTick=100*(3-(int)(this.getHealth()/this.getMaxHealth()));
				this.updateRangedMode(false);
			}
			else if(this.modeTick==0) this.updateRangedMode(false);
			//Defence Cooldown
			if(this.defenceCooldown>=0)
			{
				if(world instanceof WorldServer) ((WorldServer)world).spawnParticle(EnumParticleTypes.PORTAL,
						this.posX,this.posY+this.height*0.5,this.posZ,8,
						this.width*0.5,this.height*0.5,this.width*0.5,2
				);
				if(this.defenceCooldown==0)
				{
					//Particle
					if(world instanceof WorldServer) ((WorldServer)world).spawnParticle(EnumParticleTypes.SPELL_WITCH,
							this.posX,this.posY+this.height*0.5,this.posZ,24,
							this.width*0.5,this.height*0.5,this.width*0.5,0
					);
					//Heal
					this.heal(1);
				}
				this.defenceCooldown--;
			}
			//getAttackingEntity
			EntityLivingBase target=this.getAttackTarget();
			//Check self attack
			if(target==this) this.setAttackTarget(null);
			//Variable
			double dx=target==null?0:(target.posX-this.posX);
			double dy=target==null?0:(target.posY-this.posY);
			double dz=target==null?0:(target.posZ-this.posZ);
			double targetDistance=MathHelper.sqrt(dx*dx+dy*dy+dz*dz);
			//Drag to Target
			if(target!=null)
			{
				if(targetDistance>=(this.isRangedMode()?24:6))
				{
					//Motion
					this.motionX+=dx/targetDistance*0.2;
					this.motionY+=dy/targetDistance*0.2;
					this.motionZ+=dz/targetDistance*0.2;
					//Particle world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE,target.posX,target.posY,target.posZ,dx,dy,dz);
					if(this.world.isRemote) this.world.spawnParticle(
							EnumParticleTypes.ENCHANTMENT_TABLE,
							target.posX, target.posY+target.getEyeHeight(),target.posZ,
							-dx,-dy,-dz);
				}
			}
			if(!this.isRangedMode())
			{
				//Magnet Negative
				if(!this.world.isRemote) XCraftTools.XMagnet.xMagnetCreateGravity(this.world,this,1);
				//Random Hurt
				if(--randomHurtTick<0)
				{
					randomHurtTick=40;
					this.updateRangedMode(true);
					redRayHurtNearbyEntities(this.world,this,16,-1,true,true);
					if(target!=null)
					{
						//Particle
						XuphoriumCraft.generateParticleRay(target.world,
							this.posX,this.posY+this.getEyeHeight(),this.posZ,
							target.posX,target.posY+target.getEyeHeight(),target.posZ,
							((int)targetDistance)*4,1,EnumParticleTypes.SPELL_INSTANT,0,0d);
						//Direct Attack Entity
						if(this.attackEntityAsMob(target)) this.randomHurtTick=20;
					}
				}
			}
		}
		
		/**
		*Attack the specified entity using a ranged attack.
		 */
		public void attackEntityWithRangedAttack(EntityLivingBase target,float distanceFactor)
		{
			//Direct Attack
			XBoss.rangedAttackXBossBullet(this,target,1,true);
			//Update
			this.updateRangedMode(true);
			//Get
			try
			{
				XBoss.xBossRangedNearbyBulletAttack(this,target,8,48,0.75,true);
			}
			catch(Exception exception)
			{
				XuphoriumCraft.LOGGER.error(exception.toString());
				exception.printStackTrace();
			}
			this.playSound(SoundEvents.ENTITY_ENDERPEARL_THROW,0.5F,1F);
		}
		
		public boolean isRangedMode()
		{
			return this.modeTick<=0;
		}
		
		public void updateRangedMode(boolean onlyItem)
		{
			if(!onlyItem)
			{
				//Particle
				if (this.world != null && this.world instanceof WorldServer) {
					((WorldServer) world).spawnParticle(EnumParticleTypes.SPELL_INSTANT,
							this.posX, this.posY + this.height * 0.5, this.posZ, 24,
							this.width * 0.5, this.height * 0.5, this.width * 0.5, 0
					);
				}
				//Sound
				this.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 0.5F, 1);
			}
			//No Gravity this.setNoGravity(this.isRangedMode());
			//Equip
			if(this.isRangedMode())
			{
				this.setHeldItem(EnumHand.MAIN_HAND,getXItemBullet());
				this.setHeldItem(EnumHand.OFF_HAND,getShield());
			}
			else
			{
				this.setHeldItem(EnumHand.MAIN_HAND,getWeapon());
				this.setHeldItem(EnumHand.OFF_HAND,getXMagnet());
			}
		}
	}
	
	public static class EntityXBossBullet extends EntityFireball
	{
		protected boolean isBossBullet=false;

		protected void initSize()
		{
			this.setSize(0.5F,0.5F);
		}
		
		protected void initSize(float size)
		{
			this.setSize(size,size);
		}

		public EntityXBossBullet(World worldIn)
		{
			super(worldIn);
			this.initSize();
		}

		public EntityXBossBullet(World worldIn,double x,double y,double z,double ax,double ay,double az)
		{
			super(worldIn,x,y,z,ax,ay,az);
			this.initSize();
		}
		
		public EntityXBossBullet(World worldIn,EntityLivingBase shooter,double x,double y,double z,double ax,double ay,double az)
		{
			this(worldIn,x,y,z,ax,ay,az);
			this.shootingEntity=shooter;
		}

		public EntityXBossBullet(World worldIn,EntityLivingBase shooter,double x,double y,double z,double ax,double ay,double az,boolean isBossBullet)
		{
			super(worldIn,x,y,z,ax,ay,az);
			this.initSize(0.625F);
			this.shootingEntity=shooter;
			this.isBossBullet=isBossBullet;
			this.setNoGravity(isBossBullet);
			this.noClip=isBossBullet;
		}
		
		//========Functional Methods========//
		protected void onImpact(RayTraceResult result)
		{
			if(!this.world.isRemote)
			{
				if(result.entityHit!=null)
				{
					if(result.entityHit.attackEntityFrom(DamageSource.causeMobDamage(this.shootingEntity),4.0F))
					{
						this.applyEnchantments(this.shootingEntity,result.entityHit);
						//result.entityHit.setFire(20);
					}
				}
				this.explode();
				this.setDead();
			}
		}
		
		public void explode()
		{
			//Sound
			this.playSound(SoundEvents.BLOCK_ANVIL_LAND,0.5F,1.0F);
			//Effects
			if(this.world.isRemote)
			{
				XBoss.xBossRangedNearbyBulletAttack(this.shootingEntity,this,0,128,1,false);
			}
			else
			{
				if(this.isBossBullet)
				XBoss.punchEntities(world,this,this.posX,this.posY,this.posZ,2,2,0.1);
				XBoss.redRayHurtNearbyEntities(world,this.shootingEntity,this.posX,this.posY,this.posZ,6,4,this.shootingEntity instanceof EntityPlayer,this.shootingEntity instanceof EntityXBoss);
			}
			//Particle
			if(world instanceof WorldServer) world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE,this.posX,this.posY,this.posZ,0,0,0);
		}
		
		public boolean canBeCollidedWith()
		{
			return false;
		}
		
		protected boolean isFireballFiery()
		{
			return false;
		}
		
		protected EnumParticleTypes getParticleType()
		{
			return EnumParticleTypes.SPELL_INSTANT;
		}
		
		public void onUpdate()
		{
			super.onUpdate();
		}
	}

	public static class EntityXBossAIAttackMelee extends EntityAIAttackMelee
	{
		protected EntityXBoss host;
		public EntityXBossAIAttackMelee(EntityCreature creature,double speedIn,boolean useLongMemory)
		{
			super(creature,speedIn,useLongMemory);
			if(creature instanceof EntityXBoss) this.host=(EntityXBoss)creature;
		}

		@Override
		protected void checkAndPerformAttack(EntityLivingBase p_190102_1_,double p_190102_2_)
		{
			double d0=this.getAttackReachSqr(p_190102_1_);

			if (p_190102_2_<=d0&&this.attackTick<=0)
			{
				this.attackTick=10;//(int)(10*(Math.random()+1))
				this.attacker.swingArm(EnumHand.MAIN_HAND);
				this.attacker.attackEntityAsMob(p_190102_1_);
			}
		}
		
		@Override
		protected double getAttackReachSqr(EntityLivingBase attackTarget)
		{
			return 16F;
		}
		
		@Override
		public boolean shouldExecute()
		{
			return super.shouldExecute()&&host!=null&&!host.isRangedMode();
		}
	}
	
	/**
	*A Overridden Class as EntityAIAttackRanged
	 */
	public static class EntityXBossAIAttackRangedOverridden extends EntityAIBase
	{
		/** The entity the AI instance has been applied to */
		protected final EntityXBoss host;
		protected EntityLivingBase attackTarget;
		/**
		*A decrementing tick that spawns a ranged attack once this value reaches 0. It is then set back to the
		*maxRangedAttackTime.
		 */
		protected int rangedAttackTime;
		protected final int attackIntervalMin;
		/** The maximum time the AI has to wait before peforming another ranged attack. */
		protected final int maxRangedAttackTime;
		protected final float attackRadius;
		
		public EntityXBossAIAttackRangedOverridden(EntityXBoss attacker,int attackIntervalMin,int maxAttackTime,float maxAttackDistanceIn)
		{
			this.rangedAttackTime=-1;
			this.host=attacker;
			this.attackIntervalMin=attackIntervalMin;
			this.maxRangedAttackTime=maxAttackTime;
			this.attackRadius=maxAttackDistanceIn;
			this.setMutexBits(3);
		}
		
		/**
		*Returns whether the EntityAIBase should begin execution.
		 */
		public boolean shouldExecute()
		{
			EntityLivingBase entitylivingbase=this.host.getAttackTarget();
			if(entitylivingbase==null) return false;
			else this.attackTarget=entitylivingbase;
			return this.host.isRangedMode();
		}
		
		/**
		*Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean shouldContinueExecuting()
		{
			return this.shouldExecute();
		}
		
		/**
		*Reset the task's internal state. Called when this task is interrupted by another one
		 */
		public void resetTask()
		{
			this.attackTarget=null;
			this.rangedAttackTime=-1;
		}
		
		/**
		*Keep ticking a continuous task that has already been started
		 */
		public void updateTask()
		{
			double d0=this.host.getDistanceSq(this.attackTarget.posX,this.attackTarget.getEntityBoundingBox().minY,this.attackTarget.posZ);
			//boolean flag=this.host.getEntitySenses().canSee(this.attackTarget);
			
			this.host.getLookHelper().setLookPositionWithEntity(this.attackTarget,30.0F,30.0F);
			
			if (--this.rangedAttackTime<=0)
			{
				float f=MathHelper.sqrt(d0)/this.attackRadius;
				float distanceClamp=MathHelper.clamp(f,0.1F,1.0F);
				if(this.rangedAttackTime==0) this.host.attackEntityWithRangedAttack(this.attackTarget,distanceClamp);
				this.rangedAttackTime=MathHelper.floor(f*(float)(this.maxRangedAttackTime-this.attackIntervalMin)+(float)this.attackIntervalMin);
			}
		}
	}
}