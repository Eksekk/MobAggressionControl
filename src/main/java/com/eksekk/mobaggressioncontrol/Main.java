package com.eksekk.mobaggressioncontrol;

import org.apache.logging.log4j.Logger;

import com.eksekk.mobaggressioncontrol.config.ConfigFields;
import com.eksekk.mobaggressioncontrol.proxy.CommonProxy;
import com.eksekk.mobaggressioncontrol.util.Reference;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION)
public class Main
{
	public static Logger LOGGER;
	
	@Instance
	public static Main instance;
	
	@SidedProxy(serverSide = Reference.COMMON_PROXY_CLASS, clientSide = Reference.CLIENT_PROXY_CLASS)
	public static CommonProxy proxy;
	
	@EventHandler
	public static void preInit(FMLPreInitializationEvent event)
	{
		LOGGER = event.getModLog();
	}
	
	@EventHandler
	public static void init(FMLInitializationEvent event)
	{
		ConfigFields.processConfig();
	}
	
	@EventHandler
	public static void postInit(FMLPostInitializationEvent event)
	{
		
	}
}
