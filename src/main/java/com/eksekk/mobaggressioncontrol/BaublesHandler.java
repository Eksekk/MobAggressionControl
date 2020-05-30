package com.eksekk.mobaggressioncontrol;

import java.util.ArrayList;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class BaublesHandler
{
	public static ArrayList<ItemStack> getEquippedBaubles(EntityPlayer player)
	{
		ArrayList<ItemStack> list = new ArrayList<>();
		
		IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
		
		for (int i = 0; i < handler.getSlots(); ++i)
		{
			if (handler.getStackInSlot(i) != ItemStack.EMPTY)
			{
				list.add(handler.getStackInSlot(i));
			}
		}
		
		return list;
	}
}
