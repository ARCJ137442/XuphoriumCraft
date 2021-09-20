package com.xuphorium.xuphorium_craft;

import com.google.common.base.Predicate;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.DifficultyInstance;

import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.ai.*;

import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.model.ModelBiped;

import net.minecraft.init.SoundEvents;

import javax.annotation.Nullable;
import java.util.List;
/* 
import com.xuphorium.xuphorium_craft.*;
import com.xuphorium.xuphorium_craft.common.*;
import com.xuphorium.xuphorium_craft.proxy.*;
import com.xuphorium.xuphorium_craft.entity.*;
import com.xuphorium.xuphorium_craft.block.*;
import com.xuphorium.xuphorium_craft.item.*; */

@XuphoriumCraftElements.ModElement.Tag
public class Neoman extends XuphoriumCraftElements.ModElement
{
	//============Register About============//
	public static final int ENTITYID=7;

	public Neoman(XuphoriumCraftElements instance)
	{
		super(instance,2);
	}

	@Override
	public void initElements()
	{
		elements.entities.add(()->EntityEntryBuilder.create().entity(EntityNeoman.class).id(
				new ResourceLocation(XuphoriumCraft.MODID,"neoman"),ENTITYID
		).name("neoman").tracker(64,1,true).egg(0x0000ff,0x00ff00).build());
		elements.entities.add(()->EntityEntryBuilder.create().entity(EntityNeoHacker.class).id(
				new ResourceLocation(XuphoriumCraft.MODID,"neohacker"),ENTITYID+1
		).name("neohacker").tracker(64,1,true).egg(0x00cccc,0x400000).build());
	}
	
	@SideOnly(Side.CLIENT)
	//@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		//Add Render
		RenderingRegistry.registerEntityRenderingHandler(EntityNeoman.class,renderManager -> {
			RenderBiped customRender=new RenderBiped(renderManager,new ModelBiped(),0.5f)
			{
				protected ResourceLocation getEntityTexture(Entity entity)
				{
					return new ResourceLocation("xuphorium_craft:textures/entities/"+((entity instanceof EntityNeoHacker)?"neohacker":"neoman")+".png");
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
		RenderingRegistry.registerEntityRenderingHandler(EntityNeoHacker.class,renderManager -> {
			RenderBiped customRender=new RenderBiped(renderManager,new ModelBiped(),0.5f)
			{
				protected ResourceLocation getEntityTexture(Entity entity)
				{
					return new ResourceLocation("xuphorium_craft:textures/entities/neohacker.png");
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
	}

	public static void enrageNearbyEntities(World world,EntityLivingBase attacker,
											float radius,EntityLivingBase target,
											boolean overrideTarget,boolean limitConGeneric)
	{
		//Initial check
		if(attacker==null||attacker.isDead||target==null||target.isDead) return;
		try
		{
			List<Entity> entities=world.loadedEntityList;
			//Get
			for(Entity entity: entities)
			{
				if(entity==null||entity==target||entity==attacker) continue;
				if(limitConGeneric&&attacker.getClass()!=entity.getClass()) continue;
				if(attacker.getDistance(entity)>radius) continue;
				if(entity instanceof EntityLiving&&
						(overrideTarget||((EntityLiving)entity).getAttackTarget()==null)
				)
				{
					//Particle
					if(world instanceof WorldServer) ((WorldServer)world).spawnParticle(EnumParticleTypes.END_ROD,
							entity.posX,entity.posY,entity.posZ,16,
							entity.width*0.5,entity.height*0.5,entity.width*0.5,
							0
					);
					((EntityLiving)entity).setAttackTarget(target);
				}
			}
		}
		catch(Exception exception)
		{
			//XuphoriumCraft.LOGGER.error(exception.toString());
			//exception.printStackTrace();
		}
	}

	//====Tool Functions====//

	//============Entity Classes============//
	public static class EntityNeoman extends EntityMob
	{
		public static final ResourceLocation LOOT_TABLE_DEATH=LootTableList.register(new ResourceLocation("xuphorium_craft:entities/neoman"));

		public EntityNeoman(World world)
		{
			super(world);
			this.setSize(0.6f,1.8f);
			this.experienceValue=5;
			this.isImmuneToFire=false;
			this.setCustomNameTag("Neoman");
			this.setAlwaysRenderNameTag(false);
		}

		@Override
		protected ResourceLocation getLootTable()
		{
			return Neoman.EntityNeoman.LOOT_TABLE_DEATH;
		}

		public static final Class[] nearestAttackableTargets=new Class[]
				{
						EntityWither.class
				};

		@Override
		protected void initEntityAI()
		{
			this.aiTaskAdd(new EntityAISwimming(this));
			this.aiTaskAdd(new EntityNeomanAIAttackMelee(this,1,true));
			this.aiTaskAdd(new EntityAIMoveTowardsTarget(this,1.25D,64F));
			this.aiTaskAdd(new EntityAIMoveTowardsRestriction(this,1.0D));
			this.aiTaskAdd(new EntityAIWanderAvoidWater(this, 0.6D));
			this.aiTargetTaskAdd(new EntityAIHurtByTarget(this,true));
			for(Class entityClass : nearestAttackableTargets) this.aiTargetTaskAdd(new EntityAINearestAttackableTarget(this,entityClass,false,false));
		}

		private int aiNum=0;

		protected void addTask(EntityAITasks tasks, EntityAIBase ai)
		{
			tasks.addTask(this.aiNum++,ai);
		}

		protected void aiTaskAdd(EntityAIBase ai)
		{
			this.addTask(this.tasks,ai);
		}

		protected void aiTargetTaskAdd(EntityAIBase ai)
		{
			this.addTask(this.targetTasks,ai);
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
		/*
		protected double attribute_armor=0D;
		protected double attribute_speed=0.4D;
		protected double attribute_maxHealth=0D;
		protected double attribute_attackDamage=0D;
		protected double attribute_followRange=0D;
		*/
		@Override
		protected void applyEntityAttributes()
		{
			super.applyEntityAttributes();
			this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(0D);
			this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4D);
			this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20D);
			this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2D);
			this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64D);
		}

		//========Events========//
		public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty,IEntityLivingData livingdata)
		{
			super.onInitialSpawn(difficulty,livingdata);
			return livingdata;
		}

		//Entity has been attacked
		public boolean attackEntityFrom(DamageSource source,float amount)
		{
			boolean flag=super.attackEntityFrom(source,amount);
			return flag;
		}

		//Entity has attacked target
		public boolean attackEntityAsMob(Entity entityIn)
		{
			boolean flag=super.attackEntityAsMob(entityIn);
			return flag;
		}

		protected EntityLivingBase lastTarget=null;

		protected boolean attackConGeneric=false;
		protected boolean onlyCopyConGenericTarget=false;

		public void onUpdate()
		{
			EntityLivingBase livingBase;
			try
			{
				super.onUpdate();
			}
			catch(Exception exception)
			{
				XuphoriumCraft.LOGGER.error(exception.toString());
				exception.printStackTrace();
			}
			//Not Dead Self
			if(this.isDead) return;
			if(this.getAttackTarget()==null)
			{
				float radius=16f;
				List<Entity> entities=world.loadedEntityList;
				//Get
				for(Entity entity: entities)
				{
					if(entity==null||entity.isDead||entity==this) continue;
					if(this.getDistance(entity) > radius) continue;
					if(onlyCopyConGenericTarget&&!(entity instanceof EntityNeoman)) continue;
					if(entity instanceof EntityLiving)
					{
						//Copy Target nearby
						livingBase=((EntityLiving)entity).getAttackTarget();
						if(livingBase!=null&&!livingBase.isDead)
						{
							//Attack entity who will attack itself
							if(livingBase==this) livingBase=(EntityLiving)entity;
							else if(!attackConGeneric&&livingBase instanceof EntityNeoman) continue;
							this.setAttackTarget(livingBase);
							break;
						}
					}
				}
			}
			//Not Dead
			else if(this.getAttackTarget().isDead) this.setAttackTarget(null);
		}

		public void setAttackTarget(@Nullable EntityLivingBase p_setAttackTarget_1_)
		{
			super.setAttackTarget(p_setAttackTarget_1_);
			this.lastTarget=p_setAttackTarget_1_;
			//Enrage nearby entities when target change
			try
			{
				Neoman.enrageNearbyEntities(world, this, 8f, this.lastTarget, false,this.onlyCopyConGenericTarget);
			}
			catch(Exception exception)
			{
				XuphoriumCraft.LOGGER.error(exception.toString());
				exception.printStackTrace();
			}
		}
	}

	public static class EntityNeoHacker extends EntityNeoman
	{
		public static final ResourceLocation LOOT_TABLE_DEATH=LootTableList.register(new ResourceLocation("xuphorium_craft:entities/neohacker"));
		private static final Predicate<Entity> NOT_CONGENERIC;

		static {
			NOT_CONGENERIC = entity -> entity instanceof EntityLivingBase && !(entity instanceof EntityNeoman) && !entity.isDead && ((EntityLivingBase)entity).attackable();
		}

		public EntityNeoHacker(World world)
		{
			super(world);
			this.experienceValue=10;
			this.isImmuneToFire=true;
			this.setCustomNameTag("NeoHacker");
		}

		@Override
		protected ResourceLocation getLootTable()
		{
			return Neoman.EntityNeoHacker.LOOT_TABLE_DEATH;
		}

		@Override
		protected void initEntityAI()
		{
			this.aiTaskAdd(new EntityAISwimming(this));
			this.aiTaskAdd(new EntityNeomanAIAttackMelee(this,1,true,36,10));
			this.aiTaskAdd(new EntityAIMoveTowardsTarget(this,1.25D,64F));
			this.aiTaskAdd(new EntityAIMoveTowardsRestriction(this,1.0D));
			this.aiTaskAdd(new EntityAIWanderAvoidWater(this, 0.6D));
			this.aiTargetTaskAdd(new EntityAIHurtByTarget(this,true));
			this.aiTargetTaskAdd(new EntityAINearestAttackableTarget(this, EntityLiving.class, 10, false, false, NOT_CONGENERIC));
		}

		public EnumCreatureAttribute getCreatureAttribute()
		{
			return EnumCreatureAttribute.UNDEFINED;
		}

		/*
		protected double attribute_armor=0D;
		protected double attribute_speed=0.4D;
		protected double attribute_maxHealth=0D;
		protected double attribute_attackDamage=0D;
		protected double attribute_followRange=0D;
		*/
		@Override
		protected void applyEntityAttributes()
		{
			super.applyEntityAttributes();
		}

		public static final IAttribute[] COPY_ATTRIBUTES=new IAttribute[]{
				SharedMonsterAttributes.ARMOR,SharedMonsterAttributes.ARMOR_TOUGHNESS,
				SharedMonsterAttributes.MOVEMENT_SPEED,SharedMonsterAttributes.FLYING_SPEED,
				SharedMonsterAttributes.MAX_HEALTH,SharedMonsterAttributes.LUCK,
				SharedMonsterAttributes.ATTACK_DAMAGE,SharedMonsterAttributes.ATTACK_SPEED,
				SharedMonsterAttributes.FOLLOW_RANGE,SharedMonsterAttributes.KNOCKBACK_RESISTANCE};

		protected void copyEntityAttributesFrom(EntityLivingBase entityLiving,boolean maxMode,EntityLiving blankEntity)
		{
			IAttributeInstance attribute1,attribute2;
			double aValue;
			for(IAttribute attribute : COPY_ATTRIBUTES)
			{
				try {
					attribute1 = entityLiving.getEntityAttribute(attribute);
					if (attribute1 != null) {
						aValue = attribute1.getBaseValue();
						if (maxMode)
						{
							attribute2 = (blankEntity != null) ? blankEntity.getEntityAttribute(attribute) : this.getEntityAttribute(attribute);
							if(attribute2 != null) aValue = Math.max(attribute1.getBaseValue(), attribute2.getBaseValue());
						}
						this.getEntityAttribute(attribute).setBaseValue(aValue);
					}
				}
				catch (Exception ignored)
				{

				}
			}
		}

		public static final EntityEquipmentSlot[] COPY_ITEMSLOTS=new EntityEquipmentSlot[]{
				EntityEquipmentSlot.MAINHAND,EntityEquipmentSlot.OFFHAND,
				EntityEquipmentSlot.HEAD,EntityEquipmentSlot.CHEST,
				EntityEquipmentSlot.LEGS,EntityEquipmentSlot.FEET};

		protected void copyEntityEquipmentFrom(EntityLivingBase entityLiving,EntityLiving blankEntity)
		{
			ItemStack iStack,blankStack;
			for(EntityEquipmentSlot slot : COPY_ITEMSLOTS)
			{
				iStack=entityLiving.getItemStackFromSlot(slot);
				blankStack=(blankEntity==null)?null:blankEntity.getItemStackFromSlot(slot);
				if(iStack!=null&&!iStack.isEmpty()) this.setItemStackToSlot(slot,iStack.copy());
				else if(blankStack!=null&&!blankStack.isEmpty()) this.setItemStackToSlot(slot,blankStack.copy());
			}
		}

		//====Boss Bar====//
		public boolean isNonBoss()
		{
			return true;
		}

		//========Events========//
		public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty,IEntityLivingData livingdata)
		{
			super.onInitialSpawn(difficulty,livingdata);
			return livingdata;
		}

		//Entity has been attacked
		public boolean attackEntityFrom(DamageSource source,float amount)
		{
			boolean flag=super.attackEntityFrom(source,amount);
			return flag;
		}

		public void onKillEntity(EntityLivingBase victim) {
			super.onKillEntity(victim);
			try
			{
				//Duplicate itself as living
				if(victim instanceof EntityNeoman) return;

				boolean hardcore=this.world.getDifficulty() == EnumDifficulty.NORMAL || this.world.getDifficulty() == EnumDifficulty.HARD;
				EntityNeoHacker entitySubstitute = new EntityNeoHacker(victim.world);
				entitySubstitute.copyLocationAndAnglesFrom(victim);
				entitySubstitute.copyEntityAttributesFrom(victim,hardcore,hardcore?this:null);
				entitySubstitute.copyEntityEquipmentFrom(victim,hardcore?this:null);
				this.world.removeEntity(victim);
				//entitySubstitute.onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(entitySubstitute)), new EntityNeoHacker.GroupData(false));//Init with some data
				if(victim instanceof EntityLiving)entitySubstitute.setNoAI(((EntityLiving)victim).isAIDisabled());
				if (victim.hasCustomName()) {
					entitySubstitute.setCustomNameTag(victim.getCustomNameTag());
					entitySubstitute.setAlwaysRenderNameTag(victim.getAlwaysRenderNameTag());
				}
				this.world.spawnEntity(entitySubstitute);
				entitySubstitute.setHealth(entitySubstitute.getMaxHealth());
				//Particle
				if(world instanceof WorldServer) ((WorldServer)world).spawnParticle(EnumParticleTypes.SMOKE_LARGE,
						entitySubstitute.posX,entitySubstitute.posY,entitySubstitute.posZ,32,
						entitySubstitute.width*0.5,entitySubstitute.height*0.5,entitySubstitute.width*0.5,
						0
				);
			}
			catch(Exception exception)
			{
				XuphoriumCraft.LOGGER.error(exception.toString());
				exception.printStackTrace();
			}
		}

		//Entity has attacked target
		public boolean attackEntityAsMob(Entity entityIn)
		{
			boolean flag=super.attackEntityAsMob(entityIn);
			return flag;
		}

		@Override
		public void onUpdate()
		{
			if(!this.onlyCopyConGenericTarget) this.onlyCopyConGenericTarget=true;
			super.onUpdate();
		}
	}

	//Melee attack behavior
	public static class EntityNeomanAIAttackMelee extends EntityAIAttackMelee
	{
		protected EntityNeoman host;
		protected double attackRangeSqr=16F;
		protected int attackPeriod=15;

		public EntityNeomanAIAttackMelee(EntityCreature creature,double speedIn,boolean useLongMemory,double attackRangeSqr,int attackPeriod)
		{
			this(creature, speedIn, useLongMemory);
			this.attackRangeSqr=attackRangeSqr;
			this.attackPeriod=attackPeriod;
		}

		public EntityNeomanAIAttackMelee(EntityCreature creature,double speedIn,boolean useLongMemory)
		{
			super(creature,speedIn,useLongMemory);
			if(creature instanceof EntityNeoman) this.host=(EntityNeoman)creature;
		}

		@Override
		protected void checkAndPerformAttack(EntityLivingBase p_190102_1_,double p_190102_2_)
		{
			double d0=this.getAttackReachSqr(p_190102_1_);

			if (p_190102_2_<=d0&&this.attackTick<=0)
			{
				this.attackTick=this.attackPeriod;//(int)(10*(Math.random()+1))
				this.attacker.swingArm(EnumHand.MAIN_HAND);
				this.attacker.attackEntityAsMob(p_190102_1_);
			}
		}
		
		@Override
		protected double getAttackReachSqr(EntityLivingBase attackTarget)
		{
			return this.attackRangeSqr;
		}
	}
}