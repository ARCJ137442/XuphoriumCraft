package com.xuphorium.xuphorium_craft;

import net.minecraft.init.SoundEvents;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.DifficultyInstance;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;

import net.minecraft.inventory.EntityEquipmentSlot;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.projectile.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.Entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.model.ModelCreeper;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

@XuphoriumCraftElements.ModElement.Tag
public class XCreeper extends XuphoriumCraftElements.ModElement
{
	public static final int ENTITYID=1;

	public XCreeper(XuphoriumCraftElements instance)
	{
		super(instance,1);
	}

	@Override
	public void initElements()
	{
		elements.entities.add(()->
				EntityEntryBuilder.create().entity(
					EntityXCreeper.class
				).id(
					new ResourceLocation(XuphoriumCraft.MODID,"x_creeper"),ENTITYID
				).name(
					"x_creeper"
				).tracker(
					64,1,true
				).egg(
					-13369549,-16776961
				).build()
		);
	}

	@Override
	public void init(FMLInitializationEvent event)
	{
		Biome[] spawnBiomes=allbiomes(Biome.REGISTRY);
		EntityRegistry.addSpawn(EntityXCreeper.class,1,1,1,EnumCreatureType.MONSTER,spawnBiomes);
	}

	private Biome[] allbiomes(net.minecraft.util.registry.RegistryNamespaced<ResourceLocation,Biome> in)
	{
		Iterator<Biome> itr=in.iterator();
		ArrayList<Biome> ls=new ArrayList<Biome>();
		while (itr.hasNext()) ls.add(itr.next());
		return ls.toArray(new Biome[ls.size()]);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityXCreeper.class,renderManager -> {
			return new RenderLiving(renderManager,new ModelCreeper(),0.5f)
			{
				protected ResourceLocation getEntityTexture(Entity entity)
				{
					return new ResourceLocation("xuphorium_craft:textures/entities/x_creeper.png");
				}
			};
		});
	}

	public static class EntityXCreeper extends EntityCreeper
	{
		//============Register About============//
		public static final ResourceLocation LOOT_TABLE_DEATH=LootTableList.register(new ResourceLocation("xuphorium_craft:entities/x_creeper"));
		
		public EntityXCreeper(World world)
		{
			super(world);
			this.setSize(0.6f,1.7f);
			this.experienceValue=8;
			this.isImmuneToFire=true;
			this.setNoAI(false);
		}
		
		@Override
		protected ResourceLocation getLootTable()
		{
			return EntityXCreeper.LOOT_TABLE_DEATH;
		}
		
		@Override
		protected void initEntityAI()
		{
			this.tasks.addTask(1,new EntityAISwimming(this));
			this.tasks.addTask(2,new EntityAICreeperSwell(this));
			this.tasks.addTask(3,new EntityAIAvoidEntity(this,XBoss.EntityXBoss.class,8F,1.0D,1.1D));
			this.tasks.addTask(4,new EntityAIAttackMelee(this,1.0D,true));
			this.tasks.addTask(5,new EntityAILeapAtTarget(this,(float)1.125));
			this.tasks.addTask(6,new EntityAIWatchClosest(this,EntityCreeper.class,16.0F));
			this.tasks.addTask(6,new EntityAIWatchClosest(this,XBoss.EntityXBoss.class,16.0F));
			this.tasks.addTask(7,new EntityAIWanderAvoidWater(this,0.8D));
			this.tasks.addTask(7,new EntityAILookIdle(this));
			
			this.targetTasks.addTask(1,new EntityAIHurtByTarget(this,true));
			this.targetTasks.addTask(2,new EntityAINearestAttackableTarget(this,EntityPlayer.class,true));
			this.targetTasks.addTask(3,new EntityAINearestAttackableTarget(this,EntityVillager.class,true));
		}

		@Override
		public EnumCreatureAttribute getCreatureAttribute()
		{
			return EnumCreatureAttribute.UNDEFINED;
		}
		
		@Override
		public net.minecraft.util.SoundEvent getHurtSound(DamageSource ds)
		{
			return SoundEvents.ENTITY_CREEPER_HURT;
		}

		@Override
		public net.minecraft.util.SoundEvent getDeathSound()
		{
			return SoundEvents.ENTITY_CREEPER_DEATH;
		}
		
		protected boolean canDespawn()
		{
			return false;
		}

		@Override
		protected float getSoundVolume()
		{
			return 1.2F;
		}
		
		@Override
		protected void applyEntityAttributes()
		{
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(0D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20D);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5D);
			this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32D);
		}
		
		
		//============Events============//
		@Override
		public boolean attackEntityFrom(DamageSource source,float amount)
		{
			if(source==DamageSource.LIGHTNING_BOLT) return false;
			return super.attackEntityFrom(source,amount);
		}
		
		@Override
		public void setDead()
		{
			if(!world.isRemote)
			{
				if(this.getPowered())
				{
					List<Entity> entities=world.loadedEntityList;
					double dx,dy,dz;
					for(Entity entity : entities)
					{
						if(entity instanceof EntityLivingBase)
						{
							dx=this.posX-entity.posX;
							dy=this.posY-entity.posY;
							dz=this.posZ-entity.posZ;
							if(dx*dx+dy*dy+dz*dz<=36) world.addWeatherEffect(new EntityLightningBolt(world,entity.posX,entity.posY,entity.posZ,false));
						}
					}
				}
				else world.addWeatherEffect(new EntityLightningBolt(world,this.posX,this.posY,this.posZ,false));
				XCraftBlocks.XExplosive.XExplosiveExplode(world,new BlockPos(this.posX,this.posY,this.posZ),this.getPowered()?5:3,true,true);
			}
			super.setDead();
		}

		@Override
		public void onDeath(DamageSource source)
		{
			super.onDeath(source);
			Entity entity=this;
			int i;
			EntityItem entityToSpawn;
			if(!world.isRemote)
			{
				//XCraftBlocks.XExplosive.XExplosiveExplode(world,new BlockPos(this.posX,this.posY,this.posZ),this.getPowered()?5:3,true);
				/*for(i=0;i<Math.round(Math.random()*4+1);i++)
				{
					entityToSpawn=new EntityItem(world,this.posX,this.posY,this.posZ,new ItemStack(XCraftMaterials.X_DUST,1));
					entityToSpawn.setPickupDelay(10);
					world.spawnEntity(entityToSpawn);
				}
				if(Math.random()<Math.random())
				{
					entityToSpawn=new EntityItem(world,this.posX,this.posY,this.posZ,new ItemStack(XCraftMaterials.X_CATALYST,1));
					entityToSpawn.setPickupDelay(10);
					world.spawnEntity(entityToSpawn);
				}
				if(Math.random()+Math.random()<Math.random()*Math.random())
				{
					entityToSpawn=new EntityItem(world,this.posX,this.posY,this.posZ,new ItemStack(XCraftMaterials.X_CRYSTAL_CORE,1));
					entityToSpawn.setPickupDelay(10);
					world.spawnEntity(entityToSpawn);
				}*/
			}
		}
		/*
		@Override
		private void explode()
		{
			if(!this.world.isRemote)
			{
				boolean flag=net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this);
				float f=this.getPowered()?2.0F:1.0F;
				this.dead=true;
				List<Entity> entities=world.loadedEntityList;
				double dx,dy,dz,distance;
				for(Entity entity : entities)
				{
					if(entity instanceof EntityLivingBase)
					{
						dx=this.posX-entity.posX;
						dy=this.posY-entity.posY;
						dz=this.posZ-entity.posZ;
						distance=Math.sqrt(dx*dx+dy*dy+dz*dz);
						if(distance<=6) world.addWeatherEffect(new EntityLightningBolt(world,this.posX,this.posY,this.posZ,true));
					}
				}
				//this.world.createExplosion(this,this.posX,this.posY,this.posZ,(float)this.explosionRadius*f, flag);
				this.setDead();
				//this.spawnLingeringCloud();
			}
		}
*/
	}
}
