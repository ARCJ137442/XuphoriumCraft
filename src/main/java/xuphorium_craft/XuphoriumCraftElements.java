/**
  * MCreator注意：
  *此文件是自动生成的，以将所有MCreator mod元素连接在一起。
  */

package xuphorium_craft;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;

import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.World;
import net.minecraft.potion.Potion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.Block;

import java.util.function.Supplier;
import java.util.Random;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

import xuphorium_craft.*;
import xuphorium_craft.common.*;
import xuphorium_craft.proxy.*;
import xuphorium_craft.entity.*;
import xuphorium_craft.block.*;
import xuphorium_craft.item.*;

public class XuphoriumCraftElements implements IFuelHandler, IWorldGenerator {
	protected final List<ModElement> elements = new ArrayList<>();
	protected final List<Supplier<Block>> blocks = new ArrayList<>();
	protected final List<Supplier<Item>> items = new ArrayList<>();
	protected final List<Supplier<Biome>> biomes = new ArrayList<>();
	protected final List<Supplier<EntityEntry>> entities = new ArrayList<>();
	protected final List<Supplier<Potion>> potions = new ArrayList<>();

	public void preInit(FMLPreInitializationEvent event) {
		try {
			for (ASMDataTable.ASMData asmData : event.getAsmData().getAll(ModElement.Tag.class.getName())) {
				Class<?> clazz = Class.forName(asmData.getClassName());
				if (clazz.getSuperclass() == XuphoriumCraftElements.ModElement.class)
					elements.add((XuphoriumCraftElements.ModElement) clazz.getConstructor(this.getClass()).newInstance(this));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Collections.sort(elements);
		elements.forEach(XuphoriumCraftElements.ModElement::initElements);
		this.addNetworkMessage(xuphorium_craftVariables.WorldSavedDataSyncMessageHandler.class, xuphorium_craftVariables.WorldSavedDataSyncMessage.class,
				Side.SERVER, Side.CLIENT);
	}

	public void registerSounds(RegistryEvent.Register<net.minecraft.util.SoundEvent> event) {
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator cg, IChunkProvider cp) {
		elements.forEach(element -> element.generateWorld(random, chunkX * 16, chunkZ * 16, world, world.provider.getDimension(), cg, cp));
	}

	@Override
	public int getBurnTime(ItemStack fuel) {
		for (ModElement element : elements) {
			int ret = element.addFuel(fuel);
			if (ret != 0)
				return ret;
		}
		return 0;
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
		if (!event.player.world.isRemote) {
			WorldSavedData mapdata = xuphorium_craftVariables.MapVariables.get(event.player.world);
			WorldSavedData worlddata = xuphorium_craftVariables.WorldVariables.get(event.player.world);
			if (mapdata != null)
				xuphorium_craft.PACKET_HANDLER.sendTo(new xuphorium_craftVariables.WorldSavedDataSyncMessage(0, mapdata), (EntityPlayerMP) event.player);
			if (worlddata != null)
				xuphorium_craft.PACKET_HANDLER.sendTo(new xuphorium_craftVariables.WorldSavedDataSyncMessage(1, worlddata), (EntityPlayerMP) event.player);
		}
	}

	@SubscribeEvent
	public void onPlayerChangedDimension(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent event) {
		if (!event.player.world.isRemote) {
			WorldSavedData worlddata = xuphorium_craftVariables.WorldVariables.get(event.player.world);
			if (worlddata != null)
				xuphorium_craft.PACKET_HANDLER.sendTo(new xuphorium_craftVariables.WorldSavedDataSyncMessage(1, worlddata), (EntityPlayerMP) event.player);
		}
	}
	private int messageID = 0;

	public <T extends IMessage, V extends IMessage> void addNetworkMessage(Class<? extends IMessageHandler<T, V>> handler, Class<T> messageClass,
			Side... sides) {
		for (Side side : sides)
			xuphorium_craft.PACKET_HANDLER.registerMessage(handler, messageClass, messageID, side);
		messageID++;
	}

	public static class GuiHandler implements IGuiHandler {
		@Override
		public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
			return null;
		}

		@Override
		public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
			return null;
		}
	}

	public List<ModElement> getElements() {
		return elements;
	}

	public List<Supplier<Block>> getBlocks() {
		return blocks;
	}

	public List<Supplier<Item>> getItems() {
		return items;
	}

	public List<Supplier<Biome>> getBiomes() {
		return biomes;
	}

	public List<Supplier<EntityEntry>> getEntities() {
		return entities;
	}

	public List<Supplier<Potion>> getPotions() {
		return potions;
	}

	public static class ModElement implements Comparable<ModElement> {
		@Retention(RetentionPolicy.RUNTIME)
		public @interface Tag {
		}
		protected final XuphoriumCraftElements elements;
		protected final int sortid;

		public ModElement(XuphoriumCraftElements elements, int sortid) {
			this.elements = elements;
			this.sortid = sortid;
		}

		public void initElements() {
		}

		public void init(FMLInitializationEvent event) {
		}

		public void preInit(FMLPreInitializationEvent event) {
		}

		public void generateWorld(Random random, int posX, int posZ, World world, int dimID, IChunkGenerator cg, IChunkProvider cp) {
		}

		public void serverLoad(FMLServerStartingEvent event) {
		}

		public void registerModels(ModelRegistryEvent event) {
		}

		public int addFuel(ItemStack fuel) {
			return 0;
		}

		@Override
		public int compareTo(ModElement other) {
			return this.sortid - other.sortid;
		}
	}
}
