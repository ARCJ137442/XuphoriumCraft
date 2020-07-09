package xuphorium_craft;

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

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;

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
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityIllusionIllager;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;

import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.Minecraft;

import net.minecraft.potion.PotionEffect;

import net.minecraft.init.MobEffects;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.init.Enchantments;

import java.util.HashMap;
import java.util.List;

import net.minecraft.pathfinding.Path;
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
	public static final int ENTITYID=5;
	public static final int ENTITYID_RANGED=6;
	
	public static double m_abs(double x)
	{
		if(x==0) return 0;return Math.abs(x)<1?1/x:x;
	}

	public XBoss(XuphoriumCraftElements instance)
	{
		super(instance,2);
	}

	@Override
	public void initElements()
	{
		elements.entities.add(()->EntityEntryBuilder.create().entity(EntityXBoss.class).id(new ResourceLocation("xuphorium_craft","x_boss"),ENTITYID).name("x_boss").tracker(64,1,true).egg(-3355444,-16711681).build());
		elements.entities.add(()->EntityEntryBuilder.create().entity(EntityBulletCustom.class).id(new ResourceLocation("xuphorium_craft","x_boss_bullet"),ENTITYID_RANGED).name("x_boss_bullet").tracker(64,1,true).build());
	}
		
	public static void punchEntities(World world,Entity host,double x,double y,double z,double range)
	{
		List<Entity> entities=world.loadedEntityList;
		double dx,dy,dz;
		for(Entity entity : entities)
		{
			if(entity==host) continue;
			dx=entity.posX-x;
			dy=entity.posY-y;
			dz=entity.posZ-z;
			if(dx*dy*dz!=0&&dx*dx+dy*dy+dz*dz<=range*range)
			{
				entity.motionX+=1/m_abs(dx);
				entity.motionY+=1/m_abs(dy);
				entity.motionZ+=1/m_abs(dz);
			}
		}
	}
	
	public static double getDiatance(Entity e1,Entity e2)
	{
		return Math.sqrt((e1.posX-e2.posX)*(e1.posX-e2.posX)+(e1.posY-e2.posY)*(e1.posY-e2.posY)+(e1.posZ-e2.posZ)*(e1.posZ-e2.posZ));
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
					return new ResourceLocation("xuphorium_craft:textures/x_zombie.png");
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
		RenderingRegistry.registerEntityRenderingHandler(EntityBulletCustom.class,renderManager -> {
			return new RenderSnowball<EntityBulletCustom>(renderManager,null,Minecraft.getMinecraft().getRenderItem())
			{
				public ItemStack getStackToRender(EntityBulletCustom entity)
				{
					return new ItemStack(XCraftTools.X_ITEM,1);
				}
			};
		});
	}

	public static class EntityXBoss extends EntityMob implements IRangedAttackMob
	{
		protected int randomHurtTick=20;
		//protected EntityLivingBase lastMemEntity;
		
		public static ItemStack getWeapon()
		{
			ItemStack result=new ItemStack(XCraftTools.X_SWORD);
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
			double x=(double)this.posX;
			double y=(double)this.posY;
			double z=(double)this.posZ;
			
			if(Math.random()<Math.random())
			{
				this.playSound(SoundEvents.BLOCK_ANVIL_LAND,1.0F,1.0F);
				XBoss.punchEntities(world,this,this.posX,this.posY,this.posZ,3);
				return false;
			}
			if(source.getImmediateSource() instanceof EntityArrow||
				source==DamageSource.CACTUS||source==DamageSource.DROWN||
				source==DamageSource.LIGHTNING_BOLT) return false;
			if(source instanceof EntityDamageSource)
			{
				if(((EntityDamageSource)source).getIsThornsDamage()) return false;
			}
			
			//Suffer From Damage//
			if(world instanceof WorldServer) ((WorldServer)world).spawnParticle(EnumParticleTypes.REDSTONE,x,y,z,12,1,2,1,0,new int[0]);
			
			this.clearActivePotions();
			this.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST,1000000,2,true,false));
			this.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION,1000000,2,true,false));
			this.addPotionEffect(new PotionEffect(MobEffects.STRENGTH,40,2,true,false));
			this.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY,40,1,true,false));
			
			if(!world.isRemote)
			{
				EntityBulletCustom xb=new EntityBulletCustom(world,this,0,-1/32,0);
				xb.setVelocity(this.getLookVec().x/2,this.getLookVec().y/2,this.getLookVec().z/2);
				//XBullet.setDamage(4*2.0F);
				//XBullet.setKnockbackStrength(4);
				world.spawnEntity(xb);
				double dx,dy,dz;EntityTippedArrow arrow;
				for(dx=-1D;dx<2;dx+=2)
				{
					for(dy=-1D;dy<2;dy+=2)
					{
						for(dz=-1D;dz<2;dz+=2)
						{
							arrow=new EntityTippedArrow(world,this);
							arrow.setVelocity(dx,dy,dz);
							arrow.setDamage(3.0F);
							arrow.setKnockbackStrength(1);
							world.spawnEntity(arrow);
						}
					}
				}
				this.hurtOther(world,false,8,true);
			}
			
			if(source.getImmediateSource()instanceof EntityPotion)return false;
			if(source==DamageSource.FALL)return false;
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
		public void onDeath(DamageSource source)
		{
			super.onDeath(source);
			int x=(int)this.posX;
			int y=(int)this.posY;
			int z=(int)this.posZ;
			Entity entity=this;/*
			{
				java.util.HashMap<String,Object> $_dependencies=new java.util.HashMap<>();
				$_dependencies.put("x",x);
				$_dependencies.put("y",y);
				$_dependencies.put("z",z);
				$_dependencies.put("world",world);
				XBossMobDies.executeProcedure($_dependencies);
			}*/
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
		}
		
		//@Override
		public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty,IEntityLivingData livingdata)
		{
			super.onInitialSpawn(difficulty,livingdata);
			int x=(int)this.posX;
			int y=(int)this.posY;
			int z=(int)this.posZ;
			Entity entity=this;
			if(entity instanceof EntityLivingBase)
			{
				((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST,1000000,2,false,true));
				((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION,1000000,2,false,true));
				((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY,200,1,true,true));
				((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.GLOWING,200,1,true,true));
				((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.LEVITATION,20,16,true,true));
			}
			return livingdata;
		}
		
		@Override
		protected void applyEntityAttributes()
		{
			super.applyEntityAttributes();
			if(this.getEntityAttribute(SharedMonsterAttributes.ARMOR)!=null)this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(0D);
			if(this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)!=null)this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
			if(this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)!=null)this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(64D);
			if(this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)!=null)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4D);
			if(this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE)!=null)this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
		}
		
		//@Override
		public void setSwingingArms(boolean swingingArms)
		{
			
		}
		
		public void attackEntityWithRangedAttack(EntityLivingBase target,float flval)
		{
			EntityBulletCustom xb=new EntityBulletCustom(this.world,this,0,-1/32,0);/*
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
				hurtOther(this.world,true,16,false);
			}
			Entity target=this.getAttackTarget();//getAttackingEntity
			if(target!=null)
			{
				double targetDistance=getDiatance(this,target);
				double dx=target.posX-this.posX;
				double dy=target.posY-this.posY;
				double dz=target.posZ-this.posZ;
				if(targetDistance>=8)
				{
					this.motionX+=dx/targetDistance*0.2;
					this.motionY+=dy/targetDistance*0.2;
					this.motionZ+=dz/targetDistance*0.2;
				}
				((WorldServer)world).spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE,this.posX,this.posY+1.7,this.posZ,dx,dy,dz);
			}
		}
		
		public void hurtOther(World world,boolean random,double radius,boolean realAttack)
		{
			try
			{
				List<Entity> entities=world.loadedEntityList;
				for(Entity entity : entities)
				{
					if(entity instanceof EntityLivingBase)
					{
						if(entity instanceof EntityXBoss||entity instanceof EntityPlayer) continue;
						double dx=this.posX-entity.posX;
						double dy=this.posY+1.7-entity.posY;
						double dz=this.posZ-entity.posZ;
						if(dx*dx+dy*dy+dz*dz<=radius*radius&&(!random||Math.random()<Math.random()))
						{
							redRayHurtEntity((EntityLivingBase)entity,realAttack?-1:Math.sqrt(dx*dx+dy*dy+dz*dz));
						}
					}
				}
			}
			catch(Exception e)
			{
				
			}
		}
		
		public void redRayHurtEntity(EntityLivingBase entity,double distance)
		{
			double x,y,z;
			for(double i=0;i<=32;i++)
			{
				x=i/32*this.posX+(1-i/32)*entity.posX;
				y=i/32*(this.posY+1.7)+(1-i/32)*entity.posY;
				z=i/32*this.posZ+(1-i/32)*entity.posZ;
				((WorldServer)world).spawnParticle(EnumParticleTypes.REDSTONE,x,y,z,0,0,0,0,0,new int[0]);
			}
			if(distance<0)
			{
				super.attackEntityAsMob(entity);
			}
			else entity.attackEntityFrom(new EntityDamageSource("indirectMagic",this),(float)(4+8/(distance+1)));
		}
	}
	
	public static class EntityBulletCustom extends EntityFireball
	{
		public Entity target;
		
		public EntityBulletCustom(World a)
		{
			super(a);
			this.setSize(0.5F,0.5F);
		}
		
		public EntityBulletCustom(World worldIn,double x,double y,double z,double ax,double ay,double az)
		{
			super(worldIn,x,y,z,ax,ay,az);
			this.setSize(0.5F,0.5F);
		}
		
		public EntityBulletCustom(World worldIn,EntityLivingBase shooter,double ax,double ay,double az)
		{
			super(worldIn,shooter,ax,ay,az);
			this.setSize(0.5F,0.5F);
		}
		
		protected void onImpact(RayTraceResult result)
		{
			if(!this.world.isRemote)
			{
				if(result.entityHit!=null)
				{
					if(result.entityHit.attackEntityFrom(DamageSource.causeMobDamage(this.shootingEntity),8.0F))
					{
						this.applyEnchantments(this.shootingEntity,result.entityHit);
						result.entityHit.setFire(20);
					}
				}
				this.playSound(SoundEvents.BLOCK_ANVIL_LAND,1.0F,1.0F);
				XBoss.punchEntities(world,this,this.posX,this.posY,this.posZ,2);
			}
			this.setDead();
		}
		
		public boolean canBeCollidedWith()
		{
			return false;
		}
		
		public boolean attackEntityFrom(DamageSource source,float amount)
		{
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
				this.attackTick=(int)(10*(Math.random()+1));
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