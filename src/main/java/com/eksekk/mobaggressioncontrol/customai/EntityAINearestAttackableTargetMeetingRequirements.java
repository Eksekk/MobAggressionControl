package com.eksekk.mobaggressioncontrol.customai;

import javax.annotation.Nullable;

import com.eksekk.mobaggressioncontrol.Functions;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class EntityAINearestAttackableTargetMeetingRequirements<T extends EntityLivingBase> extends EntityAINearestAttackableTarget<T>
{

	public EntityAINearestAttackableTargetMeetingRequirements(EntityCreature creature, Class<T> classTarget, boolean checkSight)
	{
		super(creature, classTarget, checkSight);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected boolean isSuitableTarget(@Nullable EntityLivingBase target, boolean includeInvincibles)
    {
        if (target instanceof EntityPlayer)
        {
        	return Functions.playerMeetsGearRequirementsForEntity((EntityPlayer)target, this.taskOwner) && super.isSuitableTarget(target, includeInvincibles);
        }
        else
        {
        	return super.isSuitableTarget(target, includeInvincibles);
        }
    }
}
