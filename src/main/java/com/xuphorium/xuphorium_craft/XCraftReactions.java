package xuphorium_craft;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.BlockFluidClassic;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.util.ResourceLocation;

import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.ItemMeshDefinition;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;

import net.minecraft.world.World;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;

import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Rotation;
import net.minecraft.util.Mirror;

import net.minecraft.init.Blocks;

import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityFallingBlock;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.material.Material;
import net.minecraft.block.SoundType;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;

import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyBool;

import net.minecraft.creativetab.CreativeTabs;

import java.util.HashMap;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
/* 
import com.xuphorium.XuphoriumCraft*;
import com.xuphorium.XuphoriumCraftcommon.*;
import com.xuphorium.XuphoriumCraftproxy.*;
import com.xuphorium.XuphoriumCraftentity.*;
import com.xuphorium.XuphoriumCraftblock.*;
import com.xuphorium.XuphoriumCraftitem.*; */

@XuphoriumCraftElements.ModElement.Tag
public class XCraftReactions extends XuphoriumCraftElements.ModElement
{
	//============Static============//
	//Consts
	protected static final Item IRON_INGOT=Item.getByNameOrId("iron_ingot");
	protected static final Item REDSTONE=Item.getByNameOrId("redstone");
	protected static final Item GLOWSTONE_DUST=Item.getByNameOrId("glowstone_dust");
	protected static final Item DIAMOND=Item.getByNameOrId("diamond");
	protected static final Item EMERALD=Item.getByNameOrId("emerald");
	protected static final Item QUARTZ=Item.getByNameOrId("quartz");
	protected static final Item ENDER_PEARL=Item.getByNameOrId("ender_pearl");
	protected static final Block SAND=Block.getBlockFromName("sand");
	//====Reactions====//
	//I
	public static Reaction REACTION_I_1;
	public static Reaction REACTION_I_2;
	public static Reaction REACTION_I_3;
	//II
	public static Reaction REACTION_II_1;
	public static Reaction REACTION_II_2;
	public static Reaction REACTION_II_3;
	public static Reaction REACTION_II_4;
	public static Reaction REACTION_II_5;
	//III
	public static Reaction REACTION_III;
	//IV
	public static Reaction REACTION_IV;
	//V
	public static Reaction REACTION_V;
	//VI
	public static Reaction REACTION_VI;
	//VII
	public static Reaction REACTION_VII;
	//VIII
	public static Reaction REACTION_VIII;
	//IX
	public static Reaction REACTION_IX;
	//X
	public static Reaction REACTION_X;
	//XI
	public static Reaction REACTION_XI;
	//XII
	public static Reaction REACTION_XII;
	//XIII
	public static Reaction REACTION_XIII_1;
	public static Reaction REACTION_XIII_2;
	public static Reaction REACTION_XIII_3;
	public static Reaction REACTION_XIII_4;
	public static Reaction REACTION_XIII_5;
	public static Reaction REACTION_XIII_6;
	public static Reaction REACTION_XIII_7;
	public static Reaction REACTION_XIII_8;
	public static Reaction REACTION_XIII_9;
	public static Reaction REACTION_XIII_10;
	//REACTIONS
	public static Reaction[] REACTIONS;
	
	//====ReactionContainers====//	
	public static ReactionContainer CONTAINER_1;
	public static ReactionContainer CONTAINER_2_I;
	public static ReactionContainer CONTAINER_2_II;
	public static ReactionContainer CONTAINER_3_I;
	public static ReactionContainer CONTAINER_3_II;
	public static ReactionContainer CONTAINER_4_I;
	public static ReactionContainer CONTAINER_4_II;
	public static ReactionContainer CONTAINER_5_I;
	public static ReactionContainer CONTAINER_5_II;
	public static ReactionContainer CONTAINER_5_III;
	public static ReactionContainer CONTAINER_5_IV;
	public static ReactionContainer CONTAINER_5_V;
	public static ReactionContainer CONTAINER_6;
	//CONUAINERS
	public static ReactionContainer[] REACTION_CONUAINERS;

	@Override
	public void init(FMLInitializationEvent event)
	{
		//Reactions
		REACTION_I_1=new Reaction(
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_DUST),
				new ItemStack(IRON_INGOT)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_INGOT),
				new ItemStack(REDSTONE)
				),1);
		REACTION_I_2=new Reaction(
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_INGOT),
				new ItemStack(REDSTONE)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_DUST),
				new ItemStack(IRON_INGOT)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_CATALYST)
				),-1);
		REACTION_I_3=new Reaction(
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_DUST,2),
				new ItemStack(SAND)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftBlocks.X_GLASS)
				),-1);
		//II
		REACTION_II_1=new Reaction(
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_DUST,8),
				new ItemStack(XCraftMaterials.X_CRYSTAL)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_CATALYST,4)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftTools.X_ITEM)
				),-1);
		REACTION_II_2=new Reaction(
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_CATALYST,6),
				new ItemStack(REDSTONE),
				new ItemStack(GLOWSTONE_DUST)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_RUBY,2)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftTools.X_ITEM)
				),-2);
		REACTION_II_3=new Reaction(
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_RUBY),
				new ItemStack(XCraftMaterials.X_DUST),
				new ItemStack(REDSTONE)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_CRYSTAL_LEFT)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftTools.X_ITEM)
				),1);
		REACTION_II_4=new Reaction(
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_RUBY),
				new ItemStack(XCraftMaterials.X_DUST),
				new ItemStack(GLOWSTONE_DUST)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_CRYSTAL_RIGHT)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftTools.X_ITEM)
				),1);
		REACTION_II_5=new Reaction(
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_CRYSTAL_LEFT),
				new ItemStack(XCraftMaterials.X_CRYSTAL_RIGHT),
				new ItemStack(XCraftMaterials.X_DUST,2)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_CRYSTAL,2)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftTools.X_MAGNET)
				),1);
		//III
		REACTION_III=new Reaction(
			XCraftBlocks.X_FLUID,
			XCraftBlocks.X_OXYGEN,
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftTools.X_MAGNET)
				),-4);
		//IV
		REACTION_IV=new Reaction(
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_CRYSTAL_LEFT),
				new ItemStack(XCraftMaterials.X_CRYSTAL_RIGHT)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_CRYSTAL,2)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_FUEL)
				),
				XCraftBlocks.X_OXYGEN,
				XCraftBlocks.X_LIQUID,2);
		//V
		REACTION_V=new Reaction(
			null,
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_COVALENT_SHARD,4),
				new ItemStack(XCraftMaterials.X_ION_PIECE,4)
				),
			null,
			XCraftBlocks.X_LIQUID,
			null,-8);
		//VI
		REACTION_VI=new Reaction(
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_COVALENT_SHARD),
				new ItemStack(XCraftMaterials.X_CATALYST),
				new ItemStack(XCraftMaterials.X_ROD,4)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_PHASE_FIBER)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_FUEL)
				),-16);
		//VII
		REACTION_VII=new Reaction(
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_PHASE_FIBER,4),
				new ItemStack(XCraftMaterials.X_DUST,4),
				new ItemStack(XCraftMaterials.X_CATALYST)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_PHASE_WEB,2)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_ION_PIECE)
				),-4);
		//VIII
		REACTION_VIII=new Reaction(
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_ION_PIECE),
				new ItemStack(XCraftMaterials.X_CATALYST)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_METAL)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_INGOT)
				),-8);
		//IX
		REACTION_IX=new Reaction(
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_INGOT,4),
				new ItemStack(XCraftMaterials.X_DIAMOND),
				new ItemStack(XCraftMaterials.X_EMERALD),
				new ItemStack(XCraftMaterials.X_RUBY)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_ALPHA_METAL)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_CRYSTAL)
				),
				XCraftBlocks.X_OXYGEN,
				null,-12);
		//X
		REACTION_X=new Reaction(
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_METAL),
				new ItemStack(XCraftMaterials.X_ION_PIECE),
				new ItemStack(XCraftMaterials.X_PEARL)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_GAMMA_METAL)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_RUBY)
				),-12);
		//XI
		REACTION_XI=new Reaction(
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_ALPHA_METAL),
				new ItemStack(XCraftMaterials.X_DUST),
				new ItemStack(XCraftMaterials.X_CRYSTAL),
				new ItemStack(XCraftMaterials.X_ION_PIECE,2)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_DELTA_METAL)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_FUEL)
				),-12);
		//XII
		REACTION_XII=new Reaction(
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_DELTA_METAL),
				new ItemStack(XCraftMaterials.X_GAMMA_METAL),
				new ItemStack(XCraftMaterials.X_EYE),
				new ItemStack(XCraftMaterials.X_FUEL),
				new ItemStack(XCraftMaterials.X_STAR)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_OMEGA_METAL,2)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_PHASE_WEB)
				),-12);
		//XIII
		REACTION_XIII_1=new Reaction(
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_METAL),
				new ItemStack(IRON_INGOT,8)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_INGOT,8)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_CATALYST)
				),+2);
		REACTION_XIII_2=new Reaction(
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_ION_PIECE),
				new ItemStack(REDSTONE,8)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_DUST,8)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_CATALYST)
				),+2);
		REACTION_XIII_3=new Reaction(
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_COVALENT_SHARD),
				new ItemStack(DIAMOND,8)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_DIAMOND,2)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_CATALYST)
				),+2);
		REACTION_XIII_4=new Reaction(
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_COVALENT_SHARD),
				new ItemStack(EMERALD,8)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_EMERALD,2)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_CATALYST)
				),+2);
		REACTION_XIII_5=new Reaction(
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_COVALENT_SHARD),
				new ItemStack(QUARTZ,8)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_CRYSTAL_LEFT),
				new ItemStack(XCraftMaterials.X_CRYSTAL_RIGHT)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_CATALYST)
				),+1);
		REACTION_XIII_6=new Reaction(
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_COVALENT_SHARD),
				new ItemStack(SAND,16)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_CRYSTAL)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_CATALYST),
				new ItemStack(XCraftMaterials.X_FUEL)
				),-1);
		REACTION_XIII_7=new Reaction(
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_COVALENT_SHARD),
				new ItemStack(QUARTZ,8),
				new ItemStack(REDSTONE,4)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_RUBY,2)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_CATALYST)
				),+2);
		REACTION_XIII_8=new Reaction(
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_DUST,2),
				new ItemStack(XCraftMaterials.X_INGOT,2)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_ROD,4,0)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_COVALENT_SHARD)
				),-1);
		REACTION_XIII_9=new Reaction(
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_ION_PIECE),
				new ItemStack(ENDER_PEARL,4)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_PEARL)
				),
			(List<ItemStack>)Arrays.asList(
				new ItemStack(XCraftMaterials.X_COVALENT_SHARD)
				),+1);
		REACTION_XIII_10=new Reaction(
		(List<ItemStack>)Arrays.asList(
			new ItemStack(XCraftMaterials.X_COVALENT_SHARD),
			new ItemStack(XCraftMaterials.X_ION_PIECE),
			new ItemStack(XCraftMaterials.X_CRYSTAL,8),
			new ItemStack(XCraftMaterials.X_FUEL),
			new ItemStack(XCraftMaterials.X_EYE)
			),
		(List<ItemStack>)Arrays.asList(
			new ItemStack(XCraftMaterials.X_STAR)
			),
		(List<ItemStack>)Arrays.asList(
			new ItemStack(XCraftMaterials.X_PHASE_FIBER)
			),+2);
		//ReactionContainers
		CONTAINER_1=new ReactionContainer(
			REACTION_I_1,REACTION_I_2,REACTION_I_3,REACTION_II_1
			).setCornerBlockAt(0,XCraftBlocks.X_BLOCK
			).setCornerBlockAt(1,XCraftBlocks.X_DUST_BLOCK
			).setCornerBlockAt(2,XCraftBlocks.X_BLOCK
			).setSideBlockAt(2,XCraftBlocks.X_GLASS
			).setInnerBlockAt(2,XCraftBlocks.X_BLOCK_ADVANCED
			).setName("Container_1");
		CONTAINER_2_I=new ReactionContainer(
			REACTION_V
			).setCornerBlockAt(0,XCraftBlocks.X_BLOCK
			).setCornerBlockAt(1,XCraftBlocks.X_GLASS
			).setSideBlockAt(2,XCraftBlocks.X_CRYSTAL_BLOCK
			).setInnerBlockAt(2,XCraftBlocks.X_CRYSTAL_BLOCK
			).setName("Container_2_I");
		CONTAINER_2_II=new ReactionContainer(
			REACTION_II_2,REACTION_II_3,REACTION_II_4,REACTION_II_5
			).setCornerBlockAt(0,XCraftBlocks.X_GLASS
			).setCornerBlockAt(1,XCraftBlocks.X_BLOCK_ADVANCED
			).setCornerBlockAt(2,XCraftBlocks.X_GLASS
			).setInnerBlockAt(2,XCraftBlocks.X_BLOCK_ADVANCED
			).setName("Container_2_II");
		CONTAINER_3_I=new ReactionContainer(
			REACTION_IV
			).setCornerBlockAt(0,XCraftBlocks.X_CRYSTAL_BLOCK
			).setCornerBlockAt(1,XCraftBlocks.X_CRYSTAL_BLOCK
			).setCornerBlockAt(2,XCraftBlocks.X_CRYSTAL_BLOCK
			).setSideBlockAt(2,XCraftBlocks.X_CRYSTAL_BLOCK
			).setInnerBlockAt(2,XCraftBlocks.X_GLASS
			).setName("Container_3_I");
		CONTAINER_3_II=new ReactionContainer(
			REACTION_III
			).setCornerBlockAt(0,XCraftBlocks.X_BLOCK_ADVANCED
			).setSideBlockAt(0,XCraftBlocks.X_GLASS
			).setCornerBlockAt(1,XCraftBlocks.X_GLASS
			).setCornerBlockAt(2,XCraftBlocks.X_BLOCK_ADVANCED
			).setSideBlockAt(2,XCraftBlocks.X_GLASS
			).setName("Container_3_II");
		CONTAINER_4_I=new ReactionContainer(
			REACTION_VI
			).setCornerBlockAt(0,XCraftBlocks.X_EMERALD_BLOCK
			).setCornerBlockAt(1,XCraftBlocks.X_BLOCK_ADVANCED
			).setSideBlockAt(2,XCraftBlocks.X_BLOCK_ADVANCED
			).setInnerBlockAt(2,XCraftBlocks.X_DIAMOND_BLOCK
			).setName("Container_4_I");
		CONTAINER_4_II=new ReactionContainer(
			REACTION_VII
			).setCornerBlockAt(0,XCraftBlocks.X_RUBY_BLOCK
			).setCornerBlockAt(1,XCraftBlocks.X_GLASS
			).setCornerBlockAt(2,XCraftBlocks.X_BLOCK_ADVANCED
			).setSideBlockAt(2,XCraftBlocks.X_GLASS
			).setInnerBlockAt(2,XCraftBlocks.X_PHASE_FIBER_BLOCK
			).setName("Container_4_II");
		CONTAINER_5_I=new ReactionContainer(
			REACTION_VIII
			).setCornerBlockAt(0,XCraftBlocks.X_DIAMOND_BLOCK
			).setCornerBlockAt(1,XCraftBlocks.X_BLOCK
			).setCornerBlockAt(2,XCraftBlocks.X_EMERALD_BLOCK
			).setSideBlockAt(2,XCraftBlocks.X_GLASS
			).setInnerBlockAt(2,XCraftBlocks.X_PHASE_FIBER_BLOCK
			).setName("Container_5_I");
		CONTAINER_5_II=new ReactionContainer(
			REACTION_IX
			).setCornerBlockAt(0,XCraftBlocks.X_RUBY_BLOCK
			).setCornerBlockAt(1,XCraftBlocks.X_EMERALD_BLOCK
			).setCornerBlockAt(2,XCraftBlocks.X_BLOCK_ADVANCED
			).setSideBlockAt(2,XCraftBlocks.X_BLOCK_ADVANCED
			).setInnerBlockAt(2,XCraftBlocks.X_COVALENT_SOLID
			).setName("Container_5_II");
		CONTAINER_5_III=new ReactionContainer(
			REACTION_X
			).setCornerBlockAt(0,XCraftBlocks.X_EMERALD_BLOCK
			).setCornerBlockAt(1,XCraftBlocks.X_BLOCK_ADVANCED
			).setCornerBlockAt(2,XCraftBlocks.X_COVALENT_SOLID
			).setSideBlockAt(2,XCraftBlocks.X_PHASE_FIBER_BLOCK
			).setInnerBlockAt(2,XCraftBlocks.X_METAL_BLOCK
			).setName("Container_5_III");
		CONTAINER_5_IV=new ReactionContainer(
			REACTION_XI
			).setCornerBlockAt(0,XCraftBlocks.X_PHASE_FIBER_BLOCK
			).setCornerBlockAt(1,XCraftBlocks.X_BLOCK_ADVANCED
			).setCornerBlockAt(2,XCraftBlocks.X_GLASS
			).setSideBlockAt(2,XCraftBlocks.X_RUBY_BLOCK
			).setInnerBlockAt(2,XCraftBlocks.X_PHASE_WEB_BLOCK
			).setName("Container_5_IV");
		CONTAINER_5_V=new ReactionContainer(
			REACTION_XII
			).setCornerBlockAt(0,XCraftBlocks.X_METAL_BLOCK
			).setCornerBlockAt(1,XCraftBlocks.X_COVALENT_SOLID
			).setCornerBlockAt(2,XCraftBlocks.X_PHASE_FIBER_BLOCK
			).setSideBlockAt(2,XCraftBlocks.X_PHASE_WEB_BLOCK
			).setInnerBlockAt(2,XCraftBlocks.X_CURER
			).setName("Container_5_V");
		CONTAINER_6=new ReactionContainer(
			REACTION_XIII_1,REACTION_XIII_2,REACTION_XIII_3,REACTION_XIII_4,REACTION_XIII_5,
			REACTION_XIII_6,REACTION_XIII_7,REACTION_XIII_8,REACTION_XIII_9,REACTION_XIII_10
			).setCornerBlockAt(0,XCraftBlocks.X_PHASE_WEB_BLOCK
			).setCornerBlockAt(1,XCraftBlocks.X_COVALENT_SOLID
			).setCornerBlockAt(2,XCraftBlocks.X_PHASE_WEB_BLOCK
			).setSideBlockAt(2,XCraftBlocks.X_COVALENT_SOLID
			).setInnerBlockAt(2,XCraftBlocks.X_METAL_BLOCK
			).setName("Container_6");
		//Lists
			REACTIONS=new Reaction[]{
			REACTION_I_1,REACTION_I_2,REACTION_I_3,
			REACTION_II_1,REACTION_II_2,REACTION_II_3,REACTION_II_4,REACTION_II_5,
			REACTION_III,
			REACTION_IV,
			REACTION_V,
			REACTION_VI,
			REACTION_VII,
			REACTION_VIII,
			REACTION_IX,
			REACTION_X,
			REACTION_XI,
			REACTION_XII,
			REACTION_XIII_1,REACTION_XIII_2,REACTION_XIII_3,REACTION_XIII_4,REACTION_XIII_5,
			REACTION_XIII_6,REACTION_XIII_7,REACTION_XIII_8,REACTION_XIII_9,REACTION_XIII_10
		};
		REACTION_CONUAINERS=new ReactionContainer[]{
			CONTAINER_1,
			CONTAINER_2_I,CONTAINER_2_II,
			CONTAINER_3_I,CONTAINER_3_II,
			CONTAINER_4_I,CONTAINER_4_II,
			CONTAINER_5_I,CONTAINER_5_II,CONTAINER_5_III,CONTAINER_5_IV,CONTAINER_5_V
		};
	}

	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		
	}
	
	public static ReactionContainer detectContainer(World world,BlockPos pos)
	{
		for(ReactionContainer container : REACTION_CONUAINERS)
		{
			if(container.isCurrentBlocks(world,pos)) return container;
		}
		return null;
	}
	
	public XCraftReactions(XuphoriumCraftElements instance)
	{
		super(instance,4);
	}
	
	//Change the argument(items) for return old items,return the new items
	public static ReactionItemContainer reactiveAndReturnProducts(ReactionContainer rContainer,ReactionItemContainer iContainer)
	{
		ReactionItemContainer result=new ReactionItemContainer();
		if(iContainer.isEmpty()) return result;
		ReactionItemContainer reactionContainer=iContainer.copy();
		List<Reaction> allowedReactions=rContainer.containReactions;
		if(allowedReactions==null||allowedReactions.isEmpty()) return result;
		for(Reaction reaction : allowedReactions)
		{
			
		}
		return result;
	}
	
	public static List<ReactionItem> reactiveAndReturnNew(List<ReactionItem> items,ReactionContainer container)
	{
		List<ReactionItem> result=new ArrayList<ReactionItem>();
		if(items.isEmpty()) return result;
		ReactionItemContainer reactionContainer=new ReactionItemContainer(items);
		List<Reaction> allowedReactions=container.containReactions;
		if(allowedReactions==null||allowedReactions.isEmpty()) return result;
		
		return result;
	}
	
	//================Classes================//
	public static class Reaction
	{
		//============Instance============//
		protected int energy=0;
		protected List<ItemStack> inputStacks;
		protected List<ItemStack> outputStacks;
		protected List<ItemStack> catalystStacks;
		protected Block inputBlock=null;
		protected Block outputBlock=null;
		
		public Reaction(List<ItemStack> inputStacks,List<ItemStack> outputStacks,int energy)
		{
			this.energy=energy;
			this.inputStacks=inputStacks;
			this.outputStacks=outputStacks;
		}
		
		public Reaction(List<ItemStack> inputStacks,List<ItemStack> outputStacks,List<ItemStack> catalystStacks,int energy)
		{
			this.energy=energy;
			this.inputStacks=inputStacks;
			this.outputStacks=outputStacks;
			this.catalystStacks=catalystStacks;
		}
		
		public Reaction(List<ItemStack> inputStacks,List<ItemStack> outputStacks,List<ItemStack> catalystStacks,Block inputBlock,Block outputBlock,int energy)
		{
			this.energy=energy;
			this.inputStacks=inputStacks;
			this.outputStacks=outputStacks;
			this.catalystStacks=catalystStacks;
			this.inputBlock=inputBlock;
			this.outputBlock=outputBlock;
		}
		
		public Reaction(Block inputBlock,Block outputBlock,int energy)
		{
			this.energy=energy;
			this.inputBlock=inputBlock;
			this.outputBlock=outputBlock;
		}
		
		public Reaction(Block inputBlock,Block outputBlock,List<ItemStack> catalystStacks,int energy)
		{
			this.energy=energy;
			this.inputBlock=inputBlock;
			this.outputBlock=outputBlock;
			this.catalystStacks=catalystStacks;
		}
		
		public List<ItemStack> getInput()
		{
			return this.inputStacks;
		}
		
		public List<ItemStack> getOutput()
		{
			return this.outputStacks;
		}
		
		public List<ItemStack> getCatalyst()
		{
			return this.outputStacks;
		}
		
		public boolean hasInputBlock()
		{
			return this.inputBlock!=null;
		}
		
		public Block getInputBlock()
		{
			return this.inputBlock;
		}
		
		public boolean hasOutputBlock()
		{
			return this.outputBlock!=null;
		}
		
		public Block getOutputBlock()
		{
			return this.outputBlock;
		}
		
		public int getEnergy()
		{
			return this.energy;
		}
		
		public List<ReactionItem> operateReaction(List<ReactionItem> input,ReactionItemContainer container)
		{
			List<ReactionItem> result=new ArrayList<ReactionItem>();
			//...WIP
			return result;
		}
	}
	
	public static class ReactionContainer
	{
		//Static//
		public static final String DEFAULT_NAME="XCraftReactionContainer_Default";
		
		//Instance//
		protected List<Reaction> containReactions=null;
		protected Block[][][] blocks=new Block[3][3][3];
		
		protected int coreX,coreY,coreZ;
		protected String _name=DEFAULT_NAME;
		
		public ReactionContainer(Reaction... reactions)
		{
			this.initBlocksToNull();
			this.setCorePosition(1,0,1);
			this.initReactions(reactions);
		}
		
		public ReactionContainer initReactions(Reaction... reactions)
		{
			this.containReactions=(List<Reaction>)Arrays.asList(reactions);
			return this;
		}
		
		public String getName()
		{
			return this._name;
		}
		
		public ReactionContainer setName(String name)
		{
			this._name=name;
			return this;
		}
		
		//|O ~ O|N ~ N|~ G ~|
		//|~ X ~|~ C ~|G G G|
		//|O ~ O|N ~ N|~ G ~|
		//"C=Center,X=Default Core Position(0,-1,0)[From Center]"
		public ReactionContainer setCorePosition(int x,int y,int z)
		{
			this.coreX=x;this.coreY=y;this.coreZ=z;
			return this;
		}
		//z
		//^
		//| y=0 | y=1 | y=2 |
		//|2 ~ ~|N ~ N|~ G ~|
		//|1 2 ~|~ C ~|G G G|
		//|0 1 2|N ~ N|~ G ~|->x
		//"C=Center,X=Default Core Position(1,0,1)"
		public ReactionContainer initBlocksToNull()
		{
			for(int x=0;x<3;x++)
				for(int y=0;y<3;y++)
					for(int z=0;z<3;z++)
					{
						this.setBlockAt(x,y,z,null);
					}
			return this;
		}
		
		public ReactionContainer setBlockAt(int x,int y,int z,Block block)
		{
			this.blocks[x][y][z]=block;
			XCraftBlocks.LOGGER.info("Setted Block At"+x+","+y+","+z+","+block);
			return this;
		}
		
		public ReactionContainer setBlockAt(BlockPos pos,Block block)
		{
			return this.setBlockAt(pos.getX(),pos.getY(),pos.getZ(),block);
		}
		
		public ReactionContainer setCornerBlockAt(int layer,Block block)
		{
			return this.setBlockAt(0,layer,0,block).setBlockAt(0,layer,2,block).setBlockAt(2,layer,0,block).setBlockAt(2,layer,2,block);
		}
		
		public ReactionContainer setSideBlockAt(int layer,Block block)
		{
			return this.setBlockAt(0,layer,1,block).setBlockAt(1,layer,0,block).setBlockAt(2,layer,1,block).setBlockAt(1,layer,2,block);
		}
		
		public ReactionContainer setInnerBlockAt(int layer,Block block)
		{
			return this.setBlockAt(1,layer,1,block);
		}
		
		public ReactionContainer clearBlockAt(int x,int y,int z)
		{
			this.blocks[x][y][z]=null;
			return this;
		}
		
		public ReactionContainer clearBlockAt(BlockPos pos,Block block)
		{
			return clearBlockAt(pos.getX(),pos.getY(),pos.getZ());
		}
		
		public List<Reaction> getContainReactions()
		{
			return this.containReactions;
		}
		
		public Block getBlockAt(int x,int y,int z)
		{
			return this.blocks[x][y][z];
		}
		
		//Methods
		public boolean contains(Reaction reaction)
		{
			return this.containReactions.contains(reaction);
		}
		
		public boolean isCurrentBlocks(World world,BlockPos corePosition)
		{
			int x=corePosition.getX()-this.coreX;
			int y=corePosition.getY()-this.coreY;
			int z=corePosition.getZ()-this.coreZ;
			
			for(int ix=0;ix<3;ix++)
				for(int iy=0;iy<3;iy++)
					for(int iz=0;iz<3;iz++)
					{
						XCraftBlocks.LOGGER.info("ReactionContainer detecting: containerBlock: "+this.getBlockAt(ix,iy,iz)+",otherBlock: "+world.getBlockState(new BlockPos(x+ix,y+iy,z+iz)).getBlock());
						if(this.getBlockAt(ix,iy,iz)!=null&&
						   this.getBlockAt(ix,iy,iz)!=world.getBlockState(new BlockPos(x+ix,y+iy,z+iz)).getBlock()
						   ) return false;
					}
			return true;
		}
	}
	
	public static class ReactionItem
	{
		public Item item;
		public int count;
		public int data;
		public EntityItem source;
		
		public ReactionItem(Item item,int count,int data,EntityItem source)
		{
			this.item=item;
			this.count=count;
			this.data=data;
			this.source=source;
		}
		
		public static ReactionItem fromEntityItem(EntityItem item)
		{
			return ReactionItem.fromItemStack(item.getItem(),item);
		}
		
		public static ReactionItem fromItemStack(ItemStack stack,EntityItem source)
		{
			return new ReactionItem(stack.getItem(),stack.getCount(),stack.getMetadata(),source);
		}
		
		public static ItemStack toItemStack(ReactionItem item)
		{
			return new ItemStack(item.item,item.count,item.data);
		}
	}
	
	public static class ReactionItemContainer
	{
		protected ArrayList<Item> itemIndex=new ArrayList<Item>();
		protected ArrayList<Integer> itemCount=new ArrayList<Integer>();
		private int _typeCount;
		
		public ReactionItemContainer()
		{
			this._typeCount=0;
		}
		
		public ReactionItemContainer(List<ReactionItem> items)
		{
			for(ReactionItem item : items)
			{
				this.add(item);
			}
			this.frashTypeCount();
		}
		
		protected int frashTypeCount()
		{
			return this._typeCount=this.itemIndex.size();
		}
		
		public int typeCount()
		{
			return this._typeCount;
		}
		
		public boolean isEmpty()
		{
			return this._typeCount<=0;
		}
		
		public ReactionItemContainer copy()
		{
			ReactionItemContainer result=new ReactionItemContainer();
			result.itemIndex.addAll(this.itemIndex);
			result.itemCount.addAll(this.itemCount);
			result._typeCount=this._typeCount;
			return result;
		}
		
		public ReactionItemContainer add(ReactionItem item)
		{
			int count=item.count;
			int index=this.itemIndex.indexOf(item.item);
			if(index>0)
			{
				this.itemCount.set(index,Integer.valueOf(this.itemCount.get(index).intValue()+count));
				return this;
			}
			this.append(item.item,count);
			this.frashTypeCount();
			return this;
		}
		
		protected ReactionItemContainer append(Item item,int count)
		{
			this.itemIndex.add(item);
			this.itemCount.add(Integer.valueOf(count));
			this.frashTypeCount();
			return this;
		}
		
		public int getCount(Item item)
		{
			int index=this.itemIndex.indexOf(item);
			if(index>0)
			{
				return this.itemCount.get(index).intValue();
			}
			return 0;
		}
		
		public Item getItemByIndex(int index)
		{
			return this.itemIndex.get(Math.abs(index)%this.itemIndex.size());
		}
		
		public int getCountByIndex(int index)
		{
			return this.itemCount.get(Math.abs(index)%this.itemCount.size());
		}
	}
}
