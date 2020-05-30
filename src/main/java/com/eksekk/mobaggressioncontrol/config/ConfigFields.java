package com.eksekk.mobaggressioncontrol.config;

import com.eksekk.mobaggressioncontrol.Main;
import com.eksekk.mobaggressioncontrol.util.Reference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Ignore;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import scala.actors.threadpool.Arrays;

@Config(modid = Reference.MOD_ID)
public class ConfigFields
{
	@Comment
	({
		"One line has format mobid1,mobid2[...];itemid1,itemid2,[...]",
		"Wearing specified items will make specified mobs neutral"
	})
	public static String[] requirementsArray = {};
	
	@Ignore
	public static Map<Class, ArrayList<Item>> requirementsMap = new HashMap<>();
	
	public static void processConfig()
	{
		requirementsMap.clear();
		for (String line: requirementsArray)
		{
			String[] splittedMobsItems = line.split(";");
			if (splittedMobsItems.length != 2)
			{
				Main.LOGGER.error("Config entry \"" + line + "\"is invalid, skipping this entry");
				continue;
			}
			ArrayList<Class> mobs = new ArrayList<>();
			ArrayList<Item> items = new ArrayList<>();
			
			String[] splittedMobs = splittedMobsItems[0].split(",");
			String[] splittedItems = splittedMobsItems[1].split(",");
			
			if (splittedMobs.length == 0 || splittedItems.length == 0)
			{
				Main.LOGGER.error("Config entry \"" + line + "\"is invalid, skipping this entry");
				continue;
			}
			
			for (String mobid: splittedMobs)
			{
				if (!ForgeRegistries.ENTITIES.containsKey(new ResourceLocation(mobid)))
				{
					Main.LOGGER.error("Config mob entry \"" + mobid + "\" is invalid, skipping this mob");
					continue;
				}
				EntityEntry ee = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(mobid));
				mobs.add(ee.getEntityClass());
			}
			
			for (String itemid: splittedItems)
			{
				if (!ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemid)))
				{
					Main.LOGGER.error("Config item entry \"" + itemid + "\" is invalid, skipping this item");
					continue;
				}
				items.add(ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemid)));
			}
			
			for (Class mobClass: mobs)
			{
				requirementsMap.put(mobClass, items);
			}
		}
	}
}
