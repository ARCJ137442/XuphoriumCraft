package xuphorium_craft.item;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.item.ItemArmor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraft.world.World;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import net.minecraft.inventory.EntityEquipmentSlot;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import xuphorium_craft.*;
import xuphorium_craft.common.*;
import xuphorium_craft.proxy.*;
import xuphorium_craft.entity.*;
import xuphorium_craft.block.*;
import xuphorium_craft.item.*;

@XuphoriumCraftElements.ModElement.Tag
public class XArmor extends XuphoriumCraftElements.ModElement
{
	@GameRegistry.ObjectHolder("xuphorium_craft:x_helmet")
	public static final Item X_HELMET=null;
	@GameRegistry.ObjectHolder("xuphorium_craft:x_chestplate")
	public static final Item X_CHESTPLATE=null;
	@GameRegistry.ObjectHolder("xuphorium_craft:x_leggings")
	public static final Item X_LEGGINGS=null;
	@GameRegistry.ObjectHolder("xuphorium_craft:x_boots")
	public static final Item X_BOOTS=null;

	public XArmor(XuphoriumCraftElements instance)
	{
		super(instance,4);
	}
	
	@Override
	public void initElements()
	{
		elements.items.add(()-> new XHelmet());
		elements.items.add(()-> new XChestplate());
		elements.items.add(()-> new XLeggings());
		elements.items.add(()-> new XBoots());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels(ModelRegistryEvent event)
	{
		ModelLoader.setCustomModelResourceLocation(X_HELMET,0,new ModelResourceLocation("xuphorium_craft:x_helmet","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_CHESTPLATE,0,new ModelResourceLocation("xuphorium_craft:x_chestplate","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_LEGGINGS,0,new ModelResourceLocation("xuphorium_craft:x_leggings","inventory"));
		ModelLoader.setCustomModelResourceLocation(X_BOOTS,0,new ModelResourceLocation("xuphorium_craft:x_boots","inventory"));
	}
	
	public class XArmorCommon extends ItemArmor
	{
		public XArmorCommon(EntityEquipmentSlot slot,String name)
		{
			super(XCraft.xArmorMaterial,0,slot);
			this.setUnlocalizedName(name);
			this.setRegistryName(name);
			this.setCreativeTab(XCraft.CREATIVE_TAB);
		}
	}
	
	public class XHelmet extends XArmorCommon
	{
		public XHelmet()
		{
			super(EntityEquipmentSlot.HEAD,"x_helmet");
		}
	}
	
	public class XChestplate extends XArmorCommon
	{
		public XChestplate()
		{
			super(EntityEquipmentSlot.CHEST,"x_chestplate");
		}
	}
	
	public class XLeggings extends XArmorCommon
	{
		public XLeggings()
		{
			super(EntityEquipmentSlot.LEGS,"x_leggings");
		}
	}
	
	public class XBoots extends XArmorCommon
	{
		public XBoots()
		{
			super(EntityEquipmentSlot.FEET,"x_boots");
		}
		/*
		@Override
		public void onUpdate(ItemStack stack,World world,Entity entity,int par4,boolean par5)
		{
			
			EntityEquipmentSlot entityequipmentslot=EntityLiving.getSlotForItemStack(stack);
			if(entityequipmentslot==EntityEquipmentSlot.FEET&&stack.getItem()==X_BOOTS)
			{
				if(entity.motionY<0) entity.motionY*=0.2;
				else if(entity.motionY>0) entity.motionY+=0.08;
			}
		}*/
	}
}
