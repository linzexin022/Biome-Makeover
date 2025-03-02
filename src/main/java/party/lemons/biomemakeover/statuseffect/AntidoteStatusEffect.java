package party.lemons.biomemakeover.statuseffect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.InstantStatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import party.lemons.biomemakeover.init.BMCriterion;
import party.lemons.biomemakeover.mixin.StatusEffectMixin;
import party.lemons.biomemakeover.util.access.StatusEffectAccess;

import java.util.List;
import java.util.stream.Collectors;

public class AntidoteStatusEffect extends InstantStatusEffect
{
	public AntidoteStatusEffect()
	{
		super(StatusEffectType.BENEFICIAL, 0xFFFFFF);
	}

	@Override
	public void applyInstantEffect(Entity source, Entity attacker, LivingEntity target, int amplifier, double proximity)
	{
		doEffect(target);
	}

	@Override
	public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier)
	{
		doEffect(entity);
		super.onApplied(entity, attributes, amplifier);
	}

	public void doEffect(LivingEntity target)
	{
		target.getStatusEffects()
				.stream()
				.filter(
						(e)->((StatusEffectAccess)e.getEffectType()).getType() == StatusEffectType.HARMFUL)
				.collect(Collectors.toList())
				.forEach(
						e->target.removeStatusEffect(e.getEffectType())
				);

		if(target instanceof PlayerEntity && !target.world.isClient())
		{
			BMCriterion.ANTIDOTE.trigger((ServerPlayerEntity) target);
		}
	}

}
