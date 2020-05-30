package com.eksekk.mobaggressioncontrol;

import com.eksekk.mobaggressioncontrol.config.ConfigFields;
import com.eksekk.mobaggressioncontrol.customai.BaublesHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class Functions
{
	public static boolean playerMeetsGearRequirementsForEntity(EntityPlayer player, EntityLivingBase entity)
	{
		//TODO baubles
		ArrayList<Item> items = ConfigFields.requirementsMap.get(entity.getClass());
		
		if (items == null)
		{
			Main.LOGGER.info("Map entry not found, returning");
			return false;
		}
		
		ArrayList<ItemStack> armor = new ArrayList<>();
		player.getArmorInventoryList().forEach(armor::add);
		Map<Item, Boolean> equippedMap = new HashMap<>();
		for (Item item: items)
		{
			equippedMap.put(item, false);
		}
		
		for (ItemStack itemStack: armor)
		{
			equippedMap.replace(itemStack.getItem(), true);
		}
		
		if (Loader.isModLoaded("baubles"))
		{
			ArrayList<ItemStack> baubles = BaublesHandler.getEquippedBaubles(player);
			for (ItemStack bauble: baubles)
			{
				equippedMap.replace(bauble.getItem(), true);
			}
		}
		
		boolean allEquipped = true;
		for (Map.Entry<Item, Boolean> entry: equippedMap.entrySet())
		{
			if (!entry.getValue())
			{
				allEquipped = false;
				break;
			}
		}
		if (allEquipped)
		{
			Main.LOGGER.info("Player satisfies requirements for entity");
		}
		else
		{
			Main.LOGGER.info("Player doesn't satisfy requirements for entity");
		}
		return allEquipped;
	}
}
