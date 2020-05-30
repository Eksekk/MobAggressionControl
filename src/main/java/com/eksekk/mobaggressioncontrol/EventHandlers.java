package com.eksekk.mobaggressioncontrol;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.eksekk.mobaggressioncontrol.config.ConfigFields;
import com.eksekk.mobaggressioncontrol.util.Reference;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Reference.MOD_ID)
public class EventHandlers
{
	//TODO entities attacked by player and then by entities other than player do not attack player (just stand useless)
	//TODO first fix available, test more
	
	//TODO kinda breaks with endermen (they are still in "agitated" state, just don't attack player)
	
	//TODO equipping items makes mobs neutral even when they were already
	//attacking player because of not enough equipment (shouldn't happen probably)
	public static final int RESET_EVERY_X_TICKS = 100;
	public static int currentTick = 0;
	
	public static Map<EntityLivingBase, EntityPlayer> entitiesNeedingRevengeTimerReset = new HashMap<>();
	
	public static final Field attackTarget = ObfuscationReflectionHelper.findField(EntityLiving.class, "field_70696_bz");
	public static final Field revengeTimer = ObfuscationReflectionHelper.findField(EntityLivingBase.class, "field_70756_c");
	public static final Field ticksExisted = ObfuscationReflectionHelper.findField(Entity.class, "field_70173_aa");
	public static final Field target = ObfuscationReflectionHelper.findField(EntityAITarget.class, "field_188509_g");
	
	@SubscribeEvent
	public static void onLivingSetAttackTarget(LivingSetAttackTargetEvent event) throws IllegalAccessException
	{
		EntityLiving living = event.getEntity() instanceof EntityLiving ? (EntityLiving)event.getEntity() : null;
		if (living == null)
		{
			return;
		}
		//target was reset because of reason other than auto deaggroing after 100 ticks
		//(because we ruled out auto deaggro by resetting revenge timer)
		else if (event.getTarget() == null)
		{
			entitiesNeedingRevengeTimerReset.remove(living);
			return;
		}
		
		EntityAITasks tasks = living.targetTasks;
		for (EntityAITaskEntry entry: tasks.taskEntries)
		{
			if (entry.action instanceof EntityAINearestAttackableTarget
				//only players can make mobs neutral for now
				&& event.getTarget() instanceof EntityPlayer
				//if the target was set because of revenge (player attacked entity)
				//we ignore this, otherwise player could attack mobs freely and no retaliation would occur
				&& living.getRevengeTarget() != living.getAttackTarget()
				//circumvent first TODO
				&& !(entitiesNeedingRevengeTimerReset.containsKey(living))
				&& Functions.playerMeetsGearRequirementsForEntity((EntityPlayer)event.getTarget(), event.getEntityLiving()))
			{
				Main.LOGGER.info("Setting attack target to null");
				living.setAttackTarget(null);
				return;
			}
			//target was set because of EntityAIHurtByTarget task
			else if (entry.action instanceof EntityAIHurtByTarget && event.getTarget() instanceof EntityPlayer && target.get(entry.action) == event.getTarget())
			{
				entitiesNeedingRevengeTimerReset.put(living, (EntityPlayer)event.getTarget());
			}
		}
	}
	
	@SubscribeEvent
	public static void onConfigChanged(OnConfigChangedEvent event)
	{
		if (event.getModID().equals(Reference.MOD_ID))
		{
			ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
			ConfigFields.processConfig();
		}
	}
	
	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) throws IllegalAccessException
	{
		if (event.phase == Phase.END)
		{
			++currentTick;
			if (currentTick < RESET_EVERY_X_TICKS)
			{
				return;
			}
			currentTick = 0;
			ArrayList<EntityLivingBase> entitiesToRemove = new ArrayList<>();
			for (Map.Entry<EntityLivingBase, EntityPlayer> entry: entitiesNeedingRevengeTimerReset.entrySet())
			{
				EntityLivingBase entity = entry.getKey();
				if (entity.isDead)
				{
					entitiesToRemove.add(entity);
				}
				else
				{
					//reset revenge timer
					//if we don't reset this, mob will deaggro after 100 ticks (5 seconds)
					revengeTimer.set(entity, ticksExisted.get(entity));
				}
			}
			for (EntityLivingBase entity: entitiesToRemove)
			{
				entitiesNeedingRevengeTimerReset.remove(entity);
			}
		}
	}
}
