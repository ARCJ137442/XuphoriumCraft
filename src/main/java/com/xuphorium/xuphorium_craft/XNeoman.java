package com.xuphorium.xuphorium_craft;

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
public class XNeoman extends XuphoriumCraftElements.ModElement
{
	//============Register About============//
	public static final int ENTITYID=7;

	public XNeoman(XuphoriumCraftElements instance)
	{
		super(instance,2);
	}

	@Override
	public void initElements()
	{
		elements.entities.add(()->EntityEntryBuilder.create().entity(EntityXNeoman.class).id(
				new ResourceLocation(XuphoriumCraft.MODID,"x_neoman"),ENTITYID
		).name("x_neoman").tracker(64,1,true).egg(0x0000ff,0x00ff00).build());
	}
	
	@SideOnly(Side.CLIENT)
	//@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		//Add Render
		RenderingRegistry.registerEntityRenderingHandler(EntityXNeoman.class,renderManager -> {
			RenderBiped customRender=new RenderBiped(renderManager,new ModelBiped(),0.5f)
			{
				protected ResourceLocation getEntityTexture(Entity entity)
				{
					return new ResourceLocation("xuphorium_craft:textures/entities/x_neoman.png");
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
	                                            double radius,EntityLivingBase target,boolean overrideTarget)
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
	public static class EntityXNeoman extends EntityMob
	{
		public static final ResourceLocation LOOT_TABLE_DEATH=LootTableList.register(new ResourceLocation("xuphorium_craft:entities/x_neoman"));

		public EntityXNeoman(World world)
		{
			super(world);
			this.setSize(0.6f,1.8f);
			this.experienceValue=5;
			this.isImmuneToFire=false;
			this.setCustomNameTag("X-Neoman");
			this.setAlwaysRenderNameTag(false);
		}

		@Override
		protected ResourceLocation getLootTable()
		{
			return XNeoman.EntityXNeoman.LOOT_TABLE_DEATH;
		}

		public static final Class[] nearestAttackableTargets=new Class[]
		{
				EntityWither.class
		};
		
		@Override
		protected void initEntityAI()
		{
			this.aiTaskAdd(new EntityAISwimming(this));
			this.aiTaskAdd(new EntityXNeomanAIAttackMelee(this,1,true));
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
				if(victim instanceof EntityXNeoman) return;
				if(this.world.getDifficulty() == EnumDifficulty.PEACEFUL) return;

				boolean hardcore=this.world.getDifficulty() == EnumDifficulty.NORMAL || this.world.getDifficulty() == EnumDifficulty.HARD;
				EntityXNeoman entitySubstitute = new EntityXNeoman(victim.world);
				entitySubstitute.copyLocationAndAnglesFrom(victim);
				entitySubstitute.copyEntityAttributesFrom(victim,hardcore,hardcore?this:null);
				entitySubstitute.copyEntityEquipmentFrom(victim,hardcore?this:null);
				this.world.removeEntity(victim);
				//entitySubstitute.onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(entitySubstitute)), new EntityXNeoman.GroupData(false));//Init with some data
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

		protected EntityLivingBase lastTarget=null;

		protected boolean attackConGeneric=false;

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
				double radius=16;
				List<Entity> entities=world.loadedEntityList;
				//Get
				for(Entity entity: entities)
				{
					if(entity==null||entity.isDead||entity==this) continue;
					if(this.getDistance(entity) > radius) continue;
					if(entity instanceof EntityLiving)
					{
						//Copy Target nearby
						livingBase=((EntityLiving)entity).getAttackTarget();
						if(livingBase!=null&&!livingBase.isDead)
						{
							//Attack entity who will attack itself
							if(livingBase==this) livingBase=(EntityLiving)entity;
							else if(!attackConGeneric&&livingBase instanceof EntityXNeoman) continue;
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
				XNeoman.enrageNearbyEntities(world, this, 8, this.lastTarget, false);
			}
			catch(Exception exception)
			{
				XuphoriumCraft.LOGGER.error(exception.toString());
				exception.printStackTrace();
			}
		}
	}

	//Melee attack behavior
	public static class EntityXNeomanAIAttackMelee extends EntityAIAttackMelee
	{
		protected EntityXNeoman host;
		public EntityXNeomanAIAttackMelee(EntityCreature creature,double speedIn,boolean useLongMemory)
		{
			super(creature,speedIn,useLongMemory);
			if(creature instanceof EntityXNeoman) this.host=(EntityXNeoman)creature;
		}

		@Override
		protected void checkAndPerformAttack(EntityLivingBase p_190102_1_,double p_190102_2_)
		{
			double d0=this.getAttackReachSqr(p_190102_1_);

			if (p_190102_2_<=d0&&this.attackTick<=0)
			{
				this.attackTick=15;//(int)(10*(Math.random()+1))
				this.attacker.swingArm(EnumHand.MAIN_HAND);
				this.attacker.attackEntityAsMob(p_190102_1_);
			}
		}
		
		@Override
		protected double getAttackReachSqr(EntityLivingBase attackTarget)
		{
			return 16F;
		}
	}
}