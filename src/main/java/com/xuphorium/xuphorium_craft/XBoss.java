package com.xuphorium.xuphorium_craft;

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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.EnumHand;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.inventory.EntityEquipmentSlot;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.projectile.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.passive.EntityZombieHorse;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityMule;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityIllusionIllager;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.EnumCreatureAttribute;

import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.Minecraft;

import net.minecraft.potion.PotionEffect;

import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.init.Enchantments;

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
		elements.entities.add(()->EntityEntryBuilder.create().entity(EntityXBoss.class).id(new ResourceLocation("xuphorium_craft","x_boss"),ENTITYID).name("x_boss").tracker(64,1,true).egg(-3355444,-16711681).build());
		elements.entities.add(()->EntityEntryBuilder.create().entity(EntityXBossBullet.class).id(new ResourceLocation("xuphorium_craft","x_boss_bullet"),ENTITYID_RANGED).name("x_boss_bullet").tracker(64,1,true).build());
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
					return new ItemStack(XCraftMaterials.X_CRYSTAL,1);
				}
			});
	}
	
	//====X-Boss's Punch====//
	public static void punchEntities(World world,Entity host,double x,double y,double z,double range)
	{
		punchEntities(world,host,x,y,z,range,1);
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
	 * No Attacker Hurt (Use for X-Item's Mod 5)
	 * @param attackDamageFlag
	 *  >=0 : const damage as the flag.
	 *  negative number : uses distance as damage.
	 * @param radiusFlag
	 *  positive number : uses distance to select target.
	 *  0 : random select target all the list.
	 *  negative number : random select target in distance.
	 */
	public static void redRayHurtNearbyEntities(World world,double x,double y,double z,
	                                            double radiusFlag,double attackDamageFlag,
	                                            boolean ignorePlayer,boolean ignoreXBoss)
	{
		try
		{
			List<Entity> entities=world.loadedEntityList;
			for(Entity entity: entities)
			{
				try
				{
					if(entity instanceof EntityLivingBase)
					{
						//Ignore Special Entities
						if(ignoreXBoss&&entity instanceof EntityXBoss||ignorePlayer&&entity instanceof EntityPlayer)
							continue;
						//Select
						double dx=x-entity.posX;
						double dy=y-entity.posY;
						double dz=z-entity.posZ;
						double distanceSquare=dx*dx+dy*dy+dz*dz;
						if((radiusFlag==0&&Math.random()<Math.random())||
								radiusFlag!=0&&distanceSquare<=radiusFlag*radiusFlag&&(radiusFlag>0||Math.random()<Math.random()))
						{
							//Damage the Target
							redRayHurtEntity(x,y,z,(EntityLivingBase)entity,attackDamageFlag >= 0?attackDamageFlag:Math.sqrt(distanceSquare));
						}
					}
				}
				catch(Exception e)
				{
					XCraftBlocks.LOGGER.warn(e);
				}
			}
		}
		catch(Exception ignored)
		{
		
		}
	}
	
	public static void redRayHurtNearbyEntities(World world,double x,double y,double z,double radiusFlag,double attackDamageFlag)
	{
		redRayHurtNearbyEntities(world,x,y,z,radiusFlag,attackDamageFlag,false,false);
	}
	
	/**
	 * @param attackDamageFlag
	 *  positive number : const damage as the flag.
	 *  -1 : uses distance as damage.
	 *  <-1 : uses as attacker's attack (need attacker!=null,else than uses -1).
	 * @param radiusFlag
	 *  positive number : uses distance to select target.
	 *  0 : random select target all the list.
	 *  negative number : random select target in distance.
	 */
	public static void redRayHurtNearbyEntities(World world,EntityLivingBase attacker,
	                                            double radiusFlag,double attackDamageFlag,
	                                            boolean ignorePlayer,boolean ignoreXBoss)
	{
		redRayHurtNearbyEntities(world, attacker,attacker.posX,attacker.posY+attacker.getEyeHeight(), attacker.posZ,radiusFlag, attackDamageFlag, ignorePlayer, ignoreXBoss);
	}
	
	public static void redRayHurtNearbyEntities(World world,EntityLivingBase attacker,double sourceX,double sourceY,double sourceZ,
	                                            double radiusFlag,double attackDamageFlag,
	                                            boolean ignorePlayer,boolean ignoreXBoss)
	{
		try
		{
			/*
			  damage : The same as attackDamageFlag
			 */
			double damage=attackDamageFlag;
			if(attacker!=null&&attackDamageFlag<-1) damage=-2;//means uses as attacker's attack
			else if(attackDamageFlag<-1) damage=-1;
			List<Entity> entities=world.loadedEntityList;
			for(Entity entity : entities)
			{
				if(entity instanceof EntityLivingBase)
				{
					//Ignore Special Entities
					if(entity==attacker||ignoreXBoss&&entity instanceof EntityXBoss||ignorePlayer&&entity instanceof EntityPlayer) continue;
					//Select
					double dx=sourceX-entity.posX;
					double dy=sourceY-entity.posY;
					double dz=sourceZ-entity.posZ;
					double distanceSquare=dx*dx+dy*dy+dz*dz;
					if((radiusFlag==0&&Math.random()<Math.random())||
						radiusFlag!=0&&distanceSquare<=radiusFlag*radiusFlag&&(radiusFlag>0||Math.random()<Math.random()))
					{
						//damage -> damageFlag
						damage=(attackDamageFlag==-1||damage==-1)?Math.sqrt(distanceSquare):damage;
						//Damage the Target
						if(attacker!=null) redRayHurtEntity(attacker,sourceX,sourceY,sourceZ,(EntityLivingBase)entity,damage);
						else redRayHurtEntity(sourceX,sourceY,sourceZ,(EntityLivingBase)entity,damage);
					}
				}
			}
		}
		catch(Exception ignored)
		{
		
		}
	}
	
	public static void redRayHurtNearbyEntities(World world,EntityLivingBase attacker,double radiusFlag,double attackDamageFlag)
	{
		redRayHurtNearbyEntities(world,attacker,radiusFlag,attackDamageFlag,false,false);
	}
	
	/**
	 * @param distanceFlag =0 : attack as attacker. >0 : static damage. <0 : damage by distance
	 */
	public static void redRayHurtEntity(EntityLivingBase attacker,EntityLivingBase target,double distanceFlag)
	{
		redRayHurtEntity(attacker,attacker.posX,attacker.posY+attacker.getEyeHeight(), attacker.posZ, target, distanceFlag);
	}
	
	public static void redRayHurtEntity(EntityLivingBase attacker,
	                                    double sourceX,double sourceY,double sourceZ,
	                                    EntityLivingBase target,double distanceFlag)
	{
		generateRedWay(target.world,sourceX,sourceY,sourceZ,target.posX,target.posY,target.posZ);
		//Attack Entity as Direct Attack
		if(distanceFlag<0)
		{
			if(attacker instanceof EntityPlayer) ((EntityPlayer)attacker).attackTargetEntityWithCurrentItem(target);
			else attacker.attackEntityAsMob(target);
		}
		//Attack Entity as Magic
		else target.attackEntityFrom(new EntityDamageSource("indirectMagic",attacker),distanceFlag>0?(float)distanceFlag:((float)(Math.abs(distanceFlag))));
	}
	
	public static void redRayHurtEntity(double x,double y,double z,EntityLivingBase target,double damage)
	{
		generateRedWay(target.world,x,y,z,target.posX,target.posY,target.posZ);
		target.attackEntityFrom(new EntityDamageSource("indirectMagic",null),(float)damage);
	}
	
	public static void generateRedWay(World world,double x1,double y1,double z1,double x2,double y2,double z2)
	{
		generateRedWay(world,x1,y1,z1,x2,y2,z2,32);
	}
	
	public static void generateRedWay(World world,double x1,double y1,double z1,double x2,double y2,double z2,int numParticle)
	{
		XuphoriumCraft.generateParticleRay(world,x1,y1,z1,x2,y2,z2,numParticle,EnumParticleTypes.REDSTONE);
	}
	
	//====Tool Functions====//
	
	//============Entity Classes============//
	public static class EntityXBoss extends EntityMob implements IRangedAttackMob
	{
		public static final ResourceLocation LOOT_TABLE_DEATH=LootTableList.register(new ResourceLocation("xuphorium_craft:entities/x_boss"));
		
		protected int randomHurtTick=20;
		//protected EntityLivingBase lastMemEntity;
		
		public EntityXBoss(World world)
		{
			super(world);
			this.setSize(0.6f,1.8f);
			this.experienceValue=64;
			this.isImmuneToFire=true;
			this.setCustomNameTag("X-BOSS");
			this.setAlwaysRenderNameTag(true);
			this.enablePersistence();
			this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND,getWeapon());
			this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND,getShield());
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
			ItemStack result=new ItemStack(XCraftTools.X_SWORD,1,1);
			result.addEnchantment(Enchantments.SHARPNESS,5);
			result.addEnchantment(Enchantments.SMITE,5);
			result.addEnchantment(Enchantments.BANE_OF_ARTHROPODS,5);
			result.addEnchantment(Enchantments.KNOCKBACK,2);
			result.addEnchantment(Enchantments.LOOTING,10);
			result.addEnchantment(Enchantments.SWEEPING,3);
			result.addEnchantment(Enchantments.SILK_TOUCH,1);
			result.addEnchantment(Enchantments.UNBREAKING,3);
			result.addEnchantment(Enchantments.MENDING,1);
			return result;
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
		
		@Override
		protected void initEntityAI()
		{
			this.tasks.addTask(1,new EntityAISwimming(this));
			this.tasks.addTask(2,new EntityXBossAIAttackMelee(this,1,true));
			this.tasks.addTask(3,new EntityAIMoveTowardsTarget(this,1.5D,32.0F));
			this.tasks.addTask(4,new EntityAIMoveTowardsRestriction(this,1.0D));
			this.tasks.addTask(5,new EntityAIMoveIndoors(this));
			this.tasks.addTask(6,new EntityAIOpenDoor(this,true));
			this.tasks.addTask(7,new EntityAIOpenDoor(this,false));
			this.targetTasks.addTask(8,new EntityAIHurtByTarget(this,true));
			this.targetTasks.addTask(9,new EntityAINearestAttackableTarget(this,EntityWither.class,false,true));
			this.targetTasks.addTask(9,new EntityAINearestAttackableTarget(this,EntityWitch.class,false,true));
			this.targetTasks.addTask(9,new EntityAINearestAttackableTarget(this,EntityVillager.class,false,true));
			this.targetTasks.addTask(10,new EntityAINearestAttackableTarget(this,EntityEvoker.class,false,true));
			this.targetTasks.addTask(11,new EntityAINearestAttackableTarget(this,EntityIllusionIllager.class,false,true));
			this.targetTasks.addTask(12,new EntityAINearestAttackableTarget(this,EntityVindicator.class,false,true));
			this.targetTasks.addTask(13,new EntityAINearestAttackableTarget(this,EntityZombieVillager.class,false,true));
			this.targetTasks.addTask(14,new EntityAINearestAttackableTarget(this,EntityIronGolem.class,false,true));
			this.targetTasks.addTask(15,new EntityAINearestAttackableTarget(this,EntitySnowman.class,false,true));
			this.targetTasks.addTask(16,new EntityAINearestAttackableTarget(this,EntityEnderman.class,false,true));
			this.targetTasks.addTask(17,new EntityAINearestAttackableTarget(this,EntityEndermite.class,false,true));
			this.targetTasks.addTask(18,new EntityAINearestAttackableTarget(this,EntityChicken.class,false,true));
			this.targetTasks.addTask(19,new EntityAINearestAttackableTarget(this,EntityCow.class,false,true));
			this.targetTasks.addTask(20,new EntityAINearestAttackableTarget(this,EntityDonkey.class,false,true));
			this.targetTasks.addTask(21,new EntityAINearestAttackableTarget(this,EntityHorse.class,false,true));
			this.targetTasks.addTask(22,new EntityAINearestAttackableTarget(this,EntityLlama.class,false,true));
			this.targetTasks.addTask(23,new EntityAINearestAttackableTarget(this,EntityMooshroom.class,false,true));
			this.targetTasks.addTask(24,new EntityAINearestAttackableTarget(this,EntityMule.class,false,true));
			this.targetTasks.addTask(25,new EntityAINearestAttackableTarget(this,EntityOcelot.class,false,true));
			this.targetTasks.addTask(26,new EntityAINearestAttackableTarget(this,EntityParrot.class,false,true));
			this.targetTasks.addTask(27,new EntityAINearestAttackableTarget(this,EntityPig.class,false,true));
			this.targetTasks.addTask(28,new EntityAINearestAttackableTarget(this,EntityRabbit.class,false,true));
			this.targetTasks.addTask(29,new EntityAINearestAttackableTarget(this,EntitySheep.class,false,true));
			this.targetTasks.addTask(30,new EntityAINearestAttackableTarget(this,EntitySkeletonHorse.class,false,true));
			this.targetTasks.addTask(31,new EntityAINearestAttackableTarget(this,EntityWolf.class,false,true));
			this.targetTasks.addTask(32,new EntityAINearestAttackableTarget(this,EntityZombieHorse.class,false,true));
			this.targetTasks.addTask(33,new EntityAINearestAttackableTarget(this,EntityPlayer.class,false,true));
			this.targetTasks.addTask(34,new EntityAINearestAttackableTarget(this,EntityPlayerMP.class,false,true));
			this.tasks.addTask(35,new EntityAIWatchClosest(this,XCreeper.EntityXCreeper.class,(float)16));
			this.tasks.addTask(36,new EntityAILeapAtTarget(this,(float)0.8));
			//this.tasks.addTask(1,new EntityAIAttackRanged(this,1.25D,20,10.0F));
		}
		
		//@Override
		public EnumCreatureAttribute getCreatureAttribute()
		{
			return EnumCreatureAttribute.UNDEFINED;
		}
		
		//@Override
		protected boolean canDespawn()
		{
			return false;
		}
		
		//@Override
		protected Item getDropItem()
		{
			return null;
		}
		
		//@Override
		public net.minecraft.util.SoundEvent getAmbientSound()
		{
			return (net.minecraft.util.SoundEvent)net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation(""));
		}
		
		//@Override
		public net.minecraft.util.SoundEvent getHurtSound(DamageSource ds)
		{
			return (net.minecraft.util.SoundEvent)net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.player.hurt"));
		}
		
		//@Override
		public net.minecraft.util.SoundEvent getDeathSound()
		{
			return (net.minecraft.util.SoundEvent)net.minecraft.util.SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.player.death"));
		}
		
		//@Override
		protected SoundEvent getStepSound()
		{
			return null;
		}
		
		//@Override
		protected float getSoundVolume()
		{
			return 1.0F;
		}
		
		//@Override
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
			//Rebound
			if(source.getImmediateSource() instanceof EntityArrow||source==DamageSource.FALL||Math.random()<Math.random())
			{
				this.playSound(SoundEvents.BLOCK_ANVIL_LAND,1.0F,1.0F);
				XBoss.punchEntities(world,this,this.posX,this.posY,this.posZ,4);
				return false;
			}
			
			//====Suffer From Damage====//
			//Particle
			if(world instanceof WorldServer)
			{
				((WorldServer)world).spawnParticle(EnumParticleTypes.REDSTONE,
						this.posX,this.posY,this.posZ,12,
						this.width*0.5,this.height*0.5,this.width*0.5,
						0
				);
			}
			//Potion Effects
			this.clearActivePotions();
			this.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST,1000000,2,true,false));
			this.addPotionEffect(new PotionEffect(MobEffects.STRENGTH,40,2,true,false));
			this.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY,40,1,true,false));
			//Spawn Bullet
			if(!world.isRemote)
			{
				double dx,dy,dz;EntityXBossBullet bullet;
				for(dx=-1D;dx<2;dx+=2)
				{
					for(dy=-1D;dy<2;dy+=2)
					{
						for(dz=-1D;dz<2;dz+=2)
						{
							bullet=new EntityXBossBullet(world,this,this.posX+dx,this.posY+dy,this.posZ+dz,dx*0.01,-0.0375,dz*0.01);
							bullet.setVelocity(dx,dy,dz);
							world.spawnEntity(bullet);
						}
					}
				}
				redRayHurtNearbyEntities(world,this,8,-2,false,true);
			}
			
			return super.attackEntityFrom(source,amount);
		}
		
		//@Override
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
						if(!(entityL instanceof XBoss.EntityXBoss)&&!(entityL instanceof EntityPlayer)&&!(entityL instanceof EntityPlayerMP))
						{
							if(this.getRidingEntity()!=entityL) this.startRiding(entityL);
						}
						entityL.motionY+=0.875;
						world.addWeatherEffect(new EntityLightningBolt(world,entityL.posX-0.5,entityL.posY,entityL.posZ-0.5,true));
					}
				}
				return flag;
			}
			catch(Exception e)
			{
			
			}
			return flag;
		}
		
		//@Override
		/*public void onDeath(DamageSource source)
		{
			super.onDeath(source);
			int x=(int)this.posX;
			int y=(int)this.posY;
			int z=(int)this.posZ;
			if(!world.isRemote)
			{
				EntityItem entityToSpawn=new EntityItem(world,x,y,z,new ItemStack(XCraftMaterials.X_STAR,1));
				entityToSpawn.setPickupDelay(10);
				world.spawnEntity(entityToSpawn);
				int i;
				for(i=0;i<Math.random()*10;i++)
				{
					entityToSpawn=new EntityItem(world,x,y,z,new ItemStack(XCraftMaterials.X_DUST,1));
					entityToSpawn.setPickupDelay(10);
					world.spawnEntity(entityToSpawn);
				}
				for(i=0;i<Math.random()*8;i++)
				{
					entityToSpawn=new EntityItem(world,x,y,z,new ItemStack(XCraftMaterials.X_CATALYST,1));
					entityToSpawn.setPickupDelay(10);
					world.spawnEntity(entityToSpawn);
				}
				for(i=0;i<Math.random()*8;i++)
				{
					entityToSpawn=new EntityItem(world,x,y,z,new ItemStack(XCraftMaterials.X_INGOT,1));
					entityToSpawn.setPickupDelay(10);
					world.spawnEntity(entityToSpawn);
				}
				for(i=0;i<Math.random()*6;i++)
				{
					entityToSpawn=new EntityItem(world,x,y,z,new ItemStack(XCraftMaterials.X_ROD,1));
					entityToSpawn.setPickupDelay(10);
					world.spawnEntity(entityToSpawn);
				}
				for(i=0;i<Math.random()*4;i++)
				{
					entityToSpawn=new EntityItem(world,x,y,z,new ItemStack(XCraftMaterials.X_PEARL,1));
					entityToSpawn.setPickupDelay(10);
					world.spawnEntity(entityToSpawn);
				}
				for(i=0;i<Math.random()*4;i++)
				{
					entityToSpawn=new EntityItem(world,x,y,z,new ItemStack(XCraftMaterials.X_DIAMOND,1));
					entityToSpawn.setPickupDelay(10);
					world.spawnEntity(entityToSpawn);
				}
				for(i=0;i<Math.random()*4;i++)
				{
					entityToSpawn=new EntityItem(world,x,y,z,new ItemStack(XCraftMaterials.X_EMERALD,1));
					entityToSpawn.setPickupDelay(10);
					world.spawnEntity(entityToSpawn);
				}
				for(i=0;i<Math.random()*4;i++)
				{
					entityToSpawn=new EntityItem(world,x,y,z,new ItemStack(XCraftMaterials.X_CRYSTAL,1));
					entityToSpawn.setPickupDelay(10);
					world.spawnEntity(entityToSpawn);
				}
				for(i=0;i<Math.random()*8;i++)
				{
					entityToSpawn=new EntityItem(world,x,y,z,new ItemStack(Items.REDSTONE,1));
					entityToSpawn.setPickupDelay(10);
					world.spawnEntity(entityToSpawn);
				}
				for(i=0;i<Math.random()*8;i++)
				{
					entityToSpawn=new EntityItem(world,x,y,z,new ItemStack(Items.DYE,1,4));
					entityToSpawn.setPickupDelay(10);
					world.spawnEntity(entityToSpawn);
				}
			}
		}*/
		
		//@Override
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
		
		@Override
		protected void applyEntityAttributes()
		{
			super.applyEntityAttributes();
			if(this.getEntityAttribute(SharedMonsterAttributes.ARMOR)!=null) this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(0D);
			if(this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)!=null) this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
			if(this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)!=null) this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(64D);
			if(this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)!=null) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4D);
			if(this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE)!=null) this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
		}
		
		//@Override
		public void setSwingingArms(boolean swingingArms)
		{
			
		}
		
		public void attackEntityWithRangedAttack(EntityLivingBase target,float flval)
		{
			EntityXBossBullet xb=new EntityXBossBullet(this.world,this,0,0,0);/*
			double d0=target.posY+(double)target.getEyeHeight()-1.1;
			double d1=target.posX-this.posX;
			double d2=d0-xb.posY;
			double d3=target.posZ-this.posZ;
			float f=MathHelper.sqrt(d1*d1+d3*d3)*0.2F;*/
			xb.setVelocity(this.getLookVec().x/2,this.getLookVec().y/2,this.getLookVec().z/2);
			this.world.spawnEntity(xb);
		}
		
		//@Override
		public boolean isNonBoss()
		{
			return false;
		}
		
		private final BossInfoServer bossInfo=(BossInfoServer)(new BossInfoServer(this.getDisplayName(),BossInfo.Color.BLUE,BossInfo.Overlay.PROGRESS));
		
		//@Override
		public void addTrackingPlayer(EntityPlayerMP player)
		{
			super.addTrackingPlayer(player);
			this.bossInfo.addPlayer(player);
		}
		
		//@Override
		public void removeTrackingPlayer(EntityPlayerMP player)
		{
			super.removeTrackingPlayer(player);
			this.bossInfo.removePlayer(player);
		}
		
		//@Override
		public void onUpdate()
		{
			super.onUpdate();
			this.bossInfo.setPercent(this.getHealth()/this.getMaxHealth());
			if(--randomHurtTick<0)
			{
				randomHurtTick=(int)(Math.random()*30+10);
				redRayHurtNearbyEntities(this.world,this,-16,-1,true,true);
			}
			//Drag to Target
			Entity target=this.getAttackTarget();//getAttackingEntity
			if(target==this)
			{
				this.setAttackTarget(null);
			}
			else if(target!=null)
			{
				double targetDistance=this.getDistance(target);
				double dx=target.posX-this.posX;
				double dy=target.posY-this.posY;
				double dz=target.posZ-this.posZ;
				if(targetDistance>=8)
				{
					//Motion
					this.motionX+=dx/targetDistance*0.2;
					this.motionY+=dy/targetDistance*0.2;
					this.motionZ+=dz/targetDistance*0.2;
					//Particle
					if(world instanceof WorldServer) world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE,target.posX,target.posY,target.posZ,dx,dy,dz);
				}
			}
		}
	}
	
	public static class EntityXBossBullet extends EntityFireball
	{
		//public Entity target;
		
		protected void initSize()
		{
			this.setSize(0.5F,0.5F);
		}
		
		public EntityXBossBullet(World a)
		{
			super(a);
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
		
		public EntityXBossBullet(World worldIn,EntityLivingBase shooter,double ax,double ay,double az)
		{
			super(worldIn,shooter,ax,ay,az);
			this.initSize();
		}
		
		//========Functional Methods========//
		protected void onImpact(RayTraceResult result)
		{
			if(!this.world.isRemote)
			{
				if(result.entityHit!=null)
				{
					if(result.entityHit.attackEntityFrom(DamageSource.causeMobDamage(this.shootingEntity),8.0F))
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
			if(!this.world.isRemote)
			{
				//Sound
				this.playSound(SoundEvents.BLOCK_ANVIL_LAND,0.5F,1.0F);
				//Effects
				XBoss.punchEntities(world,this,this.posX,this.posY,this.posZ,2);
				XBoss.redRayHurtNearbyEntities(world,this.shootingEntity,this.posX,this.posY,this.posZ,5,4,this.shootingEntity instanceof EntityPlayer,this.shootingEntity instanceof EntityXBoss);
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
		
		public boolean attackEntityFrom(DamageSource source,float amount)
		{
			this.explode();
			return false;
		}
		
		public void onUpdate()
		{
			super.onUpdate();
		}
	}

	public static class EntityXBossAIAttackMelee extends EntityAIAttackMelee
	{
		public EntityXBossAIAttackMelee(EntityCreature creature,double speedIn,boolean useLongMemory)
		{
			super(creature,speedIn,useLongMemory);
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
			return 8.0F;
		}
	}
}